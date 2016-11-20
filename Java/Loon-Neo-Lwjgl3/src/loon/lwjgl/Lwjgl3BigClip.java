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
package loon.lwjgl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

class Lwjgl3BigClip implements Clip, LineListener {

	private SourceDataLine dataLine;

	private byte[] audioData;

	private ByteArrayInputStream inputStream;

	private int loopCount = 1;

	private int countDown = 1;

	private int loopPointStart;

	private int loopPointEnd;

	private int framePosition;

	private Thread thread;

	private boolean active;

	private long timelastPositionSet;

	private int bufferUpdateFactor = 2;

	public Lwjgl3BigClip() {
	}

	public Lwjgl3BigClip(Clip clip) throws LineUnavailableException {
		dataLine = AudioSystem.getSourceDataLine(clip.getFormat());
	}

	public byte[] getAudioData() {
		return audioData;
	}

	private long convertFramesToMilliseconds(int frames) {
		return (frames / (long) dataLine.getFormat().getSampleRate()) * 1000;
	}

	private int convertMillisecondsToFrames(long milliseconds) {
		return (int) (milliseconds / dataLine.getFormat().getSampleRate());
	}

	@Override
	public void update(LineEvent le) {

	}

	@Override
	public void loop(int count) {
		loopCount = count;
		countDown = count;
		active = true;
		inputStream.reset();
		start();
	}

	@Override
	public void setLoopPoints(int start, int end) {
		if (start < 0 || start > audioData.length - 1 || end < 0
				|| end > audioData.length) {
			throw new IllegalArgumentException("Loop points '" + start
					+ "' and '" + end + "' cannot be set for buffer of size "
					+ audioData.length);
		}
		if (start > end) {
			throw new IllegalArgumentException("End position " + end
					+ " preceeds start position " + start);
		}

		loopPointStart = start;
		framePosition = loopPointStart;
		loopPointEnd = end;
	}

	@Override
	public void setMicrosecondPosition(long milliseconds) {
		framePosition = convertMillisecondsToFrames(milliseconds);
	}

	@Override
	public long getMicrosecondPosition() {
		return convertFramesToMilliseconds(getFramePosition());
	}

	@Override
	public long getMicrosecondLength() {
		return convertFramesToMilliseconds(getFrameLength());
	}

