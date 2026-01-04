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

public final class SDLKeyMod {

	public static final int KMOD_NONE = 0x0000;
	public static final int KMOD_LSHIFT = 0x0001;
	public static final int KMOD_RSHIFT = 0x0002;
	public static final int KMOD_LCTRL = 0x0040;
	public static final int KMOD_RCTRL = 0x0080;
	public static final int KMOD_LALT = 0x0100;
	public static final int KMOD_RALT = 0x0200;
	public static final int KMOD_LGUI = 0x0400;
	public static final int KMOD_RGUI = 0x0800;
	public static final int KMOD_NUM = 0x1000;
	public static final int KMOD_CAPS = 0x2000;
	public static final int KMOD_MODE = 0x4000;
	public static final int KMOD_SCROLL = 0x8000;
	public static final int KMOD_CTRL = (KMOD_LCTRL | KMOD_RCTRL);
	public static final int KMOD_SHIFT = (KMOD_LSHIFT | KMOD_RSHIFT);
	public static final int KMOD_ALT = (KMOD_LALT | KMOD_RALT);
	public static final int KMOD_GUI = (KMOD_LGUI | KMOD_RGUI);
	public static final int KMOD_RESERVED = KMOD_SCROLL;

	private SDLKeyMod() {
	}
}
