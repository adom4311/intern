package dao;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ServerDAO {
	String driver = "org.mariadb.jdbc.Driver";
    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
 
    public ServerDAO() {
        try {
        	Class.forName(driver); // 마리아db driver
        	con = DriverManager.getConnection(
                    "jdbc:mariadb://127.0.0.1:3306/sw_test",
                    "root",
                    "daou");
        	con.setAutoCommit(false);
            
			System.out.println("DB 접속 서엉공");
		} catch (SQLException e) {
			System.out.println("DB 접속 실패");
	        e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로드 실패");
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
		        	System.out.println("회원가입 성공");
		        	con.commit();
		        }
		        else {
		        	System.out.println("회원가입 실패");
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
		System.out.println("login 함수");
		if( con != null ) {
            try {
				pstmt = con.prepareStatement("select * from user where userid = ? and password = ?");
				pstmt.setString(1, new String(id.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(pw.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	System.out.println("로그인 성공");
		        	return 1;
		        }
		        System.out.println("로그인 실패");
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
            	System.out.println("됨 : " + tempId);
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
	
	/* 친구 찾기 용 */
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
	
	/* 친구 목록 용 */
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

	public int addfri(String connectId, String data) {
		int chk = 0;

        if( con != null ) {
            try {
				pstmt = con.prepareStatement("insert into friend values(?,?)");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(data.getBytes("UTF-8"),"UTF-8"));
		        chk = pstmt.executeUpdate();
		        
		        if(chk >0) {
		        	System.out.println("친구추가 성공");
		        	con.commit();
		        }
		        else {
		        	System.out.println("친구추가 실패");
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
            	System.out.println("됨 : " + connectId);
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

	public int createGroup(String connectId, String data) { // data는 friendId
		Date today = new Date();
		System.out.println(today);
		String groupid = ("daou" + connectId + today);
		int chk = 0;

        if( con != null ) {
            try {
            	/*
            	 * select b.groupid from 
					(select groupid, count(*) ccnt from chatmember where groupid in (select a.groupid from (select groupid, count(groupid) cnt from chatmember where userid in ('q','asdf') group by groupid having cnt =2) a) group by groupid) b
					where b.ccnt = 2;
            	 */
            	String query1 = "insert into chatgroup(groupid, userid) select ?, ? from dual where NOT EXISTS (select b.groupid from \r\n" + 
            			"					(select groupid, count(*) ccnt from chatmember where groupid in (select a.groupid from (select groupid, count(groupid) cnt from chatmember where userid in (?,?) group by groupid having cnt =?) a) group by groupid) b\r\n" + 
            			"					where b.ccnt = 2)";
            	//개설자와 초대자를 가진 채팅방을 찾고 채팅방의 사람수가 개설자+초대자 수와 같은방이 존재하지 않으면 인서트
            	
            	String query2 = "insert into chatmember(groupid,userid) values(?,?)";
            	
				pstmt = con.prepareStatement(query1);
				pstmt.setString(1, new String(groupid.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(3, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(4, new String(data.getBytes("UTF-8"),"UTF-8"));
				pstmt.setInt(5, 2);
		        chk = pstmt.executeUpdate();
		        pstmt.close();
		        
		        if(chk == 0) { // 이미있다면
		        	return 0;
		        }
		        
		        // chatmember에 개설자 아이디 추가
		        pstmt = con.prepareStatement(query2);
				pstmt.setString(1, new String(groupid.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        chk = pstmt.executeUpdate();   
				System.out.println("추가되는지:::::"+chk);

		        
		        // chatmember에 초대한 아이디 추가
				pstmt.setString(1, new String(groupid.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(2, new String(data.getBytes("UTF-8"),"UTF-8"));
		        chk = pstmt.executeUpdate(); 
		        pstmt.close();
		        
		        if(chk >0) {
		        	con.commit();
		        	System.out.println("채팅방개설 성공");
		        }
		        else {
		        	System.out.println("채팅방개설 실패");
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
	
	
   
}
    
   
