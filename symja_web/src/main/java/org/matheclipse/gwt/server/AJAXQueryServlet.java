package org.matheclipse.gwt.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
// import org.matheclipse.core.eval.LastCalculationsHistory;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.util.WriterOutputStream;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.JSBuilder;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.logging.ThreadLocalNotifyingAppender;
import org.matheclipse.logging.ThreadLocalNotifyingAppender.ThreadLocalNotifierClosable;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
// import com.google.appengine.api.memcache.ErrorHandlers;
// import com.google.appengine.api.memcache.MemcacheService;
// import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AJAXQueryServlet extends HttpServlet {

  protected static final String VISJS_IFRAME = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + //
          "\n"
          + //
          "<!DOCTYPE html PUBLIC\n"
          + //
          "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + //
          "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n"
          + //
          "\n"
          + //
          "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + //
          "<head>\n"
          + //
          "<meta charset=\"utf-8\">\n"
          + //
          "<title>VIS-NetWork</title>\n"
          + //
          "\n"
          + //
          "  <script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/npm/vis-network@6.0.0/dist/vis-network.min.js\"></script>\n"
          + //
          "</head>\n"
          + //
          "<body>\n"
          + //
          "\n"
          + //
          "<div id=\"vis\" style=\"width: 600px; height: 400px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden\">\n"
          + //
          "<script type=\"text/javascript\">\n"
          + //
          "`1`\n"
          + //
          "  var container = document.getElementById('vis');\n"
          + //
          "  var data = {\n"
          + //
          "    nodes: nodes,\n"
          + //
          "    edges: edges\n"
          + //
          "  };\n"
          + //
          "`2`\n"
          + //
          "  var network = new vis.Network(container, data, options);\n"
          + //
          "</script>\n"
          + //
          "</div>\n"
          + //
          "</body>\n"
          + //
          "</html>"; //

  protected static final String HTML_IFRAME = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + //
          "\n"
          + //
          "<!DOCTYPE html PUBLIC\n"
          + //
          "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + //
          "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n"
          + //
          "\n"
          + //
          "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + //
          "<head>\n"
          + //
          "<meta charset=\"utf-8\">\n"
          + //
          "<title>HTML</title>\n"
          + //
          "</head>\n"
          + //
          "<body>\n"
          + //
          "`1`\n"
          + //
          "</body>\n"
          + //
          "</html>"; //

  // public final static Cache<String, String[]> INPUT_CACHE =
  // CacheBuilder.newBuilder().maximumSize(500).build();

  private static final int HALF_MEGA = 1024 * 500;

  private static final String USER_DATA_ENTITY = "USER_DATA";

  private static final String SESSION_DATA_ENTITY = "SESSION_DATA";

  private static final long serialVersionUID = 6265703737413093134L;

  private static final Logger log = Logger.getLogger(AJAXQueryServlet.class.getName());

  public static final String UTF8 = "utf-8";

  public static final String EVAL_ENGINE = EvalEngine.class.getName();

  public static volatile boolean INITIALIZED = false;

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
      if (req == null) {
        out.println(JSONBuilder.createJSONErrorString("No input expression posted!"));
        return;
      }
      String name = "query";
      String value = req.getParameter(name);
      if (value == null) {
        out.println(JSONBuilder.createJSONErrorString("No input expression posted!"));
        return;
      }

      String numericModeValue = req.getParameter("mode");
      if (numericModeValue == null) {
        numericModeValue = "";
      }

      String functionValue = req.getParameter("function");
      if (functionValue == null) {
        functionValue = "";
      }
      value = value.trim();
      if (value.length() > Short.MAX_VALUE) {
        out.println(JSONBuilder.createJSONErrorString("Input expression to large!"));
        return;
      }

      String result = evaluate(req, value, numericModeValue, functionValue, 0);
      out.println(result);
    } catch (Exception e) {
      e.printStackTrace();
      String msg = e.getMessage();
      if (msg != null) {
        out.println(JSONBuilder.createJSONErrorString("Exception: " + msg));
        return;
      }
      out.println(JSONBuilder.createJSONErrorString("Exception: " + e.getClass().getSimpleName()));
      return;
    }
  }

  public static String evaluate(
      HttpServletRequest request,
      String expression,
      String numericMode,
      String function,
      int counter) {
    if (expression == null || expression.length() == 0) {
      return JSONBuilder.createJSONErrorString("No input expression posted!");
    }
    if (expression.trim().length() == 0) {
      return JSONBuilder.createJSONErrorString("No input expression posted!");
    } else if (expression.length() >= Short.MAX_VALUE) {
      return JSONBuilder.createJSONErrorString(
          "Input expression greater than: " + Short.MAX_VALUE + " characters!");
    }

    EvalEngine engine = null;
    String[] result = null;
    PrintStream outs = null;
    PrintStream errors = null;
    UserService userService = UserServiceFactory.getUserService();
    HttpSession session = request.getSession();
    try {
      if (userService.isUserLoggedIn()) {
        final StringWriter outWriter = new StringWriter();
        WriterOutputStream wouts = new WriterOutputStream(outWriter);
        outs = new PrintStream(wouts);
        final StringWriter errorWriter = new StringWriter();
        WriterOutputStream werrors = new WriterOutputStream(errorWriter);
        errors = new PrintStream(werrors);
        User user = userService.getCurrentUser();
        log.warning("(" + user.getEmail() + ") In::" + expression);
        engine = new EvalEngine(user.getEmail(), 256, 256, outs, errors, true);
        EvalEngine.set(engine);
        if (getEntity(user, engine) == null) {
          engine = new EvalEngine("no-session", 256, 256, outs, errors, true);
          EvalEngine.set(engine);
        }
        engine.setOutListDisabled(false, (short) 100);
        engine.setPackageMode(false);
        result = evaluateString(engine, expression, numericMode, function, outWriter, errorWriter);
      } else {
        // result = INPUT_CACHE.getIfPresent(expression);
        // if (result != null) {
        // return result[1].toString();
        // }
        log.warning("(" + session.getId() + ") In::" + expression);
        final StringWriter outWriter = new StringWriter();
        WriterOutputStream wouts = new WriterOutputStream(outWriter);
        outs = new PrintStream(wouts);
        final StringWriter errorWriter = new StringWriter();
        WriterOutputStream werrors = new WriterOutputStream(errorWriter);
        errors = new PrintStream(werrors);
        engine = new EvalEngine(session.getId(), 256, 256, outs, errors, true);
        EvalEngine.set(engine);
        if (getEntity(session, engine) == null) {
          engine = new EvalEngine(session.getId(), 256, 256, outs, errors, true);
          EvalEngine.set(engine);
        }
        engine.setOutListDisabled(false, (short) 100);
        engine.setPackageMode(false);
        result = evaluateString(engine, expression, numericMode, function, outWriter, errorWriter);
        // INPUT_CACHE.put(expression, result);
      }
    } finally {
      if (userService.isUserLoggedIn()) {
        User user = userService.getCurrentUser();
        if (!putEntity(user, engine)) {
          // TODO error message
          return JSONBuilder.createJSONError("User data limit: " + HALF_MEGA + " bytes exceeded")[
              1];
        }
      } else {
        if (!putEntity(session, engine)) {
          // TODO error message
          return JSONBuilder.createJSONError(
              "Session data limit: " + HALF_MEGA + " bytes exceeded")[1];
        }
      }
      if (outs != null) {
        outs.close();
      }
      if (errors != null) {
        errors.close();
      }
      // tear down associated ThreadLocal from EvalEngine
      EvalEngine.remove();
    }
    if (result == null) {
      return JSONBuilder.createJSONError("Calculation result is undefined")[1];
    }
    return result[1].toString();
  }

  private static EvalEngine getEntity(User user, EvalEngine engine) {
    // MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    // syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    //
    // Context context = (Context) syncCache.get(user.getUserId() + "_c");
    // if (context != null) {
    // engine.getContextPath().setGlobalContext(context);
    // LastCalculationsHistory lch = (LastCalculationsHistory) syncCache.get(user.getUserId() +
    // "_h");
    // if (lch != null) {
    // engine.setOutListDisabled(lch);
    // }
    // return engine;
    // }

    Key pageKey = KeyFactory.createKey(USER_DATA_ENTITY, user.getUserId());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity;

    try {
      entity = datastore.get(pageKey);
      ByteArrayInputStream bais =
          new ByteArrayInputStream(((Blob) entity.getProperty("context")).getBytes());
      ObjectInputStream ois = new ObjectInputStream(bais);
      Context c = (Context) ois.readObject();
      if (c != null) {
        engine.getContextPath().setGlobalContext(c);
      }
      ois.close();
      bais.close();

      // bais = new ByteArrayInputStream(((Blob) entity.getProperty("history")).getBytes());
      // ois = new ObjectInputStream(bais);
      // LastCalculationsHistory lch = (LastCalculationsHistory) ois.readObject();
      // if (lch != null) {
      // engine.setOutListDisabled(lch);
      // }
      // ois.close();
      // bais.close();
    } catch (EntityNotFoundException nefe) {
      //
    } catch (Exception rex) {
      // rex.printStackTrace();
      log.warning("getEntity::ioexception 1");
    }
    return engine;
  }

  private static EvalEngine getEntity(HttpSession session, EvalEngine engine) {
    if (session != null) {
      Key pageKey = KeyFactory.createKey(SESSION_DATA_ENTITY, session.getId());
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity entity;

      ByteArrayInputStream bais = null;
      ObjectInputStream ois = null;
      try {
        entity = datastore.get(pageKey);
        bais = new ByteArrayInputStream(((Blob) entity.getProperty("context")).getBytes());
        ois = new ObjectInputStream(bais);
        Context c = (Context) ois.readObject();
        if (c != null) {
          engine.getContextPath().setGlobalContext(c);
        }
      } catch (EntityNotFoundException nefe) {
        //
      } catch (Exception rex) {
        rex.printStackTrace();
        log.warning("getEntity::ioexception 2");
      } finally {
        try {
          if (ois != null) {
            ois.close();
          }
          if (bais != null) {
            bais.close();
          }
        } catch (IOException e) {
          log.severe("getEntity::stream close problem");
        }
      }
    }
    return engine;
  }

  private static boolean putEntity(User user, EvalEngine engine) {
    Entity page = new Entity(USER_DATA_ENTITY, user.getUserId());
    page.setProperty("creator", user);
    Serializable context = (Serializable) engine.getContextPath().getGlobalContext();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    boolean stored = true;
    try {
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(context);
      if (baos.size() < HALF_MEGA) {
        page.setProperty("context", new Blob(baos.toByteArray()));
      } else {
        return false;
      }
      oos.close();
      baos.close();
    } catch (Exception ex) {
      // ex.printStackTrace();
      log.warning("putEntity::ioexception 3");
      return false;
    }

    // Serializable history = (Serializable) engine.getOutList();
    // baos = new ByteArrayOutputStream();
    // try {
    // ObjectOutputStream oos = new ObjectOutputStream(baos);
    // oos.writeObject(history);
    // if (baos.size() < HALF_MEGA) {
    // page.setProperty("history", new Blob(baos.toByteArray()));
    // } else {
    // return false;
    // }
    // oos.close();
    // baos.close();
    // } catch (IOException ioexception) {
    // log.warning("putEntity::ioexception 2");
    // return false;
    // }

    // MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    // syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    // syncCache.put(user.getUserId() + "_c", context);
    // syncCache.put(user.getUserId() + "_h", history);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(page);
    return stored;
  }

  private static boolean putEntity(HttpSession session, EvalEngine engine) {
    if (session != null) {
      Entity page = new Entity(SESSION_DATA_ENTITY, session.getId());
      page.setProperty("creator", session.getId());
      Serializable context = (Serializable) engine.getContextPath().getGlobalContext();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = null;
      boolean stored = true;
      try {
        oos = new ObjectOutputStream(baos);
        oos.writeObject(context);
        if (baos.size() < HALF_MEGA) {
          page.setProperty("context", new Blob(baos.toByteArray()));
        } else {
          return false;
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        log.warning("putEntity::ioexception 4");
        return false;
      } finally {
        try {
          if (oos != null) {
            oos.close();
          }
          if (baos != null) {
            baos.close();
          }
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(page);
      return stored;
    }
    return true;
  }

  // private static boolean saveModifiedUserSymbols(EvalEngine engine) {
  // UserService userService = UserServiceFactory.getUserService();
  // if (userService.getCurrentUser() != null) {
  // User user = userService.getCurrentUser();
  // if (user != null) {
  // try {
  // UserDataEntity userData = UserDataService.findByUserId(user);
  // if (userData == null) {
  // userData = new UserDataEntity(user);
  // UserDataService.save(userData);
  // }
  // Set<ISymbol> modifiedSymbols = engine.getModifiedVariables();
  // for (ISymbol symbol : modifiedSymbols) {
  // int attributes = symbol.getAttributes();
  // String source;
  //
  // source = symbol.definitionToString();
  // if (source.length() > Short.MAX_VALUE) {
  // return false;
  // }
  // UserSymbolEntity symbolEntity = new UserSymbolEntity(user, symbol.toString(), source,
  // attributes);
  // UserSymbolEntity newSymbolEntity = UserSymbolService.modify(symbolEntity);
  // if (newSymbolEntity != null) {
  // userData.incSymbolCounter();
  // if (userData.getSymbolCounter() > MAX_NUMBER_OF_VARS) {
  // UserSymbolService.delete(newSymbolEntity);
  // userData.decSymbolCounter();
  // return false;
  // }
  // }
  // }
  // UserDataService.update(userData, new Date());
  // } catch (IOException e) {
  // if (DEBUG) {
  // e.printStackTrace();
  // }
  // return false;
  // }
  // }
  // }
  // return true;
  // }

  public static String[] evaluateString(
      EvalEngine engine,
      final String inputString,
      final String numericMode,
      final String function,
      StringWriter outWriter,
      StringWriter errorWriter) {
    boolean SIMPLE_SYNTAX = true;
    String input = inputString.trim();
    if (input.length() > 1 && input.charAt(0) == '?') {
      IExpr doc = Documentation.findDocumentation(input);
      return JSONBuilder.createJSONResult(engine, doc, outWriter, errorWriter);
    }
    WriterOutputStream wouts = new WriterOutputStream(outWriter);
    WriterOutputStream werrors = new WriterOutputStream(errorWriter);
    try (PrintStream outs = new PrintStream(wouts);
        PrintStream errors = new PrintStream(werrors);
        ThreadLocalNotifierClosable c = setLogEventNotifier(outs, errors); ) {
      ExprParser parser = new ExprParser(engine, SIMPLE_SYNTAX);
      // throws SyntaxError exception, if syntax isn't valid
      IExpr inExpr = parser.parse(input);
      if (inExpr != null) {
        long numberOfLeaves = inExpr.leafCount();
        if (numberOfLeaves > Config.MAX_INPUT_LEAVES) {
          return JSONBuilder.createJSONError("Input expression too big!");
        }
        if (numericMode.equals("N")) {
          inExpr = F.N(inExpr);
        }
        if (inExpr instanceof IAST) {
          IAST ast = (IAST) inExpr;
          ISymbol sym = ast.topHead();
          if (sym.toString().equalsIgnoreCase("UserVariables")) {
            UserService userService = UserServiceFactory.getUserService();
            if (userService.getCurrentUser() != null) {
              User user = userService.getCurrentUser();
              if (user != null) {
                return listUserVariables(user.getUserId());
              }
            }
          }
        }
        // inExpr contains the user input from the web interface in
        // internal format now
        StringWriter outBuffer = new StringWriter();
        IExpr outExpr;
        // if (USE_MEMCACHE) {
        // outExpr = getFromMemcache(inExpr);
        // if (outExpr != null) {
        // if (!outExpr.equals(F.Null)) {
        // OutputFormFactory.get().convert(outBuffer, outExpr);
        // return createOutput(outBuffer, null, engine, function);
        // }
        // }
        // }

        //        outExpr = MathEvaluator.eval(engine, outBuffer, inExpr);
        outExpr = evalTopLevel(engine, outBuffer, inExpr);
        engine.addInOut(inExpr, outExpr);

        // if (USE_MEMCACHE) {
        // if (inExpr != outExpr && outExpr != null) { // compare
        // // pointers
        // putToMemcache(inExpr, outExpr);
        // }
        // }

        if (outExpr != null) {
          if (outExpr.isAST(S.Graphics)) {
            outExpr = F.Show(outExpr);
          } else if (outExpr.isAST(S.Graphics3D)) {
            StringBuilder buf = new StringBuilder();
            if (GraphicsFunctions.renderGraphics3D(buf, (IAST) outExpr, engine)) {
              try {
                return JSONBuilder.createGraphics3DIFrame(
                    JSBuilder.GRAPHICS3D_IFRAME_TEMPLATE, buf.toString());
              } catch (Exception ex) {
                if (FEConfig.SHOW_STACKTRACE) {
                  ex.printStackTrace();
                }
              }
            }
            //            IExpr expressionJSON =
            //                engine.evaluate(F.ExportString(F.N(outExpr),
            // F.stringx("ExpressionJSON")));
            //            if (expressionJSON.isString()) {
            //              String jsonStr = expressionJSON.toString();
            //              try {
            //                String html = GRAPHICS3D_IFRAME;
            //                html = StringUtils.replace(html, "`1`", jsonStr);
            //                html = StringEscapeUtils.escapeHtml4(html);
            //                return JSONBuilder.createJSONJavaScript(
            //                    "<iframe srcdoc=\""
            //                        + html
            //                        + "\" style=\"display: block; width: 100%; height: 100%;
            // border: none;\" ></iframe>");
            //              } catch (Exception ex) {
            //                if (FEConfig.SHOW_STACKTRACE) {
            //                  ex.printStackTrace();
            //                }
            //              }
            //            }
          }
          if (outExpr.isASTSizeGE(F.Show, 2)) {
            IAST show = (IAST) outExpr;
            return JSONBuilder.createJSONShow(engine, show);
          } else if (outExpr instanceof GraphExpr) {
            String javaScriptStr = GraphFunctions.graphToJSForm((GraphExpr) outExpr);
            if (javaScriptStr != null) {
              String html = VISJS_IFRAME;
              html = StringUtils.replace(html, "`1`", javaScriptStr);
              html =
                  StringUtils.replace(
                      html,
                      "`2`", //
                      "  var options = { };\n" //
                      );
              html = StringEscapeUtils.escapeHtml4(html);
              return JSONBuilder.createJSONJavaScript(
                  "<iframe srcdoc=\""
                      + html
                      + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>");
            }
          } else if (outExpr.isAST(F.JSFormData, 3)) {
            IAST jsFormData = (IAST) outExpr;
            if (jsFormData.arg2().toString().equals("mathcell")) {
              try {
                return JSONBuilder.createMathcellIFrame(
                    JSBuilder.MATHCELL_IFRAME_TEMPLATE, jsFormData.arg1().toString());
              } catch (Exception ex) {
                if (FEConfig.SHOW_STACKTRACE) {
                  ex.printStackTrace();
                }
              }
            } else if (jsFormData.arg2().toString().equals("jsxgraph")) {
              try {
                return JSONBuilder.createJSXGraphIFrame(
                    JSBuilder.JSXGRAPH_IFRAME_TEMPLATE, jsFormData.arg1().toString());
              } catch (Exception ex) {
                if (FEConfig.SHOW_STACKTRACE) {
                  ex.printStackTrace();
                }
              }
            } else if (jsFormData.arg2().toString().equals("plotly")) {
              try {
                return JSONBuilder.createPlotlyIFrame(
                    JSBuilder.PLOTLY_IFRAME_TEMPLATE, jsFormData.arg1().toString());
              } catch (Exception ex) {
                if (FEConfig.SHOW_STACKTRACE) {
                  ex.printStackTrace();
                }
              }
            } else if (jsFormData.arg2().toString().equals("treeform")) {
              try {
                String manipulateStr = jsFormData.arg1().toString();
                String html = VISJS_IFRAME;
                html = StringUtils.replace(html, "`1`", manipulateStr);
                html =
                    StringUtils.replace(
                        html,
                        "`2`", //
                        "  var options = {\n"
                            + //
                            "		  edges: {\n"
                            + //
                            "              smooth: {\n"
                            + //
                            "                  type: 'cubicBezier',\n"
                            + //
                            "                  forceDirection:  'vertical',\n"
                            + //
                            "                  roundness: 0.4\n"
                            + //
                            "              }\n"
                            + //
                            "          },\n"
                            + //
                            "          layout: {\n"
                            + //
                            "              hierarchical: {\n"
                            + //
                            "                  direction: \"UD\"\n"
                            + //
                            "              }\n"
                            + //
                            "          },\n"
                            + //
                            "          nodes: {\n"
                            + "            shape: 'box'\n"
                            + "          },\n"
                            + //
                            "          physics:false\n"
                            + //
                            "      }; " //
                        );
                html = StringEscapeUtils.escapeHtml4(html);
                return JSONBuilder.createJSONJavaScript(
                    "<iframe srcdoc=\""
                        + html
                        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>");
              } catch (Exception ex) {
                if (FEConfig.SHOW_STACKTRACE) {
                  ex.printStackTrace();
                }
              }
            }
          } else if (outExpr.isString()) {
            IStringX str = (IStringX) outExpr;
            if (str.getMimeType() == IStringX.TEXT_HTML) {
              String htmlSnippet = str.toString();
              String htmlPage = HTML_IFRAME;
              htmlPage = StringUtils.replace(htmlPage, "`1`", htmlSnippet);
              return JSONBuilder.createJSONJavaScript(
                  "<iframe srcdoc=\""
                      + htmlPage
                      + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>");
            }
          }
          return JSONBuilder.createJSONResult(engine, outExpr, outWriter, errorWriter);
        }
        return createOutput(outBuffer, null, engine, function);

      } else {
        return JSONBuilder.createJSONError("Input string parsed to null");
      }
    } catch (AbortException se) {
      return JSONBuilder.createJSONResult(engine, F.$Aborted, outWriter, errorWriter);
    } catch (FailedException se) {
      return JSONBuilder.createJSONResult(engine, F.$Failed, outWriter, errorWriter);
    } catch (SyntaxError se) {
      return JSONBuilder.createJSONSyntaxError(se.getMessage());
    } catch (MathException se) {
      return JSONBuilder.createJSONError(se.getMessage());
    } catch (IOException e) {
      String msg = e.getMessage();
      if (msg != null) {
        return JSONBuilder.createJSONError("IOException occured: " + msg);
      }
      return JSONBuilder.createJSONError("IOException occured");
    } catch (Exception e) {
      // error message
      // if (Config.SHOW_STACKTRACE) {
      e.printStackTrace();
      // }
      String msg = e.getMessage();
      if (msg != null) {
        return JSONBuilder.createJSONError("Error in evaluateString: " + msg);
      }
      return JSONBuilder.createJSONError("Error in evaluateString" + e.getClass().getSimpleName());
    }
  }

  //	public static String[] createJSONResult(EvalEngine engine, IExpr outExpr, StringWriter
  // outWriter,
  //			StringWriter errorWriter) {
  //		// DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
  //		// DecimalFormat decimalFormat = new DecimalFormat("0.0####", otherSymbols);
  //		MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
  //		StringWriter stw = new StringWriter();
  //		if (!mathUtil.toMathML(outExpr, stw, true)) {
  //			return createJSONError("Max. output size exceeded " + Config.MAX_OUTPUT_SIZE);
  //		}
  //		JSONArray temp;
  //
  //		JSONObject resultsJSON = new JSONObject();
  //		resultsJSON.put("line", new Integer(21));
  //		resultsJSON.put("result", stw.toString());
  //		temp = new JSONArray();
  //		String message = errorWriter.toString();
  //		if (message.length() > 0) {
  //			// "out": [{
  //			// "prefix": "Power::infy",
  //			// "message": true,
  //			// "tag": "infy",
  //			// "symbol": "Power",
  //			// "text": "Infinite expression 1 / 0 encountered."}]}]}
  //			JSONObject messageJSON = new JSONObject();
  //			messageJSON.put("prefix", "Error");
  //			messageJSON.put("message", Boolean.TRUE);
  //			messageJSON.put("tag", "evaluation");
  //			messageJSON.put("symbol", "General");
  //			messageJSON.put("text", "<math><mrow><mtext>" + message + "</mtext></mrow></math>");
  //			temp.add(messageJSON);
  //		}
  //
  //		message = outWriter.toString();
  //		if (message.length() > 0) {
  //			JSONObject messageJSON = new JSONObject();
  //			messageJSON.put("prefix", "Output");
  //			messageJSON.put("message", Boolean.TRUE);
  //			messageJSON.put("tag", "evaluation");
  //			messageJSON.put("symbol", "General");
  //			messageJSON.put("text", "<math><mrow><mtext>" + message + "</mtext></mrow></math>");
  //			temp.add(messageJSON);
  //		}
  //		resultsJSON.put("out", temp);
  //
  //		temp = new JSONArray();
  //		temp.add(resultsJSON);
  //		JSONObject json = new JSONObject();
  //		json.put("results", temp);
  //
  //		return new String[] { "mathml", JSONValue.toJSONString(json) };
  //	}

  // private static String[] createJSONString(EvalEngine engine, String outExpr) {
  // StringWriter stw = new StringWriter();
  // stw.append("<math><mtext>");
  // stw.append(outExpr);
  // stw.append("</mtext></math>");
  // JSONArray temp;
  //
  // JSONObject resultsJSON = new JSONObject();
  // resultsJSON.put("line", new Integer(21));
  // resultsJSON.put("result", stw.toString());
  // temp = new JSONArray();
  // resultsJSON.put("out", temp);
  //
  // temp = new JSONArray();
  // temp.add(resultsJSON);
  // JSONObject json = new JSONObject();
  // json.put("results", temp);
  //
  // return new String[] { "mathml", JSONValue.toJSONString(json) };
  // }

  //	public static String[] createJSONShow(EvalEngine engine, IAST show) throws IOException {
  //		StringBuilder stw = new StringBuilder();
  //		stw.append("<math><mtable><mtr><mtd>");
  //		Show2SVG.toSVG(show, stw);
  //		stw.append("</mtd></mtr></mtable></math>");
  //		JSONArray temp;
  //		JSONObject resultsJSON = new JSONObject();
  //		resultsJSON.put("line", new Integer(21));
  //		resultsJSON.put("result", stw.toString());
  //		temp = new JSONArray();
  //		resultsJSON.put("out", temp);
  //
  //		temp = new JSONArray();
  //		temp.add(resultsJSON);
  //		JSONObject json = new JSONObject();
  //		json.put("results", temp);
  //
  //		return new String[] { "mathml", JSONValue.toJSONString(json) };
  //	}

  //	public static String[] createJSONJavaScript(String script) throws IOException {
  //
  //		JSONArray temp;
  //		JSONObject resultsJSON = new JSONObject();
  //		resultsJSON.put("line", new Integer(21));
  //		resultsJSON.put("result", script);
  //
  //		temp = new JSONArray();
  //		resultsJSON.put("out", temp);
  //
  //		temp = new JSONArray();
  //		temp.add(resultsJSON);
  //		JSONObject json = new JSONObject();
  //		json.put("results", temp);
  //
  //		return new String[] { "mathml", JSONValue.toJSONString(json) };
  //	}

  //	public static String[] createJSONError(String str) {
  //		return new String[] { "error", createJSONErrorString(str) };
  //	}

  //	public static String createJSONErrorString(String str) {
  //		JSONArray temp;
  //		JSONObject outJSON = new JSONObject();
  //		outJSON.put("prefix", "Error");
  //		outJSON.put("message", Boolean.TRUE);
  //		outJSON.put("tag", "syntax");
  //		outJSON.put("symbol", "General");
  //		outJSON.put("text", "<math><mrow><mtext>" + str + "</mtext></mrow></math>");
  //
  //		JSONObject resultsJSON = new JSONObject();
  //		resultsJSON.put("line", null);
  //		resultsJSON.put("result", null);
  //
  //		temp = new JSONArray();
  //		temp.add(outJSON);
  //		resultsJSON.put("out", temp);
  //
  //		temp = new JSONArray();
  //		temp.add(resultsJSON);
  //		JSONObject json = new JSONObject();
  //		json.put("results", temp);
  //		// {"results":
  //		// [{"line": null, "result": null, "out":
  //		// [{"prefix": "General::syntax", "message": true, "tag": "syntax",
  //		// "symbol": "General", "text":
  //		// "<math><mrow><mtext> </mtext></mrow></math>"
  //		// }]
  //		// }]
  //		// }
  //		return JSONValue.toJSONString(json);
  //	}
  private static IExpr evalTopLevel(
      EvalEngine engine, final StringWriter buf, final IExpr parsedExpression) {
    IExpr result;
    EvalEngine[] engineRef = new EvalEngine[] {engine};
    result = ExprEvaluator.evalTopLevel(parsedExpression, engineRef);
    engine = engineRef[0];
    if ((result != null) && !result.equals(S.Null)) {
      OutputFormFactory.get(engine.isRelaxedSyntax()).convert(buf, result);
    }
    return result;
  }

  private static String[] listUserVariables(String userId) {
    StringBuilder bldr = new StringBuilder();
    // boolean rest = false;
    bldr.append("{");
    // QueryResultIterable<UserSymbolEntity> qri = UserSymbolService.getAll(userId);
    // for (UserSymbolEntity userSymbolEntity : qri) {
    // if (rest) {
    // bldr.append(", ");
    // } else {
    // rest = true;
    // }
    // bldr.append(userSymbolEntity.getSymbolName());
    // }
    bldr.append("}");
    return new String[] {"expr", bldr.toString()};
  }

  private static String[] createOutput(
      StringWriter buffer, IExpr rhsExpr, EvalEngine engine, String function) throws IOException {

    boolean textEval = true;
    // if (rhsExpr != null && rhsExpr instanceof IAST &&
    // rhsExpr.isAST(F.Show,
    // 2)) {
    // IAST ast = (IAST) rhsExpr;
    // if (ast.size() == 2 && ast.get(0).toString().equals("Show")) {
    // StringBufferWriter outBuffer = new StringBufferWriter();
    // outBuffer = new StringBufferWriter();
    // StringBufferWriter graphicBuf = new StringBufferWriter();
    // IExpr result = (IExpr) ast.get(1);
    // graphicBuf.setIgnoreNewLine(true);
    // OutputFormFactory outputFormFactory = OutputFormFactory.get();
    // outputFormFactory.convert(graphicBuf, result);
    // createJavaView(outBuffer, graphicBuf.toString());
    // textEval = false;
    // return new String[] { "applet", outBuffer.toString() };
    // }
    // }

    if (textEval) {
      String res = buffer.toString();
      if (function.length() > 0 && function.equals("$mathml")) {
        MathMLUtilities mathUtil = new MathMLUtilities(engine, false, true);
        StringWriter stw = new StringWriter();
        if (!mathUtil.toMathML(res, stw)) {
          return new String[] {"error", "Max. output size exceeded " + Config.MAX_OUTPUT_SIZE};
        }
        return new String[] {"mathml", stw.toString()};
      } else if (function.length() > 0 && function.equals("$tex")) {
        TeXUtilities texUtil = new TeXUtilities(engine, true);
        StringWriter stw = new StringWriter();
        if (!texUtil.toTeX(res, stw, false)) {
          return new String[] {"error", "Max. output size exceeded " + Config.MAX_OUTPUT_SIZE};
        }
        return new String[] {"tex", stw.toString()};
      } else {
        return new String[] {"expr", res};
      }
    }
    return new String[] {"error", "Error in createOutput"};
  }

  /**
   * Try to read an older evaluation from the Memcache
   *
   * @return null if there is no suitable evaluation stored in the memcache
   */
  // private static IExpr getFromMemcache(IExpr lhsExpr) {
  // try {
  // ArrayList list = new ArrayList<IExpr>();// F.ast(null);
  // Map<IExpr, IExpr> map = new HashMap<IExpr, IExpr>();
  // lhsExpr = lhsExpr.variables2Slots(map, list);
  // if (lhsExpr != null) {
  // String lhsString = lhsExpr.toString();
  // IExpr expr = (IExpr) cache.get(lhsString);
  // if (expr != null) {
  // if (list.size() > 0) {
  // IAST l=F.List();
  // l.addAll(list);
  // expr = Function.replaceSlots(expr, l);
  // }
  // return expr;
  // }
  //
  // }
  // } catch (Exception e) {
  // if (Config.SHOW_STACKTRACE) {
  // e.printStackTrace();
  // }
  // }
  // return null;
  // }

  /**
   * Save an evaluation in the memcache.
   *
   * @return false if the lhsExpr or rhsExpr expressions contain $-variables or patterns
   */
  // private static boolean putToMemcache(IExpr lhsExpr, IExpr rhsExpr) {
  // try {
  // ArrayList<IExpr> list = new ArrayList<IExpr>();
  // Map<IExpr, IExpr> map = new HashMap<IExpr, IExpr>();
  // lhsExpr = lhsExpr.variables2Slots(map, list);
  // rhsExpr = rhsExpr.variables2Slots(map, list);
  // if (lhsExpr != null && rhsExpr != null) {
  // String lhsString = lhsExpr.toString();
  // int lhsHash = lhsExpr.hashCode();
  // cache.put(lhsString, rhsExpr);
  // return true;
  // }
  // } catch (Exception e) {
  // if (Config.SHOW_STACKTRACE) {
  // e.printStackTrace();
  // }
  // }
  // return false;
  // }

  public static String toHTML(String res) {
    if (res != null) {
      StringBuffer sbuf = new StringBuffer(res.length() + 50);

      char ch;
      for (int i = 0; i < res.length(); i++) {
        ch = res.charAt(i);
        switch (ch) {
          case '>':
            sbuf.append("&gt;");
            break;
          case '<':
            sbuf.append("&lt;");
            break;
          case '&':
            sbuf.append("&amp;");
            break;
          case '"':
            sbuf.append("&quot;");
            break;
          default:
            sbuf.append(res.charAt(i));
        }
      }
      return sbuf.toString();
    }
    return "";
  }

  public static String toHTMLNL(String res) {
    if (res != null) {
      StringBuffer sbuf = new StringBuffer(res.length() + 50);

      char ch;
      for (int i = 0; i < res.length(); i++) {
        ch = res.charAt(i);
        switch (ch) {
          case '>':
            sbuf.append("&gt;");
            break;
          case '<':
            sbuf.append("&lt;");
            break;
          case '&':
            sbuf.append("&amp;");
            break;
          case '"':
            sbuf.append("&quot;");
            break;
          case '\n':
            sbuf.append("<br/>");
            break;
          case ' ':
            sbuf.append("&nbsp;");
            break;
          default:
            sbuf.append(res.charAt(i));
        }
      }
      return sbuf.toString();
    }
    return "";
  }

  @Override
  public void init() throws ServletException {
    super.init();
    if (!INITIALIZED) {
      AJAXQueryServlet.initialization("AJAXQueryServlet");
    }
  }

  public static synchronized void initialization(String servlet) {
    AJAXQueryServlet.INITIALIZED = true;
    ToggleFeature.COMPILE = false;
    Config.UNPROTECT_ALLOWED = false;
    Config.USE_MANIPULATE_JS = true;
    // disable threads for JAS on appengine
    Config.JAS_NO_THREADS = true;
    Config.THREAD_FACTORY = com.google.appengine.api.ThreadManager.currentRequestThreadFactory();
    Config.MATHML_TRIG_LOWERCASE = false;
    Config.MAX_AST_SIZE = ((int) Short.MAX_VALUE) * 8;
    Config.MAX_OUTPUT_SIZE = Short.MAX_VALUE;
    Config.MAX_BIT_LENGTH = ((int) Short.MAX_VALUE) * 8;
    Config.MAX_INPUT_LEAVES = 1000L;
    Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    Config.MAX_POLYNOMIAL_DEGREE = 100;
    Config.PRIME_FACTORS = new BigIntegerPrimality();

    EvalEngine.get().setPackageMode(true);
    F.initSymbols();

    F.Plot.setEvaluator(org.matheclipse.core.reflection.system.Plot.CONST);
    F.Plot3D.setEvaluator(org.matheclipse.core.reflection.system.Plot3D.CONST);
    // F.Show.setEvaluator(org.matheclipse.core.builtin.graphics.Show.CONST);
    // Config.JAS_NO_THREADS = true;
    AJAXQueryServlet.log.info(servlet + "initialized");
  }

  static ThreadLocalNotifierClosable setLogEventNotifier(PrintStream outs, PrintStream errors) {

    return ThreadLocalNotifyingAppender.addLogEventNotifier(
        e -> {
          if (e.getLevel().isMoreSpecificThan(Level.ERROR)) {
            StringBuilder msg = new StringBuilder();
            Message logMessage = e.getMessage();
            if (logMessage != null) {
              msg.append(logMessage.getFormattedMessage());
            }
            Throwable thrown = e.getThrown();
            if (thrown != null) {
              msg.append(": ").append(thrown.getMessage());
            }
            PrintStream stream = e.getLevel().isMoreSpecificThan(Level.ERROR) ? errors : outs;
            stream.println(msg.toString());
          }
        });
  }
}
