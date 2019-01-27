package client.request.sangwoo;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client.ClientBack;
import model.vo.Data;
import model.vo.Filemessage;
import model.vo.Header;


public class SendFileMessageRequest {
	
	public SendFileMessageRequest(ClientBack clientback, String file_dir, Long groupid) {
		try {
			System.out.println("�������ϸ޽���������Ʈ");
			ObjectOutputStream oos = clientback.getOos();
			synchronized(oos)
			{
				int bodylength =0;
				Header header = new Header(ClientBack.FMSG,bodylength);
				Filemessage filemessage = new Filemessage(clientback.getId(),groupid,file_dir,null);
				Data sendData = new Data(header,filemessage);
				oos.writeObject(sendData);
				oos.flush();
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
