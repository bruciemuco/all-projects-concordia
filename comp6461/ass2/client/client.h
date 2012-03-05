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
#ifndef __CLIENT_H__
#define __CLIENT_H__

const char *FILE_DIR_ROOT = "../client_files_root/";

class SockClient: public SockLib {

public:
	SockClient(){};
	~SockClient(){};
	int start(const char *filename, const char *opname);
	int handshake();
};


#endif


