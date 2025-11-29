/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.html5.gwt;

import loon.LSystem;
import loon.Sound;
import loon.html5.gwt.soundmanager2.SMSound;
import loon.html5.gwt.soundmanager2.SMSound.SMSoundCallback;
import loon.html5.gwt.soundmanager2.SMSoundOptions;
import loon.html5.gwt.soundmanager2.SoundManager;
import loon.utils.PathUtils;
import loon.utils.reply.Callback;

public class GWTSound implements Sound, SMSoundCallback {

	private boolean isPlaying = false;
	private boolean isLooping = false;
	private SMSound sound;
	private float volume = 1f;
	private float pan = 0f;
	private SMSoundOptions soundOptions;

	public GWTSound(final String path) {
		final String startAssets = LSystem.getPathPrefix();
		String url = GWTResourcesLoader.fixSlashes(path);
		if (!url.startsWith(startAssets)) {
			url = PathUtils.normalizeCombinePaths(startAssets, url);
		}
		sound = SoundManager.createSound(url);
		soundOptions = new SMSoundOptions();
		soundOptions.callback = this;
	}

	@Override
	public boolean play() {
		if (isPlaying())
			return false;
		if (sound.getPaused()) {
			resume();
			return false;
		}
		soundOptions.volume = (int) (volume * 100);
		soundOptions.pan = (int) (pan * 100);
		soundOptions.loops = 1;
		soundOptions.from = 0;
		sound.play(soundOptions);
		isPlaying = true;
		return isPlaying;
	}

	public void resume() {
		sound.resume();
	}

	public void pause() {
		sound.pause();
		isPlaying = false;
	}

	@Override
	public void stop() {
		sound.stop();
		isPlaying = false;
	}

	@Override
	public boolean isPlaying() {
		isPlaying &= sound.getPlayState() == 1;
		return isPlaying;
	}

	@Override
	public void setLooping(boolean isLooping) {
		this.isLooping = isLooping;
	}

	public boolean isLooping() {
		return isLooping;
	}

	@Override
	public void setVolume(float volume) {
		sound.setVolume((int) (volume * 100));
		this.volume = volume;
	}

	public float getVolume() {
		return volume;
	}

	public void setPan(float pan, float volume) {
		sound.setPan((int) (pan * 100));
		sound.setVolume((int) (volume * 100));
		this.pan = pan;
		this.volume = volume;
	}

	public void setPosition(float position) {
		sound.setPosition((int) (position * 1000f));
	}

	public float getPosition() {
		return sound.getPosition() / 1000f;
	}

	public void dispose() {
		sound.destruct();
	}

	@Override
	public void onfinish() {
		if (isLooping) {
			play();
		}
	}

	@Override
	public boolean prepare() {
		return true;
	}

	@Override
	public float volume() {
		return volume;
	}

	@Override
	public void release() {
		this.dispose();
	}

	@Override
	public void addCallback(Callback<Sound> callback) {

	}
}
