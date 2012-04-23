/*
 * COMP6421 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: yua_t $
 * $Date: 2012-04-09 02:50:57 -0400 (Mon, 09 Apr 2012) $
 * $Rev: 45 $
 * $HeadURL: svn+ssh://yua_t@login.encs.concordia.ca/home/y/yua_t/svn_resp/comp6421/src/SemanticActions/SemanticActions.java $
 * 
 */

package SemanticActions;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Random;

import utils.SysLogger;
import LexicalAnalyzer.Token;
import SemanticActions.Symbol.SYMBOLTYPE;

public class SemanticActions {
	public SymbolTable stHead = null;		// head of symbol table list;
	public SymbolTable stBak = null;		// only used to check global function declarations 
	private SymbolTable stCur = null;		// current symbol table;
	
	// ASM
	StringBuffer secData = new StringBuffer();		// to store data of the ASM code
	StringBuffer secCode = new StringBuffer();		// to store instructions of ASM code 
	
	// symbol table manipulation functions
	SymbolTable create(Symbol curSymbol) {
		SymbolTable st = new SymbolTable();
		st.parent = curSymbol;
		if (curSymbol == null) {
			st.addrPrefix = "G";
		} else {
			st.addrPrefix = curSymbol.self.addrPrefix + "_" + curSymbol.tk.token;
		}
		return st;
	}

	// obsolete, see the action functions defined below 
	boolean search(SymbolTable st, String i, Symbol s) {
		return true;
	}
	// obsolete, see the action functions defined below 
	int insert(SymbolTable st, String i, Symbol s) {
		return 0;
	}
	// obsolete, see the action functions defined below 
	void delete(SymbolTable st) {
		// do nothing;
	}

	// print out information 
	void printSymbol(Symbol s) {
		if (s == null) {
			return;
		}
		String str = "Token: " + s.tk.token + "\nSymbol Type: " + s.symbolType.toString() +
			"\nDataType: " + s.dataType.token + "\nAddress: " + s.address + "\nSize: " + s.size + "\n"; 
		if (s.isArray) {
			str += "Dimensions: " + s.dimensions + "\nSizeOfDimensions: ";
			for (int i = 0; i < s.sizeOfDimension.size(); i++) {
				str += "[" + s.sizeOfDimension.get(i) + "]";
			}
			str += "\n";
		}
		if (s.ifAlreadyDefined) {
			str += "ifAlreadyDefined: true\n";
		}
		if (s.ifUnkownDataType) {
			str += "ifUnknownType: true\n";
		}
		 
		SysLogger.info(str);
	}
	void print(SymbolTable st) {
		if (st == null) {
			return;
		}
		String str = "--Begin Symbol Table -- Parent Symbol: ";
		if (st.parent != null && st.parent.tk != null) {
			str += st.parent.dataType.token + " " + st.parent.tk.token;
		} else {
			str += "null";
		}
		SysLogger.info(str);
		
		for (int i = 0; i < st.symbols.size(); i++) {
			Symbol s = st.symbols.get(i);
			printSymbol(s);
		}
		str = "--End--\n";
		SysLogger.info(str);
		
		for (int i = 0; i < st.symbols.size(); i++) {
			Symbol s = st.symbols.get(i);
			if (s.child != null) {
				print(s.child);
			}
		}
	}
	public void printAll() {
		print(stHead);
	}
	
	private void printErr(Symbol s, String msg) {
		String str = String.format("Semantic error   at line: %4d, col: %4d, Token:%16s, %s", 
				s.tk.line, s.tk.column, s.tk.token, msg);
		SysLogger.err(str);	
	}
	private void printErr(Symbol s, String token, String msg) {
		String str = String.format("Semantic error   at line: %4d, col: %4d, Token:%16s, %s", 
				s.tk.line, s.tk.column, token, msg);
		SysLogger.err(str);	
	}
	private void printWarn(Symbol s, String msg) {
		String str = String.format("Semantic warning at line: %4d, col: %4d, Token:%16s, %s", 
				s.tk.line, s.tk.column, s.tk.token, msg);
		SysLogger.err(str);	
	}
	private void printLog(Symbol s, String msg) {
		String str = String.format("Semantic error   at line: %4d, col: %4d, Token:%16s, %s", 
				s.tk.line, s.tk.column, s.tk.token, msg);
		SysLogger.log(str);	
	}
	

	// actions
	
	// create the first symbol table
	public int newProg() {
		stHead = create(null);
		stCur = stHead;
		return 0;
	}
	
	// check if the ID has been defined
	public boolean ifReDefined(Symbol s) {
		if (s.symbolType == SYMBOLTYPE.CLASS) {
			// within the same scope, if there is another class with same name
			for (int i = 0; i < s.self.symbols.size(); i++) {
				Symbol tmp = s.self.symbols.get(i);
				if (tmp.symbolType == SYMBOLTYPE.CLASS && tmp.tk.token.equals(s.tk.token)) {
					return true;
				}
			}
		} else if (s.symbolType == SYMBOLTYPE.VARIABLE) {
			// within the same scope, if there is another variable with same name
			for (int i = 0; i < s.self.symbols.size(); i++) {
				Symbol tmp = s.self.symbols.get(i);
				if (tmp.symbolType == SYMBOLTYPE.VARIABLE && tmp.tk.token.equals(s.tk.token)) {
					return true;
				}
			}
		} 
		
		return false;
	}
	
	// add a new class
	public int newClass(Symbol s) {
		SysLogger.log("newClass: " + s.tk.token);
		s.symbolType = SYMBOLTYPE.CLASS;
		s.self = stCur;
		s.address = s.self.addrPrefix + "_C_" + s.tk.token + "_" + s.tk.line;
		
		// check if the identification has been defined before adding to list
		if (ifReDefined(s)) {
			printErr(s, "Class redefinition.");
			//printErr(s, "Multiply declared identifier.");
			//return -1;
			s.ifAlreadyDefined = true;		// still add into the list
		}
		s.child = create(s);
		stCur.symbols.add(s);		
		stCur = s.child;
		
		asmData(s.address, "dw", "0", "", "");
		return 0;
	}
	
	public int setClassSize(Symbol s) {
		for (int i = 0; i < stCur.symbols.size(); i++) {
			Symbol tmp = stCur.symbols.get(i);
			if (tmp.symbolType == SYMBOLTYPE.VARIABLE) {
				if (tmp.isArray) {
					s.size += getArrayTotalSize(tmp);
				} else {
					s.size += tmp.size;
				}
			}
		}
		return 0;
	}

	private int getArrayTotalSize(Symbol s) {
		int arrSize = 1;
		
		for (int j = 0; j < s.sizeOfDimension.size(); j++) {
			arrSize *= s.sizeOfDimension.get(j);
		}
		return arrSize * s.size;
	}
	
	// exit a class or function definition
	public int exitCurSymbolTable() {
		if (stCur.parent == null || stCur.parent.self == null) {
			SysLogger.err("Cannot exit a symbol table whose parent is null.");
			return -1;
		}
		SysLogger.log("exitCurSymbolTable: " + stCur.parent.tk.token);
		stCur = stCur.parent.self;
		return 0;
	}
	
