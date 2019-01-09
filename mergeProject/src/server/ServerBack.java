package server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
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
	public static final byte FMSG = 0x06;// ����, �̹��� ����
	public static final byte FRILIST = 0x09; // ģ�����
	public static final byte MESSAGE = 0x07; // �޽�����
	public static final byte CREATEGROUP = 0x08; // �׷����
	
	private ServerSocket serverSocket; // ��������
	private ServerSocket fileserverSocket;

	private Socket socket; // �޾ƿ� ����
	private Socket filesocket;

	/* ���� �������� ����ڵ��� ���� */
	private Map<String, DataOutputStream> currentClientMap = new HashMap<String, DataOutputStream>();
	private Map<String, DataOutputStream> currentClientfileMap = new HashMap<String, DataOutputStream>();

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

	private void broadcast(String data, List<String> groupmember) {
		synchronized (currentClientMap) {
			try {
				int bodylength = data.getBytes("UTF-8").length;
				byte sendData[] = new byte[6 + bodylength];// ��ü ���� ������(broad cast)
				// ���

				sendData[0] = STX;
				sendData[1] = MSG;
				byte[] bodySize = intToByteArray(bodylength);
				System.out.println("���� ������ ũ�� : " + bodylength);
				for (int i = 0; i < bodySize.length; i++) {
					sendData[2 + i] = (byte) bodySize[i];
				}
				byte body[] = new byte[bodylength];
				body = data.getBytes("UTF-8");
				System.arraycopy(body, 0, sendData, 6, body.length);
				
				DataOutputStream os;
				for (int i = 0; i < groupmember.size(); i++) {
					os = currentClientMap.get(groupmember.get(i));
					System.out.println(os);
					if(os != null) {
						os.write(sendData);
						os.flush();
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
						
						/* ���� buffer�� ����ߴµ� reciveData�� �ű�� buffer ���� �ص� ��(���� read�� ���ԵǸ� error�� �ȳ����� ����) */
						System.out.println(buffer.toString("UTF-8"));
						String data[] = buffer.toString("UTF-8").split(",");
						
						buffer.flush();
						System.out.println("data1�� ũ��� : " +data[0].length());
						int chk = sDao.signUp(data[0],data[1]);
						
						
						// ���������� ����
						int bodylength = 6 + Integer.BYTES;
						byte sendData[] = new byte[6+bodylength]; // ��ü ���� ������
						sendData[0] = STX; // ����?
						sendData[1] = SIGNUP; // ȸ������
						byte[] bodySize = intToByteArray(Integer.BYTES);
						System.out.println("���� �������� ũ�� : " + bodylength);
						for (int i = 0; i < bodySize.length; i++) {
							sendData[2+i] = (byte)bodySize[i];
						} // ���� ������ ũ��
						byte body[] = new byte[Integer.BYTES];
						body = intToByteArray(chk);
						
						System.arraycopy(body, 0, sendData, 6, body.length);
						
						os.write(sendData);
						
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
						
						// ���������� ����
						int bodylength = 6 + Integer.BYTES;
						byte sendData[] = new byte[6+bodylength]; // ��ü ���� ������
						sendData[0] = STX; // ����?
						sendData[1] = LOGIN; // �α���
						byte[] bodySize = intToByteArray(Integer.BYTES);
						System.out.println("���� �������� ũ�� : " + bodylength);
						for (int i = 0; i < bodySize.length; i++) {
							sendData[2+i] = (byte)bodySize[i];
						} // ���� ������ ũ��
						byte body[] = new byte[Integer.BYTES];
						body = intToByteArray(chk);
						
						System.arraycopy(body, 0, sendData, 6, body.length);
						
						if(chk > 0) {
							currentClientMap.put(data[0], currentClientMap.remove(connectId)); // �ӽþ��̵� �α��� ���̵�� ����
							connectId = data[0]; // serverBack�� connectId�� �����ڷ�
							System.out.println("�α����� �����ڼ� : " + currentClientMap.size());
						}
						
						os.write(sendData);
					}// �α��� END
					
					/* ģ�� ã��(��ü ���) */
					else if(headerBuffer[1] == FRIFIND) {
						System.out.println(connectId + "�� ��� ģ����� �޷�");
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
						
						// ���������� ����
						int bodylength = 6 + Integer.BYTES;
						byte sendData[] = new byte[6+bodylength]; // ��ü ���� ������
						sendData[0] = STX; // ����?
						sendData[1] = ADDFRI; // ģ���߰�
						byte[] bodySize = intToByteArray(Integer.BYTES);
						System.out.println("���� �������� ũ�� : " + bodylength);
						for (int i = 0; i < bodySize.length; i++) {
							sendData[2+i] = (byte)bodySize[i];
						} // ���� ������ ũ��
						byte body[] = new byte[Integer.BYTES];
						body = intToByteArray(chk);
						
						System.arraycopy(body, 0, sendData, 6, body.length);
						
						os.write(sendData);
						
					}/* ADDFRI END */
					
					/* ģ�� ��� */
					else if(headerBuffer[1] == FRILIST) {
						System.out.println(connectId + "�� ģ����� �޷�");
						Object rowData[][] = sDao.friList(connectId); // ģ����� int , String, String(4+20+20) 44
						int bodylength = rowData.length*44;
						
						byte sendData[] = new byte[6 + bodylength];
						
						sendData[0] = STX; // ����?
						sendData[1] = FRILIST; // ģ�� ���
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
						String data[] = buffer.toString("UTF-8").split(",");
						
						buffer.flush();
						int chk = sDao.createGroup(connectId,data); // ä�ù� ����
						String groupid = sDao.selectGroupid(connectId,data); // groupid ��������
						
						
						//����ä�ó��� ��������
						
						List<String> chatcontent = sDao.selectchatcontent(groupid);
						int Listsize = 0;
						if(chatcontent.size() > 0 ) {
							for (int i = 0; i < chatcontent.size(); i++) {
								System.out.println(chatcontent.get(i));
								Listsize += chatcontent.get(i).getBytes("UTF-8").length;
							}
							Listsize += (chatcontent.size() -1); // ������ 
						}
						// ���������� ����
						int bodylength = 84; // result(4byte) + groupid(80byte)
						byte sendData[] = new byte[6+ bodylength + Listsize]; // ��ü ���� ������
						sendData[0] = STX; // ����?
						sendData[1] = CREATEGROUP; // ä�ù� ����
						byte[] bodySize = intToByteArray((bodylength + Listsize));
						System.out.println("ä�ù� ������ ���� �������� ũ�� : " + (bodylength + Listsize));
						for (int i = 0; i < bodySize.length; i++) {
							sendData[2+i] = (byte)bodySize[i];
						} // ���� ������ ũ��
						
						byte body[] = new byte[bodylength];
						int groupidlength = groupid.getBytes("UTF-8").length;
						System.out.println("--groupidlength--" + groupidlength);
						byte[] result = intToByteArray(chk);
						for (int i = 0; i < result.length; i++) {
							body[i] = (byte)result[i];
						}// body�� 4����Ʈ
						
						System.arraycopy(groupid.getBytes("UTF-8"), 0, body, 4, groupidlength);
						System.arraycopy(new byte[80 - groupidlength], 0, body, 4 + groupidlength, 80 - groupidlength);
						System.arraycopy(body, 0, sendData, 6, bodylength);
						
						if(chatcontent.size() > 0 ) {
							int cursor = 6 + bodylength; // 90
							int i = 0;
							for (; i < chatcontent.size() -1; i++) {
								byte[] str = (chatcontent.get(i) + "&").getBytes("UTF-8");
								System.arraycopy(str, 0, sendData, cursor, str.length);
								cursor += str.length;
							}		
							byte[] str = (chatcontent.get(i)).getBytes("UTF-8");
							System.arraycopy(str, 0, sendData, cursor, str.length);
						}
						
						if(chk > 0) {
							System.out.println("ä�ù� ���� ����");
						}else if(chk == 0){
							System.out.println("ä�ù� �̹� ����");
						}
						else {
							System.out.println("ä�ù� ���� ����");
						}
						os.write(sendData);
					}// ä�ù氳�� END
					
					// �޼��� �ޱ�
					else if (headerBuffer[1] == MSG) {
						System.out.println("�޼���");
						System.out.println(connectId + "�� �޼����� �����ϴ�.");
						byte[] lengthChk = new byte[4];
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
						while ((read = is.read(reciveData, 0, reciveData.length)) != -1) {
							buffer.write(reciveData, 0, read);
							datalength -= read;
							if (datalength <= 0) { // �� ������ break
								break;
							}
						}

						System.out.println(buffer.toString("UTF-8"));
						String data[] = buffer.toString("UTF-8").split(",");
						
						String sendUserid = data[0]; // ���̵�
						String sendGroupid = data[1]; // �׷���̵�
						String sendMsg = data[2]; // msg // msg�� "" �϶� ����

						buffer.flush();
						
						// ä�ó��� ������ ����
						int chk = sDao.insertMSG(sendUserid,sendGroupid,sendMsg);
						// groupid�� ��������� ��ȸ
						List<String> groupmember = sDao.selectGroupmember(sendGroupid);
						for (int i = 0; i < groupmember.size(); i++) {
							System.out.println(groupmember.get(i));
						}
						// currentMap���� ��ġ�Ǵ� ��� ��ȸ
						// Ŭ���̾�Ʈ�� ����
						
						broadcast(data[0] + "," + data[1] + "," + data[2] , groupmember);

					}// �޼��� �ޱ� END
					
					//���� �޼���
					else if(headerBuffer[1]==FMSG) {
						System.out.println(connectId + "�� ������ �����ϴ�");
						byte[] lengthChk = new byte[4];
						lengthChk[0] = headerBuffer[2];
						lengthChk[1] = headerBuffer[3];
						lengthChk[2] = headerBuffer[4];
						lengthChk[3] = headerBuffer[5];
						int datalength = byteArrayToInt(lengthChk);
						System.out.println("���� ������ ����: " + datalength);
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						int read;
						reciveData = new byte[datalength];

						// ���� ���������� ���
						while ((read = is.read(reciveData, 0, reciveData.length)) != -1) {
							buffer.write(reciveData, 0, read);
							datalength -= read;
							if (datalength <= 0) { // �� ������ break
								break;
							}
						}

						System.out.println(buffer.toString("UTF-8"));
						String data[] = buffer.toString("UTF-8").split(",");
						new ServerFileThread(connectId).start();
					}// ���ϸ޼��� END
					
				}
			}catch (SocketException e) {
				currentClientMap.remove(connectId);
				System.out.println(connectId + "���� Ŭ���̾�Ʈ ����");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

