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

public class IndexConstructor {
	// filter options:
	public static boolean OP_NONUMBERS = false;
	public static boolean OP_CASEFOLDING = true;  // used by the tokenizer
	public static boolean OP_30STOPWORDS = true;
	public static boolean OP_150STOPWORDS = true;
	public static boolean OP_STEMMING = false;	
	
	private StopWords stopWords = new StopWords();

	public int buildInvertedIndex() {
		String path = System.getProperty("user.dir") + "\\input\\";
		
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
			
			SPIMI.spimiInvertOneToken(tk, tokenizer.curDocID);

			tk = tokenizer.nextToken();
		}

		return 0;
	}
}
