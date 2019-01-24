package client.response.sungjo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import client.ClientBack;
import client.gui.Chatwindow;
import model.vo.Chat;
import model.vo.Data;
import model.vo.Header;

public class MsgResponse {

	public MsgResponse(ClientBack clientback, Data data) {
		try {
			Map<Long,Chatwindow> chatMap = clientback.getChatMap();
			Chat message = (Chat)data.getObject();
			Long groupid = message.getGroupid();
			ObjectMapper mapper = new ObjectMapper();
			//ä�������� ���� ä��â�� �����°� ���� ����� �����ؾ���
//			if(chatMap.get(message.getGroupid()) == null) {
//				Chatwindow chatwindow = new Chatwindow(clientback.getId(),message.getGroupid(), clientback, clientback.getfilesocket());
//				chatMap.put(groupid, chatwindow);
//				chatMap.get(groupid).readchatFile();
//				chatwindow.show();
//			} 
			// ä�ù��� ���� ���� ���
			if(chatMap.get(message.getGroupid()) != null) {
				ObjectOutputStream foos = clientback.getRpoos();
				synchronized(foos) // ����ó��
				{
					Header header = new Header(clientback.UPDATELASTREAD,0);
					Data senddata = new Data(header,message);
					foos.writeObject(senddata);
					foos.flush();
				}
				
				String line = message.getUserid() + " : " + message.getContent() + "\n";
				chatMap.get(message.getGroupid()).appendMSG(line);
				
				FileWriter fw = clientback.getChatFileMap().get(groupid);
				if(fw !=null) {
					fw.write(mapper.writeValueAsString(message) + "\n");
					fw.flush();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}