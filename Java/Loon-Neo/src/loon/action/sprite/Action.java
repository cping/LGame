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

public abstract class Action {

	protected Entity entity;

	private boolean isCompleted = false;

	private boolean isCancelled = false;

	public final Entity getEntity() {
		return entity;
	}

	public final Action setEntity(Entity entity) {
		if (this.entity != null && this.entity != entity) {
			return this;
		}
		this.entity = entity;
		return this;
	}

	public final boolean isComplete() {
		return isCompleted;
	}

	public final Action setComplete() {
		isCompleted = true;
		return this;
	}

	public final boolean isCancelled() {
		return isCancelled;
	}

	public final Action cancel() {
		if (isCancelled) {
			return this;
		}
		isCancelled = true;
		onCancelled();
		return this;
	}

	protected void onQueued() {
	}

	protected void onStarted() {
	}

	protected abstract void onUpdate(float tpf);

	protected void onCompleted() {
	}

	protected void onCancelled() {
	}
}
