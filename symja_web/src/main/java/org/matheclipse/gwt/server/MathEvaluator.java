package org.matheclipse.gwt.server;

import java.io.IOException;
import java.io.StringWriter;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class MathEvaluator {
	public static double[][] eval(final StringWriter buf, final String inputString) throws Exception {
		EvalEngine fEvalEngine = new EvalEngine("", 64, 100000, System.out, System.err, false);
		IExpr parsedExpression = fEvalEngine.parse(inputString);
		fEvalEngine.reset();
		IExpr evaluationResult = fEvalEngine.evaluate(parsedExpression);
		// fEvalEngine.addOut(expr);
		if (evaluationResult instanceof IAST) {
			final IAST show = (IAST) evaluationResult;
			if ((show.size() == 2) && show.isAST("Show")) {
				final IAST graphics = (IAST) show.get(1);
				if (graphics.isAST("Graphics")) {
					// example: Plot[Sin[x],{x,0,10}]
					final IAST data = (IAST) graphics.get(1);
					if (data.isAST("Line") && data.get(1).isList()) {
						final IAST lineData = (IAST) data.get(1);
						IAST pair;
						final double[][] plotPoints = new double[lineData.size() - 1][2];
						for (int i = 1; i < lineData.size(); i++) {
							pair = (IAST) lineData.get(i);
							plotPoints[i - 1][0] = ((INum) pair.get(1)).getRealPart();
							plotPoints[i - 1][1] = ((INum) pair.get(2)).getRealPart();
							// plotPoints[1][i-1] =
							// ((IDouble)pair.get(2)).getRealPart();
						}
						return plotPoints;

					}
				} else if (graphics.get(0).isAST("SurfaceGraphics")) {
					// Plot3D[Sin[x]*Cos[y],{x,-10,10},{y,-10,10}]
				}
			}
		}
		if ((evaluationResult != null) && !evaluationResult.equals(F.Null)) {
			OutputFormFactory.get(false).convert(buf, evaluationResult);
		}
		return null;
	}

	public static IExpr eval(EvalEngine evalEngine, final StringWriter buf, final IExpr parsedExpression)
			throws IOException {
		// EvalEngine fEvalEngine = new EvalEngine("", 64, 100000,System.out);
		evalEngine.reset();
		IExpr evaluationResult = evalEngine.evaluate(parsedExpression);
		// evalEngine.addOut(evaluationResult);
		// if (evaluationResult instanceof IAST) {
		// final IAST show = (IAST) evaluationResult;
		// if ((show.size() == 2) && show.isAST("Show")) {
		// final IAST graphics = (IAST) show.get(1);
		// if (graphics.isAST("Graphics")) {
		// // example: Plot[Sin[x],{x,0,10}]
		// final IAST data = (IAST) graphics.get(1);
		// if (data.isAST("Line") && data.get(1).isList()) {
		// final IAST lineData = (IAST) data.get(1);
		// IAST pair;
		// final double[][] plotPoints = new double[lineData.size() - 1][2];
		// for (int i = 1; i < lineData.size(); i++) {
		// pair = (IAST) lineData.get(i);
		// plotPoints[i - 1][0] = ((INum) pair.get(1)).getRealPart();
		// plotPoints[i - 1][1] = ((INum) pair.get(2)).getRealPart();
		// // plotPoints[1][i-1] = ((IDouble)pair.get(2)).getRealPart();
		// }
		// return plotPoints;
		//
		// }
		// } else if (graphics.get(0).isAST("SurfaceGraphics")) {
		// // Plot3D[Sin[x]*Cos[y],{x,-10,10},{y,-10,10}]
		// }
		// }
		// }

		UserService userService = UserServiceFactory.getUserService();
		if (userService.getCurrentUser() != null) {
			IExpr lastResult = evaluationResult;
			if (lastResult == null) {
				lastResult = F.Null;
			}
			// remember last result in user variable $ans
			ISymbol ans = F.$s("$ans");
			try {
				ans.putDownRule(ISymbol.RuleType.SET, true, ans, lastResult, false);
			} catch (Exception ex) {
				// ex.printStackTrace();
				ans.putDownRule(ISymbol.RuleType.SET, true, ans, F.Null, false);
			}
		}

		if ((evaluationResult != null) && !evaluationResult.equals(F.Null)) {
			OutputFormFactory.get(true).convert(buf, evaluationResult);
		}
		return evaluationResult;
	}
}
