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

	//unsigned short ServPort; /* server port */
	WSADATA wsadata;

	char hostname[HOSTNAME_LENGTH];

	// for UDP
	struct sockaddr_in dstAddr;		// 
	struct sockaddr_in localAddr;
	// sequence num
	unsigned int seq;
	unsigned int lastSeq;		// used by receiver to check if the frame is the same as last one

	// statistics
	int reSendCnt;		// resend times
	int sendCnt;		// send times not including resend times
	int recvCnt;

public:
	SockLib() {
	}
	~SockLib();

	int init();
	int client_init(const char *servername);
	int server_init();
	//void server_start();

	unsigned long resolve_name(const char *name);

	int sock_send(int sock, char *buf, int length);
	int sock_recv(int sock, char *buf, int length);

	int send_file(int sock, const char *filename, int len);
	int recv_file(int sock, const char *filename, int len);

	// udp
	int set_dstAddr(const char *dstHostName, int dstPort);
	int udp_init(int localPort);
	

	int lib_sendto(int sock, char *buf, int length);
	int udp_sendto(int sock, char *buf, int length);
	int add_udpheader(PUDPPACKET pudp, char *buf);
	int sock_sendto(int sock, char *buf, int length);
	int lib_recvfrom(int sock, char *buf, int length);
	
	int send_ack(unsigned int seq);
	int chk_seq(unsigned int seq);
	int sock_recvfrom(int sock, char *buf, int length);
	
	int srv_wait4cnn(int sock);

	// statistics
	void reset_statistics();
	void show_statistics(bool ifSend);
};



#endif
