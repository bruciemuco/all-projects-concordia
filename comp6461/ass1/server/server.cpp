/*a small file Server
 Usage: suppose Server is running on sd1.encs.concordia.ca and server is running on sd2.encs.concordia.ca
 .Also suppose there is a file called test.txt on the server.
 In the Server,issuse "Server sd2.encs.concordia.ca test.txt size" and you can get the size of the file.
 In the Server,issuse "Server sd2.encs.concordia.ca test.txt time" and you can get creation time of the file
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

#include <winsock.h>
#include <iostream>
#include <windows.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdio.h>
#include <process.h>
#include <string>

#include "../common/syslogger.h"
#include "../common/protocol.h"
#include "../common/tcplib.h"
#include "server.h"


using namespace std;


int TcpServer::start() {
	for (;;) /* Run forever */
	{
		/* Set the size of the result-value parameter */
		int clientLen = sizeof(ServerAddr);

		/* Wait for a Server to connect */
		if ((client_sock = accept(sock, (struct sockaddr *)&ClientAddr, &clientLen)) == INVALID_SOCKET) {
			SysLogger::inst()->err("accept error");
			return -1;
		}

		/* Create a Thread for this new connection and run*/
		TcpThread * pt = new TcpThread(client_sock);
		pt->start();
	}

	return 0;
}

//////////////////////////////TcpThread Class //////////////////////////////////////////

TcpThread::~TcpThread() {
	closesocket(cs);
}

int TcpThread::msg_recv(int sock, char *buf, int length) {
	return TcpLib::sock_recv(sock, buf, length);
}

int TcpThread::msg_send(int sock, char *buf, int length) {
	return TcpLib::sock_send(sock, buf, length);
}

int TcpThread::recv_data(MSGHEADER &header, MSGREQUEST &request) {
	int sock = cs;

	SysLogger::inst()->log("accept sock: %d", sock);

	// began to receive the request header
	string type;

	if (msg_recv(sock, (char *)&header, sizeof(MSGHEADER))) {
		SysLogger::inst()->err("failed to get header of response");
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
		if (msg_recv(sock, (char *)&request, sizeof(MSGREQUEST))) {
			SysLogger::inst()->err("failed to get request info.");
			return MSGTYPE_RESP_FAILTOGETINFO;
		}
		SysLogger::inst()->log("hostname: %s, filename: %s", request.hostname, request.filename);
	}

	// send back the response
	string filename = FILE_DIR_ROOT;
	filename += request.filename;

	if (header.type == MSGTYPE_REQ_PUT) {
		// continue to receive the file before sending response
		if (TcpLib::recv_file(sock, filename.c_str(), header.len - sizeof(MSGREQUEST))) {
			return MSGTYPE_RESP_FAILTORECVFILE;
		}
		SysLogger::inst()->log("Received a file");
	}

	return MSGTYPE_RESP_OK;
}

void TcpThread::run() //cs: Server socket
{
	int sock = cs;
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
			SysLogger::inst()->err("No such a file:%s\n", filename);
			header_resp.type = MSGTYPE_RESP_NOFILE;
		}
		header_resp.len = fseek(pFile, 0, SEEK_END);
		fclose(pFile);
	}

	// send header
	if (msg_send(sock, (char *)&header_resp, sizeof(header_resp)) != 0) {
		SysLogger::inst()->err("sock_send error. header.type:%d\n", header.type);
		return;
	}
	SysLogger::inst()->log("Send response: header.type: %d, len: %d", header_resp.type, header_resp.len);

	// send file
	if (header.type == MSGTYPE_REQ_GET) {
		if (TcpLib::send_file(sock, filename.c_str(), header_resp.len)) {
			return;
		}
	}
	SysLogger::inst()->log("Send response: file: %s ", filename.c_str());

}

////////////////////////////////////////////////////////////////////////////////////////

int main(void) {
	// create logger
	if (SysLogger::inst()->set("../logs/server_log.txt")) {
		return -1;
	}
	SysLogger::inst()->wellcome();

	TcpServer *ts = new TcpServer();

	if (ts->server_init()) {
		goto ERR;
	}
	SysLogger::inst()->log("Sent reques");
	if (ts->start()) {
		goto ERR;
	}

	return 0;

ERR:
	delete ts;
	return -1;
}

