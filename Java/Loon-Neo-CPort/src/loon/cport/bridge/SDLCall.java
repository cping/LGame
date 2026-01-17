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

import java.nio.IntBuffer;

import org.teavm.backend.c.intrinsic.RuntimeInclude;
import org.teavm.interop.Address;
import org.teavm.interop.Import;

public final class SDLCall {

	public static final float SDL_STANDARD_GRAVITY = 9.80665f;

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
			SDL_GL_CONTEXT_FLAGS = 20, SDL_NUM_SCANCODES = 512;

	private SDLCall() {
		importInclude();
	}

	@RuntimeInclude("SDLSupport.h")
	@Import(name = "ImportSDLInclude")
	public final static native void importInclude();

	@Import(name = "ISDebugStatus")
	public final static native boolean isDebugStatus();

	@Import(name = "CreateSingleInstanceLock")
	public final static native boolean createAppLock();

	@Import(name = "FreeSingleLock")
	public final static native void freeAppLock();

	@Import(name = "SDL_AllowExit")
	public final static native void setAllowExit(boolean a);

	@Import(name = "LOG_Println")
	public final static native void logPrintln(String mes);

	@Import(name = "CreatePrefs")
	public final static native long createGamePrefs();

	@Import(name = "LoadPrefs")
	public final static native boolean loadGamePrefs(long handle, String filename);

	@Import(name = "GetPrefsKeys")
	public final static native String getGamePrefsKeys(long handle, String section, String delimiter);

	@Import(name = "SetPrefs")
	public final static native void setGamePrefs(long handle, String section, String key, byte[] value, int value_len);

	@Import(name = "GetPrefs")
	public final static native long getGamePrefs(long handle, String section, String key, byte[] bytes);

	@Import(name = "SavePrefs")
	public final static native boolean saveGamePrefs(long handle, String filename);

	@Import(name = "RemovePrefs")
	public final static native void removeGamePrefs(long handle, String section, String key);

	@Import(name = "FreePrefs")
	public final static native void freeGamePrefs(long handle);

	@Import(name = "CreateGameData")
	public final static native long createGameData(String fileName);

	@Import(name = "ReadGameData")
	public final static native long readGameData(long handle, String fileName, byte[] bytes);

	@Import(name = "WriteGameData")
	public final static native boolean writeGameData(long handle, String fileName, String data, long size);

	@Import(name = "GetGameDataFileCount")
	public final static native int getGameDataFileCount(long handle);

	@Import(name = "FreeGameData")
	public final static native int freeGameData(long handle);

	@Import(name = "GetPathFullName")
	public final static native String getPathFullName(String path);

	@Import(name = "GetSystemProperty")
	public final static native String getSystemProperty(String key);

	@Import(name = "FileExists")
	public final static native boolean fileExists(String fileName);

	@Import(name = "Load_SDL_RW_FileExists")
	public final static native boolean rwFileExists(String fileName);

	@Import(name = "Load_SDL_RW_FileToChars")
	public final static native String loadRWFileToChars(String fileName);

	@Import(name = "Load_SDL_RW_FileSize")
	public final static native long getFileSize(String fileName);

	@Import(name = "Load_SDL_RW_FileToBytes")
	public final static native long LoadRWFileToBytes(String fileName, byte[] outBytes);

	@Import(name = "Load_SDL_GetPreferredLocales")
	public final static native String getPreferredLocales();

	@Import(name = "Load_SDL_GetBasePath")
	public final static native String getBasePath();

	@Import(name = "Load_SDL_GetPrefPath")
	public final static native String getPrefPath(String org, String app);

	@Import(name = "Load_SDL_GetPlatform")
	public final static native String getPlatform();

	@Import(name = "Load_SDL_GetTicks")
	public final static native int getTicks();

	@Import(name = "Load_SDL_GetTicks64")
	public final static native long getTicks64();

	@Import(name = "Load_RemapControllers")
	public final static native void remapControllers(int min, int max, int dualJoy, int singleMode);

	@Import(name = "Load_IsConnected")
	public final static native boolean isConnected(int controller);

