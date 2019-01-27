package client.response.sungjo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import client.ClientBack;
import model.vo.Chat;
import model.vo.ChatcontentList;
import model.vo.Data;

public class OpenchatResponse {

	public OpenchatResponse(ClientBack clientback, Data data) {
		try {
			FileWriter fw;
			ObjectMapper mapper = new ObjectMapper();
			// 서버 파일
			ChatcontentList chatcontentList = (ChatcontentList)data.getObject();
			Long groupid = chatcontentList.getGroupid();
			List<Chat> chatcontent = chatcontentList.getChatcontent();
			
			System.out.println("서버데이터 사이즈 " + chatcontent.size()); 
			if(chatcontent.size() > 0) {
//				Long groupid  = chatcontent.get(0).getGroupid();
				fw = clientback.getChatFileMap().get(groupid);
				for(Chat content : chatcontent) {
					fw.write(mapper.writeValueAsString(content) + "\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
