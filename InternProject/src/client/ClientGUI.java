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
		System.out.println("�ݴ�ä�Ӵ� �ٿ�ä�â�");
		System.out.println("---------------");
		System.out.println("1. ȸ������");
		System.out.println("2. �α���");
		System.out.println("---------------");
		System.out.print("������ : ");
		
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
		System.out.print("���̵��Է�:");
		String id = sc.nextLine();
		System.out.print("�н������Է�:");
		String pw = sc.nextLine();
		
		user = new User(SIGNUP,id,pw);
		
		ctrl.signup(user);
	}

	public void Login() {
		System.out.print("���̵��Է�:");
		String id = sc.nextLine();
		System.out.print("�н������Է�:");
		String pw = sc.nextLine();
		
		user = new User(LOGIN,id,pw);
		
		ctrl.login(user);
	}
	
	public void home(String id) {
		System.out.println("�ݴ�ä�Ӵ� �ٿ�ä�â�\r\n" + 
				"---------------");
		System.out.println("1.ģ��ã��");
		System.out.println("2.ģ�����");
		System.out.println("3.ä�ù� ����");
		System.out.println("4.ä�ù� ���");
		System.out.println("---------------");
		int select = sc.nextInt();
		sc.nextLine();
		if(select == 1)
			ctrl.homeselect(select);
		else
			home(id);
	}

	public void findfriend() {
		System.out.println("�޴� - ģ��ã��");
	}
}
