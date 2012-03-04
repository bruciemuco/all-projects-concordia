/*
 * COMP6461 Assignment2
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
#include <winsock.h>

#define MAXPENDING 10


class SockLib {
protected:
	int sock; 		// client socket or server listening socket
	int client_sock; // sockets that accepted by server

	struct sockaddr_in ServerAddr; 		/* server socket address */
	struct sockaddr_in ClientAddr;

	// for UDP
	struct sockaddr_in dstAddr;		// 
	struct sockaddr_in localAddr;

	//unsigned short ServPort; /* server port */
	WSADATA wsadata;

	char hostname[HOSTNAME_LENGTH];

public:
	SockLib() {
	}
	~SockLib();

	int init();
	int client_init(const char *servername);
	int server_init();
	int set_dstAddr(const char *dstHostName, int dstPort);
	int udp_init(int localPort);
	//void server_start();

	unsigned long resolve_name(const char *name);

	int sock_send(int sock, char *buf, int length);
	int sock_recv(int sock, char *buf, int length);
	int sock_sendto(int sock, char *buf, int length);
	int sock_recvfrom(int sock, char *buf, int length);

	int send_file(int sock, const char *filename, int len);
	int recv_file(int sock, const char *filename, int len);
};



#endif
