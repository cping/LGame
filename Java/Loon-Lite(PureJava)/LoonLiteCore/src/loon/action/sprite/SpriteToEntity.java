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
import loon.geom.RectBox;

class SpriteToEntity extends Entity {

	private ISprite obj;

	public SpriteToEntity(ISprite spr) {
		this.obj = spr;
	}

	@Override
	public float getWidth() {
		return obj.getWidth();
	}

	@Override
	public float getHeight() {
		return obj.getHeight();
	}

	@Override
	public float getX() {
		return obj.getX();
	}

	@Override
	public float getY() {
		return obj.getY();
	}

	@Override
	public int x() {
		return obj.x();
	}

	@Override
	public int y() {
		return obj.y();
	}

	@Override
	public RectBox getCollisionBox() {
		return obj.getCollisionBox();
	}

	@Override
	public LTexture getBitmap() {
		return obj.getBitmap();
	}

	@Override
	public String getName() {
		return obj.getName();
	}

	@Override
	public Object getTag() {
		return obj.getTag();
	}

}
