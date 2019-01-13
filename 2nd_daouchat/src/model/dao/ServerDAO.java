package model.dao;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.vo.Chat;
import model.vo.Message;

public class ServerDAO {
	String driver = "org.mariadb.jdbc.Driver";
    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
 
    public ServerDAO() {
        try {
        	Class.forName(driver); // ������db driver
        	con = DriverManager.getConnection(
                    "jdbc:mariadb://127.0.0.1:3306/sw_test",
                    "root",
                    "daou");
        	con.setAutoCommit(false);
            
			System.out.println("DB ���� ������");
		} catch (SQLException e) {
			System.out.println("DB ���� ����");
	        e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("����̹� �ε� ����");
		}
    }

	public int signUp(String id, String pw) {
		int chk = 0;

        if( con != null ) {
            try {
				pstmt = con.prepareStatement("insert into user values(?,?)");
				System.out.println("id");
				pstmt.setString(1, new String(id.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(pw.getBytes("UTF-8"),"UTF-8"));
		        chk = pstmt.executeUpdate();
		        
		        if(chk >0) {
		        	System.out.println("ȸ������ ����");
		        	con.commit();
		        }
		        else {
		        	System.out.println("ȸ������ ����");
		        	con.rollback();
		        }
		        
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}

	public int login(String id, String pw) {
		int chk = 0;
		System.out.println("login �Լ�");
		if( con != null ) {
            try {
				pstmt = con.prepareStatement("select * from user where userid = ? and password = ?");
				pstmt.setString(1, new String(id.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(pw.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	System.out.println("�α��� ����");
		        	return 1;
		        }
		        System.out.println("�α��� ����");
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	} 

	public Object[][] friFind(String tempId) {
		if( con != null ) {
            try {
            	System.out.println("�� : " + tempId);
            	Object rowData[][] = new Object[totalUserCnt(tempId)][3];
				pstmt = con.prepareStatement("select userid from user where userid != ? and userid not in (select friendid from friend where userid = ?)");
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();		        
		        int i=0;
		        while(rs.next()) {
		        	rowData[i][0] = i + 1;
		        	rowData[i][1] = rs.getString(1);
		        	rowData[i++][2] = "";
		        }
		        return rowData;
			} catch (SQLException e) {
				return null;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return null;
	}
	
	/* ģ�� ã�� �� */
	public int totalUserCnt(String tempId) {
		int chk = 0;
		if( con != null ) {
            try {
				pstmt = con.prepareStatement("select count(userid) from user where userid != ? and userid not in (select friendid from friend where userid = ?)");
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	return rs.getInt(1);
		        }
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}
	
	/* ģ�� ��� �� */
	public int totalFriCnt(String tempId) {
		int chk = 0;
		if( con != null ) {
            try {
				pstmt = con.prepareStatement("select count(friendid) from friend where userid = ?");
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	return rs.getInt(1);
		        }
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}
	
	/* �� ����� ���� �Լ�*/
	public int totalRoomCnt(String tempId) {
		int chk = 0;
		if( con != null ) {
            try {
				pstmt = con.prepareStatement("select count(groupid) from chatgroup where userid = ?");
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	return rs.getInt(1);
		        }
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}

	public int addfri(String connectId, String data) {
		int chk = 0;

        if( con != null ) {
            try {
				pstmt = con.prepareStatement("insert into friend values(?,?)");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(data.getBytes("UTF-8"),"UTF-8"));
		        chk = pstmt.executeUpdate();
		        
		        if(chk >0) {
		        	System.out.println("ģ���߰� ����");
		        	con.commit();
		        }
		        else {
		        	System.out.println("ģ���߰� ����");
		        	con.rollback();
		        }
		        
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}

	public Object[][] friList(String connectId) {
		if( con != null ) {
            try {
            	System.out.println("�� : " + connectId);
            	Object rowData[][] = new Object[totalFriCnt(connectId)][3];
				pstmt = con.prepareStatement("select friendid from friend where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();		        
		        int i=0;
		        while(rs.next()) {
		        	rowData[i][0] = i + 1;
		        	rowData[i][1] = rs.getString(1);
		        	rowData[i++][2] = "";
		        }
		        return rowData;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return null;
	}

	public Object[][] roomList(String connectId){
		if(con!=null) {
			try {
				System.out.println("���� : "+connectId);
				Object rowData[][] = new Object[totalRoomCnt(connectId)][2];
				pstmt = con.prepareStatement("select groupname from chatgroup where userid = ?");
//				pstmt = con.prepareStatement("select groupid from chatgroup where userid = ?");
				//groupname  -> groupid �� �ӽú��� 
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();		        
		        int i=0;
		        while(rs.next()) {
		        	rowData[i][0] = i + 1;
		        	rowData[i++][1] = rs.getString(1);
		        }
		        return rowData;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public int createRoom(String connectId, String data[]) { // data�� friendId
		Date today = new Date();
		System.out.println(today);
		int chk = 0;

        if( con != null ) {
            try {
            	// �̹� 1:1ä�ù��� ������ 0�� ����
            	if(selectRoom(connectId,data,ONEROOM) != 0L) {
            		return 0;
            	}
            	
            	/* ä�ù� ���� */
            	String query = "insert into chatgroup(userid, groupname, type) values(?,?,?)";
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(2, new String((connectId+"�� ��").getBytes("UTF-8"),"UTF-8")); // ä�� ���
				pstmt.setByte(3, ONEROOM); // ä�� ���
		        chk = pstmt.executeUpdate();
		        pstmt.close();
		        
		        /* groupid �������� */
		        String query2 = "select LAST_INSERT_ID()";
		        pstmt = con.prepareStatement(query2);
		        rs = pstmt.executeQuery();
		        Long groupid = 0L;
		        while(rs.next()) {
		        	groupid = rs.getLong(1);
		        }
		        pstmt.close();
		        System.out.println("���� db ����� groupid : " + groupid);
		        
		        /* ä�ù� ������ �߰� */
            	String query3 = "insert into chatmember(groupid,userid) values(?,?)";
		        
		        // chatmember�� ������ ���̵� �߰�
		        pstmt = con.prepareStatement(query3);
				pstmt.setLong(1, groupid);
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				chk = pstmt.executeUpdate(); 
		        
		        // chatmember�� �ʴ��� ���̵� �߰�
				for (int i = 0; i < data.length; i++) {
					pstmt.setLong(1, groupid);
					pstmt.setString(2, new String(data[i].getBytes("UTF-8"),"UTF-8"));
					pstmt.executeUpdate();
				}  
		        pstmt.close();
		        
		        con.commit();
		        System.out.println("ä�ù氳�� ����");
//		        
			} catch (SQLException e) {
				// sql �����߻��� ���⼭ rollback????
				try {
		        	System.out.println("ä�ù氳�� ����");
					con.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}

	public Long selectRoom(String connectId, String[] data, byte type) {
		Long groupid = 0L;
		if(con != null) {
			StringBuffer query = new StringBuffer();
			query.append("select groupid, count(groupid) cnt from chatmember where groupid in (select groupid from chatgroup where type =?) and userid in (?");
        	for (int i = 0; i < data.length; i++) {
        		query.append(",?");
			}
        	query.append(") group by groupid having cnt = 2");
			try {
				pstmt = con.prepareStatement(query.toString());
				pstmt.setByte(1, type); // ä��Ÿ��
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8")); 
				for (int i = 0; i < data.length; i++) { // �� ������
					pstmt.setString(3+i, new String(data[i].getBytes("UTF-8"),"UTF-8")); 
				}
				
				rs = pstmt.executeQuery();
				while(rs.next()) {
					groupid = rs.getLong(1);
				}
				pstmt.close();
				
				return groupid;
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return groupid;
	}

	public int insertMSG(Message message) {
		int chk = 0 ;
		if(con != null) {
			String query = "insert into chatcontent values(?,?,?,now())";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(message.getSenduserid().getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(2, message.getGroupid());
		        pstmt.setString(3, new String(message.getMsg().getBytes("UTF-8"),"UTF-8"));

		        chk = pstmt.executeUpdate();
		        pstmt.close();
		        if(chk >=0) {
		        	con.commit();
		        }else
		        	con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return chk;
	}
	
	public boolean insertFile(String userid, Long roomid, String dir, String time) {
		int chk=0;
		if(con!=null) {
			try {
				pstmt=con.prepareStatement("insert into filecontent values(?,?,?,?)");
				pstmt.setString(1, new String(userid.getBytes("UTF-8"),"UTF-8"));
				pstmt.setLong(2, roomid);
				pstmt.setString(3, new String(dir.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(4, new String(time.getBytes("UTF-8"),"UTF-8"));
				chk=pstmt.executeUpdate();
				
				if(chk >=0) {
					System.out.println("�޼��� ���� ����");
		        	con.commit();
		        }else {
		        	con.rollback();
		        	System.out.println("�޼��� ���� ����");
		        }
				
				
			}catch(SQLException e){
				return false;
			}catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public List<String> selectGroupmember(Long sendGroupid) {
		List<String> groupmemberList = new ArrayList<String>();
		
		if(con != null) {
			String query = "select userid from chatmember where groupid = ?";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, sendGroupid);

		        rs = pstmt.executeQuery();
		        
		        while(rs.next()) {
		        	groupmemberList.add(rs.getString(1));
		        }		        
		        
		        pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return groupmemberList;
	}

	public List<Chat> selectchatcontent(Long groupid) {
		List<Chat> chatcontent = new ArrayList<Chat>();
		
		if(con != null) {
			String query = "select * from chatcontent where groupid = ?";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, groupid);

		        rs = pstmt.executeQuery();
		        Chat chat;
		        while(rs.next()) {
		        	chat = new Chat(0L,rs.getString("userid"),rs.getLong("groupid"),rs.getString("content"),rs.getDate("sendtime"),0);
		        	chatcontent.add(chat);
		        }		        
		        
		        pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return chatcontent;
	}
	
	
   
}
    
   
