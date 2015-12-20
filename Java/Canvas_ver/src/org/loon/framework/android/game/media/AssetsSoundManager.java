package org.loon.framework.android.game.media;

import java.util.ArrayList;

import org.loon.framework.android.game.utils.collection.ArrayMap;
import org.loon.framework.android.game.utils.collection.ArrayMap.Entry;

/**
 * 
 * Copyright 2008 - 2010
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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1.0
 */
public class AssetsSoundManager {

	private static AssetsSoundManager assetsSoundManager;

	private ArrayMap sounds = new ArrayMap(50);

	private int clipCount = 0;

	private boolean paused;

	private AssetsSound asound;

	final static public AssetsSoundManager getInstance() {
		if (assetsSoundManager == null) {
			return (assetsSoundManager = new AssetsSoundManager());
		}
		return assetsSoundManager;
	}

	private AssetsSoundManager() {
	}

	public synchronized void playSound(String name, int vol) {
		if (paused) {
			return;
		}
		if (sounds.containsKey(name)) {
			AssetsSound ass = ((AssetsSound) sounds.get(name));
			ass.setVolume(vol);
			ass.play();
		} else {
			if (clipCount > 50) {
				int idx = sounds.size() - 1;
				String k = (String) sounds.getKey(idx);
				AssetsSound clip = (AssetsSound) sounds.remove(k);
				clip.stop();
				clip = null;
				clipCount--;
			}
			asound = new AssetsSound(name);
			asound.play();
			asound.setVolume(vol);
			sounds.put(name, asound);
			clipCount++;
		}
	}

	public synchronized void stopSound(int index) {
		AssetsSound sound = (AssetsSound) sounds.get(index);
		if (sound != null) {
			sound.stop();
		}
	}

	public synchronized void playSound(String name, boolean loop) {
		if (paused) {
			return;
		}
		if (sounds.containsKey(name)) {
			AssetsSound ass = ((AssetsSound) sounds.get(name));
			if (loop) {
				ass.loop();
			} else {
				ass.play();
			}
		} else {
			if (clipCount > 50) {
				int idx = sounds.size() - 1;
				String k = (String) sounds.getKey(idx);
				AssetsSound clip = (AssetsSound) sounds.remove(k);
				clip.stop();
				clip = null;
				clipCount--;
			}
			asound = new AssetsSound(name);
			if (loop) {
				asound.loop();
			} else {
				asound.play();
			}
			sounds.put(name, asound);
			clipCount++;
		}
	}

	public synchronized void stopSoundAll() {
		if (sounds != null) {
			ArrayList<Entry> list = sounds.toList();
			for (int i = 0; i < list.size(); i++) {
				Entry sound = list.get(i);
				if (sound != null) {
					AssetsSound as = (AssetsSound) sound.getValue();
					if (as != null) {
						as.stop();
					}
				}
			}
		}
	}

	public synchronized void resetSound() {
		if (asound != null) {
			asound.reset();
		}
	}

	public synchronized void stopSound() {
		if (asound != null) {
			asound.stop();
		}
	}

	public synchronized void release() {
		if (asound != null) {
			asound.release();
		}
	}

	public synchronized void setSoundVolume(int vol) {
		if (asound != null) {
			asound.setVolume(vol);
		}
	}

	public synchronized void pause(boolean pause) {
		paused = pause;
	}

}
