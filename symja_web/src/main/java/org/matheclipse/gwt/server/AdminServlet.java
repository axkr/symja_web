package org.matheclipse.gwt.server;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AdminServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5138122751860950428L;

	private static final Logger log = Logger.getLogger(AdminServlet.class.getName());

	public static int APPLET_NUMBER = 1;

	public static final String UTF8 = "utf-8";

	public static final String EVAL_ENGINE = EvalEngine.class.getName();

	public static DataSource DATA_SOURCE = null;

	public static CacheManager CACHE_MANAGER = null;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		PrintWriter out = res.getWriter();
		res.setContentType("text/plain");
		if (user == null || !userService.isUserAdmin()) {
			out.println(URLEncoder.encode("0;error;Please login as admin user!", "UTF-8"));
			return;
		}
		String name = "evaluate";
		String value = req.getParameter(name);
		if (value == null) {
			out.println(URLEncoder.encode("0;error;No input expression posted!", "UTF-8"));
			return;
		}
		if (value.length() > Short.MAX_VALUE) {
			out.println(URLEncoder.encode("0;error;Input expression to large!", "UTF-8"));
			return;
		}
		value = value.trim();
		log.warning("In::" + value);
		try {
			String result = evaluate(req, value, "", 0);
			log.warning("Out::" + result);
			out.println(result);// URLEncoder.encode(result, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String evaluate(HttpServletRequest request, String expression, String function, int counter) {
		if (expression == null || expression.length() == 0) {
			return counter + ";error;No input expression posted!";
		}
		if (expression.trim().length() == 0) {
			return counter + ";error;No input expression posted!";
		} else if (expression.length() >= Short.MAX_VALUE) {
			return counter + ";error;Input expression greater than: " + Short.MAX_VALUE + " characters!";
		}

		HttpSession session = request.getSession(); 
		StringWriter outWriter = new StringWriter();
		WriterOutputStream wouts = new WriterOutputStream(outWriter);
		PrintStream outs = new PrintStream(wouts);
		EvalEngine engine = null;
		if (session != null) {
			// engine = (EvalEngine) session.getAttribute(EVAL_ENGINE);
			// if (engine == null) {
			// ExprFactory f = new ExprFactory(new SystemNamespace());
			// PrintStream pout = new PrintStream();
			engine = new EvalEngine(session.getId(), 256, 256, outs, false);
			// session.setAttribute(EVAL_ENGINE, engine);
			// // init ThreadLocal instance:
			// EvalEngine.get();
			// } else {
			// engine.init();
			// engine.setOutPrintStream(outs);
			// engine.setSessionID(session.getId());
			// // init ThreadLocal instance:
			// EvalEngine.set(engine);
			// }
		} else {
			engine = new EvalEngine("no-session", 256, 256, outs, false);
		}

		try {
			String[] result = evaluateString(request, engine, expression, function);
//			StringBuilder buf = outWriter.toString();
			outWriter.append(result[1]);
			return counter + ";" + result[0] + ";" + outWriter.toString();
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
			return counter + ";error;Exception occurred in evaluate!";
		} finally {
			// tear down associated ThreadLocal from EvalEngine
			EvalEngine.remove();
		}
	}

	public static String[] evaluateString(HttpServletRequest request, EvalEngine engine, String inputString, String function)
			throws UnsupportedEncodingException {
//		try {
//			String packageName = PackageLoader.installPackage(engine, inputString);
//			return new String[] { "expr", "Installed package: " + packageName };
//
//		} catch (MathException se) {
//			return new String[] { "error", se.getMessage() };
//		} catch (Exception e) {
//			// error message
//			if (Config.SHOW_STACKTRACE) {
//				e.printStackTrace();
//			}
//			return new String[] { "error", "Error in evaluateString" };
//		}
		
		return new String[] { "error", "Error in evaluateString" };
	}

}
