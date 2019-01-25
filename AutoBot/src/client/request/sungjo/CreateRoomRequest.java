package client.request.sungjo;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;

public class CreateRoomRequest {
	public CreateRoomRequest(ClientBack clientBack, String[] friendids) {
		try 
		{
			ObjectOutputStream oos = clientBack.getOos();
			synchronized(oos)
			{
				int bodylength = 0; // 데이터 길이가 필요한가?
				Header header = new Header(ClientBack.CREATEROOM,bodylength);
				Data sendData = new Data(header,friendids);
				oos.writeObject(sendData);
				oos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
