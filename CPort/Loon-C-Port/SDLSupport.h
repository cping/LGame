#pragma once
#ifndef LOON_SDL
#define LOON_SDL

#if defined(_PURE_SDL) || defined(_WIN32) || defined(_WIN64) || defined(__APPLE__) || defined(__linux__) || defined(__unix__) || \
    defined(__FreeBSD__) || defined(__NetBSD__) || defined(__OpenBSD__) || defined(__DragonFly__)
    #define LOON_DESKTOP 1
#else
    #define LOON_DESKTOP 0
#endif

#define MAX_CONTROLLERS 8
#define DEADZONE 0.2f
#define TRIGGER_THRESHOLD 0.05f
#define MAX_EVENTS 1024

#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <stddef.h>
#include <string.h>
#include <math.h>

#if defined(_WIN32) || defined(_WIN64)
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <tchar.h>
#include <tlhelp32.h>
#include <shellapi.h>
#include <shlobj.h>
#include <winbase.h>
#include <VersionHelpers.h>
#pragma comment(lib, "shell32.lib")
static void run_sleep_ms(int ms) {
    Sleep(ms);
}
#elif defined(__linux__) || defined(__APPLE__) || defined(__MACH__) || defined(__unix__)
#include <dlfcn.h>
#include <time.h>
#include <sys/utsname.h>
#include <unistd.h>
#include <pwd.h>
    #ifdef __linux__
    #include <fontconfig/fontconfig.h>
    #endif
    #if !defined(__APPLE__)
    #include <sys/sysinfo.h>
    #endif
#include <time.h>
#include <execinfo.h>
static void run_sleep_ms(int ms) {
    struct timespec ts;
    ts.tv_sec = ms / 1000;
    ts.tv_nsec = (ms % 1000) * 1000000L;
    nanosleep(&ts, NULL);
}
#endif

#ifdef IN_IDE_PARSER
#define GL_GLEXT_PROTOTYPES
#include "GL/gl.h"
#else
    #ifdef GLEW
        #include "GL/glew.h"
    #else
        #include "glad/gles2.h"
    #endif
#endif

#if defined(__SWITCH__)
    #include <switch.h>
    #include <EGL/egl.h>
    #include <EGL/eglext.h>
    #include <sys/errno.h>
#elif defined(_XBOX_ONE) || defined(_XBOX_SERIES_X)
    #include <xdk.h>
#elif defined(__ANDROID__)
    #include <jni.h>
    #include <android/log.h>
#elif defined(__APPLE__)
    #include "TargetConditionals.h"
    #if TARGET_OS_IPHONE
        #include <UIKit/UIKit.h>
        #include <AVFoundation/AVFoundation.h>
    #endif
#elif defined(STEAM_DECK) || defined(__STEAMOS__)
    #include "steam/steam_api.h"
#elif defined(__3DS__)
    #include <3ds.h>
#elif defined(__PSV__)
    #include <psp2/kernel/processmgr.h>
    #include <psp2/io/fcntl.h>
#elif defined(__ORBIS__) || defined(__PROSPERO__) 
    #include <orbis/SystemService.h>
    #include <orbis/UserService.h>
#elif defined(__PSP__)
    #include <pspkernel.h>
    #include <pspdebug.h>
    #include <pspctrl.h>
#elif defined(__EMSCRIPTEN__)
    #include <emscripten/emscripten.h>
#endif

#if defined(_WIN32) || defined(_WIN64) || \
    defined(__ANDROID__) || \
    defined(__XBOX_ONE__) || defined(_DURANGO) || defined(_GAMING_XBOX) || \
    defined(__PS4__) || defined(__PS5__) || defined(__PS3__) || \
    defined(__PSP__) || defined(__vita__) || defined(__3DS__) || \
    defined(__WII__) || defined(__WIIU__) || \
    defined(__EMSCRIPTEN__) || \
    defined(__QNX__) || defined(__OS2__) || \
    (defined(__APPLE__) && TARGET_OS_IPHONE)
    #include <SDL.h>
    #include <SDL_main.h>
    #include <SDL_mixer.h>
    #include <SDL_gamecontroller.h>
#elif defined(__APPLE__) || defined(__linux__) || defined(__SWITCH__) || \
      defined(__FreeBSD__) || defined(__OpenBSD__) || defined(__NetBSD__) || \
      defined(__DragonFly__) || defined(__STEAMOS__) || defined(__STEAM_DECK__) || \
      defined(__HAIKU__) || \
      (defined(__sun) && defined(__SVR4)) || \
      defined(_AIX) || defined(__riscos__) || \
      defined(__MORPHOS__) || defined(__amigaos4__)
    #include <SDL2/SDL.h>
    #include <SDL2/SDL_main.h>
    #include <SDL2/SDL_mixer.h>
    #include <SDL2/SDL_gamecontroller.h>
#else
    #include <SDL.h>
    #include <SDL_main.h>
    #include <SDL_mixer.h>
    #include <SDL_gamecontroller.h>
#endif

#ifdef __WINRT__
#include "winrt/base.h"
#define main main
static int __stdcall wWinMain(HINSTANCE, HINSTANCE, PWSTR, int) {
    AllocConsole();
    FILE* fpstdin = stdin, * fpstdout = stdout, * fpstderr = stderr;
    freopen_s(&fpstdin, "CONIN$", "r", stdin);
    freopen_s(&fpstdout, "CONOUT$", "w", stdout);
    freopen_s(&fpstderr, "CONOUT$", "w", stderr);
    return SDL_WinRTRunApp(SDL_main, NULL);
}
#endif