	// check if the data type is defined
	private boolean ifDataTypeDefined(Token tk) {
		if (stBak == null) {
			return false;
		}
		if (tk.token.equals("real") || tk.token.equals("integer")) {
			return true;
		}
		for (int i = 0; i < stBak.symbols.size(); i++) {
			Symbol tmp = stBak.symbols.get(i);
			if (tmp.symbolType == SYMBOLTYPE.CLASS && tmp.tk.token.equals(tk.token)) {
				return true;
			}
		}
		return false;
	}
	public boolean ifDataTypeDefined(Symbol s) {
		if (ifDataTypeDefined(s.tk)) {
			return true;
		}
		printErr(s, "Undeclared type.");
		return false;
	}
	
	// add a new function
	public int newFunction(Symbol s) {
		SysLogger.log("newFunction: " + s.tk.token);
		s.symbolType = SYMBOLTYPE.FUNCTION;
		s.self = stCur;
		s.address = s.self.addrPrefix + "_F_" + s.tk.token + "_" + s.tk.line;
		s.child = create(s);
		
		if (!ifDataTypeDefined(s.dataType)) {
			s.ifUnkownDataType = true;
		}
		stCur.symbols.add(s);
		stCur = s.child;
		if (!s.ifAlreadyDefined && !s.ifUnkownDataType) {
			asmFuncDefinition(s);
		}
		s.size = 4;
		return 0;
	}
	
	// check if the function is redefined
	// s is the last parameter of this function or the function identification
	public boolean ifFuncRedefined(Symbol s) {
		boolean ret = false;		// no redefinition
		
		// within the parent scope, if there is another function with same name, type and parameters
		if (s.symbolType == SYMBOLTYPE.PARAMETER) {
			Symbol sp = s.self.parent;		// get this function symbol
			
			// same name and type
			for (int i = 0; i < sp.self.symbols.size() - 1; i++) {
				if (ret) {
					break;
				}
				
				Symbol tmp = sp.self.symbols.get(i);
				if (tmp.symbolType == SYMBOLTYPE.FUNCTION && tmp.tk.token.equals(sp.tk.token)
						&& tmp.dataType.token.equals(sp.dataType.token)) {
					
					// check all parameters
					int paramCnt = tmp.child.symbols.size();
					ret = true;
					for (int j = 0; j < paramCnt; j++) {
						Symbol p = tmp.child.symbols.get(j);
						
						if (p.symbolType == SYMBOLTYPE.PARAMETER) {							
							if (sp.child.symbols.size() > j) {
								if (sp.child.symbols.get(j).symbolType != SYMBOLTYPE.PARAMETER) {
									ret = false;
									break;
								}
								if (!sp.child.symbols.get(j).dataType.token.equals(p.dataType.token)) {
									ret = false;
									break;
								}
							} else {
								ret = false;
								break;
							}
						} else {
							// the other function has at least one more parameter
							if (sp.child.symbols.size() > j) {
								if (sp.child.symbols.get(j).symbolType == SYMBOLTYPE.PARAMETER) {
									ret = false;
								}
							}
							break;
						}
					}
					// the other function has at least one more parameter
					if (sp.child.symbols.size() > paramCnt) {
						if (sp.child.symbols.get(paramCnt).symbolType == SYMBOLTYPE.PARAMETER) {
							ret = false;
						}
					}
				}
			}
		} else if (s.symbolType == SYMBOLTYPE.FUNCTION) {
			for (int i = 0; i < s.self.symbols.size() - 1; i++) {
				Symbol tmp = s.self.symbols.get(i);
				
				if (tmp.symbolType == SYMBOLTYPE.FUNCTION && tmp.tk.token.equals(s.tk.token) 
						&& tmp.dataType.token.equals(s.dataType.token)) {
					ret = true;
					break;
				}
			}
			
		} else {
			printErr(s, "Action error while checking function redifinition.");
		}
		
		if (ret) {
			String token = "";
			
			if (s.symbolType == SYMBOLTYPE.PARAMETER) {
				token = s.self.parent.tk.token;
				s.self.parent.ifAlreadyDefined = true;
			} else {
				token = s.tk.token;
				s.ifAlreadyDefined = true;
			}
			printErr(s, token, "Function redefinition.");
			//printErr(s, token, "Multiply declared identifier.");
		}
		asmPopFunctionParams(s);
		return ret;
	}
	
	// add a new variable for the function
	public int newVarible(Symbol s) {
		SysLogger.log("addVar: " + s.tk.token);
		if (s.symbolType != SYMBOLTYPE.PARAMETER) {
			s.symbolType = SYMBOLTYPE.VARIABLE;
		}
		s.self = stCur;
		s.address = s.self.addrPrefix + "_V_" + s.tk.token + "_" + s.tk.line;
		
		// check if the identification has been defined before adding to list
		if (ifReDefined(s)) {
			printErr(s, "Variable redefinition.");
			//printErr(s, "Multiply declared identifier.");
			s.ifAlreadyDefined = true;
		}
		if (!ifDataTypeDefined(s.dataType)) {
			s.ifUnkownDataType = true;
		}
		
		// set the size
		if (s.dataType.token.equals("integer") || s.dataType.token.equals("real")) {
			s.size = 4;
		} else {
			boolean findit = false;
			for (int i = 0; i < stHead.symbols.size(); i++) {
				Symbol tmp = stHead.symbols.get(i);
				if (tmp.symbolType == SYMBOLTYPE.CLASS && tmp.tk.token.equals(s.dataType.token)) {
					s.size = tmp.size;
					findit = true;
					break;
				}
			}
			if (!findit && stBak != null) {
				for (int i = 0; i < stBak.symbols.size(); i++) {
					Symbol tmp = stBak.symbols.get(i);
					if (tmp.symbolType == SYMBOLTYPE.CLASS && tmp.tk.token.equals(s.dataType.token)) {
						s.size = tmp.size;
						break;
					}
				}
			}
		}
	
		// generate ASM code
		if (!s.ifAlreadyDefined && !s.ifUnkownDataType) {
			asmVarDefinition(s);
		}
		
		stCur.symbols.add(s);
		return 0;
	}

