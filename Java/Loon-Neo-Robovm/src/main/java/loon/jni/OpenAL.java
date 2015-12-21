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

import java.nio.Buffer;
import java.nio.IntBuffer;

import org.robovm.rt.bro.Bro;
import org.robovm.rt.bro.annotation.*;
import org.robovm.rt.bro.ptr.VoidPtr;

@Library("OpenAL")
public class OpenAL {
	static {
		Bro.bind(OpenAL.class);
	}

	@Bridge
	public static native long alcCreateContext(@Pointer long device,
			int[] attrList);

	@Bridge
	public static native boolean alcMakeContextCurrent(@Pointer long context);

	@Bridge
	public static native void alcProcessContext(@Pointer long context);

	@Bridge
	public static native void alcSuspendContext(@Pointer long context);

	@Bridge
	public static native void alcDestroyContext(@Pointer long context);

	@Bridge
	public static native @Pointer long alcGetCurrentContext();

	@Bridge
	public static native @Pointer long alcGetContextsDevice(
			@Pointer long context);

	@Bridge
	public static native @Pointer long alcOpenDevice(String deviceName);

	@Bridge
	public static native void alcCloseDevice(@Pointer long device);

	@Bridge
	public static native int alcGetError(@Pointer long device);

	@Bridge
	public static native boolean alcIsExtensionPresent(@Pointer long device,
			String extName);

	@Bridge
	public static native VoidPtr alcGetProcAddress(@Pointer long device,
			String funcName);

	@Bridge
	public static native int alcGetEnumValue(@Pointer long device,
			String enumName);

	@Bridge
	public static native String alcGetString(@Pointer long device, int param);

	@Bridge
	public static native void alcGetString(@Pointer long device, int param,
			int size, IntBuffer data);

	@Bridge
	public static native @Pointer long alcCaptureOpenDevice(String deviceName,
			int frequency, int format, int bufferSize);

	@Bridge
	public static native boolean alcCaptureCloseDevice(@Pointer long device);

	@Bridge
	public static native void alcCaptureStart(@Pointer long device);

	@Bridge
	public static native void alcCaptureStop(@Pointer long device);

	@Bridge
	public static native void alcCaptureSamples(@Pointer long device,
			Buffer buffer, int samples);

	public static final int AL_FALSE = 0;

	public static final int AL_TRUE = 1;

	public static final int AL_SOURCE_RELATIVE = 0x202;

	public static final int AL_CONE_INNER_ANGLE = 0x1001;

	public static final int AL_CONE_OUTER_ANGLE = 0x1002;

	public static final int AL_PITCH = 0x1003;

	public static final int AL_POSITION = 0x1004;

	public static final int AL_DIRECTION = 0x1005;

	public static final int AL_VELOCITY = 0x1006;

	public static final int AL_LOOPING = 0x1007;

	public static final int AL_BUFFER = 0x1009;

	public static final int AL_GAIN = 0x100A;

	public static final int AL_MIN_GAIN = 0x100D;

	public static final int AL_MAX_GAIN = 0x100E;

	public static final int AL_ORIENTATION = 0x100F;

	public static final int AL_SOURCE_STATE = 0x1010;
	public static final int AL_INITIAL = 0x1011;
	public static final int AL_PLAYING = 0x1012;
	public static final int AL_PAUSED = 0x1013;
	public static final int AL_STOPPED = 0x1014;

	public static final int AL_BUFFERS_QUEUED = 0x1015;
	public static final int AL_BUFFERS_PROCESSED = 0x1016;

	public static final int AL_SEC_OFFSET = 0x1024;
	public static final int AL_SAMPLE_OFFSET = 0x1025;
	public static final int AL_BYTE_OFFSET = 0x1026;

	public static final int AL_SOURCE_TYPE = 0x1027;
	public static final int AL_STATIC = 0x1028;
	public static final int AL_STREAMING = 0x1029;
	public static final int AL_UNDETERMINED = 0x1030;

	public static final int AL_FORMAT_MONO8 = 0x1100;
	public static final int AL_FORMAT_MONO16 = 0x1101;
	public static final int AL_FORMAT_STEREO8 = 0x1102;
	public static final int AL_FORMAT_STEREO16 = 0x1103;

