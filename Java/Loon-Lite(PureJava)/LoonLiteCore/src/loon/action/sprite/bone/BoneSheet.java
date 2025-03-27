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
package loon.action.sprite.bone;

import loon.LSysException;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.GLEx.Direction;
import loon.utils.StringUtils;

public class BoneSheet {

	private LColor _color = LColor.white;

	private Vector2f _origin = new Vector2f();

	private LTexture _texture;

	public BoneSheet(String path) {
		load(path);
	}

	public BoneSheet(LTexture tex) {
		load(tex);
	}

	public BoneSheet load(String path) {
		if (StringUtils.isEmpty(path)) {
			throw new LSysException("The file " + path + " not found !");
		}
		return load(LTextures.loadTexture(path));
	}

	public BoneSheet load(LTexture tex) {
		if (tex == null) {
			throw new LSysException("The texture is null !");
		}
		this._texture = tex;
		return this;
	}

	public void draw(GLEx g, float x, float y, float sx, float sy, float clipX, float clipY, int clipWidth,
			int clipHeight, float ox, float oy, float angle, int m) {
		if (clipWidth <= 0 || clipHeight <= 0 || sx == 0 || sy == 0) {
			return;
		}
		_origin.set(ox, oy);
		Direction dir = null;
		if (m == 0) {
			dir = Direction.TRANS_NONE;
		} else {
			dir = Direction.TRANS_MIRROR;
		}
		g.draw(_texture, x, y, clipWidth, clipHeight, clipX, clipY, clipWidth, clipHeight, _color, angle,
				Bone.toScale(sx), Bone.toScale(sy), _origin, null, dir, false, true);
	}

	public LTexture getTexture() {
		return this._texture;
	}

}
