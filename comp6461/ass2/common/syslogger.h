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


class SysLogger {
private:
	static SysLogger *pInst;		// TODO: delete
	static FILE *pLogFile;
	SysLogger();
	~SysLogger();

public:
	static SysLogger *inst();
	
	int set(char *filename);

	void err(char *fmt, ...);
	void log(char *fmt, ...);
	void out(char *fmt, ...);

	void wellcome();
};


#endif


