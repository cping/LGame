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
package loon.stage;

import loon.geom.Transform;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public abstract class ClippedPlayer extends Player {

	private final Vector2f pos = new Vector2f();
	private final Vector2f size = new Vector2f();
	private float width, height;

	public ClippedPlayer(float width, float height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public float width() {
		return this.width;
	}

	@Override
	public float height() {
		return this.height;
	}

	public ClippedPlayer setSize(float width, float height) {
		this.width = width;
		this.height = height;
		checkOrigin();
		return this;
	}

	public ClippedPlayer setWidth(float width) {
		this.width = width;
		checkOrigin();
		return this;
	}

	public ClippedPlayer setHeight(float height) {
		this.height = height;
		checkOrigin();
		return this;
	}

	protected boolean disableClip() {
		return false;
	}

	@Override
	protected final void paintImpl(GLEx gl) {
		if (disableClip()) {
			paintClipped(gl);
		} else {
			Transform tx = gl.tx();
			float originX = originX(), originY = originY();
			tx.translate(originX, originY);
			tx.transform(pos.set(-originX, -originY), pos);
			tx.transform(size.set(width, height), size);
			tx.translate(-originX, -originY);
			boolean nonEmpty = gl.startClipped((int) pos.x, (int) pos.y,
					MathUtils.round(MathUtils.abs(size.x)), MathUtils.round(MathUtils.abs(size.y)));
			try {
				if (nonEmpty){
					paintClipped(gl);
				}
			} finally {
				gl.endClipped();
			}
		}
	}

	protected abstract void paintClipped(GLEx gl);
}
