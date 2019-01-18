package client.request.sungjo;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;
import model.vo.User;

public class AddFriendRequest {

	public AddFriendRequest(ClientBack clientBack, String friendId) {
		try {
			ObjectOutputStream oos = clientBack.getOos();
			synchronized(oos)
			{
				int bodylength = 0; // 데이터 길이가 필요한가?
				Header header = new Header(ClientBack.ADDFRI,bodylength);
				Data sendData = new Data(header,friendId);
				oos.writeObject(sendData);
				oos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
