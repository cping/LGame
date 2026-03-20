/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.map.battle;

import loon.LRelease;
import loon.action.map.battle.BattleType.ObjectState;
import loon.events.GameEvent;
import loon.events.GameEventBus;
import loon.events.GameEventType;
import loon.utils.TArray;

public class BattleSelectManager implements LRelease {

	public final TArray<BattleMapObject> _selectedObjects = new TArray<BattleMapObject>();

	private final GameEventBus<BattleMapObject> _eventBus;

	public BattleSelectManager(GameEventBus<BattleMapObject> bus) {
		this._eventBus = bus;
	}

	/**
	 * 单选对象
	 * 
	 * @param obj
	 */
	public void selectSingle(BattleMapObject obj) {
		clearSelection();
		if (obj != null && obj.state != ObjectState.DEAD) {
			_selectedObjects.add(obj);
			_eventBus.publish(new GameEvent<BattleMapObject>(GameEventType.OBJECT_SELECTED, this, obj));
		}
	}

	/**
	 * 多选对象
	 * 
	 * @param objs
	 */
	public void selectMultiple(TArray<BattleMapObject> objs) {
		clearSelection();
		for (BattleMapObject obj : objs) {
			if (obj.state != ObjectState.DEAD) {
				_selectedObjects.add(obj);
				_eventBus.publish(new GameEvent<BattleMapObject>(GameEventType.OBJECT_SELECTED, this, obj));
			}
		}
	}

	/**
	 * 切换选中对象
	 * 
	 * @param obj
	 */
	public void toggleSelection(BattleMapObject obj) {
		if (obj == null || obj.state == ObjectState.DEAD) {
			return;
		}
		if (_selectedObjects.contains(obj)) {
			_selectedObjects.remove(obj);
			_eventBus.publish(new GameEvent<BattleMapObject>(GameEventType.OBJECT_DESELECTED, this, obj));
		} else {
			_selectedObjects.add(obj);
			_eventBus.publish(new GameEvent<BattleMapObject>(GameEventType.OBJECT_SELECTED, this, obj));
		}
	}

	public void clearSelection() {
		for (BattleMapObject obj : _selectedObjects) {
			_eventBus.publish(new GameEvent<BattleMapObject>(GameEventType.OBJECT_DESELECTED, this, obj));
		}
		_selectedObjects.clear();
	}

	@Override
	public void close() {
		clearSelection();
	}

}
