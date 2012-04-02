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


package SyntacticAnalyzer;

import java.util.ArrayList;

import utils.SysLogger;
import LexicalAnalyzer.LexicalAnalyzer;
import LexicalAnalyzer.StateMachineDriver;
import LexicalAnalyzer.Token;
import SemanticActions.SemanticActions;
import SemanticActions.Symbol;
import SemanticActions.Symbol.SYMBOLTYPE;
import SemanticActions.SymbolTable;

public class SyntacticAnalyzer {
	private LexicalAnalyzer lexScanner = null;
	private Token curToken = null;
	private Token preToken = new Token();
	private Token nextToken = null;
	
	public String SYNTAX_END_SIGN = "$";
	
	public SemanticActions smActions = new SemanticActions();
	private Symbol curVar = null;
	
	public int init(LexicalAnalyzer la) {
		if (la == null) {
			SysLogger.err("SyntacticAnalyzer init. LexicalAnalyzer = null");
			return -1;
		}
		lexScanner = la;
		
		// initialize First & Follow sets
		if (FirstFollowSets.init() != 0) {
			return -1;
		}
		return 0;
	}

	// main function
	public boolean parse() {
		curToken = lexScanner.nextToken();
		if (curToken == null) {
			SysLogger.err("The input file seems empty.");
			return false;
		}
		
		//if (startSymbol() && match(SYNTAX_END_SIGN)) {
		if (startSymbol()) {
			if (curToken == null) {		// end of file
				return true;
			}
		}
		
		//err("END.");
		return false;
	}
	public boolean parseEx(SymbolTable st) {
		if (st == null) {
			return parse();
		}
		
		// copy function declarations
		smActions.stBak = (SymbolTable) st.clone();
		return parse();
	}
	
	private int getNextToken() {
		preToken = curToken;
		if (nextToken != null) {
			curToken = (Token) nextToken.clone();
			nextToken = null;
		} else {
			curToken = lexScanner.nextToken();
		}
		return 0;
	}
	
	private void err(String msg) {
		String str = "";
		
		if (curToken != null) {
			str = String.format("Syntax error at line: %4d, col: %4d. Token:%12s.  ", curToken.line, 
					curToken.column, curToken.token);
		} else {
			str = String.format("Syntax error at line: %4d, col: %4d. Token:%12s.  ", preToken.line, 
					preToken.column + preToken.token.length(), preToken.token);
		}
		SysLogger.err(str + msg);
	}
	
	// if next token match the given token
	private boolean matchEx(String tk, SYMBOLTYPE type) {
		SysLogger.log("match: " + tk);
		
		if (tk.equals("id")) {
			if (curToken.type == StateMachineDriver.TOKEN_TYPE_ID) {
				// store the symbol temporarily
				Symbol mb = new Symbol();
				if (type == SYMBOLTYPE.CHKMEMBER) {
					mb.tk = (Token) curToken.clone();
				} else {
					curVar = new Symbol();
					curVar.tk = (Token) curToken.clone();
					curVar.dataType = (Token) preToken.clone();
					curVar.symbolType = type;
				}
				
				if (type == SYMBOLTYPE.CHKVAR) {
					smActions.ifVarDefined(curVar);
				} else if (type == SYMBOLTYPE.CHKTYPE) {
					smActions.ifDataTypeDefined(curVar);
				} else if (type == SYMBOLTYPE.CHKMEMBER) {
					smActions.ifClassMember(curVar, mb);
					
					curVar = new Symbol();
					curVar.tk = (Token) curToken.clone();
					if (mb.dataType != null) {
						curVar.dataType = (Token) mb.dataType.clone();
					} else {
						curVar.dataType = (Token) preToken.clone();
					}
					curVar.symbolType = type;
					curVar.address = mb.address;		// used for checking params.
				} 
				getNextToken();
				return true;
			}
		} else if (tk.equals("int")) {
			if (curToken.type == StateMachineDriver.TOKEN_TYPE_INT) {
				if (type == SYMBOLTYPE.ARRAYSIZE) {
					curVar.isArray = true;
					curVar.sizeOfDimension.add(Integer.parseInt(curToken.token));
					curVar.dimensions++;
				}
				getNextToken();
				return true;
			} 
		} else if (tk.equals("num")) {
			if (curToken.type == StateMachineDriver.TOKEN_TYPE_INT
					|| curToken.type == StateMachineDriver.TOKEN_TYPE_FLOAT) {
				getNextToken();
				return true;
			} 
		} else if (tk.equals("addOp")) {
			if (curToken.type == StateMachineDriver.TOKEN_TYPE_OP_ADD) {
				getNextToken();
				return true;
			} 
		} else if (tk.equals("multOp")) {
			if (curToken.type == StateMachineDriver.TOKEN_TYPE_OP_MUL) {
				getNextToken();
				return true;
			} 
		} else if (tk.equals("relOp")) {
			if (curToken.type == StateMachineDriver.TOKEN_TYPE_OP_REL) {
				getNextToken();
				return true;
			} 
		} else if (tk.equals("assignOp")) {
			if (curToken.type == StateMachineDriver.TOKEN_TYPE_OP_ASS) {
				getNextToken();
				return true;
			} 
		} else if (curToken.token.equals(tk)) {
			if (type == SYMBOLTYPE.CLASS) {
				// find a new class definition
				smActions.newClass(curVar);
			} else if (type == SYMBOLTYPE.FUNCTION) {
				smActions.newFunction(curVar);
			} else if (type == SYMBOLTYPE.UNKNOWN_EXITTABLE) {
				smActions.exitCurSymbolTable();
			}
			
			getNextToken();
			return true;
		}

		err("Expected the token: " + tk);

		// try to insert this new token
		err("Inserts a new token: " + tk);
		
		// do not need to call getNextToken()

//		if (nextToken == null) {
//			nextToken = new Token();
//			
//			nextToken = (Token) curToken.clone();
//		}		 
//		// else, keep the original token, because curToken is a inserted token at this time.
//		curToken.error = true;
//		curToken.type = StateMachineDriver.TOKEN_TYPE_UNKNOWN;
//		curToken.token = tk;
//		err("Inserts a new token: " + tk);
		
		return true;		
	}
	
