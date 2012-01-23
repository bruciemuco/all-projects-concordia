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

#define RESP_LENGTH 40
#define FILENAME_LENGTH 20
#define BUFFER_LENGTH 8196
#define REQUEST_PORT 5001


//Message type
// request
#define MSGTYPE_STRGET		"get"
#define MSGTYPE_STRPUT		"put"
#define MSGTYPE_REQ_GET		1
#define MSGTYPE_REQ_PUT		2

// response
#define MSGTYPE_RESP_OK		1
#define MSGTYPE_RESP_ERR	2


//typedef struct {
//	char hostname[HOSTNAME_LENGTH];
//	char filename[FILENAME_LENGTH];
//} MSGREQUEST, *PMSGREQUEST; //request
//
//typedef struct {
//	char response[RESP_LENGTH];
//} MSGRESPONSE, *PMSGRESPONSE; //response

// msg header
typedef struct {
	char type;
} MSGHEADER, PMSGHEADER;

//message format used for sending and receiving
typedef struct {
	MSGHEADER header;
	int length; 					//length of msg body
	char body[BUFFER_LENGTH];		// msg body
} MSGFMT, *PMSGFMT;



#endif
