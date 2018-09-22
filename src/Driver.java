import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Driver class 
 * Parses the command-line arguments to build and use an in-memory search
 * engine from files or the web.
 */
public class Driver {

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		System.out.println(Arrays.deepToString(args));
		ArgumentMap arg = new ArgumentMap(args);

		//-path
		String path = arg.getString("-path");
		
		InvertedMap map = new InvertedMap();
		Path file = null;
		InvertedMapBuilder mapBuilder = new InvertedMapBuilder(map);

		if(path != null) {
			file = Paths.get(path);
			
			if(Files.exists(file)) {
				try {
					mapBuilder.buildMap(file);
				} catch (Exception e) {
					//TODO throw all exception at driver?
					System.out.println(e.getClass());
					e.printStackTrace();
				}
			}
		}
		
		
		
		

		//-index
		String index = "";
		if(arg.hasFlag("-index")) {
			index = arg.getString("-index", "index.json");
		} else {
			index = null;
		}

		//initialize writer
		InvertedIndexWriter indexWriter = new InvertedIndexWriter(map, index);
		//if output file is required, output the map
		if(index != null) {
			indexWriter.writeJson();
		}


	}

}
