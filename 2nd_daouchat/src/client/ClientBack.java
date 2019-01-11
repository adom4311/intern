package client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import model.vo.Data;
import model.vo.Header;
import model.vo.User;

public class ClientBack {
	public static final byte STX = 0x02; // 통신 시작
	public static final byte ETX = 0x03; // 통신 끝
	public static final int SIGNUP = 0x01; // 회원가입
	public static final byte LOGIN = 0x02; // 로그인
	public static final byte MSG = 0x03; // 일반메시지
	public static final byte FRIFIND = 0x04; // 친구찾기
	public static final byte ADDFRI = 0x05; // 친구추가
	public static final byte FMSG = 0x06;// 파일, 이미지 전송
	public static final byte FRILIST = 0x09; // 친구목록
	public static final byte MESSAGE = 0x07; // 메시지만
	public static final byte CREATEGROUP = 0x08; // 그룹생성
	public static final byte OPENCHAT = 0x10; // 그룹생성
	public static final byte ROOM = 0x11;//채팅방 목록
	
	private String id;
	private String pw;
	private Socket socket;
	private Socket filesocket;
	private ClientGUI gui;
	private ClientHome home;
	private Chatwindow chatwindow;
	private Map<String,Chatwindow> chatMap = new HashMap<String, Chatwindow>();
	private DataInputStream is;
	private DataOutputStream os;
	private DataInputStream fis;
	private DataOutputStream fos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String SERVER_ADDR = "127.0.0.1";
	private int PORT = 1993;
	private int FILE_PORT = 1994;
	
	
	public void setId(String i) {
		this.id=i;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setPw(String p) {
		this.pw=p;
	}
	
	public String getPw() {
		return this.pw;
	}
	
	public DataInputStream getDataInputstream() {
		return this.is;
	}
	
	public DataOutputStream getOutputStream() {
		return this.os;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public Socket getfilesocket(){
		return this.filesocket;
	}
	
	
	
	
	public void setGui(ClientGUI clientGUI) {
		this.gui = clientGUI;
	}
	
	public ClientBack() {
		connect();
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
	
	
	// 받기만 하는 쓰레드
	class ClientReceiver extends Thread{
		private ClientBack clientback;
		public ClientReceiver(Socket socket, Socket filesocket, ClientBack clientback) {
			try {
				this.clientback = clientback; 
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
				fis = new DataInputStream(filesocket.getInputStream());
				fos = new DataOutputStream(filesocket.getOutputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				
				System.out.println("클라이언트 리시버 생성");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
//			try {
//				while(is != null) {
//					byte[] reciveData = null;
//					byte[] headerBuffer = new byte[6];
//					is.read(headerBuffer);
//					
//					/* 회원가입 */
//					if(headerBuffer[1] == SIGNUP) {
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
//						int chk = byteArrayToInt(reciveData);
//						if(chk > 0) {
//							gui.Alert("회원가입 성공! 로그인해주세요.");
//							gui.signUpInvi();
//						}else {
//							gui.Alert("회원가입 실패. 아이디 중복");
//							gui.signUpInvi();
//						}
//					}// 회원가입 END
//					
//					/* 로그인 */
//					else if (headerBuffer[1] == LOGIN) {
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
//						int chk = byteArrayToInt(reciveData);
//						if(chk > 0) {
//							gui.Alert("로그인 성공!");
//							gui.loginInvi();
//							gui.setVisible(false);
//							home = new ClientHome();
//							home.home(clientback,id);
//						}else {
//							gui.Alert("로그인 실패. 아이디 또는 비밀번호 오류.");
//							gui.loginInvi();
//						}
//					}// 로그인 END
//					
//					/* 친구 찾기 (전체목록) */
//					else if(headerBuffer[1] == FRIFIND) {
//						Object rowData[][]; // 친구목록 int , String, String(4+20+20) 44
//						System.out.println("친구 찾기");
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
//						reciveData = new byte[datalength]; // 읽는 단위?
//						
//						int start = 0;
//						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//							System.out.println("start : " + start);
//							System.out.println("read : " + read);
//							
//							buffer.write(reciveData,0,read); // buffer에 reciveData내용 저장 ..뭔가 뒤바뀜
//							start += read;
//							datalength -= read;
//							System.out.println(datalength);
//							if(datalength <= 0) { // 다 받으면 break
//								break;
//							}
//						}
//						reciveData = buffer.toByteArray(); // 버퍼(byte...stream)에 저장된 내용을 바이트 배열에!
//						buffer.flush(); // 버퍼(byte...stream) 비우기
//						
//						System.out.println("친구 목록(전체) 받기 성공");
//						System.out.println("총 갯수 : " + reciveData.length/44);
//						System.out.println("총개수*44 : " + reciveData.length);
//						byte num[] = new byte[4];
//						byte friendId[] = new byte[20];
//						byte friendStatus[] = new byte[20];
//						rowData = new Object[reciveData.length/44][3];
//						
//						int cnt = 0;
//						for (int i = 0; i < reciveData.length/44; i++) {
//							System.arraycopy(reciveData, cnt, num, 0, 4);
//							cnt += 4;
//							System.arraycopy(reciveData, cnt, friendId, 0, 20);
//							cnt += 20;
//							System.arraycopy(reciveData, cnt, friendStatus, 0, 20);
//							cnt += 20;
////									System.out.println("번호 : " + byteArrayToInt(num));
////									System.out.println("아이디 : " + new String(friendId,"UTF-8").trim());
////									System.out.println("상메 : " + new String(friendStatus,"UTF-8").trim());
//							rowData[i][0] = byteArrayToInt(num);
//							rowData[i][1] = new String(friendId,"UTF-8").trim();
//							rowData[i][2] = new String(friendStatus,"UTF-8").trim();
//						}
//						home.getFrame().fn_addfriView(rowData);
//					}// 친구찾기 (전체) END
//					
//					/* 친구추가 */
//					else if(headerBuffer[1] == ADDFRI) {
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
//						int chk = byteArrayToInt(reciveData);
//						if(chk > 0) {
//							gui.Alert("친구추가 성공!");
//							home.getFrame().fn_addfri(clientback);
//						}else {
//							gui.Alert("로그인 실패. 아이디 또는 비밀번호 오류.");
//						}
//					}// 친구 추가 END
//					
//					/* 친구 목록 */
//					else if(headerBuffer[1] == FRILIST) {
//						Object rowData[][];
//						System.out.println("친구 찾기");
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
//						reciveData = new byte[datalength]; // 읽는 단위?
//						
//						int start = 0;
//						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//							System.out.println("start : " + start);
//							System.out.println("read : " + read);
//							
//							buffer.write(reciveData,0,read); // buffer에 reciveData내용 저장 ..뭔가 뒤바뀜
//							start += read;
//							datalength -= read;
//							System.out.println(datalength);
//							if(datalength <= 0) { // 다 받으면 break
//								break;
//							}
//						}
//						reciveData = buffer.toByteArray(); // 버퍼(byte...stream)에 저장된 내용을 바이트 배열에!
//						buffer.flush(); // 버퍼(byte...stream) 비우기
//						
//						System.out.println("친구 목록 받기 성공");
//						System.out.println("총 갯수 : " + reciveData.length/44);
//						System.out.println("총개수*44 : " + reciveData.length);
//						byte num[] = new byte[4];
//						byte friendId[] = new byte[20];
//						byte friendStatus[] = new byte[20];
//						rowData = new Object[reciveData.length/44][3];
//						
//						int cnt = 0;
//						for (int i = 0; i < reciveData.length/44; i++) {
//							System.arraycopy(reciveData, cnt, num, 0, 4);
//							cnt += 4;
//							System.arraycopy(reciveData, cnt, friendId, 0, 20);
//							cnt += 20;
//							System.arraycopy(reciveData, cnt, friendStatus, 0, 20);
//							cnt += 20;
//							rowData[i][0] = byteArrayToInt(num);
//							rowData[i][1] = new String(friendId,"UTF-8").trim();
//							rowData[i][2] = new String(friendStatus,"UTF-8").trim();
//						}
//						home.getFrame().fn_friListView(rowData);
//					} // 친구 목록 END
//					
//					/* 채팅방 개설 */
//					else if (headerBuffer[1] == CREATEGROUP) {
//						System.out.println("--채팅방 개설 시작--");
//						byte[] lengthChk = new byte[4]; // 데이터길이
//						lengthChk[0] = headerBuffer[2];
//						lengthChk[1] = headerBuffer[3];
//						lengthChk[2] = headerBuffer[4];
//						lengthChk[3] = headerBuffer[5];
//						int datalength = byteArrayToInt(lengthChk);
//						System.out.println("채팅방 개설시 받은 데이터길이 : " + datalength);
//						
//						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//						int read;
//						reciveData = new byte[datalength]; 
//						int temp = datalength;
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
//						
//						reciveData = buffer.toByteArray();
//						
//						byte chkByte[] = new byte[4];
//						byte groupidByte[] = new byte[80];
//						byte chatcontent[] = new byte[temp - 84];
//						
//						System.arraycopy(reciveData, 0, chkByte, 0, chkByte.length); // chk 짜르고
//						System.arraycopy(reciveData, chkByte.length, groupidByte, 0, 80); //groupid 짜르고
//						System.arraycopy(reciveData, 84, chatcontent, 0, temp - 84); //groupid 짜르고
//						
//						
//						System.out.println("채팅방개설쪽  chk : " + byteArrayToInt(chkByte));
//						System.out.println("채팅방개설쪽  chk : " + new String(groupidByte,"UTF-8"));
//						
//						int chknum = byteArrayToInt(chkByte);
//						String groupid = new String(groupidByte,"UTF-8").trim();
//						
//						String[] strcontent = new String(chatcontent,"UTF-8").split("&");
//						for (int j = 0; j < strcontent.length; j++) {
//							System.out.println(strcontent[j]);
//						}
//						
//						
//						if(chknum > 0) {
//							if(chatMap.get(groupid) == null) {
//								System.out.println("채티방 개설");
//								chatwindow = new Chatwindow(id, groupid, clientback, filesocket);
//								chatMap.put(groupid, chatwindow);
//								chatwindow.show();
//							}
//						}else if(chknum == 0) {
//							if(chatMap.get(groupid) == null) {
//								System.out.println("있는 채팅방");
//								chatwindow = new Chatwindow(id, groupid, clientback, filesocket);
//								chatMap.put(groupid, chatwindow);
//								chatwindow.show();
//								System.out.println("채팅들의 크기는" + strcontent.length);
//							}
//						}
//						else {
//							gui.Alert("채티방 개설 실패");
//						}
//					}// 채팅방 개설 END
//					
//					/* ROOM  목록*/
//					else if(headerBuffer[1]==ROOM) {
//						Object rowData[][];
//						System.out.println("채팅방 목록");
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
//						reciveData = new byte[datalength]; // 읽는 단위?
//						
//						int start = 0;
//						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//							System.out.println("start : " + start);
//							System.out.println("read : " + read);
//							
//							buffer.write(reciveData,0,read); // buffer에 reciveData내용 저장 ..뭔가 뒤바뀜
//							start += read;
//							datalength -= read;
//							System.out.println(datalength);
//							if(datalength <= 0) { // 다 받으면 break
//								break;
//							}
//						}
//						reciveData = buffer.toByteArray(); // 버퍼(byte...stream)에 저장된 내용을 바이트 배열에!
//						buffer.flush(); // 버퍼(byte...stream) 비우기
//						
//						System.out.println("친구 목록 받기 성공");
//						System.out.println("총 갯수 : " + reciveData.length/84);
//						System.out.println("총개수*84 : " + reciveData.length);
//						byte num[] = new byte[4];
//						byte roomname[] = new byte[80];
//						rowData = new Object[reciveData.length/84][3];
//						
//						int cnt = 0;
//						for (int i = 0; i < reciveData.length/84; i++) {
//							System.arraycopy(reciveData, cnt, num, 0, 4);
//							cnt += 4;
//							System.arraycopy(reciveData, cnt, roomname, 0, 80);
//							cnt += 80;
//							rowData[i][0] = byteArrayToInt(num);
//							rowData[i][1] = new String(roomname,"UTF-8").trim();
//					
//						}
//						home.getFrame().fn_roomListView(rowData);
//					}
//					
//					/* message */
//					else if (headerBuffer[1] == MSG) {
//						System.out.println("메세지");
//						byte[] lengthChk = new byte[4];
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
//						while ((read = is.read(reciveData, 0, reciveData.length)) != -1) {
//							buffer.write(reciveData, 0, read);
//							datalength -= read;
//							if (datalength <= 0) { // 다 받으면 break
//								break;
//							}
//						}
//						System.out.println(buffer.toString("UTF-8"));
//						String data[] = buffer.toString("UTF-8").split(",");
//						buffer.flush();
//						System.out.println("data1의 크기는 : " + data[0].length());
//						String userid = data[0];
//						String groupid = data[1];
//						String msg = data[2];
//
//						if(chatMap.get(groupid) == null) {
//							chatwindow = new Chatwindow(id, groupid, clientback, filesocket );
//							chatMap.put(groupid, chatwindow);
//							chatwindow.show();
//						}else {
//							chatMap.get(groupid).appendMSG(data[0] + ":" + data[2] + "\n");
//						}
//					}
//					/* 파일 받기 */
//					else if(headerBuffer[1]==FMSG) {
//						System.out.println("파일");
//						byte[] lengthChk = new byte[4];
//						lengthChk[0]=headerBuffer[2];
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
//						while ((read = is.read(reciveData, 0, reciveData.length)) != -1) {
//							buffer.write(reciveData, 0, read);
//							datalength -= read;
//							if (datalength <= 0) { // 다 받으면 break
//								break;
//							}
//						}
//						System.out.println(buffer.toString("UTF-8"));
//						String data[] = buffer.toString("UTF-8").split(",");
//						buffer.flush();
//						System.out.println(data[1]);
//						String recdir[]=data[1].split("\\\\");
//						InputStream in = filesocket.getInputStream();
//						OutputStream out = new FileOutputStream("C:\\Users\\user\\Desktop\\file\\client\\"+recdir[recdir.length-1]);//data[1];
//						byte[] bytes = new byte[16*1024];
//						byte[] sizebyte = new byte[8];
//						int count;
//						int files=in.read(sizebyte);
//						System.out.println("클라이언트가 받는 파일 크기는 : "+files);
//						long length = bytesToLong(sizebyte);
//						while ((count = in.read(bytes)) > 0) {
//				            out.write(bytes, 0, count);
//				            length-=count;
//							System.out.println(length);
//							if(length<=0) break;
//				        }
//						
//						out.close();
//					}// 파일받기 END
//					
//					/* OPENCHAT */
//					if(headerBuffer[1] == OPENCHAT) {
//						System.out.println("OPENCHAT 시작");
//						byte[] lengthChk = new byte[4]; // 데이터길이
//						lengthChk[0] = headerBuffer[2];
//						lengthChk[1] = headerBuffer[3];
//						lengthChk[2] = headerBuffer[4];
//						lengthChk[3] = headerBuffer[5];
//						int datalength = byteArrayToInt(lengthChk);
//						System.out.println("openchat시 데이터길이 : " + datalength);
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
//						reciveData = buffer.toByteArray();
//						if(reciveData.length > 0) {
//							String[] strcontent = new String(reciveData,"UTF-8").split("&");
//							for (int j = 0; j < strcontent.length; j++) {
//								System.out.println("대화내용 " + strcontent[j]);
//							}
//							String data[] = strcontent[0].split(",");
//							oldcontentView(chatMap.get(data[1]), strcontent);
//						}
//						System.out.println("OPENCAT END");
//					}// OPENCHAT END
//				}
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}

		private void oldcontentView(Chatwindow chatwindow, String[] strcontent) {
			for (int i = 0; i < strcontent.length; i++) {
				String[] data = strcontent[i].split(",");
				if( data.length > 1)
					chatwindow.appendMSG(data[0] + ":" + data[2] + "\n");
			}
		}
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
			filesocket = new Socket(SERVER_ADDR,FILE_PORT);

			System.out.println("서버와 연결됨");
			
			ClientReceiver receiver = new ClientReceiver(socket, filesocket, this);
			receiver.start();
			
//			is = new DataInputStream(socket.getInputStream());
//			os = new DataOutputStream(socket.getOutputStream());
//			fis = new DataInputStream(filesocket.getInputStream());
////			fos = new DataOutputStream(filesocket.getOutputStream());
//			ois = new ObjectInputStream(socket.getInputStream());
//			oos = new ObjectOutputStream(socket.getOutputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void signUp(String id, String pw) {
		try {
			int bodylength = id.getBytes("UTF-8").length + pw.getBytes("UTF-8").length + 1; // 아이디 + 패스워드 바이트
//			
//			sendData[0] = STX; // 시작?
//			sendData[1] = SIGNUP; // 회원가입
//			byte[] bodySize = intToByteArray(bodylength);
//			System.out.println("보낼 데이터의 크기 : " + bodylength);
//			for (int i = 0; i < bodySize.length; i++) {
//				sendData[2+i] = (byte)bodySize[i];
//			} // 보낼 데이터 크기
//			byte body[] = new byte[bodylength];
//			body = (id + "," + pw).getBytes("UTF-8");
//			
//			System.out.println("body length" + body.length);
//			
//			System.arraycopy(body, 0, sendData, 6, body.length);
//			
//			System.out.println("보낼 데이터 : " + new String(body,"UTF-8"));
			Header header = new Header(SIGNUP,bodylength);
			User user = new User(id,pw);
			Data data = new Data(header,user);

//			oos.writeObject(data);
//			oos.flush();
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
			
			System.out.println("보낼 데이터 : " + new String(body,"UTF-8"));

			this.id = id;
			os.write(sendData);
			os.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void findFriend() {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			
			System.out.println("보낼 데이터 : " + new String(body,"UTF-8"));

			os.write(sendData);
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void friList() {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void roomList() {
		try {
			byte sendData[] = new byte[6];
			sendData[0]=STX;
			sendData[1]=ROOM;
			byte[] bodySize = intToByteArray(0);
			for(int i=0;i<bodySize.length;i++) {
				sendData[2+i] = (byte)bodySize[i];
			}
			os.write(sendData);
			os.flush();
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void sendMessage(String msg, String groupid) { // 채팅 전송
		try {
			int bodylength = id.getBytes("UTF-8").length + groupid.getBytes("UTF-8").length + msg.getBytes("UTF-8").length
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
			body = (id + "," + groupid + "," + msg).getBytes("UTF-8");
			System.arraycopy(body, 0, sendData, 6, body.length);

			os.write(sendData);
			os.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createGroup(String[] friendids) { // 채팅방 생성
		try {
			int bodylength = 0;
			for (int i = 0; i < friendids.length; i++) {
				bodylength += friendids[i].getBytes("UTF-8").length; 
			}
			System.out.println();
			bodylength += (friendids.length - 1);
			byte sendData[] = new byte[6+bodylength]; // 전체 보낼 데이터
			
			sendData[0] = STX; // 시작?
			sendData[1] = CREATEGROUP; // 채팅방 생성
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("보낼 데이터의 크기 : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // 보낼 데이터 크기
			byte body[] = new byte[bodylength];
			StringBuffer str = new StringBuffer();
			int i;
			for (i = 0; i < friendids.length - 1; i++) {
				str.append(friendids[i]);
				str.append(",");
			}
			str.append(friendids[i]);
			
			System.out.println("body length" + body.length);
			
			body = str.toString().getBytes("UTF-8");
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("보낼 데이터 : " + new String(body,"UTF-8"));

			os.write(sendData);
			os.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//채팅방 개설시 채팅내용을 가져오는 메서드
	public void openChat(String groupid) {
		try {
			int bodylength = groupid.getBytes("UTF-8").length; 
			byte sendData[] = new byte[6+bodylength]; // 전체 보낼 데이터
			
			sendData[0] = STX; // 시작?
			sendData[1] = OPENCHAT; // 채팅방 오픈
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("보낼 데이터의 크기 : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // 보낼 데이터 크기
			byte body[] = new byte[bodylength];
			
			System.out.println("body length" + body.length);
			
			body = groupid.getBytes("UTF-8");
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("보낼 데이터 : " + new String(body,"UTF-8"));

			os.write(sendData);
			os.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
