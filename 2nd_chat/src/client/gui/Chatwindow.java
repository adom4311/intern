package client.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import client.ClientBack;

public class Chatwindow {

	public static final byte STX = 0x02; // ��� ����
	public static final byte ETX = 0x03; // ��� ��
	public static final byte SIGNUP = 0x01; // ȸ������
	public static final byte LOGIN = 0x02; // �α���
	public static final byte MSG = 0x03; // �Ϲݸ޽���
	public static final byte FRIFIND = 0x04; // ģ��ã��
	public static final byte ADDFRI = 0x05; // ģ���߰�
	public static final byte FMSG = 0x06;//����, �̹��� ����

	private String id;
	private Long groupid;
	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private Button buttonfile;
	private Button buttonFriList;
	private JTextField textField;
	private TextArea textArea;
	
	private Button btnfilerec;
	private ClientBack clientback;
	private ClientFileGUI clientfilegui;
	private ClientFriListGUI clientfrilistgui;
	
	String[] oldchatcontent;


	public Chatwindow(String id, Long groupid, ClientBack clientback) {
		this.id = id;
		this.groupid = groupid;
		frame = new Frame(id);
		pannel = new Panel();
		buttonSend = new Button("Send");
		buttonfile = new Button("File send");
		btnfilerec = new Button("check received file");
		buttonFriList = new Button("FriList");
		textField = new JTextField();
		textArea = new TextArea(30, 80);
		this.clientback = clientback;
	}

	public ClientFileGUI getCFG() {
		return clientfilegui;
	}
	
	public ClientFriListGUI getClientfrilistgui() {
		return clientfrilistgui;
	}
	
	public Frame getFrame() {
		return frame;
	}

	public void show() {
		// Button
		//int �� ������ jTextArea ��ü�� �ؽ�Ʈ�� �� ���̸� ����

		int pos = textArea.getText().length();
		//caret �������� ���� ���������� ����
		textArea.setCaretPosition(pos);
		//����
		textArea.requestFocus();
		//����2
		//JScrollPane�� �ٸ� �� �ϴ����� ����
//		jScrollPane.getVerticalScrollBar().setValue(jScrollPane.getVerticalScrollBar().getMaximum());
		
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonfile.setForeground(Color.WHITE);
		btnfilerec.setForeground(Color.BLUE);
		buttonFriList.setBackground(Color.BLUE);
		buttonFriList.setForeground(Color.white);
		buttonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				sendMessage();
			}
		});
		buttonfile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendfile();
				 
				 
			}

		});
		
		btnfilerec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				clientfilegui = new ClientFileGUI(id,groupid,clientback);
				System.out.println("new gui start!");
				clientback.fileList(groupid);
				
				
			}
			
		});
		buttonFriList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( clientfrilistgui == null) {
					clientfrilistgui = new ClientFriListGUI(id,groupid,clientback);
					clientback.chatfriList(groupid);
				}
				clientfrilistgui.getFrame().setVisible(true);

			}
		});

		// Textfield
		textField.setColumns(40);
		textField.setDocument(new JTextFieldLimit(333));
		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});
		
		

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		pannel.add(buttonfile);
		pannel.add(btnfilerec);
		pannel.add(buttonFriList);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
			}
		});
		frame.setVisible(true);
		frame.pack();
	}

	private void sendMessage() {
		String msg = textField.getText();
		if(!msg.equals(""))
			clientback.sendMessage(msg,groupid);
		textField.setText("");
		textField.requestFocus();
	}

	// file transfer
	private void sendfile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		String filename = null;
		OutputStream os;
		FileInputStream in;
		int returnValue = jfc.showOpenDialog(null);
		
		if(returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			filename = selectedFile.getAbsolutePath();
		}
		if(filename==null) {
			System.out.println("������ �ٽ� ã���ּ���");
			return ;
		}
		String[] filetoken = filename.split("\\\\");
		if(filetoken[filetoken.length-1].length()>=150||filename.length()>=199) {
			System.out.println("���� ���丮 ��� �Ǵ� ���� �̸��� �ʹ� ��ϴ�.");
			return ;
		}
		clientback.sendFilemessage(filename, groupid);
	}

	public void appendMSG(String msg) {
		textArea.append(msg);
	}
	
	public void readchatFile() {
		clientback.readchatFile(groupid);
	}
	
	 public void finalize() {
	    try {
	    	System.out.println("��ü�� ������ ����... ");
			clientback.getChatFileMap().get(groupid).close();
			clientback.getChatFileMap().remove(groupid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
