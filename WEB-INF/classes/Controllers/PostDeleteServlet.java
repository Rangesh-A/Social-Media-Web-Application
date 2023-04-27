package Controllers;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import Dao.PostDao;
import Database.DbConnection;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.File;


@WebServlet("/delete-post")
public class PostDeleteServlet extends HttpServlet {
public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		PrintWriter out=response.getWriter();
		response.setContentType("text/html");
		int postid=Integer.parseInt(request.getParameter("postid"));
		try {
			PostDao postDao=new PostDao(DbConnection.getConnection());
			int resource_id=postDao.deletePost(postid);
			boolean ifResorceDependent=postDao.isResourceDependent(resource_id);
			System.out.println("isdep= "+ifResorceDependent);
			if(!ifResorceDependent){
				String path=postDao.removeResourceAndGetPath(resource_id);
				File file = new File(path);
				file.delete();
			}
			out.print("<script>window.location = document.referrer;</script>");
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
