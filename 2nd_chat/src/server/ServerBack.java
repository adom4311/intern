package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import model.dao.ServerDAO;
import model.vo.Chat;
import model.vo.ChatMember;
import model.vo.ChatcontentList;
import model.vo.Data;
import model.vo.Filedownmessage;
import model.vo.Filelist;
import model.vo.Filemessage;
import model.vo.Header;
import model.vo.RoomName;
import model.vo.User;
import server.sangwoo.ServerFileThread;
import server.sangwoo.ServerFileTransferThread;

public class ServerBack {
	public static final int SIGNUP = 1; // 회원가입
	public static final int LOGIN = 2; // 로그인
	public static final int MSG = 3; // 일반메시지
	public static final int FRIFIND = 4; // 친구찾기
	public static final int ADDFRI = 5; // 친구추가
	public static final int FMSG = 6;// 파일, 이미지 전송
	public static final int CREATEROOM = 7; // 1:1 채팅방 개설
	public static final int FRILIST = 8; // 친구목록
	public static final int OPENCHAT = 9; // 채팅방 오픈시 DB 채팅 데이터 가져오기
	public static final int ROOM = 10; //채팅방목록
	public static final int GROUPROOMLIST = 11; // 그룹 채팅방 용 친구목록
	public static final int UPDATELASTREAD = 12; // 읽음처리용
	public static final int CREATEGROUPROOM = 13;  // 그룹채팅방  개설
	public static final int ROOMOPEN = 14; // 선택된 채팅방 오픈
	public static final int FILIST = 15;//파일 목록
	public static final int FIDOWN =16;//파일 다운 요청

	public static final int ROOMNAME =17;//방명 변경
	public static final int DELETEFRIEND =18;//친구 삭제

    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
	
	private ServerSocket serverSocket; // 서버소켓
	private ServerSocket fileserverSocket;

	private Socket socket; // 받아올 소켓
	private Socket filesocket;

	String connectId;

	/* 현재 접속중인 사용자들의 정보 */
	private Map<String, ObjectOutputStream> currentClientMap = new HashMap<String, ObjectOutputStream>();
	private Map<String, DataOutputStream> currentClientfileMap = new HashMap<String, DataOutputStream>();
	/* groupid 별 현재 사용자 정보 */
	private Map<Long, Map<String,ObjectOutputStream>> groupidClientMap = new HashMap<Long,Map<String,ObjectOutputStream>>();

	private int non_login_increment = 0; // 로그인 전 임시값
		
	public Map<String, ObjectOutputStream> getCurrentClientMap() {
		return currentClientMap;
	}
	public void setCurrentClientMap(Map<String, ObjectOutputStream> currentClientMap) {
		this.currentClientMap = currentClientMap;
	}
	public Map<String, DataOutputStream> getCurrentClientfileMap() {
		return currentClientfileMap;
	}
	public void setCurrentClientfileMap(Map<String, DataOutputStream> currentClientfileMap) {
		this.currentClientfileMap = currentClientfileMap;
	}
	
	
	public static void main(String[] args) {
		ServerBack serverBack = new ServerBack();
		serverBack.setting();
	}

