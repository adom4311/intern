package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import vo.User;

public class ClientGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
    private JTextArea jta = new JTextArea(30,15);
    private JTextField jtf = new JTextField(15);
    private static String nickName;
    
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
		
		add(jta, BorderLayout.CENTER);
        add(jtf, BorderLayout.SOUTH);
        jtf.addActionListener(this);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setBounds(800, 100, 400, 600);
        setTitle("클라이언트부분");
		
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
