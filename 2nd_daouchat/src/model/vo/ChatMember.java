package model.vo;

import java.io.Serializable;

public class ChatMember implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7571503592797314312L;
	/**
	 * 
	 */
	private String userid;
	private Long groupid;
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
	public ChatMember(String userid, Long groupid) {
		super();
		this.userid = userid;
		this.groupid = groupid;
	}
	
}
