import java.io.IOException;
import java.text.SimpleDateFormat;

import tokenizer.InputLoader;
import tokenizer.Tokenizer;
import utils.SysLogger;


/*
 * COMP6791 Lab Assignment 1
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

public class LabAss1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// create and initialize the logger
		try {
			SysLogger.init();
		} catch (Exception e) {
			System.out.println("Make sure there is a directory named 'logs' under the root of program.");
		}
		
		// show greetings
		System.out.println("Welcome to COMP 6791 Lab Assignment 1");
		System.out.println("Developed by Yuan Tao.\n");
//		System.out.println("Press any key to begin...\n");
		
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			
//		}
	
		// load all test input files
		InputLoader testFilesLoader = new InputLoader();
		String path = System.getProperty("user.dir");
		
		if (testFilesLoader.loadTextFiles(path) != 0) {
			return;
		}

		// begin to tokenize each file
		for (int i = 0; i < testFilesLoader.lstFiles.size(); i++) {
			//create output file first
	    	SysLogger.setOutputFilenames(testFilesLoader.lstResultFiles.get(i));

			// create a Tokenizer
			Tokenizer scanner = new Tokenizer();
			
			if (scanner.init(testFilesLoader.lstFiles.get(i)) != 0) {
				continue;
			}
			
			scanner.getAllTokens();
		}

		System.out.println("\nThe program ends successfully!");
	}

}
