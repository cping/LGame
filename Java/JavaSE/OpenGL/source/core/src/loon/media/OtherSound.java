package loon.media;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import loon.core.resource.Resources;


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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
// 非特定解码器播放用类
public class OtherSound implements Sound {

	private SourceDataLine clip;

	private boolean isRunning;

	private float volume;

	public OtherSound() {
		setSoundVolume(Sound.defaultMaxVolume);
	}


	public void playSound(String fileName) {
		try {
			playSound(Resources.openResource(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playSound(InputStream is) {
		if (is == null) {
			return;
		}

		this.isRunning = true;

		AudioInputStream din = null;
		AudioInputStream in = null;

		try {

			in = AudioSystem.getAudioInputStream(is);

			if (in == null) {
				return;
			}
			AudioFormat baseFormat = in.getFormat();

			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);

			din = AudioSystem.getAudioInputStream(decodedFormat, in);

			rawplay(decodedFormat, din, volume);
		} catch (Exception e) {

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public void setSoundVolume(int volume) {
		this.volume = volume / 100F;
	}

	private void rawplay(AudioFormat trgFormat, AudioInputStream din,
			float volume) throws IOException, LineUnavailableException {
		if (volume <= 0f) {
			return;
		}
		if (volume >= 1.0f) {
			volume = 1.0f;
		}
		byte[] data = new byte[8192];
		try {
			clip = getLine(trgFormat);
			if (clip == null) {
				return;
			}
			clip.start();
			int nBytesRead = 0;
			while (isRunning && (nBytesRead != -1)) {
				nBytesRead = din.read(data, 0, data.length);
				for (int i = 0; i < nBytesRead; i++){
					data[i] *= volume;
				}
				if (nBytesRead != -1){
					clip.write(data, 0, nBytesRead);
				}
			}
		} finally {
			clip.drain();
			clip.stop();
			clip.close();
			din.close();
		}
	}

	private SourceDataLine getLine(AudioFormat audioFormat)
			throws LineUnavailableException {
		SourceDataLine res = null;

		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);

		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);

		return res;
	}

	public void stopSound() {
		if (clip != null) {
			clip.stop();
		}
		isRunning = false;
	}

	public boolean isVolumeSupported() {
		return true;
	}

}
