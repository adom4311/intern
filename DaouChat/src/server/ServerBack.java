package server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import dao.ServerDAO;

public class ServerBack {
	public static final byte STX = 0x02; // 통신 시작
	public static final byte ETX = 0x03; // 통신 끝
	public static final byte SIGNUP = 0x01; // 회원가입
	public static final byte LOGIN = 0x02; // 로그인
	public static final byte MSG = 0x03; // 일반메시지
	public static final byte FRIFIND = 0x04; // 친구찾기
	
	private ServerSocket serverSocket; // 서버소켓
	private Socket socket; // 받아올 소켓
	/* 현재 접속중인 사용자들의 정보 */
	private Map<String, DataOutputStream> currentClientMap = new HashMap<String, DataOutputStream>();
	private int non_login_increment = 0;
    ServerDAO sDao;
	
	
	
	public static void main(String[] args) {
		ServerBack serverBack = new ServerBack();
		serverBack.setting();
	}
	
	public  int byteArrayToInt(byte bytes[]) {
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
	}

	public void setting() {
		try {
			sDao = new ServerDAO();
			serverSocket = new ServerSocket(1993); // 서버 소켓 생성
			System.out.println("---서버 오픈---");
			while(true) {
				socket = serverSocket.accept(); // 클라이언트 소켓 저장
				System.out.println(socket.getInetAddress() + "에서 접속"); // IP
				
				Receiver receiver = new Receiver(socket);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/* 현재접속자 맵에 추가 */
	public void addClient(String id, DataOutputStream os) {
		if(id == null) {
			id = "GM" + non_login_increment++;
		}
		currentClientMap.put(id, os);
	}
	
	/* 서버는 연결된 클라이언트의 데이터 수신 대기 */
	class Receiver extends Thread{
		private DataInputStream is;
		private DataOutputStream os;
		public Receiver(Socket socket) {
			try {
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
				addClient(null,os);
				System.out.println("리시버 생성");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while(is != null) { // 수신된 데이터가 있을때
					byte[] reciveData = null;
					byte[] headerBuffer = new byte[6];
					is.read(headerBuffer);
					
					/* 회원가입 */
					if(headerBuffer[1] == SIGNUP) {
						System.out.println("회원가입");
						byte[] lengthChk = new byte[4]; // 데이터길이
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
						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
							buffer.write(reciveData,0,read);
							datalength -= read;
							if(datalength <= 0) { // 다 받으면 break
								break;
							}
						}
						System.out.println(buffer);
						String data[] = buffer.toString().split(",");
						
						buffer.flush();
						
						int chk = sDao.signUp(data[0],data[1]);
						os.writeInt(chk);
						
					}// 회원가입 END
					
					else if(headerBuffer[1] == LOGIN) {
						System.out.println("로그인");
						
						byte[] lengthChk = new byte[4]; // 데이터길이
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
						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
							buffer.write(reciveData,0,read);
							datalength -= read;
							if(datalength <= 0) { // 다 받으면 break
								break;
							}
						}
						System.out.println(buffer);
						String data[] = buffer.toString().split(",");
						
						buffer.flush();
						int chk = sDao.login(data[0],data[1]);
						os.writeInt(chk);
					}
				}
			}catch (SocketException e) {
				System.out.println("클라이언트 종료");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

