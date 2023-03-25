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

public abstract class Gradient {

	private static final float[] GRADIENT_FRACTIONS = new float[] { 0f, 1f };

	public static abstract class Config {
		
		public final int[] colors;
		public final float[] positions;

		protected Config(int[] colors, float[] positions) {
			this.colors = colors;
			this.positions = positions;
		}
	}

	public static class Linear extends Config {
		
		public final float x0, y0, x1, y1;

		public Linear(float x0, float y0, float x1, float y1, LColor start, LColor end) {
			this(x0, y0, x1, y1, new int[] { start.getRGB(), end.getRGB() }, GRADIENT_FRACTIONS);
		}

		public Linear(float x0, float y0, float x1, float y1, LColor[] colors) {
			this(x0, y0, x1, y1, LColor.toRgbInt(colors), GRADIENT_FRACTIONS);
		}

		public Linear(float x0, float y0, float x1, float y1, LColor[] colors, float[] positions) {
			this(x0, y0, x1, y1, LColor.toRgbInt(colors), positions);
		}

		public Linear(float x0, float y0, float x1, float y1, int[] colors, float[] positions) {
			super(colors, positions);
			this.x0 = x0;
			this.y0 = y0;
			this.x1 = x1;
			this.y1 = y1;
		}
	}

	public static class Radial extends Config {
		
		public final float x, y, r;

		public Radial(float x, float y, float r, LColor start, LColor end) {
			this(x, y, r, new int[] { start.getRGB(), end.getRGB() }, GRADIENT_FRACTIONS);
		}

		public Radial(float x, float y, float r, LColor[] colors) {
			this(x, y, r, LColor.toRgbInt(colors), GRADIENT_FRACTIONS);
		}

		public Radial(float x, float y, float r, LColor[] colors, float[] positions) {
			super(LColor.toRgbInt(colors), positions);
			this.x = x;
			this.y = y;
			this.r = r;
		}

		public Radial(float x, float y, float r, int[] colors, float[] positions) {
			super(colors, positions);
			this.x = x;
			this.y = y;
			this.r = r;
		}
	}
}
