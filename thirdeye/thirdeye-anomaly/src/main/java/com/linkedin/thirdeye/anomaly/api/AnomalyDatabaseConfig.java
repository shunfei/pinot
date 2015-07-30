package com.linkedin.thirdeye.anomaly.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration for anomaly database.
 */
public class AnomalyDatabaseConfig {

  private String url;

  private String functionTableName;

  private String anomalyTableName;

  private String user = "";
  private String password = "";

  private boolean createTablesIfNotExists = false;

  private boolean useConnectionPool = true;

  /**
   * Default constructor needed for object mapper. Connection pooling is enabled.
   */
  public AnomalyDatabaseConfig()
  {
    super();
  }

  /**
   * Create AnomalyDatabaseConfig.
   *
   * @param url
   * @param functionTableName
   * @param anomalyTableName
   * @param user
   * @param password
   */
  public AnomalyDatabaseConfig(String url, String functionTableName, String anomalyTableName, String user,
      String password, boolean useConnectionPool)
  {
    super();
    this.url = url;
    this.functionTableName = functionTableName;
    this.anomalyTableName = anomalyTableName;
    this.user = user;
    this.password = password;
    this.useConnectionPool = useConnectionPool;
  }

  @JsonProperty
  public String getUrl() {
    return url;
  }

  @JsonProperty
  public String getFunctionTableName() {
    return functionTableName;
  }

  @JsonProperty
  public String getAnomalyTableName() {
    return anomalyTableName;
  }

  @JsonProperty
  public String getUser() {
    return user;
  }

  @JsonProperty
  public String getPassword() {
    return password;
  }

  @JsonProperty
  public boolean isCreateTablesIfNotExists() {
    return createTablesIfNotExists;
  }

  @JsonProperty
  public boolean isUseConnectionPool() {
    return useConnectionPool;
  }

  public void setUseConnectionPool(boolean useConnectionPool) {
    this.useConnectionPool = useConnectionPool;
  }

  private static final String JDBC_MYSQL_PREFIX = "jdbc:mysql://";

  /**
   * @return
   *  the jdbc prefix for creating a connection
   */
  public String getPrefix() {
    return JDBC_MYSQL_PREFIX;
  }

  private static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * @return
   */
  public String getDateFormat() {
    return MYSQL_DATE_FORMAT;
  }

  private DataSource dataSource;

  /**
   * @return
   *  A database connection from the pool.
   * @throws SQLException
   */
  public Connection getConnection() throws SQLException {
    if (useConnectionPool) {
      if (dataSource == null) {
        DriverAdapterCPDS cpds = new DriverAdapterCPDS();
        try {
          cpds.setDriver("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
        cpds.setUrl(getPrefix() + getUrl());
        cpds.setUser(getUser());
        cpds.setPassword(getPassword());

        SharedPoolDataSource tds = new SharedPoolDataSource();
        tds.setConnectionPoolDataSource(cpds);
        tds.setMaxTotal(50);

        dataSource = tds;
      }
      return dataSource.getConnection();
    } else {
      return DriverManager.getConnection(JDBC_MYSQL_PREFIX + getUrl(), getUser(), getPassword());
    }
  }

  /**
   * Executes the sql for convenience when no result set is needed.
   * @param sql
   *  Sql to execute.
   * @return
   *  Whether the sql executed successfully without exception.
   */
  public boolean runSQL(String sql) {
    Connection conn = null;
    Statement stmt = null;
    boolean success = false;
    try {
      conn = getConnection();
      stmt = conn.createStatement();
      success = stmt.execute(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return success;
  }

  @Override
  public String toString() {
    return "AnomalyDatabaseConfig [url=" + url + ", functionTableName=" + functionTableName + ", anomalyTableName="
        + anomalyTableName + ", user=" + user + ", createTablesIfNotExists=" + createTablesIfNotExists + "]";
  }
}