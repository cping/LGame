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
package loon.se;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import loon.utils.MathUtils;

public class JavaSEBlendComposite implements Composite {

	private final Blender blender;

	private float alpha;

	protected static JavaSEBlendComposite instanceNormal;

	protected static JavaSEBlendComposite instanceLinearLight;

	protected static JavaSEBlendComposite instanceMultiply;

	protected static JavaSEBlendComposite instanceDifference;

	protected static JavaSEBlendComposite instanceAdd;

	protected static JavaSEBlendComposite instanceOverlay;

	public static final JavaSEBlendComposite getNormal() {
		if (instanceNormal == null) {
			instanceNormal = new JavaSEBlendComposite(new Blender() {

				@Override
				protected int blend(int srcA, int srcR, int srcG, int srcB, int dstA, int dstR, int dstG, int dstB,
						float alpha, int blendA, int blendR, int blendG, int blendB) {
					return compose(srcA, srcR, srcG, srcB, dstA, dstR, dstG, dstB, alpha);
				}
			});
		}
		return instanceNormal;
	}

	public static final JavaSEBlendComposite getMultiply() {
		if (instanceMultiply == null) {
			instanceMultiply = new JavaSEBlendComposite(new Blender() {

				@Override
				protected int blend(int srcA, int srcR, int srcG, int srcB, int dstA, int dstR, int dstG, int dstB,
						float alpha, int blendA, int blendR, int blendG, int blendB) {
					srcA = (srcA * blendA) / 255;
					srcR = (srcR * blendR) / 255;
					srcG = (srcG * blendG) / 255;
					srcB = (srcB * blendB) / 255;
					return compose(srcA, srcR, srcG, srcB, dstA, dstR, dstG, dstB, alpha);
				}
			});
		}
		return instanceMultiply;
	}

	public static final JavaSEBlendComposite getAdd() {
		if (instanceAdd == null) {
			instanceAdd = new JavaSEBlendComposite(new Blender() {

				@Override
				protected int blend(int srcA, int srcR, int srcG, int srcB, int dstA, int dstR, int dstG, int dstB,
						float alpha, int blendA, int blendR, int blendG, int blendB) {

					srcA = MathUtils.min(255, srcA + dstA);
					srcR = MathUtils.min(255, srcR + dstR);
					srcG = MathUtils.min(255, srcG + dstG);
					srcB = MathUtils.min(255, srcB + dstB);

					srcA = (srcA * blendA) / 255;
					srcR = (srcR * blendR) / 255;
					srcG = (srcG * blendG) / 255;
					srcB = (srcB * blendB) / 255;

					return compose(srcA, srcR, srcG, srcB, dstA, dstR, dstG, dstB, alpha);
				}
			});
		}
		return instanceAdd;
	}

	public static final JavaSEBlendComposite getLinearLight() {
		if (instanceLinearLight == null) {
			instanceLinearLight = new JavaSEBlendComposite(new Blender() {

				@Override
				protected int blend(int srcA, int srcR, int srcG, int srcB, int dstA, int dstR, int dstG, int dstB,
						float alpha, int blendA, int blendR, int blendG, int blendB) {
					srcA = MathUtils.min(255, srcA + dstA);
					srcR = blendR < 128 ? (dstR + srcR) >> 8 : (dstR + (srcR - 128)) >> 7;
					srcG = blendG < 128 ? (dstG + srcG) >> 8 : (dstG + (srcG - 128)) >> 7;
					srcB = blendB < 128 ? (dstB + srcB) >> 8 : (dstB + (srcB - 128)) >> 7;

					srcA = (srcA * blendA) / 255;
					srcR = (srcR * blendR) / 255;
					srcG = (srcG * blendG) / 255;
					srcB = (srcB * blendB) / 255;

					return compose(srcA, srcR, srcG, srcB, dstA, dstR, dstG, dstB, alpha);
				}
			});
		}
		return instanceLinearLight;
	}

	public static final JavaSEBlendComposite getDifference() {
		if (instanceDifference == null) {
			instanceDifference = new JavaSEBlendComposite(new Blender() {

				@Override
				protected int blend(int srcA, int srcR, int srcG, int srcB, int dstA, int dstR, int dstG, int dstB,
						float alpha, int blendA, int blendR, int blendG, int blendB) {
					srcA = MathUtils.min(255, dstA + srcA);
					srcR = MathUtils.abs(dstR - srcR);
					srcG = MathUtils.abs(dstG - srcG);
					srcB = MathUtils.abs(dstB - srcB);

					return compose(srcA, srcR, srcG, srcB, dstA, dstR, dstG, dstB, alpha);
				}
			});
		}
		return instanceDifference;
	}

