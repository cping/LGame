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

public class SDLButton {

	public static final int SDL_BUTTON_LEFT = 1;
	public static final int SDL_BUTTON_MIDDLE = 2;
	public static final int SDL_BUTTON_RIGHT = 3;
	public static final int SDL_BUTTON_X1 = 4;
	public static final int SDL_BUTTON_X2 = 5;

	private int _value;

	private SDLButton(int v) {
		this._value = v;
	}

	public final static String toString(int v) {
		switch (v) {
		case SDL_BUTTON_LEFT:
			return "SDL_BUTTON_LEFT";
		case SDL_BUTTON_MIDDLE:
			return "SDL_BUTTON_MIDDLE";
		case SDL_BUTTON_RIGHT:
			return "SDL_BUTTON_RIGHT";
		case SDL_BUTTON_X1:
			return "SDL_BUTTON_X1";
		case SDL_BUTTON_X2:
			return "SDL_BUTTON_X2";
		default:
			return "UNKNOWN";
		}
	}

	@Override
	public String toString() {
		return toString(_value);
	}

}
