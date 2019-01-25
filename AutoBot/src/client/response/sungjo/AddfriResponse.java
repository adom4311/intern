package client.response.sungjo;

import client.ClientBack;
import model.vo.Data;

public class AddfriResponse {

	public AddfriResponse(ClientBack clientback, Data data) {
		int result = (int)data.getObject();
		
		if(result > 0) {
		}else {
		}
	}

}
