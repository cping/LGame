package loon.core.graphics.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap.Config;

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
public enum PixelFormat {

	UNDEFINED(-1, -1, -1), RGBA_4444(GL10.GL_RGBA,
			GL10.GL_UNSIGNED_SHORT_4_4_4_4, 16), RGBA_5551(GL10.GL_RGBA,
			GL10.GL_UNSIGNED_SHORT_5_5_5_1, 16), RGBA_8888(GL10.GL_RGBA,
			GL10.GL_UNSIGNED_BYTE, 32), RGB_565(GL10.GL_RGB,
			GL10.GL_UNSIGNED_SHORT_5_6_5, 16), A_8(GL10.GL_ALPHA,
			GL10.GL_UNSIGNED_BYTE, 8), I_8(GL10.GL_LUMINANCE,
			GL10.GL_UNSIGNED_BYTE, 8), AI_88(GL10.GL_LUMINANCE_ALPHA,
			GL10.GL_UNSIGNED_BYTE, 16);

	private final int glFormat;
	
	private final int gltype;
	
	private final int bitpixel;

	private PixelFormat(final int f, final int t, final int b) {
		this.glFormat = f;
		this.gltype = t;
		this.bitpixel = b;
	}

	public static PixelFormat getPixelFormat(final Config config) {
		switch (config) {
		case ARGB_4444:
			return RGBA_4444;
		case ARGB_8888:
			return RGBA_8888;
		case RGB_565:
			return RGB_565;
		case ALPHA_8:
			return A_8;
		default:
			return RGBA_8888;
		}
	}

	public int getGLFormat() {
		return this.glFormat;
	}

	public int getGLType() {
		return this.gltype;
	}

	public int getBitsPerPixel() {
		return this.bitpixel;
	}

}
