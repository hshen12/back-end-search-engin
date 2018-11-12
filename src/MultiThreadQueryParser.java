import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * MultiThreadQueryParser class
 * perform a stem to the query file and add in a partial or exact search task to the work queue
 * @author Hao Shen
 *
 *@see Search
 */
public class MultiThreadQueryParser implements QueryParserInterface {

	private WorkQueue worker;
	//key: one line of query file
	//value: sorted array list
	private TreeMap<String, ArrayList<OneResult>> searchResult;
	private final InvertedIndex index;
	/**
	 * Constructor
	 * 	initialize the data structure
	 */
	public MultiThreadQueryParser(WorkQueue wq, InvertedIndex index) {
		this.searchResult = new TreeMap<String, ArrayList<OneResult>>();
		this.index = index;
		this.worker = wq;
	}

	/**
	 * Helper class, stem the query file and add a partial or exact search task to the work queue
	 * 	multi thread version of {@link Search#stemQuery(InvertedIndex, Path, boolean, SearchResult)}
	 * @param index inverted index data structure
	 * @param queryFile query file contains query word
	 * @param exact flag, indicate partial or exact search 
	 * @param searchResult data structure to store the search result
	 * @throws IOException when Buffered Reader cannot read the query file
	 * 
	 * @see Search#stemQuery(InvertedIndex, Path, boolean, SearchResult)
	 */
	private void stemQueryHelper(Path queryFile, boolean exact) throws IOException {
		try(BufferedReader br = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
			String line;
			while((line = br.readLine()) != null) {
				worker.execute(new SearchTask(line, index, searchResult, exact));
			}
		}
	}

	/**
	 * Assign work queue and wait all work to finish
	 * @param index inverted index data structure
	 * @param queryFile query file contains query word
	 * @param exact flag, indicate partial or exact search 
	 * @param searchResult data structure to store the search result
	 * @param paraWq work queue
	 * @throws IOException when cannot read the query file
	 */
	public void stemQuery(Path queryFile, boolean exact) throws IOException {
		stemQueryHelper(queryFile, exact);
		worker.finish();
		worker.shutdown();
	}

	/**
	 * Output the search result
	 * @param resultPath the output file path
	 * @throws IOException if unable to output search result
	 * 
	 * @see TreeJSONWriter#asSearchResult(TreeMap, Path)
	 */
	public void toSearchResult(Path resultPath) throws IOException{
		TreeJSONWriter.asSearchResult(searchResult, resultPath);
	}

	private static class SearchTask implements Runnable {

		private final TreeSet<String> oneLineQuery;
		private final String line;
		private final InvertedIndex index;
		private final TreeMap<String, ArrayList<OneResult>> searchResult;
		private final boolean exact;

		public SearchTask(String line, InvertedIndex index, TreeMap<String, ArrayList<OneResult>> searchResult, boolean exact) {
			this.line = line;
			this.index = index;
			this.searchResult = searchResult;
			this.oneLineQuery = new TreeSet<String>();
			this.exact = exact;
		}

		@Override
		public void run() {
			var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			for(String s: TextParser.parse(line)) {
				String word = stemmer.stem(s).toString();
				if(!word.isEmpty()) {
					oneLineQuery.add(word);
				}
			}

			String queryLine = String.join(" ", oneLineQuery);

			if(!searchResult.containsKey(queryLine)) {
				if(!oneLineQuery.isEmpty()) {
					synchronized(searchResult) {
						if(exact) {
							searchResult.put(queryLine, index.exactSearch(oneLineQuery));
						} else {
							searchResult.put(queryLine, index.partialSearch(oneLineQuery));
						}
					}
				}
			}
		}

	}

}
