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

import javax.sql.rowset.spi.SyncResolver;

import dao.ServerDAO;

public class ServerBack {
	public static final byte STX = 0x02; // 통신 시작
	public static final byte ETX = 0x03; // 통신 끝
	public static final byte SIGNUP = 0x01; // 회원가입
	public static final byte LOGIN = 0x02; // 로그인
	public static final byte MSG = 0x03; // 일반메시지
	public static final byte FRIFIND = 0x04; // 친구찾기
	public static final byte ADDFRI = 0x05; // 친구추가
	public static final byte FRILIST = 0x06; // 친구목록
	public static final byte MESSAGE = 0x07; // 메시지만
	public static final byte CREATEGROUP = 0x08; // 그룹생성
	
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
	
	// intToByte
		public  byte[] intToByteArray(int value) {
			byte[] byteArray = new byte[4];
			byteArray[0] = (byte)(value >> 24);
			byteArray[1] = (byte)(value >> 16);
			byteArray[2] = (byte)(value >> 8);
			byteArray[3] = (byte)(value);
			return byteArray;
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
	
	public synchronized int increment() {
		return non_login_increment++;
		
	}
	
	/* 현재접속자 맵에 추가 */
	public void addClient(String id, DataOutputStream os) {
		currentClientMap.put(id, os);
	}
	
	/* 서버는 연결된 클라이언트의 데이터 수신 대기 */
	class Receiver extends Thread{
		private DataInputStream is;
		private DataOutputStream os;
		String connectId = "GM" + increment();
		public Receiver(Socket socket) {
			try {
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
				addClient(connectId,os);
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
						System.out.println(buffer.toString("UTF-8"));
						String data[] = buffer.toString("UTF-8").split(",");
						
						buffer.flush();
						System.out.println("data1의 크기는 : " +data[0].length());
						int chk = sDao.signUp(data[0],data[1]);
						os.writeInt(chk);
						
					}// 회원가입 END
					
					/* 로그인 */
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
						
						System.out.println(buffer.toString("UTF-8"));
						String data[] = buffer.toString("UTF-8").split(",");
						
						buffer.flush();
						int chk = sDao.login(data[0],data[1]);
						if(chk > 0) {
							connectId = data[0];
							currentClientMap.put(data[0], currentClientMap.remove(connectId)); // 임시아이디를 로그인 아이디로 변경
						}
						os.writeInt(chk);
					}// 로그인 END
					
					/* 친구 찾기(전체 목록) */
					else if(headerBuffer[1] == FRIFIND) {
						System.out.println(connectId + "가 친구목록 달래");
						Object rowData[][] = sDao.friFind(connectId); // 친구목록 int , String, String(4+20+20) 44
						int bodylength = rowData.length*44;
						
						byte sendData[] = new byte[6 + bodylength];
						
						sendData[0] = STX; // 시작?
						sendData[1] = FRIFIND; // 친구찾기
						byte[] bodySize = intToByteArray(bodylength);
						for (int i = 0; i < bodySize.length; i++) {
							sendData[2+i] = (byte)bodySize[i];
						} // 보낼 데이터 크기 // 여기선 totalUserCnt
						
						byte body[] = new byte[bodylength];
						int readcnt = 0;
						for (int i = 0; i < rowData.length; i++) {
							System.out.println("아이디 : " + (String)rowData[i][1]);
							byte friendId[] = String.valueOf(rowData[i][1]).getBytes("UTF-8");
							byte friendStatus[] = String.valueOf(rowData[i][2]).getBytes("UTF-8");
							int friendIdlength = friendId.length;
							int friendStatuslength = friendStatus.length;
							System.arraycopy(intToByteArray((int)rowData[i][0]), 0, body, readcnt, 4);
							readcnt += 4;
							System.arraycopy(friendId, 0, body, readcnt, friendIdlength);
							readcnt += friendIdlength;
							System.arraycopy(new byte[20 - friendIdlength], 0, body, readcnt, 20 - friendIdlength);
							readcnt += 20 - friendIdlength;
							System.arraycopy(friendStatus, 0, body, readcnt, friendStatuslength);
							readcnt += friendStatus.length;
							System.arraycopy(new byte[20 - friendStatuslength], 0, body, readcnt, 20 - friendStatuslength);
							readcnt += 20 - friendStatuslength;
							//총 44byte 씩 반복
						}
						
						System.arraycopy(body, 0, sendData, 6, body.length);
						
						os.write(sendData);
						
					}// 친구 찾기 END
					 
					/* ADDFRI */
					else if(headerBuffer[1] == ADDFRI) {
						System.out.println(connectId + "가 친구추가 해달래");
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
						
						System.out.println(buffer.toString("UTF-8"));
						String data = buffer.toString("UTF-8");
						
						buffer.flush();
						int chk = sDao.addfri(connectId,data);
						
						os.writeInt(chk);
						
					}/* ADDFRI END */
					
					/* 친구 목록 */
					else if(headerBuffer[1] == FRILIST) {
						System.out.println(connectId + "가 친구목록 달래");
						Object rowData[][] = sDao.friList(connectId); // 친구목록 int , String, String(4+20+20) 44
						int bodylength = rowData.length*44;
						
						byte sendData[] = new byte[6 + bodylength];
						
						sendData[0] = STX; // 시작?
						sendData[1] = FRIFIND; // 친구찾기
						byte[] bodySize = intToByteArray(bodylength);
						for (int i = 0; i < bodySize.length; i++) {
							sendData[2+i] = (byte)bodySize[i];
						} // 보낼 데이터 크기 // 여기선 totalUserCnt
						
						byte body[] = new byte[bodylength];
						int readcnt = 0;
						for (int i = 0; i < rowData.length; i++) {
							System.out.println("아이디 : " + (String)rowData[i][1]);
							byte friendId[] = String.valueOf(rowData[i][1]).getBytes("UTF-8");
							byte friendStatus[] = String.valueOf(rowData[i][2]).getBytes("UTF-8");
							int friendIdlength = friendId.length;
							int friendStatuslength = friendStatus.length;
							System.arraycopy(intToByteArray((int)rowData[i][0]), 0, body, readcnt, 4);
							readcnt += 4;
							System.arraycopy(friendId, 0, body, readcnt, friendIdlength);
							readcnt += friendIdlength;
							System.arraycopy(new byte[20 - friendIdlength], 0, body, readcnt, 20 - friendIdlength);
							readcnt += 20 - friendIdlength;
							System.arraycopy(friendStatus, 0, body, readcnt, friendStatuslength);
							readcnt += friendStatus.length;
							System.arraycopy(new byte[20 - friendStatuslength], 0, body, readcnt, 20 - friendStatuslength);
							readcnt += 20 - friendStatuslength;
							//총 44byte 씩 반복
						}
						
						System.arraycopy(body, 0, sendData, 6, body.length);
						
						os.write(sendData);
						
					}// 친구 목록 END
					
//					/* 메시지 전송 */
//					else if(headerBuffer[1] == MESSAGE) {
//						System.out.println("메시지 전송");
//						
//						byte[] lengthChk = new byte[4]; // 데이터길이
//						lengthChk[0] = headerBuffer[2];
//						lengthChk[1] = headerBuffer[3];
//						lengthChk[2] = headerBuffer[4];
//						lengthChk[3] = headerBuffer[5];
//						int datalength = byteArrayToInt(lengthChk);
//						System.out.println("데이터길이 : " + datalength);
//						
//						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//						int read;
//						reciveData = new byte[datalength]; 
//						
//						// 파일 받을때까지 계속
//						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//							buffer.write(reciveData,0,read);
//							datalength -= read;
//							if(datalength <= 0) { // 다 받으면 break
//								break;
//							}
//						}
//						
//						System.out.println(buffer.toString("UTF-8"));
//						String data = buffer.toString("UTF-8");
//						
//						buffer.flush();
//						int chk = sDao.message(data); // 메시지 DB저장
//						// 메시지를 전송!
//						if(chk > 0) {
//							connectId = data[0];
//						}
//						os.writeInt(chk);
//					}// 메시지 END
					
					/* 채티방 개설 */
					else if(headerBuffer[1] == CREATEGROUP) {
						System.out.println("채팅방 개설");
						
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
						
						System.out.println(buffer.toString("UTF-8"));
						String data = buffer.toString("UTF-8");
						
						buffer.flush();
						int chk = sDao.createGroup(connectId,data);
						if(chk > 0) {
							System.out.println("채팅방 개설 성공");
						}else {
							System.out.println("채팅방 개설 실패");
						}
						os.writeInt(chk);
					}// 채팅방개설 END
					
				}
			}catch (SocketException e) {
				System.out.println("클라이언트 종료");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

