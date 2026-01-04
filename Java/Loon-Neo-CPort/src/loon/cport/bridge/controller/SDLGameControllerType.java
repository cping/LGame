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
package loon.cport.bridge.controller;

public final class SDLGameControllerType {

	public static final int SDL_CONTROLLER_TYPE_UNKNOWN = 0;
	public static final int SDL_CONTROLLER_TYPE_XBOX360 = 1;
	public static final int SDL_CONTROLLER_TYPE_XBOXONE = 2;
	public static final int SDL_CONTROLLER_TYPE_PS3 = 3;
	public static final int SDL_CONTROLLER_TYPE_PS4 = 4;
	public static final int SDL_CONTROLLER_TYPE_NINTENDO_SWITCH_PRO = 5;
	public static final int SDL_CONTROLLER_TYPE_VIRTUAL = 6;
	public static final int SDL_CONTROLLER_TYPE_PS5 = 7;
	public static final int SDL_CONTROLLER_TYPE_AMAZON_LUNA = 8;
	public static final int SDL_CONTROLLER_TYPE_GOOGLE_STADIA = 9;
	public static final int SDL_CONTROLLER_TYPE_NVIDIA_SHIELD = 10;
	public static final int SDL_CONTROLLER_TYPE_NINTENDO_SWITCH_JOYCON_LEFT = 11;
	public static final int SDL_CONTROLLER_TYPE_NINTENDO_SWITCH_JOYCON_RIGHT = 12;
	public static final int SDL_CONTROLLER_TYPE_NINTENDO_SWITCH_JOYCON_PAIR = 13;

	private int _gameType;

	public SDLGameControllerType(int type) {
		_gameType = type;
	}

	public int getGameType() {
		return _gameType;
	}

	@Override
	public String toString() {
		switch (_gameType) {
		case SDL_CONTROLLER_TYPE_UNKNOWN:
			return "Unknown Controller";
		case SDL_CONTROLLER_TYPE_XBOX360:
			return "Xbox 360 Controller";
		case SDL_CONTROLLER_TYPE_XBOXONE:
			return "Xbox One Controller";
		case SDL_CONTROLLER_TYPE_PS3:
			return "PlayStation 3 Controller";
		case SDL_CONTROLLER_TYPE_PS4:
			return "PlayStation 4 Controller";
		case SDL_CONTROLLER_TYPE_PS5:
			return "PlayStation 5 Controller";
		case SDL_CONTROLLER_TYPE_NINTENDO_SWITCH_PRO:
			return "Nintendo Switch Pro Controller";
		case SDL_CONTROLLER_TYPE_VIRTUAL:
			return "Virtual Controller";
		case SDL_CONTROLLER_TYPE_AMAZON_LUNA:
			return "Amazon Luna Controller";
		case SDL_CONTROLLER_TYPE_GOOGLE_STADIA:
			return "Google Stadia Controller";
		case SDL_CONTROLLER_TYPE_NVIDIA_SHIELD:
			return "NVIDIA Shield Controller";
		default:
			return "Unrecognized Controller Type";
		}
	}
}
