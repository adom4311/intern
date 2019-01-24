package client.gui;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Galarygui {
	
	JFrame frame;
	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	JTable table;
	ArrayList<String> images;
	DefaultTableModel model;
	DefaultTableModel m;
	JScrollPane sc;
	
	public Galarygui(ArrayList<String> images) {
		this.images = images;
		String [] a = {"image"};
		int size = images.size();
		ImageIcon icon[]= new ImageIcon[size];
		Object im[][]= new Object[size][1];
		int i=0;
		for(String image : images) {
			
			icon[i]= new ImageIcon(image);
			im[i][0]=icon[i++];
		}
		
		model = new DefaultTableModel(im,a) {
			public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
			/*
			 * public boolean isCellEditable(int rowIndex, int mColIndex) { return false; }
			 */
		};
		
		frame = new JFrame("galary");
		table = new JTable(model);
			
		table.setRowHeight(100);
		table.getColumnModel().getColumn(0).setMaxWidth(400); 
        table.getColumnModel().getColumn(0).setMinWidth(400); 
        table.getColumnModel().getColumn(0).setWidth(400);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		sc = new JScrollPane(table);
		frame.add(sc);
		
		frame.setLocationByPlatform(true);
		frame.pack();
		frame.setVisible(true);
	}
}
