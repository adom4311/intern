package client.response;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import client.Chatwindow;
import client.ClientBack;
import model.vo.Chat;
import model.vo.Data;

public class OpenchatResponse {

	public OpenchatResponse(ClientBack clientback, Data data) {
		try {
			Map<Long,Chatwindow> chatMap = clientback.getChatMap();
			Chatwindow chatwindow;
			ObjectOutputStream oos;
			List<Chat> chatcontent = (List<Chat>)data.getObject();
			
			// 파일에서 읽어오기
			
			// 서버채팅 
			System.out.println("openchat size : " + chatcontent.size());
			
			if(chatcontent.size() > 0) {
				Long groupid  = chatcontent.get(0).getGroupid();
				oos = clientback.getChatFileMap().get(groupid);
				chatwindow = chatMap.get(groupid);
				for(Chat content : chatcontent) {
					oos.writeObject(content);
					chatwindow.appendMSG(content.getUserid() + " : " + content.getContent() + "\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
