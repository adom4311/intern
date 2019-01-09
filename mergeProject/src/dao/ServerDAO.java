package dao;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerDAO {
	String driver = "org.mariadb.jdbc.Driver";
    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
 
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

	public int createGroup(String connectId, String data[]) { // data�� friendId
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
//            	String query1 = "insert into chatgroup(groupid, userid) select ?, ? from dual where NOT EXISTS (select b.groupid from \r\n" + 
//            			"					(select groupid, count(*) ccnt from chatmember where groupid in (select a.groupid from (select groupid, count(groupid) cnt from chatmember where userid in (?,?) group by groupid having cnt =?) a) group by groupid) b\r\n" + 
//            			"					where b.ccnt = 2)";
            	
            	//�����ڿ� �ʴ��ڸ� ���� ä�ù��� ã�� ä�ù��� ������� ������+�ʴ��� ���� �������� �������� ������ �μ�Ʈ
            	StringBuffer query = new StringBuffer();
            	query.append("insert into chatgroup(groupid, userid) select ?, ? from dual where NOT EXISTS (select b.groupid from ");
            	query.append("(select groupid, count(*) ccnt from chatmember where groupid in (select a.groupid from (select groupid, count(groupid) cnt from chatmember where userid in (?");
            	for (int i = 0; i < data.length; i++) {
            		query.append(",?");
				}
            	query.append(") group by groupid having cnt =?) a) group by groupid) b where b.ccnt = ?)");

            	// chatgroup insert��
				pstmt = con.prepareStatement(query.toString());
				pstmt.setString(1, new String(groupid.getBytes("UTF-8"),"UTF-8")); // ������ groupid
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8")); // ä�� ������
				
				// ä�� ������
				pstmt.setString(3, new String(connectId.getBytes("UTF-8"),"UTF-8")); // ä�� ������
				for (int i = 0; i < data.length; i++) { // �� ������
					pstmt.setString(4+i, new String(data[0].getBytes("UTF-8"),"UTF-8")); 
				}
				
				pstmt.setInt(4+data.length, data.length + 1 ); // ������ + ������ �� 
				pstmt.setInt(4+data.length + 1, data.length + 1 ); // ������ + ������ �� 
		        chk = pstmt.executeUpdate();
		        pstmt.close();
		        
		        if(chk == 0) { // �̹��ִٸ�
		        	return 0;
		        }
		        
            	String query2 = "insert into chatmember(groupid,userid) values(?,?)";
            	// groupmember insert��
		        
		        // chatmember�� ������ ���̵� �߰�
		        pstmt = con.prepareStatement(query2);
				pstmt.setString(1, new String(groupid.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        chk = pstmt.executeUpdate();   
				System.out.println("�߰��Ǵ���:::::"+chk);

		        
		        // chatmember�� �ʴ��� ���̵� �߰�
				for (int i = 0; i < data.length; i++) {
					pstmt.setString(1, new String(groupid.getBytes("UTF-8"),"UTF-8"));
					pstmt.setString(2, new String(data[0].getBytes("UTF-8"),"UTF-8"));
					pstmt.executeUpdate();
				}
		        pstmt.close();
		        
		        con.commit();
		        System.out.println("ä�ù氳�� ����");
		        
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

	public String selectGroupid(String connectId, String[] data) {
		String groupid = "";
		if(con != null) {
			StringBuffer query = new StringBuffer();
			query.append("select b.groupid from (select groupid, count(*) ccnt from chatmember where groupid in (select a.groupid from (select groupid, count(groupid) cnt from chatmember where userid in (?");
        	for (int i = 0; i < data.length; i++) {
        		query.append(",?");
			}
        	query.append(") group by groupid having cnt =?) a) group by groupid) b");
			try {
				pstmt = con.prepareStatement(query.toString());
				// ä�� ������
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8")); // ä�� ������
				for (int i = 0; i < data.length; i++) { // �� ������
					pstmt.setString(2+i, new String(data[i].getBytes("UTF-8"),"UTF-8")); 
				}
				
				pstmt.setInt(2+data.length, data.length + 1 ); // ������ + ������ �� 
				
				rs = pstmt.executeQuery();
				while(rs.next()) {
					groupid = rs.getString(1);
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

	public int insertMSG(String sendUserid, String sendGroupid, String sendMsg) {
		int chk = 0 ;
		if(con != null) {
			String query = "insert into chatcontent values(?,?,?,now())";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(sendUserid.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(sendGroupid.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(3, new String(sendMsg.getBytes("UTF-8"),"UTF-8"));

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

	public List<String> selectGroupmember(String sendGroupid) {
		List<String> groupmemberList = new ArrayList<String>();
		
		if(con != null) {
			String query = "select userid from chatmember where groupid = ?";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(sendGroupid.getBytes("UTF-8"),"UTF-8"));

		        rs = pstmt.executeQuery();
		        
		        while(rs.next()) {
		        	groupmemberList.add(rs.getString(1));
		        }		        
		        
		        pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return groupmemberList;
	}
	
	
   
}
    
   
