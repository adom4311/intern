package client;

import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class ClientChat extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JTextField textField;
	private ClientBack clientback;
	private JTextArea textArea;
	private String groupid;


	/**
	 * Launch the application.
	 */
	public void start(ClientBack clientback, String groupid) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientChat frame = new ClientChat(clientback,groupid);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientChat() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 370, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(67, 585, 275, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("+");
		btnNewButton.setBounds(12, 584, 55, 23);
		contentPane.add(btnNewButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(12, 10, 330, 571);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
	}
	
	public ClientChat(ClientBack clientback, String groupid) {
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("press" + e.getKeyChar());
				super.keyPressed(e);
			}

			@Override
			public void keyTyped(KeyEvent e) {
				System.out.println(e.getKeyChar());
			}
			
		});

		
		System.out.println("chat실행2");
		this.clientback = clientback;
		this.groupid = groupid;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 100, 370, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(67, 585, 275, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.addActionListener(this);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyChar());
				if(e.getKeyChar() == '\033') // escape 문자
					setVisible(false); // 객체 소멸처리 해야할듯 보이지만 않음
			}
		
		});
		
		JButton btnNewButton = new JButton("+");
		btnNewButton.setBounds(12, 584, 55, 23);
		contentPane.add(btnNewButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(12, 10, 330, 571);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
	}

	@Override
	public void actionPerformed(ActionEvent e) {  
		if(e.getSource().equals(textField)) { // 텍스트필드 enter
			textArea.append(textField.getText() + '\n');
			String msg = textField.getText() + '\n';
			clientback.sendMessage(msg);
			textField.setText("");
		}
	}
	
	 public void appendMsg(String msg) {
		 	textArea.append(msg);
	    }
}
