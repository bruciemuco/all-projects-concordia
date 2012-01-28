/*
 * COMP6421 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2011-11-16 01:13:08 -0500 (Wed, 16 Nov 2011) $
 * $Rev: 60 $
 * $HeadURL: https://comp6471.googlecode.com/svn/Project6/src/utils/SysLogger.java $
 * 
 */

package LexicalAnalyzer;

import utils.SysLogger;

public class StateMachineDriver {
	private static final int ROW_SIZE = 50;
	private static final int COL_SIZE = 255;
	
	public static final int INIT_STATE = 2;	
	
	public static final int SN = 0;		// normal state
	public static final int SF = 1;		// final state
	public static final int SE = 2;		// error state
	
	// return value of state
	public static final int NE = 0;		// state machine goes to error state
	public static final int E = -1;		// fatal error. state machine goes to wrong state.
	public static final int F = -2;		// no back up
	public static final int B = -3;		// back up
	
	private static int stateInputTable[][] = {					// state table for displaying
		// first column defines if the row is final state row: 
		// 0. not a final state row. 1. final state. 2. error state.
		// final state row: 0. no back up, 1. back up
		
		// first row defines the input chars
		{0, ' ', 'a', 'A', '0', '1', '_', '.' },
		
		// second row defines the error states
		{SE,  },
		
		// [a..z][A..Z] ([a..z][A..Z] | [0..9] | _)*
		{SN,  2,   3,   3,   1,   1,   1,   1 },
		{SN,  4,   3,   3,   3,   3,   3,   4 },
		{SF,  B,   E,   E,   E,   E,   E,   F },
	};
	
	private static int stateTable[][] = new int[ROW_SIZE][COL_SIZE];		// the table used to look up state
	
	public static int init() {
		int row = stateTable.length;
		int col = stateTable[0].length;

		// set initial value to all the states of stateTable
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				stateTable[i][j] = 1;
			}
		}

		// convert the display table to state table by setting state to the right index
		// the column index of stateTable is the first row of stateInputTable
		row = stateInputTable.length;
		for (int i = 1; i < row; i++) {
			col = stateInputTable[i].length;
			for (int j = 0; j < col; j++) {
				stateTable[i][stateInputTable[0][j]] = stateInputTable[i][j];

				// auto add states for consecutive chars 
				char ch = (char) stateInputTable[0][j];
				
				if (ch == 'a') {
					for (int k = 'a'; k <= 'z'; k++) {
						stateTable[i][k] = stateInputTable[i][j];
					}
				}
				if (ch == 'A') {
					for (int k = 'A'; k <= 'Z'; k++) {
						stateTable[i][k] = stateInputTable[i][j];
					}
				}
				if (ch == '1') {
					for (int k = '1'; k <= '9'; k++) {
						stateTable[i][k] = stateInputTable[i][j];
					}
				}
				if (ch == ' ') {
					stateTable[i]['\t'] = stateInputTable[i][j];
					
					// CRLF is treated as space
					stateTable[i]['\r'] = stateInputTable[i][j];
					stateTable[i]['\n'] = stateInputTable[i][j];
				}
			}
		}
		
		//dump();
		
		return 0;
	}

	private static void dump() {
		// test
		System.out.print(stateTable[1][' '] + " ");
		System.out.print(stateTable[1]['\t'] + " ");
		System.out.print(stateTable[1]['a'] + " ");
		System.out.print(stateTable[1]['d'] + " ");
		System.out.print(stateTable[1]['A'] + " ");
		System.out.print(stateTable[1]['D'] + " ");
		System.out.print(stateTable[1]['0'] + " ");
		System.out.print(stateTable[1]['1'] + " ");
		System.out.print(stateTable[1]['9'] + " ");
		System.out.print(stateTable[1]['_'] + " \n");
		System.out.print(stateTable[2]['0'] + " ");
		System.out.print(stateTable[2]['1'] + " ");
		System.out.print(stateTable[2]['a'] + " ");
		System.out.print(stateTable[2]['_'] + " \n");
		System.out.print(stateTable[3]['0'] + " ");
		System.out.print(stateTable[3]['1'] + " ");
		System.out.print(stateTable[3]['a'] + " ");
		System.out.print(stateTable[3]['_'] + " \n");
	}
	
	public static int nextState(int curState, char ch) {
		if (curState < 1 || curState >= ROW_SIZE || ch <= 0 || ch >= COL_SIZE) {
			SysLogger.err("Unkown state or ch: " + curState + ", " + ch);
			return E;
		}
		
		int st = stateTable[curState][ch];
		
		if (st < 1 || st >= ROW_SIZE) {
			SysLogger.err("Unkown state: " + curState);
			return E;
		}

		if (stateTable[st][0] == SF) {
			return stateTable[st][ch];
		}
		if (stateTable[st][0] == SE) {
			return NE;
		}
		return stateTable[curState][ch];
	}

}
