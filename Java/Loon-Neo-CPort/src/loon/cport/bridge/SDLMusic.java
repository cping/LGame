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

public final class SDLMusic implements LRelease {

	public final static SDLMusic createMusic(String path) {
		long handle = SDLCall.loadMUS(path);
		return new SDLMusic(handle);
	}

	private boolean _closed;

	private long _musicHandle;

	private SDLMusic(long handle) {
		_musicHandle = handle;
	}

	public void play(boolean loop) {
		SDLCall.playMusic(_musicHandle, loop);
	}

	public void fadeInMusic(boolean loop) {
		SDLCall.playFadeInMusic(_musicHandle, loop);
	}

	public void fadeStop() {
		SDLCall.playMusicFadeStop();
	}

	public void setPosition(float v) {
		SDLCall.setPosition(v);
	}

	public void setVolume(float v) {
		SDLCall.setMusicVolume(v);
	}

	public float getVolume() {
		return SDLCall.getMusicVolume();
	}

	public void pause() {
		SDLCall.pauseMusic();
	}

	public void resume() {
		SDLCall.resumeMusic();
	}

	public void stop() {
		SDLCall.haltMusic();
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (!_closed && _musicHandle != 0) {
			stop();
			SDLCall.disposeMusic(_musicHandle);
			_musicHandle = 0;
			_closed = true;
		}
	}

}
