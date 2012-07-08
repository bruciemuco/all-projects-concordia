/*
 * SOEN6611 Project
 * 
 *  Babylonian Method to compute the square root
 *
 * This file is created by Team F
 * Licensed under GNU GPL v3
 * 
 * $Author:  $
 * $Date:  $
 * $Rev:  $
 * $HeadURL:  $
 * 
 */

import java.security.InvalidParameterException;

public enum MySqrt {
    INSTANCE;

	static final double TOLERANCE = 0.0001;
	
	// NOTE: s MUST be a non-negative real number
	public double getRoot(double s) {
		double x = s / 2;
		
		if (s < 0) {
			throw new InvalidParameterException();
		}
		while (((x * x - s > 0) && (x * x - s > TOLERANCE)) 
				|| ((x * x - s < 0) && (s - x * x > TOLERANCE))) {
			x = (x + s / x) / 2;
		}
		return x;
    }
}