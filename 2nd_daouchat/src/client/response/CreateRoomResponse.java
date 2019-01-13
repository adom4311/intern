package client.response;

import java.util.Map;

import client.Chatwindow;
import client.ClientBack;
import client.ClientGUI;
import client.ClientHome;
import model.vo.Data;

public class CreateRoomResponse {

	public CreateRoomResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		Long groupid = (Long)data.getObject();
		Map<Long, Chatwindow> chatMap = clientback.getChatMap();
		
		if(!groupid.equals("")) {
			if(chatMap.get(groupid) == null) {
				System.out.println("채티방 개설");
				Chatwindow chatwindow = new Chatwindow(clientback.getId(), groupid, clientback, clientback.getfilesocket());
				chatMap.put(groupid, chatwindow);
				chatwindow.show();
			}
		}else {
			gui.Alert("채티방 개설 실패");
		}
	}

}