	@Import(name = "Load_Buttons")
	public final static native int getButtons();

	@Import(name = "Load_Axes")
	public final static native int getAxes(int controller, float[] axes);

	@Import(name = "Load_SDL_Exit")
	public final static native boolean exit(int run);

	@Import(name = "Load_SDL_GetDrawableSize")
	public final static native void getDrawableSize(long handle, int[] values);

	@Import(name = "Load_SDL_GetDrawableSize")
	public final static native void getWindowSize(long handle, int[] values);

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

	@Import(name = "Load_SDL_CreateRGBSurfaceFrom32")
	public final static native long createRGBSurfaceFrom32(byte[] pixels, int w, int h);

	@Import(name = "Load_SDL_ConvertSurfaceFormat")
	public final static native long convertSurfaceFormat(long handle, int pixel_format, int flags);

	@Import(name = "Load_SDL_GetSurfaceSize")
	public final static native void getSurfaceSize(long handle, int[] values);

	@Import(name = "Load_SDL_GetSurfacePixels32")
	public final static native void getSurfacePixels32(long handle, int order, int[] pixels);

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
	public final static native void getSurfaceClipRect(long handle, int[] values);

	@Import(name = "Load_SDL_GetFormat")
	public final static native int getSurfaceFormat(long handle);

	@Import(name = "Load_SDL_GetPolleventType")
	public final static native int getPolleventType();

	@Import(name = "Load_SDL_Update")
	public final static native boolean runSDLUpdate();

	@Import(name = "Load_SDL_TouchData")
	public final static native int getTouchData(int[] data);

	@Import(name = "Load_SDL_GetKeyStates")
	public final static native int getKeyStates(int[] data);

	@Import(name = "Load_SDL_GetPressedKeys")
	public final static native int getPressedKeys(int[] data);

	@Import(name = "Load_SDL_GetReleasedKeys")
	public final static native int getReleasedKeys(int[] data);

	@Import(name = "Load_SDL_GetLastPressedScancode")
	public final static native int getLastPressedScancode();

	@Import(name = "Load_SDL_Current_Screen_Size")
	public final static native void getCurrentScreenSize(int[] rect);

	@Import(name = "Load_SDL_Current_Window_Size")
	public final static native void getCurrentWindowSize(int[] rect);

	@Import(name = "Load_SDL_Pause")
	public final static native boolean isPaused();

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

	@Import(name = "Load_SDL_HasClipboardText")
	public final static native boolean hasClipboardText();

	@Import(name = "Load_SDL_SetPrimarySelectionText")
	public final static native int setPrimarySelectionText(String text);

	@Import(name = "Load_SDL_GetPrimarySelectionText")
	public final static native String getPrimarySelectionText();

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
	public final static native void getDisplayUsableBounds(long handle, int[] xywh);

	@Import(name = "Load_SDL_GetDisplayBounds")
	public final static native void getDisplayBounds(long handle, int[] xywh);

	@Import(name = "Load_SDL_GetNumVideoDisplays")
	public final static native int getNumVideoDisplays();

	@Import(name = "Load_SDL_GetWindowFlags")
	public final static native int getWindowFlags(long handle);

	@Import(name = "Load_SDL_SetWindowTitle")
	public final static native void setWindowTitle(long handle, String title);

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

	@Import(name = "Load_SDL_GL_SwapScreen")
	public final static native int swapScreen();

	@Import(name = "Load_SDL_GL_SwapWindowHandle")
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
	public final static native void getDrawableSize(int[] values);

	@Import(name = "Call_SDL_GetWindowSize")
	public final static native void getWindowSize(int[] values);

	@Import(name = "Call_SDL_GetRenderScale")
	public final static native void getRenderScale(float[] values);

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

	@Import(name = "Load_SDL_Mix_LoadMUSFromMem")
	public final static native long loadMUS(byte[] bytes);

	@Import(name = "Load_SDL_Mix_PlayMusic")
	public final static native void playMusic(long handle, boolean looping);

