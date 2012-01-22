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

#define TRACE 0
#define MSGHDRSIZE 8 //Message Header Size

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

	void run(int argc, char * argv[]);
	int msg_recv(int, PMSGFMT);
	int msg_send(int, PMSGFMT);
	unsigned long resolve_name(char name[]);
};


#endif
