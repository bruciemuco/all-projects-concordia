/*
 * COMP6421 Project. 
 * Project description: http://newton.cs.concordia.ca/~paquet/wiki/index.php/COMP442/6421_winter_2012
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * Compiler Design (COMP 442/6421) Winter 2012 
 * Assignment 3, Semantic Analyzer
 *  
 * $Author: yua_t $
 * $Date: 2012-02-25 15:05:33 -0500 (Sat, 25 Feb 2012) $
 * $Rev: 18 $
 * $HeadURL: svn+ssh://yua_t@login.encs.concordia.ca/home/y/yua_t/svn_resp/comp6421/src/COMP6421Ass2Main.java $
 * 
 */


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import LexicalAnalyzer.InputLoader;
import LexicalAnalyzer.LexicalAnalyzer;
import LexicalAnalyzer.StateMachineDriver;
import SemanticActions.SymbolTable;
import SyntacticAnalyzer.FirstFollowSets;
import SyntacticAnalyzer.SyntacticAnalyzer;

import utils.SysLogger;


public class COMP6421Ass3Main {
	
	public static void main(String[] args) {
		// create and initialize the logger
		SysLogger.init();
		
		// show greetings
		System.out.println("Welcome to COMP 6421 Project. v0.1");
		System.out.println("Developed by Yuan Tao.\n");
		System.out.println("Please read $ThisProgram\\readme.pdf first.");
		System.out.println("Please put all the test files under the root directoy of $ThisProgram\\input\\, which already has some sample files.");
		System.out.println("The result of the progrom will be stored accordingly in the files under $ThisProgram\\output\\");
		System.out.println("More information about the program result please refer to $ThisProgram\\logs\\ \n");
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
			SymbolTable st = null;
			for (int j = 0; j < 2; j++) {
				if (j == 0) {
					SysLogger.enableLog(false);
				} else {
					SysLogger.enableLog(true);
					//create output file first
			    	SysLogger.setOutputFilenames(testFilesLoader.lstResultFiles.get(i), 
			    			testFilesLoader.lstErrFiles.get(i));
			    	
					// write time stamp to output files
					SimpleDateFormat tmpDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String datetimeNow = tmpDate.format(new java.util.Date());
					//SysLogger.info(datetimeNow + "\nThe following is the result:");
					String tabNote = "Note: If the length of TAB of your editor is not 4, please accordingly modify LexicalAnalyzer.java at line 26 and run again.\n";
					SysLogger.info(tabNote);
					SysLogger.err(tabNote);
					SysLogger.log("--------------------------------------------------");
					SysLogger.log("Start to analyze: " + testFilesLoader.lstFiles.get(i));
					SysLogger.log("--------------------------------------------------");
				}
				
				// create a lexical analyzer
				LexicalAnalyzer scanner = new LexicalAnalyzer();
				
				if (scanner.init(testFilesLoader.lstFiles.get(i)) != 0) {
					return;
				}
				
				// create a syntax analyzer
				SyntacticAnalyzer parser = new SyntacticAnalyzer();
				
				if (parser.init(scanner) != 0) {
					return;
				}
				
				if (parser.parseEx(st)) {
					st = (SymbolTable) parser.smActions.stHead.clone();
					
					//scanner.getAllTokens();
					parser.smActions.printAll();
				} else {
					break;
				}
			}
		}

		System.out.println("\nThe program ends successfully!");
		System.out.println("The result of the progrom has been stored in the files under $ThisProgram\\output\\ \n");


	}
}
