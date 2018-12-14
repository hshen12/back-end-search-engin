import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
		boolean urlFlag = map.hasFlag("-url");
		boolean portFlag = map.hasFlag("-port");

		WorkQueue worker = null;
		InvertedIndex index = null;
		ThreadSafeInvertedIndex threadSafe = null;
		QueryParserInterface queryParser;
		WebCrawler crawler = null;
		SearchEngin engin = null;

		if(urlFlag || portFlag) {
			threadFlag = true;
		}

		if (!threadFlag) {
			index = new InvertedIndex();
			queryParser = new QueryParser(index);
		} else {
			threads = Integer.parseInt(map.getString("-threads", "5"));
			worker = new WorkQueue(threads);
			threadSafe = new ThreadSafeInvertedIndex();
			index = threadSafe;
			queryParser = new MultiThreadQueryParser(worker, threadSafe);
		}

		//-url
		if(urlFlag) {
			crawler = new WebCrawler(worker, threadSafe);
			String seedStr = map.getString("-url");
			URL seed;
			int limit;
			try {
				seed = new URL(map.getString("-url"));
				limit = Integer.parseInt(map.getString("-limit", "50"));
			} catch (MalformedURLException e) {
				System.err.println("Illegal url: " + seedStr + " please check your argument");
				return;
			} catch (NumberFormatException numEx) {
				System.err.println("Illegal limit number: " + map.getString("-limit"));
				return;
			}

			crawler.craw(seed, limit);

		} else if(map.hasFlag("-path")) {
			Path file = map.getPath("-path");

			if(file != null && Files.exists(file)) {

				try {
					if(!threadFlag) {
						InvertedMapBuilder.buildMap(file, index);
					} else {
						MultiThreadInvertedMapBuilder.buildMap(file, threadSafe, worker);
					}
				} catch (IOException e) {
					System.err.println("Unbale to read the path or stem the file: " + file.toString() + "\n\tplease check your argument");
				}
			} else {
				if(file == null) {
					System.err.println("Missing argument for -path");
				} else {
					System.err.println("Invalid value for path flag: " + file.toString() + "\n\tplease check your argument");
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

		//-port
		if(portFlag) {
			int port;
			try {
				port = Integer.parseInt(map.getString("-port", "8080"));
			} catch (NumberFormatException numEx) {
				System.err.println("Illegal port number: " + map.getString("-port"));
				return;
			}
			engin = new SearchEngin(threadSafe, port, worker);
			try {
				engin.startServlet();
			} catch (Exception e) {
				System.err.println("Exception happened while search!!!");
			}

		} else if(map.hasFlag("-search")) {
			//-search
			Path queryFile = map.getPath("-search");
			boolean exact = map.hasFlag("-exact");

			if(Files.exists(queryFile)) {
				try {
					queryParser.stemQuery(queryFile, exact);
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

		if(worker != null) {
			worker.shutdown();
		}
	}
}
