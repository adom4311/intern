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

import dao.ServerDAO;

public class ServerBack {
	public static final byte STX = 0x02; // ��� ����
	public static final byte ETX = 0x03; // ��� ��
	public static final byte SIGNUP = 0x01; // ȸ������
	public static final byte LOGIN = 0x02; // �α���
	public static final byte MSG = 0x03; // �Ϲݸ޽���
	public static final byte FRIFIND = 0x04; // ģ��ã��
	
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
	
	/* ���������� �ʿ� �߰� */
	public void addClient(String id, DataOutputStream os) {
		if(id == null) {
			id = "GM" + non_login_increment++;
		}
		currentClientMap.put(id, os);
	}
	
	/* ������ ����� Ŭ���̾�Ʈ�� ������ ���� ��� */
	class Receiver extends Thread{
		private DataInputStream is;
		private DataOutputStream os;
		public Receiver(Socket socket) {
			try {
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
				addClient(null,os);
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
						System.out.println(buffer);
						String data[] = buffer.toString().split(",");
						
						buffer.flush();
						
						int chk = sDao.signUp(data[0],data[1]);
						os.writeInt(chk);
						
					}// ȸ������ END
					
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
						System.out.println(buffer);
						String data[] = buffer.toString().split(",");
						
						buffer.flush();
						int chk = sDao.login(data[0],data[1]);
						os.writeInt(chk);
					}
				}
			}catch (SocketException e) {
				System.out.println("Ŭ���̾�Ʈ ����");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

