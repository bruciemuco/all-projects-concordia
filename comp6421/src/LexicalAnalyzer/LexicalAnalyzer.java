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

package LexicalAnalyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;

import utils.SysLogger;

public class LexicalAnalyzer {
	public static final int ID_MAX_LEN = 255;
	
	private String strFile = null;
	private BufferedReader in = null;
	private char curChar = 0;
	private int curPos = 0;
	private int curLine = 1;
	private int curLinePos = 0;
	private String curToken = null;
	private boolean isKeyword = false;
	
	public int init(String filename) {
		if (filename == null) {
			SysLogger.err("LexicalAnalyzer. No such file.");
			return -1;
		}
		
		// set the file to be analyzed.
		strFile = filename;
		
		// open the file to be ready to read
		if (openFile() == -1) {
			return -1;			
		}
		curChar = nextChar();
		
		return 0;
	}
	
	private int openFile() {        
		try {
			in = new BufferedReader(new FileReader(strFile));

		} catch (FileNotFoundException e) {
			SysLogger.err("File not found: " + strFile);
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return -1;
		}
		
		return 0;
	}
	
	private char nextChar() {
		int ret = -1;
		
		try {
			ret = in.read();
			
			if (ret == -1) {
				// end of file stream
				return 0;
			}
	    } catch (IOException e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return 0;
		}        
		curPos++;
		curLinePos++;
		if (ret == 10) {
			curLine++;
			curLinePos = 0;
		}
		
        return (char)ret;
	}
	
	private void addChar(char ch) {
		if (curToken == null) {
			curToken = "" + (char) curChar;
		} else {
			curToken += (char) curChar;
		}
	}
	
	private void dump() {
		SysLogger.log("--------------------");
		SysLogger.log("strFile = " + strFile);
		SysLogger.log("curLine = " + curLine + ", " + curLinePos + ", curPos = " + curPos);
		SysLogger.log("curToken = " + curToken);
		SysLogger.log("curChar = " + curChar + ", " + (int)curChar);
		SysLogger.log("--------------------\n");
	}
	
	private int fatalerrorHandler() {
		dump();
		return 0;
	}
	
	private int errorHandler() {
		// skip to next char
		SysLogger.err("Unknown character " + curChar + " (ASCII: " + (int)curChar 
				+ ") found at line: " +  curLine + ", column: " + curLinePos 
				+ ". Automatically skiped to next character.");
		curChar = nextChar();
		return 0;
	}
	
	private String checkTokenValidation(String token) {
		if (token == null) {
			return null;
		}
		
		char ch = token.charAt(0);
		if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
			if (token.length() > ID_MAX_LEN) {
				SysLogger.err("Warning: Identifier length > " + ID_MAX_LEN);
				return token;
			}
		}
		
		return token;
	}

	public String nextToken() {
		int curState = StateMachineDriver.INIT_STATE;
		String token = null;
		
		while (true) {
			if (curChar == 0) {
				return null;
			}
			
			//dump();
			SysLogger.log("curstate: " + curState + ", " + curChar + ", " + (int)curChar);
			curState = StateMachineDriver.nextState(curState, curChar);
			SysLogger.log("next state: " + curState);
			
			if (curState == StateMachineDriver.F) {
				addChar((char) curChar);
				curChar = nextChar();
				token = curToken;
				curToken = null;
				break;
			}
			if (curState == StateMachineDriver.B) {
				token = curToken;
				curToken = null;
				break;
			}
			if (curState == StateMachineDriver.E) {
				SysLogger.err("Fatal error. State machine goes to wrong state. ");				
				fatalerrorHandler();
				token = null;
				break;
			}
			if (curState == StateMachineDriver.NE) {
				errorHandler();
				curState = StateMachineDriver.INIT_STATE;
				continue;
			}
			if (curState != StateMachineDriver.INIT_STATE) {
				addChar((char) curChar);
			}
			curChar = nextChar();
		}
		
		if (checkTokenValidation(token) == null) {
			return null;
		}
		
		return token;
	}
	
	public int getAllTokens() {
		String token = nextToken();
		
		while (token != null) {
			SysLogger.info(token);
			//System.out.println(token);
			token = nextToken();
		}
		return 0;
	}
	
}
