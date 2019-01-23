package model.vo;

import java.io.Serializable;

public class Roominfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -137630037872600740L;
	
	private String userid;
	private Long groupid;
	public Roominfo(String userid,Long groupid) {
		super();
		this.userid=userid;
		this.groupid=groupid;
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

}
