package Model;

import java.io.InputStream;

public class User {
	@Override
	public String toString() {
		return "User [user_name=" + user_name + ", phone_number=" + phone_number + ", password=" + password + ", email="
				+ email + ", friends=" + friends + ", bio=" + bio + ", livesin=" + livesin + ", account_opened="
				+ account_opened + ", profilepic=" + profilepic + ", isOnline=" + isOnline + "]";
	}
	private int user_id;
	private String user_name;
	private String phone_number;
	private String password;
	private String email;
	private int friends;
	private String bio;
	private String livesin;
	private String account_opened;

	public int getUnreadmessages() {
		return unreadmessages;
	}

	public void setUnreadmessages(int unreadmessages) {
		this.unreadmessages = unreadmessages;
	}

	private String profilepic;
	private int unreadmessages;
	
	public int getUserId() {
		return user_id;
	}
	public void setUserId(int user_id) {
		this.user_id = user_id;
	}
	public String getProfilepic() {
		return profilepic;
	}
	public void setProfilepic(String profilepic) {
		this.profilepic = profilepic;
	}
	public String getLivesIn() {
		return livesin;
	}
	public void setLivesIn(String livesin) {
		this.livesin = livesin;
	}
	public String getAccountOpened() {
		return account_opened;
	}
	public void setAccountOpened(String account_opened) {
		this.account_opened = account_opened;
	}
	private Boolean isOnline;
	public String getUserName() {
		return user_name;
	}
	public void setUserName(String user_name) {
		this.user_name = user_name;
	}
	public String getPhoneNumber() {
		return phone_number;
	}
	public void setPhoneNumber(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getFriends() {
		return friends;
	}
	public void setFriends(int friends) {
		this.friends = friends;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public Boolean getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}
}
