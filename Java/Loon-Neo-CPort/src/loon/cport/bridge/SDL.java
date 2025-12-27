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
	public final static native boolean getAxes(int run);

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

}
