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

import java.util.Comparator;
import java.util.Iterator;

import loon.action.map.Direction;
import loon.geom.Vector2f;
import loon.utils.Easing;
import loon.utils.TArray;

/**
 * 战斗对象移动管理器
 */
public class BattleMovementManager {

	public static enum CollisionResponse {
		CONTINUE, STOP, BACKWARD, PUSH
	}

	public static enum MovementMode {
		WALK, RUN, SNEAK, CHARGE
	}

	public static enum AnimationState {
		IDLE, WALK, RUN, SNEAK, ATTACK, ARRIVED, HURT
	}

	public static interface MovementListener {
		// 基础移动事件
		void onDirectionChanged(Direction newDirection);

		void onStepReached(int mapX, int mapY);

		void onTileEntered(int mapX, int mapY);

		void onPathCompleted();

		void onPathInterrupted();

		void onPathResumed();

		void onPaused();

		void onResumed();

		void onSpeedChanged(float newSpeed);

		void onEasingChanged(Easing newEasing);

		void onAnimationStateChanged(String state);

		// 特殊移动状态
		void onStateExpired(MovementState state);

		void onStateCooldown(MovementState state);

		void onTerrainEffectApplied(String terrain, BattleTileType effect);

		void onTerrainCostDeducted(int cost, int remainingPoints);

		void onCollision(BattleMapObject self, BattleMapObject other, CollisionResponse response);

		void onPathUpdated(TArray<Vector2f> newPath);

		void onMovementPointChanged(int remainingPoints);

		void onMovementModeChanged(MovementMode oldMode, MovementMode newMode);
	}

	public static interface MovementState {

		boolean canOverrideBlocked(Vector2f tile);

		boolean canOverrideAllowed(Vector2f tile);

		boolean isTeleport();

		int getPriority();

		float getSpeedMultiplier();

		int getExtraMovementPoints();
	}

	public static class FlyState implements MovementState {

		@Override
		public boolean canOverrideBlocked(Vector2f tile) {
			return true;
		}

		@Override
		public boolean canOverrideAllowed(Vector2f tile) {
			return false;
		}

		@Override
		public boolean isTeleport() {
			return false;
		}

		@Override
		public int getPriority() {
			return 1;
		}

		@Override
		public float getSpeedMultiplier() {
			return 1.5f;
		}

		@Override
		public int getExtraMovementPoints() {
			return 0;
		}
	}

	public static class ChargeState implements MovementState {

		@Override
		public boolean canOverrideBlocked(Vector2f tile) {
			return true;
		}

		@Override
		public boolean canOverrideAllowed(Vector2f tile) {
			return true;
		}

		@Override
		public boolean isTeleport() {
			return false;
		}

		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public float getSpeedMultiplier() {
			return 3f;
		}

		@Override
		public int getExtraMovementPoints() {
			return 5;
		}
	}

	public static class TeleportState implements MovementState {

		@Override
		public boolean canOverrideBlocked(Vector2f tile) {
			return true;
		}

		@Override
		public boolean canOverrideAllowed(Vector2f tile) {
			return true;
		}

		@Override
		public boolean isTeleport() {
			return true;
		}

		@Override
		public int getPriority() {
			return 3;
		}

		@Override
		public float getSpeedMultiplier() {
			return 1f;
		}

		@Override
		public int getExtraMovementPoints() {
			return 0;
		}
	}

	public static class MovementEffect {

		private final MovementState state;
		private final float duration, cooldown;
		private float elapsed;
		private boolean active;

		public MovementEffect(MovementState state, float duration, float cooldown) {
			this.state = state;
			this.duration = duration;
			this.cooldown = cooldown;
			this.elapsed = 0f;
			this.active = true;
		}

		public void update(float deltaTime, MovementListener listener) {
			if (active) {
				elapsed += deltaTime;
				if (elapsed >= duration) {
					active = false;
					elapsed = 0f;
					if (listener != null) {
						listener.onStateExpired(state);
					}
				}
			} else {
				elapsed += deltaTime;
				if (elapsed >= cooldown) {
					active = true;
					elapsed = 0f;
					if (listener != null)
						listener.onStateCooldown(state);
				}
			}
		}

		public MovementState getState() {
			return state;
		}

		public boolean isActive() {
			return active;
		}
	}

	private final TArray<MovementEffect> activeEffects = new TArray<MovementEffect>();

	private final MovementListener listener;

	private final static Comparator<MovementState> moveComparator = new Comparator<MovementState>() {
		@Override
		public int compare(MovementState s1, MovementState s2) {
			return s2.getPriority() - s1.getPriority();
		}

	};

	public BattleMovementManager(MovementListener listener) {
		this.listener = listener;
	}

	public void addEffect(MovementEffect effect) {
		activeEffects.add(effect);
	}

	public void removeEffect(MovementEffect skill) {
		activeEffects.remove(skill);
	}

	public void removeEffectClass(Class<? extends MovementState> state) {
		Iterator<MovementEffect> it = activeEffects.iterator();
		while (it.hasNext()) {
			MovementEffect e = it.next();
			if (e.getState().getClass() == state) {
				it.remove();
			}
		}
	}

	public void update(float deltaTime) {
		for (MovementEffect effect : activeEffects) {
			effect.update(deltaTime, listener);
		}
	}

	public TArray<MovementState> getActiveStates() {
		TArray<MovementState> states = new TArray<MovementState>();
		for (MovementEffect e : activeEffects) {
			if (e.isActive()) {
				states.add(e.getState());
			}
		}
		states.sort(moveComparator);
		return states;
	}

	public void clearAll() {
		activeEffects.clear();
	}
}
