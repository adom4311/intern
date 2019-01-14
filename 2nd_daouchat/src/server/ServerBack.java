package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import model.dao.ServerDAO;
import model.vo.Chat;
import model.vo.ChatMember;
import model.vo.Data;
import model.vo.Header;
import model.vo.User;

public class ServerBack {
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
	public static final int GROUPROOMLIST = 11; // ä�ù� ������ ģ�����
	public static final int UPDATELASTREAD = 12; // �����ð� ����

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

	private void broadcast(Chat message, List<String> groupmember) {
		synchronized (currentClientMap) {
			try {
				Header header = new Header(MSG,0); // ������ũ�Ⱑ ���ó�� ����.
				Data sendData = new Data(header,message);
				ObjectOutputStream oos;
				for (String member : groupmember) {
					oos = currentClientMap.get(member);
					if(oos != null) {
						oos.writeObject(sendData);
						oos.flush();
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setting() {
		try {
			serverSocket = new ServerSocket(1993); // ���� ���� ����
			fileserverSocket = new ServerSocket(1994);

			System.out.println("---���� ����---");

			OldDataDelete odd = new OldDataDelete(this);
			Timer scheduler = new Timer();
			scheduler.scheduleAtFixedRate(odd, 6000000, 172800000); // 1�� �ĺ��� 2�� ���� (2~3�� ������ ����)
			
			while(true) {
				socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���� ����
				filesocket=fileserverSocket.accept();
				System.out.println(socket.getInetAddress() + "���� ����"); // IP
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
	
	/* ���������� �ʿ� �߰� */
	public void addClient(String id, ObjectOutputStream oos, DataOutputStream fos) {
		currentClientMap.put(id, oos);
		currentClientfileMap.put(id, fos);
	}
	
	/* ������ ����� Ŭ���̾�Ʈ�� ������ ���� ��� */
	class Receiver extends Thread{
		private ServerDAO sDao;
		private DataInputStream is;
		private DataOutputStream os;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private DataInputStream fis;
		private DataOutputStream fos;
		private ServerBack serverback; 
		private Socket socket;
		String connectId = "GM" + increment();
		
		public Receiver(Socket socket) {
			try {
				sDao = new ServerDAO();
				this.socket = socket;
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
				fis = new DataInputStream(filesocket.getInputStream());
				fos = new DataOutputStream(filesocket.getOutputStream());
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
					if(data.getHeader().getMenu() == SIGNUP) {
						User user = (User)data.getObject();
						int result = sDao.signUp(user.getUserid(),user.getPassword());
						Header header = new Header(SIGNUP,0); // ������ũ�Ⱑ ���ó�� ����.
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == LOGIN) {
						User user = (User)data.getObject();
						int result = sDao.login(user.getUserid(),user.getPassword());
						Header header = new Header(LOGIN,0); // ������ũ�Ⱑ ���ó�� ����.
						Data sendData = new Data(header,result);
						
						if(result > 0) {
							currentClientMap.put(user.getUserid(), currentClientMap.remove(connectId)); // �ӽþ��̵� �α��� ���̵�� ����
							currentClientfileMap.put(user.getUserid(),currentClientfileMap.remove(connectId));
							connectId = user.getUserid(); // serverBack�� connectId�� �����ڷ�
							System.out.println("�α����� �����ڼ� : " + currentClientMap.size());
						}
						
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == FRIFIND) {
						Object rowData[][] = sDao.friFind(connectId);
						Header header = new Header(FRIFIND,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == ADDFRI) {
						String friendId = (String)data.getObject();
						int result = sDao.addfri(connectId,friendId);
						Header header = new Header(ADDFRI,0); // ������ũ�Ⱑ ���ó�� ����.
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == FRILIST) {
						Object rowData[][] = sDao.friList(connectId);
						Header header = new Header(FRILIST,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == CREATEROOM) {
						String[] friendids = (String[])data.getObject();
						int result = sDao.createRoom(connectId,friendids); // ä�ù� ����
						Long groupid = sDao.selectRoom(connectId,friendids,ONEROOM); // groupid
						System.out.println("CREATEROOM select �� �׷���̵� : " + groupid);
						Header header = new Header(CREATEROOM,0);
						Data sendData = new Data(header,groupid);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == ROOM) {
						Object rowData[][] = sDao.roomList(connectId);
						Header header = new Header(ROOM,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == MSG) {
						Chat message = (Chat)data.getObject();
						Chat chat = sDao.insertMSG(message);
						List<String> groupmember = sDao.selectGroupmember(chat.getGroupid());
						broadcast(chat, groupmember);
					}
					else if(data.getHeader().getMenu() == FMSG) {
					}
					else if(data.getHeader().getMenu() == OPENCHAT) {
						ChatMember chatmember = (ChatMember)data.getObject();
						List<Chat> chatcontent = sDao.selectchatcontent(chatmember);
						Header header = new Header(OPENCHAT,0);
						Data sendData = new Data(header,chatcontent);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == GROUPROOMLIST) {
						Object rowData[][] = sDao.friList(connectId);
						Header header = new Header(GROUPROOMLIST,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == UPDATELASTREAD) {
						Chat message = (Chat)data.getObject();
						int result = sDao.updatereadtime(connectId,message);
					}
				}
				
//					//���� �޼���
//					else if(headerBuffer[1]==FMSG) {
//						System.out.println(connectId + "�� ������ �����ϴ�");
//						byte[] lengthChk = new byte[4];
//						lengthChk[0] = headerBuffer[2];
//						lengthChk[1] = headerBuffer[3];
//						lengthChk[2] = headerBuffer[4];
//						lengthChk[3] = headerBuffer[5];
//						int datalength = byteArrayToInt(lengthChk);
//						System.out.println("���� ������ ����: " + datalength);
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
//
//						System.out.println(buffer.toString("UTF-8"));
//						String data[] = buffer.toString("UTF-8").split(",");
//						new ServerFileThread(connectId,data[1],data[2],sDao,currentClientMap,currentClientfileMap,filesocket).start();
//					}// ���ϸ޼��� END
			}catch (SocketException e) {
				try {
					currentClientMap.remove(connectId);
					socket.close();
					System.out.println(connectId + "���� Ŭ���̾�Ʈ ����");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

