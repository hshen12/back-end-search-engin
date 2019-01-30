import org.eclipse.jetty.server.Server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Setting up the search engin
 * @author Hao Shen
 *
 */
public class SearchEngin {

	private final ThreadSafeInvertedIndex threadSafe;
	private final int port;
	private final WorkQueue worker;

	public SearchEngin(ThreadSafeInvertedIndex threadSafe, int port, WorkQueue worker) {
		this.threadSafe = threadSafe;
		this.port = port;
		this.worker = worker;
	}

	/**
	 * Initialize the servlet and start the server
	 * @throws Exception
	 */
	public void startServlet() throws Exception {
		Server server = new Server(port);
		ServletContextHandler context = new ServletContextHandler();
		
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new index(threadSafe, worker)), "/");
		
		ServletContextHandler historyContext = new ServletContextHandler();
		
		historyContext.setContextPath("/history");
		historyContext.addServlet(new ServletHolder(new Shistory()), "/");
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { historyContext, context });
		
		server.setHandler(handlers);
		server.start();
		server.join();
	}
}
