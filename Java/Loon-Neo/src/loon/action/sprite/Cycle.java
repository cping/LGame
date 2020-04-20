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
package loon.action.sprite;

import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.opengl.GLEx;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

public class Cycle extends Entity {

	public final static Cycle getSample(int type, float srcWidth, float srcHeight, float width, float height,
			float offset, int padding) {
		Cycle cycle = new Cycle();
		cycle.setDisplayWidth(width);
		cycle.setDisplayHeight(height);

		final float scale;
		if (srcWidth > srcHeight) {
			scale = MathUtils.max(srcWidth / width, srcHeight / height);
		} else {
			scale = MathUtils.min(srcWidth / width, srcHeight / height);
		}
		switch (type) {
		case 0:
			cycle.setColor(0xFF7B24);
			cycle.setStepsPerFrame(1);
			cycle.setTrailLength(1);
			cycle.setPointDistance(0.10f);
			cycle.setMultiplier(2);
			cycle.addPath(Cycle.ARC, 10 * scale, 10 * scale, 10 * scale, -270, -90);
			cycle.addPath(Cycle.BEZIER, 10 * scale, 0 * scale, 40 * scale, 20 * scale, 20 * scale, 0, 30 * scale,
					20 * scale);
			cycle.addPath(Cycle.ARC, 40 * scale, 10 * scale, 10 * scale, 90, -90);
			cycle.addPath(Cycle.BEZIER, 40 * scale, 0 * scale, 10 * scale, 20 * scale, 30 * scale, 0, 20 * scale,
					20 * scale);
			break;
		case 1:
			cycle.setColor(0xD4FF00);
			cycle.setStepType(1);
			cycle.setDelay(55);
			cycle.setStepsPerFrame(2);
			cycle.setTrailLength(0.3f);
			cycle.setPointDistance(0.1f);
			cycle.addPath(Cycle.LINE, 0, 0, 30 * scale, 0);
			cycle.addPath(Cycle.LINE, 30 * scale, 0 * scale, 30 * scale, 30 * scale);
			cycle.addPath(Cycle.LINE, 30 * scale, 30 * scale, 0, 30 * scale);
			cycle.addPath(Cycle.LINE, 0, 30 * scale, 0, 0);
			break;
		case 2:
			cycle.setColor(0xFFA50000);
			cycle.setStepsPerFrame(1);
			cycle.setTrailLength(1);
			cycle.setPointDistance(0.025f);
			cycle.addPath(Cycle.ARC, 50 * scale, 50 * scale, 40 * scale, 0, 360);
			break;
		case 3:
			cycle.setColor(0xFF2E82);
			cycle.setDelay(60);
			cycle.setStepType(1);
			cycle.setStepsPerFrame(1);
			cycle.setTrailLength(1);
			cycle.setPointDistance(0.1f);
			cycle.addPath(Cycle.LINE, 0, 20 * scale, 100 * scale, 20 * scale);
			cycle.addPath(Cycle.LINE, 100 * scale, 20 * scale, 0, 20 * scale);
			break;
		case 4:
			cycle.setStepsPerFrame(7);
			cycle.setTrailLength(0.7f);
			cycle.setPointDistance(0.01f);
			cycle.setDelay(35);
			cycle.setLineWidth(10);
			cycle.addPath(Cycle.LINE, 20 * scale, 70 * scale, 50 * scale, 20 * scale);
			cycle.addPath(Cycle.LINE, 50 * scale, 20 * scale, 80 * scale, 70 * scale);
			cycle.addPath(Cycle.LINE, 80 * scale, 70 * scale, 20 * scale, 70 * scale);
			break;
		case 5:
			cycle.setColor(0xD4FF00);
			cycle.setStepsPerFrame(3);
			cycle.setTrailLength(1);
			cycle.setPointDistance(0.01f);
			cycle.setLineWidth(6);
			cycle.setPadding(0);
			cycle.addPath(Cycle.ARC, 50 * scale, 50 * scale, 20 * scale, 360, 0);
			break;
		case 6:
			cycle.setColor(0x05E2FF);
			cycle.setStepsPerFrame(1);
			cycle.setTrailLength(1);
			cycle.setPointDistance(0.02f);
			cycle.addPath(Cycle.ARC, 50 * scale, 50 * scale, 30 * scale, 0, 360);
			break;
		case 7:
			cycle.setStepType(1);
			cycle.setColor(0xFF2E82);
			cycle.setStepsPerFrame(2);
			cycle.setTrailLength(0.9f);
			cycle.addPath(Cycle.LINE, 10 * scale, 10 * scale, 90 * scale, 10 * scale);
			cycle.addPath(Cycle.LINE, 90 * scale, 10 * scale, 90 * scale, 90 * scale);
			cycle.addPath(Cycle.LINE, 90 * scale, 90 * scale, 10 * scale, 90 * scale);
			cycle.addPath(Cycle.LINE, 10 * scale, 90 * scale, 10 * scale, 10 * scale);
			break;
		}
		float size = MathUtils.min(srcWidth / (1 / cycle.getPointDistance()),
				srcHeight / (1 / cycle.getPointDistance()));
		cycle.setPadding(padding);
		cycle.setBlockWidth(size + offset);
		cycle.setBlockHeight(size + offset);
		cycle.setWidth(width * scale);
		cycle.setHeight(height * scale);
		return cycle;
	}

