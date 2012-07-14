/*
 * SOEN6611 Project
 * 
 * SysLogger
 * 
 * This file is created by Team F
 * Licensed under GNU GPL v3
 * 
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 * 
 */

package utils;

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
	private static boolean logEnable_ = true;
	private static Logger log_ = Logger.getLogger("MinskPrjD1");	
	private static boolean ifInit_ = false;
	
	public static void enableLog(boolean b) {
		logEnable_ = b;
	}
		
	// initialization: e.g. create log files
	public static void init() {
		// check if already init
		if (ifInit_) {
			return;
		}
		
		// disable console logging
		log_.setUseParentHandlers(false);
		
		// add a file handler
		String path = System.getProperty("user.dir") + "\\logs\\log.txt";
		FileHandler fileHandle;
		try {
			fileHandle = new FileHandler(path);
			fileHandle.setLevel(Level.ALL);
			fileHandle.setFormatter(new LoggerTextFormat());
			log_.addHandler(fileHandle);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ifInit_ = true;
	}
	
	public static void info(String msg) {
		if (logEnable_) {
			log_.info(msg);
		}
	}
	
	public static void err(String msg) {
		if (logEnable_) {
			log_.severe(msg);
		}
	}
}

