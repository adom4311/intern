package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientBack {
	public static final byte STX = 0x02; // ��� ����
	public static final byte ETX = 0x03; // ��� ��
	public static final byte SIGNUP = 0x01; // ȸ������
	public static final byte LOGIN = 0x02; // �α���
	public static final byte MSG = 0x03; // �Ϲݸ޽���
	public static final byte FRIFIND = 0x04; // ģ��ã��
	
	private Socket socket;
	private ClientGUI gui;
	private ClientHome home;
	private DataInputStream is;
	private DataOutputStream os;
	private String SERVER_ADDR = "127.0.0.1";
	private int PORT = 1993;
	
	public void setGui(ClientGUI clientGUI) {
		this.gui = clientGUI;
	}
	
	public ClientBack() {
		connect();
	}
	
	// intToByte
	public  byte[] intToByteArray(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte)(value >> 24);
		byteArray[1] = (byte)(value >> 16);
		byteArray[2] = (byte)(value >> 8);
		byteArray[3] = (byte)(value);
		return byteArray;
	}

	public void connect() {
		try {
			socket = new Socket(SERVER_ADDR,PORT);
			System.out.println("������ �����");
			
			is = new DataInputStream(socket.getInputStream());
			os = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void signUp(String id, String pw) {
		try {
			int bodylength = id.length() + pw.length() + 1; // ���̵� + �н����� ����Ʈ
			byte sendData[] = new byte[6+bodylength]; // ��ü ���� ������
			
			sendData[0] = STX; // ����?
			sendData[1] = SIGNUP; // ȸ������
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("���� �������� ũ�� : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // ���� ������ ũ��
			byte body[] = new byte[bodylength];
			body = (id + "," + pw).getBytes("UTF-8");
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("���� ������ : " + new String(body) + sendData.length);

			os.write(sendData);
			os.flush();
			
			while(is!=null) {
				int chk = is.readInt();
				if(chk > 0) {
					gui.Alert("ȸ������ ����! �α������ּ���.");
					gui.signUpInvi();
					break;
				}else {
					gui.Alert("ȸ������ ����. ���̵� �ߺ�");
					gui.signUpInvi();
					break;

				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void login(String id, String pw) {
		try {
			int bodylength = id.length() + pw.length() + 1; // ���̵� + �н����� ����Ʈ
			byte sendData[] = new byte[6+bodylength]; // ��ü ���� ������
			
			sendData[0] = STX; // ����?
			sendData[1] = LOGIN; // �α���
			byte[] bodySize = intToByteArray(bodylength);
			System.out.println("���� �������� ũ�� : " + bodylength);
			for (int i = 0; i < bodySize.length; i++) {
				sendData[2+i] = (byte)bodySize[i];
			} // ���� ������ ũ��
			byte body[] = new byte[bodylength];
			body = (id + "," + pw).getBytes("UTF-8");
			
			System.arraycopy(body, 0, sendData, 6, body.length);
			
			System.out.println("���� ������ : " + new String(body) + sendData.length);

			os.write(sendData);
			os.flush();
			
			while(is!=null) {
				int chk = is.readInt();
				if(chk > 0) {
					gui.Alert("�α��� ����!");
					gui.loginInvi();
					gui.setVisible(false);
					home = new ClientHome();
					home.home(this);
					break;
				}else {
					gui.Alert("�α��� ����. ���̵� �Ǵ� ��й�ȣ ����.");
					gui.loginInvi();
					break;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
