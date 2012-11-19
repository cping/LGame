package org.loon.framework.javase.game.media;

import java.util.HashMap;

import org.loon.framework.javase.game.utils.FileUtils;

/**
 * Copyright 2008 - 2009
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
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public abstract class SoundBox {

	final static private int MAX_CONCURRENT_SOUNDS = 5;

	private Sound[] players;

	private int currentPlayer = 0;

	public static final int SOUNDTYPE_MIDI = 1;

	public static final int SOUNDTYPE_WAV = 2;

	public static final int SOUNDTYPE_OGG = 3;

	public static final int SOUNDTYPE_AU = 4;

	public static final int SOUNDTYPE_AIFF = 5;

	public static final int SOUNDTYPE_RMF = 6;

	public static final int SOUNDTYPE_AAC = 7;

	public static final int SOUNDTYPE_MOD = 8;

	final static private HashMap<String, Integer> supportedFiles = new HashMap<String, Integer>(
			8);

	static {
		supportedFiles.put("mid", new Integer(SOUNDTYPE_MIDI));
		supportedFiles.put("ogg", new Integer(SOUNDTYPE_OGG));
		supportedFiles.put("wav", new Integer(SOUNDTYPE_WAV));
		supportedFiles.put("au", new Integer(SOUNDTYPE_AU));
		supportedFiles.put("aiff", new Integer(SOUNDTYPE_AIFF));
		supportedFiles.put("aac", new Integer(SOUNDTYPE_AAC));
		supportedFiles.put("rmf", new Integer(SOUNDTYPE_RMF));
		supportedFiles.put("mod", new Integer(SOUNDTYPE_MOD));
	}

	public static boolean isSupportedFile(String fileName) {
		return getSupportedType(fileName) > -1 ? true : false;
	}

	public static int getSupportedType(String fileName) {
		String suffix = FileUtils.getExtension(fileName).toLowerCase();
		Integer type = (Integer) supportedFiles.get(suffix);
		return type == null ? -1 : type.intValue();
	}

	public SoundBox() {
		players = new Sound[MAX_CONCURRENT_SOUNDS];
	}

	public synchronized void playSound(final String fileName) {
		if (players[currentPlayer] != null) {
			players[currentPlayer].stopSound();
		}
		Thread thread = new Thread(new Runnable() {
			public void run() {
				switch (getSupportedType(fileName)) {
				case SOUNDTYPE_MIDI:
					players[currentPlayer] = new LMidiSound();
					break;
				case SOUNDTYPE_WAV:
					players[currentPlayer] = new LWaveSound();
					break;
				case SOUNDTYPE_OGG:
					players[currentPlayer] = new LOggSound();
					break;
				case SOUNDTYPE_MOD:
					players[currentPlayer] = new LModSound();
					break;
				default:
					players[currentPlayer] = new OtherSound();
					break;
				}
				if (players[currentPlayer] != null) {
					players[currentPlayer].playSound(fileName);
				}
			}
		});
		thread.start();
		currentPlayer = (currentPlayer + 1) % players.length;
	}

	public synchronized void stopSound() {
		if (this.players == null) {
			return;
		}
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				players[i].stopSound();
				players[i] = null;
			}
		}
	}

	public synchronized void stopSound(int index) {
		if (this.players == null) {
			return;
		}
		if (players[index] != null) {
			players[index].stopSound();
			players[index] = null;
		}
	}

	public synchronized void setSoundVolume(int volume) {
		if (this.players == null) {
			return;
		}
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				players[i].setSoundVolume(volume);
				players[i] = null;
			}
		}
	}
}
