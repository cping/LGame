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
package loon.canvas;

public class ImageNinePatch extends NinePatchAbstract<Image, Canvas> {

	public ImageNinePatch(Image image) {
		super(image);
	}

	public ImageNinePatch(Image image, Repeat r) {
		super(image, r);
	}

	@Override
	public int[] getPixels(Image img, int x, int y, int w, int h) {
		return img.getPixels(x, y, w, h);
	}

	@Override
	public int getImageWidth(Image img) {
		return img.getWidth();
	}

	@Override
	public int getImageHeight(Image img) {
		return img.getHeight();
	}

	@Override
	public void pos(Canvas c, int x, int y) {
		c.translate(x, y);
	}

	@Override
	public void draw(Canvas c, Image img, int x, int y, int scaledWidth, int scaledHeight) {
		c.draw(img, x, y, scaledWidth, scaledHeight);
	}

	@Override
	public void draw(Canvas c, Image img, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
		c.draw(img, dx, dy, dx + dw, dy + dh, sx, sy, sx + sw, sy + sh);
	}

}
