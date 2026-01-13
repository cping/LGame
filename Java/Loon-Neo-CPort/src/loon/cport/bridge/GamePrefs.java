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
import loon.LSystem;
import loon.utils.StringUtils;

public final class GamePrefs implements LRelease {

	public final static GamePrefs create() {
		long handle = SDLCall.createGamePrefs();
		return new GamePrefs(handle);
	}

	private boolean _closed;

	private long _prefsHandle;

	private GamePrefs(long handle) {
		_prefsHandle = handle;
	}

	public boolean load(String fileName) {
		return SDLCall.loadGamePrefs(_prefsHandle, fileName);
	}

	public void set(String section, String key, byte[] value, int value_len) {
		if (value == null) {
			return;
		}
		SDLCall.setGamePrefs(_prefsHandle, section, key, value, value_len);
	}

	public void set(String section, String key, String text) {
		if (text == null) {
			return;
		}
		byte[] bytes = null;
		try {
			bytes = text.getBytes(LSystem.ENCODING);
		} catch (Exception e) {
			bytes = text.getBytes();
		}
		set(section, key, bytes, bytes.length);
	}

	public byte[] getBytes(String section, String key) {
		byte[] buffer = new byte[1024 * 10];
		int length = (int) SDLCall.getGamePrefs(_prefsHandle, section, key, buffer);
		final byte[] newData = new byte[length];
		System.arraycopy(buffer, 0, newData, 0, length);
		buffer = null;
		return newData;
	}

	public String getString(String section, String key) {
		final byte[] buffer = getBytes(section, key);
		try {
			return new String(buffer, LSystem.ENCODING);
		} catch (Exception e) {
			return new String(buffer);
		}
	}

	public String[] getKeys(String section, String delimiter) {
		String result = SDLCall.getGamePrefsKeys(_prefsHandle, section, delimiter);
		if (result == null || result.length() == 0) {
			return new String[] { "" };
		}
		return StringUtils.split(result, delimiter);
	}

	public byte[] get(String section, String key) {
		byte[] buffer = new byte[1024 * 10];
		int length = (int) SDLCall.getGamePrefs(_prefsHandle, section, key, buffer);
		final byte[] newData = new byte[length];
		System.arraycopy(buffer, 0, newData, 0, length);
		buffer = null;
		return newData;
	}

	public void remove(String section, String key) {
		SDLCall.removeGamePrefs(_prefsHandle, section, key);
	}

	public boolean save(String fileName) {
		return SDLCall.saveGamePrefs(_prefsHandle, fileName);
	}

	public long getHandle() {
		return _prefsHandle;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_prefsHandle != 0 && !_closed) {
			SDLCall.freeGamePrefs(_prefsHandle);
			_prefsHandle = 0;
			_closed = true;
		}
	}
}