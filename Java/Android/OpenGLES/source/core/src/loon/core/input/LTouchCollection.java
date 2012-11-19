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
package loon.core.input;

import java.util.Collection;

import loon.core.RefObject;
import loon.core.geom.Vector2f;

public class LTouchCollection extends java.util.LinkedList<LTouchLocation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean isConnected;

	public boolean AnyTouch() {
		for (LTouchLocation location : this) {
			if ((location.getState() == LTouchLocationState.Pressed)
					|| (location.getState() == LTouchLocationState.Dragged)) {
				return true;
			}
		}
		return false;
	}
	
	public final boolean getIsConnected() {
		return this.isConnected;
	}

	public final boolean getIsReadOnly() {
		return true;
	}

	public LTouchCollection() {
	}

	public LTouchCollection(Collection<LTouchLocation> locations) {
		super(locations);
	}

	public final void update() {
		for (int i = this.size() - 1; i >= 0; --i) {
			LTouchLocation t = this.get(i);
			switch (t.getState()) {
			case Pressed:
				t.setState(LTouchLocationState.Dragged);
				t.setPrevPosition(t.getPosition());
				this.set(i, t.clone());
				break;
			case Dragged:
				t.setPrevState(LTouchLocationState.Dragged);
				this.set(i, t.clone());
				break;
			case Released:
			case Invalid:
				remove(i);
				break;
			}
		}
	}

	public final int findIndexById(int id,
			RefObject<LTouchLocation> touchLocation) {
		for (int i = 0; i < this.size(); i++) {
			LTouchLocation location = this.get(i);
			if (location.getId() == id) {
				touchLocation.argvalue = this.get(i);
				return i;
			}
		}
		touchLocation.argvalue = new LTouchLocation();
		return -1;
	}

	public final void add(int id, Vector2f position) {
		for (int i = 0; i < size(); i++) {
			if (this.get(i).id == id) {
				clear();
			}
		}
		add(new LTouchLocation(id, LTouchLocationState.Pressed, position));
	}

	public final void add(int id, float x, float y) {
		for (int i = 0; i < size(); i++) {
			if (this.get(i).id == id) {
				clear();
			}
		}
		add(new LTouchLocation(id, LTouchLocationState.Pressed, x, y));
	}

	public final void update(int id, LTouchLocationState state, float posX,
			float posY) {
		if (state == LTouchLocationState.Pressed) {
			throw new IllegalArgumentException(
					"Argument 'state' cannot be TouchLocationState.Pressed.");
		}

		for (int i = 0; i < size(); i++) {
			if (this.get(i).id == id) {
				LTouchLocation touchLocation = this.get(i);
				touchLocation.setPosition(posX, posY);
				touchLocation.setState(state);
				this.set(i, touchLocation);
				return;
			}
		}
		clear();
	}

	public final void update(int id, LTouchLocationState state, Vector2f position) {
		if (state == LTouchLocationState.Pressed) {
			throw new IllegalArgumentException(
					"Argument 'state' cannot be TouchLocationState.Pressed.");
		}

		for (int i = 0; i < size(); i++) {
			if (this.get(i).id == id) {
				LTouchLocation touchLocation = this.get(i);
				touchLocation.setPosition(position);
				touchLocation.setState(state);
				this.set(i, touchLocation);
				return;
			}
		}
		clear();
	}
}
