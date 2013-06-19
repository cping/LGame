package loon.core.input;

import loon.core.input.LInputFactory.Key;
import loon.utils.collection.ArrayByte;

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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class LKey {

	int type;

	int keyCode;

	char keyChar;

	double timer;
	
	public LKey(byte[] out) {
		in(out);
	}

	LKey() {

	}

	public double getTimer() {
		return timer;
	}
	
	LKey(LKey key) {
		this.type = key.type;
		this.keyCode = key.keyCode;
		this.keyChar = key.keyChar;
	}

	public boolean equals(LKey e) {
		if (e == null) {
			return false;
		}
		if (e == this) {
			return true;
		}
		if (e.type == type && e.keyCode == keyCode && e.keyChar == keyChar) {
			return true;
		}
		return false;
	}

	public char getKeyChar() {
		return keyChar;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public int getType() {
		return type;
	}

	public boolean isDown() {
		return type == Key.KEY_DOWN;
	}

	public boolean isUp() {
		return type == Key.KEY_UP;
	}

	public byte[] out() {
		ArrayByte touchByte = new ArrayByte();
		touchByte.writeInt(type);
		touchByte.writeInt(keyCode);
		touchByte.writeInt(keyChar);
		return touchByte.getData();
	}

	public void in(byte[] out) {
		ArrayByte touchByte = new ArrayByte(out);
		type = touchByte.readInt();
		keyCode = touchByte.readInt();
		keyChar = (char) touchByte.readInt();
		type = touchByte.readInt();
	}
}
