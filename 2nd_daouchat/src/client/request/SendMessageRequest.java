package client.request;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;
import model.vo.Message;

public class SendMessageRequest {

	public SendMessageRequest(ClientBack clientBack, String msg, Long groupid) {
		try {
			ObjectOutputStream oos = clientBack.getOos();
			int bodylength = 0; // 데이터 길이가 필요한가?
			Header header = new Header(ClientBack.MSG,bodylength);
			Message message = new Message(clientBack.getId(),groupid,msg,null);
			Data sendData = new Data(header,message);
			oos.writeObject(sendData);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
