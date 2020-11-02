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

import loon.LSystem;
import loon.core.event.Updateable;
import loon.utils.StringUtils;
import loon.utils.debugging.Log;

public class Audio {

	protected static <I> void dispatchLoaded(final SoundImpl<I> sound,
			final I impl) {
		Updateable update = new Updateable() {
			@Override
			public void action(Object a) {
				sound.onLoaded(impl);
			}
		};
		LSystem.unload(update);
	}

	protected static <I> void dispatchLoadError(final SoundImpl<I> sound,
			final Throwable error) {
		Updateable update = new Updateable() {
			@Override
			public void action(Object a) {
				sound.onLoadError(error);
			}
		};
		LSystem.unload(update);
	}

	interface Resolver<I> {
		void resolve(AndroidSound<I> sound);
	}

	private final HashSet<AndroidSound<?>> playing = new HashSet<AndroidSound<?>>();
	private final HashMap<String, OpenALSound> loadingOpenAlSounds = new HashMap<String, OpenALSound>();
	private final HashMap<Integer, PooledSound> loadingSounds = new HashMap<Integer, PooledSound>();
	private final SoundPool pool;

	final static boolean notSupport() {
		return (LSystem.isDevice("GT-S5830B") || LSystem.isDevice("GT-I9100"));
	}

	private class OpenALSound extends SoundImpl<String> {

		private SoundOpenAlEnv env;

		private String path;

		private SoundOpenAlSource source;

		private SoundOpenAlBuffer buffer;

		private boolean _complete, _loop;

		public OpenALSound(final String path) {
			this.path = path;
			this.env = SoundOpenAlEnv.getInstance();
			if (SoundOpenAlEnv.isSupportNative()) {
				Updateable loading = new Updateable() {

					@Override
					public void action(Object a) {
						try {
							OpenALSound.this.buffer = env.addBuffer(path);
							OpenALSound.this.source = env.addSource(buffer);
							_complete = true;
							dispatchLoaded(OpenALSound.this, path);
						} catch (IOException e) {
							_complete = false;
							dispatchLoadError(OpenALSound.this, e);
						}
					}
				};
				LSystem.load(loading);
			}
		}

		private boolean check() {
			return buffer != null && source != null && _complete;
		}

		@Override
		public String toString() {
			return path;
		}

		@Override
		protected boolean playingImpl() {
			return false;
		}

		@Override
		protected boolean playImpl() {
			if (check()) {
				source.play(_loop);
				return true;
			}
			return false;
		}

		protected boolean prepareImpl() {
			if (check()) {
				source.setPosition(0, 0, 0);
			}
			return true;
		}

		@Override
		protected void stopImpl() {
			if (check()) {
				source.stop();
			}
		}

		@Override
		protected void setLoopingImpl(boolean looping) {
			_loop = looping;
		}

		@Override
		protected void setVolumeImpl(float volume) {
			if (check()) {
				source.setPitch(volume);
				source.setGain(volume);
			}
		}

		@Override
		protected void releaseImpl() {
			if (check()) {
				source.stop();
				source.release();
				buffer.release();
			}
		}
	};

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

		protected boolean prepareImpl() {
			pool.play(soundId, 0, 0, 0, 0, 1);
			return true;
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
		// 以标准pool监听器监听数据
		this.pool
				.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
					public void onLoadComplete(SoundPool soundPool,
							int soundId, int status) {
						PooledSound sound = loadingSounds.remove(soundId);
						if (sound == null) {
							Log.exception("load _complete for unknown sound [id="
									+ soundId + "]");
						} else if (status == 0) {
							dispatchLoaded(sound, soundId);
						} else {
							dispatchLoadError(sound, new Exception(
									"Sound load failed [errcode=" + status
											+ "]"));
						}
					}
				});
	}

	public SoundImpl<?> createSound(AssetFileDescriptor fd) {
		PooledSound sound = new PooledSound(pool.load(fd, 1));
		loadingSounds.put(sound.soundId, sound);
		return sound;
	}

	public SoundImpl<?> createSound(FileDescriptor fd, long offset, long length) {
		PooledSound sound = new PooledSound(pool.load(fd, offset, length, 1));
		loadingSounds.put(sound.soundId, sound);
		return sound;
	}

	private static AssetFileDescriptor openFd(String fileName)
			throws IOException {
		if (LSystem.type == LSystem.ApplicationType.Android) {
			if (fileName.toLowerCase().startsWith("assets/")) {
				fileName = StringUtils.replaceIgnoreCase(fileName, "assets/",
						"");
			}
			if (fileName.startsWith("/") || fileName.startsWith("\\")) {
				fileName = fileName.substring(1, fileName.length());
			}
		}
		return LSystem.getActivity().getAssets().openFd(fileName);
	}

	public SoundImpl<?> createSound(final String path) {
		if ("wav".equalsIgnoreCase(LSystem.getExtension(path))
				&& SoundOpenAlEnv.isSupportNative()) {
			OpenALSound sound = new OpenALSound(path);
			loadingOpenAlSounds.put(sound.path, sound);
			return sound;
		}
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
				LSystem.load(new Updateable() {
					@Override
					public void action(Object o) {
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
		for (OpenALSound al : loadingOpenAlSounds.values()) {
			al.stop();
		}
	}

	public void onResume() {
		pool.autoResume();
		HashSet<AndroidSound<?>> wasPlaying = new HashSet<AndroidSound<?>>(
				playing);
		playing.clear();
		if (!wasPlaying.isEmpty()) {
			Log.exception("Resuming " + wasPlaying.size() + " playing sounds.");
		}
		for (AndroidSound<?> sound : wasPlaying) {
			sound.onResume();
		}
		for (OpenALSound al : loadingOpenAlSounds.values()) {
			al.play();
		}
	}

	public void onDestroy() {
		for (OpenALSound al : loadingOpenAlSounds.values()) {
			al.release();
		}
		for (AndroidSound<?> sound : playing) {
			sound.release();
		}
		loadingOpenAlSounds.clear();
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
