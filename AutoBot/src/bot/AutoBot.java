package bot;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import client.ClientBack;

public class AutoBot {
	public static Map<String,ClientBack> clientMap;
	public static Map<String,Client> clientThreadMap;
	public static int total;
	public static Long groupid;
	public static void main(String[] args) {
		
		clientMap = new HashMap<String,ClientBack>();
		clientThreadMap = new HashMap<String,Client>();
		Scanner sc = new Scanner(System.in);
		
		
		int menu;
		while(true) {

			
			
			System.out.println("------���亿------\n"
					+ "1. ȸ������\n"
					+ "2. �α���\n"
					+ "3. �׷�ä�ù氳��\n"
					+ "4. �׷�ä�ù����\n"
					+ "5. ��ȭ����\n"
					+ "6. ��ȭ����\n"
					+ "7. ���α׷� ����\n"
					+ "---------------");
			
			menu = sc.nextInt();
			sc.nextLine();
			
			switch(menu) {
			case 1: 
				System.out.print("Ŭ���̾�Ʈ ������ : ");
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
				
				for (String key : clientMap.keySet()) {
					new Client(menu, key, clientMap.get(key)).start();					
				}
				break;
			case 2: 
				for (String key : clientMap.keySet()) {
					new Client(menu, key, clientMap.get(key)).start();					
				}
				break; 
			case 3: 
				for (String key : clientMap.keySet()) {
					new Client(menu, key, clientMap.get(key)).start();
					break;
				}
				break;
			case 4:
				for (String key : clientMap.keySet()) {
					new Client(menu, key, clientMap.get(key)).start();
				}
				break;
			case 5: 
				for (String key : clientMap.keySet()) {
					Client c = new Client(menu, key, clientMap.get(key));
					clientThreadMap.put(key, c);
					c.start();
				}
				break;
			case 6:
				for (String key : clientThreadMap.keySet()) {
					clientThreadMap.get(key).interrupt();
				}
				break;
			case 7: System.exit(0);	
			default: break;
			}
		}
	}
}

class Client extends Thread{
	int menu;
	ClientBack clientback;
	String userid;
	public Client(int menu, String userid, ClientBack clientback) {
		this.menu = menu;
		this.userid = userid;
		this.clientback = clientback;
	}

	@Override
	public void run() {
		if(menu == 1) {
			clientback.signUp(userid, userid);
		}
		else if(menu == 2) {
			clientback.login(userid, userid);
		}
		else if(menu == 3) {
			String[] friends = new String[AutoBot.total];
			int i = 0;
			for (String userid : AutoBot.clientMap.keySet()) {
				friends[i++] = userid;
					System.out.println("���̵��?" + userid);
			}
			clientback.createGroupRoom(friends);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AutoBot.groupid = clientback.getGroupid();
			System.out.println(AutoBot.groupid);
		}
		else if(menu == 4) {
			clientback.readchatFile(AutoBot.groupid);
		}
		else if(menu == 5) {
			while(!isInterrupted()){
				try {
					clientback.sendMessage("����������������", AutoBot.groupid);
					Thread.sleep(500);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
	
}
