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

package parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import retrieval.InfoRetrieval;

import utils.SysLogger;

public class Tokenizer {
	public static final int ID_MAX_LEN = 255;
	public static final int TAB_LEN = 4;
	public static final int FILEBUF_SIZE = 16384; //16 * 1024;
	
	public static final int MAX_FILES = 50;
	public static final int MAX_FILE_SIZE = 8*1024*1024;	// 8M bytes
	
	private ArrayList<String> lstFiles = new ArrayList<String>();
	private int lstFilesIndex = 0;

	private BufferedInputStream in = null;
	private byte[] fileBuf = new byte[FILEBUF_SIZE];
	private int curIndex = FILEBUF_SIZE;
	private int maxIndex = 0;
	
	private char curChar = 0;
	private int curPos = 0;
	private String curToken = "";
	
	public long curDocID = 0;
	
	// stopwords
	StopWords stopWords = new StopWords();
	
	public int init(String dir) {
		if (dir == null) {
			SysLogger.err("LexicalAnalyzer. No such file.");
			return -1;
		}
		
		// get all input file names
		if (loadFilenames(dir) != 0) {
			return -1;
		}
		
		// open the first file to be read
		if (openNextFile() != 0) {
			return -1;
		}
		
		curChar = nextChar();

		return 0;
	}
	
	// load all file names under a directory
	private int loadFilenames(String inputDir) {
        File filesDir = new File(inputDir);        
        File list[] = filesDir.listFiles();
        
        if (list.length > MAX_FILES) {
			SysLogger.err("Too many files under directory: " + inputDir);
			return -1;
		}
        
        for(int i=0;i<list.length;i++)
        {
            if(list[i].isFile())
            {
            	if (list[i].length() > MAX_FILE_SIZE) {
            		SysLogger.err("File " + list[i].getName() + " is too large: " 
            			+ list[i].length());
            		return -1;
				}
            	
            	lstFiles.add(inputDir + list[i].getName());
            }
        }
        
        if (lstFiles.size() < 1) {
			SysLogger.err("No input files");
			return -1;
		}
		return 0;
	}
	
	private int openNextFile() {
		if (lstFilesIndex == lstFiles.size()) {
			InfoRetrieval.arrDocID2File.add(curDocID + "," + lstFiles.get(lstFilesIndex - 1));
			return 1;
		}

		try {
			in = new BufferedInputStream(new FileInputStream(lstFiles.get(lstFilesIndex)));
				
		} catch (FileNotFoundException e) {
			SysLogger.err("File not found: " + lstFiles.get(lstFilesIndex));
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return -1;
		}

		if (lstFilesIndex > 0) {
			InfoRetrieval.arrDocID2File.add(curDocID + "," + lstFiles.get(lstFilesIndex - 1));
		}
		lstFilesIndex++;
		return 0;
	}
	
	private char nextChar() {
		int nRead = -1;
				
		try {
			if (curIndex == FILEBUF_SIZE || curIndex == maxIndex) {
				//read the next buffer from the file
				nRead = in.read(fileBuf, 0, FILEBUF_SIZE);
				if (nRead == -1) {
					// end of file stream, open next file
					in.close();
					if (openNextFile() != 0) {
						return 0;
					}
					nRead = in.read(fileBuf, 0, FILEBUF_SIZE);
				}
				
				if (nRead == 0) {
					// blocked...
					SysLogger.err("nextChar is blocked.");
					in.close();
					return 0;
				}

				curIndex = 0;
				maxIndex = nRead;
			}			

		} catch (IOException e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return 0;
		}
		
		curPos++;
		
		return (char) fileBuf[curIndex++];
	}
	
