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

public class InvertedIndex {
	public static final int POSTINGSLIST_INIT_SIZE = 30; 
	public static final int SIZE_OF_POINTER = 4;
	public static final int SIZE_OF_LONG = 4;
	
	//public byte[] term;	// the pointer of the term is stored in the hashmap key
							// e.g. HashMap<byte[], Index>
	
	public int docFreq;  	// the actual length of postings 
	
	public long[] postings = new long[POSTINGSLIST_INIT_SIZE];
}
