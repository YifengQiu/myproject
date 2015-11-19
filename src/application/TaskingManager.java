package application;

import service.*;
import seaitmmanager.*;

import org.apache.commons.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.ibm.iotf.client.api.Device;
import com.ibm.iotf.client.api.HistoricalEvent;
import com.ibm.iotf.client.app.Command;
import com.ibm.iotf.client.app.Event;
import com.ibm.iotf.client.app.EventCallback;
import com.ibm.json.java.JSONObject;

import application.TaskingInfo.SenseSchedule;

import com.ibm.json.java.JSONArray;

import ca.crc.sea.AppClient;
import ca.crc.sea.BlueMixAppClient;
import ca.crc.sea.test.BlueMixAppClientTest.MyEventCallback;

public class TaskingManager implements ItmConst {
	
	static private int m_iMissionCount = 1;
	static private int m_iTaskCount = 1;
	static private int m_iMsgCount = 1;
	
	static private int m_iCounter = 0;
	private BlueMixAppClient client = null;
	private Properties properties = null; 
	
	/** Device Member Table */
    static private MemberDeviceList m_oDeviceMembers = null;
    
    /** Resent Message table */
    static private MsgResentList m_oResentMsgs = new MsgResentList();
	
    private Timer m_TaskingTimer = new Timer();
    private TimerTask m_taskingManagerTimerTask = new TimerTask() {
        public void run() {
            vRepeatJob();
        }
    };
    
    public TaskingManager () {
    	/*** Create Task-related Tables ***/
    	if (Global.db_name != null) {
    		Global.g_oDbService.vCreateTaskTable(Global.db_name);
    	} else {
    		Log.print(DEBUG_ERROR, "XXXXXXXX THE db_name has NOT been inited yet! XXXXXXXX");
    		System.exit(1);
    	}
    	
    	/*** Connect to Cloud ***/
    	try {
    		InputStream in = new FileInputStream("app.properties");
    		properties = new Properties();
    		properties.load(in);
    		in.close();
    		client = new BlueMixAppClient(properties);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return;
    	}
    	client.connect();
		client.setEventCallback(new MyEventCallback(client));
		//client.subscribeToDeviceEvents();
		client.subscribeToDeviceEvents("+", "+", TASK_ACK, 0);
		
		/*** Get Member Devices from Cloud ***/
		m_oDeviceMembers = new MemberDeviceList();
		getCloudDevices();
 
		/*** Start Timer ***/
        m_TaskingTimer.scheduleAtFixedRate(m_taskingManagerTimerTask, 0, 1000);
    }
    
    public void vRepeatJob() {
        Log.print(DEBUG_ALL, "============= TaskingManager Timer Fired! Count = " + m_iCounter);
        
        //check if it is a time to resent an un-acked message
        vCheckResent();
        
        m_iCounter +=1;
        
        if (m_iCounter == 10) {
        	//send a tasking message out
        	sendTaskMsg();
        	
            //m_iCounter = 0;
        }
    }
    
    private void getCloudDevices() {
    	Device [] listDevices = client.getDevices();
    	System.out.println("Devices registered = " + listDevices.length);
    	
    	for(Device device : listDevices){
	    	//this is a patch, removing surrounding quote out of the string
	    	String deviceType = device.getType().replaceAll("^\"|\"$", "");
	    	String deviceId = device.getId().replaceAll("^\"|\"$", "");
	    	
	    	MemberDevice member = new MemberDevice(deviceType, deviceId, -1, -1, null);
	    	m_oDeviceMembers.bUpdateMember(member);
    	}
    	
    	Log.print(DEBUG_INFO, "After get devices from cloud, the device list is");
    	m_oDeviceMembers.vLog();
    }
    
