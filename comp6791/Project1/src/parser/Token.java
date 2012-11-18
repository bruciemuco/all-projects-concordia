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

public class Token {
	public static final int TK_TYPE_STRING	 = 1;
	public static final int TK_TYPE_NUM	 = 2;
	public static final int TK_TYPE_XML_LEFT_TAG = 3;
	public static final int TK_TYPE_XML_RIGHT_TAG = 4;
	
	public int type = 0;			// 1 string, 2 number
	
	public String token = null;		//
	public int pos = -1;			// position in the doc
}
