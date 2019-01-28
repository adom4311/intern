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

public class ClientFileGUI{
	JFrame frame;
	JTable table;
	String id;
	Long groupid;
	ClientBack clientback;
	Object [][] dirs;
	DefaultTableModel m;
	JScrollPane sc;
	DefaultTableModel model;
	String []a= {"file"};
	FileMouseListener listener;
	
	public JFrame getFrame() {
		return frame;
	}
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
	public ClientFileGUI(String id, Long groupid, ClientBack clientback) {
		this.id=id;
		this.groupid=groupid;
		this.clientback=clientback;
		//this.dirs=dirs;
		
//		frame=new JFrame("filelist");
//		table = new JTable(model);
		
		
	}
	
	public void show() {

		frame=new JFrame("filelist");
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});
		table = new JTable(model);
		sc = new JScrollPane(table);
		frame.add(sc);

//		m = (DefaultTableModel)table.getModel();
		frame.setBounds(0,0,300,300);
		frame.setVisible(true);
	}
	
	public void insertable(Object[][] dir) {
		table.removeMouseListener(listener);
		
		model = new DefaultTableModel(dir, a) {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};
		
		table.setModel(model);
		listener =new FileMouseListener(1);
		table.addMouseListener(listener);

		
//		
//		for(int i=0;i<dir.length;i++) {
////			m.insertRow(i, dir[i]);
//			m.addRow(dir[i]);
//		}
		
//		table.updateUI();
		frame.setVisible(true);
//		table.updateUI();
	}
	public JTable getTable() {
		return table;
	}
	
	public void setTable(JTable t) {
		table=t;
		return;
		
		
	}
	
	private class FileMouseListener extends MouseAdapter{
		int menu;
		public FileMouseListener(int menu) {
			super();
			this.menu=menu;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			//마우스 클릭되면 이벤트 여기다
			if(e.getClickCount()==2) {
				System.out.println(menu);
				System.out.println(table);
				System.out.println("셀렉"+ table.getSelectedRow());
				System.out.println(table.getValueAt(table.getSelectedRow(), 0));
				clientback.filedownreq(groupid, (String)table.getValueAt(table.getSelectedRow(), 0),false);
				//sendrequest give me the file..
			}
		}
	}

}

