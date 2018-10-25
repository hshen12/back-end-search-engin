/**
 * OneResult class
 * 	data structure to store one search result
 * @author Hao Shen
 *
 */
public class OneResult implements Comparable<OneResult>{
	
	private String path;
	private Integer total_words;
	private Integer count;
	private Double score;
	
	/**
	 * Constructor
	 * 	initialize one search result
	 * @param path the path of the file which found the query word
	 * @param total_words how many word in that file
	 * @param count how many times the query word being found in the file
	 */
	public OneResult(String path, int total_words, int count) {
		this.path = path;
		this.total_words = Integer.valueOf(total_words);
		this.count = Integer.valueOf(count);
		this.score = Double.valueOf((double) count/total_words);
	}
	
	/**
	 * Update the count and recalculate the score for the search result
	 * @param other_count the number need to be increase
	 */
	public void updateCount(int other_count) {
		this.count += other_count;
		this.score = Double.valueOf((double) count/total_words);
	}
	
	/**
	 * Return the path for this search result
	 * @return String representation of the search result
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Return the score of this search result
	 * @return the score of the search result(double)
	 */
	public double getScore() {
		return this.score;
	}
	
	/**
	 * Return the count of the search result
	 * @return the count of the search result(int)
	 */
	public int getCount() {
		return this.count;
	}
	
	/**
	 * Override hashCode method
	 * return the hashCode for path
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	/**
	 * ToString method
	 * return a String representation of the search result
	 */
	@Override
	public String toString() {
		return this.getPath() + " " + this.getCount() + " " + this.getScore();
	}
	
	/**
	 * CompareTo method
	 * compare the search result first by its score in descending order, if same, compare by count 
	 * in descending order, if same, compare by path in alphabetic order
	 */
	@Override
	public int compareTo(OneResult other) {
		if(Double.compare(this.getScore(), other.getScore()) == 0) {
			if(Integer.compare(this.getCount(), other.getCount()) == 0) {
				return String.CASE_INSENSITIVE_ORDER.compare(this.getPath(), other.getPath());
			}
			return Integer.compare(other.getCount(), this.getCount());
		}
		return Double.compare(other.getScore(), this.getScore());
		
	}

}
