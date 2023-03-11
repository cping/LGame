/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.se;

import java.awt.image.BufferedImage;

import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.utils.IntMap;

public class JavaSECacheImageColor implements LRelease {

	protected final IntMap<BufferedImage> _colorImageCaches;

	protected final JavaSEImage _superImage;
	
	private boolean closed;

	public JavaSECacheImageColor(JavaSEImage image) {
		_colorImageCaches = new IntMap<BufferedImage>();
		_superImage = image;
	}

	public BufferedImage get(int r, int g, int b) {
		int hashCodeValue = 1;
		hashCodeValue = LSystem.unite(hashCodeValue, r);
		hashCodeValue = LSystem.unite(hashCodeValue, g);
		hashCodeValue = LSystem.unite(hashCodeValue, b);
		BufferedImage buffer = _colorImageCaches.get(hashCodeValue);
		if (buffer == null) {
			Pixmap pixmap = _superImage.getPixmap();
			pixmap.setTransparent(LColor.TRANSPARENT);
			pixmap.multiply(new LColor(r, g, b), 0);
			buffer = new BufferedImage(pixmap.getWidth(), pixmap.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			buffer.setRGB(0, 0, pixmap.getWidth(), pixmap.getHeight(), pixmap.getData(), 0, pixmap.getWidth());
			_colorImageCaches.put(hashCodeValue, buffer);
		}
		return buffer;
	}

	public int count() {
		if (_colorImageCaches == null) {
			return 0;
		}
		return _colorImageCaches.size;
	}
	
	public boolean isClosed() {
		return closed;
	}

	public JavaSECacheImageColor free() {
		for (BufferedImage buffer : _colorImageCaches) {
			if (buffer != null) {
				buffer.flush();
				buffer = null;
			}
		}
		_colorImageCaches.clear();
		closed = true;
		return this;
	}

	@Override
	public void close() {
		free();
	}

}
