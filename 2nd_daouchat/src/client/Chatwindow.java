package client;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class Chatwindow {

	public static final byte STX = 0x02; // 통신 시작
	public static final byte ETX = 0x03; // 통신 끝
	public static final byte SIGNUP = 0x01; // 회원가입
	public static final byte LOGIN = 0x02; // 로그인
	public static final byte MSG = 0x03; // 일반메시지
	public static final byte FRIFIND = 0x04; // 친구찾기
	public static final byte ADDFRI = 0x05; // 친구추가
	public static final byte FMSG = 0x06;//파일, 이미지 전송

	private String id;
	private Long groupid;
	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private Button buttonfile;
	private TextField textField;
	private TextArea textArea;
	
	private ClientBack clientback;
	private Socket filesocket;
	
	String[] oldchatcontent;


	public Chatwindow(String id, Long groupid, ClientBack clientback, Socket filesocket) {
		this.id = id;
		this.groupid = groupid;
		frame = new Frame(id);
		pannel = new Panel();
		buttonSend = new Button("Send");
		buttonfile = new Button("File send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		this.clientback = clientback;
		this.filesocket=filesocket;
		clientback.openChat(groupid);

//		new ChatClientReceiveThread(clientback.getSocket(),filesocket).start();
	}

	public int byteArrayToInt(byte bytes[]) {
		return ((((int) bytes[0] & 0xff) << 24) | (((int) bytes[1] & 0xff) << 16) | (((int) bytes[2] & 0xff) << 8)
				| (((int) bytes[3] & 0xff)));
	}

	// intToByte
	public byte[] intToByteArray(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte) (value >> 24);
		byteArray[1] = (byte) (value >> 16);
		byteArray[2] = (byte) (value >> 8);
		byteArray[3] = (byte) (value);
		return byteArray;
	}

	public void show() {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonfile.setForeground(Color.WHITE);
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

		// Textfield
		textField.setColumns(80);
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
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				PrintWriter pw;
				try {
					pw = new PrintWriter(new OutputStreamWriter(clientback.getSocket().getOutputStream(), StandardCharsets.UTF_8),
							true);

					System.exit(0);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
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
//		
//		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//		String filename = null;
//		OutputStream os;
//		FileInputStream in;
//		int returnValue = jfc.showOpenDialog(null);
//		
//		if(returnValue == JFileChooser.APPROVE_OPTION) {
//			File selectedFile = jfc.getSelectedFile();
//			filename = selectedFile.getAbsolutePath();
//		}
//		
//		try {
//			int bodylength = id.getBytes("UTF-8").length+filename.getBytes("UTF-8").length+groupid.getBytes("UTF-8").length+2;//UTF-8생각
//			byte sendData[] = new byte[6 + bodylength];// 전체 보낼 데이터
//			sendData[0]=STX;
//			sendData[1]=FMSG;
//			byte[] bodySize = intToByteArray(bodylength);
//			System.out.println("보낼 데이터 크기 : " + bodylength);
//			for (int i = 0; i < bodySize.length; i++) {
//				sendData[2 + i] = (byte) bodySize[i];
//			}
//			// body생성
//			byte body[] = new byte[bodylength];
//			body=(id+","+groupid+","+filename).getBytes("UTF-8");
//			System.arraycopy(body,0,sendData,6,body.length);
//			os = new DataOutputStream(clientback.getSocket().getOutputStream());
//			os.write(sendData);
//			os.flush();
//			//Thread.sleep(10);
//			new FileTransferThread(id,filename,filesocket).start();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	private class FileTransferThread extends Thread{
		String id;
		
		String filename;
		Socket filesocket;
		
		
		
		FileTransferThread(String id, String filename, Socket filesocket)
		{
			this.id=id;
			this.filename=filename;
			this.filesocket=filesocket;
			
		}
		
		//long to byte for file transfer
		public byte[] longToBytes(long x) {
		    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		    buffer.putLong(x);
		    return buffer.array();
		}

		public long bytesToLong(byte[] bytes) {
		    ByteBuffer buffer = ByteBuffer.wrap(bytes);
		   
		    return buffer.getLong();
		}
		
		public void run() {
			//다른 포트를 통해서 파일을 전송
			//db에 id와 filename저장할것.
			try {
				
				File file = new File(filename);
				long length = file.length();
				byte[] sizebyte = longToBytes(length);
				byte[] bytes = new byte[16 * 1024];
		        InputStream in = new FileInputStream(file);
		        OutputStream out = filesocket.getOutputStream();
		        out.write(sizebyte);
		        int count;
		        while ((count = in.read(bytes)) > 0) {
		            out.write(bytes, 0, count);
		            length-=count;
		            if(length<=0) break;
		        }
		       
	       
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void appendMSG(String msg) {
		textArea.append(msg);
	}
}
