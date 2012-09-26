/*
 * COMP6791 Project
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

package tokenizer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.ArrayList;

import utils.SysLogger;

public class Tokenizer {
	public static final int ID_MAX_LEN = 255;
	public static final int TAB_LEN = 4;
	
	private String strFile = null;
	private BufferedReader in = null;
	private char curChar = 0;
	private int curPos = 0;
	private int curLine = 1;
	private int curLinePos = 0;
	private int lastLinePos = 0;		// the position of last line
	private String curToken = "";
	
	ArrayList<String> stopWords = new ArrayList<String>();
		
	public void consStopWordsList() {
		// here is the stop words list got from http://nlp.stanford.edu/IR-book/html/htmledition/dropping-common-terms-stop-words-1.html#fig:stoplist
		stopWords.add("a");
		stopWords.add("an");
		stopWords.add("and");
		stopWords.add("are");
		stopWords.add("as");
		stopWords.add("at");
		stopWords.add("be");
		stopWords.add("by");
		stopWords.add("for");
		stopWords.add("from");
		
		stopWords.add("has");
		stopWords.add("he");
		stopWords.add("in");
		stopWords.add("is");
		stopWords.add("it");
		stopWords.add("its");
		stopWords.add("of");
		stopWords.add("on");
		stopWords.add("that");
		stopWords.add("the");
		
		stopWords.add("to");
		stopWords.add("was");
		stopWords.add("were");
		stopWords.add("will");
		stopWords.add("with");
	}
	
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

		consStopWordsList();
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
		if (ret == '\t') {
			curPos += TAB_LEN;
			curLinePos += TAB_LEN;
		} else {
			curPos++;
			curLinePos++;
		}
		if (ret == '\r' || ret == '\n') {
			if (ret == '\n') {
				curLine++;
			}
			lastLinePos = curLinePos;
			curLinePos = 0;
		}
		
        return (char)ret;
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
	
	// get next valid token from the stream.
	public Token nextToken() {
		Token tk = new Token();
		boolean bExit = false;
		
		while (!bExit) {
			if (curChar == 0) {
				bExit = true;
				return null;		// exit;
			}
			
			if ((curChar >= 'a' && curChar <= 'z') || (curChar >= 'A' && curChar <= 'Z')) {
				if (curChar >= 'A' && curChar <= 'Z') {
					// lowercase the letter
					curChar = (char)(curChar + 32);
				}
				if (tk.type == 0 || tk.type == Token.TK_TYPE_STRING) {
					tk.type = Token.TK_TYPE_STRING;
					curToken += (char) curChar;
					curChar = nextChar();
					continue;
				}
				
				// it is a number, return the number
				break;
			}

			if ((curChar >= '0' && curChar <= '9')) {
				if (tk.type == 0 || tk.type == Token.TK_TYPE_NUM) {
					tk.type = Token.TK_TYPE_NUM;
					curToken += (char) curChar;
					curChar = nextChar();
					continue;
				}
				
				// return the string
				break;
			}
			
			// the current character is neither string or number
			if (tk.type == Token.TK_TYPE_STRING || tk.type == Token.TK_TYPE_NUM) {
				break;  // return the string or number
			}
			
			// then skip the character
			curChar = nextChar();
		}

		if (!curToken.isEmpty()) {
			if (curToken.length() > ID_MAX_LEN) {
				String err = "Line: " +  tk.line + ", Column: " + tk.column;
				SysLogger.err(err + ". Length of token > " + ID_MAX_LEN + ". " + tk.token);
				return null;
			}
			tk.token = curToken;
			tk.file = strFile;
			tk.line = curChar == '\n' ? curLine - 1 : curLine;
			tk.column = (curLinePos == 0 ? lastLinePos : curLinePos) - tk.token.length();
			curToken = "";
			
			return tk;
		}

		return null;
	}
	
	public void printToken(Token tk) {
		if (tk == null || (tk != null && tk.token == null)) {
			return;
		}
		
		String msg = String.format("Line: %4d,\tCol: %3d,\t%s", tk.line, tk.column, tk.token);
		SysLogger.info(msg);
		System.out.println(msg);
	}
	
	private boolean ifStopWord(String word) {
		for (int i = 0; i < stopWords.size(); i++) {
			if (stopWords.get(i).equals(word)) {
				return true;
			}
		}
		return false;
	}
	
	public int getAllTokens() {
		Token tk = nextToken();
		Stemmer s = new Stemmer();
		
		while (tk != null) {	
			printToken(tk);

			if (tk.type == Token.TK_TYPE_STRING) {
				// remove stop words
				if (!ifStopWord(tk.token)) {
					// stemmer the tokens with Porter Stemmer Algorithm
					s.add(tk.token.toCharArray(), tk.token.length());
					s.stem();
					tk.token = s.toString();
					
					printToken(tk);
				}
			}

			tk = nextToken();			
		}
		return 0;
	}
	
}
