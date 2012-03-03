/*
 * COMP6461 Assignment1
 *
 * This file is modified by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 *
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-01-23 03:02:21 -0500 (Mon, 23 Jan 2012) $
 * $Rev: 14 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6461/ass1/server/server.h $
 *
 */

#ifndef SER_TCP_H
#define SER_TCP_H


#include "Thread.h"

const char *FILE_DIR_ROOT = "../server_files_root/";

class TcpServer: public TcpLib {

public:
	TcpServer(){};
	~TcpServer(){};
	int TcpServer::start();
};

class TcpThread: public Thread {
	int cs;

public:
	TcpThread(int clientsocket) :
			cs(clientsocket) {
	}
	~TcpThread();
	virtual void run();
	int msg_recv(int sock, char *buf, int length);
	int msg_send(int sock, char *buf, int length);

	int recv_data(MSGHEADER &header, MSGREQUEST &request);
};

#endif
