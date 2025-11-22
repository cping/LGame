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
import loon.teavm.dom.ConvertUtils;

public class HowlMusic extends SoundImpl<Object> {

	private Howl howl;

	public HowlMusic(TeaResourceLoader f) {
		byte[] bytes = f.readBytes();
		ArrayBufferView data = ConvertUtils.getInt8Array(bytes);
		howl = Howl.create(data);
	}

	public void pause() {
		howl.pause();
	}

	public boolean isLooping() {
		return howl.getLoop();
	}

	public float getVolume() {
		return howl.getVolume();
	}

	public void setPan(float pan, float volume) {
		howl.setStereo(pan);
		howl.setVolume(volume);
	}

	public void setPosition(float position) {
		howl.setSeek(position);
	}

	public float getPosition() {
		return howl.getSeek();
	}

	@Override
	protected boolean playImpl() {
		if (!isPlaying()) {
			howl.play();
		}
		return isPlaying();
	}

	@Override
	protected void stopImpl() {
		howl.stop();
	}

	@Override
	protected void setLoopingImpl(boolean looping) {
		howl.setLoop(looping);
	}

	@Override
	protected void setVolumeImpl(float volume) {
		howl.setVolume(volume);
	}

	@Override
	protected void releaseImpl() {
		howl.stop();
		howl.unload();
		howl = null;
	}

}