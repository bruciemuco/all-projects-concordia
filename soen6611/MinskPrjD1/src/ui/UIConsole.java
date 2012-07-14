/*
 * SOEN6611 Project
 * 
 * Console UI
 * 
 * This file is created by Team F
 * Licensed under GNU GPL v3
 * 
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 * 
 */

package ui;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import utils.SysLogger;

import libs.Result;

public class UIConsole implements UIBasicInf, Observer {

	@Override
	public void update(Observable o, Object arg) {
		showResults(arg);
		showCommands();
	}

	@Override
	public void showGreetings() {
		System.out.println("Welcome to SOEN6611 Project D1 v0.1");
		System.out.println("Developed by Team F (Lu Ma, Meng Jia, Yuan Tao)");
		System.out.println();
	}

	@Override
	public void showCommands() {
		System.out.println("Choose a function:");
		System.out.println("1: Prime Checker");
		System.out.println("2: Quadratic Equation Solver");	
		System.out.println("other: Exit");
	}

	@Override
	public int getCommand() {
		int num = 0;

		try {
			Scanner scan = new Scanner(System.in);
			num = scan.nextInt();
		} catch (Exception e) {
			return num;
		}

		return num;
	}

	// get a integer from user
	private int getAInt() {
		int num = 0;
		boolean ifValidInput = false;
		
		while (!ifValidInput) {
			try {
				Scanner scan = new Scanner(System.in);
				num = scan.nextInt();
				ifValidInput = true;
			} catch (Exception e) {
				System.out.println("Invalid integer, please try again.");
				SysLogger.err("getAInt: invalid input: " + num);
			}
		}
		return num;
	}
	// get a double from user
	private Double getADouble() {
		Double num = 0.0;
		boolean ifValidInput = false;
		
		while (!ifValidInput) {
			try {
				Scanner scan = new Scanner(System.in);
				num = scan.nextDouble();
				ifValidInput = true;
			} catch (Exception e) {
				System.out.println("Invalid Double, please try again.");
				SysLogger.err("getADouble: invalid input: " + num);
			}
		}
		return num;
	}

	@Override
	public int getPrimeInputs() {
		System.out.println("Enter a number (less than 32768):");
		return getAInt();
	}
	
	@Override
	public double getQuaInputs(int constant) {
		switch (constant) {
		case 1:
			System.out.println("Enter the value of a (can't be 0):");
			return getADouble();
		case 2:
			System.out.println("Enter the value of b:");
			return getADouble();
		case 3:
			System.out.println("Enter the value of c:");
			//System.out.println("Your equation is " + a + "x^2 + " + b + "x + " + c + " = 0");
			return getADouble();
		}
		return 0.0;
	}
	

	@Override
	public void showResults(Object arg) {
		Result ret = (Result) arg;
		
		if (ret.opType == Result.OP_PRIME) {
			System.out.println("Result of Prime Checker: ");
			System.out.println(ret.msg);
			System.out.println();
			return;
		}
		
		System.out.println("Result of Quadratic Equation Solver: ");
		if (ret.numOfRoots == 1)
			System.out.println(ret.root1);
		if (ret.numOfRoots == 2) {
			System.out.println(ret.root1);
			System.out.println(ret.root2);
		}
		System.out.println(ret.msg);
		System.out.println();
	}

}
