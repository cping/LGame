package loon.core.graphics.opengl;

import loon.core.LRelease;
import loon.core.graphics.LImage;


import android.graphics.Bitmap.Config;

/**
 * Copyright 2008 - 2011
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
public abstract class LTextureData implements LRelease {

	android.graphics.Bitmap.Config config;

	public static boolean ALL_ALPHA = false;

	int width, height;

	int texWidth, texHeight;

	boolean hasAlpha, multipyAlpha = ALL_ALPHA;

	int[] source;

	String fileName;

	public abstract LTextureData copy();

	public abstract void createTexture();

	public int getHeight() {
		return height;
	}

	public int getTexHeight() {
		return texHeight;
	}

	public int getTexWidth() {
		return texWidth;
	}

	public boolean hasAlpha() {
		return hasAlpha;
	}

	public int getWidth() {
		return width;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isMultipyAlpha() {
		return multipyAlpha;
	}

	public void setMultipyAlpha(boolean multipyAlpha) {
		this.multipyAlpha = multipyAlpha;
	}

	final static LImage createPixelImage(int[] pixels, int texWidth,
			int texHeight, int width, int height, Config config) {
		LImage image = new LImage(texWidth, texHeight, config);
		image.setPixels(pixels, texWidth, texHeight);
		if (texWidth != width || texHeight != height) {
			LImage temp = image.getSubImage(0, 0, width, height);
			if (temp != image) {
				if (image != null) {
					image.dispose();
					image = null;
				}
				image = temp;
			}
		}
		return image;
	}

	@Override
	public void dispose() {
		source = null;
	}
}
