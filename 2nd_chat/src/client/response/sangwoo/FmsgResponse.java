package client.response.sangwoo;

import client.ClientBack;
import client.request.sangwoo.SendMessageRequest;
import model.vo.Data;
import model.vo.Filemessage;

public class FmsgResponse {

	public FmsgResponse(ClientBack clientback, Data data) {
		Filemessage filemessage = (Filemessage)data.getObject();
		String [] filename_tok = filemessage.getfile_dir().split("\\\\");
		String filename = filename_tok[filename_tok.length-1];
		String msg = filemessage.getSenduserid()+"���� [ "+filename+" ] ��(��) ���½��ϴ�. ���ϸ�Ͽ��� Ȯ���� �ּ���";
		new SendMessageRequest(clientback,msg,filemessage.getGroupid());
	}

}