	public static final int OTHER = 0, DIM = 1, DEGREE = 2, RADIUS = 3;

	public static final int BEZIER = 0, ARC = 1, LINE = 2;

	protected float pointDistance;

	protected float multiplier;

	protected int frame, padding;

	protected int stepType, lineWidth;

	protected float trailLength, stepsPerFrame;

	protected boolean isUpdate, stopped;

	protected TArray<Object[]> data;

	protected static IntMap<float[]> signatures;

	protected TArray<Progress> points;

	private LTimer timer;

	private Progress last;

	protected float blockWidth, blockHeight, blockHalfWidth, blockHalfHeight;

	protected float displayWidth, displayHeight;

	static class Progress {

		float x;

		float y;

		float progress;

		public Progress(float x, float y, float p) {
			this.x = x;
			this.y = y;
			this.progress = p;
		}
	}

	protected float scaleSize = 1f;

	public Cycle() {
		this(0, 0);
	}

	public Cycle(int x, int y) {
		this(x, y, 6, 6);
	}

	public Cycle(int x, int y, int w, int h) {
		this(null, x, y, w, h);
	}

	public Cycle(TArray<Object[]> path, int x, int y, int w, int h) {

		if (path != null) {
			data.add(path.toArray());
			isUpdate = true;
		} else {
			data = new TArray<Object[]>(10);
		}

		this.setRepaint(true);
		this.setSize(w, h);
		this.setLocation(x, y);
		this.timer = new LTimer(25);
		this.setColor(LColor.white);
		this.points = new TArray<Progress>();
		this.displayWidth = w;
		this.displayHeight = h;
		this.multiplier = 1;
		this.pointDistance = 0.05f;
		this.padding = 0;
		this.stepType = 0;
		this.stepsPerFrame = 1;
		this.trailLength = 1;
		this.blockWidth = w;
		this.blockHeight = h;
		this.blockHalfWidth = w / 2;
		this.blockHalfHeight = h / 2;
		if (signatures == null) {
			signatures = new IntMap<float[]>(3);
			signatures.put(ARC, new float[] { 1, 1, 3, 2, 2, 0 });
			signatures.put(BEZIER, new float[] { 1, 1, 1, 1, 1, 1, 1, 1 });
			signatures.put(LINE, new float[] { 1, 1, 1, 1 });
		}
		this.setup();

	}

	public void play() {
		this.stopped = false;
	}

	public void iterateFrame() {
		this.frame += this.stepsPerFrame;

		if (this.frame >= this.points.size) {
			this.frame = 0;
		}
	}

