package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ClientHome extends JFrame {
	private String userid; // �������� ���̵�
	private JPanel contentPane;
	private ClientBack clientback;
	private JTextField textField;
	private JTable chatGrouptable;
	private JTable findFritable; // ģ�� ã�� ���̺�
	private JTable friListtable; // ģ�� ��� ���̺�
	ClientHome frame;
	JScrollPane scrollPane;
	private int menuInt; // �˻���ư �޴� ������
	String findFricolumnNames[] = { "��ȣ", "���̵�", "���¸޼���" };
	String chatGroupcolumnNames[] = { "��ȣ", "ä�ù��", "�ֱ� ����" };

	/**
	 * Launch the application.
	 */
	public void home(ClientBack clientBack,String userid) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new ClientHome(clientBack,userid);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ClientHome getFrame() {
		return frame;
	}
	
	public void setuserid(String userid) {
		this.userid = userid;
	}

	public ClientHome() {
		getContentPane().setLayout(null);
	
		textField = new JTextField();
		textField.setBounds(12, 10, 227, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		JButton btnNewButton = new JButton("ä�ù� ����");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(246, 9, 97, 23);
		getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("\uCE5C\uAD6C \uCC3E\uAE30");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton_1.setBounds(12, 383, 331, 43);
		getContentPane().add(btnNewButton_1);
		
		JButton button = new JButton("\uCE5C\uAD6C \uBAA9\uB85D");
		button.setBounds(12, 436, 331, 43);
		getContentPane().add(button);
		
		JButton button_1 = new JButton("\uCC44\uD305\uBC29 \uAC1C\uC124");
		button_1.setBounds(12, 489, 331, 43);
		getContentPane().add(button_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 41, 329, 332);
		getContentPane().add(scrollPane);
		
		String columnNames[] =
			{ "��ȣ", "ä�ù��", "�ֱ� ����" };

			Object rowData[][] =
			{
			{ 1, "������", "������" },
			{ 2, "������", "�ҷ���ǰ" },
			{ 3, "ĭ����", "���ڰ��� ������" }
			};
	
			chatGrouptable = new JTable(rowData, columnNames);
		scrollPane.setViewportView(chatGrouptable);
		
		JButton button_2 = new JButton("\uCC44\uD305\uBC29 \uAC1C\uC124");
		button_2.setBounds(12, 542, 331, 43);
		getContentPane().add(button_2);
		
	}

	public ClientHome(ClientBack clientBack, String userid) {
		menuInt = 1;
		this.clientback = clientBack;
		setTitle(userid);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 370, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(12, 10, 227, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("�˻�");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switch(menuInt) {
				case 1: System.out.println("�˻���� ����");
				break;
				case 2: break;
				case 3: break;
				case 4: break;
				}
			}
		});
		btnNewButton.setBounds(246, 9, 97, 23);
		getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("ģ�� ã��");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menuInt = 1;
				fn_addfri(clientback);
			}
		});
		btnNewButton_1.setBounds(12, 383, 331, 43);
		getContentPane().add(btnNewButton_1);
		
		JButton button = new JButton("ģ�� ���");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menuInt = 2;
				fn_friList(clientback);
			}
		});
		button.setBounds(12, 436, 331, 43);
		getContentPane().add(button);
		
		JButton button_1 = new JButton("ä�ù� ����");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menuInt = 3;
				
			}
		});
		button_1.setBounds(12, 489, 331, 43);
		getContentPane().add(button_1);
		
		JButton button_2 = new JButton("ä�ù� ���");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menuInt = 4;
				
//				scrollPane.setViewportView(chatGrouptable);
			}
		}); 
		button_2.setBounds(12, 542, 331, 43);
		getContentPane().add(button_2);
		getContentPane().add(button_2);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 41, 329, 332);
		getContentPane().add(scrollPane);
		
		

			Object rowData[][] =
			{
			{ 1, "������", "������" },
			{ 2, "������", "�ҷ���ǰ" },
			{ 3, "ĭ����", "���ڰ��� ������" }};
		
		

		chatGrouptable = new JTable(rowData, chatGroupcolumnNames); // ä�ù� ��� ���̺�
		
		scrollPane.setViewportView(chatGrouptable);
		
		
	}
	
	/* ģ��ã���� ��ü */
	public void fn_addfri(ClientBack clientback) {
		menuInt = 1;
		clientback.findFriend();
	}
	
	public void fn_addfriView(Object[][] rowData) {
		// ���� ���� �Ұ� ���� //
        DefaultTableModel mod = new DefaultTableModel(rowData, findFricolumnNames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
		findFritable = new JTable(mod); // ģ�� ã�� ���̺�
		findFritable.addMouseListener(new MyMouseListener(1));
		scrollPane.setViewportView(findFritable);
	}
	
	/* ģ�� ��� */
	public void fn_friList(ClientBack clientback) {
		menuInt = 2;
		clientback.friList();
	}
	
	public void fn_friListView(Object[][] rowData) {
		// ���� ���� �Ұ� ���� //
        DefaultTableModel mod = new DefaultTableModel(rowData, findFricolumnNames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        friListtable = new JTable(mod); // ģ�� ��� ���̺�
        friListtable.addMouseListener(new MyMouseListener(2));
		scrollPane.setViewportView(friListtable);
	}
	
	/* ģ�� ã�� ���̺� Ŭ�� �̺�Ʈ */
	private class MyMouseListener extends MouseAdapter{
		int menu;
		public MyMouseListener(int menu) {
			super();
			this.menu = menu;
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println(menu);
			if(e.getButton() == 1) {
				if(e.getClickCount() == 2) {
					if(menu == 1) { // ģ�� ã�� ���̺� Ŭ�� �̺�Ʈ
						System.out.println(findFritable.getValueAt(findFritable.getSelectedRow(),1));
						System.out.println("����Ŭ��");
	
						addFriendAlert((String)findFritable.getValueAt(findFritable.getSelectedRow(),1));
					}else if (menu == 2) { // ģ�� ��� ���̺� Ŭ�� �̺�Ʈ
						String friendid = friListtable.getValueAt(friListtable.getSelectedRow(),1).toString(); 
						System.out.println(friendid);
						System.out.println("ģ�� ��� ����Ŭ��"); 
//						
//						//�׷� ä�ù� ����
						String[] str = {friendid};
						clientback.createGroup(str);
						
						//addFriendAlert((String)friListtable.getValueAt(friListtable.getSelectedRow(),1));
					}
				}
			}
		}
		
	}
	
	public void addFriendAlert(String friendId) {

		int dialogButton = JOptionPane.showConfirmDialog(null, friendId + " ģ�� �߰��Ͻðڽ��ϱ�?","ģ�� �߰�",JOptionPane.YES_NO_OPTION);
		
		if(dialogButton == JOptionPane.YES_OPTION) {
			clientback.addFriend(friendId);
		}else {
			System.out.println("����");
		}

	}
}
