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
package loon.fx;

import java.net.URL;

import javafx.scene.media.AudioClip;
import loon.SoundImpl;

public class JavaFXAudio extends SoundImpl<Object> {

	private AudioClip audioclip;

	public JavaFXAudio(URL url) {
		audioclip = new AudioClip(url.toExternalForm());
	}

	public JavaFXAudio(String path) {
		audioclip = new AudioClip(path);
	}

	@Override
	public boolean pause() {
		if (audioclip != null) {
			audioclip.stop();
		}
		return true;
	}

	@Override
	protected boolean playImpl() {
		if (audioclip != null) {
			audioclip.play();
		}
		return true;
	}

	@Override
	protected void stopImpl() {
		if (audioclip != null) {
			audioclip.stop();
		}
	}

	@Override
	protected void setLoopingImpl(boolean looping) {
		if (audioclip != null) {
			if (looping) {
				audioclip.setCycleCount(Integer.MAX_VALUE);
			} else {
				audioclip.setCycleCount(1);
			}
		}
	}

	@Override
	protected void setVolumeImpl(float volume) {
		if (audioclip != null) {
			audioclip.setVolume(volume);
		}
	}

	@Override
	protected void releaseImpl() {
		if (audioclip != null) {
			audioclip.stop();
		}
	}

}