#ifdef __cplusplus
extern "C" {
#endif

#define true 1
#define false 0

#define MAX_GAMESAVE_PATH_CAHR_LEN 256
#define MAX_GAMESAVE_FILE_LIST 1024
#define MAX_CHUNK_SIZE 8192
#define MAX_TOUCH_DEVICES 16
#define MAX_TEXTINPUT_CAHR_LEN 32

typedef enum {
        LAYOUT_HORIZONTAL, 
        LAYOUT_VERTICAL
} TextLayout;

typedef struct {
    SDL_Surface* surface_data;
    int32_t width;
    int32_t height;
} cache_surface;

typedef struct {
    char name[MAX_GAMESAVE_PATH_CAHR_LEN];
    long size;
} game_saveinfo;

typedef struct {
    char basepath[MAX_GAMESAVE_PATH_CAHR_LEN];
    game_saveinfo files[MAX_GAMESAVE_FILE_LIST];
    int filecount;
} game_filesystem;

typedef struct game_prefnode{
    char* section;
    char* key;
    uint8_t* value;
    size_t value_len;
    struct game_prefnode* next;
} game_prefnode;

typedef struct {
    game_prefnode* head;
} game_preferences;

typedef struct {
    Mix_Music* handle;
    int loopCount; 
} game_music;

typedef struct {
    const char* platform;
    int width;
    int height;
} PlatformResolution;

typedef enum {
    BTN_UP,
    BTN_DOWN,
    BTN_LEFT,
    BTN_RIGHT,
    BTN_CONFIRM,
    BTN_CANCEL,
    BTN_JUMP,
    BTN_SHOOT,
    BTN_MENU,
    BTN_BACK,
    BTN_LB,
    BTN_RB,
    BTN_LS,
    BTN_RS,
    BTN_MISC,
    BTN_MAX
} LogicalButton;

typedef enum {
    AXIS_MOVE_X,
    AXIS_MOVE_Y,
    AXIS_LOOK_X,
    AXIS_LOOK_Y,
    AXIS_MAX
} LogicalAxis;

typedef enum {
    TRIGGER_LEFT,
    TRIGGER_RIGHT,
    TRIGGER_MAX
} LogicalTrigger;

typedef enum {
    EVENT_PRESS,
    EVENT_RELEASE,
    EVENT_HOLD
} ButtonEventType;

typedef struct {
    float axes[AXIS_MAX];
    float triggers[TRIGGER_MAX];
    int buttons[BTN_MAX];
} GamepadState;

extern GamepadState gpStates[MAX_CONTROLLERS];

typedef struct {
    SDL_GameController* controller;
    Uint16 vendor;
    Uint16 product;
    Uint32 buttonDownTime[BTN_MAX];
    int buttonState[BTN_MAX];
} PlayerController;

typedef struct {
    SDL_TouchID* ids;
    int count;
    int capacity;
} TouchIdMap;

static const PlatformResolution platformResTable[] = {
    {"Nintendo Switch", 1280, 720},
    {"Nintendo Switch OLED",1280, 720},
    {"Nintendo Switch Docked", 1920, 1080},
    {"Xbox Series X", 3840, 2160},
    {"Xbox Series S", 2560, 1440},
    {"Xbox One", 1920, 1080},
    {"Xbox One X", 3840, 2160},
    {"PlayStation 4 Pro", 3840, 2160},
    {"PlayStation 5", 3840, 2160},
    {"PlayStation 4", 1920, 1080},
    {"Google Stadia", 1920, 1080},
    {"Steam Deck", 1280, 800},
    {"Amazon Luna", 1920, 1080},
    {"PS Vita", 960, 544},
    {"PSP", 480, 272},
    {"Nintendo 3DS", 400, 240},
    {"Wii U",1920, 1080},
    {"Wii",720, 480},
    {"Windows", 1920, 1080},
    {"Linux", 1920, 1080},
    {"Mac OS X", 2560, 1600},
    {"iOS", 2532, 1170 },
    {"iPadOS", 2732, 2048},
    {"Android", 2400, 1080}
};

static char global_cname[2048];

static char global_info[1024 * 10];

static inline int float_to_int_threshold(float value) {
    return (int)(value);
}

static inline int is_cjk(uint32_t cp) {
    return (cp >= 0x4E00 && cp <= 0x9FFF) || 
           (cp >= 0x3040 && cp <= 0x30FF) || 
           (cp >= 0xAC00 && cp <= 0xD7AF); 
}

static inline int utf8_decode_codepoint(const char *text, int *out_cp) {
    if (!text || !out_cp) return 1; 
    const unsigned char *s = (const unsigned char *)text;
    unsigned char c = s[0];
    if (c < 0x80) { 
        *out_cp = c;
        return 1;
    }
    else if ((c >> 5) == 0x6) {
        *out_cp = ((c & 0x1F) << 6) | (s[1] & 0x3F);
        return 2;
    }
    else if ((c >> 4) == 0xE) { 
        *out_cp = ((c & 0x0F) << 12) | ((s[1] & 0x3F) << 6) | (s[2] & 0x3F);
        return 3;
    }
    else if ((c >> 3) == 0x1E) { 
        *out_cp = ((c & 0x07) << 18) | ((s[1] & 0x3F) << 12) | ((s[2] & 0x3F) << 6) | (s[3] & 0x3F);
        return 4;
    }
    *out_cp = c;
    return 1;
}

uint32_t inline utf8_to_codepoint_full(const char* utf8, int* bytes) {
    const unsigned char* s = (const unsigned char*)utf8;
    uint32_t cp;
    *bytes = 0;
    if (s[0] < 0x80) { 
        *bytes = 1;
        return s[0];
    }
    else if ((s[0] & 0xE0) == 0xC0) {
        if ((s[1] & 0xC0) != 0x80) goto error;
        cp = ((s[0] & 0x1F) << 6) | (s[1] & 0x3F);
        if (cp < 0x80) goto error; 
        *bytes = 2;
        return cp;
    }
    else if ((s[0] & 0xF0) == 0xE0) {
        if ((s[1] & 0xC0) != 0x80 || (s[2] & 0xC0) != 0x80) goto error;
        cp = ((s[0] & 0x0F) << 12) | ((s[1] & 0x3F) << 6) | (s[2] & 0x3F);
        if (cp < 0x800 || (cp >= 0xD800 && cp <= 0xDFFF)) goto error;
        *bytes = 3;
        return cp;
    }
    else if ((s[0] & 0xF8) == 0xF0) { 
        if ((s[1] & 0xC0) != 0x80 || (s[2] & 0xC0) != 0x80 || (s[3] & 0xC0) != 0x80) goto error;
        cp = ((s[0] & 0x07) << 18) | ((s[1] & 0x3F) << 12) | ((s[2] & 0x3F) << 6) | (s[3] & 0x3F);
        if (cp < 0x10000 || cp > 0x10FFFF) goto error; 
        *bytes = 4;
        return cp;
    }
error:
    *bytes = 1;
    return 0xFFFD; 
}

static inline uint32_t utf8_decode(const char** s) {
    const unsigned char* p = (const unsigned char*)*s;
    uint32_t cp;
    if (p[0] < 0x80) {
        cp = p[0];
        *s += 1;
    }
    else if ((p[0] & 0xE0) == 0xC0) {
        cp = ((p[0] & 0x1F) << 6) | (p[1] & 0x3F);
        *s += 2;
    }
    else if ((p[0] & 0xF0) == 0xE0) { 
        cp = ((p[0] & 0x0F) << 12) | ((p[1] & 0x3F) << 6) | (p[2] & 0x3F);
        *s += 3;
    }
    else if ((p[0] & 0xF8) == 0xF0) {
        cp = ((p[0] & 0x07) << 18) | ((p[1] & 0x3F) << 12) | ((p[2] & 0x3F) << 6) | (p[3] & 0x3F);
        *s += 4;
    }
    else {
        cp = 0xFFFD; 
        *s += 1;
    }
    return cp;
}

static inline int utf8_to_utf16(const char* utf8, uint16_t* utf16, size_t max_len) {
    if (!utf8 || !utf16 || max_len == 0) return -1;
    size_t i = 0;
    while (*utf8 && i < max_len - 1) {
        unsigned char c = (unsigned char)*utf8;
        uint32_t codepoint = 0;
        if (c < 0x80) {
            codepoint = c;
            utf8++;
        }
        else if ((c & 0xE0) == 0xC0) { 
            if ((utf8[1] & 0xC0) != 0x80) return -1;
            codepoint = ((c & 0x1F) << 6) | (utf8[1] & 0x3F);
            utf8 += 2;
        }
        else if ((c & 0xF0) == 0xE0) { 
            if ((utf8[1] & 0xC0) != 0x80 || (utf8[2] & 0xC0) != 0x80) return -1;
            codepoint = ((c & 0x0F) << 12) |
                ((utf8[1] & 0x3F) << 6) |
                (utf8[2] & 0x3F);
            utf8 += 3;
        }
        else if ((c & 0xF8) == 0xF0) { 
            if ((utf8[1] & 0xC0) != 0x80 || (utf8[2] & 0xC0) != 0x80 || (utf8[3] & 0xC0) != 0x80) return -1;
            codepoint = ((c & 0x07) << 18) |
                ((utf8[1] & 0x3F) << 12) |
                ((utf8[2] & 0x3F) << 6) |
                (utf8[3] & 0x3F);
            utf8 += 4;
        }
        else {
            return -1;
        }
        if (codepoint <= 0xFFFF) {
            utf16[i++] = (uint16_t)codepoint;
        }
        else if (codepoint <= 0x10FFFF) {
            if (i + 2 >= max_len) return -1;
            codepoint -= 0x10000;
            utf16[i++] = (uint16_t)(0xD800 | (codepoint >> 10));     
            utf16[i++] = (uint16_t)(0xDC00 | (codepoint & 0x3FF));  
        }
        else {
            return -1; 
        }
    }
    utf16[i] = 0;
    return (int)i;
}

static inline int utf16_to_utf8(const uint16_t* utf16, char* utf8, size_t max_len) {
    if (!utf16 || !utf8 || max_len == 0) return -1;
    size_t i = 0;
    while (*utf16 && i < max_len - 1) {
        uint32_t codepoint;
        uint16_t ch = *utf16++;
        if (ch >= 0xD800 && ch <= 0xDBFF) { 
            uint16_t low = *utf16++;
            if (low < 0xDC00 || low > 0xDFFF) return -1;
            codepoint = (((uint32_t)(ch - 0xD800)) << 10) + (low - 0xDC00) + 0x10000;
        }
        else {
            codepoint = ch;
        }
        if (codepoint < 0x80) { 
            utf8[i++] = (char)codepoint;
        }
        else if (codepoint < 0x800) { 
            if (i + 2 >= max_len) return -1;
            utf8[i++] = (char)(0xC0 | (codepoint >> 6));
            utf8[i++] = (char)(0x80 | (codepoint & 0x3F));
        }
        else if (codepoint < 0x10000) {
            if (i + 3 >= max_len) return -1;
            utf8[i++] = (char)(0xE0 | (codepoint >> 12));
            utf8[i++] = (char)(0x80 | ((codepoint >> 6) & 0x3F));
            utf8[i++] = (char)(0x80 | (codepoint & 0x3F));
        }
        else { 
            if (i + 4 >= max_len) return -1;
            utf8[i++] = (char)(0xF0 | (codepoint >> 18));
            utf8[i++] = (char)(0x80 | ((codepoint >> 12) & 0x3F));
            utf8[i++] = (char)(0x80 | ((codepoint >> 6) & 0x3F));
            utf8[i++] = (char)(0x80 | (codepoint & 0x3F));
        }
    }
    utf8[i] = '\0';
    return (int)i;
}

static inline char* chars_strchr(const char* str, int ch, size_t maxlen) {
    if (!str) {
        return NULL;
    }
    if (maxlen == 0) {
        return NULL;
    }
    const unsigned char target = (unsigned char)ch;
    size_t i = 0;
    while (i < maxlen && str[i] != '\0') {
        if ((unsigned char)str[i] == target) {
            return (char*)(str + i);
        }
        i++;
    }
    if (target == '\0' && i < maxlen) {
        return (char*)(str + i);
    }
    return NULL;
}

static inline char* chars_strcpy(char* dest, size_t dest_size, const char* src) {
    if (!dest || !src) {
        return NULL;
    }
    if (dest_size == 0) {
        return NULL;
    }
    if (dest_size > SIZE_MAX) {
        return NULL;
    }
    size_t src_len = 0;
    while (src[src_len] != '\0') {
        src_len++;
    }
    if (src_len + 1 > dest_size) {
        return NULL;
    }
    memcpy(dest, src, src_len + 1);
    return dest;
}

static inline char* chars_strncpy(char* dest, const char* src, size_t n) {
    if (!dest || !src) {
        return NULL;
    }
    if (n == 0) {
        return dest;
    }
    if (n > SIZE_MAX) {
        return NULL;
    }
    size_t src_len = 0;
    while (src_len < n - 1 && src[src_len] != '\0') {
        src_len++;
    }
    if (src_len > 0) {
        memcpy(dest, src, src_len);
    }
    memset(dest + src_len, '\0', n - src_len);
    return dest;
}

void chars_copy(char *dest, const char *src, size_t dest_size) {
    if (!dest || !src || dest_size == 0) {
        return; 
    }
    chars_strncpy(dest, src, dest_size - 1);
    dest[dest_size - 1] = '\0';
}

static inline char* chars_strcat(char* dest, const char* src) {
    char* d = dest;
    if (!dest || !src) return dest;

    while (*d != '\0') d++;
    while (*src != '\0') *d++ = *src++;
    *d = '\0';
    return dest;
}

static inline char* chars_append(char* dest, size_t dest_size, const char* src) {
    if (!dest || !src || dest_size == 0) return NULL;
    char* d = dest;
    size_t used = 0;
    while (*d != '\0' && used < dest_size) {
        d++;
        used++;
    }
    if (used >= dest_size) return NULL;
    while (*src != '\0' && used < dest_size - 1) {
        *d++ = *src++;
        used++;
    }
    *d = '\0';
    if (*src != '\0') return NULL;
    return dest;
}

static inline char* ints_array_to_string(const int64_t* arr, int count, const char* sep) {
    if (!arr || count <= 0 || !sep) return NULL;
    size_t sep_len = strlen(sep);
    size_t total_len = 0;
    for (int i = 0; i < count; i++) {
        char temp[32];
        int len = snprintf(temp, sizeof(temp), "%lld", arr[i]);
        if (len < 0) return NULL;
        total_len += (size_t)len;
        if (i < count - 1) total_len += sep_len;
    }
    char* result = (char*)malloc(total_len + 1);
    if (!result) return NULL;
    result[0] = '\0';
    for (int i = 0; i < count; i++) {
        char temp[32];
        snprintf(temp, sizeof(temp), "%lld", arr[i]);
        chars_append(result, sizeof(result), temp);
        if (i < count - 1) chars_append(result, sizeof(result), sep);
    }
    return result;
}

static inline char* ints_varargs_to_string(const char* sep, int count, ...) {
    if (!sep || count <= 0) return NULL;
    long long* arr = (long long*)malloc(sizeof(long long) * count);
    if (!arr) return NULL;
    va_list args;
    va_start(args, count);
    for (int i = 0; i < count; i++) {
        arr[i] = va_arg(args, long long);
    }
    va_end(args);
    char* result = ints_array_to_string(arr, count, sep);
    free(arr);
    return result;
}

static inline size_t copy_uint8_array(uint8_t* dest, size_t dest_len,
    const uint8_t* src, size_t src_len) {
    if (!dest || !src) {
        return 0;
    }
    size_t bytes_to_copy = (dest_len < src_len) ? dest_len : src_len;
    memcpy(dest, src, bytes_to_copy);
    return bytes_to_copy;
}

static inline size_t copy_int32_array(int32_t* dest, size_t dest_len,
    const int32_t* src, size_t src_len) {
    if (!dest || !src) {
        return 0;
    }
    size_t int_to_copy = (dest_len < src_len) ? dest_len : src_len;
    memcpy(dest, src, int_to_copy * sizeof(int));
    return int_to_copy;
}

static inline size_t copy_float_array(float* dest, size_t dest_len,
    const float* src, size_t src_len) {
    if (!dest || !src) {
        return 0;
    }
    size_t float_to_copy = (dest_len < src_len) ? dest_len : src_len;
    memcpy(dest, src, float_to_copy * sizeof(float));
    return float_to_copy;
}

static inline size_t copy_chars_array(char* dest, size_t dest_len,
    const char* src, size_t src_len) {
    if (!dest || !src) {
        return 0;
    }
    size_t chars_to_copy = (dest_len < src_len) ? dest_len : src_len;
    memcpy(dest, src, chars_to_copy * sizeof(char));
    return chars_to_copy;
}

static inline char* chars_strcasestr(const char* haystack, const char* needle, size_t maxlen) {
    if (!haystack || !needle) {
        return NULL;
    }
    if (maxlen == 0) {
        return NULL;
    }
    if (*needle == '\0') {
        return (char*)haystack;
    }
    size_t i = 0;
    while (i < maxlen && haystack[i] != '\0') {
        size_t j = 0;
        while (needle[j] != '\0' &&
            i + j < maxlen &&
            haystack[i + j] != '\0' &&
            tolower((unsigned char)haystack[i + j]) == tolower((unsigned char)needle[j])) {
            j++;
        }
        if (needle[j] == '\0') {
            return (char*)(haystack + i);
        }
        i++;
    }
    return NULL;
}

static inline bool is_digit_char(uint32_t ch) {
    return (ch >= '0' && ch <= '9') ||      
        (ch >= 0xFF10 && ch <= 0xFF19);   
}

static inline bool is_lowercase(char c) {
    return (c >= 'a' && c <= 'z');
}

static inline bool is_uppercase(char c) {
    return (c >= 'A' && c <= 'Z');
}

static inline bool is_other_char(uint32_t ch) {
    return (ch >= 0xFF21 && ch <= 0xFF3A) || 
        (ch >= 0xFF41 && ch <= 0xFF5A);   
}

static inline  bool is_fullwidth_symbol(uint32_t ch) {
    if ((ch >= 0xFF01 && ch <= 0xFF0F) || 
        (ch >= 0xFF1A && ch <= 0xFF20) || 
        (ch >= 0xFF3B && ch <= 0xFF40) || 
        (ch >= 0xFF5B && ch <= 0xFF5E) ||
        (ch >= 0xFFE0 && ch <= 0xFFE6))
    {
        return true;
    }
    return false;
}

static inline  bool is_halfwidth_symbol(uint32_t ch) {
    if ((ch >= 33 && ch <= 47) ||   
        (ch >= 58 && ch <= 64) || 
        (ch >= 91 && ch <= 96) || 
        (ch >= 123 && ch <= 126)) { 
        return true;
    }
    return false;
}

static inline bool is_symbol(uint32_t ch) {
    return is_halfwidth_symbol(ch) || is_fullwidth_symbol(ch);
}

static inline int fix_font_char_size(const uint32_t ch, float fontSize, int size) {
    int newSize = size;
    if (is_cjk(ch)) {
        return newSize + 1;
    }
    if (is_digit_char(ch)) {
      return newSize += 2;
    } else if (is_lowercase(ch)) {
        if ('i' == ch) {
            return newSize;
        }
        return newSize += 2;
    } else if (is_uppercase(ch)) {
        if ('I' == ch) {
            return newSize;
        }
        return newSize += 2;
    } else if (is_symbol(ch)) {
        return newSize += 1;
    }
    return newSize + 2;
}

static inline void fill_pixels_u8(uint8_t* pixels, size_t length, uint8_t v) {
    if (!pixels||length == 0) return;
    memset(pixels, v, length);
}

static inline void replace_pixels_u8(uint8_t* pixels, size_t length, uint8_t src_pixel, uint8_t dst_pixel) {
    if (!pixels||length == 0) return; 
    if (src_pixel == dst_pixel) {
        return;
    }
    uint8_t* end = pixels + length;
    for (uint8_t* p = pixels; p < end; ++p) {
        if (*p == src_pixel) {
            *p = dst_pixel;
        }
    }
}

static inline void fill_pixels(uint32_t* pixels, size_t length, uint32_t v) {
    if (!pixels||length == 0) return; 
    uint32_t* end = pixels + length;
    while (pixels + 4 <= end) {
        pixels[0] = v;
        pixels[1] = v;
        pixels[2] = v;
        pixels[3] = v;
        pixels += 4;
    }
    while (pixels < end) {
        *pixels++ = v;
    }
}

static inline void replace_pixels(uint32_t* pixels, size_t length, uint32_t target, uint32_t replacement) {
    if (!pixels||length == 0) return; 
    uint32_t* end = pixels + length;
    while (pixels + 4 <= end) {
        if (pixels[0] == target) {
            pixels[0] = replacement;
        }
        if (pixels[1] == target) {
            pixels[1] = replacement;
        }
        if (pixels[2] == target) {
            pixels[2] = replacement;
        }
        if (pixels[3] == target) {
            pixels[3] = replacement;
        }
        pixels += 4;
    }
    while (pixels < end) {
        if (*pixels == target) {
            *pixels = replacement;
        }
        pixels++;
    }
}

typedef void (*ButtonCallback)(int playerIndex, LogicalButton btn, ButtonEventType type);
typedef void (*AxisCallback)(int playerIndex, LogicalAxis axis, float value);
typedef void (*TriggerCallback)(int playerIndex, LogicalTrigger trigger, float value);

void ImportSDLInclude();

char* ConvertTeaVMString(uint16_t* chars);

bool ISDebugStatus();

bool CreateSingleInstanceLock();

void FreeSingleLock();

void SDL_AllowExit(bool a);

void LOG_Println(const char* mes);

int64_t CreatePrefs();

bool LoadPrefs(int64_t handle, const char* filename);

void SetPrefs(int64_t handle, const char* section, const char* key,
    const uint8_t* value, size_t value_len);

int64_t GetPrefs(int64_t handle, const char* section, const char* key, uint8_t* outBytes);

const char* GetPrefsKeys(int64_t handle, const char* section, const char* delimiter);

bool SavePrefs(int64_t handle, const char* filename);

void RemovePrefs(int64_t handle, const char* section, const char* key);

void FreePrefs(int64_t handle);

int64_t CreateGameData(char* fileName);

int64_t ReadGameData(const int64_t handle, const char* filename, uint8_t* outBytes);

bool WriteGameData(const int64_t handle, const char* filename, const char* data, int64_t size);

int32_t GetGameDataFileCount(const int64_t handle);

void FreeGameData(const int64_t handle);

char* GetPathFullName(const char* path);

const char* GetSystemProperty(const char* key);

bool FileExists(const char* filename);

int Load_SDL_OpenURL(const char* url);

const char* Load_SDL_RW_FileToChars(const char* filename);

int64_t Load_SDL_RW_FileSize(const char* filename);

int64_t Load_SDL_RW_FileToBytes(const char* filename, uint8_t* outBytes);

int Load_SDL_GetNumTouchDevices();

bool  Load_SDL_IsTextInput();

void Load_SDL_SetTextInput(bool b);

bool Load_SDL_RW_FileExists(const char* filename);

void Load_SDL_GL_SwapScreen();

void Load_SDL_GL_SwapWindowHandle(const int64_t handle);

int Load_SDL_WindowWidth();

int Load_SDL_WindowHeight();

const char* Load_SDL_GetPreferredLocales();

const char* Load_SDL_GetBasePath();

const char* Load_SDL_GetPrefPath(const char* org, const char* app);

const char* Load_SDL_GetPlatform();

bool Load_SDL_IsGameController(int32_t joystickIndex);

int64_t Load_SDL_GameControllerOpen(int32_t joystickIndex);

int64_t Load_SDL_GameControllerTemp();

void Load_SDL_GameControllerClose(const int64_t handle);

const char* Load_SDL_GameControllerName(const int64_t handle);

const char* Load_SDL_GameControllerPath(const int64_t handle);

int32_t Load_SDL_GameControllerGetType(const int64_t handle);

int32_t Load_SDL_GameControllerGetPlayerIndex(const int64_t handle);

void Load_SDL_GameControllerSetPlayerIndex(const int64_t handle, int32_t joystickIndex);

int16_t Load_SDL_GameControllerGetVendor(const int64_t handle);

int32_t Load_SDL_GameControllerGetNumTouchpads(const int64_t handle);

const char* Load_SDL_GameControllerNameForIndex(int32_t joystickIndex);

const char* Load_SDL_GameControllerPathForIndex(int32_t joystickIndex);

const char* Load_SDL_GameControllerMappingForIndex(int32_t mappingIndex);

const char* Load_SDL_GameControllerMappingForDeviceIndex(int32_t joystickIndex);

int32_t Load_SDL_GameControllerTypeForIndex(int32_t joystickIndex);

int32_t Load_SDL_GameControllerEventState(int32_t state);

int32_t Load_SDL_GameControllerAddMapping(const char* mappingString);

int32_t Load_SDL_GameControllerGetAxisFromString(const char* axisString);

int32_t Load_SDL_GameControllerGetButtonFromString(const char* btnString);

int32_t Load_SDL_GameControllerNumMappings();

void Load_SDL_GameControllerUpdate();

int64_t Load_SDL_JoystickOpen(int32_t deviceIndex);

void Load_SDL_JoystickClose(const int64_t handle);

int64_t Load_SDL_GameControllerGetJoystick(const int64_t handle);

const char* Load_SDL_JoystickGetGUIDString(const int64_t handle);

int32_t Load_SDL_JoystickNumAxes(const int64_t handle);

int32_t Load_SDL_JoystickNumBalls(const int64_t handle);

int32_t Load_SDL_JoystickNumHats(const int64_t handle);

int32_t Load_SDL_JoystickNumButtons(const int64_t handle);

void Load_SDL_LockJoysticks();

void Load_SDL_UnlockJoysticks();

int32_t Load_SDL_NumJoysticks();

int16_t Load_SDL_JoystickGetDeviceVendor(int32_t deviceIndex);

int16_t Load_SDL_JoystickGetDeviceProduct(int32_t deviceIndex);

int16_t Load_SDL_JoystickGetDeviceProductVersion(int32_t deviceIndex);

int32_t Load_SDL_JoystickDetachVirtual(int32_t deviceIndex);

bool Load_SDL_JoystickIsVirtual(int32_t deviceIndex);

const char* Load_SDL_JoystickNameForIndex(int32_t deviceIndex);

const char* Load_SDL_JoystickPathForIndex(int32_t deviceIndex);

int32_t Load_SDL_JoystickGetDevicePlayerIndex(int32_t deviceIndex);

int64_t Load_SDL_SensorOpen(int32_t deviceIndex);

void Load_SDL_SensorClose(const int64_t handle);

const char* Load_SDL_SensorGetName(const int64_t handle);

int32_t Load_SDL_SensorGetType(const int64_t handle);

int32_t Load_SDL_SensorGetNonPortableType(const int64_t handle);

int32_t Load_SDL_SensorGetData(const int64_t handle, float* data, int32_t numValues);

void Load_SDL_LockSensors();

void Load_SDL_UnlockSensors();

int32_t Load_SDL_NumSensors();

const char* Load_SDL_GetText();

const char* Load_SDL_SensorGetDeviceName(int32_t deviceIndex);

int32_t Load_SDL_SensorGetDeviceType(int32_t deviceIndex);

int32_t Load_SDL_SensorGetDeviceNonPortableType(int32_t deviceIndex);

void Load_SDL_SensorUpdate();

int32_t Load_SDL_GetPolleventType();

int32_t Load_SDL_GetTicks();

int64_t Load_SDL_GetTicks64();

void Call_SDL_DestroyWindow();

int32_t Load_SDL_GetKeyStates(int32_t* keys);

int32_t Load_SDL_GetPressedKeys(int32_t* keys);

int32_t Load_SDL_GetReleasedKeys(int32_t* keys);

int32_t Load_SDL_GetLastPressedScancode();

void Load_SDL_Current_Screen_Size(int32_t* values);

void Load_SDL_Current_Window_Size(int32_t* values);

bool Load_SDL_Pause();

void Load_RemapControllers(const int min, const int max, const int dualJoy, const int singleMode);

bool Load_IsConnected(const int controller);

int Load_Buttons();

int32_t Load_Axes(const int controller, float* axes);

bool Load_SDL_Exit(const int run);

void Load_SDL_GetDrawableSize(const int64_t window, int32_t* values);

void Load_SDL_GetWindowSize(const int64_t window, int32_t* values);

int Load_SDL_LockSurface(const int64_t handle);

void Load_SDL_UnlockSurface(const int64_t handle);

void Load_SDL_Delay(const int32_t d);

int64_t Load_SDL_CreateRGBSurface(const int32_t flags, const int width, const int height, const int depth,int32_t rmask, const int32_t gmask, const int32_t bmask, const int32_t amask);

int64_t Load_SDL_CreateRGBSurfaceFrom(const int32_t* pixels,const int w,const int h,const int format);

int64_t Load_SDL_ConvertSurfaceFormat(const int64_t handle, int32_t pixel_format, int32_t flags);

void Load_SDL_GetSurfaceSize(const int64_t handle, int32_t* values);

void Load_SDL_GetSurfacePixels32(const int64_t handle, const int order, int32_t* pixels);

void Load_SDL_SetPixel(const int64_t handle, int x, int y, int32_t pixel);

void Load_SDL_SetPixel32(const int64_t handle, int x, int y, int32_t pixel);

void Load_SDL_SetPixels32(const int64_t handle, int nx, int ny, int nw, int nh, int32_t* pixels);

int64_t Load_SDL_LoadBMPHandle(const char* path);

bool Load_SDL_MUSTLockSurface(const int64_t handle);

void Load_SDL_SetSurfaceBlendMode(const int64_t handle,const int mode);

int Load_SDL_GetSurfaceBlendMode(const int64_t handle);

void Load_SDL_FillRect(const int64_t handle, const int x, const int y, const int w, const int h, const int r, const int g, const int b, const int a);

void Load_SDL_SetClipRect(const int64_t handle, const int x, const int y, const int w, const int h);

void Load_SDL_GetClipRect(const int64_t handle,int32_t* values);

int32_t Load_SDL_GetFormat(const int64_t handle);

bool Load_SDL_Update();

int32_t Load_SDL_TouchData(int32_t* data);

void Load_SDL_Cleanup();

int64_t Load_SDL_ScreenInit(const char* title,const int w,const int h, const bool vsync, const bool emTouch, const int flags, const bool debug);

bool Load_SDL_PathIsFile(char* path);

int Load_SDL_Init(const int flags);

int Load_SDL_InitSubSystem(const int flags);

int Load_SDL_WasInit(const int flags);

void Load_SDL_QuitSubSystem(const int flags);

const char* Load_SDL_GetError();

int Load_SDL_SetClipboardText(const char* text);

char* Load_SDL_GetClipboardText();

bool Load_SDL_HasClipboardText();

int32_t Load_SDL_SetPrimarySelectionText(const char* text);

const char* Load_SDL_GetPrimarySelectionText();

void Load_SDL_MaximizeWindow(const int64_t handle);

void Load_SDL_MinimizeWindow(const int64_t handle);

int Load_SDL_SetWindowFullscreen(const int64_t handle, const int flags);

void Load_SDL_SetWindowBordered(const int64_t handle, const bool bordered);

void Load_SDL_SetWindowSize(const int64_t handle, const int w, const int h);

void Load_SDL_SetWindowPosition(const int64_t handle, const int x, const int y);

int Load_SDL_GetWindowDisplayIndex(const int64_t handle);

void Load_SDL_GetDisplayUsableBounds(const int display, int32_t* xywh);

void Load_SDL_GetDisplayBounds(const int display, int32_t* xywh);

int Load_SDL_GetNumVideoDisplays();

int Load_SDL_GetWindowFlags(const int64_t handle);

void Load_SDL_SetWindowTitle(const int64_t handle,const char* title);

void Load_SDL_Gamepad_Init(const bool debugMode);

bool Load_SDL_Gamepad_IsSupported();

void Load_SDL_Gamepad_Close();

void Load_SDL_Gamepad_GetState(int32_t playerIndex, float* axesOut, float* triggersOut, int* buttonsOut);

int32_t Load_SDL_Gamepad_PollEvents(int32_t* outData);

int64_t Load_SDL_CreateRGBSurfaceFrom32(void* pixels, int width, int height);

int64_t Load_SDL_CreateColorCursor(const int64_t surface, const int hotx, const int hoty);

int64_t Load_SDL_CreateSystemCursor(const int type);

void Load_SDL_SetCursor(const int64_t handle);

void Load_SDL_FreeCursor(const int64_t handle);

void Load_SDL_FreeSurface(const int64_t handle);

void Load_SDL_FreeTempSurface();

int Load_SDL_ShowSimpleMessageBox(const int flags, const char* title, const char* message);

void Load_SDL_StartTextInput();

void Load_SDL_StopTextInput();

bool Load_SDL_IsTextInputActive();

bool Load_SDL_GL_ExtensionSupported(const char* exte);

int Load_SDL_GL_SetSwapInterval(int on);

int64_t Load_SDL_GL_CreateContext(const int64_t window);

int Load_SDL_GL_SetAttribute(const int attribute, const int value);

void Load_SDL_SetTextInputRect(int x, int y, int w, int h);

void Load_SDL_RestoreWindow(const int64_t handle);

void Load_SDL_SetWindowIcon(const int64_t handle, const int64_t surface);

void Load_SDL_DestroyWindow(const int64_t handle);

void Call_SDL_GetDrawableSize(int* values);

void Call_SDL_GetWindowSize(int* values);

void Call_SDL_MaximizeWindow();

void Call_SDL_MinimizeWindow();

int Call_SDL_SetWindowFullscreen(const int flags);

void Call_SDL_SetWindowBordered(const bool bordered);

void Call_SDL_SetWindowSize(const int w, const int h);

void Call_SDL_SetWindowPosition(const int x, const int y);

int Call_SDL_GetWindowDisplayIndex();

int Call_SDL_GetWindowFlags();

void Call_SDL_SetWindowTitle(const char* title);

void Call_SDL_RestoreWindow();

void Call_SDL_SetWindowIcon(const int64_t handle);

void Call_SDL_GL_SwapWindow();

void Call_SDL_GetRenderScale(float* scales);

int64_t Call_SDL_GL_CreateContext();

bool Load_SDL_SetHint(const char* name,const char* value);

int64_t Load_SDL_CreateWindow(const char* title,
    const int w,
    const int h, const int flags);

int Load_SDL_PollEvent(char* data);

char* Load_SDL_GetCompiledVersion(char* data);

char* Load_SDL_GetVersion(char* data);

int64_t Load_SDL_Mix_LoadMUS(const char* filename);

int64_t Load_SDL_Mix_LoadMUSFromMem(void* musData);

void Load_SDL_Mix_PlayMusic(const int64_t handle, const bool looping);

void Load_SDL_Mix_PlayFadeInMusic(const int64_t handle, const bool looping);

float Load_SDL_Mix_GetMusicPosition(const int64_t handle);

bool Load_SDL_Mix_IsLoopingMusic();

bool Load_SDL_Mix_PlayingMusic();

void Load_SDL_Mix_PlayMusicFadeStop();

void Load_SDL_MIX_SetMusicPosition(const float position);

void Load_SDL_Mix_SetMusicVolume(const float volume);

float Load_SDL_Mix_GetMusicVolume();

void Load_SDL_Mix_PauseMusic();

void Load_SDL_Mix_ResumeMusic();

void Load_SDL_Mix_HaltMusic();

void Load_SDL_Mix_DisposeMusic(const int64_t handle);

int64_t Load_SDL_Mix_LoadSound(const char* filename);

int64_t Load_SDL_Mix_LoadSoundFromMem(void* wavData);

int Load_SDL_Mix_PlaySound(const int64_t handle, const bool looping);

bool Load_SDL_Mix_IsLoopingSound(const int32_t channel);

int Load_SDL_Mix_SetPlaySoundLooping(const int64_t handle, const int channel, const bool looping);

void Load_SDL_Mix_SetPosition(const int32_t channel, const int32_t angle, const int32_t distance);

void Load_SDL_Mix_FadeInChannel(const int32_t channel, const int32_t ms);

void Load_SDL_Mix_FadeOutChannel(const int32_t channel, const int32_t ms);

bool Load_SDL_Mix_Playing(const int32_t channel);

void Load_SDL_Mix_PauseSound(const int channel);

void Load_SDL_Mix_ResumeSound(const int channel);

int Load_SDL_Mix_SetVolume(const int channel, const float volume);

int Load_SDL_Mix_GetVolume(const int channel);

int Load_SDL_Mix_SetPan(const int channel, const float pan);

int Load_SDL_Mix_HaltSound(const int channel);

void Load_SDL_Mix_DisposeSound(const int64_t handle);

void Load_SDL_Mix_CloseAudio();

int64_t Load_SDL_WindowHandle();

void Load_SDL_Quit();

bool Load_SDL_QuitRequested();

const char* Load_GL_Init();

void Load_GL_UseProgram(const int program);

void Load_GL_ValidateProgram(const int program);

void Load_GL_ActiveTexture(const int tex);

void Load_GL_BindTexture(const int target, const int tex);

void Load_GL_BlendFunc(const int sfactor,const int dfactor);

void Load_GL_Clear(const int mask);

void Load_GL_ClearColor(const float red, const float green, const float blue, const float alpha);

void Load_GL_ClearDepthf(const float depth);

void Load_GL_ClearStencil(const int sc);

void Load_GL_ColorMask(const bool red, const bool green, const bool blue, const bool alpha);

void Load_GL_CompressedTexImage2D(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int imageSize, void* data);

void Load_GL_CompressedTexImage2DOffset(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int imageSize, const int64_t offset);

void Load_GL_CompressedTexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int imageSize, void* data);

