package client.request.sungjo;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;

public class FriListRequest {

	public FriListRequest(ClientBack clientBack) {
		try {
			ObjectOutputStream oos = clientBack.getOos();
			synchronized(oos)
			{
				int bodylength = 0; // ������ ���̰� �ʿ��Ѱ�?
				Header header = new Header(ClientBack.FRILIST,bodylength);
				Data sendData = new Data(header,null);
				oos.writeObject(sendData);
				oos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
