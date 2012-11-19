package org.loon.framework.android.game.action.sprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.loon.framework.android.game.core.geom.Ellipse2D;
import org.loon.framework.android.game.core.geom.Shape;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.device.LGraphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;

/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.2
 */
public class WaitAnimation {

	private final LColor defaultBlackColor;

	private final LColor defaultWhiteColor;

	private final double sx = 1.0D, sy = 1.0D;

	private final int ANGLE_STEP = 15;

	private final int ARCRADIUS = 120;

	private LColor color;

	private double r;

	private List<Object> list;

	private boolean isRunning = false;

	private Paint border;

	private RectF arcRect;

	private Paint fill;

	private int width, height;

	private int angle;

	private int style;

	public WaitAnimation(int s, int width, int height) {
		this.style = s;
		this.width = width;
		this.height = height;
		this.defaultBlackColor = new LColor(0.5f, 0.5f, 0.5f);
		this.defaultWhiteColor = new LColor(240, 240, 240);
		this.color = defaultBlackColor;
		switch (style) {
		case 0:
			int r1 = width / 8,
			r2 = height / 8;
			this.r = (r1 < r2 ? r1 : r2) / 2;
			this.list = new ArrayList<Object>(Arrays
					.asList(new Object[] {
							new Ellipse2D(sx + 3 * r, sy + 0 * r, 2 * r,
									2 * r),
							new Ellipse2D(sx + 5 * r, sy + 1 * r, 2 * r,
									2 * r),
							new Ellipse2D(sx + 6 * r, sy + 3 * r, 2 * r,
									2 * r),
							new Ellipse2D(sx + 5 * r, sy + 5 * r, 2 * r,
									2 * r),
							new Ellipse2D(sx + 3 * r, sy + 6 * r, 2 * r,
									2 * r),
							new Ellipse2D(sx + 1 * r, sy + 5 * r, 2 * r,
									2 * r),
							new Ellipse2D(sx + 0 * r, sy + 3 * r, 2 * r,
									2 * r),
							new Ellipse2D(sx + 1 * r, sy + 1 * r, 2 * r,
									2 * r) }));
			break;
		case 1:
			this.border = new Paint();
			this.border.setColor(Color.WHITE);
			this.border.setAntiAlias(true);
			this.border.setStyle(Style.STROKE);
			this.border.setStrokeWidth(10);

			this.fill = new Paint();
			this.fill.setARGB(165, 0, 0, 0);

			this.arcRect = new RectF(0, 0, 0, 0);
			arcRect.top = (height - ARCRADIUS) / 2;
			arcRect.left = (width - ARCRADIUS) / 2;
			arcRect.right = arcRect.left + ARCRADIUS;
			arcRect.bottom = arcRect.top + ARCRADIUS;

			RadialGradient shader = new RadialGradient(arcRect.left,
					arcRect.top, ARCRADIUS, Color.TRANSPARENT, border
							.getColor(), TileMode.MIRROR);
			border.setShader(shader);
			break;

		}

	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public void black() {
		this.color = defaultBlackColor;
	}

	public void white() {
		this.color = defaultWhiteColor;
	}

	public void next() {
		if (isRunning) {
			switch (style) {
			case 0:
				list.add(list.remove(0));
				break;
			case 1:
				angle += ANGLE_STEP;
				break;
			}
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void draw(LGraphics g, int x, int y) {
		draw(g, x, y, width, height);
	}

	public void draw(LGraphics g, int x, int y, int w, int h) {
		switch (style) {
		case 0:
			LColor oldColor = g.getColor();
			g.setAntiAlias(true);
			g.setColor(color);
			float alpha = 0.0f;
			int nx = x + w / 2 - (int) r * 4,
			ny = y + h / 2 - (int) r * 4;
			g.translate(nx, ny);
			for (Iterator<Object> it = list.iterator(); it.hasNext();) {
				Shape s = (Shape) it.next();
				alpha = isRunning ? alpha + 0.1f : 0.5f;
				g.setAlpha(alpha);
				g.fill(s);
			}
			g.setAntiAlias(false);
			g.setAlpha(1.0F);
			g.translate(-nx, -ny);
			g.setColor(oldColor);
			break;
		case 1:
			Canvas c = g.getCanvas();
			c.translate(x, y);
			c.drawRect(0, 0, width, height, fill);
			int sa = angle % 360;
			c.drawArc(arcRect, sa, sa + ANGLE_STEP, false, border);
			g.translate(-x, -y);
			break;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
