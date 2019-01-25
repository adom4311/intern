package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import model.dao.ServerDAO;
import model.vo.Chat;
import model.vo.Data;
import model.vo.User;

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


		
	public void setGroupidClientMap(Map<Long, Map<String, ObjectOutputStream>> groupidClientMap) {
		this.groupidClientMap = groupidClientMap;
	}
	
	public Map<Long , Map<String, ObjectOutputStream>> getGroupidClientMap(){
		return groupidClientMap;
	}
	public Map<String, ObjectOutputStream> getCurrentClientMap() {
		return currentClientMap;
	}
	public void setCurrentClientMap(Map<String, ObjectOutputStream> currentClientMap) {
		this.currentClientMap = currentClientMap;
	}
	
	public ServerSocket getFileserverSocket() {
		return fileserverSocket;
	}

	public void setFileserverSocket(ServerSocket fileserverSocket) {
		this.fileserverSocket = fileserverSocket;
	}
	
	public static void main(String[] args) {
		ServerBack serverBack = new ServerBack();
		serverBack.setting();
	}
	
	private void createGroupidMap() {
		ServerDAO sDao = new ServerDAO();
		ArrayList<Long> list = sDao.selectGroupid();
		for(Long groupid : list) {
			groupidClientMap.put(groupid,new HashMap<String,ObjectOutputStream>());
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
				Receiver receiver = new Receiver(this, socket);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
					RpReceiver rpreceiver = new RpReceiver(socket2);
					rpreceiver.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

