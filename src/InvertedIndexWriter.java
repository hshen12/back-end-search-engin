import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * go through the data structure and write the data structure to JSON file use BufferedWriter
 * 
 * @see java.io.BufferedWriter
 * @see java.nio.charset.StandardCharsets
 * @author Hao Shen
 *
 */
public class InvertedIndexWriter {
	
	private InvertedMap invertedMap;
	private String index;
	
	/**
	 * constructor
	 * @param map InvertedMap data structure
	 * @param index the output path 
	 * 
	 * @see {@link InvertedMap}
	 */
	public InvertedIndexWriter(InvertedMap map, String index) {
		this.invertedMap = map;
		this.index = index;
		
	}

	/**
	 * use the map to write output to the output file
	 * @throws Exception
	 * 
	 * @see java.io.BufferedWriter
	 * @see java.nio.charset.StandardCharsets
	 * @see java.util.Iterator
	 */
	public void writeJson() {
		
		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(index), StandardCharsets.UTF_8)) {
			Iterator<String> wordIterator = invertedMap.getWordSet().iterator();
			
			TreeJSONWriter.indent(0, bw);
			bw.write("{");
			bw.newLine();
			while(wordIterator.hasNext()) {
				String word = wordIterator.next();
				
				TreeJSONWriter.indent(1, bw);
				String wordOutput = "\"" + word + "\": {";
				bw.write(wordOutput);
				bw.newLine();
				
				var pathPosition = invertedMap.getPathSet(word);
				Iterator<String> pathIterator = pathPosition.iterator();
				
				while(pathIterator.hasNext()) {
					String path = pathIterator.next();
//					path = path.replace("[\\]", "/");
					TreeJSONWriter.indent(2, bw);
					String pathOutput = "\"" + path + "\": [";
					bw.write(pathOutput);
					bw.newLine();
					
					var wordIndexSet = invertedMap.getPosition(word, path);
					Iterator<Integer> wordIndexIterator = wordIndexSet.iterator();
					
					while(wordIndexIterator.hasNext()) {
						TreeJSONWriter.indent(3, bw);
						String index = wordIndexIterator.next().toString();
						if(wordIndexIterator.hasNext()) {
							index += ",";
						}
						bw.write(index);
						bw.newLine();
					}
					
					TreeJSONWriter.indent(2, bw);
					String indexBraket = "]";
					if(pathIterator.hasNext()) {
						indexBraket += ",";
					}
					bw.write(indexBraket);
					bw.newLine();
				}
				
				TreeJSONWriter.indent(1, bw);
				String wordBraket = "}";
				if(wordIterator.hasNext()) {
					wordBraket += ",";
				}
				bw.write(wordBraket);
				bw.newLine();
			}
			
			TreeJSONWriter.indent(0, bw);
			bw.write("}");
			bw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
}
