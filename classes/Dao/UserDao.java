package Dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.User;
import Services.*;

public class UserDao {
	private Connection connection;
	private static PreparedStatement prepared_statement;
	private static Statement statement;
	private static ResultSet result_set;

	static String DELETE_USER = "DELETE FROM user_details WHERE userid=?;";
	static String UPDATE_PASSWORD = "UPDATE user_details SET password=? WHERE userid=? AND password=?;";
	static String SEARCH_USER = "SELECT * FROM user_details WHERE userid!=? AND username LIKE '%";
	static String SELECT_ALl_USERS = "SELECT * FROM user_details WHERE userid!=?;";
	static String INSERT_USER = "INSERT INTO user_details(username,email,phonenumber,password,account_opened,profilepic,signin_type) VALUES(?,?,?,?,?,?,?);";
	static String UPDATE_USER = "UPDATE user_details SET username=?,bio=?,profilepic=?,livesin=? WHERE userid=?;";
	static String UPDATE_USER_NO_PIC = "UPDATE user_details SET username=?,bio=?,livesin=? WHERE userid=?;";
	static String UPDATE_UNREAD_MESSAGES = "UPDATE user_details SET unreadmessages=? WHERE userid=?;";
	static String SELECT_USER_QUERY = "SELECT * FROM user_details where phonenumber=?  AND password=? OR email=?  AND password=? OR username=? AND password=? OR email=? AND signin_type=?";
	static String SELECT_ADUSER_QUERY = "SELECT * FROM user_details where username=? OR phonenumber=? OR email=?;";
	static String SELECT_SSOADUSER_QUERY = "SELECT * FROM user_details where domainid=?;";
	static String SELECT_USER_BY_ID = "SELECT * FROM user_details WHERE userid=?;";
	static String SELECT_USER_BY_NAME = "SELECT * FROM user_details WHERE username=?;";
	static String SELECT_USER_BY_PHONE_NUMBER = "SELECT * FROM user_details WHERE phonenumber=?;";
	static String SELECT_USER_BY_EMAIL = "SELECT * FROM user_details WHERE email=?;";
	static String SELECT_UNREADMESSAGES= "SELECT unreadmessages FROM user_details WHERE userid=?;";
	static String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS user_details(userid SERIAL PRIMARY KEY,username text NOT NULL,email text UNIQUE NOT NULL,phonenumber text UNIQUE,password text,bio text,livesin text,account_opened text NOT NULL,profilepic TEXT,isonline BOOLEAN,signin_type TEXT,unreadmessages INT DEFAULT 0);";

	public UserDao(Connection connection) throws SQLException {
		this.connection = connection;
		statement = connection.createStatement();
		statement.execute(CREATE_USER_TABLE);
	}

	public void insertUser(User user, String signin_type) throws SQLException {
		prepared_statement = connection.prepareStatement(INSERT_USER);
		prepared_statement.setString(1, user.getUserName());
		prepared_statement.setString(2, user.getEmail());
		prepared_statement.setString(3, user.getPhoneNumber());
		prepared_statement.setString(4, user.getPassword());
		prepared_statement.setString(5, user.getAccountOpened());
		prepared_statement.setString(6, user.getProfilepic());
		prepared_statement.setString(7, signin_type);
		prepared_statement.execute();
	}

