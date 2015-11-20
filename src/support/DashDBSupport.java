package support;

import service.*;
import seaitmmanager.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

import seaitmmanager.ItmConst;

public class DashDBSupport implements ItmConst {
	private Connection m_oConnection = null;
	private String url;
	private String username;
	private String pw;
	
	private String sErrorFormat(Exception e) {
		String error = "XXXXX DB Error" + e.getClass().getName() + ": " + e.getLocalizedMessage();
		return error;
	}
  
	public DashDBSupport() {
        try {
        	InputStream in = new FileInputStream("db.properties");
			Properties properties = new Properties();
			properties.load(in);
			url = properties.getProperty("url");
			pw = properties.getProperty("password");
			username = properties.getProperty("username").toUpperCase();
			Global.db_name = username;
			
			// the db2 driver string
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			m_oConnection = DriverManager.getConnection(url, username,pw);
			Log.print(DEBUG_INFO, "+++++ connection to DashDB finished!");
        } catch ( Exception e ) {
            //Log.print(DEBUG_ERROR, "XXXXX DB Error" + e.getClass().getName() + ": " + e.getMessage() );
        	Log.print(DEBUG_ERROR, sErrorFormat(e));
            System.exit(0);
        }
    }
	
	public boolean bDashDbTableExists(String db_name, String table_name) {
		boolean found = false;
		try {
			String query = "select tabname from syscat.tables where tabschema='" + db_name + "' and tabname='" + table_name + "'";
			Log.print(DEBUG_ALL, query);
			PreparedStatement check = m_oConnection.prepareStatement(query);
			ResultSet check_result = check.executeQuery();
			
			//get result
			ResultSetMetaData check_rsmd = check_result.getMetaData();
			int check_columnsNumber = check_rsmd.getColumnCount();
			System.out.println("column number is : " + check_columnsNumber);
			if (check_result != null) {
				while (check_result.next()) {
					for (int i = 1; i <= check_columnsNumber; i++) {
						Log.print(DEBUG_ALL, check_rsmd.getColumnName(i) + ":" + check_result.getString(i));
						found = true;
					}
					System.out.println("  ");
				}
			}
		} catch (SQLException e) {
			Log.print(DEBUG_ERROR, sErrorFormat(e));
		}
		return found;
	}
	
	public void vExecuteDashDbUpdate(String command) {
		Log.print(DEBUG_ALL, command);
		try {
			PreparedStatement update = m_oConnection.prepareStatement(command);
			update.executeUpdate();
		} catch (SQLException e) {
			Log.print(DEBUG_ERROR, sErrorFormat(e));
		}
	}
	
}
