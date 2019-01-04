package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import vo.ServerDAO;
import vo.User;

public class ServerController {
	private ServerSocket serverSocket; // ��������
	private Socket socket; // �޾ƿ� ���� ����
//    private ServerGUI gui; // ���� GUI
    private String msg; // ä������
    /* ���� �������� ����ڵ��� ���� */ 
    private Map<String, DataOutputStream> connClientMap = new HashMap<String, DataOutputStream>();
    
    
    
    public static void main(String[] args) {
		ServerController serverController = new ServerController();
		serverController.setting();
	}
    
    // ���� ����� �۵��Ǵ� �Լ�
    public void setting() {
    	try {
            Collections.synchronizedMap(connClientMap); // Thread-safe
			serverSocket = new ServerSocket(1993); // ���� ����
			while(true) {
	    		System.out.println("�����...");
	    		socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���� ����
	    		System.out.println(socket.getInetAddress() + "���� ����");
	    		
	    		Receiver receiver = new Receiver(socket); // ������ ���ӵ� ����� ������
	    		receiver.start();
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void addClient(String nick, DataOutputStream out) {
    	connClientMap.put("user1", out);
    }
}



class Receiver extends Thread{
	private DataInputStream in; // ������ �Է� ��Ʈ��
    private DataOutputStream out; // ������ �ƿ�ǲ ��Ʈ��
    private ObjectInputStream ois; // ������Ʈ ��ǲ ��Ʈ��
    private String msg;
    User user;
    ServerDAO sDao;
    int chk;

	public Receiver(Socket socket) {
		try { // ���� �ѹ��� ����
			sDao =new ServerDAO();
			out = new DataOutputStream(socket.getOutputStream());
	        in = new DataInputStream(socket.getInputStream());
	        ois = new ObjectInputStream(in);
	        System.out.println("����");
//	        if(in != null) {
//	        	msg = in.readUTF();
//	        	System.out.println(msg);
//	        }
		} catch (IOException e) {
			e.printStackTrace();
		}			 
	}
	
	@Override
	public void run() {
		try {
			while (ois != null) {
				user = (User)ois.readObject(); // ȸ������ ���� ����
				if(user.getSelect() == 1) { // ȸ������
					System.out.println("ID:"+user.getUserid());
					System.out.println("PW:"+user.getPassword());
					chk = sDao.signup(user); // �����ͺ��̽� ����
					out.writeInt(chk);
					
//					if(chk >0) { // ȸ������ ������ ������ ����
//						System.out.println("����������");
//						break;
//					}	
//					System.out.println("��������");
				}
				else if(user.getSelect() == 2) { // �α���
					System.out.println("�α���");
					chk = sDao.login(user);
					out.writeInt(chk);
					
//					if(chk >0) { // �α��� ������ ������ ����
//						System.out.println("����������");
//						break;
//					}	
//					System.out.println("��������");
				}
			}
		} catch (IOException e) {
			System.out.println("io����");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Ŭ��������");
			e.printStackTrace();
		}
		
	}
}