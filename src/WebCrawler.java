import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Craw the seed url base on given limit number
 * @author Hao Shen
 *
 */
public class WebCrawler {

	private final HashSet<URL> allLink;
	private final WorkQueue worker;
	private final ThreadSafeInvertedIndex index;

	public WebCrawler(WorkQueue worker, ThreadSafeInvertedIndex threadSafe) {
		this.worker = worker;
		this.allLink = new HashSet<URL>();
		this.index = threadSafe;
	}

	/**
	 * Craw the given seed
	 * @param seed url seed
	 * @param limit the maximum number of url to craw
	 */
	public void craw(URL seed, int limit) {
		allLink.add(seed);
		worker.execute(new WebCrawlerTask(seed, limit));
		worker.finish();
	}

	/**
	 * Craw the url and the link in this url if the limit has not exceeded
	 * @author Hao Shen
	 *
	 */
	private class WebCrawlerTask implements Runnable {

		private final URL eachURL;
		private final int limit;

		public WebCrawlerTask(URL url, int limit) {
			this.eachURL = url;
			this.limit = limit;
		}

		@Override
		public void run() {
			try {

				var html = HTMLFetcher.fetchHTML(eachURL, 3);
				if(html == null) {
					return;
				}

				InvertedIndex temp = new InvertedIndex();
				var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
				int start = 1;
				for(String s: TextParser.parse(HTMLCleaner.stripHTML(html))) {
					temp.putIndex(stemmer.stem(s).toString(), eachURL.toString(), start++);
				}
				index.addAll(temp);

				if(allLink.size() < limit) {
					ArrayList<URL> links = LinkParser.listLinks(eachURL, LinkParser.fetchHTML(eachURL));
					for (URL link : links) {
						synchronized (allLink) {
							if (allLink.size() >= limit) {
								break;
							} else {
								if (allLink.contains(link) == false) {
									allLink.add(link);
									worker.execute(new WebCrawlerTask(link, limit));

								}
							}
						}
					}
				}
			} catch (IOException e) {
				System.err.println("Unable to read the page: " + eachURL.toString());
			}
		}
	}
}
