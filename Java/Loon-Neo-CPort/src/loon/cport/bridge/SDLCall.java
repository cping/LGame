/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.cport.bridge;

import java.nio.Buffer;
import java.nio.IntBuffer;

import org.teavm.backend.c.intrinsic.RuntimeInclude;
import org.teavm.interop.Import;

public final class SDLCall {

	public static final int SDL_INIT_TIMER = 0x00000001, SDL_INIT_AUDIO = 0x00000010, SDL_INIT_VIDEO = 0x00000020,
			SDL_INIT_JOYSTICK = 0x00000200, SDL_INIT_HAPTIC = 0x00001000, SDL_INIT_GAMECONTROLLER = 0x00002000,
			SDL_INIT_EVENTS = 0x00004000, SDL_INIT_NOPARACHUTE = 0x00100000,
			SDL_INIT_EVERYTHING = SDL_INIT_TIMER | SDL_INIT_AUDIO | SDL_INIT_VIDEO | SDL_INIT_EVENTS | SDL_INIT_JOYSTICK
					| SDL_INIT_HAPTIC | SDL_INIT_GAMECONTROLLER,
			SDL_WINDOW_FULLSCREEN = 0x00000001, SDL_WINDOW_OPENGL = 0x00000002, SDL_WINDOW_SHOWN = 0x00000004,
			SDL_WINDOW_HIDDEN = 0x00000008, SDL_WINDOW_BORDERLESS = 0x00000010, SDL_WINDOW_RESIZABLE = 0x00000020,
			SDL_WINDOW_MINIMIZED = 0x00000040, SDL_WINDOW_MAXIMIZED = 0x00000080, SDL_WINDOW_INPUT_GRABBED = 0x00000100,
			SDL_WINDOW_INPUT_FOCUS = 0x00000200, SDL_WINDOW_MOUSE_FOCUS = 0x00000400,
			SDL_WINDOW_FULLSCREEN_DESKTOP = (SDL_WINDOW_FULLSCREEN | 0x00001000), SDL_WINDOW_FOREIGN = 0x00000800,
			SDL_WINDOW_ALLOW_HIGHDPI = 0x00002000, SDL_WINDOW_MOUSE_CAPTURE = 0x00004000, SDL_WINDOWEVENT_NONE = 0,
			SDL_WINDOWEVENT_SHOWN = 1, SDL_WINDOWEVENT_HIDDEN = 2, SDL_WINDOWEVENT_EXPOSED = 3,
			SDL_WINDOWEVENT_MOVED = 4, SDL_WINDOWEVENT_RESIZED = 5, SDL_WINDOWEVENT_SIZE_CHANGED = 6,
			SDL_WINDOWEVENT_MINIMIZED = 7, SDL_WINDOWEVENT_MAXIMIZED = 8, SDL_WINDOWEVENT_RESTORED = 9,
			SDL_WINDOWEVENT_ENTER = 10, SDL_WINDOWEVENT_LEAVE = 11, SDL_WINDOWEVENT_FOCUS_GAINED = 12,
			SDL_WINDOWEVENT_FOCUS_LOST = 13, SDL_WINDOWEVENT_CLOSE = 14, SDL_SYSTEM_CURSOR_ARROW = 0,
			SDL_SYSTEM_CURSOR_IBEAM = 1, SDL_SYSTEM_CURSOR_WAIT = 2, SDL_SYSTEM_CURSOR_CROSSHAIR = 3,
			SDL_SYSTEM_CURSOR_WAITARROW = 4, SDL_SYSTEM_CURSOR_SIZENWSE = 5, SDL_SYSTEM_CURSOR_SIZENESW = 6,
			SDL_SYSTEM_CURSOR_SIZEWE = 7, SDL_SYSTEM_CURSOR_SIZENS = 8, SDL_SYSTEM_CURSOR_SIZEALL = 9,
			SDL_SYSTEM_CURSOR_NO = 10, SDL_SYSTEM_CURSOR_HAND = 11, SDL_NUM_SYSTEM_CURSORS = 12,
			SDL_MESSAGEBOX_ERROR = 0x00000010, SDL_MESSAGEBOX_WARNING = 0x00000020,
			SDL_MESSAGEBOX_INFORMATION = 0x00000040, SDL_BUTTON_LEFT = 1, SDL_BUTTON_MIDDLE = 2, SDL_BUTTON_RIGHT = 3,
			SDL_BUTTON_X1 = 4, SDL_BUTTON_X2 = 5, SDL_EVENT_QUIT = 0, SDL_EVENT_WINDOW = 1, SDL_EVENT_MOUSE_MOTION = 2,
			SDL_EVENT_MOUSE_BUTTON = 3, SDL_EVENT_MOUSE_WHEEL = 4, SDL_EVENT_KEYBOARD = 5, SDL_EVENT_TEXT_INPUT = 6,
			SDL_EVENT_TEXT_EDIT = 8, SDL_EVENT_OTHER = 7, SDL_GL_RED_SIZE = 0, SDL_GL_GREEN_SIZE = 1,
			SDL_GL_BLUE_SIZE = 2, SDL_GL_ALPHA_SIZE = 3, SDL_GL_BUFFER_SIZE = 4, SDL_GL_DOUBLEBUFFER = 5,
			SDL_GL_DEPTH_SIZE = 6, SDL_GL_STENCIL_SIZE = 7, SDL_GL_CONTEXT_MAJOR_VERSION = 17,
			SDL_GL_CONTEXT_MINOR_VERSION = 18, SDL_GL_MULTISAMPLEBUFFERS = 13, SDL_GL_MULTISAMPLESAMPLES = 14,
			SDL_GL_CONTEXT_PROFILE_CORE = 1, SDL_GL_CONTEXT_PROFILE_COMPATIBILITY = 2, SDL_GL_CONTEXT_PROFILE_MASK = 21,
			SDL_GL_CONTEXT_FLAGS = 20;

