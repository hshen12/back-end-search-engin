import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * InvertedMapBuilder class
 * build the data structure 
 * @author Hao Shen
 *
 */
public class InvertedMapBuilder {

	/**
	 * Read through the given directory, find the file in the given directory
	 * @param file directory 
	 * @param index data structure
	 * @throws Exception
	 * 
	 * @see java.nio.DirectoryStream
	 * @see Files#newDirectoryStream(Path)
	 */
	public static void buildMap(Path file, InvertedIndex index) throws IOException {
		if(Files.isDirectory(file)) {
			try(DirectoryStream<Path> list = Files.newDirectoryStream(file)) {
				Iterator<Path> directoryStreamIt = list.iterator();
				while(directoryStreamIt.hasNext()) {
					buildMap(directoryStreamIt.next(), index);
				}
			}
		} else { 
			String fileName = file.toString().toLowerCase();
			if(fileName.endsWith(".txt") || fileName.endsWith(".text")) {
				stemFile(file, index);
			}
		}
	}

	/**
	 * Reads a file and put each word into the data structure
	 * 
	 * @param inputFile the input file to parse
	 * @param index data structure
	 * @throws IOException if unable to read the file 
	 * 
	 * @see #stemLine(String, Stemmer)
	 * @see InvertedMap#put(String, String, int)
	 * @see Stemmer#stem(CharSequence)
	 */
	public static void stemFile(Path inputFile, InvertedIndex index) throws IOException{
		var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		try(BufferedReader br = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)){
			String line;
			int start = 1;
			while((line = br.readLine()) != null) {
				String name = inputFile.toString();
				for(String s: TextParser.parse(line)) {
					index.putIndex(stemmer.stem(s).toString(), name, start++);
				}
			}
		}
	}
	
	/**
	 * Take in a input file and return a inverted index
	 * @param inputFile file
	 * @return inverted index
	 * @throws IOException
	 */
	public static InvertedIndex stemFile(Path inputFile) throws IOException {
		InvertedIndex temp = new InvertedIndex();
		stemFile(inputFile, temp);
		return temp;
	}
	
	
}
