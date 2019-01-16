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
	
	// DB Resource 정의
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
    	System.out.println("Database Connection pool을 생성합니다.");
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

        // Connection Pool 생성, 옵션세팅
        GenericObjectPool<DataSource> connectionPool = new GenericObjectPool<DataSource>();
        connectionPool.setMaxActive(45);
        connectionPool.setMinIdle(4);
        connectionPool.setMaxWait(15000);
        connectionPool.setTimeBetweenEvictionRunsMillis(3600000);
        connectionPool.setMinEvictableIdleTimeMillis(1800000);
        connectionPool.setMaxIdle(45);
        connectionPool.setTestOnBorrow(true);

        // 실제 DB와의 커넥션을 연결해주는 팩토리 생성
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(URL, USERNAME, PASSWORD);

        new PoolableConnectionFactory(
                connectionFactory,
                connectionPool,
                null, // statement pool
                "SELECT 1", // 커넥션 테스트 쿼리: 커넥션이 유효한지 테스트할 때 사용되는 쿼리.
                false, // read only 여부
                false);

        // Pooling을 위한 JDBC 드라이버 생성 및 등록
        PoolingDriver driver = new PoolingDriver();

        // JDBC 드라이버에 커넥션 풀 등록
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