	private SDLCall() {
		importInclude();
	}

	@RuntimeInclude("SDLSupport.h")
	@Import(name = "ImportSDLInclude")
	public final static native void importInclude();

	@Import(name = "GetPathFullName")
	public final static native String getPathFullName(String dst, String path);

	@Import(name = "GetSystemProperty")
	public final static native String getSystemProperty(String key);

	@Import(name = "Load_SDL_GetBasePath")
	public final static native String getBasePath();

	@Import(name = "Load_SDL_GetTicks")
	public final static native int getTicks();

	@Import(name = "Load_RemapControllers")
	public final static native void remapControllers(int min, int max, int dualJoy, int singleMode);

	@Import(name = "Load_IsConnected")
	public final static native boolean isConnected(int controller);

	@Import(name = "Load_Buttons")
	public final static native int getButtons();

	@Import(name = "Load_Axes")
	public final static native float[] getAxes(int controller, float[] axes);

	@Import(name = "Load_SDL_Exit")
	public final static native boolean exit(int run);

	@Import(name = "Load_SDL_GetDrawableSize")
	public final static native int[] getDrawableSize(long handle, int[] values);

	@Import(name = "Load_SDL_GetDrawableSize")
	public final static native int[] getWindowSize(long handle);

	@Import(name = "Load_SDL_LockSurface")
	public final static native int lockSurface(long handle);

	@Import(name = "Load_SDL_UnlockSurface")
	public final static native void unlockSurface(long handle);

	@Import(name = "Load_SDL_Delay")
	public final static native void delay(int d);

	@Import(name = "Load_SDL_CreateRGBSurface")
	public final static native long createRGBSurface(int flags, int width, int height, int depth, int rmask, int gmask,
			int bmask, int amask);

	@Import(name = "Load_SDL_CreateRGBSurfaceFrom")
	public final static native long createRGBSurfaceFrom(int[] pixels, int w, int h, int format);

	@Import(name = "Load_SDL_ConvertSurfaceFormat")
	public final static native long convertSurfaceFormat(long handle, int pixel_format, int flags);

