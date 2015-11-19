package application;

import service.*;
import seaitmmanager.ItmConst;

public class MemberDevice implements ItmConst {
	public String m_sDeviceType = null;
	public String m_sDeviceId = null;
	public int m_iSensorId = -1;
	public int m_iSsuId = -1;
	public String m_sSsuName = null;

	public MemberDevice() {
        
    }
    
    public MemberDevice(String type, String id, int sensor_id, int ssu_id, String ssu_name) {
        m_sDeviceType = new String(type);
        m_sDeviceId = new String(id);
        m_iSensorId = sensor_id;
        m_iSsuId = ssu_id;
        m_sSsuName = ssu_name;
    }
    
    public void vClone(MemberDevice device) {
        m_sDeviceType = device.m_sDeviceType;
        m_sDeviceId = device.m_sDeviceId;
        m_iSensorId = device.m_iSensorId;
        m_iSsuId = device.m_iSsuId;
        m_sSsuName = device.m_sSsuName;
    }
    
    //do not need to compare the SSU Name, it is not used when checking resending
    public boolean bIsEqual(MemberDevice device) {
    	if (m_sDeviceType.equals(device.m_sDeviceType) && m_sDeviceId.equals(device.m_sDeviceId)
    			&& m_iSensorId == device.m_iSensorId && m_iSsuId == device.m_iSsuId) {
    		return true;
    	}
    	else
    		return false;
    }
    
    public void vLog() {
        Log.print(DEBUG_INFO, "- Type: " + m_sDeviceType + " Name: " + m_sDeviceId + " Id: " 
        		+ m_iSensorId + " Ssu Id: " + m_iSsuId + " Ssu Name: " + m_sSsuName);
    }
}