	// get next valid token from the stream.
	public Token nextToken() {
		Token tk = new Token();
		
		while (curChar != 0) {
			if ((curChar >= 'a' && curChar <= 'z') || (curChar >= 'A' && curChar <= 'Z')) {
				if (curChar >= 'A' && curChar <= 'Z') {
					// lowercase the letter
					if (IndexConstructor.OP_CASEFOLDING) {
						curChar = (char)(curChar + 32);
					}
				}
				
				if (tk.type == 0 || tk.type == Token.TK_TYPE_STRING) {
					tk.type = Token.TK_TYPE_STRING;
					curToken += (char) curChar;
					curChar = nextChar();
					continue;
				} else if (tk.type == Token.TK_TYPE_XML_LEFT_TAG) {
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
				} else if (tk.type == Token.TK_TYPE_XML_LEFT_TAG) {
					curToken += (char) curChar;
					curChar = nextChar();
					continue;
				}
				
				// return the string
				break;
			}
			
			if (curChar == '<') {
				if (tk.type == Token.TK_TYPE_STRING || tk.type == Token.TK_TYPE_NUM) {
					break;
				}
				
				tk.type = Token.TK_TYPE_XML_LEFT_TAG; 	// may not be a XML tag
				curToken += (char) curChar;
				curChar = nextChar();
				
				// <!DOCTYPE or </PLACES>
				if (curChar == '!' || curChar == '/') {  
					curToken += (char) curChar;
					curChar = nextChar();
				
				} else if (!((curChar >= 'a' && curChar <= 'z') 
						|| (curChar >= 'A' && curChar <= 'Z')
						|| (curChar >= '0' && curChar <= '9'))) {
					// skip '<'
					tk.type = 0;
					curToken = "";
					curChar = nextChar();
				}
				continue;
			}
			if (curChar == '>') {
				if (tk.type != Token.TK_TYPE_XML_LEFT_TAG) {
					if (tk.type == 0) {
						curChar = nextChar();
						continue;
					}
					break;	// return the token
				}
				
//				tk.type = Token.TK_TYPE_XML_RIGHT_TAG;
//				curToken += (char) curChar;
//				break;	 // return the XML tag
				// skip this token
				tk.type = 0;
				curToken = "";
				curChar = nextChar();
				continue;
			}
			
			if (tk.type != 0) {
				// the current character is neither string or number
				break;  // return the string or number
			}
			
			curChar = nextChar();
		}

		if (curToken.isEmpty()) {
			return null;
		}

		// disable the following checking just to improve performance
//		if (curToken.length() > ID_MAX_LEN) {
//			SysLogger.err(". Length of token > " + ID_MAX_LEN + ". " + tk.token);
//			return null;
//		}
		if (tk.type == Token.TK_TYPE_XML_LEFT_TAG) {
			if (handleXMLTags(curToken) == 0) {
				curToken = "";
				return nextToken();
			} else {
				// it is a term
				curToken = curToken.substring(1);
			}
		}
		tk.token = curToken;
		curToken = "";
		
		return tk;
	}
	
	public static final String XML_DOCTYPE = "<!DOCTYPE";
	public static final String XML_REUTERS = "<REUTERS";
	
	private int handleXMLTags(String tk) {
		if (tk.equalsIgnoreCase(XML_DOCTYPE)) {
			// skip the afterwards characters, until it is '>'
			while (curChar != '>') {
				curChar = nextChar();
			}
			curChar = nextChar();
			
		} else if (tk.equalsIgnoreCase(XML_REUTERS)) {
			// get the value of newid
			String ele = "";
			while (curChar != '>') {
				ele += curChar;
				curChar = nextChar();
			}
			curChar = nextChar();
			
			// parse string: TOPICS="YES" LEWISSPLIT="TRAIN" CGISPLIT="TRAINING-SET" OLDID="8914" NEWID="4001"
			String[] temp;
			temp = ele.split(" ");
			temp = temp[temp.length - 1].split("=");
			
			String docID = temp[1].substring(1, temp[1].length() - 1);
			curDocID = Long.parseLong(docID);
			
			//SysLogger.info("get a docID: " + curDocID);
		} else {
			return -1;
		}
		
		return 0;
	}
	
	
	// --------------------- unit test ------------------------
	
	public void printToken(Token tk) {
		if (tk == null || (tk != null && tk.token == null)) {
			return;
		}
		
		SysLogger.info(tk.token);
	}
	
	// 
	public int getAllTokens() {
		Token tk = nextToken();
	
		while (tk != null) {	
			printToken(tk);

			tk = nextToken();
		}
		return 0;
	}
	
}
