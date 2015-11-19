package application;

import java.util.Calendar;
import com.ibm.json.java.JSONObject;
import com.ibm.json.java.JSONArray;

import seaitmmanager.Global;
import seaitmmanager.ItmConst;
import service.Log;

public class TaskingInfo implements ItmConst {
	
	//sensing schedule
	public class SenseSchedule {
		public double m_dStartTime;
		public double m_dStopTime;
		public int m_iSensePeriod; //in microseconds
		public int m_iSleepPeriod;	//in microseconds
	}
	
	//ellipse defination
	public class Ellipse {
		public double m_dCenterLat;
		public double m_dCenterLon;
		public double m_dMajorAxisLen;
		public double m_dMinorAxisLen;
	}
	
	public class Polygon {
		//TODO: define later
	}
	
	//geo fence
	public class GeoFence {
		public boolean m_bEnabled;
		public String m_sType; 
		public Ellipse m_oEllipse = new Ellipse();
		public Polygon m_oPolygon = new Polygon();
	}
	
	//report
	public class Report {
		public String m_sFormat;
		public String m_sType;
		public String m_sLink;
		public double m_dStartTime;
		public double m_dEndTime;
		public int m_iInterval;
	}
	
	//channel list
	public class ChannelList {
		public int m_iChannelId;
		public double m_dCenterFqcy;
		public double m_dBandWidth;
	}
	
	//frequency groups
	public class FqcyGroups {
		public double m_dStartFqcy;
		public double m_dEndFqcy;
		public double m_dStep;
	}
	
	//report data
	public class ReportData {
		public ChannelList[] m_aChannelList = null;
		public FqcyGroups[] m_aFrequencyGroup = null;
		public String [] m_aRequestedMeasurements = null;
	}
	
	public int m_iMsgId;
	public int m_iMissionId;
	public int m_iTaskId;
	public int m_iSensorId;
	public String m_sSensorName;
	public int m_iSsuId;
	public String m_sSsuName;
	public String m_sRole;
	public String m_sSubRole;
	
	public SenseSchedule m_oSenseSchedule = new SenseSchedule();
	public GeoFence m_oGeoFence = new GeoFence();
	public Report m_oReport = new Report();
	public ReportData m_oReportData = new ReportData();
	
	public TaskingInfo() {
		
	}
	
	//create a generate task that could be sent to many sensors, but do not initial sensor id/name/ssu infor
	//the sensor id/name/ssu info will be given later, when the task is sent out to each sensor
	public void vCreateFakeTask(int msg_id, int mission_id, int task_id) {
		m_iMsgId = msg_id;
		m_iMissionId = mission_id;
		m_iTaskId = task_id;
		m_sRole = ROLE[0];	//scanner
		switch (m_sRole) {
			case SCANNER:
				m_sSubRole = SCANNER_SUBROLE[0];	//channel_raw
				break;
			default:
				Log.print(DEBUG_ERROR, "XXXXXXXX Unknown Role Value: " + m_sRole);
				System.exit(1);
				break;
		}
		
		Calendar c = Calendar.getInstance();
		long currentTime = c.getTimeInMillis();
		double start_time = (double)(currentTime/1000);
		m_oSenseSchedule.m_dStartTime = start_time;
		m_oSenseSchedule.m_dStopTime = m_oSenseSchedule.m_dStartTime + 3600.00;
		m_oSenseSchedule.m_iSensePeriod = 1;
		m_oSenseSchedule.m_iSleepPeriod = 10 * 1000 * 1000;
		
		m_oGeoFence.m_bEnabled = true;
		m_oGeoFence.m_sType = GEOFENCE_TYPE[0];	//ellipse
		m_oGeoFence.m_oEllipse.m_dCenterLon = -75.79;
		m_oGeoFence.m_oEllipse.m_dCenterLat = 45.45;
		m_oGeoFence.m_oEllipse.m_dMajorAxisLen = 100.00;
		m_oGeoFence.m_oEllipse.m_dMinorAxisLen = 50.00;
		
		m_oReport.m_sFormat = REPORT_FORMAT[0];	//netCDE
		m_oReport.m_sType = REPORT_TYPE[0];	//realTime
		m_oReport.m_sLink = REPORT_LINK[1];	//WiFi
		m_oReport.m_dStartTime = m_oSenseSchedule.m_dStartTime + 0.1;
		m_oReport.m_dEndTime = m_oSenseSchedule.m_dStopTime;
		m_oReport.m_iInterval = 10;
		
		m_oReportData.m_aChannelList = new ChannelList[1];
		m_oReportData.m_aChannelList[0] = new ChannelList();
		m_oReportData.m_aChannelList[0].m_iChannelId = 1;
		m_oReportData.m_aChannelList[0].m_dCenterFqcy = 700.00;
		m_oReportData.m_aChannelList[0].m_dBandWidth = 5.00;
		m_oReportData.m_aRequestedMeasurements = new String[3];
		switch(m_sSubRole) {
			case CHANNEL_RAW:
				m_oReportData.m_aRequestedMeasurements[0] = CHANNEL_RAW_MEASUREMENTS[0];
				m_oReportData.m_aRequestedMeasurements[1] = CHANNEL_RAW_MEASUREMENTS[1];
				m_oReportData.m_aRequestedMeasurements[2] = CHANNEL_RAW_MEASUREMENTS[2];
				break;
			default:
				Log.print(DEBUG_ERROR, "XXXXXXXX Unknown Sub-Role Value: " + m_sSubRole);
				System.exit(1);
				break;
		}
	}
	
