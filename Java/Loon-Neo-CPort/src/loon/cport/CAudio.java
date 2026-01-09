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
package loon.cport;

import loon.LSystem;
import loon.Sound;
import loon.SoundImpl;
import loon.cport.bridge.SDLCall;
import loon.events.Updateable;
import loon.utils.PathUtils;

public class CAudio {

	protected static <I> void dispatchLoaded(final SoundImpl<I> sound, final I impl) {
		Updateable update = new Updateable() {
			@Override
			public void action(Object a) {
				sound.onLoaded(impl);
			}
		};
		LSystem.unload(update);
	}

	protected static <I> void dispatchLoadError(final SoundImpl<I> sound, final Throwable error) {
		Updateable update = new Updateable() {
			@Override
			public void action(Object a) {
				sound.onLoadError(error);
			}
		};
		LSystem.unload(update);
	}

	public Sound newSound(byte[] bytes) {
		if (bytes == null || bytes.length <= 1) {
			return new CEmptySound();
		}
		return createSound(bytes);
	}

	public Sound newSound(String path) {
		if (path == null || (!SDLCall.fileExists(path) && !SDLCall.rwFileExists(path))) {
			return new CEmptySound();
		}
		String ext = PathUtils.getExtension(path).toLowerCase();
		if ("mp3".equals(ext) || "mpeg".equals(ext) || "webm".equals(ext)) {
			return createMusic(path);
		}
		return createSound(path);
	}

	public final static Sound createMusic(byte[] bytes) {
		CMusic sound = null;
		try {
			sound = new CMusic(bytes);
			dispatchLoaded(sound, new Object());
		} catch (Exception e) {
			dispatchLoadError(sound, e);
		}
		return sound;
	}

	public final static Sound createMusic(String path) {
		CMusic sound = null;
		try {
			sound = new CMusic(path);
			dispatchLoaded(sound, new Object());
		} catch (Exception e) {
			dispatchLoadError(sound, e);
		}
		return sound;
	}

	public final static Sound createSound(byte[] bytes) {
		CSound sound = null;
		try {
			sound = new CSound(bytes);
			dispatchLoaded(sound, new Object());
		} catch (Exception e) {
			dispatchLoadError(sound, e);
		}
		return sound;
	}

	public final static Sound createSound(String path) {
		CSound sound = null;
		try {
			sound = new CSound(path);
			dispatchLoaded(sound, new Object());
		} catch (Exception e) {
			dispatchLoadError(sound, e);
		}
		return sound;
	}
}
