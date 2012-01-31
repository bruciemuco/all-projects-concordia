/*
 * COMP6421 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * Compiler Design (COMP 442/642) Winter 2012 
 * Assignment 1, Lexical Analyzer
 *  
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 * 
 */


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import LexicalAnalyzer.InputLoader;
import LexicalAnalyzer.LexicalAnalyzer;
import LexicalAnalyzer.StateMachineDriver;

import utils.SysLogger;


public class COMP6421Ass1Main {
	
	public static void main(String[] args) {
		// create and initialize the logger
		SysLogger.init();
		
		// show greetings
		System.out.println("Wellcome to COMP 6421 Project. v0.1");
		System.out.println("Developed by Yuan Tao.\n");
		System.out.println("Please read $ThisProgram\\readme.pdf first.");
		System.out.println("Please put all the test files under the root directoy of $ThisProgram\\input\\, which already has some sample files.");
		System.out.println("The result of the progrom will be stored accordingly in the files under $ThisProgram\\output\\ \n");
		System.out.println("Press any key to begin...\n");
		
		try {
			System.in.read();
		} catch (IOException e) {
			
		}
	
		// load all test input files
		InputLoader testFilesLoader = new InputLoader();
		String path = System.getProperty("user.dir");
		
		if (testFilesLoader.loadTextFiles(path) != 0) {
			return;
		}

		// create state machine table driver
		if (StateMachineDriver.init() != 0) {
			return;
		}

		// begin to lexical analyze for each file
		for (int i = 0; i < testFilesLoader.lstFiles.size(); i++) {
			//create output file first
	    	SysLogger.setOutputFilenames(testFilesLoader.lstResultFiles.get(i), 
	    			testFilesLoader.lstErrFiles.get(i));
	    	
			// write time stamp to output files
			SimpleDateFormat tmpDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetimeNow = tmpDate.format(new java.util.Date());
			//SysLogger.info(datetimeNow + "\nThe following is the result:");
			
			// create a lexical analyzer
			LexicalAnalyzer scanner = new LexicalAnalyzer();
			
			if (scanner.init(testFilesLoader.lstFiles.get(i)) != 0) {
				return;
			}
			
			scanner.getAllTokens();
		}

		System.out.println("\nThe program ends successfully!");
		System.out.println("The result of the progrom has been stored in the files under $ThisProgram\\output\\ \n");
	}
}
