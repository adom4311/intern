package client.response.sungjo;

import client.ClientBack;
import client.gui.ClientGUI;
import client.gui.ClientHome;
import model.vo.Data;
import model.vo.RoomName;

public class GroupNameChangRequestResponse {

	public GroupNameChangRequestResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		ClientHome home = clientback.getHome();
		RoomName result= (RoomName)data.getObject();
		System.out.println("����� " + result);
		if(result.getResult() > 0) {
			System.out.println("����!");
			//clientback.roomList();
			if(clientback.getChatMap().get(result.getGroupid()) != null)
				clientback.getChatMap().get(result.getGroupid()).getFrame().setTitle(result.getGroupName());
			home.fn_roomList(clientback);
		}else {
			gui.Alert("ä�ù�� �������");
		}
	}

}
