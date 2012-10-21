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
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.print.DocFlavor;

import retrieval.InfoRetrieval;

import utils.ByteArrayWrapper;
import utils.Mergesort;
import utils.SysLogger;

public class SPIMI {
	public static final long MAX_MEM_SIZE = 8 * 1024 * 1024;
	public static final int MAX_FILE_NUMBERS = 100;

	private String curDocID = "";
	public long memSizeUsed = 0;
	
	private String filePath = "";
	private String fileNamePrefix = "spimi-temp-";
	private int fileCount = 0;

	private String invertedIndexPrefix = "inverted-index-";
	private int invertedIndexFileCount = 0;
	
	// In order to use byte[] as hashmap key, here we wrap the byte[] in ByteArrayWrapper class  
	private HashMap<ByteArrayWrapper, InvertedIndex> mapUnsortedIndex = new HashMap<ByteArrayWrapper, InvertedIndex>();
	//private HashMap<String, Index> htIndex = new HashMap<String, Index>();
	
	private Mergesort sorter = new Mergesort();
	
	public SPIMI(String outputPath) {
		filePath = outputPath;
	}
	
	// When writing the sorted inverted index to file, the program buffers the data 
	// to be written. Once the buffer reaches a size, or it gets the last element 
	// of the index, it starts to write the buffer to the file.
	// Before buffering, it sorts first.
	public int writeSPIMITempFile() {
		if (fileCount > MAX_FILE_NUMBERS) {
			SysLogger.err("too many temp files");
			return -1;
		}
		
		try {
			BufferedWriter out = new BufferedWriter(
					new FileWriter(filePath + fileNamePrefix + fileCount));
			int count = 0;
			StringBuffer buf2File = new StringBuffer();
			
			// write the sorted inverted index to file
			// first sort the dictionary
			//SortedSet<ByteArrayWrapper> keys = new TreeSet<ByteArrayWrapper>(mapIndex.keySet());
			ByteArrayWrapper[] keys = sortTerms();

			for (ByteArrayWrapper key : keys) {
				InvertedIndex idx = (InvertedIndex) mapUnsortedIndex.get(key);

				buf2File.append(new String(key.data));
				buf2File.append(",");
				buf2File.append(idx.docFreq);
				buf2File.append(",");

				// then sort the postings
				Arrays.sort(idx.postings, 0, idx.docFreq);

				// store the postings to buffer
				for (int i = 0; i < idx.docFreq; i++) {
					buf2File.append(idx.postings[i]);
					buf2File.append(",");
				}
				buf2File.append("\n");
				count++;

				if (count == 1000) {
					out.write(buf2File.toString(), 0, buf2File.length());
					buf2File = new StringBuffer();
					count = 0;
				}
			}
			
			if (count > 0) {
				out.write(buf2File.toString(), 0, buf2File.length());
			}
			out.close();
			
			// clear data
			mapUnsortedIndex.clear();
			
			fileCount++;
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return -1;
		}
	}
	
	public int writeInvertedIndexFile(TreeMap map) {
		if (invertedIndexFileCount > MAX_FILE_NUMBERS) {
			SysLogger.err("too many files");
			return -1;
		}

		String filename = filePath + invertedIndexPrefix + invertedIndexFileCount;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			int count = 0;
			StringBuffer buf2File = new StringBuffer();
			
			// write the sorted inverted index to file
			for (ByteArrayWrapper key : ((TreeMap<ByteArrayWrapper, InvertedIndex>) map).keySet()) {
				InvertedIndex idx = (InvertedIndex) map.get(key);

				//SysLogger.info(new String(key.data));
				buf2File.append(new String(key.data));
				buf2File.append(",");
				buf2File.append(idx.docFreq);
				buf2File.append(",");

				// store the postings to buffer
				for (int i = 0; i < idx.docFreq; i++) {
					buf2File.append(idx.postings[i]);
					buf2File.append(",");
				}
				buf2File.append("\n");
				count++;

				if (count == 1000) {
					//SysLogger.info(new String(key.data));
					out.write(buf2File.toString(), 0, buf2File.length());
					buf2File = new StringBuffer();
					out.flush();
					count = 0;
				}
			}
			
			if (count > 0) {
				out.write(buf2File.toString(), 0, buf2File.length());
			}
			out.close();
			
			// clear data
			map.clear();			
			invertedIndexFileCount++;
			
			// record the last term with the filename for searching
			ByteArrayWrapper lastTerm = (ByteArrayWrapper) map.lastKey();
			InfoRetrieval.arrTerm2File.add((new String(lastTerm.data)) + "," + filename);

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
		idx = mapUnsortedIndex.get(term);
		if (idx == null) {
			idx = new InvertedIndex();
			
			mapUnsortedIndex.put(term, idx);
			
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
			writeSPIMITempFile();
			memSizeUsed = 0;			
		}		
		
		return 0;
	}
	
