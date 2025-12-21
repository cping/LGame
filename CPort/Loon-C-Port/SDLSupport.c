#include "SDLSupport.h"

int Load_SDL_GL_SetSwapInterval(int on)
{
    return SDL_GL_SetSwapInterval(on);
}

void Load_SDL_GL_SwapWindow(const long window)
{
    SDL_GL_SwapWindow((SDL_Window*)window);
}

long Load_SDL_GL_CreateContext(const long window)
{
    return (long)SDL_GL_CreateContext((SDL_Window*)window);
}

int Load_SDL_GL_SetAttribute(const int attribute, const int value)
{
    return SDL_GL_SetAttribute((SDL_GLattr)attribute, value);
}

char* GetSystemProperty(const char* key)
{
#if defined(__SWITCH__)
        if (strcmp(key,"os.name")==0)
            return "horizon";
        if (strcmp(name,"os.arch")==0)
            return "aarch64";
#elif defined(__WINRT__)
        if (strcmp(name,"os.name")==0)
            return "uwp";
        if (strcmp(name,"os.arch")==0)
            return "x86_64";
#else
        if (strcmp(key,"os.name")==0)
            return "unknown";
        if (strcmp(key,"os.arch")==0)
            return "x86_64";
#endif
        if (strcmp(key,"line.separator")==0)
            return "\n";
        if (strcmp(key,"java.io.tmpdir")==0)
            return "temp";
        if (strcmp(key,"user.home")==0)
            return "home";
        if (strcmp(key,"user.name")==0)
            return "user";
        return "null";
}

int Load_SDL_Init(const int flags)
{
    return SDL_Init(flags);
}

int Load_SDL_InitSubSystem(const int flags)
{
    return SDL_InitSubSystem(flags);
}

int Load_SDL_WasInit(const int flags)
{
    return SDL_WasInit(flags);
}

void Load_SDL_QuitSubSystem(const int flags)
{
    SDL_QuitSubSystem(flags);
}

char* Load_SDL_GetError()
{
    return SDL_GetError();
}

int Load_SDL_SetClipboardText(const char* text)
{
    return SDL_SetClipboardText(text);
}

char* Load_SDL_GetClipboardText()
{
    return SDL_GetClipboardText();
}

void Load_SDL_MaximizeWindow(const long handle)
{
    SDL_MaximizeWindow((SDL_Window*)handle);
}

void Load_SDL_MinimizeWindow(const long handle)
{
    SDL_MinimizeWindow((SDL_Window*)handle);
}

int Load_SDL_SetWindowFullscreen(const long handle, const int flags)
{
    return SDL_SetWindowFullscreen((SDL_Window*)handle, flags);
}

void Load_SDL_SetWindowBordered(const long handle, const bool bordered)
{
    SDL_SetWindowBordered((SDL_Window*)handle, (SDL_bool)bordered);
}

void Load_SDL_SetWindowSize(const long handle, const int w, const int h)
{
    SDL_SetWindowSize((SDL_Window*)handle, w, h);
}

void Load_SDL_SetWindowPosition(const long handle, const int x, const int y)
{
    SDL_SetWindowPosition((SDL_Window*)handle, x, y);
}

int Load_SDL_GetWindowDisplayIndex(const long handle)
{
    return SDL_GetWindowDisplayIndex((SDL_Window*)handle);
}

int Load_SDL_GetDisplayUsableBounds(const int display, int* xywh)
{
    SDL_Rect bounds = { 0,0,0,0 };
    int result = SDL_GetDisplayUsableBounds(display, &bounds);

    xywh[0] = bounds.x;
    xywh[1] = bounds.y;
    xywh[2] = bounds.w;
    xywh[3] = bounds.h;

    return result;
}

int Load_SDL_GetDisplayBounds(const int display, int* xywh)
{
    SDL_Rect bounds = {0,0,0,0};
    int result = SDL_GetDisplayBounds(display, &bounds);

    xywh[0] = bounds.x;
    xywh[1] = bounds.y;
    xywh[2] = bounds.w;
    xywh[3] = bounds.h;

    return result;
}

int Load_SDL_GetNumVideoDisplays()
{
    return SDL_GetNumVideoDisplays();
}

int Load_SDL_GetWindowFlags(const long handle)
{
    return SDL_GetWindowFlags((SDL_Window*)handle);
}

void Load_SDL_SetWindowTitle(const long handle, const char* title)
{
    SDL_SetWindowTitle((SDL_Window*)handle, title);
}

long Load_SDL_CreateRGBSurfaceFrom(void* pixels, int width, int height)
{
    return (long)SDL_CreateRGBSurfaceFrom(pixels, width, height, 32, 4 * width, 0x000000ff, 0x0000ff00, 0x00ff0000, 0xff000000);
}

long Load_SDL_CreateColorCursor(const long surface, const int hotx, const int hoty)
{
    return (long)SDL_CreateColorCursor((SDL_Surface*)surface, hotx, hoty);
}

