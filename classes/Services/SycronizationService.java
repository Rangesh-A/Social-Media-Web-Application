package Services;

import Dao.ConnectionLogDao;
import Dao.UserDao;
import Database.DbConnection;
import Model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import static Services.LocalServices.*;

public class SycronizationService {
    static void syncUnreadMessages() throws Exception {
        URL url = new URL("http://localhost:8080/Zoho_Chat/sync-to-social");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("type", "unreadmessages");
        obj.put("subtype","GET");
        OutputStream os = conn.getOutputStream();
        String signature=LocalServices.createSignature(obj.toJSONString());
        String signature_algorithm="SHA1withDSA";
        String data_algorithm="AES/ECB/PKCS5Padding";
//        System.out.println(encryptData(obj.toJSONString(),data_algorithm));
        String request="data="+encryptData(obj.toJSONString(),data_algorithm)+"&signature="+signature+"&signaturealgo="+signature_algorithm+"&dataalgo="+data_algorithm;
//        System.out.println(request);
        os.write(request.getBytes());
        os.flush();
        os.close();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        line = reader.readLine();
        if(line.equals("NO updates")){
            return;
        }
        reader.close();
        String data=decryptData(line,data_algorithm);
        System.out.println(data);
        UserDao userdao=new UserDao(DbConnection.getConnection());
        userdao.updateUnreadMessages(LocalServices.jsonToHashmap(data));
    }
    static void syncConnectionDetails()throws Exception{
        System.out.println("connection syncing...");
        URL url = new URL("http://localhost:8080/Zoho_Chat/sync-from-social");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        JSONObject logs_cont = new JSONObject();
        JSONArray logs=null;
        ConnectionLogDao connectionLogDao=new ConnectionLogDao(DbConnection.getConnection());
        logs=connectionLogDao.getConnections();
        logs_cont.put("connectionlogs",logs);
        System.out.println("log = "+logs);
        if(logs.isEmpty()){
            System.out.println("log table is empty");
            return;
        }
        OutputStream os = conn.getOutputStream();

        String signature= null;
        try {
            signature = LocalServices.createSignature(logs_cont.toJSONString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String request_body= null;
        String signature_algorithm="SHA1withDSA";
        String data_algorithm="AES/ECB/PKCS5Padding";
        System.out.println("actual data= "+logs_cont);
        System.out.println("sending signature= "+signature);
        System.out.println("sending data= "+LocalServices.encryptData(logs_cont.toJSONString(),data_algorithm));
        try {
            request_body="data<>"+LocalServices.encryptData(logs_cont.toJSONString(),data_algorithm)+"&signature<>"+signature+"&signaturealgo<>"+signature_algorithm+"&dataalgo<>"+data_algorithm;
        }  catch (Exception e) {
            return;
        }
        os.write(request_body.getBytes());
        os.flush();
        os.close();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        String response="";
        while ((line = reader.readLine()) != null) {
            System.out.println("response="+line);
            response=line;
        }
        if(response.equals("success")){
            connectionLogDao.truncateTable();
        }
        reader.close();
    }
}
