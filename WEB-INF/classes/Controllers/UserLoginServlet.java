package Controllers;

import Model.User;
import Dao.UserDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import Database.DbConnection;

@WebServlet("/loginuser")
public class UserLoginServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String phone_email_username = request.getParameter("phone_email_username");
        String password = request.getParameter("password");
        try {
            UserDao userdao = new UserDao(DbConnection.getConnection());
            User user = userdao.getUser(phone_email_username, password, "zoho");
            if (user == null) {
                out.println("<script>alert('Invalid Login Credentials');history.back();</script>");
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("auth", user);
                response.sendRedirect("user-home.jsp");
            }
        } catch (Exception e) {
            out.println(e);
        }
    }
}
