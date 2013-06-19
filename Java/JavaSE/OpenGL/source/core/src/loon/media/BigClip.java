/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.media;

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

/**
 * Reformatted and adapted version of BigClip as posted to:
 * http://stackoverflow.
 * com/questions/9470148/how-do-you-play-a-long-audio-clip-in-java
 * 
 * An implementation of the javax.sound.sampled.Clip that is designed to handle
 * Clips of arbitrary size, limited only by the amount of memory available to
 * the app. It uses the post 1.4 thread behaviour (daemon thread) that will stop
 * the sound running after the main has exited.
 * <ul>
 * <li>2012-12-18 - Fixed bug with LOOP_CONTINUOUSLY and some bugs with drain()
 * and buffer sizes.
 * <li>2012-02-29 - Reworked play/loop to fix several bugs.
 * <li>2009-09-01 - Fixed bug that had clip ..clipped at the end, by calling
 * drain() (before calling stop()) on the dataline after the play loop was
 * complete. Improvement to frame and microsecond position determination.
 * <li>2009-08-17 - added convenience constructor that accepts a Clip. Changed
 * the private convertFrameToM..seconds methods from 'micro' to 'milli' to
 * reflect that they were dealing with units of 1000/th of a second.
 * <li>2009-08-14 - got rid of flush() after the sound loop, as it was cutting
 * off tracks just before the end, and was found to be not needed for the
 * fast-forward/rewind functionality it was introduced to support.
 * <li>2009-08-11 - First binary release.
 * </ul>
 * N.B. Remove @Override notation and logging to use in 1.3+
 * 
 * @since 1.5
 * @version 2012-12-18
 * @author Andrew Thompson
 * @author Alejandro Garcia
 * @author Michael Thomas
 */
class BigClip implements Clip, LineListener {

	/** The DataLine used by this Clip. */
	private SourceDataLine dataLine;

	/** The raw bytes of the audio data. */
	private byte[] audioData;

	/** The stream wrapper for the audioData. */
	private ByteArrayInputStream inputStream;

	/** Loop count set by the calling code. */
	private int loopCount = 1;
	/** Internal count of how many loops to go. */
	private int countDown = 1;
	/** The start of a loop point. Defaults to 0. */
	private int loopPointStart;
	/** The end of a loop point. Defaults to the end of the Clip. */
	private int loopPointEnd;

	/** Stores the current frame position of the clip. */
	private int framePosition;

	/** Thread used to run() sound. */
	private Thread thread;
	/** Whether the sound is currently playing or active. */
	private boolean active;
	/** Stores the last time bytes were dumped to the audio stream. */
	private long timelastPositionSet;

	private int bufferUpdateFactor = 2;

	/**
	 * Default constructor for a BigClip. Does nothing. Information from the
	 * AudioInputStream passed in open() will be used to get an appropriate
	 * SourceDataLine.
	 */
	public BigClip() {
	}

	/**
	 * There are a number of AudioSystem methods that will return a configured
	 * Clip. This convenience constructor allows us to obtain a SourceDataLine
	 * for the BigClip that uses the same AudioFormat as the original Clip.
	 * 
	 * @param clip
	 *            Clip The Clip used to configure the BigClip.
	 */
	public BigClip(Clip clip) throws LineUnavailableException {
		dataLine = AudioSystem.getSourceDataLine(clip.getFormat());
	}

	/**
	 * Provides the entire audio buffer of this clip.
	 * 
	 * @return audioData byte[] The bytes of the audio data that is loaded in
	 *         this Clip.
	 */
	public byte[] getAudioData() {
		return audioData;
	}

	/** Converts a frame count to a duration in milliseconds. */
	private long convertFramesToMilliseconds(int frames) {
		return (frames / (long) dataLine.getFormat().getSampleRate()) * 1000;
	}

	/** Converts a duration in milliseconds to a frame count. */
	private int convertMillisecondsToFrames(long milliseconds) {
		return (int) (milliseconds / dataLine.getFormat().getSampleRate());
	}

	@Override
	public void update(LineEvent le) {
		// PlayN.log().debug("update: " + le);
	}

	@Override
	public void loop(int count) {
		// PlayN.log().debug("loop(" + count + ") - framePosition: " +
		// framePosition);
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

		// Step down to the next whole frame.
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
		// TODO - throw IAE for invalid frame size, format.
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
		// PlayN.log().debug("BigClip.stop()");
		active = false;
		// why did I have this commented out?
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
				// PlayN.log().debug("bytesRead " + bytesRead);
				bytesRead = inputStream.read(data, 0, data.length);

				// PlayN.log().debug("loopCount " + loopCount);
				// PlayN.log().debug("countDown " + countDown);
				// PlayN.log().debug("bytesRead " + bytesRead);

				while (bytesRead != -1
						&& (loopCount == Clip.LOOP_CONTINUOUSLY || countDown > 0)
						&& active) {
					// PlayN.log().debug("BigClip.start() loop " +
					// framePosition);
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
						// PlayN.log().debug("Loop Count: " + countDown);
					}
					timelastPositionSet = System.currentTimeMillis();

					byte[] newData = tempData;
					dataLine.write(newData, 0, newData.length);
					if (startOrMove) {
						int len = bufSize / bufferUpdateFactor;

						// Step down to the next whole frame.
						len /= frameSize;
						len *= frameSize;

						data = new byte[len];
						startOrMove = false;
					}

					bytesRead = inputStream.read(data, 0, data.length);
					if (bytesRead < 0
							&& (--countDown > 0 || loopCount == Clip.LOOP_CONTINUOUSLY)) {
						inputStream.read(new byte[offset], 0, offset);
						// PlayN.log().debug("loopCount " + loopCount);
						// PlayN.log().debug("countDown " + countDown);
						inputStream.reset();
						bytesRead = inputStream.read(data, 0, data.length);
					}
				}

				// PlayN.log().debug("BigClip.start() loop ENDED" +
				// framePosition);
				active = false;
				countDown = 1;
				framePosition = 0;
				inputStream.reset();
				dataLine.stop();

			}
		};
		thread = new Thread(r);
		// makes thread behaviour compatible with JavaSound post 1.4
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
