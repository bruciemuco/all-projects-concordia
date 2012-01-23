/*
 * COMP6461 Assignment1
 *
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 *
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 *
 */

#include <windows.h>
#include <stdio.h>
#include <stdarg.h>

#include "syslogger.h"

SysLogger * SysLogger::pInst = NULL;
FILE * SysLogger::pLogFile = NULL;

SysLogger::SysLogger() {
}
SysLogger::~SysLogger() {
	fclose(pLogFile);
}

SysLogger *SysLogger::inst() {
	if (pInst == NULL) {
		// open log file.
		pLogFile = fopen ("../logs/log.txt" , "w");
		if (pLogFile == NULL) {
			printf("Failed to fopen\n");
			exit(1);
		}

		pInst = new SysLogger();
	}
	return pInst;
}
	
void SysLogger::err(char *fmt, ...) {
	if (pLogFile == NULL) {
		return;
	}
	va_list args;
	va_start(args, fmt);
	vfprintf(pLogFile, fmt, args);
	fprintf(pLogFile, "\n");
	vfprintf(stdout, fmt, args);
	fprintf(stdout, "\n");
	va_end(args);

	fprintf(pLogFile, "WSAGetLastError:%d\n", WSAGetLastError());
	fprintf(stdout, "WSAGetLastError:%d\n", WSAGetLastError());
	fflush(pLogFile);
}

void SysLogger::log(char *fmt, ...) {
	if (pLogFile == NULL) {
		return;
	}
	va_list args;
	va_start(args, fmt);
	vfprintf(pLogFile, fmt, args);
	fprintf(pLogFile, "\n");
	vfprintf(stdout, fmt, args);
	fprintf(stdout, "\n");
	va_end(args);
	fflush(pLogFile);
}



