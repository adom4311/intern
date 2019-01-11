package model.vo;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String userid;
	String password;
	public String getUserid() {
		return userid;
	}
	@Override
	public String toString() {
		return "User [userid=" + userid + ", password=" + password + ", getUserid()=" + getUserid() + ", getPassword()="
				+ getPassword() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
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
	public User(String userid, String password) {
		super();
		this.userid = userid;
		this.password = password;
	}
}