	public boolean isUsernameExists(String username) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_USER_BY_NAME);
		prepared_statement.setString(1, username);
		result_set = prepared_statement.executeQuery();
		return result_set.next();
	}

	public boolean isEmailExists(String email) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_USER_BY_EMAIL);
		prepared_statement.setString(1, email);
		result_set = prepared_statement.executeQuery();
		return result_set.next();
	}

	public boolean isPhoneNumberExists(String phone_nubmer) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_USER_BY_PHONE_NUMBER);
		prepared_statement.setString(1, phone_nubmer);
		result_set = prepared_statement.executeQuery();
		return result_set.next();
	}

	public User getUserById(int userid) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_USER_BY_ID);
		prepared_statement.setInt(1, userid);
		result_set = prepared_statement.executeQuery();
		if (result_set.next()) {
			User user = new User();
			user.setUserId(result_set.getInt("userid"));
			user.setPhoneNumber(result_set.getString("phonenumber"));
			user.setLivesIn(result_set.getString("livesin"));
			user.setProfilepic(result_set.getString("profilepic"));
			user.setBio(result_set.getString("bio"));
			user.setUserName(LocalServices.capitalizeName(result_set.getString("username")));
			return user;
		}
		return null;
	}

	public User getUser(String phone_email_username, String password, String signin_type)
		throws SQLException, FileNotFoundException, IOException {
		prepared_statement = connection.prepareStatement(SELECT_USER_QUERY);
		prepared_statement.setString(1, phone_email_username);
		prepared_statement.setString(2, password);
		prepared_statement.setString(3, phone_email_username);
		prepared_statement.setString(4, password);
		prepared_statement.setString(5, phone_email_username);
		prepared_statement.setString(6, password);
		prepared_statement.setString(7, phone_email_username);
		prepared_statement.setString(8, signin_type);
		result_set = prepared_statement.executeQuery();
		if (result_set.next()) {
			User user = new User();
			user.setUserId(Integer.parseInt(result_set.getString("userid")));
			user.setUserName(LocalServices.capitalizeName(result_set.getString("username")));
			user.setEmail((result_set.getString("email")));
			user.setPhoneNumber((result_set.getString("phonenumber")));
			user.setPassword((result_set.getString("password")));
			user.setIsOnline(result_set.getBoolean("isonline"));
			user.setAccountOpened(result_set.getString("account_opened"));
			user.setLivesIn(result_set.getString("livesin"));
			user.setProfilepic(result_set.getString("profilepic"));
			user.setBio(result_set.getString("bio"));
			user.setUnreadmessages(result_set.getInt("unreadmessages"));
			return user;
		}
		return null;
	}
	public User getAdUser(String phone_email_username)
		throws SQLException, FileNotFoundException, IOException {
		prepared_statement = connection.prepareStatement(SELECT_ADUSER_QUERY);
		prepared_statement.setString(1, phone_email_username);
		prepared_statement.setString(2, phone_email_username);
		prepared_statement.setString(3, phone_email_username);
		result_set = prepared_statement.executeQuery();
		if (result_set.next()) {
			User user = new User();
			user.setUserId(Integer.parseInt(result_set.getString("userid")));
			user.setUserName(LocalServices.capitalizeName(result_set.getString("username")));
			user.setEmail((result_set.getString("email")));
			user.setPhoneNumber((result_set.getString("phonenumber")));
			user.setPassword((result_set.getString("password")));
			user.setIsOnline(result_set.getBoolean("isonline"));
			user.setAccountOpened(result_set.getString("account_opened"));
			user.setLivesIn(result_set.getString("livesin"));
			user.setProfilepic(result_set.getString("profilepic"));
			user.setBio(result_set.getString("bio"));
			user.setUnreadmessages(result_set.getInt("unreadmessages"));
			return user;
		}
		return null;
	}
	public User getSSOAdUser(String phone_email_username)
		throws SQLException, FileNotFoundException, IOException {
		prepared_statement = connection.prepareStatement(SELECT_SSOADUSER_QUERY);
		prepared_statement.setString(1, phone_email_username);
		result_set = prepared_statement.executeQuery();
		if (result_set.next()) {
			User user = new User();
			user.setUserId(Integer.parseInt(result_set.getString("userid")));
			user.setUserName(LocalServices.capitalizeName(result_set.getString("username")));
			user.setEmail((result_set.getString("email")));
			user.setPhoneNumber((result_set.getString("phonenumber")));
			user.setPassword((result_set.getString("password")));
			user.setIsOnline(result_set.getBoolean("isonline"));
			user.setAccountOpened(result_set.getString("account_opened"));
			user.setLivesIn(result_set.getString("livesin"));
			user.setProfilepic(result_set.getString("profilepic"));
			user.setBio(result_set.getString("bio"));
			user.setUnreadmessages(result_set.getInt("unreadmessages"));
			return user;
		}
		return null;
	}

	public List<User> getUsers(int current_userid, String username)
			throws SQLException, FileNotFoundException, IOException {
		prepared_statement = connection.prepareStatement(SEARCH_USER + username + "%';");
		prepared_statement.setInt(1, current_userid);
		result_set = prepared_statement.executeQuery();
		ArrayList<User> users = new ArrayList<>();
		while (result_set.next()) {
			User user = new User();
			user.setUserId(Integer.parseInt(result_set.getString("userid")));
			user.setUserName(LocalServices.capitalizeName(result_set.getString("username")));
			user.setLivesIn(result_set.getString("livesin"));
			user.setProfilepic(result_set.getString("profilepic"));
			users.add(user);
		}
		return users;
	}

	public List<User> getAllUsers(int current_userid) throws SQLException, FileNotFoundException, IOException {
		prepared_statement = connection.prepareStatement(SELECT_ALl_USERS);
		prepared_statement.setInt(1, current_userid);
		result_set = prepared_statement.executeQuery();
		ArrayList<User> users = new ArrayList<>();
		while (result_set.next()) {
			User user = new User();
			user.setUserId(Integer.parseInt(result_set.getString("userid")));
			user.setUserName(LocalServices.capitalizeName(result_set.getString("username")));
			user.setLivesIn(result_set.getString("livesin"));
			user.setProfilepic(result_set.getString("profilepic"));
			users.add(user);
		}
		return users;
	}

	public void updateUser(User user) throws SQLException {
		prepared_statement = connection.prepareStatement(UPDATE_USER);
		prepared_statement.setString(1, user.getUserName());
		prepared_statement.setString(2, user.getBio());
		prepared_statement.setString(3,user.getUserId()+"");
		prepared_statement.setString(4, user.getLivesIn());
		prepared_statement.setInt(5, user.getUserId());
		prepared_statement.executeUpdate();
	}
	public void updateUserWithOutProfilePic(User user) throws SQLException {
		prepared_statement = connection.prepareStatement(UPDATE_USER_NO_PIC);
		prepared_statement.setString(1, user.getUserName());
		prepared_statement.setString(2, user.getBio());
		prepared_statement.setString(3, user.getLivesIn());
		prepared_statement.setInt(4, user.getUserId());
		prepared_statement.executeUpdate();
	}
	public void updateUnreadMessages(HashMap<Integer,Integer> hashmap) throws SQLException {
		prepared_statement = connection.prepareStatement(UPDATE_UNREAD_MESSAGES);
		for(Map.Entry user:hashmap.entrySet()){
			prepared_statement.setInt(1, (Integer)user.getValue());
			prepared_statement.setInt(2, (Integer)user.getKey());
			prepared_statement.executeUpdate();
		}
	}

	public boolean changePassword(int user_id, String old_password, String new_password) throws SQLException {
		prepared_statement = connection.prepareStatement(UPDATE_PASSWORD);
		prepared_statement.setString(1, new_password);
		prepared_statement.setInt(2, user_id);
		prepared_statement.setString(3, old_password);
		return prepared_statement.executeUpdate() > 0;
	}

	public void deleteUser(int user_id) throws SQLException {
		prepared_statement = connection.prepareStatement(DELETE_USER);
		prepared_statement.setInt(1, user_id);
		prepared_statement.executeUpdate();
	}

	public int getunreadMessages(int userid) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_UNREADMESSAGES);
		prepared_statement.setInt(1, userid);
		result_set = prepared_statement.executeQuery();
		result_set.next();
		return result_set.getInt(1);
	}
}
