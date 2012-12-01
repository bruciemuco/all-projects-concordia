package exe;

import common.Conf;
import common.RM;

public class RM2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RM rm = new RM(Conf.RM_NAME_DRS2, Conf.RM_PORT_DRS2, false);
		rm.start();
	}

}
