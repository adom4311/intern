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

import common.DBCPTemplate;
import model.vo.Chat;
import model.vo.ChatMember;
import static common.DBCPTemplate.*;
public class ServerDAO {
	DBCPTemplate dataSource;
    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
 
    public ServerDAO() {
    	dataSource = getDataSource();
    }
 
	public int signUp(String id, String pw) {
		con = dataSource.getConnection();
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
		        
		        dataSource.freeConnection(con,pstmt);
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}

	public int login(String id, String pw) {
		con = dataSource.getConnection();
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
			        dataSource.freeConnection(con,pstmt,rs);
		        	return 1;
		        }
		        System.out.println("�α��� ����");
		        

		        dataSource.freeConnection(con,pstmt,rs);
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	} 

	public Object[][] friFind(String tempId) {
		con = dataSource.getConnection();
//		con = getConnection();
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
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
            	
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

		        dataSource.freeConnection(con,pstmt,rs);
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
		con = dataSource.getConnection();
		int chk = 0;
		if( con != null ) {
            try {
				pstmt = con.prepareStatement("select count(groupid) from chatgroup where userid = ?");
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	return rs.getInt(1);
		        }
		        dataSource.freeConnection(con,pstmt,rs);
			} catch (SQLException e) {
				return -1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return chk;
	}

	public int addfri(String connectId, String data) {
		con = dataSource.getConnection();
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
		        dataSource.freeConnection(con,pstmt);
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
		con = dataSource.getConnection();
		if( con != null ) {
            try {
            	int totalcnt = 0;
            	pstmt = con.prepareStatement("select count(friendid) from friend where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalcnt =  rs.getInt(1);
		        }            	
            	
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
		        
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
		        dataSource.freeConnection(con,pstmt,rs);
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
		con = dataSource.getConnection();
		if(con!=null) {
			try {
				int totalcount = 0;
				System.out.println("���� : "+connectId);
				pstmt = con.prepareStatement("select count(*) from chatgroup where groupid in (select groupid from chatmember where userid = ?)");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalcount =  rs.getInt(1);
		        }
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
				
				Object rowData[][] = new Object[totalcount][2];
				pstmt = con.prepareStatement("select groupid, groupname from chatgroup where groupid in (select groupid from chatmember where userid = ?)");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();		        
		        int i=0;
		        while(rs.next()) {
		        	rowData[i][0] = rs.getLong(1);
		        	rowData[i++][1] = rs.getString(2);
		        }
		        dataSource.freeConnection(con,pstmt,rs);
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
		con = dataSource.getConnection();
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
				
				dataSource.freeConnection(pstmt);
				dataSource.freeConnection(rs);
				
				if(groupidbefore != 0L) {
					dataSource.freeConnection(con);
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
		        dataSource.freeConnection(pstmt);
		        
		        /* groupid �������� */
		        String query2 = "select LAST_INSERT_ID()";
		        pstmt = con.prepareStatement(query2);
		        rs = pstmt.executeQuery();
		        Long groupid = 0L;
		        while(rs.next()) {
		        	groupid = rs.getLong(1);
		        }
//		        pstmt.close();
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
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
		        
		        dataSource.freeConnection(con,pstmt);
		        System.out.println("ä�ù氳�� Ŀ�ؼ� ����");
//		        
			} catch (SQLException e) {
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
		con = dataSource.getConnection();
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
		        dataSource.freeConnection(pstmt);
		        
		        /* groupid �������� */
		        String query2 = "select LAST_INSERT_ID()";
		        pstmt = con.prepareStatement(query2);
		        rs = pstmt.executeQuery();
		        groupid = 0L;
		        while(rs.next()) {
		        	groupid = rs.getLong(1);
		        }
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
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
		        dataSource.freeConnection(con,pstmt);
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
		con = dataSource.getConnection();
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

				dataSource.freeConnection(con,pstmt,rs);
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
		con = dataSource.getConnection();
		int chk = 0 ;
		Chat chat = null;
		if(con != null) {
	        String query = "select * from chatcontent where chatid = (select LAST_INSERT_ID())";
			String query2 = "insert into chatcontent(userid,groupid,content,sendtime,count) values(?,?,?,now(6),(select count(*) from chatmember where groupid = ?))";
			
			try {
				pstmt = con.prepareStatement(query2);
				pstmt.setString(1, new String(message.getUserid().getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(2, message.getGroupid());
		        pstmt.setString(3, new String(message.getContent().getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(4, message.getGroupid());
				chk = pstmt.executeUpdate();
				
				dataSource.freeConnection(pstmt);
				
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	chat = new Chat(rs.getLong("chatid"),rs.getString("userid"),rs.getLong("groupid"),rs.getString("content"),rs.getTimestamp("sendtime"),rs.getInt("count"));
		        }
		        
		        if(chk >=0) {
		        	con.commit();
		        }else
		        	con.rollback();
		        dataSource.freeConnection(con,pstmt,rs);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return chat;
	}
	
	public boolean insertFile(String userid, Long roomid, String dir, String time) {
		con = dataSource.getConnection();
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

				dataSource.freeConnection(con,pstmt);
				
			}catch(SQLException e){
				return false;
			}catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public List<String> selectGroupmember(Long sendGroupid) {
		con = dataSource.getConnection();
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

		        dataSource.freeConnection(con,pstmt,rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return groupmemberList;
	}

	public List<Chat> selectchatcontent(ChatMember chatmember) {
		con = dataSource.getConnection();
		List<Chat> chatcontent = new ArrayList<Chat>();
		Long groupid = chatmember.getGroupid();
		String userid = chatmember.getUserid();
		if(con != null) {
			// �ش��ϴ� ä�ù��� ä�������� �α����� ������� ���������� �ð����� �ʴµ����� ��������
			String query = "select * from chatcontent where groupid = ? "
					+ "and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?)";
			// ī��Ʈ���� 
//			String query2 = "update chatcontent set count = count-1 where groupid = ? "
//					+ "and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?)";
			String query2 = "update chatcontent set count = count-1 where chatid in (select chatid from chatcontent where groupid = ? and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?))";
			// ������ �����ð� ����
			String query3 = "update chatmember set lastreadtime = ? where groupid = ? and userid = ?";
			// count = 0 �̸� ����
//			String query4 = "delete from chatcontent where count = 0";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, groupid);
				pstmt.setLong(2, groupid);
				pstmt.setString(3, userid);
				
		        rs = pstmt.executeQuery();
		        Chat chat = null;
		        System.out.println("������ ��������");
		        while(rs.next()) {
		        	chat = new Chat(rs.getLong("chatid"),rs.getString("userid"),rs.getLong("groupid"),rs.getString("content"),rs.getTimestamp("sendtime"),rs.getInt("count"));
		        	chatcontent.add(chat);
		        }		 
		        
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
			        
			    if( chat != null) {   
			        pstmt = con.prepareStatement(query2);
			        pstmt.setLong(1, groupid);
					pstmt.setLong(2, groupid);
					pstmt.setString(3, userid);
					int result = pstmt.executeUpdate();
	//				close(pstmt);
					dataSource.freeConnection(pstmt);
					
					pstmt = con.prepareStatement(query3);
					pstmt.setTimestamp(1, chat.getSendtime());
					pstmt.setLong(2, groupid);
			        pstmt.setString(3, userid);				
					int result2 = pstmt.executeUpdate();
					
			        if(result >= 0 && result2 >=0/* && result3 >=0 */) {
						con.commit();
					}else {
						con.rollback();
					}
		        }
			    dataSource.freeConnection(con,pstmt);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return chatcontent;
	}

	public int updatereadtime(String member, Chat message) {
//		// ī��Ʈ���� 
//		String query = "update chatcontent set count = count-1 where groupid = ? "
//				+ "and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?) "
//				+ "and sendtime <= ?";
//		// ������ �����ð� ����
//		String query2 = "update chatmember set lastreadtime = ? where groupid = ? and userid = ?";
		
		con = dataSource.getConnection();
		int result = 0;
		String query = "update chatcontent set count = count -1 where chatid = ?";
		String query2 = "update chatmember set lastreadtime = ? where groupid = ? and userid = ?";
		Long groupid = message.getGroupid();
		Long chatid = message.getChatid();
		Timestamp date = message.getSendtime();
		try {
			pstmt = con.prepareStatement(query);
			pstmt.setLong(1, chatid);
			result = pstmt.executeUpdate();
			dataSource.freeConnection(pstmt);
			
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
			
			dataSource.freeConnection(con,pstmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}

	public void deleteolddata() {
		con = dataSource.getConnection();
//		String query = "delete from chatcontent where sendtime <= date_add(now(), interval -2 day)";
		String query = "delete from chatcontent where sendtime <= date_add(now(), interval -2 minute)";
		//2�� ���� ������ ����
		try {
			pstmt = con.prepareStatement(query);
			int result = pstmt.executeUpdate();
			
			if(result >= 0) {
				con.commit();
				System.out.println("--server data remove--");
			}else {
				con.rollback();
			}
			
			dataSource.freeConnection(con,pstmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Object[][] selectfilecontent(Long groupid){
		con = dataSource.getConnection();
		int totalfileCnt = 0;
		if(con!=null) {
			try {
				String query = "select count(file_dir) from filecontent where groupid = ?";
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, groupid);
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalfileCnt = rs.getInt(1);
		        }
				dataSource.freeConnection(pstmt);
				dataSource.freeConnection(rs);
				
				String query2 = "select * from filecontent where groupid = ? order by sendtime";
				Object rowData[][] = new Object[totalfileCnt][1];
			
				pstmt = con.prepareStatement(query2);
				pstmt.setLong(1, groupid);
				
				rs = pstmt.executeQuery();
				int i=0;
				while(rs.next()) {
					rowData[i++][0]=rs.getString("file_dir");
					System.out.println(rs.getString("file_dir"));
				}
				
				dataSource.freeConnection(con,pstmt,rs);
				
				return rowData;
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
		
	}
}
    
   
