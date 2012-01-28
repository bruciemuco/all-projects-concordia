/*
 * COMP6421 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2011-11-16 01:13:08 -0500 (Wed, 16 Nov 2011) $
 * $Rev: 60 $
 * $HeadURL: https://comp6471.googlecode.com/svn/Project6/src/utils/SysLogger.java $
 * 
 */

package LexicalAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import utils.SysLogger;

public class InputLoader {
	public static final int MAX_FILES = 50;
	public static final int MAX_FILE_SIZE = 2*1024*1024;	// 2M bytes
	
	public ArrayList<String> lstFiles = new ArrayList<String>();			// input files
	public ArrayList<String> lstResultFiles = new ArrayList<String>();		// output files
	
	// load all files under a directory
	public int loadTextFiles(String dirPath) {
		String inputDir = dirPath + "\\input\\";
		String outputDir = dirPath + "\\output\\";		
        File filesDir = new File(inputDir);        
        File list[] = filesDir.listFiles();
        
        if (list.length > MAX_FILES) {
			SysLogger.err("Too many files under directory: " + inputDir);
			return -1;
		}
        
        for(int i=0;i<list.length;i++)
        {
            if(list[i].isFile())
            {
            	SysLogger.log("load a file:" + inputDir + list[i].getName());            	

            	if (list[i].length() > MAX_FILE_SIZE) {
            		SysLogger.err("File " + list[i].getName() + " is too large: " 
            			+ list[i].length());
            		return -1;
				}
            	
            	lstFiles.add(inputDir + list[i].getName());
            	lstResultFiles.add(outputDir + list[i].getName());
            }
        }
		return 0;
	}
	


}