	@Import(name = "Load_SDL_GetSurfaceSize")
	public final static native int[] convertSurfaceFormat(long handle);

	@Import(name = "Load_SDL_GetPixels")
	public final static native int[] getSurfacePixels(long handle, int x, int y, int w, int h);

	@Import(name = "Load_SDL_GetPixels32")
	public final static native int[] getSurfacePixels32(long handle);

	@Import(name = "Load_SDL_SetPixel")
	public final static native void setSurfacePixel(long handle, int x, int y, int pixel);

	@Import(name = "Load_SDL_SetPixel32")
	public final static native void setSurfacePixel32(long handle, int x, int y, int pixel);

	@Import(name = "Load_SDL_SetPixels32")
	public final static native void setSurfacePixels32(long handle, int nx, int ny, int nw, int nh, int[] pixels);

	@Import(name = "Load_SDL_LoadBMPHandle")
	public final static native long loadBMPHandle(String path);

	@Import(name = "Load_SDL_MUSTLockSurface")
	public final static native boolean MUSTLockSurface(long handle);

	@Import(name = "Load_SDL_SetSurfaceBlendMode")
	public final static native void setSurfaceBlendMode(long handle, int mode);

	@Import(name = "Load_SDL_GetSurfaceBlendMode")
	public final static native int getSurfaceBlendMode(long handle);

	@Import(name = "Load_SDL_FillRect")
	public final static native void fillRectSurface(long handle, int x, int y, int w, int h, int r, int g, int b,
			int a);

	@Import(name = "Load_SDL_SetClipRect")
	public final static native void setSurfaceClipRect(long handle, int x, int y, int w, int h);

	@Import(name = "Load_SDL_GetClipRect")
	public final static native int[] getSurfaceClipRect(long handle);

	@Import(name = "Load_SDL_Update")
	public final static native boolean update();

	@Import(name = "Load_SDL_TouchData")
	public final static native int[] getTouchData(int[] data);

	@Import(name = "Load_SDL_Cleanup")
	public final static native void cleanup();

	@RuntimeInclude("SDLSupport.h")
	@Import(name = "Load_SDL_ScreenInit")
	public final static native long screenInit(String title, int w, int h, boolean vsync, int flags, boolean debug);

	@Import(name = "Load_SDL_PathIsFile")
	public final static native boolean pathIsFile(String path);

	@Import(name = "Load_SDL_Init")
	public final static native int init(int flags);

	@Import(name = "Load_SDL_InitSubSystem")
	public final static native int initSubSystem(int flags);

	@Import(name = "Load_SDL_WasInit")
	public final static native int wasInit(int flags);

	@Import(name = "Load_SDL_QuitSubSystem")
	public final static native int quitSubSystem(int flags);

	@Import(name = "Load_SDL_GetError")
	public final static native String getError();

	@Import(name = "Load_SDL_SetClipboardText")
	public final static native int setClipboardText(String text);

	@Import(name = "Load_SDL_GetClipboardText")
	public final static native String getClipboardText();

	@Import(name = "Load_SDL_MaximizeWindow")
	public final static native void maximizeWindow(long handle);

	@Import(name = "Load_SDL_MinimizeWindow")
	public final static native void minimizeWindow(long handle);

	@Import(name = "Load_SDL_SetWindowFullscreen")
	public final static native void setWindowFullscreen(long handle, int flags);

	@Import(name = "Load_SDL_SetWindowBordered")
	public final static native void setWindowBordered(long handle, boolean bordered);

	@Import(name = "Load_SDL_SetWindowSize")
	public final static native void setWindowSize(long handle, int w, int h);

	@Import(name = "Load_SDL_SetWindowPosition")
	public final static native void setWindowPosition(long handle, int x, int y);

	@Import(name = "Load_SDL_GetWindowDisplayIndex")
	public final static native int getWindowDisplayIndex(long handle, int x, int y);

	@Import(name = "Load_SDL_GetDisplayUsableBounds")
	public final static native int[] getDisplayUsableBounds(long handle, int[] xywh);

