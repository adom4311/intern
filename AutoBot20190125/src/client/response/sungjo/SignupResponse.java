package client.response.sungjo;

import client.ClientBack;
import model.vo.Data;

public class SignupResponse {

	public SignupResponse(ClientBack clientback, Data data) {
		int result = (int)data.getObject();
	}

}
