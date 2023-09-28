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

import loon.events.EventAction;
import loon.utils.HelperUtils;

public class ActionCounter extends LimitedCounter {

	private EventAction _actionListener;

	public ActionCounter(int limit, EventAction actListener) {
		super(limit);
	}

	public ActionCounter(int limit) {
		super(limit);
	}

	public ActionCounter setActionListener(EventAction u) {
		this._actionListener = u;
		return this;
	}

	public EventAction getActionListener() {
		return this._actionListener;
	}

	@Override
	public int increment(int v) {
		boolean isLimitReachedBefore = isLimitReached();
		int result = super.increment(v);
		if (_actionListener != null && isLimitReached() && !isLimitReachedBefore) {
			HelperUtils.callEventAction(_actionListener, this);
		}
		return result;
	}

	@Override
	public int increment() {
		boolean isLimitReachedBefore = isLimitReached();
		int result = super.increment();
		if (_actionListener != null && isLimitReached() && !isLimitReachedBefore) {
			HelperUtils.callEventAction(_actionListener, this);
		}
		return result;
	}

	@Override
	public int reduction(int v) {
		boolean isLimitReachedBefore = isLimitReached();
		int result = super.reduction(v);
		if (_actionListener != null && isLimitReached() && !isLimitReachedBefore) {
			HelperUtils.callEventAction(_actionListener, this);
		}
		return result;
	}

	@Override
	public int reduction() {
		boolean isLimitReachedBefore = isLimitReached();
		int result = super.reduction();
		if (_actionListener != null && isLimitReached() && !isLimitReachedBefore) {
			HelperUtils.callEventAction(_actionListener, this);
		}
		return result;
	}
}