	@Import(name = "Load_SDL_GetDisplayBounds")
	public final static native int[] getDisplayBounds(long handle, int[] xywh);

	@Import(name = "Load_SDL_GetNumVideoDisplays")
	public final static native int getNumVideoDisplays();

	@Import(name = "Load_SDL_GetWindowFlags")
	public final static native int getWindowFlags(long handle);

	@Import(name = "Load_SDL_SetWindowTitle")
	public final static native void setWindowTitle(long handle, String title);

	@Import(name = "Load_SDL_CreateRGBSurfaceFrom32")
	public final static native long createRGBSurfaceFrom32(byte[] pixels, int w, int h);

	@Import(name = "Load_SDL_CreateColorCursor")
	public final static native long createColorCursor(long handle, int hotx, int hoty);

	@Import(name = "Load_SDL_CreateSystemCursor")
	public final static native long createSystemCursor(int type);

	@Import(name = "Load_SDL_SetCursor")
	public final static native void setCursor(long handle);

	@Import(name = "Load_SDL_FreeCursor")
	public final static native void freeCursor(long handle);

	@Import(name = "Load_SDL_FreeSurface")
	public final static native void freeSurface(long handle);

	@Import(name = "Load_SDL_FreeTempSurface")
	public final static native void freeTempSurface();

	@Import(name = "Load_SDL_ShowSimpleMessageBox")
	public final static native int showSimpleMessageBox(int flags, String title, String message);

	@Import(name = "Load_SDL_StartTextInput")
	public final static native void startTextInput();

	@Import(name = "Load_SDL_StopTextInput")
	public final static native void stopTextInput();

	@Import(name = "Load_SDL_IsTextInputActive")
	public final static native boolean isTextInputActive();

	@Import(name = "Load_SDL_GL_ExtensionSupported")
	public final static native boolean extensionSupported(String exte);

	@Import(name = "Load_SDL_GL_SetSwapInterval")
	public final static native int setSwapInterval(int on);

	@Import(name = "Load_SDL_GL_SwapWindow")
	public final static native void swapWindow(long handle);

	@Import(name = "Load_SDL_GL_CreateContext")
	public final static native long createContext(long winhandle);

	@Import(name = "Load_SDL_GL_SetAttribute")
	public final static native int setAttribute(int attribute, int value);

	@Import(name = "Load_SDL_SetTextInputRect")
	public final static native void setAttribute(int x, int y, int w, int h);

	@Import(name = "Load_SDL_RestoreWindow")
	public final static native void restoreWindow(long handle);

	@Import(name = "Load_SDL_SetWindowIcon")
	public final static native void setWindowIcon(long handle, long surface);

	@Import(name = "Load_SDL_DestroyWindow")
	public final static native void destroyWindow(long handle);

	@Import(name = "Call_SDL_GetDrawableSize")
	public final static native int[] getDrawableSize(int[] values);

	@Import(name = "Call_SDL_GetWindowSize")
	public final static native int[] getWindowSize();

	@Import(name = "Call_SDL_MaximizeWindow")
	public final static native void maximizeWindow();

	@Import(name = "Call_SDL_MinimizeWindow")
	public final static native void minimizeWindow();

	@Import(name = "Call_SDL_SetWindowFullscreen")
	public final static native void setWindowFullscreen(int flags);

	@Import(name = "Call_SDL_SetWindowBordered")
	public final static native void setWindowBordered(boolean bordered);

	@Import(name = "Call_SDL_SetWindowSize")
	public final static native void setWindowSize(int w, int h);

	@Import(name = "Call_SDL_SetWindowPosition")
	public final static native void setWindowPosition(int x, int y);

	@Import(name = "Call_SDL_GetWindowDisplayIndex")
	public final static native int getWindowDisplayIndex();

	@Import(name = "Call_SDL_GetWindowFlags")
	public final static native int getWindowFlags();

	@Import(name = "Call_SDL_SetWindowTitle")
	public final static native void setWindowTitle(String title);

