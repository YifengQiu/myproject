package seaitmmanager;

public interface ItmConst {
	//length of various char arrays used in DB
	public static final int MAX_SENSOR_NAME = 100;
	public static final int MAX_OTHER_NAME = 30;
	
	//various pre-defined names used in Task Message
	//Geo Fence
	public static final String ELLIPSE = "ellipse";
	public static final String POLYGON = "polygon";
	public static final String[] GEOFENCE_TYPE = new String[] {ELLIPSE, POLYGON};
	//report
	public static final String[] REPORT_FORMAT = new String[] {"netCDE", "CVS"};
	public static final String[] REPORT_TYPE = new String[] {"realTime", "batch"};
	public static final String[] REPORT_LINK = new String[] {"wired", "WiFi", "CM"};
	//roles
	public static final String SCANNER = "scanner";
	public static final String SPECTRUM_ANALYZER = "spectrum analyzer";
	public static final String SNIFFER = "sniffer";
	public static final String COMPUTING = "computing";
	public static final String[] ROLE = new String[] {SCANNER, SPECTRUM_ANALYZER, SNIFFER, COMPUTING};
	//sub-roles
	public static final String CHANNEL_RAW = "channel_raw";
	public static final String CHANNEL_CONSOLIDATED = "channel_consolidated";
	public static final String BAND_CONSOLIDATED = "band_consolidated";
	public static final String TXREPORT_RAW = "txreport_raw";
	public static final String[] SCANNER_SUBROLE = new String[] {CHANNEL_RAW, CHANNEL_CONSOLIDATED, BAND_CONSOLIDATED, TXREPORT_RAW};
	//measurements
	public static final String[] CHANNEL_RAW_MEASUREMENTS = new String[] {"power", "SNR", "carrier", "BW", "bearing", "bearingStd", "sampler"};
	
	public static final int DEBUG_ERROR = 0;
    public static final int DEBUG_CRITIC = 1;
    public static final int DEBUG_INFO = 2;
    public static final int DEBUG_ALL = 3;
    
    public static final int DASH_DB = 0;
    
    public static final String TASK_MSG = "TaskMsg";
    public static final String TASK_ACK = "TaskAck";	
    
    public static final int TASK_STATUS_PENDING = 0;
    public static final int TASK_STATUS_ACCEPT = 1;
    public static final int TASK_STATUS_REJECT = 2;
    public static final int TASK_STATUS_FAILED = 3;
    
