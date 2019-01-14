package client.response;

import client.ClientBack;
import client.ClientHome;
import model.vo.Data;

public class CreateGroupRoomListResponse {

	public CreateGroupRoomListResponse(ClientBack clientback, Data data) {
		ClientHome home = clientback.getHome();
		Object rowData[][] = (Object[][])data.getObject();
		home.getFrame().fn_CreateGroupRoomListView(rowData);
	}

}