	public void stop() {
		this.stopped = true;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public void addPath(int type, float... f) {
		Object[] o = new Object[2];
		o[0] = type;
		o[1] = f;
		data.add(o);
		isUpdate = true;
	}

	private void setup() {
		if (!isUpdate) {
			return;
		}
		float[] args;
		float value;
		int index;
		for (Object[] o : data) {
			Integer type = (Integer) o[0];
			args = (float[]) o[1];

			for (int a = -1, al = args.length; ++a < al;) {

				index = (int) signatures.get(type)[a];
				value = args[a];
				switch (index) {
				case RADIUS:
					value *= this.multiplier;
					break;
				case DIM:
					value *= this.multiplier;
					value += this.padding;
					break;
				case DEGREE:
					value *= MathUtils.PI / 180;
					break;
				}

				args[a] = value;
			}
			callMethod(type, args);

		}
		this.isUpdate = false;
	}

	private final void step(GLEx g, Progress e, int index, int frame, LColor color, float alpha, float offsetX,
			float offsetY) {
		switch (stepType) {
		case 0:
			g.fillOval(x() + e.x - blockHalfWidth + offsetX, y() + e.y - blockHalfHeight + offsetY, blockWidth,
					blockHeight);
			break;
		case 1:
			g.fillRect(x() + e.x - blockHalfWidth + offsetX, y() + e.y - blockHalfHeight + offsetY, blockWidth,
					blockHeight);
			break;
		case 2:
			if (last != null) {
				float[] xs = { x() + last.x + offsetX, x() + e.x + offsetX };
				float[] ys = { y() + last.y + offsetY, y() + e.y + offsetY };
				g.drawPolygon(xs, ys, 2);
			}
			last = e;
			break;
		case 3:
			if (last != null) {
				g.drawLine(x() + last.x + offsetX, y() + last.y + offsetY, x() + e.x + offsetX, y() + e.y + offsetY);
			}
			last = e;
			break;
		case 4:
			step(g, e.x + offsetX, e.y + offsetY, e.progress, index, frame, color, alpha);
			break;
		}
	}

	public void step(GLEx g, float x, float y, float progress, int index, int frame, LColor color, float alpha) {

	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			this.iterateFrame();
		}
	}

