package client.request;

import static java.lang.Math.toIntExact;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import client.ClientBack;

public class SendFileRequest extends Thread{
	ClientBack clientback;
	String file_dir;
	Long groupid;
	
	public SendFileRequest(ClientBack clientback, String file_dir, Long groupid){
		this.clientback=clientback;
		this.file_dir=file_dir;
		this.groupid=groupid;
	}
	public byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}
	
	
	public void run() {
		try
		{
			synchronized(clientback.getfilesocket().getOutputStream())
			{
				System.out.println("센드파일리퀘스트");
				File file = new File(file_dir);
				long length=file.length();
				byte[] sizebyte = longToBytes(length);
				int wordsize = 16*1024;
				byte[] bytes = new byte[wordsize];
				InputStream in = new FileInputStream(file);
				OutputStream out = clientback.getfilesocket().getOutputStream();
				out.write(sizebyte);
				int count;
				while ((count = in.read(bytes)) > 0) {
		            out.write(bytes, 0, count);
		            length-=count;
		            if(length<=wordsize)
		            {
		            	bytes=new byte[toIntExact(length)];
		            }
		            if(length<=0) break;
		        }
				in.close();
				System.out.println("다보냄");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
