package org.loon.framework.javase.game.media;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
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
public class LModSound implements Sound {

	private SourceDataLine line = null;

	private boolean running;

	private float volume;

	public LModSound() {
		setSoundVolume(Sound.defaultMaxVolume);
	}

	public void playSound(String fileName) {
		playSound(Resources.getResourceAsStream(fileName));
	}

	public void playSound(InputStream in) {
		if (in == null) {
			return;
		}
		byte[] songdata = Resources.getDataSource(in);

		int buflen = 1024;
		int[] lbuf = new int[buflen];
		int[] rbuf = new int[buflen];
		byte[] obuf = new byte[buflen << 2];

		micromod = new Micromod(songdata, SAMPLE_RATE);

		running = true;

		try {
			AudioFormat af = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);

			DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);

			line = (SourceDataLine) AudioSystem.getLine(lineInfo);
			line.open();
			line.start();

			int songlen = micromod.getlen();
			int remain = songlen;

			while (remain > 0 && running) {
				int count = buflen;
				if (count > remain) {
					count = remain;
				}
				micromod.mix(lbuf, rbuf, 0, count);

				for (int ix = 0; ix < count; ix++) {
					int ox = ix << 2;
					obuf[ox] = (byte) (lbuf[ix] & 0xFF);
					obuf[ox + 1] = (byte) (lbuf[ix] >> 8);
					obuf[ox + 2] = (byte) (rbuf[ix] & 0xFF);
					obuf[ox + 3] = (byte) (rbuf[ix] >> 8);
					lbuf[ix] = rbuf[ix] = 0;
				}

				for (int i = 0; i < count << 2; i++) {
					obuf[i] *= volume;
				}
				line.write(obuf, 0, count << 2);
				remain -= count;
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			line.drain();
			line.close();
		}
	}

	public void setSoundVolume(int volume) {
		this.volume = volume / 100F;
	}

	public void stopSound() {
		if (line != null) {
			line.stop();
		}
		running = false;
	}

	public boolean isVolumeSupported() {
		return true;
	}

	// 以下部分为开源的mod解码器，直接引用于此
	// 
	// Miscellaneous Fields
	//

	/**
	 * A micromod object to handle the transform the MOD file to playable audio
	 * data.
	 */
	private Micromod micromod;

	/**
	 * The sample rate.
	 */
	private static final int SAMPLE_RATE = 44100;

	//
	// Internal Classes
	//

	/**
	 * This class handles the transformation of the MOD file to playable audio
	 * data. It's been adapted for use here under the BSD license.
	 * 
	 * <blockquote>
	 * <p>
	 * Micromod/e Java MOD Player
	 * </p>
	 * 
	 * <p>
	 * This is the successor to Micromod, a Java MOD player of mine you might
	 * find on the net if you look hard enough. It's certainly the first version
	 * to deserve the "micro".
	 * </p>
	 * 
	 * <ul>
	 * <li> Fast! Implemented in 100% 32 bit integer arithmetic.
	 * <li> High quality optimised mixing routines, including a 16 point FIR
	 * resampler.
	 * <li> Micro volume ramping to reduce zipper noise (although not
	 * completely).
	 * <li> Good support for almost all XM effects, envelopes, autovibrato, etc.
	 * <li> Sample accurate song length calculation and fast seeking.
	 * </ul>
	 * 
	 * <p>
	 * Copyright 2005 Martin Cameron.
	 * </p>
	 * </blockquote>
	 */
	private class Micromod {
		private final int FP_SHIFT = 13,
				FP_ONE = 1 << FP_SHIFT,
				// FP_MASK = FP_ONE - 1,
				IN_STRUCT_LEN = 0x05, IN_SAMPLE_INDEX = 0x00,
				IN_LOOP_START = 0x01, IN_LOOP_END = 0x02, IN_VOLUME = 0x03,
				IN_FINETUNE = 0x04, CH_STRUCT_LEN = 0x16, CH_SPOS = 0x00,
				CH_STEP = 0x01, CH_AMPL = 0x02, CH_INSTRUMENT = 0x03,
				CH_ASSIGNED = 0x04, CH_VOLUME = 0x05, CH_FINETUNE = 0x06,
				CH_PERIOD = 0x07, CH_PORTA_PERIOD = 0x08,
				CH_PORTA_PARAM = 0x09, CH_PANNING = 0x0A, CH_ARPEGGIO = 0x0B,
				CH_VIBR_PERIOD = 0x0C, CH_VIBR_PARAM = 0x0D,
				CH_VIBR_COUNT = 0x0E, CH_TREM_VOLUME = 0x0F,
				CH_TREM_PARAM = 0x10, CH_PAT_LOOP_ROW = 0x11,
				CH_NOTE_PERIOD = 0x12, CH_NOTE_INSTRU = 0x13,
				CH_NOTE_EFFECT = 0x14, CH_NOTE_EPARAM = 0x15,
				FX_ARPEGGIO = 0x00, FX_PORTA_UP = 0x01, FX_PORTA_DOWN = 0x02,
				FX_TONE_PORTA = 0x03, FX_VIBRATO = 0x04, FX_TPORTA_VOL = 0x05,
				FX_VIBRATO_VOL = 0x06, FX_TREMOLO = 0x07,
				FX_SET_PANNING = 0x08, FX_SET_SPOS = 0x09,
				FX_VOLUME_SLIDE = 0x0A, FX_PAT_JUMP = 0x0B,
				FX_SET_VOLUME = 0x0C, FX_PAT_BREAK = 0x0D, FX_EXTENDED = 0x0E,
				FX_SET_SPEED = 0x0F, EX_FINE_PORT_UP = 0x10,
				EX_FINE_PORT_DN = 0x20, EX_SET_GLISS = 0x30,
				EX_SET_VIBR_WAV = 0x40, EX_SET_FINETUNE = 0x50,
				EX_PAT_LOOP = 0x60, EX_SET_TREM_WAV = 0x70,
				EX_SET_PANNING = 0x80, EX_RETRIG = 0x90, EX_FINE_VOL_UP = 0xA0,
				EX_FINE_VOL_DN = 0xB0, EX_NOTE_CUT = 0xC0,
				EX_NOTE_DELAY = 0xD0, EX_PAT_DELAY = 0xE0,
				EX_INVERT_LOOP = 0xF0;

		private final int[] arptable = new int[] { 8192, 8679, 9195, 9742,
				10321, 10935, 11585, 12274, 13004, 13777, 14596, 15464, 16384,
				17358, 18390, 19484 };

		private final int[] fttable = new int[] { 15464, 15576, 15689, 15803,
				15918, 16033, 16149, 16266, 16384, 16503, 16622, 16743, 16864,
				16986, 17109, 17233 };

		private final int[] sintable = new int[] { 0, 24, 49, 74, 97, 120, 141,
				161, 180, 197, 212, 224, 235, 244, 250, 253, 255, 253, 250,
				244, 235, 224, 212, 197, 180, 161, 141, 120, 97, 74, 49, 24 };

		private byte[] mod;

		private boolean amiga;

		private int numchan, songlen, restart;

		private int pat, npat, row, nrow, tick, tempo, bpm;

		private int fcount, loopcount, loopchan;

		private int[] instruments = new int[IN_STRUCT_LEN * 32];

		private int[] channels = new int[CH_STRUCT_LEN * 32];

		private int samplerate, tickremain;

		/* constructor ( mod - module data ) */
		public Micromod(byte[] mod, int samplerate) {
			this.mod = mod;
			this.samplerate = samplerate;
			songlen = mod[950] & 0x7F;
			restart = mod[951] & 0x7F;
			if (restart >= songlen)
				restart = 0;
			int numpatterns = 0;
			for (int n = 0; n < 128; n++) {
				int pat = mod[952 + n] & 0x7F;
				if (pat >= numpatterns)
					numpatterns = pat + 1;
			}
			switch ((mod[1082] << 8) | mod[1083]) {
			case 0x4b2e: // M.K.
			case 0x4b21: // M!K!
			case 0x5434: // FLT4
				numchan = 4;
				amiga = true;
				break;
			case 0x484e: // xCHN
				numchan = mod[1080] - 48;
				amiga = false;
				break;
			case 0x4348: // xxCH
				numchan = ((mod[1080] - 48) * 10) + (mod[1081] - 48);
				amiga = false;
				break;
			default:
				throw new IllegalArgumentException();
			}
			int sampleidx = 1084 + 4 * numchan * 64 * numpatterns;
			for (int inst = 0; inst < 31; inst++) {
				int slen = ushortbe(mod, inst * 30 + 42) << 1;
				int fine = mod[inst * 30 + 44] & 0xF;
				if (fine > 7)
					fine -= 16;
				int vol = mod[inst * 30 + 45] & 0x7F;
				if (vol > 64)
					vol = 64;
				int lsta = ushortbe(mod, inst * 30 + 46) << 1;
				int llen = ushortbe(mod, inst * 30 + 48) << 1;
				if (sampleidx + slen - 1 >= mod.length) {
					slen = mod.length - sampleidx;
					if (slen < 0)
						slen = 0;
				}
				if (llen < 4 || lsta >= slen) {
					lsta = slen - 1;
					llen = 1;
				}
				int lend = lsta + llen - 1;
				if (lend >= slen)
					lend = slen - 1;
				int ioffset = (inst + 1) * IN_STRUCT_LEN;
				instruments[ioffset + IN_SAMPLE_INDEX] = sampleidx;
				instruments[ioffset + IN_LOOP_START] = lsta;
				instruments[ioffset + IN_LOOP_END] = lend;
				instruments[ioffset + IN_FINETUNE] = fine;
				instruments[ioffset + IN_VOLUME] = vol;
				sampleidx += slen;
			}
			reset();
		}

		/* return the song length in samples */
		public int getlen() {
			reset();
			int len = getticklen();
			while (!tick())
				len += getticklen();
			reset();
			return len;
		}

		/* mix 16 bit stereo audio into the buffers */
		public void mix(int[] l, int[] r, int offset, int len) {
			while (len > 0) {
				int count = tickremain;
				if (count > len)
					count = len;
				for (int chan = 0; chan < numchan; chan++) {
					int coffset = chan * CH_STRUCT_LEN;
					int ampl = channels[coffset + CH_AMPL];
					int pann = channels[coffset + CH_PANNING] << FP_SHIFT - 8;
					int lamp = ampl * (FP_ONE - pann) >> FP_SHIFT;
					int ramp = ampl * pann >> FP_SHIFT;
					int inst = channels[coffset + CH_INSTRUMENT];
					int ioffset = inst * IN_STRUCT_LEN;
					int sidx = instruments[ioffset + IN_SAMPLE_INDEX];
					int lsta = instruments[ioffset + IN_LOOP_START] << FP_SHIFT;
					int lep1 = instruments[ioffset + IN_LOOP_END] + 1 << FP_SHIFT;
					int spos = channels[coffset + CH_SPOS];
					int step = channels[coffset + CH_STEP];
					int llen = lep1 - lsta;
					boolean dontmix = llen <= FP_ONE && spos >= lsta;
					if (!dontmix)
						for (int x = 0; x < count; x++) {
							while (spos >= lep1)
								spos -= llen;
							int sample = mod[sidx + (spos >> FP_SHIFT)] << 8;
							l[offset + x] += sample * lamp >> FP_SHIFT;
							r[offset + x] += sample * ramp >> FP_SHIFT;
							spos += step;
						}
					channels[coffset + CH_SPOS] = spos;
				}
				tickremain -= count;
				if (tickremain == 0) {
					tick();
					tickremain = getticklen();
				}
				offset += count;
				len -= count;
			}
		}

		private void reset() {
			pat = npat = 0;
			row = nrow = 0;
			tick = tempo = 6;
			bpm = 125;
			loopcount = loopchan = 0;
			for (int n = 0; n < channels.length; n++)
				channels[n] = 0;
			for (int chan = 0; chan < numchan; chan++) {
				int p = 128;
				switch (chan & 0x3) {
				case 0:
					p = 64;
					break;
				case 1:
					p = 192;
					break;
				case 2:
					p = 192;
					break;
				case 3:
					p = 64;
					break;
				}
				channels[chan * CH_STRUCT_LEN + CH_PANNING] = p;
			}
			row();
			tickremain = getticklen();
		}

		private boolean tick() {
			tick--;
			if (tick <= 0) {
				tick = tempo;
				return row();
			}
			// Update channel fx
			for (int chan = 0; chan < numchan; chan++) {
				int coffset = chan * CH_STRUCT_LEN;
				int effect = channels[coffset + CH_NOTE_EFFECT];
				int eparam = channels[coffset + CH_NOTE_EPARAM];
				switch (effect) {
				case FX_ARPEGGIO:
					switch (fcount % 3) {
					case 0:
						channels[coffset + CH_ARPEGGIO] = 0;
						break;
					case 1:
						channels[coffset + CH_ARPEGGIO] = (eparam & 0xF0) >> 4;
						break;
					case 2:
						channels[coffset + CH_ARPEGGIO] = eparam & 0x0F;
						break;
					}
					break;
				case FX_PORTA_UP:
					channels[coffset + CH_PERIOD] -= eparam;
					break;
				case FX_PORTA_DOWN:
					channels[coffset + CH_PERIOD] += eparam;
					break;
				case FX_TONE_PORTA:
					toneporta(coffset);
					break;
				case FX_VIBRATO:
					vibrato(coffset);
					break;
				case FX_TPORTA_VOL:
					volslide(coffset, eparam);
					toneporta(coffset);
					break;
				case FX_VIBRATO_VOL:
					volslide(coffset, eparam);
					vibrato(coffset);
					break;
				case FX_TREMOLO:
					tremolo(coffset);
					break;
				case FX_VOLUME_SLIDE:
					volslide(coffset, eparam);
					break;
				case FX_EXTENDED:
					switch (eparam & 0xF0) {
					case EX_RETRIG:
						int rtparam = eparam & 0x0F;
						if (rtparam == 0)
							rtparam = 1;
						if (fcount % rtparam == 0)
							channels[coffset + CH_SPOS] = 0;
						break;
					case EX_NOTE_CUT:
						if ((eparam & 0x0F) == fcount)
							channels[coffset + CH_VOLUME] = 0;
						break;
					case EX_NOTE_DELAY:
						if ((eparam & 0x0F) == fcount)
							trigger(coffset);
						break;
					}
					break;
				}
				channels[coffset + CH_VIBR_COUNT]++;
			}
			mixupdate();
			fcount++;
			return false;
		}

		private boolean row() {
			// Decide whether to restart.
			boolean songend = false;
			if (npat < pat)
				songend = true;
			if (npat == pat && nrow <= row && loopcount <= 0)
				songend = true;
			// Jump to next row
			pat = npat;
			row = nrow;
			// Decide next row.
			nrow = row + 1;
			if (nrow == 64) {
				npat = pat + 1;
				nrow = 0;
			}
			// Load channels and process fx
			fcount = 0;
			int poffset = mod[952 + pat] & 0x7F;
			int roffset = 1084 + (poffset * 64 * numchan * 4)
					+ (row * numchan * 4);
			for (int chan = 0; chan < numchan; chan++) {
				int coffset = chan * CH_STRUCT_LEN;
				int noffset = roffset + (chan * 4);
				channels[coffset + CH_NOTE_PERIOD] = (mod[noffset + 1] & 0xFF)
						| ((mod[noffset] & 0x0F) << 8);
				channels[coffset + CH_NOTE_INSTRU] = ((mod[noffset + 2] & 0xF0) >> 4)
						| (mod[noffset] & 0x10);
				channels[coffset + CH_NOTE_EFFECT] = mod[noffset + 2] & 0x0F;
				channels[coffset + CH_NOTE_EPARAM] = mod[noffset + 3] & 0xFF;
				int effect = channels[coffset + CH_NOTE_EFFECT];
				int eparam = channels[coffset + CH_NOTE_EPARAM];
				if (!(effect == FX_EXTENDED && ((eparam & 0xF0) == EX_NOTE_DELAY)))
					trigger(coffset);
				channels[coffset + CH_ARPEGGIO] = 0;
				channels[coffset + CH_VIBR_PERIOD] = 0;
				channels[coffset + CH_TREM_VOLUME] = 0;
				switch (effect) {
				case FX_ARPEGGIO:
					break;
				case FX_PORTA_UP:
					break;
				case FX_PORTA_DOWN:
					break;
				case FX_TONE_PORTA:
					if (eparam != 0)
						channels[coffset + CH_PORTA_PARAM] = eparam;
					break;
				case FX_VIBRATO:
					if (eparam != 0)
						channels[coffset + CH_VIBR_PARAM] = eparam;
					vibrato(coffset);
					break;
				case FX_TPORTA_VOL:
					break;
				case FX_VIBRATO_VOL:
					vibrato(coffset);
					break;
				case FX_TREMOLO:
					if (eparam != 0)
						channels[coffset + CH_TREM_PARAM] = eparam;
					tremolo(coffset);
					break;
				case FX_SET_PANNING:
					if (!amiga)
						channels[coffset + CH_PANNING] = eparam;
					break;
				case FX_SET_SPOS:
					channels[coffset + CH_SPOS] = eparam << FP_SHIFT + 8;
					break;
				case FX_VOLUME_SLIDE:
					break;
				case FX_PAT_JUMP:
					if (loopcount <= 0) {
						npat = eparam;
						nrow = 0;
					}
					break;
				case FX_SET_VOLUME:
					channels[coffset + CH_VOLUME] = (eparam > 64) ? 64 : eparam;
					break;
				case FX_PAT_BREAK:
					if (loopcount <= 0) {
						npat = pat + 1;
						nrow = ((eparam & 0xF0) >> 4) * 10 + (eparam & 0x0F);
					}
					break;
				case FX_EXTENDED:
					switch (eparam & 0xF0) {
					case EX_FINE_PORT_UP:
						channels[coffset + CH_PERIOD] -= (eparam & 0x0F);
						break;
					case EX_FINE_PORT_DN:
						channels[coffset + CH_PERIOD] += (eparam & 0x0F);
						break;
					case EX_SET_GLISS:
						break;
					case EX_SET_VIBR_WAV:
						break;
					case EX_SET_FINETUNE:
						int ftval = eparam & 0x0F;
						if (ftval > 7)
							ftval -= 16;
						channels[coffset + CH_FINETUNE] = ftval;
						break;
					case EX_PAT_LOOP:
						int plparam = eparam & 0x0F;
						if (plparam == 0)
							channels[coffset + CH_PAT_LOOP_ROW] = row;
						if (plparam > 0
								&& channels[coffset + CH_PAT_LOOP_ROW] < row) {
							if (loopcount <= 0) {
								loopcount = plparam;
								loopchan = chan;
								nrow = channels[coffset + CH_PAT_LOOP_ROW];
								npat = pat;
							} else if (loopchan == chan) {
								if (loopcount == 1) {
									channels[coffset + CH_PAT_LOOP_ROW] = row + 1;
								} else {
									nrow = channels[coffset + CH_PAT_LOOP_ROW];
									npat = pat;
								}
								loopcount--;
							}
						}
						break;
					case EX_SET_TREM_WAV:
						break;
					case EX_SET_PANNING:
						break;
					case EX_RETRIG:
						break;
					case EX_FINE_VOL_UP:
						int fvolup = channels[coffset + CH_VOLUME]
								+ (eparam & 0x0F);
						if (fvolup > 64)
							fvolup = 64;
						channels[coffset + CH_VOLUME] = fvolup;
						break;
					case EX_FINE_VOL_DN:
						int fvoldn = channels[coffset + CH_VOLUME]
								- (eparam & 0x0F);
						if (fvoldn > 64)
							fvoldn = 0;
						channels[coffset + CH_VOLUME] = fvoldn;
						break;
					case EX_NOTE_CUT:
						if ((eparam & 0x0F) == fcount)
							channels[coffset + CH_VOLUME] = 0;
						break;
					case EX_NOTE_DELAY:
						if ((eparam & 0x0F) == fcount)
							trigger(coffset);
						break;
					case EX_PAT_DELAY:
						tick = tempo + tempo * (eparam & 0x0F);
						break;
					case EX_INVERT_LOOP:
						break;
					}
					break;
				case FX_SET_SPEED:
					if (eparam < 32)
						tick = tempo = eparam;
					else
						bpm = eparam;
					break;
				}
			}
			mixupdate();
			fcount++;
			if (npat >= songlen)
				npat = restart;
			if (nrow >= 64)
				nrow = 0;
			return songend;
		}

		private void trigger(int coffset) {
			int period = channels[coffset + CH_NOTE_PERIOD];
			int instru = channels[coffset + CH_NOTE_INSTRU];
			int effect = channels[coffset + CH_NOTE_EFFECT];
			if (instru != 0) {
				channels[coffset + CH_ASSIGNED] = instru;
				int ioffset = instru * IN_STRUCT_LEN;
				channels[coffset + CH_VOLUME] = instruments[ioffset + IN_VOLUME];
				channels[coffset + CH_FINETUNE] = instruments[ioffset
						+ IN_FINETUNE];
				if (amiga) {
					int atlsta = instruments[ioffset + IN_LOOP_START];
					int atlend = instruments[ioffset + IN_LOOP_END];
					if (atlend > atlsta)
						channels[coffset + CH_INSTRUMENT] = instru;
				}
			}
			if (period != 0) {
				channels[coffset + CH_INSTRUMENT] = channels[coffset
						+ CH_ASSIGNED];
				channels[coffset + CH_PORTA_PERIOD] = period;
				if (effect != FX_TONE_PORTA && effect != FX_TPORTA_VOL) {
					channels[coffset + CH_PERIOD] = period;
					channels[coffset + CH_SPOS] = 0;
				}
				channels[coffset + CH_VIBR_COUNT] = 0;
			}
		}

		private void volslide(int coffset, int eparam) {
			int vol = channels[coffset + CH_VOLUME];
			vol += (eparam & 0xF0) >> 4;
			vol -= eparam & 0x0F;
			if (vol > 64)
				vol = 64;
			if (vol < 0)
				vol = 0;
			channels[coffset + CH_VOLUME] = vol;
		}

		private void toneporta(int coffset) {
			int sp = channels[coffset + CH_PERIOD];
			int dp = channels[coffset + CH_PORTA_PERIOD];
			if (sp < dp) {
				sp += channels[coffset + CH_PORTA_PARAM];
				if (sp > dp)
					sp = dp;
			}
			if (sp > dp) {
				sp -= channels[coffset + CH_PORTA_PARAM];
				if (sp < dp)
					sp = dp;
			}
			channels[coffset + CH_PERIOD] = sp;
		}

		private void vibrato(int coffset) {
			int vparam = channels[coffset + CH_VIBR_PARAM];
			int vspeed = (vparam & 0xF0) >> 4;
			int vdepth = vparam & 0x0F;
			int vibpos = vspeed * channels[coffset + CH_VIBR_COUNT];
			int tval = sintable[vibpos & 0x1F];
			if ((vibpos & 0x20) > 0)
				tval = -tval;
			channels[coffset + CH_VIBR_PERIOD] = tval * vdepth >> 7;
		}

		private void tremolo(int coffset) {
			int tparam = channels[coffset + CH_TREM_PARAM];
			int tspeed = (tparam & 0xF0) >> 4;
			int tdepth = tparam & 0x0F;
			int trempos = tspeed * channels[coffset + CH_VIBR_COUNT];
			int tval = sintable[trempos & 0x1F];
			if ((trempos & 0x20) > 0)
				tval = -tval;
			channels[coffset + CH_TREM_VOLUME] = tval * tdepth >> 7;
		}

		private void mixupdate() {
			for (int chan = 0; chan < numchan; chan++) {
				int coffset = chan * CH_STRUCT_LEN;
				int a = channels[coffset + CH_VOLUME]
						+ channels[coffset + CH_TREM_VOLUME];
				if (a < 0)
					a = 0;
				if (a > 64)
					a = 64;
				channels[coffset + CH_AMPL] = a << FP_SHIFT - 8;
				int p = channels[coffset + CH_PERIOD]
						+ channels[coffset + CH_VIBR_PERIOD];
				if (p < 27)
					p = 27;
				int clk = amiga ? 3546894 : 3579364;
				int s = (clk / p << FP_SHIFT) / samplerate;
				s = s * fttable[channels[coffset + CH_FINETUNE] + 8] >> 14;
				s = s * arptable[channels[coffset + CH_ARPEGGIO]] >> 13;
				channels[coffset + CH_STEP] = s;
			}
		}

		private int getticklen() {
			return ((samplerate << 1) + (samplerate >> 1)) / bpm;
		}

		private int ushortbe(byte[] buf, int offset) {
			return ((buf[offset] & 0xFF) << 8) | (buf[offset + 1] & 0xFF);
		}
	}
	// 以上部分为开源的mod解码器，直接引用于此
	// 
	// Miscellaneous Fields End
	//
}