	private boolean match(String tk) {
		return matchEx(tk, SYMBOLTYPE.UNKNOWN);
	}
	
	// check if the next token is one of the tokens in the FIRST set for grammar 'symbol'
	private boolean isFirst(String symbol) {
		String set = FirstFollowSets.isFirst(symbol, curToken);
		
		if (set == "") {
			if (curToken != null && symbol.equals(curToken.token)) {
				return true;
			}
			return false;
		} else if (set != null) {
			return false;
		}
		return true;
	}
	
	// check if the next token is one of the tokens in the FOLLOW set for grammar 'symbol'
	private boolean isFollow(String symbol) {
		String set = FirstFollowSets.isFollow(symbol, curToken);
		
		if (set == null) {
			return true;
		}

		return false;
	}
	
	// if there is a invalid token, skips it
	// if the invalid token is a newline, insert ';'
	private boolean skipErrors(String first, String follow) {
		SysLogger.log("skipErrors: " + first + ", " + follow);
		
		// if there is an error
		if ((!first.equals("") && isFirst(first)) 
				|| (!follow.equals("") && isFollow(follow))) {
			return true;
		}
		
		// print the error message	
		String expectedTokens = FirstFollowSets.getFFSets(first, follow);
		if (expectedTokens == null && !first.equals("")) {
			expectedTokens = first;
		}
		err("Expected tokens: " + expectedTokens);
		
		// skip the tokens until find a valid one
		String lst[] = expectedTokens.split(" ");
		do {
			String newTk = null;

			if (curToken != null && preToken != null && curToken.line > preToken.line) {
				// if encounters a newline token
				// try to insert ')' or ';' if they are in the expected tokens list.
				for (int i = 0; i < lst.length; i++) {
					if (lst[i].equals(")")) {
						newTk = ")";
						break;
					} else if (lst[i].equals(";")) {
						newTk = ";";
						break;
					}
				}
				if (newTk != null) {
					if (nextToken == null) {
						nextToken = new Token();
						
						nextToken = (Token) curToken.clone();
					}
					// else, keep the original token, because curToken is a inserted token at this time.
					curToken.error = true;
					curToken.type = StateMachineDriver.TOKEN_TYPE_UNKNOWN;
					curToken.token = newTk;
					err("Inserts a new token: " + newTk);
					break;
				}
			}
			if (newTk == null) {
				if (curToken != null && nextToken == null) {
					// do not need to skip the token we inserted.
					err("Skips a token: " + curToken.token);
				}
				getNextToken();
				if (curToken != null && curToken.token != null) {
					if (curToken.token.equals("class")) {
						err("Finding the keyword 'class' while skiping invalid tokens. Discards previous grammars.");
						prog();
						curToken = null;		// exit;
						break;
					} else if (curToken.token.equals("program")) {
						err("Finding the keyword 'program' while skiping invalid tokens. Discards previous grammars.");
						progBody();
						curToken = null;		// exit;
						break;
					}
				}
			}
			
		} while (curToken != null && !(isFirst(first) || isFollow(follow)));

		return false;
	}

