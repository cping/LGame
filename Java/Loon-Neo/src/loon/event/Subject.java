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

import loon.utils.TArray;

public class Subject {

	private TArray<Observer> _observers;

	private int _id;

	private int _notifycode;

	private Observed _parentObject;

	public Subject(int id, Observed parentObject) {
		this._id = id;
		this._parentObject = parentObject;
		_observers = new TArray<Observer>();
	}

	public Subject addObserber(Observer o) {
		_observers.add(o);
		return this;
	}

	public boolean removeObserber(Observer o) {
		return _observers.remove(o);
	}

	public Subject notify(int code) {
		this._notifycode = code;
		InputMapEvent e = InputMapEvent.setID(_id);
		InputMapEvent eve = InputMapEvent.setType(e.getCode());
		for (Observer obs : _observers) {
			obs.onNotify(eve, _parentObject);
		}
		return this;
	}

	public int getID() {
		return this._id;
	}

	public int getNotify() {
		return this._notifycode;
	}

	public Observed getParentObject() {
		return _parentObject;
	}
}
