package model.vo;

import java.io.Serializable;
import java.util.List;

public class ChatcontentList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2296897224149804822L;
	private Long groupid;
	private String groupName;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public ChatcontentList(Long groupid, String groupName, List<Chat> chatcontent) {
		super();
		this.groupid = groupid;
		this.groupName = groupName;
		this.chatcontent = chatcontent;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private List<Chat> chatcontent;
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
	public List<Chat> getChatcontent() {
		return chatcontent;
	}
	public void setChatcontent(List<Chat> chatcontent) {
		this.chatcontent = chatcontent;
	}
	public ChatcontentList(Long groupid, List<Chat> chatcontent) {
		super();
		this.groupid = groupid;
		this.chatcontent = chatcontent;
	}
}
