#ifndef LOON_SDL
#define LOON_SDL

#if defined(_WIN32) || defined(_WIN64) || defined(__APPLE__) || defined(__linux__) || defined(__unix__) || \
    defined(__FreeBSD__) || defined(__NetBSD__) || defined(__OpenBSD__) || defined(__DragonFly__)
    #define LOON_DESKTOP 1
#else
    #define LOON_DESKTOP 0
#endif

#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#ifdef __APPLE__
    #include <SDL2/SDL.h>
    #include <SDL2/SDL_main.h>
#else
    #include <SDL.h>
    #include <SDL_main.h>
#endif

#ifdef _WIN32
#include <windows.h>
#include <shlobj.h>
#include <winbase.h>
#include <winhttp.h>
#include <VersionHelpers.h>
#pragma comment(lib, "shell32.lib")
#pragma comment(lib, "winhttp.lib")
static void run_sleep_ms(int ms) {
    Sleep(ms);
}
#elif defined(__linux__) || defined(__APPLE__) || defined(__MACH__) || defined(__unix__)
#include <dlfcn.h>
#include <time.h>
#include <sys/utsname.h>
#include <unistd.h>
#include <pwd.h>
    #if !defined(__APPLE__)
    #include <sys/sysinfo.h>
    #endif
#include <curl/curl.h>
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
    #elif !defined(LOON_DESKTOP)
        #ifdef __SWITCH__
            #include <switch.h>
        #elif defined(__ORBIS__) || defined(__PROSPERO__) 
           //?
        #elif defined(_XBOX)
            #include <xgame.h>
        #endif
        #include <EGL/egl.h>
        #include <EGL/eglext.h>
        #include <glad/glad.h>
        #include <sys/errno.h>
    #else
        #include "glad/gles2.h"
#endif
#endif

#include "SDL_mixer.h"
#include <SDL_gamecontroller.h>

