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

public final class SDLArrayOrder {

	public static final int SDL_ARRAYORDER_NONE = 0;
	public static final int SDL_ARRAYORDER_RGB = 1;
	public static final int SDL_ARRAYORDER_RGBA = 2;
	public static final int SDL_ARRAYORDER_ARGB = 3;
	public static final int SDL_ARRAYORDER_BGR = 4;
	public static final int SDL_ARRAYORDER_BGRA = 5;
	public static final int SDL_ARRAYORDER_ABGR = 6;

	private SDLArrayOrder() {
	}
}