	@Import(name = "Call_SDL_RestoreWindow")
	public final static native void restoreWindow();

	@Import(name = "Call_SDL_SetWindowIcon")
	public final static native void setWindowIcon(long surfaceHandle);

	@Import(name = "Call_SDL_GL_SwapWindow")
	public final static native void swapWindow();

	@Import(name = "Call_SDL_GL_CreateContext")
	public final static native long createContext();

	@Import(name = "Load_SDL_SetHint")
	public final static native boolean setHint(String name, String value);

	@Import(name = "Load_SDL_CreateWindow")
	public final static native long createWindow(String title, int w, int h, int flags);

	@Import(name = "Load_SDL_PollEvent")
	public final static native int pollEvent(String data);

	@Import(name = "Load_SDL_GetCompiledVersion")
	public final static native String getCompiledVersion(String data);

	@Import(name = "Load_SDL_GetVersion")
	public final static native String getVersion(String data);

	@Import(name = "Load_SDL_Mix_LoadMUS")
	public final static native long loadMUS(String filename);

	@Import(name = "Load_SDL_Mix_PlayMusic")
	public final static native void playMusic(long handle, boolean looping);

	@Import(name = "Load_SDL_Mix_PlayFadeInMusic")
	public final static native void playFadeInMusic(long handle, boolean looping);

	@Import(name = "Load_SDL_Mix_PlayMusicFadeStop")
	public final static native void playMusicFadeStop();

	@Import(name = "Load_SDL_MIX_SetPosition")
	public final static native void setPosition(float position);

	@Import(name = "Load_SDL_Mix_SetMusicVolume")
	public final static native void setMusicVolume(float volume);

	@Import(name = "Load_SDL_Mix_GetMusicVolume")
	public final static native float getMusicVolume();

	@Import(name = "Load_SDL_Mix_PauseMusic")
	public final static native void pauseMusic();

	@Import(name = "Load_SDL_Mix_ResumeMusic")
	public final static native void resumeMusic();

	@Import(name = "Load_SDL_Mix_HaltMusic")
	public final static native void haltMusic();

	@Import(name = "Load_SDL_Mix_DisposeMusic")
	public final static native void disposeMusic(long handle);

	@Import(name = "Load_SDL_Mix_LoadSound")
	public final static native long loadSound(String filename);

	@Import(name = "Load_SDL_Mix_LoadSoundFromMem")
	public final static native long loadSound(byte[] bytes);

	@Import(name = "Load_SDL_Mix_PlaySound")
	public final static native int playSound(long handle, boolean looping);

	@Import(name = "Load_SDL_Mix_SetPlaySoundLooping")
	public final static native int setPlaySoundLooping(long handle, int channel, boolean looping);

	@Import(name = "Load_SDL_Mix_PauseSound")
	public final static native void pauseSound(int channel);

	@Import(name = "Load_SDL_Mix_ResumeSound")
	public final static native void resumeSound(int channel);

	@Import(name = "Load_SDL_Mix_SetVolume")
	public final static native int setVolume(int channel, float volume);

	@Import(name = "Load_SDL_Mix_SetPan")
	public final static native int setPan(int channel, float pan);

	@Import(name = "Load_SDL_Mix_HaltSound")
	public final static native int haltSound(int channel);

	@Import(name = "Load_SDL_Mix_DisposeSound")
	public final static native void disposeSound(long handle);

	@Import(name = "Load_SDL_Mix_CloseAudio")
	public final static native void closeAudio();

	@Import(name = "Load_SDL_WindowHandle")
	public final static native long getWindowHandle();

	@Import(name = "Load_SDL_Quit")
	public final static native void quit();

	@Import(name = "Load_GL_Init")
	public final static native String init();

	@Import(name = "Call_SDL_DestroyWindow")
	public final static native void destroyWindow();

	/// GL
	@Import(name = "Load_GL_UseProgram")
	public final static native void glUseProgram(int program);

