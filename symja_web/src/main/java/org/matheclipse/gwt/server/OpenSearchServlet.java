package org.matheclipse.gwt.server;

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OpenSearchServlet extends HttpServlet {

	private static final long serialVersionUID = -8877250696412310875L;

	@Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	@Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// res.setContentType("text/html; charset=UTF-8");
		// res.setCharacterEncoding("UTF-8");
		// res.setHeader("Cache-Control", "no-cache");
		// PrintWriter out = res.getWriter();

		String value = req.getParameter("i");
		if (value == null) {
			String nextJSP = "/index.jsp";
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
			dispatcher.forward(req, res);
			return;
		}
		String nextJSP = "/index.jsp";
		req.setAttribute("input", value);
		req.setAttribute("i", null);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
		dispatcher.forward(req, res);

		return;
	}

}
