import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;


import parser.InvertedIndex;
import parser.IndexConstructor;
import parser.Tokenizer;
import retrieval.InfoRetrieval;
import retrieval.OkapiBM25;
 
import utils.ByteArrayWrapper;
import utils.Mergesort;
import utils.SysLogger;


/*
 * COMP6791 Lab Assignment 1
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

public class Project1 {

	public static void main(String[] args) {
		// create and initialize the logger
		try {
			SysLogger.init();
		} catch (Exception e) {
			System.out.println("Make sure there is a directory named 'logs' under the root of program.");
		}
		
		// show greetings
		System.out.println("COMP 6791 Project 1");
		System.out.println("Developed by Yuan Tao.\n");

		// build the inverted index
		Date date = new Date();
		System.out.println("Start to build the inverted index...\n" + date.toString());
		System.out.println();
		
		IndexConstructor ic = new IndexConstructor();
		if (ic.buildInvertedIndex() != 0) {
			return;
		}

		// load one inverted index file to memory
		date = new Date();
		System.out.println("Start to load the first inverted index file to memory...\n" + date.toString());
		System.out.println();
		
		InfoRetrieval ir = new InfoRetrieval();
		ir.init();

		date = new Date();
		System.out.println("Done.\n" + date.toString());
		System.out.println();
		
		// get user input
		String query = "";
		StringBuffer sbResult = null;
		
		//testTF();
		
		while (true) {
			try {
				System.out.println("\nPlease input your query (e.g. Oct 10) [type ! to exit program]:");

				Scanner scan = new Scanner(System.in);
				query = scan.nextLine();
				
				if (query.trim().length() < 1) {
					System.out.println("Invalid query.");
					continue;
				}
				
				if (query.equals("!")) {
					break;
				}
				System.out.println();
				
				sbResult = ir.search(query);
				if (sbResult.length() > 10000) {
					System.out.println(sbResult.toString().substring(0, 10000));
					System.out.println("\n\n...");
					System.out.println("[For all the results, please see the result file.]\n...\n\n");

					System.out.println(sbResult.toString().substring(sbResult.length() - 500));
				} else {
					System.out.println(sbResult.toString());
				}
			} catch (Exception e) {
				SysLogger.err("Scanner: invalid input: " + query);
			}
		}
		
	}
	
	static void testTF() {
		for (Long k : OkapiBM25.mapLenOfDocs.keySet()) {
			System.out.println(k.toString() + ": " + OkapiBM25.mapLenOfDocs.get(k).toString());
		}
		System.out.println("avgDocLen: " + OkapiBM25.avgDocLen);
		
		ByteArrayWrapper[] sortedTerms = new ByteArrayWrapper[OkapiBM25.mapTF.size()];
		int i = 0;
		for (ByteArrayWrapper key : OkapiBM25.mapTF.keySet()) {
			sortedTerms[i++] = key;
		}		
		Mergesort sorter = new Mergesort();
	    sorter.sort(sortedTerms);

		HashMap<Long, Long> map = null;
		for (ByteArrayWrapper term : sortedTerms) {
			map = OkapiBM25.mapTF.get(term);
			for (Long key : map.keySet()) {
				Long tf = map.get(key);
				System.out.println((new String(term.data)) + ", " + key.toString() + ", " + tf.toString());
			}
		}
	}

}
