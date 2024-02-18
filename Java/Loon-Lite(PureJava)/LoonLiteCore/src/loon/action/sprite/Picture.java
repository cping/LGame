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
package loon.action.sprite;

import loon.LTexture;
import loon.LSystem;
import loon.canvas.Image;
import loon.utils.MathUtils;

/**
 * 显示图片用的精灵(本质上就是个显示图用的Entity)
 */
public class Picture extends Entity {

	public Picture(String fileName) {
		this(fileName, 0, 0);
	}

	public Picture(float x, float y) {
		this((LTexture) null, x, y);
	}

	public Picture(String fileName, float x, float y) {
		this(LSystem.loadTexture(fileName), x, y);
	}

	public Picture(Image image) {
		this(image.texture(), 0, 0);
	}

	public Picture(LTexture image) {
		this(image, 0, 0);
	}

	public Picture(LTexture image, float x, float y) {
		super(image);
		this.setLocation(x, y);
	}

	@Override
	public int hashCode() {
		int result = 59;
		result = LSystem.unite(result, _image.hashCode());
		result = LSystem.unite(result, _objectLocation.x);
		result = LSystem.unite(result, _objectLocation.y);
		result = LSystem.unite(result, _objectAlpha);
		result = LSystem.unite(result, _objectRotation);
		result = LSystem.unite(result, _scaleX);
		result = LSystem.unite(result, _scaleY);
		result = LSystem.unite(result, _width);
		result = LSystem.unite(result, _height);
		result = LSystem.unite(result, _baseColor.toIntBits());
		result = LSystem.unite(result, super.hashCode());
		return result;
	}

	public boolean equals(Picture p) {
		if (null == p) {
			return false;
		}
		if (this == p) {
			return true;
		}
		if (MathUtils.equal(this._width, p._width) && MathUtils.equal(this._height, p._height)) {
			if (_image.hashCode() == p._image.hashCode()) {
				return true;
			}
		}
		return false;
	}

	public Picture setImage(String fileName) {
		this.setTexture(fileName);
		return this;
	}

	public Picture setImage(LTexture image) {
		this.setTexture(image);
		return this;
	}

}
