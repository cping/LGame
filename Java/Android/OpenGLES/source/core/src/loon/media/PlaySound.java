package loon.media;

/**
 * 
 * Copyright 2008 - 2011
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
 * @email javachenpeng@yahoo.com
 * @version 0.1.1
 */
public class PlaySound {

	private final PlaySoundManager soundPlayer;

	private float playVol;

	private final int resId;

	private int playerSoundId;

	private int streamId;

	public PlaySound(PlaySoundManager player, int resId, float vol) {
		this.soundPlayer = player;
		this.resId = resId;
		this.playerSoundId = -1;
		this.playVol = vol;
	}

	final int getResourceId() {
		return resId;
	}

	final int getSoundId() {
		return playerSoundId;
	}

	final void setSoundId(int id) {
		playerSoundId = id;
	}

	final int getStreamId() {
		return streamId;
	}

	final void setStreamId(int id) {
		streamId = id;
	}

	final float getVol() {
		return playVol;
	}

	final void setVol(float vol) {
		playVol = vol;
	}

	public void play() {
		if (PlaySoundManager.notSupport()) {
			return;
		}
		soundPlayer.play(this, playVol, false);
	}

	public void play(float vol) {
		if (PlaySoundManager.notSupport()) {
			return;
		}
		soundPlayer.play(this, vol * playVol, false);
	}

	public void loop() {
		if (PlaySoundManager.notSupport()) {
			return;
		}
		soundPlayer.play(this, playVol, true);
	}

	public void play(float vol, boolean loop) {
		if (PlaySoundManager.notSupport()) {
			return;
		}
		soundPlayer.play(this, vol * playVol, loop);
	}

	public void stop() {
		if (PlaySoundManager.notSupport()) {
			return;
		}
		soundPlayer.stop(this);
	}

	public final boolean isPlaying() {
		return streamId != 0;
	}

}
