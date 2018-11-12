import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		ArgumentMap map = new ArgumentMap(args);
		int threads = 1;
		boolean threadFlag = map.hasFlag("-threads");

		WorkQueue wq = null;
		InvertedIndex index = null;
		QueryParserInterface queryParser;

		if (!threadFlag) {
			index = new InvertedIndex();
			queryParser = new QueryParser(index);
		} else {
			threads = Integer.parseInt(map.getString("-threads", "5"));
			wq = new WorkQueue(threads);
			index = new ThreadSafeInvertedIndex();
			queryParser = new MultiThreadQueryParser(wq, index);
		}

		//-path
		if(map.hasFlag("-path")) {
			Path file = map.getPath("-path");

			if(file != null && Files.exists(file)) {

				try {
					if(!threadFlag) {
						InvertedMapBuilder.buildMap(file, index);
					} else {
						MultiThreadInvertedMapBuilder.buildMap(file, index, wq);
					}
				} catch (IOException e) {
					System.err.println("Unbale to read the path or stem the file: " + file.toString() + "\n\tplease check your argument");
				}
			} else {
				if(file == null) {
					System.err.println("Missing argument for -path");
					return;
				} else {
					System.err.println("Invalid value for path flag: " + file.toString() + "\n\tplease check your argument");
					return;
				}
			}
		} else {
			System.err.println("Missing flag for -path");
		}

		//-index
		if(map.hasFlag("-index")) {
			Path indexPath = map.getPath("-i" + "ndex", Paths.get("index.json"));
			try {
				index.toIndexJSON(indexPath);
			} catch (IOException e) {
				System.err.println("Unable to print out to file: "  + indexPath.toString() + "\n\tplease check your argument.");
			}
		} 

		//-locations
		if(map.hasFlag("-locations")) {
			Path locationsPath = map.getPath("-locations", Paths.get("locations.json"));
			try {
				index.toLocationsJSON(locationsPath);
			} catch(IOException e) {
				System.err.println("Unable to print out to file: " + locationsPath.toString() + "\n\tplease check your argument.");
			}
		}

		//-search
		if(map.hasFlag("-search")) {
			Path queryFile = map.getPath("-search");
			boolean exact = map.hasFlag("-exact");

			if(Files.exists(queryFile)) {
				try {
					if(!threadFlag) {
						queryParser.stemQuery(queryFile, exact);
					} else {
						queryParser.stemQuery(queryFile, exact);
					}
				} catch(IOException e) {
					System.err.println("Unable to search on: " + queryFile.toString() + "\n\tplease check your argument");
				}

			} else {
				System.err.println("Invalid value for search flag: " + queryFile.toString() + "\n\tplease check your argument");
			}
		}

		//-results
		if(map.hasFlag("-results")) {
			Path resultPath = map.getPath("-results", Paths.get("results.json"));
			try {
				queryParser.toSearchResult(resultPath);
			} catch(IOException e) {
				System.err.println("Unable to generate the search result file: " + resultPath.toString() + "\n\tplease check your argument");
			}
		}
	}
}