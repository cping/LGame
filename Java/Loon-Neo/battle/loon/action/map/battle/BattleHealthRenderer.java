/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.map.battle;

import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class BattleHealthRenderer extends BattleEffectRenderer {

	private float delta;
	private float duration;

	private int deltaHealth;
	private LColor color;
	private Vector2f pos;
	private int time = 0;

	public BattleHealthRenderer(BattleEffect effect, LColor a, LColor b, float newX, float newY, float duration,
			float delta) {
		super(effect);
		pos = Vector2f.at(newX, newY);
		deltaHealth = effect.getValue();
		if (deltaHealth > 0) {
			color = a;
		} else {
			color = b;
		}
	}

	public float getDelta() {
		return delta;
	}

	public float getDuration() {
		return duration;
	}

	public int getDeltaHealth() {
		return deltaHealth;
	}

	public LColor getColor() {
		return color;
	}

	public Vector2f getPos() {
		return pos;
	}

	public int getTime() {
		return time;
	}

	@Override
	public boolean completed() {
		return time >= duration;
	}

	@Override
	public void draw(GLEx g) {
		if (time < duration) {
			g.drawString(String.valueOf(MathUtils.abs(deltaHealth)), pos.x, pos.y - (time * delta / (duration / 2)),
					color);
		}
	}

	@Override
	public void update(long delta) {
		this.time += delta;
	}

}