    private void sendTaskMsg() {
    	Log.print(DEBUG_INFO, "Tasking Manager is sending Task Message!");
    	
    	/*** Build a Tasking Info and convert to JSON ***/
    	TaskingInfo task = new TaskingInfo();
    	task.vCreateFakeTask(m_iMsgCount, m_iMissionCount, m_iTaskCount);
    	// update Mission Count and Task Count
    	m_iMsgCount += 1;
    	m_iMissionCount += 1;
    	m_iTaskCount += 1;
        
        /*** update the general task database ***/
        task.vInsertMissuibTaskDB();
        task.vInsertTaskDB();
        task.vInsertGeoFenceDB();
        task.vInsertTaskReportChannelDB();
        task.vInsertTaskReportFrequencyDB();
        task.vInsertTaskMeasurementDB();
        
        /*** remenber current Tasking Msg as resent msg ***/
        MsgResent currentResentMsg = new MsgResent();
        currentResentMsg.m_sResentPendingMsgType = TASK_MSG;
        currentResentMsg.m_oTaskingInfo = task;
        
        //send a tasking message out
        //fake a sensor id
		int sn_id = 1;
    	for (int i = 0; i < m_oDeviceMembers.iGetSize(); i++) {
    		MemberDevice device = m_oDeviceMembers.oGetMember(i);
    		
    		//update the sensor id/name/ssu
    		task.m_iSensorId = sn_id;	//fake one for the time being
    		task.m_sSensorName = device.m_sDeviceId;
    		task.m_iSsuId = 1;
    		task.m_sSsuName = "SSU-1-RAW";	//fake one for the time being
    		
    		JSONObject jsonObject = task.oBuildTaskJson();
            Log.print(DEBUG_INFO, "the Tasking Message to be sent out is: " + jsonObject.toString());
            
            //update the task-sensor table
            task.vInsertSensorTaskDB(TASK_STATUS_PENDING);
    		
    		//updatte the Resent Msg List
            device.m_iSensorId = task.m_iSensorId;
    		device.m_iSsuId = task.m_iSsuId;
    		device.m_sSsuName = task.m_sSsuName;
    		currentResentMsg.m_oMsgRcverList.add(device);
    		
    		//send the Task out
    		System.out.println("publish command to device type: " + device.m_sDeviceType + " device Id: " + device.m_sDeviceId);
    		client.publishCommand(device.m_sDeviceType, device.m_sDeviceId, TASK_MSG, jsonObject);
    		
    		sn_id += 1;
    	}
    	m_oResentMsgs.vAddResendMsg(currentResentMsg);
        Log.print(DEBUG_INFO, "Sending Task to Device Finished!");
        Log.print(DEBUG_INFO, "After send the Task Out, the ResendMsg List is");
        m_oResentMsgs.vLog();
    }
    
    public void vCheckResent() {
    	int i;
        for (i=0; i<m_oResentMsgs.m_oResendMsgList.size(); i++) {
            MsgResent currentResent = m_oResentMsgs.m_oResendMsgList.get(i);
            //update timer related to the message that need to be resent
            if(currentResent.m_iNextResendingTime > 0) {
                currentResent.m_iNextResendingTime -= 1;
            }
            if(currentResent.m_iNextResendingTime == 0) {
                //send the resent message out
                String msgType = currentResent.m_sResentPendingMsgType;
                switch (msgType) {
                	case TASK_MSG:
                		TaskingInfo resentTasking = currentResent.m_oTaskingInfo;
                		for (int j=0; j<currentResent.m_oMsgRcverList.size(); j++) {
                        	MemberDevice device = currentResent.m_oMsgRcverList.get(j);
                        	resentTasking.m_iSensorId = device.m_iSensorId;
                        	resentTasking.m_iSsuId = device.m_iSsuId;
                        	resentTasking.m_sSensorName = device.m_sDeviceId;
                        	resentTasking.m_sSsuName = device.m_sSsuName;
                        	JSONObject resentJson = resentTasking.oBuildTaskJson();
                        	System.out.println("resent command to device type: " + device.m_sDeviceType + " device Id: " + device.m_sDeviceId);
                    		client.publishCommand(device.m_sDeviceType, device.m_sDeviceId, msgType, resentJson);
                        }
                		break;
                	default:
                		Log.print(DEBUG_ERROR, "UNKNOWN Msg Type: " + msgType);	
                		System.exit(1);
                }
                
                //update the resentist
                currentResent.m_iNextResendingTime = Global.g_iMessageResentInterval;
                currentResent.m_iRemainingResending -= 1;
                
                if (currentResent.m_iRemainingResending < 0) {
                    Log.print(DEBUG_INFO, "XXXXXXXXXX all resent used, delete the entry!");
                    
                    //update the sensor_task table
                    String update_value = "TASK_STATUS = " + TASK_STATUS_FAILED;
                    for (int k=0; k<currentResent.m_oMsgRcverList.size(); k++) {
                    	MemberDevice device = currentResent.m_oMsgRcverList.get(k);
                    	String condition_value = "TASK_ID = " + currentResent.m_oTaskingInfo.m_iTaskId + " and SENSOR_ID = " + device.m_iSensorId  + " and SSU_ID = " + device.m_iSsuId;
                    	Global.g_oDbService.vUpdateTableEntry(TASK_SENSOR_TABLE, update_value, condition_value);
                    }
                    
                    //remove the entry from resending list
                    m_oResentMsgs.m_oResendMsgList.remove(currentResent);
                    i -= 1;
                }
                Log.print(DEBUG_INFO, "+++ after resent, the List is:");
                m_oResentMsgs.vLog();
            }
        }
    }
    
