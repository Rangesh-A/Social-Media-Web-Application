package Dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



import Model.FriendRequest;
import Model.Post;

public class RequestDao {
	private Connection connection;
	private static PreparedStatement prepared_statement;
	private static Statement statement;
	private static ResultSet result_set;

	static String SELECT_REQUESTID = "SELECT reqid FROM friend_request_details WHERE senderid=? AND receiverid=?;";
	static String UPDATE_REACT = "UPDATE friend_request_details SET isreacted=TRUE,status=? WHERE reqid=?;";
	static String SELECT_REQUESTS = "SELECT * FROM friend_request_details WHERE receiverid=? AND isreacted=FALSE;";
	static String DELETE_REQUEST = "DELETE FROM friend_request_details where senderid=? AND receiverid=?;";
	static String CHECK_REQUEST = "SELECT * FROM friend_request_details WHERE senderid=? AND receiverid=? and isreacted=false;";
	static String INSERT_REQUEST = "INSERT INTO friend_request_details(senderid,receiverid,isreacted,status,time) VALUES(?,?,?,?,?);";
	static String CREATE_REQUEST_TABLE = "CREATE TABLE IF NOT EXISTS friend_request_details(reqid SERIAL PRIMARY KEY,senderid INT REFERENCES user_details(userid) ON DELETE CASCADE,receiverid INT REFERENCES user_details(userid) ON DELETE CASCADE,isreacted boolean,status boolean,time text,UNIQUE(senderid, receiverid));";

	public RequestDao(Connection connection) throws SQLException {
		this.connection = connection;
		statement = connection.createStatement();
		statement.execute(CREATE_REQUEST_TABLE);
	}

	public void insertRequest(FriendRequest request) throws SQLException {
		prepared_statement = connection.prepareStatement(INSERT_REQUEST);
		prepared_statement.setInt(1, request.getSenderId());
		prepared_statement.setInt(2, request.getReceiverId());
		prepared_statement.setBoolean(3, request.isReacted());
		prepared_statement.setBoolean(4, request.getStatus());
		prepared_statement.setString(5, request.getTime());
		prepared_statement.execute();
	}

	public boolean isAlreadyRequestSent(int senderid, int receiverid) throws SQLException {
		prepared_statement = connection.prepareStatement(CHECK_REQUEST);
		prepared_statement.setInt(1, senderid);
		prepared_statement.setInt(2, receiverid);
		result_set = prepared_statement.executeQuery();
		return result_set.next();
	}

	public boolean isAlreadyRequestCame(int senderid, int receiverid) throws SQLException {
		prepared_statement = connection.prepareStatement(CHECK_REQUEST);
		prepared_statement.setInt(2, senderid);
		prepared_statement.setInt(1, receiverid);
		result_set = prepared_statement.executeQuery();
		return result_set.next();
	}

	public List<FriendRequest> getRequests(int receiverid) throws SQLException, FileNotFoundException, IOException {
		ArrayList<FriendRequest> requests = new ArrayList<>();
		prepared_statement = connection.prepareStatement(SELECT_REQUESTS);
		prepared_statement.setInt(1, receiverid);
		result_set = prepared_statement.executeQuery();
		while (result_set.next()) {
			FriendRequest request = new FriendRequest();
			request.setRequestId(result_set.getInt("reqid"));
			request.setSenderId(result_set.getInt("senderid"));
			request.setReceiverId(result_set.getInt("receiverid"));
			request.setStatus(result_set.getBoolean("status"));
			request.setIsReacted(result_set.getBoolean("isreacted"));
			request.setTime(result_set.getString("time"));
			requests.add(request);
		}
		return requests;
	}

	public void deleteRequest(int senderid, int receiverid) throws SQLException {
		prepared_statement = connection.prepareStatement(DELETE_REQUEST);
		prepared_statement.setInt(1, senderid);
		prepared_statement.setInt(2, receiverid);
		prepared_statement.executeUpdate();
		prepared_statement.setInt(2, senderid);
		prepared_statement.setInt(1, receiverid);
		prepared_statement.executeUpdate();
	}

	public void updateReacted(int request_id, boolean status) throws SQLException {
		prepared_statement = connection.prepareStatement(UPDATE_REACT);
		prepared_statement.setInt(2, request_id);
		prepared_statement.setBoolean(1, status);
		prepared_statement.executeUpdate();
	}

	public int getRequestId(int senderid, int receiverid) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_REQUESTID);
		prepared_statement.setInt(1, senderid);
		prepared_statement.setInt(2, receiverid);
		result_set = prepared_statement.executeQuery();
		if (result_set.next()) {
			return result_set.getInt(1);
		}
		return 1;
	}
}
