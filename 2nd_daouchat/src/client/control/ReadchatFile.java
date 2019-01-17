package client.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;

import client.Chatwindow;
import client.ClientBack;
import model.vo.Chat;

public class ReadchatFile {

	public ReadchatFile(ClientBack clientback, Long groupid) {
		Map<Long, Chatwindow> chatMap = clientback.getChatMap();
		try {
			if(chatMap.get(groupid) == null) { // ä�ù��� ������ ����
				System.out.println("äƼ�� ����");
				Chatwindow chatwindow = new Chatwindow(clientback.getId(), groupid, clientback);
				chatMap.put(groupid, chatwindow);
				chatMap.get(groupid).readchatFile();
//				chatwindow.show();
			}else { // ä�ù��� ������ ���Ͽ��� �о���� 
				String fileFolder = "chatcontent" + File.separator + clientback.getId(); // �� ��ǻ�Ϳ��� ������ �����͸� ������ ���Ͽ�
				String fileName = groupid + ".txt";
				String filePATH = fileFolder + File.separator + fileName;
				File file = new File(fileFolder);
				if(!file.exists()) { // ������ ���� ���
					file.mkdirs();
				}
				file = new File(filePATH);
				if(!file.exists()) { // ���� ���� ���
					file.createNewFile();
				}
				
				FileInputStream fis = new FileInputStream(file);
				if(fis.available() > 0) {
					ObjectInputStream ois = new ObjectInputStream(fis);
					
					ArrayList<Chat> list = new ArrayList<Chat>();
					Chat msg;
					System.out.println("���̺�1 : " +fis.available());
					while(fis.available() > 0){
						msg = (Chat)ois.readObject();
						list.add(msg);
						clientback.getChatMap().get(groupid).appendMSG(msg.getUserid() + " : " + msg.getContent() + "\n");
					}
					
					FileOutputStream fos = new FileOutputStream(file);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					clientback.getChatFileMap().put(groupid, oos);
					for(Chat writemsg : list) {
						oos.writeObject(writemsg);
					}
					ois.close();
				}else {
					FileOutputStream fos = new FileOutputStream(file);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					clientback.getChatFileMap().put(groupid, oos);
				}

				clientback.openChat(groupid);
			}
			// db�� ����� ä�� �ҷ�����
			System.out.println("db������ ��û�ϱ�");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
