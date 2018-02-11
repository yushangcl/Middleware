package com.whh.middleware.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ResourceBundle;

public class JdbcUtil {
    private static final Log logger = LogFactory.getLog(JdbcUtil.class);

    private String url;
    private String classDriver;
    private String userName;
    private String password;

    private Connection conn = null;
    private PreparedStatement pst = null;
    private boolean hasBatch = false;

    public JdbcUtil() {
        conn = getConnectionNoPool();
    }
//
//	public JdbcUtil(ServletContext servletContext) {
//		conn = getConnection(servletContext);
//	}
//
//	public Connection getConnection(ServletContext servletContext) {
//		if (conn != null) {
//			return conn;
//		}
//		Connection connection = null;
//		if (servletContext != null) {
//			DataSource ds = (DataSource) SpringUtil.getBean("basicDataSource");
//			try {
//				connection = ds.getConnection();
//			} catch (SQLException e) {
//				logger.error("", e);
//			}
//		} else {
//			connection = getConnectionNoPool();
//		}
//		return connection;
//	}

//	public Connection getConnection() {
//		if (ServletActionContext.getServletContext() != null) {
//			return getConnection(ServletActionContext.getServletContext());
//		} else {
//			return getConnectionNoPool();
//		}
//	}


    private Connection getConnectionNoPool() {
        if (conn != null) {
            return conn;
        }
        Connection connection = null;
        try {
            final ResourceBundle rbn = ResourceBundle.getBundle("generator");
            this.url = rbn.getString("generator.jdbc.url");
            this.classDriver = rbn.getString("generator.jdbc.driver");
            this.userName = rbn.getString("generator.jdbc.username");
            this.password = rbn.getString("generator.jdbc.password");
        } catch (Exception e) {
            logger.error("generator.properties not found", e);
        }
        try {
            Class.forName(this.classDriver);
            connection = DriverManager.getConnection(this.url, this.userName, this.password);
        } catch (ClassNotFoundException e) {
            logger.error("jdbc class not found", e);
        } catch (SQLException e) {
            logger.error("", e);
        }
        return connection;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        logger.debug(sql);
        pst = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return pst.executeQuery();
    }

    /**
     * 返回第一行第一列的数据
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public Object executeScalar(String sql) throws SQLException {
        ResultSet rs = this.executeQuery(sql);
        if (rs.first()) {
            return rs.getObject(1);
        } else {
            return null;
        }
    }

    public ResultSet executeQuery(String sql, Object[] objs) throws SQLException {
        logger.debug(sql);
        pst = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        if (objs != null) {
            for (int i = 0; i < objs.length; i++) {
                pst.setObject(i + 1, objs[i]);
            }
        }

        return pst.executeQuery();
    }

    public int executeUpdate(String sql) throws SQLException {
        logger.debug(sql);
        pst = conn.prepareStatement(sql);
        return pst.executeUpdate();
    }

    public int executeUpdate(String sql, Object[] objs) throws SQLException {
        logger.debug(sql);
        pst = conn.prepareStatement(sql);
        if (objs != null) {
            for (int i = 0; i < objs.length; i++) {
                pst.setObject(i + 1, objs[i]);
            }
        }
        return pst.executeUpdate();
    }

    public void addBatch(String sql, Object[] objs) throws SQLException {
        logger.debug(sql);
        if (pst == null && !hasBatch) {
            pst = conn.prepareStatement(sql);
        }
        if (objs != null) {
            for (int i = 0; i < objs.length; i++) {
                pst.setObject(i + 1, objs[i]);
            }
        }
        if (null != pst) {
            pst.addBatch();
            hasBatch = true;
        }
    }

    public int[] executeBatch() throws SQLException {
        if (!hasBatch) {
            throw new SQLException("Do not add batch");
        }
        hasBatch = false;
        return pst.executeBatch();
    }

    public void close() {
        try {
            if (pst != null) {
                pst.close();
                pst = null;
            }
        } catch (Exception ex) {
        }
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (Exception ex) {
        }
    }

    public void beginTransaction() throws SQLException {
        conn.setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        conn.commit();
        conn.setAutoCommit(true);
    }

    public void rollbackTransaction() throws SQLException {
        conn.rollback();
    }
}
