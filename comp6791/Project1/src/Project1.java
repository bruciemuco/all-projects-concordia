import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;


import parser.DocTermParser;
import parser.InvertedIndex;
import parser.IndexConstructor;
import parser.Tokenizer;
import retrieval.InfoRetrieval;
import retrieval.OkapiBM25;
 
import utils.ByteArrayWrapper;
import utils.Mergesort;
import utils.SysLogger;


/*
 * COMP6791 Project2
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
		System.out.println("COMP 6791 Project 2");
		System.out.println("Developed by Yuan Tao.\n");

		// build the inverted index
		Date date = new Date();
		System.out.println("Start to build the inverted index...\n" + date.toString());
		System.out.println();
		
		IndexConstructor ic = new IndexConstructor();
		
		// NOTE: inputPath MUST be ended with \\ 
		//String inputPath = "D:\\workspace\\COMP6791\\rootdir\\";
		//String inputPath = "D:\\workspace\\COMP6791\\Project1\\aa\\";
		String inputPath = "D:\\workspace\\COMP6791\\ehs.concordia.ca\\";
		
		if (ic.buildInvertedIndex(inputPath) != 0) {
			return;
		}

		// load one inverted index file to memory
		date = new Date();
		System.out.println("Start to load the first inverted index file to memory...\n" + date.toString());
		System.out.println();
		
		InfoRetrieval ir = new InfoRetrieval();
		ir.init();
		
		// traverse all docs again and get all terms
		DocTermParser docParser = new DocTermParser();
		docParser.getAllTermsForAllDocs(inputPath, ir);
		
		/*
		 * 联调时执行到此即可。注意需要修改上面的 inputPath，此路径必须以 \\ 结尾。
		 * 创建工程时需要导入 工程目录/libs/jsoup-1.7.1.jar
		 *  
		 * tf: OkapiBM25.getTF()
		 * df: InfoRetrieval.getIntersectionPostingsOfTerms，每次传一个term即可，
		 * doc len: OkapiBM25.mapLenOfDocs.get(docID);
		 * average doc len: OkapiBM25.avgDocLen
		 * 
		 * 计算BM25的例子可以参考代码：OkapiBM25.getScore
		 * 
		 * 
		 * 取URL. URLList.getURL(long docID) 取该docID对应的URL
		 * 
		 *  
		 */

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
				
				// test tuning parameters of k and b
				if (query.equals("kb")) {
					OkapiBM25.k = scan.nextDouble();
					OkapiBM25.b = scan.nextDouble();
					continue;
				}
				
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
