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

import org.teavm.backend.c.intrinsic.RuntimeInclude;
import org.teavm.interop.Import;

public final class SDL {

	private SDL() {
	}

	@RuntimeInclude("SDLSupport.h")
	@Import(name = "GetPathFullName")
	public final static native String getPathFullName(String dst, String path);

	@Import(name = "GetSystemProperty")
	public final static native String getSystemProperty(String key);

	@Import(name = "Call_SDL_DestroyWindow")
	public final static native void destroyWindow();

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

	@Import(name = "Load_SDL_ScreenInit")
	public final static native long screenInit(String title, int w, int h, boolean vsync);

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
}