package vo;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private char select;
	private String userid;
	private String password;
	
	public User(char select, String userid, String password) {
		this.select = select;
		this.userid = userid;
		this.password = password;
	}
	
	public char getSelect() {
		return select;
	}
	public void setSelect(char select) {
		this.select = select;
	}
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
