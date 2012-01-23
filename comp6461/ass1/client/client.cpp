/*a small file client
 Usage: suppose client is running on sd1.encs.concordia.ca and server is running on sd2.encs.concordia.ca
 .Also suppose there is a file called test.txt on the server.
 In the client,issuse "client sd2.encs.concordia.ca test.txt size" and you can get the size of the file.
 In the client,issuse "client sd2.encs.concordia.ca test.txt time" and you can get creation time of the file
 */

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
#include <iostream>
#include <string>


#include "../common/syslogger.h"
#include "../common/tcplib.h"
#include "client.h"

using namespace std;


int main(int argc, char *argv[]) {
	// create logger
	SysLogger::inst()->log("Wellcome to COMP6461 assignment 1.");
	SysLogger::inst()->log("Developed by Yuan Tao & Xiaodong Zhang.");

	//get input
	string servername, filename, opname;

	SysLogger::inst()->log("Type name of ftp server: ");
	cin >> servername;
	SysLogger::inst()->log("Type name of file to be transferred: ");
	cin >> filename;
	SysLogger::inst()->log("Type direction of transfer: ");
	cin >> opname;

	//start to connect to the server
	TcpLib * tc = new TcpLib();

	if (tc->client_init(servername.c_str())) {
		goto ERR;
	}
	SysLogger::inst()->log("Sent request to %s, waiting...", servername);

	if (tc->msg_send(filename.c_str(), opname.c_str())) {
		goto ERR;
	}

	return 0;
ERR:
	delete tc;
	return -1;
}
