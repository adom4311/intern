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
	public static final int DELETEFRIEND =18;//ģ�� ����

    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
	
	private ServerSocket serverSocket; // ��������
	private ServerSocket fileserverSocket;

	private Socket socket; // �޾ƿ� ����
	private Socket filesocket;

	String connectId;

	/* ���� �������� ����ڵ��� ���� */
	private Map<String, ObjectOutputStream> currentClientMap = new HashMap<String, ObjectOutputStream>();
	private Map<String, DataOutputStream> currentClientfileMap = new HashMap<String, DataOutputStream>();
	/* groupid �� ���� ����� ���� */
	private Map<Long, Map<String,ObjectOutputStream>> groupidClientMap = new HashMap<Long,Map<String,ObjectOutputStream>>();

	private int non_login_increment = 0; // �α��� �� �ӽð�
		
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
			
			Header header = new Header(MSG,0); // ������ũ�Ⱑ ���ó�� ����.
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
			serverSocket = new ServerSocket(1993); // ���� ���� ����
			fileserverSocket = new ServerSocket(1994);

			System.out.println("---���� ����---");
			
			//groupid �� map setting
			createGroupidMap();

			OldDataDelete odd = new OldDataDelete(this);
			Timer scheduler = new Timer();
			scheduler.scheduleAtFixedRate(odd, 60 * 1000, 24 * 60 * 60 * 1000); // 1�� �ĺ��� 1�� ���� (2~3�� ������ ����)
			
			while(true) {
				socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���� ����
				System.out.println(socket.getInetAddress() + "���� ����"); // IP
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
	
	/* ���������� �ʿ� �߰� */
	public void addClient(String id, ObjectOutputStream oos, DataOutputStream fos) {
		currentClientMap.put(id, oos);
		currentClientfileMap.put(id, fos);
	}
	
	/* ������ ����� Ŭ���̾�Ʈ�� ������ ���� ��� */
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
				System.out.println("���ù� ����");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while(ois != null){
					Data data = (Data) ois.readObject();
					/* �輺�� ���ϻ�� */
					if(data.getHeader().getMenu() == SIGNUP) {
						User user = (User)data.getObject();
						int result = sDao.signUp(user.getUserid(),user.getPassword());
						Header header = new Header(SIGNUP,0); // ������ũ�Ⱑ ���ó�� ����.
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == LOGIN) {
						User user = (User)data.getObject();
						user = sDao.login(user.getUserid(),user.getPassword());
						Header header = new Header(LOGIN,0); // ������ũ�Ⱑ ���ó�� ����.
						Data sendData = new Data(header,user);
						if(user != null) {
							currentClientMap.put(user.getUserid().toLowerCase(), currentClientMap.remove(connectId)); // �ӽþ��̵� �α��� ���̵�� ����
							currentClientfileMap.put(user.getUserid().toLowerCase(),currentClientfileMap.remove(connectId));
							connectId = user.getUserid().toLowerCase(); // serverBack�� connectId�� �����ڷ�
						}
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == FRIFIND) { 
						String searchContent = (String)data.getObject();
						Object rowData[][] = sDao.friFind(connectId,searchContent);
						Header header = new Header(FRIFIND,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == ADDFRI) {
						String friendId = (String)data.getObject();
						int result = sDao.addfri(connectId,friendId);
						Header header = new Header(ADDFRI,0); // ������ũ�Ⱑ ���ó�� ����.
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == FRILIST) {
						Object rowData[][] = sDao.friList(connectId);
						Header header = new Header(FRILIST,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == CREATEROOM) {
						String[] friendids = (String[])data.getObject();
						int result = sDao.createRoom(connectId,friendids); // ä�ù� ����
						Long groupid = sDao.selectRoom(connectId,friendids,ONEROOM); // groupid
						if(groupid != null)
							groupidClientMap.put(groupid, new HashMap<String,ObjectOutputStream>());
						System.out.println("CREATEROOM select �� �׷���̵� : " + groupid);
						Header header = new Header(CREATEROOM,0);
						Data sendData = new Data(header,groupid);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == MSG) {
						Chat message = (Chat)data.getObject();
						List<String> groupmember = sDao.selectGroupmember(message.getGroupid());
						broadcast(message, groupmember ,sDao);
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == OPENCHAT) {
						ChatMember chatmember = (ChatMember)data.getObject();
						List<Chat> chatcontent = sDao.selectchatcontent(chatmember);
						Header header = new Header(OPENCHAT,0);
						ChatcontentList chatcontentList = new ChatcontentList(chatmember.getGroupid(), chatcontent);
						Data sendData = new Data(header,chatcontentList);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == GROUPROOMLIST) {
						Object rowData[][] = sDao.friList(connectId);
						Header header = new Header(GROUPROOMLIST,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == CREATEGROUPROOM) {
						String[] friendids = (String[])data.getObject();
						Long groupid = sDao.createGroupRoom(connectId,friendids); // ä�ù� ����
						if(groupid != null)
							groupidClientMap.put(groupid, new HashMap<String,ObjectOutputStream>());
						System.out.println("CREATEROOM select �� �׷���̵� : " + groupid);
						Header header = new Header(CREATEGROUPROOM,0);
						Data sendData = new Data(header,groupid);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == UPDATELASTREAD) {
						Chat message = (Chat)data.getObject();
						int result = sDao.updatereadtime(connectId,message);
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == ROOMNAME) {
						RoomName rn = (RoomName)data.getObject();
						rn.setUserid(connectId);
						int result = sDao.updateRoomName(rn);
						Header header = new Header(ROOMNAME,0);
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == DELETEFRIEND) {
						String friendid = (String)data.getObject();
						int result = sDao.deleteFriend(connectId,friendid);
						Header header = new Header(DELETEFRIEND,0);
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* ���� ���ϻ�� */
					else if(data.getHeader().getMenu() == ROOM) {
						Object rowData[][] = sDao.roomList(connectId);
						Header header = new Header(ROOM,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* ���� ���ϻ�� */
					else if(data.getHeader().getMenu() == FMSG) {
						Filemessage filemessage = (Filemessage) data.getObject();
						List<String> groupmember = sDao.selectGroupmember(filemessage.getGroupid());
						new ServerFileThread(filemessage,fileserverSocket).start();
						//���� �ް� 
					}
					/* ���� ���ϻ�� */
					else if(data.getHeader().getMenu()==FILIST) {
						Long groupid = (Long)data.getObject();
						Object rowdata[][] = sDao.selectfilecontent(groupid);
						Header header = new Header(FILIST,0);
						Filelist filelist = new Filelist(groupid,rowdata);
						Data sendData = new Data(header, filelist);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* ���� ���ϻ�� */
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
					System.out.println(connectId + "���� Ŭ���̾�Ʈ �����Ͽ� ������ �����մϴ�.");
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

