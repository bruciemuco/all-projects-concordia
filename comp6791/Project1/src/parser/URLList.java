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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import retrieval.OkapiBM25;

import utils.ByteArrayWrapper;
import utils.SysLogger;

public class URLList {
	private FileWriter out = null;
	private static String filename;
	
	public URLList() {
		try {
			filename = System.getProperty("user.dir") + "\\output\\urllist.txt";
			out = new FileWriter(filename);
				
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void store2File(String url) {
		try {
			// TODO: store into multi files
			
			out.write(url + "\n");
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getURL(long docID) {
		try {									
			BufferedReader in = new BufferedReader(new FileReader(filename));
			int cnt = 1;
			while(true) {
				if (cnt++ == docID) {
					String url = in.readLine();
					return url.replaceFirst("\\.html", ".php");
				}
				in.readLine();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.err(e.getMessage());
			return null;
		}
	}
}
