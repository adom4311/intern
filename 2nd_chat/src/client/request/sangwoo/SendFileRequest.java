package client.request.sangwoo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import javax.swing.JOptionPane;

import client.ClientBack;

public class SendFileRequest extends Thread{
	ClientBack clientback;
	String file_dir;
	Long groupid;
	Socket filesocket;
	
	public SendFileRequest(ClientBack clientback, String file_dir, Long groupid){
		this.clientback=clientback;
		this.file_dir=file_dir;
		this.groupid=groupid;
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
	public byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}
	public void Alert(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
	
	public void run() {
		InputStream in = null;
		OutputStream out = null;
		try
		{
			System.out.println("센드파일리퀘스트");
			File file = new File(file_dir);
			long length=file.length();
			
			out = filesocket.getOutputStream();
			if(length>=1.0737e+9) {//파일의 크기가 2GB를 넘을 경우
				out.write(longToBytes(-1));
				filesocket.close();
				System.out.println("전송하기에는 파일의 크기가 너무 큽니다.");
				Alert("전송하기에는 파일의 크기가 너무 큽니다.");
				return ;
			}
			in = new FileInputStream(file);
			byte[] sizebyte = longToBytes(length);
			int wordsize = 16*1024;
			byte[] bytes = new byte[wordsize];
			
			out.write(sizebyte);
			int count;
			while ((count = in.read(bytes)) > 0) {
	            out.write(bytes, 0, count);
	            length-=count;
	            System.out.println(length);
//	            if(length<=wordsize)
//	            {
//	            	bytes=new byte[toIntExact(length)];
//	            }
	            if(length<=0) break;
	        }
			in.close();
			System.out.println("다보냄");
			filesocket.close();
			
		}catch(SocketException se) {
			Alert("인터넷 연결을 확인해 주세요");
			se.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				filesocket.close();
				in.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}
