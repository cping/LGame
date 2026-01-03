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

public final class SDLWindowFlags {

	public final static int Shown = SDLCall.SDL_WINDOW_SHOWN;

	public final static int Hidden = SDLCall.SDL_WINDOW_HIDDEN;

	public final static int Resizable = SDLCall.SDL_WINDOW_RESIZABLE;

	public final static int Maximized = SDLCall.SDL_WINDOW_MAXIMIZED;

	public final static int Minimized = SDLCall.SDL_WINDOW_MINIMIZED;

	public final static int Fullscreen = SDLCall.SDL_WINDOW_FULLSCREEN;

	public final static int FullscreenDesktop = SDLCall.SDL_WINDOW_FULLSCREEN;

	private int _defaultType;

	private boolean _isShow;

	private boolean _isHide;

	private boolean _isResize;

	private boolean _isMax;

	private boolean _isMin;

	private boolean _isFull;

	private boolean _isFullDesktp;

	public SDLWindowFlags() {

	}

	public SDLWindowFlags show() {
		_isShow = true;
		return this;
	}

	public SDLWindowFlags hide() {
		_isHide = true;
		return this;
	}

	public SDLWindowFlags resize() {
		_isResize = true;
		return this;
	}

	public SDLWindowFlags min() {
		_isMin = true;
		return this;
	}

	public SDLWindowFlags max() {
		_isMax = true;
		return this;
	}

	public SDLWindowFlags full() {
		_isFull = true;
		return this;
	}

	public SDLWindowFlags fullDesktop() {
		_isFullDesktp = true;
		return this;
	}

	public SDLWindowFlags noshow() {
		_isShow = false;
		return this;
	}

	public SDLWindowFlags nohide() {
		_isHide = false;
		return this;
	}

	public SDLWindowFlags noresize() {
		_isResize = false;
		return this;
	}

	public SDLWindowFlags nomin() {
		_isMin = false;
		return this;
	}

	public SDLWindowFlags nomax() {
		_isMax = false;
		return this;
	}

	public SDLWindowFlags nofull() {
		_isFull = false;
		return this;
	}

	public SDLWindowFlags nofullDesktop() {
		_isFullDesktp = false;
		return this;
	}

	public int getValue() {
		_defaultType = Shown;
		if (!_isShow) {
			_defaultType |= Hidden;
		}
		if (_isHide) {
			_defaultType |= Hidden;
		}
		if (_isResize) {
			_defaultType |= Resizable;
		}
		if (_isMin) {
			_defaultType |= Minimized;
		}
		if (_isMax) {
			_defaultType |= Maximized;
		}
		if (_isFull) {
			_defaultType |= Fullscreen;
		}
		if (_isFullDesktp) {
			_defaultType |= FullscreenDesktop;
		}
		return _defaultType;
	}
}
