package Dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import Database.DbConnection;
import Model.Post;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PostDao {
	private Connection connection;
	private static PreparedStatement prepared_statement;
	private static Statement statement;
	private static ResultSet result_set;

	static String SELECT_TRENDING_HASHTAGS = "select hashtag,count(hashtag) from hashtag_details group by hashtag order by count(hashtag) desc;";
	static String SELECT_HASHTAG_POSTID = "SELECT * FROM hashtag_details where postid=? and hashtag=?;";
	static String SELECT_POST_BY_POSTID="SELECT * FROM post_details p,resource_details r WHERE p.postid=? AND p.resource_id=r.resource_id;";
	static String SELECT_POSTS_FROM_HASHTAG = "SELECT * FROM post_details p,resource_details r WHERE p.resource_id=r.resource_id;";
	static String UPDATE_VIEWERS = "UPDATE post_details SET viewers=? where postid=?;";
	static String SELECT_HASHTAGS = "SELECT hashtag FROM post_details where postid=?;";
	static String SELECT_VIEWERS = "SELECT viewers FROM post_details where postid=?;";
	static String SELECT_POSTID = "SELECT MAX(postid) FROM post_details;";
	static String REACT_POST = "UPDATE post_details SET likers=?,likes=? WHERE postid=?;";
	static String SELECT_LIKERS = "SELECT likers FROM post_details where postid=?;";
	static String SELECT_PATH = "SELECT resource_path FROM resource_details where resource_id=?;";
	static String DELETE_POST_QUERY = "DELETE FROM post_details WHERE postid=? RETURNING resource_id;";
	static String DELETE_RESOURCE_QUERY = "DELETE FROM resource_details WHERE resource_id=? RETURNING resource_path;";
	static String SELECT_DEPENDENT_POST = "SELECT * FROM post_details WHERE resource_id=?;";
	static String SELECT_POST_QUERY = "SELECT * from post_details p WHERE userid=? OR userid=(SELECT user1id FROM connection_details c WHERE c.user2id=? AND c.user1id=p.userid) OR userid=(SELECT user2id FROM connection_details c WHERE c.user1id=? AND c.user2id=p.userid) ORDER BY p.time DESC;";
	static String SELECT_POST_BY_USER = "SELECT * FROM post_details p,resource_details r WHERE p.userid=? AND p.resource_id=r.resource_id ORDER BY p.time DESC;";
	static String INSERT_POST = "INSERT INTO post_details(userid,content,time,media,hashtag,resource_id) VALUES(?,?,?,?,?,?) RETURNING postid;";
	static String INSERT_POST2 = "INSERT INTO post_details(userid,content,time,media,hashtag) VALUES(?,?,?,?,?);";
	static String INSERT_HASHTAG = "INSERT INTO hashtag_details(hashtag,postid) VALUES(?,?);";
	static String UPDATE_PATH = "UPDATE resource_details SET resource_path=? where resource_id=?;";
	static String CREATE_HASHTAG_TABLE = "CREATE TABLE IF NOT EXISTS hashtag_details(hashtag TEXT,postid INT REFERENCES post_details(postid) ON DELETE CASCADE)";
	static String CREATE_POST_TABLE = "CREATE TABLE IF NOT EXISTS post_details(postid SERIAL PRIMARY KEY,userid INT REFERENCES user_details(userid) ON DELETE CASCADE,likes INT DEFAULT 0,content TEXT,time TEXT,media TEXT,hashtag TEXT[],likers INT[],viewers INT[],path TEXT,resource_id INT REFERENCES resource_details(resource_id));";
	static String CREATE_CONNECTION_TABLE = "CREATE TABLE IF NOT EXISTS connection_details(connectionid SERIAL PRIMARY KEY,user1id INT REFERENCES user_details(userid) ON DELETE CASCADE,user2id INT REFERENCES user_details(userid) ON DELETE CASCADE,time text,UNIQUE(user1id,user2id),UNIQUE(user2id,user1id));";
	static String CREATE_RESOURCE_TABLE = "CREATE TABLE IF NOT EXISTS resource_details(resource_id SERIAL primary key,hashvalue text,resource_path text);";
	static String SELECT_RESOURCEID_BY_HASHID = "SELECT resource_id from resource_details WHERE hashvalue=?;";
	static String INSERT_RESOURCE = "INSERT INTO resource_details(hashvalue) values(?) RETURNING resource_id;";

	public PostDao(Connection connection) throws SQLException {
		this.connection = connection;
		statement = connection.createStatement();
		statement.execute(CREATE_POST_TABLE);
		statement.execute(CREATE_HASHTAG_TABLE);
		statement.execute(CREATE_CONNECTION_TABLE);
		statement.execute(CREATE_RESOURCE_TABLE);
	}
	public void updatePath(int resource_id,String path)throws SQLException{
		prepared_statement = connection.prepareStatement(UPDATE_PATH);
		prepared_statement.setString(1, path);
		prepared_statement.setInt(2, resource_id);
		prepared_statement.executeUpdate();
	}
	public int insertResource(String hashvalue) throws SQLException {
		prepared_statement = connection.prepareStatement(INSERT_RESOURCE);
		prepared_statement.setString(1,hashvalue);
		result_set = prepared_statement.executeQuery();
		result_set.next();
		return result_set.getInt(1);
	}
	public int getResourceIdByHash(String hashvalue)throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_RESOURCEID_BY_HASHID);
		prepared_statement.setString(1,hashvalue);
		result_set = prepared_statement.executeQuery();
		if(result_set.next()){
			return result_set.getInt(1);
		}
		return 0;
	}
	
	public int insertPost(Post post, String[] hastags,int resource_id) throws SQLException {
		prepared_statement = connection.prepareStatement(INSERT_POST);
		prepared_statement.setInt(1, post.getUserId());
		prepared_statement.setString(2, post.getContent());
		prepared_statement.setString(3, post.getTime());
		prepared_statement.setString(4, post.getMedia());
		prepared_statement.setArray(5, connection.createArrayOf("TEXT", hastags));
		prepared_statement.setInt(6, resource_id);
		result_set = prepared_statement.executeQuery();
		result_set.next();
		return result_set.getInt(1);
	}
	public int insertPost(Post post, String[] hastags) throws SQLException {
		prepared_statement = connection.prepareStatement(INSERT_POST2);
		prepared_statement.setInt(1, post.getUserId());
		prepared_statement.setString(2, post.getContent());
		prepared_statement.setString(3, post.getTime());
		prepared_statement.setString(4, post.getMedia());
		prepared_statement.setArray(5, connection.createArrayOf("TEXT", hastags));
		prepared_statement.execute();
		prepared_statement = connection.prepareStatement(SELECT_POSTID);
		result_set = prepared_statement.executeQuery();
		result_set.next();
		return result_set.getInt(1);
	} 
	public List<Post> getPostsById(int userid) throws SQLException, FileNotFoundException, IOException {
		ArrayList<Post> posts = new ArrayList<>();
		prepared_statement = connection.prepareStatement(SELECT_POST_BY_USER);
		prepared_statement.setInt(1, userid);
		result_set = prepared_statement.executeQuery();
		while (result_set.next()) {
			Post post = new Post();
			post.setPostId(Integer.parseInt(result_set.getString("postid")));
			post.setUserId(Integer.parseInt(result_set.getString("userid")));
			post.setTime(result_set.getString("time"));
			post.setMedia(result_set.getString("media"));
			post.setLikes(Integer.parseInt(result_set.getString("likes")));
			post.setContent(result_set.getString("content"));
			String path=result_set.getString("resource_path");
			path=path.replace("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\","");
			path=path.replace("\\","/");
			post.setPath(path);
			posts.add(post);
		}
		return posts;
	}
	public Post getPostById(int postid) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_POST_BY_POSTID);
		prepared_statement.setInt(1, postid);
		result_set = prepared_statement.executeQuery();
		if (result_set.next()) {
			Post post = new Post();
			post.setPostId(Integer.parseInt(result_set.getString("postid")));
			post.setUserId(Integer.parseInt(result_set.getString("userid")));
			post.setTime(result_set.getString("time"));
			post.setMedia(result_set.getString("media"));
			post.setLikes(Integer.parseInt(result_set.getString("likes")));
			post.setContent(result_set.getString("content"));
			String path=result_set.getString("resource_path");
			path=path.replace("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\","");
			path=path.replace("\\","/");
			post.setPath(path);
			return post;
		}
		return null;
	}

	public boolean isHashtagAvailable(int postid, String hashtag) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_HASHTAG_POSTID);
		prepared_statement.setInt(1, postid);
		prepared_statement.setString(2, hashtag);
		result_set = prepared_statement.executeQuery();
		return result_set.next();
	}

	public List<Post> getAllPosts(int userid) throws SQLException, FileNotFoundException, IOException {
		ArrayList<Post> posts = new ArrayList<>();
		prepared_statement = connection.prepareStatement(SELECT_POST_QUERY);
		prepared_statement.setInt(1, userid);
		prepared_statement.setInt(2, userid);
		prepared_statement.setInt(3, userid);
		result_set = prepared_statement.executeQuery();
		while (result_set.next()) {
			Post post = new Post();
			int resourceid=result_set.getInt("resource_id");
			post.setPostId(Integer.parseInt(result_set.getString("postid")));
			post.setUserId(Integer.parseInt(result_set.getString("userid")));
			post.setTime(result_set.getString("time"));
			post.setMedia(result_set.getString("media"));
			post.setLikes(Integer.parseInt(result_set.getString("likes")));
			post.setContent(result_set.getString("content"));
			if(resourceid!=0){
				String path=getResourcePath(resourceid);
				path=path.replace("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\","");
				path=path.replace("\\","/");
				post.setPath(path);
			}
			posts.add(post);
		}
		return posts;
	}
	public JSONArray getAllPostsAsJSON(int userid) throws Exception {
		JSONArray posts = new JSONArray();
		prepared_statement = connection.prepareStatement(SELECT_POST_QUERY);
		prepared_statement.setInt(1, userid);
		prepared_statement.setInt(2, userid);
		prepared_statement.setInt(3, userid);
		result_set = prepared_statement.executeQuery();
		while (result_set.next()) {
			JSONObject post = new JSONObject();
			int resourceid=result_set.getInt("resource_id");
			post.put("postid",result_set.getInt("postid"));
			post.put("userid",result_set.getInt("userid"));
			post.put("time",result_set.getString("time"));
			post.put("likes",result_set.getInt("likes"));
			post.put("media",result_set.getString("media"));
			post.put("content",result_set.getString("content"));
			String path=getResourcePath(resourceid);
			path=path.replace("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\","");
			path=path.replace("\\","/");
			post.put("path",path);
			posts.add(post);
		}
		return posts;
	}
	public JSONObject getAllPostsAsJSON(JSONArray postids) throws SQLException, FileNotFoundException, IOException {
		JSONObject allposts=new JSONObject();
		prepared_statement = connection.prepareStatement(SELECT_POST_BY_POSTID);
		System.out.println("c1");
		JSONArray posts=new JSONArray();
		for (int i = 0; i < postids.size(); i++) {
			System.out.println(postids.get(i).toString());
			System.out.println("c2");
			prepared_statement.setInt(1,Integer.parseInt(postids.get(i).toString()));
			System.out.println("c3");
			result_set = prepared_statement.executeQuery();
			if(result_set.next()){
				JSONObject post=new JSONObject();
				post.put("postid",result_set.getInt("postid"));
				post.put("username",new UserDao(DbConnection.getConnection()).getUserById(result_set.getInt("userid")).getUserName());
				post.put("content",result_set.getString("content"));
				post.put("media",result_set.getString("media"));
				post.put("path",result_set.getString("resource_path"));
				posts.add(post);
			}
		}
		System.out.println(allposts);
		allposts.put("postdetails",posts);
		return allposts;
	}
	
	public List<Post> getAllUserPosts() throws SQLException, FileNotFoundException, IOException {
		ArrayList<Post> posts = new ArrayList<>();
		prepared_statement = connection.prepareStatement(SELECT_POSTS_FROM_HASHTAG);
		result_set = prepared_statement.executeQuery();
		while (result_set.next()) {
			Post post = new Post();
			post.setPostId(Integer.parseInt(result_set.getString("postid")));
			post.setUserId(Integer.parseInt(result_set.getString("userid")));
			post.setTime(result_set.getString("time"));
			post.setMedia(result_set.getString("media"));
			post.setLikes(Integer.parseInt(result_set.getString("likes")));
			post.setContent(result_set.getString("content"));
			String path=result_set.getString("resource_path");
			path=path.replace("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\Zoho_Social\\","");
			path=path.replace("\\","/");
			post.setPath(path);
			posts.add(post);
		}
		return posts;
	}

	public void reactPost(int post_id, int user_id, boolean type) throws SQLException {
		Integer new_likers[] = getLikers(post_id);
		if (type) {
			new_likers = getLikers(post_id);
			new_likers[new_likers.length - 1] = user_id;
		} else {
			new_likers = removeLike(user_id, getLikers(post_id));
		}
		Array likers_array = connection.createArrayOf("int", new_likers);
		prepared_statement = connection.prepareStatement(REACT_POST);
		prepared_statement.setArray(1, likers_array);
		prepared_statement.setInt(2, new_likers.length);
		prepared_statement.setInt(3, post_id);
		prepared_statement.executeUpdate();
	}

	public Integer[] removeLike(int user_id, Integer[] likers) {
		Integer new_likers[] = new Integer[likers.length - 2];
		for (int i = 0; i < likers.length - 1; i++) {
			if (likers[i] != null && likers[i] != user_id) {
				new_likers[i] = likers[i];
			}
		}
		return new_likers;
	}

	public Integer[] getLikers(int post_id) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_LIKERS);
		prepared_statement.setInt(1, post_id);
		result_set = prepared_statement.executeQuery();
		Integer likers[] = null;
		Array temp = null;
		if (result_set.next()) {
			temp = result_set.getArray(1);
		}
		Integer new_likers[] = null;
		if (temp != null && temp.getArray() != null) {
			likers = (Integer[]) temp.getArray();
			new_likers = new Integer[likers.length + 1];
			for (int i = 0; i < likers.length; i++) {
				new_likers[i] = likers[i];
			}
		} else {
			new_likers = new Integer[1];
		}
		return new_likers;
	}

	public String[] getHashTags(int post_id) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_HASHTAGS);
		prepared_statement.setInt(1, post_id);
		result_set = prepared_statement.executeQuery();
		String hashtags[] = null;
		Array temp = null;
		if (result_set.next()) {
			temp = result_set.getArray(1);
		}
		String new_likers[] = null;
		if (temp != null && temp.getArray() != null) {
			hashtags = (String[]) temp.getArray();
			new_likers = new String[hashtags.length + 1];
			for (int i = 0; i < hashtags.length; i++) {
				new_likers[i] = hashtags[i];
			}
		} else {
			new_likers = new String[1];
		}
		return hashtags;
	}

	public Integer[] getViewers(int post_id) throws SQLException {
		prepared_statement = connection.prepareStatement(SELECT_VIEWERS);
		prepared_statement.setInt(1, post_id);
		result_set = prepared_statement.executeQuery();
		Integer likers[] = null;
		Array temp = null;
		if (result_set.next()) {
			temp = result_set.getArray(1);
		}
		Integer new_likers[] = null;
		if (temp != null && temp.getArray() != null) {
			likers = (Integer[]) temp.getArray();
			new_likers = new Integer[likers.length + 1];
			for (int i = 0; i < likers.length; i++) {
				new_likers[i] = likers[i];
			}
		} else {
			new_likers = new Integer[1];
		}
		return new_likers;
	}

	public void viewPost(int post_id, int user_id) throws SQLException {
		Integer new_likers[] = getViewers(post_id);
		new_likers[new_likers.length - 1] = user_id;
		Array likers_array = connection.createArrayOf("int", new_likers);
		prepared_statement = connection.prepareStatement(UPDATE_VIEWERS);
		prepared_statement.setArray(1, likers_array);
		prepared_statement.setInt(2, post_id);
		prepared_statement.executeUpdate();
	}

	public boolean isViewedByUser(int user_id, int post_id) throws SQLException {
		Integer likers[] = getViewers(post_id);
		for (Integer liker : likers) {
			if (liker != null && user_id == liker) {
				return true;
			}
		}
		return false;
	}

	public boolean isLikedByUser(int user_id, int post_id) throws SQLException {
		Integer likers[] = getLikers(post_id);
		for (Integer liker : likers) {
			if (liker != null && user_id == liker) {
				return true;
			}
		}
		return false;
	}

	public int deletePost(int postid) throws SQLException {
		prepared_statement = connection.prepareStatement(DELETE_POST_QUERY);
		prepared_statement.setInt(1, postid);
		result_set= prepared_statement.executeQuery();
		result_set.next();
		return result_set.getInt(1);
	}

	public void insertHashtags(int postId, ArrayList<String> hashTags) throws SQLException {
		prepared_statement = connection.prepareStatement(INSERT_HASHTAG);
		for (String hashtag : hashTags) {
			prepared_statement.setString(1, hashtag);
			prepared_statement.setInt(2, postId);
			prepared_statement.executeUpdate();
		}
	}

	public LinkedHashMap<String, Integer> getTrendingHashTags() throws SQLException {
		LinkedHashMap<String, Integer> trends = new LinkedHashMap<>();
		prepared_statement = connection.prepareStatement(SELECT_TRENDING_HASHTAGS);
		result_set = prepared_statement.executeQuery();
		while (result_set.next()) {
			String tag = result_set.getString("hashtag");
			trends.put(tag, result_set.getInt(2));
		}
		return trends;
	}
	public boolean isResourceDependent(int resource_id)throws SQLException{
		System.out.println("delete-post-resource_id= "+resource_id);
		prepared_statement = connection.prepareStatement(SELECT_DEPENDENT_POST);
		prepared_statement.setInt(1, resource_id);
		result_set=prepared_statement.executeQuery();
		return result_set.next();
	}
	public String removeResourceAndGetPath(int resource_id)throws SQLException{
		prepared_statement = connection.prepareStatement(DELETE_RESOURCE_QUERY);
		prepared_statement.setInt(1, resource_id);
		result_set= prepared_statement.executeQuery();
		result_set.next();
		return result_set.getString(1);
	}
	public String getResourcePath(int resource_id)throws SQLException{
		PreparedStatement prepared_statement = connection.prepareStatement(SELECT_PATH);
		prepared_statement.setInt(1, resource_id);
		ResultSet result_set= prepared_statement.executeQuery();
		result_set.next();
		return result_set.getString(1);
	}
}
