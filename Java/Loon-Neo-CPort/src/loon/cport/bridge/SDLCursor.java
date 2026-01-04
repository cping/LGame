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

import loon.LRelease;

public final class SDLCursor implements LRelease {

	public final static SDLCursor createSurfaceCursor(SDLSurface surface, int x, int y) {
		if (surface == null) {
			return null;
		}
		long handle = SDLCall.createColorCursor(surface.getHandle(), x, y);
		return new SDLCursor(handle);
	}

	public final static SDLCursor createSystemCursor(int type) {
		long handle = SDLCall.createSystemCursor(type);
		return new SDLCursor(handle);
	}

	private boolean _closed;

	private long _cursorHandle;

	private SDLCursor(long handle) {
		_cursorHandle = handle;
	}

	public long getHandle() {
		return _cursorHandle;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_cursorHandle != 0 && !_closed) {
			SDLCall.freeCursor(_cursorHandle);
			_cursorHandle = 0;
			_closed = true;
		}
	}

}
