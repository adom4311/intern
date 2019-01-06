package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
				pstmt.setString(1, id);
		        pstmt.setString(2, pw);
		        chk = pstmt.executeUpdate();
		        
		        if(chk >0)
		        	System.out.println("ȸ������ ����");
		        else
		        	System.out.println("ȸ������ ����");
		        
			} catch (SQLException e) {
				return -1;
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
				pstmt.setString(1, id);
		        pstmt.setString(2, pw);
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	System.out.println("�α��� ����");
		        	return 1;
		        }
		        System.out.println("�α��� ����");
			} catch (SQLException e) {
				return -1;
			}
        }
        return chk;
	}
   
}
    
   