	public JSONObject oBuildTaskJson() {
		JSONObject mainTask = new JSONObject();
		mainTask.put("messageId", m_iMsgId);
		mainTask.put("missionId", m_iMissionId);
    	mainTask.put("taskId", m_iTaskId);
    	mainTask.put("sensorId", m_iSensorId);
    	mainTask.put("sensorName", m_sSensorName);
    	mainTask.put("ssuId", m_iSsuId);
    	mainTask.put("ssuName", m_sSsuName);
    	mainTask.put("role", m_sRole);
    	mainTask.put("subrole", m_sSubRole);
    	//Sensing Schedule
    	JSONObject sensingSchedule = new JSONObject();
    	sensingSchedule.put("startTime", m_oSenseSchedule.m_dStartTime);
    	sensingSchedule.put("stopTime", m_oSenseSchedule.m_dStopTime);
    	sensingSchedule.put("sensePeriod", m_oSenseSchedule.m_iSensePeriod);
    	sensingSchedule.put("sleepPeriod", m_oSenseSchedule.m_iSleepPeriod);
    	mainTask.put("senseSchedule", sensingSchedule);
    	//Geo Fences
    	JSONObject geofenceParameter = new JSONObject();
    	geofenceParameter.put("enabled", m_oGeoFence.m_bEnabled);
    	if (m_oGeoFence.m_bEnabled) {
    		geofenceParameter.put("type", m_oGeoFence.m_sType);
    		switch (m_oGeoFence.m_sType) {
    			case ELLIPSE:
    				geofenceParameter.put("centerLat", m_oGeoFence.m_oEllipse.m_dCenterLat);
    				geofenceParameter.put("centerLon", m_oGeoFence.m_oEllipse.m_dCenterLon);
    				geofenceParameter.put("minorAxisLen", m_oGeoFence.m_oEllipse.m_dMinorAxisLen);
    				geofenceParameter.put("majorAxisLen", m_oGeoFence.m_oEllipse.m_dMajorAxisLen);
    				break;
    			default:
    				Log.print(DEBUG_ERROR, "XXXXXXXX Unknown GEO Fence Type: " + m_oGeoFence.m_sType);
    				System.exit(1);
    				break;
    		}
    	}
    	mainTask.put("geoFence", geofenceParameter);
    	//report
    	JSONObject reportParameter = new JSONObject();
    	reportParameter.put("format", m_oReport.m_sFormat);
    	reportParameter.put("type", m_oReport.m_sType);
    	reportParameter.put("links", m_oReport.m_sLink);
    	JSONObject reportSchedule = new JSONObject();
    	reportSchedule.put("startTime", m_oReport.m_dStartTime);
    	reportSchedule.put("endTime", m_oReport.m_dEndTime);
    	reportSchedule.put("interval", m_oReport.m_iInterval);
    	reportParameter.put("scheduling", reportSchedule);
    	mainTask.put("report", reportParameter);
    	//reportData
    	JSONObject reportData = new JSONObject();
    	switch (m_sSubRole) {
    		case CHANNEL_RAW:
    		case CHANNEL_CONSOLIDATED:
    			JSONArray channelArray = new JSONArray();
    			for (int i=0; i<m_oReportData.m_aChannelList.length; i++) {
    				JSONObject channel = new JSONObject();
    				channel.put("channelId", m_oReportData.m_aChannelList[i].m_iChannelId);
    				channel.put("centerFqcy", m_oReportData.m_aChannelList[i].m_dCenterFqcy);
    				channel.put("bandwidth", m_oReportData.m_aChannelList[i].m_dBandWidth);
    				channelArray.add(i, channel);
    			}
    			reportData.put("channelList", channelArray);
    			break;
    		default:
    			Log.print(DEBUG_ERROR, "XXXXXXXX Unknown Sub Role Type: " + m_sSubRole);
				System.exit(1);
				break;
    	}
    	JSONArray measurementArray = new JSONArray();
    	for (int i=0; i<m_oReportData.m_aRequestedMeasurements.length; i++) {
    		measurementArray.add(i, m_oReportData.m_aRequestedMeasurements[i]);
    	}
    	reportData.put("requestMeasurements", measurementArray);
    	mainTask.put("reportData", reportData);
		return mainTask;
	}
	
	public void vInsertMissuibTaskDB() {
		Global.g_oDbService.vInsertTableEntry(MISSION_TASK_TABLE, "" + m_iMsgId + ", " + m_iMissionId + ", " + m_iTaskId);
	}
	
