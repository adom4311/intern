package client.response;

import client.ClientBack;
import model.vo.Data;
import model.vo.Filelist;

public class FileListResponse {
	
	public FileListResponse(ClientBack clientback, Data data) {
		Filelist flist = (Filelist)data.getObject();
		Object rowdata[][] = flist.getDirs();
		Long groupid = flist.getGroupid();
		
//		for(int i=0;i<rowdata.length;i++) {
//			System.out.println(rowdata[i][0]);
//		}
		
		clientback.getChatMap().get(groupid).getCFG().insertable(rowdata);
		
	}

}
