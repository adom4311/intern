package server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dao.ServerDAO;


public class ServerFileThread extends Thread {

	public static final byte STX = 0x02; // 통신 시작
	public static final byte ETX = 0x03; // 통신 끝
	public static final byte SIGNUP = 0x01; // 회원가입
	public static final byte LOGIN = 0x02; // 로그인
	public static final byte MSG = 0x03; // 일반메시지
	public static final byte FRIFIND = 0x04; // 친구찾기
	public static final byte ADDFRI = 0x05; // 친구추가
	public static final byte FMSG = 0x06;// 파일, 이미지 전송

	String id;
	String file;
	String date;
	String groupid;
	public static int PORT = 1994;
	ServerSocket serverSocket = null;
	ServerDAO sDao;
	Map<String, DataOutputStream> currentClientMap;
	Map<String, DataOutputStream> currentClientfileMap;
	Socket filesocket;

	public ServerFileThread(String id, String groupid, String file, ServerDAO sDao, Map<String, DataOutputStream> currentClientMap,
			Map<String, DataOutputStream> currentClientfileMap, Socket filesocket) {
		this.id = id;
		this.file = file;
		this.sDao = sDao;
		this.currentClientMap = currentClientMap;
		this.currentClientfileMap = currentClientfileMap;
		this.filesocket = filesocket;
		this.groupid=groupid;
	}

	// int <-> byte
	public byte[] intToByteArray(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte) (value >> 24);
		byteArray[1] = (byte) (value >> 16);
		byteArray[2] = (byte) (value >> 8);
		byteArray[3] = (byte) (value);
		return byteArray;
	}

	public int byteArrayToInt(byte bytes[]) {
		return ((((int) bytes[0] & 0xff) << 24) | (((int) bytes[1] & 0xff) << 16) | (((int) bytes[2] & 0xff) << 8)
				| (((int) bytes[3] & 0xff)));
	}

	//
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
			Date date = new Date();
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
			String time = transFormat.format(date);
			InputStream in = null;
			OutputStream out = null;
			String[] recdir = file.split("\\\\");
			String dir = "C:\\Users\\user\\Desktop\\file\\server\\" + recdir[recdir.length - 1];
			boolean chk;

			in = filesocket.getInputStream();
			out = new FileOutputStream(dir);

			byte[] bytes = new byte[16 * 1024];
			byte[] sizebyte = new byte[8];
			int count;
			in.read(sizebyte);
			long length = bytesToLong(sizebyte);
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
				length -= count;
				System.out.println(length);
				if (length <= 0)
					break;
			}
			
			out.close();
			// sDao=new ServerDAO();

			chk = sDao.insertFile(id, groupid, dir, time);
			if (chk)
				System.out.println("성공***********");
			else
				System.out.println("실패*********");
			System.out.println("성공여부");
			filebroadcast(dir, id, groupid);

			// 파일 브로드캐스팅 같은방 사람들에게..

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void filebroadcast(String dir, String id, String groupid) {
		synchronized (currentClientMap) {
			DataOutputStream os;
			DataOutputStream fos;

			try {
				int bodylength = id.getBytes("UTF-8").length + dir.getBytes("UTF-8").length + 1;
				byte sendData[] = new byte[6 + bodylength];

				sendData[0] = STX;
				sendData[1] = FMSG;
				byte[] bodySize = intToByteArray(bodylength);
				System.out.println("보낼 데이터 크기: " + bodylength);
				for (int i = 0; i < bodySize.length; i++) {
					sendData[2 + i] = (byte) bodySize[i];
				}
				byte body[] = new byte[bodylength];
				body = (id + "," + dir).getBytes("UTF-8");
				System.arraycopy(body, 0, sendData, 6, bodylength);
				List<String> groupmember = sDao.selectGroupmember(groupid);
				for (int i = 0; i < groupmember.size(); i++) {
					os = currentClientMap.get(groupmember.get(i));
					fos = currentClientfileMap.get(groupmember.get(i));
					System.out.println(os);
					System.out.println(fos);
					if (os != null) {
						os.write(sendData);
						os.flush();
					}
					if (fos != null) {
						new FileTransferThread(groupmember.get(i).toString(), dir, fos).start();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	private class FileTransferThread extends Thread{
		String id;
		String filename;
		DataOutputStream fos;
		
		
		
		
		FileTransferThread(String id, String filename, DataOutputStream fos)
		{
			this.id=id;
			this.filename=filename;
			this.fos = fos;
			
		}
		
		public void run() {
			//다른 포트를 통해서 파일을 전송
			//db에 id와 filename저장할것.
			try {
				File file = new File(filename);
				long length = file.length();
				byte[] sizebyte = longToBytes(length);
				byte[] bytes = new byte[16 * 1024];
				InputStream in = new FileInputStream(file);
				fos.write(sizebyte);
				System.out.println(length);
				int count;
		        while ((count = in.read(bytes)) > 0) {
		            fos.write(bytes, 0, count);
		            length-=count;
		            System.out.println(length);
		            if(length<=0) break;
		        }
				System.out.println("끝");
				in.close();
		       
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
