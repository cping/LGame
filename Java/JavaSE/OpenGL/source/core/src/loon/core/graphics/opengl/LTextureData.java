package loon.core.graphics.opengl;

import java.nio.Buffer;

import loon.core.LRelease;


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
 * @project loonframework
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
public abstract class LTextureData implements LRelease {
	
	public static boolean ALL_ALPHA = false;

	int width, height;

	int texWidth, texHeight;

	boolean hasAlpha, multipyAlpha = ALL_ALPHA;
	
	Buffer source;

	int[] pixels;

	String fileName;

	public abstract LTextureData copy();

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
	
	public void dispose() {
		if (pixels != null) {
			pixels = null;
		}
		if (source != null) {
			source.clear();
			source = null;
		}
	}

}
