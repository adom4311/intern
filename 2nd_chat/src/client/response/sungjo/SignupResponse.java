package client.response.sungjo;

import client.ClientBack;
import client.gui.ClientGUI;
import model.vo.Data;

public class SignupResponse {

	public SignupResponse(ClientBack clientback, Data data) {
		ClientGUI gui = clientback.getGui();
		int result = (int)data.getObject();
		if(result > 0) {
			gui.Alert("회원가입 성공! 로그인해주세요.");
			gui.signUpInvi();
		}else {
			gui.Alert("회원가입 실패. 아이디 중복");
			gui.signUpInvi();
		}
	}

}
