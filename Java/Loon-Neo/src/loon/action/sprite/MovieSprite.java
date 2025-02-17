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
package loon.action.sprite;

import loon.LTexture;
import loon.PlayerUtils;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.opengl.GLEx;
import loon.utils.IArray;
import loon.utils.TArray;

public class MovieSprite extends DisplayObject implements IArray {

	private final TArray<DisplayObject> _childs;

	public MovieSprite() {
		this._childs = new TArray<DisplayObject>();
	}

	@Override
	protected void addedToStage() {
		for (DisplayObject obj : _childs) {
			obj.addedToStage();
		}
	}

	@Override
	protected void removedFromStage() {
		for (DisplayObject obj : _childs) {
			obj.removedFromStage();
		}
	}

	public DisplayObject addChild(DisplayObject obj) {
		if (obj == null) {
			return this;
		}
		if (obj == this) {
			return this;
		}
		if (obj.getParent() != null && (obj instanceof MovieSprite)) {
			MovieSprite parent = (MovieSprite) obj.getParent();
			parent.removeChild(obj);
		}
		_childs.add(obj);
		obj.setParent(this);
		obj.setState(State.ADDED);
		return obj;
	}

	public DisplayObject addChildAt(DisplayObject obj, int index) {
		if (obj == null) {
			return this;
		}
		if (obj == this) {
			return this;
		}
		if (obj.getParent() != null && (obj instanceof MovieSprite)) {
			MovieSprite parent = (MovieSprite) obj.getParent();
			parent.removeChild(obj);
		}
		_childs.insert(index, obj);
		obj.setParent(this);
		obj.setState(State.ADDED);
		return obj;
	}

	public DisplayObject removeChild(DisplayObject obj) {
		if (_childs.remove(obj)) {
			obj.setParent(null);
			obj.setState(State.REMOVED);
			removeActionEvents(obj);
		}
		return obj;
	}

	public DisplayObject removeChildAt(int index) {
		if (index >= _childs.size) {
			return null;
		}
		DisplayObject obj = _childs.get(index);
		if (obj != null) {
			obj.setParent(null);
			obj.setState(State.REMOVED);
			removeActionEvents(obj);
		}
		_childs.removeIndex(index);
		return obj;
	}

	public int getChildIndex(DisplayObject obj) {
		return _childs.indexOf(obj);
	}

	public DisplayObject getChildAt(int index) {
		if (index < 0 || index >= _childs.size) {
			return null;
		}
		return _childs.get(index);
	}

	public DisplayObject getChildByName(String name) {
		if (null != name) {
			for (DisplayObject obj : _childs) {
				if (null != obj.getName() && obj.getName().equals(name)) {
					return obj;
				}
			}
		}
		return null;
	}

	public int numChildren() {
		return _childs.size;
	}

	public void removeAllChildren() {
		if (this._childs == null) {
			return;
		}
		for (int i = this._childs.size - 1; i >= 0; i--) {
			final DisplayObject removed = this._childs.get(i);
			if (removed != null) {
				removed.setState(State.REMOVED);
				// 删除精灵同时，删除缓动动画
				if (removed instanceof ActionBind) {
					removeActionEvents(removed);
				}
			}
		}
		_childs.clear();
	}

	@Override
	protected void enterFrame(long time) {
		for (DisplayObject obj : _childs) {
			obj.enterFrame(time);
		}
	}

	@Override
	protected void onScaleChange(float scaleX, float scaleY) {
		for (DisplayObject obj : _childs) {
			obj.onScaleChange(scaleX, scaleY);
		}
	}

	@Override
	public void update(long elapsedTime) {
		for (DisplayObject obj : _childs) {
			obj.enterFrame(elapsedTime);
		}
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0f, 0f);
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		for (DisplayObject obj : _childs) {
			if (obj.isVisible()) {
				obj.createUI(g, offsetX + _offset.x, offsetY + _offset.y);
			}
		}
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public int size() {
		return _childs == null ? 0 : _childs.size;
	}

	@Override
	public void clear() {
		removeAllChildren();
	}

	@Override
	public boolean isEmpty() {
		return _childs == null || _childs.size == 0;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	@Override
	protected void _onDestroy() {
		for (DisplayObject obj : _childs) {
			obj.close();
		}
		_childs.clear();
	}

}
