package client.response.sungjo;

import java.util.Map;

import client.ClientBack;
import client.gui.Chatwindow;
import client.gui.ClientGUI;
import model.vo.Data;

public class CreateGroupRoomResponse {

	public CreateGroupRoomResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		Long groupid = (Long)data.getObject();
		Map<Long, Chatwindow> chatMap = clientback.getChatMap();
		
		if(groupid != null) {
			if(chatMap.get(groupid) == null) {
				System.out.println("채티방 개설");
				Chatwindow chatwindow = new Chatwindow(clientback.getId(), groupid, clientback);
				chatMap.put(groupid, chatwindow);
				chatMap.get(groupid).readchatFile();
//				chatwindow.show();
			}else {
				chatMap.get(groupid).getFrame().setVisible(true);
			}
		}else {
			gui.Alert("채티방 개설 실패");
		}
	}

}
