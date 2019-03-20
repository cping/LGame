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

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.FloatArray;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/*
 * 单纯绘制一个闪电
 */
public class LightningBolt implements ILightning {

	public float alpha;
	public float alphaMultiplier;
	public float fadeOutRate;
	public LColor tint;

	private LTimer timer = new LTimer(0);

	private TArray<LightningLine> segments = new TArray<LightningLine>();

	public LightningBolt(Vector2f source, Vector2f dest) {
		this(source, dest, new LColor(0.9f, 0.8f, 1f));
	}

	public LightningBolt(Vector2f source, Vector2f dest, LColor color) {
		this.segments = CreateBolt(source, dest, 2);
		this.tint = color;
		this.alpha = 1f;
		this.alphaMultiplier = 0.6f;
		this.fadeOutRate = 0.03f;
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float x, float y) {
		if (alpha <= 0) {
			return;
		}
		for (LightningLine segment : segments) {
			segment.draw(spriteBatch, x, y, tint.mul(alpha * alphaMultiplier));
		}
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	@Override
	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			alpha -= fadeOutRate;
		}
	}

	public Vector2f getStart() {
		return segments.get(0).lineA;
	}

	public Vector2f getEnd() {
		return segments.last().lineB;
	}

	@Override
	public boolean isComplete() {
		return alpha <= 0;
	}

	protected static TArray<LightningLine> CreateBolt(Vector2f source, Vector2f dest, float thickness) {
		TArray<LightningLine> results = new TArray<LightningLine>();
		Vector2f tangent = dest.sub(source);
		Vector2f normal = Vector2f.nor(new Vector2f(tangent.y, -tangent.x));
		float length = tangent.length();

		FloatArray positions = new FloatArray();
		positions.add(0);

		for (int i = 0; i < length / 4; i++) {
			positions.add(rand(0, 1));
		}

		positions.sort();

		final float sway = 80;
		final float jaggedness = 1 / sway;

		Vector2f prevPoint = source;
		float prevDisplacement = 0;
		for (int i = 1; i < positions.size(); i++) {
			float pos = positions.get(i);

			float scale = (length * jaggedness) * (pos - positions.get(i - 1));

			float envelope = pos > 0.95f ? 20 * (1 - pos) : 1;

			float displacement = rand(-sway, sway);
			displacement -= (displacement - prevDisplacement) * (1 - scale);
			displacement *= envelope;

			Vector2f point = source.add(tangent.mul(pos)).add(normal.mul(displacement));
			results.add(new LightningLine(prevPoint, point, thickness));
			prevPoint = point;
			prevDisplacement = displacement;
		}

		results.add(new LightningLine(prevPoint, dest, thickness));

		return results;
	}

	public Vector2f getPoint(float position) {
		final Vector2f start = getStart();
		float length = Vector2f.dst(start, getEnd());
		Vector2f dir = (getEnd().sub(start)).div(length);
		position *= length;
		LightningLine line = null;

		for (LightningLine x : segments) {
			if (Vector2f.dot(x.lineB.sub(start), dir) >= position) {
				line = x;
			}
		}
		if (line == null) {
			return start;
		}

		float lineStartPos = Vector2f.dot(line.lineA.sub(start), dir);
		float lineEndPos = Vector2f.dot(line.lineB.sub(start), dir);
		float linePos = (position - lineStartPos) / (lineEndPos - lineStartPos);

		return Vector2f.lerp(line.lineA, line.lineB, linePos);
	}

	private static float rand(float min, float max) {
		return MathUtils.random(min, max);
	}

	@Override
	public void close() {
		segments.clear();
	}

}
