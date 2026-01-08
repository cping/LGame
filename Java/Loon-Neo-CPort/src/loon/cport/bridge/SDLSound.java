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
package loon.cport.bridge;

import java.util.Iterator;

import loon.LRelease;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entries;
import loon.utils.OrderedMap;

public final class SDLSound implements LRelease {

	public final static SDLSound createSound(byte[] bytes) {
		long handle = SDLCall.loadSound(bytes);
		return new SDLSound(handle);
	}

	public final static SDLSound createSound(String path) {
		long handle = SDLCall.loadSound(path);
		return new SDLSound(handle);
	}

	private final OrderedMap<Integer, Boolean> _channels;

	private int _lastChannel = -1;

	private boolean _closed;

	private long _soundHandle;

	private SDLSound(long handle) {
		_soundHandle = handle;
		_channels = new OrderedMap<Integer, Boolean>();
	}

	public int playSound(boolean looping) {
		int channel = SDLCall.playSound(_soundHandle, looping);
		_channels.put(channel, true);
		_lastChannel = channel;
		return channel;
	}

	public boolean isLoopingSound() {
		return isLoopingSound(_lastChannel);
	}

	public boolean isLoopingSound(int channel) {
		return SDLCall.isLoopingSound(channel);
	}

	public int getSoundVolume() {
		return getSoundVolume(_lastChannel);
	}

	public int getSoundVolume(int channel) {
		return SDLCall.getSoundVolume(channel);
	}

	public void setPosition(int angle, int distance) {
		setPosition(_lastChannel, angle, distance);
	}

	public void setPosition(int channel, int angle, int distance) {
		SDLCall.setPosition(channel, angle, distance);
	}

	public void fadeInSoundChannel(int ms) {
		fadeInSoundChannel(_lastChannel, ms);
	}

	public void fadeInSoundChannel(int channel, int ms) {
		SDLCall.fadeInSoundChannel(channel, ms);
	}

	public void fadeOutSoundChannel(int ms) {
		fadeOutSoundChannel(_lastChannel, ms);
	}

	public void fadeOutSoundChannel(int channel, int ms) {
		SDLCall.fadeOutSoundChannel(channel, ms);
	}

	public boolean isSoundPlaying() {
		return SDLCall.isSoundPlaying(_lastChannel);
	}

	public boolean isSoundPlaying(int channel) {
		return SDLCall.isSoundPlaying(channel);
	}

	public int setPlaySoundLooping(boolean looping) {
		return setPlaySoundLooping(_lastChannel, looping);
	}

	public int setPlaySoundLooping(int channel, boolean looping) {
		return SDLCall.setPlaySoundLooping(_soundHandle, channel, looping);
	}

	public void pauseLastSound() {
		pauseSound(_lastChannel);
	}

	public void pauseSound(int channel) {
		SDLCall.pauseSound(channel);
		_channels.put(channel, false);
	}

	public void pauseSoundAll() {
		for (Entries<Integer, Boolean> it = _channels.entries(); it.hasNext();) {
			ObjectMap.Entry<Integer, Boolean> v = it.next();
			if (v != null && v.getValue() != null && v.getValue().booleanValue()) {
				if (v.getKey() != null) {
					SDLCall.pauseSound(v.getKey().intValue());
					v.value = Boolean.FALSE;
				}
			}
		}
	}

	public void resumeLastSound() {
		resumeSound(_lastChannel);
	}

	public void resumeSound(int channel) {
		SDLCall.resumeSound(channel);
		_channels.put(channel, true);
	}

	public void resumeSoundAll() {
		for (Entries<Integer, Boolean> it = _channels.entries(); it.hasNext();) {
			ObjectMap.Entry<Integer, Boolean> v = it.next();
			if (v != null && v.getValue() != null && !v.getValue().booleanValue()) {
				if (v.getKey() != null) {
					SDLCall.pauseSound(v.getKey().intValue());
					v.value = Boolean.TRUE;
				}
			}
		}
	}

	public boolean isLastPaused() {
		return isPaused(_lastChannel);
	}

	public boolean isPaused(int channel) {
		Boolean result = _channels.get(_channels);
		if (result == null) {
			return false;
		}
		return result.booleanValue();
	}

	public void setLastVolume(float volume) {
		setVolume(_lastChannel, volume);
	}

	public void setVolume(int channel, float volume) {
		SDLCall.setVolume(channel, volume);
	}

	public void setLastPan(float pan) {
		setPan(_lastChannel, pan);
	}

	public void setPan(int channel, float pan) {
		SDLCall.setPan(channel, pan);
	}

	public void stop() {
		for (Iterator<Integer> it = _channels.keys(); it.hasNext();) {
			Integer c = it.next();
			if (c != null) {
				SDLCall.haltSound(c.intValue());
			}
		}
		_channels.clear();
		_lastChannel = -1;
	}

	public long getHandle() {
		return _soundHandle;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (!_closed && _soundHandle != 0) {
			stop();
			SDLCall.disposeSound(_soundHandle);
			_soundHandle = 0;
			_closed = true;
		}
	}

}
