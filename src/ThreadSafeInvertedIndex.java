import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.SortedSet;

/**
 * Thread safe version of inverted index
 * @author Hao Shen
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex{

	private final ReadWriteLock lock;
	
	public ThreadSafeInvertedIndex() {
		super();
		this.lock = new ReadWriteLock();
	}
	
	@Override
	public void putLocations(String path, int count) {
		lock.lockReadWrite();
		super.putLocations(path, count);
		lock.unlockReadWrite();
	}

	@Override
	public Set<String> getWordSet() {
		lock.lockReadOnly();
		try {
			return super.getWordSet();
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public Set<String> getPathSet(String word) {
		lock.lockReadOnly();
		try {
			return super.getPathSet(word);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public SortedSet<Integer> getPositionSet(String word, String path) {
		lock.lockReadOnly();
		try {
			return super.getPositionSet(word, path);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public boolean containsWord(String word) {
		lock.lockReadOnly();
		try {
			return super.containsWord(word);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public boolean containsPath(String word, String path) {
		lock.lockReadOnly();
		try {
			return super.containsPath(word, path);
		} finally {
			lock.unlockReadOnly();
		}
	}
	
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
}
