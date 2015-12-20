package org.loon.framework.javase.game.action.sprite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


import org.loon.framework.javase.game.core.graphics.LColor;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;

/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
public class WaitAnimation {

	private final LColor defaultBlackColor;

	private final LColor defaultWhiteColor;

	private final double sx = 1.0D, sy = 1.0D;

	private final int ANGLE_STEP = 15;

	private final int ARCRADIUS = 120;

	private LColor color;

	private double r;

	private ArrayList<Object> list;

	private boolean isRunning = false;

	private Paint border;

	private LColor fill;

	private int width, height;

	private int angle;

	private int style;

	private int paintX, paintY, paintWidth, paintHeight;

	private Stroke stroke;

	public WaitAnimation(int s, int width, int height) {
		this.style = s;
		this.width = width;
		this.height = height;
		this.defaultBlackColor = new LColor(0.5f, 0.5f, 0.5f);
		this.defaultWhiteColor = new LColor(240, 240, 240);
		this.color = defaultBlackColor;
		this.stroke = new BasicStroke(10.0f);
		switch (style) {
		case 0:
			int r1 = width / 8,
			r2 = height / 8;
			this.r = (r1 < r2 ? r1 : r2) / 2;
			this.list = new ArrayList<Object>(Arrays
					.asList(new Object[] {
							new Ellipse2D.Double(sx + 3 * r, sy + 0 * r, 2 * r,
									2 * r),
							new Ellipse2D.Double(sx + 5 * r, sy + 1 * r, 2 * r,
									2 * r),
							new Ellipse2D.Double(sx + 6 * r, sy + 3 * r, 2 * r,
									2 * r),
							new Ellipse2D.Double(sx + 5 * r, sy + 5 * r, 2 * r,
									2 * r),
							new Ellipse2D.Double(sx + 3 * r, sy + 6 * r, 2 * r,
									2 * r),
							new Ellipse2D.Double(sx + 1 * r, sy + 5 * r, 2 * r,
									2 * r),
							new Ellipse2D.Double(sx + 0 * r, sy + 3 * r, 2 * r,
									2 * r),
							new Ellipse2D.Double(sx + 1 * r, sy + 1 * r, 2 * r,
									2 * r) }));
			break;
		case 1:

			this.paintX = (width - ARCRADIUS) / 2;
			this.paintY = (height - ARCRADIUS) / 2;
			this.paintWidth = paintX + ARCRADIUS;
			this.paintHeight = paintY + ARCRADIUS;
			this.fill = new LColor(165, 0, 0, 0);
			this.border = new RadialGradientPaint(0, 0, ARCRADIUS, new float[] {
					0.0f, 0.6f, 1.0f }, new Color[] { new Color(0, 0, 0, 200),
					Color.WHITE, new Color(255, 255, 255, 200) });
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
			Color oldColor = g.getColor();
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
			g.translate(x, y);
			g.setAntialiasAll(true);
			g.setColor(fill);
			g.drawRect(0, 0, width, height);
			g.setStroke(stroke);
			int sa = angle % 360;
			g.setPaint(border);
			g.drawArc(x + (width - paintWidth) / 2, y + (height - paintHeight)
					/ 2, paintWidth, paintHeight, sa, sa + ANGLE_STEP);
			g.setAntialiasAll(false);
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
