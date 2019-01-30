import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Index page for the search engin
 * @author Hao Shen
 *
 */
@SuppressWarnings("serial")
public class index extends HttpServlet {

	private final ThreadSafeInvertedIndex index;
	private final WorkQueue worker;
	private final LinkedHashMap<String, ArrayList<OneResult>> searchResult;

	public index(ThreadSafeInvertedIndex threadSafe, WorkQueue worker) {
		super();
		this.index = threadSafe;
		this.worker = worker;
		this.searchResult = new LinkedHashMap<String, ArrayList<OneResult>>();
	}

	/**
	 * Present a empty search page
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>Search engine</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("<a style = \"float: right; color: #000\"href = \"/history\">Search History</a><br>");
		out.print("Hello, how is your day:" + 
				"<form method = \"post\" action = \"/\">"+
				"          <input type=\"text\" placeholder=\"query\" name = \"query\"><br>\n" + 
				"			<input type=\"radio\" name=\"searchMode\" value=\"partial\" checked> partial search<br>\n" + 
				"			<input type=\"radio\" name=\"searchMode\" value=\"exact\"> exact search<br>\n" + 
				"          <input type=\"submit\" value=\"Search\"  class=\"button\">\n" + 
				"</form>");
		//      HttpSession session = request.getSession();
		//      session.setAttribute("result", new ArrayList<LinkedHashMap<String, ArrayList<OneResult>>>());
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * Present the search base on query
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		String query = request.getParameter("query").trim();
		if(!query.equals("")) {
			//if there is word in the query
			searchPage(request, response, query);
		} else {
			//if no query word provided
			response.sendRedirect("/");
		}
	}

	/**
	 * Present a search page and the search result below
	 * @param request
	 * @param response
	 * @param query query word
	 * @throws IOException
	 */
	private void searchPage(HttpServletRequest request, HttpServletResponse response, String query) throws IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>Search engine</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("<a style = \"float: right; color: #000\"href = \"/history\">Search History</a><br>");
		out.print("Hello, how is your day:" + 
				"<form method = \"post\" action = \"/\">"+
				"          <input type=\"text\" placeholder=\"query\" name = \"query\"><br>\n" + 
				"			<input type=\"radio\" name=\"searchMode\" value=\"partial\" checked> partial search<br>\n" + 
				"			<input type=\"radio\" name=\"searchMode\" value=\"exact\"> exact search<br>\n" + 
				"          <input type=\"submit\" value=\"Search\"  class=\"button\">\n" + 
				"</form>");

		boolean exact = request.getParameter("searchMode") == "partial" ? false : true;
		String[] queries = query.split(" ");
		search(queries, exact);
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		ArrayList<LinkedHashMap<String, ArrayList<OneResult>>> result = (ArrayList<LinkedHashMap<String, ArrayList<OneResult>>>) session.getAttribute("result");
		session.setAttribute("history", result);
		for(String s: queries) {
			if(searchResult.get(s).size() != 0) {
				out.printf("<p>Term: %s",s);
				ArrayList<OneResult> listResult = searchResult.get(s);
				for(OneResult r: listResult) {
					out.printf("<div style = \"background: #CFCFCF\">Where: <a href= %s>%s</a><br/>", r.getPath(), r.getPath());
					out.printf("Count: %s<br>",r.getCount());
					out.printf("Score: %s</div></p>\n",r.getScore()); 
				}
			} else {
				out.printf("<p>No results of \"%s\"</p>\n",s);
			}
		}
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * Perform a search use multi threading
	 * @param query query word array
	 * @param partial partial or exact search
	 */
	private void search(String[] query, boolean partial) {
		for(String oneQuery: query) {
			worker.execute(new SearchTask(oneQuery, partial));
		}
		worker.finish();
	}

	/**
	 * Perform a search for one query word
	 * @author Hao Shen
	 *
	 */
	private class SearchTask implements Runnable {

		private final String oneQuery;
		private final boolean exact;

		public SearchTask(String oneQuery, boolean exact) {
			this.oneQuery = oneQuery;
			this.exact = exact;
		}

		@Override
		public void run() {
			var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			String word = stemmer.stem(oneQuery).toString();

			if(!word.trim().isEmpty()) {
				synchronized(searchResult) {
					if(searchResult.containsKey(word)) {
						return;
					}
				}

				ArrayList<OneResult> result;
				if(exact) {
					result = index.exactSearch(word);
				} else {
					result = index.partialSearch(word);
				}
				synchronized(searchResult) {
					searchResult.put(word, result);
				}
			}
		}
	}

}