	// check if the variable has been defined
	// get the data type of s as well, if it is a function, wait until all the 
	// parameters of the function has been parsed.
	public boolean ifVarDefined(Symbol s) {
		// first, check within local scope
		for (int i = 0; i < stCur.symbols.size(); i++) {
			Symbol tmp = stCur.symbols.get(i);
			if (tmp.symbolType == SYMBOLTYPE.VARIABLE && tmp.tk.token.equals(s.tk.token)) {
				copySymbolInfo(s, tmp);				
				return true;
			}
		}
		// if it is parameter
		for (int i = 0; i < stCur.symbols.size(); i++) {
			Symbol tmp = stCur.symbols.get(i);
			if (tmp.symbolType == SYMBOLTYPE.PARAMETER && tmp.tk.token.equals(s.tk.token)) {
				copySymbolInfo(s, tmp);
				return true;
			}
		}
		
		// then, if parent is a member function, including global variables
		if (stCur.parent != null) {
			if (stCur.parent.symbolType != SYMBOLTYPE.FUNCTION) {
				printErr(s, "Action error while checking variable definition.");
				return false;
			}
			// if it is a member of class (function or variable which is previous defined)
			for (int i = 0; i < stCur.parent.self.symbols.size(); i++) {		// allow the function itself
				Symbol tmp = stCur.parent.self.symbols.get(i);
				if (tmp.symbolType == SYMBOLTYPE.VARIABLE && tmp.tk.token.equals(s.tk.token)) {
					// s might be a function.
					copySymbolInfo(s, tmp);
					//s.dataType = (Token) tmp.dataType.clone();		
					s.self = stCur.parent.self;
					return true;
				}
				if (tmp.symbolType == SYMBOLTYPE.FUNCTION && tmp.tk.token.equals(s.tk.token)) {
					// check all the parameters later
					s.dataType = (Token) tmp.dataType.clone();
					s.self = stCur.parent.self;
					//varFuncParams.add(s);
					return true;
				}
			}
		}

		// if it is a global function
		if (stBak == null) {
			return false;
		}
		for (int i = 0; i < stBak.symbols.size(); i++) {
			Symbol tmp = stBak.symbols.get(i);
			if (tmp.symbolType == SYMBOLTYPE.FUNCTION && tmp.tk.token.equals(s.tk.token)) {
				// check all the parameters later
				s.dataType = (Token) tmp.dataType.clone();				
				//varFuncParams.add(s);
				return true;
			}
		}

		// class member function defined after this function 
		SymbolTable curParentSelf = null; 
		for (int i = 0; i < stBak.symbols.size(); i++) {
			Symbol t = stBak.symbols.get(i);
			if (stCur.parent.self.parent != null 
					&& t.address.equals(stCur.parent.self.parent.address)) {
				curParentSelf = t.child;
				break;
			}
		}
		if (curParentSelf != null) {
			for (int i = 0; i < curParentSelf.symbols.size(); i++) {
				Symbol tmp = curParentSelf.symbols.get(i);
				if (tmp.symbolType == SYMBOLTYPE.FUNCTION && tmp.tk.token.equals(s.tk.token)) {
					// check all the parameters later
					s.dataType = (Token) tmp.dataType.clone();
					s.self = curParentSelf;
					return true;
				}
			}
		}

		printErr(s, "Undeclared identifier.");
		return false;
	}

	private void copySymbolInfo(Symbol d, Symbol s) {
		d.dataType = (Token) s.dataType.clone();
		d.address = s.address;
		d.size = s.size;
		d.dimensions = s.dimensions;
		d.isArray = s.isArray;
		d.sizeOfDimension = s.sizeOfDimension;
		d.symbolType = s.symbolType;
	}
	
	// check if the token is a class member
	public boolean ifClassMember(Symbol s, Symbol m) {
		if (stBak == null) {
			return false;
		}
		//System.out.println("--" + s.dataType.token + ", " + m.tk.token);
		// first, if its type is a class
		for (int i = 0; i < stBak.symbols.size(); i++) {
			Symbol tmp = stBak.symbols.get(i);
			
			if (tmp.symbolType == SYMBOLTYPE.CLASS && tmp.tk.token.equals(s.dataType.token)) {
				// check if it is a member
				int offset = 0;
				
				for (int j = 0; j < tmp.child.symbols.size(); j++) {
					Symbol p = tmp.child.symbols.get(j);
					
					// s: the instance of the class
					// p: the member of the class
					// m: current symbol
					// tmp: the class symbol
					if (p.tk.token.equals(m.tk.token)) {
						if (m.address == null) {
							m.address = s.address;			// for ASM code
						}
						m.dataType = (Token) p.dataType.clone();
						m.size = p.size;
						m.dimensions = p.dimensions;
						m.isArray = p.isArray;
						m.sizeOfDimension = p.sizeOfDimension;

						m.className = tmp.tk.token;		// used to check the function params later.
						asmClassMemberOffset(offset);
						
						// parameter with class type
						if (s.symbolType == SYMBOLTYPE.PARAMETER) {
							m.ifPassedByAddress = true;
						}
						return true;
					}
					if (p.symbolType == SYMBOLTYPE.VARIABLE) {
						if (p.isArray) {
							offset += getArrayTotalSize(p);
						} else {
							offset += p.size;
						}
					}
				}
				printErr(s, "'" + m.tk.token + "' is not a member of '" + tmp.tk.token + "'");
				return false;
			}
		}
		printErr(m, "left of '." + m.tk.token + "' must have class type");
		return false;
	}

	// using a array
	private ArrayList<Symbol> arrIndexList = new ArrayList<Symbol>();
	
	// check if the type of express is a valid type for the index of array
	public boolean ifValidIndexType(Symbol s) {
		if (!s.dataType.token.equals("integer")) {
			printErr(s, "Invalid array index type: " + s.dataType.token);
			return false;
		}
		arrIndexList.add(s);
		return true;
	}
	
	// get the element address of the array
	public int calcArrayAddr(Symbol s) {
		int dimensions = arrIndexList.size();
		
		// check the number of dimensions
		if (s.dimensions != dimensions) {
			printErr(s, "Invalid array dimensions: " + dimensions);
			arrIndexList.clear();
			return -1;
		}
		
		// calculate the element address
		if (dimensions == 0) {
			// 
		} else {
			//asmCode("", "add", "r11", "r11", "r0", "% calc array index");
			for (int i = 0; i < arrIndexList.size(); i++) {
				int size = 1;		// total size of sub dimensions
				
				for (int j = i + 1; j < s.sizeOfDimension.size(); j++) {
					size *= s.sizeOfDimension.get(j);
				}
				size *= s.size;
				
				asmCode("", "addi", "r1", "r0", "" + size, "% array offset index: " + arrIndexList.get(i).tk.token);
				asmLW(arrIndexList.get(i), "r2", "");
				asmCode("", "mul", "r1", "r1", "r2");
				asmCode("", "add", "r11", "r11", "r1");
			}
			//asmCode("", "muli", "r11", "r11", "" + s.size);
		}
		arrIndexList.clear();
		return 0;
	}
	private int asmClassMemberOffset(int offset) {
		asmCode("", "addi", "r11", "r11", "" + offset, "% class member offset: " + offset);
		return 0;
	}

	// compare date types
	public Symbol compDateType(Symbol a, Symbol b) {
		if (a.dataType.token.equals(b.dataType.token)) {
			// generate asm code
			//asmCode("% " + a.tk.token + "=" + b.tk.token);			
			asmLW(b, "r1", "% " + a.tk.token + "=" + b.tk.token);
			asmSW(a);
			return a;
		}
		if (a.dataType.token.equals("integer") && b.dataType.token.equals("real")) {
			// convert a -> real
			printWarn(a, "Warning: Convert from 'real' to 'integer'");
			asmLW(b, "r1", "% " + a.tk.token + "=" + b.tk.token);
			//asmCode("", "sr", "r1", "8", "", "% real -> integer: " + b.tk.token);
			asmCode("", "divi", "r1", "r1", floatMask, "% real -> integer: " + b.tk.token);
			asmSW(a);
			return a;
		}
		if (a.dataType.token.equals("real") && b.dataType.token.equals("integer")) {
			// convert b -> real
			printWarn(a, "Warning: Convert from 'integer' to 'real'");
			asmLW(b, "r1", "% " + a.tk.token + "=" + b.tk.token);
			//asmCode("", "sl", "r1", "8", "", "% integer -> real: " + b.tk.token);
			asmCode("", "muli", "r1", "r1", floatMask, "% integer -> real: " + b.tk.token);
			asmSW(a);
			return a;
		}
//		Symbol err = new Symbol();
//		err.tk = (Token)b.tk.clone();
//		err.dataType = (Token)b.dataType.clone();
//		err.dataType.token = "err";
		printErr(a, "Cannot convert from '" + b.dataType.token + "' to '" + a.dataType.token + "'.");
		return b;
	}

