package seaitmmanager;

import service.*;

public class Global implements ItmConst {
	public static int g_iDebugLevel = DEBUG_ALL;
	
	public static int g_iDbType = DASH_DB;
	
	/** Timer */
    public static int g_iMaxMessageResent = 3;  //how many times of retry if a message is not acknowledged
    public static int g_iMessageResentInterval = 5;    //how long a node should wait befor re-send a message, if the message is not acknowledged

    /** the database service, manipulate the database */
    public static String db_name = null;	//actually, the value should be same as the "username" in db.properties
    public static DatabaseService g_oDbService;
}