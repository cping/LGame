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

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;

public class LNLabelAtlas extends LNAtlasNode {
	
	private int _charWidth;
	
	private char _startchar;
	
	private String _text;
	
	private LabelType _type;
	
	public LNFrameStruct fs;

	public LNLabelAtlas() {
		this._type = LabelType.TEXT_ALIGNMENT_LEFT;
	}

	public LNLabelAtlas(String fsName, LabelType type, String text,
			char startchar, int itemWidth, int itemHeight, int charWidth) {
		super(fsName, itemWidth, itemHeight);
		this.fs = LNDataCache.getFrameStruct(fsName);
		super._left = (int) this.fs._textCoords.x;
		super._top = (int) this.fs._textCoords.y;
		this._type = type;
		this._charWidth = charWidth;
		this._startchar = startchar;
		this.setString(text);
	}

	private float[] pos;

	private float[] scale;

	private float rotation;

	@Override
	public void draw(SpriteBatch batch) {
		if (super._visible) {
			pos = super.convertToWorldPos();
			scale = super.convertToWorldScale();
			rotation = super.convertToWorldRot();
			int size = _text.length();
			if (this._type == LabelType.TEXT_ALIGNMENT_LEFT) {
				for (int i = 0; i < size; i++) {
					super._textureAtlas.draw(i, batch, pos[0] + i
							* this._charWidth, pos[1], rotation, scale[0],
							scale[1], batch.getColor());
				}
			} else if (this._type == LabelType.TEXT_ALIGNMENT_RIGHT) {
				for (int j = 0; j < size; j++) {
					super._textureAtlas.draw(j, batch, pos[0]
							- (size * this._charWidth) + (j * this._charWidth),
							pos[1], rotation, scale[0], scale[1],
							batch.getColor());
				}
			} else {
				for (int k = 0; k < size; k++) {
					super._textureAtlas.draw(k, batch,
					pos[0] - ((size * this._charWidth) / 2)
							+ (k * this._charWidth), pos[1], rotation,
							scale[0], scale[1], batch.getColor());
				}
			}
		}
	}

	public void setString(String text) {
		this._text = text;
		super._textureAtlas.resetRect();
		for (int i = 0; i < this._text.length(); i++) {
			int num2 = this._text.charAt(i) - this._startchar;
			super._textureAtlas.addRect(new RectBox((super._left)
					+ (num2 * super._itemWidth), super._top,
					super._itemWidth, super._itemHeight));
		}
	}

	public enum LabelType {
		TEXT_ALIGNMENT_LEFT, TEXT_ALIGNMENT_RIGHT, TEXT_ALIGNMENT_CENTER
	}
}
