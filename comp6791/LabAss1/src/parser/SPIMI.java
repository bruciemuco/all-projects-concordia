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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.print.DocFlavor;

import utils.ByteArrayWrapper;
import utils.Mergesort;
import utils.SysLogger;

public class SPIMI {
	public static final long MAX_MEM_SIZE = 8 * 1024 * 1024;

	private String curDocID = "";
	public long memSizeUsed = 0;
	
	private String filePath = "";
	private String fileNamePrefix = "spimi-temp-";
	private int fileCount = 0;
	
	// In order to use byte[] as hashmap key, here we wrap the byte[] in ByteArrayWrapper class  
	private HashMap<ByteArrayWrapper, InvertedIndex> mapIndex = new HashMap<ByteArrayWrapper, InvertedIndex>();
	//private HashMap<String, Index> htIndex = new HashMap<String, Index>();
	
	private Mergesort sorter = new Mergesort();
	
	public SPIMI(String outputPath) {
		filePath = outputPath;
	}
	
	// When writing the sorted inverted index to file, the program buffers the data 
	// to be written. Once the buffer reaches a size, or it gets the last element 
	// of the index, it starts to write the buffer to the file.
	// Before buffering, it sorts first.
	public int store2File() {
		try {
			BufferedWriter out = new BufferedWriter(
					new FileWriter(filePath + fileNamePrefix + fileCount));
			
			// write the sorted inverted index to file
			// first sort the dictionary
			//SortedSet<ByteArrayWrapper> keys = new TreeSet<ByteArrayWrapper>(mapIndex.keySet());
			ByteArrayWrapper[] keys = sortTerms();
			
			int count = 0;
			StringBuffer strToWrite = new StringBuffer();
			
			for (ByteArrayWrapper key: keys) {
				InvertedIndex idx = mapIndex.get(key);
				
				StringBuffer postings = new StringBuffer();
				
				// then sort the postings
				Arrays.sort(idx.postings, 0, idx.docFreq);				
				
				// store the postings to buffer				
				for (int i = 0; i < idx.docFreq; i++) {
					postings.append(idx.postings[i]);
					postings.append(",");
				}
				strToWrite.append(new String(key.data));
				strToWrite.append(",");
				strToWrite.append(idx.docFreq);
				strToWrite.append(",");
				strToWrite.append(postings);
				strToWrite.append("\n");
				count++;
				
				if (count == 1000) {
					out.write(strToWrite.toString(), 0, strToWrite.length());
					count = 0;
				}
			}
			
			if (count > 0) {
				out.write(strToWrite.toString(), 0, strToWrite.length());
			}
			out.close();
			
			// clear data
			mapIndex.clear();
			
			fileCount++;
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return -1;
		}
	}
	
	// add a DocID to the postings of the term
	private int addDocID2Postings(InvertedIndex idx, long docID) {
		int maxLen = idx.postings.length;
		int len = idx.docFreq;
		boolean ifFind = false;
		
		// check if it is full
		if (len != maxLen) {
			// add the docID (only merge, do not sort)
			for (int i = 0; i < len; i++) {
				if (docID == idx.postings[i]) {
					ifFind = true;
					break;
				}
			}			
			
		} else {
			// double the size
			long[] newPostings;
			newPostings = new long[len * 2];
			memSizeUsed += InvertedIndex.SIZE_OF_LONG * len;
			
			for (int i = 0; i < len; i++) {
				newPostings[i] = idx.postings[i];
				
				// merge at the same time
				if (!ifFind && docID == idx.postings[i]) {
					ifFind = true;
				}
			}
			idx.postings = newPostings;			
		}
		
		if (!ifFind) {
			idx.postings[len] = docID;
			idx.docFreq++;
		}
		
		return idx.docFreq;
	}
	
	// SPIMI algorithm for one token
	public int spimiInvertOneToken(Token tk, long docID) {
		InvertedIndex idx;
		int len = tk.token.length();		
		ByteArrayWrapper term = new ByteArrayWrapper(tk.token.getBytes());
		
		// check if the term has already in the dictionary
		idx = mapIndex.get(term);
		if (idx == null) {
			idx = new InvertedIndex();
			
			mapIndex.put(term, idx);
			
			// increase memory size that has been used.
			memSizeUsed += InvertedIndex.SIZE_OF_POINTER;		// hash key
			memSizeUsed += InvertedIndex.SIZE_OF_POINTER * 2;	// used by mergesort of hash key
			memSizeUsed += len;		// term length
			memSizeUsed += InvertedIndex.SIZE_OF_LONG * InvertedIndex.POSTINGSLIST_INIT_SIZE;
		}
				
		addDocID2Postings(idx, docID);		
		//SysLogger.info(tk.token + ", " + curMemSize + ", " + docID);
		
		if (memSizeUsed >= MAX_MEM_SIZE) {
			// create a new file to store the already sorted inverted index
			store2File();
			memSizeUsed = 0;			
		}		
		
		return 0;
	}
	
	// finally using SortedSet for sorting instead of mergesort
	public ByteArrayWrapper[] sortTerms() {
		// in order to use mergesort, we need copy the keys into an array first
		ByteArrayWrapper[] sortedTerms = new ByteArrayWrapper[mapIndex.size()];
		int i = 0;
		for (ByteArrayWrapper key : mapIndex.keySet()) {
			sortedTerms[i++] = key;
		}
		
//	    long startTime = System.currentTimeMillis();

		// around 55 milliseconds for 52980 terms
	    sorter.sort(sortedTerms);
	    
	    // around 88 milliseconds for 52980 terms
		//SortedSet<ByteArrayWrapper> keys = new TreeSet<ByteArrayWrapper>(mapIndex.keySet()); 
		
//	    long stopTime = System.currentTimeMillis();
//	    long elapsedTime = stopTime - startTime;

//		for (ByteArrayWrapper key: sortedTerms) {
//			//Index idx = htIndex.get(key);
//			
//			System.out.println((new String(key.data)));
//		}
//	    System.out.println("Mergesort " + elapsedTime);
//	    System.out.println("count: " + mapIndex.size());
	    return sortedTerms;
	}
	
	// last step of SPIMI algorithm: merge the sorted files, generate new files which
	// have the sorted inverted index.
	public int mergeSortedFiles() {
		ArrayList<BufferedReader> lstIn = new ArrayList<BufferedReader>();
		ArrayList<String> lstBuf = new ArrayList<String>();

		memSizeUsed = 0;
		mapIndex.clear();

		// open all the files
		for (int i = 0; i < fileCount; i++) {
			try {
				// read one line from each file
				BufferedReader in = new BufferedReader(new FileReader(filePath
						+ fileNamePrefix + i));
				lstIn.add(in);

			} catch (FileNotFoundException e) {
				SysLogger.err("File not found: " + filePath + fileNamePrefix
						+ i);
				return -1;
			} catch (Exception e) {
				e.printStackTrace();
				SysLogger.err(e.getMessage());
				return -1;
			}
		}

		// read one line from each file until the end of file
		while (true) {
			for (int i = 0; i < lstIn.size(); i++) {
				try {
					String buf = lstIn.get(i).readLine();
					
					if (buf == null) {	// end of file
						lstIn.remove(i);
						lstBuf.remove(i);
						i--;
						continue;
					}					
					lstBuf.add(buf);

				} catch (Exception e) {
					e.printStackTrace();
					SysLogger.err(e.getMessage());
					return -1;
				}
			}
			if (lstIn.size() < 1) {
				break;
			}
			
			// start to merge
			
		}
		
		return 0;
	}
}
