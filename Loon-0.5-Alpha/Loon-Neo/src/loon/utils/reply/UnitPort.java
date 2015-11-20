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
package loon.utils.reply;

import loon.event.Updateable;

public abstract class UnitPort extends Port<Object> implements Updateable {

	public static UnitPort toPort(final Updateable update) {
		return new UnitPort() {
			public void onEmit() {
				update.action(this);
			}
		};
	}

	public abstract void onEmit();

	public UnitPort andThen(final UnitPort after) {
		final UnitPort before = this;
		return new UnitPort() {
			public void onEmit() {
				before.onEmit();
				after.onEmit();
			}
		};
	}

	@Override
	public final void onEmit(Object event) {
		onEmit();
	}

	@Override
	public void action(Object o) {
		onEmit();
	}
}
