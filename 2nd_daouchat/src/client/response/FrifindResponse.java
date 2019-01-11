package client.response;

import client.ClientBack;
import client.ClientGUI;
import client.ClientHome;
import model.vo.Data;

public class FrifindResponse {

	public FrifindResponse(ClientBack clientback, Data data) {
		ClientHome home = clientback.getHome();
		Object rowData[][] = (Object[][])data.getObject();
		home.getFrame().fn_addfriView(rowData);
	}

}
