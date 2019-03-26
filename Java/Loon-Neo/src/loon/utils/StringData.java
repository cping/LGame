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
package loon.utils;

import loon.canvas.LColor;

public class StringData {

	private StringKeyValue text;
	private int posX, posY;
	private LColor color;

	public StringData(String message, int x, int y) {
		this(new StringKeyValue("StringData"), x, y, LColor.white);
	}

	public StringData(StringKeyValue message, int x, int y) {
		this(message, x, y, LColor.white);
	}

	public StringData(StringKeyValue message, int x, int y, LColor c) {
		this.text = message;
		this.posX = x;
		this.posY = y;
		color = c;
	}

	public StringKeyValue add(CharSequence ch) {
		return text.addValue(ch);
	}

	public StringKeyValue getText() {
		return text;
	}

	public int getX() {
		return posX;
	}

	public void setX(int x) {
		posX = x;
	}

	public int getY() {
		return posY;
	}

	public void setY(int y) {
		posY = y;
	}

	public void setColor(LColor newColor) {
		color = newColor;
	}

	public LColor getColor() {
		return color.cpy();
	}
}
