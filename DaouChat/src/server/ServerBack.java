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
	public static final byte STX = 0x02; // ��� ����
	public static final byte ETX = 0x03; // ��� ��
	public static final byte SIGNUP = 0x01; // ȸ������
	public static final byte LOGIN = 0x02; // �α���
	public static final byte MSG = 0x03; // �Ϲݸ޽���
	public static final byte FRIFIND = 0x04; // ģ��ã��
	public static final byte ADDFRI = 0x05; // ģ���߰�
	public static final byte FRILIST = 0x06; // ģ�����
	public static final byte MESSAGE = 0x07; // �޽�����
	public static final byte CREATEGROUP = 0x08; // �׷����
	
	private ServerSocket serverSocket; // ��������
	private Socket socket; // �޾ƿ� ����
	/* ���� �������� ����ڵ��� ���� */
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
			serverSocket = new ServerSocket(1993); // ���� ���� ����
			System.out.println("---���� ����---");
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
	
	public synchronized int increment() {
		return non_login_increment++;
		
	}
	
	/* ���������� �ʿ� �߰� */
	public void addClient(String id, DataOutputStream os) {
		currentClientMap.put(id, os);
	}
	
	/* ������ ����� Ŭ���̾�Ʈ�� ������ ���� ��� */
	class Receiver extends Thread{
		private DataInputStream is;
		private DataOutputStream os;
		String connectId = "GM" + increment();
		public Receiver(Socket socket) {
			try {
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
				addClient(connectId,os);
				System.out.println("���ù� ����");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while(is != null) { // ���ŵ� �����Ͱ� ������
					byte[] reciveData = null;
					byte[] headerBuffer = new byte[6];
					is.read(headerBuffer);
					
					/* ȸ������ */
					if(headerBuffer[1] == SIGNUP) {
						System.out.println("ȸ������");
						byte[] lengthChk = new byte[4]; // �����ͱ���
						lengthChk[0] = headerBuffer[2];
						lengthChk[1] = headerBuffer[3];
						lengthChk[2] = headerBuffer[4];
						lengthChk[3] = headerBuffer[5];
						int datalength = byteArrayToInt(lengthChk);
						System.out.println("�����ͱ��� : " + datalength);
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						int read;
						reciveData = new byte[datalength]; 
						
						// ���� ���������� ��� 
						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
							buffer.write(reciveData,0,read);
							datalength -= read;
							if(datalength <= 0) { // �� ������ break
								break;
							}
						}
						System.out.println(buffer.toString("UTF-8"));
						String data[] = buffer.toString("UTF-8").split(",");
						
						buffer.flush();
						System.out.println("data1�� ũ��� : " +data[0].length());
						int chk = sDao.signUp(data[0],data[1]);
						os.writeInt(chk);
						
					}// ȸ������ END
					
					/* �α��� */
					else if(headerBuffer[1] == LOGIN) {
						System.out.println("�α���");
						
						byte[] lengthChk = new byte[4]; // �����ͱ���
						lengthChk[0] = headerBuffer[2];
						lengthChk[1] = headerBuffer[3];
						lengthChk[2] = headerBuffer[4];
						lengthChk[3] = headerBuffer[5];
						int datalength = byteArrayToInt(lengthChk);
						System.out.println("�����ͱ��� : " + datalength);
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						int read;
						reciveData = new byte[datalength]; 
						
						// ���� ���������� ���
						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
							buffer.write(reciveData,0,read);
							datalength -= read;
							if(datalength <= 0) { // �� ������ break
								break;
							}
						}
						
						System.out.println(buffer.toString("UTF-8"));
						String data[] = buffer.toString("UTF-8").split(",");
						
						buffer.flush();
						int chk = sDao.login(data[0],data[1]);
						if(chk > 0) {
							connectId = data[0];
							currentClientMap.put(data[0], currentClientMap.remove(connectId)); // �ӽþ��̵� �α��� ���̵�� ����
						}
						os.writeInt(chk);
					}// �α��� END
					
					/* ģ�� ã��(��ü ���) */
					else if(headerBuffer[1] == FRIFIND) {
						System.out.println(connectId + "�� ģ����� �޷�");
						Object rowData[][] = sDao.friFind(connectId); // ģ����� int , String, String(4+20+20) 44
						int bodylength = rowData.length*44;
						
						byte sendData[] = new byte[6 + bodylength];
						
						sendData[0] = STX; // ����?
						sendData[1] = FRIFIND; // ģ��ã��
						byte[] bodySize = intToByteArray(bodylength);
						for (int i = 0; i < bodySize.length; i++) {
							sendData[2+i] = (byte)bodySize[i];
						} // ���� ������ ũ�� // ���⼱ totalUserCnt
						
						byte body[] = new byte[bodylength];
						int readcnt = 0;
						for (int i = 0; i < rowData.length; i++) {
							System.out.println("���̵� : " + (String)rowData[i][1]);
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
							//�� 44byte �� �ݺ�
						}
						
						System.arraycopy(body, 0, sendData, 6, body.length);
						
						os.write(sendData);
						
					}// ģ�� ã�� END
					 
					/* ADDFRI */
					else if(headerBuffer[1] == ADDFRI) {
						System.out.println(connectId + "�� ģ���߰� �ش޷�");
						byte[] lengthChk = new byte[4]; // �����ͱ���
						lengthChk[0] = headerBuffer[2];
						lengthChk[1] = headerBuffer[3];
						lengthChk[2] = headerBuffer[4];
						lengthChk[3] = headerBuffer[5];
						int datalength = byteArrayToInt(lengthChk);
						System.out.println("�����ͱ��� : " + datalength);
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						int read;
						reciveData = new byte[datalength]; 
						
						// ���� ���������� ���
						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
							buffer.write(reciveData,0,read);
							datalength -= read;
							if(datalength <= 0) { // �� ������ break
								break;
							}
						}
						
						System.out.println(buffer.toString("UTF-8"));
						String data = buffer.toString("UTF-8");
						
						buffer.flush();
						int chk = sDao.addfri(connectId,data);
						
						os.writeInt(chk);
						
					}/* ADDFRI END */
					
					/* ģ�� ��� */
					else if(headerBuffer[1] == FRILIST) {
						System.out.println(connectId + "�� ģ����� �޷�");
						Object rowData[][] = sDao.friList(connectId); // ģ����� int , String, String(4+20+20) 44
						int bodylength = rowData.length*44;
						
						byte sendData[] = new byte[6 + bodylength];
						
						sendData[0] = STX; // ����?
						sendData[1] = FRIFIND; // ģ��ã��
						byte[] bodySize = intToByteArray(bodylength);
						for (int i = 0; i < bodySize.length; i++) {
							sendData[2+i] = (byte)bodySize[i];
						} // ���� ������ ũ�� // ���⼱ totalUserCnt
						
						byte body[] = new byte[bodylength];
						int readcnt = 0;
						for (int i = 0; i < rowData.length; i++) {
							System.out.println("���̵� : " + (String)rowData[i][1]);
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
							//�� 44byte �� �ݺ�
						}
						
						System.arraycopy(body, 0, sendData, 6, body.length);
						
						os.write(sendData);
						
					}// ģ�� ��� END
					
//					/* �޽��� ���� */
//					else if(headerBuffer[1] == MESSAGE) {
//						System.out.println("�޽��� ����");
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
//						String data = buffer.toString("UTF-8");
//						
//						buffer.flush();
//						int chk = sDao.message(data); // �޽��� DB����
//						// �޽����� ����!
//						if(chk > 0) {
//							connectId = data[0];
//						}
//						os.writeInt(chk);
//					}// �޽��� END
					
					/* äƼ�� ���� */
					else if(headerBuffer[1] == CREATEGROUP) {
						System.out.println("ä�ù� ����");
						
						byte[] lengthChk = new byte[4]; // �����ͱ���
						lengthChk[0] = headerBuffer[2];
						lengthChk[1] = headerBuffer[3];
						lengthChk[2] = headerBuffer[4];
						lengthChk[3] = headerBuffer[5];
						int datalength = byteArrayToInt(lengthChk);
						System.out.println("�����ͱ��� : " + datalength);
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						int read;
						reciveData = new byte[datalength]; 
						
						// ���� ���������� ���
						while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
							buffer.write(reciveData,0,read);
							datalength -= read;
							if(datalength <= 0) { // �� ������ break
								break;
							}
						}
						
						System.out.println(buffer.toString("UTF-8"));
						String data = buffer.toString("UTF-8");
						
						buffer.flush();
						int chk = sDao.createGroup(connectId,data);
						if(chk > 0) {
							System.out.println("ä�ù� ���� ����");
						}else {
							System.out.println("ä�ù� ���� ����");
						}
						os.writeInt(chk);
					}// ä�ù氳�� END
					
				}
			}catch (SocketException e) {
				System.out.println("Ŭ���̾�Ʈ ����");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

