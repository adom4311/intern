package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;

public class ClientHome extends JFrame {

	private JPanel contentPane;
	private ClientBack clientback;
	private JTextField textField;
	private JTable table;
	private JTable table2;
	JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public void home(ClientBack clientBack) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientHome frame = new ClientHome(clientBack);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
	
		table = new JTable(rowData, columnNames);
		scrollPane.setViewportView(table);
		
		JButton button_2 = new JButton("\uCC44\uD305\uBC29 \uAC1C\uC124");
		button_2.setBounds(12, 542, 331, 43);
		getContentPane().add(button_2);
		
	}

	public ClientHome(ClientBack clientBack) {
		this.clientback = clientBack;
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
		btnNewButton.setBounds(246, 9, 97, 23);
		getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("ģ�� ã��");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.setViewportView(table2);
			}
		});
		btnNewButton_1.setBounds(12, 383, 331, 43);
		getContentPane().add(btnNewButton_1);
		
		JButton button = new JButton("ģ�� ���");
		button.setBounds(12, 436, 331, 43);
		getContentPane().add(button);
		
		JButton button_1 = new JButton("ä�ù� ����");
		button_1.setBounds(12, 489, 331, 43);
		getContentPane().add(button_1);
		
		JButton button_2 = new JButton("ä�ù� ���");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.setViewportView(table);
			}
		});
		button_2.setBounds(12, 542, 331, 43);
		getContentPane().add(button_2);
		getContentPane().add(button_2);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 41, 329, 332);
		getContentPane().add(scrollPane);
		
		String columnNames[] =
			{ "��ȣ", "ä�ù��", "�ֱ� ����" };

			Object rowData[][] =
			{
			{ 1, "������", "������" },
			{ 2, "������", "�ҷ���ǰ" },
			{ 3, "ĭ����", "���ڰ��� ������" }};
		
		String columnNames2[] =
			{ "��ȣ", "���̵�", "���¸޼���" };

			Object rowData2[][] =
			{
			{ 1, "�̺���", "��������" },
			{ 2, "�̹���", "ī�����" },
			{ 3, "�ں���", "������ �׸� �޺�" },
			};
	
		table = new JTable(rowData, columnNames); // ä�ù� ��� ���̺�
		table2 = new JTable(rowData2, columnNames2); // ģ�� ã�� ���̺�
		scrollPane.setViewportView(table);
	}
}
