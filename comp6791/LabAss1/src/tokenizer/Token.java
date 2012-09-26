/*
 * COMP6791 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: yua_t $
 * $Date: 2012-02-23 16:31:48 -0500 (Thu, 23 Feb 2012) $
 * $Rev: 12 $
 * $HeadURL: svn+ssh://yua_t@login.encs.concordia.ca/home/y/yua_t/svn_resp/comp6421/src/LexicalAnalyzer/Token.java $
 * 
 */

package tokenizer;

public class Token implements Cloneable {
	public static final char TK_TYPE_STRING	 = 1;
	public static final char TK_TYPE_NUM	 = 2;
	
	public String file = null;		// file name
	public int line = 0;			// line number
	public int column = 0;			// the column of the first character
	public String token = null;		// 
	
	public char type = 0;		// 1 string, 2 number
}
