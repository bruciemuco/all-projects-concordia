package common;

public class Conf {
	// the name of the machine for each RM
	public static final String RM_NAME_DRS1 = "localhost";
	public static final String RM_NAME_DRS2 = "127.0.0.1";
	public static final String RM_NAME_DRS3 = "192.168.1.71";
	
	// listening port for each RM
	public static final int PORT_BASE = 20000;
	public static final int RM_PORT_DRS1 = PORT_BASE + 1;
	public static final int RM_PORT_DRS2 = PORT_BASE + 2;
	public static final int RM_PORT_DRS3 = PORT_BASE + 3;

	// listening port for FE
	public static final int FE_UDP_PORT_BASE = PORT_BASE + 1000;
	
	// 
	public static final int FE_CMD_BUY = 1;
	public static final int FE_CMD_RETURN = 2;
	public static final int FE_CMD_CHECK = 3;
	public static final int FE_CMD_EXCHANGE = 4;
	
	public static final int FE_CMD_REPLACE = 5;
	
	public static final int RM_CMD_SEQUENCER = 10;
	
	public static final int RM_CMD_SYNC_DATA_REQ = 20;
	public static final int RM_CMD_SYNC_DATA_RESP = 21;
}
