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

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import loon.canvas.Gradient;

class Lwjgl3Gradient extends Gradient {

	static Lwjgl3Gradient create(Linear cfg) {
		Point2D.Float start = new Point2D.Float(cfg.x0, cfg.y0);
		Point2D.Float end = new Point2D.Float(cfg.x1, cfg.y1);
		Color[] javaColors = convertColors(cfg.colors);
		return new Lwjgl3Gradient(new LinearGradientPaint(start, end,
				cfg.positions, javaColors));
	}

	static Lwjgl3Gradient create(Radial cfg) {
		Point2D.Float center = new Point2D.Float(cfg.x, cfg.y);
		Color[] javaColors = convertColors(cfg.colors);
		return new Lwjgl3Gradient(new RadialGradientPaint(center, cfg.r,
				cfg.positions, javaColors));
	}

	private static Color[] convertColors(int[] colors) {
		Color[] javaColors = new Color[colors.length];
		for (int i = 0; i < colors.length; ++i) {
			javaColors[i] = new Color(colors[i], true);
		}
		return javaColors;
	}

	Paint paint;

	private Lwjgl3Gradient(Paint paint) {
		this.paint = paint;
	}
}
