package client.response;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import client.Chatwindow;
import client.ClientBack;
import model.vo.Chat;
import model.vo.Data;

public class MsgResponse {

	public MsgResponse(ClientBack clientback, Data data) {
		try {
			Map<Long,Chatwindow> chatMap = clientback.getChatMap();
			Chat message = (Chat)data.getObject();
			Long groupid = message.getGroupid();
			if(chatMap.get(message.getGroupid()) == null) {
				Chatwindow chatwindow = new Chatwindow(clientback.getId(),message.getGroupid(), clientback, clientback.getfilesocket());
				chatMap.put(message.getGroupid(), chatwindow);
				chatMap.get(groupid).readchatFile();
				chatwindow.show();
			}
			String line = message.getUserid() + " : " + message.getContent() + "\n";
			chatMap.get(message.getGroupid()).appendMSG(line);
			//파일에 저장
			String fileFolder = "chatcontent" + File.separator + clientback.getId(); // 한 컴퓨터에서 유저별 데이터를 나누기 위하여
			String fileName = message.getGroupid() + ".txt";
			String filePATH = fileFolder + File.separator + fileName;
			File file = new File(fileFolder);
			if(!file.exists()) { // 폴더가 없는 경우
				file.mkdirs();
			}
			file = new File(filePATH);
			if(!file.exists()) { // 파일 없는 경우
				file.createNewFile();
			}
			
			ObjectOutputStream oos = clientback.getChatFileMap().get(groupid);
			if(oos !=null) {
				oos.writeObject(message);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}