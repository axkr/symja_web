package org.matheclipse.gwt.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * A ServletContextListener that will do necessary initialization and cleanup when the Servlet Context is initialized
 * and destroyed.
 */
public class EvalContextListener implements ServletContextListener {

	/**
	 * Initialize the database connection pool and disk cache.
	 *
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		// OS.initialize();
	}
 
	/**
	 * Clean up the database connection pool and disk cache.
	 *
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
	}
}