	public static final JavaSEBlendComposite getOverlay() {
		if (instanceOverlay == null) {
			instanceOverlay = new JavaSEBlendComposite(new Blender() {

				@Override
				protected int blend(int srcA, int srcR, int srcG, int srcB, int dstA, int dstR, int dstG, int dstB,
						float alpha, int blendA, int blendR, int blendG, int blendB) {
					srcA = MathUtils.min(255, srcA + dstA);
					srcR = blendR < 128 ? (dstR + srcR) >> 7 : 255 - (((255 - dstR) * (255 - srcR)) >> 7);
					srcG = blendG < 128 ? (dstG + srcG) >> 7 : 255 - (((255 - dstG) * (255 - srcG)) >> 7);
					srcB = blendB < 128 ? (dstB + srcB) >> 7 : 255 - (((255 - dstB) * (255 - srcB)) >> 7);

					return compose(srcA, srcR, srcG, srcB, dstA, dstR, dstG, dstB, alpha);
				}
			});
		}
		return instanceOverlay;
	}

	private final CompositeContext context = new CompositeContext() {
		@Override
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			final int width = Math.min(src.getWidth(), dstIn.getWidth());
			final int height = Math.min(src.getHeight(), dstIn.getHeight());
			final int[] srcPixels = new int[width], dstPixels = new int[width];
			for (int yy = 0; yy < height; yy++) {
				src.getDataElements(0, yy, width, 1, srcPixels);
				dstIn.getDataElements(0, yy, width, 1, dstPixels);
				blender.blend(srcPixels, dstPixels, width, alpha, blendA, blendR, blendG, blendB);
				dstOut.setDataElements(0, yy, width, 1, dstPixels);
			}
		}

		@Override
		public void dispose() {

		}
	};

	public JavaSEBlendComposite derive(float alpha) {
		return (alpha == this.alpha) ? this : new JavaSEBlendComposite(blender, alpha);
	}

	@Override
	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
		return context;
	}

	protected JavaSEBlendComposite(Blender blender) {
		this(blender, 1f);
	}

	protected JavaSEBlendComposite(Blender blender, float alpha) {
		this.blender = blender;
		this.alpha = alpha;
		this.blendA = this.blendR = this.blendG = this.blendB = 255;
	}

	protected int blendR, blendG, blendB, blendA;

	public JavaSEBlendComposite setAlpha(float alpha) {
		this.alpha = alpha;
		return this;
	}

	public JavaSEBlendComposite setColor(int r, int g, int b, int a) {
		blendR = r;
		blendG = g;
		blendB = b;
		blendA = a;
		return this;
	}

	public JavaSEBlendComposite setColor(int r, int g, int b) {
		return setColor(r, g, b, 255);
	}

	protected static abstract class Blender {

		public void blend(int[] srcPixels, int[] dstPixels, int width, float alpha, int blendA, int blendR, int blendG,
				int blendB) {
			final int trans = 0;
			for (int xx = 0; xx < width; xx++) {
				int srcARGB = srcPixels[xx], dstARGB = dstPixels[xx];
				if (srcARGB == trans) {
					continue;
				}
				if (dstARGB == trans) {
					continue;
				}
				int srcA = (srcARGB >> 24) & 0xFF, dstA = (dstARGB >> 24) & 0xFF;
				int srcR = (srcARGB >> 16) & 0xFF, dstR = (dstARGB >> 16) & 0xFF;
				int srcG = (srcARGB >> 8) & 0xFF, dstG = (dstARGB >> 8) & 0xFF;
				int srcB = (srcARGB) & 0xFF, dstB = (dstARGB) & 0xFF;
				dstPixels[xx] = blend(srcA, srcR, srcG, srcB, dstA, dstR, dstG, dstB, alpha, blendA, blendR, blendG,
						blendB);
			}
		}

		protected abstract int blend(int srcA, int srcR, int srcG, int srcB, int dstA, int dstR, int dstG, int dstB,
				float alpha, int blendA, int blendR, int blendG, int blendB);

		protected int compose(int a, int r, int g, int b, int dstA, int dstR, int dstG, int dstB, float alpha) {
			return ((0xFF & (int) (dstA + (a - dstA) * alpha)) << 24 | (0xFF & (int) (dstR + (r - dstR) * alpha)) << 16
					| (0xFF & (int) (dstG + (g - dstG) * alpha)) << 8 | (0xFF & (int) (dstB + (b - dstB) * alpha)));
		}
	}
}
