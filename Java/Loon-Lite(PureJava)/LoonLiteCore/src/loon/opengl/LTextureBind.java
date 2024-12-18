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
package loon.opengl;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.utils.GLUtils;

public class LTextureBind extends GLBase {

	protected Canvas gl = null;

	protected int curTexId = -1;
	protected int lastTexId = -1;

	protected LTexture lastTexture = null;

	public int getCurrentTextureID() {
		return this.curTexId;
	}

	public LTexture getCurrentTexture() {
		if (lastTexture != null && !lastTexture.isClosed() && lastTexture.getID() == this.curTexId) {
			return lastTexture;
		}
		return LSystem.getTexture(this.curTexId);
	}

	public void setTexture(final LTexture texture) {
		lastTexture = texture;
		final int id = texture.getID();
		if (!texture.isLoaded()) {
			texture.loadTexture();
		}
		if (curTexId != -1 && curTexId != id) {
			flush();
		}
		this.lastTexId = this.curTexId;
		this.curTexId = id;
	}

	@Override
	public void end() {
		super.end();
		lastTexId = -1;
		curTexId = -1;
		drawCallCount = -1;
	}

	public int getDrawCallCount() {
		return drawCallCount;
	}

	protected LTextureBind(Canvas g) {
		this.gl = g;
	}

	protected void bindTexture() {
		GLUtils.bindTexture(curTexId);
	}

	@Override
	public void init() {

	}

}
