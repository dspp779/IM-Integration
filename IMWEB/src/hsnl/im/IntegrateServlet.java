package hsnl.im;

import hsnl.im.integrate.IntegrationPool;
import hsnl.im.service.JsonService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class IntegrateServlet
 */
@WebServlet("/IntegrateServlet")
public class IntegrateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public IntegrateServlet() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Set<String> imset = IntegrationPool.getFriendList();
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(JsonService.serialize(imset));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String from = request.getParameter("from");
		if(from != null) {
			from = new String(from.getBytes("ISO-8859-1"), "UTF-8");
			String body = getRequestBody(request);
			List<String> to = (List<String>) JsonService.deserialize(body, List.class);
			System.out.println(body);
			System.out.println(to.size() + to.toString());
			if(to!=null && to.size() > 0) {
				System.out.println(from+" "+to);
				IntegrationPool.SetIntegration(from, to);
			} else {
				IntegrationPool.removeIntegration(from);
			}
		}
	}

	private String getRequestBody(HttpServletRequest req) throws IOException {
		StringBuffer buf = new StringBuffer();
		char[] c = new char[1024];
		int len;
		while ((len = req.getReader().read(c)) != -1) {
			buf.append(c, 0, len);
		}
		return buf.toString();
	}

}
