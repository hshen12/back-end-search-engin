import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * SearchResult class
 * 	data structure to store the search result
 * @author Hao Shen
 *
 */
public class SearchResult {
	
	//key: one line of query file
	//value: sorted array list
	private TreeMap<TreeSet<String>, ArrayList<OneResult>> searchResult;
	
	/**
	 * Constructor
	 * 	initialize the data structure
	 */
	public SearchResult() {
		this.searchResult = new TreeMap<TreeSet<String>, ArrayList<OneResult>>(new TreeSetComparator());
	}

	/**
	 * Add the given key and value to the data structure
	 * @param oneLineQuery TreeSet contains one line of query file
	 * @param value, array list of search result
	 */
	public void add(TreeSet<String> oneLineQuery, ArrayList<OneResult> value) {
		searchResult.putIfAbsent(oneLineQuery, value);
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
