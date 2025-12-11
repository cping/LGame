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
package loon.teavm;

import loon.LSystem;
import loon.Sound;
import loon.SoundImpl;
import loon.events.Updateable;
import loon.teavm.assets.AssetData;
import loon.teavm.assets.AssetPreloader;
import loon.teavm.audio.HowlEmptySound;
import loon.teavm.audio.HowlMusic;
import loon.teavm.audio.HowlSound;
import loon.teavm.audio.HowlerAudioManager;
import loon.utils.PathUtils;

public class TeaAudio {

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

	private final HowlerAudioManager webAudio;

	public TeaAudio() {
		webAudio = new HowlerAudioManager();
	}

	public Sound newSound(String path) {
		TeaAudio audio = Loon.self.getAudio();
		if (audio != null) {
			final AssetPreloader assets = Loon.self.getPreloader();
			TeaResourceLoader gwtFile = assets.internal(path);
			if (gwtFile.exists()) {
				return audio.newSound(gwtFile);
			}
			String newPath = gwtFile.path();
			if ((newPath.indexOf('\\') != -1 || newPath.indexOf('/') != -1)) {
				gwtFile = assets.internal(newPath.substring(newPath.indexOf('/') + 1, newPath.length()));
			}
			if (!gwtFile.exists() && (newPath.indexOf('\\') != -1 || newPath.indexOf('/') != -1)) {
				gwtFile = assets.internal(LSystem.getFileName(newPath = gwtFile.path()));
			}
			if (!gwtFile.exists()) {
				gwtFile = assets.internal(LSystem
						.getFileName(newPath = (PathUtils.normalizeCombinePaths(LSystem.getPathPrefix(), newPath))));
			}
			if (gwtFile.exists()) {
				return audio.newSound(gwtFile);
			}
		}
		return new HowlEmptySound();
	}

	public Sound newSound(TeaResourceLoader res) {
		if (res == null || !res.exists()) {
			return new HowlEmptySound();
		}
		String ext = PathUtils.getExtension(res.path()).toLowerCase();
		if ("mp3".equals(ext) || "mpeg".equals(ext) || "webm".equals(ext)) {
			return createMusic(res);
		}
		return createSound(res);
	}

	public Sound newSound(AssetData asset) {
		if (asset == null) {
			return new HowlEmptySound();
		}
		String ext = PathUtils.getExtension(asset.getPath()).toLowerCase();
		if ("mp3".equals(ext) || "mpeg".equals(ext) || "webm".equals(ext)) {
			return createMusic(asset);
		}
		return createSound(asset);
	}

	public Sound createSound(TeaResourceLoader res) {
		HowlSound sound = null;
		try {
			sound = webAudio.createSound(res);
			dispatchLoaded(sound, new Object());
		} catch (Exception e) {
			dispatchLoadError(sound, e);
		}
		return sound;
	}

	public Sound createSound(AssetData asset) {
		HowlSound sound = null;
		try {
			sound = webAudio.createSound(asset);
			dispatchLoaded(sound, new Object());
		} catch (Exception e) {
			dispatchLoadError(sound, e);
		}
		return sound;
	}

	public Sound createMusic(TeaResourceLoader res) {
		HowlMusic sound = null;
		try {
			sound = webAudio.createMusic(res);
			dispatchLoaded(sound, new Object());
		} catch (Exception e) {
			dispatchLoadError(sound, e);
		}
		return sound;
	}

	public Sound createMusic(AssetData asset) {
		HowlMusic sound = null;
		try {
			sound = webAudio.createMusic(asset);
			dispatchLoaded(sound, new Object());
		} catch (Exception e) {
			dispatchLoadError(sound, e);
		}
		return sound;
	}

}
