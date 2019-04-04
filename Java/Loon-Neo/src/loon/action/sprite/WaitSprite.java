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
package loon.action.sprite;

import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 各种等待特效的合集,各种圈和线条滴溜溜的转……
 */
public class WaitSprite extends Entity {

	private final class DrawWait {

		private final float sx = 1.0f, sy = 1.0f;

		private final int ANGLE_STEP = 15;

		private final int ARCRADIUS = 120;

		private LColor color;

		private double r;

		private TArray<RectBox> list;

		int width, height;

		private int angle;

		private int style;

		private int paintX, paintY, paintWidth, paintHeight;

		private LColor fill;

		public DrawWait(int s, int width, int height) {
			this.style = s;
			this.width = width;
			this.height = height;
			this.color = new LColor(LColor.white);
			switch (style) {
			case 0:
				int r1 = width / 8, r2 = height / 8;
				this.r = (r1 < r2 ? r1 : r2) / 2;
				this.list = new TArray<RectBox>(new RectBox[] { new RectBox(sx + 3 * r, sy + 0 * r, 2 * r, 2 * r),
						new RectBox(sx + 5 * r, sy + 1 * r, 2 * r, 2 * r),
						new RectBox(sx + 6 * r, sy + 3 * r, 2 * r, 2 * r),
						new RectBox(sx + 5 * r, sy + 5 * r, 2 * r, 2 * r),
						new RectBox(sx + 3 * r, sy + 6 * r, 2 * r, 2 * r),
						new RectBox(sx + 1 * r, sy + 5 * r, 2 * r, 2 * r),
						new RectBox(sx + 0 * r, sy + 3 * r, 2 * r, 2 * r),
						new RectBox(sx + 1 * r, sy + 1 * r, 2 * r, 2 * r) });
				break;
			case 1:
				this.fill = new LColor(165, 0, 0, 255);
				this.paintX = (width - ARCRADIUS);
				this.paintY = (height - ARCRADIUS);
				this.paintWidth = paintX + ARCRADIUS;
				this.paintHeight = paintY + ARCRADIUS;
				break;
			}
		}

		public void next() {
			switch (style) {
			case 0:
				list.add(list.removeIndex(0));
				break;
			case 1:
				angle += ANGLE_STEP;
				break;
			}
		}

		public void draw(GLEx g, float x, float y) {
			LColor oldColor = g.getColor();
			g.setColor(color);
			switch (style) {
			case 0:
				float _alpha = 0.0f;
				float nx = x + width / 2 - (int) r * 4;
				float ny = y + height / 2 - (int) r * 4;
				for (RectBox s : list) {
					_alpha = _alpha + 0.1f;
					g.setAlpha(_alpha);
					g.fillOval(nx + s.x, ny + s.y, s.width, s.height);
				}
				g.setAlpha(1.0F);
				break;
			case 1:
				int old = g.getBlendMode();
				g.setBlendMode(LSystem.MODE_NORMAL);
				g.setLineWidth(10);
				g.setColor(fill);
				g.drawOval(x, y, width, height);
				int sa = angle % 360;
				g.fillArc(x + (width - paintWidth) / 2, y + (height - paintHeight) / 2, paintWidth, paintHeight, sa,
						sa + ANGLE_STEP);
				g.resetLineWidth();
				g.setBlendMode(old);
				break;
			}
			g.setColor(oldColor);
		}
	}

	private LTimer delay;

	private DrawWait wait;

	private int style;

	private Cycle cycle;

	public WaitSprite(int s) {
		this(s, LSystem.landscape() ? 360 : 300, 300);
	}

	public WaitSprite(int s, int w, int h) {
		this.style = s;
		this.wait = new DrawWait(s, w, h);
		this.delay = new LTimer(120);
		this.setRepaint(true);
		this.setSize(w, h);
		if (s > 1) {
			int idx = s - 2;
			int width = w / 2;
			int height = h / 2;
			cycle = newSample(idx, width, height);
			cycle.setLocation((w - cycle.getWidth()) / 2, (h - cycle.getHeight()) / 2);
		}
		setLocation((LSystem.viewSize.getWidth() - w) / 2, (LSystem.viewSize.getHeight() - h) / 2);
		update(0);
	}

	private final static Cycle newSample(int type, float srcWidth, float srcHeight) {
		float width = 1;
		float height = 1;
		float offset = 0;
		int padding = 0;
		type = MathUtils.max(0, MathUtils.min(type, 7));
		switch (type) {
		case 0:
			width = 100;
			height = 40;
			if (srcWidth < srcHeight) {
				offset = 0;
			} else {
				offset = 8;
			}
			break;
		case 1:
			width = 30;
			height = 30;
			if (srcWidth < srcHeight) {
				offset = 0;
			} else {
				offset = 6;
			}
			break;
		case 2:
			width = 80;
			height = 80;
			offset = 14;
			padding = -15;
			break;
		case 3:
			width = 100;
			height = 100;
			if (srcWidth < srcHeight) {
				offset = -4;
			}
			break;
		case 4:
			width = 60;
			height = 60;
			offset = 12;
			padding = -60;
			break;
		case 5:
			width = 70;
			height = 80;
			offset = 12;
			if (srcWidth < srcHeight) {
				padding = -80;
			} else {
				padding = -30;
			}
			break;
		case 6:
			width = 60;
			height = 60;
			offset = 12;
			if (srcWidth < srcHeight) {
				padding = -60;
			} else {
				padding = -60;
			}
			break;
		case 7:
			width = 80;
			height = 80;
			offset = -2;
			padding = -20;
			break;
		}
		return Cycle.getSample(type, srcWidth, srcHeight, width, height, offset, padding);
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (style < 2) {
			wait.draw(g, drawX(offsetX), drawY(offsetY));
		} else if (cycle != null) {
			cycle.createUI(g, drawX(offsetX), drawY(offsetY));
		}

	}

	@Override
	public float getHeight() {
		if (cycle != null) {
			return cycle.getCollisionBox().height;
		} else {
			return wait.height;
		}
	}

	@Override
	public float getWidth() {
		if (cycle != null) {
			return cycle.getCollisionBox().width;
		} else {
			return wait.width;
		}
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (cycle != null) {
			cycle.update(elapsedTime);
		} else {
			if (delay.action(elapsedTime)) {
				wait.next();
			}
		}
	}

	@Override
	public void setAlpha(float a) {
		if (cycle != null) {
			cycle.setAlpha(a);
		} else {
			super.setAlpha(a);
		}
	}

	@Override
	public float getAlpha() {
		if (cycle != null) {
			return cycle.getAlpha();
		} else {
			return super.getAlpha();
		}
	}

	@Override
	public RectBox getCollisionBox() {
		if (cycle != null) {
			return cycle.getCollisionBox();
		} else {
			return super.getCollisionBox();
		}
	}

	@Override
	public boolean isVisible() {
		return cycle != null ? cycle.isVisible() : super.isVisible();
	}

	@Override
	public void setVisible(boolean pVisible) {
		super.setVisible(pVisible);
		if (cycle != null) {
			cycle.setVisible(pVisible);
		}
	}

}
