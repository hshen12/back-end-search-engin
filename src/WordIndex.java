import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Data structure to store strings and their positions.
 */
public class WordIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	private Map<String, Set<Integer>> index;
	private String txt;
	

	/**
	 * Initializes the index.
	 */
	public WordIndex() {
		index = new HashMap<String, Set<Integer>>();
		this.txt = null;
	}
	
	public WordIndex(String txt) {
		this();
		this.txt = txt;
	}

	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word
	 *            word to clean and add to index
	 * @param position
	 *            position word was found
	 */
	public void add(String word, int position) {
		if(index.get(word) == null) {
			Set<Integer> p = new TreeSet<>();
			p.add(position);
			index.put(word, p);
			return;
		}
		index.get(word).add(position);
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is
	 * at position 1.
	 *
	 * @param words
	 *            array of words to add
	 *
	 * @see #addAll(String[], int)
	 */
	public void addAll(String[] words) {
		addAll(words, 1);
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is
	 * at the provided starting position
	 *
	 * @param words
	 *            array of words to add
	 * @param start
	 *            starting position
	 */
	public void addAll(String[] words, int start) {
		for(int i = 0; i < words.length; i++) {
			Set<Integer> temp;
			if(index.containsKey(words[i])) {
				temp = index.get(words[i]);
			} else {
				temp = new TreeSet<Integer>();
			}
			temp.add(i+start);
			index.put(words[i], temp);
		}
	}

	/**
	 * Returns the number of times a word was found (i.e. the number of
	 * positions associated with a word in the index).
	 *
	 * @param word
	 * 	word to look for
	 * @return number of times the word was found
	 */
	public int count(String word) {
		
		if(index.containsKey(word) == false) {
			return 0;
		}
		return index.get(word).size();
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return number of words
	 */
	public int words() {
		return index.size();
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word
	 * 	word to look for
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}

	/**
	 * Returns a copy of the words in this index as a sorted list.
	 *
	 * @return sorted list of words
	 *
	 * @see ArrayList#ArrayList(java.util.Collection)
	 * @see Collections#sort(List)
	 */
	public List<String> copyWords() {
		Set<String> s = index.keySet();
		ArrayList<String> list = new ArrayList<>();
		for(String st: s) {
			list.add(st);
		}
		return list;
	}

	/**
	 * Returns a copy of the positions for a specific word.
	 *
	 * @param word
	 * 	to find in index
	 * @return sorted list of positions for that word
	 *
	 * @see ArrayList#ArrayList(java.util.Collection)
	 * @see Collections#sort(List)
	 */
	public List<Integer> copyPositions(String word) {
		if(index.containsKey(word) == false) {
			return new ArrayList<>();
		}
		List<Integer> list = new ArrayList<>();
		for(Integer s: index.get(word)) {
			list.add(s);
		}
		return list;
	}

	/**
	 * getter method
	 * @return keySet 
	 * 	set view of the data structure
	 */
	public Set<String> getKeySet() {
		return index.keySet();
	}
	
	/**
	 * getter method
	 * @return string 
	 * 	String represent txt path 
	 */
	public String getTxt() {
		return txt;
	}
	
	/**
	 * getter method
	 * @param word
	 * 	key
	 * @return value of the given key
	 */
	public TreeSet<Integer> getSet(String word) {
		return (TreeSet<Integer>) index.get(word);
	}
	
	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		
		return index.toString();
	}

	/**
	 * add all the word in the parameter to the data structure
	 * @param cleanedText 
	 * 	ArrayList contains the words
	 */
	public void addAll(ArrayList<String> cleanedText) {
		
		int position = 1;
		for(String s: cleanedText) {
			if(s.trim() != "") {
				add(s.toLowerCase().trim(), position);
				position++;
			}
		}
	}
	
	/**
	 * return true if the map is empty, false otherwise
	 * @return true if the map is empty, false otherwise
	 */
	public boolean isEmpty() {
		return index.isEmpty();
	}
	
}