	public void vInsertTaskDB() {
		String insert = "";
		insert += m_iTaskId;
		insert += ", ";
		insert += "'";
		insert += m_sRole;
		insert += "'";
		insert += ", ";
		insert += "'";
		insert += m_sSubRole;
		insert += "'";
		insert += ", ";
		insert += m_oGeoFence.m_bEnabled?1:0;
		insert += ", ";
		insert += "'";
		insert += m_oGeoFence.m_sType;
		insert += "'";
		insert += ", ";
		insert += m_oSenseSchedule.m_dStartTime;
		insert += ", ";
		insert += m_oSenseSchedule.m_dStopTime;
		insert += ", ";
		insert += m_oSenseSchedule.m_iSensePeriod;
		insert += ", ";
		insert += m_oSenseSchedule.m_iSleepPeriod;
		insert += ", ";
		insert += "'";
		insert += m_oReport.m_sFormat;
		insert += "'";
		insert += ", ";
		insert += "'";
		insert += m_oReport.m_sType;
		insert += "'";
		insert += ", ";
		insert += "'";
		insert += m_oReport.m_sLink;
		insert += "'";
		insert += ", ";
		insert += m_oReport.m_dStartTime;
		insert += ", ";
		insert += m_oReport.m_dEndTime;
		insert += ", ";
		insert += m_oReport.m_iInterval;
		insert += ", ";
		if (m_oReportData.m_aChannelList != null)
		{
			insert += m_oReportData.m_aChannelList.length;
		} else {
			insert += 0;
		}
		insert += ", ";
		if (m_oReportData.m_aFrequencyGroup != null) {
			insert += m_oReportData.m_aFrequencyGroup.length;
		} else {
			insert += 0;
		}
		insert += ", ";
		insert += m_oReportData.m_aRequestedMeasurements.length;
		System.out.println("+++ the result command is: " + insert);
		
		Global.g_oDbService.vInsertTableEntry(TASK_TABLE, insert);
	}
	
	public void vInsertGeoFenceDB() {
		if (m_oGeoFence.m_bEnabled) {
    		switch (m_oGeoFence.m_sType) {
    			case ELLIPSE:
    				Global.g_oDbService.vInsertTableEntry(TASK_ELLIPSE_TABLE, "" + m_iTaskId + ", " 
    						+ m_oGeoFence.m_oEllipse.m_dCenterLat + ", "
    						+ m_oGeoFence.m_oEllipse.m_dCenterLon + ", "
    						+ m_oGeoFence.m_oEllipse.m_dMajorAxisLen + ", "
    						+ m_oGeoFence.m_oEllipse.m_dMinorAxisLen);
    				break;
    			default:
    				Log.print(DEBUG_ERROR, "XXXXXXXX Unknown GEO Fence Type: " + m_oGeoFence.m_sType);
    				System.exit(1);
    				break;	
    		}
		}
	}
	
	public void vInsertSensorTaskDB(int input_status) {
		Global.g_oDbService.vInsertTableEntry(TASK_SENSOR_TABLE, "" + m_iTaskId + ", "
							+ m_iSensorId + ", "
							+ "'" + m_sSensorName + "'," 
							+ m_iSsuId + ", "
							+ "'" + m_sSsuName + "',"
							+ input_status);
	}
	
	public void vInsertTaskReportChannelDB() {
		if (m_oReportData.m_aChannelList == null) {
			return;
		}
		for (int i=0; i< m_oReportData.m_aChannelList.length; i++) {
			Global.g_oDbService.vInsertTableEntry(TASK_REPORT_CHANNELLIST_TABLE, "" + m_iTaskId + ","
					+ i + ", "
					+ m_oReportData.m_aChannelList[i].m_iChannelId + ", "
					+ m_oReportData.m_aChannelList[i].m_dCenterFqcy + ", "
					+ m_oReportData.m_aChannelList[i].m_dBandWidth);
		}
	}
	
	public void vInsertTaskReportFrequencyDB() {
		if (m_oReportData.m_aFrequencyGroup == null) {
			return;
		}
		for (int i=0; i< m_oReportData.m_aFrequencyGroup.length; i++) {
			Global.g_oDbService.vInsertTableEntry(TASK_REPORT_FQCYGROUP_TABLE, "" + m_iTaskId + ","
					+ i + ", "
					+ m_oReportData.m_aFrequencyGroup[i].m_dStartFqcy + ", "
					+ m_oReportData.m_aFrequencyGroup[i].m_dEndFqcy + ", "
					+ m_oReportData.m_aFrequencyGroup[i].m_dStep);
		}
	}
	
	public void vInsertTaskMeasurementDB() {
		for (int i=0; i< m_oReportData.m_aRequestedMeasurements.length; i++) {
			Global.g_oDbService.vInsertTableEntry(TASK_REPORT_MEASUREMENT_TABLE, "" + m_iTaskId + ","
					+ i + ", "
					+ "'" + m_oReportData.m_aRequestedMeasurements[i] + "'");
		}
	}
}
