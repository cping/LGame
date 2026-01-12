#pragma once
#ifndef LOON_SOCKET
#define LOON_SOCKET
#include "SDLSupport.h"
#if defined(_WIN32) || defined(_WIN64) || defined(_XBOX)
    #define WIN32_LEAN_AND_MEAN
    #include <winhttp.h>
    #include <winsock2.h>
    #include <ws2tcpip.h>
    #include <iphlpapi.h>
    #pragma comment(lib, "winhttp.lib")
    #pragma comment(lib, "ws2_32.lib")
    #pragma comment(lib, "iphlpapi.lib")
    typedef SOCKET socket_t;
#else
    #include <unistd.h>
    #if defined(__ORBIS__) || defined(__PROSPERO__)
#       include <net.h>
    #else
        #include <arpa/inet.h>
    #endif
    #if defined(__SWITCH__) 
        #include <types.h>
        #include <socket.h> 
    #else
        #include <sys/types.h>
        #include <sys/socket.h>
    #endif
    #include <curl/curl.h>
    #include <netinet/in.h>
    #include <fcntl.h>
    #include <netdb.h>
    #include <ifaddrs.h>
    #include <unistd.h>
    #include <errno.h>
    typedef int socket_t;
#endif

#define BUFFER_SIZE 1024

#ifdef __cplusplus
extern "C" {
#endif
    
void ImportSocketInclude();

int64_t GetURLFileSize(const char* url);

void DownloadURL(const char* url,uint8_t* outbytes, int32_t len);

int Load_Socket_Init();

void Load_Socket_Free();

void Load_Socket_Close(socket_t sock);

socket_t Load_Create_Server(int port);

socket_t Load_Create_Client(const char* ip, int port);

socket_t Load_Create_LinkServerToClient(socket_t server_fd);

int Load_Socket_Send(socket_t sock, const char* msg, const int flags);

int Load_Socket_Recv(socket_t sock, char* buf, int bufsize);

void Load_Socket_FirstIP(char* out_ip, int out_size, int prefer_ipv6);

#ifdef __cplusplus
}
#endif

#endif
