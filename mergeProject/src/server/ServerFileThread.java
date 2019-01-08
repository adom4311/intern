package server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFileThread extends Thread{
	String id;
	public static int PORT = 1994;
	ServerSocket serverSocket = null;
	
	ServerFileThread(String id)
	{
		this.id = id;
	}
	
	public void run() {
		
		try {
			serverSocket = new ServerSocket(1994);
			Socket socket = null;
	        InputStream in = null;
	        OutputStream out = null;
	        
	        
	        socket = serverSocket.accept();
	        in = socket.getInputStream();
	        out = new FileOutputStream("C:\\Users\\user\\Desktop\\file\\1234.jpg");
	        
	        byte[] bytes = new byte[16*1024];
			
			int count;
			while ((count = in.read(bytes)) > 0) {
	            out.write(bytes, 0, count);
	        }

	        out.close();
	        in.close();
	        socket.close();
	        serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
