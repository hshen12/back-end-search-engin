import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class Search {

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
	public static void stemQuery(InvertedIndex index, Path queryFile, boolean exact, SearchResult searchResult) 
			throws IOException {
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
							searchResult.add(oneLineQuery ,doExactSearch(oneLineQuery, index));
						} else {
							searchResult.add(oneLineQuery ,doPartialSearch(oneLineQuery, index));
						}
					}
				}
			}
		}
	}

	/**
	 * Perform a partial search to the given index
	 * @param oneLineQuery TreeSet stores one line of the query file
	 * @param index data structure
	 * @return sorted ArrayList stores the search result
	 */
	public static ArrayList<OneResult> doPartialSearch(TreeSet<String> oneLineQuery, InvertedIndex index) {
		HashMap<String, OneResult> result = new HashMap<String, OneResult>();
		for(String oneQuery: oneLineQuery) {
			for(String indexKey: index.getWordSet()) {
				if(indexKey.startsWith(oneQuery)) {
					for(String path: index.getPathSet(indexKey)) {
						if(result.containsKey(path)) {
							result.get(path).updateCount(index.getPositionSet(indexKey, path).size());
						} else {
							result.put(path, new OneResult(path, index.getTotalWords(path), 
									index.getPositionSet(indexKey, path).size()));
						}
					}
				}
			}
			
		}
		ArrayList<OneResult> list = new ArrayList<OneResult>(result.values());
		Collections.sort(list);
		return list;
	}

	/**
	 * Perform a exact search to the given index
	 * @param oneLineQuery TreeSet stores one line of the query file
	 * @param index data structure
	 * @return sorted ArrayList stores the search result
	 */
	public static ArrayList<OneResult> doExactSearch(TreeSet<String> oneLineQuery, InvertedIndex index) {
		HashMap<String, OneResult> result = new HashMap<String, OneResult>();
		for(String oneQuery: oneLineQuery) {
			for(String indexKey: index.getWordSet()) {
				if(indexKey.equals(oneQuery)) {
					for(String path: index.getPathSet(indexKey)) {
						if(result.containsKey(path)) {
							result.get(path).updateCount(index.getPositionSet(indexKey, path).size());
						} else {
							result.put(path, new OneResult(path, index.getTotalWords(path), 
									index.getPositionSet(indexKey, path).size()));
						}
					}
				}
			}
			
		}
		ArrayList<OneResult> list = new ArrayList<OneResult>(result.values());
		Collections.sort(list);
		return list;
	}

}
