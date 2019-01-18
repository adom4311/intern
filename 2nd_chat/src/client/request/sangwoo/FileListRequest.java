package client.request.sangwoo;

import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Header;

public class FileListRequest {
	
	public FileListRequest(ClientBack clientBack,Long groupid) {
		try {
			ObjectOutputStream oos = clientBack.getOos();
			synchronized(oos)
			{
				int bodylength=0;
				Header header = new Header(ClientBack.FILIST,bodylength);
				Data sendData = new Data(header,groupid);
				oos.writeObject(sendData);
				oos.flush();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
