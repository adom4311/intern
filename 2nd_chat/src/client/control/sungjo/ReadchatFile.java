package client.control.sungjo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.parser.JSONParser;

import client.ClientBack;
import client.gui.Chatwindow;
import model.vo.Chat;

public class ReadchatFile {

	public ReadchatFile(ClientBack clientback, Long groupid) {
		Map<Long, Chatwindow> chatMap = clientback.getChatMap();
		try {
			if(chatMap.get(groupid) == null) { // 채팅방이 없으면 개설
				System.out.println("채티방 개설");
				Chatwindow chatwindow = new Chatwindow(clientback.getId(), groupid, clientback);
				chatMap.put(groupid, chatwindow);
				chatMap.get(groupid).readchatFile();
//				chatwindow.show();
			}else { // 채팅방이 있으면 파일에서 읽어오기 
				String fileFolder = "chatcontent" + File.separator + clientback.getId(); // 한 컴퓨터에서 유저별 데이터를 나누기 위하여
				String fileName = groupid + ".txt";
				String filePATH = fileFolder + File.separator + fileName;
				File file = new File(fileFolder);
				if(!file.exists()) { // 폴더가 없는 경우
					file.mkdirs();
				}
				file = new File(filePATH);
				if(!file.exists()) { // 파일 없는 경우
					file.createNewFile();
				}
				
				JSONParser parser = new JSONParser();
				ObjectMapper mapper = new ObjectMapper();
				
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String str;
				Chat c;
				while((str = br.readLine())!=null) {
					c = mapper.readValue(str, Chat.class);
					clientback.getChatMap().get(groupid).appendMSG(c.getUserid() + " : " + c.getContent() + "\n");
				}
				br.close();
				fr.close();

				FileWriter fw = new FileWriter(file,true);
				clientback.getChatFileMap().put(groupid, fw);

				clientback.openChat(groupid);
			}
			// db에 저장된 채팅 불러오기
			System.out.println("db데이터 요청하기");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
