package Dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Database.DbConnection;
import Model.FriendRequest;
import Model.Post;
import Model.User;

public class ConnectionDao {
	private Connection connection;
	private static PreparedStatement prepared_statement;
	private static Statement statement;
	private static ResultSet result_set;

	static String SELECT_CONNECTION_TIME = "SELECT time FROM connection_details WHERE user1id=? AND user2id=? OR user2id=? AND user1id=?;";
	static String DELETE_CONNECTION = "DELETE FROM connection_details WHERE user1id=? AND user2id=? OR user2id=? or user1id=?;";
	static String CHECK_CONNECTION = "SELECT * FROM connection_details WHERE user1id=? AND user2id=?;";
	static String SELECT_FRIENDS = "SELECT user2id FROM connection_details WHERE user1id=? ";
	static String SELECT_FRIENDS2 = "SELECT user1id FROM connection_details WHERE user2id=? ";
	static String INSERT_CONNECTION = "INSERT INTO connection_details(user1id,user2id,time) VALUES(?,?,?);";
	static String CREATE_CONNECTION_TABLE = "CREATE TABLE IF NOT EXISTS connection_details(connectionid SERIAL PRIMARY KEY,user1id INT REFERENCES user_details(userid) ON DELETE CASCADE,user2id INT REFERENCES user_details(userid) ON DELETE CASCADE,time text,UNIQUE(user1id,user2id),UNIQUE(user2id,user1id));";

	public ConnectionDao(Connection connection) throws SQLException {
		this.connection = connection;
		statement = connection.createStatement();
		statement.execute(CREATE_CONNECTION_TABLE);
	}

	public void insertConnection(int user1id, int user2id, String time) throws SQLException {
		prepared_statement = connection.prepareStatement(INSERT_CONNECTION);
		prepared_statement.setInt(1, user1id);
		prepared_statement.setInt(2, user2id);
		prepared_statement.setString(3, time);
		prepared_statement.execute();
	}

	public boolean isFriends(int user1id, int user2id) throws SQLException {
		prepared_statement = connection.prepareStatement(CHECK_CONNECTION);
		prepared_statement.setInt(1, user1id);
		prepared_statement.setInt(2, user2id);
		result_set = prepared_statement.executeQuery();
		boolean check1 = result_set.next();
		prepared_statement.setInt(1, user2id);
		prepared_statement.setInt(2, user1id);
		result_set = prepared_statement.executeQuery();
		boolean check2 = result_set.next();
		return check1 || check2;
	}

	public List<User> getAllFriends(int userid) throws SQLException, FileNotFoundException, IOException {
		ArrayList<User> friends = new ArrayList<>();
		prepared_statement = connection.prepareStatement(SELECT_FRIENDS);
		prepared_statement.setInt(1, userid);
		result_set = prepared_statement.executeQuery();
		while (result_set.next()) {
			User user = new User();
			UserDao userdao = new UserDao(connection);
			user = userdao.getUserById(result_set.getInt(1));
			friends.add(user);
		}
		prepared_statement = connection.prepareStatement(SELECT_FRIENDS2);
		prepared_statement.setInt(1, userid);
		result_set = prepared_statement.executeQuery();
		while (result_set.next()) {
			User user = new User();
			UserDao userdao = new UserDao(connection);
			user = userdao.getUserById(result_set.getInt(1));
			friends.add(user);
		}
		return friends;
	}

	public void deleteConnection(int user1id, int user2id) throws SQLException {
		prepared_statement = connection.prepareStatement(DELETE_CONNECTION);
		prepared_statement.setInt(1, user1id);
		prepared_statement.setInt(2, user2id);
		prepared_statement.setInt(3, user1id);
		prepared_statement.setInt(4, user2id);
		prepared_statement.executeUpdate();
	}

	public String getConnectionTime(int user1id, int user2id) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_CONNECTION_TIME);
		prepared_statement.setInt(1, user1id);
		prepared_statement.setInt(2, user2id);
		prepared_statement.setInt(3, user1id);
		prepared_statement.setInt(4, user2id);
		result_set = prepared_statement.executeQuery();
		if (result_set.next()) {
			return result_set.getString("time");
		}
		return "";
	}
}
