package org.matheclipse.core.builtin.graphics;


import java.io.IOException;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

public class Show implements IFunctionEvaluator {
	/**
	 * Constructor for the singleton
	 */
	public final static Show CONST = new Show();

	@Override
	public IExpr evaluate(IAST ast, EvalEngine engine) {
		return F.NIL;
	}

	@Override
	public IExpr numericEval(IAST arg0, EvalEngine engine) {
		return F.NIL;
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

	private static void lineToSVG(IAST ast, Appendable buf, double width,
			double height) throws IOException {
		try {
			if (ast.arg1().isList()) {
				buf.append("<polyline points=\"");
				IAST pointList = (IAST) ast.arg1();
				double x[], y[];
				int numberOfPoints = pointList.size() - 1;

				double xMin = Double.MAX_VALUE;
				double xMax = Double.MIN_VALUE;
				double yMin = Double.MAX_VALUE;
				double yMax = Double.MIN_VALUE;
				x = new double[numberOfPoints];
				y = new double[numberOfPoints];
				IExpr point;
				for (int i = 0; i < numberOfPoints; i++) {
					point = pointList.get(i + 1);
					if (point.isList() && ((IAST) point).size() == 3) {
						x[i] = ((INum) ((IAST) point).arg1()).doubleValue();
						if (x[i] < xMin) {
							xMin = x[i];
						}
						if (x[i] > xMax) {
							xMax = x[i];
						}
						y[i] = ((INum) ((IAST) point).arg2()).doubleValue();
						if (y[i] < yMin) {
							yMin = y[i];
						}
						if (y[i] > yMax) {
							yMax = y[i];
						}
					}
				}
				double xAxisScalingFactor = width / (double) numberOfPoints;
				double yAxisScalingFactor = height / (yMax - yMin);
				for (int i = 0; i < numberOfPoints; i++) {
					buf.append(Double.toString((i) * xAxisScalingFactor));
					buf.append(" ");
					buf.append(Double.toString(height
							- ((y[i] - yMin) * yAxisScalingFactor)));
					if (i < numberOfPoints) {
						buf.append(", ");
					}
				}

			}
		} catch (Exception ex) {
			// catch cast exceptions for example
		} finally {
			buf.append("\" style=\"stroke: rgb(24.720000%, 24.000000%, 60.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" />");
		}
	}

	private static void graphicsToSVG(IAST ast, Appendable buf)
			throws IOException {
		double width = 400;
		double height = 200;
		buf.append("<svg xmlns:svg=\"http://www.w3.org/2000/svg\" xmlns=\"http://www.w3.org/2000/svg\"\nversion=\"1.0\" "
				// +
				// "width=\"400.000000\" height=\"247.213595\" viewBox=\"-17.666667 -14.610939 435.333333 276.435473\">");
				+ "width=\"400\" height=\"200\">");

		try {
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isASTSizeGE(F.Line, 2)) {
					lineToSVG(ast.getAST(i), buf, width, height);
				}
			}
		} finally {
			buf.append("</svg>");
		}
	}

	private static void graphics3dToSVG(IAST ast, Appendable buf)
			throws IOException {
//		double width = 400;
//		double height = 200;
		buf.append("<graphics3d data=\"{");
		try {
			boxes3DToXML(ast, buf);
			// for (int i = 1; i < ast.size(); i++) {
			// if (ast.get(i).isASTSizeGE(F.Line, 2)) {
			// lineToSVG(ast.getAST(i), buf, width, height);
			// }
			// }
		} finally {
			buf.append("}\" />");
		}
	} 

	private static void boxes3DToXML(IAST ast, Appendable buf)
			throws IOException {
		buf.append("&quot;viewpoint&quot;: [1.3, -2.4, 2.0], ");
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isASTSizeGE(F.Polygon, 2)
					&& ast.get(i).getAt(1).isAST()) {
				IAST matrix = (IAST) (ast.get(i).getAt(1));
				int[] dim = matrix.isMatrix();
				if (dim != null) {
					buf.append("&quot;elements&quot;: [");
					buf.append("{&quot;coords&quot;: [");
					int rowLength = dim[0];
					int colLength = dim[1];
					IAST row;
					IExpr value;
					// [{"coords": [[[0.0, 0.0, 0.0], null],...[[1.0, 0.0, 0.0],
					// null]],
					for (int j = 0; j < rowLength; j++) {
						row = matrix.getAST(j + 1);
						buf.append("[[");
						for (int k = 0; k < colLength; k++) {
							value = row.get(k + 1);
							buf.append(value.toString());
							if (k < colLength - 1) {
								buf.append(", ");
							}
						}
						buf.append("], null]");
						if (j < rowLength - 1) {
							buf.append(", ");
						}
					}
					buf.append("], ");// and coords
					buf.append("&quot;type&quot;: &quot;polygon&quot;, &quot;faceColor&quot;: [1, 1, 1, 1]}");
					buf.append("],");// and elements

					buf.append("&quot;lighting&quot;: [{&quot;color&quot;: [0.3, 0.2, 0.4], "
							+ "&quot;type&quot;: &quot;Ambient&quot;},{&quot;color&quot;: [0.8, 0.0, 0.0], &quot;position&quot;: [2.0, 0.0, 2.0], "
							+ "&quot;type&quot;: &quot;Directional&quot;}, {&quot;color&quot;: [0.0, 0.8, 0.0], &quot;position&quot;: [2.0, 2.0, 2.0], "
							+ "&quot;type&quot;: &quot;Directional&quot;}, {&quot;color&quot;: [0.0, 0.0, 0.8], &quot;position&quot;: [0.0, 2.0, 2.0], "
							+ "&quot;type&quot;: &quot;Directional&quot;}], ");

					buf.append("&quot;axes&quot;: {"
							+ "&quot;hasaxes&quot;: [false, false, false], "
							+ "&quot;ticks&quot;: "
							+ "  [[[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], "
							+ "    [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], "
							+ "	[&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]"
							+ "   ], "
							+ "   [[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], "
							+ "	[0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], "
							+ "	[&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]"
							+ "   ], "
							+ "   [[0.0, 0.2, 0.4, 0.6000000000000001, 0.8, 1.0], [0.05, 0.1, 0.15000000000000002, 0.25, 0.30000000000000004, 0.35000000000000003, 0.45, 0.5, 0.55, 0.65, 0.7000000000000001, 0.75, 0.8500000000000001, 0.9, 0.9500000000000001], "
							+ "    [&quot;0.0&quot;, &quot;0.2&quot;, &quot;0.4&quot;, &quot;0.6&quot;, &quot;0.8&quot;, &quot;1.0&quot;]"
							+ "   ]]}, ");

					buf.append("&quot;extent&quot;: {" + "  &quot;zmax&quot;: 1.0, "
							+ "  &quot;ymax&quot;: 1.0, " + "  &quot;zmin&quot;: 0.0, "
							+ "  &quot;xmax&quot;: 1.0, " + "  &quot;xmin&quot;: 0.0, "
							+ "  &quot;ymin&quot;: 0.0}");

				}

			}
		}
	}

	public static void toSVG(IAST ast, Appendable buf) throws IOException {
		buf.append("<math><mtable><mtr><mtd>");
		if (ast.size() > 1 && ast.get(1).isASTSizeGE(F.Graphics, 2)) {
			graphicsToSVG(ast.getAST(1), buf);
		} else if (ast.size() > 1 && ast.get(1).isASTSizeGE(F.Graphics3D, 2)) {
			graphics3dToSVG(ast.getAST(1), buf);
		}
		buf.append("</mtd></mtr></mtable></math>");
	}
}