	private void printGrammar(String msg) {
		String str = "";
		
		if (preToken != null) {
			str = String.format("Line: %4s, Col: %4s, Token: %12s, \t", 
					preToken.line, preToken.column, preToken.token); 
		}
		//SysLogger.info(str + "Grammar: " + msg);
	}
	
	private void copyType(Symbol s, Symbol d) {
		s.tk = (Token)d.tk.clone();
		s.dataType = (Token)d.dataType.clone();
	}
	
	/* Grammar definition:
   prog             -> classDeclList progBody 
   classDeclList    -> classDecl classDeclList 
                     | EPSILON 
   classDecl        -> class id { varFuncDeclList } ; 
   varFuncDeclList  -> type id varFuncDeclListP 
                     | EPSILON 
   varFuncDeclListP -> ( fParams ) funcBody ; funcDefList 
                     | arraySizeList ; varFuncDeclList 
   funcDefList      -> funcDef funcDefList 
                     | EPSILON 
   funcDef          -> funcHead funcBody ; 
   funcHead         -> type id ( fParams ) 
   funcBody         -> { varStateList } 
   varStateList     -> integer id arraySizeList ; varStateList 
                     | real id arraySizeList ; varStateList 
                     | id varStateListP 
                     | EPSILON 
   varStateListP    -> indiceList variableP assignOp expr ; statementList 
                     | id arraySizeList ; varStateList 
   progBody         -> program funcBody ; funcDefList 
   statementList    -> statement statementList 
                     | EPSILON 
   arraySizeList    -> arraySize arraySizeList 
                     | EPSILON 
   statement        -> if ( expr ) then statBlock else statBlock ; 
                     | while ( expr ) do statBlock ; 
                     | read ( variable ) ; 
                     | return ( expr ) ; 
                     | write ( expr ) ; 
                     | variable assignOp expr ; 
   statBlock        -> { statementList } | statement 
                     | EPSILON 
   expr             -> arithExpr exprP 
   exprP            -> relOp arithExpr | EPSILON 
   arithExpr        -> term arithExprP 
   arithExprP       -> addOp term arithExprP 
                     | EPSILON 
   sign             -> + | - 
   term             -> factor termP 
   termP            -> multOp factor termP 
                     | EPSILON 
   factor           -> ( expr ) | id factorPP | num | not factor 
                     | sign factor 
   factorPP         -> ( aParams ) | indiceList factorP 
   factorP          -> . id factorPP | EPSILON 
   variable         -> id indiceList variableP 
   variableP        -> . id indiceList variableP 
                     | EPSILON 
   indiceList       -> indice indiceList | EPSILON 
   indice           -> [ arithExpr ] 
   arraySize        -> [ int ] 
   type             -> id | integer | real 
   fParams          -> type id arraySizeList fParamsTailList 
                     | EPSILON 
   fParamsTailList  -> fParamsTail fParamsTailList 
                     | EPSILON 
   aParams          -> expr aParamsTailList 
                     | EPSILON 
   aParamsTailList  -> aParamsTail aParamsTailList 
                     | EPSILON 
   fParamsTail      -> , type id arraySizeList 
   aParamsTail      -> , expr 

	*/	
	
	// entry point of the grammar
	private boolean startSymbol() {		
		return prog();
	}	

