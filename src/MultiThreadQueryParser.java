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

	
	private final WorkQueue worker;
	//key: one line of query file
	//value: sorted array list
	private final TreeMap<String, ArrayList<OneResult>> searchResult;
	private final ThreadSafeInvertedIndex index;
	
	/**
	 * Constructor
	 * 	initialize the data structure
	 */
	public MultiThreadQueryParser(WorkQueue wq, ThreadSafeInvertedIndex index) {
		this.searchResult = new TreeMap<String, ArrayList<OneResult>>();
		this.index = index;
		this.worker = wq;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stemQuery(Path queryFile, boolean exact) throws IOException {
		try(BufferedReader br = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
			String line;
			while((line = br.readLine()) != null) {
				worker.execute(new SearchTask(line, exact));
			}
		}
		worker.finish();
	}

	/**
	 * {@inheritDoc}
	 */
	public void toSearchResult(Path resultPath) throws IOException{
		synchronized(searchResult) {
			TreeJSONWriter.asSearchResult(searchResult, resultPath);
		}
	}

	/**
	 * Perform a partial or exact search to the inverted index
	 * @author Hao Shen
	 *
	 */
	private class SearchTask implements Runnable {

		private final String line;
		private final boolean exact;

		public SearchTask(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			TreeSet<String> oneLineQuery = new TreeSet<String>();
			var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			for(String s: TextParser.parse(line)) {
				String word = stemmer.stem(s).toString();
				if(!word.isEmpty()) {
					oneLineQuery.add(word);
				}
			}

			String queryLine = String.join(" ", oneLineQuery);
		
			synchronized(searchResult) {
				if(searchResult.containsKey(queryLine)) {
					return;
				}
			}

			if(!oneLineQuery.isEmpty()) {
				synchronized(searchResult) {
					if(searchResult.containsKey(queryLine)) {
						return;
					}
				}

				ArrayList<OneResult> result;
				if(exact) {
					result = index.exactSearch(oneLineQuery);
				} else {
					result = index.partialSearch(oneLineQuery);
				}
				synchronized(searchResult) {
					searchResult.put(queryLine, result);
				}
			}
		}
	}
}
