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

import common.SvrInfo;

import server.DRSCommonServiceServerImpl;

public class Server_AOM_T implements Runnable{
	
	static DRSCommonServiceServerImpl servant1 = null;

	public static void main(String[] args) {
		createSvr(SvrInfo.SVR2_NAME_TORONTO);
	}
	
	public static void createSvr(String svrName) {

		Properties props = System.getProperties();
		props.setProperty("org.omg.CORBA.ORBClass", "com.sun.corba.se.internal.POA.POAORB");
		props.setProperty("org.omg.CORBA.ORBSingletonClass", "com.sun.corba.se.internal.corba.ORBSingleton");

		try {
			// Initialize the ORB.
			org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init((String[])null, props);

			// get a reference to the root POA
			org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
			POA poaRoot = POAHelper.narrow(obj);

			// Create policies for our persistent POA
			org.omg.CORBA.Policy[] policies = {
					
					// poaRoot.create_lifespan_policy(LifespanPolicyValue.PERSISTENT),
					poaRoot.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID),
					poaRoot.create_thread_policy(ThreadPolicyValue.ORB_CTRL_MODEL) 
			};

			// Create myPOA with the right policies
			POA poa = poaRoot.create_POA("DRSServerImpl_poa",	poaRoot.the_POAManager(), policies);

			// Create the servant
			// Montreal servant
			
			servant1 = new DRSCommonServiceServerImpl(svrName);
			
//			// Toronto servant
//			DRSServerImpl servant2 = new DRSServerImpl("Toronto");
//			
//			// Vancouver servant
//			DRSServerImpl servant3 = new DRSServerImpl("Vancouver");
//			
			
			

			// Activate the servant with the ID on myPOA
		//	byte[] objectId = "AnyObjectID".getBytes();
			//poa.activate_object_with_id(objectId, servant);
			
			byte[] objectId1 = svrName.getBytes();
			poa.activate_object_with_id(objectId1, servant1);

		
//			
//			byte[] objectId2 = "Toronto".getBytes();
//			poa.activate_object_with_id(objectId2, servant2);
//			
//		
//			
//			byte[] objectId3 = "Vancouver".getBytes();
//			poa.activate_object_with_id(objectId3, servant3);
//			
//			
			
			
			// Activate the POA manager
			poaRoot.the_POAManager().activate();

			// Get a reference to the servant and write it down.
			obj = poa.servant_to_reference(servant1); // Montreal
			
			// ---- Uncomment below to enable Naming Service access. ----
//			 org.omg.CORBA.Object ncobj = orb.resolve_initial_references("NameService");
//			 NamingContextExt nc = NamingContextExtHelper.narrow(ncobj);
//			// nc.bind(nc.to_name("MyServerObject"), obj);
//			 nc.rebind(nc.to_name("Montreal"),obj);

			PrintWriter ps = new PrintWriter(new FileOutputStream(new File(svrName)));
			ps.println(orb.object_to_string(obj));
			 
			 
//			 obj = poa.servant_to_reference(servant2); // Toronto
//			 nc.rebind(nc.to_name("Toronto"), obj);
//			 ps.println(orb.object_to_string(obj));
//
//			 
//			 obj = poa.servant_to_reference(servant3); // Vancouver
//             nc.rebind(nc.to_name("Vancouver"), obj);	
// 		   	ps.println(orb.object_to_string(obj));

			 

			ps.close();
			

			System.out.println("RM2: " + svrName + " is ready...");

			// Wait for incoming requests
			orb.run();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	public static void reloadData(String data) {
		servant1.reloadData(data);
	}
	
	public static String getData() {
		return servant1.getData();
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		main(null);
	}
	
	public static void svrStart() {
		(new Thread(new Server_AOM_T())).start();
	}
}
