package model.vo;

import java.io.Serializable;

public class GroupInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5858182734435642295L;
	private Long groupid;
	private String groupname;
	public GroupInfo(Long groupid, String groupname) {
		super();
		this.groupid = groupid;
		this.groupname = groupname;
	}
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
}
