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
#include <winsock.h>

#define MAXPENDING 10
#define TIMEOUT_USEC 300000 

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
	int udp_init(const char *dstHostName, int dstPort, int localPort);
	//void server_start();

	unsigned long resolve_name(const char *name);

	static int sock_send(int sock, char *buf, int length);
	static int sock_recv(int sock, char *buf, int length);
	static int sock_sendto(int sock, char *buf, int length);
	static int sock_recvfrom(int sock, char *buf, int length);

	static int send_file(int sock, const char *filename, int len);
	static int recv_file(int sock, const char *filename, int len);
};



#endif
