package client.control.sungjo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.parser.JSONParser;

import client.ClientBack;
import model.vo.Chat;

public class ReadchatFile {
	public ArrayList<Chat> list;
	public ReadchatFile(ClientBack clientback, Long groupid) {
		try {
				String fileFolder = "chatcontent" + File.separator + clientback.getId(); // �� ��ǻ�Ϳ��� ������ �����͸� ������ ���Ͽ�
				String fileName = groupid + ".txt";
				String filePATH = fileFolder + File.separator + fileName;
				File file = new File(fileFolder);
				if(!file.exists()) { // ������ ���� ���
					file.mkdirs();
				}
				file = new File(filePATH);
				if(!file.exists()) { // ���� ���� ���
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
				}
				br.close();
				fr.close();

				FileWriter fw = new FileWriter(file,true);
				clientback.getChatFileMap().put(groupid, fw);

				clientback.openChat(groupid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
