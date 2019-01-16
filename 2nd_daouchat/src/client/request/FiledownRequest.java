package client.request;

import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Filedownmessage;
import model.vo.Header;

public class FiledownRequest {
	
	public FiledownRequest(ClientBack clientback,Long groupid,String dir) {
		try {
			ObjectOutputStream oos = clientback.getOos();
			int bodylength=0;
			Header header = new Header(ClientBack.FIDOWN,bodylength);
			Filedownmessage filedownmessage = new Filedownmessage(groupid,dir);
			Data sendData = new Data(header,filedownmessage);
			oos.writeObject(sendData);
			oos.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