	private boolean prog() {
		smActions.newProg();
		skipErrors("classDeclList", "");
		if (isFirst("classDeclList")) {
			if (classDeclList() && progBody()) {
				printGrammar("prog             -> classDeclList progBody");
				return true;
			} else
				return false;
		}
		return false;		
	}
	private boolean classDeclList() {
		skipErrors("classDecl", "classDeclList");
		if (isFirst("classDecl")) {
			if (classDecl() && classDeclList()) {
				printGrammar("classDeclList    -> classDecl classDeclList");
				return true;
			} else
				return false;
		} else if (isFollow("classDeclList")) {
			printGrammar("classDeclList    -> EPSILON");
			return true;
		}
		return false;
	}	
	private boolean progBody() {
		skipErrors("program", "");
		if (isFirst("program")) {
			if (match("program") && funcBody() && match(";") && funcDefList()) {
				printGrammar("progBody         -> program funcBody ; funcDefList");
				return true;
			} else
				return false;
		}
		return false;		
	}
	private boolean classDecl() {
		skipErrors("class", "");
		if (isFirst("class")) {
			if (match("class") && match("id") && matchEx("{", SYMBOLTYPE.CLASS) && varFuncDeclList() && match("}") && match(";")) {
				printGrammar("classDecl        -> class id { varFuncDeclList } ;");
				smActions.exitCurSymbolTable();
				return true;
			} else
				return false;
		}
		return false;		
	}
	private boolean funcBody() {
		skipErrors("{", "");
		if (isFirst("{")) {
			if (match("{") && varStateList() && match("}")) {
				printGrammar("funcBody         -> { varStateList }");
				return true;
			} else
				return false;
		}
		return false;		
	}
	private boolean funcDefList() {
		skipErrors("funcDef", "funcDefList");
		if (isFirst("funcDef")) {
			if (funcDef() && funcDefList()) {
				printGrammar("funcDefList      -> funcDef funcDefList");
				return true;
			} else
				return false;
		} else if (isFollow("funcDefList")) {
			printGrammar("funcDefList      -> EPSILON");
			return true;
		}
		return false;
	}	
	private boolean varFuncDeclList() {
		skipErrors("type", "varFuncDeclList");
		if (isFirst("type")) {
			if (type() && match("id")  && varFuncDeclListP()) {
				printGrammar("varFuncDeclList  -> type id varFuncDeclListP");
				return true;
			} else
				return false;
		} else if (isFollow("varFuncDeclList")) {
			printGrammar("varFuncDeclList  -> EPSILON");
			return true;
		}
		return false;
	}	
	private boolean varStateList() {
		skipErrors("varStateList", "varStateList");
		Symbol expr = new Symbol();
		if (isFirst("integer")) {
			if (match("integer") && match("id") && arraySizeList() && match(";") && varStateList()) {
				printGrammar("varStateList     -> integer id arraySizeList ; varStateList");
				return true;
			} else
				return false;
		} else if (isFirst("real")) {
			if (match("real") && match("id") && arraySizeList() && match(";") && varStateList()) {
				printGrammar("varStateList     -> real id arraySizeList ; varStateList");
				return true;
			} else
				return false;
		} else if (isFirst("id")) {
			if (match("id") && varStateListP()) {
				printGrammar("varStateList     -> id varStateListP");
				return true;
			} else
				return false;
		} else if (isFirst("if")) {
			if (match("if") && match("(") && expr(expr) && match(")") && match("then") && statBlock() && match("else") && statBlock() && match(";") && statementList()) {
				printGrammar("varStateList     -> if ( expr ) then statBlock else statBlock ; statementList");
				return true;
			} else
				return false;
		} else if (isFirst("while")) {
			if (match("while") && match("(") && expr(expr) && match(")") && match("do") && statBlock() && match(";") && statementList()) {
				printGrammar("varStateList     -> while ( expr ) do statBlock ; statementList");
				return true;
			} else
				return false;
		} else if (isFirst("read")) {
			if (match("read") && match("(") && variable() && match(")") && match(";") && statementList()) {
				printGrammar("varStateList     -> read ( variable ) ; statementList");
				return true;
			} else
				return false;
		} else if (isFirst("return")) {
			if (match("return") && match("(") && expr(expr) && match(")") && match(";") && statementList()) {
				printGrammar("varStateList     -> return ( expr ) ; statementList");
				return true;
			} else
				return false;
		} else if (isFirst("write")) {
			if (match("write") && match("(") && expr(expr) && match(")") && match(";") && statementList()) {
				printGrammar("varStateList     -> write ( expr ) ; statementList");
				return true;
			} else
				return false;
		} else if (isFollow("varStateList")) {
			printGrammar("varStateList     -> EPSILON");
			return true;
		}
		return false;
	}	
	private boolean funcDef() {
		skipErrors("funcHead", "");
		if (isFirst("funcHead")) {
			if (funcHead() && funcBody() && match(";")) {
				printGrammar("funcDef          -> funcHead funcBody ;");
				smActions.exitCurSymbolTable();
				return true;
			} else
				return false;
		}
		return false;		
	}
	private boolean varFuncDeclListP() {
		skipErrors("varFuncDeclListP", "");
		if (isFirst("(")) {
			if (matchEx("(", SYMBOLTYPE.FUNCTION) && fParams() && match(")") && funcBody() && matchEx(";", SYMBOLTYPE.UNKNOWN_EXITTABLE) && funcDefList()) {
				printGrammar("varFuncDeclListP -> ( fParams ) funcBody ; funcDefList");
				return true;
			} else
				return false;
		} else if (isFirst("varFuncDeclListP")) {
			if (arraySizeList() && match(";") && varFuncDeclList()) {
				printGrammar("varStateList     -> arraySizeList ; varFuncDeclList");
				return true;
			} else
				return false;
		}
		return false;
	}
	private boolean varStateListP() {
		skipErrors("varStateListP", "");
		if (isFirst("id")) {
			smActions.ifDataTypeDefined(curVar);
			if (match("id") && arraySizeList() && match(";") && varStateList()) {
				printGrammar("varStateList     -> id arraySizeList ; varStateList");
				return true;
			} else
				return false;
		} else if (isFirst("varStateListP")) {
			smActions.ifVarDefined(curVar);
			Symbol expr = new Symbol();
			if (indiceList() && variableP() && match("assignOp")) {
				Symbol tmp = (Symbol)curVar.clone();
				copyType(tmp, curVar);
				if (expr(expr)) {
					smActions.compDateType(tmp, expr);
					if (match(";") && statementList()) {
						printGrammar("varStateListP    -> indiceList variableP assignOp expr ; statementList");
						return true;
					}
				}
			}
		}
		return false;
	}	
	private boolean funcHead() {
		skipErrors("type", "");
		if (isFirst("type")) {
			if (type() && match("id") && matchEx("(", SYMBOLTYPE.FUNCTION) && fParams() && match(")")) {
				printGrammar("funcHead         -> type id ( fParams )");
				smActions.ifFuncRedefined(curVar);
				return true;
			} else
				return false;
		} 
		return false;
	}
	private boolean fParams() {
		skipErrors("type", "fParams");
		if (isFirst("type")) {
			if (type() && matchEx("id", SYMBOLTYPE.PARAMETER) && arraySizeList() && fParamsTailList()) {
				printGrammar("fParams          -> type id arraySizeList fParamsTailList");
				return true;
			} else
				return false;
		} else if (isFollow("fParams")) {
			printGrammar("fParams          -> EPSILON");
			return true;
		}
		return false;
	}
	private boolean indiceList() {
		skipErrors("indice", "indiceList");
		if (isFirst("indice")) {
			if (indice() && indiceList()) {
				printGrammar("indiceList       -> indice indiceList");
				return true;
			} else
				return false;
		} else if (isFollow("indiceList")) {
			printGrammar("indiceList       -> EPSILON");
			return true;
		}
		return false;
	}
	private boolean variableP() {
		skipErrors(".", "variableP");
		if (isFirst(".")) {
			if (match(".") && matchEx("id", SYMBOLTYPE.CHKMEMBER) && indiceList() && variableP()) {
				printGrammar("variableP        -> . id indiceList variableP");
				return true;
			} else
				return false;
		} else if (isFollow("variableP")) {
			printGrammar("variableP        -> EPSILON");
			return true;
		}
		return false;
	}
	private boolean expr(Symbol expr) {
		skipErrors("arithExpr", "");
		if (isFirst("arithExpr")) {
			Symbol arithExpr = new Symbol();
			Symbol exprP = new Symbol();
			if (arithExpr(arithExpr) && exprP(arithExpr, exprP)) {
				printGrammar("expr             -> arithExpr exprP");
				//expr.dataType = (Token) exprP.dataType.clone();
				copyType(expr, exprP);
				return true;
			} else
				return false;
		}
		return false;
	}
	private boolean statementList() {
		skipErrors("statement", "statementList");
		if (isFirst("statement")) {
			if (statement() && statementList()) {
				printGrammar("statementList    -> statement statementList");
				return true;
			} else
				return false;
		} else if (isFollow("statementList")) {
			printGrammar("statementList    -> EPSILON");
			return true;
		}
		return false;
	}
	private boolean fParamsTailList() {
		skipErrors("fParamsTail", "fParamsTailList");
		if (isFirst("fParamsTail")) {
			if (fParamsTail() && fParamsTailList()) {
				printGrammar("fParamsTailList  -> fParamsTail fParamsTailList");
				return true;
			} else
				return false;
		} else if (isFollow("fParamsTailList")) {
			printGrammar("fParamsTailList  -> EPSILON");
			return true;
		}
		return false;
	}
	private boolean indice() {
		skipErrors("[", "");
		if (isFirst("[")) {
			Symbol tmp = (Symbol) curVar.clone();			
			copyType(tmp, curVar);
			Symbol arithExpr = new Symbol();
			if (match("[") && arithExpr(arithExpr) && match("]")) {
				printGrammar("indice           -> [ arithExpr ]");
				smActions.ifValidIndexType(arithExpr);
				curVar = (Symbol) tmp.clone();
				copyType(curVar, tmp);
				return true;
			} else
				return false;
		}
		return false;
	}
	private boolean arithExpr(Symbol arithExpr) {
		skipErrors("term", "");
		if (isFirst("term")) {
			Symbol term = new Symbol();
			Symbol arithExprP = new Symbol();
			if (term(term) && arithExprP(term, arithExprP)) {
				printGrammar("arithExpr        -> term arithExprP");
				//arithExpr.dataType = (Token) arithExprP.dataType.clone();
				copyType(arithExpr, arithExprP);
				return true;
			} else
				return false;
		}
		return false;
	}
	private boolean exprP(Symbol arithExpr, Symbol exprP) {
		skipErrors("relOp", "exprP");
		if (isFirst("relOp")) {
			Symbol arithExprP = new Symbol();
			if (match("relOp") && arithExpr(arithExprP)) {
				printGrammar("exprP            -> relOp arithExpr");
				Symbol tmp = smActions.compDateTypeNum(arithExpr, arithExprP);
				copyType(exprP, tmp);
				//exprP.dataType = (Token).dataType.clone();
				return true;
			} else
				return false;
		} else if (isFollow("exprP")) {
			printGrammar("exprP            -> EPSILON");
			//exprP.dataType = (Token) arithExpr.dataType.clone();
			copyType(exprP, arithExpr);
			return true;
		}
		return false;
	}
	private boolean statement() {
		skipErrors("statement", "");
		Symbol expr = new Symbol();
		if (isFirst("if")) {
			if (match("if") && match("(") && expr(expr) && match(")") && match("then") && statBlock() && match("else") && statBlock() && match(";")) {
				printGrammar("statement        -> if ( expr ) then statBlock else statBlock ;");
				return true;
			} else
				return false;
		} else if (isFirst("while")) {
			if (match("while") && match("(") && expr(expr) && match(")") && match("do") && statBlock() && match(";")) {
				printGrammar("statement        -> while ( expr ) do statBlock ;");
				return true;
			} else
				return false;
		} else if (isFirst("read")) {
			if (match("read") && match("(") && variable() && match(")") && match(";")) {
				printGrammar("statement        -> read ( variable ) ;");
				return true;
			} else
				return false;
		} else if (isFirst("return")) {
			if (match("return") && match("(") && expr(expr) && match(")") && match(";")) {
				printGrammar("statement        -> return ( expr ) ;");
				return true;
			} else
				return false;
		} else if (isFirst("write")) {
			if (match("write") && match("(") && expr(expr) && match(")") && match(";")) {
				printGrammar("statement        -> write ( expr ) ;");
				return true;
			} else
				return false;
		} else if (isFirst("variable")) {
			if (variable() && match("assignOp")) {
				Symbol tmp = (Symbol) curVar.clone();
				copyType(tmp, curVar);
				if (expr(expr) && match(";")) {
					printGrammar("statement        -> variable assignOp expr ;");
					smActions.compDateType(tmp, expr);
					return true;
				} else
					return false;
			} else
				return false;
		}
		return false;
	}
	private boolean fParamsTail() {
		skipErrors(",", "");
		if (isFirst(",")) {
			if (match(",") && type() && matchEx("id", SYMBOLTYPE.PARAMETER) && arraySizeList()) {
				printGrammar("fParamsTail      -> , type id arraySizeList");
				return true;
			} else
				return false;
		}
		return false;
	}
	private boolean term(Symbol term) {
		skipErrors("factor", "");
		if (isFirst("factor")) {
			Symbol factor = new Symbol();
			Symbol termP = new Symbol();
			if (factor(factor) && termP(factor, termP)) {
				printGrammar("term             -> factor termP");
				//term.dataType = (Token) termP.dataType.clone();
				copyType(term, termP);
				return true;
			} else
				return false;
		}
		return false;
	}
	private boolean arithExprP(Symbol term, Symbol arithExprP) {
		skipErrors("addOp", "arithExprP");
		if (isFirst("addOp")) {
			Symbol arithExprPP = new Symbol();
			Symbol termP = new Symbol();
			if (match("addOp") && term(termP) && arithExprP(termP, arithExprPP)) {
				printGrammar("arithExprP       -> addOp term arithExprP");
				//arithExprP.dataType = (Token)smActions.compDateTypeNum(term, arithExprPP).dataType.clone();
				Symbol tmp = smActions.compDateTypeNum(term, arithExprPP);
				copyType(arithExprP, tmp);
				return true;
			} else
				return false;
		} else if (isFollow("arithExprP")) {
			printGrammar("arithExprP       -> EPSILON");
			//arithExprP.dataType = (Token)term.dataType.clone();
			copyType(arithExprP, term);
			return true;
		}
		return false;
	}
	private boolean statBlock() {
		skipErrors("statBlock", "statBlock");
		if (isFirst("{")) {
			if (match("{") && statementList() && match("}")) {
				printGrammar("statBlock        -> { statementList }");
				return true;
			} else
				return false;
		} else if (isFirst("statBlock")) {
			if (statement()) {
				printGrammar("statBlock        -> statement");
				return true;
			} else
				return false;
		} else if (isFollow("statBlock")) {
			printGrammar("statBlock        -> EPSILON");
			return true;
		}
		return false;
	}
	private boolean variable() {
		skipErrors("id", "");
		if (isFirst("id")) {
			if (matchEx("id", SYMBOLTYPE.CHKVAR) && indiceList() && variableP()) {
				printGrammar("variable         -> id indiceList variableP");
				return true;
			} else
				return false;
		}
		return false;
	}
	private boolean termP(Symbol factor, Symbol termP) {
		skipErrors("multOp", "termP");
		if (isFirst("multOp")) {
			Symbol factorP = new Symbol();
			Symbol termPP = new Symbol();
			if (match("multOp") && factor(factorP) && termP(factorP, termPP)) {
				printGrammar("termP            -> multOp factor termP");
				//termP.dataType = (Token)smActions.compDateTypeNum(factor, termPP).dataType.clone();
				Symbol tmp = smActions.compDateTypeNum(factor, termPP);
				copyType(termP, tmp);
				return true;
			} else
				return false;
		} else if (isFollow("termP")) {
			printGrammar("termP            -> EPSILON");
			//termP.dataType = (Token)factor.dataType.clone();
			copyType(termP, factor);
			return true;
		}
		return false;
	}
	private boolean factor(Symbol factor) {
		skipErrors("factor", "");
		if (isFirst("(")) {
			Symbol expr = new Symbol();
			if (match("(") && expr(expr) && match(")")) {
				printGrammar("factor           -> ( expr )");
				//factor.dataType = (Token) expr.dataType.clone();
				copyType(factor, expr);
				return true;
			} else
				return false;
		} else if (isFirst("id")) {			
			Symbol factorPP = new Symbol();
			if (matchEx("id", SYMBOLTYPE.CHKVAR) && factorPP(curVar, factorPP)) {
				printGrammar("factor           -> id factorPP");
				//factor.dataType = (Token)factorPP.dataType.clone();
				copyType(factor, factorPP);
				return true;
			} else
				return false;
		} else if (isFirst("num")) {
			if (match("num")) {
				printGrammar("factor           -> num");
				
				// get the type of 'num'
				factor.tk = (Token)preToken.clone();
				factor.dataType = (Token)preToken.clone();
				if (preToken.type == StateMachineDriver.TOKEN_TYPE_INT) {
					factor.dataType.token = "integer";
				} else if (preToken.type == StateMachineDriver.TOKEN_TYPE_FLOAT) {
					factor.dataType.token = "real";
				} 
				return true;
			} else
				return false;
		} else if (isFirst("not")) {
			if (match("not") && factor(factor)) {
				printGrammar("factor           -> not factor");
				return true;
			} else
				return false;
		} else if (isFirst("sign")) {
			if (sign() && factor(factor)) {
				printGrammar("factor           -> sign factor");
				return true;
			} else
				return false;
		}
		return false;
	}
	private boolean factorPP(Symbol s, Symbol factorPP) {
		skipErrors("factorPP", "factorPP");
		if (isFirst("(")) {
			ArrayList<Symbol> tmpParams = smActions.varFuncParams;
			smActions.varFuncParams = new ArrayList<Symbol>();
			Symbol tmp = (Symbol) curVar.clone();
			copyType(tmp,  curVar);
			if (match("(") && aParams() && match(")")) {
				printGrammar("factorPP         -> ( aParams )");
				// check the parameters
				smActions.ifValidFuncParamType(tmp);
				s.dataType = (Token)tmp.dataType.clone();		// reset data type
				smActions.varFuncParams = tmpParams;
				//factorPP.dataType = (Token)s.dataType.clone();
				copyType(factorPP, s);
				return true;
			} else
				return false;
		} else if (isFirst("factorPP")) {
			Symbol factorP = new Symbol();
			if (indiceList() && factorP(s, factorP)) {
				printGrammar("factorPP         -> indiceList factorP");
				//factorPP.dataType = (Token)factorP.dataType.clone();
				copyType(factorPP, factorP);
				return true;
			} else
				return false;
		} else if (isFollow("factorPP")) {
			printGrammar("factorPP         -> EPSILON");
			//factorPP.dataType = (Token)s.dataType.clone();
			copyType(factorPP, s);
			return true;
		}
		return false;
	}
	private boolean factorP(Symbol s, Symbol factorP) {
		skipErrors(".", "factorP");
		if (isFirst(".")) {
			Symbol factorPP = new Symbol();
			if (match(".") && matchEx("id", SYMBOLTYPE.CHKMEMBER) && factorPP(curVar, factorPP)) {
				printGrammar("factorP          -> . id factorPP");
				//factorP.dataType = (Token)factorPP.dataType.clone();
				copyType(factorP, factorPP);
				return true;
			} else
				return false;
		} else if (isFollow("factorP")) {
			printGrammar("factorP          -> EPSILON");
			//factorP.dataType = (Token)s.dataType.clone();
			copyType(factorP, s);
			return true;
		}
		return false;
	}
	private boolean sign() {
		skipErrors("sign", "");
		if (isFirst("+")) {
			if (match("+")) {
				printGrammar("sign             -> +");
				return true;
			} else
				return false;
		} else if (isFirst("-")) {
			if (match("-")) {
				printGrammar("sign             -> -");
				return true;
			} else
				return false;
		}
		return false;
	}
	private boolean aParams() {
		skipErrors("expr", "aParams");
		if (isFirst("expr")) {
			Symbol expr = new Symbol();
			if (expr(expr)) {
				// store the parameter
				smActions.varFuncParams.add(expr);
				if (aParamsTailList()) {
					printGrammar("aParams          -> expr aParamsTailList");
					return true;
				}
			}
		} else if (isFollow("aParams")) {
			printGrammar("aParams          -> EPSILON");
			return true;
		}
		return false;
	}
	private boolean aParamsTailList() {
		skipErrors("aParamsTail", "aParamsTailList");
		if (isFirst("aParamsTail")) {
			if (aParamsTail() && aParamsTailList()) {
				printGrammar("aParamsTailList  -> aParamsTail aParamsTailList");
				return true;
			} else
				return false;
		} else if (isFollow("aParamsTailList")) {
			printGrammar("aParamsTailList  -> EPSILON");
			return true;
		}
		return false;
	}
	private boolean aParamsTail() {
		skipErrors(",", "");
		if (isFirst(",")) {
			Symbol expr = new Symbol();
			if (match(",") && expr(expr)) {
				printGrammar("aParamsTail      -> , expr");
				smActions.varFuncParams.add(expr);
				return true;
			} else
				return false;
		}
		return false;
	}

