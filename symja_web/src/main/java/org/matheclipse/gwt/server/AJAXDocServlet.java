package org.matheclipse.gwt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.matheclipse.core.builtin.SourceCodeFunctions;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.ISymbol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AJAXDocServlet extends HttpServlet {
  public static final String FUNCTIONS_PREFIX = "/functions/";

  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  private static class DocVisitor extends AbstractVisitor {

//    @Override
//    public void visit(FencedCodeBlock fencedCodeBlock) {
//      //    	System.out.println(fencedCodeBlock.getLiteral() );
//      super.visit(fencedCodeBlock);
//      Link link = new Link();
//      link.setDestination("javascript:loadDoc('/functions/Abs')");
//      link.setTitle("Abs");
//      fencedCodeBlock.prependChild(link);
//    }

    @Override
    public void visit(Link link) {
      String destination = link.getDestination();
      int index = destination.indexOf(".md");
      if (index > 0) {
        String functionName = destination.substring(0, index);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("doc/functions/" + destination);
        if (is != null) {
          destination = "javascript:loadDoc('/functions/" + functionName + "')";
        } else {
          destination = "javascript:loadDoc('/" + functionName + "')";
        }
        link.setDestination(destination);

        Node node = link.getFirstChild();
        while (node != null) {
          // A subclass of this visitor might modify the node, resulting in getNext returning a
          // different node or no
          // node after visiting it. So get the next node before visiting.
          Node next = node.getNext();
          node.accept(this);
          node = next;
        }
      }
    }
  }
  /** */
  private static final long serialVersionUID = -7389567393700726482L;

  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    doPost(req, res);
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
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
        } else if (pathInfo.startsWith("/functions/")) {
          value = pathInfo.substring(1);
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
        out.println(
            createJSONDocString(
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
    DocVisitor visitor = new DocVisitor();
    document.accept(visitor);
    HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();
    return renderer.render(document); // "<p>This is <em>Sparta</em></p>\n"
  }

  public static void printMarkdown(Appendable out, String docName) {
    // read markdown file
    String fileName = Documentation.buildDocFilename(docName);

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

        if (docName.startsWith(FUNCTIONS_PREFIX)) {
          String functionName = docName.substring(FUNCTIONS_PREFIX.length());
          String identifier = F.symbolNameNormalized(functionName);
          ISymbol symbol = Context.SYSTEM.get(identifier);
          if (symbol != null) {
            String functionURL = SourceCodeFunctions.functionURL(symbol);
            if (functionURL != null) {

              out.append("\n\n### Github");
              out.append("\n\n* [Implementation of ");
              out.append(functionName);
              out.append("](");
              out.append(functionURL);
              out.append(") ");
            }
          }
        }
        // jump back to Main documentation page
        out.append("\n\n [<< Main](index.md) ");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String createJSONDocString(String str) {
    ObjectNode outJSON = JSON_OBJECT_MAPPER.createObjectNode();
    outJSON.put("content", str);
    return outJSON.toString();
    //    JSONObject outJSON = new JSONObject();
    //    outJSON.put("content", str);
    //    return JSONValue.toJSONString(outJSON);
  }
}
