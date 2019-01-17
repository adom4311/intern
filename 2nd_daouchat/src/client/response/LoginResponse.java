package client.response;

import client.ClientBack;
import client.gui.ClientGUI;
import client.gui.ClientHome;
import model.vo.Data;

public class LoginResponse {

	public LoginResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		ClientHome home = clientback.getHome();
		int result = (int)data.getObject();
		if(result > 0) {
			gui.Alert("�α��� ����!");
			gui.loginInvi();
			gui.setVisible(false);
			home = new ClientHome();
			clientback.setHome(home);
			home.home(clientback,clientback.getId());
		}else {
			gui.Alert("�α��� ����. ���̵� �Ǵ� ��й�ȣ ����.");
			gui.loginInvi();
		}
	}

}