	private final void callMethod(int index, float... f) {

		float[] result;

		for (float pd = this.pointDistance, t = pd; t <= 1; t += pd) {

			t = MathUtils.round(t * 1f / pd) / (1f / pd);
			switch (index) {
			case BEZIER:
				result = bezier(t, f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]);
				break;
			case ARC:
				result = arc(t, f[0], f[1], f[2], f[3], f[4]);
				break;
			case LINE:
				result = line(t, f[0], f[1], f[2], f[3]);
				break;
			default:
				result = new float[] { 0f, 0f };
			}

			points.add(new Progress(result[0], result[1], t));

		}

	}

	private final float[] bezier(float t, float p0x, float p0y, float p1x, float p1y, float c0x, float c0y, float c1x,
			float c1y) {

		t = 1 - t;

		float i = 1 - t, x = t * t, y = i * i, a = x * t, b = 3 * x * i, c = 3 * t * y, d = y * i;

		return new float[] { a * p0x + b * c0x + c * c1x + d * p1x, a * p0y + b * c0y + c * c1y + d * p1y };
	}

	private final float[] arc(float t, float cx, float cy, float radius, float start, float end) {
		float point = (end - start) * t + start;

		return new float[] { (MathUtils.cos(point) * radius) + cx, (MathUtils.sin(point) * radius) + cy };

	}

	private final float[] line(float t, float sx, float sy, float ex, float ey) {
		return new float[] { (ex - sx) * t + sx, (ey - sy) * t + sy };
	}

	private int tmpColor;

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {

		this.setup();

		int pointsLength = points.size;

		Progress point;
		int index;
		int frameD;
		int indexD;

		float size = (pointsLength * this.trailLength);

		for (float i = -1, l = size; ++i < l && !this.stopped;) {

			index = (int) (frame + i);
			if (index < pointsLength) {
				point = points.get(index);
			} else {
				point = points.get(index - pointsLength);
			}
			this._alpha = (i / (l - 1));
			frameD = frame / (pointsLength - 1);
			indexD = (int) _alpha;
			if (lineWidth > 0) {
				g.setLineWidth(lineWidth);
			}
			if (_scaleX != 1 || _scaleY != 1) {
				g.saveTx();
				g.scale(_scaleX, _scaleY);
			}
			tmpColor = g.color();
			if (_alpha > 0 && _alpha < 1) {
				g.setAlpha(_alpha);
			}
			g.setTint(_baseColor);
			step(g, point, indexD, frameD, _baseColor, _alpha, offsetX + _offset.x, offsetY + _offset.y);
			if (_alpha != 1f) {
				g.setAlpha(1f);
			}
			g.setTint(tmpColor);
			if (lineWidth > 0) {
				g.resetLineWidth();
			}
			if (_scaleX != 1 || _scaleY != 1) {
				g.restoreTx();
			}
		}

	}

	public TArray<Object[]> getData() {
		return data;
	}

	public Cycle setData(TArray<Object[]> data) {
		this.data = data;
		return this;
	}

	public int getFrame() {
		return frame;
	}

	public Cycle setFrame(int frame) {
		this.frame = frame;
		return this;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public Cycle setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
		return this;
	}

	public Progress getLast() {
		return last;
	}

	public Cycle setLast(Progress last) {
		this.last = last;
		return this;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public Cycle setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
		return this;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public Cycle setMultiplier(float multiplier) {
		this.multiplier = multiplier;
		return this;
	}

	public int getPadding() {
		return padding;
	}

	public Cycle setPadding(int padding) {
		this.padding = padding;
		return this;
	}

	public float getPointDistance() {
		return pointDistance;
	}

	public Cycle setPointDistance(float pointDistance) {
		this.pointDistance = pointDistance;
		return this;
	}

	public TArray<Progress> getPoints() {
		return points;
	}

	public Cycle setPoints(TArray<Progress> points) {
		this.points = points;
		return this;
	}

	public float getStepsPerFrame() {
		return stepsPerFrame;
	}

	public Cycle setStepsPerFrame(float stepsPerFrame) {
		this.stepsPerFrame = stepsPerFrame;
		return this;
	}

	public int getStepType() {
		return stepType;
	}

	public Cycle setStepType(int stepType) {
		this.stepType = stepType;
		return this;
	}

	public boolean isStopped() {
		return stopped;
	}

	public float getTrailLength() {
		return trailLength;
	}

	public Cycle setTrailLength(float trailLength) {
		this.trailLength = trailLength;
		return this;
	}

	public int getBlockHeight() {
		return (int) blockHeight;
	}

	public Cycle setBlockHeight(float blockHeight) {
		this.blockHeight = blockHeight;
		this.blockHalfHeight = blockHeight / 2;
		return this;
	}

	public int getBlockWidth() {
		return (int) blockWidth;
	}

	public Cycle setBlockWidth(float blockWidth) {
		this.blockWidth = blockWidth;
		this.blockHalfWidth = blockWidth / 2;
		return this;
	}

	public RectBox getShape() {
		float maxX = 0, maxY = 0;
		float minX = 0, minY = 0;
		if (isUpdate) {
			setup();
			for (Progress point : points) {
				maxX = MathUtils.max(maxX, point.x);
				maxY = MathUtils.max(maxY, point.y);
				minX = MathUtils.min(minX, point.x);
				minY = MathUtils.min(minY, point.y);
			}

		}
		return new RectBox(minX, minY, maxX, maxY);
	}

	public float getDisplayWidth() {
		return displayWidth;
	}

	public void setDisplayWidth(float displayWidth) {
		this.displayWidth = displayWidth;
	}

	public float getDisplayHeight() {
		return displayHeight;
	}

	public void setDisplayHeight(float displayHeight) {
		this.displayHeight = displayHeight;
	}

	@Override
	public RectBox getCollisionBox() {
		Shape shape = getShape();
		return getRect(shape.getX(), shape.getY(), shape.getMaxX(), shape.getMaxY());
	}

}
