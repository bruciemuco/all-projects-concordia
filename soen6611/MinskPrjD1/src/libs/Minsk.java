/*
 * SOEN6611 Project
 * 
 * Class Minsk. provide function prime checker and quadratic equation solver
 * 
 * This file is created by Team F
 * Licensed under GNU GPL v3
 * 
 * $Date$
 * $Rev$
 * $HeadURL$
 * 
 */

package libs;

import java.util.Observable;

import utils.SysLogger;

public class Minsk extends Observable {
	private Prime prime_ = Prime.INSTANCE;
	private Quadratic quadratic_ = Quadratic.INSTANCE;
	
	public Result isPrime(int n) {
		Result ret = prime_.isPrime(n);
		
		printResults(ret);
		notifyAllObservers(ret);
		return ret;
	}

	public Result solve(double a, double b, double c) {
		Result ret = quadratic_.solve(a, b, c);

		printResults(ret);
		notifyAllObservers(ret);
		return ret;
	}
	
	// notify UI to show the results
	public void notifyAllObservers(Object obj) {
		setChanged();
		notifyObservers(obj);
	}
	
	private void printResults(Result ret) {
		SysLogger.info("====Result====");
		SysLogger.info("type: " + ret.opType);
		SysLogger.info("msg: " + ret.msg);
		SysLogger.info("rootNum: " + ret.numOfRoots);
		SysLogger.info("root1: " + ret.root1);
		SysLogger.info("root2: " + ret.root2);
	}
}
