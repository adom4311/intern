package bot;

import client.ClientBack;
import client.control.sungjo.ReadchatFile;
import model.vo.Chat;

public class datacompare {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientBack c = new ClientBack();
		c.login("user0", "user0");
		c.setId("user0");
		Long aaaa = 1L;
		c.readchatFile(aaaa);
		c.selectchat(aaaa);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("���ϻ����� : " + ReadchatFile.list.size());
//		System.out.println("db ������ : " + ClientBack.list.size());
//		
//		for (int i = 0; i < ReadchatFile.list.size(); i++) {
//			if(!(ReadchatFile.list.get(i).getChatid().equals(ClientBack.list.get(i).getChatid()))) {
//				System.out.println("�ٸ� ���� : " + ReadchatFile.list.get(i).getChatid());
//			}
////			System.out.println("���� ������ : " + ReadchatFile.list.get(i).getChatid() + ", " + ReadchatFile.list.get(i).getSendtime());
//		}
		System.exit(0);
//		for (int i = 0; i < 100; i++) {
//			System.out.println("���� ������ : " + ReadchatFile.list.get(i).getChatid() + ", " + ReadchatFile.list.get(i).getSendtime());}
//		for (int i = 0; i < 100; i++) {
//			System.out.println("db ������ : " + ClientBack.list.get(i).getChatid() + ", " + ClientBack.list.get(i).getSendtime());}
		
	}

}
