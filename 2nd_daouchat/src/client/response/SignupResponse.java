package client.response;

import client.ClientBack;
import client.ClientGUI;
import model.vo.Data;

public class SignupResponse {

	public SignupResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		int result = (int)data.getObject();
		if(result > 0) {
			gui.Alert("ȸ������ ����! �α������ּ���.");
			gui.signUpInvi();
		}else {
			gui.Alert("ȸ������ ����. ���̵� �ߺ�");
			gui.signUpInvi();
		}
	}

}
