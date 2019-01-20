package client.response.sungjo;

import client.ClientBack;
import client.gui.ClientGUI;
import client.gui.ClientHome;
import model.vo.Data;
import model.vo.User;

public class LoginResponse {

	public LoginResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		ClientHome home = clientback.getHome();
		User user= (User)data.getObject();
		if(user != null) {
			clientback.setId(user.getUserid());
			gui.Alert("로그인 성공!");
			gui.loginInvi();
			gui.setVisible(false);
			home = new ClientHome();
			clientback.setHome(home);
			home.home(clientback,clientback.getId());
		}else {
			gui.Alert("로그인 실패. 아이디 또는 비밀번호 오류.");
			gui.loginInvi();
		}
	}

}
