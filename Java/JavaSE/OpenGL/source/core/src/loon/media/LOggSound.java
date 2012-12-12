package loon.media;

import java.io.IOException;
import java.io.InputStream;

import loon.core.LSystem;
import loon.core.resource.Resources;


/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
public class LOggSound implements Sound {

	private int volume;

	private JoggStreamer player;

	public LOggSound() {
		setSoundVolume(Sound.defaultMaxVolume);
	}

	public void playSound(String fileName) {
		try {
			playSound(Resources.openResource(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playSound(InputStream in) {
		try {
			stopSound();
			player = new JoggStreamer(in);
			try {
				synchronized (player) {
					setSoundVolume(volume);
					player.start();
					player.wait(LSystem.SECOND);
				}
			} catch (InterruptedException e) {
				throw new IOException("interrupted: " + e);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSoundVolume(int volume) {
		this.volume = volume;
		if (this.player == null) {
			return;
		}
		player.updateVolume(volume);

	}

	public void stopSound() {
		if (this.player == null) {
			return;
		}
		player.interrupt();

	}

	public boolean isVolumeSupported() {
		return true;
	}

}
