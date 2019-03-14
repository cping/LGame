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

import loon.LTexture;
import loon.utils.GLUtils;

public class LTextureBind extends GLBase {

	public static abstract class Source {

		public abstract String fragmentShader() ;
		
		public abstract String vertexShader() ;
		
	}

	public final GL20 gl;
	protected int curTexId = -1;
	protected int lastTexId = -1;

	public int getCurrentTextureID() {
		return this.curTexId;
	}

	public void setTexture(final LTexture texture) {
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
	}

	protected LTextureBind(GL20 gl) {
		this.gl = gl;
	}

	protected void bindTexture() {
		GLUtils.bindTexture(gl, curTexId);
	}

	@Override
	public void init() {

	}

}
