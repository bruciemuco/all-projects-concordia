/*
 * COMP6461 Assignment1
 *
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 *
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-01-31 15:59:38 -0500 (Tue, 31 Jan 2012) $
 * $Rev: 31 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6461/ass1/common/syslogger.h $
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


