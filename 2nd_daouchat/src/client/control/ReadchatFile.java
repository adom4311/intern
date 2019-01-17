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
			if(chatMap.get(groupid) == null) { // 채팅방이 없으면 개설
				System.out.println("채티방 개설");
				Chatwindow chatwindow = new Chatwindow(clientback.getId(), groupid, clientback);
				chatMap.put(groupid, chatwindow);
				chatMap.get(groupid).readchatFile();
//				chatwindow.show();
			}else { // 채팅방이 있으면 파일에서 읽어오기 
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
					
					ArrayList<Chat> list = new ArrayList<Chat>();
					Chat msg;
					System.out.println("에이블1 : " +fis.available());
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
			// db에 저장된 채팅 불러오기
			System.out.println("db데이터 요청하기");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
