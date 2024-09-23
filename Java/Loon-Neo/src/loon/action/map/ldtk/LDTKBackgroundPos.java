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
package loon.action.map.ldtk;

import loon.Json;
import loon.geom.RectBox;
import loon.geom.Vector2f;

public class LDTKBackgroundPos {

	protected float _top;

	protected float _left;

	protected Vector2f _scale;

	protected RectBox _cropRect;
	
	protected boolean _supportBackground;

	public LDTKBackgroundPos(Json.Object o) {
		if (o == null) {
			_scale = new Vector2f(1f);
			_cropRect = new RectBox();
		} else {
			Json.Array topLeft = o.getArray("topLeftPx");
			_top = topLeft.getNumber(0);
			_left = topLeft.getNumber(1);
			Json.Array scale = o.getArray("scale");
			_scale = new Vector2f(scale.getNumber(0), scale.getNumber(1));
			Json.Array cropRect = o.getArray("cropRect");
			_cropRect = new RectBox(cropRect.getNumber(0), cropRect.getNumber(1), cropRect.getNumber(2),
					cropRect.getNumber(3));
			_supportBackground = true;
		}
	}
	
	public boolean isSupport() {
		return _supportBackground;
	}

	public float getTop() {
		return this._top;
	}

	public float getLeft() {
		return this._left;
	}

	public Vector2f getScale() {
		return this._scale;
	}

	public RectBox getCropRect() {
		return this._cropRect;
	}

}
