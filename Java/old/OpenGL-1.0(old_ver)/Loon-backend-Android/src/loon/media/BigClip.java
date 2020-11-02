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

import android.media.MediaPlayer;

public class BigClip extends AndroidSound<MediaPlayer> {

	private final Audio audio;
	private final Audio.Resolver<MediaPlayer> resolver;
	private int position;

	public BigClip(Audio audio,
			Audio.Resolver<MediaPlayer> resolver) {
		this.audio = audio;
		this.resolver = resolver;
		resolve();
	}

	@Override
	public void onLoaded(MediaPlayer impl) {
		super.onLoaded(impl);
		impl.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				audio.onStopped(BigClip.this);
			}
		});
	}

	@Override
	protected boolean playingImpl() {
		return impl.isPlaying();
	}

	@Override
	protected boolean playImpl() {
		audio.onPlaying(this);
		impl.seekTo(position);
		impl.start();
		position = 0;
		return true;
	}

	@Override
	protected void stopImpl() {
		audio.onStopped(this);
		impl.pause();
	}

	@Override
	protected void setLoopingImpl(boolean looping) {
		impl.setLooping(looping);
	}

	@Override
	protected void setVolumeImpl(float volume) {
		impl.setVolume(volume, volume);
	}

	@Override
	protected void releaseImpl() {
		if (impl.isPlaying())
			impl.stop();
		impl.release();
	}

	private void resolve() {
		resolver.resolve(BigClip.this);
	}

	@Override
	void onPause() {
		if (impl != null) {
			if (impl.isPlaying()) {
				position = impl.getCurrentPosition();
			}
			impl.release();
			impl = null;
		}
	}

	@Override
	void onResume() {
		resolve();
	}
}