void Load_GL_CompressedTexSubImage2DOffset(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int imageSize, const int64_t offset);

void Load_GL_CopyTexImage2D(const int target, const int level, const int internalformat, const int x, const int y, const int width, const int height, const int border);

void Load_GL_CopyTexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int x, const int y, const int width, const int height);

void Load_GL_CullFace(const int mode);

void Load_GL_DeleteTexture(const int texture);

void Load_GL_DeleteTextures(const int n, const void* textures);

void Load_GL_DepthFunc(const int func);

void Load_GL_DepthMask(const bool flag);

void Load_GL_DepthRangef(const float zNear, const float zFar);

void Load_GL_Disable(const int cap);

void Load_GL_DrawArrays(const int mode, const int first, const int count);

void Load_GL_DrawElements(const int mode, const int count, const int type, const void* indices);

void Load_GL_DrawElementsOffset(const int mode, const int count, const int type, const int64_t offset);

void Load_GL_Enable(const int cap);

void Load_GL_Finish();

void Load_GL_Flush();

void Load_GL_FrontFace(const int mode);

int Load_GL_GenTexture();

void Load_GL_GenTextures(const int n, const void* textures);

int Load_GL_GenRenderbuffer();

int Load_GL_GetError();

char* Load_GL_GetString(const int name);

