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
import java.util.ArrayList;
import java.util.StringTokenizer;

import utils.SysLogger;

public class InputLoader {
	public static final int MAX_FILES = 50;
	
	// load all files under a directory
	public int loadTextFiles(String dirPath) {
        File filesDir = new File(dirPath);
        
        File list[] = filesDir.listFiles();
        if (list.length > MAX_FILES) {
			SysLogger.output("Too many files under directory: " + dirPath);
			return -1;
		}
        
        for(int i=0;i<list.length;i++)
        {
            if(list[i].isFile())
            {
            	String fileFullname =dirPath + "\\" + list[i].getName(); 
            	SysLogger.info(fileFullname);
            	
            	// load the file

            }
        }
		return 0;
	}
	


}


