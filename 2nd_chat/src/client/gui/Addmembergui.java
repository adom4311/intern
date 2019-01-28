package client.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import client.ClientBack;

public class Addmembergui {
	
	JFrame frame;
	JTable table;
	ClientBack clientback;
	DefaultTableModel model;
	DefaultTableModel m;
	JScrollPane sc;
	Long groupid;
	String [] a = {"목록"};
	AddmemMouseListner listener;
	
	public Addmembergui(Long groupid, ClientBack clientback) {
		this.groupid = groupid;
		this.clientback = clientback;
		
		
		frame = new JFrame("대화상대 추가");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});
		
		table = new JTable(model);
		
		sc = new JScrollPane(table);
		frame.add(sc);
		frame.setBounds(0,0,300,300);
		frame.setVisible(true);
		
	}
	
	public void insertbl(ArrayList<String> member_avail) {
		table.removeMouseListener(listener);
		int length = member_avail.size();
		Object row[][] = new Object[length][1];
		int i=0;
		for(String member : member_avail) {
			row[i++][0]=member;
			//System.out.println(member);
		}

		
		model = new DefaultTableModel(row, a) {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};
		table.setModel(model);
		listener =new AddmemMouseListner();
		table.addMouseListener(listener);
		frame.setVisible(true);
//		table.updateUI();
	}
	
	
	private class AddmemMouseListner extends MouseAdapter{
		
		public AddmemMouseListner()
		{
			super();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()==2) {
				System.out.println(table.getValueAt(table.getSelectedRow(),0));
				clientback.memberfunc(groupid,(String)table.getValueAt(table.getSelectedRow(),0));
				frame.setVisible(false);
			}
		}
		
		
	}
}
