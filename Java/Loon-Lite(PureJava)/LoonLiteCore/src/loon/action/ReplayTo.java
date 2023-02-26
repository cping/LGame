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

	private Array<ActionEvent> repeatList;

	protected Array<ActionEvent> replays;

	protected Array<ActionEvent> _cache_list;

	protected ActionEvent currentEvent;

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
		this._cache_list = new Array<ActionEvent>(list);
		this.replays = new Array<ActionEvent>();
		for (; _cache_list.hashNext();) {
			ActionEvent result = _cache_list.next();
			if (result != null) {
				replays.add(result);
			}
		}
		_cache_list.stopNext();
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		if (replays == null) {
			_isCompleted = true;
			return;
		}
		if (replays != null) {
			if (currentEvent != null && !currentEvent.isComplete()) {
				return;
			} else if (currentEvent != null && currentEvent.isComplete()) {
				if (repeatList == null) {
					repeatList = new Array<ActionEvent>();
				}
				if (!(currentEvent instanceof ReplayTo)) {
					repeatList.add(currentEvent);
				}
			}
			ActionEvent event = replays.last();
			if (event != currentEvent && event != null) {
				replays.remove();
				if (replay) {
					if (event instanceof ReplayTo && repeatList != null && repeatList.size() > 0) {
						Array<ActionEvent> tmp = new Array<ActionEvent>();
						for (; repeatList.hashNext();) {
							tmp.add(repeatList.next().reverse());
						}
						repeatList.stopNext();
						((ReplayTo) event).set(tmp);
						repeatList.clear();
						repeatList.addAll(tmp);
					}
				}
				ActionControl.get().addAction(event, original);
				currentEvent = event;
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
		ReplayTo r = new ReplayTo(_cache_list, replay);
		r.set(this);
		return r;
	}

	@Override
	public ActionEvent reverse() {
		if (_cache_list == null || _cache_list.size() == 0) {
			return null;
		}
		ReplayTo r = new ReplayTo(_cache_list.reverse(), replay);
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
		if (replays != null && replays.size() > 0) {
			for (; replays.hashNext();) {
				ActionEvent event = replays.next();
				if (event != null) {
					builder.addValue(event.toString());
					builder.comma();
				}
			}
			replays.stopNext();
		}
		return builder.toString();
	}
}
