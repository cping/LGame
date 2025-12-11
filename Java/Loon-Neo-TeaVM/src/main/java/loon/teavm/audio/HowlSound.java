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
package loon.teavm.audio;

import org.teavm.jso.typedarrays.ArrayBufferView;

import loon.SoundImpl;
import loon.teavm.TeaResourceLoader;
import loon.teavm.assets.AssetData;
import loon.teavm.dom.ConvertUtils;
import loon.utils.CollectionUtils;

public class HowlSound extends SoundImpl<Object> {

	private Howl howl;

	public HowlSound(TeaResourceLoader fileHandle) {
		byte[] bytes = fileHandle.readBytes();
		ArrayBufferView data = ConvertUtils.getInt8Array(bytes);
		howl = Howl.create(data);
	}

	public HowlSound(AssetData asset) {
		byte[] bytes = CollectionUtils.copyOf(asset.getBytes());
		ArrayBufferView data = ConvertUtils.getInt8Array(bytes);
		howl = Howl.create(data);
	}

	public long playDefault() {
		return howl.play();
	}

	public long play(float volume) {
		int soundId = howl.play();
		howl.setVolume(volume, soundId);
		return soundId;
	}

	public long play(float volume, float pitch, float pan) {
		int soundId = howl.play();
		howl.setVolume(volume, soundId);
		howl.setRate(pitch, soundId);
		howl.setStereo(pan, soundId);
		return soundId;
	}

	public long loop() {
		int soundId = howl.play();
		howl.setLoop(true, soundId);
		return soundId;
	}

	public long loop(float volume) {
		int soundId = howl.play();
		howl.setLoop(true, soundId);
		howl.setVolume(volume, soundId);
		return soundId;
	}

	public long loop(float volume, float pitch, float pan) {
		int soundId = howl.play();
		howl.setLoop(true, soundId);
		howl.setVolume(volume, soundId);
		howl.setStereo(volume, soundId);
		return soundId;
	}

	public void pause() {
		howl.pause();
	}

	public void resume() {
		howl.play();
	}

	public void dispose() {
		howl.stop();
		howl = null;
	}

	public void stop(long soundId) {
		howl.stop((int) soundId);
	}

	public void pause(long soundId) {
		howl.pause((int) soundId);
	}

	public void resume(long soundId) {
		howl.play((int) soundId);
	}

	public void setLooping(long soundId, boolean looping) {
		howl.setLoop(looping, (int) soundId);
	}

	public void setPitch(long soundId, float pitch) {
		howl.setRate(pitch, (int) soundId);
	}

	public void setVolume(long soundId, float volume) {
		howl.setVolume(volume, (int) soundId);
	}

	public void setPan(long soundId, float pan, float volume) {
		int soundIdd = (int) soundId;
		howl.setStereo(pan, soundIdd);
		howl.setVolume(volume, soundIdd);
	}

	@Override
	protected boolean playImpl() {
		playDefault();
		return isPlaying();
	}

	@Override
	protected void stopImpl() {
		howl.stop();
	}

	@Override
	protected void setLoopingImpl(boolean looping) {
		int soundId = howl.play();
		howl.setLoop(looping, soundId);
	}

	@Override
	protected void setVolumeImpl(float volume) {
		int soundId = howl.play();
		howl.setVolume(volume, soundId);
	}

	@Override
	protected void releaseImpl() {
		dispose();
	}
}