/*
   COMP6461 Assignment2

   Yuan Tao (ID: 5977363) 
   Xiaodong Zhang (ID: 6263879) 
 
   Course Instructor: Amin Ranj Bar 
   Lab Instructor: Steve Morse   
   Lab number: Friday 

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

#define CLIENT_RECV_PORT 5000
#define SERVER_RECV_PORT 5001

// router ports
#define ROUTER_RECV_PORT 7000

// select timeout
#define TIMEOUT_USEC 300000 


//#define RESP_LENGTH 40
//#define BUFFER_LENGTH (80 - sizeof(MSGHEADER))
#define BUFFER_LENGTH 80

#define FILENAME_LENGTH 32
#define HOSTNAME_LENGTH 32


//Message type
// request
#define MSGTYPE_STRGET		"get"
#define MSGTYPE_STRPUT		"put"
#define MSGTYPE_REQ_GET			1
#define MSGTYPE_REQ_PUT			2

//#define MSGTYPE_REQ_HOSTNAME	3
//#define MSGTYPE_REQ_FILENAME	4


// response
#define MSGTYPE_RESP_FAILTOGETHEADER	1
#define MSGTYPE_RESP_WRONGHEADER		2
#define MSGTYPE_RESP_UNKNOWNTYPE		3
#define MSGTYPE_RESP_FAILTOGETINFO		4
#define MSGTYPE_RESP_FAILTORECVFILE		5
#define MSGTYPE_RESP_NOFILE				6

#define MSGTYPE_RESP_OK_BASE 			100
#define MSGTYPE_RESP_OK					MSGTYPE_RESP_OK_BASE + 1


//const char *ERROR_MSG[] = {
//	"NULL",
//	"Fail to receive the request header",
//	"Wrong request header",
//	"Unknown request type",
//	"Fail to receive the request data",
//	"Fail to receive the file",
//	"No such a file",
//};


typedef struct {
	char hostname[HOSTNAME_LENGTH];
	char filename[FILENAME_LENGTH];
} MSGREQUEST, *PMSGREQUEST;

//typedef struct {
//	char response[RESP_LENGTH];
//} MSGRESPONSE, *PMSGRESPONSE; //response

// msg header
typedef struct {
	char type;	
	unsigned long len;		// length of the data following this header to be received
} MSGHEADER, *PMSGHEADER;

// UDP packet
typedef struct {
	unsigned int seq:1;			// sequence number
	char data[BUFFER_LENGTH];	// stores TCP packets, in order to reuse code of assignment1
} UDPPACKET, *PUDPPACKET;

// three-way hand shake
typedef struct {
	int clientSeq;
	int serverSeq;
} HANDSHAKE, *PHANDSHAKE;


#endif
