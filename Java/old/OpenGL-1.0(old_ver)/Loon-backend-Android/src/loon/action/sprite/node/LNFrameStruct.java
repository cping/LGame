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

import loon.action.sprite.SpriteBatch.BlendState;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.TextureUtils;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.graphics.opengl.LTextures;

public class LNFrameStruct {

	public Vector2f _anchor;

	public Vector2f _place;

	public int _orig_width;

	public int _orig_height;

	public int _size_width;

	public int _size_height;
	
	public Vector2f _textCoords;

	public LTexture _texture;
	
	public BlendState _state;

	public static LNFrameStruct InitWithImage(DefImage img) {
		LNFrameStruct struct2 = new LNFrameStruct();
		if (img.maskColor == null) {
			struct2._texture = LTextures.loadTexture(img.fileName,
					Format.LINEAR);
		} else {
			struct2._texture = TextureUtils.filterColor(img.fileName,
					img.maskColor, Format.LINEAR);
		}
		struct2._state = img.blend;
		struct2._textCoords = img.pos;
		struct2._orig_width = img.orig.x();
		struct2._orig_height = img.orig.y();
		struct2._size_width = img.size.x();
		struct2._size_height = img.size.y();
		struct2._anchor = img.anchor;
		struct2._place = img.place;
		return struct2;
	}

	public static LNFrameStruct InitWithImageName(String imgName) {
		DefImage image = LNDataCache.imageByKey(imgName);
		LNFrameStruct struct2 = new LNFrameStruct();
		if (image.maskColor == null) {
			struct2._texture = LTextures.loadTexture(image.fileName,
					Format.LINEAR);
		} else {
			struct2._texture = TextureUtils.filterColor(image.fileName,
					image.maskColor, Format.LINEAR);
		}
		struct2._state = image.blend;
		struct2._textCoords = image.pos;
		struct2._orig_width = image.orig.x();
		struct2._orig_height = image.orig.y();
		struct2._size_width = image.size.x();
		struct2._size_height = image.size.y();
		struct2._anchor = image.anchor;
		struct2._place = image.place;
		return struct2;
	}
}
