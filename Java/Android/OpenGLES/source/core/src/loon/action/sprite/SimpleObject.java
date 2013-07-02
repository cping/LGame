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
package loon.action.sprite;

import loon.action.map.TileMap;

public abstract class SimpleObject extends SpriteBatchObject {

	public SimpleObject(float x, float y, float w, float h,
			Animation animation, TileMap tiles) {
		super(x, y, w, h, animation, tiles);
	}

	public SimpleObject(float x, float y, Animation animation, TileMap tiles) {
		super(x, y, animation, tiles);
	}

	@Override
	public void update(long elapsedTime) {
		animation.update(elapsedTime);
	}
}
