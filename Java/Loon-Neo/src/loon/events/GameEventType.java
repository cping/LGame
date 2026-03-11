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
package loon.events;

public enum GameEventType {
	MOVE_COMPLETE, MOVE_BLOCKED, 
	COLLISION, ATTACK, DEFEND, SKILL, SKILL_EFFECT,
	ATTACK_HIT, ATTACK_MISS, 
	ENTER_GAME,
	ENTER_ATTACK_RANGE, 
	EXIT_ATTACK_RANGE, HP_CHANGED, MP_CHANGED, 
	DEATH, 
	OBJECT_SELECTED, OBJECT_DESELECTED,
	DRAG_START, 
	DRAG_END, 
	CAMERA_MOVED, ZOOM_CHANGED, DIRECTION_CHANGED, 
	TURN_START, 
	TURN_END, 
	COMBAT_START, COMBAT_END,
	TILE_CLICKED, ANIMATION_COMPLETE, 
	SKILL_CAST_START, SKILL_CAST_END, 
	MOVE_START, PATH_FOUND, PATH_FAILED,
	TERRAIN_EFFECT_TRIGGERED
}
