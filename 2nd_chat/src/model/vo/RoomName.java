package model.vo;

import java.io.Serializable;

public class RoomName implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2275127896970190936L;
	private int result;
	public RoomName(int result, String userid, Long groupid, String groupName) {
		super();
		this.result = result;
		this.userid = userid;
		this.groupid = groupid;
		this.groupName = groupName;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String userid;
	private Long groupid;
	private String groupName;
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
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public RoomName(String userid, Long groupid, String groupName) {
		super();
		this.userid = userid;
		this.groupid = groupid;
		this.groupName = groupName;
	}
	
}
