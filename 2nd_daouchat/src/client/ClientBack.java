package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import client.request.AddFriendRequest;
import client.request.CreateRoomRequest;
import client.request.FindfriendRequest;
import client.request.FriListRequest;
import client.request.LoginRequest;
import client.request.OpenChatRequest;
import client.request.RoomListRequest;
import client.request.SendMessageRequest;
import client.request.SignupRequest;
import client.response.AddfriResponse;
import client.response.CreateRoomResponse;
import client.response.FmsgResponse;
import client.response.FrifindResponse;
import client.response.FrilistResponse;
import client.response.LoginResponse;
import client.response.MsgResponse;
import client.response.OpenchatResponse;
import client.response.RoomResponse;
import client.response.SignupResponse;
import model.vo.Data;

public class ClientBack {
	public static final int SIGNUP = 1; // ȸ������
	public static final int LOGIN = 2; // �α���
	public static final int MSG = 3; // �Ϲݸ޽���
	public static final int FRIFIND = 4; // ģ��ã��
	public static final int ADDFRI = 5; // ģ���߰�
	public static final int FMSG = 6;// ����, �̹��� ����
	public static final int CREATEROOM = 7; // �׷����
	public static final int FRILIST = 8; // ģ�����
	public static final int OPENCHAT = 9; // �׷����
	public static final int ROOM = 10; //ä�ù���

    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
	
	private String id;
	private String pw;
	private Socket socket;
	private Socket filesocket;
	private ClientGUI gui;
	private ClientHome home;
	private Chatwindow chatwindow;
	private Map<Long,Chatwindow> chatMap = new HashMap<Long, Chatwindow>();
	private DataInputStream is;
	private DataOutputStream os;
	private DataInputStream fis;
	private DataOutputStream fos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String SERVER_ADDR = "127.0.0.1";
	private int PORT = 1993;
	private int FILE_PORT = 1994;
	
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
	
	// �ޱ⸸ �ϴ� ������
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
					}else if(data.getHeader().getMenu() == LOGIN) {
						new LoginResponse(clientback,data);
					}else if(data.getHeader().getMenu() == FRIFIND) {
						new FrifindResponse(clientback,data);
					}else if(data.getHeader().getMenu() == ADDFRI) {
						new AddfriResponse(clientback,data);
					}else if(data.getHeader().getMenu() == FRILIST) {
						new FrilistResponse(clientback,data);
					}else if(data.getHeader().getMenu() == CREATEROOM) {
						new CreateRoomResponse(clientback,data);
					}else if(data.getHeader().getMenu() == ROOM) {
						new RoomResponse(clientback,data);
					}else if(data.getHeader().getMenu() == MSG) {
						new MsgResponse(clientback,data);
					}else if(data.getHeader().getMenu() == FMSG) {
						new FmsgResponse(clientback,data);
					}else if(data.getHeader().getMenu() == OPENCHAT) {
						new OpenchatResponse(clientback,data);
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//					/* ���� �ޱ� */
//					else if(headerBuffer[1]==FMSG) {
//						System.out.println("����");
//						byte[] lengthChk = new byte[4];
//						lengthChk[0]=headerBuffer[2];
//						lengthChk[1] = headerBuffer[3];
//						lengthChk[2] = headerBuffer[4];
//						lengthChk[3] = headerBuffer[5];
//						int datalength = byteArrayToInt(lengthChk);
//						System.out.println("�����ͱ��� : " + datalength);
//
//						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//						int read;
//						reciveData = new byte[datalength];
//
//						// ���� ���������� ���
//						while ((read = is.read(reciveData, 0, reciveData.length)) != -1) {
//							buffer.write(reciveData, 0, read);
//							datalength -= read;
//							if (datalength <= 0) { // �� ������ break
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
//						System.out.println("Ŭ���̾�Ʈ�� �޴� ���� ũ��� : "+files);
//						long length = bytesToLong(sizebyte);
//						while ((count = in.read(bytes)) > 0) {
//				            out.write(bytes, 0, count);
//				            length-=count;
//							System.out.println(length);
//							if(length<=0) break;
//				        }
//						
//						out.close();
//					}// ���Ϲޱ� END
		}
	}
	public void connect() {
		try {
			socket = new Socket(SERVER_ADDR,PORT);
			filesocket = new Socket(SERVER_ADDR,FILE_PORT);
			System.out.println("������ �����");
			ClientReceiver receiver = new ClientReceiver(socket, filesocket, this);
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

	public void sendMessage(String msg, Long groupid) { // ä�� ����
		new SendMessageRequest(this,msg,groupid);
	}

	public void createRoom(String[] friendids) { // ä�ù� ����
		new CreateRoomRequest(this, friendids);
	}

	//ä�ù� ������ ä�ó����� �������� �޼���
	public void openChat(Long groupid) {
		new OpenChatRequest(this,groupid);
	}
}
