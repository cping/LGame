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
package loon.physics;

import loon.core.geom.Shape;
import loon.core.geom.Vector2f;
import loon.utils.MathUtils;


import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsShell {

	public static final float PIXEL_TO_METER_RATIO_DEFAULT = 32.0f;

	protected final Shape shape;

	protected final Body body;

	protected final float shapeHalfBaseWidth;

	protected final float shapeHalfBaseHeight;

	protected boolean updatePosition;

	protected boolean updateRotation;

	protected final float pixelToMeterRatio;

	public PhysicsShell(final Shape a, final Body b) {
		this(a, b, true, true);
	}

	public PhysicsShell(final Shape a, final Body b, final float p) {
		this(a, b, true, true, p);
	}

	public PhysicsShell(final Shape a, final Body b, final boolean u,
			final boolean r) {
		this(a, b, u, r, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	public PhysicsShell(final Shape a, final Body b, final boolean u,
			final boolean r, final float p) {
		this.shape = a;
		this.body = b;

		this.updatePosition = u;
		this.updateRotation = r;
		this.pixelToMeterRatio = p;

		this.shapeHalfBaseWidth = a.getWidth() * 0.5f;
		this.shapeHalfBaseHeight = a.getHeight() * 0.5f;
	}

	public Shape getShape() {
		return this.shape;
	}

	public Body getBody() {
		return this.body;
	}

	public boolean isUpdatePosition() {
		return this.updatePosition;
	}

	public boolean isUpdateRotation() {
		return this.updateRotation;
	}

	public void setUpdatePosition(final boolean pUpdatePosition) {
		this.updatePosition = pUpdatePosition;
	}

	public void setUpdateRotation(final boolean pUpdateRotation) {
		this.updateRotation = pUpdateRotation;
	}

	public void reset() {

	}

	public void update(final float secondsElapsed) {
		final Shape shape = this.shape;
		final Body body = this.body;

		if (this.updatePosition) {
			final Vector2f position = body.getPosition();
			final float pixelToMeterRatio = this.pixelToMeterRatio;
			shape.setLocation(position.x * pixelToMeterRatio
					- this.shapeHalfBaseWidth, position.y * pixelToMeterRatio
					- this.shapeHalfBaseHeight);
		}

		if (this.updateRotation) {
			final float angle = body.getAngle();
			shape.setRotation(MathUtils.radToDeg(angle));
		}
	}
}
