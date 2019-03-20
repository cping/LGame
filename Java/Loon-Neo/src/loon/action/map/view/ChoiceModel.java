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
package loon.action.map.view;

public class ChoiceModel {
	final int _x;
	final int _y;
	final int _width;
	final int _height;

	public ChoiceModel(int x, int y, int w, int h) {
		this._x = x;
		this._y = y;
		this._width = w;
		this._height = h;
	}

	public boolean isChoiced(int x, int y) {
		return _x <= x && x < _x + _width && _y <= y && y < _y + _height;
	}
}
