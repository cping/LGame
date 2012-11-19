/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite.node;

import loon.core.graphics.opengl.LTexture;

public class LNAtlasNode extends LNNode {

	protected int _itemHeight;

	protected int _itemsPerColumn;

	protected int _itemsPerRow;

	protected int _itemWidth;

	protected LNTextureAtlas _textureAtlas;

	public LNAtlasNode() {
		this._itemsPerRow = 0;
		this._itemsPerColumn = 0;
		this._itemWidth = 0;
		this._itemHeight = 0;
	}

	public LNAtlasNode(String fsName, int tileWidth, int tileHeight) {
		try {
			this._itemWidth = tileWidth;
			this._itemHeight = tileHeight;
			LTexture texture = LNDataCache.getFrameStruct(fsName)._texture;
			this._itemsPerRow = texture.getWidth() / tileWidth;
			this._itemsPerColumn = texture.getHeight() / tileHeight;
			this._textureAtlas = new LNTextureAtlas(texture, this._itemsPerRow
					* this._itemsPerColumn);
		} catch (Exception ex) {
			throw new RuntimeException("LNAtlasNode Exception in the data load : " + fsName);
		}
	}
}
