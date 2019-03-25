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
package loon.opengl;

import loon.LSystem;
import loon.LTexture;
import loon.event.Updateable;
import loon.utils.GLUtils;

public abstract class BindGL implements Updateable {

	public final GL20 gl;

	protected int curTexId = -1;
	protected int lastTexId = -1;

	public BindGL() {
		this(LSystem.base().graphics().gl);
	}

	public BindGL(GL20 gl) {
		this.gl = gl;
	}

	public int getCurrentTextureID() {
		return this.curTexId;
	}

	public void setTexture(final LTexture texture) {
		final int id = texture.getID();
		if (!texture.isLoaded()) {
			texture.loadTexture();
		}
		if (curTexId != -1 && curTexId != id) {
			action(this);
		}
		this.lastTexId = this.curTexId;
		this.curTexId = id;
	}

	public void clear() {
		lastTexId = -1;
		curTexId = -1;
	}

	public void bindTexture() {
		GLUtils.bindTexture(gl, curTexId);
	}

}
