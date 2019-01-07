package client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientBack {
	public static final byte STX = 0x02; // ��� ����
	public static final byte ETX = 0x03; // ��� ��
	public static final byte SIGNUP = 0x01; // ȸ������
	public static final byte LOGIN = 0x02; // �α���
	public static final byte MSG = 0x03; // �Ϲݸ޽���
	public static final byte FRIFIND = 0x04; // ģ��ã��
	public static final byte ADDFRI = 0x05; // ģ���߰�
	public static final byte FRILIST = 0x06; // ģ�����
	
	private Socket socket;
	private ClientGUI gui;
	private ClientHome home;
	private DataInputStream is;
	private DataOutputStream os;
	private String SERVER_ADDR = "127.0.0.1";
	private int PORT = 1993;
	
	public void setGui(ClientGUI clientGUI) {
		this.gui = clientGUI;
	}
	
	public ClientBack() {
		connect();
	}
	
	public  int byteArrayToInt(byte bytes[]) {
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
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

	public void connect() {
		try {
			socket = new Socket(SERVER_ADDR,PORT);
			System.out.println("������ �����");
			
			is = new DataInputStream(socket.getInputStream());
			os = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void signUp(String id, String pw) {
		try {
			int bodylength = id.getBytes("UTF-8").length + pw.getBytes("UTF-8").length + 1; // ���̵� + �н����� ����Ʈ
			byte sendData[] = new byte[6+bodylength]; // ��ü ���� ������
			
			sendData[0] = STX; // ����?
			sendData[1] = SIGNUP; // ȸ������
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("���� �������� ũ�� : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // ���� ������ ũ��
			byte body[] = new byte[bodylength];
			body = (id + "," + pw).getBytes("UTF-8");
			
			System.out.println("body length" + body.length);
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("���� ������ : " + new String(body) + sendData.length);

			os.write(sendData);
			os.flush();
			
			while(is!=null) {
				int chk = is.readInt();
				if(chk > 0) {
					gui.Alert("ȸ������ ����! �α������ּ���.");
					gui.signUpInvi();
					break;
				}else {
					gui.Alert("ȸ������ ����. ���̵� �ߺ�");
					gui.signUpInvi();
					break;

				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void login(String id, String pw) {
		try {
			int bodylength = id.getBytes("UTF-8").length + pw.getBytes("UTF-8").length + 1; // ���̵� + �н����� ����Ʈ
			byte sendData[] = new byte[6+bodylength]; // ��ü ���� ������
			
			sendData[0] = STX; // ����?
			sendData[1] = LOGIN; // �α���
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("���� �������� ũ�� : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // ���� ������ ũ��
			byte body[] = new byte[bodylength];
			body = (id + "," + pw).getBytes("UTF-8");
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("���� ������ : " + new String(body) + sendData.length);

			os.write(sendData);
			os.flush();
			
			while(is!=null) {
				int chk = is.readInt();
				if(chk > 0) {
					gui.Alert("�α��� ����!");
					gui.loginInvi();
					gui.setVisible(false);
					home = new ClientHome();
					home.setuserid(id);
					home.home(this);
					break;
				}else {
					gui.Alert("�α��� ����. ���̵� �Ǵ� ��й�ȣ ����.");
					gui.loginInvi();
					break;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object[][] findFriend() {
		Object rowData[][];
		try {
			byte sendData[] = new byte[6]; // ��ü ���� ������
			
			sendData[0] = STX; // ����?
			sendData[1] = FRIFIND; // ģ��ã��
			byte[] bodySize = intToByteArray(0);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // ���� ������ ũ��
			
			os.write(sendData);
			os.flush();
			
			while(is != null) {
				byte[] reciveData = null; 
				byte[] headerBuffer = new byte[6];
				is.read(headerBuffer);
				
				/* ģ�� ã�� ��� */
				if(headerBuffer[1] == FRIFIND) {
					System.out.println("ģ�� ã��");
					byte[] lengthChk = new byte[4]; // �����ͱ���
					lengthChk[0] = headerBuffer[2];
					lengthChk[1] = headerBuffer[3];
					lengthChk[2] = headerBuffer[4];
					lengthChk[3] = headerBuffer[5];
					int datalength = byteArrayToInt(lengthChk);
					System.out.println("�����ͱ��� : " + datalength);
					
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int read;
					reciveData = new byte[datalength]; // �д� ����?
					
					// ���� ���������� ���
//					while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//						System.out.println("read : " + read);
//						buffer.write(reciveData,0,read);
//						datalength -= read;
//						if(datalength <= 0) { // �� ������ break
//							break;
//						}
//					}
										
					int start = 0;
					while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
						System.out.println("start : " + start);
						System.out.println("read : " + read);
						
						buffer.write(reciveData,0,read); // buffer�� reciveData���� ���� ..���� �ڹٲ�
						start += read;
						datalength -= read;
						System.out.println(datalength);
						if(datalength <= 0) { // �� ������ break
							break;
						}
					}
					
					
					reciveData = buffer.toByteArray(); // ����(byte...stream)�� ����� ������ ����Ʈ �迭��!
					buffer.flush(); // ����(byte...stream) ����
					
					System.out.println("ģ�� ��� �ޱ� ����");
					System.out.println("�� ���� : " + reciveData.length/44);
					System.out.println("�Ѱ���*44 : " + reciveData.length);
					byte num[] = new byte[4];
					byte friendId[] = new byte[20];
					byte friendStatus[] = new byte[20];
					rowData = new Object[reciveData.length/44][3];
					
					
					
					int cnt = 0;
					for (int i = 0; i < reciveData.length/44; i++) {
						System.arraycopy(reciveData, cnt, num, 0, 4);
						cnt += 4;
						System.arraycopy(reciveData, cnt, friendId, 0, 20);
						cnt += 20;
						System.arraycopy(reciveData, cnt, friendStatus, 0, 20);
						cnt += 20;
//						System.out.println("��ȣ : " + byteArrayToInt(num));
//						System.out.println("���̵� : " + new String(friendId,"UTF-8").trim());
//						System.out.println("��� : " + new String(friendStatus,"UTF-8").trim());
						rowData[i][0] = byteArrayToInt(num);
						rowData[i][1] = new String(friendId,"UTF-8").trim();
						rowData[i][2] = new String(friendStatus,"UTF-8").trim();
					}
					
					return rowData;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void addFriend(String friendId) {
		try {
			int bodylength = friendId.getBytes("UTF-8").length; 
			byte sendData[] = new byte[6+bodylength]; // ��ü ���� ������
			
			sendData[0] = STX; // ����?
			sendData[1] = ADDFRI; // ģ�� �߰�
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("���� �������� ũ�� : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // ���� ������ ũ��
			byte body[] = new byte[bodylength];
			body = friendId.getBytes("UTF-8");
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("���� ������ : " + new String(body) + sendData.length);

			os.write(sendData);
			os.flush();
			
			while(is!=null) {
				int chk = is.readInt();
				if(chk > 0) {
					gui.Alert("ģ���߰� ����!");
					home.getFrame().fn_addfri(this);
					break;
				}else {
					gui.Alert("�α��� ����. ���̵� �Ǵ� ��й�ȣ ����.");
					break;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object[][] friList() {
		Object rowData[][];
		try {
			byte sendData[] = new byte[6]; // ��ü ���� ������
			
			sendData[0] = STX; // ����?
			sendData[1] = FRILIST; // ģ�����
			byte[] bodySize = intToByteArray(0);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // ���� ������ ũ��
			
			os.write(sendData);
			os.flush();
			
			while(is != null) {
				byte[] reciveData = null; 
				byte[] headerBuffer = new byte[6];
				is.read(headerBuffer);
				
				/* ģ�� ã�� ��� */
				if(headerBuffer[1] == FRIFIND) {
					System.out.println("ģ�� ã��");
					byte[] lengthChk = new byte[4]; // �����ͱ���
					lengthChk[0] = headerBuffer[2];
					lengthChk[1] = headerBuffer[3];
					lengthChk[2] = headerBuffer[4];
					lengthChk[3] = headerBuffer[5];
					int datalength = byteArrayToInt(lengthChk);
					System.out.println("�����ͱ��� : " + datalength);
					
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();
					int read;
					reciveData = new byte[datalength]; // �д� ����?
					
					// ���� ���������� ���
//					while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
//						System.out.println("read : " + read);
//						buffer.write(reciveData,0,read);
//						datalength -= read;
//						if(datalength <= 0) { // �� ������ break
//							break;
//						}
//					}
										
					int start = 0;
					while((read = is.read(reciveData, 0, reciveData.length))!= -1) {
						System.out.println("start : " + start);
						System.out.println("read : " + read);
						
						buffer.write(reciveData,0,read); // buffer�� reciveData���� ���� ..���� �ڹٲ�
						start += read;
						datalength -= read;
						System.out.println(datalength);
						if(datalength <= 0) { // �� ������ break
							break;
						}
					}
					
					
					reciveData = buffer.toByteArray(); // ����(byte...stream)�� ����� ������ ����Ʈ �迭��!
					buffer.flush(); // ����(byte...stream) ����
					
					System.out.println("ģ�� ��� �ޱ� ����");
					System.out.println("�� ���� : " + reciveData.length/44);
					System.out.println("�Ѱ���*44 : " + reciveData.length);
					byte num[] = new byte[4];
					byte friendId[] = new byte[20];
					byte friendStatus[] = new byte[20];
					rowData = new Object[reciveData.length/44][3];
					
					
					
					int cnt = 0;
					for (int i = 0; i < reciveData.length/44; i++) {
						System.arraycopy(reciveData, cnt, num, 0, 4);
						cnt += 4;
						System.arraycopy(reciveData, cnt, friendId, 0, 20);
						cnt += 20;
						System.arraycopy(reciveData, cnt, friendStatus, 0, 20);
						cnt += 20;
//						System.out.println("��ȣ : " + byteArrayToInt(num));
//						System.out.println("���̵� : " + new String(friendId,"UTF-8").trim());
//						System.out.println("��� : " + new String(friendStatus,"UTF-8").trim());
						rowData[i][0] = byteArrayToInt(num);
						rowData[i][1] = new String(friendId,"UTF-8").trim();
						rowData[i][2] = new String(friendStatus,"UTF-8").trim();
					}
					
					return rowData;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