	private int asmGenMathExpr(Symbol a, Symbol b, Token tkOp, int flag) {
		//asmCode("% " + a.tk.token + tkOp.token + b.tk.token);	
		if (flag == 0 || flag == 3) {
			asmLW(b, "r2", "% " + a.tk.token + tkOp.token + b.tk.token);
			asmPopOffset(a);
			asmLW(a, "r1", "");			
		} else {
			asmLW(b, "r2", "% integer -> real: " + b.tk.token);
			//asmCode("", "sl", "r2", "8", "");
			asmCode("", "muli", "r2", "r2", floatMask);
			asmPopOffset(a);
			if (flag == 1) {
				asmLW(a, "r1", "% " + b.tk.token + tkOp.token + a.tk.token);
			} else {
				asmLW(a, "r1", "% " + a.tk.token + tkOp.token + b.tk.token);
			}			
		} 
		// ASM operation
		String asmOp = "add";
		if (tkOp.token.equals("+")) {
			asmOp = "add";
		} else if (tkOp.token.equals("-")) {
			asmOp = "sub";
		} else if (tkOp.token.equals("*")) {
			asmOp = "mul";
		} else if (tkOp.token.equals("/")) {
			asmOp = "div";
			if (flag == 1) {
				//asmCode("", "sl", "r2", "8", "", "% << 8");
				asmCode("", "muli", "r2", "r2", floatMask, "% mul " + floatMask);
			} else if (flag != 0) {
				//asmCode("", "sl", "r1", "8", "", "% << 8");
				asmCode("", "muli", "r1", "r1", floatMask, "% mul " + floatMask);
			}			
			//asmCode("", "sl", "r2", "8", "", "% << 8");
		} else if (tkOp.token.equals("and")) {
			asmOp = "and";
		} else if (tkOp.token.equals("or")) {
			asmOp = "or";
		} else if (tkOp.token.equals("==")) {
			asmOp = "ceq";
		} else if (tkOp.token.equals("<>")) {
			asmOp = "cne";
		} else if (tkOp.token.equals("<")) {
			asmOp = "clt";
		} else if (tkOp.token.equals("<=")) {
			asmOp = "cle";
		} else if (tkOp.token.equals(">")) {
			asmOp = "cgt";
		} else if (tkOp.token.equals(">=")) {
			asmOp = "cge";
		} 
		if (flag == 1) {
			asmCode("", asmOp, "r3", "r2", "r1");
		} else {
			asmCode("", asmOp, "r3", "r1", "r2");
		} 
		if (flag != 0 && tkOp.token.equals("*")) {
			//asmCode("", "sr", "r3", "8", "", "% >> 8");
			asmCode("", "divi", "r3", "r3", floatMask, "% div " + floatMask);
		}
		
		// create a temporary address for the result
		String addr = "expr_" + createTempAddr(a);
		asmData(addr, "dw", "0", "", "");
		asmCode("", "sw", addr + "(r0)", "r3", "");
		a.address = addr;		// change the address of the symbol.
		if (a.symbolType == SYMBOLTYPE.NUMBER) {
			a.symbolType = SYMBOLTYPE.UNKNOWN;		// it is not a number anymore
		}
		// the value has been stored into the temporary address.
		//a.isArray = false;
		//asmResetOffset();
		a.symbolType = SYMBOLTYPE.UNKNOWN;
		return 0;
	}
	public Symbol compDateTypeNum(Symbol a, Symbol b, Token tkOp) {
		//System.out.println(a.tk.token + ", " + b.tk.token);
		if (a.dataType.token.equals("integer") && b.dataType.token.equals("real")) {
			// convert a -> real
			printWarn(a, "Warning: Convert from 'integer' to 'real'");
			asmGenMathExpr(b, a, tkOp, 1);
			return b;
		}
		if (b.dataType.token.equals("integer") && a.dataType.token.equals("real")) {
			// convert b -> real
			printWarn(b, "Warning: Convert from 'integer' to 'real'");
			asmGenMathExpr(a, b, tkOp, 2);
			return a;
		}
		if (a.dataType.token.equals("integer") && b.dataType.token.equals("integer")) {
			asmGenMathExpr(a, b, tkOp, 0);
			return a;
		}
		if (a.dataType.token.equals("real") && b.dataType.token.equals("real")) {
			asmGenMathExpr(a, b, tkOp, 3);
			return a;
		}
		printErr(a, "and token: " + b.tk.token + ", Type should be integer or real.");
		return b;
	}

	// store function parameters of the variable
	public ArrayList<Symbol> varFuncParams = null;	
	public ArrayList<String> varFuncParamsAttr = new ArrayList<String>();

	private boolean compParams(SymbolTable st) {
		int cnt = 0;
		for (int i = 0; i < st.symbols.size(); i++) {
			Symbol s = st.symbols.get(i);
			if (s.symbolType == SYMBOLTYPE.PARAMETER) {
				cnt++;
				if (varFuncParams.size() < cnt) {
					return false;
				}
				Symbol p = varFuncParams.get(i);
				if (!s.dataType.token.equals(p.dataType.token)) {
					if (!((s.dataType.token.equals("real") && p.dataType.token.equals("integer"))
							|| (s.dataType.token.equals("integer") && p.dataType.token.equals("real")))) {
						return false;
					}
				}
			}
		}
		if (varFuncParams.size() != cnt) {
			return false;
		}
		
		// print warning message
		cnt = 0;
		varFuncParamsAttr.clear();
		for (int i = 0; i < st.symbols.size(); i++) {
			Symbol s = st.symbols.get(i);
			if (s.symbolType == SYMBOLTYPE.PARAMETER) {
				cnt++;
				Symbol p = varFuncParams.get(i);
				varFuncParamsAttr.add(s.dataType.token);
				if (s.dataType.token.equals("real") && p.dataType.token.equals("integer")) {
					printWarn(p, "Warning: Convert parameter " + cnt + " from 'integer' to 'real'");
				}
				if (s.dataType.token.equals("integer") && p.dataType.token.equals("real")) {
					printWarn(p, "Warning: Convert parameter " + cnt + " from 'real' to 'integer'");
				}
			}
		}		
		return true;
	}
	// check all parameters for a function variable 
	public boolean ifValidFuncParamType(Symbol var) {
		if (var.symbolType == SYMBOLTYPE.CHKMEMBER) {
			// statement likes: a = class.func(a, b);
			// find the function from the class scope
			if (stBak == null) {
				return false;
			}
			for (int i = 0; i < stBak.symbols.size(); i++) {
				Symbol s = stBak.symbols.get(i);
				if (s.symbolType == SYMBOLTYPE.CLASS && s.tk.token.equals(var.className)) {
					for (int j = 0; j < s.child.symbols.size(); j++) {
						Symbol t = s.child.symbols.get(j);
						if (t.symbolType == SYMBOLTYPE.FUNCTION && t.tk.token.equals(var.tk.token)) {
							if (compParams(t.child)) {
								var.dataType = (Token) t.dataType.clone();
								var.address = t.address;
								var.size = t.size;
								var.symbolType = SYMBOLTYPE.FUNCTION;
								printLog(t, "ifValidFuncParamType OK");
								printLog(var, "ifValidFuncParamType OK");
								return true;
							}
						}
					}
				}
			}
		} else  {
			// statement likes: a = func(a, b);
			// first try to find the function within the scope which var is placed in.
			if (var.self != null) {
				for (int i = 0; i < var.self.symbols.size(); i++) {
					Symbol s = var.self.symbols.get(i);
					if (s.symbolType == SYMBOLTYPE.FUNCTION && s.tk.token.equals(var.tk.token)) {
						if (compParams(s.child)) {
							// correct the data type
							var.dataType = (Token) s.dataType.clone();
							var.address = s.address;
							var.size = s.size;
							var.symbolType = SYMBOLTYPE.FUNCTION;
							printLog(s, "ifValidFuncParamType OK");
							printLog(var, "ifValidFuncParamType OK");
							return true;
						}
					}
				}
			}
			
			// then try to find the function from Global functions
			if (stBak == null) {
				return false;
			}
			for (int i = 0; i < stBak.symbols.size(); i++) {
				Symbol s = stBak.symbols.get(i);
				if (s.symbolType == SYMBOLTYPE.FUNCTION && s.tk.token.equals(var.tk.token)) {
					if (compParams(s.child)) {
						var.dataType = (Token) s.dataType.clone();
						var.address = s.address;
						var.size = s.size;
						var.symbolType = SYMBOLTYPE.FUNCTION;
						printLog(s, "ifValidFuncParamType OK");
						printLog(var, "ifValidFuncParamType OK");
						return true;
					}
				}
			}
		}
		
		printErr(var, "Undeclared identifier: function parameters mismatch.");		
		return false;
	}
	
