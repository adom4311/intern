package model.vo;

import java.io.Serializable;

public class Header implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8372821077359666554L;
	/**
	 * 
	 */
	private int menu;
	private int bodylength;
	public Header(int menu, int bodylength) {
		super();
		this.menu = menu;
		this.bodylength = bodylength;
	}
	public int getMenu() {
		return menu;
	}
	public void setMenu(int menu) {
		this.menu = menu;
	}
	public int getDatalength() {
		return bodylength;
	}
	public void setDatalength(int bodylength) {
		this.bodylength = bodylength;
	}
}
