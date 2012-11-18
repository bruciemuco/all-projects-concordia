package parser;

import java.util.ArrayList;

public class StopWords {
	private ArrayList<String> stopWords = new ArrayList<String>();
	private ArrayList<String> stopWords150 = new ArrayList<String>();
	
	public StopWords() {
		// here is the stop words list got from http://nlp.stanford.edu/IR-book/html/htmledition/dropping-common-terms-stop-words-1.html#fig:stoplist
		stopWords.add("a");
		stopWords.add("an");
		stopWords.add("and");
		stopWords.add("are");
		stopWords.add("as");
		stopWords.add("at");
		stopWords.add("be");
		stopWords.add("by");
		stopWords.add("for");
		stopWords.add("from");
		
		stopWords.add("has");
		stopWords.add("he");
		stopWords.add("in");
		stopWords.add("is");
		stopWords.add("it");
		stopWords.add("its");
		stopWords.add("of");
		stopWords.add("on");
		stopWords.add("that");
		stopWords.add("the");
		
		stopWords.add("to");
		stopWords.add("was");
		stopWords.add("were");
		stopWords.add("will");
		stopWords.add("with");
	}
	
	public boolean ifStopWord(String word) {
		for (int i = 0; i < stopWords.size(); i++) {
			if (stopWords.get(i).equals(word)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean if150StopWord(String word) {
		for (int i = 0; i < stopWords150.size(); i++) {
			if (stopWords150.get(i).equals(word)) {
				return true;
			}
		}
		return false;
	}
}
