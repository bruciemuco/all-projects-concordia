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


#include <stdio.h>

#include "syslogger.h"
#include "protocol.h"
#include "tcplib.h"

#pragma comment(lib, "ws2_32.lib")

int TcpLib::init() {
	//initilize winsocket
	if (WSAStartup(0x0202, &wsadata) != 0) {
		SysLogger::inst()->err("Error in starting WSAStartup()\n");
		return -1;
	}

	//Display name of local host and copy it to the req
	if (gethostname(hostname, HOSTNAME_LENGTH) != 0) {
		SysLogger::inst()->err("can not get the host name,program exit");
		WSACleanup();
		return -1;
	}

	SysLogger::inst()->log("ftp_tcp starting on host: %s", hostname);

	//Create the socket
	if ((sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) == INVALID_SOCKET) {
		SysLogger::inst()->err("Socket Creating Error");
		WSACleanup();
		return -1;
	}

	return 0;
}

int TcpLib::client_init(const char *servername) {
	if (servername == 0) {
		SysLogger::inst()->err("init params error");
		return -1;
	}
	
	if (init()) {
		SysLogger::inst()->err("socket init error");
		return -1;
	}
	
	//connect to the server
	ServPort = REQUEST_PORT;
	memset(&ServAddr, 0, sizeof(ServAddr)); /* Zero out structure */
	ServAddr.sin_family = AF_INET; /* Internet address family */
	ServAddr.sin_addr.s_addr = resolve_name(servername); /* Server IP address */
	ServAddr.sin_port = htons(ServPort); /* Server port */
	if (connect(sock, (struct sockaddr *) &ServAddr, sizeof(ServAddr)) < 0) {
		SysLogger::inst()->err("Faild to connect to server: %s", servername);
		closesocket(sock);
		WSACleanup();
		return -1;
	}


	return 0;
}

TcpLib::~TcpLib() {
	/* When done uninstall winsock.dll (WSACleanup()) and exit */
	closesocket(sock);
	WSACleanup();
}

unsigned long TcpLib::resolve_name(const char *name) {
	struct hostent *host; /* Structure containing host information */

	if ((host = gethostbyname(name)) == NULL) {
		SysLogger::inst()->err("gethostbyname() failed");
		return 0;
	}

	/* Return the binary, network byte ordered address */
	return *((unsigned long *) host->h_addr_list[0]);
}

int TcpLib::msg_recv(char *buf, int length) {
	return 0;
}

int TcpLib::sock_recv(char *buf, int length) {
	int ret = SOCKET_ERROR;

	do {
		ret = recv(sock, buf, length, 0);
		if (ret == 0) {
			SysLogger::inst()->err("msg_recv connection closed");
			return -1;
		}
		else {
			SysLogger::inst()->err("msg_recv recv error");
			return -1;
		}
	} while (length - ret > 0);
}

int TcpLib::sock_send(char *data, int length) {
	int ret = SOCKET_ERROR;

	do {
		ret = send(sock, (char *)data, length, 0);
		if (ret == SOCKET_ERROR) {
			SysLogger::inst()->err("sock_send, len = %d", length);
			return -1;
		}
	} while (length - ret > 0);

	return 0;
}

int TcpLib::msg_send(const char *filename, const char *opname) {
	if (filename == 0 || opname == 0) {
		SysLogger::inst()->err("msg_send params error");
		return -1;
	}

	// construct the header of the msg
	MSGHEADER header;
	if (strcmp(opname, MSGTYPE_STRGET) == 0)
		header.type = MSGTYPE_REQ_GET;
	else if (strcmp(opname, MSGTYPE_STRPUT) == 0)
		header.type = MSGTYPE_REQ_PUT;
	else {
		SysLogger::inst()->err("Wrong request type\n");
		return -1;
	}

	//send out the header
	sock_send((char *)&header, sizeof(header));

	//receive the response
	MSGHEADER header_resp;
	if (sock_recv((char *)&header_resp, sizeof(header_resp))) {
		SysLogger::inst()->err("failed to get header of response");
		return -1;
	}

	if (header_resp.type != MSGTYPE_RESP_OK) {
		SysLogger::inst()->err("Response ERROR: %d. ", header_resp.type);
		return -1;
	}

	SysLogger::inst()->log("Response OK: %d. ", header_resp.type);

	// start to send file
	return 0;
}
