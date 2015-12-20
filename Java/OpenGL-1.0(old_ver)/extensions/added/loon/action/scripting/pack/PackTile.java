package loon.action.scripting.pack;

import loon.core.graphics.device.LColor;
import loon.core.graphics.opengl.LTexturePack;


/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public abstract class PackTile {

	public abstract int width();

	public abstract int height();

	public abstract void update(long t);

	public abstract void draw(LTexturePack pack, float x, float y, LColor[] c);

	public abstract boolean isSolid();

	public abstract void setSolid(boolean s);

	public PackTile touch(PackSprite sprite, float x, float y) {
		return this;
	}

}
