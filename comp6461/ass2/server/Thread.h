/*
 * COMP6461 Assignment1
 *
 * This file is modified by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 *
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-01-22 17:51:15 -0500 (Sun, 22 Jan 2012) $
 * $Rev: 11 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6461/ass1/server/Thread.h $
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
