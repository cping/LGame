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
package loon.cport;

import loon.SoundImpl;
import loon.cport.bridge.SDLSound;

public class CSound extends SoundImpl<Object> {

	private SDLSound _sound;

	public CSound(byte[] bytes) {
		_sound = SDLSound.createSound(bytes);
	}

	public CSound(String path) {
		_sound = SDLSound.createSound(path);
	}

	public int playDefault() {
		return _sound.playSound(looping);
	}

	public int play(float volume) {
		int soundId = _sound.playSound(looping);
		_sound.setVolume(soundId, volume);
		return soundId;
	}

	public int play(float volume, float pitch, float pan) {
		int soundId = _sound.playSound(looping);
		_sound.setVolume(soundId, volume);
		_sound.setPan(soundId, pan);
		return soundId;
	}

	public int loop() {
		looping = true;
		int soundId = _sound.playSound(looping);
		_sound.setPlaySoundLooping(soundId, looping);
		return soundId;
	}

	public int loop(float volume) {
		looping = true;
		int soundId = _sound.playSound(looping);
		_sound.setPlaySoundLooping(soundId, looping);
		_sound.setVolume(soundId, volume);
		return soundId;
	}

	public long loop(float volume, float pitch, float pan) {
		looping = true;
		int soundId = _sound.playSound(looping);
		_sound.setPlaySoundLooping(soundId, looping);
		_sound.setVolume(soundId, volume);
		_sound.setPan(soundId, pan);
		return soundId;
	}

	public void pause() {
		_sound.pauseLastSound();
	}

	public void resume() {
		_sound.resumeLastSound();
	}

	public SDLSound getSDLSound() {
		return _sound;
	}

	@Override
	protected boolean playImpl() {
		playDefault();
		return isPlaying();
	}

	@Override
	protected void stopImpl() {
		_sound.stop();
	}

	@Override
	protected void setLoopingImpl(boolean l) {
		looping = l;
	}

	@Override
	protected void setVolumeImpl(float volume) {
		int soundId = _sound.playSound(looping);
		_sound.setVolume(soundId, volume);
	}

	@Override
	protected void releaseImpl() {
		_sound.close();
	}

}