void Load_GL_Hint(const int target, const int mode);

void Load_GL_LineWidth(const float width);

void Load_GL_PixelStorei(const int pname, const int param);

void Load_GL_PolygonOffset(const float factor, const float units);

void Load_GL_ReadPixels(const int x, const int y, const int width, const int height, const int format, const int type, void* pixels);

void Load_GL_ReadPixelsOffset(const int x, const int y, const int width, const int height, const int format, const int type, const int64_t pixelsOffset);

void Load_GL_Scissor(const int x, const int y, const int width, const int height);

void Load_GL_StencilFunc(const int func, const int ref, const int mask);

void Load_GL_StencilMask(const int mask);

void Load_GL_StencilOp(const int fail, const int zfail, const int zpass);

void Load_GL_TexImage2D(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int format, const int type, const void* pixels);

void Load_GL_TexImage2DOffset(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int format, const int type, const int64_t pixelsOffset);

void Load_GL_TexParameterf(const int target, const int pname, const float param);

void Load_GL_TexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int type, const void* pixels);

void Load_GL_TexSubImage2DOffset(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int type, const int64_t pixelsOffset);

void Load_GL_Viewport(const int x, const int y, const int width, const int height);

void Load_GL_AttachShader(const int program, const int shader);

void Load_GL_BindAttribLocation(const int program, const int index, const char* name);

