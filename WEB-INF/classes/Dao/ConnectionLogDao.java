package Dao;

import Model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionLogDao {
	private Connection connection;
	private static PreparedStatement prepared_statement;
	private static Statement statement;
	private static ResultSet result_set;

	static String INSERT_CONNECTIONLOG = "INSERT INTO connection_log(user1id,user2id,event,timestamp) VALUES(?,?,?,NOW()::timestamp);";
	static String TRUNCATE_CONNECTIONLOG = "TRUNCATE TABLE connection_log;";
	static String CREATE_CONNECTIONLOG_TABLE = "CREATE TABLE IF NOT EXISTS connection_log(logid SERIAL PRIMARY KEY,user1id INT REFERENCES user_details(userid) ON DELETE CASCADE,user2id INT REFERENCES user_details(userid) ON DELETE CASCADE,event TEXT,timestamp timestamp);";
	static String SELECT_DISTINCT_lOG="SELECT DISTINCT user1id,user2id FROM connection_log;";
	static String SELECT_LATEST_lOG="SELECT * FROM connection_log WHERE user1id=? AND user2id=? ORDER BY timestamp DESC LIMIT 1;";
	public ConnectionLogDao(Connection connection) throws SQLException {
		this.connection = connection;
		statement = connection.createStatement();
		statement.execute(CREATE_CONNECTIONLOG_TABLE);
	}
	public void insertConnectionLog(int user1id, int user2id,String event) throws SQLException {
		prepared_statement = connection.prepareStatement(INSERT_CONNECTIONLOG);
		prepared_statement.setInt(1, user1id);
		prepared_statement.setInt(2, user2id);
		prepared_statement.setString(3, event);
		prepared_statement.execute();
	}
	public void truncateTable() throws SQLException {
		prepared_statement = connection.prepareStatement(TRUNCATE_CONNECTIONLOG);
		prepared_statement.execute();
	}
	public JSONArray getConnections() throws SQLException {
		JSONArray logs=new JSONArray();
		prepared_statement = connection.prepareStatement(SELECT_DISTINCT_lOG);
		result_set=prepared_statement.executeQuery();
		PreparedStatement preparedStatement2=connection.prepareStatement(SELECT_LATEST_lOG);
		while (result_set.next()){
			JSONObject log=new JSONObject();
			int user1id=result_set.getInt(1);
			int user2id=result_set.getInt(2);
			preparedStatement2.setInt(1,user1id);
			preparedStatement2.setInt(2,user2id);
			ResultSet resultSet2=preparedStatement2.executeQuery();
			resultSet2.next();
			log.put("user1id",user1id);
			log.put("user2id",user2id);
			log.put("event",resultSet2.getString("event"));
			logs.add(log);
		}
		System.out.println(logs);
		return logs;
	}
}
