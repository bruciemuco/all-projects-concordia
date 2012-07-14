/*
 * SOEN6611 Project
 * 
 * Driver class to run minsk 
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
import ui.UIConsole;
import utils.SysLogger;

import libs.Minsk;
import libs.Result;

public class MinskDriver {

	public static void main(String[] args) {
		// initialize SysLogger
		SysLogger.init();
		
		// create console UI
		UIConsole ui = new UIConsole();
		
		// create Minsk
		Minsk minsk = new Minsk();
		
		// add an UI observer for Minsk
		minsk.addObserver(ui);

		// show greetings
		ui.showGreetings();
		
		// get a command from UI
		ui.showCommands();

		while (true) {
			int cmd = ui.getCommand();

			// Prime Checker
			if (cmd == Result.OP_PRIME) {
				minsk.isPrime(ui.getPrimeInputs());
				
			// Quadratic Equation Solver
			} else if (cmd == Result.OP_QUADRATIC) {
				double a = ui.getQuaInputs(1);
				double b = ui.getQuaInputs(2);
				double c = ui.getQuaInputs(3);
				
				minsk.solve(a, b, c);
			} else {
				break;
			}
		}
		
	}

}
