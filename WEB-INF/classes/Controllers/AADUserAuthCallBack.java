package Controllers;

import Model.User;
import Dao.UserDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import Database.DbConnection;
import Model.Authenticator;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.google.gson.Gson;
import com.microsoft.aad.adal4j.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


@WebServlet("/userAuthCallBack")
public class AADUserAuthCallBack extends HttpServlet {
    private static final String CLIENT_ID = "3d1ca881-3035-474c-b573-44392ecf23cb";
    private static final String CLIENT_SECRET = "G4Z8Q~ssTKD2wtIOo3qJD~y4KxWUIC5buhmDrbls";
    private static final String TENANT_ID = "bcd431be-38a2-419a-989f-a6919afdcdbd";
    private static String AUTHORIZATION_CODE = "";
    private static final String GRAPH_API_URL = "https://graph.microsoft.com/v1.0/me";
    private static String ACCESS_CODE = "";
    private static final String TOKEN_ENDPOINT = "https://login.microsoftonline.com/"+TENANT_ID+"/oauth2/v2.0/token";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        AUTHORIZATION_CODE=request.getParameter("code");
        String received_state=request.getParameter("state");
        if(!received_state.equals("login")){
            out.println("<script>alert('Authentication Revoked');history.back();</script>");
            return;
        }
        System.out.println("AUTHORIZATION TOKEN--->"+AUTHORIZATION_CODE);
        JSONObject userAttributes=new JSONObject();
        try{
           ACCESS_CODE=getAccessCodeFromAuthCode();
           userAttributes=getAttributesOfUser();
            User user=new User();
            HttpSession session = request.getSession();
            UserDao userdao = new UserDao(DbConnection.getConnection());
            String email=userAttributes.getString("mail");
            user = userdao.getAdUser(email.trim());
            session.setAttribute("auth", user);
            session.setAttribute("login_type", "azureAD");
            System.out.println(user);
            response.sendRedirect("user-home.jsp");
        }catch(Exception e){
            out.println("<script>alert('Invalid Login Credentials');history.back();</script>");
        }
    }
    public static String getAccessCodeFromAuthCode() throws IOException{
        String redirectUri = "http://localhost:8080/Zoho_Social/userAuthCallBack";
        URL url = new URL(TOKEN_ENDPOINT);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String encodedAuth = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String postParams = "grant_type=authorization_code&code=" + AUTHORIZATION_CODE + "&redirect_uri=" + redirectUri + "&client_id=" + CLIENT_ID;
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeBytes(postParams);
        dataOutputStream.flush();
        dataOutputStream.close();

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject responseJson = new JSONObject(response.toString());
            System.out.println("Response--->: " + responseJson);
            String accessToken = responseJson.getString("access_token");
            System.out.println("Access Token: " + accessToken);
            // String refresh_token = responseJson.getString("refresh_token");
            // System.out.println("Refresh Token: " + refresh_token);
            return accessToken;
        } else {
            System.out.println("Error: " + responseCode);
        }
        return null;
    }
    public static JSONObject getAttributesOfUser() throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(GRAPH_API_URL);
        httpGet.setHeader("Authorization", "Bearer " + ACCESS_CODE);
        httpGet.setHeader("Accept", "application/json");

        CloseableHttpResponse response = httpClient.execute(httpGet);
        String responseString = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseString);

        String userId = responseJson.getString("id");
        String userDisplayName = responseJson.getString("displayName");
        String userMail = responseJson.getString("mail");
        System.out.println("Response " + responseJson);
        System.out.println("User ID: " + userId);
        System.out.println("Display Name: " + userDisplayName);
        System.out.println("Mail: " + userMail);
        return responseJson;
    }
}
