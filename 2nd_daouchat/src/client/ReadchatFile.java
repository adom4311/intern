package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.vo.Message;

public class ReadchatFile {

	public ReadchatFile(ClientBack clientback, Long groupid) {
		try {
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
				return;
			}
			FileInputStream fis = new FileInputStream(file);
			if(fis.available() > 0) {
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				ArrayList<Message> list = new ArrayList<Message>();
				Message msg;
				System.out.println("���̺�1 : " +fis.available());
				while(fis.available() > 0){
					msg = (Message)ois.readObject();
					list.add(msg);
					clientback.getChatMap().get(groupid).appendMSG(msg.getSenduserid() + " : " + msg.getMsg() + "\n");
					System.out.println(msg.getSenduserid() + " : " + msg.getMsg() + "\n");
				}
				
				FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				clientback.getChatFileMap().put(groupid, oos);
				for(Message writemsg : list) {
					oos.writeObject(writemsg);
				}
				ois.close();
			}else {
				FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				clientback.getChatFileMap().put(groupid, oos);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
