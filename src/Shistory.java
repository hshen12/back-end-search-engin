import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class Shistory extends HttpServlet {

//	public static ArrayList<LinkedHashMap<String, ArrayList<OneResult>>> history = new ArrayList<LinkedHashMap<String, ArrayList<OneResult>>>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>Search engine</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print(" <p> Search History </p>");
		out.print(" <p>"); 
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		ArrayList<LinkedHashMap<String, ArrayList<OneResult>>> history = (ArrayList<LinkedHashMap<String, ArrayList<OneResult>>>) session.getAttribute("history");
		if(!history.isEmpty()) {
			int i = 1;
			for(LinkedHashMap<String, ArrayList<OneResult>> o: history) {
				out.printf("Search #%d<br>", i);
				for(String s: o.keySet()) {
					if(o.get(s).size() != 0) {
						out.printf("Term: %s",s);
						ArrayList<OneResult> listResult = o.get(s);
						for(OneResult r: listResult) {
							out.printf("<div style = \"background: #CFCFCF\">Where: <a href= %s>%s</a><br/>", r.getPath(), r.getPath());
							out.printf("Count: %s<br>",r.getCount());
							out.printf("Score: %s</div></p>\n",r.getScore()); 
						}
					} else {
						out.printf("<p>No results of \"%s\"</p>\n",s);
					}
				}
				i++;
			}
		} else {
			out.print("NO history");
		}
		out.print(" </p>");
		out.println("<form method = \"post\" action = \"/history/\">");
        out.println(" <input type=\"submit\" value=\"Clear\"  >");
        out.println("  </form>");
		out.printf("<a href=/ >Click here back to Home page.</a>", request.getParameter("query"));
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		ArrayList<LinkedHashMap<String, ArrayList<OneResult>>> history = (ArrayList<LinkedHashMap<String, ArrayList<OneResult>>>) session.getAttribute("history");
		history.clear();
		doGet(request,response);
	}

}