	@Import(name = "Load_SDL_Mix_PlayFadeInMusic")
	public final static native void playFadeInMusic(long handle, boolean looping);

	@Import(name = "Load_SDL_Mix_PlayMusicFadeStop")
	public final static native void playMusicFadeStop();

	@Import(name = "Load_SDL_MIX_SetMusicPosition")
	public final static native void setMusicPosition(float position);

	@Import(name = "Load_SDL_Mix_GetMusicPosition(")
	public final static native float getMusicPosition(long handle);

	@Import(name = "Load_SDL_Mix_SetMusicVolume")
	public final static native void setMusicVolume(float volume);

	@Import(name = "Load_SDL_Mix_GetMusicVolume")
	public final static native float getMusicVolume();

	@Import(name = "Load_SDL_Mix_PlayingMusic")
	public final static native boolean isPlayingMusic();

	@Import(name = "Load_SDL_Mix_IsLoopingMusic")
	public final static native boolean isLoopingMusic();

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

	@Import(name = "Load_SDL_Mix_IsLoopingSound")
	public final static native boolean isLoopingSound(int channel);

	@Import(name = "Load_SDL_Mix_GetVolume")
	public final static native int getSoundVolume(int channel);

	@Import(name = "Load_SDL_Mix_SetPosition")
	public final static native void setPosition(int channel, int angle, int distance);

	@Import(name = "Load_SDL_Mix_FadeInChannel")
	public final static native void fadeInSoundChannel(int channel, int ms);

	@Import(name = "Load_SDL_Mix_FadeOutChannel")
	public final static native void fadeOutSoundChannel(int channel, int ms);

	@Import(name = "Load_SDL_Mix_Playing")
	public final static native boolean isSoundPlaying(int channel);

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

