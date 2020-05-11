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
package loon.event;

public class InputMapEvent {

	public static int NO_TYPE = -1;

	public static int CONFIRM = 0;

	public static int CANCEL = 1;

	public static int PAINT = 2;

	public static int NEXT = 3;

	public static int INFO = 4;

	public static int MOVE = 5;

	public static int BATTLE = 6;

	public static InputMapEvent setID(int id) {
		return setID(NO_TYPE, id);
	}

	public static InputMapEvent setID(int event, int id) {
		return new InputMapEvent((event & 0xFFFF0000) | id);
	}

	public static InputMapEvent getID(int event) {
		return new InputMapEvent(event & 0x0000FFFF);
	}

	public static InputMapEvent setType(int type) {
		return setType(NO_TYPE, type);
	}

	public static InputMapEvent setType(int event, int type) {
		return new InputMapEvent((event & 0x0000FFFF) | (type << 16));
	}

	public static InputMapEvent getType(int event) {
		return new InputMapEvent((event & 0xFFFF0000) >> 16);
	}

	private int _code = NO_TYPE;

	public InputMapEvent() {
		this(NO_TYPE);
	}

	public InputMapEvent(int c) {
		this._code = c;
	}

	public int getCode() {
		return this._code;
	}
}
