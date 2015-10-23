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
package loon.jni;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDataReadingOptions;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSRange;

import static loon.jni.OpenAL.*;

public class CAFLoader {

	public static class CAFDesc {

		public double sampleRate;

		public String formatID;

		public int formatFlags;

		public int bytesPerPacket;

		public int framesPerPacket;

		public int channelsPerFrame;

		public int bitsPerChannel;

		public CAFDesc(byte[] data) {
			this(ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN));
		}

		public CAFDesc(ByteBuffer buf) {
			sampleRate = buf.getDouble();
			formatID = getString(buf, 4);
			formatFlags = buf.getInt();
			bytesPerPacket = buf.getInt();
			framesPerPacket = buf.getInt();
			channelsPerFrame = buf.getInt();
		}

		public int getALFormat() {
			switch (channelsPerFrame) {
			case 1:
				return (bitsPerChannel == 8) ? AL_FORMAT_MONO8
						: AL_FORMAT_MONO16;
			case 2:
				return (bitsPerChannel == 8) ? AL_FORMAT_STEREO8
						: AL_FORMAT_STEREO16;
			default:
				return AL_FORMAT_STEREO16;
			}
		}

		@Override
		public String toString() {
			return String
					.format("CAFHeader: sampleRate=%f formatID=%s formatFlags=%x bytesPerPacket=%d "
							+ "framesPerPacket=%d channelsPerFrame=%d bitsPerChannel=%d",
							sampleRate, formatID, formatFlags, bytesPerPacket,
							framesPerPacket, channelsPerFrame, bitsPerChannel);
		}
	}

	public static void load(File path, int bufferId) {
		NSData data;
		try {
			data = NSData.read(path, READ_OPTS);

			ByteBuffer buf = data.asByteBuffer().order(ByteOrder.BIG_ENDIAN);
			if (!getString(buf, 4).equals("caff")) {
				throw new RuntimeException("Input file not CAFF: " + path);
			}
			buf.position(buf.position() + 4);
			CAFDesc desc = null;
			int offset = 8, dataOffset = 0, dataLength = 0;
			do {
				String type = getString(buf, 4);
				int size = (int) buf.getLong();
				offset += 12;

				if (type.equals("data")) {
					dataOffset = offset;
					dataLength = size;
				} else if (type.equals("desc")) {
					desc = new CAFDesc(buf);
					if ("ima4".equalsIgnoreCase(desc.formatID))
						throw new RuntimeException(
								"Cannot use compressed CAFF. "
										+ "Use AIFC for compressed audio on iOS.");
				}

				offset += size;
				buf.position(offset);
			} while (dataOffset == 0);

			ByteBuffer adata = data.getSubdata(
					new NSRange(dataOffset, dataLength)).asByteBuffer();
			alBufferData(bufferId, desc.getALFormat(), adata, dataLength,
					(int) desc.sampleRate);

			data.dispose();

			int error = alGetError();
			if (error != AL_NO_ERROR) {
				throw new RuntimeException("AL error " + error);
			}
		} catch (NSErrorException e) {
			e.printStackTrace();
		}
	}

	protected static String getString(ByteBuffer buf, int length) {
		byte[] data = new byte[length];
		buf.get(data);
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException(uee);
		}
	}

	private static final NSDataReadingOptions READ_OPTS = new NSDataReadingOptions(
			NSDataReadingOptions.MappedIfSafe.value()
					| NSDataReadingOptions.Uncached.value());
}
