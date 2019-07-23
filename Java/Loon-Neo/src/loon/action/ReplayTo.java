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
package loon.action;

import loon.utils.Array;
import loon.utils.StringKeyValue;

public class ReplayTo extends ActionEvent {

	private Array<ActionEvent> _repeatList;

	protected Array<ActionEvent> _replays;

	protected Array<ActionEvent> _cacheList;

	protected ActionEvent _currentEvent;

	protected boolean replay;

	protected int count;

	public ReplayTo(Array<ActionEvent> list) {
		this(list, true);
	}

	public ReplayTo(Array<ActionEvent> list, boolean rp) {
		this.replay = rp;
		this.set(list);
	}

	public ReplayTo set(Array<ActionEvent> list) {
		if (list == null || list.size() == 0) {
			return this;
		}
		this._cacheList = new Array<ActionEvent>(list);
		this._replays = new Array<ActionEvent>();
		for (; _cacheList.hashNext();) {
			ActionEvent result = _cacheList.next();
			if (result != null) {
				_replays.add(result);
			}
		}
		_cacheList.stopNext();
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		if (_replays == null) {
			_isCompleted = true;
			return;
		}
		if (_replays != null) {
			if (_currentEvent != null && !_currentEvent.isComplete()) {
				return;
			} else if (_currentEvent != null && _currentEvent.isComplete()) {
				if (_repeatList == null) {
					_repeatList = new Array<ActionEvent>();
				}
				if (!(_currentEvent instanceof ReplayTo)) {
					_repeatList.add(_currentEvent);
				}
			}
			ActionEvent event = _replays.last();
			if (event != _currentEvent && event != null) {
				_replays.remove();
				if (replay) {
					if (event instanceof ReplayTo && _repeatList != null && _repeatList.size() > 0) {
						Array<ActionEvent> tmp = new Array<ActionEvent>();
						for (; _repeatList.hashNext();) {
							tmp.add(_repeatList.next().reverse());
						}
						_repeatList.stopNext();
						((ReplayTo) event).set(tmp);
						_repeatList.clear();
						_repeatList.addAll(tmp);
					}
				}
				ActionControl.get().addAction(event, original);
				_currentEvent = event;
			} else {
				_isCompleted = true;
			}
		}
	}

	@Override
	public void onLoad() {
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		ReplayTo r = new ReplayTo(_cacheList, replay);
		r.set(this);
		return r;
	}

	@Override
	public ActionEvent reverse() {
		if (_cacheList == null || _cacheList.size() == 0) {
			return null;
		}
		ReplayTo r = new ReplayTo(_cacheList.reverse(), replay);
		r.set(this);
		return r;
	}

	@Override
	public String getName() {
		return "replay";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		if (_replays != null && _replays.size() > 0) {
			for (; _replays.hashNext();) {
				ActionEvent event = _replays.next();
				if (event != null) {
					builder.addValue(event.toString());
					builder.comma();
				}
			}
			_replays.stopNext();
		}
		return builder.toString();
	}
}
