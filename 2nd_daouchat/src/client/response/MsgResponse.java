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
			if(chatMap.get(message.getGroupid()) == null) {
				Chatwindow chatwindow = new Chatwindow(clientback.getId(),message.getGroupid(), clientback, clientback.getfilesocket());
				chatMap.put(message.getGroupid(), chatwindow);
				chatwindow.show();
			}else {
				String line = message.getSenduserid() + " : " + message.getMsg() + "\n";
				chatMap.get(message.getGroupid()).appendMSG(line);
				//���Ͽ� ����
				String fileFolder = "chatcontent" + File.separator + clientback.getId(); // �� ��ǻ�Ϳ��� ������ �����͸� ������ ���Ͽ�
				String fileName = message.getGroupid() + ".txt";
				String filePATH = fileFolder + File.separator + fileName;
				File file = new File(fileFolder);
				if(!file.exists()) { // ������ ���� ���
					file.mkdirs();
				}
				file = new File(filePATH);
				if(!file.exists()) { // ���� ���� ���
					file.createNewFile();
				}
				
				JSONObject json = new JSONObject();
				json.put("userid", message.getSenduserid());
				json.put("groupid", message.getGroupid());
				json.put("msg", message.getMsg());
				json.put("date", message.getSendtime());
				
				BufferedWriter bufferwriter = new BufferedWriter(new FileWriter(file,true));
				bufferwriter.write(json.toJSONString());
				bufferwriter.close();
				
				
				System.out.println("�Ľ���");
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(new FileReader(file));
				
				JSONObject jsonobject = (JSONObject) obj;
				
				System.out.println(jsonobject.get("userid"));
				
				System.out.println("�Ľ���");
				
				
				
//				
//				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file,true));
//				for(int i=0;i<300;i++) {
//					oos.writeObject(message);
//				}
//				oos.flush();
//				oos.close();
//				
//				FileInputStream fis = new FileInputStream(file);
//				ObjectInputStream ois = new ObjectInputStream(fis);
//				Message msg;
//				System.out.println("���̺�1 : " +fis.available());
//				while(fis.available() > 0){
//					msg = (Message)ois.readObject();
//					System.out.println("��ȭ���� :" + msg.getMsg());
//				}
//				ois.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
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
//	// ���� ���������� ���
//	while ((read = is.read(reciveData, 0, reciveData.length)) != -1) {
//		buffer.write(reciveData, 0, read);
//		datalength -= read;
//		if (datalength <= 0) { // �� ������ break
//			break;
//		}
//	}
//	System.out.println(buffer.toString("UTF-8"));
//	String data[] = buffer.toString("UTF-8").split(",");
//	buffer.flush();
//	System.out.println("data1�� ũ��� : " + data[0].length());
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