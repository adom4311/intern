package client.request;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;

public class CreateGroupRequest {

	public CreateGroupRequest(ClientBack clientBack, String[] friendids) {
		try {
			ObjectOutputStream oos = clientBack.getOos();
			int bodylength = 0; // ������ ���̰� �ʿ��Ѱ�?
			Header header = new Header(ClientBack.CREATEGROUP,bodylength);
			System.out.println("ģ�� ���̵� " + friendids);
			Data sendData = new Data(header,friendids);
			oos.writeObject(sendData);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
