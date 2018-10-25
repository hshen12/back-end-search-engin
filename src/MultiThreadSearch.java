import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * MultiThreadSearch class
 * perform a stem to the query file and add in a partial or exact search task to the work queue
 * @author Hao Shen
 *
 *@see Search
 */
public class MultiThreadSearch {

	private static WorkQueue wq;
	
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
	private static void stemQueryHelper(InvertedIndex index, Path queryFile, boolean exact, SearchResult searchResult) 
			throws IOException{
		var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		String queryFileName = queryFile.toString().toLowerCase();
		if(queryFileName.endsWith(".txt") || queryFileName.endsWith(".text")) {
			try(BufferedReader br = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
				String line;
				while((line = br.readLine()) != null) {
					TreeSet<String> oneLineQuery = new TreeSet<String>();
					for(String s: TextParser.parse(line)) {
						String word = stemmer.stem(s).toString();
						if(!word.isEmpty()) {
							oneLineQuery.add(word);
						}
					}
					if(!oneLineQuery.isEmpty()) {
						if(exact) {
							wq.execute(new ExactSearchTask(oneLineQuery, index, searchResult));
						} else {
							wq.execute(new PartialSearchTask(oneLineQuery, index, searchResult));
						}
					}
				}
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
	public static void stemQuery(InvertedIndex index, Path queryFile, boolean exact, SearchResult searchResult,
			WorkQueue paraWq) throws IOException {
		wq = paraWq;
		stemQueryHelper(index, queryFile, exact, searchResult);
		wq.finish();
	}

}
