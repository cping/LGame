package loon.action.sprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import loon.core.LObject;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.LTimer;


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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class WaitSprite extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final class DrawWait {

		private final float sx = 1.0f, sy = 1.0f;

		private final int ANGLE_STEP = 15;

		private final int ARCRADIUS = 120;

		private LColor color;

		private double r;

		private ArrayList<Object> list;

		int width, height;

		private int angle;

		private int style;

		private int paintX, paintY, paintWidth, paintHeight;

		private LColor fill;

		public DrawWait(int s, int width, int height) {
			this.style = s;
			this.width = width;
			this.height = height;
			this.color = new LColor(0.0f, 0.0f, 0.0f);
			switch (style) {
			case 0:
				int r1 = width / 8,
				r2 = height / 8;
				this.r = (r1 < r2 ? r1 : r2) / 2;
				this.list = new ArrayList<Object>(Arrays.asList(new Object[] {
						new RectBox(sx + 3 * r, sy + 0 * r, 2 * r, 2 * r),
						new RectBox(sx + 5 * r, sy + 1 * r, 2 * r, 2 * r),
						new RectBox(sx + 6 * r, sy + 3 * r, 2 * r, 2 * r),
						new RectBox(sx + 5 * r, sy + 5 * r, 2 * r, 2 * r),
						new RectBox(sx + 3 * r, sy + 6 * r, 2 * r, 2 * r),
						new RectBox(sx + 1 * r, sy + 5 * r, 2 * r, 2 * r),
						new RectBox(sx + 0 * r, sy + 3 * r, 2 * r, 2 * r),
						new RectBox(sx + 1 * r, sy + 1 * r, 2 * r, 2 * r) }));
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
				list.add(list.remove(0));
				break;
			case 1:
				angle += ANGLE_STEP;
				break;
			}
		}

		public void draw(GLEx g, int x, int y) {
			LColor oldColor = g.getColor();
			g.setColor(color);
			switch (style) {
			case 0:
				float alpha = 0.0f;
				int nx = x + width / 2 - (int) r * 4,
				ny = y + height / 2 - (int) r * 4;
				g.translate(nx, ny);
				for (Iterator<Object> it = list.iterator(); it.hasNext();) {
					RectBox s = (RectBox) it.next();
					alpha = alpha + 0.1f;
					g.setAlpha(alpha);
					g.fillOval(s.x, s.y, s.width, s.height);
				}
				g.setAlpha(1.0F);
				g.translate(-nx, -ny);
				break;
			case 1:
				int old = g.getBlendMode();
				g.setBlendMode(GL.MODE_SPEED);
				g.setLineWidth(10);
				g.translate(x, y);
				g.setColor(fill);
				g.drawOval(0, 0, width, height);
				int sa = angle % 360;
				g.fillArc(x + (width - paintWidth) / 2, y
						+ (height - paintHeight) / 2, paintWidth, paintHeight,
						sa, sa + ANGLE_STEP);
				g.translate(-x, -y);
				g.resetLineWidth();
				g.setBlendMode(old);
				break;
			}
			g.setColor(oldColor);
		}
	}

	private LTimer delay;

	private boolean visible;

	private DrawWait wait;

	private int style;

	private Cycle cycle;

	public WaitSprite(int s) {
		this(s, LSystem.screenRect.width, LSystem.screenRect.height);
	}

	public WaitSprite(int s, int w, int h) {
		this.style = s;
		this.wait = new DrawWait(s, w, h);
		this.delay = new LTimer(120);
		this.alpha = 1.0F;
		this.visible = true;
		if (s > 1) {
			int width = w / 2;
			int height = h / 2;
			cycle = newSample(s - 2, width, height);
			RectBox limit = cycle.getCollisionBox();
			setLocation(
					(w - (limit.getWidth() == 0 ? 20 : limit.getWidth())) / 2,
					(h - (limit.getHeight() == 0 ? 20 : limit.getHeight())) / 2);
		}
		update(0);
	}

	private final static Cycle newSample(int type, float srcWidth,
			float srcHeight) {
		float width = 1;
		float height = 1;
		float offset = 0;
		int padding = 0;
		switch (type) {
		case 0:
			offset = 12;
			if (srcWidth < srcHeight) {
				width = 60;
				height = 60;
				padding = -35;
			} else {
				width = 100;
				height = 100;
				padding = -35;
			}
			break;
		case 1:
			width = 100;
			height = 40;
			if (srcWidth < srcHeight) {
				offset = 0;
			} else {
				offset = 8;
			}
			break;
		case 2:
			width = 30;
			height = 30;
			if (srcWidth < srcHeight) {
				offset = 0;
			} else {
				offset = 6;
			}
			break;
		case 3:
			width = 100;
			height = 100;
			padding = -30;
			break;
		case 4:
			width = 80;
			height = 80;
			offset = 14;
			padding = -15;
			break;
		case 5:
			width = 100;
			height = 100;
			if (srcWidth < srcHeight) {
				offset = -4;
			}
			break;
		case 6:
			width = 60;
			height = 60;
			offset = 12;
			if (srcWidth < srcHeight) {
				padding = -60;
			} else {
				padding = -80;
			}
			break;
		case 7:
			width = 60;
			height = 60;
			offset = 12;
			if (srcWidth < srcHeight) {
				padding = -80;
			} else {
				padding = -120;
			}
			break;
		case 8:
			width = 60;
			height = 60;
			offset = 12;
			if (srcWidth < srcHeight) {
				padding = -60;
			} else {
				padding = -80;
			}
			break;
		case 9:
			width = 80;
			height = 80;
			if (srcWidth < srcHeight) {
				offset = -2;
				padding = -20;
			} else {
				padding = -30;
			}
			break;
		}
		return Cycle.getSample(type, srcWidth, srcHeight, width, height,
				offset, padding);
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (style < 2) {
			if (alpha > 0.1 && alpha < 1.0) {
				g.setAlpha(alpha);
				wait.draw(g, x(), y());
				g.setAlpha(1.0F);
			} else {
				wait.draw(g, x(), y());
			}
		} else {
			if (cycle != null) {
				cycle.createUI(g);
			}
		}
	}

	@Override
	public int getHeight() {
		if (cycle != null) {
			return cycle.getCollisionBox().height;
		} else {
			return wait.height;
		}
	}

	@Override
	public int getWidth() {
		if (cycle != null) {
			return cycle.getCollisionBox().width;
		} else {
			return wait.width;
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		if (cycle != null) {
			if (cycle.x() != x() || cycle.y() != y()) {
				cycle.setLocation(x(), y());
			}
			cycle.update(elapsedTime);
		} else {
			if (delay.action(elapsedTime)) {
				wait.next();
			}
		}
	}

	@Override
	public void setAlpha(float alpha) {
		if (cycle != null) {
			cycle.setAlpha(alpha);
		} else {
			this.alpha = alpha;
		}
	}

	@Override
	public float getAlpha() {
		if (cycle != null) {
			return cycle.getAlpha();
		} else {
			return alpha;
		}
	}

	@Override
	public RectBox getCollisionBox() {
		if (cycle != null) {
			return cycle.getCollisionBox();
		} else {
			return getRect(x(), y(), getWidth(), getHeight());
		}
	}

	@Override
	public boolean isVisible() {
		return cycle != null ? cycle.isVisible() : visible;
	}

	@Override
	public void setVisible(boolean visible) {
		if (cycle != null) {
			cycle.setVisible(visible);
		} else {
			this.visible = visible;
		}
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public void dispose() {

	}

}
