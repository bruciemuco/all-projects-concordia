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

import java.io.File;

import utils.SysLogger;

public class DirTraversal implements Runnable {
	private String dirRoot;
	private boolean ifStarted = false;
	
	private void traverse(File file) {
		if (!file.isDirectory()) {
			if (!ifStarted) {
				ifStarted = true;
				SysLogger.info("DirTraversal started.");
				Tokenizer.semNextFileDone.release();
			}
			try {
				Tokenizer.semNextFileBegin.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			String filename = file.getAbsolutePath();
			//SysLogger.info("========" + filename);
			
			if (filename.matches(
					".+\\.html|.+\\.php|.+\\.shtml|.+\\.htm|.+\\.asp")) {
				Tokenizer.nextFile = filename;
				Tokenizer.semNextFileDone.release();
			} else {
				Tokenizer.semNextFileBegin.release();
			}
			return;
		}
		
		String[] list = file.list();
		if (list != null) {
			for (String child : list) {
				traverse(new File(file, child));
			}
		}
	}

	@Override
	public void run() {
		traverse(new File(dirRoot));

		try {
			Tokenizer.semNextFileBegin.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Tokenizer.nextFile = null;
		Tokenizer.semNextFileDone.release();
	}
	
	public DirTraversal(String path) {
		dirRoot = path;
	}
	
	public static void start(String path) {
		new Thread(new DirTraversal(path)).start();
	}
}
