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

import loon.LTexture;
import loon.Stage;
import loon.action.map.battle.BattleProcess;
import loon.action.sprite.Entity;
import loon.action.sprite.StatusBar;
import loon.action.sprite.effect.PixelChopEffect;
import loon.action.sprite.effect.StringEffect;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.component.LTextArea;
import loon.geom.BooleanValue;

public class RpgBattleTest extends Stage {

	public void attackEnemyEntity(final BattleProcess process, final LTextArea text, final StatusBar status,
			final Entity e) {
		if (process.isBool("battle")) {
			return;
		}
		process.setVar("battle", true);
		// 我方回合才能触发攻击事件,并且动画动作要播放完毕
		if (process.isCurrentPlayer() && e.isActionCompleted()) {
			text.put("我方选择攻击");
			call(yield -> {
				// 随机使用一种像素斩机模式,颜色红,宽度2,对象为e,构建到Screen
				PixelChopEffect.chopRandom(LColor.red, 2, e).buildToScreen();
				// 延迟1秒后执行下一个yield事件(这个写法是loon特有的,用循环模拟的yield,非真实yield,优点是可以跨平台,
				// 缺点是只有call函数里的yield会互相影响,范围外的不会被暂停,不能真正延迟整个loon系统)
				return yield.seconds(1f);
			}, yield -> {
				// 使用缓动动画,y轴震动后闪烁,开始执行,执行完毕后提交血量减少与增加战斗提示
				e.selfAction().shakeTo(0f, 3f).flashTo().start().dispose(() -> {
					StringEffect.up("- 100", e.getCenterLocation(-25, 0), LColor.red).buildToScreen();
					status.setUpdate(status.getValue() - 100);
					text.put("你使用[认真一击],\n敌人生命值-100", LColor.red);
					process.set(true);
				});
				return yield.seconds(2f);
			});

		}
	}

	@Override
	public void create() {

		// 设定背景
		background("assets/rpg/battle/back.png");
		// 设定敌人dragon
		final Entity dragon = node("e", "assets/rpg/battle/dragon.png");
		// 敌人居中
		centerOn(dragon);
		// 添加敌人到Screen
		add(dragon);

		// 设定信息显示用类
		LTextArea rpgBattleText = new LTextArea(0, 0, 300, 100, true);
		// 构建一个300x100的游戏窗体背景图,颜色黑蓝相间,横向渐变
		LTexture texture = getGameWinFrame(300, 100, LColor.black, LColor.blue, false);
		rpgBattleText.setBackground(texture);
		rpgBattleText.setLeftOffset(5);
		rpgBattleText.setTopOffset(5);
		add(rpgBattleText);
		centerTopOn(rpgBattleText);

		// 血条状态
		StatusBar status = node("status", 0, 0, 150, 22);
		status.set(500);
		status.setShowNumber(true);
		centerOn(status, 0, 60);
		add(status);

		// 设定一个战斗进程
		BattleProcess rpgBattleProcess = new BattleProcess();
		// 添加我方战斗事件(不跳过开始与结束步骤)
		rpgBattleProcess.addEvent(new BattleProcess.TurnPlayerEvent(false) {

			@Override
			public void onStart(long elapsedTime, BooleanValue process) {
				rpgBattleText.put("正在进行第" + rpgBattleProcess.getTurnCount() + "回合战斗");
				process.set(true);
			}

			@Override
			public void onProcess(long elapsedTime, BooleanValue process) {
				if (rpgBattleProcess.get()) {
					process.set(true);
				}
			}

			@Override
			public void onEnd(long elapsedTime, BooleanValue process) {
				// 锁定战斗进程
				rpgBattleProcess.lockProcess(process, () -> {
					// 提交任务,间隔两秒,执行一次
					postTask(() -> {
						rpgBattleText.put("我方战斗结束");
						// 解锁战斗进程
						rpgBattleProcess.unlockProcess(process);
						rpgBattleProcess.setVar("battle", false);
					}, 2f, 1);
				});
			}

		});
		// 添加敌方战斗事件
		rpgBattleProcess.addEvent(new BattleProcess.TurnEnemyEvent(false) {

			@Override
			public void onStart(long elapsedTime, BooleanValue process) {
				// 锁定战斗进程
				rpgBattleProcess.lockProcess(process, () -> {
					// 提交任务,间隔两秒,执行一次
					postTask(() -> {
						rpgBattleText.put("进入敌方回合");
						// 解锁战斗进程
						rpgBattleProcess.unlockProcess(process);
					}, 2f, 1);
				});
			}

			@Override
			public void onProcess(long elapsedTime, BooleanValue process) {
				// 锁定战斗进程
				rpgBattleProcess.lockProcess(process, () -> {
					// 如果Screen动画播放完毕
					if (isActionCompleted()) {
						// 提交任务,间隔两秒,执行一次
						postTask(() -> {
							rpgBattleText.put("敌方发动攻击");
							selfAction().shakeTo(2f).start().dispose(() -> {
								rpgBattleText.put("你被击中了,\n生命值-100", LColor.red);
								// 解锁战斗进程
								rpgBattleProcess.unlockProcess(process);
							});
						}, 2f, 1);
					}
				});
			}

			@Override
			public void onEnd(long elapsedTime, BooleanValue process) {
				// 锁定战斗进程
				rpgBattleProcess.lockProcess(process, () -> {
					// 提交任务,间隔两秒,执行一次
					postTask(() -> {
						rpgBattleText.put("敌方回合结束");
						// 解锁战斗进程
						rpgBattleProcess.unlockProcessEnd(process);
						rpgBattleProcess.setVar("battle", false);
					}, 2f, 1);
				});
			}
		});
		// 注入战斗进程
		add(rpgBattleProcess);
		// 关闭Screen时同样关闭战斗进程
		putRelease(rpgBattleProcess);

		// 设定攻击按钮
		LClickButton attack = node("click", "Attack", 0, 0, 120, 35);
		// 按钮居于最下,位置偏移5,-5
		bottomLeftOn(attack, 5, -5);
		// 添加按钮并设定按下按钮时事件
		add(attack.up((x, y) -> {
			attackEnemyEntity(rpgBattleProcess, rpgBattleText, status, dragon);
		}));

		// 设定防御按钮
		LClickButton defend = node("click", "Defend", 0, 0, 120, 35);
		bottomOn(defend, -2, -5);
		add(defend.up((x, y) -> {
			rpgBattleText.put("你不敢移动,只能全力防御了");
			rpgBattleProcess.set(true);
		}));

		// 设定逃跑按钮
		LClickButton run = node("click", "Run", 0, 0, 120, 35);
		bottomRightOn(run, -5, -5);
		add(run.up((x, y) -> {
			rpgBattleText.put("你尝试逃跑,\n但伟大意志阻止了你");
		}));
	}

}
