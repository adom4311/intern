package client.response.sungjo;

import client.ClientBack;
import client.gui.ClientGUI;
import model.vo.Data;

public class DeleteFriendResponse {

	public DeleteFriendResponse(ClientBack clientback, Data data) {
		int result = (int)data.getObject();
		ClientGUI gui = clientback.getGui();
		if(result > 0 ) {
			gui.Alert("模备昏力 己傍");
			clientback.friList();
		}else {
			gui.Alert("模备昏力 角菩");
		}
	}

}