	private void broadcast(Chat message, List<String> groupmember, ServerDAO sDao) {
		Map<String, ObjectOutputStream> groupCurrentMap = groupidClientMap.get(message.getGroupid());
		synchronized (groupCurrentMap) {
			for (String member : groupmember) {
				if(groupCurrentMap.get(member) == null && currentClientMap.get(member) != null) {
					groupCurrentMap.put(member, currentClientMap.get(member));
				}
			}
			Chat chat = sDao.insertMSG(message);
			
			Header header = new Header(MSG,0); // 데이터크기가 사용처가 없음.
			Data sendData = new Data(header,chat);
			ObjectOutputStream oos;
			for (String member : groupmember) {
				try {
					oos = groupCurrentMap.get(member);
					if(oos != null) {
						oos.writeObject(sendData);
						oos.flush();
					}
				}catch(NullPointerException e) {
					e.printStackTrace();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setting() {
		try {
			serverSocket = new ServerSocket(1993); // 서버 소켓 생성
			fileserverSocket = new ServerSocket(1994);

			System.out.println("---서버 오픈---");
			
			//groupid 별 map setting
			createGroupidMap();

			OldDataDelete odd = new OldDataDelete(this);
			Timer scheduler = new Timer();
			scheduler.scheduleAtFixedRate(odd, 60 * 1000, 24 * 60 * 60 * 1000); // 1분 후부터 1일 간격 (2~3일 데이터 저장)
			
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
	
	private void createGroupidMap() {
		ServerDAO sDao = new ServerDAO();
		ArrayList<Long> list = sDao.selectGroupid();
		for(Long groupid : list) {
			groupidClientMap.put(groupid,new HashMap<String,ObjectOutputStream>());
		}
	}
	
	public synchronized int increment() {
		return non_login_increment++;
	}
	
	/* 현재접속자 맵에 추가 */
	public void addClient(String id, ObjectOutputStream oos, DataOutputStream fos) {
		currentClientMap.put(id, oos);
		currentClientfileMap.put(id, fos);
	}
	
	/* 서버는 연결된 클라이언트의 데이터 수신 대기 */
	class Receiver extends Thread{
		private ServerDAO sDao;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private DataInputStream fis;
		private DataOutputStream fos;
		private ServerBack serverback; 
		private Socket socket;
		private Socket filesocket;
		String connectId = "GM" + increment();
		
		public Receiver(Socket socket) {
			try {
				sDao = new ServerDAO();
				this.socket = socket;
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				
				addClient(connectId,oos,fos);
				System.out.println("리시버 생성");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while(ois != null){
					Data data = (Data) ois.readObject();
					/* 김성조 인턴사원 */
					if(data.getHeader().getMenu() == SIGNUP) {
						User user = (User)data.getObject();
						int result = sDao.signUp(user.getUserid(),user.getPassword());
						Header header = new Header(SIGNUP,0); // 데이터크기가 사용처가 없음.
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == LOGIN) {
						User user = (User)data.getObject();
						user = sDao.login(user.getUserid(),user.getPassword());
						Header header = new Header(LOGIN,0); // 데이터크기가 사용처가 없음.
						Data sendData = new Data(header,user);
						if(user != null) {
							currentClientMap.put(user.getUserid().toLowerCase(), currentClientMap.remove(connectId)); // 임시아이디를 로그인 아이디로 변경
							currentClientfileMap.put(user.getUserid().toLowerCase(),currentClientfileMap.remove(connectId));
							connectId = user.getUserid().toLowerCase(); // serverBack의 connectId를 접속자로
						}
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == FRIFIND) { 
						String searchContent = (String)data.getObject();
						Object rowData[][] = sDao.friFind(connectId,searchContent);
						Header header = new Header(FRIFIND,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == ADDFRI) {
						String friendId = (String)data.getObject();
						int result = sDao.addfri(connectId,friendId);
						Header header = new Header(ADDFRI,0); // 데이터크기가 사용처가 없음.
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == FRILIST) {
						Object rowData[][] = sDao.friList(connectId);
						Header header = new Header(FRILIST,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == CREATEROOM) {
						String[] friendids = (String[])data.getObject();
						int result = sDao.createRoom(connectId,friendids); // 채팅방 개설
						Long groupid = sDao.selectRoom(connectId,friendids,ONEROOM); // groupid
						if(groupid != null)
							groupidClientMap.put(groupid, new HashMap<String,ObjectOutputStream>());
						System.out.println("CREATEROOM select 시 그룹아이디 : " + groupid);
						Header header = new Header(CREATEROOM,0);
						Data sendData = new Data(header,groupid);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == MSG) {
						Chat message = (Chat)data.getObject();
						List<String> groupmember = sDao.selectGroupmember(message.getGroupid());
						broadcast(message, groupmember ,sDao);
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == OPENCHAT) {
						ChatMember chatmember = (ChatMember)data.getObject();
						List<Chat> chatcontent = sDao.selectchatcontent(chatmember);
						Header header = new Header(OPENCHAT,0);
						ChatcontentList chatcontentList = new ChatcontentList(chatmember.getGroupid(), chatcontent);
						Data sendData = new Data(header,chatcontentList);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == GROUPROOMLIST) {
						Object rowData[][] = sDao.friList(connectId);
						Header header = new Header(GROUPROOMLIST,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == CREATEGROUPROOM) {
						String[] friendids = (String[])data.getObject();
						Long groupid = sDao.createGroupRoom(connectId,friendids); // 채팅방 개설
						if(groupid != null)
							groupidClientMap.put(groupid, new HashMap<String,ObjectOutputStream>());
						System.out.println("CREATEROOM select 시 그룹아이디 : " + groupid);
						Header header = new Header(CREATEGROUPROOM,0);
						Data sendData = new Data(header,groupid);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == UPDATELASTREAD) {
						Chat message = (Chat)data.getObject();
						int result = sDao.updatereadtime(connectId,message);
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == ROOMNAME) {
						RoomName rn = (RoomName)data.getObject();
						rn.setUserid(connectId);
						int result = sDao.updateRoomName(rn);
						Header header = new Header(ROOMNAME,0);
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 김성조 인턴사원 */
					else if(data.getHeader().getMenu() == DELETEFRIEND) {
						String friendid = (String)data.getObject();
						int result = sDao.deleteFriend(connectId,friendid);
						Header header = new Header(DELETEFRIEND,0);
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 백상우 인턴사원 */
					else if(data.getHeader().getMenu() == ROOM) {
						Object rowData[][] = sDao.roomList(connectId);
						Header header = new Header(ROOM,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 백상우 인턴사원 */
					else if(data.getHeader().getMenu() == FMSG) {
						Filemessage filemessage = (Filemessage) data.getObject();
						List<String> groupmember = sDao.selectGroupmember(filemessage.getGroupid());
						new ServerFileThread(filemessage,fileserverSocket).start();
						//파일 받고 
					}
					/* 백상우 인턴사원 */
					else if(data.getHeader().getMenu()==FILIST) {
						Long groupid = (Long)data.getObject();
						Object rowdata[][] = sDao.selectfilecontent(groupid);
						Header header = new Header(FILIST,0);
						Filelist filelist = new Filelist(groupid,rowdata);
						Data sendData = new Data(header, filelist);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* 백상우 인턴사원 */
					else if(data.getHeader().getMenu()==FIDOWN) {
						Filedownmessage filedownmessage = (Filedownmessage)data.getObject();
//						Long groupid = filedownmessage.getGroupid();
						String filedir = filedownmessage.getFile_dir();
						Header header = new Header(FIDOWN,0);
						Data sendData = new Data(header,filedir);
						oos.writeObject(sendData);
						oos.flush();
						new ServerFileTransferThread(filedownmessage,fileserverSocket).start();
					}
				}
			}catch (SocketException e) {
				try {
					currentClientMap.remove(connectId);
					ArrayList<Long> list = sDao.selectgroupiduser(connectId);
					for(Long groupid : list) {
						groupidClientMap.get(groupid).remove(connectId);
					}
					socket.close();
					System.out.println(connectId + "님이 클라이언트 종료하여 쓰레드 종료합니다.");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

