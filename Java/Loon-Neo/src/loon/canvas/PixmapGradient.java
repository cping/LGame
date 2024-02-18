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

import loon.LSysException;
import loon.geom.Affine2f;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

public abstract class PixmapGradient {

	public static enum CycleMethod {
		NO_CYCLE, REFLECT, REPEAT
	}

	public static enum ColorSpaceType {
		SRGB, LINEAR_RGB
	}

	private final Affine2f affine = new Affine2f();

	final boolean hasAlpha;

	final float[] fractions;

	final LColor[] colors;

	final CycleMethod cycleMethod;

	final ColorSpaceType colorSpace;

	protected boolean isLookup;

	protected int fastGradientArraySize;

	protected int[] gradient;

	private int[][] gradients;

	protected float m00, m01, m10, m11, m02, m12;

	private float[] normalizedIntervals;

	protected static final float[] GRADIENT_FRACTIONS = new float[] { 0f, 1f };

	protected static final int GRADIENT_SIZE = 256;
	protected static final int GRADIENT_SIZE_INDEX = GRADIENT_SIZE - 1;

	private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;

	private static class Gradient {

		static final int[] SRGBtoLinearRGB = new int[256];
		static final int[] LinearRGBtoSRGB = new int[256];

		static {
			for (int k = 0; k < 256; k++) {
				SRGBtoLinearRGB[k] = toLinearRGB(k);
				LinearRGBtoSRGB[k] = toSRGB(k);
			}
		}
	}

	PixmapGradient(float[] fractions, LColor[] colors, CycleMethod cycleMethod, ColorSpaceType colorSpace,
			Affine2f aff) {
		if (fractions == null) {
			throw new LSysException("Fractions array cannot be null");
		}

		if (colors == null) {
			throw new LSysException("Colors array cannot be null");
		}

		if (cycleMethod == null) {
			throw new LSysException("Cycle method cannot be null");
		}

		if (colorSpace == null) {
			throw new LSysException("Color space cannot be null");
		}
		if (aff == null) {
			throw new LSysException("Gradient transform cannot be null");
		}

		if (fractions.length != colors.length) {
			throw new LSysException("Colors and fractions must have equal size");
		}

		if (colors.length < 2) {
			throw new LSysException("User must specify at least 2 colors");
		}
		this.affine.set(aff);

		this.m00 = affine.m00;
		this.m01 = affine.m01;
		this.m10 = affine.m10;
		this.m11 = affine.m11;
		this.m02 = affine.tx;
		this.m12 = affine.ty;

		float previousFraction = -1.0f;
		for (float currentFraction : fractions) {
			if (currentFraction < 0f || currentFraction > 1f) {
				throw new LSysException("Fraction values must be in the range 0 to 1: " + currentFraction);
			}

			if (currentFraction <= previousFraction) {
				throw new LSysException("Keyframe fractions must be increasing: " + currentFraction);
			}

			previousFraction = currentFraction;
		}

		boolean fixFirst = false;
		boolean fixLast = false;
		int len = fractions.length;
		int off = 0;

		if (fractions[0] != 0f) {
			fixFirst = true;
			len++;
			off++;
		}
		if (fractions[fractions.length - 1] != 1f) {
			fixLast = true;
			len++;
		}

		this.fractions = new float[len];
		System.arraycopy(fractions, 0, this.fractions, off, fractions.length);
		this.colors = new LColor[len];
		System.arraycopy(colors, 0, this.colors, off, colors.length);

		if (fixFirst) {
			this.fractions[0] = 0f;
			this.colors[0] = colors[0];
		}
		if (fixLast) {
			this.fractions[len - 1] = 1f;
			this.colors[len - 1] = colors[colors.length - 1];
		}

		this.colorSpace = colorSpace;
		this.cycleMethod = cycleMethod;
		boolean opaque = true;
		for (int i = 0; i < colors.length; i++) {
			opaque = opaque && (colors[i].getAlpha() == 0xff);
		}
		hasAlpha = opaque ? false : true;
		if (gradient == null && gradients == null) {
			lookupData(colors);
		}
	}

	private void lookupData(LColor[] colors) {
		LColor[] normalizedColors;
		if (colorSpace == ColorSpaceType.LINEAR_RGB) {
			normalizedColors = new LColor[colors.length];
			for (int i = 0; i < colors.length; i++) {
				int argb = colors[i].getRGB();
				int a = argb >>> 24;
				int r = Gradient.SRGBtoLinearRGB[(argb >> 16) & 0xff];
				int g = Gradient.SRGBtoLinearRGB[(argb >> 8) & 0xff];
				int b = Gradient.SRGBtoLinearRGB[(argb) & 0xff];
				normalizedColors[i] = new LColor(r, g, b, a);
			}
		} else {
			normalizedColors = colors;
		}

		normalizedIntervals = new float[fractions.length - 1];

		for (int i = 0; i < normalizedIntervals.length; i++) {
			normalizedIntervals[i] = this.fractions[i + 1] - this.fractions[i];
		}

		gradients = new int[normalizedIntervals.length][];

		float Imin = 1;
		for (int i = 0; i < normalizedIntervals.length; i++) {
			Imin = (Imin > normalizedIntervals[i]) ? normalizedIntervals[i] : Imin;
		}

		int estimatedSize = 0;
		for (int i = 0; i < normalizedIntervals.length; i++) {
			estimatedSize += (normalizedIntervals[i] / Imin) * GRADIENT_SIZE;
		}

		if (estimatedSize > MAX_GRADIENT_ARRAY_SIZE) {
			multipleGradient(normalizedColors);
		} else {
			singleGradient(normalizedColors, Imin);
		}
	}

