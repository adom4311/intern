package client.request.sungjo;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;
import model.vo.User;

public class SignupRequest {

	public SignupRequest(ClientBack clientBack, String id, String pw) {
		try {
			ObjectOutputStream oos = clientBack.getOos();
			synchronized(oos)
			{
				int bodylength = 0; // 데이터 길이가 필요한가?
				Header header = new Header(ClientBack.SIGNUP,bodylength);
				User user = new User(id,pw);
				Data senddata = new Data(header,user);
				oos.writeObject(senddata);
				oos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
