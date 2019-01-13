package client.response;

import java.util.List;
import java.util.Map;

import client.Chatwindow;
import client.ClientBack;
import model.vo.Chat;
import model.vo.Data;

public class OpenchatResponse {

	public OpenchatResponse(ClientBack clientback, Data data) {
		Map<Long,Chatwindow> chatMap = clientback.getChatMap();
		Chatwindow chatwindow;
		List<Chat> chatcontent = (List<Chat>)data.getObject();
		
		// 파일에서 읽어오기
		
		// 서버채팅 
		System.out.println("openchat size : " + chatcontent.size());
		if(chatcontent.size() > 0) {
			Long groupid  = chatcontent.get(0).getGroupid();
			chatwindow = chatMap.get(groupid);
			for(Chat content : chatcontent) {
				chatwindow.appendMSG(content.getUserid() + " : " + content.getContent() + "\n");
			}
		}
	}
}
