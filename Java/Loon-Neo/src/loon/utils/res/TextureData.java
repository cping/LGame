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
package loon.utils.res;

import loon.LSystem;
import loon.geom.XY;

public class TextureData implements XY {

	protected int _sourceW = 0;

	protected int _sourceH = 0;

	protected int _x = 0;

	protected int _y = 0;

	protected int _offX = 0;

	protected int _offY = 0;

	protected int _w = 0;

	protected int _h = 0;

	protected String _name = LSystem.UNKNOWN;

	protected TextureData() {

	}

	public int x() {
		return _x;
	}

	public int y() {
		return _y;
	}

	public int w() {
		return _w;
	}

	public int h() {
		return _h;
	}

	public int offX() {
		return _offX;
	}

	public int offY() {
		return _offY;
	}

	public int sourceW() {
		return _sourceW;
	}

	public int sourceH() {
		return _sourceH;
	}

	@Override
	public float getX() {
		return _x;
	}

	@Override
	public float getY() {
		return _y;
	}

	public String name() {
		return _name;
	}

}
