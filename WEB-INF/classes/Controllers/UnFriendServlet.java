package Controllers;
import java.io.IOException;
import java.io.PrintWriter;

import Dao.ConnectionDao;
import Dao.ConnectionLogDao;
import Dao.RequestDao;
import Database.DbConnection;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;


@WebServlet("/unfriend")
public class UnFriendServlet extends HttpServlet {
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		PrintWriter out=response.getWriter();
		response.setContentType("text/html");
		int user1id=((User)request.getSession().getAttribute("auth")).getUserId();
		int user2id=(Integer.parseInt(request.getParameter("friend_id")));
		try {
			ConnectionDao connectiondao=new ConnectionDao(DbConnection.getConnection());
			ConnectionLogDao connectionLogDao=new ConnectionLogDao(DbConnection.getConnection());
			RequestDao requestdao=new RequestDao(DbConnection.getConnection());
			connectiondao.deleteConnection(user1id,user2id);
			connectionLogDao.insertConnectionLog(user1id,user2id,"remove");
			requestdao.deleteRequest(user1id, user2id);
//			request.setAttribute("type","connection");
//			request.setAttribute("sub_type","delete");
//			request.setAttribute("user1id",user1id);
//			request.setAttribute("user2id",user2id);
//			RequestDispatcher rd = request.getRequestDispatcher("/sync-to-chat");
//			rd.include(request, response);
			out.print("<script>window.location = document.referrer;</script>");
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
