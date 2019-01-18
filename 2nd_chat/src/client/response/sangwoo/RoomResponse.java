package client.response.sangwoo;

import client.ClientBack;
import client.gui.ClientHome;
import model.vo.Data;

public class RoomResponse {

	public RoomResponse(ClientBack clientback, Data data) {
		ClientHome home = clientback.getHome();
		Object rowData[][] = (Object[][])data.getObject();
		home.getFrame().fn_roomListView(rowData);
	}

}
