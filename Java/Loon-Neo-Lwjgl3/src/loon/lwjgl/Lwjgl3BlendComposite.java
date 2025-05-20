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
package loon.lwjgl;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class Lwjgl3BlendComposite implements Composite {

	public static final Lwjgl3BlendComposite Multiply = new Lwjgl3BlendComposite(new Blender() {
		@Override
		protected int blend(int srcA, int srcR, int srcG, int srcB, int dstA, int dstR, int dstG, int dstB,
				float alpha) {
			return compose(srcA + dstA - (srcA * dstA) / 255, (srcR * dstR) >> 8, (srcG * dstG) >> 8,
					(srcB * dstB) >> 8, dstA, dstR, dstG, dstB, alpha);
		}
	});

	private final Blender blender;
	private final float alpha;

	private final CompositeContext context = new CompositeContext() {
		@Override
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			int width = Math.min(src.getWidth(), dstIn.getWidth());
			int height = Math.min(src.getHeight(), dstIn.getHeight());
			int[] srcPixels = new int[width], dstPixels = new int[width];
			for (int yy = 0; yy < height; yy++) {
				src.getDataElements(0, yy, width, 1, srcPixels);
				dstIn.getDataElements(0, yy, width, 1, dstPixels);
				blender.blend(srcPixels, dstPixels, width, alpha);
				dstOut.setDataElements(0, yy, width, 1, dstPixels);
			}
		}

		@Override
		public void dispose() {

		}
	};

	public Lwjgl3BlendComposite derive(float alpha) {
		return (alpha == this.alpha) ? this : new Lwjgl3BlendComposite(blender, alpha);
	}

	@Override
	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
		return context;
	}

	protected Lwjgl3BlendComposite(Blender blender) {
		this(blender, 1f);
	}

	protected Lwjgl3BlendComposite(Blender blender, float alpha) {
		this.blender = blender;
		this.alpha = alpha;
	}

	protected static abstract class Blender {
		public void blend(int[] srcPixels, int[] dstPixels, int width, float alpha) {
			for (int xx = 0; xx < width; xx++) {
				int srcARGB = srcPixels[xx], dstARGB = dstPixels[xx];
				int srcA = (srcARGB >> 24) & 0xFF, dstA = (dstARGB >> 24) & 0xFF;
				int srcR = (srcARGB >> 16) & 0xFF, dstR = (dstARGB >> 16) & 0xFF;
				int srcG = (srcARGB >> 8) & 0xFF, dstG = (dstARGB >> 8) & 0xFF;
				int srcB = (srcARGB) & 0xFF, dstB = (dstARGB) & 0xFF;
				dstPixels[xx] = blend(srcA, srcR, srcG, srcB, dstA, dstR, dstG, dstB, alpha);
			}
		}

		protected abstract int blend(int srcA, int srcR, int srcG, int srcB, int dstA, int dstR, int dstG, int dstB,
				float alpha);

		protected int compose(int a, int r, int g, int b, int dstA, int dstR, int dstG, int dstB, float alpha) {
			return ((0xFF & (int) (dstA + (a - dstA) * alpha)) << 24 | (0xFF & (int) (dstR + (r - dstR) * alpha)) << 16
					| (0xFF & (int) (dstG + (g - dstG) * alpha)) << 8 | (0xFF & (int) (dstB + (b - dstB) * alpha)));
		}
	}
}