	@Override
	public void setFramePosition(int frames) {
		framePosition = frames;
		int offset = framePosition * format.getFrameSize();
		try {
			inputStream.reset();
			inputStream.read(new byte[offset]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getFramePosition() {
		long timeSinceLastPositionSet = System.currentTimeMillis()
				- timelastPositionSet;
		int size = dataLine.getBufferSize() * (format.getChannels() / 2)
				/ bufferUpdateFactor;

		size /= dataLine.getFormat().getFrameSize();
		size *= dataLine.getFormat().getFrameSize();

		int framesSinceLast = (int) ((timeSinceLastPositionSet / 1000f) * dataLine
				.getFormat().getFrameRate());
		int framesRemainingTillTime = size - framesSinceLast;
		return framePosition - framesRemainingTillTime;
	}

	@Override
	public int getFrameLength() {
		return audioData.length / format.getFrameSize();
	}

	AudioFormat format;

	@Override
	public void open(AudioInputStream stream) throws IOException,
			LineUnavailableException {

		AudioInputStream is1;
		format = stream.getFormat();

		if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
			is1 = AudioSystem.getAudioInputStream(
					AudioFormat.Encoding.PCM_SIGNED, stream);
		} else {
			is1 = stream;
		}
		format = is1.getFormat();
		InputStream is2 = is1;

		byte[] buf = new byte[1 << 16];
		int numRead = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		numRead = is2.read(buf);
		while (numRead > -1) {
			baos.write(buf, 0, numRead);
			numRead = is2.read(buf, 0, buf.length);
		}
		is2.close();
		audioData = baos.toByteArray();
		AudioFormat afTemp;
		if (format.getChannels() < 2) {
			int frameSize = format.getSampleSizeInBits() * 2 / 8;
			afTemp = new AudioFormat(format.getEncoding(),
					format.getSampleRate(), format.getSampleSizeInBits(), 2,
					frameSize, format.getFrameRate(), format.isBigEndian());
		} else {
			afTemp = format;
		}

		setLoopPoints(0, audioData.length);
		dataLine = AudioSystem.getSourceDataLine(afTemp);
		dataLine.open();
		inputStream = new ByteArrayInputStream(audioData);
	}

	@Override
	public void open(AudioFormat format, byte[] data, int offset, int bufferSize)
			throws LineUnavailableException {
		byte[] input = new byte[bufferSize];
		for (int ii = 0; ii < input.length; ii++) {
			input[ii] = data[offset + ii];
		}
		ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
		try {
			AudioInputStream ais1 = AudioSystem
					.getAudioInputStream(inputStream);
			AudioInputStream ais2 = AudioSystem.getAudioInputStream(format,
					ais1);
			open(ais2);
		} catch (UnsupportedAudioFileException uafe) {
			throw new IllegalArgumentException(uafe);
		} catch (IOException ioe) {
			throw new IllegalArgumentException(ioe);
		}
	}

	@Override
	public float getLevel() {
		return dataLine.getLevel();
	}

	@Override
	public long getLongFramePosition() {
		return dataLine.getLongFramePosition() * 2 / format.getChannels();
	}

	@Override
	public int available() {
		return dataLine.available();
	}

	@Override
	public int getBufferSize() {
		return dataLine.getBufferSize();
	}

	@Override
	public AudioFormat getFormat() {
		return format;
	}

	@Override
	public boolean isActive() {
		return dataLine.isActive();
	}

	@Override
	public boolean isRunning() {
		return dataLine.isRunning();
	}

	@Override
	public boolean isOpen() {
		return dataLine.isOpen();
	}

	@Override
	public void stop() {
		active = false;
		dataLine.stop();
		if (thread != null) {
			try {
				active = false;
				thread.join();
			} catch (InterruptedException wakeAndContinue) {
			}
		}
	}

	public byte[] convertMonoToStereo(byte[] data, int bytesRead) {
		byte[] tempData = new byte[bytesRead * 2];
		if (format.getSampleSizeInBits() == 8) {
			for (int ii = 0; ii < bytesRead; ii++) {
				byte b = data[ii];
				tempData[ii * 2] = b;
				tempData[ii * 2 + 1] = b;
			}
		} else {
			for (int ii = 0; ii < bytesRead - 1; ii += 2) {
				byte b1 = data[ii];
				byte b2 = data[ii + 1];
				tempData[ii * 2] = b1;
				tempData[ii * 2 + 1] = b2;
				tempData[ii * 2 + 2] = b1;
				tempData[ii * 2 + 3] = b2;
			}
		}
		return tempData;
	}

	@Override
	public void start() {
		Runnable r = new Runnable() {
			public void run() {
				dataLine.start();

				active = true;

				int bytesRead = 0;
				int frameSize = dataLine.getFormat().getFrameSize();
				int bufSize = dataLine.getBufferSize();
				boolean startOrMove = true;
				byte[] data = new byte[bufSize];
				int offset = framePosition * frameSize;
				bytesRead = inputStream.read(new byte[offset], 0, offset);
				bytesRead = inputStream.read(data, 0, data.length);

				while (bytesRead != -1
						&& (loopCount == Clip.LOOP_CONTINUOUSLY || countDown > 0)
						&& active) {
					int framesRead;
					byte[] tempData;
					if (format.getChannels() < 2) {
						tempData = convertMonoToStereo(data, bytesRead);
						framesRead = bytesRead / format.getFrameSize();
						bytesRead *= 2;
					} else {
						framesRead = bytesRead
								/ dataLine.getFormat().getFrameSize();
						tempData = Arrays.copyOfRange(data, 0, bytesRead);
					}

					framePosition += framesRead;
					if (framePosition >= loopPointEnd) {
						framePosition = loopPointStart;
						inputStream.reset();
						countDown--;
					}
					timelastPositionSet = System.currentTimeMillis();

					byte[] newData = tempData;
					dataLine.write(newData, 0, newData.length);
					if (startOrMove) {
						int len = bufSize / bufferUpdateFactor;

						len /= frameSize;
						len *= frameSize;

						data = new byte[len];
						startOrMove = false;
					}

					bytesRead = inputStream.read(data, 0, data.length);
					if (bytesRead < 0
							&& (--countDown > 0 || loopCount == Clip.LOOP_CONTINUOUSLY)) {
						inputStream.read(new byte[offset], 0, offset);
						inputStream.reset();
						bytesRead = inputStream.read(data, 0, data.length);
					}
				}

				active = false;
				countDown = 1;
				framePosition = 0;
				inputStream.reset();
				dataLine.stop();

			}
		};
		thread = new Thread(r);
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void flush() {
		dataLine.flush();
	}

	@Override
	public void drain() {
		dataLine.drain();
	}

	@Override
	public void removeLineListener(LineListener listener) {
		dataLine.removeLineListener(listener);
	}

	@Override
	public void addLineListener(LineListener listener) {
		dataLine.addLineListener(listener);
	}

	@Override
	public Control getControl(Control.Type control) {
		return dataLine.getControl(control);
	}

	@Override
	public Control[] getControls() {
		if (dataLine == null) {
			return new Control[0];
		} else {
			return dataLine.getControls();
		}
	}

	@Override
	public boolean isControlSupported(Control.Type control) {
		return dataLine.isControlSupported(control);
	}

	@Override
	public void close() {
		dataLine.close();
	}

	@Override
	public void open() throws LineUnavailableException {
		throw new IllegalArgumentException(
				"illegal call to open() in interface Clip");
	}

	@Override
	public Line.Info getLineInfo() {
		return dataLine.getLineInfo();
	}
}
