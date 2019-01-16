package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;

public class CreateGroupRoomGUI extends JFrame {
    JTable findFritable;
    DefaultTableModel mod;

	private JPanel contentPane;
	HashMap <String,String> map = new HashMap<String,String>();

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					CreateGroupRoomGUI frame = new CreateGroupRoomGUI();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public CreateGroupRoomGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 208, 312);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 39, 167, 188);
		contentPane.add(scrollPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		scrollPane.setViewportView(tabbedPane);
		
		
		Object rowData[][] ={};
		String findFricolumnNames[] = { "아이디"};
		// 내용 수정 불가 시작 //
        mod = new DefaultTableModel(rowData, findFricolumnNames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
//        JTable findFritable;
		findFritable = new JTable(mod); // 친구 찾기 테이블
		findFritable.addMouseListener(new MyMouseListener());
		scrollPane.setViewportView(findFritable);
		
		JLabel label = new JLabel("\uADF8\uB8F9\uCC44\uD305\uBC29 \uAC1C\uC124");
		label.setBounds(50, 10, 106, 15);
		contentPane.add(label);
		
		JButton btnNewR = new JButton("개설");
		btnNewR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("취소");
				btnNewR.setVisible(false);
				
			}
		});
		btnNewR.setBounds(44, 237, 97, 23);
		contentPane.add(btnNewR);
		
	}
	public CreateGroupRoomGUI(ClientHome frame, ClientBack clientback) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 208, 312);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 39, 167, 188);
		contentPane.add(scrollPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		scrollPane.setViewportView(tabbedPane);
		
		
		Object rowData[][] ={};
		String findFricolumnNames[] = { "아이디"};
		// 내용 수정 불가 시작 //
        mod = new DefaultTableModel(rowData, findFricolumnNames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
//        JTable findFritable;
		findFritable = new JTable(mod); // 친구 찾기 테이블
		findFritable.addMouseListener(new MyMouseListener());
		scrollPane.setViewportView(findFritable);
		
		JLabel label = new JLabel("\uADF8\uB8F9\uCC44\uD305\uBC29 \uAC1C\uC124");
		label.setBounds(50, 10, 106, 15);
		contentPane.add(label);
		
		JButton btnNewR = new JButton("개설");
		btnNewR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("개설");
				
				String[] friends = new String[map.size()];
				int i = 0;
				for(String key : map.keySet()) {
					friends[i++] = key;
				}
				
				clientback.createGroupRoom(friends);
				
				frame.cgrgui.setVisible(false);
				frame.cgrgui = null;
			}
		});
		btnNewR.setBounds(44, 237, 97, 23);
		contentPane.add(btnNewR);
		
	}
	/* 친구 찾기 테이블 클릭 이벤트 */
	private class MyMouseListener extends MouseAdapter{
		public MyMouseListener() {
			super();
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == 1) {
				if(e.getClickCount() == 2) {
					String str = (String)findFritable.getValueAt(findFritable.getSelectedRow(),0);
					mod.removeRow(findFritable.getSelectedRow());
					map.remove(str);
				}
			}
		}
		
	}
	public void addrow(Object[] object) {
//		mod.addRow(object);
		if(map.get(object[0].toString()) == null) {
			map.put(object[0].toString(), object[0].toString());
			mod.addRow(object);
		}
	}
}
