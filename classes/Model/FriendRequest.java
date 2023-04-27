package Model;

public class FriendRequest {
	private int request_id;
	private int sender_id;
	private int receiver_id;
	private boolean reacted;
	private boolean status;
	private String time;
	public int getRequestId() {
		return request_id;
	}
	public void setRequestId(int request_id) {
		this.request_id = request_id;
	}
	public int getSenderId() {
		return sender_id;
	}
	public void setSenderId(int sender_id) {
		this.sender_id = sender_id;
	}
	public int getReceiverId() {
		return receiver_id;
	}
	public void setReceiverId(int receiver_id) {
		this.receiver_id = receiver_id;
	}
	public boolean isReacted() {
		return reacted;
	}
	public void setIsReacted(boolean isreacted) {
		this.reacted = isreacted;
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
