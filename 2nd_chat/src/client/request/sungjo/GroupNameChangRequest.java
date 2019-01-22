package client.request.sungjo;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;
import model.vo.RoomName;

public class GroupNameChangRequest {

	public GroupNameChangRequest(ClientBack clientback, RoomName rn) {
		try 
		{
			ObjectOutputStream oos = clientback.getOos();
			synchronized(oos)
			{
				int bodylength = 0; // 데이터 길이가 필요한가?
				Header header = new Header(ClientBack.ROOMNAME,bodylength);
				Data sendData = new Data(header,rn);
				oos.writeObject(sendData);
				oos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
