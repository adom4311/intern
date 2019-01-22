package client.response.sungjo;

import client.ClientBack;
import client.gui.ClientGUI;
import client.gui.ClientHome;
import model.vo.Data;
import model.vo.User;

public class GroupNameChangRequestResponse {

	public GroupNameChangRequestResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		ClientHome home = clientback.getHome();
		int result= (int)data.getObject();
		System.out.println("결과값 " + result);
		if(result > 0) {
			System.out.println("성공!");
			//clientback.roomList();
			home.fn_roomList(clientback);
		}else {
			gui.Alert("채팅방명 변경실패");
		}
	}

}
