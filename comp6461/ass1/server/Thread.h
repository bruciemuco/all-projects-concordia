/*
 * COMP6461 Assignment1
 *
 * This file is modified by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 *
 * $Author: ewan.msn@gmail.com $
 * $Date: 2011-10-12 00:12:44 -0400 (Wed, 12 Oct 2011) $
 * $Rev: 33 $
 * $HeadURL: https://comp6471.googlecode.com/svn/Project2/src/Project2Main.java $
 *
 */

/*From COEN320:Real-Time System and COEN421: Embedded System Design*/
#ifndef THREAD_HPP
#define THREAD_HPP

#include <stdio.h>
#include <stdlib.h>
#include <process.h>

#define	STKSIZE	 16536
class Thread {
public:

	Thread() {
	}
	virtual ~Thread() {
	}

	static void * pthread_callback(void * ptrThis);

	virtual void run() =0;
	void start();
};
#endif
