package org.matheclipse.gwt.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
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
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.reflection.system.Names;

import com.github.rjeschke.txtmark.BlockEmitter;
import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Processor;
import com.github.rjeschke.txtmark.Configuration.Builder;

public class AJAXDocServlet extends HttpServlet {

	//
	// public static Cache cache = null;
	//
	// public static int APPLET_NUMBER = 1;
	//
	// public static final String UTF8 = "utf-8";
	//
	// public static final String EVAL_ENGINE = EvalEngine.class.getName();
	//
	// public static boolean INITIALIZED = false;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7389567393700726482L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/plain; charset=UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		PrintWriter out = res.getWriter();
		try {
			String name = "p";
			String value = req.getParameter(name);
			if (value == null) {
				value="index";
			}
			StringBuilder markdownBuf = new StringBuilder(1024);
			printMarkdown(markdownBuf, value);
			String markdownStr = markdownBuf.toString().trim();
			if (markdownStr.length() > 0) {
				String html = generateHTMLString(markdownBuf.toString());
				StringBuilder htmlBuf = new StringBuilder(1024);
				htmlBuf.append("<div id=\"docContent\">\n");
				htmlBuf.append(html);
				htmlBuf.append("\n</div>");
				out.println(createJSONDocString(htmlBuf.toString()));
			} else {
				out.println(createJSONDocString(
						"<p>Insert a keyword and append a '*' to search for keywords. Example: <b>Int*</b>.</p>"));
			}
			return;
		} catch (Exception e) {
			// ...
		}
	}

	public static String generateHTMLString(final String markdownStr) {
		Builder builder = Configuration.builder();
		BlockEmitter emitter = new BlockEmitter() {
			public void emitBlock(StringBuilder out, List<String> lines, String meta) {
				out.append("<pre>");
				for (final String s : lines) {
					for (int i = 0; i < s.length(); i++) {
						final char c = s.charAt(i);
						switch (c) {
						case '&':
							out.append("&amp;");
							break;
						case '<':
							out.append("&lt;");
							break;
						case '>':
							out.append("&gt;");
							break;
						default:
							out.append(c);
							break;
						}
					}
					out.append('\n');
				}
				out.append("</pre>\n");
			}

		};
		Configuration config = builder.setCodeBlockEmitter(emitter).enableSafeMode().forceExtentedProfile().build();
		return Processor.process(markdownStr, config);
	}

	public static void printMarkdown(Appendable out, String symbolName) {
		// read markdown file
		String fileName = symbolName + ".md";

		// Get file from resources folder
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();

		try {
			InputStream is = classloader.getResourceAsStream(fileName);
			if (is != null) {
				final BufferedReader f = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String line;
				while ((line = f.readLine()) != null) {
					out.append(line);
					out.append("\n");
				}
				f.close();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String createJSONDocString(String str) {
		JSONObject outJSON = new JSONObject();
		outJSON.put("content", str);
		return JSONValue.toJSONString(outJSON);
	}

}
