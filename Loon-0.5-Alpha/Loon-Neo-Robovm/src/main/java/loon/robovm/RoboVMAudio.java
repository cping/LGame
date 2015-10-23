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
package loon.robovm;

import java.io.File;

import loon.Sound;
import loon.SoundImpl;
import loon.jni.CAFLoader;

import org.robovm.apple.avfoundation.AVAudioPlayer;
import org.robovm.apple.avfoundation.AVAudioSession;
import org.robovm.apple.avfoundation.AVAudioSessionSetActiveOptions;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSURL;

import static loon.jni.OpenAL.*;

public class RoboVMAudio {

	protected <I> void dispatchLoaded(final SoundImpl<I> sound, final I impl) {
		game.invokeLater(new Runnable() {
			public void run() {
				sound.onLoaded(impl);
			}
		});
	}

	protected void dispatchLoadError(final SoundImpl<?> sound,
			final Throwable error) {
		game.invokeLater(new Runnable() {
			public void run() {
				sound.onLoadError(error);
			}
		});
	}

	private final RoboVMGame game;
	private final AVAudioSession session;
	private final long oalDevice;
	private final long oalContext;

	private final int[] sources;
	private final RoboVMSoundOAL[] active;
	private final int[] started;

	public RoboVMAudio(RoboVMGame game, int numSources) {

		this.game = game;

		session = AVAudioSession.getSharedInstance();
		try {
			session.setActive(true, AVAudioSessionSetActiveOptions.None);
		} catch (NSErrorException nse) {
			game.log().warn("Unable to initialize audio session", nse);
		}

		oalDevice = alcOpenDevice(null);
		if (oalDevice != 0) {
			oalContext = alcCreateContext(oalDevice, null);
			alcMakeContextCurrent(oalContext);
		} else {
			game.log().warn(
					"Unable to open OpenAL device. Disabling OAL sound.");
			oalContext = 0;
		}
		sources = new int[numSources];
		alGenSources(numSources, sources);
		active = new RoboVMSoundOAL[sources.length];
		started = new int[sources.length];
	}

	public Sound createSound(File path, boolean isMusic) {
		return (isMusic || !path.getName().endsWith(".caf")) ? createAVAP(new NSURL(
				path)) : createOAL(path);
	}

	Sound createAVAP(final NSURL url) {
		final RoboVMSoundAVAP sound = new RoboVMSoundAVAP();
		game.invokeAsync(new Runnable() {
			public void run() {
				try {
					AVAudioPlayer player = new AVAudioPlayer(url);
					dispatchLoaded(sound, player);
				} catch (NSErrorException e) {
					game.log().warn(
							"Error loading sound [" + url + "]: " + e);
					dispatchLoadError(sound, e);
				}
			}
		});
		return sound;
	}

	Sound createOAL(final File assetPath) {
		final RoboVMSoundOAL sound = new RoboVMSoundOAL(this);
		game.invokeAsync(new Runnable() {
			public void run() {
				int bufferId = 0;
				try {
					bufferId = alGenBuffer();
					CAFLoader.load(assetPath, bufferId);
					dispatchLoaded(sound, bufferId);
				} catch (Throwable t) {
					if (bufferId != 0)
						alDeleteBuffer(bufferId);
					dispatchLoadError(sound, t);
				}
			}
		});
		return sound;
	}

	boolean isPlaying(int sourceIdx, RoboVMSoundOAL sound) {
		if (active[sourceIdx] != sound)
			return false;
		int[] result = new int[1];
		alGetSourcei(sources[sourceIdx], AL_SOURCE_STATE, result);
		return (result[0] == AL_PLAYING);
	}

	int play(RoboVMSoundOAL sound, float volume, boolean looping) {
		int sourceIdx = -1, eldestIdx = 0;
		for (int ii = 0; ii < sources.length; ii++) {
			if (!isPlaying(ii, active[ii])) {
				sourceIdx = ii;
				break;
			} else if (started[ii] < started[eldestIdx]) {
				eldestIdx = ii;
			}
		}
		if (sourceIdx < 0) {
			stop(eldestIdx, active[eldestIdx]);
			sourceIdx = eldestIdx;
		}
		int sourceId = sources[sourceIdx];
		alSourcei(sourceId, AL_BUFFER, sound.bufferId());
		alSourcef(sourceId, AL_GAIN, volume);
		alSourcei(sourceId, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
		alSourcePlay(sourceId);
		active[sourceIdx] = sound;
		started[sourceIdx] = game.tick();
		return sourceIdx;
	}

	void stop(int sourceIdx, RoboVMSoundOAL sound) {
		if (active[sourceIdx] == sound) {
			alSourceStop(sources[sourceIdx]);
		}
	}

	void delete(RoboVMSoundOAL sound) {
		alDeleteBuffer(sound.bufferId());
	}

	void setLooping(int sourceIdx, RoboVMSoundOAL sound, boolean looping) {
		if (active[sourceIdx] == sound) {
			alSourcei(sources[sourceIdx], AL_LOOPING, looping ? AL_TRUE
					: AL_FALSE);
		}
	}

	void setVolume(int sourceIdx, RoboVMSoundOAL sound, float volume) {
		if (active[sourceIdx] == sound) {
			alSourcef(sources[sourceIdx], AL_GAIN, volume);
		}
	}

	void terminate() {

	}
}
