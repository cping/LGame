#ifndef LOON_SOCKET
#define LOON_SOCKET

#if defined(_WIN32) || defined(_XBOX)
	#include <winsock2.h>
	#include <ws2tcpip.h>
	#pragma comment(lib, "ws2_32.lib")
    typedef SOCKET socket_t;
#elif defined(__SWITCH__) 
    #include <socket.h> 
    typedef int socket_t;
#elif defined(__ORBIS__) || defined(__PROSPERO__)
    #include <net.h>
    typedef int socket_t;
#else
    #include <unistd.h>
    #include <arpa/inet.h>
    #include <sys/socket.h>
    #include <netinet/in.h>
    #include <fcntl.h>
    #include <netdb.h>
    typedef int socket_t;
#endif

#define BUFFER_SIZE 1024

#ifdef __cplusplus
extern "C" {
#endif

int Load_Socket_Init();

void Load_Socket_Free();

void Load_Socket_Close(socket_t sock);

socket_t Load_Create_Server(int port);

socket_t Load_Create_Client(const char* ip, int port);

socket_t Load_Create_LinkServerToClient(socket_t server_fd);

int Load_Socket_Send(socket_t sock, const char* msg, const int flags);

int Load_Socket_Recv(socket_t sock, char* buf, int bufsize);

#ifdef __cplusplus
}
#endif

#endif
