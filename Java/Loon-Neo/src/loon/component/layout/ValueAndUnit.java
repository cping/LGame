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
package loon.component.layout;

import loon.LSystem;
import loon.geom.IV;
import loon.geom.ObservableValue;
import loon.geom.SetIV;
import loon.geom.TValue;
import loon.geom.XYChange;

public class ValueAndUnit implements IV<Number>, SetIV<Number> {

	public final static int UNITMODE_PERCENTAGE = 0;
	public final static int UNITMODE_PIXEL = 1;

	TValue<Number> _value;

	XYChange<Number> _onChangedObservable;

	int _unit;

	public ValueAndUnit(XYChange<Number> c, float v) {
		this._onChangedObservable = c;
		this._value = new TValue<Number>(v);
		this._unit = UNITMODE_PIXEL;
	}

	public boolean isPercentage() {
		return this._unit == ValueAndUnit.UNITMODE_PERCENTAGE;
	}

	public boolean isPixel() {
		return this._unit == ValueAndUnit.UNITMODE_PIXEL;
	}

	@Override
	public Number get() {
		return _value.get();
	}

	@Override
	public void set(Number v) {
		_value.set(v);
	}

	public int getUnit() {
		return _unit;
	}

	public ObservableValue<Number> observable() {
		return _value.observable(_onChangedObservable);
	}

	public ValueAndUnit setUnit(int u) {
		this._unit = u;
		return this;
	}

	public XYChange<Number> getChangedObservable() {
		return _onChangedObservable;
	}

	public ValueAndUnit setChangedObservable(XYChange<Number> changedObservable) {
		this._onChangedObservable = changedObservable;
		return this;
	}

	@Override
	public String toString() {
		switch (this._unit) {
		case ValueAndUnit.UNITMODE_PERCENTAGE: {
			float percentage = get().floatValue() * 100f;
			return percentage + "%";
		}
		case ValueAndUnit.UNITMODE_PIXEL: {
			float pixels = get().floatValue();
			return pixels + "px";
		}
		}
		return LSystem.NULL;
	}
}
