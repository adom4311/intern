package client.response.sungjo;

import java.util.Map;

import client.ClientBack;
import model.vo.Data;

public class CreateGroupRoomResponse {

	public CreateGroupRoomResponse(ClientBack clientback, Data data) {
		Long groupid = (Long)data.getObject();
		clientback.setGroupid(groupid);
	}

}
