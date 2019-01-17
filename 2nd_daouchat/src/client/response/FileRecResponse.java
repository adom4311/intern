package client.response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import client.ClientBack;
import model.vo.Data;

public class FileRecResponse extends Thread{
	ClientBack clientback;
	Data data;
	Socket filesocket;
	public FileRecResponse(ClientBack clientback,Data data) {
		this.clientback=clientback;
		this.data=data;
		try {
			filesocket=new Socket("127.0.0.1",1994);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		return buffer.getLong();
	}
	
	@Override
	public void run() {
		try {
			String dir = (String)data.getObject();
			String[] dirtoken = dir.split("\\\\");
			String savedir = "C:\\Users\\user\\Desktop\\file\\server\\"+clientback.getId();
			File file = new File(savedir);
			if(!file.exists()) {
				file.mkdirs();
			}
			System.out.println("클라이언트에서 파일 받기 시작");
			InputStream in = filesocket.getInputStream();
			OutputStream out = new FileOutputStream(savedir+"\\"+dirtoken[dirtoken.length-1]);
			int wordsize=16*1024;
			byte[] bytes = new byte[wordsize];
			byte[] sizebyte = new byte[8];
			int count;
			in.read(sizebyte);
			long length = bytesToLong(sizebyte);
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
				length -= count;
				System.out.println(length);
				/*
				 * if(length<wordsize) { bytes=new byte[toIntExact(length)]; }
				 */
				if (length <= 0)
				{
					bytes=null;
					break;
				}	
			}
			out.flush();
			out.close();
			filesocket.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
