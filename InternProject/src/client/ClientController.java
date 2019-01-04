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
			socket = new Socket("127.0.0.1",1993); // 서버주소 및 포트번호
			System.out.println("서버에 연결");
			
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
					System.out.println("회원가입 성공! 로그인해주세요.");
					gui.index();
				}else {
					System.out.println("회원가입 실패! 중복된 아이디입니다.");
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
			oos.writeObject(user); // 유저 정보 전송
			while(in != null) { // 서버 반응 대기
				if(in.readInt() > 0) {
					System.out.println("로그인 성공!");
					gui.home(user.getUserid());
				}else {
					System.out.println("로그인 실패");
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
		case 1: // 1. 친구찾기
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
