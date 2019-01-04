package client;

import java.util.Scanner;

import vo.User;

public class ClientGUI {
	public final static char SIGNUP = 1;
	public final static char LOGIN = 2;

	private ClientController ctrl;
	Scanner sc = new Scanner(System.in);
	private User user;

	public ClientGUI() {
		ctrl = new ClientController();
		index();		
	}
	
	public void index() {
		if(ctrl.getGui() == null) {
			ctrl.setGui(this);
		}
		System.out.println("♬다채롭다 다우채팅♬");
		System.out.println("---------------");
		System.out.println("1. 회원가입");
		System.out.println("2. 로그인");
		System.out.println("---------------");
		System.out.print("선택지 : ");
		
		int select = sc.nextInt();
		sc.nextLine();
		if(select == SIGNUP || select == LOGIN)
			ctrl.front(select);
		else
			index();
	}
	
	public static void main(String[] args) {
		new ClientGUI();
	}
	
	public void SignUp() {
		System.out.print("아이디입력:");
		String id = sc.nextLine();
		System.out.print("패스워드입력:");
		String pw = sc.nextLine();
		
		user = new User(SIGNUP,id,pw);
		
		ctrl.signup(user);
	}

	public void Login() {
		System.out.print("아이디입력:");
		String id = sc.nextLine();
		System.out.print("패스워드입력:");
		String pw = sc.nextLine();
		
		user = new User(LOGIN,id,pw);
		
		ctrl.login(user);
	}
	
	public void home(String id) {
		System.out.println("♬다채롭다 다우채팅♬\r\n" + 
				"---------------");
		System.out.println("1.친구찾기");
		System.out.println("2.친구목록");
		System.out.println("3.채팅방 개설");
		System.out.println("4.채팅방 목록");
		System.out.println("---------------");
		int select = sc.nextInt();
		sc.nextLine();
		if(select == 1)
			ctrl.homeselect(select);
		else
			home(id);
	}

	public void findfriend() {
		System.out.println("메뉴 - 친구찾기");
	}
}
