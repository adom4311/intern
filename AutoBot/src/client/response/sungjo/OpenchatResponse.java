package client.response.sungjo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import client.ClientBack;
import model.vo.Chat;
import model.vo.ChatcontentList;
import model.vo.Data;

public class OpenchatResponse {

	public OpenchatResponse(ClientBack clientback, Data data) {
		try {
			ObjectOutputStream oos;
			// ���� ����
			ChatcontentList chatcontentList = (ChatcontentList)data.getObject();
			Long groupid = chatcontentList.getGroupid();
			List<Chat> chatcontent = chatcontentList.getChatcontent();
			
			System.out.println("���������� ������ " + chatcontent.size()); 
			if(chatcontent.size() > 0) {
//				Long groupid  = chatcontent.get(0).getGroupid();
				oos = clientback.getChatFileMap().get(groupid);
				for(Chat content : chatcontent) {
					oos.writeObject(content);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
