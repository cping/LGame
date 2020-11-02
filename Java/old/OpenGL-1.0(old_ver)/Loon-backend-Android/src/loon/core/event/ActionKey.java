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
package loon.core.event;

public class ActionKey {

	public static final int NORMAL = 0;

	public static final int DETECT_INITIAL_PRESS_ONLY = 1;

	private static final int STATE_RELEASED = 0;

	private static final int STATE_PRESSED = 1;

	private static final int STATE_WAITING_FOR_RELEASE = 2;

	private int mode;

	private int amount;

	private int state;
	
	public boolean isReturn;

	public ActionKey() {
		this(NORMAL);
	}

	public ActionKey(int mode) {
		this.mode = mode;
		reset();
	}
	
	public void act(long elapsedTime){
		
	}

	public void reset() {
		state = STATE_RELEASED;
		amount = 0;
	}

	public void press() {
		if (state != STATE_WAITING_FOR_RELEASE) {
			amount++;
			state = STATE_PRESSED;
		}
	}

	public void release() {
		state = STATE_RELEASED;
	}

	public boolean isPressed() {
		if (amount != 0) {
			if (state == STATE_RELEASED) {
				amount = 0;
			} else if (mode == DETECT_INITIAL_PRESS_ONLY) {
				state = STATE_WAITING_FOR_RELEASE;
				amount = 0;
			}
			return true;
		}
		return false;
	}
}
