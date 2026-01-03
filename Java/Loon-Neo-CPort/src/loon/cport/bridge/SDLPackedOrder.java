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

public final class SDLPackedOrder {

	public static final int SDL_PACKEDORDER_NONE = 0;
	public static final int SDL_PACKEDORDER_XRGB = 1;
	public static final int SDL_PACKEDORDER_RGBX = 2;
	public static final int SDL_PACKEDORDER_ARGB = 3;
	public static final int SDL_PACKEDORDER_RGBA = 4;
	public static final int SDL_PACKEDORDER_XBGR = 5;
	public static final int SDL_PACKEDORDER_BGRX = 6;
	public static final int SDL_PACKEDORDER_ABGR = 7;
	public static final int SDL_PACKEDORDER_BGRA = 8;

	private SDLPackedOrder() {
	}
}
