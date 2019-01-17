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
		
		if(groupid != 0L) {
			if(chatMap.get(groupid) == null) {
				System.out.println("äƼ�� ����");
				Chatwindow chatwindow = new Chatwindow(clientback.getId(), groupid, clientback);
				chatMap.put(groupid, chatwindow);
				chatMap.get(groupid).readchatFile();
//				chatwindow.show();
			}else {
				chatMap.get(groupid).getFrame().setVisible(true);
			}
		}else {
			gui.Alert("äƼ�� ���� ����");
		}
	}

}
