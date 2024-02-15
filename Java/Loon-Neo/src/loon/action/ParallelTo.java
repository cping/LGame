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

import loon.utils.StringKeyValue;
import loon.utils.TArray;

/**
 * 如果要[并行](也就是旋转,变色什么的一起来)进行缓动动画,并而分别进行,请把要演示的ActionEvent注入此类,此类用于同时运行多个ActionEvent
 */
public class ParallelTo extends ActionEvent {

	private final TArray<ActionEvent> events;

	public ParallelTo(ActionEvent... eves) {
		events = new TArray<ActionEvent>(eves);
	}

	public ParallelTo(TArray<ActionEvent> list) {
		events = new TArray<ActionEvent>(list);
	}

	@Override
	public void update(long elapsedTime) {
		int over = 0;
		final TArray<ActionEvent> actions = this.events;
		for (int i = 0; i < actions.size; i++) {
			ActionEvent currentAction = actions.get(i);
			if (currentAction != null) {
				currentAction.update(elapsedTime);
				if (currentAction.isComplete()) {
					over++;
				}
			}
		}
		this._isCompleted = (over == actions.size);
	}

	@Override
	public ActionEvent start(ActionBind o) {
		super.start(o);
		final TArray<ActionEvent> actions = this.events;
		for (int i = 0; i < actions.size; i++) {
			ActionEvent currentAction = actions.get(i);
			if (currentAction != null) {
				currentAction.start(o);
			}
		}
		return this;
	}

	@Override
	public ActionEvent stop() {
		super.stop();
		TArray<ActionEvent> actions = this.events;
		for (int i = 0; i < actions.size; i++) {
			ActionEvent currentAction = actions.get(i);
			if (currentAction != null) {
				currentAction.stop();
			}
		}
		return this;
	}

	@Override
	public void onLoad() {
		final TArray<ActionEvent> actions = this.events;
		for (int i = 0; i < actions.size; i++) {
			ActionEvent currentAction = actions.get(i);
			if (currentAction != null) {
				currentAction.onLoad();
			}
		}
	}

	@Override
	public ActionEvent cpy() {
		final TArray<ActionEvent> tmp = new TArray<ActionEvent>(events.size);
		for (int i = 0, size = events.size; i < size; i++) {
			tmp.add(events.get(i).cpy());
		}
		ParallelTo p = new ParallelTo(tmp);
		p.set(this);
		return p;
	}

	@Override
	public ActionEvent reverse() {
		final TArray<ActionEvent> tmp = new TArray<ActionEvent>(events.size);
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

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		int size = events.size;
		if (events != null && size > 0) {
			for (int i = 0; i < size; i++) {
				ActionEvent event = events.get(i);
				if (event != null) {
					builder.addValue(event.toString());
					builder.comma();
				}
			}
		}
		return builder.toString();
	}
}
