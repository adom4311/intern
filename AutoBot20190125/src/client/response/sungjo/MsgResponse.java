package client.response.sungjo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import client.ClientBack;
import model.vo.Chat;
import model.vo.Data;
import model.vo.Header;

public class MsgResponse {

	public MsgResponse(ClientBack clientback, Data data) {
		try {
			Chat message = (Chat)data.getObject();
			Long groupid = message.getGroupid();
			ObjectMapper mapper = new ObjectMapper();
		
			String line = message.getUserid() + " : " + message.getContent() + "\n";
			
			FileWriter fw = clientback.getChatFileMap().get(groupid);
			if(fw !=null) {
				fw.write(mapper.writeValueAsString(message) + "\n");
			}
			ObjectOutputStream foos = clientback.getRpoos();
			Header header = new Header(clientback.UPDATELASTREAD,0);
			Data senddata = new Data(header,message);
			foos.writeObject(senddata);
			foos.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}