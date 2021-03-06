package client.request.sungjo;

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
			synchronized(oos)
			{
				int bodylength = 0; // 데이터 길이가 필요한가?
				Header header = new Header(ClientBack.OPENCHAT,bodylength);
				ChatMember chatmember = new ChatMember(clientback.getId(), groupid);
				Data sendData = new Data(header,chatmember);
				oos.writeObject(sendData);
				oos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if( clientback.getChatMap().get(groupid) !=null)
				clientback.getChatMap().get(groupid).show();
			e.printStackTrace();
		}
	}

}
