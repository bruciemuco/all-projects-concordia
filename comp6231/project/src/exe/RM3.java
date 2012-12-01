package exe;

import common.Conf;
import common.RM;

public class RM3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RM rm = new RM(Conf.RM_NAME_DRS3, Conf.RM_PORT_DRS3, false);
		rm.start();
	}

}
