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

import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.timer.Duration;

public class DelayTo extends ActionEvent {

	private float delay;

	public DelayTo(float d) {
		this.delay = MathUtils.max(0f, d);
	}

	@Override
	public DelayTo reset() {
		super.reset();
		setDelay(Duration.ofS(this.delay) / 2);
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		_isCompleted = true;
	}

	public float getTimeDelay() {
		return delay;
	}

	@Override
	public void onLoad() {
		setDelay(Duration.ofS(this.delay) / 2);
	}

	@Override
	public ActionEvent cpy() {
		DelayTo d = new DelayTo(delay);
		d.set(this);
		return d;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "delay";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("delay", delay);
		return builder.toString();
	}

}
