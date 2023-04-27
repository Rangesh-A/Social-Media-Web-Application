package Controllers;
import java.io.IOException;
import java.io.PrintWriter;

import Dao.ConnectionDao;
import Dao.ConnectionLogDao;
import Dao.RequestDao;
import Database.DbConnection;
import Model.FriendRequest;
import Model.User;
import Services.LocalServices;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;


@WebServlet("/react-friend-request")
public class ReactFriendRequestServlet extends HttpServlet {
public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		PrintWriter out=response.getWriter();
		response.setContentType("text/html");
		FriendRequest frnd_request=new FriendRequest();
		int user1_id=((User)request.getSession().getAttribute("auth")).getUserId();
		int user2_id= Integer.parseInt(request.getParameter("friend_id"));
		boolean react_type=request.getParameter("type").equals("accept")?true:false;
		int request_id=Integer.parseInt(request.getParameter("request_id"));
		try {
			ConnectionDao connectiondao=new ConnectionDao(DbConnection.getConnection());
			ConnectionLogDao connectionLogDao=new ConnectionLogDao(DbConnection.getConnection());
			RequestDao requestdao=new RequestDao(DbConnection.getConnection());
			if(react_type) {
				String time=LocalServices.getCurrentDateTime();
				connectiondao.insertConnection(user1_id,user2_id,time);
				connectionLogDao.insertConnectionLog(user1_id,user2_id,"add");
				requestdao.updateReacted(request_id,true);
			}else {
				requestdao.updateReacted(request_id,false);
				// requestdao.deleteRequest(user1_id, user2_id);
			}
				requestdao.deleteRequest(user1_id, user2_id);
			out.print("<script> window.location = document.referrer;</script>");
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
