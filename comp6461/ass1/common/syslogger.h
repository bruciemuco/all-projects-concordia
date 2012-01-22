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

#ifndef __SYSLOGGER_H__
#define __SYSLOGGER_H__

#include "stdio.h"

class SysLogger {
private:
	static SysLogger *pInst;
	static FILE *pLogFile;
	SysLogger() {
	}
	~SysLogger() {
		fclose(pLogFile);
	}

public:
	static SysLogger *inst() {
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
	
	void err(char *fmt, ...) {
		if (pLogFile == NULL) {
			return;
		}
		//perror(NULL);
		va_list args;
		va_start(args, fmt);
		fprintf(pLogFile, "error: ");
		vfprintf(pLogFile, fmt, args);
		fprintf(pLogFile, "\n");
		va_end(args);
		exit(1);		// TODO: memory leak.
	}
	void log(char *fmt, ...) {
		if (pLogFile == NULL) {
			return;
		}
		va_list args;
		va_start(args, fmt);
		vfprintf(pLogFile, fmt, args);
		fprintf(pLogFile, "\n");
		va_end(args);
	}
};

SysLogger * SysLogger::pInst = NULL;
FILE * SysLogger::pLogFile = NULL;

#endif
