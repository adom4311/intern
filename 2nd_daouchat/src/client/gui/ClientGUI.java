package client.gui;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import client.ClientBack;

public class ClientGUI extends JFrame {

	private JPanel contentPane;
	private ClientBack clientback;
	JDialog signUpdialog; 
	JDialog logindialog; 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI();
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
	public ClientGUI() {
		clientback = new ClientBack();
		clientback.setGui(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(900, 300, 262, 258);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel programName = new JLabel("다중 채팅프로그램");
		programName.setHorizontalAlignment(SwingConstants.CENTER);
		programName.setBounds(30, 10, 180, 40);
		contentPane.add(programName);
		
		JButton signUpBtn = new JButton("회원가입");
		signUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signUpDialog();
			}
		});
		signUpBtn.setBounds(30, 90, 180, 50);
		contentPane.add(signUpBtn);
		
		JButton loginBtn = new JButton("로그인");
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginDialog();
			}
		});
		loginBtn.setBounds(30, 159, 180, 50);
		contentPane.add(loginBtn);
	}
	
	private void signUpDialog() 
	{
		JPanel contentPane2;
		JTextField id_tf;
		JTextField pw_tf;
		
	    signUpdialog = new JDialog(this, Dialog.ModalityType.APPLICATION_MODAL);

	    signUpdialog.setBounds(700, 300, 265, 127);
	    
		contentPane2 = new JPanel();
		contentPane2.setBorder(new EmptyBorder(5, 5, 5, 5));
		signUpdialog.setContentPane(contentPane2);
	    contentPane2.setLayout(null);
		
		JLabel loginLabel = new JLabel("아이디");
		loginLabel.setBounds(12, 10, 57, 15);
		contentPane2.add(loginLabel);
		
		JLabel pwLabel = new JLabel("패스워드");
		pwLabel.setBounds(12, 35, 57, 15);
		contentPane2.add(pwLabel);
		
		id_tf = new JTextField();
		id_tf.setBounds(81, 7, 150, 21);
		contentPane2.add(id_tf);
		id_tf.setColumns(10);
		id_tf.setDocument(new JTextFieldLimit(20));
		
		pw_tf = new JTextField("");
		pw_tf.setBounds(81, 32, 150, 21);
		contentPane2.add(pw_tf);
		pw_tf.setColumns(10);
		pw_tf.setDocument(new JTextFieldLimit(20));
		
		JButton okBtn = new JButton("확인");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(id_tf.getText().equals("") || pw_tf.getText().equals("")) {
					Alert("회원정보를 입력해주세요.");
					return;
				}
				clientback.signUp(id_tf.getText(),pw_tf.getText());
			}
		});
		
		JButton cancelBtn = new JButton("취소");
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("취소");
				signUpdialog.setVisible(false);
			}
		});
		
		cancelBtn.setBounds(163, 58, 70, 23);
		contentPane2.add(cancelBtn);
		
		okBtn.setBounds(91, 58, 70, 23);
		contentPane2.add(okBtn);
		
		signUpdialog.setVisible(true);
	}
	
	private void loginDialog() {
		JPanel contentPane2;
		JTextField id_tf;
		JTextField pw_tf;
		
		logindialog = new JDialog(this, Dialog.ModalityType.APPLICATION_MODAL);

		logindialog.setBounds(700, 300, 265, 127);
	    
		contentPane2 = new JPanel();
		contentPane2.setBorder(new EmptyBorder(5, 5, 5, 5));
		logindialog.setContentPane(contentPane2);
	    contentPane2.setLayout(null);
		
		JLabel loginLabel = new JLabel("아이디");
		loginLabel.setBounds(12, 10, 57, 15);
		contentPane2.add(loginLabel);
		
		JLabel pwLabel = new JLabel("패스워드");
		pwLabel.setBounds(12, 35, 57, 15);
		contentPane2.add(pwLabel);
		
		id_tf = new JTextField();
		id_tf.setBounds(81, 7, 150, 21);
		contentPane2.add(id_tf);
		id_tf.setColumns(10);
		id_tf.setDocument(new JTextFieldLimit(20));
		
		pw_tf = new JTextField("");
		pw_tf.setBounds(81, 32, 150, 21);
		contentPane2.add(pw_tf);
		pw_tf.setColumns(10);
		pw_tf.setDocument(new JTextFieldLimit(20));
		
		JButton okBtn = new JButton("확인");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(id_tf.getText().equals("") || pw_tf.getText().equals("")) {
					Alert("회원정보를 입력해주세요.");
					return;
				}
				System.out.println("확인id" + id_tf.getText());
				clientback.login(id_tf.getText(),pw_tf.getText());
			}
		});
		
		JButton cancelBtn = new JButton("취소");
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("취소");
				logindialog.setVisible(false);
			}
		});
		
		cancelBtn.setBounds(163, 58, 70, 23);
		contentPane2.add(cancelBtn);
		
		okBtn.setBounds(91, 58, 70, 23);
		contentPane2.add(okBtn);
		
		logindialog.setVisible(true);
	}
	
	public void signUpInvi() {
		signUpdialog.setVisible(false);
	}
	
	public void loginInvi() {
		logindialog.setVisible(false);
	}
	
	public void Alert(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
	
	public class JTextFieldLimit extends PlainDocument{
		private int limit;
		public JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// TODO Auto-generated method stub
			if (str == null)
				return;
			if(getLength() + str.length() <= limit)
				super.insertString(offs, str, a);
		}
		
		
	}
	
}