	private void singleGradient(LColor[] colors, float Imin) {

		isLookup = true;
		int rgb1, rgb2;
		int gradientsTot = 1;

		for (int i = 0; i < gradients.length; i++) {
			int nGradients = (int) ((normalizedIntervals[i] / Imin) * 255f);
			gradientsTot += nGradients;
			gradients[i] = new int[nGradients];

			rgb1 = colors[i].getRGB();
			rgb2 = colors[i + 1].getRGB();

			interpolate(rgb1, rgb2, gradients[i]);
		}

		gradient = new int[gradientsTot];
		int curOffset = 0;
		for (int i = 0; i < gradients.length; i++) {
			System.arraycopy(gradients[i], 0, gradient, curOffset, gradients[i].length);
			curOffset += gradients[i].length;
		}
		gradient[gradient.length - 1] = colors[colors.length - 1].getRGB();

		if (colorSpace == ColorSpaceType.LINEAR_RGB) {
			for (int i = 0; i < gradient.length; i++) {
				gradient[i] = linearRGBtoSRGB(gradient[i]);
			}
		}

		fastGradientArraySize = gradient.length - 1;
	}

	private void multipleGradient(LColor[] colors) {
		isLookup = false;

		int rgb1, rgb2;

		for (int i = 0; i < gradients.length; i++) {
			gradients[i] = new int[GRADIENT_SIZE];

			rgb1 = colors[i].getRGB();
			rgb2 = colors[i + 1].getRGB();

			interpolate(rgb1, rgb2, gradients[i]);
		}

		if (colorSpace == ColorSpaceType.LINEAR_RGB) {
			for (int j = 0; j < gradients.length; j++) {
				for (int i = 0; i < gradients[j].length; i++) {
					gradients[j][i] = linearRGBtoSRGB(gradients[j][i]);
				}
			}
		}
	}

	private void interpolate(int rgb1, int rgb2, int[] output) {

		int a1, r1, g1, b1, da, dr, dg, db;

		float stepSize = 1.0f / output.length;

		a1 = (rgb1 >> 24) & 0xff;
		r1 = (rgb1 >> 16) & 0xff;
		g1 = (rgb1 >> 8) & 0xff;
		b1 = (rgb1) & 0xff;

		da = ((rgb2 >> 24) & 0xff) - a1;
		dr = ((rgb2 >> 16) & 0xff) - r1;
		dg = ((rgb2 >> 8) & 0xff) - g1;
		db = ((rgb2) & 0xff) - b1;

		for (int i = 0; i < output.length; i++) {
			output[i] = (((int) ((a1 + i * da * stepSize) + 0.5) << 24))
					| (((int) ((r1 + i * dr * stepSize) + 0.5) << 16)) | (((int) ((g1 + i * dg * stepSize) + 0.5) << 8))
					| (((int) ((b1 + i * db * stepSize) + 0.5)));
		}
	}

	public final float[] getFractions() {
		return CollectionUtils.copyOf(fractions, fractions.length);
	}

	public final LColor[] getColors() {
		return CollectionUtils.copyOf(colors, colors.length);
	}

	private int linearRGBtoSRGB(int rgb) {

		int a1, r1, g1, b1;

		a1 = (rgb >> 24) & 0xff;
		r1 = (rgb >> 16) & 0xff;
		g1 = (rgb >> 8) & 0xff;
		b1 = (rgb) & 0xff;

		r1 = Gradient.LinearRGBtoSRGB[r1];
		g1 = Gradient.LinearRGBtoSRGB[g1];
		b1 = Gradient.LinearRGBtoSRGB[b1];

		return ((a1 << 24) | (r1 << 16) | (g1 << 8) | (b1));
	}

	protected final int gradients(float position) {
		if (cycleMethod == CycleMethod.NO_CYCLE) {
			if (position > 1) {
				position = 1;
			} else if (position < 0) {
				position = 0;
			}
		} else if (cycleMethod == CycleMethod.REPEAT) {
			position = position - (int) position;
			if (position < 0) {
				position = position + 1;
			}
		} else {
			if (position < 0) {
				position = -position;
			}

			int part = (int) position;

			position = position - part;

			if ((part & 1) == 1) {
				position = 1 - position;
			}
		}

		if (isLookup) {
			return gradient[(int) (position * fastGradientArraySize)];
		} else {
			for (int i = 0; i < gradients.length; i++) {
				if (position < fractions[i + 1]) {
					float delta = position - fractions[i];

					int index = (int) ((delta / normalizedIntervals[i]) * (GRADIENT_SIZE_INDEX));

					return gradients[i][index];
				}
			}
		}

		return gradients[gradients.length - 1][GRADIENT_SIZE_INDEX];
	}

	private final static int toLinearRGB(int color) {
		float input, output;
		input = color / 255f;
		if (input <= 0.04045f) {
			output = input / 12.92f;
		} else {
			output = MathUtils.pow((input + 0.055f) / 1.055f, 2.4f);
		}
		return MathUtils.round(output * 255f);
	}

	private final static int toSRGB(int color) {
		float input, output;
		input = color / 255f;
		if (input <= 0.0031308) {
			output = input * 12.92f;
		} else {
			output = (1.055f * (MathUtils.pow(input, (1.0f / 2.4f)))) - 0.055f;
		}
		return MathUtils.round(output * 255f);
	}

	public Affine2f getAffine() {
		return affine;
	}

	public abstract void fill(int[] pixels, int off, int x, int y, int w, int h);

	public abstract void fill(int[] pixels, int off, int adjust, int x, int y, int w, int h);
}