#ifdef __cplusplus
extern "C" {
#endif

#define true 1
#define false 0

#define MAX_GAMESAVE_PATH_CAHR_LEN 256
#define MAX_GAMESAVE_FILE_LIST 1024
#define MAX_CHUNK_SIZE 8192

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

static int32_t global_result[8];

static char global_cname[2048];

static char global_info[1024 * 10];

void ImportSDLInclude();

const uint8_t* DownloadURL(const char* url);

int64_t CreatePrefs();

bool LoadPrefs(int64_t handle, const char* filename);

void SetPrefs(int64_t handle, const char* section, const char* key,
    const uint8_t* value, size_t value_len);

const uint8_t* GetPrefs(int64_t handle, const char* section, const char* key, size_t* len);

const char* GetPrefsKeys(int64_t handle, const char* section, const char* delimiter);

bool SavePrefs(int64_t handle, const char* filename);

void RemovePrefs(int64_t handle, const char* section, const char* key);

void FreePrefs(int64_t handle);

int64_t CreateGameData(char* fileName);

const char* ReadGameData(const int64_t handle, const char* filename, int64_t* outSize);

bool WriteGameData(const int64_t handle, const char* filename, const char* data, int64_t size);

int32_t GetGameDataFileCount(const int64_t handle);

void FreeGameData(const int64_t handle);

const char* GetPathFullName(char* dst, const char* path);

const char* GetSystemProperty(const char* key);

bool FileExists(const char* filename);

const char* Load_SDL_RW_FileToChars(const char* filename);

const uint8_t* Load_SDL_RW_FileToBytes(const char* filename);

bool Load_SDL_RW_FileExists(const char* filename);

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

const char* Load_SDL_SensorGetDeviceName(int32_t deviceIndex);

int32_t Load_SDL_SensorGetDeviceType(int32_t deviceIndex);

int32_t Load_SDL_SensorGetDeviceNonPortableType(int32_t deviceIndex);

void Load_SDL_SensorUpdate();

int32_t Load_SDL_GetPolleventType();

int32_t Load_SDL_GetTicks();

int64_t Load_SDL_GetTicks64();

void Call_SDL_DestroyWindow();

void Load_RemapControllers(const int min, const int max, const int dualJoy, const int singleMode);

bool Load_IsConnected(const int controller);

int Load_Buttons();

float* Load_Axes(const int controller, float* axes);

bool Load_SDL_Exit(const int run);

int* Load_SDL_GetDrawableSize(const int64_t window, int* values);

int* Load_SDL_GetWindowSize(const int64_t window);

int Load_SDL_LockSurface(const int64_t handle);

void Load_SDL_UnlockSurface(const int64_t handle);

void Load_SDL_Delay(const int32_t d);

int64_t Load_SDL_CreateRGBSurface(const int32_t flags, const int width, const int height, const int depth,int32_t rmask, const int32_t gmask, const int32_t bmask, const int32_t amask);

int64_t Load_SDL_CreateRGBSurfaceFrom(const int32_t* pixels,const int w,const int h,const int format);

int64_t Load_SDL_ConvertSurfaceFormat(const int64_t handle, int32_t pixel_format, int32_t flags);

int* Load_SDL_GetSurfaceSize(const int64_t handle);

int* Load_SDL_GetPixels(const int64_t handle, int x, int y, int w, int h);

int* Load_SDL_GetPixels32(const int64_t handle, const int order);

void Load_SDL_SetPixel(const int64_t handle, int x, int y, int32_t pixel);

void Load_SDL_SetPixel32(const int64_t handle, int x, int y, int32_t pixel);

void Load_SDL_SetPixels32(const int64_t handle, int nx, int ny, int nw, int nh, int32_t* pixels);

int64_t Load_SDL_LoadBMPHandle(const char* path);

bool Load_SDL_MUSTLockSurface(const int64_t handle);

void Load_SDL_SetSurfaceBlendMode(const int64_t handle,const int mode);

int Load_SDL_GetSurfaceBlendMode(const int64_t handle);

void Load_SDL_FillRect(const int64_t handle, const int x, const int y, const int w, const int h, const int r, const int g, const int b, const int a);

void Load_SDL_SetClipRect(const int64_t handle, const int x, const int y, const int w, const int h);

int* Load_SDL_GetClipRect(const int64_t handle);

int32_t Load_SDL_GetFormat(const int64_t handle);

bool Load_SDL_Update();

int* Load_SDL_TouchData(int* data);

void Load_SDL_Cleanup();

int64_t Load_SDL_ScreenInit(const char* title,const int w,const int h, const bool vsync , const int flags, const bool debug);

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

int* Load_SDL_GetDisplayUsableBounds(const int display, int* xywh);

int* Load_SDL_GetDisplayBounds(const int display, int* xywh);

int Load_SDL_GetNumVideoDisplays();

int Load_SDL_GetWindowFlags(const int64_t handle);

void Load_SDL_SetWindowTitle(const int64_t handle,const char* title);

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

void Load_SDL_GL_SwapWindow(const int64_t window);

int64_t Load_SDL_GL_CreateContext(const int64_t window);

int Load_SDL_GL_SetAttribute(const int attribute, const int value);

void Load_SDL_SetTextInputRect(int x, int y, int w, int h);

void Load_SDL_RestoreWindow(const int64_t handle);

void Load_SDL_SetWindowIcon(const int64_t handle, const int64_t surface);

void Load_SDL_DestroyWindow(const int64_t handle);

int* Call_SDL_GetDrawableSize(int* values);

int* Call_SDL_GetWindowSize();

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

char* Load_GL_Init();

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

int Load_GL_GetError();

void Load_GL_GetIntegerv(const int pname, const void* params);

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

void Load_GL_GetIntegerv(const int pname, const void* params);

int Load_GL_GetIntegervResult(const int pname);

void Load_GL_GetFramebufferAttachmentParameteriv(const int target, const int attachment, const int pname, const void* params);

void Load_GL_GetProgramiv(const int program, const int pname, const void* params);

char* Load_GL_GetProgramInfoLog(const int program);

const char* Load_GL_GetProgramInfoLogs(const int program, const int bufsize, const void* length, const void* infolog);

void Load_GL_GetRenderbufferParameteriv(const int target, const int pname, const void* params);

void Load_GL_GetShaderiv(const int shader, const int pname, const void* params);

char* Load_GL_GetShaderInfoLog(const int shader);

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

