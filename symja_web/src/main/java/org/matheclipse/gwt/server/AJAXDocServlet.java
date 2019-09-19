package org.matheclipse.gwt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

//import com.github.rjeschke.txtmark.BlockEmitter;
//import com.github.rjeschke.txtmark.Configuration;
//import com.github.rjeschke.txtmark.Configuration.Builder;
//import com.github.rjeschke.txtmark.Processor;

public class AJAXDocServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7389567393700726482L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html; charset=UTF-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		PrintWriter out = res.getWriter();
		try {
			String value = "index";
			String pathInfo = req.getPathInfo();
			if (pathInfo != null) {
				int pos = pathInfo.lastIndexOf('/');
				if (pos == 0 && pathInfo.length() > 1) {
					value = pathInfo.substring(pos + 1).trim();
				}
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
		Set<Extension> EXTENSIONS = Collections.singleton(TablesExtension.create());
		Parser parser = Parser.builder().extensions(EXTENSIONS).build();
		Node document = parser.parse(markdownStr);
		HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();
		return renderer.render(document); // "<p>This is <em>Sparta</em></p>\n"
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
