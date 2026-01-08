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

import loon.utils.MathUtils;

public class PixmapGradientPaint {

	public static enum GradientType {
		LINEAR, RADIAL, ELLIPTICAL, CONICAL
	}

	public static enum CycleMethod {
		NO_CYCLE, REPEAT, REFLECT
	}

	private final Pixmap _source;
	private final Pixmap _mask;

	public PixmapGradientPaint(Pixmap src, GradientType type, float x1, float y1, float x2, float y2, float radiusX,
			float radiusY, int[] colors, CycleMethod cycleMethod, Pixmap mask) {
		_source = src;
		_mask = mask;
		render(type, x1, y1, x2, y2, radiusX, radiusY, colors, cycleMethod);
	}

	public static float applyCycleMethod(float t, CycleMethod method) {
		switch (method) {
		case NO_CYCLE:
			return MathUtils.max(0, MathUtils.min(1, t));
		case REPEAT:
			return t - MathUtils.floor(t);
		case REFLECT:
			t = t % 2f;
			if (t < 0)
				t += 2f;
			return t <= 1f ? t : 2f - t;
		default:
			return t;
		}
	}

	private void render(GradientType type, float x1, float y1, float x2, float y2, float radiusX, float radiusY,
			int[] colors, CycleMethod cycleMethod) {
		switch (type) {
		case LINEAR:
			renderLinear(x1, y1, x2, y2, colors, cycleMethod);
			break;
		case RADIAL:
			renderRadial(x1, y1, radiusX, colors, cycleMethod);
			break;
		case ELLIPTICAL:
			renderElliptical(x1, y1, radiusX, radiusY, colors, cycleMethod);
			break;
		case CONICAL:
			renderConical(x1, y1, colors, cycleMethod);
			break;
		}
	}

	private void renderLinear(float x1, float y1, float x2, float y2, int[] colors, CycleMethod cycleMethod) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float lenSq = dx * dx + dy * dy;
		for (int y = 0; y < _source.getHeight(); y++) {
			for (int x = 0; x < _source.getWidth(); x++) {
				float t = ((x - x1) * dx + (y - y1) * dy) / lenSq;
				t = applyCycleMethod(t, cycleMethod);
				applyColorStops(x, y, t, colors);
			}
		}
	}

	private void renderRadial(float cx, float cy, float radius, int[] colors, CycleMethod cycleMethod) {
		for (int y = 0; y < _source.getHeight(); y++) {
			for (int x = 0; x < _source.getWidth(); x++) {
				float dx = x - cx;
				float dy = y - cy;
				float dist = MathUtils.sqrt(dx * dx + dy * dy) / radius;
				float t = applyCycleMethod(dist, cycleMethod);
				applyColorStops(x, y, t, colors);
			}
		}
	}

	private void renderElliptical(float cx, float cy, float radiusX, float radiusY, int[] colors,
			CycleMethod cycleMethod) {
		for (int y = 0; y < _source.getHeight(); y++) {
			for (int x = 0; x < _source.getWidth(); x++) {
				float dx = (x - cx) / radiusX;
				float dy = (y - cy) / radiusY;
				float dist = MathUtils.sqrt(dx * dx + dy * dy);
				float t = applyCycleMethod(dist, cycleMethod);
				applyColorStops(x, y, t, colors);
			}
		}
	}

	private void renderConical(float cx, float cy, int[] colors, CycleMethod cycleMethod) {
		for (int y = 0; y < _source.getHeight(); y++) {
			for (int x = 0; x < _source.getWidth(); x++) {
				float dx = x - cx;
				float dy = y - cy;
				float angle = MathUtils.atan2(dy, dx);
				float t = (angle + MathUtils.PI) / (2f * MathUtils.PI);
				t = applyCycleMethod(t, cycleMethod);
				applyColorStops(x, y, t, colors);
			}
		}
	}

	private void applyColorStops(int x, int y, float t, int[] colors) {
		float scaled = t * (colors.length - 1);
		int idx = MathUtils.floor(scaled);
		float result = scaled - idx;
		int c1 = colors[idx];
		int c2 = colors[MathUtils.min(idx + 1, colors.length - 1)];
		int color = LColor.interpolate(c1, c2, result);
		if (_mask != null) {
			int maskPixel = _mask.getPixel(x, y);
			int maskAlpha = (maskPixel >> 24) & 0xFF;
			int gradAlpha = (color >> 24) & 0xFF;
			int finalAlpha = (gradAlpha * maskAlpha) / 255;
			color = (finalAlpha << 24) | (color & 0x00FFFFFF);
		}
		_source.set(x, y, color);
	}
}
