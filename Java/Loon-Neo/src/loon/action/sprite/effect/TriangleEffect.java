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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.action.sprite.Entity;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

/**
 * 像素风三角风格渲染具体实现用类(就是画各种成片的三角形)
 */
public class TriangleEffect extends Entity implements BaseEffect {

	private float[][] delta;

	private float[] currentPos;

	private float[] movePos;

	private float[] avg;

	private float vector;

	private float viewSpeed;

	private LTimer timer;

	private boolean completed;

	public TriangleEffect(float[][] res, float x, float y, float speed) {
		this(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), res, null, x, y, speed);
	}

	public TriangleEffect(float w, float h, float[][] res, float x, float y, float speed) {
		this(w, h, res, null, x, y, speed);
	}

	public TriangleEffect(float w, float h, float[][] res, float[] ads, float x, float y, float speed) {
		this.setDelta(res);
		this.viewSpeed = speed;
		this.currentPos = new float[2];
		this.movePos = new float[2];
		this.movePos[0] = x;
		this.movePos[1] = y;
		if (ads == null) {
			this.resetAverage();
		} else {
			this.setAverage(ads);
		}
		this.setRepaint(true);
		this.setSize(w, h);
		this.timer = new LTimer(10);
		this.completed = false;
	}

	public void setDelta(float[][] res) {
		this.delta = res;
	}

	public float[][] getDelta() {
		return delta;
	}

	public void setAverage(float[] res) {
		this.avg = res;
	}

	public void resetAverage() {
		this.avg = new float[2];
		for (int j = 0; j < delta.length; j++) {
			for (int i = 0; i < avg.length; i++) {
				avg[i] += delta[j][i];
			}
		}
		for (int i = 0; i < avg.length; i++) {
			avg[i] /= 3f;
		}
	}

	public void setPosX(float x) {
		this.currentPos[0] = x;
	}

	public void setPosY(float y) {
		this.currentPos[1] = y;
	}

	public void setPos(float x, float y) {
		this.setPosX(x);
		this.setPosY(y);
	}

	public float getPosX() {
		return currentPos[0];
	}

	public float getPosY() {
		return currentPos[1];
	}

	public void setVector(float v) {
		this.vector = v;
	}

	public void setVectorSpeed(float v) {
		this.viewSpeed = v;
	}

	public void setMoveX(float x) {
		this.movePos[0] = x;
	}

	public void setMoveY(float y) {
		this.movePos[1] = y;
	}

	public void setMove(int x, int y) {
		setMoveX(x);
		setMoveY(y);
	}

	public float next() {
		currentPos[0] += movePos[0];
		currentPos[1] += movePos[1];
		vector += viewSpeed;
		vector %= 360f;
		if (vector < 0f) {
			vector += 360f;
		}
		return vector;
	}

	public float[][] drawing(float x, float y) {
		float[][] location = new float[3][2];
		for (int i = 0; i < delta.length; i++) {
			float d = getLine(delta[i][0] - avg[0], delta[i][1] - avg[1]);
			float d1 = getDegrees(delta[i][0] - avg[0], delta[i][1] - avg[1]);
			float d2 = MathUtils.cos(MathUtils.toRadians(vector + d1)) * d + avg[0] + currentPos[0] + x;
			float d3 = MathUtils.sin(MathUtils.toRadians(vector + d1)) * d + avg[1] + currentPos[1] + y;
			location[i][0] = (d2 + 0.5f);
			location[i][1] = (d3 + 0.5f);
		}
		return location;
	}

	public void draw(GLEx g) {
		draw(g, 0, 0);
	}

	public void draw(GLEx g, float x, float y) {
		float[][] res = drawing(x, y);
		for (int i = 0; i < res.length; i++) {
			int index = (i + 1) % 3;
			g.drawLine(_width - res[i][0], _height - res[i][1], _width - res[index][0], _height - res[index][1], 2);
		}
	}

	public void drawPaint(GLEx g, float x, float y) {
		float[][] res = drawing(x, y);
		float xs[] = new float[3];
		float ys[] = new float[3];
		final int size = res.length;
		for (int i = 0; i < size; i++) {
			xs[i] = _width - res[i][0];
			ys[i] = _height - res[i][1];

		}
		g.fillPolygon(xs, ys, 3);
	}

	private float getLine(float x, float y) {
		return MathUtils.sqrt(MathUtils.pow(MathUtils.abs(x), 2f) + MathUtils.pow(MathUtils.abs(y), 2f));
	}

	public static float getDegrees(float r1, float r2) {
		if (r1 == 0.0f && r2 == 0.0f) {
			return 0.0f;
		}
		float d2 = MathUtils.sqrt(MathUtils.pow(r1, 2f) + MathUtils.pow(r2, 2f));
		float d3 = MathUtils.toDegrees(MathUtils.acos(r1 / d2));
		if (MathUtils.asin(r2 / d2) < 0f) {
			return 360f - d3;
		} else {
			return d3;
		}
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (!completed) {
			if (timer.action(elapsedTime)) {
				next();
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		draw(g, drawX(offsetX), drawY(offsetY));
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
	}

}
