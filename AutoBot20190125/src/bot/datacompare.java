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
//		System.out.println("파일사이즈 : " + ReadchatFile.list.size());
//		System.out.println("db 사이즈 : " + ClientBack.list.size());
//		
//		for (int i = 0; i < ReadchatFile.list.size(); i++) {
//			if(!(ReadchatFile.list.get(i).getChatid().equals(ClientBack.list.get(i).getChatid()))) {
//				System.out.println("다른 내용 : " + ReadchatFile.list.get(i).getChatid());
//			}
////			System.out.println("파일 데이터 : " + ReadchatFile.list.get(i).getChatid() + ", " + ReadchatFile.list.get(i).getSendtime());
//		}
		System.exit(0);
//		for (int i = 0; i < 100; i++) {
//			System.out.println("파일 데이터 : " + ReadchatFile.list.get(i).getChatid() + ", " + ReadchatFile.list.get(i).getSendtime());}
//		for (int i = 0; i < 100; i++) {
//			System.out.println("db 데이터 : " + ClientBack.list.get(i).getChatid() + ", " + ClientBack.list.get(i).getSendtime());}
		
	}

}
