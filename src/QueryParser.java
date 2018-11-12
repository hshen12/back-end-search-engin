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
 * Search class
 * 	perform a stem for the query file and a partial or exact search for the index passed in
 * @author Hao Shen
 * 
 * @see {@link InvertedIndex}
 * @see SnowballStemmer
 */
public class QueryParser implements QueryParserInterface {

	//key: one line of query file
	//value: sorted array list
	private final TreeMap<String, ArrayList<OneResult>> searchResult;
	private final InvertedIndex index;

	/**
	 * Constructor
	 * 	initialize the data structure
	 */
	public QueryParser(InvertedIndex index) {
		this.searchResult = new TreeMap<String, ArrayList<OneResult>>();
		this.index = index;
	}

	/**
	 * Read the query file and perform a partial or exact search to the index, store the search result to the
	 * data structure
	 * @param index data structure
	 * @param queryFile query file
	 * @param exact boolean indicate exact or partial search
	 * @param searchResult data structure to store the search result
	 * @throws IOException when encounter problem when reading the query file
	 * 
	 * @see #doPartialSearch(TreeSet, InvertedIndex)
	 * @see #doExactSearch(TreeSet, InvertedIndex)
	 */
	public void stemQuery(Path queryFile, boolean exact) throws IOException {
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
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

				String queryLine = String.join(" ", oneLineQuery);
				if(!searchResult.containsKey(queryLine)) {
					if(!oneLineQuery.isEmpty()) {
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
}
