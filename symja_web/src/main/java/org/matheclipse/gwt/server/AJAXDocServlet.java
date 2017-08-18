package org.matheclipse.gwt.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.matheclipse.core.eval.EvalEngine;

public class AJAXDocServlet extends HttpServlet {
	private static final long serialVersionUID = 6265703737413093134L;

	private static final Logger log = Logger.getLogger(AJAXDocServlet.class.getName());

	// private static final boolean UNIT_TEST = false;

	private static final boolean DEBUG = true;

	// private static final boolean USE_MEMCACHE = false;

	private static final int MAX_NUMBER_OF_VARS = 100;

	public static Cache cache = null;

	public static int APPLET_NUMBER = 1;

	public static final String UTF8 = "utf-8";

	public static final String EVAL_ENGINE = EvalEngine.class.getName();

	public static boolean INITIALIZED = false;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/plain; charset=UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		PrintWriter out = res.getWriter();
		try {
			String name = "query";
			String value = req.getParameter(name);
			if (value == null) {
				out.println(createJSONDocString("No input expression posted!"));
				return;
			}
			out.println(createJSONDocString("Test content"));
		} catch (Exception e) { 
		}
	}

	private static String createJSONDocString(String str) {
		JSONArray temp;
		JSONObject outJSON = new JSONObject();
		outJSON.put("content", "<div id=\"docContent\">" + str + "</div>");
		return JSONValue.toJSONString(outJSON);
	}

}
