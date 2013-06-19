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

import loon.core.RefObject;
import loon.core.geom.Vector2f;
import loon.core.input.LInputFactory.Touch;

public class LTouchLocation {

	protected int id;

	private Vector2f position;

	private Vector2f previousPosition;

	private LTouchLocationState state = LTouchLocationState.Invalid;

	private LTouchLocationState previousState = LTouchLocationState.Invalid;

	private float pressure;

	private float previousPressure;

	public boolean isDrag() {
		return Touch.isDrag()
				&& (previousState == LTouchLocationState.Dragged && state == LTouchLocationState.Dragged);
	}

	public boolean isDown() {
		return Touch.isDown()
				&& (previousState == LTouchLocationState.Pressed && (state == LTouchLocationState.Pressed || state == LTouchLocationState.Dragged));
	}

	public boolean isUp() {
		return Touch.isUp()
				&& (previousState == LTouchLocationState.Pressed || previousState == LTouchLocationState.Dragged)
				&& (state == LTouchLocationState.Released);
	}

	public int getId() {
		return id;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(float x, float y) {
		previousPosition.set(position);
		position.set(x, y);
	}

	public void setPosition(Vector2f value) {
		previousPosition.set(position);
		position.set(value);
	}

	public float getPressure() {
		return pressure;
	}

	public float getPrevPressure() {
		return previousPressure;
	}

	public Vector2f getPrevPosition() {
		return previousPosition;
	}

	public void setPrevPosition(Vector2f value) {
		previousPosition.set(value);
	}

	public LTouchLocationState getState() {
		return state;
	}

	public void setState(LTouchLocationState value) {
		previousState = state;
		state = value;
	}

	public LTouchLocationState getPrevState() {
		return previousState;
	}

	public void setPrevState(LTouchLocationState value) {
		previousState = value;
	}

	public LTouchLocation() {
		this(0, LTouchLocationState.Invalid, new Vector2f(0, 0),
				LTouchLocationState.Invalid, new Vector2f(0, 0));
	}

	public LTouchLocation(int aId, LTouchLocationState aState,
			Vector2f aPosition, LTouchLocationState aPreviousState,
			Vector2f aPreviousPosition) {
		id = aId;
		position = aPosition;
		previousPosition = aPreviousPosition;
		state = aState;
		previousState = aPreviousState;
		pressure = 0.0f;
		previousPressure = 0.0f;
	}

	public LTouchLocation(int aId, LTouchLocationState aState,
			Vector2f aPosition) {
		id = aId;
		position = aPosition;
		previousPosition = Vector2f.ZERO();
		state = aState;
		previousState = LTouchLocationState.Invalid;
		pressure = 0.0f;
		previousPressure = 0.0f;
	}

	public LTouchLocation(int aId, LTouchLocationState aState, float x, float y) {
		id = aId;
		position = new Vector2f(x, y);
		previousPosition = Vector2f.ZERO();
		state = aState;
		previousState = LTouchLocationState.Invalid;
		pressure = 0.0f;
		previousPressure = 0.0f;
	}

	public LTouchLocation(int aId, LTouchLocationState aState,
			Vector2f aPosition, float aPressure,
			LTouchLocationState aPreviousState, Vector2f aPreviousPosition,
			float aPreviousPressure) {
		id = aId;
		position = aPosition;
		previousPosition = aPreviousPosition;
		state = aState;
		previousState = aPreviousState;
		pressure = aPressure;
		previousPressure = aPreviousPressure;
	}

	public LTouchLocation(int aId, LTouchLocationState aState,
			Vector2f aPosition, float aPressure) {
		id = aId;
		position = aPosition;
		previousPosition = Vector2f.ZERO();
		state = aState;
		previousState = LTouchLocationState.Invalid;
		pressure = aPressure;
		previousPressure = 0.0f;
	}

	public boolean tryGetPreviousLocation(
			RefObject<LTouchLocation> aPreviousLocation) {
		if (aPreviousLocation.argvalue == null) {
			aPreviousLocation.argvalue = new LTouchLocation();
		}
		if (previousState == LTouchLocationState.Invalid) {
			aPreviousLocation.argvalue.id = -1;
			aPreviousLocation.argvalue.state = LTouchLocationState.Invalid;
			aPreviousLocation.argvalue.position = Vector2f.ZERO();
			aPreviousLocation.argvalue.previousState = LTouchLocationState.Invalid;
			aPreviousLocation.argvalue.previousPosition = Vector2f.ZERO();
			aPreviousLocation.argvalue.pressure = 0.0f;
			aPreviousLocation.argvalue.previousPressure = 0.0f;
			return false;
		} else {
			aPreviousLocation.argvalue.id = this.id;
			aPreviousLocation.argvalue.state = this.previousState;
			aPreviousLocation.argvalue.position = this.previousPosition.cpy();
			aPreviousLocation.argvalue.previousState = LTouchLocationState.Invalid;
			aPreviousLocation.argvalue.previousPosition = Vector2f.ZERO();
			aPreviousLocation.argvalue.pressure = this.previousPressure;
			aPreviousLocation.argvalue.previousPressure = 0.0f;
			return true;
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof LTouchLocation) {
			result = equals((LTouchLocation) obj);
		}
		return result;
	}

	public boolean equals(LTouchLocation other) {
		return (id == other.id)
				&& (this.getPosition().equals(other.getPosition()))
				&& (this.previousPosition.equals(other.previousPosition));
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "Touch id:" + id + " state:" + state + " position:" + position
				+ " pressure:" + pressure + " prevState:" + previousState
				+ " prevPosition:" + previousPosition + " previousPressure:"
				+ previousPressure;
	}

	@Override
	public LTouchLocation clone() {
		LTouchLocation varCopy = new LTouchLocation();

		varCopy.id = this.id;
		varCopy.position.set(this.position);
		varCopy.previousPosition.set(this.previousPosition);
		varCopy.state = this.state;
		varCopy.previousState = this.previousState;
		varCopy.pressure = this.pressure;
		varCopy.previousPressure = this.previousPressure;

		return varCopy;
	}
}
