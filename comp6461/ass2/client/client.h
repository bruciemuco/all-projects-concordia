/*
 * COMP6461 Assignment1
 *
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 *
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-01-23 03:02:21 -0500 (Mon, 23 Jan 2012) $
 * $Rev: 14 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6461/ass1/client/client.h $
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


