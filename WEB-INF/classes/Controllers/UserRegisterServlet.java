package Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import Dao.UserDao;
import Database.DbConnection;
import Model.User;
import Services.LocalServices;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet("/registeruser")
public class UserRegisterServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		boolean isGoogleData = request.getParameter("type") != null ? true : false;
		boolean isSSOData = request.getAttribute("type") != null && request.getAttribute("type").equals("sso") ? true
				: false;
		User user = new User();
		if (isGoogleData) {
			user.setProfilepic(request.getParameter("profilepic"));
		} else {
			user.setProfilepic(null);
			user.setPassword(request.getParameter("password"));
			user.setPhoneNumber(request.getParameter("phone_number"));
		}
		user.setUserName(request.getParameter("name"));
		user.setEmail(request.getParameter("email"));
		if (isSSOData) {
			user.setUserName(((String) request.getAttribute("name")).substring(0,
					((String) request.getAttribute("name")).indexOf("@")));
			user.setEmail((String) request.getAttribute("name"));
		}
		user.setAccountOpened(LocalServices.getCurrentDateTime());
		user.setIsOnline(false);
		try {
			UserDao userdao = new UserDao(DbConnection.getConnection());
			if (userdao.isEmailExists(user.getEmail())) {
				if (isGoogleData || isSSOData) {
					HttpSession session = request.getSession();
					session.setAttribute("auth", userdao.getUser(user.getEmail(), "", "others"));
				} else {
					request.setAttribute("error", "Email already exists.");
				}
			} else if (userdao.isPhoneNumberExists(user.getPhoneNumber())) {
				request.setAttribute("error", "Phone Number already exists.");
			} else {
				String signin_type="";
				if (isGoogleData || isSSOData) {
					signin_type="others";
					userdao.insertUser(user, "others");
					HttpSession session = request.getSession();
					session.setAttribute("auth", userdao.getUser(user.getEmail(), "", "others"));
					// response.sendRedirect("user-home.jsp");
				} else {
					signin_type="zoho";
					userdao.insertUser(user, "zoho");
					request.setAttribute("success", "Registration Success.");
				}
				request.setAttribute("type","user");
				request.setAttribute("sub_type","create");
				request.setAttribute("username",user.getUserName());
				request.setAttribute("cemail",user.getEmail());
				request.setAttribute("cpassword",user.getPassword());
				request.setAttribute("phonenumber",user.getPhoneNumber());
				request.setAttribute("profilepic",user.getProfilepic());
				request.setAttribute("signintype",signin_type);
				RequestDispatcher rd = request.getRequestDispatcher("/sync-to-chat");
				rd.include(request, response);
			}
			if (!isGoogleData && !isSSOData) {
				RequestDispatcher rd = request.getRequestDispatcher("Register.jsp");
				rd.include(request, response);
			} else {
				response.sendRedirect("user-home.jsp");
			}
		} catch (Exception e) {
			out.write("" + e);
			// e.printStackTrace();
		}
	}
}