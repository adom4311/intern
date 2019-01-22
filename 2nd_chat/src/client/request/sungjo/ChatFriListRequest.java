package client.request.sungjo;

import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;

public class ChatFriListRequest {

	public ChatFriListRequest(ClientBack clientBack, Long groupid) {
		try {
			ObjectOutputStream oos = clientBack.getOos();
			synchronized(oos)
			{
				int bodylength=0;
				Header header = new Header(ClientBack.CHATFRILIST,bodylength);
				Data sendData = new Data(header,groupid);
				oos.writeObject(sendData);
				oos.flush();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
