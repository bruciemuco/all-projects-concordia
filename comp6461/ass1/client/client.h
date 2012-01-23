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
#ifndef __CLIENT_H__
#define __CLIENT_H__

#include <winsock.h>
#include "../common/protocol.h"


class TcpClient {
	int sock; /* Socket descriptor */
	struct sockaddr_in ServAddr; /* server socket address */
	unsigned short ServPort; /* server port */
	WSADATA wsadata;

	MSGREQUEST req; /* request */
	PMSGRESPONSE respp; /* pointer to response*/
	MSGFMT smsg, rmsg; /* receive_message and send_message */

public:
	TcpClient() {
	}
	~TcpClient();

	int init(const char *servername);
	int msg_recv(char *buf, int length);
	int msg_send(const char *filename, const char *opname);
	unsigned long resolve_name(const char *name);

	int sock_send(char *data, int length);
	int sock_recv(char *buf, int length);
};


#endif
