package seaitmmanager;

import application.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.ibm.iotf.client.api.Device;
import com.ibm.iotf.client.api.HistoricalEvent;
import com.ibm.iotf.client.app.Command;
import com.ibm.iotf.client.app.Event;
import com.ibm.iotf.client.app.EventCallback;

import ca.crc.sea.AppClient;
import ca.crc.sea.BlueMixAppClient;
import service.DatabaseService;

/**
 * Interface for connecting to IoTF as an application.
 * Reads configuration file from the file system in app.conf
 * 
 * @author jfroy
 */

public class SEAITMManager {
	/**
	 * Example using the App API to connect to IoTF, subscribe to events and sends commands.
	 * It also shows how to query the Mgnt API to list existing devices and their history.
	 * 
	 * @param args No arguments
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) {
		
		Global.g_oDbService = new DatabaseService();
		
		new TaskingManager();
	}
}
