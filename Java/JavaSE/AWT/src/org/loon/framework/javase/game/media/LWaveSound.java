package org.loon.framework.javase.game.media;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.loon.framework.javase.game.core.resource.Resources;

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
public class LWaveSound implements Sound {

	private SourceDataLine clip;

	private boolean isRunning;

	private float volume;

	public LWaveSound() {
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

		isRunning = true;

		AudioInputStream ain = null;

		try {

			ain = AudioSystem.getAudioInputStream(is);

			if (ain == null) {
				return;
			}
			AudioFormat format = ain.getFormat();

			if ((format.getEncoding() == AudioFormat.Encoding.ULAW)
					|| (format.getEncoding() == AudioFormat.Encoding.ALAW)) {

				AudioFormat temp = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						format.getSampleRate(),
						format.getSampleSizeInBits() * 2, format.getChannels(),
						format.getFrameSize() * 2, format.getFrameRate(), true);
				ain = AudioSystem.getAudioInputStream(temp, ain);
				format = temp;
			}

			rawplay(format, ain, volume);
		} catch (Exception e) {

		} finally {
			if (ain != null) {
				try {
					ain.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public void setSoundVolume(int volume) {
		this.volume = volume;
	}

	private void rawplay(AudioFormat trgFormat, AudioInputStream ain,
			float volume) throws IOException, LineUnavailableException {
		byte[] data = new byte[8192];
		try {
			clip = getLine(ain, trgFormat);
			if (clip == null) {
				return;
			}
			Control.Type vol1 = FloatControl.Type.VOLUME, vol2 = FloatControl.Type.MASTER_GAIN;
			FloatControl c = (FloatControl) clip
					.getControl(FloatControl.Type.MASTER_GAIN);
			float min = c.getMinimum();
			float v = volume * (c.getMaximum() - min) / 100f + min;
			if (this.clip.isControlSupported(vol1)) {
				FloatControl volumeControl = (FloatControl) this.clip
						.getControl(vol1);
				volumeControl.setValue(v);
			} else if (this.clip.isControlSupported(vol2)) {
				FloatControl gainControl = (FloatControl) this.clip
						.getControl(vol2);
				gainControl.setValue(v);
			}
			clip.start();
			int nBytesRead = 0;
			while (isRunning && (nBytesRead != -1)) {
				nBytesRead = ain.read(data, 0, data.length);
				if (nBytesRead != -1) {
					clip.write(data, 0, nBytesRead);
				}
			}
		} finally {
			clip.drain();
			clip.stop();
			clip.close();
			ain.close();
		}
	}

	private SourceDataLine getLine(AudioInputStream ain, AudioFormat audioFormat)
			throws LineUnavailableException {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, ain
				.getFormat(), ((int) ain.getFrameLength() * audioFormat
				.getFrameSize()));
		clip = (SourceDataLine) AudioSystem.getLine(info);
		clip.open(audioFormat);
		return clip;
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
