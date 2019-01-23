package client.response.sangwoo;

import java.util.ArrayList;

import client.ClientBack;
import client.gui.Addmembergui;
import model.vo.Amemessage;
import model.vo.Data;

public class Addmemberresponse {
	
	public Addmemberresponse(ClientBack clientback, Data data) {
		Amemessage amem = (Amemessage)data.getObject();
		ArrayList<String> avail_member = amem.getMem_avail();
		Long groupid = amem.getGroupid();
		new Addmembergui(avail_member,groupid,clientback);
	}
}
