/*
 * SOEN6611 Project
 * 
 * Primality algorithm 3 to check prime number
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

package libs;

//applied Singleton Pattern
public enum Prime {
	INSTANCE;
	
	// check if num inputted is valid
	private boolean checkInputs(int num) {
		if (num < 0 || num > 32767) {
			return false;
		}
		return true;
	}
	
	public Result isPrime(int num) {
		if (!checkInputs(num)) {
			Result ret = new Result();
			
			ret.msg = "Invalid input: " + num;
			return ret;
		}

		Result retTrue = new Result();
		Result retFalse = new Result();
		
		retFalse.msg = "false";
		retTrue.msg = "true";
		
		if (num == 1)
			return retFalse;
		if (num == 2 || num == 3)
			return retTrue;
		if (num % 2 == 0 || num % 3 == 0)
			return retFalse;

		// check for integer in form 6i+-1 till sqr(n)
		for (int i = 1; (6 * i - 1) * (6 * i - 1) <= num; i++) {
			// if integer 6i+-1 is the facter of n
			if (num % (6 * i + 1) == 0 || num % (6 * i - 1) == 0)
				return retFalse;
		}
		return retTrue;
	}
	
}
