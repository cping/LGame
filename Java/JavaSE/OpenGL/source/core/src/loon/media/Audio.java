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

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import loon.core.LSystem;
import loon.core.event.Updateable;

public class Audio {

	protected static <I> void dispatchLoaded(final SoundImpl<I> sound,
			final I impl) {
		Updateable update = new Updateable() {
			@Override
			public void action() {
				sound.onLoaded(impl);
			}
		};
		LSystem.unload(update);
	}

	protected static <I> void dispatchLoadError(final SoundImpl<I> sound,
			final Throwable error) {
		Updateable update = new Updateable() {
			@Override
			public void action() {
				sound.onLoadError(error);
			}
		};
		LSystem.unload(update);
	}

	public JavaSound createSound(final InputStream in, final boolean music) {
		final JavaSound sound = new JavaSound();
		LSystem.callScreenRunnable(new Runnable() {
			public void run() {
				try {
					AudioInputStream ais = AudioSystem.getAudioInputStream(in);
					Clip clip = AudioSystem.getClip();
					if (music) {
						clip = new BigClip(clip);
					}
					AudioFormat baseFormat = ais.getFormat();
					if (baseFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
						AudioFormat decodedFormat = new AudioFormat(
								AudioFormat.Encoding.PCM_SIGNED, baseFormat
										.getSampleRate(), 16, baseFormat
										.getChannels(), baseFormat
										.getChannels() * 2, baseFormat
										.getSampleRate(), false);
						ais = AudioSystem.getAudioInputStream(decodedFormat,
								ais);
					}
					clip.open(ais);
					dispatchLoaded(sound, clip);
				} catch (Exception e) {
					dispatchLoadError(sound, e);
				}
			}
		});
		return sound;
	}

	public void onPause() {

	}

	public void onResume() {

	}

	public void onDestroy() {

	}

}
