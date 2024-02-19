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
import loon.geom.Affine2f;
import loon.utils.StringKeyValue;
import loon.utils.reply.GoFuture;

public abstract class Painter extends TextureSource {

	public Object Tag;

	public static final int TOP_LEFT = 0;

	public static final int TOP_RIGHT = 1;

	public static final int BOTTOM_RIGHT = 2;

	public static final int BOTTOM_LEFT = 3;

	public static LTexture firstFather(LTexture texture) {
		if (texture.getParent() == null) {
			return texture;
		} else {
			return firstFather(texture.getParent());
		}
	}

	public abstract LTexture texture();

	public abstract float width();

	public abstract float height();

	public abstract float getDisplayWidth();

	public abstract float getDisplayHeight();

	public abstract float sx();

	public abstract float sy();

	public abstract float tx();

	public abstract float ty();

	public abstract void addToBatch(BaseBatch batch, int tint, Affine2f tx, float x, float y, float width,
			float height);

	public abstract void addToBatch(BaseBatch batch, int tint, Affine2f tx, float dx, float dy, float dw, float dh,
			float sx, float sy, float sw, float sh);

	public abstract void quad(BaseBatch batch, int tint, Affine2f tx, float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4);

	@Override
	public boolean isLoaded() {
		return _isLoaded;
	}

	@Override
	public Painter draw() {
		return this;
	}

	@Override
	public GoFuture<Painter> tileAsync() {
		return GoFuture.success(this);
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Painter");
		builder.kv("size", width() + "x" + height()).comma().kv("xOff", sx()).comma().kv("yOff", sy()).comma()
				.kv("widthRatio", tx()).comma().kv("heightRatio", ty());
		return builder.toString() + " <- " + texture();
	}
}
