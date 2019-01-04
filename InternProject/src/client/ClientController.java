package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import vo.User;

public class ClientController {
	private Socket socket;
	private DataInputStream in;
    private DataOutputStream out;
    private ObjectOutputStream oos;
	private ClientGUI gui;
	
	public void setGui(ClientGUI gui) {
		this.gui = gui;
	}
	public ClientGUI getGui() {
		return this.gui;
	}
	
	public ClientController() {
		connect();
	}
	

	public void connect() {
		try {
			socket = new Socket("127.0.0.1",1993); // �����ּ� �� ��Ʈ��ȣ
			System.out.println("������ ����");
			
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			oos = new ObjectOutputStream(out);
			
//			Sender sender = new Sender(socket);
//			sender.start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void front(int select) {
		switch(select) {
		case 1:
			gui.SignUp();
			break;
		case 2:
			gui.Login();
			break;
		}
	}
	
	public void signup(User user) {
		try {
			oos.writeObject(user);
			while(in != null) {
				if(in.readInt() > 0) {
					System.out.println("ȸ������ ����! �α������ּ���.");
					gui.index();
				}else {
					System.out.println("ȸ������ ����! �ߺ��� ���̵��Դϴ�.");
					gui.SignUp();
				}
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void login(User user) {
		try {
			oos.writeObject(user); // ���� ���� ����
			while(in != null) { // ���� ���� ���
				if(in.readInt() > 0) {
					System.out.println("�α��� ����!");
					gui.home(user.getUserid());
				}else {
					System.out.println("�α��� ����");
					gui.Login();
				}
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void homeselect(int select) {
		switch(select) {
		case 1: // 1. ģ��ã��
			gui.findfriend();
			break;
		}
	}
}

class Sender extends Thread{
	
	public Sender(Socket socket) {
		
	}
	
	@Override
	public void run() {
		
	}
}
