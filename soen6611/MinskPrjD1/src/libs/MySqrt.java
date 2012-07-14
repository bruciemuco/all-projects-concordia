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

package libs;

import java.security.InvalidParameterException;

//applied Singleton Pattern
public enum MySqrt {
    INSTANCE;

	static final double TOLERANCE = 0.0000000001;
	
	// NOTE: s MUST be a non-negative real number
	public double getRoot(double s) {
		double x = s / 2.0;
		int cnt = 0;
		
		if (s < 0) {
			throw new InvalidParameterException();
		}
		while (((x * x - s > 0) && (x * x - s > TOLERANCE)) 
				|| ((x * x - s < 0) && (s - x * x > TOLERANCE))) {
			x = (x + s / x) / 2.0;
			
			cnt++;
			if (cnt > 100) {
				break;
			}
		}
		return x;
    }
}