package server.sangwoo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.dao.ServerDAO;
import model.vo.Data;
import model.vo.Filemessage;
import model.vo.Header;


public class ServerFileThread extends Thread {

	public static final int FMSG = 6;// 파일, 이미지 전송

	Filemessage filemessage;
	//ServerDAO sDao;
	ServerSocket serversocket;
	Socket filesocket;
	String dir;
	InputStream in;
	OutputStream out;
	ObjectOutputStream oos;
	
	public ServerFileThread(Filemessage filemessage, ServerSocket serversocket,ObjectOutputStream oos) { //serverdao 새로 생성 추후 수정시 parameter추가
		this.filemessage = filemessage;
		this.oos = oos;
		//this.sDao = sDao;
		try {
			filesocket=serversocket.accept();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
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
			Long start = System.currentTimeMillis();
			System.out.println("시작 ******************"+start);
			System.out.println("서버파일 받기쓰레드  시작");
			Date date = new Date();
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
			String time = transFormat.format(date);
			String[] recdir = filemessage.getfile_dir().split("\\\\");
			dir = "C:\\Users\\Naya\\Desktop\\file\\server\\" + time+recdir[recdir.length - 1];
			boolean chk;
			int wordsize = 16*1024;
			byte[] bytes = new byte[wordsize];
			byte[] sizebyte = new byte[8];
			int count;
			System.out.println("서버 - 팡ㄹ읽기 시작");
			in = filesocket.getInputStream();
			in.read(sizebyte);
			out = new FileOutputStream(dir);
			System.out.println("서버 - 팡ㄹ읽기 진행");

			long length = bytesToLong(sizebyte);
			
			if(length==-1) {//파일의 크기가 2GB를 넘을 경우
				out.close();
				File deletefile = new File(dir);
				deletefile.delete();
			}
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
				length -= count;
				//System.out.println(length);
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
			Long end = System.currentTimeMillis();
			System.out.println("끝*********************"+end);
			System.out.println("총시간 ****************"+(end-start));
			ServerDAO sDao=new ServerDAO();

			chk = sDao.insertFile(filemessage.getSenduserid(), filemessage.getGroupid(), dir);
			if (chk) {
				System.out.println("성공***********");
				Header header = new Header(FMSG,0);
				Data sendData = new Data(header,filemessage);
				synchronized(oos) {
					oos.writeObject(sendData);
					oos.flush();
				}
			}		
			else
				System.out.println("실패*********");
			
			

			// 파일 브로드캐스팅 같은방 사람들에게..
			System.out.println("파일읽기 서버쓰레드 끝");
			filesocket.close();

			
			
		} catch(SocketException e) {

			try {
				filesocket.close();
				out.close();
				File deletfile = new File(dir);
				deletfile.delete();
				System.out.println("파일받기에 실패한 파일이 삭제되었습니다.");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			

	}
}