long Load_SDL_CreateSystemCursor(const int type)
{
    return (long)SDL_CreateSystemCursor((SDL_SystemCursor)type);
}

void Load_SDL_SetCursor(const long handle)
{
    SDL_SetCursor((SDL_Cursor*)handle);
}

void Load_SDL_FreeCursor(const long handle)
{
    SDL_FreeCursor((SDL_Cursor*)handle);
}

void Load_SDL_FreeSurface(const long handle)
{
    SDL_FreeSurface((SDL_Surface*)handle);
}

void Load_SDL_ShowSimpleMessageBox(const int flags, const char* title, const char* message)
{
    return SDL_ShowSimpleMessageBox(flags, title, message, NULL);
}

void Load_SDL_StartTextInput()
{
    SDL_StartTextInput();
}

void Load_SDL_StopTextInput()
{
      SDL_StopTextInput();
}

bool Load_SDL_IsTextInputActive()
{
    return SDL_IsTextInputActive();
}

bool Load_SDL_GL_ExtensionSupported(const char* exte) {
    return SDL_GL_ExtensionSupported(exte);
}

void Load_SDL_SetTextInputRect(const int x, const int y, const int w, const int h)
{
    SDL_Rect rect = { x,y,w,h }; 
    rect.x = x;
    rect.y = y;
    rect.w = w;
    rect.h = h;
    SDL_SetTextInputRect(&rect);
}

void Load_SDL_RestoreWindow(const long handle)
{
    SDL_RestoreWindow((SDL_Window*)handle);
}

void Load_SDL_SetWindowIcon(const long handle, const long surface)
{
    SDL_SetWindowIcon((SDL_Window*)handle, (SDL_Surface*)surface);
}

void Load_SDL_DestroyWindow(const long handle)
{
    SDL_DestroyWindow((SDL_Window*)handle);
}

bool Load_SDL_SetHint(const char* name, const char* value)
{
    return (SDL_SetHint(name, value) == SDL_TRUE);
}

long Load_SDL_CreateWindow(const char* title, int w, int h, int flags)
{
    return (long)SDL_CreateWindow(title, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, w, h, flags);
}

void Load_SDL_PollEvent(char* data)
{
    SDL_Event e;
    if (SDL_PollEvent(&e)) {
        switch (e.type) {
        case SDL_QUIT:
            data[0] = 0;
            break;
        case SDL_WINDOWEVENT:
            data[0] = 1;
            data[1] = e.window.event;
            data[2] = e.window.data1;
            data[3] = e.window.data2;
            break;
        case SDL_MOUSEMOTION:
            data[0] = 2;
            data[1] = e.motion.x;
            data[2] = e.motion.y;
            break;
        case SDL_MOUSEBUTTONDOWN:
        case SDL_MOUSEBUTTONUP:
            data[0] = 3;
            data[1] = (e.type == SDL_MOUSEBUTTONDOWN);
            data[2] = e.button.x;
            data[3] = e.button.y;
            data[4] = e.button.button;
            break;
        case SDL_MOUSEWHEEL:
            data[0] = 4;
            data[1] = e.wheel.x;
            data[2] = e.wheel.y;
            break;
        case SDL_KEYDOWN:
        case SDL_KEYUP:
            data[0] = 5;
            data[1] = (e.type == SDL_KEYDOWN);
            data[2] = e.key.keysym.sym;
            data[3] = e.key.repeat;
            data[4] = e.key.keysym.scancode;
            data[5] = e.key.keysym.mod;
            data[6] = e.key.timestamp;
            break;
        case SDL_TEXTINPUT:
            data[0] = 6;
            for (int i = 0; i < 32; i++) {
                data[i + 1] = e.text.text[i];
                if (e.text.text[i] == '\0') {
                    break;
                }
            }
            break;
        case SDL_TEXTEDITING:
            data[0] = 8;
            data[1] = e.edit.start;
            data[2] = e.edit.length;
            for (int i = 0; i < 32; i++) {
                data[i + 3] = e.edit.text[i];
                if (e.edit.text[i] == '\0') {
                    break;
                }
            }

            break;
        default:
            data[0] = 7;
            break;
        }
        return 1;
    }
    return 0;
}

void Load_SDL_GetCompiledVersion(char* data)
{
    SDL_version compiled = {0,0,0};
    SDL_VERSION(&compiled);
    data[0] = compiled.major;
    data[1] = compiled.minor;
    data[2] = compiled.patch;
}

void Load_SDL_GetVersion(char* data)
{
    SDL_version compiled = { 0,0,0 };
    SDL_GetVersion(&compiled);
    data[0] = compiled.major;
    data[1] = compiled.minor;
    data[2] = compiled.patch;
}

void Load_SDL_Quit()
{
    SDL_Quit();
}

void Load_SDL_GL_GetDrawableSize(const long window, int* values)
{
    int w, h;
    SDL_GL_GetDrawableSize((SDL_Window*)window, &w, &h);
    values[0] = w;
    values[1] = h;
}
