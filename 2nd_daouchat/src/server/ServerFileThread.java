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
	
	public ServerFileThread(Filemessage filemessage, Socket filesocket) { //serverdao 새로 생성 추후 수정시 parameter추가
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
				System.out.println("서버파일 받기쓰레드  시작");
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
				System.out.println("서버 - 팡ㄹ읽기 시작");

				in.read(sizebyte);
				System.out.println("서버 - 팡ㄹ읽기 진행");

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
				System.out.println("서버 - 팡ㄹ읽기 끝");
				out.flush();
				out.close();
				
				ServerDAO sDao=new ServerDAO();

				chk = sDao.insertFile(filemessage.getSenduserid(), filemessage.getGroupid(), dir, time);
				if (chk) {
					System.out.println("성공***********");
				}		
				else
					System.out.println("실패*********");
				
				

				// 파일 브로드캐스팅 같은방 사람들에게..
				System.out.println("파일읽기 서버쓰레드 끝");
				

			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}


