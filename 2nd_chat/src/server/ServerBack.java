package server;

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
import model.vo.Amemessage;
import model.vo.Chat;
import model.vo.ChatFriList;
import model.vo.ChatMember;
import model.vo.ChatcontentList;
import model.vo.Data;
import model.vo.Filedownmessage;
import model.vo.Filelist;
import model.vo.Filemessage;
import model.vo.Galmessage;
import model.vo.GroupInfo;
import model.vo.Header;
import model.vo.RoomName;
import model.vo.Roominfo;
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
	public static final int DELETEFRIEND =18;// ģ�� ����
	public static final int CHATFRILIST =19;// ä�ù� ģ�� ����Ʈ
	
	public static final int OROOM = 20;//�׷�� ������ ��û
	public static final int GAL = 21;//������ ��� ��û
	public static final int AMEM = 22;//ä�ù� ��� �߰� ���ɸ���Ʈ ��û
	public static final int MEM = 23;//ä�ù� ��� �߰� ��â


    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;

	private int PORT = 1993;
	private int FILE_PORT = 1994;
	private int READ_PORT = 1995;
	
	private ServerSocket serverSocket; // ��������
	private ServerSocket fileserverSocket;
	private ServerSocket readProcessingSocket;

	private Socket socket; // �޾ƿ� ����
	private Socket socket2;

	String connectId;

	/* ���� �������� ����ڵ��� ���� */
	private Map<String, ObjectOutputStream> currentClientMap = new HashMap<String, ObjectOutputStream>();
	/* groupid �� ���� ����� ���� */
	private Map<Long, Map<String,ObjectOutputStream>> groupidClientMap = new HashMap<Long,Map<String,ObjectOutputStream>>();

	private int non_login_increment = 0; // �α��� �� �ӽð�
		
	public Map<String, ObjectOutputStream> getCurrentClientMap() {
		return currentClientMap;
	}
	public void setCurrentClientMap(Map<String, ObjectOutputStream> currentClientMap) {
		this.currentClientMap = currentClientMap;
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
			serverSocket = new ServerSocket(PORT); // ���� ���� ����
			fileserverSocket = new ServerSocket(FILE_PORT);
			readProcessingSocket = new ServerSocket(READ_PORT);

			System.out.println("---���� ����---");
			
			//groupid �� map setting
			createGroupidMap();

			OldDataDelete odd = new OldDataDelete(this);
			Timer scheduler = new Timer();
			scheduler.scheduleAtFixedRate(odd, 60 * 1000, 24 * 60 * 60 * 1000); // 1�� �ĺ��� 1�� ���� (2~3�� ������ ����)
			rpmReceiver rpmreceiver = new rpmReceiver(readProcessingSocket);
			rpmreceiver.start();
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
	public void addClient(String id, ObjectOutputStream oos) {
		currentClientMap.put(id, oos);
	}
	
	/* ������ ����� Ŭ���̾�Ʈ�� ������ ���� ��� */
	class Receiver extends Thread{
		private ServerDAO sDao;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private Socket socket;
		String connectId = "GM" + increment();
		
		public Receiver(Socket socket) {
			try {
				sDao = new ServerDAO();
				this.socket = socket;
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				
				addClient(connectId,oos);
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
						GroupInfo info = sDao.selectRoom(connectId,friendids,ONEROOM); // groupid
						if(info != null)
							groupidClientMap.put(info.getGroupid(), new HashMap<String,ObjectOutputStream>());
						System.out.println("CREATEROOM select �� �׷���̵� : " + info.getGroupid());
						Header header = new Header(CREATEROOM,0);
						Data sendData = new Data(header,info);
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
						String groupname = sDao.selectGroupName(chatmember);
						Header header = new Header(OPENCHAT,0);
						ChatcontentList chatcontentList = new ChatcontentList(chatmember.getGroupid(), groupname, chatcontent);
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
						rn.setResult(result);
						Header header = new Header(ROOMNAME,0);
						Data sendData = new Data(header,rn);
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
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu()==CHATFRILIST) {
						Long groupid = (Long)data.getObject();
						Object rowdata[][] = sDao.selectChatFriList(groupid);
						Header header = new Header(CHATFRILIST,0);
						ChatFriList chatFriList = new ChatFriList(groupid,rowdata);
						Data sendData = new Data(header, chatFriList);
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
						new ServerFileThread(filemessage,fileserverSocket,oos).start();
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
					/* ���� ���ϻ�� */
					else if(data.getHeader().getMenu()==OROOM) {
						Roominfo roominfo = (Roominfo)data.getObject();
						if(sDao.outRoom(roominfo)) {
							System.out.println("�濡�� ���������ϴ�.");
							Header header = new Header(OROOM,0);
							Data sendData = new Data(header,roominfo);
							oos.writeObject(sendData);
							oos.flush();
						}
					}
					/*���� ���ϻ�� */
					else if(data.getHeader().getMenu()==GAL) {
						Long groupid = (Long)data.getObject();
						ArrayList <String> images = sDao.selectimagecontents(groupid);
						Galmessage galmessage = new Galmessage(groupid,images);
						Header header = new Header(GAL,0);
						Data sendData = new Data(header,galmessage);
						oos.writeObject(sendData);
						oos.flush();
					}
					/* ���� ���ϻ�� */
					else if(data.getHeader().getMenu()==AMEM) {
						Long groupid = (Long)data.getObject();
						ArrayList<String> memadd_avail = sDao.memberavailable(connectId,groupid);
						Header header = new Header(AMEM,0);
						Amemessage amem = new Amemessage(groupid,memadd_avail);
						Data sendData = new Data(header,amem);
						oos.writeObject(sendData);
						oos.flush();
						}
					
					/* ���� ���ϻ�� */
					else if(data.getHeader().getMenu()==MEM) {
						ChatMember member = (ChatMember)data.getObject();
						byte type =sDao.typechk(member.getGroupid());
						if(type == 0x01) {
							String[] friends = sDao.selectmember(connectId,member.getGroupid(),member.getUserid());
							if(friends != null) {
								Long groupid = sDao.createGroupRoom(connectId,friends); // ä�ù� ����
								if(groupid != null)
									groupidClientMap.put(groupid, new HashMap<String,ObjectOutputStream>());
								System.out.println("CREATEROOM select �� �׷���̵� : " + groupid);
								Header header = new Header(CREATEGROUPROOM,0);
								Data sendData = new Data(header,groupid);
								oos.writeObject(sendData);
								oos.flush();
							}
						}else {
							if(sDao.memberInsert(connectId, member.getUserid(),member.getGroupid())) {
								System.out.println("������ �Ϸ�");
							}
						}
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
	
	class rpReceiver extends Thread{
		private ServerDAO sDao;
		private Socket socket;
		private ObjectInputStream ois;
		private String connectId;
		public rpReceiver(Socket socket) {
			try {
				sDao = new ServerDAO();
				this.socket = socket;
				ois = new ObjectInputStream(socket.getInputStream());
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
					if(data.getHeader().getMenu() == LOGIN) {
						User user = (User)data.getObject();
						user = sDao.login(user.getUserid(),user.getPassword());
						if(user != null) {
							connectId = user.getUserid().toLowerCase(); // serverBack�� connectId�� �����ڷ�
						}

					}
					/* �輺�� ���ϻ�� */
					else if(data.getHeader().getMenu() == UPDATELASTREAD) {
						Chat message = (Chat)data.getObject();
						int result = sDao.updatereadtime(connectId,message);
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
	
	class rpmReceiver extends Thread{
		private ServerSocket socket;
		public rpmReceiver(ServerSocket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					socket2 = socket.accept();
					rpReceiver rpreceiver = new rpReceiver(socket2);
					rpreceiver.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

