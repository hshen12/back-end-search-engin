import java.util.Comparator;
import java.util.TreeSet;

/**
 * TreeSetComparator class
 * 	let the data structure remain the same order as query file
 * @author Hao Shen
 *
 */
public class TreeSetComparator implements Comparator<TreeSet<String>> {
		
		/**
		 * compare the first word of two TreeSet. if same, compare the second.
		 * if same, compare the third...
		 */
		@Override
		public int compare(TreeSet<String> t1, TreeSet<String> t2) {
			//compare the length of each TreeSet
			int compare = t1.size()-t2.size();
			if(compare > 0) {
				//if t1 is longer
				//go through each TreeSet and compare String in each position
				String t1String = nullChecker(t1.first());
				String t2String = nullChecker(t2.first());
				for(int i = 0; i < t1.size(); i++) {

					if(String.CASE_INSENSITIVE_ORDER.compare(t1String, t2String) != 0) {
						return String.CASE_INSENSITIVE_ORDER.compare(t1String, t2String);
					} else {
						t1String = nullChecker(t1.higher(t1String));
						t2String = nullChecker(t2.higher(t2String));
						continue;
					}
				}
			} else if(compare < 0) {
				//if t2 is longer
				String t1String = nullChecker(t1.first());
				String t2String = nullChecker(t2.first());
				for(int i = 0; i < t2.size(); i++) {

					if(String.CASE_INSENSITIVE_ORDER.compare(t1String, t2String) != 0) {
						return String.CASE_INSENSITIVE_ORDER.compare(t1String, t2String);
					} else {
						t1String = nullChecker(t1.higher(t1String));
						t2String = nullChecker(t2.higher(t2String));
						continue;
					}
				}
			}
			//if they have the same length
			String t1String = nullChecker(t1.first());
			String t2String = nullChecker(t2.first());
			for(int i =0; i < t1.size(); i++) {
				if(String.CASE_INSENSITIVE_ORDER.compare(t1String, t2String) != 0) {
					return String.CASE_INSENSITIVE_ORDER.compare(t1String, t2String);
				} else {
					t1String = nullChecker(t1.higher(t1String));
					t2String = nullChecker(t2.higher(t2String));
					continue;
				}
			}
			//two TreeSet contain same thing
			return 0;
		}
		
		/**
		 * nullChecker method
		 * check the value is null or not
		 * @param value
		 * 					String
		 * @return String
		 * 					return the value if it is not null. otherwise return an empty String
		 */
		private String nullChecker(String value) {
			return value != null ? value : "";
		}
	}