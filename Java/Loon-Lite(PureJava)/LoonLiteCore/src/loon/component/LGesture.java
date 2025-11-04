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

	private float _startX, _startY;

	private float _moveX;
	private float _moveY;

	private float _curveEndX;
	private float _curveEndY;

	private boolean _resetGesture;

	private boolean _autoClear;

	private Path _goalPath;

	private int _lineWidth;

	public LGesture(int x, int y, int w, int h, boolean c) {
		this(x, y, w, h, c, LColor.orange);
	}

	public LGesture(int x, int y, int w, int h, boolean c, LColor col) {
		super(x, y, w, h);
		this._drawBackground = false;
		this._component_baseColor = col;
		this._autoClear = c;
		this._lineWidth = 5;
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
			if (_autoClear) {
				clear();
			}
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (isVisible() && _goalPath != null) {
			g.saveBrush();
			int tint = g.color();
			g.setLineWidth(_lineWidth);
			g.setColor(_component_baseColor);
			g.drawPolyline(_goalPath);
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
			if (_startX == 0 && _startY == 0) {
				_startX = x;
				_startY = y;
			}
			if (!MathUtils.equal(x, _moveX) || !MathUtils.equal(y, _moveY)) {
				_moveX = x;
				_moveY = y;
				if (_resetGesture) {
					_resetGesture = false;
					if (_goalPath != null) {
						_goalPath.clear();
					}
				}
				if (_goalPath == null) {
					_goalPath = new Path(x, y);
				} else {
					_goalPath.set(x, y);
				}
				_curveEndX = x;
				_curveEndY = y;
			}
		}
		super.processTouchPressed();
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (_autoClear) {
			clear();
		}
	}

	@Override
	protected void processTouchDragged() {
		if (SysTouch.isDrag() && _input.isMoving()) {
			final float x = getUITouchX();
			final float y = getUITouchY();
			if (isPointInUI(x, y)) {
				if (!MathUtils.equal(x, _moveX) || !MathUtils.equal(y, _moveY)) {
					final float previousX = _moveX;
					final float previousY = _moveY;

					final float dx = MathUtils.abs(x - previousX);
					final float dy = MathUtils.abs(y - previousY);

					if (dx >= 3 || dy >= 3) {
						float cX = _curveEndX = (x + previousX) / 2;
						float cY = _curveEndY = (y + previousY) / 2;
						if (_goalPath != null) {
							_goalPath.lineTo(previousX, previousY, cX, cY);
						}
						_moveX = x;
						_moveY = y;
					}
				}
			}
		}
		super.processTouchDragged();
	}

	public float[] getPoints() {
		if (_goalPath != null) {
			return _goalPath.getPoints();
		}
		return null;
	}

	public TArray<PointF> getListPoint() {
		if (_goalPath != null) {
			final float[] points = _goalPath.getPoints();
			final int size = points.length;
			final TArray<PointF> result = new TArray<PointF>(size);
			for (int i = 0; i < size; i += 2) {
				result.add(new PointF(points[i], points[i + 1]));
			}
			return result;
		}
		return null;
	}

	public TArray<Vector2f> getList() {
		if (_goalPath != null) {
			final float[] points = _goalPath.getPoints();
			final int size = points.length;
			final TArray<Vector2f> result = new TArray<Vector2f>(size);
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
		if (_goalPath != null) {
			float length = 0;
			final float[] points = _goalPath.getPoints();
			final int size = points.length;
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
		if (_goalPath != null) {
			return _goalPath.getCenter();
		}
		return new float[] { 0, 0 };
	}

	public void clear() {
		if (_goalPath != null) {
			_goalPath.clear();
		}
	}

	public float getCurveEndX() {
		return _curveEndX;
	}

	public void setCurveEndX(float curveEndX) {
		this._curveEndX = curveEndX;
	}

	public float getCurveEndY() {
		return _curveEndY;
	}

	public void setCurveEndY(float curveEndY) {
		this._curveEndY = curveEndY;
	}

	public Path getPath() {
		return _goalPath;
	}

	public int getLineWidth() {
		return _lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this._lineWidth = lineWidth;
	}

	public boolean isAutoClear() {
		return _autoClear;
	}

	/**
	 * 不愿意清除绘制的内容时，可以设置此项为false
	 * 
	 * @param autoClear
	 */
	public void setAutoClear(boolean autoClear) {
		this._autoClear = autoClear;
	}

	/**
	 * 获得一个指定数据的手势分析器
	 * 
	 * @param type
	 * @return
	 */
	public URecognizerResult getRecognizer(GestureData data, int type) {
		final URecognizer analyze = new URecognizer(data, type);
		if (_goalPath != null) {
			final float[] points = _goalPath.getPoints();
			final int size = points.length;
			final TArray<PointF> v = new TArray<PointF>();
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

	public float getStartX() {
		return _startX;
	}

	public float getStartY() {
		return _startY;
	}

	@Override
	public String getUIName() {
		return "Gesture";
	}

	@Override
	public void destory() {

	}

}
