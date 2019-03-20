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
package loon.opengl.light;

import loon.canvas.LColor;
import loon.geom.Vector3f;


public class DirectionalLight extends BaseLight {
	public final Vector3f direction = new Vector3f();
	
	public DirectionalLight set(final DirectionalLight copyFrom) {
		return set(copyFrom.color, copyFrom.direction);
	}
	
	public DirectionalLight set(final LColor color, final Vector3f direction) {
		if (color != null){
			this.color.setColor(color);
		}
		if (direction != null){
			this.direction.set(direction).norSelf();
		}
		return this;
	}
	
	public DirectionalLight set(final float r, final float g, final float b, final Vector3f direction) {
		this.color.setColor(r,g,b,1f);
		if (direction != null){
			this.direction.set(direction).norSelf();
		}
		return this;
	}
	
	public DirectionalLight set(final LColor color, final float dirX, final float dirY, final float dirZ) {
		if (color != null){
			this.color.setColor(color);
		}
		this.direction.set(dirX, dirY, dirZ).norSelf();
		return this;
	}
	
	public DirectionalLight set(final float r, final float g, final float b, final float dirX, final float dirY, final float dirZ) {
		this.color.setColor(r,g,b,1f);
		this.direction.set(dirX, dirY, dirZ).norSelf();
		return this;
	}
	
	@Override
	public boolean equals (Object o) {
		return (o instanceof DirectionalLight) ? equals((DirectionalLight)o) : false;
	}
	
	public boolean equals (final DirectionalLight other) {
		return (other != null) && ((other == this) || ((color.equals(other.color) && direction.equals(other.direction))));
	}
}
