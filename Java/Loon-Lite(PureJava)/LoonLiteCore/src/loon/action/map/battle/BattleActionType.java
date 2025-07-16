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

public enum BattleActionType {
	// 无状态
	None,
	// 等待中
	Waiting,
	// 进入战斗
	Enter,
	// 离开战斗
	Exit,
	// 改变战斗目标
	ChangeTarget,
	// 移动向目标
	Move,
	// 攻击
	Attack,
	// 防御
	Defense,
	// 使用技能
	UseSkill,
	// 使用物品
	UseItems,
	// 成功
	Success,
	// 失败
	Failure,
	// 取消操作
	Cancel
}
