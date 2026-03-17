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
package org.test;

import loon.Stage;
import loon.action.map.Direction;
import loon.action.map.battle.BattleType.ObjectState;
import loon.action.sprite.AnimationManager;
import loon.action.sprite.AnimationRenderer;
import loon.component.LClickButton;

public class AnimationManagerTest extends Stage {

	@Override
	public void create() {
		// 构建一个纹理动画渲染器，位置在0x0，大小128x128
		AnimationRenderer renderer = new AnimationRenderer(0, 0, 128, 128);

		// 加载json动画配置资源到纹理动画管理器，不绑定任何对象(若绑定具体战斗对象或事件，则在执行对应命令时会回调触发)
		AnimationManager mang = new AnimationManager("anitest.json", null, null, null);

		// 完整json配置格式
		/* sample:
			 * {
			  "frameWidth": 64,   // 每帧宽度，用于切割spritesheet
			  "frameHeight": 64,  // 每帧高度
			  "basePath": "assets/anim/",   // 所有图片的统一上级目录
			  "animations": {
			    // 待机动画：单帧图片，循环播放
			    "IDLE": { "filePattern": "idle_{0}.png", "speed": 1.0, "looping": true, "isSheet": false },

			    // 火球术施法与释放
			    "CASTING_FIREBALL": { "filePattern": "cast_fireball_{0}_sheet.png", "speed": 0.12, "looping": false, "isSheet": true },
			    "FIREBALL": { "filePattern": "fireball_{0}_sheet.png", "speed": 0.15, "looping": false, "isSheet": true },

			    // 冰冻术施法与释放
			    "CASTING_ICE": { "filePattern": "cast_ice_{0}_sheet.png", "speed": 0.12, "looping": false, "isSheet": true },
			    "ICE": { "filePattern": "ice_{0}_sheet.png", "speed": 0.15, "looping": false, "isSheet": true },

			    // 闪电术施法与释放
			    "CASTING_LIGHTNING": { "filePattern": "cast_lightning_{0}_sheet.png", "speed": 0.12, "looping": false, "isSheet": true },
			    "LIGHTNING": { "filePattern": "lightning_{0}_sheet.png", "speed": 0.15, "looping": false, "isSheet": true },
			    
			    // 若某图桢的宽高大小与基础桢设定不一致，可以添加单独的 clipWidth 和 clipHeight 属性单独设定大小, 这项全图设置通用
                "ATTACK": { "filePattern": "attack_{0}_sheet.png", "speed": 0.22, "clipWidth": 100, "clipHeight": 100, "looping": true, "isSheet": true },
			    
			    // 大图拆分：如果我们设定一张大图包含多个动作时，分组时这样进行(不同方向可以细分frames子项，但不细分也支持直接写frames:[0,1,2,3]这种)
			    "BIG_SHEET_MUL": {
				  "filePattern": "big_sheet.png",
				  "speed": 0.12,
				  "looping": false,
				  "isSheet": true,
				  "region": { "x": 0, "y": 0, "rows": 1, "cols": 6 },
				  "frames": {
				    "NONE": [0,1,2,3,4,5], 
				    "UP": [0,1,2,3],
				    "DOWN": [4,5,6,7],
				    "LEFT": [8,9,10,11],
				    "RIGHT": [12,13,14,15],
				    "UP_LEFT": [16,17,18,19],
				    "UP_RIGHT": [20,21,22,23],
				    "DOWN_LEFT": [24,25,26,27],
				    "DOWN_RIGHT": [28,29,30,31]
				  }
				},
			    "BIG_SHEET_ATTACK": {
			      "filePattern": "big_sheet.png",
			      "speed": 0.12,
			      "looping": false,
			      "isSheet": true,
			      "region": { "x": 0, "y": 0, "rows": 1, "cols": 6 },   // 从大图的 (0,0) 开始，取 1 行 6 列
			      "frames": [0,1,2,3,4,5]                               // 指定播放的帧序列，可跳桢，可重复，只要索引存在[4,7,3,3]这样也可以
			    },
			    "BIG_SHEET_SKILL": {
			      "filePattern": "big_sheet.png",
			      "speed": 0.12,
			      "looping": false,
			      "isSheet": true,
			      "region": { "x": 0, "y": 64, "rows": 1, "cols": 6 },  // 从大图的第2行开始
			      "frames": [0,1,2,3]                                   
			    }
			  },

			  "transitions": {
			    // 火球术过渡
			    "IDLE->CASTING_FIREBALL": { "blendTime": 0.2 },
			    "CASTING_FIREBALL->FIREBALL": { "blendTime": 0.1 },
			    "FIREBALL->IDLE": { "blendTime": 0.3 },

			    // 冰冻术过渡
			    "IDLE->CASTING_ICE": { "blendTime": 0.2 },
			    "CASTING_ICE->ICE": { "blendTime": 0.1 },
			    "ICE->IDLE": { "blendTime": 0.3 },

			    // 闪电术过渡
			    "IDLE->CASTING_LIGHTNING": { "blendTime": 0.2 },
			    "CASTING_LIGHTNING->LIGHTNING": { "blendTime": 0.1 },
			    "LIGHTNING->IDLE": { "blendTime": 0.3 }
			  },

			  "events": {
			    // 火球术关键帧事件
			    "CASTING_FIREBALL": {
			      "2": "chargeFireball"   // 第2帧：开始蓄力
			    },
			    "FIREBALL": {
			      "3": "spawnFireball",   // 第3帧：生成火球
			      "6": "explodeFireball"  // 第6帧：火球爆炸
			    },

			    // 冰冻术关键帧事件
			    "CASTING_ICE": {
			      "2": "chargeIce"        // 第2帧：开始蓄力
			    },
			    "ICE": {
			      "3": "spawnIce",        // 第3帧：生成冰锥
			      "6": "freezeArea"       // 第6帧：范围冻结
			    },

			    // 闪电术关键帧事件
			    "CASTING_LIGHTNING": {
			      "2": "chargeLightning"  // 第2帧：开始蓄力
			    },
			    "LIGHTNING": {
			      "3": "spawnLightning",  // 第3帧：生成闪电
			      "5": "shockArea"        // 第5帧：范围电击
			    }
			  },

			  "eventActions": { //自定义事件触发
			    // 战斗相关事件
			    "attackHit": { "type": "combat", "method": "applyDamage" }, 
			    // 播放攻击音效
			    "attackSound": { "type": "sound", "method": "playSound", "args": ["attack"] },

			    // 火球术事件映射
			    "chargeFireball": { "type": "skill", "method": "startCharge", "args": ["火球术"] },
			    "spawnFireball": { "type": "skill", "method": "spawnProjectile", "args": ["火球"] },
			    "explodeFireball": { "type": "skill", "method": "explode", "args": ["火球爆炸"] },

			    // 冰冻术事件映射
			    "chargeIce": { "type": "skill", "method": "startCharge", "args": ["冰冻术"] },
			    "spawnIce": { "type": "skill", "method": "spawnProjectile", "args": ["冰锥"] },
			    "freezeArea": { "type": "skill", "method": "explode", "args": ["冰冻范围"] },

			    // 闪电术事件映射
			    "chargeLightning": { "type": "skill", "method": "startCharge", "args": ["闪电术"] },
			    "spawnLightning": { "type": "skill", "method": "spawnProjectile", "args": ["闪电"] },
			    "shockArea": { "type": "skill", "method": "explode", "args": ["雷击范围"] }
			  },

			  "stateMachine": { //状态机设置，用以通过条件选择方式，跳转动画事件
			    "layer1": [
			      { "param": "isCastingFireball", "value": true, "target": "CASTING_FIREBALL" },
			      { "param": "isCastingIce", "value": true, "target": "CASTING_ICE" },
			      { "param": "default", "target": "IDLE" }
			    ]
			  }
			}
			 * 
			 */

		
		// PS: 默认不添加的动画标识不会执行，因为json中可以配置大量不同图片与标识，不清楚具体执行哪一个
		// 插入图层IDLE标识动画，同时添加动作标识LEFT和RIGHT(任何状态标识都是json中设置了才能执行，没有对应名称则无效，另外此处使用字符串或者枚举类型的效果一样，只是用枚举方便用我自定义的模板，若自定义字符串则只能自行定义具体触发的事件)
		mang.addLayer(ObjectState.IDLE, Direction.LEFT, Direction.RIGHT);
		mang.addLayer("ATTACK", Direction.RIGHT);

		// 将动画播放状态设定为播放IDLE动画，状态向右
		mang.setState("IDLE", Direction.RIGHT);

		// 插入动画管理管理器中动画显示位置为125x100,大小缩放2倍
		renderer.addCharacter(mang, 125, 100, 2f);

		add(renderer);
		
		//在屏幕底部添加向左按钮
		LClickButton leftBtn = LClickButton.make("LEFT");
		leftBtn.up((x,y) -> {
			mang.setState("IDLE", Direction.LEFT);
		});
		bottomLeftOn(leftBtn, 25, -25);
		add(leftBtn);
		
		//在屏幕底部添加中间按钮
		LClickButton centerBtn = LClickButton.make("CENTER");
		centerBtn.up((x,y) -> {
			mang.setState("ATTACK", Direction.RIGHT);
		});
		centerBottomOn(centerBtn, 0, -25);
		add(centerBtn);
		
		//在屏幕底部添加向右按钮
		LClickButton rightBtn = LClickButton.make("RIGHT");
		rightBtn.up((x,y) -> {
			mang.setState("IDLE", Direction.RIGHT);
		});
		bottomRightOn(rightBtn, -25, -25);
		add(rightBtn);

	}
}