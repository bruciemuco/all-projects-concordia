/*
 * COMP6421 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * Compiler Design (COMP 442/642) Winter 2012 
 * Assignment 1, Lexical Analyzer
 *  
 * $Author: ewan.msn@gmail.com $
 * $Date: 2011-11-16 01:13:08 -0500 (Wed, 16 Nov 2011) $
 * $Rev: 60 $
 * $HeadURL: https://comp6471.googlecode.com/svn/Project6/src/utils/SysLogger.java $
 * 
 */


import java.text.SimpleDateFormat;

import LexicalAnalyzer.InputLoader;

import utils.SysLogger;


public class COMP6421Ass1Main {
	public static void main(String[] args) {
		// create and initialize the logger
		SysLogger.init();
	
		// format the result.txt file.
		SimpleDateFormat tmpDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetimeNow = tmpDate.format(new java.util.Date());
		SysLogger.output(datetimeNow + "\nThe result is:" + "\n");
		
		// load all txt files
		InputLoader txtLoader = new InputLoader();
		String path = System.getProperty("user.dir") + "\\input";
		if (txtLoader.loadTextFiles(path) != 0) {
			return;
		}
		SysLogger.output("Too many files under directory: ");
	}
}
