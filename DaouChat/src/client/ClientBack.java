package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientBack {
	public static final byte STX = 0x02; // 통신 시작
	public static final byte ETX = 0x03; // 통신 끝
	public static final byte SIGNUP = 0x01; // 회원가입
	public static final byte LOGIN = 0x02; // 로그인
	public static final byte MSG = 0x03; // 일반메시지
	public static final byte FRIFIND = 0x04; // 친구찾기
	
	private Socket socket;
	private ClientGUI gui;
	private ClientHome home;
	private DataInputStream is;
	private DataOutputStream os;
	private String SERVER_ADDR = "127.0.0.1";
	private int PORT = 1993;
	
	public void setGui(ClientGUI clientGUI) {
		this.gui = clientGUI;
	}
	
	public ClientBack() {
		connect();
	}
	
	// intToByte
	public  byte[] intToByteArray(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte)(value >> 24);
		byteArray[1] = (byte)(value >> 16);
		byteArray[2] = (byte)(value >> 8);
		byteArray[3] = (byte)(value);
		return byteArray;
	}

	public void connect() {
		try {
			socket = new Socket(SERVER_ADDR,PORT);
			System.out.println("서버와 연결됨");
			
			is = new DataInputStream(socket.getInputStream());
			os = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void signUp(String id, String pw) {
		try {
			int bodylength = id.length() + pw.length() + 1; // 아이디 + 패스워드 바이트
			byte sendData[] = new byte[6+bodylength]; // 전체 보낼 데이터
			
			sendData[0] = STX; // 시작?
			sendData[1] = SIGNUP; // 회원가입
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("보낼 데이터의 크기 : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // 보낼 데이터 크기
			byte body[] = new byte[bodylength];
			body = (id + "," + pw).getBytes("UTF-8");
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("보낼 데이터 : " + new String(body) + sendData.length);

			os.write(sendData);
			os.flush();
			
			while(is!=null) {
				int chk = is.readInt();
				if(chk > 0) {
					gui.Alert("회원가입 성공! 로그인해주세요.");
					gui.signUpInvi();
					break;
				}else {
					gui.Alert("회원가입 실패. 아이디 중복");
					gui.signUpInvi();
					break;

				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void login(String id, String pw) {
		try {
			int bodylength = id.length() + pw.length() + 1; // 아이디 + 패스워드 바이트
			byte sendData[] = new byte[6+bodylength]; // 전체 보낼 데이터
			
			sendData[0] = STX; // 시작?
			sendData[1] = LOGIN; // 로그인
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("보낼 데이터의 크기 : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // 보낼 데이터 크기
			byte body[] = new byte[bodylength];
			body = (id + "," + pw).getBytes("UTF-8");
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("보낼 데이터 : " + new String(body) + sendData.length);

			os.write(sendData);
			os.flush();
			
			while(is!=null) {
				int chk = is.readInt();
				if(chk > 0) {
					gui.Alert("로그인 성공!");
					gui.loginInvi();
					gui.setVisible(false);
					home = new ClientHome();
					home.home(this);
					break;
				}else {
					gui.Alert("로그인 실패. 아이디 또는 비밀번호 오류.");
					gui.loginInvi();
					break;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
