import java.util.TreeSet;

/**
 * Perform a exact search on the inverted index data structure
 * @author Hao Shen
 *
 */
public class ExactSearchTask implements Runnable {

	private final TreeSet<String> oneLineQuery;
	private final InvertedIndex index;
	private final SearchResult searchResult;
	
	/**
	 * Constructor
	 * @param oneLineQuery treeset contains one line of query file
	 * @param index inverted index data structure
	 * @param searchResult data structure to store search result
	 */
	public ExactSearchTask(TreeSet<String> oneLineQuery, InvertedIndex index, SearchResult searchResult) {
		this.oneLineQuery = oneLineQuery;
		this.index = index;
		this.searchResult = searchResult;
	}

	/**
	 * Define how to run the work
	 * 
	 * @see Search#doExactSearch(TreeSet, InvertedIndex)
	 */
	@Override
	public void run() {
		searchResult.add(oneLineQuery, Search.doExactSearch(oneLineQuery, index));
	}

}
