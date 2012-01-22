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
#ifndef __PROTOCOL_H__
#define __PROTOCOL_H__

#define HOSTNAME_LENGTH 20
#define RESP_LENGTH 40
#define FILENAME_LENGTH 20
#define BUFFER_LENGTH 1024 
#define REQUEST_PORT 5001


typedef enum {
	REQ_SIZE = 1, REQ_TIME, RESP
//Message type
} MSGTYPE;

typedef struct {
	char hostname[HOSTNAME_LENGTH];
	char filename[FILENAME_LENGTH];
} MSGREQUEST, *PMSGREQUEST; //request

typedef struct {
	char response[RESP_LENGTH];
} MSGRESPONSE, *PMSGRESPONSE; //response

typedef struct {
	MSGTYPE type;
	int length; //length of effective bytes in the buffer
	char buffer[BUFFER_LENGTH];
} MSGFMT, *PMSGFMT; //message format used for sending and receiving



#endif