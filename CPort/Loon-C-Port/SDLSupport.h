#ifndef LOON_SDL
#define LOON_SDL

#include <stdbool.h>
#ifdef __APPLE__
#include <SDL2/SDL.h>
#else
#include <SDL.h>
#endif

#ifdef _WIN32
#include <windows.h>
#include <winbase.h>
#endif

#if defined(__unix__) || defined(__APPLE__)
#include <execinfo.h>
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

#include "SDL_mixer.h"
#include <SDL_gamecontroller.h>

char* GetPathFullName(char* dst, const char* path);

char* GetSystemProperty(const char* key);

void Load_RemapControllers(const int min, const int max, const int dualJoy, const int singleMode);

bool Load_IsConnected(const int controller);

int Load_Buttons();

float* Load_Axes(const int controller, float* axes);

bool Load_SDL_Exit(const int run);

int* Load_SDL_GetDrawableSize(const int64_t window, int* values);

int* Load_SDL_GetWindowSize(const int64_t handle);

int Load_SDL_LockSurface(const int64_t handle);

void Load_SDL_UnlockSurface(const int64_t handle);

int64_t Load_SDL_CreateRGBSurfaceFrom(const int32_t* pixels,const int w,const int h,const int format);

int64_t Load_SDL_ConvertSurfaceFormat(const int64_t handle, int32_t pixel_format, int32_t flags);

int* Load_SDL_GetPixels(const int64_t handle, int x, int y, int w, int h);

void Load_SDL_SetPixel(const int64_t handle, int x, int y, int32_t pixel);

void Load_SDL_SetPixel32(const int64_t handle, int x, int y, int32_t pixel);

void Load_SDL_SetPixels32(const int64_t handle, int nx, int ny, int nw, int nh, int32_t* pixels);

void Load_SDL_SetSurfaceBlendMode(const int64_t handle,const int mode);

int Load_SDL_GetSurfaceBlendMode(const int64_t handle);

void Load_SDL_FillRect(const int64_t handle, const int x, const int y, const int w, const int h, const int r, const int g, const int b, const int a);

void Load_SDL_SetClipRect(const int64_t handle, const int x, const int y, const int w, const int h);

int* Load_SDL_GetClipRect(const int64_t handle);

bool Load_SDL_Update();

int* Load_SDL_TouchData(int* data);

void Load_SDL_Cleanup();

int64_t Load_SDL_ScreenInit(const char* title,const int w,const int h, const bool vsync);

bool Load_SDL_PathIsFile(char* path);

int Load_SDL_Init(const int flags);

int Load_SDL_InitSubSystem(const int flags);

int Load_SDL_WasInit(const int flags);

void Load_SDL_QuitSubSystem(const int flags);

char* Load_SDL_GetError();

int Load_SDL_SetClipboardText(const char* text);

char* Load_SDL_GetClipboardText();

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

bool Load_SDL_SetHint(const char* name,const char* value);

int64_t Load_SDL_CreateWindow(const char* title,
    const int w,
    const int h, const int flags);

int Load_SDL_PollEvent(char* data);

void Load_SDL_GetCompiledVersion(char* data);

void Load_SDL_GetVersion(char* data);

void Load_SDL_Quit();

char* Load_GL_Init();

void Load_GL_ActiveTexture(const int tex);

void Load_GL_BindTexture(const int target, const int tex);

void Load_GL_BlendFunc(const int sfactor,const int dfactor);

void Load_GL_Clear(const int mask);

void Load_GL_ClearColor(const float red, const float green, const float blue, const float alpha);

void Load_GL_ClearDepthf(const float depth);

void Load_GL_ClearStencil(const int sc);

void Load_GL_ColorMask(const bool red, const bool green, const bool blue, const bool alpha);

void Load_GL_CompressedTexImage2D(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int imageSize, void* data);

void Load_GL_CompressedTexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int imageSize, void* data);

void Load_GL_CopyTexImage2D(const int target, const int level, const int internalformat, const int x, const int y, const int width, const int height, const int border);

void Load_GL_CopyTexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int x, const int y, const int width, const int height);

void Load_GL_CullFace(const int mode);

void Load_GL_DeleteTexture(const int texture);

void Load_GL_DepthFunc(const int func);

void Load_GL_DepthMask(const bool flag);

void Load_GL_DepthRangef(const float zNear, const float zFar);

void Load_GL_Disable(const int cap);

void Load_GL_DrawArrays(const int mode, const int first, const int count);

