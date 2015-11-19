package application;

import seaitmmanager.Global;
import service.Log;
import seaitmmanager.ItmConst;

import java.util.ArrayList;

import com.ibm.json.java.JSONObject;

public class MsgResent implements ItmConst {
	public String m_sResentPendingMsgType = null;
	public TaskingInfo m_oTaskingInfo = null;
	
	/** When the reply should be generated */
    public int m_iRemainingResending = Global.g_iMaxMessageResent;
    public int m_iNextResendingTime = Global.g_iMessageResentInterval;
    
    /** to whom the re-sent message should be sent to */
    public ArrayList<MemberDevice> m_oMsgRcverList = new ArrayList<MemberDevice>();
    
    public MsgResent () {
    	
    }
    
    public boolean bIsSame(String msgType, int missionID, int taskID) {
    	//System.out.println("+++++ is same: " + msgType + " missionID" + missionID + " taskID: " + taskID);
        if(m_sResentPendingMsgType.equals(msgType) && m_oTaskingInfo.m_iMissionId == missionID && m_oTaskingInfo.m_iTaskId == taskID) {
            //System.out.println("return true");
        	return true;
        } else {
        	//System.out.println("return false");
            return false;
        }
    }
    
    public boolean bIsSame(MsgResent input) {
        boolean same = bIsSame(input.m_sResentPendingMsgType, input.m_oTaskingInfo.m_iMissionId, input.m_oTaskingInfo.m_iTaskId);
        return same;
    }
    
    public void vLog() {
        Log.print(DEBUG_INFO, "- MsgType: " + m_sResentPendingMsgType + " MissionID: " + m_oTaskingInfo.m_iMissionId + " TaskID: " + m_oTaskingInfo.m_iTaskId + " Remaining Reresend: " + m_iRemainingResending);
        Log.print(DEBUG_INFO, "          DestList: ");
        for (int i=0; i< m_oMsgRcverList.size(); i++) {
            m_oMsgRcverList.get(i).vLog();
        }
    }
}
