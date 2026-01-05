#include "SocketSupport.h"

int Load_Socket_Init()
{
#if defined(_WIN32) || defined(_XBOX)
    WSADATA wsa;
    return WSAStartup(MAKEWORD(2, 2), &wsa);
#elif defined(__SWITCH__)
    socketInitializeDefault();
    nxlinkStdio();
    return 0;
#elif defined(__ORBIS__) || defined(__PROSPERO__)
    sceNetInit();
    return 0;
#else
    return 0;
#endif
}

void Load_Socket_Free()
{
#if defined(_WIN32) || defined(_XBOX)
    WSACleanup();
#elif defined(__SWITCH__)
    socketExit();
#elif defined(__ORBIS__) || defined(__PROSPERO__)
    sceNetTerm();
#endif
}

void Load_Socket_Close(socket_t sock)
{
    #if defined(_WIN32) || defined(_XBOX)
        closesocket(sock);
    #else
        close(sock);
    #endif
}

socket_t Load_Create_Server(int port)
{
    socket_t server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) return -1;
    int opt = 1;
#if defined(_WIN32) || defined(_XBOX)
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, (const char*)&opt, sizeof(opt));
#else
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));
#endif
    struct sockaddr_in addr = { 0 };
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(port);
    if (bind(server_fd, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
        Load_Socket_Close(server_fd);
        return -1;
    }
    if (listen(server_fd, 5) < 0) {
        Load_Socket_Close(server_fd);
        return -1;
    }
    return server_fd;
}

socket_t Load_Create_Client(const char* ip, int port)
{
    socket_t sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) return -1;
    struct sockaddr_in addr = { 0 };
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    if (inet_pton(AF_INET, ip, &addr.sin_addr) <= 0) {
        Load_Socket_Close(sock);
        return -1;
    }
    if (connect(sock, (struct sockaddr*)&addr, sizeof(addr)) < 0) {
        Load_Socket_Close(sock);
        return -1;
    }
    return sock;
}

socket_t Load_Create_LinkServerToClient(socket_t server_fd)
{
    struct sockaddr_in client_addr;
    #if defined(_WIN32) || defined(_XBOX)
        int client_len = sizeof(client_addr);
    #else
        socklen_t client_len = sizeof(client_addr);
    #endif
        socket_t client_fd = accept(server_fd, (struct sockaddr*)&client_addr, &client_len);
    return client_fd;
}

int Load_Socket_Send(socket_t sock, const char* msg, const int flags)
{
    return send(sock, msg, (int)strlen(msg), flags);
}

int Load_Socket_Recv(socket_t sock, char* buf, int bufsize)
{
    int bytes = recv(sock, buf, bufsize - 1, 0);
    if (bytes > 0) buf[bytes] = '\0';
    return bytes;
}
