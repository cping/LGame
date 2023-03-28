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
package loon.fx;

import java.nio.IntBuffer;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import loon.utils.MathUtils;
import loon.utils.cache.Pool;

public class JavaFXImageCachePool extends Pool<WritableImage> {

	private static JavaFXImageCachePool _instance;

	public static void freeStatic() {
		_instance = null;
	}

	public static final JavaFXImageCachePool get() {
		if (_instance == null) {
			synchronized (JavaFXImageCachePool.class) {
				if (_instance == null) {
					_instance = new JavaFXImageCachePool();
				}
			}
		}
		return _instance;
	}

	private int _imageWidth;

	private int _imageHeight;

	private PixelReader _pixelreader;

	public JavaFXImageCachePool() {
		super();
	}

	public JavaFXImageCachePool findImage(PixelReader reader, int w, int h) {
		this._imageWidth = w;
		this._imageHeight = h;
		this._pixelreader = reader;
		return this;
	}

	public WritableImage find(PixelReader reader, int w, int h) {
		findImage(reader, w, h);
		return obtain();
	}

	@Override
	public WritableImage obtain() {
		if (freeObjects.size == 0) {
			return newObject();
		}
		WritableImage image = freeObjects.find((img) -> {
			return MathUtils.equal(MathUtils.floor(img.getWidth()), _imageWidth)
					&& MathUtils.equal(MathUtils.floor(img.getHeight()), _imageHeight);
		});
		if (image == null) {
			return newObject();
		} else {
			int[] rgba = new int[_imageWidth * _imageHeight];
			PixelWriter writer = image.getPixelWriter();
			PixelFormat.Type type = writer.getPixelFormat().getType();
			WritablePixelFormat<IntBuffer> format = null;
			if (type == PixelFormat.Type.INT_ARGB_PRE) {
				format = PixelFormat.getIntArgbPreInstance();
			} else {
				format = PixelFormat.getIntArgbInstance();
			}
			writer.setPixels(0, 0, _imageWidth, _imageHeight, format, rgba, 0, _imageWidth);
			freeObjects.remove(image);
		}
		return image;
	}

	@Override
	protected WritableImage newObject() {
		if (_pixelreader != null) {
			return new WritableImage(_pixelreader, _imageWidth, _imageHeight);
		}
		return new WritableImage(_imageWidth, _imageHeight);
	}

	@Override
	public boolean isLimit(WritableImage src, WritableImage old) {
		if (src == null) {
			return true;
		}
		return src.getWidth() > 1024 && src.getHeight() > 1024;
	}

	@Override
	protected WritableImage filterObtain(WritableImage o) {
		return o;
	}
}
