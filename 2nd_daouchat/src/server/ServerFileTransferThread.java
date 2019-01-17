package server;

import static java.lang.Math.toIntExact;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.swing.JOptionPane;

import model.vo.Filedownmessage;

public class ServerFileTransferThread extends Thread{
	Filedownmessage fdm;
	ServerSocket fileserversocket;
	Socket filesocket;
	File file;
	InputStream in;
	
	ServerFileTransferThread(Filedownmessage fdm,ServerSocket fileserversocket){
		this.fdm=fdm;
		this.fileserversocket=fileserversocket;
		try {
			filesocket = fileserversocket.accept();
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
	
	@Override
	public void run() {
		try {
			System.out.println("Ŭ���̾�Ʈ�� ��û�� ���� ���� ����");
			
			String dir = fdm.getFile_dir();
			Long groupid = fdm.getGroupid();
			file = new File(dir);
			long length = file.length();
			byte[] sizebyte = longToBytes(length);
			int wordsize = 16*1024;
			byte[] bytes = new byte[wordsize];
			try {
				in = new FileInputStream(file);
			}catch(FileNotFoundException e) {
				System.out.println("������ �����Ǿ��ų� �������� �ʽ��ϴ�.");
				Alert("������ �����Ǿ��ų� �������� �ʽ��ϴ�.");
				filesocket.close();
				return;
				
			}
			OutputStream out = filesocket.getOutputStream();
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
			filesocket.close();
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
