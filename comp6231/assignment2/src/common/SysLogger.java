/*
 * COMP6231 Project
 * 
 * SysLogger
 * 
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-10-08 03:25:29 -0400 (Mon, 08 Oct 2012) $
 * $Rev: 147 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6231/project1/drs-common/src/common/SysLogger.java $
 * 
 */

package common;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


class LoggerTextFormat extends Formatter { 
    @Override 
    public String format(LogRecord record) { 
    	return record.getLevel() + ": " + record.getMessage() + "\r\n"; 
    } 
}

class OutputTextFormat extends Formatter { 
    @Override 
    public String format(LogRecord record) { 
    	return record.getMessage() + "\r\n"; 
    } 
}

public class SysLogger {
	private static boolean logEnable = true;
	private static Logger log = Logger.getLogger("COMP6231Prj");	
	private static boolean ifInit = false;
	
	public static void enableLog(boolean b) {
		logEnable = b;
	}
		
	// initialization: e.g. create log files
	public static void init() {
		// check if already init
		if (ifInit) {
			return;
		}
		
		// disable console logging
		log.setUseParentHandlers(false);
		
		// add a file handler
		String path = System.getProperty("user.dir") + "\\logs\\log.txt";
		FileHandler fileHandle;
		try {
			fileHandle = new FileHandler(path);
			fileHandle.setLevel(Level.ALL);
			fileHandle.setFormatter(new LoggerTextFormat());
			log.addHandler(fileHandle);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ifInit = true;
	}
	
	public static void info(String msg) {
		if (logEnable) {
			log.info(msg);
			System.out.println(msg);
		}
	}
	
	public static void err(String msg) {
		if (logEnable) {
			log.severe(msg);
			System.out.println(msg);
		}
	}
}

