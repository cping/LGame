#include "SocketSupport.h"

void ImportSocketInclude()
{
}

int Load_Socket_Init()
{
#if defined(_WIN32) || defined(_WIN64) || defined(_XBOX)
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
#if defined(_WIN32) || defined(_WIN64) || defined(_XBOX)
    WSACleanup();
#elif defined(__SWITCH__)
    socketExit();
#elif defined(__ORBIS__) || defined(__PROSPERO__)
    sceNetTerm();
#endif
}

void Load_Socket_Close(socket_t sock)
{
    #if defined(_WIN32) || defined(_WIN64) || defined(_XBOX)
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
#if defined(_WIN32) || defined(_WIN64) || defined(_XBOX)
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
    #if defined(_WIN32) || defined(_WIN64) || defined(_XBOX)
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

const char* Load_Socket_FirstIP(char* out_ip, int out_size, int prefer_ipv6)
{
    if (!out_ip || out_size == 0) {
        return "127.0.0.1";
    }
    out_ip[0] = '\0';
#if defined(_WIN32) || defined(_WIN64) || defined(_XBOX)
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        return "127.0.0.1";
    }
    DWORD bufLen = 15000;
    IP_ADAPTER_ADDRESSES* addresses = (IP_ADAPTER_ADDRESSES*)malloc(bufLen);
    if (!addresses) {
        WSACleanup();
        return "127.0.0.1";
    }
    if (GetAdaptersAddresses(AF_UNSPEC, GAA_FLAG_SKIP_ANYCAST | GAA_FLAG_SKIP_MULTICAST | GAA_FLAG_SKIP_DNS_SERVER, NULL, addresses, &bufLen) == NO_ERROR) {
        int order[2] = { prefer_ipv6 ? AF_INET6 : AF_INET, prefer_ipv6 ? AF_INET : AF_INET6 };
        for (int pass = 0; pass < 2; pass++) {
            IP_ADAPTER_ADDRESSES* adapter = addresses;
            while (adapter) {
                IP_ADAPTER_UNICAST_ADDRESS* unicast = adapter->FirstUnicastAddress;
                while (unicast) {
                    if (unicast->Address.lpSockaddr->sa_family == order[pass]) {
                        void* addrPtr = NULL;
                        if (order[pass] == AF_INET) {
                            struct sockaddr_in* ipv4 = (struct sockaddr_in*)unicast->Address.lpSockaddr;
                            if (ipv4->sin_addr.s_addr == htonl(INADDR_LOOPBACK)) { unicast = unicast->Next; continue; }
                            addrPtr = &ipv4->sin_addr;
                        }
                        else {
                            struct sockaddr_in6* ipv6 = (struct sockaddr_in6*)unicast->Address.lpSockaddr;
                            struct in6_addr loopback = IN6ADDR_LOOPBACK_INIT;
                            if (memcmp(&ipv6->sin6_addr, &loopback, sizeof(loopback)) == 0) { unicast = unicast->Next; continue; }
                            if (IN6_IS_ADDR_LINKLOCAL(&ipv6->sin6_addr)) { unicast = unicast->Next; continue; }
                            addrPtr = &ipv6->sin6_addr;
                        }
                        if (addrPtr) {
                            inet_ntop(order[pass], addrPtr, out_ip, (socklen_t)out_size);
                            free(addresses);
                            WSACleanup();
                            return out_ip;
                        }
                    }
                    unicast = unicast->Next;
                }
                adapter = adapter->Next;
            }
        }
    }
    free(addresses);
    WSACleanup();
#else
    struct ifaddrs* ifaddr, * ifa;
    if (getifaddrs(&ifaddr) == -1) {
        return "127.0.0.1";
    }
    int order[2] = { prefer_ipv6 ? AF_INET6 : AF_INET, prefer_ipv6 ? AF_INET : AF_INET6 };
    for (int pass = 0; pass < 2; pass++) {
        for (ifa = ifaddr; ifa != NULL; ifa = ifa->ifa_next) {
            if (!ifa->ifa_addr) continue;
            if (ifa->ifa_addr->sa_family == order[pass]) {
                if (if (order[pass] == AF_INET) { 
                    struct sockaddr_in* ipv4 = (struct sockaddr_in*)ifa->ifa_addr;
                    if (ipv4->sin_addr.s_addr == htonl(INADDR_LOOPBACK)) continue;
                    inet_ntop(AF_INET, &ipv4->sin_addr, out_ip, out_size);
                    freeifaddrs(ifaddr);
                    return out_ip;
                }
                else if (order[pass] == AF_INET6) { 
                    struct sockaddr_in6* ipv6 = (struct sockaddr_in6*)ifa->ifa_addr;
                    struct in6_addr loopback = IN6ADDR_LOOPBACK_INIT;
                    if (memcmp(&ipv6->sin6_addr, &loopback, sizeof(loopback)) == 0) continue;
                    if (IN6_IS_ADDR_LINKLOCAL(&ipv6->sin6_addr)) continue;
                    inet_ntop(AF_INET6, &ipv6->sin6_addr, out_ip, out_size);
                    freeifaddrs(ifaddr);
                    return out_ip;
                }
            }
        }
    }
    freeifaddrs(ifaddr);
#endif
    return "127.0.0.1";
}
