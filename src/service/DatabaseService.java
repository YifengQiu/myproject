package service;

import seaitmmanager.*;
import support.*;

public class DatabaseService implements ItmConst {
	private DashDBSupport m_oDashDbSupport = null;
	
	public DatabaseService() {
		//System.out.println("+++++++++++++++++++++++++ DB Service starts!");
		
        switch (Global.g_iDbType) {
        case DASH_DB:
        	m_oDashDbSupport = new DashDBSupport();
        	break;
        default:
        	Log.print(DEBUG_ERROR, "XXXXXXXX ATTENTION! DB Type unrecognized!");
        	System.exit(0);
        }
    }
	
	public boolean bTableExists(String db_name, String table_name) {
		return m_oDashDbSupport.bDashDbTableExists(db_name, table_name);
	}
	
	public void vDeleteTable(String db_name, String table_name) {
		
	}
	
	public void vCreateTaskTable(String db_name) {
		//first of all, delete all existing tables
		for (int i=0; i<TABLE_NAME_ARRAY.length; i++) {
			String table_name = TABLE_NAME_ARRAY[i];
			Log.print(DEBUG_ALL, "+++ Delete table " + table_name);
			boolean should_delete = bTableExists(db_name, table_name);
			if (should_delete) {
				String update = "DROP TABLE " + db_name + "." + table_name;
				m_oDashDbSupport.vExecuteDashDbUpdate(update);
				Log.print(DEBUG_ALL, "     table " + table_name + " delete finished");
			}
		}
		
		//create new tables
		for (int i=0; i<CREATE_SEA_TASKING_TABLES.length; i++) {
			m_oDashDbSupport.vExecuteDashDbUpdate(CREATE_SEA_TASKING_TABLES[i]);
		}
	}
	
	public void vInsertTableEntry(String table_name, String input_value) {
		String command = "INSERT INTO " + table_name + " values (" + input_value + ")";
		m_oDashDbSupport.vExecuteDashDbUpdate(command);
	}
	
	public void vUpdateTableEntry(String table_name, String update_value, String condition_value) {
		String command = "UPDATE " + table_name + " set " + update_value + " where " + condition_value;
		m_oDashDbSupport.vExecuteDashDbUpdate(command);
	}
}
