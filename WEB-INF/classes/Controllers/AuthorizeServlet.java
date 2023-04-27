package Controllers;

import java.io.IOException;
import java.io.PrintWriter;

import Services.SAMLGetResponse;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet("/authorize")
public class AuthorizeServlet extends HttpServlet {
    private static final String CLIENT_ID = "3d1ca881-3035-474c-b573-44392ecf23cb";
    private static final String REDIRECT_URI = "http://localhost:8080/Zoho_Social/userAuthCallBack";
    private static final String TENANT_ID = "bcd431be-38a2-419a-989f-a6919afdcdbd";

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws IOException{
        String authorizationUrl = String.format(
            "https://login.microsoftonline.com/%s/oauth2/v2.0/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=offline_access openid profile User.Read&state=login",
            TENANT_ID,
            CLIENT_ID,
            REDIRECT_URI
        );
        response.sendRedirect(authorizationUrl);
    }
}