	// print ASM code
	public void asmData(String op) {
		String cmd;
		cmd = String.format("%-25s %-7s ", "", op);
		secData.append(cmd + "\r\n");
		//System.out.println(cmd);
	}
	public void asmCode(String op) {
		String cmd;
		if (secCode.charAt(secCode.length() - 1) != '\n') {
			cmd = String.format(" %-7s ", op);
		} else {
			cmd = String.format("%-25s %-7s ", "", op);
		}
		secCode.append(cmd + "\r\n");
		//System.out.println(cmd);
	}
	public void asmDataLable(String lbl) {
		String cmd = String.format("%-25s", lbl);
		secData.append(cmd);
	}	
	public void asmCodeLable(String lbl) {
		String cmd = String.format("%-25s", lbl);
		secCode.append(cmd);
	}
	public String asmCmd(String lbl, String op, String arg1, String arg2, String arg3, boolean isCode) {
		String cmd;
		if (arg2.isEmpty()) {
			if (isCode && lbl.isEmpty() && secCode.charAt(secCode.length() - 1) != '\n') {
				cmd = String.format(" %-7s %s ", op, arg1);
			} else {
				cmd = String.format("%-25s %-7s %s ", lbl, op, arg1);
			}
		} else if (arg3.isEmpty()) {
			if (isCode && lbl.isEmpty() && secCode.charAt(secCode.length() - 1) != '\n') {
				cmd = String.format(" %-7s %s, %s ", op, arg1, arg2);
			} else {
				cmd = String.format("%-25s %-7s %s, %s ", lbl, op, arg1, arg2);
			}
		} else {
			if (isCode && lbl.isEmpty() && secCode.charAt(secCode.length() - 1) != '\n') {
				cmd = String.format(" %-7s %s, %s, %s ", op, arg1, arg2, arg3);
			} else {
				cmd = String.format("%-25s %-7s %s, %s, %s ", lbl, op, arg1, arg2, arg3);
			}
		}
		return cmd;
	}
	public void asmData(String lbl, String op, String arg1, String arg2, String arg3) {
		secData.append(asmCmd(lbl, op, arg1, arg2, arg3, false) + "\r\n");
	}
	public void asmCode(String lbl, String op, String arg1, String arg2, String arg3) {
		secCode.append(asmCmd(lbl, op, arg1, arg2, arg3, true) + "\r\n");
	}
	public void asmCode(String lbl, String op, String arg1, String arg2, String arg3, String arg4) {
		String cmd;
		if (arg2.isEmpty()) {
			if (lbl.isEmpty() && secCode.charAt(secCode.length() - 1) != '\n') {
				cmd = String.format(" %-7s %s \t\t\t%s", op, arg1, arg4);
			} else {
				cmd = String.format("%-25s %-7s %s \t\t\t%s", lbl, op, arg1, arg4);
			}
		} else if (arg3.isEmpty()) {
			if (lbl.isEmpty() && secCode.charAt(secCode.length() - 1) != '\n') {
				cmd = String.format(" %-7s %s, %s \t\t%s", op, arg1, arg2, arg4);
			} else {
				cmd = String.format("%-25s %-7s %s, %s \t\t%s", lbl, op, arg1, arg2, arg4);
			}
		} else {
			if (lbl.isEmpty() && secCode.charAt(secCode.length() - 1) != '\n') {
				cmd = String.format(" %-7s %s, %s, %s \t%s", op, arg1, arg2, arg3, arg4);
			} else {
				cmd = String.format("%-25s %-7s %s, %s, %s \t%s", lbl, op, arg1, arg2, arg3, arg4);
			}
		}
		secCode.append(cmd + "\r\n");
	}
	public void asmStartASM() {
		asmData("% Data Section");
		asmCodeLable(" ");
		asmCode("% Code Section");	
	}
	public void asmStartProg() {
		asmCode("");
		asmCode("entry");
		//asmCode("", "addi", "r14", "r0", "topaddr", "% Set stack pointer\r\n");
		asmCode("", "add", "r14", "r0", "r0", "% Set stack pointer\r\n");
		//asmCode("", "add", "r11", "r0", "r0", "% Reserved to calculate offset\r\n");
	}
	public void asmEndProg() {
		asmCode("hlt");
	}
	public void asmGenDataAndCode() {
		SysLogger.asm(secData.toString());
		//SysLogger.asm("\n");
		SysLogger.asm(secCode.toString());
	}
	public void asmVarDefinition(Symbol s) {
		if (s.self.parent != null && s.self.parent.symbolType == SYMBOLTYPE.CLASS) {
			// class member, do not need to allocate address
			return;
		} 
		
		if (s.isArray) {
			asmData(s.address, "res", "" + getArrayTotalSize(s), "", "");
		} else {
			if (s.size > 4 && s.symbolType == SYMBOLTYPE.VARIABLE) {
				asmData(s.address, "res", "" + (s.size), "", "");
			} else {
				asmData(s.address, "dw", "0", "", "");
			}
		}
	}
	private String strFuncEntry = "_e";
	public void asmFuncDefinition(Symbol s) {
		asmData(s.address, "dw", "0", "", "");
		asmCode("% Function definition: " + s.tk.token);
		asmCodeLable(s.address + strFuncEntry);
	}
	
