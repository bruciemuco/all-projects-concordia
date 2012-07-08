/*
 * SOEN6611 Project
 * 
 * Babylonian Method to compute the square root
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

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MySqrt mySqrt = MySqrt.INSTANCE;
		
		System.out.println("=" + mySqrt.getRoot(0));
		System.out.println("=" + mySqrt.getRoot(0.1));
		System.out.println("=" + mySqrt.getRoot(1));
		System.out.println("=" + mySqrt.getRoot(1.44));
		System.out.println("=" + mySqrt.getRoot(3));
		System.out.println("=" + mySqrt.getRoot(4));
		System.out.println("=" + mySqrt.getRoot(9));

		System.out.println("=" + mySqrt.getRoot(-1));
	}

}
