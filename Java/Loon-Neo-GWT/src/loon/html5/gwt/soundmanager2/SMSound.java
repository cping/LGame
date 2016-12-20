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
package loon.html5.gwt.soundmanager2;

import com.google.gwt.core.client.JavaScriptObject;

public class SMSound {
	public interface SMSoundCallback {
		public void onfinish();
	}

	public static final int STOPPED = 0;
	public static final int PLAYING = 1;

	private JavaScriptObject jsSound;

	protected SMSound(JavaScriptObject jsSound) {
		this.jsSound = jsSound;
	}

	public native final void destruct() /*-{
										this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.destruct();
										}-*/;

	public native final int getPosition() /*-{
											return this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.position;
											}-*/;

	public native final void setPosition(int position) /*-{
														this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.setPosition(position);
														}-*/;

	public native final void pause() /*-{
										this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.pause();
										}-*/;

	public native final void play(SMSoundOptions options) /*-{
															this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.play(
															{
															volume: options.@loon.html5.gwt.soundmanager2.SMSoundOptions::volume,
															pan: options.@loon.html5.gwt.soundmanager2.SMSoundOptions::pan,
															loops: options.@loon.html5.gwt.soundmanager2.SMSoundOptions::loops,
															from: options.@loon.html5.gwt.soundmanager2.SMSoundOptions::from,
															onfinish: function() {
															var callback = options.@loon.html5.gwt.soundmanager2.SMSoundOptions::callback;
															if(callback != null) {
															callback.@loon.html5.gwt.soundmanager2.SMSound.SMSoundCallback::onfinish()();
															}
															}
															}
															);
															}-*/;

	public native final void play() /*-{
									this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.play();
									}-*/;

	public native final void resume() /*-{
										this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.resume();
										}-*/;

	public native final void stop() /*-{
									this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.stop();
									}-*/;

	public native final void setVolume(int volume) /*-{
													this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.setVolume(volume);
													}-*/;

	public native final int getVolume() /*-{
										return this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.volume;
										}-*/;

	public native final void setPan(int pan) /*-{
												this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.setPan(pan);
												}-*/;

	public native final int getPan() /*-{
										return this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.pan;
										}-*/;

	public native final int getPlayState() /*-{
											return this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.playState;
											}-*/;

	public native final boolean getPaused() /*-{
											return this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.paused;
											}-*/;

	public native final int getLoops() /*-{
										return this.@loon.html5.gwt.soundmanager2.SMSound::jsSound.loops;
										}-*/;
}
