package client.request.sangwoo;

import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.ChatMember;
import model.vo.Data;
import model.vo.Header;

public class Memberrequest {
	
	public Memberrequest(ClientBack clientback,Long groupid, String userid) {
		try {
			ObjectOutputStream oos = clientback.getOos();
			synchronized(oos) {
				int bodylength = 0;
				Header header = new Header(ClientBack.MEM,bodylength);
				ChatMember member = new ChatMember(userid,groupid);
				Data sendData = new Data(header,member);
				oos.writeObject(sendData);
				oos.flush();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
