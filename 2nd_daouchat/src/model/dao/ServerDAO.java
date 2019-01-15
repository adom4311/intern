package model.dao;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.vo.Chat;
import model.vo.ChatMember;
import static common.JDBCTemplate.*;
public class ServerDAO {
	String driver = "org.mariadb.jdbc.Driver";
    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
 
    public ServerDAO() {
//        try {
//        	Class.forName(driver); // ������db driver
//        	con = DriverManager.getConnection(
//                    "jdbc:mariadb://127.0.0.1:3306/sw_test",
//                    "root",
//                    "daou");
//        	con.setAutoCommit(false);
//            
//			System.out.println("DB ���� ������");
//		} catch (SQLException e) {
//			System.out.println("DB ���� ����");
//	        e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			System.out.println("����̹� �ε� ����");
//		}
    }

	public int signUp(String id, String pw) {
		con = getConnection();
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
		        
		        close(pstmt);
		        close(con);
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}

	public int login(String id, String pw) {
		con = getConnection();
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
		        	pstmt.close();
		        	return 1;
		        }
		        System.out.println("�α��� ����");
		        

		        close(pstmt);
		        close(con);
		        System.out.println("�α��� Ŀ�ؼ� ����");

			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	} 

	public Object[][] friFind(String tempId) {
		con = getConnection();
		if( con != null ) {
            try {
            	int totalcount = 0;
            	pstmt = con.prepareStatement("select count(userid) from user where userid != ? and userid not in (select friendid from friend where userid = ?)");
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalcount =  rs.getInt(1);
		        }
            	
            	Object rowData[][] = new Object[totalcount][3];
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
		        

		        close(pstmt);
		        close(con);
		        System.out.println("��ü ģ��ã�� Ŀ�ؼ� ����");

		        
		        return rowData;
			} catch (SQLException e) {
				return null;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return null;
	}
	
	/* �� ����� ���� �Լ�*/
	public int totalRoomCnt(String tempId) {
		con = getConnection();
		int chk = 0;
		if( con != null ) {
            try {
				pstmt = con.prepareStatement("select count(groupid) from chatgroup where userid = ?");
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	return rs.getInt(1);
		        }
		        close(pstmt);
		        close(con);
		        System.out.println("���� Ŀ�ؼ� ����");
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}

	public int addfri(String connectId, String data) {
		con = getConnection();
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
		        close(pstmt);
		        close(con);
		        System.out.println("ģ���߰� Ŀ�ؼ� ����");
		        
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}

	public Object[][] friList(String connectId) {
		con = getConnection();
		if( con != null ) {
            try {
            	int totalcnt = 0;
            	pstmt = con.prepareStatement("select count(friendid) from friend where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalcnt =  rs.getInt(1);
		        }            	
            	
            	
            	Object rowData[][] = new Object[totalcnt][3];
				pstmt = con.prepareStatement("select friendid from friend where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();		        
		        int i=0;
		        while(rs.next()) {
		        	rowData[i][0] = i + 1;
		        	rowData[i][1] = rs.getString(1);
		        	rowData[i++][2] = "";
		        }
		        close(pstmt);
		        close(con);
		        System.out.println("ģ����� Ŀ�ؼ� ����");
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
		con = getConnection();
		if(con!=null) {
			try {
				int totalcount = 0;
				System.out.println("���� : "+connectId);
				pstmt = con.prepareStatement("select count(groupid) from chatgroup where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalcount =  rs.getInt(1);
		        }
				
				Object rowData[][] = new Object[totalcount][2];
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
		        close(pstmt);
		        close(con);
		        System.out.println("�� ��� Ŀ�ؼ� ����");
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
		con = getConnection();
		Date today = new Date();
		System.out.println(today);
		int chk = 0;

        if( con != null ) {
            try {
            	/* 1:1 �ִ��� �˻� */
            	long groupidbefore = 0L;
            	StringBuffer query5 = new StringBuffer();
    			query5.append("select groupid, count(groupid) cnt from chatmember where groupid in (select groupid from chatgroup where type =?) and userid in (?");
            	for (int i = 0; i < data.length; i++) {
            		query5.append(",?");
            	}
            	query5.append(") group by groupid having cnt = 2");
				pstmt = con.prepareStatement(query5.toString());
				pstmt.setByte(1, ONEROOM); // ä��Ÿ��
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8")); 
				for (int i = 0; i < data.length; i++) { // �� ������
					pstmt.setString(3+i, new String(data[i].getBytes("UTF-8"),"UTF-8")); 
				}
				
				rs = pstmt.executeQuery();
				while(rs.next()) {
					groupidbefore = rs.getLong(1);
				}
				close(pstmt);
				
				if(groupidbefore != 0L) {
					con.close();
					return 0;
				}
				/* 1:1 ä�ù� �ִ��� �˻� */
            	
            	
            	/* ä�ù� ���� */
            	String query = "insert into chatgroup(userid, groupname, type) values(?,?,?)";
            	long groupid3;
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
            	String query3 = "insert into chatmember(groupid,userid,lastreadtime) values(?,?,now(6))";
		        
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
		        
		        con.commit();
		        System.out.println("ä�ù氳�� ����");
		        close(pstmt);
		        close(con);
		        System.out.println("ä�ù氳�� Ŀ�ؼ� ����");
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
	
	public Long createGroupRoom(String connectId, String data[]) { // data�� friendId
		con = getConnection();
		Long groupid = 0L;
		int chk = 0;

        if( con != null ) {
            try {
            	/* ä�ù� ���� */
            	String query = "insert into chatgroup(userid, groupname, type) values(?,?,?)";
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(2, new String((connectId+"�� ��").getBytes("UTF-8"),"UTF-8")); // ä�� ���
				pstmt.setByte(3, GROUPROOM); // ä�� ���
		        chk = pstmt.executeUpdate();
		        pstmt.close();
		        
		        /* groupid �������� */
		        String query2 = "select LAST_INSERT_ID()";
		        pstmt = con.prepareStatement(query2);
		        rs = pstmt.executeQuery();
		        groupid = 0L;
		        while(rs.next()) {
		        	groupid = rs.getLong(1);
		        }
		        pstmt.close();
		        System.out.println("���� db ����� groupid : " + groupid);
		        
		        /* ä�ù� ������ �߰� */
            	String query3 = "insert into chatmember(groupid,userid,lastreadtime) values(?,?,now(6))";
		        
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
		        
		        con.commit();
		        System.out.println("ä�ù氳�� ����");
		        close(pstmt);
		        close(con);
		        System.out.println("ä�ù氳�� Ŀ�ؼ� ����");
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
				return -1L;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return groupid;
	}
	
	

	public Long selectRoom(String connectId, String[] data, byte type) {
		con = getConnection();
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

		        close(pstmt);
		        close(con);	
		        System.out.println("selectroom Ŀ�ؼ� ����");			
				return groupid;
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return groupid;
	}

	public Chat insertMSG(Chat message) {
		con = getConnection();
		int chk = 0 ;
		Chat chat = null;
		if(con != null) {
//			String query = "select count(*) from chatmember where groupid = ?";
	        String query = "select * from chatcontent where chatid = (select LAST_INSERT_ID())";
			String query2 = "insert into chatcontent(userid,groupid,content,sendtime,count) values(?,?,?,now(6),(select count(*) from chatmember where groupid = ?))";
			// �޽����� db�� ������ ��������� �����ð��� ����
			//String query3 = "update chatmember set lastreadtime = now(6) where groupid = ? and userid = ?";
			
			try {
//				pstmt = con.prepareStatement(query);
//				pstmt.setLong(1, message.getGroupid());
//				rs = pstmt.executeQuery();
//				while(rs.next()) {
//					totalcount = rs.getInt(1);
//				}
//				pstmt.close();
				
				pstmt = con.prepareStatement(query2);
				pstmt.setString(1, new String(message.getUserid().getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(2, message.getGroupid());
		        pstmt.setString(3, new String(message.getContent().getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(4, message.getGroupid());
				chk = pstmt.executeUpdate();
				close(pstmt);
				
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	chat = new Chat(rs.getLong("chatid"),rs.getString("userid"),rs.getLong("groupid"),rs.getString("content"),rs.getTimestamp("sendtime"),rs.getInt("count"));
		        	System.out.println(chat);
		        }
		        
		        if(chk >=0) {
		        	con.commit();
		        }else
		        	con.rollback();
		        close(pstmt);
		        close(con);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return chat;
	}
	
	public boolean insertFile(String userid, Long roomid, String dir, String time) {
		con = getConnection();
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

		        close(pstmt);
		        close(con);
				
			}catch(SQLException e){
				return false;
			}catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public List<String> selectGroupmember(Long sendGroupid) {
		con = getConnection();
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

		        close(pstmt);
		        close(con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return groupmemberList;
	}

	public List<Chat> selectchatcontent(ChatMember chatmember) {
		con = getConnection();
		List<Chat> chatcontent = new ArrayList<Chat>();
		Long groupid = chatmember.getGroupid();
		String userid = chatmember.getUserid();
		System.out.println("groupid : "+ groupid);
		System.out.println("userid : "+ userid);
		if(con != null) {
			// �ش��ϴ� ä�ù��� ä�������� �α����� ������� ���������� �ð����� �ʴµ����� ��������
			String query = "select * from chatcontent where groupid = ? "
					+ "and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?)";
			// ī��Ʈ���� 
			String query2 = "update chatcontent set count = count-1 where groupid = ? "
					+ "and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?)";
			// ������ �����ð� ����
			String query3 = "update chatmember set lastreadtime = now(6) where groupid = ? and userid = ?";
			// count = 0 �̸� ����
//			String query4 = "delete from chatcontent where count = 0";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, groupid);
				pstmt.setLong(2, groupid);
				pstmt.setString(3, userid);
				
		        rs = pstmt.executeQuery();
		        close(pstmt);
		        Chat chat;
		        System.out.println("������ ��������");
		        while(rs.next()) {
		        	chat = new Chat(rs.getLong("chatid"),rs.getString("userid"),rs.getLong("groupid"),rs.getString("content"),rs.getTimestamp("sendtime"),rs.getInt("count"));
		        	chatcontent.add(chat);
		        }		 
		        pstmt = con.prepareStatement(query2);
		        pstmt.setLong(1, groupid);
				pstmt.setLong(2, groupid);
				pstmt.setString(3, userid);
				int result = pstmt.executeUpdate();
				close(pstmt);
				
				pstmt = con.prepareStatement(query3);
				pstmt.setLong(1, groupid);
		        pstmt.setString(2, userid);				
				int result2 = pstmt.executeUpdate();
				
		        if(result >= 0 && result2 >=0/* && result3 >=0 */) {
					con.commit();
				}else {
					con.rollback();
				}
		        close(pstmt);
		        close(con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return chatcontent;
	}

	public int updatereadtime(String member, Chat message) {
		con = getConnection();
		int result = 0;
		// ī��Ʈ���� 
		String query = "update chatcontent set count = count-1 where groupid = ? "
				+ "and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?) "
				+ "and sendtime <= ?";
		// ������ �����ð� ����
		String query2 = "update chatmember set lastreadtime = ? where groupid = ? and userid = ?";
		
		Long groupid = message.getGroupid();
		Timestamp date = message.getSendtime();
		try {
			pstmt = con.prepareStatement(query);
			pstmt.setLong(1, groupid);
			pstmt.setLong(2, groupid);
			pstmt.setString(3, member);
			pstmt.setTimestamp(4, date);
			result = pstmt.executeUpdate();
			close(pstmt);
			
			pstmt = con.prepareStatement(query2);
			pstmt.setTimestamp(1, date);
			pstmt.setLong(2, groupid);
			pstmt.setString(3, member);
			int result2 = pstmt.executeUpdate();
			
			if(result >=0 && result2 >=0) {
				con.commit();
			}else {
				con.rollback();
			}

	        close(pstmt);
	        close(con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void deleteolddata() {
		con = getConnection();
		String query = "delete from chatcontent where count = 0";
		try {
			pstmt = con.prepareStatement(query);
			int result = pstmt.executeUpdate();
			
			if(result >= 0) {
				con.commit();
				System.out.println("--server data remove--");
			}else {
				con.rollback();
			}
			

	        close(pstmt);
	        close(con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
    
   
