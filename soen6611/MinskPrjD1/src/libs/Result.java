/*
 * SOEN6611 Project
 * 
 * The Result class that stores the result information 
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

public class Result {
	// types of operation
	public final static int OP_PRIME = 1;
	public final static int OP_QUADRATIC = 2;
	
	public int opType = OP_PRIME;
	
	public String msg = "";
	public int numOfRoots = 0;
	public double root1 = 0.0;
	public double root2 = 0.0;
}
