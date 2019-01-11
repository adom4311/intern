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

import model.dao.ServerDAO;
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
	public static final int CREATEGROUP = 7; // �׷����
	public static final int FRILIST = 8; // ģ�����
	public static final int OPENCHAT = 9; // �׷����
	public static final int ROOM = 10; //ä�ù���
	
	private ServerSocket serverSocket; // ��������
	private ServerSocket fileserverSocket;

	private Socket socket; // �޾ƿ� ����
	private Socket filesocket;

	private DataInputStream is;
	private DataOutputStream os;
	private DataInputStream fis;
	private DataOutputStream fos;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	String connectId;

	/* ���� �������� ����ڵ��� ���� */
	private Map<String, DataOutputStream> currentClientMap = new HashMap<String, DataOutputStream>();
	private Map<String, DataOutputStream> currentClientfileMap = new HashMap<String, DataOutputStream>();

	private int non_login_increment = 0; // �α��� �� �ӽð�
    private ServerDAO sDao;
	
	public ServerDAO getsDao() {
		return sDao;
	}
	public ObjectInputStream getOis() {
		return ois;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}
	
	public Map<String, DataOutputStream> getCurrentClientMap() {
		return currentClientMap;
	}
	public void setCurrentClientMap(Map<String, DataOutputStream> currentClientMap) {
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

	private void broadcast(String data, List<String> groupmember) {
//		synchronized (currentClientMap) {
//			try {
//				int bodylength = data.getBytes("UTF-8").length;
//				byte sendData[] = new byte[6 + bodylength];// ��ü ���� ������(broad cast)
//				// ���
//
//				sendData[0] = STX;
//				sendData[1] = MSG;
//				byte[] bodySize = intToByteArray(bodylength);
//				System.out.println("���� ������ ũ�� : " + bodylength);
//				for (int i = 0; i < bodySize.length; i++) {
//					sendData[2 + i] = (byte) bodySize[i];
//				}
//				byte body[] = new byte[bodylength];
//				body = data.getBytes("UTF-8");
//				System.arraycopy(body, 0, sendData, 6, body.length);
//				
//				DataOutputStream os;
//				for (int i = 0; i < groupmember.size(); i++) {
//					os = currentClientMap.get(groupmember.get(i));
//					System.out.println("������������ os " + os);
//					if(os != null) {
//						os.write(sendData);
//						os.flush();
//					}
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
	
	public void setting() {
		try {
			sDao = new ServerDAO();
			serverSocket = new ServerSocket(1993); // ���� ���� ����
			fileserverSocket = new ServerSocket(1994);

			System.out.println("---���� ����---");
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
	public void addClient(String id, DataOutputStream os, DataOutputStream fos) {
		currentClientMap.put(id, os);
		currentClientfileMap.put(id, fos);
	}
	
	/* ������ ����� Ŭ���̾�Ʈ�� ������ ���� ��� */
	class Receiver extends Thread{
		private ServerBack serverback; 
		private Socket socket;
		String connectId = "GM" + increment();
		
		public ServerBack getServerback() {
			return serverback;
		}

		public void setServerback(ServerBack serverback) {
			this.serverback = serverback;
		}

		public Socket getSocket() {
			return socket;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}

		public String getConnectId() {
			return connectId;
		}

		public void setConnectId(String connectId) {
			this.connectId = connectId;
		}

		
		public Receiver(Socket socket) {
			try {
				this.socket = socket;
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
				fis = new DataInputStream(filesocket.getInputStream());
				fos = new DataOutputStream(filesocket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				
				addClient(connectId,os,fos);
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
					else if(data.getHeader().getMenu() == CREATEGROUP) {
						String[] friendids = (String[])data.getObject();
						int result = sDao.createGroup(connectId,friendids); // ä�ù� ����
						String groupid = sDao.selectGroupid(connectId,friendids); // groupid
						Header header = new Header(CREATEGROUP,0);
						Data sendData = new Data(header,groupid);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == ROOM) {
					}
					else if(data.getHeader().getMenu() == MSG) {
					}
					else if(data.getHeader().getMenu() == FMSG) {
					}
					else if(data.getHeader().getMenu() == OPENCHAT) {
						String groupid = (String)data.getObject();
						List<String[]> chatcontent = sDao.selectchatcontent(groupid);
						Header header = new Header(OPENCHAT,0);
						Data sendData = new Data(header,chatcontent);
						oos.writeObject(sendData);
						oos.flush();
					}
				}
//					/* äƼ�� ���� */
//					else if(headerBuffer[1] == CREATEGROUP) {
//						System.out.println("ä�ù� ����");
//						
//						byte[] lengthChk = new byte[4]; // �����ͱ���
//						lengthChk[0] = headerBuffer[2];
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
//						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//							buffer.write(reciveData,0,read);
//							datalength -= read;
//							if(datalength <= 0) { // �� ������ break
//								break;
//							}
//						}
//						
//						System.out.println(buffer.toString("UTF-8"));
//						String data[] = buffer.toString("UTF-8").split(",");
//						
//						buffer.flush();
//						int chk = sDao.createGroup(connectId,data); // ä�ù� ����
//						String groupid = sDao.selectGroupid(connectId,data); // groupid ��������
//						
//						//����ä�ó��� ��������
//						
//						List<String> chatcontent = sDao.selectchatcontent(groupid);
//						int Listsize = 0;
//						if(chatcontent.size() > 0 ) {
//							for (int i = 0; i < chatcontent.size(); i++) {
//								System.out.println(chatcontent.get(i));
//								Listsize += chatcontent.get(i).getBytes("UTF-8").length;
//							}
//							Listsize += (chatcontent.size() -1); // ������ 
//						}
//						// ���������� ����
//						int bodylength = 84; // result(4byte) + groupid(80byte)
//						byte sendData[] = new byte[6+ bodylength + Listsize]; // ��ü ���� ������
//						sendData[0] = STX; // ����?
//						sendData[1] = CREATEGROUP; // ä�ù� ����
//						byte[] bodySize = intToByteArray((bodylength + Listsize));
//						System.out.println("ä�ù� ������ ���� �������� ũ�� : " + (bodylength + Listsize));
//						for (int i = 0; i < bodySize.length; i++) {
//							sendData[2+i] = (byte)bodySize[i];
//						} // ���� ������ ũ��
//						
//						byte body[] = new byte[bodylength];
//						int groupidlength = groupid.getBytes("UTF-8").length;
//						System.out.println("--groupidlength--" + groupidlength);
//						byte[] result = intToByteArray(chk);
//						for (int i = 0; i < result.length; i++) {
//							body[i] = (byte)result[i];
//						}// body�� 4����Ʈ
//						
//						System.arraycopy(groupid.getBytes("UTF-8"), 0, body, 4, groupidlength);
//						System.arraycopy(new byte[80 - groupidlength], 0, body, 4 + groupidlength, 80 - groupidlength);
//						System.arraycopy(body, 0, sendData, 6, bodylength);
//						
//						if(chatcontent.size() > 0 ) {
//							int cursor = 6 + bodylength; // 90
//							int i = 0;
//							for (; i < chatcontent.size() -1; i++) {
//								byte[] str = (chatcontent.get(i) + "&").getBytes("UTF-8");
//								System.arraycopy(str, 0, sendData, cursor, str.length);
//								cursor += str.length;
//							}		
//							byte[] str = (chatcontent.get(i)).getBytes("UTF-8");
//							System.arraycopy(str, 0, sendData, cursor, str.length);
//						}
//						
//						if(chk > 0) {
//							System.out.println("ä�ù� ���� ����");
//						}else if(chk == 0){
//							System.out.println("ä�ù� �̹� ����");
//						}
//						else {
//							System.out.println("ä�ù� ���� ����");
//						}
//						os.write(sendData);
//					}// ä�ù氳�� END
//					//ä�ù� ��� START
//					else if(headerBuffer[1]==ROOM) {
//						System.out.println(connectId + "�� �� �ִ� ���� �޷�");
//						Object rowData[][] = sDao.roomList(connectId); // ģ����� int , String(4+80) 84
//						int bodylength = rowData.length*84;
//						
//						byte sendData[] = new byte[6 + bodylength];
//						
//						sendData[0] = STX; // ����?
//						sendData[1] = ROOM; // ä�ù� ���
//						byte[] bodySize = intToByteArray(bodylength);
//						for (int i = 0; i < bodySize.length; i++) {
//							sendData[2+i] = (byte)bodySize[i];
//						} // ���� ������ ũ�� // ���⼱ totalUserCnt
//						
//						byte body[] = new byte[bodylength];
//						int readcnt = 0;
//						for (int i = 0; i < rowData.length; i++) {
//							System.out.println("ä�ù� : " + (String)rowData[i][1]);
//							byte roomname[] = String.valueOf(rowData[i][1]).getBytes("UTF-8");
//							int roomnamelength = roomname.length;
//							System.arraycopy(intToByteArray((int)rowData[i][0]), 0, body, readcnt, 4);
//							readcnt += 4;
//							System.arraycopy(roomname, 0, body, readcnt, roomnamelength);
//							readcnt += roomnamelength;
//							System.arraycopy(new byte[80 - roomnamelength], 0, body, readcnt, 80 - roomnamelength);
//							readcnt += 80 - roomnamelength;
//							
//							//�� 84byte �� �ݺ�
//						}
//						
//						System.arraycopy(body, 0, sendData, 6, body.length);
//						
//						os.write(sendData);
//					}
//					// �޼��� �ޱ�
//					else if (headerBuffer[1] == MSG) {
//						System.out.println("�޼���");
//						System.out.println(connectId + "�� �޼����� �����ϴ�.");
//						byte[] lengthChk = new byte[4];
//						lengthChk[0] = headerBuffer[2];
//						lengthChk[1] = headerBuffer[3];
//						lengthChk[2] = headerBuffer[4];
//						lengthChk[3] = headerBuffer[5];
//						int datalength = byteArrayToInt(lengthChk);
//						System.out.println("�����ͱ��� : " + datalength);		
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
//						
//						String sendUserid = data[0]; // ���̵�
//						String sendGroupid = data[1]; // �׷���̵�
//						String sendMsg = data[2]; // msg // msg�� "" �϶� ����
//
//						buffer.flush();
//						
//						// ä�ó��� ������ ����
//						int chk = sDao.insertMSG(sendUserid,sendGroupid,sendMsg);
//						// groupid�� ��������� ��ȸ
//						List<String> groupmember = sDao.selectGroupmember(sendGroupid);
//						for (int i = 0; i < groupmember.size(); i++) {
//							System.out.println(groupmember.get(i));
//						}
//						// currentMap���� ��ġ�Ǵ� ��� ��ȸ
//						// Ŭ���̾�Ʈ�� ����
//						
//						broadcast(data[0] + "," + data[1] + "," + data[2] , groupmember);
//
//					}// �޼��� �ޱ� END
//					
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
//					
//					/* openCHAT */
//					else if(headerBuffer[1] == OPENCHAT) {
//						System.out.println("ä�ù� ����");
//						
//						byte[] lengthChk = new byte[4]; // �����ͱ���
//						lengthChk[0] = headerBuffer[2];
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
//						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//							buffer.write(reciveData,0,read);
//							datalength -= read;
//							if(datalength <= 0) { // �� ������ break
//								break;
//							}
//						}
//						System.out.println(buffer.toString("UTF-8"));
//						reciveData = buffer.toByteArray();
//						buffer.flush();
//						
//						List<String> chatcontent = sDao.selectchatcontent(new String(reciveData,"UTF-8"));
//						int bodylength = 0;
//						if(chatcontent.size() > 0 ) {
//							for (int i = 0; i < chatcontent.size(); i++) {
//								System.out.println(chatcontent.get(i));
//								bodylength += chatcontent.get(i).getBytes("UTF-8").length;
//							}
//							bodylength += (chatcontent.size() -1); // ������ 
//						}
//						// ���������� ����
//						byte sendData[] = new byte[6+bodylength]; // ��ü ���� ������
//						sendData[0] = STX; // ����?
//						sendData[1] = OPENCHAT; // �α���
//						byte[] bodySize = intToByteArray(bodylength);
//						System.out.println("���� �������� ũ�� : " + bodylength);
//						for (int i = 0; i < bodySize.length; i++) {
//							sendData[2+i] = (byte)bodySize[i];
//						} // ���� ������ ũ��
//
//						byte body[] = new byte[bodylength];
//
//						if(chatcontent.size() > 0 ) {
//							int cursor = 6;
//							int i = 0;
//							for (; i < chatcontent.size() -1; i++) {
//								byte[] str = (chatcontent.get(i) + "&").getBytes("UTF-8");
//								System.arraycopy(str, 0, sendData, cursor, str.length);
//								cursor += str.length;
//							}		
//							byte[] str = (chatcontent.get(i)).getBytes("UTF-8");
//							System.arraycopy(str, 0, sendData, cursor, str.length);
//						}
//						os.write(sendData);
//					}// openCHAT END
//				}
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

