package model.vo;

import java.io.Serializable;

public class Filedownmessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8469043561500763361L;
	private Long groupid;
	private String file_dir;
	
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid() {
		this.groupid=groupid;
	}
	public String getFile_dir() {
		return file_dir;
	}
	public void setFile_dir() {
		this.file_dir=file_dir;
	}
	
	public Filedownmessage(Long groupid, String file_dir) {
		this.groupid = groupid;
		this.file_dir=file_dir;
	}
}