	private String createTempAddr(Symbol a) {
		String addr = a.tk.line + "_" + a.tk.column + "_" + (new Random()).nextInt(999999);
		return addr;
	}
	private String createTempAddr(Token tk) {
		String addr = tk.line + "_" + tk.column + "_" + (new Random()).nextInt(999999);
		return addr;
	}
	
	public int asmOPNot(Symbol s) {
		//asmCode("% not " + s.tk.token);		
		String addr = "expr_" + createTempAddr(s);
		asmLW(s, "r1", "% not " + s.tk.token);
		asmCode("", "not", "r3", "r1", "");
		asmData(addr, "dw", "0", "", "");
		asmCode("", "sw", addr + "(r0)", "r3", "");
		s.address = addr;
		if (s.symbolType == SYMBOLTYPE.NUMBER) {
			s.symbolType = SYMBOLTYPE.UNKNOWN;		// it is not a number anymore
		}
		return 0;
	}
	public int asmOPSign(Symbol s) {
		//asmCode("% - " + s.tk.token);		
		String addr = "expr_" + createTempAddr(s);
		asmCode("", "add", "r1", "r0", "r0", "\t\t\t% - " + s.tk.token);
		asmLW(s, "r2", "");	
		asmCode("", "sub", "r3", "r1", "r2");
		asmData(addr, "dw", "0", "", "");
		asmCode("", "sw", addr + "(r0)", "r3", "");
		s.address = addr;
		if (s.symbolType == SYMBOLTYPE.NUMBER) {
			s.symbolType = SYMBOLTYPE.UNKNOWN;		// it is not a number anymore
		}
		return 0;
	}
	
	public String asmOPIfThen(Symbol s) {
		//asmCode("% if " + s.tk.token + " then");		
		asmLW(s, "r1", "% if " + s.tk.token + " then");	
		String addr = "else_" + createTempAddr(s);
		asmCode("", "bz", "r1", addr, "");

		return addr;
	}

	private void asmLW(Symbol s, String r, String m) {
		// r12 is reserved for floating number.
		if (s.symbolType == SYMBOLTYPE.NUMBER) {
			String addr = "num_" + createTempAddr(s);

			if (s.dataType.token.equals("real")) {
				String[] lst = s.tk.token.split("\\.");
				if (lst[0].isEmpty()) {
					asmCode("", "addi", "r12", "r0", "0", "\t\t\t" + m);
				} else {
					asmData(addr, "dw", lst[0], "", "");
					asmCode("", "lw", "r12", addr + "(r0)", "", "\t\t\t" + m);
					//asmCode("", "addi", "r12", "r0", lst[0], "\t\t\t" + m);
					asmCode("", "muli", "r12", "r12", floatMask);
				}
				//asmCode("", "sl", "r12", "8", "");
				//asmCode("", "addi", r, "r12", lst[1]);
				Double f = Double.parseDouble("0." + lst[1]);
				int mask = Integer.parseInt(floatMask);

				asmCode("", "addi", r, "r12", "" + ((int)(f * mask)) % mask);
			} else {
				asmData(addr, "dw", s.tk.token, "", "");
				asmCode("", "lw", r, addr + "(r0)", "", "\t\t\t" + m);
				//asmCode("", "addi", r, "r0", s.tk.token, "\t\t\t" + m);
			}
			return;
		}
		
		// if it is a member variable, get start address from func address
		if (stBak == null) {
			return;
		}
		Symbol c = null, v = null;
		boolean findit = false;
		int offset = 0;
		
		if (s.symbolType != SYMBOLTYPE.FUNCTION) {
			for (int i = 0; i < stBak.symbols.size(); i++) {
				c = stBak.symbols.get(i);
				offset = 0;
				if (c.symbolType == SYMBOLTYPE.CLASS) {
					for (int j = 0; j < c.child.symbols.size(); j++) {
						v = c.child.symbols.get(j);
						if (v.address.equals(s.address)) {
							findit = true;
							break;
						}
						if (v.isArray) {
							offset += getArrayTotalSize(v);
						} else {
							offset += v.size;
						}
					}
					if (findit) {
						break;
					}
				}
			}
		}
		
		if (s.symbolType == SYMBOLTYPE.CHKMEMBER ||
				(s.symbolType != SYMBOLTYPE.UNKNOWN && s.isArray)) {
			if (s.ifPassedByAddress) {
				// get address of the class instance
				if (findit) {
					// get class instance address
					asmCode("", "lw", r, c.address + "(r0)", "", "% Get variable address. " + m);
					asmCode("", "addi", "r10", "r0", "" + offset, "% member variable offset");
					asmCode("", "add", r, r, "r10");
				} else {
					asmCode("", "lw", r, s.address + "(r0)", "", m + ". pass by address");
				}
				// get the address of the member
				asmCode("", "add", r, r, "r11");
				// get the value
				asmCode("", "lw", r, "0(" + r + ")", "");
			} else {
				if (findit) {
					asmCode("", "lw", r, c.address + "(r0)", "", "% Get variable address. " + m);
					asmCode("", "addi", "r10", "r0", "" + offset, "% member variable offset");
					asmCode("", "add", r, r, "r10");
					asmCode("", "add", r, r, "r11");
					asmCode("", "lw", r, "0(" + r + ")", "");
				} else {
					asmCode("", "lw", r, s.address + "(r11)", "", m);
				}
			}
		} else {
			if (findit) {
				asmCode("", "lw", r, c.address + "(r0)", "", "% Get variable address. " + m);
				asmCode("", "addi", "r10", "r0", "" + offset, "% member variable offset");
				asmCode("", "add", r, r, "r10");
				asmCode("", "lw", r, "0(" + r + ")", "");
			} else {
				asmCode("", "lw", r, s.address + "(r0)", "", m);
			}
		}
	}
	private void asmSW(Symbol s) {
		// if it is a member variable, get start address from func address
		if (stBak == null) {
			return;
		}
		Symbol c = null, v = null;
		boolean findit = false;
		int offset = 0;
		
		if (s.symbolType != SYMBOLTYPE.FUNCTION) {
			for (int i = 0; i < stBak.symbols.size(); i++) {
				c = stBak.symbols.get(i);
				offset = 0;
				if (c.symbolType == SYMBOLTYPE.CLASS) {
					for (int j = 0; j < c.child.symbols.size(); j++) {
						v = c.child.symbols.get(j);
						if (v.address.equals(s.address)) {
							findit = true;
							break;
						}
						if (v.isArray) {
							offset += getArrayTotalSize(v);
						} else {
							offset += v.size;
						}
					}
					if (findit) {
						break;
					}
				}
			}
		}
		
		if (s.symbolType == SYMBOLTYPE.CHKMEMBER ||
				(s.symbolType != SYMBOLTYPE.UNKNOWN && s.isArray)) {
			asmPopR("r11");
			if (s.ifPassedByAddress) {
				// get address of the class instance			
				if (findit) {
					asmCode("", "lw", "r2", c.address + "(r0)", "", "% Get variable address ");
					asmCode("", "addi", "r10", "r0", "" + offset, "% member variable offset");
					asmCode("", "add", "r2", "r2", "r10");
				} else {
					asmCode("", "lw", "r2", s.address + "(r0)", "", "% pass by address");
				}
				// get the address of the member
				asmCode("", "add", "r2", "r2", "r11");
				// get the value
				asmCode("", "sw", "0(r2)", "r1", "");				
			} else {
				if (findit) {
					asmCode("", "lw", "r2", c.address + "(r0)", "", "% Get variable address ");
					asmCode("", "addi", "r10", "r0", "" + offset, "% member variable offset");
					asmCode("", "add", "r2", "r2", "r10");
					asmCode("", "add", "r2", "r2", "r11");
					asmCode("", "sw", "0(r2)", "r1", "");
				} else {
					asmCode("", "sw", s.address + "(r11)", "r1", "");
				}
			}
		} else {
			if (findit) {
				asmCode("", "lw", "r2", c.address + "(r0)", "", "% Get variable address ");
				asmCode("", "addi", "r10", "r0", "" + offset, "% member variable offset");
				asmCode("", "add", "r2", "r2", "r10");
				asmCode("", "sw", "0(r2)", "r1", "");
			} else {
				asmCode("", "sw", s.address + "(r0)", "r1", "");
			}
		}
	}
	
