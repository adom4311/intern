package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;


public class DBCPTemplate {

	private static DBCPTemplate dataSource;
	
	// DB Resource ����
	private static final String DRIVER = "org.mariadb.jdbc.Driver";
    private static final String URL = "jdbc:mariadb://127.0.0.1:3306/sw_test";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "daou";

    synchronized public static DBCPTemplate getDataSource() {

        try {
            if (dataSource == null) {
                dataSource = new DBCPTemplate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSource;
    }

    private DBCPTemplate() {
    	System.out.println("Database Connection pool�� �����մϴ�.");
        initFirstConnection();
    }

    private void initFirstConnection(){
        try {
            setupFirstDriver();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public Connection getConnection() {
        Connection con = null;

        try {
            con = DriverManager.getConnection("jdbc:apache:commons:dbcp:first_connection");
        } catch (SQLException e) {
        	e.printStackTrace();
        }

        return con;
    }

    public void setupFirstDriver() throws Exception {
    	Class.forName(DRIVER).newInstance();

        // Connection Pool ����, �ɼǼ���
        GenericObjectPool<DataSource> connectionPool = new GenericObjectPool<DataSource>();
        // �ִ� Ŀ�ؼ� ���� 
        connectionPool.setMaxActive(1);
        // Ǯ�� ����ɼ� �ִ� Ŀ�ؼ� �ִ밹�� ����
        connectionPool.setMaxIdle(1);
        // Ŀ�ؼ� �ּҰ��� ����
        connectionPool.setMinIdle(1); 
        // Ŀ�ؼ��� ��������� ��ٸ��� �ð� �и���
        connectionPool.setMaxWait(5000);
        // ��ȿ Ŀ�ؼ� �˻� �ֱ�
        connectionPool.setTimeBetweenEvictionRunsMillis(60 * 60 * 1000);
        //  ������ �ʴ� Ŀ�ؼ��� ������ �� �� �Ӽ����� ������ �ð� �̻� ��Ȱ��ȭ ������ ��ü�� ����
        connectionPool.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
        
        // Ŀ�ؼ��� ��ȿ�� �˻翩�� true �� �ణ�� ��������
        connectionPool.setTestOnBorrow(true);

        // ���� DB���� Ŀ�ؼ��� �������ִ� ���丮 ����
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(URL, USERNAME, PASSWORD);

        new PoolableConnectionFactory(
                connectionFactory,
                connectionPool,
                null, // statement pool
                "SELECT 1", // Ŀ�ؼ� �׽�Ʈ ����: Ŀ�ؼ��� ��ȿ���� �׽�Ʈ�� �� ���Ǵ� ����.
                false, // read only ����
                false); // auto commit;

        // Pooling�� ���� JDBC ����̹� ���� �� ���
        PoolingDriver driver = new PoolingDriver();

        // JDBC ����̹��� Ŀ�ؼ� Ǯ ���
        driver.registerPool("first_connection", connectionPool);
    }

    public void freeConnection(Connection con, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            freeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection con, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            freeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection con, PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
            freeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection con, Statement stmt) {
        try {
            if (stmt != null) stmt.close();
            freeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection con) {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Statement stmt) {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}