package client.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import client.ClientBack;

public class ClientFriListGUI{
	JFrame frame;
	JTable table;
	String id;
	Long groupid;
	ClientBack clientback;
	Object [][] dirs;
	DefaultTableModel m;
	JScrollPane sc;
	DefaultTableModel model;
	String a[] = {"FRIEND LIST"};
	
	public JFrame getFrame() {
		return frame;
	}
	public ClientFriListGUI(String id, Long groupid, ClientBack clientback) {
		this.id=id;
		this.groupid=groupid;
		this.clientback=clientback;
		//this.dirs=dirs;
	}
	
	public void show() {

		frame=new JFrame("FRIEND LIST");

		frame.setBounds(0,0,300,300);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
//				frame.setVisible(false);
				frame.dispose();
			}
		});
	}
	
	public void insertable(Object[][] dir) {
		model = new DefaultTableModel(dir, a) {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};
		
		table = new JTable(model);
		
		sc = new JScrollPane(table);
		frame.add(sc);
		
		frame.setVisible(true);
	}
	public JTable getTable() {
		return table;
	}
	
	public void setTable(JTable t) {
		table=t;
		return;
		
		
	}
}

