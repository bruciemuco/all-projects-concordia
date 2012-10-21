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

package retrieval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.crypto.spec.PSource;

import parser.InvertedIndex;
import parser.Stemmer;

import utils.ByteArrayWrapper;
import utils.SysLogger;

public class InfoRetrieval {
	// array of the last term and the file that has the term
	// "null,c:\\inverted-index-0"		// first one 
	// "term,c:\\inverted-index-0"
	public static ArrayList<String> arrTerm2File = new ArrayList<String>();
	
	// array of the last docID and the file that has the docID
	// "3008,c:\\inverted-index-0"
	public static ArrayList<String> arrDocID2File = new ArrayList<String>();
	
	// inverted index in memory for the first file
	// because the memory size only allowed to load one file
	// we keep the data of first file in memory
	private HashMap<ByteArrayWrapper, InvertedIndex> mapDic = new HashMap<ByteArrayWrapper, InvertedIndex>();
	
	public int init() {
		// load the inverted index into memory from the first file
		String[] tmp = arrTerm2File.get(0).split(","); 
		try {									
			BufferedReader in = new BufferedReader(new FileReader(tmp[1]));
			while(true) {
				String buf = in.readLine();
				if (buf == null) {
					in.close();
					return -1;
				}
				tmp = buf.split(",");
				ByteArrayWrapper term = new ByteArrayWrapper(tmp[0].getBytes());
				InvertedIndex idx = new InvertedIndex();
				int cnt = Integer.parseInt(tmp[1]);				
				long[] postings = new long[cnt];
				
				idx.docFreq = cnt;
				for (int j = 2; j < tmp.length; j++) {
					postings[j - 2] = Long.parseLong(tmp[j]);
				}
				idx.postings = postings;

				mapDic.put(term, idx);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return -1;
		}		
	}
	
	public InvertedIndex getPostingsFromFiles(String term) {
		// find the filename that has the term
		String filename = null;
		for (int i = 1; i < arrTerm2File.size(); i++) {		// start from the second file
			String[] tmp = arrTerm2File.get(i).split(","); 
			if (tmp[0].compareTo(term) <= 0) {
				filename = tmp[1];
				break;
			}
		}
		
		// do not find any file that contains the term
		if (filename == null) {
			return null;
		}
		
		// load the inverted index file
		try {									
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while (true) {
				String buf = in.readLine();
				if (buf == null) {
					return null;
				}
				String[] tmp = buf.split(",");
				String t = new String(tmp[0].getBytes());
				
				if (!t.equals(term)) {
					continue;
				}
				
				// find the term, return the postings
				InvertedIndex idx = new InvertedIndex();
				int cnt = Integer.parseInt(tmp[1]);				
				long[] postings = new long[cnt];
				
				idx.docFreq = cnt;
				for (int j = 2; j < tmp.length; j++) {
					postings[j - 2] = Long.parseLong(tmp[j]);
				}
				idx.postings = postings;
				return idx;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return null;
		}
	}
	
	// get the intersection of postings list
	private ArrayList<Long> findIntersectionOfDocID(InvertedIndex[] arrIdx) {
		// first find the smallest docFreq one
		int k = 0;
		for (int i = 1; i < arrIdx.length; i++) {
			if (arrIdx[k].docFreq > arrIdx[i].docFreq) {
				k = i;
			}
		}
		
		ArrayList<Long> ret = new ArrayList<Long>();
		long[] s = arrIdx[k].postings;
		for (int i = 0; i < s.length; i++) {
			int findIt = 0;
			for (int j = 0; j < arrIdx.length; j++) {
				if (k == j) {
					continue;
				}
				for (int j2 = 0; j2 < arrIdx[j].postings.length; j2++) {
					if (s[i] == arrIdx[j].postings[j2]) {
						findIt++;
						break;
					}
				}
			}
			if (findIt == arrIdx.length - 1) {
				ret.add(s[i]);
			}
		}
		
		for (Long l : ret) {
			SysLogger.info(l.toString() + " ");
		}
		return ret;
	}
	
	private StringBuffer getDocData(ArrayList<Long> docIDs) {
		StringBuffer sbRet = new StringBuffer();
		
		return sbRet;
	}
	
	public StringBuffer search(String[] terms) {
		StringBuffer sbRet = new StringBuffer();
		InvertedIndex[] arrIdx = new InvertedIndex[terms.length];
		Stemmer s = new Stemmer();
		
		// get all postings
		for (int i = 0; i < terms.length; i++) {
			// stemmer each term
			String stemmedTerm;
			s.add(terms[i].toCharArray(), terms[i].length());
			s.stem();
			stemmedTerm = s.toString();

			// look up the term in memory first
			arrIdx[i] = mapDic.get(new ByteArrayWrapper(stemmedTerm.getBytes()));
			
			// try to find it in files
			if (arrIdx[i] == null) {
				arrIdx[i] = getPostingsFromFiles(stemmedTerm);
				if (arrIdx[i] == null) {
					sbRet.append("Not found any result for the term: " + stemmedTerm);
					return sbRet;
				}
			}
		}
		
		// compare the postings, find the intersection of the docID
		ArrayList<Long> docIDs = findIntersectionOfDocID(arrIdx);
		
		if (docIDs.size() < 1) {
			sbRet.append("Not found any intersection for the terms");
			return sbRet;
		}
		
		// get doc from files		
		return getDocData(docIDs);
	}
	
}
