import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Multi thread version of {@linkplain InvertedMapBuilder}
 * Find all text or txt file in the directory and add in the work queue
 * @author Hao Shen
 *
 */
public class MultiThreadInvertedMapBuilder {
	
	/**
	 * Initialize the work queue and wait all work to be finish
	 * @param file the file we want to store in the data structure
	 * @param index inverted index data structure
	 * @param paraWq work queue
	 * @throws IOException when buffered reader cannot read the file
	 * @throws InterruptedException when encountered interrupted exception
	 */
	public static void buildMap(Path file, InvertedIndex index, WorkQueue worker) throws IOException {
		buildMapHelper(file, index, worker);
		worker.finish();
	}
	
	/**
	 * Read through the given directory, find the file in the given directory and add the work to the work queue
	 * @param file directory to be go through
	 * @param index inverted index data structure
	 * @throws IOException 
	 * 
	 * @see {@link InvertedMapBuilder#buildMap(Path, InvertedIndex)}
	 */
	private static void buildMapHelper(Path file, InvertedIndex index, WorkQueue worker) throws IOException {
		
		if(Files.isDirectory(file)) {
			try(DirectoryStream<Path> list = Files.newDirectoryStream(file)) {
				Iterator<Path> directoryStreamIt = list.iterator();
				while(directoryStreamIt.hasNext()) {
					buildMapHelper(directoryStreamIt.next(), index, worker);
				}
			}
		} else {
			String fileName = file.toString().toLowerCase();
			if(fileName.endsWith(".text") || fileName.endsWith(".txt")) {
				worker.execute(new StemFileTask(file, index));
			}
		}
	}
	
	/**
	 * StemFile class
	 * 	stem the given file and add in the inverted index data structure
	 * @author Hao Shen
	 *
	 */
	private static class StemFileTask implements Runnable {

		private final Path file;
		private final InvertedIndex index;
		
		public StemFileTask(Path file, InvertedIndex index) {
			this.file = file;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				InvertedIndex temp = InvertedMapBuilder.stemFile(file);
				index.addAll(temp);
			} catch (IOException e1) {
				System.err.println("Unable to stem file: " + file.toString());
			}
		}
		
	}

}
