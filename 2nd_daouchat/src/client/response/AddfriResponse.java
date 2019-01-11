package client.response;

import client.ClientBack;
import client.ClientGUI;
import client.ClientHome;
import model.vo.Data;

public class AddfriResponse {

	public AddfriResponse(ClientBack clientback, Data data) {
		ClientHome home = clientback.getHome();
		ClientGUI gui = clientback.getGui();
		
		int result = (int)data.getObject();
		
		if(result > 0) {
			gui.Alert("ģ���߰� ����!");
			home.getFrame().fn_addfri(clientback);
		}else {
			gui.Alert("ģ���߰� ����!");
		}
	}

}
