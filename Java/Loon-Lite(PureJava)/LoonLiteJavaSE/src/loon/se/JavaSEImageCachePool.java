/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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

import loon.utils.MathUtils;
import loon.utils.cache.Pool;

public class JavaSEImageCachePool extends Pool<BufferedImage> {

	private static JavaSEImageCachePool _instance;

	public static void freeStatic() {
		_instance = null;
	}

	public static final JavaSEImageCachePool get() {
		if (_instance == null) {
			synchronized (JavaSEImageCachePool.class) {
				if (_instance == null) {
					_instance = new JavaSEImageCachePool();
				}
			}
		}
		return _instance;
	}

	private int _imageWidth;

	private int _imageHeight;

	private int _bufferedType;

	public JavaSEImageCachePool() {
		super();
	}

	public JavaSEImageCachePool findImage(int bufferedType, int w, int h) {
		this._imageWidth = w;
		this._imageHeight = h;
		this._bufferedType = bufferedType;
		return this;
	}

	public BufferedImage find(int bufferedType, int w, int h) {
		findImage(bufferedType, w, h);
		return obtain();
	}

	@Override
	public BufferedImage obtain() {
		if (freeObjects.size == 0) {
			return newObject();
		}
		BufferedImage image = freeObjects.find((img) -> {
			return MathUtils.equal(MathUtils.floor(img.getWidth()), _imageWidth)
					&& MathUtils.equal(MathUtils.floor(img.getHeight()), _imageHeight);
		});
		if (image == null) {
			return newObject();
		} else {
			int[] rgba = new int[_imageWidth * _imageHeight];
			image.setRGB(0, 0, _imageWidth, _imageHeight, rgba, 0, _imageWidth);
			freeObjects.remove(image);
		}
		return image;
	}

	@Override
	protected BufferedImage newObject() {
		if (_bufferedType != -1) {
			return new BufferedImage(_imageWidth, _imageHeight, _bufferedType);
		}
		return new BufferedImage(_imageWidth, _imageHeight, BufferedImage.TYPE_INT_ARGB_PRE);
	}

	@Override
	public boolean isLimit(BufferedImage src, BufferedImage old) {
		if (src == null) {
			return true;
		}
		return src.getWidth() > 1024 && src.getHeight() > 1024;
	}

	@Override
	protected BufferedImage filterObtain(BufferedImage o) {
		return o;
	}

}
