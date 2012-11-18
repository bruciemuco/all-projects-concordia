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

import java.util.Date;
import java.util.HashMap;

import retrieval.InfoRetrieval;
import retrieval.OkapiBM25;
import utils.ByteArrayWrapper;

public class IndexConstructor {
	// filter options:
	public static boolean OP_NONUMBERS = false;
	public static boolean OP_CASEFOLDING = true;  // used by the tokenizer
	public static boolean OP_30STOPWORDS = true;
	public static boolean OP_150STOPWORDS = false;
	public static boolean OP_STEMMING = true;	
	
	private StopWords stopWords = new StopWords();
	private SPIMI spimi = null;

//	public void getMemorySize() {
//		long heapSize = Runtime.getRuntime().totalMemory();
//		long heapMaxSize = Runtime.getRuntime().maxMemory();
//		long heapFreeSize = Runtime.getRuntime().freeMemory();
//		
//		System.out.println("" + heapSize + ", " + heapMaxSize + ", " + heapFreeSize);
//	}
	
	public int buildInvertedIndex() {
		long preDocLen = 0;		// the length of doc
		long preDocID = -1;

		String path = System.getProperty("user.dir") + "\\input\\";
		
		// search result:
		InfoRetrieval.filenameResult = System.getProperty("user.dir") + "\\output\\search-results.txt";
		
		// create SPIMI object
		spimi = new SPIMI(System.getProperty("user.dir") + "\\output\\");
		
		// create a Tokenizer
		Tokenizer tokenizer = new Tokenizer();
		
		if (tokenizer.init(path) != 0) {
			return -1;
		}
		
		Token tk = tokenizer.nextToken();
		Stemmer s = new Stemmer();
		
		// iteratively load the file and parse tokens
		while (tk != null) {	
			if (OP_NONUMBERS && tk.type == Token.TK_TYPE_NUM) {
				// filter numbers
				tk = tokenizer.nextToken();
				continue;
			}
			
			if (tk.type == Token.TK_TYPE_STRING) {
				// remove stop words
				if (OP_30STOPWORDS && stopWords .ifStopWord(tk.token)) {
					tk = tokenizer.nextToken();
					continue;
				}
				if (OP_150STOPWORDS && stopWords.if150StopWord(tk.token)) {
					tk = tokenizer.nextToken();
					continue;
				}

				if (OP_STEMMING) {
					// stemmer the tokens with Porter Stemmer Algorithm
					s.add(tk.token.toCharArray(), tk.token.length());
					s.stem();
					tk.token = s.toString();
				}				
			}
			
			spimi.spimiInvertOneToken(tk, tokenizer.curDocID);
			
			// update doc length
			if (preDocID != tokenizer.curDocID) {
				if (preDocID != -1) {
					OkapiBM25.mapLenOfDocs.put(preDocID, preDocLen);
				}
				preDocID = tokenizer.curDocID;
				preDocLen = 0;
			}
			preDocLen++;

			// calculate and store term frequency
			OkapiBM25.calcTF(tk.token, tokenizer.curDocID);
			
			tk = tokenizer.nextToken();
		}
		
		// update length for the last doc
		if (preDocID != -1) {
			OkapiBM25.mapLenOfDocs.put(preDocID, preDocLen);
		}
		
		// 
		if (spimi.memSizeUsed > 0) {
			spimi.writeSPIMITempFile();
		}
		
		// merge the temporary files of sorted inverted index
		Date date = new Date();
		System.out.println("Start to merge the sorted SPIMI temporary files...\n" + date.toString());
		System.out.println();

		spimi.mergeSortedFiles();
		
		return 0;
	}

}
