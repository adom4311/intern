package client.response.sungjo;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

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
			//채팅전송이 오면 채팅창이 켜지는건 따로 기능을 제작해야함
//			if(chatMap.get(message.getGroupid()) == null) {
//				Chatwindow chatwindow = new Chatwindow(clientback.getId(),message.getGroupid(), clientback, clientback.getfilesocket());
//				chatMap.put(groupid, chatwindow);
//				chatMap.get(groupid).readchatFile();
//				chatwindow.show();
//			} 
			// 채팅방이 켜져 있을 경우
			if(chatMap.get(message.getGroupid()) != null) {
				ObjectOutputStream foos = clientback.getOos();
				synchronized(foos) // 읽음처리
				{
					Header header = new Header(clientback.UPDATELASTREAD,0);
					Data senddata = new Data(header,message);
					foos.writeObject(senddata);
					foos.flush();
				}
				
				String line = message.getUserid() + " : " + message.getContent() + "\n";
				chatMap.get(message.getGroupid()).appendMSG(line);
				
				ObjectOutputStream oos = clientback.getChatFileMap().get(groupid);
				if(oos !=null) {
					oos.writeObject(message);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}