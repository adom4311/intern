package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import client.control.sungjo.ReadchatFile;
import client.gui.Chatwindow;
import client.gui.ClientGUI;
import client.gui.ClientHome;
import client.gui.Galarygui;
import client.request.sangwoo.Addmemberrequest;
import client.request.sangwoo.FileListRequest;
import client.request.sangwoo.FiledownRequest;
import client.request.sangwoo.Galaryrequest;
import client.request.sangwoo.Memberrequest;
import client.request.sangwoo.Outroomrequest;
import client.request.sangwoo.RoomListRequest;
import client.request.sangwoo.SendFileMessageRequest;
import client.request.sangwoo.SendFileRequest;
import client.request.sangwoo.SendMessageRequest;
import client.request.sungjo.AddFriendRequest;
import client.request.sungjo.ChatFriListRequest;
import client.request.sungjo.CreateGroupRoomListRequest;
import client.request.sungjo.CreateGroupRoomRequest;
import client.request.sungjo.CreateRoomRequest;
import client.request.sungjo.DeleteFriendRequest;
import client.request.sungjo.FindfriendRequest;
import client.request.sungjo.FriListRequest;
import client.request.sungjo.GroupNameChangRequest;
import client.request.sungjo.LoginRequest;
import client.request.sungjo.OpenChatRequest;
import client.request.sungjo.SignupRequest;
import client.response.sangwoo.Addmemberresponse;
import client.response.sangwoo.FileListResponse;
import client.response.sangwoo.FileRecResponse;
import client.response.sangwoo.FmsgResponse;
import client.response.sangwoo.GalListResponse;
import client.response.sangwoo.OutRoomResponse;
import client.response.sangwoo.RoomResponse;
import client.response.sungjo.AddfriResponse;
import client.response.sungjo.ChatFriListResponse;
import client.response.sungjo.CreateGroupRoomListResponse;
import client.response.sungjo.CreateGroupRoomResponse;
import client.response.sungjo.CreateRoomResponse;
import client.response.sungjo.DeleteFriendResponse;
import client.response.sungjo.FrifindResponse;
import client.response.sungjo.FrilistResponse;
import client.response.sungjo.GroupNameChangRequestResponse;
import client.response.sungjo.LoginResponse;
import client.response.sungjo.MsgResponse;
import client.response.sungjo.OpenchatResponse;
import client.response.sungjo.SignupResponse;
import model.vo.Data;
import model.vo.RoomName;

public class ClientBack {
	public static final int SIGNUP = 1; // ȸ������
	public static final int LOGIN = 2; // �α���
	public static final int MSG = 3; // �Ϲݸ޽���
	public static final int FRIFIND = 4; // ģ��ã��
	public static final int ADDFRI = 5; // ģ���߰�
	public static final int FMSG = 6;// ����, �̹��� ����
	public static final int CREATEROOM = 7; // 1:1 ä�ù� ����
	public static final int FRILIST = 8; // ģ�����
	public static final int OPENCHAT = 9; // ä�ù� ���½� DB ä�� ������ ��������
	public static final int ROOM = 10; //ä�ù���
	public static final int GROUPROOMLIST = 11; // �׷� ä�ù� �� ģ�����
	public static final int UPDATELASTREAD = 12; // ����ó����
	public static final int CREATEGROUPROOM = 13;  // �׷�ä�ù�  ����
	public static final int ROOMOPEN = 14; // ���õ� ä�ù� ����
	public static final int FILIST = 15;//���� ���
	public static final int FIDOWN =16;//���� �ٿ� ��û
	
	public static final int ROOMNAME =17;//��� ����
	public static final int DELETEFRIEND =18;// ģ�� ����
	public static final int CHATFRILIST =19;// ä�ù� ģ�� ����Ʈ
	
	public static final int OROOM = 20;//�׷�� ������ ��û
	public static final int GAL = 21;//������ ��� ��û
	public static final int AMEM = 22;//ä�ù� ��� �߰� ���ɸ���Ʈ ��û
	public static final int MEM = 23;//ä�ù� ��� �߰� ��â
	

    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
	
	private String id;
	private Socket socket;
	private Socket filesocket;
	private Socket readProcessingSocket;

	private ClientGUI gui;
	private ClientHome home;
	private Object dirs[][];
	private Map<Long,Chatwindow> chatMap = new HashMap<Long, Chatwindow>();
	private Map<Long,FileWriter> chatFileMap = new HashMap<Long, FileWriter>();
	