void Load_GL_BindBuffer(const int target, const int buffer);

void Load_GL_BindFramebuffer(const int target, const int framebuffer);

void Load_GL_BindRenderbuffer(const int target, const int renderbuffer);

void Load_GL_BlendColor(const float red, const float green, const float blue, const float alpha);

void Load_GL_BlendEquation(const int mode);

void Load_GL_BlendEquationSeparate(const int modeRGB, const int modeAlpha);

void Load_GL_BlendFuncSeparate(const int srcRGB, const int dstRGB, const int srcAlpha, const int dstAlpha);

void Load_GL_BufferData(const int target, const int size, const void* data, const int usage);

void Load_GL_BufferSubData(const int target, const int offset, const int size, const void* data);

int Load_GL_CheckFramebufferStatus(const int target);

void Load_GL_CompileShader(const int shader);

int Load_GL_CreateProgram();

int Load_GL_CreateShader(const int type);

void Load_GL_DeleteBuffer(const int buffer);

void Load_GL_DeleteBuffers(const int n, const void* buffers);

void Load_GL_DeleteFramebuffer(const int framebuffer);

void Load_GL_DeleteFramebuffers(const int n, const void* framebuffer);

void Load_GL_DeleteProgram(const int program);

void Load_GL_DeleteRenderbuffer(const int renderbuffer);

