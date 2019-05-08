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
package loon.canvas;

import loon.Graphics;
import loon.LGame;
import loon.LSysException;
import loon.LSystem;
import loon.utils.Scale;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;

public abstract class ImageImpl extends Image {

	public static class Data {
		public final Scale scale;
		public final Object bitmap;
		public final int pixelWidth, pixelHeight;

		public Data(Scale scale, Object bitmap, int pixelWidth, int pixelHeight) {
			this.bitmap = bitmap;
			this.scale = scale;
			this.pixelWidth = pixelWidth;
			this.pixelHeight = pixelHeight;
		}
	}

	protected final String source;
	protected Scale scale;
	protected int pixelWidth, pixelHeight;

	public synchronized void succeed(Data data) {
		scale = data.scale;
		pixelWidth = data.pixelWidth;
		pixelHeight = data.pixelHeight;
		setBitmap(data.bitmap);
		((GoPromise<Image>) state).succeed(this); 
	}

	public synchronized void fail(Throwable error) {
		if (pixelWidth == 0){
			pixelWidth = 50;
		}
		if (pixelHeight == 0){
			pixelHeight = 50;
		}
		setBitmap(createErrorBitmap(pixelWidth, pixelHeight));
		((GoPromise<Image>) state).fail(error); 
	}

	@Override
	public Scale scale() {
		return scale;
	}

	@Override
	public int pixelWidth() {
		return pixelWidth;
	}

	@Override
	public int pixelHeight() {
		return pixelHeight;
	}

	protected ImageImpl(Graphics gfx, Scale scale, int pixelWidth,
			int pixelHeight, String source, Object bitmap) {
		super(gfx);
		if (pixelWidth == 0 || pixelHeight == 0){
			throw new LSysException("Invalid size for ready image: "
					+ pixelWidth + "x" + pixelHeight + " bitmap: " + bitmap);
		}
		this.source = source;
		this.scale = scale;
		this.pixelWidth = pixelWidth;
		this.pixelHeight = pixelHeight;
		setBitmap(bitmap);
	}

	protected ImageImpl(Graphics gfx, GoFuture<Image> state, Scale preScale,
			int preWidth, int preHeight, String source) {
		super(gfx, state);
		this.source = source;
		this.scale = preScale;
		this.pixelWidth = preWidth;
		this.pixelHeight = preHeight;
	}

	protected ImageImpl(LGame game, boolean async, Scale preScale,
			int preWidth, int preHeight, String source) {
		this(game.graphics(), async ? game.asyn().<Image> deferredPromise()
				: GoPromise.<Image> create(), preScale, preWidth, preHeight,
				source);
	}

	protected abstract void setBitmap(Object bitmap);

	protected abstract Object createErrorBitmap(int pixelWidth, int pixelHeight);

	public Image getLightImage(Image buffer, float v) {
		return getLightImage(buffer, (int) (v * 255));
	}

	public Image getLightImage(Image buffer, int v) {
		Canvas canvas = LSystem.base().graphics()
				.createCanvas(buffer.width(), buffer.height());
		canvas.draw(buffer, 0, 0);
		getLight(canvas.image, v);
		canvas.close();
		return canvas.image;
	}

	@Override
	public String getSource() {
		return this.source;
	}

}
