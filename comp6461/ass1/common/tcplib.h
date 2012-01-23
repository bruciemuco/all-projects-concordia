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
#ifndef __TCPLIB_H__
#define __TCPLIB_H__

#include <windows.h>

#define HOSTNAME_LENGTH 20

class TcpLib {
	int sock; /* Socket descriptor */
	struct sockaddr_in ServAddr; /* server socket address */
	unsigned short ServPort; /* server port */
	WSADATA wsadata;

	char hostname[HOSTNAME_LENGTH];

public:
	TcpLib() {
	}
	~TcpLib();

	int init();
	int client_init(const char *servername);
	int server_init(const char *servername);

	int msg_recv(char *buf, int length);
	int msg_send(const char *filename, const char *opname);
	unsigned long resolve_name(const char *name);

	int sock_send(char *data, int length);
	int sock_recv(char *buf, int length);
};



#endif
