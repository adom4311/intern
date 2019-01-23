package model.vo;

import java.io.Serializable;
import java.util.ArrayList;

public class Galmessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4795020619735351368L;
	
	private Long groupid;
	ArrayList <String> images;
	public Galmessage(Long groupid, ArrayList<String> images) {
		super();
		this.groupid = groupid;
		this.images = images;
	}
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
	public ArrayList<String> getImages() {
		return images;
	}
	public void setImages(ArrayList<String> images) {
		this.images = images;
	}
	
	

}
