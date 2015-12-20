package org.loon.framework.android.game.media;

import java.util.HashMap;

import android.content.Context;

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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1.1
 */
public class PlaySoundManager {

	private final Context activity;

	private final int numStreams;

	private float soundValue = 1F;

	private android.media.SoundPool soundPool;

	private HashMap<Integer, PlaySound> soundPoolMap;

	public PlaySoundManager(Context context) {
		this(context, 4);
	}

	public PlaySoundManager(Context context, int s) {
		this.activity = context;
		this.numStreams = s;
		this.soundPoolMap = new HashMap<Integer, PlaySound>();
		this.soundPool = null;
	}

	public PlaySound addPlaySound(int resId) {
		return addPlaySound(resId, 1f);
	}

	public PlaySound addPlaySound(final int resId, final float vol) {
		PlaySound play = soundPoolMap.get(resId);
		if (play == null) {
			play = new PlaySound(this, resId, vol);
		} else {
			play.setVol(vol);
		}
		synchronized (this) {
			if (!soundPoolMap.containsKey(resId)) {
				soundPoolMap.put(resId, play);
				initSoundPool();
				int id = soundPool.load(activity, play.getResourceId(), 1);
				play.setSoundId(id);
			}
		}
		return play;
	}

	private void initSoundPool() {
		if (soundPool == null) {
			soundPool = new android.media.SoundPool(numStreams,
					android.media.AudioManager.STREAM_MUSIC, 100);
		}
	}

	public float getValue() {
		return soundValue;
	}

	public void setValue(float v) {
		soundValue = v;
	}

	/**
	 * 播放指定的音频文件
	 * 
	 * @param play
	 */
	public void play(PlaySound play) {
		play(play, 1F, false);
	}

	/**
	 * 以指定音量播放指定的音频文件，并决定是否循环
	 * 
	 * @param play
	 * @param v
	 * @param loop
	 */
	public void play(PlaySound play, float v, boolean loop) {
		synchronized (this) {
			initSoundPool();
			float vol = soundValue * v;
			if (vol <= 0f) {
				return;
			}
			if (vol > 1f) {
				vol = 1f;
			}
			int stream = soundPool.play(play.getSoundId(), vol, vol, 1,
					loop ? -1 : 0, 1f);
			play.setStreamId(stream);
		}
	}

	/**
	 * 停止指定的音频文件
	 * 
	 * @param play
	 */
	public void stop(PlaySound play) {
		synchronized (this) {
			if (soundPool == null) {
				return;
			}
			int id = play.getStreamId();
			if (id != 0) {
				soundPool.stop(id);
				play.setStreamId(0);
			}
		}
	}

	/**
	 * 停止全部的音频播放
	 */
	public void stopSoundAll() {
		synchronized (this) {
			for (PlaySound e : soundPoolMap.values()) {
				stop(e);
			}
		}
	}

	/**
	 * 释放全部音频资源
	 */
	public void releaseAll() {
		for (PlaySound e : soundPoolMap.values()) {
			this.soundPool.unload(e.getResourceId());
		}
		this.soundPoolMap.clear();
	}

	/**
	 * 恢复音频播放
	 */
	public void resume() {
		synchronized (this) {
			if (soundPool == null) {
				soundPool = new android.media.SoundPool(numStreams,
						android.media.AudioManager.STREAM_MUSIC, 100);
				for (PlaySound e : soundPoolMap.values()) {
					int id = soundPool.load(activity, e.getResourceId(), 1);
					e.setSoundId(id);
				}
			}
		}
	}

	/**
	 * 暂停音频播放
	 */
	public void pause() {
		synchronized (this) {
			if (soundPool != null) {
				for (PlaySound e : soundPoolMap.values()) {
					stop(e);
					e.setSoundId(-1);
				}
				soundPool.release();
				soundPool = null;
			}
		}
	}

	/**
	 * 从缓存中取得制定资源索引对应的音频文件
	 * 
	 * @param resId
	 * @return
	 */
	public PlaySound getCacheSound(final int resId) {
		return soundPoolMap.get(resId);
	}

}
