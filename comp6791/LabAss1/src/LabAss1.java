import java.io.IOException;
import java.text.SimpleDateFormat;

import parser.Tokenizer;
 
import utils.SysLogger;


/*
 * COMP6791 Lab Assignment 1
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
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
	
		String path = System.getProperty("user.dir") + "\\input\\";;
		
		// create a Tokenizer
		Tokenizer scanner = new Tokenizer();
		
		if (scanner.init(path) != 0) {
			return;
		}
		
		scanner.getAllTokens();

		System.out.println("\nThe program ends successfully!");
	}

}
