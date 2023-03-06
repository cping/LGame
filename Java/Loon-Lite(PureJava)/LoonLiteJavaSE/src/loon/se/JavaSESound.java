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
package loon.se;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.jcraft.jogg.OggClip;

import loon.SoundImpl;
import loon.utils.MathUtils;

class JavaSESound extends SoundImpl<Object> {

	private int mode = 0;

	OggClip ogg_clip;

	public JavaSESound() {

	}

	synchronized void loadOgg(InputStream ins) throws IOException {
		ogg_clip = new OggClip(ins);
		mode = 1;
	}

	@Override
	protected synchronized boolean playingImpl() {
		switch (mode) {
		case 0:
			return (((Clip) impl)).isActive();
		case 1:
			return !ogg_clip.stopped();
		}
		return false;
	}

	@Override
	protected synchronized boolean playImpl() {
		switch (mode) {
		case 0:
			((Clip) impl).setFramePosition(0);
			if (looping) {
				((Clip) impl).loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				((Clip) impl).start();
			}
			break;
		case 1:
			if (ogg_clip.stopped()) {
				ogg_clip.setGain(volume);
				if (looping) {
					ogg_clip.loop();
				} else {
					ogg_clip.play();
				}
			}
			break;
		}
		return true;
	}

	@Override
	public boolean pause() {
		stopImpl();
		return true;
	}
	
	@Override
	protected synchronized void stopImpl() {
		switch (mode) {
		case 0:
			((Clip) impl).stop();
			((Clip) impl).flush();
			break;
		case 1:

			break;
		}
	}

	@Override
	protected synchronized void setLoopingImpl(boolean looping) {
		this.looping = looping;
	}

	@Override
	protected synchronized void setVolumeImpl(float volume) {
		switch (mode) {
		case 0:
			if (((Clip) impl).isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				FloatControl volctrl = (FloatControl) ((Clip) impl)
						.getControl(FloatControl.Type.MASTER_GAIN);
				volctrl.setValue(toGain(volume, volctrl.getMinimum(),
						volctrl.getMaximum()));
			}
			break;
		case 1:
			this.volume = volume;
			ogg_clip.setGain(volume);
			break;
		}
	}

	@Override
	protected synchronized void releaseImpl() {
		switch (mode) {
		case 0:
			((Clip) impl).close();
			break;
		case 1:
			ogg_clip.close();
			break;
		}
	}

	protected static float toGain(float volume, float min, float max) {
		return MathUtils.clamp((float) (20 * Math.log10(volume)), min, max);
	}

}
