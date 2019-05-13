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
package loon.fx;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import loon.canvas.Gradient;
import loon.canvas.LColor;
import loon.geom.Point;
import loon.utils.MathUtils;

public class JavaFXGradient extends Gradient {

	javafx.scene.paint.Paint fxpaint;

	static JavaFXGradient create(Linear cfg) {
		Color color1 = convertColors(cfg.colors[0]);
		Color color2 = convertColors(cfg.colors[1]);
		Stop[] stops = new Stop[] { new Stop(0, color1), new Stop(1, color2) };
		LinearGradient lg = new LinearGradient(cfg.x0, cfg.y0, cfg.x1, cfg.y1, false, CycleMethod.NO_CYCLE, stops);
		return new JavaFXGradient(lg);
	}

	static JavaFXGradient create(Radial cfg) {
		Point focus = new Point(cfg.x, cfg.y);
		Point center = new Point(cfg.x, cfg.y);
		float focusDistance = focus.distanceTo(center);
		float focusAngle = 0f;
		if (!focus.equals(center)) {
			focusAngle = MathUtils.atan2(focus.getY() - center.getY(), focus.getX() - center.getX());
		}
		Stop[] stops = new Stop[cfg.colors.length];
		for (int i = 0; i < cfg.colors.length; i++) {
			stops[i] = new Stop(i, convertColors(cfg.colors[i]));
		}
		RadialGradient rg = new RadialGradient(MathUtils.toDegrees(focusAngle), focusDistance, cfg.x, cfg.y, cfg.r,
				false, CycleMethod.NO_CYCLE, stops);
		return new JavaFXGradient(rg);
	}

	static Color convertColors(int pixel) {
		LColor color = new LColor(pixel);
		return Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.a);
	}

	static Color[] convertColors(int[] colors) {
		Color[] javaColors = new Color[colors.length];
		for (int i = 0; i < colors.length; ++i) {
			LColor color = new LColor(colors[i]);
			javaColors[i] = Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.a);
		}
		return javaColors;
	}

	private JavaFXGradient(javafx.scene.paint.Paint paint) {
		this.fxpaint = paint;
	}

}
