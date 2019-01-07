package client;

import java.io.ByteArrayOutputStream;
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
	public static final byte ADDFRI = 0x05; // 친구추가
	public static final byte FRILIST = 0x06; // 친구목록
	
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
	
	public  int byteArrayToInt(byte bytes[]) {
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
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
			int bodylength = id.getBytes("UTF-8").length + pw.getBytes("UTF-8").length + 1; // 아이디 + 패스워드 바이트
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
			
			System.out.println("body length" + body.length);
			
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
			int bodylength = id.getBytes("UTF-8").length + pw.getBytes("UTF-8").length + 1; // 아이디 + 패스워드 바이트
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
					home.setuserid(id);
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

	public Object[][] findFriend() {
		Object rowData[][];
		try {
			byte sendData[] = new byte[6]; // 전체 보낼 데이터
			
			sendData[0] = STX; // 시작?
			sendData[1] = FRIFIND; // 친구찾기
			byte[] bodySize = intToByteArray(0);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // 보낼 데이터 크기
			
			os.write(sendData);
			os.flush();
			
			while(is != null) {
				byte[] reciveData = null; 
				byte[] headerBuffer = new byte[6];
				is.read(headerBuffer);
				
				/* 친구 찾기 목록 */
				if(headerBuffer[1] == FRIFIND) {
					System.out.println("친구 찾기");
					byte[] lengthChk = new byte[4]; // 데이터길이
					lengthChk[0] = headerBuffer[2];
					lengthChk[1] = headerBuffer[3];
					lengthChk[2] = headerBuffer[4];
					lengthChk[3] = headerBuffer[5];
					int datalength = byteArrayToInt(lengthChk);
					System.out.println("데이터길이 : " + datalength);
					
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int read;
					reciveData = new byte[datalength]; // 읽는 단위?
					
					// 파일 받을때까지 계속
//					while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//						System.out.println("read : " + read);
//						buffer.write(reciveData,0,read);
//						datalength -= read;
//						if(datalength <= 0) { // 다 받으면 break
//							break;
//						}
//					}
										
					int start = 0;
					while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
						System.out.println("start : " + start);
						System.out.println("read : " + read);
						
						buffer.write(reciveData,0,read); // buffer에 reciveData내용 저장 ..뭔가 뒤바뀜
						start += read;
						datalength -= read;
						System.out.println(datalength);
						if(datalength <= 0) { // 다 받으면 break
							break;
						}
					}
					
					
					reciveData = buffer.toByteArray(); // 버퍼(byte...stream)에 저장된 내용을 바이트 배열에!
					buffer.flush(); // 버퍼(byte...stream) 비우기
					
					System.out.println("친구 목록 받기 성공");
					System.out.println("총 갯수 : " + reciveData.length/44);
					System.out.println("총개수*44 : " + reciveData.length);
					byte num[] = new byte[4];
					byte friendId[] = new byte[20];
					byte friendStatus[] = new byte[20];
					rowData = new Object[reciveData.length/44][3];
					
					
					
					int cnt = 0;
					for (int i = 0; i < reciveData.length/44; i++) {
						System.arraycopy(reciveData, cnt, num, 0, 4);
						cnt += 4;
						System.arraycopy(reciveData, cnt, friendId, 0, 20);
						cnt += 20;
						System.arraycopy(reciveData, cnt, friendStatus, 0, 20);
						cnt += 20;
//						System.out.println("번호 : " + byteArrayToInt(num));
//						System.out.println("아이디 : " + new String(friendId,"UTF-8").trim());
//						System.out.println("상메 : " + new String(friendStatus,"UTF-8").trim());
						rowData[i][0] = byteArrayToInt(num);
						rowData[i][1] = new String(friendId,"UTF-8").trim();
						rowData[i][2] = new String(friendStatus,"UTF-8").trim();
					}
					
					return rowData;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void addFriend(String friendId) {
		try {
			int bodylength = friendId.getBytes("UTF-8").length; 
			byte sendData[] = new byte[6+bodylength]; // 전체 보낼 데이터
			
			sendData[0] = STX; // 시작?
			sendData[1] = ADDFRI; // 친구 추가
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("보낼 데이터의 크기 : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // 보낼 데이터 크기
			byte body[] = new byte[bodylength];
			body = friendId.getBytes("UTF-8");
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("보낼 데이터 : " + new String(body) + sendData.length);

			os.write(sendData);
			os.flush();
			
			while(is!=null) {
				int chk = is.readInt();
				if(chk > 0) {
					gui.Alert("친구추가 성공!");
					home.getFrame().fn_addfri(this);
					break;
				}else {
					gui.Alert("로그인 실패. 아이디 또는 비밀번호 오류.");
					break;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object[][] friList() {
		Object rowData[][];
		try {
			byte sendData[] = new byte[6]; // 전체 보낼 데이터
			
			sendData[0] = STX; // 시작?
			sendData[1] = FRILIST; // 친구목록
			byte[] bodySize = intToByteArray(0);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // 보낼 데이터 크기
			
			os.write(sendData);
			os.flush();
			
			while(is != null) {
				byte[] reciveData = null; 
				byte[] headerBuffer = new byte[6];
				is.read(headerBuffer);
				
				/* 친구 찾기 목록 */
				if(headerBuffer[1] == FRIFIND) {
					System.out.println("친구 찾기");
					byte[] lengthChk = new byte[4]; // 데이터길이
					lengthChk[0] = headerBuffer[2];
					lengthChk[1] = headerBuffer[3];
					lengthChk[2] = headerBuffer[4];
					lengthChk[3] = headerBuffer[5];
					int datalength = byteArrayToInt(lengthChk);
					System.out.println("데이터길이 : " + datalength);
					
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int read;
					reciveData = new byte[datalength]; // 읽는 단위?
					
					// 파일 받을때까지 계속
//					while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//						System.out.println("read : " + read);
//						buffer.write(reciveData,0,read);
//						datalength -= read;
//						if(datalength <= 0) { // 다 받으면 break
//							break;
//						}
//					}
										
					int start = 0;
					while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
						System.out.println("start : " + start);
						System.out.println("read : " + read);
						
						buffer.write(reciveData,0,read); // buffer에 reciveData내용 저장 ..뭔가 뒤바뀜
						start += read;
						datalength -= read;
						System.out.println(datalength);
						if(datalength <= 0) { // 다 받으면 break
							break;
						}
					}
					
					
					reciveData = buffer.toByteArray(); // 버퍼(byte...stream)에 저장된 내용을 바이트 배열에!
					buffer.flush(); // 버퍼(byte...stream) 비우기
					
					System.out.println("친구 목록 받기 성공");
					System.out.println("총 갯수 : " + reciveData.length/44);
					System.out.println("총개수*44 : " + reciveData.length);
					byte num[] = new byte[4];
					byte friendId[] = new byte[20];
					byte friendStatus[] = new byte[20];
					rowData = new Object[reciveData.length/44][3];
					
					
					
					int cnt = 0;
					for (int i = 0; i < reciveData.length/44; i++) {
						System.arraycopy(reciveData, cnt, num, 0, 4);
						cnt += 4;
						System.arraycopy(reciveData, cnt, friendId, 0, 20);
						cnt += 20;
						System.arraycopy(reciveData, cnt, friendStatus, 0, 20);
						cnt += 20;
//						System.out.println("번호 : " + byteArrayToInt(num));
//						System.out.println("아이디 : " + new String(friendId,"UTF-8").trim());
//						System.out.println("상메 : " + new String(friendStatus,"UTF-8").trim());
						rowData[i][0] = byteArrayToInt(num);
						rowData[i][1] = new String(friendId,"UTF-8").trim();
						rowData[i][2] = new String(friendStatus,"UTF-8").trim();
					}
					
					return rowData;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
