package client.response.sungjo;

import client.ClientBack;
import client.gui.ClientGUI;
import client.gui.ClientHome;
import model.vo.Data;

public class FrifindResponse {

	public FrifindResponse(ClientBack clientback, Data data) {
		ClientHome home = clientback.getHome();
		Object rowData[][] = (Object[][])data.getObject();
		home.getFrame().fn_addfriView(rowData);
	}

}
