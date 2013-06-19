/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.graphics.component;

import java.util.ArrayList;

import loon.core.LSystem;
import loon.core.geom.Path;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LComponent;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LInputFactory.Touch;
import loon.utils.MathUtils;


//0.3.3版新增类,用以进行跨平台手势操作。
public class LGesture extends LComponent {

	private float mX;
	private float mY;

	private float curveEndX;
	private float curveEndY;

	private boolean resetGesture;

	private boolean autoClear;

	private Path goalPath;

	private LColor color = LColor.orange;

	private int lineWidth;

	public LGesture(int x, int y, int w, int h, boolean c) {
		super(x, y, w, h);
		this.autoClear = c;
		this.lineWidth = 5;
	}

	public LGesture(int x, int y, int w, int h) {
		this(x, y, w, h, true);
	}

	public LGesture() {
		this(0, 0, LSystem.screenRect.width, LSystem.screenRect.height, true);
	}

	public LGesture(boolean flag) {
		this(0, 0, LSystem.screenRect.width, LSystem.screenRect.height, flag);
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (visible && goalPath != null) {
			g.setLineWidth(lineWidth);
			g.setColor(color);
			g.draw(goalPath);
			g.resetLineWidth();
			g.resetColor();
		}
	}

	@Override
	protected void processTouchPressed() {
		final int x = Touch.x();
		final int y = Touch.y();
		if (getCollisionBox().contains(x, y)) {
			mX = x;
			mY = y;
			if (resetGesture) {
				resetGesture = false;
				if (goalPath != null) {
					goalPath.clear();
				}
			}
			if (goalPath == null) {
				goalPath = new Path(x, y);

			} else {
				goalPath.set(x, y);
			}
			curveEndX = x;
			curveEndY = y;
			downClick();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (autoClear) {
			clear();
		}
		upClick();
	}

	@Override
	protected void processTouchDragged() {
		if (input.isMoving()) {
			final int x = Touch.x();
			final int y = Touch.y();
			if (getCollisionBox().contains(x, y)) {
				final float previousX = mX;
				final float previousY = mY;

				final float dx = MathUtils.abs(x - previousX);
				final float dy = MathUtils.abs(y - previousY);

				if (dx >= 3 || dy >= 3) {
					float cX = curveEndX = (x + previousX) / 2;
					float cY = curveEndY = (y + previousY) / 2;
					if (goalPath != null) {
						goalPath.quadTo(previousX, previousY, cX, cY);
					}
					mX = x;
					mY = y;
				}
				dragClick();
			}
		}
	}

	public float[] getPoints() {
		if (goalPath != null) {
			return goalPath.getPoints();
		}
		return null;
	}

	public ArrayList<Vector2f> getList() {
		if (goalPath != null) {
			float[] points = goalPath.getPoints();
			int size = points.length;
			ArrayList<Vector2f> result = new ArrayList<Vector2f>(size);
			for (int i = 0; i < size; i++) {
				result.add(new Vector2f(points[i], points[i + 1]));
			}
			return result;
		}
		return null;
	}

	private final static float distance(float x1, float y1, float x2, float y2) {
		float deltaX = x1 - x2;
		float deltaY = y1 - y2;
		return MathUtils.sqrt(deltaX * deltaX + deltaY * deltaY);
	}

	public float getLength() {
		if (goalPath != null) {
			float length = 0;
			float[] points = goalPath.getPoints();
			int size = points.length;
			for (int i = 0; i < size;) {
				if (i < size - 3) {
					length += distance(points[0 + i], points[1 + i],
							points[2 + i], points[3 + i]);
				}
				i += 4;
			}
			return length;
		}
		return 0;
	}

	public float[] getCenter() {
		if (goalPath != null) {
			return goalPath.getCenter();
		}
		return new float[] { 0, 0 };
	}

	public void dragClick() {
		if (Click != null) {
			Click.DragClick(this, input.getTouchX(), input.getTouchY());
		}
	}

	public void downClick() {
		if (Click != null) {
			Click.DownClick(this, input.getTouchX(), input.getTouchY());
		}
	}

	public void upClick() {
		if (Click != null) {
			Click.UpClick(this, input.getTouchX(), input.getTouchY());
		}
	}

	public void clear() {
		if (goalPath != null) {
			goalPath.clear();
		}
	}

	public float getCurveEndX() {
		return curveEndX;
	}

	public void setCurveEndX(float curveEndX) {
		this.curveEndX = curveEndX;
	}

	public float getCurveEndY() {
		return curveEndY;
	}

	public void setCurveEndY(float curveEndY) {
		this.curveEndY = curveEndY;
	}

	public Path getPath() {
		return goalPath;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public boolean isAutoClear() {
		return autoClear;
	}

	public void setAutoClear(boolean autoClear) {
		this.autoClear = autoClear;
	}

	@Override
	public String getUIName() {
		return "Gesture";
	}

}
