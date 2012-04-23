/*
 * COMP6421 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: yua_t $
 * $Date: 2012-03-22 21:17:56 -0400 (Thu, 22 Mar 2012) $
 * $Rev: 22 $
 * $HeadURL: svn+ssh://yua_t@login.encs.concordia.ca/home/y/yua_t/svn_resp/comp6421/src/SemanticActions/SymbolTable.java $
 * 
 */

package SemanticActions;

import java.util.ArrayList;

public class SymbolTable implements Cloneable {
	// pointer to its parent symbol which created this symbol table;
	// null means the first table
	Symbol parent = null;			
	ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	
	String addrPrefix;		// prefix of all the symbol addresses of this table
	
    public Object clone(){
    	SymbolTable tk = null;
        try{
        	tk = (SymbolTable)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return tk;
    }
}
