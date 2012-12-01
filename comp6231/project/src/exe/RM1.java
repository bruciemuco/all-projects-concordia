package exe;

import common.Conf;
import common.RM;

public class RM1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RM rm = new RM(Conf.RM_NAME_DRS1, Conf.RM_PORT_DRS1, true);
		rm.start();
	}

}