void Load_GL_DeleteRenderbuffers(const int n, const void* renderbuffers);

void Load_GL_DeleteShader(const int shader);

void Load_GL_DetachShader(const int program, const int shader);

void Load_GL_DisableVertexAttribArray(const int index);

void Load_GL_EnableVertexAttribArray(const int index);

void Load_GL_FramebufferRenderbuffer(const int target, const int attachment, const int renderbuffertarget, const int renderbuffer);

void Load_GL_FramebufferTexture2D(const int target, const int attachment, const int textarget, const int texture, const int level);

int Load_GL_GenBuffer();

void Load_GL_GenBuffers(const int n, const void* buffers);

void Load_GL_GenRenderbuffers(const int n, const void* buffers);

void Load_GL_GenerateMipmap(const int target);

int Load_GL_GenFramebuffer();

void Load_GL_GenFramebuffers(const int n, const void* buffers);

char* Load_GL_GetActiveAttrib(const int program, const int index,const void* size,const void* type);

char* Load_GL_GetActiveUniform(const int program, const int index, const void* size, const void* type);

int Load_GL_GetAttribLocation(const int program, const char* name);

void Load_GL_GetBooleanv(const int pname, const void* params);

bool Load_GL_GetBooleanvResult(const int pname);

void Load_GL_GetBufferParameteriv(const int target, const int pname, const void* params);

