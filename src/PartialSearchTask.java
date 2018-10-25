import java.util.TreeSet;

/**
 * Perform a partial search on inverted index
 * @author Hao Shen
 *
 */
public class PartialSearchTask implements Runnable {

	private final TreeSet<String> oneLineQuery;
	private final InvertedIndex index;
	private final SearchResult searchResult;
	
	/**
	 * Constructor
	 * @param oneLineQuery treeset contains one line of query file
	 * @param index inverted index data structure
	 * @param searchResult data structure to store search result
	 */
	public PartialSearchTask(TreeSet<String> oneLineQuery, InvertedIndex index, SearchResult searchResult) {
		this.oneLineQuery = oneLineQuery;
		this.index = index;
		this.searchResult = searchResult;
	}

	/**
	 * Define how to run the work
	 * 
	 * @see Search#doPartialSearch(TreeSet, InvertedIndex)
	 */
	@Override
	public void run() {
		searchResult.add(oneLineQuery, Search.doPartialSearch(oneLineQuery, index));
	}

}
