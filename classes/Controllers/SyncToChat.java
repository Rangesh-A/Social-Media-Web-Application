package Controllers;
import java.io.*;

import Dao.ConnectionDao;
import Dao.RequestDao;
import Database.DbConnection;
import Model.User;
import Services.LocalServices;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@WebServlet("/sync-to-chat")
public class SyncToChat extends HttpServlet {
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		PrintWriter out=response.getWriter();
		String type=(String)request.getAttribute("type")==null?"":(String)request.getAttribute("type");
		URL url = new URL("http://localhost:8080/Zoho_Chat/sync-from-social");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		JSONObject obj = new JSONObject();
		if(type.equals("user")){
			if(request.getAttribute("sub_type").equals("update")){
				obj.put("type", type);
				obj.put("username", request.getAttribute("username"));
				obj.put("userid", request.getAttribute("userid"));
				obj.put("profilepic", request.getAttribute("profilepic"));
				obj.put("sub_type", "update");
			}else if(request.getAttribute("sub_type").equals("updatepassword")){
				obj.put("type", type);
				obj.put("userid", request.getAttribute("userid"));
				obj.put("new_password", request.getAttribute("new_password"));
				obj.put("sub_type", "updatepassword");
			}else {
				obj.put("type", type);
				obj.put("username", request.getAttribute("username"));
				obj.put("email", request.getAttribute("cemail"));
				obj.put("password", request.getAttribute("cpassword"));
				obj.put("phonenumber", request.getAttribute("phonenumber"));
				obj.put("profilepic", request.getAttribute("profilepic"));
				obj.put("signintype", request.getAttribute("signintype"));
				obj.put("sub_type", "put");
			}
		}
		type=(String)request.getParameter("type");
		if(type!=null&&type.equals("share-post")){
			int senderid=((User)request.getSession().getAttribute("auth")).getUserId();
			int receiverid=Integer.parseInt(request.getParameter("receiverid"));
			int postid=Integer.parseInt(request.getParameter("postid"));
			obj.put("type", type);
			obj.put("senderid", senderid);
			obj.put("receiverid", receiverid);
			obj.put("postid", postid);
			obj.put("time", LocalServices.getCurrentDateTime());
			obj.put("sub_type", "put");
			System.out.println(obj);

		}
		OutputStream os = conn.getOutputStream();

		String signature= null;
		try {
			signature = LocalServices.createSignature(obj.toJSONString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println(obj.toJSONString());
		String request_body= null;
		String signature_algorithm="SHA1withDSA";
		String data_algorithm="AES/ECB/PKCS5Padding";
		try {
			request_body="data<>"+LocalServices.encryptData(obj.toJSONString(),data_algorithm)+"&signature<>"+signature+"&signaturealgo<>"+signature_algorithm+"&dataalgo<>"+data_algorithm;
		}  catch (Exception e) {
			return;
		}
		os.write(request_body.getBytes());
		os.flush();
		os.close();
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		reader.close();
	}
}
