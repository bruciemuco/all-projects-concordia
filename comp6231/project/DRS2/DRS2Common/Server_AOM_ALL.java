package DRS2Common;

/* 
 * COMP6231-Assignment2-CORBA
 * Author: YICHEN LI
 * Student ID: 6389635
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.IdAssignmentPolicyValue;
// import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.ThreadPolicyValue;

import server.DRSServer_Montreal;
import server.DRSServer_Toronto;
import server.DRSServer_Vancouver;

public class Server_AOM_ALL {

	public static void main(String[] args) {

		try {
			Server_AOM_M.svrStart();
			Server_AOM_T.svrStart();
			Server_AOM_V.svrStart();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void reloadData(String data) {
		Server_AOM_M.reloadData(data);
		Server_AOM_T.reloadData(data);
		Server_AOM_V.reloadData(data);
	}
	
	public static String getData() {
		return Server_AOM_M.getData() + "," + Server_AOM_T.getData() + "," + 
				Server_AOM_V.getData();
	}
}
