import java.util.Date;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;


import parser.InvertedIndex;
import parser.IndexConstructor;
import parser.Tokenizer;
import retrieval.InfoRetrieval;
 
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

public class LabAss1 {

	private static void showCurrentTime() {
		Date date = new Date();
		System.out.println(date.toString());
	}
	
	public static void main(String[] args) {
		// create and initialize the logger
		try {
			SysLogger.init();
		} catch (Exception e) {
			System.out.println("Make sure there is a directory named 'logs' under the root of program.");
		}
		
		// show greetings
		System.out.println("Welcome to COMP 6791 Lab Assignment 1");
		System.out.println("Developed by Yuan Tao.\n");
//		System.out.println("Press any key to begin...\n");
		
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			
//		}
	
		showCurrentTime();
		IndexConstructor ic = new IndexConstructor();
		ic.buildInvertedIndex();
		showCurrentTime();
		
		InfoRetrieval ir = new InfoRetrieval();
		ir.init();
		String[] s = new String[3];
		s[0] = "largely";
		s[1] = "last";
		s[2] = "large";
		System.out.println(ir.search(s));
		
//		
//		System.out.println("\nThe program ends successfully!");
//		String s1 = "12345";
//		String s2 = "33345";
//				
//		Hashtable<UnsignedBytes[], Integer> htIndex = new Hashtable<UnsignedBytes[], Integer>();
//		
//		htIndex.put(new UnsignedBytes(), 111);
//		htIndex.put(s2.getBytes(), 222);
//		
//		SortedSet<UnsignedBytes[]> keys = new TreeSet<UnsignedBytes[]>(htIndex.keySet());
//		
//		for (UnsignedBytes[] key: keys) {
//			Integer idx = htIndex.get(key);
//			
//			System.out.println("" +  idx);
//		}
		
//		Mergesort sorter = new Mergesort();
//		byte[][] a = new byte[3][];
//		a[0] = (new String("12345")).getBytes();
//		a[1] = (new String("12345")).getBytes();
//		a[2] = (new String("11345")).getBytes();
//		int ret = sorter.compare(a[1], a[2]);
//		System.out.println(":" + ret);
//		System.out.println("" + a[0]);
//		System.out.println("" + a[1]);
//		System.out.println("" + a[2]);
//		
//		System.out.println("" + (new String(a[0])) + (new String(a[1])) + (new String(a[2])));
//	    sorter.sort(a);	    
//		System.out.println("" + (new String(a[0])) + (new String(a[1])) + (new String(a[2])));
	}

}