    static public void vProcessReceivedTaskAck(String input, MemberDevice ack_sender) {
    	/*** get the mission ID, task ID, Sensor ID, Ssu ID from the jsonObject ***/
    	long mission_id, task_id, sensor_id, ssu_id;
    	String rcv_time, rcv_status;
    	try {
        	JSONObject ackJSON = null;
    		ackJSON = JSONObject.parse(input);
    		JSONObject ackDetails = (JSONObject)ackJSON.get("d");
    		mission_id = (long)(ackDetails.get("missionId"));
    		task_id = (long) ackDetails.get("taskId");
    		sensor_id = (long) ackDetails.get("sensorId");
    		ssu_id = (long) ackDetails.get("ssuId");
    		rcv_time = (String) ackDetails.get("timestamp");
    		rcv_status = (String) ackDetails.get("status");
    		System.out.println("mission id: " + mission_id + " task id: " + task_id + " sensor id: " + sensor_id + " ssu id: " + ssu_id
    				+ " timestamp: " + rcv_time + " status: " + rcv_status);
    		
    		//update sensor_task database
    		String update_value, condition_value;
    		int task_status = TASK_STATUS_PENDING;
    		switch (rcv_status) {
    			case "accept":
    				task_status = TASK_STATUS_ACCEPT; 
    				break;
    			case "reject":
    				task_status = TASK_STATUS_REJECT;
    				break;
    			default:
    				Log.print(DEBUG_ERROR, "XXXXX UNKNOWN TASK STATUS: " + rcv_status);
    				System.exit(1);
    				break;
    		}
    		update_value = "TASK_STATUS = " + task_status;
    		condition_value = "TASK_ID = " + task_id + " and SENSOR_ID = " + sensor_id + " and SSU_ID = " + ssu_id;
    		Global.g_oDbService.vUpdateTableEntry(TASK_SENSOR_TABLE, update_value, condition_value);
    		
    		//update re-send message table
    		ack_sender.m_iSensorId = (int)sensor_id;
    		ack_sender.m_iSsuId = (int)ssu_id;
    		m_oResentMsgs.bRmvResendMsg(TASK_MSG, (int)mission_id, (int)task_id, ack_sender);
    		
    		Log.print(DEBUG_INFO, "+++ after process TaskAck, the resent List is:");
            m_oResentMsgs.vLog();
    		
    	} catch (Exception exp) {
    		Log.print(DEBUG_ERROR, exp.getMessage());
    		System.exit(1);
    	}
    }
    
    static public class MyEventCallback implements EventCallback {
    	BlueMixAppClient client; 

		public MyEventCallback(BlueMixAppClient client) {
			this.client = client;
		}

		@Override
		public void processEvent(Event e) {

				System.out.println("Event " + e.getEvent() + "=" + e.getPayload());
			
	            final String deviceId = e.getDeviceId();
	            final String deviceType = e.getDeviceType();
	            
	            System.out.println("Received Device ID: " + deviceId + " Device Type: " + deviceType);
	            
	            if(e.getEvent().equals(TASK_ACK)) {
	            	Log.print(DEBUG_INFO, "++++ Ack is received from Device Type:" + deviceType + " Device Name: " + deviceId);
	            	MemberDevice ack_sender = new MemberDevice();
	            	ack_sender.m_sDeviceType = deviceType;
	            	ack_sender.m_sDeviceId = deviceId;
	            	String input = e.getPayload();
	            	vProcessReceivedTaskAck(input, ack_sender);
	          }
		}
		@Override
		public void processCommand(Command cmd) {
			System.out.println("Command " + cmd.getPayload());
		}
	}
}
