package client.request;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.ChatMember;
import model.vo.Data;
import model.vo.Header;

public class OpenChatRequest {

	public OpenChatRequest(ClientBack clientback, Long groupid) {
		try {
			ObjectOutputStream oos = clientback.getOos();
			int bodylength = 0; // 데이터 길이가 필요한가?
			Header header = new Header(ClientBack.OPENCHAT,bodylength);
			ChatMember chatmember = new ChatMember(clientback.getId(), groupid);
			Data sendData = new Data(header,chatmember);
			oos.writeObject(sendData);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
