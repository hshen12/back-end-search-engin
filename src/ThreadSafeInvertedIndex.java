import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Thread safe version of inverted index
 * @author Hao Shen
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	private final ReadWriteLock lock;
	
	/**
	 * Invoke the parent constructor and initialize the read write lock
	 */
	public ThreadSafeInvertedIndex() {
		super();
		this.lock = new ReadWriteLock();
	}

	/**
	 * Return a set view of all word
	 * invoke the parent method
	 * 
	 * @see InvertedIndex#getWordSet()
	 */
	@Override
	public Set<String> getWordSet() {
		lock.lockReadOnly();
		try {
			return super.getWordSet();
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Return set view of all path key
	 * invoke the parent method
	 * 
	 * @see InvertedIndex#getPathSet(String)
	 */
	@Override
	public Set<String> getPathSet(String word) {
		lock.lockReadOnly();
		try {
			return super.getPathSet(word);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Return the set of given key at given position
	 * invoke the parent method
	 * 
	 * @see InvertedIndex#getPositionSet(String, String)
	 */
	@Override
	public SortedSet<Integer> getPositionSet(String word, String path) {
		lock.lockReadOnly();
		try {
			return super.getPositionSet(word, path);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Return whether the map contains a word
	 * invoke the parent method
	 * 
	 * @see InvertedIndex#containsWord(String)
	 */
	@Override
	public boolean containsWord(String word) {
		lock.lockReadOnly();
		try {
			return super.containsWord(word);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Check in the given word, is there a given path exist
	 * invoke the parent method
	 * 
	 * @see InvertedIndex#containsPath(String, String)
	 */
	@Override
	public boolean containsPath(String word, String path) {
		lock.lockReadOnly();
		try {
			return super.containsPath(word, path);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	//end lab
	@Override
	public boolean containsPosition(String word, String path, int position) {
		lock.lockReadOnly();
		try {
			return super.containsPosition(word, path, position);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public boolean putIndex(String word, String location, int position) {
		lock.lockReadWrite();
		try {
			return super.putIndex(word, location, position);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public void toLocationsJSON(Path path) throws IOException{
		lock.lockReadOnly();
		try {
			super.toLocationsJSON(path);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public void toIndexJSON(Path path) throws IOException{
		lock.lockReadOnly();
		try {
			super.toIndexJSON(path);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public boolean isEmpty() {
		lock.lockReadOnly();
		try {
			return super.isEmpty();
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public ArrayList<OneResult> partialSearch(TreeSet<String> oneLineQuery) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(oneLineQuery);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public ArrayList<OneResult> exactSearch(TreeSet<String> oneLineQuery) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(oneLineQuery);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public void addAll(InvertedIndex temp) {
		lock.lockReadWrite();
		try {
			super.addAll(temp);
		} finally {
			lock.unlockReadWrite();
		}
	}
	
	
}
