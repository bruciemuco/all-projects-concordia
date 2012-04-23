/*
 * COMP6421 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: yua_t $
 * $Date: 2012-04-07 18:45:07 -0400 (Sat, 07 Apr 2012) $
 * $Rev: 35 $
 * $HeadURL: svn+ssh://yua_t@login.encs.concordia.ca/home/y/yua_t/svn_resp/comp6421/src/SemanticActions/Symbol.java $
 * 
 */

package SemanticActions;

import java.util.ArrayList;
import LexicalAnalyzer.Token;

public class Symbol implements Cloneable {
	public static enum SYMBOLTYPE {
		UNKNOWN, CLASS, FUNCTION, VARIABLE, PARAMETER, 
		UNKNOWN_EXITTABLE, ARRAYSIZE, CHKVAR, CHKTYPE, CHKMEMBER,
		NUMBER};
	
	public SYMBOLTYPE symbolType;
	public Token tk;
	public Token dataType;
	public int size;				// size of the symbol
	public String address;			// unique name
	//public String addrTmp;			// temporary address used by expression
	
	public boolean ifAlreadyDefined = false;
	public boolean ifUnkownDataType = false;

	public SymbolTable self = null;		// pointer to the symbol table it is placed;

	// class or function
	public SymbolTable child = null;		// pointer to its symbol table;
	
	// array
	public boolean isArray = false;
	public int dimensions;		// the number of dimensions
	public ArrayList<Integer> sizeOfDimension = new ArrayList<Integer>();
	
	// class name
	public String className;
	public boolean ifPassedByAddress = false;
	
    public Object clone(){
    	Symbol tk = null;
        try{
        	tk = (Symbol)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return tk;
    }
}
