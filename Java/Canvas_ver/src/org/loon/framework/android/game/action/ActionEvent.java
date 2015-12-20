package org.loon.framework.android.game.action;

import org.loon.framework.android.game.core.graphics.component.Actor;
import org.loon.framework.android.game.core.timer.LTimer;

/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public abstract class ActionEvent {

	private LTimer timer;

	private ActionListener actionListener;

	boolean firstTick, isComplete, isInit;

	Actor original;

	Object tag;

	public ActionEvent() {
		timer = new LTimer(0);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public void setDelay(long d) {
		timer.setDelay(d);
	}

	public void step(long elapsedTime) {
		if (original == null) {
			return;
		}
		if (firstTick) {
			this.firstTick = false;
			this.timer.refresh();
		} else {
			if (timer.action(elapsedTime)) {
				update(elapsedTime);
			}
		}
		if (actionListener != null) {
			actionListener.process(original);
		}
	}

	public Object getOriginal() {
		return original;
	}

	public void start(Actor o) {
		if (o == null) {
			return;
		}
		this.original = o;
		this.timer.refresh();
		this.firstTick = true;
		this.isComplete = false;
		this.isInit = false;
		if (actionListener != null) {
			actionListener.start(o);
		}
	}

	public abstract void update(long elapsedTime);

	public abstract void onLoad();

	public void stop() {
		if (actionListener != null) {
			actionListener.stop(original);
		}
	}

	public abstract boolean isComplete();

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public final void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
}
