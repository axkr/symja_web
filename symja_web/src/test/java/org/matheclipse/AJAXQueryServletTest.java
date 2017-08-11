package org.matheclipse;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;
import org.matheclipse.gwt.server.AJAXQueryServlet;

public class AJAXQueryServletTest {

	@Test
	public void test() throws ServletException, IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.putParameter("query", "1+1");
		new AJAXQueryServlet().doGet(null, response);
		Assert.assertEquals("text/plain; charset=UTF-8", response.getContentType());
		Assert.assertEquals(
				"{\"results\":[{\"result\":null,\"line\":null,\"out\":[{\"symbol\":\"General\",\"prefix\":\"Error\",\"tag\":\"syntax\",\"text\":\"<math><mrow><mtext>No input expression posted!<\\/mtext><\\/mrow><\\/math>\",\"message\":true}]}]}\r\n",
				response.getWriterContent().toString());
	}
}
