package client.response.sungjo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import client.ClientBack;
import client.gui.Chatwindow;
import model.vo.Chat;
import model.vo.ChatcontentList;
import model.vo.Data;

public class OpenchatResponse {

	public OpenchatResponse(ClientBack clientback, Data data) {
		try {
			Map<Long,Chatwindow> chatMap = clientback.getChatMap();
			Chatwindow chatwindow;
			ObjectOutputStream oos;
			// 서버 파일
			ChatcontentList chatcontentList = (ChatcontentList)data.getObject();
			Long groupid = chatcontentList.getGroupid();
			List<Chat> chatcontent = chatcontentList.getChatcontent();
			
			System.out.println("서버데이터 사이즈 " + chatcontent.size()); 
			if(chatcontent.size() > 0) {
//				Long groupid  = chatcontent.get(0).getGroupid();
				oos = clientback.getChatFileMap().get(groupid);
				chatwindow = chatMap.get(groupid);
				for(Chat content : chatcontent) {
					oos.writeObject(content);
					chatwindow.appendMSG(content.getUserid() + " : " + content.getContent() + "\n");
				}
			}
			if( chatMap.get(groupid) != null) {
				chatMap.get(groupid).getFrame().setTitle(chatcontentList.getGroupName());
				chatMap.get(groupid).show();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
