/*
 * SOEN6611 Project
 * 
 * Solve quadratic equation according difference coefficient. 
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

// applied Singleton Pattern
public enum Quadratic {
	INSTANCE;
	
	private MySqrt mySqrt_ = MySqrt.INSTANCE;

	public boolean validation(double a, double b, double c) {
		if (a == 0)
			return false;
		else
			return true;
	}

	// Calculate the numbers of roots
	public int howManyRoots(double a, double b, double c) {
		double delta = b * b - 4 * a * c;
		if (delta < 0)
			return 0;
		if (delta == 0)
			return 1;
		else
			return 2;
	}

	public Result solve(double a, double b, double c) {
		Result myRes = new Result();
		
		myRes.opType = Result.OP_QUADRATIC;
		if (validation(a, b, c)) {
			int rootsNum = howManyRoots(a, b, c);
			if (rootsNum == 0) {
				myRes.numOfRoots = 0;
				myRes.msg = "No real number roots";
			}
			if (rootsNum == 1) {
				myRes.numOfRoots = 1;
				myRes.root1 = -(b / (2 * a));
			}
			if (rootsNum == 2) {
				myRes.numOfRoots = 2;
				if (b == 0) {
					myRes.root1 = -(mySqrt_.getRoot(-a / c));
					myRes.root2 = (mySqrt_.getRoot(-a / c));
				} else {
					// To avoid the loss of significance, use equation
					// 2c/-b-(sign(b))*sqr(b*b-4ac) to get the smaller roots.
					myRes.root1 = (-b - Math.signum(b)
							* mySqrt_.getRoot(b * b - 4 * a * c))
							/ 2 * a;
					myRes.root2 = 2
							* c
							/ (-b - Math.signum(b)
									* mySqrt_.getRoot(b * b - 4 * a * c));
				}
			}
		} else
			myRes.msg = "Invalid input";
		return myRes;
	}

}
