/*
 * COMP6231 Project
 * 
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-10-28 00:12:47 -0400 (Sun, 28 Oct 2012) $
 * $Rev: 181 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6231/assignment2/src/common/SvrInfo.java $
 * 
 */

package common;

import ca.com.Concordia.distributed.Ass_2.DRS_ServerMTL.SVR_Montreal;
import ca.com.Concordia.distributed.Ass_2.DRS_ServerTNT.SVR_Toronto;
import ca.com.Concordia.distributed.Ass_2.DRS_ServerVAN.SVR_Vancouver;
import DRS2Common.Server_AOM_ALL;
import server.DRSServer_Montreal;
import server.DRSServer_Toronto;
import server.DRSServer_Vancouver;

public class ServersCommon {
	private String RMName;
	
	ServersCommon(String name) {
		RMName = name;
	}
	
	public void startAllServers() {
		if (RMName.equals(Conf.RM_NAME_DRS1)) {
			// implementation of DRS 1 (developed by Yuan Tao)
			DRSServer_Montreal.svrStart();
			DRSServer_Toronto.svrStart();
			DRSServer_Vancouver.svrStart();
			
		} else if (RMName.equals(Conf.RM_NAME_DRS2)) {
			// implementation of DRS 2 (developed by Yichen Li)
			Server_AOM_ALL.main(null);
			
		} else {
			// implementation of DRS 3 (developed by Xiaodong Zhang)
			SVR_Montreal.svrStart();
			SVR_Toronto.svrStart();
			SVR_Vancouver.svrStart();
		}	
	}
	
	public void stopAllServers() {
		
	}
	
	public void restartAllServers() {
		SysLogger.info("stoping all servers...");
		stopAllServers();
		
		SysLogger.info("starting all servers...");
		startAllServers();
	}
	
	public int setSyncData(String data) {
		SysLogger.info("storing new data to new servers...");
		
		if (RMName.equals(Conf.RM_NAME_DRS1)) {
			// implementation of DRS 1 (developed by Yuan Tao)
			DRSServer_Montreal.reloadData(data);
			DRSServer_Toronto.reloadData(data);
			DRSServer_Vancouver.reloadData(data);
			
		} else if (RMName.equals(Conf.RM_NAME_DRS2)) {
			// implementation of DRS 2 (developed by Yichen Li)
			Server_AOM_ALL.reloadData(data);
			
		} else {
			// implementation of DRS 3 (developed by Xiaodong Zhang)
			SVR_Montreal.reloadData(data);
			SVR_Toronto.reloadData(data);
			SVR_Vancouver.reloadData(data);
		}	
		return 0;
	}
	
	public String getSyncData() {
		SysLogger.info("getSyncData: get data in memory");
		
		if (RMName.equals(Conf.RM_NAME_DRS1)) {
			// implementation of DRS 1 (developed by Yuan Tao)
			String ret = DRSServer_Montreal.getData();
			ret += "," + DRSServer_Toronto.getData();
			ret += "," + DRSServer_Vancouver.getData();
			return ret;
			
		} else if (RMName.equals(Conf.RM_NAME_DRS2)) {
			// implementation of DRS 2 (developed by Yichen Li)
			return Server_AOM_ALL.getData();
			
		} else {
			// implementation of DRS 3 (developed by Xiaodong Zhang)
			String ret = SVR_Montreal.getData();
			ret += "," + SVR_Toronto.getData();
			ret += "," + SVR_Vancouver.getData();
			return ret;			
		}
		
	}
}