	@Import(name = "Load_SDL_QuitRequested")
	public final static native boolean quitRequested();

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
			int height, int border, int imageSize, Address data);

	@Import(name = "Load_GL_CompressedTexImage2DOffset")
	public final static native void glCompressedTexImage2DOffset(int target, int level, int internalformat, int width,
			int height, int border, int imageSize, long offset);

	@Import(name = "Load_GL_CompressedTexSubImage2D")
	public final static native void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset,
			int width, int height, int format, int imageSize, Address data);

	@Import(name = "Load_GL_CompressedTexSubImage2DOffset")
	public final static native void glCompressedTexSubImage2DOffset(int target, int level, int xoffset, int yoffset,
			int width, int height, int format, int imageSize, long offset);

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

	@Import(name = "Load_GL_DeleteTextures")
	public final static native void glDeleteTextures(int texture, Address textures);

	@Import(name = "Load_GL_DepthFunc")
	public final static native void glDepthFunc(int func);

	@Import(name = "Load_GL_DepthMask")
	public final static native void glDepthMask(boolean flag);

	@Import(name = "Load_GL_DepthRangef")
	public final static native void glDepthRangef(float zNear, float zFar);

	@Import(name = "Load_GL_Disable")
	public final static native void glDisable(int cap);

	@Import(name = "Load_GL_DrawArrays")
	public final static native void glDrawArrays(int mode, int first, int count);

	@Import(name = "Load_GL_DrawElements")
	public final static native void glDrawElements(int mode, int count, int type, Address indices);

	@Import(name = "Load_GL_DrawElementsOffset")
	public final static native void glDrawElementsOffset(int mode, int count, int type, long offset);

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

	@Import(name = "Load_GL_GenTextures")
	public final static native void glGenTextures(int n, Address textures);

	@Import(name = "Load_GL_GetError")
	public final static native int glGetError();

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
			Address pixels);

	@Import(name = "Load_GL_ReadPixelsOffset")
	public final static native void glReadPixelsOffset(int x, int y, int width, int height, int format, int type,
			long pixelsOffset);

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
			int border, int format, int type, Address pixels);

	@Import(name = "Load_GL_TexImage2DOffset")
	public final static native void glTexImage2DOffset(int target, int level, int internalformat, int width, int height,
			int border, int format, int type, long pixelsOffset);

	@Import(name = "Load_GL_TexParameterf")
	public final static native void glTexParameterf(int target, int pname, float param);

	@Import(name = "Load_GL_TexSubImage2D")
	public final static native void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width,
			int height, int format, int type, Address pixels);

	@Import(name = "Load_GL_TexSubImage2DOffset")
	public final static native void glTexSubImage2DOffset(int target, int level, int xoffset, int yoffset, int width,
			int height, int format, int type, long pixelsOffset);

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
	public final static native void glBufferData(int target, int size, Address data, int usage);

	@Import(name = "Load_GL_BufferSubData")
	public final static native void glBufferSubData(int target, int offset, int size, Address data);

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

	@Import(name = "Load_GL_DeleteBuffers")
	public final static native void glDeleteBuffers(int n, Address buffers);

	@Import(name = "Load_GL_DeleteFramebuffer")
	public final static native void glDeleteFramebuffer(int buffer);

	@Import(name = "Load_GL_DeleteFramebuffers")
	public final static native void glDeleteFramebuffers(int n, Address framebuffers);

	@Import(name = "Load_GL_DeleteProgram")
	public final static native void glDeleteProgram(int program);

	@Import(name = "Load_GL_DeleteRenderbuffer")
	public final static native void glDeleteRenderbuffer(int renderbuffer);

	@Import(name = "Load_GL_DeleteRenderbuffers")
	public final static native void glDeleteRenderbuffers(int n, Address renderbuffers);

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

	@Import(name = "Load_GL_GenBuffers")
	public final static native void glGenBuffers(int n, Address buffers);

	@Import(name = "Load_GL_GenRenderbuffer")
	public final static native int glGenRenderbuffer();

	@Import(name = "Load_GL_GenRenderbuffers")
	public final static native void glGenRenderbuffers(int n, Address buffers);

	@Import(name = "Load_GL_GenerateMipmap")
	public final static native void glGenerateMipmap(int target);

	@Import(name = "Load_GL_GenFramebuffer")
	public final static native int glGenFramebuffer();

	@Import(name = "Load_GL_GenFramebuffers")
	public final static native void glGenFramebuffers(int n, Address buffers);

	@Import(name = "Load_GL_GetActiveAttrib")
	public final static native String glGetActiveAttrib(int program, int index, Address size, Address type);

	@Import(name = "Load_GL_GetActiveUniform")
	public final static native String glGetActiveUniform(int program, int index, Address size, Address type);

	@Import(name = "Load_GL_GetAttribLocation")
	public final static native int glGetAttribLocation(int program, String name);

	@Import(name = "Load_GL_GetBooleanv")
	public final static native void glGetBooleanv(int pname, Address params);

	@Import(name = "Load_GL_GetBooleanvResult")
	public final static native boolean glGetBooleanvResult(int pname);

	@Import(name = "Load_GL_GetBufferParameteriv")
	public final static native void glGetBufferParameteriv(int target, int pname, Address params);

	@Import(name = "Load_GL_GetFloatv")
	public final static native void glGetFloatv(int pname, Address params);

	@Import(name = "Load_GL_GetFloatvResult")
	public final static native float glFloatvResult(int pname);

	@Import(name = "Load_GL_GetIntegerv")
	public final static native void glGetIntegerv(int pname, Address params);

	@Import(name = "Load_GL_GetIntegervResult")
	public final static native int glIntegervResult(int pname);

	@Import(name = "Load_GL_GetFramebufferAttachmentParameteriv")
	public final static native void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname,
			Address params);

	@Import(name = "Load_GL_GetProgramiv")
	public final static native void glGetProgramiv(int program, int pname, Address params);

	@Import(name = "Load_GL_GetProgramInfoLog")
	public final static native String glGetProgramInfoLog(int program);

	@Import(name = "Load_GL_GetProgramInfoLogs")
	public final static native String glGetProgramInfoLogs(int program, int bufsize, Address length, Address infolog);

	@Import(name = "Load_GL_GetRenderbufferParameteriv")
	public final static native void glGetRenderbufferParameteriv(int target, int pname, Address params);

	@Import(name = "Load_GL_GetShaderiv")
	public final static native void glGetShaderiv(int shader, int pname, Address params);

	@Import(name = "Load_GL_GetShaderInfoLog")
	public final static native String glGetShaderInfoLog(int shader);

	@Import(name = "Load_GL_GetShaderInfoLogs")
	public final static native String glGetShaderInfoLogs(int shader, int bufsize, Address length, Address infolog);

	@Import(name = "Load_GL_GetShaderPrecisionFormat")
	public final static native void glGetShaderPrecisionFormat(int shadertype, int precisiontype, Address range,
			Address precision);

	@Import(name = "Load_GL_ShaderBinary")
	public final static native void glShaderBinary(int count, IntBuffer shaders, int binaryFormat, Address binary,
			int length);

	@Import(name = "Load_GL_GetTexParameterfv")
	public final static native void glGetTexParameterfv(int target, int pname, Address params);

	@Import(name = "Load_GL_GetTexParameteriv")
	public final static native void glGetTexParameteriv(int target, int pname, Address params);

	@Import(name = "Load_GL_GetUniformfv")
	public final static native void glGetUniformfv(int program, int location, Address params);

	@Import(name = "Load_GL_GetUniformiv")
	public final static native void glGetUniformiv(int program, int location, Address params);

	@Import(name = "Load_GL_GetUniformLocation")
	public final static native int glGetUniformLocation(int program, String name);

	@Import(name = "Load_GL_GetVertexAttribfv")
	public final static native void glGetVertexAttribfv(int index, int pname, Address params);

	@Import(name = "Load_GL_GetVertexAttribiv")
	public final static native void glGetVertexAttribiv(int index, int pname, Address params);

	@Import(name = "Load_GL_IsBuffer")
	public final static native boolean glIsBuffer(int buffer);

	@Import(name = "Load_GL_IsEnabled")
	public final static native boolean glIsEnabled(int cap);

	@Import(name = "Load_GL_IsFramebuffer")
	public final static native boolean glIsFramebuffer(int framebuffer);

	@Import(name = "Load_GL_IsProgram")
	public final static native boolean glIsProgram(int program);

	@Import(name = "Load_GL_IsRenderbuffer")
	public final static native boolean glIsRenderbuffer(int renderbuffer);

	@Import(name = "Load_GL_IsShader")
	public final static native boolean glIsShader(int shader);

	@Import(name = "Load_GL_IsTexture")
	public final static native boolean glIsTexture(int texture);

	@Import(name = "Load_GL_LinkProgram")
	public final static native void glLinkProgram(int texture);

	@Import(name = "Load_GL_ReleaseShaderCompiler")
	public final static native void glReleaseShaderCompiler();

	@Import(name = "Load_GL_RenderbufferStorage")
	public final static native void glRenderbufferStorage(int target, int internalformat, int width, int height);

	@Import(name = "Load_GL_SampleCoverage")
	public final static native void glSampleCoverage(float value, boolean invert);

	@Import(name = "Load_GL_ShaderSource")
	public final static native void glShaderSource(int shader, String str);

	@Import(name = "Load_GL_StencilFuncSeparate")
	public final static native void glStencilFuncSeparate(int face, int func, int ref, int mask);

	@Import(name = "Load_GL_StencilMaskSeparate")
	public final static native void glStencilMaskSeparate(int face, int mask);

	@Import(name = "Load_GL_StencilOpSeparate")
	public final static native void glStencilOpSeparate(int face, int fail, int zfail, int zpass);

	@Import(name = "Load_GL_TexParameterfv")
	public final static native void glTexParameterfv(int target, int pname, Address params);

	@Import(name = "Load_GL_TexParameteri")
	public final static native void glTexParameteri(int target, int pname, int param);

	@Import(name = "Load_GL_TexParameteriv")
	public final static native void glTexParameteriv(int target, int pname, Address params);

	@Import(name = "Load_GL_Uniform1f")
	public final static native void glUniform1f(int location, float x);

	@Import(name = "Load_GL_Uniform1fv")
	public final static native void glUniform1fv(int location, int count, Address v);

	@Import(name = "Load_GL_Uniform1fvOffset")
	public final static native void glUniform1fvOffset(int location, int count, Address v, int offset);

	@Import(name = "Load_GL_Uniform1i")
	public final static native void glUniform1i(int location, int x);

	@Import(name = "Load_GL_Uniform1iv")
	public final static native void glUniform1iv(int location, int count, Address v);

	@Import(name = "Load_GL_Uniform1ivOffset")
	public final static native void glUniform1ivOffset(int location, int count, Address v, int offset);

	@Import(name = "Load_GL_Uniform2f")
	public final static native void glUniform2f(int location, float x, float y);

	@Import(name = "Load_GL_Uniform2fv")
	public final static native void glUniform2fv(int location, int count, Address v);

	@Import(name = "Load_GL_Uniform2fvOffset")
	public final static native void glUniform2fvOffset(int location, int count, Address v, int offset);

	@Import(name = "Load_GL_Uniform2i")
	public final static native void glUniform2i(int location, int x, int y);

	@Import(name = "Load_GL_Uniform2iv")
	public final static native void glUniform2iv(int location, int count, Address v);

	@Import(name = "Load_GL_Uniform2ivOffset")
	public final static native void glUniform2ivOffset(int location, int count, Address v, int offset);

	@Import(name = "Load_GL_Uniform3f")
	public final static native void glUniform3f(int location, float x, float y, float z);

	@Import(name = "Load_GL_Uniform3fv")
	public final static native void glUniform3fv(int location, int count, Address v);

	@Import(name = "Load_GL_Uniform3fvOffset")
	public final static native void glUniform3fvOffset(int location, int count, Address v, int offset);

	@Import(name = "Load_GL_Uniform3i")
	public final static native void glUniform3i(int location, int x, int y, int z);

	@Import(name = "Load_GL_Uniform3iv")
	public final static native void glUniform3iv(int location, int count, Address v);

	@Import(name = "Load_GL_Uniform3ivOffset")
	public final static native void glUniform3ivOffset(int location, int count, Address v, int offset);

	@Import(name = "Load_GL_Uniform4f")
	public final static native void glUniform4f(int location, float x, float y, float z, float w);

	@Import(name = "Load_GL_Uniform4fv")
	public final static native void glUniform4fv(int location, int count, Address v);

	@Import(name = "Load_GL_Uniform4fvOffset")
	public final static native void glUniform4fvOffset(int location, int count, Address v, int offset);

	@Import(name = "Load_GL_Uniform4i")
	public final static native void glUniform4i(int location, int x, int y, int z, int w);

	@Import(name = "Load_GL_Uniform4iv")
	public final static native void glUniform4iv(int location, int count, Address v);

	@Import(name = "Load_GL_Uniform4ivOffset")
	public final static native void glUniform4ivOffset(int location, int count, Address v, int offset);

	@Import(name = "Load_GL_UniformMatrix2fv")
	public final static native void glUniformMatrix2fv(int location, int count, boolean transpose, Address value);

	@Import(name = "Load_GL_UniformMatrix2fvOffset")
	public final static native void glUniformMatrix2fvOffset(int location, int count, boolean transpose, Address value,
			int offset);

	@Import(name = "Load_GL_UniformMatrix3fv")
	public final static native void glUniformMatrix3fv(int location, int count, boolean transpose, Address value);

	@Import(name = "Load_GL_UniformMatrix4fv")
	public final static native void glUniformMatrix4fv(int location, int count, boolean transpose, Address value);

	@Import(name = "Load_GL_UniformMatrix4fvOffset")
	public final static native void glUniformMatrix4fvOffset(int location, int count, boolean transpose, Address value,
			int offset);

	@Import(name = "Load_GL_VertexAttrib1f")
	public final static native void glVertexAttrib1f(int index, float x);

	@Import(name = "Load_GL_VertexAttrib1fv")
	public final static native void glVertexAttrib1fv(int index, Address values);

	@Import(name = "Load_GL_VertexAttrib2f")
	public final static native void glVertexAttrib2f(int index, float x, float y);

	@Import(name = "Load_GL_VertexAttrib2fv")
	public final static native void glVertexAttrib2fv(int index, Address values);

	@Import(name = "Load_GL_VertexAttrib3f")
	public final static native void glVertexAttrib3f(int index, float x, float y, float z);

	@Import(name = "Load_GL_VertexAttrib3fv")
	public final static native void glVertexAttrib3fv(int index, Address values);

	@Import(name = "Load_GL_VertexAttrib4f")
	public final static native void glVertexAttrib4f(int index, float x, float y, float z, float w);

	@Import(name = "Load_GL_VertexAttrib4fv")
	public final static native void glVertexAttrib4fv(int index, Address values);

	@Import(name = "Load_GL_VertexAttribPointer")
	public final static native void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride,
			Address ptr);

	@Import(name = "Load_GL_VertexAttribPointerOffset")
	public final static native void glVertexAttribPointerOffset(int index, int size, int type, boolean normalized,
			int stride, long offset);

	@Import(name = "Load_GL_GetAttachedShaders")
	public final static native void glGetAttachedShaders(int program, int maxCount, Address count, Address shaders);

	@Import(name = "Load_GL_GetShaderSource")
	public final static native void glGetShaderSource(int shader, int bufSize, Address count, Address shaders);

	// Controller
	@Import(name = "Load_SDL_IsGameController")
	public final static native boolean isGameController(int joystickIndex);

	@Import(name = "Load_SDL_GameControllerOpen")
	public final static native long gameControllerOpen(int joystickIndex);

	@Import(name = "Load_SDL_GameControllerTemp")
	public final static native long gameControllerTemp();

	@Import(name = "Load_SDL_GameControllerClose")
	public final static native void gameControllerClose(long handle);

	@Import(name = "Load_SDL_GameControllerName")
	public final static native String gameControllerName(long handle);

	@Import(name = "Load_SDL_GameControllerPath")
	public final static native String gameControllerPath(long handle);

	@Import(name = "Load_SDL_GameControllerGetType")
	public final static native int gameControllerGetType(long handle);

	@Import(name = "Load_SDL_GameControllerGetPlayerIndex")
	public final static native int gameControllerGetPlayerIndex(long handle);

	@Import(name = "Load_SDL_GameControllerSetPlayerIndex")
	public final static native void gameControllerSetPlayerIndex(long handle, int joystickIndex);

	@Import(name = "Load_SDL_GameControllerGetVendor")
	public final static native short gameControllerGetVendor(long handle);

	@Import(name = "Load_SDL_GameControllerGetNumTouchpads")
	public final static native int gameControllerGetNumTouchpads(long handle);

	@Import(name = "Load_SDL_GameControllerNameForIndex")
	public final static native String gameControllerNameForIndex(int joystickIndex);

	@Import(name = "Load_SDL_GameControllerPathForIndex")
	public final static native String gameControllerPathForIndex(int joystickIndex);

	@Import(name = "Load_SDL_GameControllerMappingForIndex")
	public final static native String gameControllerMappingForIndex(int mappingIndex);

	@Import(name = "Load_SDL_GameControllerMappingForDeviceIndex")
	public final static native String gameControllerMappingForDeviceIndex(int joystickIndex);

	@Import(name = "Load_SDL_GameControllerTypeForIndex")
	public final static native int gameControllerTypeForIndex(int joystickIndex);

	@Import(name = "Load_SDL_GameControllerEventState")
	public final static native int gameControllerEventState(int state);

	@Import(name = "Load_SDL_GameControllerAddMapping")
	public final static native int gameControllerAddMapping(String mappingString);

	@Import(name = "Load_SDL_GameControllerGetAxisFromString")
	public final static native int gameControllerGetAxisFromString(String axisString);

	@Import(name = "Load_SDL_GameControllerGetButtonFromString")
	public final static native int gameControllerGetButtonFromString(String btnString);

	@Import(name = "Load_SDL_GameControllerNumMappings")
	public final static native int gameControllerNumMappings();

	@Import(name = "Load_SDL_GameControllerUpdate")
	public final static native void gameControllerUpdate();

	@Import(name = "Load_SDL_GameControllerGetJoystick")
	public final static native long gameControllerGetJoystick(long handle);

	@Import(name = "Load_SDL_JoystickOpen")
	public final static native long joystickOpen(long handle);

	@Import(name = "Load_SDL_JoystickGetGUIDString")
	public final static native String joystickGetGUIDString(long handle);

	@Import(name = "Load_SDL_JoystickClose")
	public final static native void joystickClose(long handle);

	@Import(name = "Load_SDL_JoystickNumAxes")
	public final static native int joystickNumAxes(long handle);

	@Import(name = "Load_SDL_JoystickNumBalls")
	public final static native int joystickNumBalls(long handle);

	@Import(name = "Load_SDL_JoystickNumHats")
	public final static native int joystickNumHats(long handle);

	@Import(name = "Load_SDL_JoystickNumButtons")
	public final static native int joystickNumButtons(long handle);

	@Import(name = "Load_SDL_LockJoysticks")
	public final static native void lockJoysticks();

	@Import(name = "Load_SDL_UnlockJoysticks")
	public final static native void unlockJoysticks();

	@Import(name = "Load_SDL_NumJoysticks")
	public final static native int numJoysticks();

	@Import(name = "Load_SDL_JoystickGetDeviceVendor")
	public final static native short joystickGetDeviceVendor(int deviceIndex);

	@Import(name = "Load_SDL_JoystickGetDeviceProduct")
	public final static native short joystickGetDeviceProduct(int deviceIndex);

	@Import(name = "Load_SDL_JoystickGetDeviceProductVersion")
	public final static native short joystickGetDeviceProductVersion(int deviceIndex);

	@Import(name = "Load_SDL_JoystickDetachVirtual")
	public final static native int joystickDetachVirtual(int deviceIndex);

	@Import(name = "Load_SDL_JoystickIsVirtual")
	public final static native boolean joystickIsVirtual(int deviceIndex);

	@Import(name = "Load_SDL_JoystickNameForIndex")
	public final static native String joystickNameForIndex(int deviceIndex);

	@Import(name = "Load_SDL_JoystickPathForIndex")
	public final static native String joystickPathForIndex(int deviceIndex);

	@Import(name = "Load_SDL_JoystickGetDevicePlayerIndex")
	public final static native int joystickGetDevicePlayerIndex(int deviceIndex);

	@Import(name = "Load_SDL_SensorOpen")
	public final static native long sensorOpen(int deviceIndex);

	@Import(name = "Load_SDL_SensorClose")
	public final static native void sensorClose(long handle);

	@Import(name = "Load_SDL_SensorGetName")
	public final static native String sensorGetName(long handle);

	@Import(name = "Load_SDL_SensorGetType")
	public final static native int sensorGetType(long handle);

	@Import(name = "Load_SDL_SensorGetNonPortableType")
	public final static native int sensorGetNonPortableType(long handle);

	@Import(name = "Load_SDL_SensorGetData")
	public final static native int sensorGetData(long handle, float[] data, int numValues);

	@Import(name = "Load_SDL_LockSensors")
	public final static native void lockSensors();

	@Import(name = "Load_SDL_UnlockSensors")
	public final static native void unlockSensors();

	@Import(name = "Load_SDL_NumSensors")
	public final static native int numSensors();

	@Import(name = "Load_SDL_SensorGetDeviceName")
	public final static native String sensorGetDeviceName(int deviceIndex);

	@Import(name = "Load_SDL_SensorGetDeviceType")
	public final static native String sensorGetDeviceType(int deviceIndex);

	@Import(name = "Load_SDL_SensorGetDeviceNonPortableType")
	public final static native String sensorGetDeviceNonPortableType(int deviceIndex);

	@Import(name = "Load_SDL_SensorUpdate")
	public final static native void sensorUpdate();

}