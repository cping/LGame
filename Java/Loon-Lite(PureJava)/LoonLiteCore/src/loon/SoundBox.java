/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon;

import loon.utils.CollectionUtils;
import loon.utils.ObjectMap;

public abstract class SoundBox extends BaseIO {

	private ObjectMap<String, Sound> SOUND_CACHE = new ObjectMap<>(
			CollectionUtils.INITIAL_CAPACITY);

	public void playSound(String path) {
		playSound(path, false);
	}

	public void playSound(String path, boolean loop) {
		Sound sound = SOUND_CACHE.get(path);
		if (sound == null) {
			sound = LSystem.base().assets().getSound(path);
			SOUND_CACHE.put(path, sound);
		} else {
			sound.stop();
		}
		sound.setLooping(loop);
		sound.play();
	}

	public void volume(String path, float volume) {
		Sound sound = SOUND_CACHE.get(path);
		if (sound != null) {
			sound.setVolume(volume);
		}
	}

	public void stopSound(String path) {
		Sound sound = SOUND_CACHE.get(path);
		if (sound != null) {
			sound.stop();
		}
	}

	public void stopSound() {
		for (Sound s : SOUND_CACHE.values()) {
			if (s != null) {
				s.stop();
			}
		}
	}

	public void release() {
		for (Sound s : SOUND_CACHE.values()) {
			if (s != null) {
				s.release();
			}
		}
		SOUND_CACHE.clear();
	}
}
