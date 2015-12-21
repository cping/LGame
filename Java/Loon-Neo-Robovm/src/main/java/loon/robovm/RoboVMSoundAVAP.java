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

import loon.SoundImpl;

import org.robovm.apple.avfoundation.AVAudioPlayer;
import org.robovm.apple.avfoundation.AVAudioPlayerDelegateAdapter;

public class RoboVMSoundAVAP extends SoundImpl<AVAudioPlayer> {

	private AVAudioPlayerDelegateAdapter delegate = new AVAudioPlayerDelegateAdapter() {
		public void endInterruption(AVAudioPlayer player, long flags) {
			impl.setCurrentTime(0);
			impl.prepareToPlay();
			impl.play();
		}
	};

	@Override
	public void onLoaded(AVAudioPlayer impl) {
		super.onLoaded(impl);
		impl.setDelegate(delegate);
	}

	@Override
	protected boolean prepareImpl() {
		return impl.prepareToPlay();
	}

	@Override
	protected boolean playingImpl() {
		return impl.isPlaying();
	}

	@Override
	protected boolean playImpl() {
		impl.setCurrentTime(0);
		return impl.play();
	}

	@Override
	protected void stopImpl() {
		impl.stop();
		impl.setCurrentTime(0);
	}

	@Override
	protected void setLoopingImpl(boolean looping) {
		impl.setNumberOfLoops(looping ? -1 : 0);
	}

	@Override
	protected void setVolumeImpl(float volume) {
		impl.setVolume(volume);
	}

	@Override
	protected void releaseImpl() {
		impl.dispose();
	}
}