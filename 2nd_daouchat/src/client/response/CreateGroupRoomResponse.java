package client.response;

import java.util.Map;

import client.Chatwindow;
import client.ClientBack;
import client.ClientGUI;
import model.vo.Data;

public class CreateGroupRoomResponse {

	public CreateGroupRoomResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		Long groupid = (Long)data.getObject();
		Map<Long, Chatwindow> chatMap = clientback.getChatMap();
		
		if(groupid != 0L) {
			if(chatMap.get(groupid) == null) {
				System.out.println("채티방 개설");
				Chatwindow chatwindow = new Chatwindow(clientback.getId(), groupid, clientback);
				chatMap.put(groupid, chatwindow);
				chatMap.get(groupid).readchatFile();
//				chatwindow.show();
			}
		}else {
			gui.Alert("채티방 개설 실패");
		}
	}

}
