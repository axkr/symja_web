package org.matheclipse;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.matheclipse.gwt.server.AJAXQueryServlet;
import jakarta.servlet.ServletException;

public class AJAXQueryServletTest {

	@Test
	public void test() throws ServletException, IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.putParameter("query", "1+1");
		new AJAXQueryServlet().doGet(null, response);
		Assert.assertEquals("text/html; charset=UTF-8", response.getContentType());
		Assert.assertEquals(
				"{\"results\":[{\"line\":null,\"result\":null,\"out\":[{\"prefix\":\"Error\",\"message\":true,\"tag\":\"syntax\",\"symbol\":\"General\",\"text\":\"<math><mrow><mtext>No input expression posted!</mtext></mrow></math>\"}]}]}\r\n",
				response.getWriterContent().toString());
	}
}