void Load_GL_DrawElements(const int mode, const int count, const int type, const void* indices);

void Load_GL_Enable(const int cap);

void Load_GL_Finish();

void Load_GL_Flush();

void Load_GL_FrontFace(const int mode);

int Load_GL_GenTexture();

int Load_GL_GetError();

void Load_GL_GetIntegerv(const int pname, const int32_t* params);

char* Load_GL_GetString(const int name);

void Load_GL_Hint(const int target, const int mode);

void Load_GL_LineWidth(const float width);

void Load_GL_PixelStorei(const int pname, const int param);

void Load_GL_PolygonOffset(const float factor, const float units);

void Load_GL_ReadPixels(const int x, const int y, const int width, const int height, const int format, const int type, const void* pixels);

void Load_GL_Scissor(const int x, const int y, const int width, const int height);

void Load_GL_StencilFunc(const int func, const int ref, const int mask);

void Load_GL_StencilMask(const int mask);

void Load_GL_StencilOp(const int fail, const int zfail, const int zpass);

void Load_GL_TexImage2D(const int target, const int level, const int internalformat, const int width, const int height, const int border, const int format, const int type, const void* pixels);

void Load_GL_TexParameterf(const int target, const int pname, const float param);

void Load_GL_TexSubImage2D(const int target, const int level, const int xoffset, const int yoffset, const int width, const int height, const int format, const int type, const void* pixels);

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

void Load_GL_DeleteFramebuffer(const int framebuffer);

void Load_GL_DeleteProgram(const int program);

void Load_GL_DeleteRenderbuffer(const int renderbuffer);

void Load_GL_DeleteShader(const int shader);

void Load_GL_DetachShader(const int program, const int shader);

void Load_GL_DisableVertexAttribArray(const int index);

void Load_GL_DrawElements(const int mode, const int count, const int type, const void* indices);

void Load_GL_EnableVertexAttribArray(const int index);

void Load_GL_FramebufferRenderbuffer(const int target, const int attachment, const int renderbuffertarget, const int renderbuffer);

void Load_GL_FramebufferTexture2D(const int target, const int attachment, const int textarget, const int texture, const int level);

int Load_GL_GenBuffer();

void Load_GL_GenerateMipmap(const int target);

int Load_GL_GenFramebuffer();

char* Load_GL_GetActiveAttrib(const int program, const int index,const int32_t* size,const void* type);

char* Load_GL_GetActiveUniform(const int program, const int index, const int32_t* size, const void* type);

int Load_GL_GetAttribLocation(const int program, const char* name);

void Load_GL_GetBooleanv(const int pname, const void* params);

void Load_GL_GetBufferParameteriv(const int target, const int pname, const int32_t* params);

void Load_GL_GetFloatv(const int pname, const float* params);

void Load_GL_GetFramebufferAttachmentParameteriv(const int target, const int attachment, const int pname, const int32_t* params);

void Load_GL_GetProgramiv(const int program, const int pname, const int32_t* params);

char* Load_GL_GetProgramInfoLog(const int program);

void Load_GL_GetRenderbufferParameteriv(const int target, const int pname, const int32_t* params);

void Load_GL_GetShaderiv(const int shader, const int pname, const int32_t* params);

char* Load_GL_GetShaderInfoLog(const int shader);

void Load_GL_GetShaderPrecisionFormat(const int shadertype, const int precisiontype, const int32_t* range, const int32_t* precision);

void Load_GL_GetTexParameterfv(const int target, const int pname, const float* params);

void Load_GL_GetTexParameteriv(const int target, const int pname, const int32_t* params);

void Load_GL_GetUniformfv(const int program, const int location, const float* params);

void Load_GL_GetUniformiv(const int program, const int location, const int32_t* params);

int Load_GL_GetUniformLocation(const int program, const char* name);

void Load_GL_GetVertexAttribfv(const int index, const int pname, const float* params);

void Load_GL_GetVertexAttribiv(const int index, const int pname, const int32_t* params);

bool Load_GL_IsBuffer(const int buffer);

bool Load_GL_IsEnabled(const int cap);

bool Load_GL_IsFramebuffer(const int framebuffer);

bool Load_GL_IsProgram(const int program);

bool Load_GL_IsRenderbuffer(const int renderbuffer);

bool Load_GL_IsShader(const int shader);

bool Load_GL_IsTexture(const int texture);

void Load_GL_LinkProgram(const int program);

void Load_GL_ReleaseShaderCompiler();

#endif