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
import loon.geom.PointF;
import loon.utils.MathUtils;

public class PixmapRadial extends PixmapGradient {

	private static final float SCALEBACK = 0.99f;

	private static final int SQRT_LUT_SIZE = (1 << 11);

	private static class Radial {

		static final float[] sqrtLut = new float[SQRT_LUT_SIZE + 1];

		static {
			for (int i = 0; i < sqrtLut.length; i++) {
				sqrtLut[i] = MathUtils.sqrt(i / ((float) SQRT_LUT_SIZE));
			}
		}
	}

	private boolean isFocus = false;

	private boolean isNonCyclic = false;

	private float radius;

	private float centerX, centerY, focusX, focusY;

	private float radiusSq;

	private float constA, constB;

	private float gRadialDelta;

	private float trivial;

	public PixmapRadial(float cx, float cy, float radius, LColor start, LColor end) {
		this(new Affine2f(), cx, cy, radius, cx, cy, GRADIENT_FRACTIONS, new LColor[] { start, end },
				CycleMethod.NO_CYCLE, ColorSpaceType.SRGB);
	}

	public PixmapRadial(float cx, float cy, float radius, float[] fractions, LColor start, LColor end) {
		this(new Affine2f(), cx, cy, radius, cx, cy, fractions, new LColor[] { start, end }, CycleMethod.NO_CYCLE,
				ColorSpaceType.SRGB);
	}

	public PixmapRadial(float cx, float cy, float radius, float[] fractions, LColor[] colors) {
		this(new Affine2f(), cx, cy, radius, cx, cy, fractions, colors, CycleMethod.NO_CYCLE, ColorSpaceType.SRGB);
	}

	public PixmapRadial(float cx, float cy, float radius, LColor[] colors) {
		this(new Affine2f(), cx, cy, radius, cx, cy, GRADIENT_FRACTIONS, colors, CycleMethod.NO_CYCLE,
				ColorSpaceType.SRGB);
	}

	public PixmapRadial(Affine2f t, float cx, float cy, float radius, float[] fractions, LColor[] colors) {
		this(t, cx, cy, radius, cx, cy, fractions, colors, CycleMethod.NO_CYCLE, ColorSpaceType.SRGB);
	}

	public PixmapRadial(Affine2f t, PointF center, float radius, float[] fractions, LColor[] colors) {
		this(t, center, center, radius, fractions, colors, CycleMethod.NO_CYCLE, ColorSpaceType.SRGB);
	}

	public PixmapRadial(Affine2f t, float cx, float cy, float radius, float[] fractions, LColor[] colors,
			CycleMethod cycleMethod) {
		this(t, cx, cy, radius, cx, cy, fractions, colors, cycleMethod, ColorSpaceType.SRGB);
	}

	public PixmapRadial(Affine2f t, PointF center, PointF focus, float radius, float[] fractions, LColor[] colors,
			CycleMethod cycleMethod, ColorSpaceType colorSpace) {
		this(t, center.x, center.y, radius, focus.x, focus.y, fractions, colors, cycleMethod, colorSpace);
	}

	PixmapRadial(Affine2f t, float cx, float cy, float r, float fx, float fy, float[] fractions, LColor[] colors,
			CycleMethod cycleMethod, ColorSpaceType colorSpace) {
		super(fractions, colors, cycleMethod, colorSpace, t);

		this.centerX = cx;
		this.centerY = cy;
		this.focusX = fx;
		this.focusY = fy;
		this.radius = r;

		this.isFocus = (MathUtils.equal(focusX, centerX)) && (MathUtils.equal(focusY, centerY));
		this.isNonCyclic = (cycleMethod == CycleMethod.NO_CYCLE);

		this.radiusSq = radius * radius;

		float dX = focusX - centerX;
		float dY = focusY - centerY;

		float distSq = (dX * dX) + (dY * dY);

		if (distSq > radiusSq * SCALEBACK) {
			float scalefactor = MathUtils.sqrt(radiusSq * SCALEBACK / distSq);
			dX = dX * scalefactor;
			dY = dY * scalefactor;
			focusX = centerX + dX;
			focusY = centerY + dY;
		}

		this.trivial = MathUtils.sqrt(radiusSq - (dX * dX));
		this.constA = m02 - centerX;
		this.constB = m12 - centerY;

		this.gRadialDelta = 2 * (m00 * m00 + m10 * m10) / radiusSq;
	}

