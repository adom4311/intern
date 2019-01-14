package model.vo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Chat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long chatid;
	private String userid;
	private Long groupid;
	private String content;
	private Timestamp sendtime;
	private int count;
	
	public Long getChatid() {
		return chatid;
	}
	public void setChatid(Long chatid) {
		this.chatid = chatid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Timestamp getSendtime() {
		return sendtime;
	}
	public void setSendtime(Timestamp sendtime) {
		this.sendtime = sendtime;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Chat(Long chatid, String userid, Long groupid, String content, Timestamp sendtime, int count) {
		super();
		this.chatid = chatid;
		this.userid = userid;
		this.groupid = groupid;
		this.content = content;
		this.sendtime = sendtime;
		this.count = count;
	}
}
