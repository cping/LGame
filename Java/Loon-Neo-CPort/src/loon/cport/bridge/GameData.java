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

public final class GameData implements LRelease {

	public final static GameData create(String basepath) {
		long handle = SDLCall.createGameData(basepath);
		return new GameData(handle);
	}

	private boolean _closed;

	private long _gamedataHandle;

	private GameData(long handle) {
		_gamedataHandle = handle;
	}

	public boolean write(String fileName, String text) {
		if (text == null) {
			return false;
		}
		return write(fileName, text, text.length());
	}

	public boolean write(String fileName, String text, long size) {
		if (fileName == null || text == null) {
			return false;
		}
		return SDLCall.writeGameData(_gamedataHandle, fileName, text, size);
	}

	public String read(String fileName) {
		return read(fileName, 0);
	}

	public String read(String fileName, long size) {
		if (fileName == null) {
			return null;
		}
		return SDLCall.readGameData(_gamedataHandle, fileName, size);
	}

	public int getFileCount() {
		return SDLCall.getGameDataFileCount(_gamedataHandle);
	}

	public long getHandle() {
		return _gamedataHandle;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_gamedataHandle != 0 && !_closed) {
			SDLCall.freeGameData(_gamedataHandle);
			_gamedataHandle = 0;
			_closed = true;
		}
	}

}
