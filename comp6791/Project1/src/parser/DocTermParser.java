package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import retrieval.InfoRetrieval;

import utils.SysLogger;

public class DocTermParser {
	private StopWords stopWords = new StopWords();
	private long preDocID = 1;
	private HashSet<String> terms = new HashSet<String>();

	public int getAllTermsForAllDocs(String inputPath, InfoRetrieval ir) {
		// create directory traversal thread
		DirTraversal.start(inputPath);
		
		// wait for this thread until it has been started
		try {
			Tokenizer.semNextFileDone.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// create a Tokenizer
		Tokenizer tokenizer = new Tokenizer();
		
		if (tokenizer.init(inputPath) != 0) {
			return -1;
		}
		
		Token tk = tokenizer.nextToken();
		Stemmer s = new Stemmer();
		
		// iteratively load the file and parse tokens
		while (tk != null) {	
			if (IndexConstructor.OP_NONUMBERS && tk.type == Token.TK_TYPE_NUM) {
				// filter numbers
				tk = tokenizer.nextToken();
				continue;
			}
			
			if (tk.type == Token.TK_TYPE_STRING) {
				// remove stop words
				if (IndexConstructor.OP_30STOPWORDS && stopWords .ifStopWord(tk.token)) {
					tk = tokenizer.nextToken();
					continue;
				}
				if (IndexConstructor.OP_150STOPWORDS && stopWords.if150StopWord(tk.token)) {
					tk = tokenizer.nextToken();
					continue;
				}

				if (IndexConstructor.OP_STEMMING) {
					// stemmer the tokens with Porter Stemmer Algorithm
					s.add(tk.token.toCharArray(), tk.token.length());
					s.stem();
					tk.token = s.toString();
				}				
			}
			
			// TODO:
			//SysLogger.info("docID: " + tokenizer.curDocID + ", term: " + tk.token);
			if (preDocID != tokenizer.curDocID) {
				// now it is parsing a new doc
				calcVectorsForADoc(preDocID, ir);
				
				preDocID = tokenizer.curDocID;
			}
			terms.add(tk.token);
			
			tk = tokenizer.nextToken();
		}

		// the end of the last doc
		calcVectorsForADoc(preDocID, ir);
		
		return 0;
	}
	
	public void calcVectorsForADoc(long docID, InfoRetrieval ir) {
		// e.g. 
//		for (String t : terms) {
//			System.out.println("docID: " + docID + ", token: " 
//					+ t + " , df: " + ir.getDF(t));
//		}

		
		// clear the terms of this doc for calculating next doc
		terms.clear();
	}
	
	
}