	public String asmOPIfElse(Symbol s, String elseAddr) {
		asmCode("% if ... else ");
		
		String addr = "endif_" + createTempAddr(s);
		asmCode("", "j", addr, "", "");
		//asmCode(elseAddr, "nop", "", "", "");
		asmCodeLable(elseAddr);
		return addr;
	}
	public void asmOPIfEndif(Symbol s, String endifAddr) {
		asmCode("% if ... endif ");		
		//asmCode(endifAddr, "nop", "", "", "");
		asmCodeLable(endifAddr);
	}
	public String asmOPWhile(Token tk) {
		String addr = "gowhile_" + createTempAddr(tk);
		asmCodeLable(addr);
		return addr;
	}
	public String asmOPWhileDo(Symbol s) {
		asmLW(s, "r1", "% while " + s.tk.token + " do");	
		String addr = "endwhile_" + createTempAddr(s);

		asmCode("", "bz", "r1", addr, "");
		return addr;
	}
	public void asmOPIfEndWhile(String goWhile, String endWhile) {
		asmCode("", "j", goWhile, "", "");
		asmCode("% while ... end ");		
		asmCodeLable(endWhile);
	}	
	
	// push data into stack and pop data from stack
	// r13, r14 are reserved for stack
	public int asmPush(Symbol s) {
		asmLW(s, "r13", "% Push");
		asmCode("", "subi", "r14", "r14", "4");
		asmCode("", "sw", "topaddr(r14)", "r13", "");
		return 0;
	}
	public int asmPushAndShift(Symbol s, String shift) {
		asmLW(s, "r13", "% Push");
		//asmCode("", shift, "r13", "8", "", "% shift parameter");
		asmCode("", shift, "r13", "r13", floatMask, "% shift parameter");
		asmCode("", "subi", "r14", "r14", "4");
		asmCode("", "sw", "topaddr(r14)", "r13", "");
		return 0;
	}
	public int asmPushR(String r) {
		asmCode("", "subi", "r14", "r14", "4", "\t\t% push " + r);
		asmCode("", "sw", "topaddr(r14)", r, "");
		return 0;
	}

	public void asmPushOffset(Symbol s) {
		if (s.symbolType == SYMBOLTYPE.CHKMEMBER ||
				(s.symbolType != SYMBOLTYPE.UNKNOWN && s.isArray)) {
			asmPushR("r11");
		}
	}
	private void asmPopOffset(Symbol s) {
		if (s.symbolType == SYMBOLTYPE.CHKMEMBER ||
				(s.symbolType != SYMBOLTYPE.UNKNOWN && s.isArray)) {
			asmPopR("r11");
		}
	}
	public void asmResetOffset() {
		asmCode("", "add", "r11", "r0", "r0", "% reset offset");
	}
	public int asmPop(Symbol s) {
		asmCode("", "lw", "r13", "topaddr(r14)", "", "% Pop");
		asmCode("", "addi", "r14", "r14", "4");
		asmCode("", "sw", s.address + "(r0)", "r13", "");
		return 0;
	}
	public int asmPopR(String r) {
		asmCode("", "lw", r, "topaddr(r14)", "", "% Pop " + r);
		asmCode("", "addi", "r14", "r14", "4");
		return 0;
	}
	
	// function definition
	public int asmPopFunctionParams(Symbol func) {
		// pop the class instance address first
		if (stCur.parent != null && stCur.parent.self.parent != null) {
			// it is member function, get the class address first
			asmPop(func.self.parent);		// TODO: temporarily store in the class address.
		}
		
		// pop all the parameters of the function in the reverse order
		for (int i = stCur.symbols.size() - 1; i >= 0; i--) {
			Symbol s = stCur.symbols.get(i);
			if (s.symbolType == SYMBOLTYPE.PARAMETER) {
				if ((s.dataType.token.equals("integer") || s.dataType.token.equals("real"))) {
					asmPop(s);		// pass by value
				} else {
					// get the address first
					asmPopR("r1");
					//asmCode("", "lw", "r1", "0(r2)", "", "% get value of address");
					asmCode("", "sw", s.address + "(r0)", "r1", "");
				}
			}
		}
		return 0;
	}
	public int asmEndOfFuncDefinition() {
		// go back to the calling PC
		// r15 is reserved for calling link.
		asmCode("", "jr", "r15", "", "");
		return 0;
	}
	public int asmFuncReturn(Symbol s) {
		// check the types
		if (stCur.parent != null && stCur.parent.symbolType == SYMBOLTYPE.FUNCTION) {
			compDateType(stCur.parent, s);
			return 0;
		}
		SysLogger.err("asmFuncReturn: " + s.tk.token);
		return -1;
	}	
	
