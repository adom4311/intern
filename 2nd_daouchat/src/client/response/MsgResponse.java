package client.response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import client.Chatwindow;
import client.ClientBack;
import model.vo.Data;
import model.vo.Message;

public class MsgResponse {

	public MsgResponse(ClientBack clientback, Data data) {
		try {
			Map<Long,Chatwindow> chatMap = clientback.getChatMap();
			Message message = (Message)data.getObject();
			Long groupid = message.getGroupid();
			if(chatMap.get(message.getGroupid()) == null) {
				Chatwindow chatwindow = new Chatwindow(clientback.getId(),message.getGroupid(), clientback, clientback.getfilesocket());
				chatMap.put(message.getGroupid(), chatwindow);
				chatMap.get(groupid).readchatFile();
				chatwindow.show();
			}else {
				String line = message.getSenduserid() + " : " + message.getMsg() + "\n";
				chatMap.get(message.getGroupid()).appendMSG(line);
				//파일에 저장
				String fileFolder = "chatcontent" + File.separator + clientback.getId(); // 한 컴퓨터에서 유저별 데이터를 나누기 위하여
				String fileName = message.getGroupid() + ".txt";
				String filePATH = fileFolder + File.separator + fileName;
				File file = new File(fileFolder);
				if(!file.exists()) { // 폴더가 없는 경우
					file.mkdirs();
				}
				file = new File(filePATH);
				if(!file.exists()) { // 파일 없는 경우
					file.createNewFile();
				}
				
				ObjectOutputStream oos = clientback.getChatFileMap().get(groupid);
				oos.writeObject(message);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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