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
};

#endif
