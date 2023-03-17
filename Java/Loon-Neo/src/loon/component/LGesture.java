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
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.events.SysTouch;
import loon.geom.Path;
import loon.geom.PointF;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.GestureData;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.URecognizer;
import loon.utils.URecognizerResult;

/**
 * 0.3.3版新增类,用以进行跨平台手势操作(触屏经过的路径,默认会以指定颜色显示出来轨迹,当然也可以隐藏轨迹,仅仅获得经过的路径)
 */
public class LGesture extends LComponent {

	private float mX;
	private float mY;

	private float curveEndX;
	private float curveEndY;

	private boolean resetGesture;

	private boolean autoClear;

	private Path goalPath;

	private int lineWidth;

	public LGesture(int x, int y, int w, int h, boolean c) {
		this(x, y, w, h, c, LColor.orange);
	}

	public LGesture(int x, int y, int w, int h, boolean c, LColor col) {
		super(x, y, w, h);
		this._drawBackground = false;
		this._component_baseColor = col;
		this.autoClear = c;
		this.lineWidth = 5;
	}

	public LGesture(int x, int y, int w, int h) {
		this(x, y, w, h, true);
	}

	public LGesture() {
		this(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), true);
	}

	public LGesture(boolean flag) {
		this(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), flag);
	}

	@Override
	public void update(long elapsedTime) {
		if (SysTouch.isUp()) {
			if (autoClear) {
				clear();
			}
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (isVisible() && goalPath != null) {
			g.saveBrush();
			int tint = g.getTint();
			g.setLineWidth(lineWidth);
			g.setColor(_component_baseColor);
			g.drawPolyline(goalPath);
			g.resetLineWidth();
			g.setTint(tint);
			g.restoreBrush();
		}
	}

	@Override
	protected void processTouchPressed() {
		final float x = getUITouchX();
		final float y = getUITouchY();
		if (isPointInUI(x, y)) {
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
		}
		super.processTouchPressed();
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (autoClear) {
			clear();
		}
	}

	@Override
	protected void processTouchDragged() {
		if (SysTouch.isDrag() && input.isMoving()) {
			final float x = getUITouchX();
			final float y = getUITouchY();
			if (isPointInUI(x, y)) {
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
			}
		}
		super.processTouchDragged();
	}

	public float[] getPoints() {
		if (goalPath != null) {
			return goalPath.getPoints();
		}
		return null;
	}

	public TArray<PointF> getListPoint() {
		if (goalPath != null) {
			float[] points = goalPath.getPoints();
			int size = points.length;
			TArray<PointF> result = new TArray<PointF>(size);
			for (int i = 0; i < size; i += 2) {
				result.add(new PointF(points[i], points[i + 1]));
			}
			return result;
		}
		return null;
	}

	public TArray<Vector2f> getList() {
		if (goalPath != null) {
			float[] points = goalPath.getPoints();
			int size = points.length;
			TArray<Vector2f> result = new TArray<Vector2f>(size);
			for (int i = 0; i < size; i += 2) {
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
					length += distance(points[0 + i], points[1 + i], points[2 + i], points[3 + i]);
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

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public boolean isAutoClear() {
		return autoClear;
	}

	/**
	 * 不愿意清除绘制的内容时，可以设置此项为false
	 * 
	 * @param autoClear
	 */
	public void setAutoClear(boolean autoClear) {
		this.autoClear = autoClear;
	}

	/**
	 * 获得一个指定数据的手势分析器
	 * 
	 * @param type
	 * @return
	 */
	public URecognizerResult getRecognizer(GestureData data, int type) {
		URecognizer analyze = new URecognizer(data, type);
		if (goalPath != null) {
			float[] points = goalPath.getPoints();
			int size = points.length;
			TArray<PointF> v = new TArray<PointF>();
			for (int i = 0; i < size; i += 2) {
				v.add(new PointF(points[i], points[i + 1]));
			}
			return analyze.getRecognize(v);
		}
		return new URecognizerResult();
	}

	/**
	 * 获得一个指定数据的手势分析器
	 * 
	 * @param data
	 * @return
	 */
	public URecognizerResult getRecognizer(GestureData data) {
		return getRecognizer(data, URecognizer.GESTURES_DEFAULT);
	}

	/**
	 * 使用指定文件中的采样数据分析手势
	 * 
	 * @param path
	 * @param resampledFirst
	 * @return
	 */
	public URecognizerResult getRecognizer(String path, boolean resampledFirst) {
		return getRecognizer(GestureData.loadUserPoints(path, resampledFirst), URecognizer.GESTURES_NONE);
	}

	/**
	 * 获得一个手势分析器
	 * 
	 * @return
	 */
	public URecognizerResult getRecognizer() {
		return getRecognizer(new GestureData(), URecognizer.GESTURES_DEFAULT);
	}

	@Override
	public String getUIName() {
		return "Gesture";
	}

	@Override
	public void destory() {

	}

}
