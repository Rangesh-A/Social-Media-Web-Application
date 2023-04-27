package Controllers;

import java.io.IOException;
import java.io.PrintWriter;

import Dao.PostDao;
import Database.DbConnection;
import Model.Post;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet("/like-post")
public class LikePostServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		Post post = new Post();
		int post_id = Integer.parseInt(request.getParameter("post_id"));
		int liker_id = ((User) request.getSession().getAttribute("auth")).getUserId();
		boolean type = request.getParameter("type").equals("like") ? true : false;
		try {
			PostDao postDao = new PostDao(DbConnection.getConnection());
			postDao.reactPost(post_id, liker_id, type);
			out.print("<script>window.location = document.referrer;</script>");
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
