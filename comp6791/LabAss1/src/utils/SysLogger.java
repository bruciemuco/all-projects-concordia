/*
 * COMP6791 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: yua_t $
 * $Date: 2012-04-08 17:25:55 -0400 (Sun, 08 Apr 2012) $
 * $Rev: 41 $
 * $HeadURL: svn+ssh://yua_t@login.encs.concordia.ca/home/y/yua_t/svn_resp/comp6421/src/utils/SysLogger.java $
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
	public static Logger log = Logger.getLogger("COMP6791Project");
	public static Logger result = Logger.getLogger("COMP6791ProjectResult");
	
	public static boolean bLexicalAnalyzer = false;
	
	private static FileHandler fhLast = null;
	private static FileHandler fhErrLast = null;
	
	private static boolean logEnable = true;
	
	public static void enableLog(boolean b) {
		logEnable = b;
	}
		
	// initialization: e.g. create log files
	public static void init() {
		// disable console logging
		log.setUseParentHandlers(false);
		result.setUseParentHandlers(false);
		
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
	}

	public static void setOutputFilenames(String filename) {
		// create a logger to show the results of the program.
		String path = filename;
		FileHandler fileHandle;
		try {
			fileHandle = new FileHandler(path);
			fileHandle.setLevel(Level.ALL);
			fileHandle.setFormatter(new OutputTextFormat());
			if (fhLast != null) {
				fhLast.flush();
				fhLast.close();
				result.removeHandler(fhLast);
			}
			result.addHandler(fileHandle);
			fhLast = fileHandle;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void log(String msg) {
		if (logEnable) {
			log.info(msg);
		}
	}

	public static void info(String msg) {
		if (logEnable) {
			log.info(msg);
			result.info(msg);
		}
	}
	
	public static void err(String msg) {
		if (logEnable) {
			log.severe(msg);
		}
	}
}


	
	
	