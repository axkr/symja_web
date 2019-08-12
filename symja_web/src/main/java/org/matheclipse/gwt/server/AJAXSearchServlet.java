package org.matheclipse.gwt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.cache.Cache;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;

public class AJAXSearchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7668302968904993646L;

	public static Cache cache = null;

	public static int APPLET_NUMBER = 1;

	public static final String UTF8 = "utf-8";

	public static final String EVAL_ENGINE = EvalEngine.class.getName();

	public static boolean INITIALIZED = false;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html; charset=UTF-8");
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
			StringBuilder markdownBuf = new StringBuilder(1024);
			findDocumentation(markdownBuf, value);
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

	private static void findDocumentation(Appendable out, String trimmedInput) {
		String name = trimmedInput;// .substring(1);
		IAST list = IOFunctions.getNamesByPrefix(name);
		try {
			if (list.size() != 2) {
				for (int i = 1; i < list.size(); i++) {

					out.append(list.get(i).toString());
					if (i != list.size() - 1) {
						out.append(", ");
					}
				}
			}
			out.append("\n");
			if (list.size() == 2) {
				printMarkdown(out, list.get(1).toString());
			} else if (list.size() == 1
					&& (name.equals("D") || name.equals("E") || name.equals("I") || name.equals("N"))) {
				printMarkdown(out, name);
			}
		} catch (IOException e) {
		}
	}

	public static String generateHTMLString(final String markdownStr) {

		Parser parser = Parser.builder().build();
		Node document = parser.parse(markdownStr);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);

		// Builder builder = Configuration.builder();
		// BlockEmitter emitter = new BlockEmitter() {
		// public void emitBlock(StringBuilder out, List<String> lines, String meta) {
		// out.append("<pre>");
		// for (final String s : lines) {
		// for (int i = 0; i < s.length(); i++) {
		// final char c = s.charAt(i);
		// switch (c) {
		// case '&':
		// out.append("&amp;");
		// break;
		// case '<':
		// out.append("&lt;");
		// break;
		// case '>':
		// out.append("&gt;");
		// break;
		// default:
		// out.append(c);
		// break;
		// }
		// }
		// out.append('\n');
		// }
		// out.append("</pre>\n");
		// }
		//
		// };
		// Configuration config = builder.setCodeBlockEmitter(emitter).enableSafeMode().forceExtentedProfile().build();
		// return Processor.process(markdownStr, config);
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
