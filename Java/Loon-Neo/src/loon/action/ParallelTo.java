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

import loon.utils.TArray;

/**
 * 同时运行多个ActionEvent
 */
public class ParallelTo extends ActionEvent {

	private TArray<ActionEvent> events = null;

	public ParallelTo(ActionEvent... eves) {
		events = new TArray<ActionEvent>(eves);
	}

	public ParallelTo(TArray<ActionEvent> list) {
		events = new TArray<ActionEvent>(list);
	}

	@Override
	public void update(long elapsedTime) {
		int over = 0;
		TArray<ActionEvent> actions = this.events;
		for (int i = 0, n = actions.size; i < n; i++) {
			ActionEvent currentAction = actions.get(i);
			if (currentAction != null) {
				if (!currentAction.isInit) {
					currentAction.isInit = true;
					currentAction.onLoad();
				}
				if (!currentAction.isComplete()) {
					currentAction.step(elapsedTime);
				} else {
					currentAction.stop();
					over++;
				}
			}
		}
		this._isCompleted = (over == actions.size);
	}

	@Override
	public void onLoad() {
		TArray<ActionEvent> actions = this.events;
		for (int i = 0, n = actions.size; i < n; i++) {
			ActionEvent currentAction = actions.get(i);
			if (currentAction != null) {
				currentAction.start(original);
			}
		}
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		TArray<ActionEvent> tmp = new TArray<ActionEvent>(events.size);
		for (int i = 0, size = events.size; i < size; i++) {
			tmp.add(events.get(i).cpy());
		}
		ParallelTo p = new ParallelTo(tmp);
		p.set(this);
		return p;
	}

	@Override
	public ActionEvent reverse() {
		TArray<ActionEvent> tmp = new TArray<ActionEvent>(events.size);
		for (int i = 0, size = events.size; i < size; i++) {
			tmp.add(events.get(i).reverse());
		}
		ParallelTo p = new ParallelTo(tmp);
		p.set(this);
		return p;
	}

	@Override
	public String getName() {
		return "parallel";
	}
}
