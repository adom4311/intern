package client.response;

import java.util.List;
import java.util.Map;

import client.Chatwindow;
import client.ClientBack;
import client.ClientHome;
import model.vo.Data;

public class OpenchatResponse {

	public OpenchatResponse(ClientBack clientback, Data data) {
		Map<String,Chatwindow> chatMap = clientback.getChatMap();
		Chatwindow chatwindow;
		List<String[]> chatcontent = (List<String[]>)data.getObject();
		if(chatcontent.size()>0) {
			chatwindow = chatMap.get(chatcontent.get(0)[1]);
			for (int i = 0; i < chatcontent.size(); i++) {
				chatwindow.appendMSG(chatcontent.get(0) + " : " + chatcontent.get(2));
			}
		}
	}

}
