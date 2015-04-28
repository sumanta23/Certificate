package org.sumanta.db.util;

import java.sql.SQLException;

public class DbUtil {

	public static void loadDriver() {
		try {
			Class.forName(Constant.driver).newInstance();
			System.out.println("Loaded the appropriate driver");
		} catch (ClassNotFoundException localClassNotFoundException) {
			System.err.println("\nUnable to load the JDBC driver "
					+ Constant.driver);
			System.err.println("Please check your CLASSPATH.");
			localClassNotFoundException.printStackTrace(System.err);
		} catch (InstantiationException localInstantiationException) {
			System.err.println("\nUnable to instantiate the JDBC driver "
					+ Constant.driver);
			localInstantiationException.printStackTrace(System.err);
		} catch (IllegalAccessException localIllegalAccessException) {
			System.err.println("\nNot allowed to access the JDBC driver "
					+ Constant.driver);
			localIllegalAccessException.printStackTrace(System.err);
		}
	}

	public static void printSQLException(SQLException paramSQLException) {
		while (paramSQLException != null) {
			System.err.println("\n----- SQLException -----");
			System.err.println("  SQL State:  "
					+ paramSQLException.getSQLState());
			System.err.println("  Error Code: "
					+ paramSQLException.getErrorCode());
			System.err.println("  Message:    "
					+ paramSQLException.getMessage());
			paramSQLException = paramSQLException.getNextException();
		}
	}

	public static void parseArguments(String[] paramArrayOfString) {
		if (paramArrayOfString.length > 0
				&& paramArrayOfString[0].equalsIgnoreCase("derbyclient")) {
			Constant.framework = "derbyclient";
			Constant.driver = "org.apache.derby.jdbc.ClientDriver";
			Constant.protocol = "jdbc:derby://localhost:1527/";
		}
		if (paramArrayOfString.length > 0
				&& paramArrayOfString[0].equalsIgnoreCase("embeded")) {
			Constant.framework = "embedded";
			Constant.driver = "org.apache.derby.jdbc.EmbeddedDriver";
			Constant.protocol = "jdbc:derby:";
		}
	}

	public static void reportFailure(String paramString) {
		System.err.println("\nData verification failed:");
    System.err.println('\t' + paramString);
  }

}
