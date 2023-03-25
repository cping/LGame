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

import loon.geom.Affine2f;

public class PixmapLinear extends PixmapGradient {

	private float drawX, drawY, gc;

	public PixmapLinear(float startX, float startY, float endX, float endY, LColor start, LColor end) {
		this(new Affine2f(), startX, startY, endX, endY, GRADIENT_FRACTIONS, new LColor[] { start, end },
				CycleMethod.NO_CYCLE, ColorSpaceType.LINEAR_RGB);
	}

	public PixmapLinear(float startX, float startY, float endX, float endY, float[] fractions, LColor start,
			LColor end) {
		this(new Affine2f(), startX, startY, endX, endY, fractions, new LColor[] { start, end }, CycleMethod.NO_CYCLE,
				ColorSpaceType.LINEAR_RGB);
	}

	public PixmapLinear(float startX, float startY, float endX, float endY, float[] fractions, LColor[] colors) {
		this(new Affine2f(), startX, startY, endX, endY, fractions, colors, CycleMethod.NO_CYCLE,
				ColorSpaceType.LINEAR_RGB);
	}

	public PixmapLinear(float startX, float startY, float endX, float endY, LColor[] colors) {
		this(new Affine2f(), startX, startY, endX, endY, GRADIENT_FRACTIONS, colors, CycleMethod.NO_CYCLE,
				ColorSpaceType.LINEAR_RGB);
	}

	public PixmapLinear(Affine2f t, float startX, float startY, float endX, float endY, LColor[] colors) {
		this(t, startX, startY, endX, endY, GRADIENT_FRACTIONS, colors, CycleMethod.NO_CYCLE,
				ColorSpaceType.LINEAR_RGB);
	}

	public PixmapLinear(Affine2f t, float startX, float startY, float endX, float endY, LColor[] colors,
			CycleMethod cycleMethod, ColorSpaceType colorSpace) {
		this(t, startX, startY, endX, endY, GRADIENT_FRACTIONS, colors, cycleMethod, colorSpace);
	}

	PixmapLinear(Affine2f t, float startX, float startY, float endX, float endY, float[] fractions, LColor[] colors,
			CycleMethod cycleMethod, ColorSpaceType colorSpace) {
		super(fractions, colors, cycleMethod, colorSpace, t);

		float startx = startX;
		float starty = startY;
		float endx = endX;
		float endy = endY;

		float dx = endx - startx;
		float dy = endy - starty;
		float dSq = dx * dx + dy * dy;

		float constX = dx / dSq;
		float constY = dy / dSq;

		this.drawX = m00 * constX + m10 * constY;
		this.drawY = m01 * constX + m11 * constY;
		this.gc = (m02 - startx) * constX + (m12 - starty) * constY;
	}

	@Override
	public void fill(int[] pixels, int off, int x, int y, int w, int h) {
		fill(pixels, off, 0, x, y, w, h);
	}

	@Override
	public void fill(int[] pixels, int off, int adjust, int x, int y, int w, int h) {

		float g = 0;
		int rowLimit = off + w;

		float initConst = (drawX * x) + gc;

		for (int i = 0; i < h; i++) {
			g = initConst + drawY * (y + i);
			while (off < rowLimit) {
				pixels[off++] = gradients(g);
				g += drawX;
			}

			off += adjust;

			rowLimit = off + w;
		}
	}
}
