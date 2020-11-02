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
package loon.media;

import java.util.HashMap;

import loon.core.Assets;
import loon.utils.CollectionUtils;

public abstract class SoundBox {

	private HashMap<String, Sound> sounds = new HashMap<String, Sound>(
			CollectionUtils.INITIAL_CAPACITY);

	public void playSound(String path) {
		playSound(path, false);
	}

	public void playSound(String path, boolean loop) {
		Sound sound = sounds.get(path);
		if (sound == null) {
			sound = Assets.getSound(path);
			sounds.put(path, sound);
		} else {
			sound.stop();
		}
		sound.setLooping(loop);
		sound.play();
	}

	public void volume(String path, float volume) {
		Sound sound = sounds.get(path);
		if (sound != null) {
			sound.setVolume(volume);
		}
	}

	public void stopSound(String path) {
		Sound sound = sounds.get(path);
		if (sound != null) {
			sound.stop();
		}
	}

	public void stopSound() {
		for (Sound s : sounds.values()) {
			if (s != null) {
				s.stop();
			}
		}
	}

	public void release() {
		for (Sound s : sounds.values()) {
			if (s != null) {
				s.release();
			}
		}
		sounds.clear();
	}
}
