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

	private LTimer _timer = new LTimer(0);
	private Vector2f _end;
	private Vector2f _direction;
	private TArray<LightningBolt> _bolts = new TArray<LightningBolt>();
	private boolean _closed;

	public LightningBranch(Vector2f s, Vector2f e) {
		this(s, e, LColor.white);
	}

	public LightningBranch(Vector2f s, Vector2f e, LColor c) {
		this._end = e;
		this._direction = e.sub(s).norSelf();
		this.create(s, e, c);
	}

	@Override
	public boolean isComplete() {
		return _bolts.isEmpty();
	}

	public LightningBranch setDelay(long delay) {
		_timer.setDelay(delay);
		return this;
	}

	public long getDelay() {
		return _timer.getDelay();
	}

	@Override
	public void update(long elapsedTime) {
		if (_timer.action(elapsedTime)) {
			_bolts = _bolts.where(new QueryEvent<LightningBolt>() {

				@Override
				public boolean hit(LightningBolt t) {
					return !t.isComplete();
				}
			});
			for (LightningBolt bolt : _bolts) {
				bolt.update(elapsedTime);
			}
		}
	}

	@Override
	public void draw(GLEx g, float x, float y) {
		for (LightningBolt bolt : _bolts) {
			bolt.draw(g, x, y);
		}
	}

	private void create(Vector2f start, Vector2f end, LColor c) {
		LightningBolt mainBolt = new LightningBolt(start, end, c);
		_bolts.add(mainBolt);
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
			_bolts.add(new LightningBolt(boltStart, boltEnd, c));
		}
	}

	public Vector2f getDirection() {
		return _direction;
	}

	public Vector2f getEnd() {
		return _end;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_bolts != null) {
			_bolts.clear();
		}
		_closed = true;
	}

}
