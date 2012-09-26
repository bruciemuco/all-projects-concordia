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

public class Token implements Cloneable {
	public static final char TK_TYPE_STRING	 = 1;
	public static final char TK_TYPE_NUM	 = 2;
	
	public String file = null;		// file name
	public int line = 0;			// line number
	public int column = 0;			// the column of the first character
	public String token = null;		// 
	
	public char type = 0;		// 1 string, 2 number
}
