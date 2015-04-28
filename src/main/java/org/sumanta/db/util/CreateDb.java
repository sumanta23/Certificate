package org.sumanta.db.util;

import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class CreateDb {

  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  @SuppressWarnings("unchecked")
  public static void createSchema() {
    Connection localConnection = null;
    ArrayList localArrayList = new ArrayList();
    String str = "CertDB";
    ResultSet localResultSet = null;
    try {

      localConnection = DriverManager.getConnection(Constant.protocol + str);

    } catch (SQLException e) {
      if (e.getErrorCode() == 40000 && e.getSQLState().equals("XJ004")) {
        out.println("right");
        Properties localProperties = new Properties();
        localProperties.put("user", "user1");
        localProperties.put("password", "user1");
        try {
          localConnection = DriverManager.getConnection(Constant.protocol + str + ";create=true", localProperties);
        } catch (SQLException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        out.println("Connected to and created database " + str);
      }
    }

    try {
      if (localConnection != null) {
        localConnection.setAutoCommit(false);

        Statement statement = localConnection.createStatement();
        localArrayList.add(statement);

        statement.addBatch(DBSchema.RootCA_table);
        statement.addBatch(DBSchema.CA_table);
        statement.addBatch(DBSchema.IssuedCertificate_table);
        statement.addBatch(DBSchema.CRL_table);

        statement.executeBatch();
        out.println("command executed");
        localConnection.commit();
      }

      if (Constant.framework.equals("embedded"))
        try {
          DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException localSQLException2) {
          if ((localSQLException2.getErrorCode() == 50000) && ("XJ015".equals(localSQLException2.getSQLState()))) {
            out.println("Derby shut down normally");
          } else {
            err.println("Derby did not shut down normally");
            DbUtil.printSQLException(localSQLException2);
          }
        }
    } catch (SQLException localSQLException1) {
      DbUtil.printSQLException(localSQLException1);
    } finally {
      try {
        if (localResultSet != null) {
          localResultSet.close();
          localResultSet = null;
        }
      } catch (SQLException localSQLException3) {
        DbUtil.printSQLException(localSQLException3);
      }
      int k = 0;
      while (!localArrayList.isEmpty()) {
        Statement localStatement2 = (Statement) localArrayList.remove(k);
        try {
          if (localStatement2 != null) {
            localStatement2.close();
            localStatement2 = null;
          }
        } catch (SQLException localSQLException5) {
          DbUtil.printSQLException(localSQLException5);
        }
      }
      try {
        if (localConnection != null) {
          localConnection.close();
          localConnection = null;
        }
      } catch (SQLException localSQLException4) {
        DbUtil.printSQLException(localSQLException4);
      }
    }
  }

  public static void main(String[] args) {
    DbUtil.parseArguments(args);
    DbUtil.loadDriver();
    createSchema();
  }

}