void Load_GL_GetFloatv(const int pname, const void* params);

float Load_GL_GetFloatvResult(const int pname);

void Load_GL_GetInteger(const int pname, const void* params);

void Load_GL_GetIntegerv(const int pname, const void* params);

int Load_GL_GetIntegervResult(const int pname);

void Load_GL_GetFramebufferAttachmentParameteriv(const int target, const int attachment, const int pname, const void* params);

void Load_GL_GetProgramiv(const int program, const int pname, const void* params);

char* Load_GL_GetProgramInfoLog(const int program);

const char* Load_GL_GetProgramInfoLogs(const int program, const int bufsize, const void* length, const void* infolog);

void Load_GL_GetRenderbufferParameteriv(const int target, const int pname, const void* params);

void Load_GL_GetShaderiv(const int shader, const int pname, const void* params);

const char* Load_GL_GetShaderInfoLog(const int shader);

const char* Load_GL_GetShaderInfoLogs(const int shader, const int bufsize, const void* length, const void* infolog);

void Load_GL_GetShaderPrecisionFormat(const int shadertype, const int precisiontype, const void* range, const void* precision);

void Load_GL_ShaderBinary(int count, const void* shaders, int binaryFormat, const void* binary, int length);

void Load_GL_GetTexParameterfv(const int target, const int pname, const void* params);

