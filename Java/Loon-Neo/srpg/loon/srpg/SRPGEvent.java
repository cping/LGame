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
package loon.srpg;

import loon.event.GameTouch;

public class SRPGEvent {

	public static int EVENT_EMPTY = -1;

	public static int EVENT_SUBMIT = 0;

	public static int EVENT_CANCEL = 1;

	public int x;

	public int y;

	int type;

	boolean isExist;

	public SRPGEvent() {
		reset();
	}

	public SRPGEvent(int x, int y, int type) {
		set(x, y, type);
	}

	public SRPGEvent(GameTouch e, int type) {
		set(e, type);
	}

	public SRPGEvent(int type) {
		set(type);
	}

	public void reset() {
		this.isExist = false;
		this.type = EVENT_EMPTY;
		this.x = 0;
		this.y = 0;
		this.type = 0;
	}

	public void set(int x, int y, int type) {
		this.isExist = true;
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public void set(GameTouch e, int type) {
		this.set(e.x(), e.y(), type);
	}

	public void set(int type) {
		this.set(-1, -1, type);
	}

	public boolean isExist() {
		return isExist;
	}

	public void setExist(boolean flag) {
		isExist = flag;
	}

	public boolean queueExist() {
		return isExist = !isExist;
	}

}