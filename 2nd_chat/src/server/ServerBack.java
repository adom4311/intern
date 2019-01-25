package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import model.dao.ServerDAO;
import model.vo.Chat;
import model.vo.Data;
import model.vo.User;

public class ServerBack {
	public static final int SIGNUP = 1; // 회원가입
	public static final int LOGIN = 2; // 로그인
	public static final int MSG = 3; // 일반메시지
	public static final int FRIFIND = 4; // 친구찾기
	public static final int ADDFRI = 5; // 친구추가
	public static final int FMSG = 6;// 파일, 이미지 전송
	public static final int CREATEROOM = 7; // 1:1 채팅방 개설
	public static final int FRILIST = 8; // 친구목록
	public static final int OPENCHAT = 9; // 채팅방 오픈시 DB 채팅 데이터 가져오기
	public static final int ROOM = 10; //채팅방목록
	public static final int GROUPROOMLIST = 11; // 그룹 채팅방 용 친구목록
	public static final int UPDATELASTREAD = 12; // 읽음처리용
	public static final int CREATEGROUPROOM = 13;  // 그룹채팅방  개설
	public static final int ROOMOPEN = 14; // 선택된 채팅방 오픈
	public static final int FILIST = 15;//파일 목록
	public static final int FIDOWN =16;//파일 다운 요청
	
	public static final int ROOMNAME =17;//방명 변경
	public static final int DELETEFRIEND =18;// 친구 삭제
	public static final int CHATFRILIST =19;// 채팅방 친구 리스트
	
	public static final int OROOM = 20;//그룹방 날가기 요청
	public static final int GAL = 21;//갤러리 기능 요청
	public static final int AMEM = 22;//채팅방 멤버 추가 가능리스트 요청
	public static final int MEM = 23;//채팅방 멤버 추가 요창


    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;

	private int PORT = 1993;
	private int FILE_PORT = 1994;
	private int READ_PORT = 1995;
	
	private ServerSocket serverSocket; // 서버소켓
	private ServerSocket fileserverSocket;
	

	private ServerSocket readProcessingSocket;

	private Socket socket; // 받아올 소켓
	private Socket socket2;

	String connectId;

	/* 현재 접속중인 사용자들의 정보 */
	private Map<String, ObjectOutputStream> currentClientMap = new HashMap<String, ObjectOutputStream>();
	/* groupid 별 현재 사용자 정보 */
	private Map<Long, Map<String,ObjectOutputStream>> groupidClientMap = new HashMap<Long,Map<String,ObjectOutputStream>>();


		
	public void setGroupidClientMap(Map<Long, Map<String, ObjectOutputStream>> groupidClientMap) {
		this.groupidClientMap = groupidClientMap;
	}
	
	public Map<Long , Map<String, ObjectOutputStream>> getGroupidClientMap(){
		return groupidClientMap;
	}
	public Map<String, ObjectOutputStream> getCurrentClientMap() {
		return currentClientMap;
	}
	public void setCurrentClientMap(Map<String, ObjectOutputStream> currentClientMap) {
		this.currentClientMap = currentClientMap;
	}
	
	public ServerSocket getFileserverSocket() {
		return fileserverSocket;
	}

	public void setFileserverSocket(ServerSocket fileserverSocket) {
		this.fileserverSocket = fileserverSocket;
	}
	
	public static void main(String[] args) {
		ServerBack serverBack = new ServerBack();
		serverBack.setting();
	}
	
	private void createGroupidMap() {
		ServerDAO sDao = new ServerDAO();
		ArrayList<Long> list = sDao.selectGroupid();
		for(Long groupid : list) {
			groupidClientMap.put(groupid,new HashMap<String,ObjectOutputStream>());
		}
	}

	

	public void setting() {
		try {
			serverSocket = new ServerSocket(PORT); // 서버 소켓 생성
			fileserverSocket = new ServerSocket(FILE_PORT);
			readProcessingSocket = new ServerSocket(READ_PORT);

			System.out.println("---서버 오픈---");
			
			//groupid 별 map setting
			createGroupidMap();

			OldDataDelete odd = new OldDataDelete(this);
			Timer scheduler = new Timer();
			scheduler.scheduleAtFixedRate(odd, 60 * 1000, 24 * 60 * 60 * 1000); // 1분 후부터 1일 간격 (2~3일 데이터 저장)
			rpmReceiver rpmreceiver = new rpmReceiver(readProcessingSocket);
			rpmreceiver.start();
			while(true) {
				socket = serverSocket.accept(); // 클라이언트 소켓 저장
				System.out.println(socket.getInetAddress() + "에서 접속"); // IP
				Receiver receiver = new Receiver(this, socket);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	class rpmReceiver extends Thread{
		private ServerSocket socket;
		public rpmReceiver(ServerSocket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					socket2 = socket.accept();
					RpReceiver rpreceiver = new RpReceiver(socket2);
					rpreceiver.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

