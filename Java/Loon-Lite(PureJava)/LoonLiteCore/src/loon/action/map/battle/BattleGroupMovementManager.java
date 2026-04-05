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
import loon.action.map.Direction;
import loon.action.map.battle.BattleFormationManager.FormationType;
import loon.geom.PointI;
import loon.utils.TArray;

public class BattleGroupMovementManager implements LRelease {

	private final TArray<BattleMapObject> movers = new TArray<BattleMapObject>();

	private boolean groupPaused = false;

	private final BattleFormationManager formation;

	private FormationType formationType = FormationType.SQUARE;

	public BattleGroupMovementManager(BattleMap map) {
		this.formation = new BattleFormationManager(map);
	}

	public void setGroupPath(TArray<PointI> basePath) {
		if (basePath.isEmpty() || movers.isEmpty()) {
			return;
		}
		Direction moveDir = Direction.DOWN;
		if (basePath.size() > 1) {
			PointI p0 = basePath.get(0);
			PointI p1 = basePath.get(1);
			moveDir = Direction.fromDelta(p1.x - p0.x, p1.y - p0.y);
		}
		int total = movers.size();
		for (int i = 0; i < total; i++) {
			PointI off = formation.getOffset(formationType, i, total, moveDir);
			TArray<PointI> path = formation.offsetPath(basePath, off);
			movers.get(i).setPath(path);
		}
	}

	public void setFormationType(FormationType type) {
		this.formationType = type;
	}

	public void setSpacing(int tileSpace) {
		formation.setSpacing(tileSpace);
	}

	public void addGroupMember(BattleMapObject mover) {
		movers.add(mover);
	}

	public void updateGroup(float deltaTime) {
		if (groupPaused) {
			return;
		}
		for (int i = 0; i < movers.size(); i++) {
			movers.get(i).update(deltaTime);
		}
	}

	public void pauseAll() {
		groupPaused = true;
		for (BattleMapObject o : movers) {
			if (o != null) {
				o.pause();
			}
		}
	}

	public void resumeAll() {
		groupPaused = false;
		for (BattleMapObject o : movers) {
			if (o != null) {
				o.resume();
			}
		}
	}

	public void clearAllPaths() {
		groupPaused = false;
		for (BattleMapObject o : movers) {
			if (o != null) {
				o.clearPath();
			}
		}
	}

	public boolean isGroupPaused() {
		return groupPaused;
	}

	public void setGroupPaused(boolean groupPaused) {
		this.groupPaused = groupPaused;
	}

	public TArray<BattleMapObject> getMovers() {
		return new TArray<BattleMapObject>(movers);
	}

	public BattleFormationManager getFormation() {
		return formation;
	}

	public FormationType getFormationType() {
		return formationType;
	}

	@Override
	public void close() {
		clearAllPaths();
	}

}
