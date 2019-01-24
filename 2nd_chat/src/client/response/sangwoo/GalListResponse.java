package client.response.sangwoo;

import java.io.File;
import java.util.ArrayList;

import client.ClientBack;
import client.gui.Galarygui;
import client.request.sangwoo.FiledownRequest;
import model.vo.Data;
import model.vo.Galmessage;

public class GalListResponse {
	Galarygui galgui;
	ArrayList<String> images;
	
	public GalListResponse(ClientBack clientback, Data data, Galarygui galgui) {
		this.galgui = galgui;
		Galmessage galmessage = (Galmessage)data.getObject();
		images = galmessage.getImages();
		String path;
		for(String image : images) {
			System.out.println("**@**@**@*@*@*"+image);
			String []filetokens = image.split("\\\\");
			path = "images\\"+galmessage.getGroupid()+"\\"+filetokens[filetokens.length-1];
			File file = new File(path);
			if(!file.exists())
			{
				new FiledownRequest(clientback,galmessage.getGroupid(),image,true);				
			}
		}
	
		
	}
	public Galarygui show() {
		if(galgui==null)
		{
			galgui=new Galarygui(images);
		}
		galgui.getFrame().setVisible(true);
		return galgui;
	}
}
