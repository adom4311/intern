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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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
import java.nio.charset.StandardCharsets;

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
	private String pw;
	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private Button buttonfile;
	private TextField textField;
	private TextArea textArea;

	private Socket socket;

	public Chatwindow(String id, String pw, Socket socket) {
		this.id = id;
		this.pw = pw;
		frame = new Frame(id);
		pannel = new Panel();
		buttonSend = new Button("Send");
		buttonfile = new Button("File send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		this.socket = socket;

//		new ChatClientReceiveThread(socket).start();
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
					pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
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

	// 쓰레드를 만들어서 대화를 보내기
	private void sendMessage() {
		DataOutputStream os;

		try {
			os = new DataOutputStream(socket.getOutputStream());
			String msg = textField.getText();
			int bodylength = id.getBytes("UTF-8").length + pw.getBytes("UTF-8").length + msg.getBytes("UTF-8").length
					+ 2;// ,포함
			byte sendData[] = new byte[6 + bodylength];// 전체 보낼 데이터
			// 헤더생성(flag와 body의 크기)
			sendData[0] = STX;
			sendData[1] = MSG;
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("보낼 데이터 크기 : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2 + i] = (byte) bodySize[i];
			}
			// body생성
			byte body[] = new byte[bodylength];
			body = (id + "," + pw + "," + msg).getBytes("UTF-8");
			System.arraycopy(body, 0, sendData, 6, body.length);

			os.write(sendData);
			os.flush();

			textField.setText("");
			textField.requestFocus();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// file transfer
	private void sendfile() {
		
		String filename = "C:\\Users\\user\\Desktop\\file\\asdf.jpg";
		OutputStream os;
		FileInputStream in;
		int bodylength = id.getBytes().length;
		byte sendData[] = new byte[6 + bodylength];// 전체 보낼 데이터
		sendData[0]=STX;
		sendData[1]=FMSG;
		byte[] bodySize = intToByteArray(bodylength);
		System.out.println("보낼 데이터 크기 : " + bodylength);
		for (int i = 0; i < bodySize.length; i++) {
			sendData[2 + i] = (byte) bodySize[i];
		}
		// body생성
		byte body[] = new byte[bodylength];
		body=id.getBytes();
		System.arraycopy(body,0,sendData,6,body.length);
		try {
			os = new DataOutputStream(socket.getOutputStream());
			os.write(sendData);
			os.flush();
			//Thread.sleep(10);
//			new FileTransferThread(id,filename).start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// file transfer
	private class ChatClientReceiveThread extends Thread {
		Socket socket = null;

		ChatClientReceiveThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			DataInputStream is;
			try {
				is = new DataInputStream(socket.getInputStream());
				while (is != null) {
					byte[] reciveData = null;
					byte[] headerBuffer = new byte[6];
					is.read(headerBuffer);
					if (headerBuffer[1] == MSG) {
						System.out.println("메세지");
						byte[] lengthChk = new byte[4];
						lengthChk[0] = headerBuffer[2];
						lengthChk[1] = headerBuffer[3];
						lengthChk[2] = headerBuffer[4];
						lengthChk[3] = headerBuffer[5];
						int datalength = byteArrayToInt(lengthChk);
						System.out.println("데이터길이 : " + datalength);

						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						int read;
						reciveData = new byte[datalength];

						// 파일 받을때까지 계속
						while ((read = is.read(reciveData, 0, reciveData.length)) != -1) {
							buffer.write(reciveData, 0, read);
							datalength -= read;
							if (datalength <= 0) { // 다 받으면 break
								break;
							}
						}
						System.out.println(buffer.toString("UTF-8"));
						String data[] = buffer.toString("UTF-8").split(",");
						buffer.flush();
						System.out.println("data1의 크기는 : " + data[0].length());
						textArea.append(data[0] + ":" + data[2]);
						textArea.append("\n");
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class FileTransferThread extends Thread{
		String id;
		String filename;
		String host = "127.0.0.1";
		public static final int fileport = 1994;
		
		
		FileTransferThread(String id, String filename)
		{
			this.id=id;
			this.filename=filename;
		}
		
		public void run() {
			//다른 포트를 통해서 파일을 전송
			//db에 id와 filename저장할것.
			try {
				Socket socket = new Socket(host,fileport);
				File file = new File(filename);
				long length = file.length();
				byte[] bytes = new byte[16 * 1024];
		        InputStream in = new FileInputStream(file);
		        OutputStream out = socket.getOutputStream();

		        int count;
		        while ((count = in.read(bytes)) > 0) {
		            out.write(bytes, 0, count);
		        }

		        out.close();
		        in.close();
		        socket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
