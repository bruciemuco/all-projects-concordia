/*a small file Server
 Usage: suppose Server is running on sd1.encs.concordia.ca and server is running on sd2.encs.concordia.ca
 .Also suppose there is a file called test.txt on the server.
 In the Server,issuse "Server sd2.encs.concordia.ca test.txt size" and you can get the size of the file.
 In the Server,issuse "Server sd2.encs.concordia.ca test.txt time" and you can get creation time of the file
 */
/*
   COMP6461 Assignment2

   Yuan Tao (ID: 5977363) 
   Xiaodong Zhang (ID: 6263879) 
 
   Course Instructor: Amin Ranj Bar 
   Lab Instructor: Steve Morse   
   Lab number: Friday 

 *
 * This file is modified by Yuan Tao (ewan.msn@gmail.com) & Xiaodong Zhang
 * Licensed under GNU GPL v3
 *
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 *
 */

#include <winsock.h>
#include <iostream>
#include <windows.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdio.h>
#include <process.h>
#include <string>
#include <exception>

#include "../common/syslogger.h"
#include "../common/protocol.h"
#include "../common/socklib.h"
#include "server.h"


using namespace std;


int SockServer::start() {
// 	for (;;) /* Run forever */
// 	{
// 		/* Set the size of the result-value parameter */
// 		int clientLen = sizeof(ServerAddr);
// 
// 		/* Wait for a Server to connect */
// 		if ((client_sock = accept(sock, (struct sockaddr *)&ClientAddr, &clientLen)) == INVALID_SOCKET) {
// 			SysLogger::inst()->err("accept error");
// 			return -1;
// 		}
// 
// 		/* Create a Thread for this new connection and run*/
// 		TcpThread * pt = new TcpThread(client_sock);
// 		pt->start();
// 	}
	while (1) {
		if (srv_wait4cnn(sock) < 0) {
			return -1;
		}
		client_handler();
	}
	return 0;
}

int SockServer::recv_data(MSGHEADER &header, MSGREQUEST &request) {
	// began to receive the request header
	string type;

	if (sock_recvfrom(sock, (char *)&header, sizeof(MSGHEADER))) {
		SysLogger::inst()->err("failed to get header of request");
		return MSGTYPE_RESP_FAILTOGETHEADER;
	}
	header.len = ntohl(header.len);
	if (header.type == MSGTYPE_REQ_GET) {
		type = MSGTYPE_STRGET;
		if (header.len != sizeof(MSGREQUEST)) {
			SysLogger::inst()->err("header.len != sizeof(request). %d, %d", header.len, sizeof(MSGREQUEST));
			return MSGTYPE_RESP_WRONGHEADER;
		}
	} else if (header.type == MSGTYPE_REQ_PUT) {
		type = MSGTYPE_STRPUT;
	} else {
		SysLogger::inst()->err("unknown request type");
		return MSGTYPE_RESP_UNKNOWNTYPE;
	}
	SysLogger::inst()->log("Received a request(type: %s, len: %d)", type.c_str(), header.len);

	// get the filename and host name
	if (header.len > 0) {
		if (sock_recvfrom(sock, (char *)&request, sizeof(MSGREQUEST))) {
			SysLogger::inst()->err("failed to get request info.");
			return MSGTYPE_RESP_FAILTOGETINFO;
		}
		SysLogger::inst()->log("hostname: %s, filename: %s", request.hostname, request.filename);
	}
	if (header.type == MSGTYPE_REQ_PUT) {
		SysLogger::inst()->out("User \"%s\" requested file %s to be Received.", "spacewalker", request.filename);
	} else {
		SysLogger::inst()->out("User \"%s\" requested file %s to be sent.", "spacewalker", request.filename);
	}

	// send back the response
	string filename = FILE_DIR_ROOT;
	filename += request.filename;

	if (header.type == MSGTYPE_REQ_PUT) {
		// continue to receive the file before sending response
		SysLogger::inst()->out("Receiving file from %s, waiting...", request.hostname);
		if (SockLib::recv_file(sock, filename.c_str(), header.len - sizeof(MSGREQUEST))) {
			return MSGTYPE_RESP_FAILTORECVFILE;
		}
		SysLogger::inst()->out("Successfully receive the file: %s", request.filename);
	}

	return MSGTYPE_RESP_OK;
}

void SockServer::client_handler()
{
	MSGHEADER header;
	MSGREQUEST request;
	MSGHEADER header_resp;

	// handle request
	memset((void *)&header, 0, sizeof(MSGHEADER));
	memset((void *)&request, 0, sizeof(MSGREQUEST));
	header_resp.type = recv_data(header, request);

	// send back the response
	header_resp.len = 0;
	string filename = FILE_DIR_ROOT;
	filename += request.filename;

	if (header.type == MSGTYPE_REQ_GET) {
		// get the file size
		FILE *pFile = 0;

		pFile = fopen(filename.c_str(), "rb");
		if (pFile == NULL) {
			SysLogger::inst()->err("No such a file: %s\n", filename.c_str());
			header_resp.type = MSGTYPE_RESP_NOFILE;
		} else {
			fseek(pFile, 0, SEEK_END);
			header_resp.len = ftell(pFile);
			fclose(pFile);
		}
	}
	show_statistics(false);

	// send header
	if (sock_sendto(sock, (char *)&header_resp, sizeof(header_resp)) != 0) {
		SysLogger::inst()->err("sock_sendto error. header.type:%d\n", header.type);
		return;
	}
	SysLogger::inst()->log("Send response: header.type: %d, len: %d", header_resp.type, header_resp.len);

	// send file
	if (header.type == MSGTYPE_REQ_GET && header_resp.type == MSGTYPE_RESP_OK) {
		SysLogger::inst()->out("Sending file to %s, waiting...", request.hostname);
		if (send_file(sock, filename.c_str(), header_resp.len)) {
			return;
		}
		SysLogger::inst()->out("Successfully send the file: %s", request.filename);
	}
	SysLogger::inst()->log("Send response: file: %s ", filename.c_str());
	show_statistics(true);
	SysLogger::inst()->out("\n");
}


int main(void) {
	// create logger
	if (SysLogger::inst()->set("../logs/server_log.txt")) {
		return -1;
	}
	SysLogger::inst()->wellcome();

	SockServer *ts = new SockServer();

	try {
		if (ts->udp_init(SERVER_RECV_PORT)) {
			delete ts;
			return -1;
		}
		
		if (ts->start()) {
			delete ts;
			return -1;
		}
	}
	catch (int e) {
		e = 0;
		delete ts;
		return -1;
	}

	delete ts;
	return 0;
}

