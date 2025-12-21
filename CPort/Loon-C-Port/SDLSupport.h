#ifndef LOON_SDL
#define LOON_SDL

#include <stdbool.h>
#ifdef __APPLE__
#include <SDL2/SDL.h>
#else
#include <SDL.h>
#endif

char* GetSystemProperty(const char* key);

int Load_SDL_Init(const int flags);

int Load_SDL_InitSubSystem(const int flags);

int Load_SDL_WasInit(const int flags);

void Load_SDL_QuitSubSystem(const int flags);

char* Load_SDL_GetError();

int Load_SDL_SetClipboardText(const char* text);

char* Load_SDL_GetClipboardText();

void Load_SDL_MaximizeWindow(const long handle);

void Load_SDL_MinimizeWindow(const long handle);

int Load_SDL_SetWindowFullscreen(const long handle, const int flags);

void Load_SDL_SetWindowBordered(const long handle, const bool bordered);

void Load_SDL_SetWindowSize(const long handle, const int w, const int h);

void Load_SDL_SetWindowPosition(const long handle, const int x, const int y);

int Load_SDL_GetWindowDisplayIndex(const long handle);

int Load_SDL_GetDisplayUsableBounds(const int display, int* xywh);

int Load_SDL_GetDisplayBounds(const int display, int* xywh);

int Load_SDL_GetNumVideoDisplays();

int Load_SDL_GetWindowFlags(const long handle);

void Load_SDL_SetWindowTitle(const long handle,const char* title);

long Load_SDL_CreateRGBSurfaceFrom(void* pixels, int width, int height);

long Load_SDL_CreateColorCursor(const long surface, const int hotx, const int hoty);

long Load_SDL_CreateSystemCursor(const int type);

void Load_SDL_SetCursor(const long handle);

void Load_SDL_FreeCursor(const long handle);

void Load_SDL_FreeSurface(const long handle);

void Load_SDL_ShowSimpleMessageBox(const int flags, const char* title, const char* message);

void Load_SDL_StartTextInput();

void Load_SDL_StopTextInput();

bool Load_SDL_IsTextInputActive();

bool Load_SDL_GL_ExtensionSupported(const char* exte);

int Load_SDL_GL_SetSwapInterval(int on);

void Load_SDL_GL_SwapWindow(const long window);

long Load_SDL_GL_CreateContext(const long window);

int Load_SDL_GL_SetAttribute(const int attribute, const int value);

void Load_SDL_SetTextInputRect(int x, int y, int w, int h);

void Load_SDL_RestoreWindow(const long handle);

void Load_SDL_SetWindowIcon(const long handle, const long surface);

void Load_SDL_DestroyWindow(const long handle);

bool Load_SDL_SetHint(const char* name,const char* value);

long Load_SDL_CreateWindow(const char* title,
    const int w,
    const int h, const int flags);

void Load_SDL_PollEvent(char* data);

void Load_SDL_GetCompiledVersion(char* data);

void Load_SDL_GetVersion(char* data);

void Load_SDL_Quit();

#endif