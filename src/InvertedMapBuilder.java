import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * InvertedMapBuilder class
 * build the data structure 
 * @author Hao Shen
 *
 */
public class InvertedMapBuilder {

	private InvertedMap map;
	private ArrayList<Path> fileList;

	/**
	 * Constructor
	 * @param map
	 */
	public InvertedMapBuilder(InvertedMap map) {
		this.map = map;
		this.fileList = new ArrayList<Path>();
	}

	/**
	 * loop through the given directory and find the file and put them into a arraylist
	 * @param file directory 
	 * @throws Exception
	 * 
	 * @see java.nio.DirectoryStream
	 */
	public void buildMap(Path file) throws Exception {

		//if statement determine whether the given file is a directory or not
		//if directory, send to getDirectory() method
		if(Files.isDirectory(file)) {
			//use DirectoryStream to loop through the given directory
			DirectoryStream<Path> list = Files.newDirectoryStream(file);
				Iterator<Path> iterator = list.iterator();
				Path currentFile;
				while(iterator.hasNext()) {
					
					currentFile = iterator.next();
					buildMap(currentFile);
				}
			
		} else {
			
			String fileName = file.toString().toLowerCase();
			if(fileName.endsWith("txt") || fileName.endsWith("text")) {
				fileList.add(file);
			}
		}

		addData();

	}

	/**
	 * use TextFileStemmer to parse file content
	 * @throws Exception
	 * 
	 * {@link TextFileStemmer#stemFile(Path)}
	 */
	private void addData() throws Exception {
		
		for(Path file: fileList) {
			ArrayList<String> cleanedText = TextFileStemmer.stemFile(file);
			WordIndex oneFile = new WordIndex(file.toString());
			oneFile.addAll(cleanedText);
			
			add(oneFile);
		}
	}

	/**
	 * add the WordIndex into InvertedMap
	 * @param oneFile WordIndex data structure
	 * 
	 * @see {@link WordIndex}
	 * @see {@link InvertedMap}
	 */
	private void add(WordIndex oneFile) {
		
		for(String word: oneFile.getKeySet()) {
			
			map.put(word, oneFile.getTxt(), oneFile.getSet(word));
		}
	}

}
