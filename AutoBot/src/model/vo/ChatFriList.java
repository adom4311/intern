package model.vo;

import java.io.Serializable;

public class ChatFriList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6212608913011564986L;
	private Long groupid;
	Object object[][];
	public ChatFriList(Long groupid, Object[][] object) {
		super();
		this.groupid = groupid;
		this.object = object;
	}
	public Long getGroupid() {
		return groupid;
	}
	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}
	public Object[][] getObject() {
		return object;
	}
	public void setObject(Object[][] object) {
		this.object = object;
	}
}
