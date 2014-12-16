package test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class SimpleApp {
	private String framework = "embedded";
	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private String protocol = "jdbc:derby:";

	public static void main(String[] paramArrayOfString) {
		new SimpleApp().go(paramArrayOfString);
		System.out.println("SimpleApp finished");
	}

	void go(String[] paramArrayOfString) {
		parseArguments(paramArrayOfString);
		System.out.println("SimpleApp starting in " + this.framework + " mode");
		loadDriver();
		Connection localConnection = null;
		ArrayList localArrayList = new ArrayList();
		PreparedStatement localPreparedStatement2 = null;
		Statement localStatement1 = null;
		ResultSet localResultSet = null;
		try {
			Properties localProperties = new Properties();
			localProperties.put("user", "user1");
			localProperties.put("password", "user1");
			String str = "derbyDB";
			localConnection = DriverManager.getConnection(this.protocol + str
					+ ";create=true", localProperties);
			System.out.println("Connected to and created database " + str);
			localConnection.setAutoCommit(false);
			localStatement1 = localConnection.createStatement();
			localArrayList.add(localStatement1);
			localStatement1
					.execute("create table location(num int, addr varchar(40))");
			localStatement1
					.execute("create table CA(id int, dn varchar(256),certificate blob(1M))");
			System.out.println("Created table location");

			localResultSet = localStatement1
					.executeQuery("SELECT id, dn FROM CA");
			int j = 0;
			if (!localResultSet.next()) {
				j = 1;
				reportFailure("No rows in ResultSet");
			}
			int i;
			if ((i = localResultSet.getInt(1)) != 300) {
				j = 1;
				reportFailure("Wrong row returned, expected num=300, got " + i);
			}
			if (!localResultSet.next()) {
				j = 1;
				reportFailure("Too few rows");
			}
			if ((i = localResultSet.getInt(1)) != 1910) {
				j = 1;
				reportFailure("Wrong row returned, expected num=1910, got " + i);
			}
			if (localResultSet.next()) {
				j = 1;
				reportFailure("Too many rows");
			}
			if (j == 0)
				System.out.println("Verified the rows");
			localStatement1.execute("drop table location");
			System.out.println("Dropped table location");
			localConnection.commit();
			System.out.println("Committed the transaction");
			if (this.framework.equals("embedded"))
				try {
					DriverManager.getConnection("jdbc:derby:;shutdown=true");
				} catch (SQLException localSQLException2) {
					if ((localSQLException2.getErrorCode() == 50000)
							&& ("XJ015"
									.equals(localSQLException2.getSQLState()))) {
						System.out.println("Derby shut down normally");
					} else {
						System.err.println("Derby did not shut down normally");
						printSQLException(localSQLException2);
					}
				}
		} catch (SQLException localSQLException1) {
			printSQLException(localSQLException1);
		} finally {
			try {
				if (localResultSet != null) {
					localResultSet.close();
					localResultSet = null;
				}
			} catch (SQLException localSQLException3) {
				printSQLException(localSQLException3);
			}
			int k = 0;
			while (!localArrayList.isEmpty()) {
				Statement localStatement2 = (Statement) localArrayList
						.remove(k);
				try {
					if (localStatement2 != null) {
						localStatement2.close();
						localStatement2 = null;
					}
				} catch (SQLException localSQLException5) {
					printSQLException(localSQLException5);
				}
			}
			try {
				if (localConnection != null) {
					localConnection.close();
					localConnection = null;
				}
			} catch (SQLException localSQLException4) {
				printSQLException(localSQLException4);
			}
		}
	}

	private void loadDriver() {
		try {
			Class.forName(this.driver).newInstance();
			System.out.println("Loaded the appropriate driver");
		} catch (ClassNotFoundException localClassNotFoundException) {
			System.err.println("\nUnable to load the JDBC driver "
					+ this.driver);
			System.err.println("Please check your CLASSPATH.");
			localClassNotFoundException.printStackTrace(System.err);
		} catch (InstantiationException localInstantiationException) {
			System.err.println("\nUnable to instantiate the JDBC driver "
					+ this.driver);
			localInstantiationException.printStackTrace(System.err);
		} catch (IllegalAccessException localIllegalAccessException) {
			System.err.println("\nNot allowed to access the JDBC driver "
					+ this.driver);
			localIllegalAccessException.printStackTrace(System.err);
		}
	}

	private void reportFailure(String paramString) {
		System.err.println("\nData verification failed:");
		System.err.println('\t' + paramString);
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

	private void parseArguments(String[] paramArrayOfString) {
		if ((paramArrayOfString.length > 0)
				&& (paramArrayOfString[0].equalsIgnoreCase("derbyclient"))) {
			this.framework = "derbyclient";
			this.driver = "org.apache.derby.jdbc.ClientDriver";
			this.protocol = "jdbc:derby://localhost:1527/";
		}
	}
}