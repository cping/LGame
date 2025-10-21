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
package loon.javase;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import loon.LGame;
import loon.LSystem;
import loon.SoundImpl;
import loon.events.Updateable;

public final class JavaSEAudio {

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

	private final static class AudioUpdateable implements Updateable {

		final SoundImpl<Object> _sound;

		final InputStream _input;

		final boolean _music;

		AudioUpdateable(SoundImpl<Object> s, InputStream in, boolean m) {
			this._sound = s;
			this._input = in;
			this._music = m;
		}

		public void action(Object o) {
			try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(_input);
				Clip clip = AudioSystem.getClip();
				if (_music) {
					clip = new JavaSEBigClip(clip);
				}
				AudioFormat baseFormat = ais.getFormat();
				if (baseFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
					AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
							baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
							baseFormat.getSampleRate(), false);
					ais = AudioSystem.getAudioInputStream(decodedFormat, ais);
				}
				clip.open(ais);
				dispatchLoaded(_sound, clip);
			} catch (Exception e) {
				dispatchLoadError(_sound, e);
			}

		}

	}

	public static SoundImpl<Object> createSound(final LGame game, final String path, final InputStream in,
			final boolean music) {
		SoundImpl<Object> anySound = null;
		final String ext = LSystem.getExtension(path).trim().toLowerCase();
		if ("ogg".equals(ext)) {
			final JavaSEOggSound sound = new JavaSEOggSound(game);
			LSystem.load(new Updateable() {
				@Override
				public void action(Object o) {
					try {
						sound.loadOgg(in);
						dispatchLoaded(sound, new Object());
					} catch (IOException e) {
						dispatchLoadError(sound, e);
					}
				}
			});
			anySound = sound;
		} else if ("mp3".equals(ext)) {
			final JavaSEMP3Sound sound = new JavaSEMP3Sound(game);
			LSystem.load(new Updateable() {
				@Override
				public void action(Object o) {
					try {
						sound.loadMP3(in);
						dispatchLoaded(sound, new Object());
					} catch (IOException e) {
						dispatchLoadError(sound, e);
					}
				}
			});
			anySound = sound;
		} else {
			if (anySound == null) {
				anySound = new JavaSEOggSound(game);
			}
			LSystem.load(new AudioUpdateable(anySound, in, music));
		}
		return anySound;
	}

	public void onPause() {

	}

	public void onResume() {

	}

	public void onDestroy() {

	}

}
