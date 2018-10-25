import java.util.Collections;
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
public class InvertedMap {

	//data structure
	//TreeMap<word, TreeMap<path, TreeSet<position>>>
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> map;
	
	/**
	 * Constructor 
	 * 	initialize data structure 
	 * @see java.util.TreeMap
	 */
	public InvertedMap() {
		this.map = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}
	
	/**
	 * 	return a set view of all word
	 * @return Set<String>
	 * 
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<String> getWordSet() {
		Set<String> result = map.keySet();
		return Collections.unmodifiableSet(result);
	}
	
	/**
	 * return the set view of all path key
	 * @param word key
	 * @return Set<String>
	 * 
	 * @see java.util.TreeMap#keySet()
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<String> getPathSet(String word) {
		Set<String> result = map.get(word).keySet();
		return Collections.unmodifiableSet(result);
	}
	
	/**
	 * return the set of given key at given position
	 * @param word key
	 * @param position key
	 * @return value of the given word at given position
	 * 
	 * @see Collections#unmodifiableSortedSet(SortedSet)
	 */
	public SortedSet<Integer> getPosition(String word, String path) {
		TreeSet<Integer> result = map.get(word).get(path);
		return Collections.unmodifiableSortedSet(result);
	}
	
	/**
	 * toString method
	 * @return a string representation of the data structure
	 */
	@Override
	public String toString() {
		
		return map.toString();
		
	}
	
	/**
	 * return true if the data structure is empty
	 * @return true if the data structure is empty, false otherwise
	 */
	public boolean isEmpty() {
		boolean result = map.isEmpty();
		return result;
	}

	/**
	 * return whether the map contains a key
	 * @param word key
	 * @return true if the map contains a key, false otherwise
	 */
	public boolean containsKey(String word) {
		return map.containsKey(word);
	}

	/**
	 * put the given TreeSet at given word for given position
	 * @param word key
	 * @param txt file path
	 * 	string representation of the path
	 * @param set 
	 */
	public void put(String word, String txt, TreeSet<Integer> set) {
		
		if(word.equals("")) {
			return;
		}
		
		if(containsKey(word)) {
			
			TreeMap<String, TreeSet<Integer>> temp = map.get(word);
			temp.put(txt, set);
			map.put(word, temp);
		} else {
			
			TreeMap<String, TreeSet<Integer>> temp = new TreeMap<String, TreeSet<Integer>>();
			temp.put(txt, set);
			map.put(word, temp);
		}
	}

}
