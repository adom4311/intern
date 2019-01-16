package model.vo;

import java.io.Serializable;
import java.util.Date;

public class Filemessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8979468741855378082L;
	/**
	 * 
	 */
	private String senduserid;
	private Long groupid;
	private String file_dir;
	private Date sendtime;
	public String getSenduserid() {
		return senduserid;
	}
	public void setSenduserid(String senduserid) {
		this.senduserid = senduserid;
	}
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
	public String getfile_dir() {
		return file_dir;
	}
	public void setfile_dir(String file_dir) {
		this.file_dir = file_dir;
	}
	public Date getSendtime() {
		return sendtime;
	}
	public void setSendtime(Date sendtime) {
		this.sendtime = sendtime;
	}
	public Filemessage(String senduserid, Long groupid, String file_dir, Date sendtime) {
		super();
		this.senduserid = senduserid;
		this.groupid = groupid;
		this.file_dir = file_dir;
		this.sendtime = sendtime;
	}
}
