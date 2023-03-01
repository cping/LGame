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

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import loon.SoundImpl;

public class JavaFXMusic extends SoundImpl<Object> {

	private MediaPlayer mediaPlayer;
	
	public JavaFXMusic(URL url) {
		this.mediaPlayer = new MediaPlayer(new Media(url.toExternalForm()));
	}

	public JavaFXMusic(String path) {
		this.mediaPlayer = new MediaPlayer(new Media(path));
	}
	
	@Override
	protected boolean playImpl() {
		if (mediaPlayer != null) {
			mediaPlayer.play();
		}
		return true;
	}

	@Override
	protected void stopImpl() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}

	@Override
	public boolean pause() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
		return true;
	}

	@Override
	protected void setLoopingImpl(boolean looping) {
		if (mediaPlayer != null) {
			if (looping) {
				mediaPlayer.setCycleCount(Integer.MAX_VALUE);
			} else {
				mediaPlayer.setCycleCount(1);
			}
		}
	}

	@Override
	protected void setVolumeImpl(float volume) {
		if (mediaPlayer != null) {
			mediaPlayer.setVolume(volume);
		}
	}

	@Override
	protected void releaseImpl() {
		if (mediaPlayer != null) {
			mediaPlayer.dispose();
		}
	}

}
