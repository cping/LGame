package org.loon.framework.javase.game.media;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

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
public class JoggStreamer extends Thread {

	private static final int INITIAL_PACKET_COUNT = 3;

	public static final int BUFFER_SIZE = 8192;

	private static final float SHORT_RANGE = 32768 - 8192;

	private InputStream in;

	private SourceDataLine out;

	private Throwable failure;

	private Page oggPage;

	private SyncState oggSyncState;

	private Packet oggPacket;

	private StreamState oggStreamState;

	private Info vorbisInfo;

	private Comment vorbisComment;

	private DspState vorbisDspState;

	private Block vorbisBlock;

	private float[][][] mpcm;

	private int[] mindex;

	private byte[] sampleBuffer;

	private static int volume = 90;

	public JoggStreamer(InputStream in) {
		super("JoggStreamer");
		this.in = in;
	}

	public static void setDefaultVolume(int percent) {
		volume = percent;
	}

	public boolean isRunning() {
		return in != null && isAlive();
	}

	private static String convertField(byte[] field) {
		return field == null ? null : new String(field, 0, field.length - 1);
	}

	public int getChannelCount() {
		return vorbisInfo == null ? -1 : vorbisInfo.channels;
	}

	public int getRate() {
		return vorbisInfo == null ? -1 : vorbisInfo.rate;
	}

	public String getVendor() {
		return vorbisComment == null ? null
				: convertField(vorbisComment.vendor);
	}

	public int getCommentCount() {
		return vorbisComment == null ? 0 : vorbisComment.user_comments.length;
	}

	public String getComment(int index) {
		return vorbisComment == null ? null
				: convertField(vorbisComment.user_comments[index]);
	}

	public void run() {
		if (Thread.currentThread() != this) {
			throw new IllegalStateException("not this thread");
		}
		try {
			SyncState syncState = this.oggSyncState = new SyncState();
			while (in != null) {
				int off = syncState.buffer(BUFFER_SIZE);
				int n = in.read(syncState.data, off, BUFFER_SIZE);
				if (n > 0) {
					syncState.wrote(n);
					pageOut();
				} else {
					break;
				}
			}
		} catch (EOFException e) {

		} catch (IOException e) {
			failure = e;
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
				if (failure != null) {
					failure = e;
				}
				e.printStackTrace();
			}
			if (out != null) {
				out.stop();
				out.close();
			}
			if (vorbisBlock != null) {
				vorbisBlock.clear();
				vorbisBlock = null;
			}
			if (vorbisDspState != null) {
				vorbisDspState.clear();
				vorbisDspState = null;
			}
			if (vorbisInfo != null) {
				vorbisInfo.clear();
				vorbisInfo = null;
			}
			if (oggStreamState != null) {
				oggStreamState.clear();
				oggStreamState = null;
			}
			if (oggSyncState != null) {
				oggSyncState.clear();
				oggSyncState = null;
			}
			synchronized (this) {
				notifyAll();
			}
		}
	}

	private void pageOut() throws IOException {
		if (oggPage == null) {
			oggPage = new Page();
		}

		for (;;) {
			switch (oggSyncState.pageout(oggPage)) {
			case 0:
				return;
			case 1:
				if (oggStreamState == null) {
					oggStreamState = new StreamState();
					oggStreamState.init(oggPage.serialno());
					oggStreamState.reset();
				}
				if (oggStreamState.pagein(oggPage) < 0) {
					throw new IOException("error reading ogg page");
				} else {
					packetOut();
					if (oggPage.eos() != 0) {
						throw new EOFException();
					}
				}
				break;
			default:
				throw new IOException("ogg input format error");
			}
		}
	}

	private void packetOut() throws IOException {
		if (oggPacket == null) {
			oggPacket = new Packet();
		}

		for (;;) {
			switch (oggStreamState.packetout(oggPacket)) {
			case 0:
				return;
			case 1:
				if (oggPacket.packetno < INITIAL_PACKET_COUNT) {
					headerOut();
				} else {
					pcmOut();
				}
				break;
			default:
				if (oggPacket.packetno < INITIAL_PACKET_COUNT - 1) {
					throw new IOException("ogg input format error");
				} else {
					synchronized (this) {
						notifyAll();
					}
				}
				break;
			}
		}
	}

	private void headerOut() throws IOException {
		synchronized (this) {
			if (vorbisInfo == null) {
				vorbisInfo = new Info();
				vorbisInfo.init();
				vorbisComment = new Comment();
				vorbisComment.init();
			}
		}
		if (vorbisInfo.synthesis_headerin(vorbisComment, oggPacket) < 0) {
			throw new IOException("error reading vorbis header");
		}
	}

	private void pcmOut() throws IOException {
		if (vorbisDspState == null) {
			mpcm = new float[1][][];
			mindex = new int[vorbisInfo.channels];
			vorbisDspState = new DspState();
			vorbisDspState.synthesis_init(vorbisInfo);
			vorbisBlock = new Block(vorbisDspState);
			sampleBuffer = new byte[BUFFER_SIZE * 2];
		}
		if (out == null) {
			openOutput();
		}
		if (vorbisBlock.synthesis(oggPacket) == 0) {
			vorbisDspState.synthesis_blockin(vorbisBlock);
		}
		int n;
		int[] idx = mindex;
		int nch = vorbisInfo.channels;
		int max = sampleBuffer.length;
		while ((n = vorbisDspState.synthesis_pcmout(mpcm, idx)) > 0) {
			int len = (n < max ? n : max);
			float[][] pcm = mpcm[0];
			int off = 0;
			for (int i = 0; i < len; i++) {
				for (int ch = 0; ch < nch; ch++) {
					int m = (int) (pcm[ch][idx[ch] + i] * SHORT_RANGE);
					if (m < Short.MIN_VALUE) {
						sampleBuffer[off++] = (byte) 0x00;
						sampleBuffer[off++] = (byte) 0x80;
					} else if (m > Short.MAX_VALUE) {
						sampleBuffer[off++] = (byte) 0xff;
						sampleBuffer[off++] = (byte) 0x7f;
					} else {
						short s = (short) m;
						sampleBuffer[off++] = (byte) s;
						sampleBuffer[off++] = (byte) (s >>> 8);
					}
				}
			}
			out.write(sampleBuffer, 0, 2 * nch * len);
			vorbisDspState.synthesis_read(len);
		}
	}

	private void openOutput() throws IOException {

		AudioFormat audioFormat = new AudioFormat((float) vorbisInfo.rate, 16,
				vorbisInfo.channels, true, false);

		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat, AudioSystem.NOT_SPECIFIED);

		if (!AudioSystem.isLineSupported(info))

		{
			throw new IOException("line format " + info + "not supported");
		}

		try

		{
			out = (SourceDataLine) AudioSystem.getLine(info);
			out.open(audioFormat);
		}

		catch (LineUnavailableException e) {
			throw new IOException("audio unavailable: " + e.toString());
		}

		out.start();
		updateVolume(volume);
	}

	public void updateVolume(int percent) {
		if (out != null && out.isOpen()) {
			try {
				FloatControl c = (FloatControl) out
						.getControl(FloatControl.Type.MASTER_GAIN);
				float min = c.getMinimum();
				float v = percent * (c.getMaximum() - min) / 100f + min;
				c.setValue(v);
			} catch (IllegalArgumentException e) {
			}
		}
		volume = percent;
	}

	public void interrupt() {
		in = null;
	}

}
