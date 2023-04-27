package Controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

@WebServlet("/logoutuser")
public class UserLogoutServlet extends HttpServlet {
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String token=(String)session.getAttribute("SSOtoken");
        String login_type=(String)session.getAttribute("login_type");
        session.invalidate();
        if(login_type!=null){
            if(login_type.equals("azureAD")){
                String tenantId = "bcd431be-38a2-419a-989f-a6919afdcdbd";
                String redirectUri = "http://localhost:8080/Zoho_Social/index.jsp";
                String logoutUrl = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/logout?post_logout_redirect_uri=%s", tenantId, redirectUri);
                response.sendRedirect(logoutUrl);
            }
        }else{
            response.sendRedirect("index.jsp");
        }
        //response.sendRedirect("https://dev-20721398-admin.okta.com/logout?id_token_hint="+token+"&post_logout_redirect_uri=https://localhost:8443/Zoho_Social/Login.jsp");
    }
}