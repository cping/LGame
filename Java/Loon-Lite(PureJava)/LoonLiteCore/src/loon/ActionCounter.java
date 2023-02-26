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
package loon;

import loon.events.Updateable;

public class ActionCounter extends LimitedCounter {

	private Updateable actListener;

	public ActionCounter(int limit, Updateable actListener) {
		super(limit);
	}

	public ActionCounter(int limit) {
		super(limit);
	}

	public void setActionListener(Updateable u) {
		this.actListener = u;
	}

	public Updateable getActionListener() {
		return this.actListener;
	}

	@Override
	public int increment(int v) {
		boolean isLimitReachedBefore = isLimitReached();
		int result = super.increment(v);
		if (actListener != null && isLimitReached() && !isLimitReachedBefore) {
			actListener.action(this);
		}
		return result;
	}

	@Override
	public int increment() {
		boolean isLimitReachedBefore = isLimitReached();
		int result = super.increment();
		if (actListener != null && isLimitReached() && !isLimitReachedBefore) {
			actListener.action(this);
		}
		return result;
	}

	@Override
	public int reduction(int v) {
		boolean isLimitReachedBefore = isLimitReached();
		int result = super.reduction(v);
		if (actListener != null && isLimitReached() && !isLimitReachedBefore) {
			actListener.action(this);
		}
		return result;
	}

	@Override
	public int reduction() {
		boolean isLimitReachedBefore = isLimitReached();
		int result = super.reduction();
		if (actListener != null && isLimitReached() && !isLimitReachedBefore) {
			actListener.action(this);
		}
		return result;
	}
}
