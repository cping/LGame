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

import loon.LRelease;
import loon.events.EventActionT;
import loon.events.EventActionTN;
import loon.geom.BooleanValue;

public interface IStateBuilder<T extends StateBase, TParent> extends LRelease {

	IStateBuilder<State<T>, IStateBuilder<T, TParent>> state(State<T> state);

	IStateBuilder<State<T>, IStateBuilder<T, TParent>> state(String stateName, State<T> state);

	IStateBuilder<State<T>, IStateBuilder<T, TParent>> state(String name);

	IStateBuilder<State<T>, IStateBuilder<T, TParent>> state();

	IStateBuilder<T, TParent> enter(EventActionT<T> onEnter);

	IStateBuilder<T, TParent> exit(EventActionT<T> onExit);

	IStateBuilder<T, TParent> update(EventActionTN<T, Long> onUpdate);

	IStateBuilder<T, TParent> condition(EventActionTN<T, BooleanValue> action, boolean v);

	IStateBuilder<T, TParent> condition(EventActionTN<T, BooleanValue> action);

	IStateBuilder<T, TParent> event(String identifier, EventActionT<T> action);

	TParent end();
}
