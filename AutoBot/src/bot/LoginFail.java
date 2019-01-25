package bot;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import client.ClientBack;

public class LoginFail {
	public static Map<String,ClientBack> clientMap;
	public static Map<String,ClientLogin> clientThreadMap;
	public static int total;
	public static Long groupid;

	public static void main(String[] args) {
		clientMap = new HashMap<String,ClientBack>();
		clientThreadMap = new HashMap<String,ClientLogin>();
		Scanner sc = new Scanner(System.in);
		
		while(true) {
			int total = 0;
			System.out.println("--�α��� ������ ��û ���亿--");
			System.out.println("�α��� ��û�� Ŭ���̾�Ʈ �� : ");
			total = sc.nextInt();
			sc.nextLine();
			System.out.print("Ŭ���̾�Ʈ ���̵� : ");
			String user = sc.nextLine();
			int i = 0;
			String userid;
			for (; i < total; i++) {
				userid = user + i;
				clientMap.put(userid, new ClientBack());
			}
			int cnt = 0;
			for (String key : clientMap.keySet()) {
				if(cnt++ >= (total/10)) {
					clientMap.get(key).signUp(key, key);
				}				
			}
			cnt = 0;
			for (String key : clientMap.keySet()) {
				if(cnt++ < (total/10)) {
					new ClientLogin(1, key, clientMap.get(key)).start();	
				}else {
					clientMap.get(key).login(key, key);	
				}				
			}
		}
	}

}

class ClientLogin extends Thread{
	int menu;
	ClientBack clientback;
	String userid;
	public ClientLogin(int menu, String userid, ClientBack clientback) {
		this.menu = menu;
		this.userid = userid;
		this.clientback = clientback;
	}

	@Override
	public void run() {
		if(menu == 1) {
			while(true) {
				try {
					System.out.println(userid + "�α��� �õ�");
					clientback.login(userid, userid);
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}

