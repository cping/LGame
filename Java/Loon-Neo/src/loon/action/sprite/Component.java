/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.sprite;

import loon.geom.BooleanValue;

public abstract class Component {

	protected Entity entity;

	private BooleanValue paused = new BooleanValue(false);

	public final Entity getEntity() {
		return entity;
	}

	final void setEntity(Entity entity) {
		this.entity = entity;
	}

	public final boolean isPaused() {
		return paused.get();
	}

	public final void pause() {
		paused.set(true);
	}

	public final void resume() {
		paused.set(false);
	}

	public BooleanValue pausedValue() {
		return paused;
	}

	public void onAdded() {

	}

	public void onUpdate(float dt) {

	}

	public void onRemoved() {

	}

}
