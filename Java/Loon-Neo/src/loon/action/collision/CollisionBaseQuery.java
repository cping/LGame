/**
 * 
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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.action.collision;

import loon.geom.RectBox;
import loon.geom.Vector2f;

public class CollisionBaseQuery implements CollisionQuery {

	private String flag;

	private CollisionObject compareObject;

	private Vector2f offsetLocation;

	private RectBox tempRect = new RectBox(0, 0, 0, 0);

	public void init(String flag, CollisionObject actor, Vector2f offset) {
		this.flag = flag;
		this.compareObject = actor;
		this.offsetLocation = offset;
	}

	private float offsetX(float x) {
		if (offsetLocation == null) {
			return x;
		}
		return x + offsetLocation.x;
	}

	private float offsetY(float y) {
		if (offsetLocation == null) {
			return y;
		}
		return y + offsetLocation.y;
	}

	public boolean checkOnlyCollision(CollisionObject other) {
		if (!offsetLocation.isZero()) {
			tempRect.setBounds(offsetX(this.compareObject.getX()), offsetY(this.compareObject.getY()),
					this.compareObject.getWidth(), this.compareObject.getHeight());
			return (this.compareObject == null ? true : other.intersects(tempRect));
		}
		return (this.compareObject == null ? true : other.intersects(this.compareObject));
	}

	@Override
	public boolean checkCollision(CollisionObject other) {
		if (!offsetLocation.isZero()) {
			tempRect.setBounds(offsetX(this.compareObject.getX()), offsetY(this.compareObject.getY()),
					this.compareObject.getWidth(), this.compareObject.getHeight());
			return this.flag != null && !flag.equals(other.getObjectFlag()) ? false
					: (this.compareObject == null ? true : other.intersects(tempRect));
		}
		return this.flag != null && !flag.equals(other.getObjectFlag()) ? false
				: (this.compareObject == null ? true : other.intersects(this.compareObject));
	}

	@Override
	public void setOffsetPos(Vector2f offset) {
		offsetLocation = offset;
	}

	@Override
	public Vector2f getOffsetPos() {
		return offsetLocation;
	}
}
