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

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.SoundPool;

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

	interface Resolver<I> {
		void resolve(AndroidSound<I> sound);
	}

	private final HashSet<AndroidSound<?>> playing = new HashSet<AndroidSound<?>>();

	private final HashMap<Integer, PooledSound> loadingSounds = new HashMap<Integer, PooledSound>();
	private final SoundPool pool;

	final static boolean notSupport() {
		return (LSystem.isDevice("GT-S5830B") || LSystem.isDevice("GT-I9100"));
	}

	private class PooledSound extends SoundImpl<Integer> {
		public final int soundId;
		private int streamId;

		public PooledSound(int soundId) {
			this.soundId = soundId;
		}

		@Override
		public String toString() {
			return "pooled:" + soundId;
		}

		@Override
		protected boolean playingImpl() {
			return false;
		}

		@Override
		protected boolean playImpl() {
			if (notSupport()) {
				return false;
			}
			streamId = pool.play(soundId, volume, volume, 1, looping ? -1 : 0,
					1);
			return (streamId != 0);
		}

		@Override
		protected void stopImpl() {
			if (notSupport()) {
				return;
			}
			if (streamId != 0) {
				pool.stop(streamId);
				streamId = 0;
			}
		}

		@Override
		protected void setLoopingImpl(boolean looping) {
			if (notSupport()) {
				return;
			}
			if (streamId != 0) {
				pool.setLoop(streamId, looping ? -1 : 0);
			}
		}

		@Override
		protected void setVolumeImpl(float volume) {
			if (notSupport()) {
				return;
			}
			if (streamId != 0) {
				pool.setVolume(streamId, volume, volume);
			}
		}

		@Override
		protected void releaseImpl() {
			if (notSupport()) {
				return;
			}
			pool.unload(soundId);
		}
	};

	public Audio() {
		this.pool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
	}

	private void loading(int soundId) {
		PooledSound sound = loadingSounds.get(soundId);
		if (sound != null) {
			dispatchLoaded(sound, soundId);
		} else {
			dispatchLoadError(sound, new Exception("Sound load failed [id="
					+ soundId + "]"));
		}
	}

	public SoundImpl<?> createSound(AssetFileDescriptor fd) {
		PooledSound sound = new PooledSound(pool.load(fd, 1));
		loadingSounds.put(sound.soundId, sound);
		loading(sound.soundId);
		return sound;
	}

	public SoundImpl<?> createSound(FileDescriptor fd, long offset, long length) {
		PooledSound sound = new PooledSound(pool.load(fd, offset, length, 1));
		loadingSounds.put(sound.soundId, sound);
		loading(sound.soundId);
		return sound;
	}

	private static AssetFileDescriptor openFd(String fileName)
			throws IOException {
		return LSystem.getActivity().getAssets().openFd(fileName);
	}

	public SoundImpl<?> createSound(final String path) {
		try {
			return createSound(openFd(path));
		} catch (IOException ioe) {
			PooledSound sound = new PooledSound(0);
			sound.onLoadError(ioe);
			return sound;
		}
	}

	public SoundImpl<?> createMusic(final String path) {
		return new BigClip(this, new Resolver<MediaPlayer>() {
			@Override
			public void resolve(final AndroidSound<MediaPlayer> sound) {
				final MediaPlayer mp = new MediaPlayer();
				LSystem.callScreenRunnable(new Runnable() {
					@Override
					public void run() {
						try {
							AssetFileDescriptor fd = openFd(path);
							mp.setDataSource(fd.getFileDescriptor(),
									fd.getStartOffset(), fd.getLength());
							fd.close();
							mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
								@Override
								public void onPrepared(final MediaPlayer mp) {
									dispatchLoaded(sound, mp);
								}
							});
							mp.setOnErrorListener(new OnErrorListener() {

								@Override
								public boolean onError(MediaPlayer mp,
										int what, int extra) {
									String errmsg = "MediaPlayer prepare failure [what="
											+ what + ", x=" + extra + "]";
									dispatchLoadError(sound, new Exception(
											errmsg));
									return false;
								}
							});
							mp.prepareAsync();
						} catch (Exception e) {
							dispatchLoadError(sound, e);
						}
					}
				});
			}
		});
	}

	public void onPause() {
		for (PooledSound p : loadingSounds.values()) {
			pool.pause(p.soundId);
		}
		for (AndroidSound<?> sound : playing) {
			sound.onPause();
		}
	}

	public void onResume() {
		for (PooledSound p : loadingSounds.values()) {
			pool.resume(p.soundId);
		}
		HashSet<AndroidSound<?>> wasPlaying = new HashSet<AndroidSound<?>>(
				playing);
		playing.clear();
		for (AndroidSound<?> sound : wasPlaying) {
			sound.onResume();
		}
	}

	public void onDestroy() {
		for (AndroidSound<?> sound : playing) {
			sound.release();
		}
		playing.clear();
		pool.release();
	}

	void onPlaying(AndroidSound<?> sound) {
		playing.add(sound);
	}

	void onStopped(AndroidSound<?> sound) {
		playing.remove(sound);
	}

}