	// finally using SortedSet for sorting instead of mergesort
	public ByteArrayWrapper[] sortTerms() {
		// in order to use mergesort, we need copy the keys into an array first
		ByteArrayWrapper[] sortedTerms = new ByteArrayWrapper[mapUnsortedIndex.size()];
		int i = 0;
		for (ByteArrayWrapper key : mapUnsortedIndex.keySet()) {
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
		if (fileCount < 2) {
			// do not need to merge, just copy the file
			String filenameIn = filePath + fileNamePrefix + 0;
			String filenameOut = filePath + invertedIndexPrefix + 0;
			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(filenameIn));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filenameOut));
				byte[] buf = new byte[Tokenizer.FILEBUF_SIZE];
				int nRead = -1;
				while (true) {
					nRead = in.read(buf, 0, Tokenizer.FILEBUF_SIZE);
					if (nRead == -1) {
						in.close();
						out.close();
						break;
					}
					out.write(buf, 0, nRead);
				}
					
			} catch (Exception e) {
				e.printStackTrace();
				SysLogger.err(e.getMessage());
				return -1;
			}

			InfoRetrieval.arrTerm2File.add("null," + filenameOut);
			return 0; 
		}
		
		BufferedReader[] arrInFile = new BufferedReader[fileCount];

		// open all the files
		for (int i = 0; i < fileCount; i++) {
			try {
				// read one line from each file
				arrInFile[i] = new BufferedReader(new FileReader(filePath
						+ fileNamePrefix + i));

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
		
		TreeMap<ByteArrayWrapper, InvertedIndex> treeIndex = new TreeMap<ByteArrayWrapper, InvertedIndex>();
		ByteArrayWrapper[] arrTerm = new ByteArrayWrapper[fileCount];
		InvertedIndex[] arrPostings = new InvertedIndex[fileCount];
		memSizeUsed = 0;

		// keep reading data from SPIMI temp files
		while (true) {
			for (int i = 0; i < arrInFile.length; i++) {
				try {
					// read data from files if any of arrTerm is null
					if (arrTerm[i] == null && arrInFile[i] != null) {
						String buf = arrInFile[i].readLine();
						
						if (buf == null) {
							// end of file
							arrInFile[i].close();
							arrInFile[i] = null;
							continue;
						}

						arrTerm[i] = new ByteArrayWrapper(null);
						arrPostings[i] = new InvertedIndex();
						
						// parse the string of dictionary and postings
						String[] tmp = buf.split(",");
						arrTerm[i].data = tmp[0].getBytes();
						
						int cnt = Integer.parseInt(tmp[1]);
						arrPostings[i].docFreq = cnt;
						
						if (cnt > InvertedIndex.POSTINGSLIST_INIT_SIZE) {
							arrPostings[i].postings = new long[cnt];
						}
						for (int j = 2; j < tmp.length; j++) {
							arrPostings[i].postings[j - 2] = Long.parseLong(tmp[j]);
						}
						continue;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					SysLogger.err(e.getMessage());
					return -1;
				}
			}

			// start to merge
			// first find out the lexicographically smallest one
			int k = -1;
			for (int i = 0; i < arrInFile.length; i++) {
				if (arrTerm[i] == null) {
					continue;
				}
				k = i;
				break;
			}
			if (k == -1) {
				break; 		// end of all files.
			}
			
			for (int i = k + 1; i < arrInFile.length; i++) {
				if (arrTerm[i] == null) {
					continue;
				}
				if (sorter.compare(arrTerm[k].data, arrTerm[i].data) > 0) {
					k = i;
				}
			}
			
			// merge the smallest one to the tree
			add2Tree(treeIndex, arrTerm[k], arrPostings[k]);

			// reset the memory size used
			memSizeUsed += InvertedIndex.SIZE_OF_POINTER;		// map key
			memSizeUsed += arrTerm[k].data.length;			// term length
			memSizeUsed += InvertedIndex.SIZE_OF_LONG * arrPostings[k].docFreq;
			
			// reset term and postings
			arrTerm[k] = null;
			arrPostings[k] = null;
			
			// 
			if (memSizeUsed >= MAX_MEM_SIZE) {
				// write the result to a new file.
				writeInvertedIndexFile(treeIndex);
				memSizeUsed = 0;
				continue;
			}

		}
		
		if (memSizeUsed > 0) {
			// write the result to a new file.
			writeInvertedIndexFile(treeIndex);
			//SysLogger.info("memSize: " + memSizeUsed);
			
			memSizeUsed = 0;
		}
		
		return 0;
	}
	
	private void add2Tree(TreeMap<ByteArrayWrapper, InvertedIndex> t, 
			ByteArrayWrapper term, InvertedIndex idx) {
		//SysLogger.info(new String(term.data) + ", " + idx.docFreq);
		
		InvertedIndex oldIdx = (InvertedIndex) t.get(term);
		if (oldIdx == null) {
			t.put(term, idx);
			return;
		}
		
		// merge the postings
		TreeSet<Long> treePostings = new TreeSet<Long>();
		
		for (int i = 0; i < oldIdx.docFreq; i++) {
			treePostings.add(oldIdx.postings[i]);
		}
		for (int i = 0; i < idx.docFreq; i++) {
			treePostings.add(idx.postings[i]);
		}
		long[] newPostings = new long[treePostings.size()];
		int i = 0;
		for (Long key : treePostings) {
			newPostings[i++] = key;
		}
		oldIdx.docFreq = newPostings.length;
		oldIdx.postings = newPostings;
	}
}
