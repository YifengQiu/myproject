package application;

import service.*;
import seaitmmanager.ItmConst;

import java.util.ArrayList;

public class MemberDeviceList implements ItmConst {
	public ArrayList<MemberDevice> m_oMemberDeviceList;
    
    public MemberDeviceList() {
        m_oMemberDeviceList = new ArrayList<MemberDevice>();
    }
    
    public int iGetSize() {
    	return m_oMemberDeviceList.size();
    }
    
    public MemberDevice oGetMember(int i) {
    	return m_oMemberDeviceList.get(i);
    }
    
    public int iFind(MemberDevice member) {
        for (int i = 0; i < m_oMemberDeviceList.size(); i++) {
            MemberDevice currentMember = m_oMemberDeviceList.get(i);
            if (currentMember.m_sDeviceId.equals(member.m_sDeviceId) && currentMember.m_sDeviceType.equals(member.m_sDeviceType)) {
                return i;
            }
        }
        return -1;            
   }
   
   /** add a new member, or update the member information 
     * if it is a new member, return TRUE
     * if an existing member, return FALSE
     */
   public boolean bUpdateMember(MemberDevice rcvdMember) {
       boolean newMemberInserted = false;
       int position = iFind(rcvdMember);
 
       if ( position!= -1) {
           Log.print(DEBUG_INFO, "Device with Id: " + rcvdMember.m_sDeviceId + " Type: " + rcvdMember.m_sDeviceType + " already in the list!");
       } else {
           m_oMemberDeviceList.add(rcvdMember);
           newMemberInserted = true;
       }
       return newMemberInserted;
   }
   
   public void vLog() {
       Log.print(DEBUG_INFO, "************ Device Members **********");
       for (int i = 0; i < m_oMemberDeviceList.size(); i++) {
            MemberDevice currentMember = m_oMemberDeviceList.get(i);
            currentMember.vLog();
        }
       Log.print(DEBUG_INFO, "***************************************");
   }
}
