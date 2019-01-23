package client.request.sangwoo;

import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;
import model.vo.Roominfo;

public class Outroomrequest {
	
	public Outroomrequest(ClientBack clientback,String userid,Long groupid) {
		try {
			ObjectOutputStream oos = clientback.getOos();
			synchronized(oos)
			{
				int bodylength=0;
				Header header = new Header(ClientBack.OROOM,bodylength);
				Roominfo roominfo = new Roominfo(userid,groupid);
				Data sendData = new Data(header,roominfo);
				oos.writeObject(sendData);
				oos.flush();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
