package server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static java.lang.Math.toIntExact;

import model.dao.ServerDAO;
import model.vo.Data;
import model.vo.Filemessage;
import model.vo.Header;


public class ServerFileThread extends Thread {


	Filemessage filemessage;
	//ServerDAO sDao;
	Socket filesocket;
	
	public ServerFileThread(Filemessage filemessage, Socket filesocket) { //serverdao ���� ���� ���� ������ parameter�߰�
		this.filemessage = filemessage;
		//this.sDao = sDao;
		this.filesocket = filesocket;
		
	}

	// long(8byte)<->byte
	public byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

	public long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);

		return buffer.getLong();
	}

	//
	public void run() {

		try {
			synchronized(filesocket.getInputStream())
			{
				System.out.println("�������� �ޱ⾲����  ����");
				Date date = new Date();
				SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
				String time = transFormat.format(date);
				InputStream in = null;
				OutputStream out = null;
				String[] recdir = filemessage.getfile_dir().split("\\\\");
				String dir = "C:\\Users\\user\\Desktop\\file\\server\\" + recdir[recdir.length - 1];
				boolean chk;

				in = filesocket.getInputStream();
				out = new FileOutputStream(dir);
				int wordsize = 16*1024;
				byte[] bytes = new byte[wordsize];
				byte[] sizebyte = new byte[8];
				int count;
				System.out.println("���� - �Τ��б� ����");

				in.read(sizebyte);
				System.out.println("���� - �Τ��б� ����");

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
				System.out.println("���� - �Τ��б� ��");
				out.flush();
				out.close();
				
				ServerDAO sDao=new ServerDAO();

				chk = sDao.insertFile(filemessage.getSenduserid(), filemessage.getGroupid(), dir, time);
				if (chk) {
					System.out.println("����***********");
				}		
				else
					System.out.println("����*********");
				
				

				// ���� ��ε�ĳ���� ������ ����鿡��..
				System.out.println("�����б� ���������� ��");
				

			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}


