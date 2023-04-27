package Controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import Dao.UserDao;
import Database.DbConnection;
import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@MultipartConfig
@WebServlet("/update-user-details")
public class UserUpdateServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		Part dp_pic = request.getPart("dp");
		User user = new User();
		User current_user = (User) request.getSession().getAttribute("auth");
		user.setUserName(request.getParameter("username").toLowerCase().trim());
		user.setEmail(current_user.getEmail().toLowerCase().trim());
		user.setPhoneNumber(current_user.getPhoneNumber());
		user.setBio(request.getParameter("bio").trim());
		user.setLivesIn(request.getParameter("livesin"));
		user.setUserId(current_user.getUserId());
		user.setProfilepic(current_user.getProfilepic());
		// -----
		user.setPassword(current_user.getPassword());
		user.setIsOnline(current_user.getIsOnline());
		user.setAccountOpened(current_user.getAccountOpened());
		out.print(user.getBio());
		out.print(user.getLivesIn());
		try {
			UserDao userdao = new UserDao(DbConnection.getConnection());
			if (dp_pic.getSize() > 0) {
				File file = new File(
						"C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\data\\user_dp");
				if (!file.exists()) {
					file.mkdirs();
				}
				user.setProfilepic(current_user.getUserId()+"");
				dp_pic.write(
						"C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\data\\user_dp\\"
								+ user.getUserId() + ".jpg");
				userdao.updateUser(user);
			}else{
				userdao.updateUserWithOutProfilePic(user);
			}
				request.setAttribute("type","user");
				request.setAttribute("sub_type","update");
				request.setAttribute("username",user.getUserName());
				request.setAttribute("userid",user.getUserId());
				request.setAttribute("profilepic",user.getProfilepic());
				RequestDispatcher rd = request.getRequestDispatcher("/sync-to-chat");
				rd.include(request, response);
				out.print(
						"<script>alert('Profile Succefully Updated!!!');window.location = document.referrer;</script>");
				request.getSession().setAttribute("auth", user);
		} catch (Exception e) {
			out.print(e);
			e.printStackTrace();
		}
	}
}