    public static final String MISSION_TASK_TABLE = "MISSION_TASK_TB";
    public static final String TASK_TABLE = "TASK_TB";
    public static final String TASK_ELLIPSE_TABLE = "TASK_ELLIPSE_TB";
    public static final String TASK_SENSOR_TABLE = "TASK_SENSOR_TB";
    public static final String TASK_REPORT_CHANNELLIST_TABLE = "TASK_REPORT_CHANNELLIST_TB";
    public static final String TASK_REPORT_FQCYGROUP_TABLE = "TASK_REPORT_FQCYGROUP_TB";
    public static final String TASK_REPORT_MEASUREMENT_TABLE = "TASK_REPORT_MEASUREMENT_TB";
    public static final String [] TABLE_NAME_ARRAY = new String[] {MISSION_TASK_TABLE, TASK_TABLE, TASK_ELLIPSE_TABLE, TASK_SENSOR_TABLE, TASK_REPORT_CHANNELLIST_TABLE, TASK_REPORT_FQCYGROUP_TABLE, TASK_REPORT_MEASUREMENT_TABLE};
    public static final String [] CREATE_SEA_TASKING_TABLES = {"create table " + MISSION_TASK_TABLE + "("
            + "     MSG_ID      INT       not null,"
            + "     MISSION_ID      INT       not null,"
    		+ "		TASK_ID			INT		  not null,"
            + "		PRIMARY KEY(MSG_ID)"
            + ")",
            "create table " + TASK_TABLE + " ("
            + "		TASK_ID					INT				not null,"
            + "		ROLE					VARCHAR(30)		not null," 
            + "		SUB_ROLE				VARCHAR(30)		not null," 
            + "		GEO_FENCES				INT				not null,"
            + "		GEO_FENCE_TYPE			VARCHAR(30)		not null,"
            + "		SENSING_START_TIME		DOUBLE			not null,"
            + "		SENSING_STOP_TIME		DOUBLE			not null,"
            + "		SENSING_PERIOD			INT				not null,"
            + "		SLEEPING_PERIOD			INT				not null,"
            + "		REPORT_FORMAT			VARCHAR(30)		not null,"
            + "		REPORT_TYPE				VARCHAR(30)		not null,"
            + "		REPORT_LINK				VARCHAR(30)		not null,"
            + "		REPORT_START_TIME		DOUBLE			not null,"
            + "		REPORT_END_TIME			DOUBLE			not null,"
            + "		REPORT_INTERVAL			INT				not null,"
            + "		REPORT_CHANNEL_LIST_NUM	INT				not null,"
            + "		REPORT_FQCY_GROUP_NUM	INT				not null,"
            + "		REQUEST_MEASUREMENT_NUM	INT				not null,"
            + "		PRIMARY KEY(TASK_ID)"
            + ")",
            "create table " + TASK_ELLIPSE_TABLE + " ("
            + "		TASK_ID				INT				not null,"
            + "		CENTER_LAT			DOUBLE			not null,"
            + "		CENTER_LON			DOUBLE			not null,"
            + "		MAJOR_AX_LEN		DOUBLE			not null,"
            + "		MINOR_AX_LEN		DOUBLE			not null,"
            + "		PRIMARY KEY(TASK_ID)"
            + ")",
            "create table " + TASK_SENSOR_TABLE + " ("
            + "		TASK_ID			INT				not null,"	
            + "		SENSOR_ID		INT				not null,"
            + "		SENSOR_NAME		VARCHAR(100)	not null,"
            + "		SSU_ID			INT				not null,"
            + "		SSU_NAME		VARCHAR(100)	not null,"
            + "		TASK_STATUS		INT				not null,"
            + "		CONSTRAINT TASK_SENSOR_UNIQ UNIQUE (TASK_ID, SENSOR_ID, SSU_ID)"
            + ")",
            "create table " + TASK_REPORT_CHANNELLIST_TABLE + " ("
            + "		TASK_ID				INT			not null,"
            + "		CHANNEL_LIST_INDEX	INT			not null,"
            + "		CHANNEL_ID			INT			not null,"
            + "		CHANNEL_CENTER_FQCY	DOUBLE		not null,"
            + "		CHANNEL_BANDWIDTH	DOUBLE		not null,"
            + "		CONSTRAINT TASK_CHANNEL_UNIQ UNIQUE (TASK_ID, CHANNEL_LIST_INDEX)"
            + ")",
            "create table " + TASK_REPORT_FQCYGROUP_TABLE + " ("
            + "		TASK_ID				INT			not null,"
            + "		FQCYGROUP_INDEX		INT			not null,"
            + "		START_FREQUENCY		DOUBLE		not null,"
            + "		END_FREQUENCY		DOUBLE		not null,"
            + "		STEP				DOUBLE		not null,"
            + "		CONSTRAINT TASK_FQCY_UNIQ UNIQUE (TASK_ID, FQCYGROUP_INDEX)"
            + ")",
            "create table " + TASK_REPORT_MEASUREMENT_TABLE + " ("
            + "		TASK_ID				INT			not null,"	
            + "		MEASUREMENT_INDEX	INT			not null,"
            + "		MEASUREMENT			VARCHAR(30)	not null,"
            + "		CONSTRAINT TASK_MEASUREMENT_UNIQ UNIQUE (TASK_ID, MEASUREMENT_INDEX)"
            + ")"
    };
}
