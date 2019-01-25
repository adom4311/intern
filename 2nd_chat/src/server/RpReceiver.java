package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import model.dao.ServerDAO;
import model.vo.Chat;
import model.vo.Data;
import model.vo.User;

public class RpReceiver extends Thread{
	private ServerDAO sDao;
	private Socket socket;
	private ObjectInputStream ois;
	private String connectId;
	public RpReceiver(Socket socket) {
		try {
			sDao = new ServerDAO();
			this.socket = socket;
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			while(ois != null){
				Data data = (Data) ois.readObject();
				/* 김성조 인턴사원 */
				if(data.getHeader().getMenu() == ServerBack.LOGIN) {
					User user = (User)data.getObject();
					user = sDao.login(user.getUserid(),user.getPassword());
					if(user != null) {
						connectId = user.getUserid().toLowerCase(); // serverBack의 connectId를 접속자로
					}

				}
				/* 김성조 인턴사원 */
				else if(data.getHeader().getMenu() == ServerBack.UPDATELASTREAD) {
					Chat message = (Chat)data.getObject();
					int result = sDao.updatereadtime(connectId,message);
				}
			}
		}catch (SocketException e) {
			try {
				socket.close();
				System.out.println(connectId + "님이 클라이언트 종료하여 쓰레드 종료합니다.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
}
