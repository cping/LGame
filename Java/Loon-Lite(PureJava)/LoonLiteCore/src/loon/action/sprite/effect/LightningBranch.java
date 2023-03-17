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

import loon.canvas.LColor;
import loon.events.QueryEvent;
import loon.geom.Quaternion;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.opengl.GLEx;
import loon.utils.FloatArray;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/*
 * 绘制一个存在支流的闪电线
 */
public class LightningBranch implements ILightning {

	private LTimer timer = new LTimer(0);
	private Vector2f end;
	private Vector2f direction;
	private TArray<LightningBolt> bolts = new TArray<>();
	private boolean closed;

	public LightningBranch(Vector2f s, Vector2f e) {
		this(s, e, LColor.white);
	}

	public LightningBranch(Vector2f s, Vector2f e, LColor c) {
		this.end = e;
		this.direction = Vector2f.nor(e.sub(s));
		this.create(s, e, c);
	}

	@Override
	public boolean isComplete() {
		return bolts.isEmpty();
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
			bolts = bolts.where(new QueryEvent<LightningBolt>() {

				@Override
				public boolean hit(LightningBolt t) {
					return !t.isComplete();
				}
			});
			for (LightningBolt bolt : bolts) {
				bolt.update(elapsedTime);
			}
		}
	}

	@Override
	public void draw(GLEx g, float x, float y) {
		for (LightningBolt bolt : bolts) {
			bolt.draw(g, x, y);
		}
	}

	private void create(Vector2f start, Vector2f end, LColor c) {
		LightningBolt mainBolt = new LightningBolt(start, end, c);
		bolts.add(mainBolt);
		int numBranches = MathUtils.random(3, 6);
		Vector2f diff = end.sub(start);
		FloatArray branchPoints = FloatArray.range(1, numBranches + 1).where(new QueryEvent<Float>() {

			@Override
			public boolean hit(Float t) {
				return MathUtils.nextBoolean();
			}
		}).sort();
		for (int i = 0; i < branchPoints.length; i++) {
			Vector2f boltStart = mainBolt.getPoint(branchPoints.get(i));
			Quaternion rot = Quaternion.createFromAxisAngle(Vector3f.AXIS_Z(),
					MathUtils.toRadians(30 * ((i & 1) == 0 ? 1 : -1)));
			Vector2f boltEnd = Vector2f.transform(diff.mul(1 - branchPoints.get(i)), rot).add(boltStart);
			bolts.add(new LightningBolt(boltStart, boltEnd, c));
		}
	}

	public Vector2f getDirection() {
		return direction;
	}

	public Vector2f getEnd() {
		return end;
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		if (bolts != null) {
			bolts.clear();
		}
		closed = true;
	}

}
