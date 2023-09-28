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
package loon.utils.processes.state;

import loon.events.EventAction;
import loon.geom.BooleanValue;

public class Condition {

	protected BooleanValue _predicate;
	protected EventAction _action;

	public Condition(BooleanValue p, EventAction a) {
		this._predicate = p;
		this._action = a;
	}

	public BooleanValue predicate() {
		return _predicate;
	}

	public void setPredicate(BooleanValue p) {
		this._predicate = p;
	}

	public EventAction action() {
		return _action;
	}

	public void setAction(EventAction a) {
		this._action = a;
	}

}
