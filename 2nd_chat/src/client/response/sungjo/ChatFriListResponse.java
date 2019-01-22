package client.response.sungjo;

import client.ClientBack;
import model.vo.ChatFriList;
import model.vo.Data;

public class ChatFriListResponse {

	public ChatFriListResponse(ClientBack clientback, Data data) {
		ChatFriList fri = (ChatFriList)data.getObject();
		Object rowdata[][] = fri.getObject();
		Long groupid = fri.getGroupid();
		
		clientback.getChatMap().get(groupid).getClientfrilistgui().insertable(rowdata);
	}

}
