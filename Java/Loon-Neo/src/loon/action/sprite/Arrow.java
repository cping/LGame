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
package loon.action.sprite;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.map.MoveArrow;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.TArray;

/**
 * 移动路径精灵渲染用类
 */
public class Arrow extends Entity {

	private MoveArrow _arrow;

	private float _tileWidth;

	private float _tileHeight;

	public Arrow(String path) {
		this(path, LSystem.LAYER_TILE_SIZE);
	}

	public Arrow(String path, int clipArrowSize) {
		this(LTextures.loadTexture(path), clipArrowSize);
	}

	public Arrow(String path, int clipArrowSize, float tileWidth, float tileHeight) {
		this(LTextures.loadTexture(path), clipArrowSize, tileWidth, tileHeight);
	}

	public Arrow(LTexture tex, float tileWidth, float tileHeight) {
		this(tex, LSystem.LAYER_TILE_SIZE, tileWidth, tileHeight);
	}

	public Arrow(LTexture tex, int clipArrowSize) {
		this(tex, clipArrowSize, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE);
	}

	public Arrow(LTexture tex, int clipArrowSize, float tileWidth, float tileHeight) {
		this._arrow = new MoveArrow(tex, clipArrowSize);
		this._tileWidth = tileWidth;
		this._tileHeight = tileHeight;
		this.setRepaint(true);
	}

	public Arrow updatePath(TArray<Vector2f> path) {
		_arrow.dirty();
		_arrow.updatePath(path);
		return this;
	}

	public MoveArrow getArrowSet() {
		return _arrow;
	}

	public Arrow clearArrow() {
		_arrow.clear();
		return this;
	}

	@Override
	protected void repaint(GLEx g, float offsetX, float offsetY) {
		_arrow.draw(g, drawX(offsetX), drawY(offsetY), _tileWidth, _tileHeight, _baseColor);
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		_arrow.close();
	}
}
