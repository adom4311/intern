package client.response.sangwoo;

import java.io.File;
import java.io.IOException;

import client.ClientBack;
import model.vo.Data;
import model.vo.Roominfo;

public class OutRoomResponse {
	File delfile;
	
	public OutRoomResponse(ClientBack clientback, Data data) {
		Roominfo roominfo = (Roominfo)data.getObject();
		String dir = "chatcontent"+File.separator+roominfo.getUserid()+File.separator+roominfo.getGroupid()+".txt";
		delfile = new File(dir);
		System.out.println(dir);
		try {
			System.out.println("roominfo�� : " + roominfo.getGroupid());
			clientback.getChatFileMap().get(roominfo.getGroupid()).close();
			if(delfile.exists()) {
				
				if(delfile.delete()) {
					System.out.println("���� ����");
				}
				else System.out.println("���� ���� �Ұ�");
			}
			else System.out.println("���� ���� x");
			
			clientback.getChatMap().remove(roominfo.getGroupid());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
