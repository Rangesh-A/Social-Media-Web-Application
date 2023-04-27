package Model;

public class Post {
	private int post_id;
	private int user_id;
	private String content;
	private String media;
	private String time;
	private int likes;
	private int[] likers;
	private String path;
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public int getPost_id() {
		return this.post_id;
	}

	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	public int getUser_id() {
		return this.user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	
	public int[] getLikers() {
		return likers;
	}
	public void setLikers(int[] likers) {
		this.likers = likers;
	}
	public int getPostId() {
		return post_id;
	}
	public void setPostId(int post_id) {
		this.post_id = post_id;
	}
	public int getUserId() {
		return user_id;
	}
	public void setUserId(int user_id) {
		this.user_id = user_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
}
