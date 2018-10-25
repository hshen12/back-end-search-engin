import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This is the thread safe version of {@link SearchResult}
 * @author Hao Shen
 *
 */
public class ThreadSafeSearchResult extends SearchResult {
	
	private final ReadWriteLock lock;
	
	/**
	 * Constructor
	 * invoke the parent constructor
	 */
	public ThreadSafeSearchResult() {
		super();
		this.lock = new ReadWriteLock();
	}
	
	/**
	 * Add the given key and value to the data structure
	 * @param oneLineQuery TreeSet contains one line of query file
	 * @param value, array list of search result
	 */
	@Override
	public void add(TreeSet<String> oneLineQuery, ArrayList<OneResult> value) {
		lock.lockReadWrite();
		super.add(oneLineQuery, value);
		lock.unlockReadWrite();
	}
	
	/**
	 * Output the search result
	 * @param resultPath the output file path
	 * @throws IOException if unable to output search result
	 * 
	 * @see TreeJSONWriter#asSearchResult(TreeMap, Path)
	 */
	@Override
	public void toSearchResult(Path resultPath) throws IOException {
		lock.lockReadOnly();
		try {
			super.toSearchResult(resultPath);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
}
