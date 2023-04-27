package Controllers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import Services.LocalServices;
import Dao.PostDao;
import Dao.UserDao;
import Database.DbConnection;
import Model.Post;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.SQLException;


@WebServlet("/sync-from-chat")
public class SyncFromChat extends HttpServlet {
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
		PrintWriter out = response.getWriter();
		String type="";
		String sub_type="";
		BufferedReader reader = request.getReader();
		String line=reader.readLine();
		System.out.println(line);
		reader.close();
		String jsondata=null;
		String signature=null;
		String signaturealgo=null;
		String dataalgo=null;
		try {
			String params[] = line.split("\\&");
//        System.out.println(Arrays.toString(params));
			jsondata = params[0].split("<>")[1];
			signature = params[1].split("<>")[1];
			signaturealgo = params[2].split("<>")[1];
			dataalgo = params[3].split("<>")[1];
		}catch (Exception e){
			response.setContentType("text/plain");
			response.setStatus(500);
			response.getWriter().println("No response");
		}
		System.out.println(jsondata);
		System.out.println(signature);
		System.out.println(signaturealgo);
		System.out.println(dataalgo);
		boolean request_validation=false;
		try {
			request_validation= LocalServices.validateSignature(signature,jsondata,signaturealgo);
		}catch (Exception e) {
			return;
		}
		System.out.println(request_validation);
		if(!request_validation){
			response.setContentType("text/plain");
			response.setStatus(500);
			response.getWriter().println("No response");
		}
		JSONParser parser = new JSONParser();
		JSONObject json=null;
		try {
			jsondata=LocalServices.decryptData(jsondata,dataalgo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			json = (JSONObject) parser.parse(jsondata);
			System.out.println(json.get("type"));
			type=(String)json.get("type");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		if(type.equals("postdetails")) {
			int unreadmessages_count = 0;
			JSONObject obj = new JSONObject();
			try {
				PostDao postDao = new PostDao(DbConnection.getConnection());
				obj=postDao.getAllPostsAsJSON((JSONArray)json.get("postids"));
				System.out.println(obj);
			} catch (SQLException e) {
				System.out.println(e);
			}
			response.setContentType("text/plain");
			response.setStatus(200);
			try {
				response.getWriter().println(LocalServices.encryptData(obj.toJSONString(),dataalgo));
			}catch (Exception e) {
				System.out.println("post details share request failed");
			}
		}
	}
}
