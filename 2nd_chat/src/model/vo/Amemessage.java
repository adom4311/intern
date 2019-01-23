package model.vo;

import java.io.Serializable;
import java.util.ArrayList;

public class Amemessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5744013520007606278L;
	
	private Long groupid;
	ArrayList<String> mem_avail;
	public Amemessage(Long groupid, ArrayList<String> mem_avail) {
		super();
		this.groupid = groupid;
		this.mem_avail = mem_avail;
	}
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
	public ArrayList<String> getMem_avail() {
		return mem_avail;
	}
	public void setMem_avail(ArrayList<String> mem_avail) {
		this.mem_avail = mem_avail;
	}
	
	
}
