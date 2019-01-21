package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import client.control.sungjo.ReadchatFile;
import client.gui.Chatwindow;
import client.gui.ClientGUI;
import client.gui.ClientHome;
import client.request.sangwoo.FileListRequest;
import client.request.sangwoo.FiledownRequest;
import client.request.sangwoo.RoomListRequest;
import client.request.sangwoo.SendFileMessageRequest;
import client.request.sangwoo.SendFileRequest;
import client.request.sangwoo.SendMessageRequest;
import client.request.sungjo.AddFriendRequest;
import client.request.sungjo.CreateGroupRoomListRequest;
import client.request.sungjo.CreateGroupRoomRequest;
import client.request.sungjo.CreateRoomRequest;
import client.request.sungjo.FindfriendRequest;
import client.request.sungjo.FriListRequest;
import client.request.sungjo.LoginRequest;
import client.request.sungjo.OpenChatRequest;
import client.request.sungjo.SignupRequest;
import client.response.sangwoo.FileListResponse;
import client.response.sangwoo.FileRecResponse;
import client.response.sangwoo.FmsgResponse;
import client.response.sangwoo.RoomResponse;
import client.response.sungjo.AddfriResponse;
import client.response.sungjo.CreateGroupRoomListResponse;
import client.response.sungjo.CreateGroupRoomResponse;
import client.response.sungjo.CreateRoomResponse;
import client.response.sungjo.FrifindResponse;
import client.response.sungjo.FrilistResponse;
import client.response.sungjo.LoginResponse;
import client.response.sungjo.MsgResponse;
import client.response.sungjo.OpenchatResponse;
import client.response.sungjo.SignupResponse;
import model.vo.Chat;
import model.vo.Data;

public class ClientBack {
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

    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
	
	private String id;
	private String pw;
	private Socket socket;
	private Socket filesocket;
	private ClientGUI gui;
	private ClientHome home;
	private Chatwindow chatwindow;
	private Object dirs[][];
	private Map<Long,Chatwindow> chatMap = new HashMap<Long, Chatwindow>();
	private Map<Long,ObjectOutputStream> chatFileMap = new HashMap<Long, ObjectOutputStream>();
	private Map<Long,ArrayList<Chat>> chatFileListMap = new HashMap<Long, ArrayList<Chat>>();
	
	private DataInputStream is;
	private DataOutputStream os;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String SERVER_ADDR = "127.0.0.1";
	private int PORT = 1993;
	private int FILE_PORT = 1994;
	
	public Map<Long, ArrayList<Chat>> getChatFileListMap() {
		return chatFileListMap;
	}
	public void setChatFileListMap(Map<Long, ArrayList<Chat>> chatFileListMap) {
		this.chatFileListMap = chatFileListMap;
	}
	public ClientHome getHome() {
		return home;
	}
	public void setHome(ClientHome home) {
		this.home = home;
	}
	public ClientGUI getGui() {
		return gui;
	}

	public void setId(String i) {
		this.id=i;
	}
	
	public String getId() {
		return this.id;
	}
	
	public DataInputStream getDataInputstream() {
		return this.is;
	}
	
	public DataOutputStream getOutputStream() {
		return this.os;
	}
	
	public ObjectInputStream getOis() {
		return ois;
	}

	public ObjectOutputStream getOos() {
		return oos;
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
	
	public Map<Long, Chatwindow> getChatMap() {
		return chatMap;
	}
	public void setChatMap(Map<Long, Chatwindow> chatMap) {
		this.chatMap = chatMap;
	}
	public Object[][] getdirs(){
		return dirs;
	}
	public void setdirs(Object dirs[][]) {
		this.dirs=dirs;
	}
	
	public ClientBack() {
		connect();
	}
	
	public Map<Long, ObjectOutputStream> getChatFileMap() {
		return chatFileMap;
	}
	public void setChatFileMap(Map<Long, ObjectOutputStream> chatFileMap) {
		this.chatFileMap = chatFileMap;
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
		public ClientReceiver(Socket socket, ClientBack clientback) {
			try {
				this.clientback = clientback; 
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				System.out.println("클라이언트 리시버 생성");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while(ois != null) {
					Data data = (Data) ois.readObject();
					if(data.getHeader().getMenu() == SIGNUP) {
						new SignupResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == LOGIN) {
						new LoginResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == FRIFIND) {
						new FrifindResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == ADDFRI) {
						new AddfriResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == FRILIST) {
						new FrilistResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == CREATEROOM) {
						new CreateRoomResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == ROOM) {
						new RoomResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == MSG) {
						new MsgResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == FMSG) {
						new FmsgResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == OPENCHAT) {
						new OpenchatResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == GROUPROOMLIST) {
						new CreateGroupRoomListResponse(clientback,data);
					}
					else if(data.getHeader().getMenu() == CREATEGROUPROOM) {
						new CreateGroupRoomResponse(clientback,data);
					}
					else if(data.getHeader().getMenu()==FILIST) {
						new FileListResponse(clientback,data);
					}
					else if(data.getHeader().getMenu()==FIDOWN) {
						new FileRecResponse(clientback,data).start();
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void connect() {
		try {
			socket = new Socket(SERVER_ADDR,PORT);
			System.out.println("서버와 연결됨");
			ClientReceiver receiver = new ClientReceiver(socket, this);
			receiver.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void signUp(String id, String pw) {
		new SignupRequest(this,id,pw);
	}

	public void login(String id, String pw) {
		new LoginRequest(this,id,pw);
	}

	public void findFriend() {
		new FindfriendRequest(this);
	}

	public void addFriend(String friendId) {
		new AddFriendRequest(this,friendId);
	}

	public void friList() {
		new FriListRequest(this);
	}
	
	public void roomList() {
		new RoomListRequest(this);
	}

	public void sendMessage(String msg, Long groupid) { // 채팅 전송
		new SendMessageRequest(this,msg,groupid);
	}

	public void createRoom(String[] friendids) { // 채팅방 생성
		new CreateRoomRequest(this, friendids);
	}
	
	public void fileList(Long groupid) {
		new FileListRequest(this,groupid);
	}
	
	public void sendFilemessage(String file_dir, Long groupid) {
		new SendFileMessageRequest(this,file_dir,groupid);
		new SendFileRequest(this,file_dir, groupid).start();
	}
	
	public void filedownreq(Long groupid, String dir) {
		new FiledownRequest(this,groupid,dir);
	}
	
	public void createGroupRoom(String[] friendids) { // 채팅방 생성
		new CreateGroupRoomRequest(this, friendids);
	}

	//채팅방 개설시 채팅내용을 가져오는 메서드
	public void openChat(Chat chat) {
		new OpenChatRequest(this,chat);
	}
	public void readchatFile(Long groupid) {
		new ReadchatFile(this,groupid);
	}
	public void createGroupRoom() {
		new CreateGroupRoomListRequest(this);
	}
	public void roomOpen(Long groupid) {
		new ReadchatFile(this,groupid);
	}
}