	private void cyclicFill(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
		float rowX = (m00 * x) + (m01 * y) + constA;
		float rowY = (m10 * x) + (m11 * y) + constB;
		float gRadialDelta = this.gRadialDelta;
		adjust += w;
		int rgbclip = gradient[fastGradientArraySize];

		for (int j = 0; j < h; j++) {
			float gRel = (rowX * rowX + rowY * rowY) / radiusSq;
			float gDelta = (2 * (m00 * rowX + m10 * rowY) / radiusSq + gRadialDelta / 2);
			int i = 0;
			while (i < w && gRel >= 1f) {
				pixels[off + i] = rgbclip;
				gRel += gDelta;
				gDelta += gRadialDelta;
				i++;
			}
			while (i < w && gRel < 1f) {
				int gIndex;
				if (gRel <= 0) {
					gIndex = 0;
				} else {
					float fIndex = gRel * SQRT_LUT_SIZE;
					int iIndex = (int) (fIndex);
					float s0 = Radial.sqrtLut[iIndex];
					float s1 = Radial.sqrtLut[iIndex + 1] - s0;
					fIndex = s0 + (fIndex - iIndex) * s1;
					gIndex = (int) (fIndex * fastGradientArraySize);
				}
				pixels[off + i] = gradient[gIndex];
				gRel += gDelta;
				gDelta += gRadialDelta;
				i++;
			}
			while (i < w) {
				pixels[off + i] = rgbclip;
				i++;
			}
			off += adjust;
			rowX += m01;
			rowY += m11;
		}
	}

	private void cyclicGradientFill(int[] pixels, int off, int adjust, int x, int y, int w, int h) {

		final float constC = -radiusSq + (centerX * centerX) + (centerY * centerY);
		float A, B, C;
		float slope, yintcpt;
		float solutionX, solutionY;

		final float constX = (m00 * x) + (m01 * y) + m02;
		final float constY = (m10 * x) + (m11 * y) + m12;

		final float precalc2 = 2 * centerY;
		final float precalc3 = -2 * centerX;

		float g;
		float det;
		float currentToFocusSq;
		float intersectToFocusSq;
		float deltaXSq, deltaYSq;
		int indexer = off;
		int pixInc = w + adjust;

		for (int j = 0; j < h; j++) {

			float nx = (m01 * j) + constX;
			float ny = (m11 * j) + constY;

			for (int i = 0; i < w; i++) {

				if (MathUtils.equal(nx, focusX)) {
					solutionX = focusX;
					solutionY = centerY;
					solutionY += (ny > focusY) ? trivial : -trivial;
				} else {
					slope = (ny - focusY) / (nx - focusX);
					yintcpt = ny - (slope * nx);

					A = (slope * slope) + 1;
					B = precalc3 + (-2 * slope * (centerY - yintcpt));
					C = constC + (yintcpt * (yintcpt - precalc2));

					det = MathUtils.sqrt((B * B) - (4 * A * C));
					solutionX = -B;

					solutionX += (nx < focusX) ? -det : det;
					solutionX = solutionX / (2 * A);
					solutionY = (slope * solutionX) + yintcpt;
				}

				deltaXSq = nx - focusX;
				deltaXSq = deltaXSq * deltaXSq;

				deltaYSq = ny - focusY;
				deltaYSq = deltaYSq * deltaYSq;

				currentToFocusSq = deltaXSq + deltaYSq;

				deltaXSq = MathUtils.floor(solutionX - focusX);
				deltaXSq = deltaXSq * deltaXSq;

				deltaYSq = MathUtils.floor(solutionY - focusY);
				deltaYSq = deltaYSq * deltaYSq;

				intersectToFocusSq = deltaXSq + deltaYSq;

				g = MathUtils.sqrt(currentToFocusSq / intersectToFocusSq);

				pixels[indexer + i] = gradients(g);

				nx += m00;
				ny += m10;
			}

			indexer += pixInc;
		}
	}

	@Override
	public void fill(int[] pixels, int off, int x, int y, int w, int h) {
		fill(pixels, off, 0, x, y, w, h);
	}

	@Override
	public void fill(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
		if (isFocus && isNonCyclic && isLookup) {
			cyclicFill(pixels, off, adjust, x, y, w, h);
		} else {
			cyclicGradientFill(pixels, off, adjust, x, y, w, h);
		}
	}
}
