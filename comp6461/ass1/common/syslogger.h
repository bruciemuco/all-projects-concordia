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
	static SysLogger *pInst;		// TODO: delete
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

	void log(char *fmt, ...) {
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
};

SysLogger * SysLogger::pInst = NULL;
FILE * SysLogger::pLogFile = NULL;

#endif