	private DataInputStream is;
	private DataOutputStream os;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ObjectOutputStream rpoos;
	private String SERVER_ADDR = "127.0.0.1";
	private int PORT = 1993;
	private int READ_PORT = 1995;
	
	private Galarygui galgui=null;
	
	public Galarygui getGalgui() {
		return galgui;
	}
	public void setGalgui(Galarygui galgui) {
		this.galgui = galgui;
	}
	public ObjectOutputStream getRpoos() {
		return rpoos;
	}
	public void setRpoos(ObjectOutputStream rpoos) {
		this.rpoos = rpoos;
	}
	public Socket getReadProcessingSocket() {
		return readProcessingSocket;
	}
	public void setReadProcessingSocket(Socket readProcessingSocket) {
		this.readProcessingSocket = readProcessingSocket;
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
	
	public Map<Long, FileWriter> getChatFileMap() {
		return chatFileMap;
	}
	public void setChatFileMap(Map<Long, FileWriter> chatFileMap) {
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
	
	// �ޱ⸸ �ϴ� ������
	class ClientReceiver extends Thread{
		private ClientBack clientback;
		public ClientReceiver(Socket socket, ClientBack clientback) {
			try {
				this.clientback = clientback; 
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				System.out.println("Ŭ���̾�Ʈ ���ù� ����");
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
					else if(data.getHeader().getMenu()==ROOMNAME) {
						new GroupNameChangRequestResponse(clientback,data);
					}
					else if(data.getHeader().getMenu()==DELETEFRIEND) {
						new DeleteFriendResponse(clientback,data);
					}
					else if(data.getHeader().getMenu()==CHATFRILIST) {
						new ChatFriListResponse(clientback,data);
					}
					else if(data.getHeader().getMenu()==OROOM) {
						new OutRoomResponse(clientback,data);
					}
					else if(data.getHeader().getMenu()==GAL) {
						galgui = new GalListResponse(clientback,data,galgui).show();
					}
					else if(data.getHeader().getMenu()==AMEM) {
						new Addmemberresponse(clientback,data);
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Alert("�����ð� �Է��� ���� ����˴ϴ�.");
				System.exit(0);
				e.printStackTrace();
			}
		}
	}
	
	public void Alert(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
	
	public void connect() {
		try {
			socket = new Socket(SERVER_ADDR,PORT);
			readProcessingSocket = new Socket(SERVER_ADDR,READ_PORT);
			rpoos = new ObjectOutputStream(readProcessingSocket.getOutputStream());
			System.out.println("������ �����");
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

	public void findFriend(String searchContent) {
		new FindfriendRequest(this,searchContent );
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

	public void sendMessage(String msg, Long groupid) { // ä�� ����
		new SendMessageRequest(this,msg,groupid);
	}

	public void createRoom(String[] friendids) { // ä�ù� ����
		new CreateRoomRequest(this, friendids);
	}
	
	public void fileList(Long groupid) {
		new FileListRequest(this,groupid);
	}
	
	public void chatfriList(Long groupid) {
		new ChatFriListRequest(this,groupid);
	}
	
	public void sendFilemessage(String file_dir, Long groupid) {
		new SendFileMessageRequest(this,file_dir,groupid);
		new SendFileRequest(this,file_dir, groupid).start();
	}
	
	public void filedownreq(Long groupid, String dir,boolean isImg) {
		new FiledownRequest(this,groupid,dir,isImg);
	}
	
	public void createGroupRoom(String[] friendids) { // ä�ù� ����
		new CreateGroupRoomRequest(this, friendids);
	}

	//ä�ù� ������ ä�ó����� �������� �޼���
	public void openChat(Long groupid) {
		new OpenChatRequest(this,groupid);
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
	public void groupNameChange(RoomName rn) {
		new GroupNameChangRequest(this,rn);
	}
	public void deleteFriend(String friendid) {
		new DeleteFriendRequest(this,friendid);
	}
	public void outroom(String userid,Long groupid) {
		new Outroomrequest(this,userid,groupid);
	}
	
	public void galaryfunc(Long groupid) {
		new Galaryrequest(this,groupid);
	}
	
	public void addmemberfunc(Long groupid) {
		new Addmemberrequest(this,groupid);
	}
	
	public void memberfunc(Long groupid,String userid) {
		new Memberrequest(this,groupid,userid);
	}
}
