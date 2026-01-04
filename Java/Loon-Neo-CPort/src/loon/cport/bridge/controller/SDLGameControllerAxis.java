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

public final class SDLGameControllerAxis {

	public static final int SDL_CONTROLLER_AXIS_INVALID = -1;
	public static final int SDL_CONTROLLER_AXIS_LEFTX = 0;
	public static final int SDL_CONTROLLER_AXIS_LEFTY = 1;
	public static final int SDL_CONTROLLER_AXIS_RIGHTX = 2;
	public static final int SDL_CONTROLLER_AXIS_RIGHTY = 3;
	public static final int SDL_CONTROLLER_AXIS_TRIGGERLEFT = 4;
	public static final int SDL_CONTROLLER_AXIS_TRIGGERRIGHT = 5;
	public static final int SDL_CONTROLLER_AXIS_MAX = 6;

	private SDLGameControllerAxis() {
	}
}
