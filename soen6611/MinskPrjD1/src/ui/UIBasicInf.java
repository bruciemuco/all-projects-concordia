/*
 * SOEN6611 Project
 * 
 * UI interface to show messages on screen
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

public interface UIBasicInf {
	public void showGreetings();
	public void showCommands();
	public int getCommand();
	public int getPrimeInputs();
	public double getQuaInputs(int constant);
	public void showResults(Object arg);
}
