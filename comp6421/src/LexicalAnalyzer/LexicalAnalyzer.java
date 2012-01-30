/*
 * COMP6421 Project
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
	private int lastLinePos = 0;		// the position of last line
	private String curToken = null;
	
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
		if (ret == '\r' || ret == '\n') {
			if (ret == '\n') {
				curLine++;
			}
			lastLinePos = curLinePos;
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
	
	private int checkTokenValidation(Token tk) {
		if (tk.token == null) {
			return -1;
		}
		
		if (tk.type == StateMachineDriver.TOKEN_TYPE_ID) {
			if (tk.token.length() > ID_MAX_LEN) {
				String err = "Line: " +  tk.line + ", column: " + tk.column;
				SysLogger.err(err + ". Identifier length > " + ID_MAX_LEN);
				return -1;
			}
			
			// check the type
			if (StateMachineDriver.ifKeyword(tk.token)) {
				tk.type = StateMachineDriver.TOKEN_TYPE_KEYWORD;
			} else if (StateMachineDriver.ifPunctuation(tk.token)) {
				tk.type = StateMachineDriver.TOKEN_TYPE_PUNCTUATION;
			}
		}
		
		if (tk.type == StateMachineDriver.TOKEN_TYPE_INT) {
			try {
				Integer.parseInt(tk.token);
			} catch (NumberFormatException e) {				
				e.printStackTrace();
				String err = "Line: " +  tk.line + ", column: " + tk.column;
				SysLogger.err(err + ". Number is too big");
				return -1;
			}
		}
		
		if (tk.type == StateMachineDriver.TOKEN_TYPE_FLOAT) {
			try {
				Float.parseFloat(tk.token);
			} catch (NumberFormatException e) {				
				e.printStackTrace();
				String err = "Line: " +  tk.line + ", column: " + tk.column;
				SysLogger.err(err + ". Float is too big");
				return -1;
			}
		}
		
		
		return 0;
	}

	// get next valid token from the stream.
	public Token nextToken() {
		int curState = StateMachineDriver.INIT_STATE;
		Token tk = new Token();
		
		tk.error = false;
		while (true) {
			if (curChar == 0) {
				return null;
			}
			
			SysLogger.log("Line(" + curLine + "," + curLinePos + ")curstate: " + curState 
					+ ", " + curChar + ", " + (int)curChar);
			curState = StateMachineDriver.nextState(curState, curChar, tk);
			SysLogger.log("next state: " + curState);

			// final state without backing up
			if (curState == StateMachineDriver.N) {
				addChar((char) curChar);
				curChar = nextChar();
				break;
			}
			// back up
			if (curState == StateMachineDriver.B) {
				break;
			}

			// error
			if (curState == StateMachineDriver.E) {
				SysLogger.err("Fatal error. State machine goes to wrong state. ");				
				fatalerrorHandler();
				tk = null;
				return tk;
			}
			// find an invalid character
			if (curState == StateMachineDriver.EC) {
				String err = "Line: " +  curLine + ", column: " + curLinePos;
				
				SysLogger.err(err + ". Unknown character " + curChar + " (ASCII: " + (int)curChar + ").");
				
				// skip to next char
				curChar = nextChar();
				
				// still output the part of token has been analyzed
				if (curToken != null) {
					tk.error = true;
					break;
				}
				
				curState = StateMachineDriver.INIT_STATE;
				continue;
			}
			// find a valid but unexpected character
			if (curState == StateMachineDriver.ES) {
				String err = "Line: " +  curLine + ", column: " + curLinePos;
				
				SysLogger.err(err + ". Unexpected character " + curChar + " (ASCII: " 
					+ (int)curChar + "), when analyzing " + curToken);
				
				curState = StateMachineDriver.INIT_STATE;
				continue;
			}
			
			if (curState != StateMachineDriver.INIT_STATE) {
				addChar((char) curChar);
			}
			curChar = nextChar();
		}

		tk.token = curToken;
		curToken = null;
		tk.file = strFile;
		tk.line = curChar == '\n' ? curLine - 1 : curLine;
		tk.column = (curLinePos == 0 ? lastLinePos : curLinePos) - tk.token.length();

		if (tk.error) {
			tk.column--;		// nextChar was called in errorHandler()			
		}

		if (checkTokenValidation(tk) == -1) {
			return null;
		}		
		
		return tk;
	}
	
	public int getAllTokens() {
		Token tk = nextToken();
		String msg = null;
		
		while (tk != null) {	
			msg = "Line: " + tk.line + ", Column: " + tk.column + ", Type: " 
					+ StateMachineDriver.TOKEN_STR_TYPE[tk.type];
			if (tk.error) {
				msg += ", By Error Recovery";
			}
			SysLogger.info(msg + ", Lexeme/Value: " + tk.token);
			//System.out.println(token);
			tk = nextToken();
		}
		return 0;
	}
	
}
