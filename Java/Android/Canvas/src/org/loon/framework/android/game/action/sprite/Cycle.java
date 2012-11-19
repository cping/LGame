package org.loon.framework.android.game.action.sprite;

import java.util.ArrayList;
import java.util.HashMap;

import org.loon.framework.android.game.core.LObject;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimer;

import android.graphics.Bitmap;

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
 * @version 0.1
 */
public class Cycle extends LObject implements ISprite {

	public final static Cycle getSample(int type, float srcWidth,
			float srcHeight, float width, float height, float offset,
			int padding) {
		Cycle cycle = new Cycle();
		float s = 1;
		if (srcWidth > srcHeight) {
			s = Math.max(srcWidth / width, srcHeight / height);
		} else {
			s = Math.min(srcWidth / width, srcHeight / height);
		}
		final float scale = s;
		switch (type) {
		case 0:
			cycle.setColor(0xFF7B24);
			cycle.setStepsPerFrame(1);
			cycle.setTrailLength(1);
			cycle.setPointDistance(0.10f);
			cycle.setMultiplier(2);
			cycle.addPath(Cycle.ARC, 10 * scale, 10 * scale, 10 * scale, -270,
					-90);
			cycle.addPath(Cycle.BEZIER, 10 * scale, 0 * scale, 40 * scale,
					20 * scale, 20 * scale, 0, 30 * scale, 20 * scale);
			cycle.addPath(Cycle.ARC, 40 * scale, 10 * scale, 10 * scale, 90,
					-90);
			cycle.addPath(Cycle.BEZIER, 40 * scale, 0 * scale, 10 * scale,
					20 * scale, 30 * scale, 0, 20 * scale, 20 * scale);
			break;
		case 1:
			cycle.setColor(0xD4FF00);
			cycle.setStepType(1);
			cycle.setDelay(55);
			cycle.setStepsPerFrame(2);
			cycle.setTrailLength(0.3f);
			cycle.setPointDistance(0.1f);
			cycle.addPath(Cycle.LINE, 0, 0, 30 * scale, 0);
			cycle.addPath(Cycle.LINE, 30 * scale, 0 * scale, 30 * scale,
					30 * scale);
			cycle.addPath(Cycle.LINE, 30 * scale, 30 * scale, 0, 30 * scale);
			cycle.addPath(Cycle.LINE, 0, 30 * scale, 0, 0);
			break;
		case 2:
			cycle.setColor(0xFFA50000);
			cycle.setStepsPerFrame(1);
			cycle.setTrailLength(1);
			cycle.setPointDistance(0.025f);
			cycle
					.addPath(Cycle.ARC, 50 * scale, 50 * scale, 40 * scale, 0,
							360);
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
			cycle.addPath(Cycle.LINE, 20 * scale, 70 * scale, 50 * scale,
					20 * scale);
			cycle.addPath(Cycle.LINE, 50 * scale, 20 * scale, 80 * scale,
					70 * scale);
			cycle.addPath(Cycle.LINE, 80 * scale, 70 * scale, 20 * scale,
					70 * scale);
			break;
		case 5:
			cycle.setColor(0x05E2FF);
			cycle.setStepsPerFrame(1);
			cycle.setTrailLength(1);
			cycle.setPointDistance(0.02f);
			cycle
					.addPath(Cycle.ARC, 50 * scale, 50 * scale, 30 * scale, 0,
							360);
			break;
		case 6:
			cycle.setStepType(1);
			cycle.setColor(LColor.yellow);
			cycle.addPath(Cycle.LINE, 10 * scale, 10 * scale, 90 * scale,
					10 * scale);
			cycle.addPath(Cycle.LINE, 90 * scale, 10 * scale, 90 * scale,
					90 * scale);
			cycle.addPath(Cycle.LINE, 90 * scale, 90 * scale, 10 * scale,
					90 * scale);
			cycle.addPath(Cycle.LINE, 10 * scale, 90 * scale, 10 * scale,
					10 * scale);
			break;
		default:
			return null;
		}
		float size = Math.min(srcWidth / (1 / cycle.getPointDistance()),
				srcHeight / (1 / cycle.getPointDistance()));
		cycle.setPadding(padding);
		cycle.setBlockWidth(size + offset);
		cycle.setBlockHeight(size + offset);
		cycle.setWidth(width * scale);
		cycle.setHeight(height * scale);
		return cycle;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4197405628446701982L;

	public static final int OTHER = 0, DIM = 1, DEGREE = 2, RADIUS = 3;

	public static final int BEZIER = 0, ARC = 1, LINE = 2;

	protected float pointDistance;

	protected float multiplier;

	protected int frame, padding;

	protected int stepType;

	protected float trailLength, stepsPerFrame;

	protected boolean isUpdate, isVisible, stopped;

	protected ArrayList<Object[]> data;

	protected static HashMap<Integer, float[]> signatures;

	protected ArrayList<Progress> points;

	private LTimer timer;

	private LColor color;

	private Progress last;

	protected float alpha;

	protected float blockWidth, blockHeight, blockHalfWidth, blockHalfHeight;

	protected float width, height;

	class Progress {

		float x;

		float y;

		float progress;

		public Progress(float x, float y, float p) {
			this.x = x;
			this.y = y;
			this.progress = p;
		}
	}

	public Cycle() {
		this(0, 0);
	}

	public Cycle(int x, int y) {
		this(x, y, 6, 6);
	}

	public Cycle(int x, int y, int w, int h) {
		this(null, x, y, w, h);
	}

	public Cycle(ArrayList<Object[]> path, int x, int y, int w, int h) {

		if (path != null) {
			data.add(path.toArray());
			isUpdate = true;
		} else {
			data = new ArrayList<Object[]>(10);
		}

		this.setLocation(x, y);
		this.timer = new LTimer(25);
		this.color = LColor.white;
		this.points = new ArrayList<Progress>();
		this.multiplier = 1;
		this.pointDistance = 0.05f;
		this.padding = 0;
		this.stepType = 0;
		this.stepsPerFrame = 1;
		this.trailLength = 1;
		this.alpha = 1;
		this.blockWidth = w;
		this.blockHeight = h;
		this.blockHalfWidth = w / 2;
		this.blockHalfHeight = h / 2;
		if (signatures == null) {
			signatures = new HashMap<Integer, float[]>(3);
			signatures.put(ARC, new float[] { 1, 1, 3, 2, 2, 0 });
			signatures.put(BEZIER, new float[] { 1, 1, 1, 1, 1, 1, 1, 1 });
			signatures.put(LINE, new float[] { 1, 1, 1, 1 });
		}
		this.setup();
		this.isVisible = true;

	}

	public void play() {
		this.stopped = false;
	}

	public void iterateFrame() {
		this.frame += this.stepsPerFrame;

		if (this.frame >= this.points.size()) {
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
					value *= Math.PI / 180;
					break;
				}

				args[a] = value;
			}
			callMethod(type, args);

		}
		this.isUpdate = false;
	}

	private final void step(LGraphics g, Progress e, int index, int frame,
			LColor color, float alpha) {
		switch (stepType) {
		case 0:
			g.fillOval((int) (x() + e.x - blockHalfWidth),
					(int) (y() + e.y - blockHalfHeight), (int) blockWidth,
					(int) blockHeight);
			break;
		case 1:
			g.fillRect((int) (x() + e.x - blockHalfWidth),
					(int) (y() + e.y - blockHalfHeight), (int) blockWidth,
					(int) blockHeight);
			break;
		case 2:
			if (last != null) {
				int[] xs = { (int) (x() + last.x), (int) (x() + e.x) };
				int[] ys = { (int) (y() + last.y), (int) (y() + e.y) };
				g.drawPolygon(xs, ys, 2);
			}
			last = e;
			break;
		case 3:
			if (last != null) {
				g.drawLine((int) (x() + last.x), (int) (y() + last.y),
						(int) (x() + e.x), (int) (y() + e.y));
			}
			last = e;
			break;
		case 4:
			step(g, e.x, e.y, e.progress, index, frame, color, alpha);
			break;
		}
	}

	public void step(LGraphics g, float x, float y, float progress, int index,
			int frame, LColor color, float alpha) {

	}

	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			this.iterateFrame();
		}
	}

	private final void callMethod(int index, float... f) {

		float[] result;

		for (float pd = this.pointDistance, t = pd; t <= 1; t += pd) {

			t = Math.round(t * 1f / pd) / (1f / pd);
			switch (index) {
			case BEZIER:
				result = bezier(t, f[0], f[1], f[2], f[3], f[4], f[5], f[6],
						f[7]);
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

	private final float[] bezier(float t, float p0x, float p0y, float p1x,
			float p1y, float c0x, float c0y, float c1x, float c1y) {

		t = 1 - t;

		float i = 1 - t, x = t * t, y = i * i, a = x * t, b = 3 * x * i, c = 3
				* t * y, d = y * i;

		return new float[] { a * p0x + b * c0x + c * c1x + d * p1x,
				a * p0y + b * c0y + c * c1y + d * p1y };
	}

	private final float[] arc(float t, float cx, float cy, float radius,
			float start, float end) {
		float point = (end - start) * t + start;

		return new float[] { (float) ((Math.cos(point) * radius) + cx),
				(float) ((Math.sin(point) * radius) + cy) };

	}

	private final float[] line(float t, float sx, float sy, float ex, float ey) {
		return new float[] { (ex - sx) * t + sx, (ey - sy) * t + sy };
	}

	public void createUI(LGraphics g) {
		if (!isVisible) {
			return;
		}

		this.setup();

		int pointsLength = points.size();

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
			this.alpha = (i / (l - 1));
			frameD = frame / (pointsLength - 1);
			indexD = (int) alpha;
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(alpha);
			}
			g.setColor(color.getRed(), color.getGreen(), color.getBlue(),
					(int) (alpha * 255));
			step(g, point, indexD, frameD, color, alpha);
			g.resetColor();
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(1);
			}
		}

	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public void setColor(int pixel) {
		this.color = new LColor(pixel);
	}

	public ArrayList<Object[]> getData() {
		return data;
	}

	public void setData(ArrayList<Object[]> data) {
		this.data = data;
	}

	public int getFrame() {
		return frame;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public Progress getLast() {
		return last;
	}

	public void setLast(Progress last) {
		this.last = last;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public float getPointDistance() {
		return pointDistance;
	}

	public void setPointDistance(float pointDistance) {
		this.pointDistance = pointDistance;
	}

	public ArrayList<Progress> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Progress> points) {
		this.points = points;
	}

	public float getStepsPerFrame() {
		return stepsPerFrame;
	}

	public void setStepsPerFrame(float stepsPerFrame) {
		this.stepsPerFrame = stepsPerFrame;
	}

	public int getStepType() {
		return stepType;
	}

	public void setStepType(int stepType) {
		this.stepType = stepType;
	}

	public boolean isStopped() {
		return stopped;
	}

	public float getTrailLength() {
		return trailLength;
	}

	public void setTrailLength(float trailLength) {
		this.trailLength = trailLength;
	}

	public int getBlockHeight() {
		return (int) blockHeight;
	}

	public void setBlockHeight(float blockHeight) {
		this.blockHeight = blockHeight;
		this.blockHalfHeight = blockHeight / 2;
	}

	public int getBlockWidth() {
		return (int) blockWidth;
	}

	public void setBlockWidth(float blockWidth) {
		this.blockWidth = blockWidth;
		this.blockHalfWidth = blockWidth / 2;
	}

	public Bitmap getBitmap() {
		return null;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), (int) width, (int) height);
	}

	public void setWidth(float w) {
		this.width = w;
	}

	public void setHeight(float h) {
		this.height = h;
	}

	public int getWidth() {
		return (int) height;
	}

	public int getHeight() {
		return (int) width;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	public void dispose() {

	}

}
