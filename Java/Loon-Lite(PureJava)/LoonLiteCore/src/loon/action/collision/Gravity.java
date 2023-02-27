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
package loon.action.collision;

import loon.LRelease;
import loon.LSystem;
import loon.action.ActionBind;
import loon.geom.RectBox;

/**
 * 自0.3.2版起新增类，用以绑定任意一个LGame对象进行简单的重力牵引操作。
 */
public class Gravity implements LRelease {

	private static final RectBox _hitRect = new RectBox();

	public Object tag;

	public ActionBind bind;

	public boolean enabled;

	public float bounce;

	public boolean limitX;

	public boolean limitY;

	public float gadd;

	public float g;

	public float accelerationX;

	public float accelerationY;

	public float velocityX;

	public float velocityY;

	public float angularVelocity;

	public String name;

	public RectBox bounds = new RectBox();

	public Gravity(ActionBind o) {
		this(LSystem.UNKNOWN, o);
	}

	public Gravity(String name, ActionBind o) {
		this(name, o, null);
	}

	public Gravity(String name, ActionBind o, Object tag) {
		this(name, o, tag, o.getX(), o.getY(), o.getWidth(), o.getHeight());
	}

	public Gravity(String name, ActionBind o, Object tag, float x, float y, float w, float h) {
		this.name = name;
		this.tag = tag;
		this.bind = o;
		this.enabled = true;
		this.setBounds(x, y, w, h);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public Gravity setEnabled(final boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public float getVelocityX() {
		return this.velocityX;
	}

	public float getVelocityY() {
		return this.velocityY;
	}

	public Gravity setVelocityX(final float velocityX) {
		this.velocityX = velocityX;
		return this;
	}

	public Gravity setVelocityY(final float velocityY) {
		this.velocityY = velocityY;
		return this;
	}

	public Gravity setVelocity(final float velocity) {
		return setVelocity(velocity, velocity);
	}

	public Gravity setVelocity(final float velocityX, final float velocityY) {
		this.setVelocityX(velocityX);
		this.setVelocityY(velocityY);
		return this;
	}

	public float getAccelerationX() {
		return this.accelerationX;
	}

	public float getAccelerationY() {
		return this.accelerationY;
	}

	public Gravity setAccelerationX(final float accelerationX) {
		this.accelerationX = accelerationX;
		return this;
	}

	public Gravity setAccelerationY(final float accelerationY) {
		this.accelerationY = accelerationY;
		return this;
	}

	public Gravity setAcceleration(final float accelerationX, final float accelerationY) {
		this.setAccelerationX(accelerationX);
		this.setAccelerationY(accelerationY);
		return this;
	}

	public Gravity setAcceleration(final float acceleration) {
		this.setAcceleration(acceleration);
		return this;
	}

	public Gravity accelerate(final float accelerationX, final float accelerationY) {
		this.accelerationX += accelerationX;
		this.accelerationY += accelerationY;
		return this;
	}

	public float getAngularVelocity() {
		return this.angularVelocity;
	}

	public void setAngularVelocity(final float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public ActionBind getBind() {
		return bind;
	}

	public Object getTag() {
		return tag;
	}

	public String getName() {
		return name;
	}

	public float getG() {
		return g;
	}

	public Gravity setG(float g) {
		this.g = g;
		return this;
	}

	public Gravity setAntiGravityX() {
		return setAntiGravityX(true);
	}

	public Gravity setAntiGravityX(boolean r) {
		float og = this.g;
		float ox = this.velocityX;
		if (r) {
			reset();
		}
		this.g = -og;
		this.velocityX = ox;
		return this;
	}

	public Gravity setAntiGravityY() {
		return setAntiGravityY(true);
	}

	public Gravity setAntiGravityY(boolean r) {
		float og = this.g;
		float oy = this.velocityY;
		if (r) {
			reset();
		}
		this.g = -og;
		this.velocityY = oy;
		return this;
	}

	public float getBounce() {
		return bounce;
	}

	public void setBounce(float bounce) {
		this.bounce = bounce;
	}

	public Gravity setBounds(float x, float y, float w, float h) {
		bounds.setBounds(x, y, w, h);
		return this;
	}

	public boolean hitInPath(float scale, Gravity other) {
		_hitRect.setBounds(other.getAreaOfTravel(scale));
		return bounds.overlaps(_hitRect) || _hitRect.overlaps(bounds);
	}

	public RectBox getAreaOfTravel(float scale) {
		_hitRect.setBounds(bounds.x, bounds.y, velocityX * scale + bounds.width, velocityY * scale + bounds.height);
		bounds.normalize(_hitRect);
		return _hitRect;
	}

	public void reset() {
		this.accelerationX = 0;
		this.accelerationY = 0;
		this.gadd = 0;
		this.g = 0;
		this.bounce = 0;
		this.velocityX = 0;
		this.velocityY = 0;
		this.angularVelocity = 0;
		this.limitX = this.limitY = false;
	}

	public Gravity dispose() {
		this.enabled = false;
		this.bind = null;
		return this;
	}

	public boolean isClosed() {
		return bind == null;
	}

	@Override
	public void close() {
		dispose();
	}

}
