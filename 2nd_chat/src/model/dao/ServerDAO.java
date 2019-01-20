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
import model.vo.User;

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
    
    // ���⼭���� �輺�� ���ϻ�� �ڵ�
    // 591�� �ٺ��� ���� ���ϻ�� �ڵ�
    
 
	public int signUp(String id, String pw) {
		con = dataSource.getConnection();
		int chk = 0;

        if( con != null ) {
            try {
				pstmt = con.prepareStatement("insert into user values(?,?)");
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
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
		        dataSource.freeConnection(con,pstmt);
			}
        }
        return chk;
	}

	public User login(String id, String pw) {
		con = dataSource.getConnection();
		
		User user = null;
		if( con != null ) {
            try {
				pstmt = con.prepareStatement("select * from user where userid = ? and password = ?");
				pstmt.setString(1, new String(id.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(pw.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	user = new User(rs.getString("userid"),rs.getString("password"));
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
		        dataSource.freeConnection(con,pstmt,rs);
			}
        }
        return user;
	} 

	public Object[][] friFind(String tempId) {
		con = dataSource.getConnection();
		Object rowData[][] = null;
		if( con != null ) {
            try {
            	int totalcount = 0;
            	// ���� ģ���� ������ ��� ������� ��
            	pstmt = con.prepareStatement("select count(userid) from user where userid != ? and userid not in (select friendid from friend where userid = ?)");
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalcount =  rs.getInt(1);
		        }
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
            	
            	rowData = new Object[totalcount][3];
            	// ���� ģ���� ������ ��� �����
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

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
		        dataSource.freeConnection(con,pstmt,rs);
			}
        }
        return rowData;
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
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con,pstmt);
			}
        }
        return chk;
	}

	public Object[][] friList(String connectId) {
		con = dataSource.getConnection();
		Object[][] rowData = null;
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
		        
            	rowData = new Object[totalcnt][3];
				pstmt = con.prepareStatement("select friendid from friend where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();		        
		        int i=0;
		        while(rs.next()) {
		        	rowData[i][0] = i + 1;
		        	rowData[i][1] = rs.getString(1);
		        	rowData[i++][2] = "";
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
		        dataSource.freeConnection(con,pstmt,rs);
			}
        }
        return rowData;
	}
	
	public int createRoom(String connectId, String data[]) { // data�� friendId
		con = dataSource.getConnection();
		int chk = 0;
		int chk2 = 0;
        if( con != null ) {
            try {
            	/* 1:1 �ִ��� �˻� */
            	long groupidbefore = 0L;
            	String query = "select groupid from chatmember where groupid in (select groupid from chatgroup where type =?) and userid in (?,?)";
				pstmt = con.prepareStatement(query);
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
					return chk;
				}
            	
            	/* ä�ù� ���� */
            	String query2 = "insert into chatgroup(userid, groupname, type) values(?,?,?)";
				pstmt = con.prepareStatement(query2);
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(2, new String((connectId+"�� ��").getBytes("UTF-8"),"UTF-8")); // ä�� ���
				pstmt.setByte(3, ONEROOM);
		        chk = pstmt.executeUpdate();
		        dataSource.freeConnection(pstmt);
		        
		        /* groupid �������� */
		        String query3 = "select LAST_INSERT_ID()";
		        pstmt = con.prepareStatement(query3);
		        rs = pstmt.executeQuery();
		        Long groupid = 0L;
		        while(rs.next()) {
		        	groupid = rs.getLong(1);
		        }
//		        pstmt.close();
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
		        
		        /* ä�ù� ������ �߰� */
            	String query4 = "insert into chatmember(groupid,userid,lastreadtime) values(?,?,now(6))";
		        
		        // chatmember�� ������ ���̵� �߰�
		        pstmt = con.prepareStatement(query4);
				pstmt.setLong(1, groupid);
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				pstmt.executeUpdate(); 
		        
		        // chatmember�� �ʴ��� ���̵� �߰�
				for (int i = 0; i < data.length; i++) {
					pstmt.setLong(1, groupid);
					pstmt.setString(2, new String(data[i].getBytes("UTF-8"),"UTF-8"));
					chk2 = pstmt.executeUpdate();
				}  
				
			} catch (SQLException e) {
	        	System.out.println("ä�ù氳�� ����");
	        	e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally { // return �ص� �����
				try {
					if(chk > 0 && chk2 > 0) {
						con.commit();
					}else {
						con.rollback();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 dataSource.freeConnection(con,pstmt,rs); 
			}
        }
        return chk2;
	}
	
	public Long createGroupRoom(String connectId, String data[]) { // data�� friendId
		con = dataSource.getConnection();
		Long groupid = null;
		int chk = 0;
		int chk2 = 0;

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
				pstmt.executeUpdate(); 
		        
		        // chatmember�� �ʴ��� ���̵� �߰�
				for (int i = 0; i < data.length; i++) {
					pstmt.setLong(1, groupid);
					pstmt.setString(2, new String(data[i].getBytes("UTF-8"),"UTF-8"));
					chk2 = pstmt.executeUpdate();
				}  
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				try {
					if(chk > 0 && chk2 >0) {
						con.commit();
					}else {
						con.rollback();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 dataSource.freeConnection(con,pstmt,rs);
			}
        }
        return groupid;
	}
	
	
	public Chat insertMSG(Chat message) {
		con = dataSource.getConnection();
		int chk = 0 ;
		Chat chat = null;
		if(con != null) {
			String query = "insert into chatcontent(userid,groupid,content,sendtime,count) values(?,?,?,now(6),(select count(*) from chatmember where groupid = ?))";
			String query2 = "select * from chatcontent where chatid = (select LAST_INSERT_ID())";
			
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(message.getUserid().getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(2, message.getGroupid());
		        pstmt.setString(3, new String(message.getContent().getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(4, message.getGroupid());
			    chk = pstmt.executeUpdate();
				
				dataSource.freeConnection(pstmt);
				
				pstmt = con.prepareStatement(query2);
				rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	chat = new Chat(rs.getLong("chatid"),rs.getString("userid"),rs.getLong("groupid"),rs.getString("content"),rs.getTimestamp("sendtime"),rs.getInt("count"));
		        }
		        
		       
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				try {
					if(chk >=0) 
						con.commit();
					else
						con.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return chat;
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
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con,pstmt,rs);
			}
		
		}
		
		return groupmemberList;
	}

	public List<Chat> selectchatcontent(ChatMember chatmember) {
		con = dataSource.getConnection();
		List<Chat> chatcontent = new ArrayList<Chat>();
		Long groupid = chatmember.getGroupid();
		String userid = chatmember.getUserid();
		int result = 0;
		int result2 = 0;
		if(con != null) {
			// �ش��ϴ� ä�ù��� ä�������� �α����� ������� ���������� �ð����� �ʴµ����� ��������
			String query = "select * from chatcontent where groupid = ? "
					+ "and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?) order by sendtime, chatid";
			// ī��Ʈ���� 
			String query2 = "update chatcontent set count = count-1 where chatid in (select chatid from chatcontent where groupid = ? and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?))";
			// ������ �����ð� ����
			String query3 = "update chatmember set lastreadtime = ? where groupid = ? and userid = ?";
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
					result = pstmt.executeUpdate();
					dataSource.freeConnection(pstmt);
					
					pstmt = con.prepareStatement(query3);
					pstmt.setTimestamp(1, chat.getSendtime());
					pstmt.setLong(2, groupid);
			        pstmt.setString(3, userid);				
					result2 = pstmt.executeUpdate();
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if(result >= 0 && result2 >=0/* && result3 >=0 */) {
						con.commit();
					}else {
						con.rollback();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return chatcontent;
	}

	public int updatereadtime(String member, Chat message) {
		con = dataSource.getConnection();
		int result = 0;
		int result2 = 0;
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
			result2 = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(result >=0 && result2 >=0) {
					con.commit();
				}else {
					con.rollback();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dataSource.freeConnection(con,pstmt);
		}
		return result;
	}

	public Long selectRoom(String connectId, String[] data, byte type) {
		con = dataSource.getConnection();
		Long groupid = null;
		if(con != null) {
			StringBuffer query = new StringBuffer();
			//select groupid from chatgroup where type = 1 and groupid in (select groupid from chatmember where userid in('q','w')) ;
			query.append("select groupid from chatgroup where type = ? and groupid in (select groupid from chatmember where userid in(?");
        	for (int i = 0; i < data.length; i++) {
        		query.append(",?");
			}
        	query.append("))");
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
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return groupid;
	}
	
	/*
	 *  �輺�� ���ϻ��													
	 */
	
	                     
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/*
	 *  ���� ���ϻ��
	 */
		
	
	
	
   
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


	public boolean insertFile(String userid, Long roomid, String dir) {
		con = dataSource.getConnection();
//		con = getConnection();
		int chk=0;
		if(con!=null) {
			try {
				pstmt=con.prepareStatement("insert into filecontent values(?,?,?,now(6))");
				pstmt.setString(1, new String(userid.getBytes("UTF-8"),"UTF-8"));
				pstmt.setLong(2, roomid);
				pstmt.setString(3, new String(dir.getBytes("UTF-8"),"UTF-8"));
				//pstmt.setString(4, new String(time.getBytes("UTF-8"),"UTF-8"));
				chk=pstmt.executeUpdate();
				if(chk >=0) {
					System.out.println("�޼��� ���� ����");
		        	con.commit();
		        }else {
		        	con.rollback();
		        	System.out.println("�޼��� ���� ����");
		        }

//		        close(pstmt);
//		        close(con);
				dataSource.freeConnection(con,pstmt);
				
			}catch(SQLException e){
				return false;
			}catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return true;
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
	
	public void deleteoldFiledata() {
		con = dataSource.getConnection();
		String query = "delete from filecontent where sendtime <= date_add(now(), interval -30 day)";
		
		//2�� ���� ������ ����
		try {
			pstmt = con.prepareStatement(query);
			int result = pstmt.executeUpdate();
			
			if(result>0) {
				con.commit();
				System.out.println("--file data remove--");
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
    
   
