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
import loon.action.ActionBind;
import loon.geom.RectBox;

/**
 * 自0.3.2版起新增类，用以绑定任意一个LGame对象进行简单的重力牵引操作。
 */
public class Gravity implements LRelease {

	private static final RectBox HIT_SELF = new RectBox();

	public Object object;

	public ActionBind bind;

	public boolean enabled;

	public float bounce;

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
		this("unkown", o);
	}

	public Gravity(String name, ActionBind o) {
		this(name, o, o.getX(), o.getY(), o.getWidth(), o.getHeight());
	}

	public Gravity(String name, ActionBind o, float x, float y, float w, float h) {
		this.name = name;
		this.object = o;
		this.bind = o;
		this.enabled = true;
		this.setBounds(x, y, w, h);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public float getVelocityX() {
		return this.velocityX;
	}

	public float getVelocityY() {
		return this.velocityY;
	}

	public void setVelocityX(final float velocityX) {
		this.velocityX = velocityX;
	}

	public void setVelocityY(final float velocityY) {
		this.velocityY = velocityY;
	}

	public void setVelocity(final float velocity) {
		setVelocity(velocity);
	}

	public void setVelocity(final float velocityX, final float velocityY) {
		this.setVelocityX(velocityX);
		this.setVelocityY(velocityY);
	}

	public float getAccelerationX() {
		return this.accelerationX;
	}

	public float getAccelerationY() {
		return this.accelerationY;
	}

	public void setAccelerationX(final float accelerationX) {
		this.accelerationX = accelerationX;
	}

	public void setAccelerationY(final float accelerationY) {
		this.accelerationY = accelerationY;
	}

	public void setAcceleration(final float accelerationX, final float accelerationY) {
		this.setAccelerationX(accelerationX);
		this.setAccelerationY(accelerationY);
	}

	public void setAcceleration(final float acceleration) {
		this.setAcceleration(acceleration);
	}

	public void accelerate(final float accelerationX, final float accelerationY) {
		this.accelerationX += accelerationX;
		this.accelerationY += accelerationY;
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

	public Object getObject() {
		return object;
	}

	public String getName() {
		return name;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
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
		HIT_SELF.setBounds(other.getAreaOfTravel(scale));
		return bounds.overlaps(HIT_SELF) || HIT_SELF.overlaps(bounds);
	}

	public RectBox getAreaOfTravel(float scale) {
		HIT_SELF.setBounds(bounds.x, bounds.y, velocityX * scale + bounds.width, velocityY * scale + bounds.height);
		bounds.normalize(HIT_SELF);
		return HIT_SELF;
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
	}

	public void dispose() {
		this.enabled = false;
		this.bind = null;
	}

	public boolean isClosed() {
		return bind == null;
	}

	@Override
	public void close() {
		dispose();
	}

}
