/*a small file client
 Usage: suppose client is running on sd1.encs.concordia.ca and server is running on sd2.encs.concordia.ca
 .Also suppose there is a file called test.txt on the server.
 In the client,issuse "client sd2.encs.concordia.ca test.txt size" and you can get the size of the file.
 In the client,issuse "client sd2.encs.concordia.ca test.txt time" and you can get creation time of the file
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


#include <stdio.h>
#include <iostream>
#include <string.h>
#include <windows.h>

#include "client.h"
#include "../common/syslogger.h"

#pragma comment(lib, "ws2_32.lib")

void TcpClient::run(int argc, char * argv[]) {
	if (argc != 4)
		SysLogger::inst()->err("usage: client servername filename size/time");

	//initilize winsocket
	if (WSAStartup(0x0202, &wsadata) != 0) {
		WSACleanup();
		SysLogger::inst()->err("Error in starting WSAStartup()\n");
	}

	//Display name of local host and copy it to the req
	if (gethostname(req.hostname, HOSTNAME_LENGTH) != 0) //get the hostname
		SysLogger::inst()->err("can not get the host name,program exit");
	printf("%s%s\n", "Client starting at host:", req.hostname);

	memmove(req.filename, argv[2], strlen(argv[2]));

	if (strcmp(argv[3], "time") == 0)
		smsg.type = REQ_TIME;
	else if (strcmp(argv[3], "size") == 0)
		smsg.type = REQ_SIZE;
	else
		SysLogger::inst()->err("Wrong request type\n");
	//Create the socket
	if ((sock = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) //create the socket 
		SysLogger::inst()->err("Socket Creating Error");

	//connect to the server
	ServPort = REQUEST_PORT;
	memset(&ServAddr, 0, sizeof(ServAddr)); /* Zero out structure */
	ServAddr.sin_family = AF_INET; /* Internet address family */
	ServAddr.sin_addr.s_addr = resolve_name(argv[1]); /* Server IP address */
	ServAddr.sin_port = htons(ServPort); /* Server port */
	if (connect(sock, (struct sockaddr *) &ServAddr, sizeof(ServAddr)) < 0)
		SysLogger::inst()->err("Socket Creating Error");

	//send out the message
	memcpy(smsg.buffer, &req, sizeof(req)); //copy the request to the msg's buffer
	smsg.length = sizeof(req);
	fprintf(stdout, "Send reqest to %s\n", argv[1]);
	if (msg_send(sock, &smsg) != sizeof(req))
		SysLogger::inst()->err("Sending req packet error.,exit");

	//receive the response
	if (msg_recv(sock, &rmsg) != rmsg.length)
		SysLogger::inst()->err("recv response error,exit");

	//cast it to the response structure
	respp = (PMSGRESPONSE) rmsg.buffer;
	printf("Response:%s\n\n\n", respp->response);

	//close the client socket
	closesocket(sock);

}
TcpClient::~TcpClient() {
	/* When done uninstall winsock.dll (WSACleanup()) and exit */
	WSACleanup();
}

unsigned long TcpClient::resolve_name(char name[]) {
	struct hostent *host; /* Structure containing host information */

	if ((host = gethostbyname(name)) == NULL)
		SysLogger::inst()->err("gethostbyname() failed");

	/* Return the binary, network byte ordered address */
	return *((unsigned long *) host->h_addr_list[0]);
}

/*
 msg_recv returns the length of bytes in the msg_ptr->buffer,which have been recevied successfully.
 */
int TcpClient::msg_recv(int sock, PMSGFMT msg_ptr) {
	int rbytes, n;

	for (rbytes = 0; rbytes < MSGHDRSIZE; rbytes += n)
		if ((n = recv(sock, (char *) msg_ptr + rbytes, MSGHDRSIZE - rbytes, 0))
				<= 0)
			SysLogger::inst()->err("Recv MSGHDR Error");

	for (rbytes = 0; rbytes < msg_ptr->length; rbytes += n)
		if ((n = recv(sock, (char *) msg_ptr->buffer + rbytes,
				msg_ptr->length - rbytes, 0)) <= 0)
			SysLogger::inst()->err("Recevier Buffer Error");

	return msg_ptr->length;
}

/* msg_send returns the length of bytes in msg_ptr->buffer,which have been sent out successfully
 */
int TcpClient::msg_send(int sock, PMSGFMT msg_ptr) {
	int n;
	if ((n = send(sock, (char *) msg_ptr, MSGHDRSIZE + msg_ptr->length, 0))
			!= (MSGHDRSIZE + msg_ptr->length))
		SysLogger::inst()->err("Send MSGHDRSIZE+length Error");
	return (n - MSGHDRSIZE);

}

//argv[1]=servername argv[2]=filename argv[3]=time/size
int main(int argc, char *argv[]) {
	// create logger
	SysLogger::inst()->log("Wellcome to COMP6461 assignment 1.");
	SysLogger::inst()->log("Developed by Yuan Tao & Xiaodong Zhang.");

	TcpClient * tc = new TcpClient();
	tc->run(argc, argv);
	return 0;
}
