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
		InvertedIndex index = new InvertedIndex();
		WorkQueue wq = null;
		int thread = 1;
		boolean threadFlag = map.hasFlag("-threads");
		
		//-path
		if(map.hasFlag("-path")) {
			Path file = map.getPath("-path");

			if(file != null && Files.exists(file)) {
				if(!threadFlag) {
					try {
						InvertedMapBuilder.buildMap(file, index);
					} catch (IOException e) {
						System.err.println("Unbale to read the path or stem the file: " + file.toString() + "\n\tplease check your argument");
					}
				} else {
					thread = Integer.parseInt(map.getString("-threads", "5"));
					index = new ThreadSafeInvertedIndex();
					wq = new WorkQueue(thread);
					try {
						MultiThreadInvertedMapBuilder.buildMap(file, index, wq);
					} catch(Exception e) {
						System.err.println("Unable to read the path " + file.toString() +"\n\tplease check your argument");
					}
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
			Path indexPath = map.getPath("-index", Paths.get("index.json"));
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

		SearchResult searchResult = new SearchResult();
		//-search
		if(map.hasFlag("-search")) {
			Path queryFile = map.getPath("-search");
			boolean exact = map.hasFlag("-exact");

			if(Files.exists(queryFile)) {
				if(!threadFlag) {
					try {
						Search.stemQuery(index, queryFile, exact, searchResult);
					} catch (IOException e) {
						System.err.println("Unable to search on: " + queryFile.toString() + "\n\tplease check your argument");
					}
				} else {
					searchResult = new ThreadSafeSearchResult();
					try {
						MultiThreadSearch.stemQuery(index, queryFile, exact, searchResult, wq);
					} catch(IOException e) {
						System.err.println("Unable to search on: "+ queryFile.toString() + "\n\tplease check your argument");
					}
					wq.shutdown();
				}
			} else {
				System.err.println("Invalid value for search flag: " + queryFile.toString() + "\n\tplease check your argument");
			}
		}

		//-results
		if(map.hasFlag("-results")) {
			Path resultPath = map.getPath("-results", Paths.get("results.json"));
			try {
				searchResult.toSearchResult(resultPath);
			} catch(IOException e) {
				System.err.println("Unable to generate the search result file: " + resultPath.toString() + "\n\tplease check your argument");
			}
		}
	}
}
