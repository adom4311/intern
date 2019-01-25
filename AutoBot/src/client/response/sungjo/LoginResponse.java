package client.response.sungjo;

import client.ClientBack;
import model.vo.Data;
import model.vo.User;

public class LoginResponse {

	public LoginResponse(ClientBack clientback, Data data) {
		User user= (User)data.getObject();
		if(user != null)
			clientback.setId(user.getUserid());
	}

}