	public static final int AL_REFERENCE_DISTANCE = 0x1020;

	public static final int AL_ROLLOFF_FACTOR = 0x1021;

	public static final int AL_CONE_OUTER_GAIN = 0x1022;

	public static final int AL_MAX_DISTANCE = 0x1023;

	public static final int AL_FREQUENCY = 0x2001;
	public static final int AL_BITS = 0x2002;
	public static final int AL_CHANNELS = 0x2003;
	public static final int AL_SIZE = 0x2004;

	public static final int AL_UNUSED = 0x2010;
	public static final int AL_PENDING = 0x2011;
	public static final int AL_PROCESSED = 0x2012;

	public static final int AL_NO_ERROR = AL_FALSE;

	public static final int AL_INVALID_NAME = 0xA001;

	public static final int AL_INVALID_ENUM = 0xA002;

	public static final int AL_INVALID_VALUE = 0xA003;

	public static final int AL_INVALID_OPERATION = 0xA004;

	public static final int AL_OUT_OF_MEMORY = 0xA005;

	public static final int AL_VENDOR = 0xB001;
	public static final int AL_VERSION = 0xB002;
	public static final int AL_RENDERER = 0xB003;
	public static final int AL_EXTENSIONS = 0xB004;

	public static final int AL_DOPPLER_FACTOR = 0xC000;

	public static final int AL_DOPPLER_VELOCITY = 0xC001;

	public static final int AL_SPEED_OF_SOUND = 0xC003;

	public static final int AL_DISTANCE_MODEL = 0xD000;
	public static final int AL_INVERSE_DISTANCE = 0xD001;
	public static final int AL_INVERSE_DISTANCE_CLAMPED = 0xD002;
	public static final int AL_LINEAR_DISTANCE = 0xD003;
	public static final int AL_LINEAR_DISTANCE_CLAMPED = 0xD004;
	public static final int AL_EXPONENT_DISTANCE = 0xD005;
	public static final int AL_EXPONENT_DISTANCE_CLAMPED = 0xD006;

	@Bridge
	public static native int alGetError();

	@Bridge
	public static native void alGenSources(int n, int[] sources);

	@Bridge
	public static native void alSourcef(int sid, int param, float value);

	@Bridge
	public static native void alSource3f(int sid, int param, float value1,
			float value2, float value3);

	@Bridge
	public static native void alSourcefv(int sid, int param, float[] values);

	@Bridge
	public static native void alSourcei(int sid, int param, int value);

	@Bridge
	public static native void alSource3i(int sid, int param, int value1,
			int value2, int value3);

	@Bridge
	public static native void alSourceiv(int sid, int param, int[] values);

	@Bridge
	public static native void alGetSourcef(int sid, int param, float[] value);

	@Bridge
	public static native void alGetSource3f(int sid, int param, float[] value1,
			float[] value2, float[] value3);

	@Bridge
	public static native void alGetSourcefv(int sid, int param, float[] values);

	@Bridge
	public static native void alGetSourcei(int sid, int param, int[] value);

	@Bridge
	public static native void alGetSource3i(int sid, int param, int[] value1,
			int[] value2, int[] value3);

	@Bridge
	public static native void alGetSourceiv(int sid, int param, int[] values);

	@Bridge
	public static native void alSourcePlayv(int ns, int[] sids);

	@Bridge
	public static native void alSourceStopv(int ns, int[] sids);

	@Bridge
	public static native void alSourceRewindv(int ns, int[] sids);

	@Bridge
	public static native void alSourcePausev(int ns, int[] sids);

	@Bridge
	public static native void alSourcePlay(int sid);

	@Bridge
	public static native void alSourceStop(int sid);

	@Bridge
	public static native void alSourceRewind(int sid);

	@Bridge
	public static native void alSourcePause(int sid);

	@Bridge
	public static native void alGenBuffers(int n, int[] buffers);

	public static int alGenBuffer() {
		int[] result = new int[1];
		alGenBuffers(1, result);
		return result[0];
	}

	@Bridge
	public static native void alDeleteBuffers(int n, int[] buffers);

	public static void alDeleteBuffer(int id) {
		alDeleteBuffers(1, new int[] { id });
	}

	@Bridge
	public static native void alBufferData(int bid, int format, Buffer data,
			int size, int freq);

}
