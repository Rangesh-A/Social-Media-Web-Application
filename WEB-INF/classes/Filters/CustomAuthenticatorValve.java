package Filters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

public class CustomAuthenticatorValve extends ValveBase {

    public void invoke(Request request, Response response){
        try{

        // Check if the request requires authentication
        if (request.getAuthType() == null || request.getRemoteUser() == null) {
            // Redirect the user to the custom login page
            String loginUrl = "http://192.168.10.54:8080/Zoho_Social/SSOAdLogin.jsp";
            response.sendRedirect(loginUrl);
            return;
        }

        // Allow the request to proceed to the next Valve in the chain
        getNext().invoke(request, response);
        }catch(Exception e){}
    }
}