package Controllers;

import Dao.PostDao;
import Database.DbConnection;
import Model.Post;
import Model.User;
import Services.LocalServices;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/view-post")
public class ViewPostServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		try {
			int post_id = Integer.parseInt(request.getParameter("postid"));
			PostDao postDao = new PostDao(DbConnection.getConnection());
			int user_id = ((User) request.getSession().getAttribute("auth")).getUserId();
			if (!postDao.isViewedByUser(user_id, post_id)) {
				postDao.viewPost(post_id, user_id);
			}
		} catch (Exception e) {
			response.getWriter().write("error");
		}
	}
}
