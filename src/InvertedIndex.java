import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * InvertedMap class
 * 	data structure to store the map
 * @author Hao Shen
 *
 */
public class InvertedIndex {

	//data structure
	//TreeMap<word, TreeMap<path, TreeSet<position>>>
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> locations;

	/**
	 * Initializes the inverted index. 
	 */
	public InvertedIndex() {
		this.index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		this.locations = new TreeMap<String, Integer>();
	}

	/**
	 * Return a set view of all word
	 * @return Set<String>
	 * 
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<String> getWordSet() {
		Set<String> result = index.keySet();
		return (result != null ? Collections.unmodifiableSet(result) : null);
	}

	/**
	 * Return set view of all path key
	 * @param word key
	 * @return Set<String>
	 * 
	 * @see java.util.TreeMap#keySet()
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<String> getPathSet(String word) {
		TreeMap<String, TreeSet<Integer>> result = index.get(word);
		return (result != null ? Collections.unmodifiableSet(result.keySet()) : null);
	}

	/**
	 * Return the set of given key at given position
	 * @param word key
	 * @param position key
	 * @return value of the given word at given position
	 * 
	 * @see Collections#unmodifiableSortedSet(SortedSet)
	 */
	public SortedSet<Integer> getPositionSet(String word, String path) {
		TreeSet<Integer> result = getterPosition(word, path);
		return (result != null ? Collections.unmodifiableSortedSet(result) : null);
	}

	/**
	 * Helper method return a TreeSet of given word and path
	 * @param word key for the TreeMap
	 * @param path key for the TreeMap
	 * @return TreeSet<Integer>
	 */
	private TreeSet<Integer> getterPosition(String word, String path) {
		TreeMap<String, TreeSet<Integer>> result = index.get(word);
		return (result != null ? result.get(path) : null);
	}

	/**
	 * Return whether the map contains a word
	 * @param word key
	 * @return true if the map contains a key, false otherwise
	 */
	public boolean containsWord(String word) {
		return (!isEmpty() ? index.containsKey(word) : false);
	}

	/**
	 * Check in the given word, is there a given path exist
	 * @param word word in the file
	 * @param path the path of the file
	 * @return true if the path in the given word already exist, false otherwise
	 */
	public boolean containsPath(String word, String path) {
		TreeMap<String, TreeSet<Integer>> result = index.get(word);
		return (result != null ? result.containsKey(path) : false);
	}

	/**
	 * Check in the given word, given path, is there a given position
	 * @param word word in the file
	 * @param path the path of the file
	 * @param position the position of the word in the file
	 * @return true if the word in the given file and given position already exist, false otherwise
	 */
	public boolean containsPosition(String word, String path, int position) {
		TreeSet<Integer> result = getterPosition(word, path);
		return (result != null ? result.contains(position) : false);
	}

	/**
	 * Put the given position at given word and given path
	 * @param word word in the file
	 * @param location the path of the file 
	 * @param position the position of the word in the file
	 */
	public boolean putIndex(String word, String location, int position) {
		return putIndexHelper(word, location, position);
	}

	/**
	 * Put in the given path and given count to the location map
	 * @param path key, represent the path
	 * @param count value, represent the total word in the path
	 * @return 
	 */
	private void incrementLocation(String path) {
		if(this.locations.containsKey(path)) {
			this.locations.put(path, this.locations.get(path).intValue()+1);
		} else {
			this.locations.put(path, 1);
		}
	}

	/**
	 * Output the location map 
	 * @param path output file location
	 * @throws IOException
	 * 
	 * {@link TreeJSONWriter#asObject(TreeMap)}
	 */
	public void toLocationsJSON(Path path) throws IOException {
		TreeJSONWriter.asObject(locations, path);
	}

	/**
	 * Return the total word for given path
	 * @param path key
	 * @return total word
	 */
	public int getTotalWords(String path) {
		return locations.get(path) != null ? locations.get(path) : 0;
	}

	/**
	 * Outputs the inverted index as pretty JSON to file
	 * @param path output file location
	 * @throws IOException
	 * 
	 * {@link TreeJSONWriter#asDoubleNestedObject(TreeMap, Path)}
	 */
	public void toIndexJSON(Path path) throws IOException {
		TreeJSONWriter.asDoubleNestedObject(index, path);
	}

	/**
	 * Return true if the data structure is empty
	 * @return true if the data structure is empty, false otherwise
	 */
	public boolean isEmpty() {
		return index.isEmpty();
	}

	/**
	 * Return a string representation of the content in the data structure
	 * @return a string representation of the data structure
	 */
	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * Perform a partial search to the inverted index data structure
	 * @param oneLineQuery a line of the query file
	 * @return a sorted array list
	 */
	public ArrayList<OneResult> partialSearch(TreeSet<String> oneLineQuery) {
		HashMap<String, OneResult> result = new HashMap<String, OneResult>();
		ArrayList<OneResult> list = new ArrayList<>();
		for(String oneQuery: oneLineQuery) {
			for(String indexKey: index.tailMap(oneQuery).keySet()) {
				if(indexKey.startsWith(oneQuery)) {
					searchHelper(result, indexKey, list);
				} else {
					break;
				}
			}
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * Perform a exact search to the inverted index data structure
	 * @param oneLineQuery one line of query file
	 * @return a sorted array list
	 */
	public ArrayList<OneResult> exactSearch(TreeSet<String> oneLineQuery) {
		HashMap<String, OneResult> result = new HashMap<String, OneResult>();
		ArrayList<OneResult> list = new ArrayList<>();
		for(String oneQuery: oneLineQuery) {
			searchHelper(result, oneQuery, list);
		}
		Collections.sort(list);
		return list;
	}


	/**
	 * Search helper method
	 * @param result hashMap to store the OneResult
	 * @param indexKey index word
	 * @param list returned array list
	 */
	private void searchHelper(HashMap<String, OneResult> result, String indexKey, ArrayList<OneResult> list) {
		var pathTreeMap = index.get(indexKey);
		if(pathTreeMap != null) {
			for(String path: pathTreeMap.keySet()) {
				if(result.containsKey(path)) {
					result.get(path).updateCount(getPositionSet(indexKey, path).size());
				} else {
					OneResult oneResult = new OneResult(path, getTotalWords(path), getPositionSet(indexKey, path).size());
					result.put(path, oneResult);
					list.add(oneResult);
				}
			}
		}
	}

	/**
	 * Add a temporary inverted index to this index
	 * @param temp inverted index
	 */
	public void addAll(InvertedIndex temp) {
		for (String word : temp.index.keySet()) {
			if (this.index.containsKey(word)) {
				for(String path: temp.index.get(word).keySet()) {
					if(this.index.get(word).containsKey(path)) {
						for(int position: temp.index.get(word).get(path)) {
							if(!this.index.get(word).get(path).contains(position)) {
								this.index.get(word).get(path).add(position);
							}
						}
					} else {
						this.index.get(word).put(path, temp.index.get(word).get(path));
					}
				}
			} else {
				this.index.put(word, temp.index.get(word));
			}
		}
		this.locations.putAll(temp.locations);
	}

	/**
	 * Put index helper method
	 * 	used for addAll method
	 * @param word word
	 * @param location path
	 * @param position position
	 * @return true if successfully add, false otherwise
	 */
	private boolean putIndexHelper(String word, String location, int position) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(location, new TreeSet<>());
		incrementLocation(location);
		return index.get(word).get(location).add(position);
	}
}