	/*

varDeclList -> varDecl varDeclList  | EPSILON
varDecl -> type id arraySizeList ; 
arraySizeList -> arraySize arraySizeList | EPSILON
arraySize -> [ int ] 
type -> integer | real | id

	 */
//	private boolean varDeclList() {
//		skipErrors("varDecl", "varDeclList");
//		if (isFirst("varDecl")) {
//			if (varDecl() && varDeclList()) {
//				printGrammar("varDeclList -> varDecl varDeclList");
//				return true;
//			} else
//				return false;
//		} else if (isFollow("varDeclList")) {
//			printGrammar("varDeclList -> EPSILON");
//			return true;
//		}
//		return false;
//	}
//	
//	private boolean varDecl() {
//		skipErrors("type", "");
//		if (isFirst("type")) {
//			if (type() && match("id") && arraySizeList() && match(";")) {
//				printGrammar("varDecl -> type id arraySizeList ;");
//				return true;
//			} else
//				return false;
//		}
//		return false;
//	}
	private boolean arraySizeList() {
		skipErrors("arraySize", "arraySizeList");
		if (isFirst("arraySize")) {
			if (arraySize() && arraySizeList()) {
				printGrammar("arraySizeList    -> arraySize arraySizeList");
				return true;
			} else
				return false;
		} else if (isFollow("arraySizeList")) {
			printGrammar("arraySizeList    -> EPSILON");
			smActions.newVarible(curVar);
			return true;
		}
		return false;
	}
	
	private boolean arraySize() {
		skipErrors("[", "");
		if (isFirst("[")) {
			if (match("[") && matchEx("int", SYMBOLTYPE.ARRAYSIZE) && match("]")) {
				printGrammar("arraySize        -> [ int ]");
				return true;
			} else
				return false;
		}
		return false;
	}
	
	private boolean type() {
		skipErrors("type", "");
		if (isFirst("integer")) {
			if (match("integer")) {
				printGrammar("type             -> integer");
				return true;
			} else
				return false;
		} else if (isFirst("real")) {
			if (match("real")) {
				printGrammar("type             -> real");
				return true;
			} else
				return false;
		} else if (isFirst("id")) {
			if (matchEx("id", SYMBOLTYPE.CHKTYPE)) {
				printGrammar("type             -> id");
				return true;
			} else
				return false;
		}
		return false;
	}
}
