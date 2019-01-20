package model.vo;

import java.io.Serializable;

public class Filelist implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6329273606336176868L;
	private Long groupid;
	private Object [][] dirs;
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid=groupid;
	}
	public Object [][] getDirs(){
		return dirs;
	}
	public void setDirs(Object [][] dirs) {
		this.dirs=dirs;
	}
	
	public Filelist(Long groupid, Object [][] dirs) {
		super();
		this.groupid=groupid;
		this.dirs=dirs;
	}
}
