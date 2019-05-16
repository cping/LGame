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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.action;

import loon.action.collision.CollisionFilter;
import loon.action.collision.CollisionResult;
import loon.action.collision.CollisionWorld;
import loon.utils.StringKeyValue;
import loon.utils.timer.LTimer;

public abstract class ActionEvent {

	private LTimer timer;

	private ActionListener actionListener;

	protected boolean firstTick, _isCompleted, isInit;

	protected CollisionWorld collisionWorld;

	protected CollisionFilter worldCollisionFilter;

	protected ActionBind original;

	protected Object tag;

	protected float offsetX, offsetY;

	protected float oldX, oldY;

	public ActionEvent() {
		timer = new LTimer(0);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public ActionEvent setDelay(long d) {
		timer.setDelay(d);
		return this;
	}

	public ActionEvent paused(boolean pause) {
		ActionControl.get().paused(pause, original);
		return this;
	}

	public ActionEvent step(long elapsedTime) {
		if (original == null) {
			return this;
		}
		if (timer.action(elapsedTime)) {
			if (firstTick) {
				this.firstTick = false;
				this.timer.refresh();
			} else {
				update(elapsedTime);
			}
			if (actionListener != null) {
				actionListener.process(original);
			}
		}
		return this;
	}

	public Object getOriginal() {
		return original;
	}

	public ActionEvent start(ActionBind o) {
		if (o == null) {
			return this;
		}
		this.original = o;
		if (original != null) {
			oldX = original.getX();
			oldY = original.getY();
		}
		this.timer.refresh();
		this.firstTick = true;
		this._isCompleted = false;
		this.isInit = false;
		if (actionListener != null) {
			actionListener.start(o);
		}
		return this;
	}

	public abstract void update(long elapsedTime);

	public abstract void onLoad();

	public ActionEvent stop() {
		if (actionListener != null) {
			actionListener.stop(original);
		}
		return this;
	}

	public abstract boolean isComplete();

	public Object getTag() {
		return tag;
	}

	public ActionEvent setTag(Object tag) {
		this.tag = tag;
		return this;
	}

	public final ActionEvent setComplete(boolean isComplete) {
		this._isCompleted = isComplete;
		return this;
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	public ActionEvent setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
		return this;
	}

	public ActionEvent setOffset(float x, float y) {
		this.offsetX = x;
		this.offsetY = y;
		return this;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public ActionEvent setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		return this;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public ActionEvent setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	public ActionEvent set(ActionEvent e) {
		setOffset(e.offsetX, e.offsetY);
		oldX = e.oldX;
		oldY = e.oldY;
		tag = e.tag;
		actionListener = e.actionListener;
		original = e.original;
		return this;
	}

	public ActionEvent kill() {
		this._isCompleted = true;
		return this;
	}

	public void movePos(float x, float y) {
		movePos(x, y, -1f, -1f);
	}

	public void movePos(float x, float y, float lastX, float lastY) {
		if (original == null) {
			return;
		}
		if (collisionWorld != null) {
			if (worldCollisionFilter == null) {
				worldCollisionFilter = CollisionFilter.getDefault();
			}
			CollisionResult.Result result = collisionWorld.move(original, x, y, worldCollisionFilter);
			if (lastX != -1 && lastY != -1) {
				if (result.goalX != x || result.goalY != y) {
					original.setLocation(lastX, lastY);
				} else {
					original.setLocation(result.goalX, result.goalY);
				}
			} else {
				original.setLocation(result.goalX, result.goalY);
			}
		} else {
			original.setLocation(x, y);
		}
	}

	public CollisionWorld getCollisionWorld() {
		return collisionWorld;
	}

	public void setCollisionWorld(CollisionWorld world) {
		this.collisionWorld = world;
	}

	public abstract ActionEvent cpy();

	public abstract ActionEvent reverse();

	public abstract String getName();

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("loaded", isInit).comma().kv("bind", original).comma().kv("offset", (offsetX + " x " + offsetY))
				.comma().kv("tag", tag);
		return builder.toString();
	}
}
