package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import vo.ServerDAO;
import vo.User;

public class ServerController {
	private ServerSocket serverSocket; // 서버소켓
	private Socket socket; // 받아올 소켓 저장
//    private ServerGUI gui; // 서버 GUI
    private String msg; // 채팅정보
    /* 현재 접속중인 사용자들의 정보 */ 
    private Map<String, DataOutputStream> connClientMap = new HashMap<String, DataOutputStream>();
    
    
    
    public static void main(String[] args) {
		ServerController serverController = new ServerController();
		serverController.setting();
	}
    
    // 서버 실행시 작동되는 함수
    public void setting() {
    	try {
            Collections.synchronizedMap(connClientMap); // Thread-safe
			serverSocket = new ServerSocket(1993); // 소켓 생성
			while(true) {
	    		System.out.println("대기중...");
	    		socket = serverSocket.accept(); // 클라이언트 소켓 저장
	    		System.out.println(socket.getInetAddress() + "에서 접속");
	    		
	    		Receiver receiver = new Receiver(socket); // 서버에 접속된 사용자 쓰레드
	    		receiver.start();
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void addClient(String nick, DataOutputStream out) {
    	connClientMap.put("user1", out);
    }
}



class Receiver extends Thread{
	private DataInputStream in; // 데이터 입력 스트림
    private DataOutputStream out; // 데이터 아웃풋 스트림
    private ObjectInputStream ois; // 오브젝트 인풋 스트림
    private String msg;
    User user;
    ServerDAO sDao;
    int chk;

	public Receiver(Socket socket) {
		try { // 여긴 한번만 실행
			sDao =new ServerDAO();
			out = new DataOutputStream(socket.getOutputStream());
	        in = new DataInputStream(socket.getInputStream());
	        ois = new ObjectInputStream(in);
	        System.out.println("실행");
//	        if(in != null) {
//	        	msg = in.readUTF();
//	        	System.out.println(msg);
//	        }
		} catch (IOException e) {
			e.printStackTrace();
		}			 
	}
	
	@Override
	public void run() {
		try {
			while (ois != null) {
				user = (User)ois.readObject(); // 회원가입 정보 수신
				if(user.getSelect() == 1) { // 회원가입
					System.out.println("ID:"+user.getUserid());
					System.out.println("PW:"+user.getPassword());
					chk = sDao.signup(user); // 데이터베이스 저장
					out.writeInt(chk);
					
//					if(chk >0) { // 회원가입 성공시 쓰레드 정지
//						System.out.println("쓰레드종료");
//						break;
//					}	
//					System.out.println("쓰레드계속");
				}
				else if(user.getSelect() == 2) { // 로그인
					System.out.println("로그인");
					chk = sDao.login(user);
					out.writeInt(chk);
					
//					if(chk >0) { // 로그인 성공시 쓰레드 정지
//						System.out.println("쓰레드종료");
//						break;
//					}	
//					System.out.println("쓰레드계속");
				}
			}
		} catch (IOException e) {
			System.out.println("io에러");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("클래스에러");
			e.printStackTrace();
		}
		
	}
}