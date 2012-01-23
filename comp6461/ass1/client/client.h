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

const char *FILE_DIR_ROOT = "../client_files_root/";

class TcpClient: public TcpLib {

public:
	TcpClient(){};
	~TcpClient(){};
	int start(const char *filename, const char *opname);
};


#endif


