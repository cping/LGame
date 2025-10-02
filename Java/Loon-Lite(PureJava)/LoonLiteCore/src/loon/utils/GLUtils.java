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
package loon.utils;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;

public final class GLUtils {

	private GLUtils() {
	}

	private static int currentHardwareTextureID = -1;

	private static int currentBlendMode = -1;

	public static int getCurrentHardwareTextureID() {
		return currentHardwareTextureID;
	}

	public static void setBlendMode(Canvas g, int blend) {
		GLUtils.currentBlendMode = blend;
		g.setBlendMethod(blend);
	}

	public static int getBlendMode() {
		return currentBlendMode;
	}

	public static void reset() {
		GLUtils.reload();
	}

	public static void reload() {
		GLUtils.currentHardwareTextureID = -1;
	}

	public static int nextPOT(int value) {
		int bit = 0x8000, highest = -1, count = 0;
		for (int ii = 15; ii >= 0; ii--, bit >>= 1) {
			if ((value & bit) == 0)
				continue;
			count++;
			if (highest == -1)
				highest = ii;
		}
		return (count > 1) ? (1 << (highest + 1)) : value;
	}

	public static int powerOfTwo(int value) {
		if (value == 0) {
			return 1;
		}
		if ((value & value - 1) == 0) {
			return value;
		}
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		value |= value >> 16;
		return value + 1;
	}

	public static void bindTexture(int id) {
		currentHardwareTextureID = id;
	}

	public static void bindTexture(LTexture tex2d) {
		if (tex2d == null) {
			return;
		}
		if (!tex2d.isLoaded()) {
			tex2d.loadTexture();
		}
		currentHardwareTextureID = tex2d.getID();
	}

	public static Image getScreenshot() {
		return LSystem.base().graphics().getCanvas().snapshot();
	}

}
