/*
 * COMP6461 Assignment1
 *
 * This file is modified by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 *
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
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

