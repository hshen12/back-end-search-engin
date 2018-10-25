import java.io.IOException;
import java.nio.file.Path;

/**
 * StemFile class
 * 	stem the given file and add in the inverted index data structure
 * @author Hao Shen
 *
 */
public class StemFileTask implements Runnable {
	
	private Path file;
	private InvertedIndex index;
	
	/**
	 * Constructor
	 * @param file
	 * @param index
	 */
	public StemFileTask(Path file, InvertedIndex index) {
		this.file = file;
		this.index = index;
	}

	/**
	 * Invoke the {@link InvertedMapBuilder#stemFile(Path, InvertedIndex)} to build the index
	 */
	@Override
	public void run(){
		try {
			InvertedMapBuilder.stemFile(file, index);
		} catch (IOException e1) {
			System.err.println("Unable to stem file: " + file.toString());
		}
	}

}