	@Import(name = "Load_GL_ValidateProgram")
	public final static native void glValidateProgram(int program);

	@Import(name = "Load_GL_ActiveTexture")
	public final static native void glActiveTexture(int tex);

	@Import(name = "Load_GL_BindTexture")
	public final static native void glBindTexture(int target, int tex);

	@Import(name = "Load_GL_BlendFunc")
	public final static native void glBlendFunc(int sfactor, int dfactor);

	@Import(name = "Load_GL_Clear")
	public final static native void glClear(int mask);

	@Import(name = "Load_GL_ClearColor")
	public final static native void glClearColor(float red, float green, float blue, float alpha);

	@Import(name = "Load_GL_ClearDepthf")
	public final static native void glClearDepthf(float depth);

	@Import(name = "Load_GL_ClearStencil")
	public final static native void glClearStencil(int sc);

	@Import(name = "Load_GL_ColorMask")
	public final static native void glColorMask(boolean red, boolean green, boolean blue, boolean alpha);

	@Import(name = "Load_GL_CompressedTexImage2D")
	public final static native void glCompressedTexImage2D(int target, int level, int internalformat, int width,
			int height, int border, int imageSize, Buffer data);

	@Import(name = "Load_GL_CompressedTexSubImage2D")
	public final static native void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset,
			int width, int height, int format, int imageSize, Buffer data);

	@Import(name = "Load_GL_CopyTexImage2D")
	public final static native void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width,
			int height, int border);

	@Import(name = "Load_GL_CopyTexSubImage2D")
	public final static native void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y,
			int width, int height);

	@Import(name = "Load_GL_CullFace")
	public final static native void glCullFace(int mode);

	@Import(name = "Load_GL_DeleteTexture")
	public final static native void glDeleteTexture(int texture);

	@Import(name = "Load_GL_DepthFunc")
	public final static native void glDepthFunc(int func);

	@Import(name = "Load_GL_DepthMask")
	public final static native void glDepthMask(int flag);

	@Import(name = "Load_GL_DepthRangef")
	public final static native void glDepthRangef(float zNear, float zFar);

	@Import(name = "Load_GL_Disable")
	public final static native void glDisable(int cap);

	@Import(name = "Load_GL_DrawArrays")
	public final static native void glDrawArrays(int mode, int first, int count);

	@Import(name = "Load_GL_DrawElements")
	public final static native void glDrawElements(int mode, int count, int type, Buffer indices);

	@Import(name = "Load_GL_Enable")
	public final static native void glEnable(int cap);

	@Import(name = "Load_GL_Finish")
	public final static native void glFinish();

	@Import(name = "Load_GL_Flush")
	public final static native void glFlush();

	@Import(name = "Load_GL_FrontFace")
	public final static native void glFrontFace(int mode);

	@Import(name = "Load_GL_GenTexture")
	public final static native int glGenTexture();

	@Import(name = "Load_GL_GetError")
	public final static native int glGetError();

	@Import(name = "Load_GL_GetIntegerv")
	public final static native void glGetIntegerv(int pname, IntBuffer params);

	@Import(name = "Load_GL_GetString")
	public final static native String glGetString(int name);

	@Import(name = "Load_GL_Hint")
	public final static native void glHint(int target, int mode);

	@Import(name = "Load_GL_LineWidth")
	public final static native void glLineWidth(float width);

	@Import(name = "Load_GL_PixelStorei")
	public final static native void glPixelStorei(int pname, int param);

	@Import(name = "Load_GL_PolygonOffset")
	public final static native void glPolygonOffset(float factor, float units);

	@Import(name = "Load_GL_ReadPixels")
	public final static native void glReadPixels(int x, int y, int width, int height, int format, int type,
			Buffer pixels);

	@Import(name = "Load_GL_Scissor")
	public final static native void glScissor(int x, int y, int width, int height);

	@Import(name = "Load_GL_StencilFunc")
	public final static native void glStencilFunc(int func, int ref, int mask);

	@Import(name = "Load_GL_StencilMask")
	public final static native void glStencilMask(int mask);

	@Import(name = "Load_GL_StencilOp")
	public final static native void glStencilOp(int fail, int zfail, int zpass);

	@Import(name = "Load_GL_TexImage2D")
	public final static native void glTexImage2D(int target, int level, int internalformat, int width, int height,
			int border, int format, int type, Buffer pixels);

	@Import(name = "Load_GL_TexParameterf")
	public final static native void glTexParameterf(int target, int pname, float param);

	@Import(name = "Load_GL_TexSubImage2D")
	public final static native void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width,
			int height, int format, int type, Buffer pixels);

	@Import(name = "Load_GL_Viewport")
	public final static native void glViewport(int x, int y, int width, int height);

	@Import(name = "Load_GL_AttachShader")
	public final static native void glAttachShader(int program, int shader);

	@Import(name = "Load_GL_BindAttribLocation")
	public final static native void glBindAttribLocation(int program, int index, String name);

	@Import(name = "Load_GL_BindBuffer")
	public final static native void glBindBuffer(int target, int buffer);

	@Import(name = "Load_GL_BindFramebuffer")
	public final static native void glBindFramebuffer(int target, int framebuffer);

	@Import(name = "Load_GL_BindRenderbuffer")
	public final static native void glBindRenderbuffer(int target, int renderbuffer);

	@Import(name = "Load_GL_BlendColor")
	public final static native void glBlendColor(float red, float green, float blue, float alpha);

	@Import(name = "Load_GL_BlendEquation")
	public final static native void glBlendEquation(int mode);

	@Import(name = "Load_GL_BlendEquationSeparate")
	public final static native void glBlendEquationSeparate(int modeRGB, int modeAlpha);

	@Import(name = "Load_GL_BlendFuncSeparate")
	public final static native void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);

	@Import(name = "Load_GL_BufferData")
	public final static native void glBufferData(int target, int size, Buffer data, int usage);

	@Import(name = "Load_GL_BufferSubData")
	public final static native void glBufferSubData(int target, int offset, int size, Buffer data);

	@Import(name = "Load_GL_CheckFramebufferStatus")
	public final static native int glCheckFramebufferStatus(int target);

	@Import(name = "Load_GL_CompileShader")
	public final static native void glCompileShader(int shader);

	@Import(name = "Load_GL_CreateProgram")
	public final static native int glCreateProgram();

	@Import(name = "Load_GL_CreateShader")
	public final static native int glCreateShader(int type);

	@Import(name = "Load_GL_DeleteBuffer")
	public final static native void glDeleteBuffer(int buffer);

	@Import(name = "Load_GL_DeleteFramebuffer")
	public final static native void glDeleteFramebuffer(int buffer);

	@Import(name = "Load_GL_DeleteProgram")
	public final static native void glDeleteProgram(int program);

	@Import(name = "Load_GL_DeleteRenderbuffer")
	public final static native void glDeleteRenderbuffer(int renderbuffer);

	@Import(name = "Load_GL_DeleteShader")
	public final static native void glDeleteShader(int shader);

	@Import(name = "Load_GL_DetachShader")
	public final static native void glDetachShader(int program, int shader);

	@Import(name = "Load_GL_DisableVertexAttribArray")
	public final static native void glDisableVertexAttribArray(int index);

	@Import(name = "Load_GL_EnableVertexAttribArray")
	public final static native void glEnableVertexAttribArray(int index);

	@Import(name = "Load_GL_FramebufferRenderbuffer")
	public final static native void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget,
			int renderbuffer);

	@Import(name = "Load_GL_FramebufferTexture2D")
	public final static native void glFramebufferTexture2D(int target, int attachment, int textarget, int texture,
			int level);

	@Import(name = "Load_GL_GenBuffer")
	public final static native int glGenBuffer();

	@Import(name = "Load_GL_GenerateMipmap")
	public final static native void glGenerateMipmap();

	@Import(name = "Load_GL_GenFramebuffer")
	public final static native int glGenFramebuffer();
}