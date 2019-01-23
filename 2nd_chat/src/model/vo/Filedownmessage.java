package model.vo;

import java.io.Serializable;

public class Filedownmessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8469043561500763361L;
	private Long groupid;
	private String file_dir;
	private boolean isImg;
	
	public boolean getisImg() {
		return isImg;
	}
	public void setImg(boolean isImg) {
		this.isImg = isImg;
	}
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
	
	public Filedownmessage(Long groupid, String file_dir,boolean isImg) {
		this.groupid = groupid;
		this.file_dir=file_dir;
		this.isImg=isImg;
	}
}
