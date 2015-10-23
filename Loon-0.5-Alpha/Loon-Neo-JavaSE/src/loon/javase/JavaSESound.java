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

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import loon.SoundImpl;
import loon.utils.MathUtils;

class JavaSESound extends SoundImpl<Clip> {

	@Override
	protected boolean playingImpl() {
		return impl.isActive();
	}

	@Override
	protected boolean playImpl() {
		impl.setFramePosition(0);
		if (looping) {
			impl.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			impl.start();
		}
		return true;
	}

	@Override
	protected void stopImpl() {
		impl.stop();
		impl.flush();
	}

	@Override
	protected void setLoopingImpl(boolean looping) {
		this.looping = looping;
	}

	@Override
	protected void setVolumeImpl(float volume) {
		if (impl.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl volctrl = (FloatControl) impl
					.getControl(FloatControl.Type.MASTER_GAIN);
			volctrl.setValue(toGain(volume, volctrl.getMinimum(),
					volctrl.getMaximum()));
		}
	}

	@Override
	protected void releaseImpl() {
		impl.close();
	}

	protected static float toGain(float volume, float min, float max) {
		return MathUtils.clamp((float) (20 * Math.log10(volume)), min, max);
	}
}
