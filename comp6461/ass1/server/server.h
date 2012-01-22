/*
 * COMP6461 Assignment1
 *
 * This file is modified by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 *
 * $Author: ewan.msn@gmail.com $
 * $Date: 2011-10-12 00:12:44 -0400 (Wed, 12 Oct 2011) $
 * $Rev: 33 $
 * $HeadURL: https://comp6471.googlecode.com/svn/Project2/src/Project2Main.java $
 *
 */

#ifndef SER_TCP_H
#define SER_TCP_H

#include "../common/protocol.h"

#define MAXPENDING 10
#define MSGHDRSIZE 8 //Message Header Size

class TcpServer {
	int serverSock, clientSock; /* Socket descriptor for server and client*/
	struct sockaddr_in ClientAddr; /* Client address */
	struct sockaddr_in ServerAddr; /* Server address */
	unsigned short ServerPort; /* Server port */
	int clientLen; /* Length of Server address data structure */
	char servername[HOSTNAME_LENGTH];

public:
	TcpServer();
	~TcpServer();
	void TcpServer::start();
};

class TcpThread: public Thread {
	int cs;

public:
	TcpThread(int clientsocket) :
			cs(clientsocket) {
	}
	virtual void run();
	int msg_recv(int, PMSGFMT);
	int msg_send(int, PMSGFMT);
	unsigned long resolve_name(char name[]);
	static void err_sys(char * fmt, ...);
};

#endif
