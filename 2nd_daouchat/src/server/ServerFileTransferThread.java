package server;

import static java.lang.Math.toIntExact;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import model.vo.Filedownmessage;

public class ServerFileTransferThread extends Thread{
	Filedownmessage fdm;
	DataOutputStream fos;
	
	ServerFileTransferThread(Filedownmessage fdm,DataOutputStream fos){
		this.fdm=fdm;
		this.fos=fos;
	}
	
	public byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}
	
	@Override
	public void run() {
		try {
			synchronized(fos) {
				System.out.println("클라이언트가 요청한 파일 전송 시작");
				
				String dir = fdm.getFile_dir();
				Long groupid = fdm.getGroupid();
				File file = new File(dir);
				long length = file.length();
				byte[] sizebyte = longToBytes(length);
				int wordsize = 16*1024;
				byte[] bytes = new byte[wordsize];
				InputStream in = new FileInputStream(file);
				OutputStream out = fos;
				out.write(sizebyte);
				int count;
				while ((count = in.read(bytes)) > 0) {
		            out.write(bytes, 0, count);
		            length-=count;
		            if(length<wordsize)
		            {
		            	bytes=new byte[toIntExact(length)];
		            }
		            if(length<=0) break;
		        }
				in.close();
			}
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
