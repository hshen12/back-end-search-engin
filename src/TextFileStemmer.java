import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;


public class TextFileStemmer {

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 * Uses the English {@link SnowballStemmer.ALGORITHM} for stemming.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see SnowballStemmer.ALGORITHM#ENGLISH
	 * @see #stemLine(String, Stemmer)
	 */
	public static List<String> stemLine(String line) {
		// This is provided for you.
		return stemLine(line, new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH));
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static List<String> stemLine(String line, Stemmer stemmer) {
		
		var parser = TextParser.parse(line);
		var result = new ArrayList<String>();
		
		
		for(String s: parser) {
			if(!s.trim().isEmpty()) {
				result.add(stemmer.stem(s).toString());
			} 
		}
		
		return result;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then writes that line to a new file.
	 *
	 * @param inputFile the input file to parse
	 * @return ArrayList of String in the given inputFile
	 * @throws IOException if unable to read or write to file
	 *
	 * @see #stemLine(String)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> stemFile(Path inputFile) throws Exception{
		
		ArrayList<String> result = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile.toString()));
			
			String line;
			
			while((line = br.readLine()) != null) {
				
				List<String> oneLine = stemLine(line);
				
				for(String s: oneLine) {
					result.add(s);
				}
			}
			br.close();
			
		
		return result;	
	}
	
	/*
	 * TODO 
	 * Efficiency...
	 * 
	 * Loop through the entire file of words, add to a list
	 * Loop through the entire list and add to a word index
	 * Loop through and add from your word index to your indexmap
	 * 
	 * Actually create a version of this method in your build that immediately adds
	 */
}
