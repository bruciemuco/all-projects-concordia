/*
   COMP6461 Assignment2

   Yuan Tao (ID: 5977363) 
   Xiaodong Zhang (ID: 6263879) 
 
   Course Instructor: Amin Ranj Bar 
   Lab Instructor: Steve Morse   
   Lab number: Friday 

 *
 * This file is created by Yuan Tao (ewan.msn@gmail.com) & Xiaodong Zhang
 * Licensed under GNU GPL v3
 *
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 *
 */


#include <stdio.h>

#include "syslogger.h"
#include "protocol.h"
#include "socklib.h"

#pragma comment(lib, "ws2_32.lib")


SockLib::~SockLib() {
	/* When done uninstall winsock.dll (WSACleanup()) and exit */
	closesocket(sock);
	WSACleanup();
}

int SockLib::init() {
	//initilize winsocket
	if (WSAStartup(0x0202, &wsadata) != 0) {
		SysLogger::inst()->err("Error in starting WSAStartup()\n");
		return -1;
	}

	//Display name of local host and copy it to the req
	if (gethostname(hostname, HOSTNAME_LENGTH) != 0) {
		SysLogger::inst()->err("can not get the host name,program exit");
		//WSACleanup();
		return -1;
	}

	//Create the socket
	//if ((sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) == INVALID_SOCKET) {
	if ((sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == INVALID_SOCKET) {
		SysLogger::inst()->err("Socket Creating Error");
		//WSACleanup();
		return -1;
	}
	SysLogger::inst()->log("create a socket: %d", sock);

	return 0;
}

int SockLib::client_init(const char *servername) {
	if (servername == 0) {
		SysLogger::inst()->err("init params error");
		return -1;
	}
	
	if (init()) {
		SysLogger::inst()->err("socket init error");
		return -1;
	}

	SysLogger::inst()->out("ftp_tcp starting on host: [%s]", hostname);
	
	//connect to the server
	memset(&ServerAddr, 0, sizeof(ServerAddr)); /* Zero out structure */
	ServerAddr.sin_family = AF_INET; /* Internet address family */
	ServerAddr.sin_addr.s_addr = resolve_name(servername); /* Server IP address */
	ServerAddr.sin_port = htons(SERVER_RECV_PORT); /* Server port */
	if (connect(sock, (struct sockaddr *) &ServerAddr, sizeof(ServerAddr)) < 0) {
		SysLogger::inst()->err("Faild to connect to server: %s", servername);
		//closesocket(sock);
		//WSACleanup();
		return -1;
	}


	return 0;
}

int SockLib::server_init() {
	if (init()) {
		SysLogger::inst()->err("socket init error");
		return -1;
	}

	SysLogger::inst()->out("ftpd_tcp starting at host: [%s]", hostname);

	//Fill-in Server Port and Address info.
	memset(&ServerAddr, 0, sizeof(ServerAddr)); /* Zero out structure */
	ServerAddr.sin_family = AF_INET; /* Internet address family */
	ServerAddr.sin_addr.s_addr = htonl(INADDR_ANY); /* Any incoming interface */
	ServerAddr.sin_port = htons(SERVER_RECV_PORT); /* Local port */

	//Bind the server socket
	if (bind(sock, (struct sockaddr *) &ServerAddr, sizeof(ServerAddr))	== SOCKET_ERROR) {
		SysLogger::inst()->err("bind error");
		//closesocket(sock);
		//WSACleanup();
		return -1;
	}

	//Successfull bind, now listen for Server requests.
	if (listen(sock, MAXPENDING) == SOCKET_ERROR) {
		SysLogger::inst()->err("listen error");
		//closesocket(sock);
		//WSACleanup();
		return -1;
	}
	SysLogger::inst()->out("waiting to be contacted for transferring files...\n");

	return 0;
}

unsigned long SockLib::resolve_name(const char *name) {
	struct hostent *host; /* Structure containing host information */

	if ((host = gethostbyname(name)) == NULL) {
		SysLogger::inst()->err("gethostbyname() failed");
		return 0;
	}

	/* Return the binary, network byte ordered address */
	return *((unsigned long *) host->h_addr_list[0]);
}

int SockLib::sock_recv(int sock, char *buf, int length) {
	int ret = SOCKET_ERROR, left = length;
	char *p = buf;

	do {
		ret = recv(sock, p, left, 0);
		if (ret == 0) {
			SysLogger::inst()->err("msg_recv connection closed");
			return -1;
		} else if (ret < 0) {
			SysLogger::inst()->err("msg_recv recv error");
			return -1;
		}
		left -= ret;
		SysLogger::inst()->log("Recv %d bytes, left: %d", ret, left);
		p += ret;
		ret = SOCKET_ERROR;
	} while (left > 0);

	return left;
}

int SockLib::sock_send(int sock, char *buf, int length) {
	int ret = SOCKET_ERROR, left = length;
	char *p = buf;

	do {
		ret = send(sock, p, left, 0);
		if (ret == SOCKET_ERROR) {
			SysLogger::inst()->err("sock_send, len = %d", length);
			return -1;
		}
		left -= ret;
		SysLogger::inst()->log("Send %d bytes, left: %d", ret, left);
		p += ret;
		ret = SOCKET_ERROR;

	} while (left > 0);

	return left;
}

int SockLib::send_file(int sock, const char *filename, int len) {
	FILE *pFile = 0;
	char buf[BUFFER_LENGTH + 1];

	pFile = fopen(filename, "rb");
	if (pFile == NULL) {
		SysLogger::inst()->err("No such a file: %s\n", filename);
		return -1;
	}
	showFile = true;
	if (showFile) {
		printf("\n\n-------------------- File content Begin------------------------\n");
	}
	while (!feof(pFile)) {
		memset((void *)buf, 0, BUFFER_LENGTH + 1);
		//fgets(buf, BUFFER_LENGTH, pFile);
		int cnt = fread(buf, 1, BUFFER_LENGTH, pFile);
		if (sock_sendto(sock, buf, cnt) != 0) {				// call sock_send if TCP
			SysLogger::inst()->err("send_file error. buf.len:%d, file_len:%d\n", strlen(buf), len);
			return -1;
		}
	}
	fclose(pFile);
	if (showFile) {
		printf("\n-------------------- File content End------------------------\n\n");
	}
	showFile = false;

	return 0;
}

int SockLib::recv_file(int sock, const char *filename, int len) {
	FILE *pFile = 0;
	char buf[BUFFER_LENGTH + 1];

	pFile = fopen(filename, "wb");
	if (pFile == NULL) {
		SysLogger::inst()->err("No such a file:%s\n", filename);
		return -1;
	}
	int left = len, recv_len = 0;

	while (left > 0) {
		recv_len = left > BUFFER_LENGTH ? BUFFER_LENGTH : left;
		memset((void *)buf, 0, BUFFER_LENGTH + 1);
		if (sock_recvfrom(sock, buf, recv_len)) {			// call sock_recv if TCP
			SysLogger::inst()->err("recv_file error. %d, %d, %d", recv_len, left, len);
			return -1;
		}
		fwrite(buf, 1, recv_len, pFile);
		//fputs(buf, pFile);
		left -= recv_len;
	}
	fclose(pFile);

	return 0;
}

// ---------------------------------------------
// Assignment 2 UDP

int SockLib::set_dstAddr(const char *dstHostName, int dstPort) {
	if (dstHostName == 0) {
		SysLogger::inst()->err("set_dstAddr params error");
		return -1;
	}
	// specify destination address
	memset(&dstAddr, 0, sizeof(dstAddr));
	dstAddr.sin_family = AF_INET;
	dstAddr.sin_addr.s_addr = resolve_name(dstHostName);
	dstAddr.sin_port = htons(dstPort); 

	return 0;	
}

int SockLib::udp_init(int localPort) {
	if (init()) {
		SysLogger::inst()->err("socket init error");
		return -1;
	}
	
	// bind receiving port
	memset(&localAddr, 0, sizeof(localAddr)); /* Zero out structure */
	localAddr.sin_family = AF_INET; /* Internet address family */
	localAddr.sin_addr.s_addr = htonl(INADDR_ANY); /* Any incoming interface */
	localAddr.sin_port = htons(localPort); /* Local port */
	if (bind(sock, (struct sockaddr *) &localAddr, sizeof(localAddr)) == SOCKET_ERROR) {
		SysLogger::inst()->err("bind error");
		return -1;
	}

	dstAddr.sin_port = 0;

	// generate seq num
	seq = 0;
	lastSeq = -1;
	reset_statistics();	
	showFile = false;
	
	SysLogger::inst()->out("ftp_udp starting on host: [%s:%d]", hostname, localPort);
	return 0;
}

int SockLib::lib_recvfrom(int sock, char *buf, int length, int handshake) {
	int ret = SOCKET_ERROR, left = length;
	char *p = buf;
	fd_set readfds;
	struct timeval *tp = new timeval;
	int waitCnt = 3;		// waiting total time: waitCnt * TIMEOUT_USEC
	SOCKADDR from;
	int fromlen;
	
	tp->tv_sec = 0;
	tp->tv_usec = TIMEOUT_USEC;
	
	while (1) {
		FD_ZERO(&readfds);
		FD_SET(sock, &readfds);
		
		if ((ret = select(sock + 1, &readfds, NULL, NULL, tp)) == SOCKET_ERROR) {
			SysLogger::inst()->err("select error");
			return -1;
			
		} else if (ret == 0) {
			// select timeout
			if (waitCnt <= 1) {
				return 0;		// receive timeout
			}
			waitCnt--;
			
		} else if (ret > 0) {
			if (handshake) {
				return 1;
			}

			// there is something to be received in the windows socket buffer
			fromlen = sizeof(from);
			ret = recvfrom(sock, p, left, 0, &from, &fromlen);
			if (ret == 0) {
				SysLogger::inst()->err("recvfrom: connection closed");
				return -1;
			} else if (ret == SOCKET_ERROR) {
				SysLogger::inst()->err("recvfrom: error");
				return -1;
			}
			
			SysLogger::inst()->log("recvfrom: %d bytes, left: %d", ret, left);
			if (dstAddr.sin_port == 0) {
				// save the destination address
				memmove((void *)&dstAddr, (void *)&from, sizeof(from));
			}
			if (ret != left) {
				return -1;
			}
			break;	// 
			
		} else {
			SysLogger::inst()->err("select unknown error");
			return -1;
		}
	}
	
	return ret;
}

int SockLib::lib_sendto(int sock, char *buf, int length) {
	int ret = SOCKET_ERROR;

	ret = sendto(sock, buf, length, 0, (SOCKADDR *)&dstAddr, sizeof(dstAddr));
	if (ret == SOCKET_ERROR) { 
		SysLogger::inst()->err("lib_sendto, len = %d", length);
		return -1;
	}
	SysLogger::inst()->log("lib_sendto: %d bytes", ret);
	if (ret != length) {
		SysLogger::inst()->err("lib_sendto, ret != length");
		return -1;
	}

	return ret;
}

// param handshake: 0: wait for ack and receive it, 1: wait for ack but not receive, 2: do not wait for ack
int SockLib::udp_sendto(int sock, char *buf, int length, int handshake) {
	int ret = SOCKET_ERROR, retryCnt = 5;
	UDPPACKET udp;

	while (retryCnt > 0) {
		// sendto destination
		if (lib_sendto(sock, buf, length) < 0) {
			return -1;
		}
		if (!handshake) {
			PUDPPACKET pudp = (PUDPPACKET)buf;
			SysLogger::inst()->asslog("Sender: sent packet %d", pudp->seq);
		}
		if (handshake == 2) {
			return 1;
		}

		// wait for ACK
		memset((void *)&udp, 0, sizeof(UDPPACKET));
		ret = lib_recvfrom(sock, (char *)&udp, sizeof(UDPPACKET), handshake);
		
		if (ret == 0) {
			// get ACK timeout, sendto again
			SysLogger::inst()->asslog("Get ACK Timeout, sendto again.");
			retryCnt--;
			reSendCnt++;
			continue;

		} else if (ret < 0) {
			return -1;
		}

		if (!handshake) {
			// check the sequence number
			if (udp.seq != ((seq - 1) & 0x01)) {
				SysLogger::inst()->asslog("Get a Wrong ACK.");
				return -1;
			}
			SysLogger::inst()->asslog("Sender: received ACK for packet %d", udp.seq);
		}
		
		break;
	} 

	return ret;
}

int SockLib::add_udpheader(PUDPPACKET pudp, char *buf) {
	memset((void *)pudp, 0, sizeof(UDPPACKET));

	pudp->seq = seq++;
	memmove(pudp->data, buf, BUFFER_LENGTH);
	return 0;
}

int SockLib::sock_sendto(int sock, char *buf, int length, int handshake) {
	int ret = SOCKET_ERROR, left = length;
	UDPPACKET udp;
	char *p = buf;
	
	// divide the tcp packet into small udp frames
	while (left > 0) {
		// add udp header and copy the data
		if (add_udpheader(&udp, p)) {
			return -1;
		}
		if (udp_sendto(sock, (char *)&udp, sizeof(UDPPACKET), handshake) <= 0) {
			return -1;
		}

		left -= BUFFER_LENGTH;
		p = buf + BUFFER_LENGTH;
		sendCnt++;

		if (showFile) {
			printf("%s\n", udp.data);
		}
	}
	if (left < 0) {
		left = 0;		// to be compatible with TCP
	}

	return left;
}

// send an ACK to the sender
int SockLib::send_ack(unsigned int seq) {
	UDPPACKET udp;

	memset((void *)&udp, 0, sizeof(UDPPACKET));	
	udp.seq = seq;
	lib_sendto(sock, (char *)&udp, sizeof(UDPPACKET));

	SysLogger::inst()->asslog("Receiver: sent an ACK for packet %d", udp.seq);
	return 0;
}

// used by receiver to check if the frame is the same as last one
int SockLib::chk_seq(unsigned int seq) {
	if (lastSeq == -1) {
		lastSeq = seq;
		return 0;
	}

	if (lastSeq == seq) {
		return -1;			// duplicated frame
	}

	lastSeq = seq;
	return 0;
}

int SockLib::sock_recvfrom(int sock, char *buf, int length, int handshake) {
	int ret = SOCKET_ERROR, left = length;
	char *p = buf;
	UDPPACKET udp;

	do {
		memset((void *)&udp, 0, sizeof(UDPPACKET));		
		ret = lib_recvfrom(sock, (char *)&udp, sizeof(UDPPACKET));
		if (ret == 0) {
			continue;
		} else if (ret < 0) {
			return -1;
		}

		if (!handshake) {
			SysLogger::inst()->asslog("Receiver: received packet %d", udp.seq);

			// send ACK
			send_ack(udp.seq);
			
			// check sequence number
			if (chk_seq(udp.seq)) {
				SysLogger::inst()->asslog("Receiver: received same packet %d", udp.seq);
				continue;
			}			
		}

		// copy the data from udp frame
		memmove(p, udp.data, (left < BUFFER_LENGTH) ? left : BUFFER_LENGTH );
		left -= BUFFER_LENGTH;
		SysLogger::inst()->log("sock_recvfrom %d bytes, left: %d", ret, left);
		p += BUFFER_LENGTH;
		ret = SOCKET_ERROR;
		recvCnt++;
	} while (left > 0);
	
	if (left < 0) {
		left = 0;		// to be compatible with TCP
	}

	return left;
}

// wait for requests from client
int SockLib::srv_wait4cnn(int sock) {
	int ret = SOCKET_ERROR;
	fd_set readfds;
	struct timeval *tp = new timeval;
	
	tp->tv_sec = 0;
	tp->tv_usec = TIMEOUT_USEC;
	while (1) {
		FD_ZERO(&readfds);
		FD_SET(sock, &readfds);
		
		if ((ret = select(sock + 1, &readfds, NULL, NULL, tp)) < 0) {
			SysLogger::inst()->err("srv_wait4cnn select error");
			return -1;
			
		} else if (ret == 0) {
			// select timeout
			continue;
		}
		break;
	}
	return 0;
}

void SockLib::reset_statistics() 
{
	reSendCnt = 0;
	recvCnt = 0;
	sendCnt = 0;
}
void SockLib::show_statistics(bool ifSend) 
{
	if (ifSend) {
		SysLogger::inst()->out("Sender: number of effective bytes sent: %d (%d * %d)", 
			sendCnt * sizeof(UDPPACKET), sendCnt, sizeof(UDPPACKET));
		SysLogger::inst()->out("Sender: number of packets sent: %d", sendCnt + reSendCnt);
		SysLogger::inst()->out("Sender: number of bytes sent: %d (%d * %d)\n", 
			(sendCnt + reSendCnt) * sizeof(UDPPACKET), (sendCnt + reSendCnt), sizeof(UDPPACKET));
	} else {
		SysLogger::inst()->out("Receiver: number of bytes received: %d (%d * %d)\n", 
			recvCnt * sizeof(UDPPACKET), recvCnt, sizeof(UDPPACKET));
	}
	reset_statistics();
}
