package client.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import client.ClientBack;

public class Addmembergui {
	
	JFrame frame;
	JTable table;
	ArrayList<String> member_avail;
	ClientBack clientback;
	DefaultTableModel model;
	DefaultTableModel m;
	JScrollPane sc;
	Long groupid;
	
	public Addmembergui(ArrayList<String> member_avail,Long groupid, ClientBack clientback) {
		this.member_avail = member_avail;
		this.groupid = groupid;
		this.clientback = clientback;
		String [] a = {"목록"};
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
		frame = new JFrame("대화상대 추가");
		table = new JTable(model);
		table.addMouseListener(new AddmemMouseListner());
		
		sc = new JScrollPane(table);
		frame.add(sc);
		frame.setBounds(0,0,300,300);
		frame.setVisible(true);
		
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
