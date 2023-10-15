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

public class FloatActionCounter extends FloatLimitedCounter {

	private EventAction _actionListener;

	public FloatActionCounter(float limit, EventAction actListener) {
		super(limit);
	}

	public FloatActionCounter(float limit) {
		super(limit);
	}

	public FloatActionCounter setActionListener(EventAction u) {
		this._actionListener = u;
		return this;
	}

	public EventAction getActionListener() {
		return this._actionListener;
	}

	@Override
	public float increment(float v) {
		boolean isLimitReachedBefore = isLimitReached();
		float result = super.increment(v);
		if (_actionListener != null && isLimitReached() && !isLimitReachedBefore) {
			HelperUtils.callEventAction(_actionListener, this);
		}
		return result;
	}

	@Override
	public float increment() {
		boolean isLimitReachedBefore = isLimitReached();
		float result = super.increment();
		if (_actionListener != null && isLimitReached() && !isLimitReachedBefore) {
			HelperUtils.callEventAction(_actionListener, this);
		}
		return result;
	}

	@Override
	public float reduction(float v) {
		boolean isLimitReachedBefore = isLimitReached();
		float result = super.reduction(v);
		if (_actionListener != null && isLimitReached() && !isLimitReachedBefore) {
			HelperUtils.callEventAction(_actionListener, this);
		}
		return result;
	}

	@Override
	public float reduction() {
		boolean isLimitReachedBefore = isLimitReached();
		float result = super.reduction();
		if (_actionListener != null && isLimitReached() && !isLimitReachedBefore) {
			HelperUtils.callEventAction(_actionListener, this);
		}
		return result;
	}
}
