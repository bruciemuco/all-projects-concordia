/*
   COMP6461 Assignment1

   Yuan Tao (ID: 5977363) 
   Xiaodong Zhang (ID: 6263879) 
 
   Course Instructor: Amin Ranj Bar 
   Lab Instructor: Steve Morse   
   Lab number: Friday 

 *
 * This file is modified by Yuan Tao (ewan.msn@gmail.com) & Xiaodong Zhang
 * Licensed under GNU GPL v3
 *
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-02-11 23:22:34 -0500 (Sat, 11 Feb 2012) $
 * $Rev: 36 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6461/ass1/server/Thread.cpp $
 *
 */

#include <stdio.h>
#include "Thread.h"

/*
 * This is the callback needed by the Thread class
 */
void * Thread::pthread_callback(void * ptrThis) {

	if (ptrThis == NULL)
		return NULL;
	Thread * ptr_this = (Thread *) (ptrThis);
	ptr_this->run();
	return NULL;
}

void Thread::start() {
	int result;
	if ((result = _beginthread((void (*)(void *))Thread::pthread_callback,STKSIZE,this ))<0)
	{
		printf("_beginthread error\n");
		exit(-1);
	}

}