void Load_GL_GetTexParameteriv(const int target, const int pname, const void* params);

void Load_GL_GetUniformfv(const int program, const int location, const void* params);

void Load_GL_GetUniformiv(const int program, const int location, const void* params);

int Load_GL_GetUniformLocation(const int program, const char* name);

void Load_GL_GetVertexAttribfv(const int index, const int pname, const void* params);

void Load_GL_GetVertexAttribiv(const int index, const int pname, const void* params);

bool Load_GL_IsBuffer(const int buffer);

bool Load_GL_IsEnabled(const int cap);

bool Load_GL_IsFramebuffer(const int framebuffer);

bool Load_GL_IsProgram(const int program);

bool Load_GL_IsRenderbuffer(const int renderbuffer);

bool Load_GL_IsShader(const int shader);

bool Load_GL_IsTexture(const int texture);

void Load_GL_LinkProgram(const int program);

void Load_GL_ReleaseShaderCompiler();

void Load_GL_RenderbufferStorage(const int target, const int internalformat, const int width, const int height);

void Load_GL_SampleCoverage(const float value, const bool invert);

void Load_GL_ShaderSource(const int shader, const char* string);

void Load_GL_StencilFuncSeparate(const int face, const int func, const int ref, const int mask);

void Load_GL_StencilMaskSeparate(const int face,const int mask);

void Load_GL_StencilOpSeparate(const int face, const int fail, const int zfail, const int zpass);

void Load_GL_TexParameterfv(const int target, const int pname, const void* params);

void Load_GL_TexParameteri(const int target, const int pname, const int param);

void Load_GL_TexParameteriv(const int target, const int pname, const void* params);

void Load_GL_Uniform1f(const int location, const float x);

void Load_GL_Uniform1fv(const int location, const int count, const void* v);

void Load_GL_Uniform1fvOffset(const int location, const int count, const void* v, const int offset);

void Load_GL_Uniform1i(const int location, const int x);

void Load_GL_Uniform1iv(const int location, const int count, const void* v);

void Load_GL_Uniform1ivOffset(const int location, const int count, const void* v, const int offset);

void Load_GL_Uniform2f(const int location, const float x, const float y);

void Load_GL_Uniform2fv(const int location, const int count, const void* v);

void Load_GL_Uniform2fvOffset(const int location, const int count, const void* v, const int offset);

void Load_GL_Uniform2i(const int location, const int x, const int y);

void Load_GL_Uniform2iv(const int location, const int count, const void* v);

void Load_GL_Uniform2ivOffset(const int location, const int count, const void* v, const int offset);

void Load_GL_Uniform3f(const int location, const float x, const float y, const float z);

void Load_GL_Uniform3fv(const int location, const int count, const void* v);

void Load_GL_Uniform3fvOffset(const int location, const int count, const void* v, const int offset);

void Load_GL_Uniform3i(const int location, const int x, const int y, const int z);

void Load_GL_Uniform3iv(const int location, const int count, const void* v);

void Load_GL_Uniform3ivOffset(const int location, const int count, const void* v, const int offset);

void Load_GL_Uniform4f(const int location, const float x, const float y, const float z, const float w);

void Load_GL_Uniform4fv(const int location, const int count, const void* v);

void Load_GL_Uniform4fvOffset(const int location,const int count,const void* v,const int offset);

void Load_GL_Uniform4i(const int location, const int x, const int y, const int z, const int w);

void Load_GL_Uniform4iv(const int location, const int count, const void* v);

void Load_GL_Uniform4ivOffset(const int location, const int count, const void* v, const int offset);

void Load_GL_UniformMatrix2fv(const int location, const int count, const bool transpose, const void* value);

void Load_GL_UniformMatrix2fvOffset(const int location, const int count, const bool transpose, const void* value, const int offset);

void Load_GL_UniformMatrix3fv(const int location, const int count, const bool transpose, const void* value);

void Load_GL_UniformMatrix3fvOffset(const int location, const int count, const bool transpose, const void* value, const int offset);

void Load_GL_UniformMatrix4fv(const int location, const int count, const bool transpose, const void* value);

void Load_GL_UniformMatrix4fvOffset(const int location, const int count, const bool transpose, const void* value, const int offset);

void Load_GL_VertexAttrib1f(const int indx, const float x);

void Load_GL_VertexAttrib1fv(const int indx, const void* values);

void Load_GL_VertexAttrib2f(const int indx, const float x, const float y);

void Load_GL_VertexAttrib2fv(const int indx, const void* values);

void Load_GL_VertexAttrib3f(const int indx, const float x, const float y, const float z);

void Load_GL_VertexAttrib3fv(const int indx, const void* values);

void Load_GL_VertexAttrib4f(const int indx, const float x, const float y, const float z, const float w);

void Load_GL_VertexAttrib4fv(const int indx, const void* values);

void Load_GL_VertexAttribPointer(const int indx, const int size, const int type, bool normalized, const int stride, void* ptr);

void Load_GL_VertexAttribPointerOffset(const int indx, const int size, const int type, bool normalized, const int stride, const int64_t offset);

void Load_GL_GetAttachedShaders(const int program, const int maxCount, void* count, void* shaders);

void Load_GL_GetShaderSource(const int shader, const int bufSize, void* length, void* source);

#ifdef __cplusplus
}
#endif

#endif