	// function calling
	public int asmCallingFunc(Symbol s, Symbol cls) {
		if (s.address == null) {		// stBak == null
			return -1;
		}
		
		// check if it is a recursively calling
		if (stCur.parent != null && stCur.parent.address.equals(s.address)) {
			// save environment, parameters and local variables
			// Push the data of parameters passed by value into the stack.
			for (int i = 0; i < stCur.symbols.size(); i++) {
				Symbol tmp = stCur.symbols.get(i);
				if (tmp.symbolType == SYMBOLTYPE.PARAMETER) {
					if (tmp.dataType.token.equals("integer") || tmp.dataType.token.equals("real")) {
						asmPush(tmp);
					}
				}
				// Push the data of all local variables into the stack.
				if (tmp.symbolType == SYMBOLTYPE.VARIABLE) {
					if (tmp.dataType.token.equals("integer") || tmp.dataType.token.equals("real")) {
						asmPush(tmp);
					} else {
						// TODO:
					}
				}
			}
			
		}
		
		// push r15
		asmPushR("r15");
		
		// push all parameters into stack
		for (int i = 0; i < varFuncParams.size(); i++) {
			if (varFuncParams.size() == varFuncParamsAttr.size()) {
				if (varFuncParamsAttr.get(i).equals("integer") 
						&& varFuncParams.get(i).dataType.token.equals("real")) {
					//asmPushAndShift(varFuncParams.get(i), "sr");
					asmPushAndShift(varFuncParams.get(i), "divi");
					continue;
				}
				if (varFuncParamsAttr.get(i).equals("real") 
						&& varFuncParams.get(i).dataType.token.equals("integer")) {
					//asmPushAndShift(varFuncParams.get(i), "sl");
					asmPushAndShift(varFuncParams.get(i), "muli");
					continue;
				}
			}
			if ((varFuncParams.get(i).dataType.token.equals("integer")
					|| varFuncParams.get(i).dataType.token.equals("real"))) {
				// parameter passed by value
				asmPush(varFuncParams.get(i));		
			} else {
				// passed by address
				asmCode("", "addi", "r1", "r11", varFuncParams.get(i).address, "% pass by address");
				asmPushR("r1");
			}
		}
		
		// push class instance address into stack
		if (cls.symbolType == SYMBOLTYPE.CHKMEMBER || 
				(stCur.parent != null && stCur.parent.self.parent != null && cls.self != null &&
				stCur.parent.self.parent.address.equals(cls.self.parent.address))) {
			// get the offset of all member variables
			Symbol c = null, v = null;
			int offset = 0;
			
			if (stBak == null) {
				return 0;
			}
			for (int i = 0; i < stBak.symbols.size(); i++) {
				c = stBak.symbols.get(i);
				if (c.symbolType == SYMBOLTYPE.CLASS && c.tk.token.equals(cls.className)) {
					offset = 0;
					for (int j = 0; j < c.child.symbols.size(); j++) {
						v = c.child.symbols.get(j);
						if (v.symbolType == SYMBOLTYPE.VARIABLE) {
							if (v.isArray) {
								offset += getArrayTotalSize(v);
							} else {
								offset += v.size;
							}
						}
					}
				}
			}
			
			// if cls is a member of this function
			boolean ifLocalVar = false;
			for (int i = 0; i < stCur.symbols.size(); i++) {
				Symbol m = stCur.symbols.get(i);
				if (m.address.equals(cls.address)) {
					ifLocalVar = true;
					break;
				}
			}
			
			// if it is class function, calculate the real class instance address
			if (!ifLocalVar && stCur.parent != null && stCur.parent.self.parent != null 
					&& stCur.parent.self.parent.symbolType == SYMBOLTYPE.CLASS) {
				int cls_offset = 0;
				for (int j = 0; j < stCur.parent.self.symbols.size(); j++) {
					v = stCur.parent.self.symbols.get(j);
					if (v.address.equals(cls.address)) {
						break;
					}
					if (v.symbolType == SYMBOLTYPE.VARIABLE) {
						if (v.isArray) {
							cls_offset += getArrayTotalSize(v);
						} else {
							cls_offset += v.size;
						}
					}
				}
				if (cls.self != null && stCur.parent.self.parent.address.equals(cls.self.parent.address)) {
					// call class function which is located in the same class
					asmCode("", "lw", "r1", stCur.parent.self.parent.address + "(r0)", "", "% push class instance address");
				} else {
					asmCode("", "lw", "r1", stCur.parent.self.parent.address + "(r0)", "", "% offset of class var");
					asmCode("", "addi", "r1", "r1", "" + cls_offset);
					asmCode("", "subi", "r11", "r11", "" + offset);
					asmCode("", "add", "r1", "r1", "r11", "% push class instance address");
				}
				
			} else {
				// subtract offset from r11
				asmCode("", "subi", "r11", "r11", "" + offset);
				asmCode("", "addi", "r1", "r11", cls.address, "% push class instance address");
			}
			asmPushR("r1");
		}
		
		// entry of function call
		asmCode("", "jl", "r15", s.address + strFuncEntry, "", "% call a function");
		
		// after calling the function
		// pop r15
		asmPopR("r15");
		
		// check if it is a recursively calling
		if (stCur.parent != null && stCur.parent.address.equals(s.address)) {
			// pop all the local variables of the function in the reverse order
			for (int i = stCur.symbols.size() - 1; i >= 0; i--) {
				Symbol tmp = stCur.symbols.get(i);
				if (tmp.symbolType == SYMBOLTYPE.VARIABLE) {
					if (tmp.dataType.token.equals("integer") || tmp.dataType.token.equals("real")) {
						asmPop(tmp);
					} else {
						// TODO:
					}
				}
				// and pop parameters
				if (tmp.symbolType == SYMBOLTYPE.PARAMETER) {
					if (tmp.dataType.token.equals("integer") || tmp.dataType.token.equals("real")) {
						asmPop(tmp);
					}
				}
			}
		}
		return 0;
	}
	
	// write & read
	private static String floatMask = "1000";
	public int asmWrite(Symbol s) {
		asmPushR("r15");
		asmLW(s, "r1", "% write " + s.tk.token);
		
		if (s.dataType.token.equals("real")) {
			String addr = "PositiveFloat_" + (new Random()).nextInt(999) + (new Random()).nextInt(9999999);

			asmCode("", "clt", "r2", "r1", "r0");		// if r1 < 0
			asmCode("", "bz", "r2", addr, "");
			
			asmCode("", "sub", "r1", "r0", "r1", "% NegativeFloat");	// -11.22 -> 11.22
			asmCode("", "addi", "r2", "r0", "45");		// print '-'
			asmCode("", "putc", "r2", "", "");			
			
			asmCode(addr, "add", "r12", "r0", "r1");	// save float number		
			//asmCode("", "sr", "r1", "8", "");			// print left part
			asmCode("", "divi", "r1", "r1", floatMask);
			asmCode("", "jl", "r15", "putint", "");			
			
			asmCode("", "addi", "r1", "r0", "46");		// print '.'
			asmCode("", "putc", "r1", "", "");
			
			asmCode("", "add", "r1", "r0", "r12", "% fraction");	// load float number again
			//asmCode("", "sl", "r1", "24", "");			// print right part
			//asmCode("", "sr", "r1", "24", "");
			asmCode("", "modi", "r1", "r1", floatMask);
			
			// add '0'
			String addr1 = "FloatAdd00_" + (new Random()).nextInt(999) + (new Random()).nextInt(9999999);
			String addr2 = "FloatAdd0_" + (new Random()).nextInt(999) + (new Random()).nextInt(9999999);
			asmCode("", "cgei", "r2", "r1", "100");		// if r1 >= 100
			asmCode("", "bnz", "r2", addr1, "");
			asmCode("", "cgei", "r2", "r1", "10");		// if r1 >= 10
			asmCode("", "bnz", "r2", addr2, "");
			asmCode("", "addi", "r2", "r0", "48");		// add 0
			asmCode("", "putc", "r2", "", "");		
			asmCode(addr2, "addi", "r2", "r0", "48");	// add 0
			asmCode("", "putc", "r2", "", "");		

			asmCode(addr1, "jl", "r15", "putint", "");
		} else
			asmCode("", "jl", "r15", "putint", "");			
		
		// print '\r\n'
		asmCode("", "addi", "r1", "r0", "13", "% print LFCR");
		asmCode("", "putc", "r1", "", "");
		asmCode("", "addi", "r1", "r0", "10");
		asmCode("", "putc", "r1", "", "");
		asmPopR("r15");
		return 0;
	}
	public int asmRead(Symbol s) {
		asmPushR("r15");
		asmPushOffset(s);
		asmCode("", "jl", "r15", "getint", "", "% read: " + s.tk.token);
		asmSW(s);
		asmPopR("r15");
		return 0;
	}


}
