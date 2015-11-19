package application;

import service.Log;
import seaitmmanager.ItmConst;

import java.util.ArrayList;

public class MsgResentList implements ItmConst {
	public ArrayList<MsgResent> m_oResendMsgList;
    
    public MsgResentList() {
        m_oResendMsgList = new ArrayList<MsgResent>();
    }
    
    public void vAddResendMsg(MsgResent msgResend) {
        for (int i=0; i<m_oResendMsgList.size(); i++) {
            MsgResent currentMsg = m_oResendMsgList.get(i);
            if(currentMsg.bIsSame(msgResend)) {
                Log.print(DEBUG_INFO, "***** ResentMsg Type: " + msgResend.m_sResentPendingMsgType + " missionID: " + msgResend.m_oTaskingInfo.m_iMissionId + " taskID: " + msgResend.m_oTaskingInfo.m_iTaskId + " alreay in the List! ");
                return;
            }
        }
        m_oResendMsgList.add(msgResend);
    }
    
    public boolean bRmvResendMsg(String msgType, int missionID, int taskID, MemberDevice ackDevice) {
        boolean rmvd = false;
        for (int i=0; i<m_oResendMsgList.size(); i++) {
            MsgResent currentMsg = m_oResendMsgList.get(i);
            if (currentMsg.bIsSame(msgType, missionID, taskID)) {
                //check if the device is in the rcverList of the currentMsg
            	//System.out.println("+++ looking at msg type: " + msgType + "mission ID: " + missionID + " task ID: " + taskID);
                for (int index=0; index< currentMsg.m_oMsgRcverList.size(); index++) {
                    MemberDevice device = currentMsg.m_oMsgRcverList.get(index);
                    if(device.bIsEqual(ackDevice)) {
                        //msg received by the ackIP, remove it from rcverList
                        currentMsg.m_oMsgRcverList.remove(index);
                        index -= 1;
                        rmvd = true;
                        break;
                    }
                }
                if(currentMsg.m_oMsgRcverList.isEmpty()) {
                   m_oResendMsgList.remove(currentMsg);
                }
                i -= 1;
                break;
            }
        }
        return rmvd;
    }
    
    public void vLog() {
        Log.print(DEBUG_INFO, "************ Resent Messages **********");
        for (int i = 0; i < m_oResendMsgList.size(); i++) {
            MsgResent currentMsg = m_oResendMsgList.get(i);
            currentMsg.vLog();
        }
       Log.print(DEBUG_INFO, "*********End of Resent Messages **********");
    }
    
}
