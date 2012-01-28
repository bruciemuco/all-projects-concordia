/*
 * COMP6421 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2011-11-16 01:13:08 -0500 (Wed, 16 Nov 2011) $
 * $Rev: 60 $
 * $HeadURL: https://comp6471.googlecode.com/svn/Project6/src/utils/SysLogger.java $
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
    	return record.getLevel() + ": " + record.getMessage() + "\n"; 
    } 
}

class OutputTextFormat extends Formatter { 
    @Override 
    public String format(LogRecord record) { 
    	return record.getMessage() + "\n"; 
    } 
}

public class SysLogger {
	public static Logger log = Logger.getLogger("COMP6421Project");
	public static Logger result = Logger.getLogger("COMP6421ProjectResult");
	
	private static FileHandler fhLast = null;
		
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

	public static void setResultFilename(String filename) {
		// create a logger to show the results of the program.
		String path = filename;
		FileHandler fileHandle;
		try {
			fileHandle = new FileHandler(path);
			fileHandle.setLevel(Level.ALL);
			fileHandle.setFormatter(new OutputTextFormat());
			if (fhLast != null) {
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
		log.info(msg);
	}

	public static void info(String msg) {
		log.info(msg);
		result.info(msg);
	}
	
	public static void err(String msg) {
		log.severe(msg);
		result.severe(msg);
	}
}


	
	
	