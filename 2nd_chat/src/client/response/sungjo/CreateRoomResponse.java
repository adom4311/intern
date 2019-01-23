package client.response.sungjo;

import java.util.Map;

import client.ClientBack;
import client.gui.Chatwindow;
import client.gui.ClientGUI;
import model.vo.Data;
import model.vo.GroupInfo;

public class CreateRoomResponse {

	public CreateRoomResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		GroupInfo info = (GroupInfo)data.getObject();
		Map<Long, Chatwindow> chatMap = clientback.getChatMap();
		
		if(info != null) {
			Long groupid = info.getGroupid();
			if(chatMap.get(groupid) == null) {
				System.out.println("채티방 개설");
				Chatwindow chatwindow = new Chatwindow(clientback.getId(), groupid, clientback);
				chatwindow.getFrame().setTitle(info.getGroupname());
				
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
