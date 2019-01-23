package client.control.sungjo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;

import client.ClientBack;
import model.vo.Chat;

public class ReadchatFile {
	public ArrayList<Chat> list;
	public ReadchatFile(ClientBack clientback, Long groupid) {
		try {
				String fileFolder = "chatcontent" + File.separator + clientback.getId(); // 한 컴퓨터에서 유저별 데이터를 나누기 위하여
				String fileName = groupid + ".txt";
				String filePATH = fileFolder + File.separator + fileName;
				File file = new File(fileFolder);
				if(!file.exists()) { // 폴더가 없는 경우
					file.mkdirs();
				}
				file = new File(filePATH);
				if(!file.exists()) { // 파일 없는 경우
					file.createNewFile();
				}
				
				FileInputStream fis = new FileInputStream(file);
				if(fis.available() > 0) {
					ObjectInputStream ois = new ObjectInputStream(fis);
					
					list = new ArrayList<Chat>();
					Chat msg;
					System.out.println("에이블1 : " +fis.available());
					while(fis.available() > 0){
						msg = (Chat)ois.readObject();
						list.add(msg);
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
				clientback.getChatFileMap().remove(groupid);
				fis.close();

				clientback.openChat(groupid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
