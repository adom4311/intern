package client.response;

import java.util.Map;

import client.Chatwindow;
import client.ClientBack;
import model.vo.Data;
import model.vo.Message;

public class MsgResponse {

	public MsgResponse(ClientBack clientback, Data data) {
		Map<Long,Chatwindow> chatMap = clientback.getChatMap();
		Message message = (Message)data.getObject();
		if(chatMap.get(message.getGroupid()) == null) {
			Chatwindow chatwindow = new Chatwindow(clientback.getId(),message.getGroupid(), clientback, clientback.getfilesocket());
			chatMap.put(message.getGroupid(), chatwindow);
			chatwindow.show();
		}else {
			chatMap.get(message.getGroupid()).appendMSG(message.getSenduserid() + " : " + message.getMsg() + "\n");
		}
	}
}

/* message */
//else if (headerBuffer[1] == MSG) {
//	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//	int read;
//	reciveData = new byte[datalength];
//
//	// 파일 받을때까지 계속
//	while ((read = is.read(reciveData, 0, reciveData.length)) != -1) {
//		buffer.write(reciveData, 0, read);
//		datalength -= read;
//		if (datalength <= 0) { // 다 받으면 break
//			break;
//		}
//	}
//	System.out.println(buffer.toString("UTF-8"));
//	String data[] = buffer.toString("UTF-8").split(",");
//	buffer.flush();
//	System.out.println("data1의 크기는 : " + data[0].length());
//	String userid = data[0];
//	String groupid = data[1];
//	String msg = data[2];
//
//	if(chatMap.get(groupid) == null) {
//		chatwindow = new Chatwindow(id, groupid, clientback, filesocket );
//		chatMap.put(groupid, chatwindow);
//		chatwindow.show();
//	}else {
//		chatMap.get(groupid).appendMSG(data[0] + ":" + data[2] + "\n");
//	}
//}