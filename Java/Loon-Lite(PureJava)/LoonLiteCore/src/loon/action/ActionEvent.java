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

import loon.LRelease;
import loon.action.collision.CollisionFilter;
import loon.action.collision.CollisionResult;
import loon.action.collision.CollisionWorld;
import loon.utils.StringKeyValue;
import loon.utils.timer.EaseTimer;
import loon.utils.timer.LTimer;

public abstract class ActionEvent {

	protected static int _ACTION_ID = 0;

	protected EaseTimer _easeTimer;

	private LTimer _currentTimer;

	private ActionListener actionListener;

	private LRelease _released;

	protected boolean firstTick, isInit;

	protected boolean _isCompleted, _isCompletedEvent;

	protected CollisionWorld collisionWorld;

	protected CollisionFilter worldCollisionFilter;

	protected ActionBind original;

	protected Object tag;

	protected float offsetX, offsetY;

	protected float oldX, oldY;

	public ActionEvent() {
		this._currentTimer = new LTimer(0);
		this.nextActionId();
	}

	protected int nextActionId() {
		return _ACTION_ID++;
	}

	public int getActionId() {
		return _ACTION_ID;
	}

	public LTimer getTimer() {
		return _currentTimer;
	}

	public long getDelay() {
		return _currentTimer.getDelay();
	}

	public ActionEvent setDelay(long d) {
		_currentTimer.setDelay(d);
		_currentTimer.action(d);
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
		if (_currentTimer.action(elapsedTime)) {
			if (firstTick) {
				this.firstTick = false;
				this._currentTimer.refresh();
			} else {
				update(elapsedTime);
			}
			if (actionListener != null) {
				actionListener.process(original);
			}
			if (_isCompleted && !_isCompletedEvent) {
				if (_released != null) {
					_released.close();
				}
				_isCompletedEvent = true;
			}
		}
		return this;
	}

	public ActionEvent dispose(LRelease r) {
		this._released = r;
		return this;
	}

	public Object getOriginal() {
		return original;
	}

	protected void initAction() {
		if (actionListener != null) {
			actionListener.start(this.original);
		}
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
		this._currentTimer.refresh();
		if (!this.isInit) {
			this.initAction();
		}
		this.firstTick = true;
		this._isCompleted = false;
		this._isCompletedEvent = false;
		this.isInit = false;
		return this;
	}

	public abstract void update(long elapsedTime);

	public abstract void onLoad();

	public ActionEvent stop() {
		if (actionListener != null) {
			actionListener.stop(original);
		}
		if (_released != null) {
			_released.close();
		}
		this._isCompleted = true;
		return this;
	}

	public final Object getTag() {
		return tag;
	}

	public final ActionEvent setTag(Object tag) {
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

	public ActionEvent forceCompleted() {
		if (!this._isCompleted && this.original != null) {
			this._isCompleted = true;
			String name = this.getName();
			if ("flash".equals(name) || "fade".equals(name) || "alpha".equals(name) || "show".equals(name)) {
				original.setAlpha(1f);
				original.setVisible(true);
			}
		}
		return this;
	}

	public boolean isComplete(ActionBind bind) {
		if (bind == null) {
			return true;
		}
		if (bind == original || bind.equals(original)) {
			return this._isCompleted;
		}
		return bind.isActionCompleted();
	}

	public boolean isComplete() {
		return this._isCompleted;
	}

	public boolean isPause() {
		return _currentTimer.paused();
	}

	public ActionEvent pause() {
		_currentTimer.pause();
		if (_easeTimer != null) {
			_easeTimer.pause();
		}
		return this;
	}

	public ActionEvent resume() {
		_currentTimer.resume();
		if (_easeTimer != null) {
			_easeTimer.resume();
		}
		return this;
	}

	public ActionEvent reset() {
		_currentTimer.reset();
		if (_easeTimer != null) {
			_easeTimer.reset();
		}
		_isCompleted = false;
		_isCompletedEvent = false;
		return this;
	}

	public ActionEvent loop(int count) {
		if (_easeTimer != null) {
			_easeTimer.setLoop(count);
		}
		return this;
	}

	public ActionEvent loop(boolean l) {
		if (_easeTimer != null) {
			_easeTimer.setLoop(l);
		}
		return this;
	}

	public boolean isLoop() {
		if (_easeTimer != null) {
			return _easeTimer.isLoop();
		}
		return false;
	}

	public ActionEvent setEaseTimer(EaseTimer e) {
		this._easeTimer = e;
		return this;
	}

	public EaseTimer getEaseTimer() {
		return _easeTimer;
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

	public CollisionFilter getCollisionFilter() {
		return worldCollisionFilter;
	}

	public ActionEvent setCollisionFilter(CollisionFilter filter) {
		this.worldCollisionFilter = filter;
		return this;
	}

	public CollisionWorld getCollisionWorld() {
		return collisionWorld;
	}

	public ActionEvent setCollisionWorld(CollisionWorld world) {
		this.collisionWorld = world;
		return this;
	}

	public boolean isFirstTick() {
		return firstTick;
	}

	public boolean isInit() {
		return isInit;
	}

	public float getOldX() {
		return oldX;
	}

	public float getOldY() {
		return oldY;
	}

	public abstract ActionEvent cpy();

	public abstract ActionEvent reverse();

	public abstract String getName();

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("loaded", isInit).comma().kv("bind", original).comma().kv("offset", (offsetX + " x " + offsetY))
				.comma().kv("tag", tag);
		return builder.toString();
	}

}
