package org.matheclipse.gwt.server;

import java.io.IOException;
import java.io.StringWriter;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

public class MathEvaluator {

  public static IExpr eval(EvalEngine engine, final StringWriter buf, final IExpr parsedExpression)
      throws IOException {
    //    engine.reset();
    IExpr result;
    try {
      result = engine.evaluate(parsedExpression);
    } catch (final IterationLimitExceeded e) {
      // Recursion depth of `1` exceeded during evaluation of `2`.
      int iterationLimit = engine.getIterationLimit();
      Errors.printMessage(
          S.$IterationLimit,
          "itlim",
          F.List(iterationLimit < 0 ? F.CInfinity : F.ZZ(iterationLimit), parsedExpression),
          engine);
      result = F.Hold(parsedExpression);
    } catch (final RecursionLimitExceeded e) {
      // Recursion depth of `1` exceeded during evaluation of `2`.
      int recursionLimit = engine.getRecursionLimit();
      Errors.printMessage(
          S.$RecursionLimit,
          "reclim2",
          F.List(recursionLimit < 0 ? F.CInfinity : F.ZZ(recursionLimit), parsedExpression),
          engine);
      result = F.Hold(parsedExpression);
    }
    if ((result != null) && !result.equals(F.Null)) {
      OutputFormFactory.get(engine.isRelaxedSyntax()).convert(buf, result);
    }
    engine.addInOut(parsedExpression, result);
    return result;
  }
}
