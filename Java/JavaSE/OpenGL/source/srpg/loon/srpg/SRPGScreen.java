package loon.srpg;

import loon.action.avg.drama.Command;
import loon.action.sprite.AnimationHelper;
import loon.core.EmulatorButton;
import loon.core.EmulatorButtons;
import loon.core.EmulatorListener;
import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.LGradation;
import loon.core.graphics.LImage;
import loon.core.graphics.Screen;
import loon.core.graphics.component.LMessage;
import loon.core.graphics.component.LSelect;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LTouch;
import loon.core.input.LInputFactory.Touch;
import loon.core.timer.LTimerContext;
import loon.srpg.ability.SRPGAbilityFactory;
import loon.srpg.ability.SRPGAbilityOption;
import loon.srpg.ability.SRPGDamageAverage;
import loon.srpg.ability.SRPGDamageData;
import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActorFactory;
import loon.srpg.actor.SRPGActors;
import loon.srpg.actor.SRPGPosition;
import loon.srpg.actor.SRPGStatus;
import loon.srpg.effect.SRPGEffect;
import loon.srpg.effect.SRPGEffectFactory;
import loon.srpg.effect.SRPGNumberEffect;
import loon.srpg.effect.SRPGPhaseEffect;
import loon.srpg.effect.SRPGUpperEffect;
import loon.srpg.field.SRPGField;
import loon.srpg.field.SRPGFieldElement;
import loon.srpg.field.SRPGFieldElements;
import loon.srpg.field.SRPGFieldMove;
import loon.srpg.field.SRPGTeams;
import loon.srpg.view.SRPGAbilityNameView;
import loon.srpg.view.SRPGActorStatusView;
import loon.srpg.view.SRPGAvgView;
import loon.srpg.view.SRPGChoiceView;
import loon.srpg.view.SRPGDamageExpectView;
import loon.srpg.view.SRPGDrawView;
import loon.srpg.view.SRPGFieldChoiceView;
import loon.srpg.view.SRPGMessageListener;
import loon.srpg.view.SRPGMessageView;
import loon.srpg.view.SRPGMiniStatusView;
import loon.utils.RecordStoreUtils;
import loon.utils.collection.ArrayMap;


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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.1
 */
public abstract class SRPGScreen extends Screen implements SRPGType, Runnable {

	public static int TILE_WIDTH, TILE_HEIGHT;

	private LTexture cursor, messageImage;

	private boolean isCursor;

	private boolean isBattleMode;

	private Thread srpgThread;

	private SRPGAI srpgAI;

	private SRPGAvgView srpgAvgView;

	private final static LColor[] colors = { new LColor(255, 0, 32, 125),
			new LColor(0, 192, 0, 125), new LColor(192, 192, 0, 125) };

	private LColor moving_our = new LColor(0, 128, 255, 125),
			moving_other = new LColor(255, 100, 20, 125),
			attack_target = new LColor(255, 192, 0, 125),
			attack_range = new LColor(255, 128, 64, 125),
			moving_change = new LColor(0, 64, 255, 125),
			hero_flag = LColor.blue, enemy_flag = LColor.red;

	private SRPGMessageView srpgHelper;

	private int cam_x, cam_y;

	private int move;

	private int currentActor;

	private int mouse_x, mouse_y;

	private int halfTileWidth;

	private int halfTileHeight;

	private int halfWidth;

	private int halfHeight;

	private SRPGChoiceView srpgChoiceView;

	private SRPGFieldChoiceView srpgChoiceField;

	private SRPGDrawView srpgDrawView;

	private SRPGActors srpgActors;

	private SRPGField srpgField;

	private SRPGFieldElements srpgElements;

	private SRPGFieldMove srpgMove;

	private SRPGEvent srpgEvent, temp_event_1, temp_event_2;

	private SRPGPosition srpgPosition, tempPosition;

	private SRPGEffect srpgEffect;

	private SRPGTeams srpgTeams;

	private int choiceX, choiceY;

	private int tileWidth, tileHeight, procFlag;

	private boolean isGridAlpha, isSrpgTouchLock, isSrpgNoMove;

	private String fileName;

	private LFont choiceFont = LFont.getFont("黑体", 0, 22);

	private LFont simpleFont = LFont.getFont("Dialog", 0, 12);

	// 配置主菜单
	private final String[][] menuItems = new String[4][2];

	// 游戏循环是否进行中，摄像机是否锁定，战斗是否开始，动画事件是否正在执行
	private boolean isEventLoop, isCameraLock, isBattleStart, isAnimationEvent;

	// 是否显示棋盘网格,是否显示不同的组别颜色,行动完毕是否有显示,是否显示敌方状态,是否开启音效，使用使用默认的战斗伤害判定,是否允许自动滚屏
	private static boolean isGrid, isTeamColor, isEndView = true,
			isEnemyView = true, isPhase = true, isBattle = true, isSound;

	// 角色移动速度
	private static int moveSpeed = 20;

	private int sleepTime = 300;

	// 允许的SRPG角色最大等级，最大经验值
	private int maxLevel, maxExp;

	private int language_index;

	public SRPGScreen(String fileName) {
		this(null, fileName, null, 32, 32);
	}

	public SRPGScreen(String fileName, LTexture img) {
		this(null, fileName, img, 32, 32);
	}

	public SRPGScreen(String fileName, int row, int col) {
		this(null, fileName, null, row, col);
	}

	public SRPGScreen(String fileName, LTexture img, int row, int col) {
		this(null, fileName, img, row, col);
	}

	public SRPGScreen(SRPGFieldElements elements, String fileName,
			LTexture img, int row, int col) {
		this.srpgElements = elements;
		this.tileWidth = row;
		this.tileHeight = col;
		this.halfTileWidth = tileWidth / 2;
		this.halfTileHeight = tileHeight / 2;
		this.halfWidth = getWidth() / 2;
		this.halfHeight = getHeight() / 2;
		this.fileName = fileName;
		this.messageImage = img;
		this.move = -1;
		this.currentActor = -1;
		this.mouse_x = -1;
		this.mouse_y = -1;
		this.choiceX = 50;
		this.choiceY = 50;
		this.procFlag = PROC_NORMAL;
		SRPGScreen.TILE_WIDTH = tileWidth;
		SRPGScreen.TILE_HEIGHT = tileHeight;
	}

	/**
	 * Screen完全载入前加载过程中画面(建议重载为适当画面)
	 * 
	 * @param g
	 */
	protected void initLoading(GLEx g) {

	}

	/**
	 * 缓存游戏的角色数据,以供记录器保存调用
	 * 
	 */
	protected final void initActorsData() {
		SRPGData.getInstnace().initActors(srpgActors);
	}

	/**
	 * 保存角色分布为指定名称的记录
	 * 
	 * @param name
	 * @throws Exception
	 */
	protected final void savePositionData(String name) throws Exception {
		RecordStoreUtils.setBytes(name, SRPGData.getInstnace().savePosition());
	}

	/**
	 * 加载指定名称的角色分布记录
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected SRPGPosition[] loadPositionData(String name) throws Exception {
		byte[] result = RecordStoreUtils.getBytes(name);
		if (result != null) {
			return SRPGData.getInstnace().loadPosition(result);
		}
		return null;
	}

	/**
	 * 保存角色数据为指定名称的记录
	 * 
	 * @param name
	 * @throws Exception
	 */
	protected final void saveStatusData(String name) throws Exception {
		RecordStoreUtils.setBytes(name, SRPGData.getInstnace().saveStatus());
	}

	/**
	 * 加载指定名称的角色数据记录(通过SRPGData调用)
	 * 
	 * @param name
	 * @throws Exception
	 */
	protected boolean loadStatusData(String name) throws Exception {
		byte[] result = RecordStoreUtils.getBytes(name);
		if (result != null) {
			return SRPGData.getInstnace().loadStatus(result);
		}
		return false;
	}

	/**
	 * 查找指定角色
	 * 
	 * @param index
	 * @return
	 */
	public SRPGActor findActor(int index) {
		if (srpgActors != null) {
			return srpgActors.find(index);
		} else {
			return null;
		}
	}

	/**
	 * 查找指定的角色状态
	 * 
	 * @param index
	 * @return
	 */
	public SRPGStatus findActorStatus(int index) {
		return srpgActors.find(index).getActorStatus();
	}

	/**
	 * 构建一个SRPG角色
	 * 
	 * @param status
	 * @param mv
	 * @param atk
	 * @return
	 */
	protected SRPGActor makeActor(SRPGStatus status, AnimationHelper mv,
			AnimationHelper atk) {
		return new SRPGActor(status, mv, atk, tileWidth, tileHeight);
	}

	/**
	 * 构建一个SRPG角色
	 * 
	 * @param status
	 * @param mv
	 * @return
	 */
	protected SRPGActor makeActor(SRPGStatus status, AnimationHelper mv) {
		return new SRPGActor(status, mv, tileWidth, tileHeight);
	}

	/**
	 * 战场事务主循环
	 * 
	 */
	protected synchronized void mainProcess() {
		if (isClose()) {
			return;
		}
		// 开始执行战场事务
		isBattleStart = true;
		// 已进行回合数为0
		srpgTeams.setPhase(0);
		boolean battleStop = false;
		for (; isEventLoop && !isClose();) {
			if (!isEventLoop) {
				break;
			}
			// 处理选择的战场事件
			if (srpgEvent.queueExist() && battleStop) {
				callSRPGBattleProcEvent(srpgEvent);
			}
			if (!isEventLoop) {
				break;
			}
			// 可于此进行本回合的初始化事务
			processInitialize();
			if (!srpgTeams.checkMoving(srpgActors) || !battleStop) {
				if (battleStop) {
					srpgTeams.changePhase(srpgActors);
				} else {
					battleStop = true;
				}
				this.isAnimationEvent = true;
				this.processChangePhaseBefore();
				this.isAnimationEvent = false;
				srpgTeams.startTurn(srpgActors);
				afterCheck();
				// 胜利检查
				winnerCheck();
				if (!isEventLoop) {
					break;
				}
				if (!srpgTeams.checkPhase(srpgActors)) {
					continue;
				}
				int roleIndex = -1;
				for (int i = 0; i < srpgActors.size(); i++) {
					SRPGActor actor = srpgActors.find(i);
					if (!actor.isVisible()
							|| srpgTeams.getTeamPhase() != actor
									.getActorStatus().team) {
						continue;
					}
					if (actor.getActorStatus().leader == LEADER_NORMAL
							|| actor.getActorStatus().leader == LEADER_MAIN) {
						roleIndex = i;
						break;
					}
					if (roleIndex == -1) {
						roleIndex = i;
					}
				}
				// 当指定索引的角色存在时
				if (roleIndex != -1) {
					// 移动摄像机镜头向此角色
					centerCamera(roleIndex);
				}
				// 使用回合交替提示
				if (isPhase) {
					// 锁定键盘
					setLock(true);
					// 回合开始特效
					setEffect(makePhaseEffect(srpgTeams.getName()));
					setLock(false);
				}
				// 回合变更后
				processChangePhaseAfter();
				// 锁定摄像机
				setCameraLock(true);
				if (srpgActors == null) {
					return;
				}
				for (int i = 0; i < srpgActors.size(); i++) {
					SRPGActor actor = srpgActors.find(i);
					if (!actor.isVisible()) {
						continue;
					}
					SRPGStatus status = actor.getActorStatus();

					if (isEndView()) {
						actor.setActionEnd(false);
					}

					boolean isMoving = status.team == srpgTeams.getPhase()
							&& status.moveCheck() && status.action > 0;

					// 如果角色陷入恋爱状态失常
					if (isMoving && status.status[SRPGStatus.STATUS_LOVER] != 0) {
						int group = status.group;
						int[] computer = status.computer;
						status.group = status.substatus[SRPGStatus.STATUS_LOVER];
						status.computer = SRPGType.WIZARD_NORMAL;
						moveCPU(i, false);
						status.group = group;
						status.computer = computer;
						winnerCheck();

						// 当角色要求由CPU操纵时
					} else if (isMoving && status.isComputer) {
						int[] computer = status.computer;

						if (computer == null) {
							status.computer = SRPGType.NORMAL_PRIEST_WIZARD;
						}
						moveCPU(i, false);

						status.computer = computer;
						winnerCheck();
					}
					if (status.action == 0) {
						status.action = 1;
					}
				}
				setCameraLock(false);

			}
			if (srpgTeams.getTeamPhase() != 0) {
				setCameraLock(true);
				for (; isEventLoop;) {
					// 检查移动
					if (!srpgTeams.checkMoving(srpgActors)) {
						break;
					}
					for (int i = 0; i < srpgActors.size(); i++) {
						SRPGActor actor = srpgActors.find(i);
						SRPGStatus status1 = actor.getActorStatus();
						if (!actor.isVisible() || status1.action <= 0
								|| !status1.actionCheck()
								|| status1.team != srpgTeams.getTeamPhase()) {
							continue;
						}
						moveCPU(i);
						waitTime(sleepTime);
						if (!isEventLoop) {
							break;
						}
					}

				}
				setCameraLock(false);
				if (!isEventLoop) {
					break;
				}
				srpgTeams.endTurn(srpgActors);
			}
			try {
				super.wait();
			} catch (Exception ex) {
			}
		}
		// 战场事务结束
		isBattleStart = false;
	}

	/**
	 * 初始化地图元素
	 * 
	 * @param elements
	 */
	protected abstract void initFieldElementConfig(SRPGFieldElements elements);

	/**
	 * 初始化地图设置
	 * 
	 * @param field
	 */
	protected abstract void initMapConfig(SRPGField field);

	/**
	 * 初始化角色设置
	 * 
	 * @param actors
	 */
	protected abstract void initActorConfig(SRPGActors actors);

	/**
	 * 初始化角色分组
	 * 
	 * @param team
	 */
	protected abstract void initTeamConfig(SRPGTeams team);

	/**
	 * 因角色死亡而游戏胜利时调用此函数(建议重载为自己需要的效果)
	 * 
	 * @return
	 */
	protected boolean gameWinner() {
		setLock(true);
		setHelper(WINNER_LOSER[language_index][0]);
		isEventLoop = false;
		setLock(false);
		return false;
	}

	/**
	 * 因角色死亡而游戏失败时调用此函数（建议重载为自己需要的效果）
	 * 
	 * @return
	 */
	protected boolean gameLoser() {
		setLock(true);
		setHelper(WINNER_LOSER[language_index][1]);
		isEventLoop = false;
		setLock(false);
		return false;
	}

	/**
	 * 使用道具(暂未实现,建议重载)
	 * 
	 */
	protected synchronized void changeItem() {
		setHelper(NO_SUPPORT[language_index]);
		defaultCommand();
	}

	/**
	 * 游戏背景画面绘制
	 * 
	 * @param g
	 */
	public void background(GLEx g) {

	}

	/**
	 * 游戏前景画面绘制
	 * 
	 * @param g
	 */
	public void foreground(GLEx g) {
	}

	/**
	 * 载入战场前将调用此函数，如果返回值为true将循环调用
	 * 
	 * @return
	 */
	protected abstract boolean startProcess();

	/**
	 * 战场进程执行完毕后前将调用此函数，如果返回值为true将循环调用
	 * 
	 * @return
	 */
	protected abstract boolean endProcess();

	/**
	 * 战场主进程初始化时将调用此函数
	 * 
	 */
	protected void processInitialize() {

	}

	/**
	 * 战场主进行回合变更前将调用此函数
	 * 
	 */
	protected abstract void processChangePhaseBefore();

	/**
	 * 战场主进行回合变更后将调用此函数
	 * 
	 */
	protected abstract void processChangePhaseAfter();

	/**
	 * 特定角色死亡前调用此函数，并告知该角色ID
	 * 
	 * @param i
	 */
	protected abstract void processDeadActorBefore(int index, SRPGActor actor);

	/**
	 * 角色死亡且完全消失后将调用此函数
	 * 
	 * @param actor
	 */
	protected abstract void processDeadActorAfter(int index, SRPGActor actor);

	/**
	 * 当角色进行攻击前,将调用此函数
	 * 
	 * @param actor
	 */
	protected abstract void processAttackBefore(int index, SRPGActor actor);

	/**
	 * 当角色进行攻击后,将调用此函数
	 * 
	 * @param actor
	 */
	protected abstract void processAttackAfter(int index, SRPGActor actor);

	/**
	 * 当角色等级上升前，将调用此函数
	 * 
	 * @param actor
	 * @param level
	 */
	public abstract void processLevelUpBefore(int index, SRPGActor actor);

	/**
	 * 当角色等级上升后，将调用此函数
	 * 
	 * @param actor
	 * @param level
	 */
	public abstract void processLevelUpAfter(int index, SRPGActor actor);

	/**
	 * 伤害判定正式进行前将重载此函数,可以在此创建自己需要的任何伤害数值
	 * (也可以通过设置setBattle(false),在此处自行设置战斗画面及计算伤害)
	 * 
	 * @param damagedata
	 * @param atk
	 * @param def
	 */
	protected abstract void processDamageInputAfter(SRPGDamageData damagedata,
			int atk, int def);

	/**
	 * 伤害判定进行后将重载此函数,可以在此创建自己需要的任何伤害数值
	 * 
	 * @param damagedata
	 * @param atk
	 * @param def
	 */
	protected abstract void processDamageInputBefore(SRPGDamageData damagedata,
			int atk, int def);

	/**
	 * 某一分组灭亡时触发此函数
	 * 
	 * @param i
	 */
	protected void processDeadTeam(int i) {
	}

	/**
	 * 结束该回合的角色ID(最后一个行动的ID)
	 * 
	 * @param actorIndex
	 */
	protected void processTurnEndActor(int actorIndex) {
	}

	/**
	 * 角色死亡(如果返回假,则强制不死)
	 * 
	 * @param i
	 * @return
	 */
	protected boolean processDeadActor(int i) {
		return true;
	}

	/**
	 * 当前触屏所选中的角色
	 * 
	 * @param actor
	 * @param x
	 * @param y
	 */
	public abstract void onClickActor(final SRPGActor actor, int x, int y);

	/**
	 * 当前触屏所选中的非角色区域
	 * 
	 * @param element
	 * @param x
	 * @param y
	 */
	public abstract void onClickField(final SRPGFieldElement element, int x,
			int y);

	/**
	 * 构建回合交替特效(此为默认效果，建议重载)
	 * 
	 * @param teamName
	 * @return
	 */
	protected SRPGEffect makePhaseEffect(String teamName) {
		return new SRPGPhaseEffect(teamName);
	}

	/**
	 * 角色升级时特效
	 * 
	 * @param actor
	 * @return
	 */
	protected SRPGEffect makeUpperDeltaEffect(int index, SRPGActor actor) {
		return new SRPGUpperEffect(actor.drawX() + halfTileWidth, actor.drawY()
				+ tileHeight, LColor.black);
	}

	/**
	 * 构建战斗时技能名称画面，可重载此函数实现自己设计的同作用画面
	 * 
	 * @param factory
	 * @param status
	 * @return
	 */
	protected SRPGDrawView makeAbilityNameView(SRPGAbilityFactory factory,
			SRPGStatus status) {
		return new SRPGAbilityNameView(factory, status);
	}

	/**
	 * 构建伤害预估画面，可重载此函数实现自己设计的同作用画面
	 * 
	 * @param ab
	 * @param srpgField
	 * @param atk
	 * @param def
	 * @return
	 */
	protected SRPGDrawView makeDamageExpectView(final SRPGAbilityFactory ab,
			final SRPGField srpgField, int atk, int def) {
		SRPGDamageExpectView view = new SRPGDamageExpectView(ab, srpgField,
				srpgActors, atk, def);
		view.setLocation((getWidth() - view.getWidth()) / 2, getHeight()
				- view.getHeight() - 10);
		return view;
	}

	/**
	 * 构建角色详细状态画面，可重载此函数实现自己设计的同作用画面
	 * 
	 * @param status
	 * @return
	 */
	protected SRPGDrawView makeActorStatusView(SRPGStatus status) {
		SRPGDrawView view = new SRPGActorStatusView(status);
		view.setLocation(5, 5);
		return view;
	}

	/**
	 * 构建角色简单状态画面，可重载此函数实现自己设计的同作用画面
	 * 
	 * @param status
	 * @return
	 */
	protected SRPGDrawView makeActorMiniStatusView(SRPGStatus status,
			SRPGActor actor) {
		SRPGDrawView view = new SRPGMiniStatusView(status);
		view.setLocation(10, getHeight() - 10 - view.getHeight());
		if (mouse_x >= view.getLeft()
				&& mouse_x <= view.getLeft() + view.getWidth()
				&& mouse_y >= view.getTop()
				&& mouse_y <= view.getTop() + view.getHeight()) {
			view.setLocation(10, 10);
		}
		return view;
	}

	/**
	 * 构建角色特技
	 * 
	 * @param ability
	 * @param actor
	 * @param x
	 * @param y
	 * @return
	 */
	protected SRPGEffect makeAbilityEffect(SRPGAbilityFactory ability,
			SRPGActor actor, int x, int y) {
		return ability.getAbilityEffect(actor, x, y);
	}

	/**
	 * 构建点击角色产生的游标
	 * 
	 * @param w
	 * @param h
	 */
	protected void makeCursor(int w, int h) {
		this.cursor = AnimationHelper.makeCursor(w, h);
		this.isCursor = true;
	}

	/**
	 * 构建取消事件用的模拟按钮
	 * 
	 * @param f
	 */
	protected void makeEmulatorButton(String f) {
		makeEmulatorButton(f, -1, -1);
	}

	/**
	 * 构建取消事件用的模拟按钮
	 * 
	 * @param f
	 * @param x
	 * @param y
	 */
	protected void makeEmulatorButton(String f, int x, int y) {
		makeEmulatorButtons(LTextures.loadTexture(f), x, y);
	}

	/**
	 * 构建取消事件用的模拟按钮
	 * 
	 */
	protected void makeEmulatorButton() {
		makeEmulatorButton(-1, -1);
	}

	/**
	 * 构建取消事件用的模拟按钮
	 * 
	 * @param x
	 * @param y
	 */
	protected void makeEmulatorButton(int x, int y) {
		makeEmulatorButtons(null, x, y);
	}

	/**
	 * 构建取消事件用的模拟按钮
	 * 
	 * @param on
	 * @param un
	 * @param x
	 * @param y
	 */
	protected void makeEmulatorButtons(LTexture texture, int x, int y) {

		EmulatorListener listener = new EmulatorListener() {

			public void onUpClick() {
			}

			public void onDownClick() {
			}

			public void onLeftClick() {
			}

			public void onRightClick() {
			}

			public void onCircleClick() {
			}

			public void onCancelClick() {
				onCancel(-1, -1);
				setTouchLock(true);
			}

			public void onSquareClick() {
			}

			public void onTriangleClick() {
			}

			public void unCircleClick() {
			}

			public void unCancelClick() {
				setTouchLock(false);
			}

			public void unDownClick() {
			}

			public void unLeftClick() {
			}

			public void unRightClick() {
			}

			public void unSquareClick() {
			}

			public void unTriangleClick() {
			}

			public void unUpClick() {
			}

		};

		setEmulatorListener(listener);

		EmulatorButtons buttons = getEmulatorButtons();

		if (buttons != null) {

			buttons.hideLeft();
			EmulatorButton square = buttons.getSquare();
			EmulatorButton triangle = buttons.getTriangle();
			EmulatorButton circle = buttons.getCircle();
			EmulatorButton cancel = buttons.getCancel();
			if (texture != null ) {
				cancel.setClickImage(texture);
			}
			if (x != -1 || y != -1) {
				cancel.setLocation(x, y);
			} else {
				cancel.setLocation(getWidth() - circle.getWidth() - 40,
						getHeight() - cancel.getHeight() - 30);
			}
			circle.disable(true);
			square.disable(true);
			triangle.disable(true);

		}
	}

	/**
	 * 创建选择框
	 * 
	 * @param message
	 * @param view
	 */
	private void createChoice(String[] message, SRPGChoiceView view) {
		if (view == null) {
			view = new SRPGChoiceView(message, choiceFont, halfWidth - 50,
					halfHeight - 50);
		} else {
			view.set(message, choiceFont, halfWidth - 50, halfHeight - 50);
		}
	}

	/**
	 * 创建一副临时的对话框图像
	 * 
	 * @return
	 */
	private LTexture createTempImage() {
		if (messageImage == null) {
			LImage tmp = LImage.createImage(getWidth() - 40,
					getHeight() / 2 - 20, true);
			LGraphics g = tmp.getLGraphics();
			g.setColor(0, 0, 0, 125);
			g.fillRect(0, 0, tmp.getWidth(), tmp.getHeight());
			g.dispose();
			messageImage = new LTexture(GLLoader.getTextureData(tmp));
			if (tmp != null) {
				tmp.dispose();
				tmp = null;
			}
		}
		return messageImage;
	}

	/**
	 * 创建角色分组
	 * 
	 * @param actors
	 * @return
	 */
	public final SRPGTeams createTeams(SRPGActors actors) {
		if (srpgTeams == null) {
			this.srpgTeams = new SRPGTeams(actors);
		} else {
			this.srpgTeams.set(actors);
		}
		return srpgTeams;
	}

	private final void resetWindow() {
		if (srpgDrawView == null) {
			srpgDrawView = new SRPGDrawView();
			return;
		}
		srpgDrawView.reset();
	}

	// ---- 摄像机处理部分开始 ----//

	/**
	 * 让摄像机指向当前操作中角色
	 */
	public void centerCamera() {
		centerCamera(currentActor);
	}

	/**
	 * 让摄像机指向指定索引的角色
	 * 
	 * @param index
	 */
	public void centerCamera(int index) {
		SRPGActor actor = srpgActors.find(index);
		centerCamera((actor.drawX() + halfTileWidth) - halfWidth, (actor
				.drawY() + halfTileHeight)
				- halfHeight);
	}

	/**
	 * 设定摄像机向选择的SRPG角色居中
	 * 
	 */
	public void centerCameraSetting() {
		if (currentActor != -1) {
			centerCamera();
		}
	}

	/**
	 * 让摄像机处于指定的X,Y轴位置
	 * 
	 * @param x
	 * @param y
	 */
	public void centerCamera(int x, int y) {
		this.cam_x = x;
		this.cam_y = y;
	}

	/**
	 * 让摄像机尽量居中的移动向指定索引的X,Y坐标
	 * 
	 * @param x
	 * @param y
	 * @param sleep
	 */
	public void moveCameraCenter(int x, int y, int sleep) {
		moveCamera(x - halfWidth, y - halfHeight, sleep);
	}

	/**
	 * 让摄像机移动向指定索引的X,Y坐标
	 * 
	 * @param x
	 * @param y
	 * @param sleep
	 */
	public void moveCamera(int x, int y, int sleep) {
		boolean flag = isLock();
		setLock(true);
		for (float i = 0; i < sleep; i++) {
			float nx = (cam_x * (sleep - i)) / sleep + (x * i) / sleep;
			float ny = (cam_y * (sleep - i)) / sleep + (y * i) / sleep;
			if (nx >= 0.0F) {
				nx += 0.5F;
			} else {
				nx -= 0.5F;
			}
			if (ny >= 0.0F) {
				ny += 0.5F;
			} else {
				ny -= 0.5F;
			}
			centerCamera((int) nx, (int) ny);
			try {
				super.wait();
			} catch (Exception ex) {
			}
		}
		centerCamera((int) x, (int) y);
		setLock(flag);
	}

	/**
	 * 让摄像机移动向指定索引的角色
	 * 
	 * @param index
	 * @param sleep
	 */
	public void moveCamera(int index, int sleep) {
		SRPGActor actor = srpgActors.find(index);
		moveCamera((actor.drawX() + halfTileWidth) - halfWidth,
				(actor.drawY() + halfTileHeight) - halfHeight, sleep);
	}

	public void setCameraX(int i) {
		cam_x = i;
	}

	public void setCameraY(int i) {
		cam_y = i;
	}

	public int getCameraX() {
		return cam_x;
	}

	public int getCameraY() {
		return cam_y;
	}

	public void setCenterActor(int i) {
		currentActor = i;
	}

	public int getCenterActor() {
		return currentActor;
	}

	// ---- 摄像机处理部分结束 ----//

	// ---- 攻击伤害部分开始 ----//

	private void returningDamageValue(int[] ability, int atk, int def) {
		SRPGActor attacker = srpgActors.find(atk);
		SRPGActor defender = srpgActors.find(def);
		// 判定是否允许当前角色移动
		if (!attacker.getActorStatus().moveCheck()) {
			return;
		}
		if (srpgField.getPosMapElement(attacker.getPosX(), attacker.getPosY()).state == 4) {
			return;
		}
		if (defender.getActorStatus().hp <= 0
				&& !defender.getActorStatus().checkSkill(
						SRPGStatus.SKILL_UNDEAD)) {
			return;
		}
		int[] res = SRPGAbilityFactory.filtedRange(SRPGAbilityFactory
				.filtedAbility(ability, attacker.getActorStatus(), false),
				srpgField, attacker.getPosX(), attacker.getPosY(), defender
						.getPosX(), defender.getPosY());
		if (res == null) {
			return;
		}
		SRPGPosition oldPosition = srpgPosition;
		if (tempPosition == null) {
			tempPosition = new SRPGPosition();
		} else {
			tempPosition.reset();
		}
		srpgPosition = tempPosition;
		srpgPosition.counter = true;
		srpgPosition.setTarget(defender.getPosX(), defender.getPosY());

		int optimizeAbility = 0;
		srpgPosition.number = atk;
		srpgPosition.enemy = def;
		procFlag = PROC_COUNTER;
		srpgPosition.ability = res[0];
		setTargetRange(res[0], srpgPosition.target[0], srpgPosition.target[1]);
		if (attacker.getActorStatus().team == 0) {
			setChoiceAbility(res);
			srpgPosition.ability = srpgChoiceView.getJointContent();
			srpgDrawView = makeDamageExpectView(SRPGAbilityFactory
					.getInstance(srpgChoiceView.getJointContent()), srpgField,
					srpgPosition.number, srpgPosition.enemy);

			optimizeAbility = srpgChoiceView.choiceWait(this);
		} else {
			optimizeAbility = SRPGAbilityFactory.getOptimizeAbility(res,
					srpgField, srpgActors, atk, def);
			if (isEnemyView()) {
				boolean flag = getCameraLock();
				setCameraLock(true);
				srpgPosition.ability = optimizeAbility;
				setTargetRange(optimizeAbility, srpgPosition.target[0],
						srpgPosition.target[1]);
				srpgDrawView = makeDamageExpectView(SRPGAbilityFactory
						.getInstance(optimizeAbility), srpgField,
						srpgPosition.number, srpgPosition.enemy);
				waitTime(sleepTime * 2);
				setCameraLock(flag);
			}
		}
		procFlag = PROC_ATTACK;
		resetWindow();
		srpgPosition.ability = optimizeAbility;
		setDamageValueImplement(optimizeAbility, srpgPosition.number,
				SRPGAbilityOption.getInstance(false), false);
		srpgPosition = oldPosition;
	}

	protected synchronized boolean setDamageValue(int number, int index) {
		return setDamageValue(number, index, true);
	}

	protected synchronized boolean setDamageValue(int number, int index,
			boolean flag) {
		return setDamageValue(number, index, SRPGAbilityOption
				.getInstance(flag), true);
	}

	protected synchronized boolean setDamageValue(int number, int index,
			boolean flag, boolean flag1) {
		return beforeDamageValue(number, index, SRPGAbilityOption
				.getInstance(flag), flag1);
	}

	protected synchronized boolean setDamageValue(int number, int index,
			SRPGAbilityOption abilityoption, boolean flag) {
		return beforeDamageValue(number, index, abilityoption, flag);
	}

	protected synchronized boolean beforeDamageValue(int number, int index,
			SRPGAbilityOption abilityoption, boolean flag) {
		SRPGAbilityFactory ability = SRPGAbilityFactory.getInstance(number);
		if (ability.checkAbilitySkill(SRPGStatus.SKILL_CARRY)) {
			int ai[] = srpgPosition.target;
			int l = srpgActors.checkActor(ai[0], ai[1]);
			if (!getTargetTrue(ability, index, l)) {
				setHelper(STRING_CARRY[language_index][0]);
				return false;
			}
			setHelper(STRING_CARRY[language_index][1]);
			SRPGActor actor = srpgActors.find(l);
			int nx = -1;
			int ny = -1;
			do {
				for (;;) {
					if (srpgChoiceField == null) {
						srpgChoiceField = new SRPGFieldChoiceView(srpgField);
					} else {
						srpgChoiceField.set(srpgField);
					}
					int[] res = srpgChoiceField.choiceWait(this, true);
					if (res == null) {
						return false;
					}
					nx = res[0];
					ny = res[1];
					if (srpgActors.checkActor(nx, ny) == -1
							&& srpgField.getMoveCost(
									actor.getActorStatus().movetype, nx, ny) != -1) {
						break;
					}
					setHelper(STRING_CARRY[language_index][2]);
				}
				String[] mes = { STRING_CARRY[language_index][3],
						STRING_CARRY[language_index][4] };
				createChoice(mes, srpgChoiceView);
			} while (srpgChoiceView.choiceWait(this, true) != 0);
			abilityoption.warp = true;
			abilityoption.setWarpPos(nx, ny);
		}
		setDamageValueImplement(number, index, abilityoption, flag);
		return true;
	}

	protected synchronized void setDamageValueImplement(int abilityIndex,
			int roleIndex, SRPGAbilityOption abilityoption, boolean flag) {
		SRPGAbilityFactory ability = SRPGAbilityFactory
				.getInstance(abilityIndex);
		SRPGActor actorObject = srpgActors.find(roleIndex);
		setLock(true);
		processAttackBefore(roleIndex, actorObject);
		if (abilityoption.extinctmp) {
			actorObject.getActorStatus().mp -= ability.getMP(actorObject
					.getActorStatus());
		}
		SRPGDamageAverage damageaverage = new SRPGDamageAverage();
		SRPGDrawView temp_view = srpgDrawView;
		srpgDrawView = makeAbilityNameView(ability, actorObject
				.getActorStatus());
		int[] res = srpgPosition.target;
		int posX = res[0];
		int posY = res[1];
		actorObject.setDirection(actorObject.findDirection(posX, posY));
		if (posX * tileWidth - cam_x < tileWidth
				|| posY * tileHeight - cam_y < tileHeight
				|| (posX * tileWidth - cam_x) + tileWidth > getWidth()
						- tileWidth
				|| (posX * tileHeight - cam_y) + tileHeight > getHeight()
						- tileHeight) {
			moveCameraCenter(posX * tileWidth + halfTileWidth, posY
					* tileHeight + halfTileWidth, 10);
		}
		actorObject.setAttack(true);

		// 使用技能
		setEffect(makeAbilityEffect(ability, actorObject, posX, posY));

		setLock(false);

		int[] actors = new int[srpgActors.size()];
		for (int i = 0; i < actors.length; i++) {
			actors[i] = -1;
		}

		int count = 0;
		int actorExp = 0;

		setTargetRange(abilityIndex, posX, posY);
		int[][] area = srpgPosition.area;
		for (int y = 0; y < area.length; y++) {
			for (int x = 0; x < area[y].length; x++) {
				if (area[y][x] == -1) {
					continue;
				}
				int index = srpgActors.checkActor(x, y);

				if (index == -1
						|| !getTargetTrue(ability, roleIndex, index)
						&& (abilityoption.counter || index != srpgPosition.enemy)) {
					continue;
				}
				boolean lock = isLock();
				setLock(true);
				SRPGActor actor = srpgActors.find(index);
				int chp = actor.getActorStatus().hp;
				srpgDrawView = makeActorMiniStatusView(actor.getActorStatus(),
						actor);
				waitTime(sleepTime);

				SRPGDamageData damageData = null;
				if (isBattle) {
					damageData = ability.getDamageExpect(srpgField, srpgActors,
							roleIndex, index);
					if (damageData.isHit()) {
						if (abilityoption.warp) {
							actor.setVisible(false);
							actor.setPos(abilityoption.warp_pos);
							if (actor.drawX() - cam_x < tileWidth
									|| actor.drawY() - cam_y < tileHeight
									|| (actor.drawX() - cam_x) + tileWidth > getWidth()
											- tileWidth
									|| (actor.drawY() - cam_y) + tileHeight > getHeight()
											- tileHeight) {
								moveCamera((actor.drawX() + halfTileWidth)
										- halfWidth,
										(actor.drawY() + halfTileHeight)
												- halfHeight, 10);
							}
							waitTime(sleepTime);
							actor.setVisible(true);

							setEffect(SRPGEffectFactory.getAbilityEffect(
									SRPGEffectFactory.EFFECT_OUT, actor, actor
											.getPosX(), actor.getPosY()));

							waitTime(sleepTime);
						}
					}
					if (damageData.isHit() && damageData.getMoveStack() != null) {
						damageData.getMoveStack().moveActor(actor, this);
					}
					if (damageData.isHit() && damageData.getPosX() != -1
							&& damageData.getPosY() != -1) {
						actor
								.setPos(damageData.getPosX(), damageData
										.getPosY());
					}
					if (actor.getActorStatus().moveCheckStatus()
							&& damageData.getActorStatus().moveCheckStatus()) {
						actor.setDirection(actor.findDirection(actorObject
								.getPosX(), actorObject.getPosY()));
					}
					if (damageData.isHit() && damageData.getDirection() != -1) {
						actor.setDirection(damageData.getDirection());
					}
					if (damageData.isHit()) {
						damageaverage.addDamage(damageData.getDamage());
						damageaverage.addMP(damageData.getMP());
					}
				} else {
					damageData = new SRPGDamageData();
					processDamageInputAfter(damageData, roleIndex, index);
				}

				int cexp = ((actor.getActorStatus().level - actorObject
						.getActorStatus().level) + 1) * 4;
				if (cexp < 1) {
					cexp = 1;
				}
				actorExp += cexp;

				processDamageInputBefore(damageData, roleIndex, index);
				setDamage(damageData, actor);
				resetWindow();
				if (damageData.isHit()) {
					innerDamageValue(ability, damageData, index);
				}
				if (actor.getActorStatus().hp <= 0
						&& chp > 0
						&& !actor.getActorStatus().checkSkill(
								SRPGStatus.SKILL_UNDEAD)) {
					int exp = ((actor.getActorStatus().level - actorObject
							.getActorStatus().level) + 1) * 60 - 40;
					if (exp < 1) {
						exp = 1;
					}
					actorExp += exp;
				}
				if (ability.getDirect() == 0 && roleIndex != index) {
					actors[count] = index;
					count++;
				}
				setLock(lock);
			}
		}

		// 获得技能伤害判定
		SRPGDamageData damagedata = ability.dataInput(damageaverage,
				actorObject.getActorStatus());

		if (damagedata != null) {
			boolean isLock = isLock();
			this.setLock(true);
			this.srpgDrawView = makeActorMiniStatusView(actorObject
					.getActorStatus(), actorObject);
			this.waitTime(sleepTime);
			this.processDamageInputBefore(damagedata, -1, roleIndex);
			this.setDamage(damagedata, srpgActors.find(roleIndex));
			this.resetWindow();
			this.setLock(isLock);
		}

		if (actorExp > maxExp) {
			actorExp = maxExp;
		}
		actorObject.getActorStatus().exp += actorExp;
		if (actorObject.getActorStatus().exp > maxExp) {
			actorObject.getActorStatus().exp = maxExp;
		}
		resetWindow();
		if (temp_view.isExist()) {
			srpgDrawView = temp_view;
		} else {
			srpgDrawView = new SRPGDrawView();
		}
		int type = 1;
		if (actorObject.getActorStatus().status[SRPGStatus.STATUS_DUPLICATE] != 0) {
			type = 2;
		}
		abilityoption.attack_value = abilityoption.attack_value + 1;
		if (type > abilityoption.attack_value
				&& abilitySuccess(ability, actorObject, null, posX, posY)) {
			boolean lock = isLock();
			setLock(true);
			waitTime(sleepTime);
			setEffect(makeUpperDeltaEffect(roleIndex, actorObject));
			waitTime(sleepTime);
			SRPGAbilityOption option = SRPGAbilityOption.getInstance(false);
			option.attack_value = abilityoption.attack_value;
			setDamageValueImplement(abilityIndex, roleIndex, option, false);
			setLock(lock);
		}
		if (!abilityoption.counter) {
			return;
		}
		int typeFlag = 1;
		if (ability.checkAbilitySkill(SRPGStatus.SKILL_STATUSINVALID)) {
			typeFlag = 2;
		}
		if (ability.checkAbilitySkill(SRPGStatus.SKILL_DOUBLEATTACK)) {
			typeFlag = 3;
		}
		for (int i = 1; i < typeFlag; i++) {
			if (abilitySuccess(ability, actorObject, null, posX, posY)) {
				setDamageValueImplement(abilityIndex, roleIndex,
						SRPGAbilityOption.getInstance(false), false);
			}
		}
		for (int i = 0; i < actors.length && actors[i] != -1; i++) {
			returningDamageValue(
					srpgActors.find(actors[i]).getActorStatus().ability,
					actors[i], roleIndex);
		}
		if ((ability.checkAbilitySkill(SRPGStatus.SKILL_DOUBLEACTION) || actorObject
				.getActorStatus().checkSkill(SRPGStatus.SKILL_DOUBLEATTACK))
				&& abilitySuccess(ability, srpgActors.find(roleIndex), null,
						posX, posY)) {
			setDamageValueImplement(abilityIndex, roleIndex, SRPGAbilityOption
					.getInstance(false), false);
		}

		processAttackAfter(roleIndex, actorObject);
		afterCheck();
		if (flag) {
			winnerCheck();
		}
		srpgEvent.reset();
	}

	protected boolean abilitySuccess(SRPGAbilityFactory ability, SRPGActor atk,
			SRPGActor def, int x, int y) {
		if (atk.getActorStatus().actionCheck()
				&& atk.getActorStatus().mp >= ability.getMP(atk
						.getActorStatus())) {
			boolean[][] range = ability.setTrueRange(srpgField, atk.getPosX(),
					atk.getPosY());
			if (def != null && range[def.getPosY()][def.getPosX()]) {
				return true;
			}
			if (def == null && range[y][x]) {
				if (ability.getSelectNeed() == 1) {
					return true;
				}
				int i = srpgActors.checkActor(x, y);
				if (i != -1
						&& ability.getSelectNeed() == 0
						&& ability.getTargetTrue(atk.getActorStatus().group,
								srpgActors.find(i).getActorStatus().group)) {
					return true;
				}
			}
		}
		return false;
	}

	protected void innerDamageValue(SRPGAbilityFactory ability,
			SRPGDamageData damagedata, int index) {
		SRPGActor actor = srpgActors.find(index);
		SRPGStatus status = actor.getActorStatus();
		if (ability.checkAbilitySkill(SRPGStatus.SKILL_UNDEAD)) {
			if (status.hp > 0) {
				status.hp = 0;
			}
			if (processDeadActor(index)) {
				actor.setVisible(false);
			}
		}
	}

	protected void setDamage(SRPGDamageData damagedata, SRPGActor actor) {
		SRPGStatus status = actor.getActorStatus();
		if (actor.drawX() - cam_x < tileWidth
				|| actor.drawY() - cam_y < tileHeight
				|| (actor.drawX() - cam_x) + tileWidth > getWidth() - tileWidth
				|| (actor.drawY() - cam_y) + tileHeight > getHeight()
						- tileHeight) {
			moveCamera((actor.drawX() + halfTileWidth) - halfWidth, (actor
					.drawY() + halfTileHeight)
					- halfHeight, 10);
		}
		if (damagedata.getGenre() != GENRE_MPRECOVERY
				&& damagedata.getGenre() != GENRE_MPDAMAGE) {
			status.hp = damagedata.getActorStatus().hp;
		} else {
			status.mp = damagedata.getActorStatus().mp;
		}
		setEffect(damagedata.getNumberEffect(actor.getPosX() * tileWidth, actor
				.getPosY()
				* tileHeight));
		if ((damagedata.getGenre() == GENRE_ALLDAMAGE || damagedata.getGenre() == GENRE_ALLRECOVERY)
				&& damagedata.isHit()) {
			int genre = 0;
			if (damagedata.getGenre() == GENRE_ALLDAMAGE) {
				genre = GENRE_MPRECOVERY;
			} else if (damagedata.getGenre() == GENRE_ALLRECOVERY) {
				genre = GENRE_MPDAMAGE;
			}
			status.mp = damagedata.getActorStatus().mp;
			setEffect(damagedata.getNumberEffect(genre, String
					.valueOf(damagedata.getMP()), actor.getPosX() * tileWidth,
					actor.getPosY() * tileHeight));
		}
		SRPGEffect[] effects = damagedata.getPopupEffect(actor.getPosX()
				* tileWidth, actor.getPosY() * tileHeight);
		if (effects != null) {
			for (int i = 0; i < effects.length; i++) {
				srpgEffect = effects[i];
				srpgEffect.wait(this);
			}
		}
		status.copy(damagedata.getActorStatus());
	}

	// ---- 攻击伤害部分结束 ----//

	/**
	 * 获得角色移动范围
	 * 
	 * @param i
	 * @param actor
	 */
	private void setMoveViews(int i, SRPGActor actor) {
		srpgPosition.number = i;
		move = actor.getActorStatus().move;
		if (srpgMove == null) {
			srpgMove = SRPGFieldMove.getInstance(srpgField.getMoveSpaceAll(
					srpgActors, i));
		} else {
			srpgMove.set(srpgField.getMoveSpaceAll(srpgActors, i));
		}
		srpgPosition.area = srpgMove.moveArea(actor.getPosX(), actor.getPosY(),
				move);
		procFlag = PROC_MOVEVIEW;
	}

	/**
	 * 设定角色移动范围
	 * 
	 * @param i
	 * @param actor
	 */
	private synchronized void setMove(int i, SRPGActor actor) {
		setMoveViews(i, actor);
		if (srpgChoiceField == null) {
			srpgChoiceField = new SRPGFieldChoiceView(srpgField);
		} else {
			srpgChoiceField.set(srpgField);
		}
		int[] pos = srpgChoiceField.choiceWait(this, true);
		resetWindow();
		if (pos == null) {
			procFlag = PROC_NORMAL;
		} else {
			if (actor.getActorStatus().team != 0
					|| actor.getActorStatus().action == 0
					|| !actor.getActorStatus().actionCheck()
					|| actor.getActorStatus().isComputer) {
				procFlag = PROC_NORMAL;
				return;
			}
			srpgPosition.route = srpgMove.moveRoute(actor.getPosX(), actor
					.getPosY(), pos[0], pos[1], move);
			if (actor.getPosX() == pos[0] && actor.getPosY() == pos[1]) {
				srpgPosition.setPast(actor.getPosX(), actor.getPosY());
				srpgPosition.vector = actor.getDirection();
				defaultCommand();
				return;
			}
			if (srpgActors.checkActor(pos[0], pos[1]) != -1) {
				setHelper(TOUCH_NO_SUPPORT[language_index][0]);
				setMove(i, actor);
				return;
			}
			if (srpgPosition.route == null) {
				setHelper(TOUCH_NO_SUPPORT[language_index][0]);
				setMove(i, actor);
				return;
			}
			procFlag = PROC_MOVING;
			srpgPosition.setPast(actor.getPosX(), actor.getPosY());
			srpgPosition.vector = actor.getDirection();
			callActorMove(srpgPosition.number);
			srpgEvent.reset();
			defaultCommand();
		}

	}

	/**
	 * 让CPU自行计算指定角色的移动
	 * 
	 * @param i
	 */
	private void moveCPU(int i) {
		moveCPU(i, true);
	}

	/**
	 * 让CPU自行计算指定角色的移动，并设定是否显示造成的伤害
	 * 
	 * @param i
	 * @param flag
	 */
	private void moveCPU(int index, boolean flag) {
		centerCamera(index);
		SRPGActor actor = srpgActors.find(index);
		if (srpgAI == null) {
			srpgAI = new SRPGAI(srpgField, srpgActors, index, actor
					.getActorStatus().computer);
		} else {
			srpgAI.set(srpgField, srpgActors, index,
					actor.getActorStatus().computer);
		}
		srpgAI.runThinking();
		srpgPosition.number = index;
		// 当显示敌方视图时
		if (isEnemyView()) {
			setMoveViews(index, actor);
			waitTime(sleepTime);
			procFlag = PROC_MOVING;
		}
		// 当存在移动区域时
		if (srpgAI.getRoute() != null) {
			callActorMove(index, srpgAI.getRoute());
			waitTime(sleepTime);
		}
		if (srpgAI.getAbility() != -1) {
			srpgPosition.setTarget(srpgAI.getTargetX(), srpgAI.getTargetY());
			srpgPosition.ability = srpgAI.getAbility();
			if (isEnemyView()) {
				setAttackRange(srpgAI.getAbility(), actor.getPosX(), actor
						.getPosY());
				procFlag = PROC_ABILITYTARGET;
				moveCameraCenter(srpgAI.getTargetX() * tileWidth
						+ halfTileWidth, srpgAI.getTargetY() * tileHeight
						+ halfTileHeight, 8);
				waitTime(sleepTime / 2);
				int role = srpgActors.checkActor(srpgAI.getTargetX(), srpgAI
						.getTargetY());
				if (!getTargetTrue(SRPGAbilityFactory.getInstance(srpgAI
						.getAbility()), srpgPosition.number, role)) {
					role = -1;
				}
				int[] res = { srpgAI.getTargetX(), srpgAI.getTargetY() };
				abilityTargetSetting(srpgAI.getAbility(), res, index, role);
				procFlag = PROC_TARGETSURE;
				waitTime(sleepTime * 3);
			}
			resetWindow();
			procFlag = PROC_ATTACK;
			setDamageValue(srpgAI.getAbility(), index, true, flag);
		} else if (srpgAI.getDirection() != -1) {
			actor.setDirection(srpgAI.getDirection());
		}
		procFlag = PROC_NORMAL;
		setTurnMinus();
	}

	/**
	 * 向指定方向移动对应指定索引的角色
	 * 
	 * @param i
	 */
	public void callActorMove(int i) {
		callActorMove(i, srpgPosition.route);
	}

	/**
	 * 按照指定的位置，向指定方向移动对应指定索引的角色
	 * 
	 * @param i
	 * @param res
	 */
	public void callActorMove(int i, int[][] res) {
		setCenterActor(i);
		SRPGActor actor = srpgActors.find(i);
		for (int j = 0; j < res.length; j++) {
			int direction = MOVE_DOWN;
			int x = res[j][0] - actor.getPosX();
			int y = res[j][1] - actor.getPosY();
			if (y < 0) {
				direction = MOVE_UP;
			} else if (y > 0) {
				direction = MOVE_DOWN;
			} else if (x < 0) {
				direction = MOVE_LEFT;
			} else if (x > 0) {
				direction = MOVE_RIGHT;
			}
			actor.moveActorShow(direction, getMoveSpeed());
			actor.waitMove(this);
		}
		centerCamera();
		setCenterActor(-1);
	}

	private void afterCheck() {
		boolean[] teamValues = new boolean[SRPGTeams.getTeamsValue(srpgActors)];
		for (int i = 0; i < teamValues.length; i++) {
			teamValues[i] = true;
		}
		for (int j = 0; j < srpgActors.size(); j++) {
			SRPGActor actor = srpgActors.find(j);
			if (!actor.isVisible()) {
				continue;
			}
			SRPGStatus status = actor.getActorStatus();
			if (status.status[SRPGStatus.STATUS_REVIVE] != 0 && status.hp <= 0
					&& !status.checkSkill(SRPGStatus.SKILL_UNDEAD)) {
				int hp = status.max_hp / 2;
				if (actor.drawX() - cam_x < 0 || actor.drawY() - cam_y < 0
						|| actor.drawX() - cam_x > getWidth() - tileWidth
						|| actor.drawY() - cam_y > getHeight() - tileHeight) {
					moveCamera(j, 10);
				}

				// 角色死亡
				actor.setVisible(false);
				processDeadActorAfter(j, actor);
				// 延迟250毫秒
				waitTime(sleepTime);

				setEffect(SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_OUT, actor, actor.getPosX(),
						actor.getPosY()));

				srpgDrawView = makeActorMiniStatusView(actor.getActorStatus(),
						actor);

				waitTime(sleepTime);
				SRPGDamageData damagedata = new SRPGDamageData();
				damagedata.setDamage(hp);
				damagedata.setGenre(GENRE_RECOVERY);
				damagedata.setActorStatus(SRPGAbilityFactory.damageInput(
						damagedata, status));
				processDamageInputBefore(damagedata, -1, j);
				setDamage(damagedata, actor);
				int chp = status.hp;
				status.defaultStatus();
				status.hp = chp;
				resetWindow();
			}
			boolean flag = false;
			SRPGFieldElement element = srpgField.getPosMapElement(actor
					.getPosX(), actor.getPosY());
			// 特殊地形判定
			if (element != null && element.state == SRPGField.FIELD_KILL) {
				flag = true;
				if (status.hp > 0) {
					status.hp = 0;
				}
			} else if (element != null && element.state == SRPGField.FIELD_PLUS) {

				flag = true;
				if (status.hp > 0) {
					status.hp += status.max_hp / 10;
					status.mp += status.max_mp / 10;
				}
				if (status.hp > status.max_hp) {
					status.hp = status.max_hp;
				}
				if (status.mp > status.max_mp) {
					status.mp = status.max_mp;
				}

			}
			if ((status.hp > 0 || status.checkSkill(SRPGStatus.SKILL_UNDEAD))
					&& !flag) {
				continue;
			}
			processDeadActorBefore(j, actor);
			if (!processDeadActor(j)) {
				continue;
			}
			if (actor.drawX() - cam_x < 0 || actor.drawY() - cam_y < 0
					|| actor.drawX() - cam_x > getWidth() - tileWidth
					|| actor.drawY() - cam_y > getHeight() - tileHeight) {
				moveCamera(j, 10);
			}
			actor.setVisible(false);
			if (teamValues[status.team]) {
				teamValues[status.team] = srpgTeams.leaderCheck(srpgActors, j);
			}
			processDeadActorAfter(j, actor);

		}

		// 判定是否有角色应当升级
		for (int j = 0; j < srpgActors.size(); j++) {
			SRPGActor actor = srpgActors.find(j);
			SRPGStatus status = actor.getActorStatus();
			if (!actor.isExist() || !actor.isVisible()
					|| (status.exp != -100 && status.exp < maxExp)) {
				continue;
			}
			setLock(true);
			processLevelUpBefore(j, actor);
			if (actor.drawX() - cam_x < 0 || actor.drawY() - cam_y < 0
					|| actor.drawX() - cam_x > getWidth() - tileWidth
					|| actor.drawY() - cam_y > getHeight() - tileHeight) {
				moveCamera(j, 10);
			}
			int d = actor.getDirection();
			int[] moving = { MOVE_DOWN, MOVE_LEFT, MOVE_UP, MOVE_RIGHT,
					MOVE_DOWN };
			for (int i = 0; i < moving.length; i++) {
				actor.setDirection(moving[i]);
				waitFrame(2);
			}
			if (status.exp != -100) {
				setEffect(new SRPGNumberEffect(actor.getPosX() * tileWidth,
						actor.getPosY() * tileHeight, LColor.red, "Level Up!"));
				int level = status.level;
				if (level < maxLevel) {
					status = SRPGActorFactory.runLevelUp(status, level + 1);
				}

			} else {
				setEffect(new SRPGNumberEffect(actor.getPosX() * tileWidth,
						actor.getPosY() * tileHeight, LColor.red,
						"Level Down!"));
			}

			status.exp = 0;
			actor.setDirection(d);
			processLevelUpAfter(j, actor);
			setLock(false);
		}

		for (int j = 0; j < teamValues.length; j++) {
			if (teamValues[j]) {
				continue;
			}
			processDeadTeam(j);
			setLock(true);
			for (int i = 0; i < srpgActors.size(); i++) {
				SRPGActor actor = srpgActors.find(i);
				if (actor.isVisible() && actor.getActorStatus().team == j) {
					waitTime(sleepTime);
					actor.setVisible(false);
				}
			}
			setLock(false);
		}

	}

	/**
	 * 胜利判定(建议重载完成自己需要的额外判定)
	 * 
	 */
	protected void winnerCheck() {
		int group = 0;
		int index = 0;
		for (;;) {
			if (index >= srpgActors.size()) {
				break;
			}
			SRPGActor actor = srpgActors.find(index);
			if (actor.isVisible() && actor.getActorStatus().team == 0) {
				group = actor.getActorStatus().group;
				break;
			}
			index++;
		}
		if (!srpgTeams.checkPhase(0, srpgActors)) {
			for (; gameLoser();) {
				try {
					super.wait();
				} catch (Exception ex) {
				}
			}
			return;
		}
		for (int i = 0; i < srpgActors.size(); i++) {
			SRPGActor actor = srpgActors.find(i);
			if (actor.isVisible() && actor.getActorStatus().group != group) {
				return;
			}
		}
		for (; gameWinner();) {
			try {
				super.wait();
			} catch (Exception ex) {
			}
		}
	}

	public boolean getTargetTrue(SRPGAbilityFactory ability, int atk, int def) {
		return def != -1
				&& (ability.getTarget() != 0 || srpgActors.find(atk)
						.getActorStatus().group != srpgActors.find(def)
						.getActorStatus().group)
				&& (ability.getTarget() != 1 || srpgActors.find(atk)
						.getActorStatus().group == srpgActors.find(def)
						.getActorStatus().group);
	}

	/**
	 * 重新开始战斗
	 * 
	 */
	protected synchronized void battleReset() {
		setLock(true);
		isEventLoop = false;
		waitTime(1000);
		srpgActors.reset();
		if (srpgActors != null) {
			initActorConfig(srpgActors);
		}
		createTeams(srpgActors);
		if (srpgTeams != null) {
			initTeamConfig(srpgTeams);
		}
		isEventLoop = true;
		mainProcess();
		setLock(false);
	}

	/**
	 * 敌我分组
	 * 
	 */
	private synchronized void teamList() {
		for (;;) {
			int size = SRPGTeams.getTeamsAlive(srpgActors);
			String[] names = new String[size];
			int[] phase = new int[size];
			int count = 0;
			newFor: for (int i = 0; i < srpgTeams.getLength(); i++) {
				int index = 0;
				for (;;) {
					if (index >= srpgActors.size()) {
						continue newFor;
					}
					if (srpgActors.find(index).isVisible()
							&& srpgActors.find(index).getActorStatus().team == srpgTeams
									.getTeamPhase(i)) {
						names[count] = srpgTeams.getName(i);
						phase[count] = srpgTeams.getTeamPhase(i);
						count++;
						continue newFor;
					}
					index++;
				}
			}
			if (srpgChoiceView == null) {
				srpgChoiceView = new SRPGChoiceView(names, phase, choiceFont,
						choiceX, choiceY);
			} else {
				srpgChoiceView.set(names, phase, choiceFont, choiceX, choiceY);
			}
			int index = srpgChoiceView.choiceWait(this, true);
			if (index != -1) {
				actorList(index);
			} else {
				return;
			}
		}
	}

	/**
	 * 角色列表
	 * 
	 * @param i
	 */
	private synchronized void actorList(int i) {
		for (;;) {
			int j = 0;
			for (int c = 0; c < srpgActors.size(); c++) {
				if (srpgActors.find(c).isVisible()
						&& srpgActors.find(c).getActorStatus().team == i) {
					j++;
				}
			}
			if (j == 0) {
				return;
			}
			String[][] mes = new String[j][3];
			int[] list = new int[j];
			int l = 0;
			for (int c = 0; c < srpgActors.size(); c++) {
				if (!srpgActors.find(c).isVisible()) {
					continue;
				}
				SRPGStatus status = srpgActors.find(c).getActorStatus();
				if (status.team == i) {
					mes[l][0] = status.name;
					mes[l][1] = status.jobname;
					mes[l][2] = String.valueOf(status.hp + " / "
							+ status.max_hp);
					list[l] = c;
					l++;
				}
			}
			srpgChoiceView = new SRPGChoiceView(mes, list, choiceFont, 15, 15);
			srpgChoiceView.setTab(15);
			int index = srpgChoiceView.choiceWait(this, true);
			if (index != -1) {
				moveCamera((srpgActors.find(index).drawX() - halfWidth)
						+ halfTileWidth,
						(srpgActors.find(index).drawY() - halfHeight)
								+ halfTileHeight, 10);
				setCameraLock(true);
				waitTime(sleepTime);
				setCameraLock(false);
				srpgDrawView = makeActorStatusView(srpgActors.find(index)
						.getActorStatus());
			} else {
				return;
			}
		}

	}

	/**
	 * 默认的背景菜单1
	 * 
	 */
	protected synchronized void defaultBackMenu1() {
		boolean flag = true;
		for (;;) {
			if (!flag) {
				break;
			}
			createChoice(BACK_MENU_1[language_index], srpgChoiceView);
			switch (srpgChoiceView.choiceWait(this, true)) {
			case 0:
				createChoice(YES_NO[language_index], srpgChoiceView);
				if (srpgChoiceView.choiceWait(this, true) == 0) {
					srpgTeams.endTurn(srpgActors);
					flag = false;
				}
				break;
			case 1:
				teamList();
				break;
			case 2:
				defaultBackMenu2();
				break;
			case 3:
				createChoice(YES_NO[language_index], srpgChoiceView);
				if (srpgChoiceView.choiceWait(this, true) == 0) {
					flag = false;
					battleReset();
				}
				break;
			default:
				flag = false;
				break;
			}
		}
	}

	/**
	 * 默认的背景菜单2
	 * 
	 */
	protected synchronized void defaultBackMenu2() {
		boolean flag = true;
		int i = 0;
		for (;;) {
			if (!flag) {
				break;
			}
			int menuIndex = 0;

			// 是否显示棋盘,0
			String[] menuItemGridSelect = new String[2];
			menuItemGridSelect[0] = BACK_MENU_2[language_index][0];
			if (isGrid()) {
				menuItemGridSelect[1] = DISPLAY[language_index][0];
			} else {
				menuItemGridSelect[1] = DISPLAY[language_index][1];
			}
			menuItems[menuIndex++] = menuItemGridSelect;

			// 是否显示敌我标识,1
			String[] menuItemTeamColorSelect = new String[2];
			menuItemTeamColorSelect[0] = BACK_MENU_2[language_index][1];
			if (isTeamColor()) {
				menuItemTeamColorSelect[1] = DISPLAY[language_index][0];
			} else {
				menuItemTeamColorSelect[1] = DISPLAY[language_index][1];
			}
			menuItems[menuIndex++] = menuItemTeamColorSelect;

			// 是否显示角色行动完毕,2
			String[] menuItemEndViewSelect = new String[2];
			menuItemEndViewSelect[0] = BACK_MENU_2[language_index][2];
			if (isEndView()) {
				menuItemEndViewSelect[1] = DISPLAY[language_index][0];
			} else {
				menuItemEndViewSelect[1] = DISPLAY[language_index][1];
			}
			menuItems[menuIndex++] = menuItemEndViewSelect;

			// 是否显示敌方行动,4
			String[] menuItemEnemyViewSelect = new String[2];
			menuItemEnemyViewSelect[0] = BACK_MENU_2[language_index][3];
			if (isEnemyView()) {
				menuItemEnemyViewSelect[1] = DISPLAY[language_index][0];
			} else {
				menuItemEnemyViewSelect[1] = DISPLAY[language_index][1];
			}
			menuItems[menuIndex++] = menuItemEnemyViewSelect;

			if (srpgChoiceView == null) {
				// 配置菜单
				srpgChoiceView = new SRPGChoiceView(menuItems, choiceFont, 15,
						25);
			} else {
				srpgChoiceView.set(menuItems, choiceFont, 15, 25);
			}
			srpgChoiceView.setTab(15);
			srpgChoiceView.setContent(i);
			i = srpgChoiceView.choiceWait(this, true);
			switch (i) {
			// 地图棋盘
			case 0:
				setGrid(!isGrid());
				break;

			// 敌我标识
			case 1:
				setTeamColor(!isTeamColor());
				break;

			// 行动结束
			case 2:
				setEndView(!isEndView());
				break;

			// 敌方行动
			case 3:
				setEnemyView(!isEnemyView());
				break;

			case -1:
				flag = false;
				break;
			}
		}
	}

	/**
	 * 默认的游戏菜单项
	 * 
	 */
	protected synchronized void defaultCommand() {
		this.procFlag = PROC_COMMAND;
		SRPGActor actor = srpgActors.find(srpgPosition.number);
		setAttackRange(actor.getActorStatus().ability, actor.getPosX(), actor
				.getPosY());
		if (srpgChoiceView == null) {
			srpgChoiceView = new SRPGChoiceView(BATTLE[language_index],
					choiceFont, choiceX, choiceY);
		} else {
			srpgChoiceView.set(BATTLE[language_index], choiceFont, choiceX,
					choiceY);
		}
		srpgChoiceView.setTab(15);

		switch (srpgChoiceView.choiceWait(this, true)) {
		default:
			break;

		case 0:
			srpgPosition.ability = -1;
			setAbilityCommand();
			break;

		case 1:
			changeItem();
			break;
		case 2:
			changeDirection();
			break;

		case 3:
			srpgDrawView = makeActorStatusView(srpgActors.find(
					srpgPosition.number).getActorStatus());
			srpgChoiceView.setExist(true);
			defaultCommand();
			break;

		case 4:
			procFlag = PROC_NORMAL;
			setTurnMinus();
			break;

		case -1:
			int[] past = srpgPosition.past;
			SRPGActor actorObject = srpgActors.find(srpgPosition.number);
			actorObject.setPosX(past[0]);
			actorObject.setPosY(past[1]);
			actorObject.setDirection(srpgPosition.vector);
			int x = (actorObject.drawX() + halfTileWidth) - halfWidth;
			int y = (actorObject.drawY() + halfTileHeight) - halfHeight;
			setMoveViews(srpgPosition.number, actorObject);
			if (actorObject.drawX() - cam_x < tileWidth
					|| actorObject.drawY() - cam_y < tileHeight
					|| (actorObject.drawX() - cam_x) + tileWidth > getWidth()
							- tileWidth
					|| (actorObject.drawY() - cam_y) + tileHeight > getHeight()
							- tileHeight) {
				moveCamera(x, y, 10);
			}
			setMove(srpgPosition.number, actorObject);
			break;
		}
	}

	/**
	 * 变更角色所处的方向
	 * 
	 */
	protected synchronized void changeDirection() {
		this.procFlag = PROC_CHANGEVECTOR;
		if (srpgChoiceField == null) {
			srpgChoiceField = new SRPGFieldChoiceView(srpgField);
		} else {
			srpgChoiceField.set(srpgField);
		}
		int[] res = srpgChoiceField.choiceWait(this, true);
		resetWindow();
		if (res == null) {
			defaultCommand();
			return;
		}
		SRPGActor actor = srpgActors.find(srpgPosition.number);
		int x = actor.getPosX() - res[0];
		int y = actor.getPosY() - res[1];
		if (x < 0) {
			x *= -1;
		}
		if (y < 0) {
			y *= -1;
		}
		if (x + y != 1) {
			changeDirection();
			return;
		} else {
			actor.setDirection(actor.findDirection(res[0], res[1]));
			procFlag = PROC_NORMAL;
			setTurnMinus();
			return;
		}
	}

	// ---- 技能选择开始 ----//

	/**
	 * 设置默认的技能选择菜单
	 * 
	 */
	private synchronized void setAbilityCommand() {
		SRPGStatus status = srpgActors.find(srpgPosition.number)
				.getActorStatus();
		int[] abilitys = SRPGAbilityFactory.filtedAbility(status.ability,
				status, true);
		if (abilitys == null) {
			setHelper(NO_SUPPORT[language_index][0]);
			defaultCommand();
			return;
		} else {
			setChoiceAbility(abilitys);
			procFlag = PROC_ABILITYSELECT;
			setAbilitySelect();
			return;
		}
	}

	/**
	 * 选择要使用的战斗技能
	 * 
	 */
	private synchronized void setAbilitySelect() {
		int i = srpgChoiceView.choiceWait(this, true);
		switch (i) {
		case -1:
			defaultCommand();
			break;
		default:
			SRPGAbilityFactory ability = SRPGAbilityFactory.getInstance(i);
			SRPGActor actor = srpgActors.find(srpgPosition.number);
			int mp = ability.getMP(actor.getActorStatus());
			if (ability.getMP(actor.getActorStatus()) > actor.getActorStatus().mp) {
				setHelper("   MP < " + mp + " !   ");
				srpgChoiceView.setExist(true);
				setAbilitySelect();
				break;
			}
			setAttackRange(i, actor.getPosX(), actor.getPosY());
			srpgPosition.ability = i;
			if (ability.getSelectNeed() == 0) {
				boolean flag = false;
				int[][] area = srpgPosition.area;
				int index = 0;
				for (;;) {
					if (index >= area.length) {
						break;
					}
					for (int role = 0; role < area[index].length; role++) {
						if (area[index][role] == 0) {
							continue;
						}
						int res = srpgActors.checkActor(role, index);
						if (!getTargetTrue(ability, srpgPosition.number, res)) {
							continue;
						}
						flag = true;
						break;
					}

					if (flag) {
						break;
					}
					index++;
				}
				if (!flag) {
					setHelper(NO_SUPPORT[language_index][0]);
					srpgChoiceView.setExist(true);
					setAbilitySelect();
					break;
				}
			}
			procFlag = PROC_ABILITYTARGET;
			abilityTarget();
			break;
		}
	}

	private void abilityTargetSetting(int i, int[] res, int atk, int def) {
		srpgPosition.setTarget(res[0], res[1]);
		srpgDrawView = makeDamageExpectView(SRPGAbilityFactory.getInstance(i),
				srpgField, atk, def);
		setTargetRange(i, res[0], res[1]);
	}

	private synchronized void abilityTarget() {
		if (srpgDrawView == null) {
			srpgDrawView = new SRPGDrawView();
		} else {
			srpgDrawView.reset();
		}
		boolean flag = false;
		SRPGAbilityFactory ability = SRPGAbilityFactory
				.getInstance(srpgPosition.ability);
		int[] pos = null;
		if (ability.getMinLength() != 0 || ability.getMaxLength() != 0) {
			if (srpgChoiceField == null) {
				srpgChoiceField = new SRPGFieldChoiceView(srpgField);
			} else {
				srpgChoiceField.set(srpgField);
			}
			pos = srpgChoiceField.choiceWait(this, true);
		} else {
			pos = new int[2];
			pos[0] = srpgActors.find(srpgPosition.number).getPosX();
			pos[1] = srpgActors.find(srpgPosition.number).getPosY();
			flag = true;
		}
		if (srpgDrawView == null) {
			srpgDrawView = new SRPGDrawView();
		} else {
			srpgDrawView.reset();
		}
		if (pos != null) {
			SRPGAbilityFactory ability1 = SRPGAbilityFactory
					.getInstance(srpgPosition.ability);
			int i = srpgActors.checkActor(pos[0], pos[1]);
			if (srpgPosition.area[pos[1]][pos[0]] == 0) {
				setHelper(TOUCH_NO_SUPPORT[language_index][0]);
				abilityTarget();
				return;
			}
			boolean result = true;
			if (!getTargetTrue(ability1, srpgPosition.number, i)) {
				result = false;
			}
			if (ability1.getSelectNeed() == 0) {
				if (i == -1) {
					setHelper(NO_SUPPORT[language_index][0]);
					abilityTarget();
					return;
				}
				if (!result) {
					setHelper(NO_SUPPORT[language_index][0]);
					abilityTarget();
					return;
				}
			}
			if (!result) {
				i = -1;
			}
			abilityTargetSetting(srpgPosition.ability, pos,
					srpgPosition.number, i);
			createChoice(YES_NO[language_index], srpgChoiceView);
			procFlag = PROC_TARGETSURE;
			switch (srpgChoiceView.choiceWait(this, true)) {
			case 0:
				procFlag = PROC_ATTACK;
				resetWindow();
				if (setDamageValue(srpgPosition.ability, srpgPosition.number)) {
					procFlag = 0;
					setTurnMinus();
					return;
				}

			case -1:
			case 1:
				setAttackRange(srpgPosition.ability, srpgActors.find(
						srpgPosition.number).getPosX(), srpgActors.find(
						srpgPosition.number).getPosY());
				procFlag = PROC_ABILITYTARGET;
				srpgChoiceView.setExist(false);
				if (!flag) {
					abilityTarget();
				} else {
					resetWindow();
					srpgPosition.ability = -1;
					setAbilityCommand();
				}
				return;
			}
		} else {
			srpgPosition.ability = -1;
			setAbilityCommand();
		}
	}

	private synchronized void setChoiceAbility(int[] res) {
		String[][] mes = new String[res.length][3];
		int[] abilitys = new int[res.length];
		int i = 0;
		for (int j = 0; j < res.length; j++) {
			SRPGAbilityFactory ability = SRPGAbilityFactory.getInstance(res[j]);
			mes[i][0] = ability.getAbilityName();
			mes[i][1] = String.valueOf(ability.getMinLength()) + "-"
					+ String.valueOf(ability.getMaxLength());
			if (ability.getMP() > 0) {
				mes[i][2] = String.valueOf(ability.getMP(srpgActors.find(
						srpgPosition.number).getActorStatus()));
			} else {
				mes[i][2] = "";
			}
			abilitys[i] = res[j];
			i++;
		}
		if (srpgChoiceView == null) {
			srpgChoiceView = new SRPGChoiceView(mes, abilitys, choiceFont,
					choiceX, choiceY);
		} else {
			srpgChoiceView.set(mes, abilitys, choiceFont, choiceX, choiceY);
		}
		srpgChoiceView.setTab(25);
	}

	// ---- 技能选择结束 ----//

	private synchronized void setAttackRange(int i, int x, int y) {
		setAttackRange(new int[] { i }, x, y);
	}

	private synchronized void setAttackRange(int[] res, int x, int y) {
		srpgPosition.area = SRPGAbilityFactory.setAttackRange(res, srpgField,
				x, y);
	}

	private synchronized void setTargetRange(int i, int x, int y) {
		srpgPosition.area = SRPGAbilityFactory.setTargetRange(
				SRPGAbilityFactory.getInstance(i), srpgField, x, y);
	}

	private synchronized void setTurnMinus() {
		if (srpgActors == null) {
			return;
		}
		srpgActors.find(srpgPosition.number).getActorStatus().action--;
		processTurnEndActor(srpgPosition.number);
	}

	public void setCameraLock(boolean lock) {
		isCameraLock = lock;
	}

	public boolean getCameraLock() {
		return isCameraLock;
	}

	public synchronized void draw(GLEx g) {
		if (!isOnLoadComplete()) {
			initLoading(g);
			return;
		}
		// 绘制背景
		background(g);

		int cx = 0;
		int cy = 0;

		if (mouse_x < tileWidth && mouse_x >= 0) {
			cx -= 2;
		}
		if (mouse_x > getWidth() - tileWidth) {
			cx += 2;
		}
		if (mouse_y < tileHeight && mouse_y >= 0) {
			cy -= 2;
		}
		if (mouse_y > getHeight() - tileHeight) {
			cy += 2;
		}

		// 天空地白

		int sizeX1 = (halfWidth) * -1;
		int sizeX2 = srpgField.getDrawWidth() + sizeX1;
		int sizeY1 = (halfHeight) * -1;
		int sizeY2 = srpgField.getDrawHeight() + sizeY1;
		if (cx < 0 && cam_x + cx < sizeX1) {
			cx = sizeX1 - cam_x;
			if (cx > 0) {
				cx = 0;
			}
		}
		if (cx > 0 && cam_x + cx > sizeX2) {
			cx = sizeX2 - cam_x;
			if (cx < 0) {
				cx = 0;
			}
		}
		if (cy < 0 && cam_y + cy < sizeY1) {
			cy = sizeY1 - cam_y;
			if (cy > 0) {
				cy = 0;
			}
		}
		if (cy > 0 && cam_y + cy > sizeY2) {
			cy = sizeY2 - cam_y;
			if (cy < 0) {
				cy = 0;
			}
		}

		if (!isLock() && !isWindowExist() && !srpgEffect.isExist()
				&& !isCameraLock) {
			cam_x = (cam_x + cx);
			cam_y = (cam_y + cy);
		}
		// 变等角色动画
		this.srpgActors.next();

		// 变更摄像头位置
		this.centerCameraSetting();

		// 绘制战场地图
		srpgField.draw(g, cam_x, cam_y, getWidth(), getHeight());

		// 显示战场网格
		if (isGrid() && isBattleStart && !isAnimationEvent) {
			g.setColor(LColor.red);
			drawGrid(g);
			g.resetColor();
		}

		// 绘制角色移动范围
		if (procFlag == PROC_MOVEVIEW) {
			int[][] area = srpgPosition.area;
			LColor color = moving_our;
			if (srpgActors.find(srpgPosition.number).getActorStatus().team != 0) {
				color = moving_other;
			}
			for (int y = 0; y < area.length; y++) {
				for (int x = 0; x < area[y].length; x++) {
					if (area[y][x] == 0) {
						drawCubeView(g, color, x, y);
					}
				}
			}

		}

		// 变更角色使用的技能
		if (procFlag == PROC_ABILITYSELECT
				&& srpgPosition.ability != srpgChoiceView.getJointContent()) {
			setAttackRange(srpgChoiceView.getJointContent(), srpgActors.find(
					srpgPosition.number).getPosX(), srpgActors.find(
					srpgPosition.number).getPosY());
			srpgPosition.ability = srpgChoiceView.getJointContent();
		}

		if (procFlag == PROC_COUNTER && srpgPosition.counter
				&& srpgChoiceView.isExist()
				&& srpgPosition.ability != srpgChoiceView.getJointContent()) {
			setTargetRange(srpgChoiceView.getJointContent(), srpgActors.find(
					srpgPosition.enemy).getPosX(), srpgActors.find(
					srpgPosition.enemy).getPosY());
		}

		// 执行命令等情况
		if (procFlag == PROC_COMMAND || procFlag == PROC_ABILITYSELECT
				|| procFlag == PROC_ABILITYTARGET
				|| procFlag == PROC_TARGETSURE || procFlag == PROC_COUNTER) {
			int[][] area = srpgPosition.area;
			LColor color = LColor.red;
			for (int y = 0; y < area.length; y++) {
				for (int x = 0; x < area[y].length; x++) {

					if (procFlag != PROC_TARGETSURE && procFlag != PROC_COUNTER) {
						if (area[y][x] == 0) {
							continue;
						}
						color = colors[area[y][x] - 1];
					} else {
						if (area[y][x] == -1) {
							continue;
						}
						if (getTargetTrue(SRPGAbilityFactory
								.getInstance(srpgPosition.ability),
								srpgPosition.number, srpgActors
										.checkActor(x, y))) {
							color = attack_target;
						} else {
							color = attack_range;
						}
					}
					drawCubeView(g, color, x, y);
				}
			}

		}

		if (srpgPosition.counter && procFlag == PROC_COUNTER) {
			if (srpgChoiceView.isExist()
					&& srpgPosition.ability != srpgChoiceView.getJointContent()
					&& srpgDrawView.isExist()) {
				srpgDrawView = makeDamageExpectView(SRPGAbilityFactory
						.getInstance(srpgChoiceView.getJointContent()),
						srpgField, srpgPosition.number, srpgPosition.enemy);
				srpgPosition.ability = srpgChoiceView.getJointContent();
			}
			drawCubeView(g, attack_target, srpgActors.find(srpgPosition.enemy)
					.getPosX(), srpgActors.find(srpgPosition.enemy).getPosY());

		}
		if (procFlag == PROC_CHANGEVECTOR) {
			SRPGActor actor = srpgActors.find(srpgPosition.number);
			int x = actor.getPosX();
			int y = actor.getPosY();
			for (int d = 0; d < 4; d++) {
				int tx = x;
				int ty = y;
				if (d < 2) {
					tx += d * 2 - 1;
				} else {
					ty += (d % 2) * 2 - 1;
				}
				if (tx >= 0 && ty >= 0 && tx < srpgField.getWidth()
						&& ty < srpgField.getHeight()) {
					drawCubeView(g, moving_change, tx, ty);
				}
			}

		}

		// 角色绘制
		srpgActors.draw(g, cam_x, cam_y);

		for (int i = 0; i < srpgActors.size(); i++) {
			SRPGActor actor = srpgActors.find(i);
			if (!actor.isVisible()) {
				continue;
			}
			SRPGStatus status = actor.getActorStatus();
			if (status.team == srpgTeams.getTeamPhase() && status.action == 0
					&& isBattleStart && !isAnimationEvent) {
				if (isEndView()) {
					actor.setActionEnd(true);
				}
			}
			if (isCursor) {
				int index = srpgActors.getCursorIndex();
				if (index == i) {
					g.drawTexture(cursor, actor.drawX() - cam_x,
							(actor.drawY() - cam_y));
				}
			}

			if (!isTeamColor() || !isBattleStart || isAnimationEvent) {
				continue;
			}
			if (status.team == 0) {
				g.setColor(hero_flag);
			} else {
				g.setColor(enemy_flag);
			}
			g.drawRect((actor.drawX() - cam_x) + 2,
					(actor.drawY() - cam_y) + 2, tileWidth - 4, tileHeight - 4);
			g.resetColor();
		}

		// 绘制伤害
		for (int i = 0; i < srpgActors.size(); i++) {
			SRPGActor actor = srpgActors.find(i);
			if (!actor.isVisible()) {
				continue;
			}
			SRPGStatus status = actor.getActorStatus();
			String result = null;
			int index = 9;
			for (;;) {
				if (index >= SRPGStatus.STATUS_MAX) {
					break;
				}
				if (status.status[index] != 0) {
					result = SRPGStatus.STATUS_NAME[index];
					break;
				}
				index++;
			}
			if (result == null && status.hp <= 0
					&& status.checkSkill(SRPGStatus.SKILL_UNDEAD)) {
				result = "HP<=0";
			}
			if (result != null) {
				LFont old = g.getFont();
				g.setFont(simpleFont);
				int size = (tileWidth - result.length() * 6) / 2;
				g.drawStyleString(result, (actor.drawX() + size) - cam_x, actor
						.drawY()
						- 3 - cam_y, LColor.red, LColor.white);
				g.setFont(old);
			}
		}

		if ((procFlag == PROC_MOVEVIEW || procFlag == PROC_ABILITYTARGET || procFlag == PROC_CHANGEVECTOR)
				&& !srpgChoiceView.isExist()
				&& !srpgHelper.isExist()
				&& !srpgAvgView.isExist() && srpgTeams.getTeamPhase() == 0) {
			int actorIndex = srpgActors.checkActor((mouse_x + cam_x)
					/ tileWidth, (mouse_y + cam_y) / tileHeight);
			if (actorIndex != -1) {
				SRPGActor actor = srpgActors.find(actorIndex);
				srpgDrawView = makeActorMiniStatusView(actor.getActorStatus(),
						actor);
			} else {
				resetWindow();
			}
		}

		if (srpgAvgView.isExist()) {
			srpgAvgView.draw(g);
		} else {

			if (srpgEffect.isExist()) {
				srpgEffect.draw(g, cam_x, cam_y);
			}
			if (srpgChoiceView.isExist()) {
				srpgChoiceView.drawChoice(g);
			}
			if (srpgDrawView.isExist()) {
				srpgDrawView.draw(g);
			}
			if (srpgHelper.isExist()) {
				srpgHelper.draw(g);
			}
		}

		// 前景
		foreground(g);
		// 通知主线程取消等待
		notify();

	}

	private final void drawGrid(GLEx g) {
		g.glBegin(GL.GL_LINES);
		for (int x = 0; x < srpgField.getWidth() + 1; x++) {
			g.glVertex2f(x * tileWidth - cam_x, 0 - cam_y);
			g.glVertex2f(x * tileWidth - cam_x, srpgField.getHeight()
					* tileWidth - cam_y);
		}
		for (int y = 0; y < srpgField.getHeight() + 1; y++) {
			g.glVertex2f(0 - cam_x, y * tileHeight - cam_y);
			g.glVertex2f(srpgField.getWidth() * tileWidth - cam_x, y
					* tileHeight - cam_y);
		}
		g.glEnd();
	}

	/**
	 * 绘制小方块图
	 * 
	 * @param g
	 * @param color
	 * @param x
	 * @param y
	 */
	private final void drawCubeView(GLEx g, LColor color, int x, int y) {
		int x1 = (x * tileWidth - cam_x) + 1;
		int y1 = (y * tileHeight - cam_y) + 1;
		if (isGridAlpha) {
			g.setColor(color);
			g.fillRect(x1, y1, tileWidth - 2, tileHeight - 2);
		} else {
			g.setColor(color);
			g.fillRect(x1, y1, tileWidth - 2, tileHeight - 2);
		}
		g.resetColor();
	}

	/**
	 * 判断是否有打开的窗体存在
	 * 
	 * @return
	 */
	public boolean isWindowExist() {
		return srpgChoiceView.isExist() || srpgDrawView.isExist()
				|| srpgHelper.isExist() || srpgAvgView.isExist();
	}

	public final void setMessage(String s, boolean flag) {
		setMessage(new String[] { s }, -1, 0, 0, flag);
	}

	public final void setMessage(String s, int left, int top, boolean flag) {
		setMessage(new String[] { s }, -1, left, top, flag);
	}

	public final void setMessage(String[] message, boolean flag) {
		setMessage(message, -1, 0, 0, flag);
	}

	public final void setMessage(String[] message, int index, int offsetLeft,
			int offsetTop, boolean flag) {
		this.isSrpgNoMove = true;
		try {
			createTempImage();
			if (srpgHelper == null) {
				srpgHelper = new SRPGMessageView(message, language_index,
						DEFAULT_FONT,
						(getWidth() - messageImage.getWidth()) / 2, getHeight()
								- messageImage.getHeight() - 10, messageImage
								.getWidth(), messageImage.getHeight(),
						offsetLeft, offsetTop);
			} else {
				srpgHelper.dispose();
				srpgHelper.set(message, language_index, DEFAULT_FONT,
						(getWidth() - messageImage.getWidth()) / 2, getHeight()
								- messageImage.getHeight() - 10, messageImage
								.getWidth(), messageImage.getHeight(),
						offsetLeft, offsetTop, true);
			}
			if (srpgHelper.getBackground() == null) {
				srpgHelper.setBackground(messageImage);
			}
			if (index != -1) {
				SRPGActor actor = srpgActors.find(index);
				LTexture img = actor.getActorStatus().face;
				if (img != null) {
					srpgHelper.setFaceImage(img);
				}
			}
			if (flag) {
				srpgHelper.printWait(this);
			} else {
				srpgHelper.messageWait(this);
			}
		} catch (Exception e) {

		} finally {
			this.isSrpgNoMove = false;
		}
	}

	public final void setMessageCharacterLocation(int x, int y) {
		this.setHelperCharacterLocation(x, y);
	}

	public final void setMessageCharacter(LTexture img, int x, int y) {
		this.setHelperCharacter(img, x, y);
	}

	public final void setMessageCharacter(String fileName, int x, int y) {
		this.setHelperCharacter(fileName, x, y);
	}

	public final void setMessageCharacter(String fileName) {
		this.setHelperCharacter(fileName);
	}

	public final void setMessageListener(SRPGMessageListener l) {
		this.setHelperListener(l);
	}

	public final void setHelper(String[] message, int x, int y, int w, int h,
			int offsetLeft, int offsetTop, boolean flag) {
		if (srpgHelper == null) {
			srpgHelper = new SRPGMessageView(message, language_index,
					DEFAULT_FONT, x, y, w, h, offsetLeft, offsetTop);
		} else {
			srpgHelper.set(message, language_index, DEFAULT_FONT, x, y, w, h,
					offsetLeft, offsetTop, false);
		}
		srpgHelper.setBackground((LTexture) null);
		if (flag) {
			srpgHelper.printWait(this);
		} else {
			srpgHelper.messageWait(this);
		}
	}

	public final void setHelper(String[] message, int left, int top,
			boolean flag) {
		int w = getWidth() - 40;
		int h = 70;
		setHelper(message, (getWidth() - w) / 2, 20, w, h, left, top, flag);
	}

	public final void setHelper(String[] message, boolean flag) {
		setHelper(message, 0, 0, flag);
	}

	public final void setHelper(String[] message) {
		setHelper(message, false);
	}

	public final void setHelper(String s, boolean flag) {
		setHelper(new String[] { s }, flag);
	}

	public final void setHelper(String s) {
		setHelper(new String[] { s }, false);
	}

	public final void setHelperCharacterLocation(int x, int y) {
		if (srpgHelper != null) {
			srpgHelper.setCharacterLocation(x, y);
		}
	}

	public final void setHelperCharacter(LTexture img, int x, int y) {
		if (srpgHelper != null) {
			srpgHelper.setCharacterImage(img, x, y);
		}
	}

	public final void setHelperCharacter(String fileName, int x, int y) {
		if (srpgHelper != null) {
			srpgHelper.setCharacterImage(fileName, x, y);
		}
	}

	public final void setHelperCharacter(String fileName) {
		if (srpgHelper != null) {
			srpgHelper.setCharacterImage(fileName);
		}
	}

	public final void setHelperListener(SRPGMessageListener l) {
		if (srpgHelper != null) {
			srpgHelper.setListener(l);
		}
	}

	public final void setChoice(String[] mes, int x, int y) {
		if (srpgChoiceView == null) {
			srpgChoiceView = new SRPGChoiceView(mes, choiceFont, x, y);
		} else {
			srpgChoiceView.set(mes, choiceFont, x, y);
		}
	}

	protected final int choiceWait() {
		return srpgChoiceView.choiceWait(this, true);
	}

	/**
	 * 将选中的X轴由像素位置转为瓦片位置
	 * 
	 * @param x
	 * @return
	 */
	public int touchXPixelsToTiles(int x) {
		return cam_x + (x / tileWidth);
	}

	public int touchYPixelsToTiles(int y) {
		return cam_y + (y / tileHeight);
	}

	public int touchXTilesToPixels(int x) {
		return cam_x + (x * tileWidth);
	}

	public int touchYTilesToPixels(int y) {
		return cam_y + (y * tileHeight);
	}

	public int tilesToWidthPixels(int tiles) {
		return tiles * tileWidth;
	}

	public int tilesToHeightPixels(int tiles) {
		return tiles * tileHeight;
	}

	public int pixelsToTilesWidth(int x) {
		return x / tileWidth;
	}

	public int pixelsToTilesHeight(int y) {
		return y / tileHeight;
	}

	private boolean componentEnter(int x, int y) {
		if (srpgAvgView.isExist()) {
			srpgAvgView.onMouse(x, y);
			return true;
		} else {
			if (srpgHelper.isRunning()) {
				srpgHelper.next();
				return true;
			}
			if (srpgDrawView.isExist() && srpgDrawView.isLock()) {
				resetWindow();
				return true;
			}
			if (srpgChoiceView.isExist()) {
				srpgChoiceView.choiceMouseExecute(x, y);
				srpgChoiceView.scrollMouse(x, y);
				return true;
			}
		}
		return false;
	}

	/**
	 * 触摸屏按下
	 * 
	 * @param e
	 * @return
	 */
	public final void touchDown(final LTouch e) {
		if (!isEventLoop || isSrpgTouchLock) {
			return;
		}
		Runnable runnable = new Runnable() {
			public void run() {
				if (!isSrpgNoMove) {
					mouse_x = e.x();
					mouse_y = e.y();
				}
				onSubmit(mouse_x, mouse_y);
				onDown(e);
			}
		};
		callEvent(runnable);
	}

	public abstract void onDown(LTouch e);

	/**
	 * 触摸屏放开
	 * 
	 * @param e
	 * @return
	 */
	public final void touchUp(LTouch e) {
		if (!isEventLoop || isSrpgTouchLock) {
			return;
		}
		onUp(e);
	}

	public abstract void onUp(LTouch e);

	/**
	 * 在触摸屏上移动
	 * 
	 * @param e
	 */
	public final void touchMove(final LTouch e) {
		if (!isEventLoop || isSrpgTouchLock || isSrpgNoMove) {
			return;
		}
		if ((!srpgDrawView.isExist() || !srpgDrawView.isExist()
				|| !srpgHelper.messageExist() || !srpgHelper.isLock())) {
			if (srpgChoiceView.isExist()) {
				srpgChoiceView.setContent(srpgChoiceView.choiceMouse(mouse_x,
						mouse_y));
			}
			if (srpgChoiceField.isExist()) {
				srpgChoiceField.fieldSelect(mouse_x + cam_x, mouse_y + cam_y);
			}
		}
		mouse_x = e.x();
		mouse_y = e.y();
		onMove(e);
	}

	public abstract void onMove(LTouch e);

	public final void touchDrag(final LTouch e) {
		if (!isEventLoop || isSrpgTouchLock || isSrpgNoMove) {
			return;
		}
		if ((!srpgDrawView.isExist() || !srpgDrawView.isExist()
				|| !srpgHelper.messageExist() || !srpgHelper.isLock())) {
			if (srpgChoiceView.isExist()) {
				srpgChoiceView.setContent(srpgChoiceView.choiceMouse(mouse_x,
						mouse_y));
			}
			if (srpgChoiceField.isExist()) {
				srpgChoiceField.fieldSelect(mouse_x + cam_x, mouse_y + cam_y);
			}
		}
		mouse_x = e.x();
		mouse_y = e.y();
		onDrag(e);
	}

	public abstract void onDrag(LTouch e);

	public void alter(LTimerContext timer) {

	}

	/**
	 * 构建一个指定规格的事件触发器
	 * 
	 * @param x
	 * @param y
	 * @param type
	 * @return
	 */
	private SRPGEvent onEvent(int x, int y, int type) {
		if (temp_event_2 == null) {
			temp_event_2 = new SRPGEvent(x, y, type);
		} else {
			temp_event_2.set(x, y, type);
		}
		return temp_event_2;
	}

	/**
	 * 把指定坐标发生的事件传递给游戏
	 * 
	 * @param x
	 * @param y
	 */
	public final void onSubmit(int x, int y) {
		callSRPGBattleEvent(onEvent(x, y, SRPGEvent.EVENT_SUBMIT));
	}

	public final void onSubmit() {
		onSubmit(-1, -1);
	}

	/**
	 * 调用事件触发
	 * 
	 * @param eventqueue
	 */
	private synchronized final void callSRPGBattleProcEvent(
			final SRPGEvent event) {
		if (temp_event_1 == null) {
			temp_event_1 = new SRPGEvent();
		} else {
			temp_event_1.reset();
		}
		srpgEvent = temp_event_1;
		int x = (event.x + cam_x) / tileWidth;
		int y = (event.y + cam_y) / tileHeight;
		if (event.x + cam_x < 0) {
			x--;
		}
		if (event.y + cam_y < 0) {
			y--;
		}

		int type = event.type;
		switch (procFlag) {
		default:
			break;
		case PROC_NORMAL:
			// 确定
			if (type == SRPGEvent.EVENT_SUBMIT && Touch.isDown()) {
				// 角色存在
				int index = srpgActors.checkActor(x, y);
				if (index != -1) {
					SRPGActor actor = srpgActors.find(index);
					setMove(index, actor);
					onClickActor(actor, x, y);
				} else if (x >= 0 && x < srpgField.getWidth() && y >= 0
						&& y < srpgField.getHeight()) {
					onClickField(srpgField.getPosMapElement(x, y), x, y);
				}

				break;
			}
			// 取消
			if (type != SRPGEvent.EVENT_CANCEL) {
				break;
			}
			int actorIndex = srpgActors.checkActor(x, y);
			if (actorIndex != -1) {
				srpgDrawView = makeActorStatusView(srpgActors.find(actorIndex)
						.getActorStatus());
			} else {
				defaultBackMenu1();
			}
			break;
		}

		notify();
	}

	/**
	 * 事件触发处理(选择器)
	 * 
	 * @param e
	 */
	private synchronized final void callSRPGBattleEvent(SRPGEvent e) {
		if (e.type == SRPGEvent.EVENT_SUBMIT && Touch.isDown()) {
			if (componentEnter(e.x, e.y)) {
				return;
			}
			if (srpgChoiceField.isExist()) {
				srpgChoiceField.fieldSelectInput(e.x + cam_x, e.y + cam_y);
				return;
			}
		}
		if (srpgDrawView.isExist() && srpgDrawView.isLock()) {
			return;
		}
		if (srpgChoiceView.isExist() && e.type == SRPGEvent.EVENT_CANCEL) {
			srpgChoiceView.setCancel(true);
			return;
		}
		if (srpgChoiceField.isExist() && e.type == SRPGEvent.EVENT_CANCEL) {
			srpgChoiceField.setCancel(true);
			return;
		}
		if (srpgHelper.isLock()) {
			return;
		}
		if (srpgAvgView.isLock()) {
			return;
		}
		if (isLock()) {
			return;
		} else {
			srpgEvent = e;
			return;
		}
	}

	/**
	 * 取消指定坐标发生的事件传递
	 * 
	 * @param x
	 * @param y
	 */
	public final void onCancel(int x, int y) {
		callSRPGBattleEvent(onEvent(x, y, SRPGEvent.EVENT_CANCEL));
	}

	public final void onCancel() {
		onCancel(-1, -1);
	}

	/**
	 * 当开启AVG窗体时其选择数据将传递至此
	 * 
	 * @param mes
	 * @param i
	 */
	public void onAvgViewSelect(String mes, int i) {

	}

	/**
	 * 当开启AVG窗体时其脚本数据将传递至此
	 * 
	 * @param mes
	 */
	public void onAvgViewNext(String mes) {

	}

	public final void onLoad() {
		this.maxLevel = 99;
		this.maxExp = 100;
		this.srpgAvgView = new SRPGAvgView(this, createTempImage(), getWidth(),
				getHeight());
		this.makeCursor(tileWidth, tileHeight);
		if (srpgElements == null) {
			initFieldElementConfig(this.srpgElements = new SRPGFieldElements());
		}
		this.srpgField = new SRPGField(fileName, tileWidth, tileHeight,
				srpgElements);
		if (srpgField != null) {
			initMapConfig(srpgField);
		}
		this.srpgActors = new SRPGActors(32, tileWidth, tileHeight);
		if (srpgActors != null) {
			initActorConfig(srpgActors);
		}
		this.srpgPosition = new SRPGPosition();
		this.srpgEffect = new SRPGEffect();
		this.srpgChoiceField = new SRPGFieldChoiceView();
		this.srpgChoiceView = new SRPGChoiceView();
		this.srpgHelper = new SRPGMessageView();
		this.srpgDrawView = new SRPGDrawView();
		this.srpgEvent = new SRPGEvent();
		this.srpgTeams = new SRPGTeams(srpgActors);
		if (srpgTeams != null) {
			initTeamConfig(srpgTeams);
		}
		this.initActorsData();
		this.isGridAlpha = true;
		this.isBattleMode = true;
		this.isEventLoop = true;
		this.onLoading();
	}

	public abstract void onLoading();

	public void run() {
		try {
			for (; startProcess();) {
				try {
					super.wait();
				} catch (Exception ex) {
				}
			}
			if (isBattleMode) {
				mainProcess();
			}
			for (; endProcess();) {
				try {
					super.wait();
				} catch (Exception ex) {
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 返回AVG脚本控制器
	 * 
	 * @return
	 */
	public Command getAvgCommand() {
		if (srpgAvgView != null) {
			return srpgAvgView.getCommand();
		}
		return null;
	}

	/**
	 * 返回AVG对话框控制器
	 * 
	 * @return
	 */
	public LMessage getAvgMessage() {
		if (srpgAvgView != null) {
			return srpgAvgView.getMessage();
		}
		return null;
	}

	/**
	 * 返回AVG选择框控制器
	 * 
	 * @return
	 */
	public LSelect getAvgSelect() {
		if (srpgAvgView != null) {
			return srpgAvgView.getSelect();
		}
		return null;
	}

	/**
	 * 执行简化的AVG脚本
	 * 
	 * @param fileName
	 */
	public void callAvgScript(String fileName) {
		callAvgScript(fileName, null);
	}

	/**
	 * 执行简化的AVG脚本
	 * 
	 * @param fileName
	 * @param key
	 * @param value
	 */
	public void callAvgScript(String fileName, String key, String value) {
		ArrayMap map = new ArrayMap();
		map.put(key, value);
		callAvgScript(fileName, map);
	}

	/**
	 * 执行简化的AVG脚本
	 * 
	 * @param fileName
	 * @param flag
	 */
	public void callAvgScript(String fileName, ArrayMap map) {
		if (srpgAvgView != null) {
			isSrpgNoMove = true;
			srpgAvgView.setCommand(fileName, map);
			if (isBattleStart) {
				srpgAvgView.printWaitBattle();
			} else {
				srpgAvgView.printWait();
			}
			isSrpgNoMove = false;
		}
	}

	/**
	 * 当Screen加载完毕后，执行游戏主线程
	 */
	public final void onLoaded() {
		srpgThread = new Thread(this);
		srpgThread.setPriority(Thread.NORM_PRIORITY);
		srpgThread.start();
	}

	public void setTouchMoveLock(boolean move) {
		this.isSrpgNoMove = move;
	}

	public boolean getTouchMoveLock() {
		return isSrpgNoMove;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public void setGrid(boolean g) {
		isGrid = g;
	}

	public boolean isGrid() {
		return isGrid;
	}

	public void setTeamColor(boolean flag) {
		isTeamColor = flag;
	}

	public boolean isTeamColor() {
		return isTeamColor;
	}

	public void setEndView(boolean isEnd) {
		isEndView = isEnd;
	}

	public boolean isEndView() {
		return isEndView;
	}

	public void setMoveSpeed(int i) {
		moveSpeed = i;
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public void setEnemyView(boolean e) {
		isEnemyView = e;
	}

	public boolean isEnemyView() {
		return isEnemyView;
	}

	public void setSound(boolean s) {
		isSound = s;
	}

	public boolean isSound() {
		return isSound;
	}

	public boolean isGridAlpha() {
		return isGridAlpha;
	}

	public void setGridAlpha(boolean i) {
		this.isGridAlpha = i;
	}

	public void setHeroMoveColor(LColor c) {
		this.moving_our = c;
	}

	public void setEnemyMoveColor(LColor c) {
		this.moving_other = c;
	}

	public void setAttackTargetColor(LColor c) {
		this.attack_target = c;
	}

	public void setAttackRangeColor(LColor c) {
		this.attack_range = c;
	}

	public void setVectorChangeColor(LColor c) {
		this.moving_change = c;
	}

	public void setMaxLevel(int i) {
		this.maxLevel = i;
	}

	public void setMaxExp(int i) {
		this.maxExp = i;
	}

	public void setCursor(boolean flag) {
		this.isCursor = flag;
	}

	public boolean isCursor() {
		return isCursor;
	}

	public final void setSRPGHelper(SRPGMessageView view) {
		this.srpgHelper = view;
	}

	public final SRPGMessageView getHelper() {
		return srpgHelper;
	}

	public void setMessageImage(LTexture img) {
		if (img == messageImage) {
			return;
		}
		if (messageImage != null) {
			messageImage.destroy();
			messageImage = null;
		}
		this.messageImage = img;
	}

	public void setMessageImage(String fileName) {
		setMessageImage(new LTexture(fileName));
	}

	/**
	 * 选择默认的语言种类
	 * 
	 * @param i
	 */
	public final void setLanguage(int i) {
		if (i < 4) {
			this.language_index = i;
		}
	}

	public final int getLanguage() {
		return language_index;
	}

	public final SRPGActors getSRPGActors() {
		if (srpgActors == null) {
			throw new RuntimeException("SRPGActors is NULL !");
		}
		return srpgActors;
	}

	public boolean isTouchLock() {
		return isSrpgTouchLock;
	}

	public void setTouchLock(boolean i) {
		this.isSrpgTouchLock = i;
	}

	public boolean isEventLoop() {
		return isEventLoop;
	}

	public void setEventLoop(boolean isEventLoop) {
		this.isEventLoop = isEventLoop;
	}

	public boolean isBattleMode() {
		return isBattleMode;
	}

	/**
	 * 设定是否以战场模式开始SRPGScreen,如果为false则不执行mainProcess中主循环事务
	 * 
	 * @param isBattleMode
	 */
	public void setBattleMode(boolean isBattleMode) {
		this.isBattleMode = isBattleMode;
	}

	public final SRPGAvgView getAvgView() {
		if (srpgAvgView != null) {
			return srpgAvgView;
		}
		return null;
	}

	public final SRPGEffect getEffect() {
		return srpgEffect;
	}

	public final void setEffect(SRPGEffect eff) {
		srpgEffect = eff;
		srpgEffect.wait(this);
	}

	public static boolean isPhase() {
		return isPhase;
	}

	public static void setPhase(boolean isPhase) {
		SRPGScreen.isPhase = isPhase;
	}

	public int getCamX() {
		return cam_x;
	}

	public void setCamX(int i) {
		this.cam_x = i;
	}

	public int getCamY() {
		return cam_y;
	}

	public void setCamY(int i) {
		this.cam_y = i;
	}

	public LTexture getCursor() {
		return cursor;
	}

	public void setCursor(LTexture cursor) {
		this.cursor = cursor;
	}

	public static boolean isBattle() {
		return isBattle;
	}

	public static void setBattle(boolean isBattle) {
		SRPGScreen.isBattle = isBattle;
	}

	public int getHalfHeight() {
		return halfHeight;
	}

	public int getHalfTileHeight() {
		return halfTileHeight;
	}

	public int getHalfTileWidth() {
		return halfTileWidth;
	}

	public int getHalfWidth() {
		return halfWidth;
	}

	public int getProcFlag() {
		return procFlag;
	}

	public void setProcFlag(int procFlag) {
		this.procFlag = procFlag;
	}

	public SRPGActors getActors() {
		return srpgActors;
	}

	public void setActors(SRPGActors a) {
		this.srpgActors = a;
	}

	public SRPGAI getAI() {
		return srpgAI;
	}

	public SRPGFieldChoiceView getChoiceField() {
		return srpgChoiceField;
	}

	public void setChoiceField(SRPGFieldChoiceView s) {
		this.srpgChoiceField = s;
	}

	public SRPGChoiceView getChoiceView() {
		return srpgChoiceView;
	}

	public void setChoiceView(SRPGChoiceView c) {
		this.srpgChoiceView = c;
	}

	public SRPGDrawView getDrawView() {
		return srpgDrawView;
	}

	public void setDrawView(SRPGDrawView d) {
		this.srpgDrawView = d;
	}

	public SRPGFieldElements getElements() {
		return srpgElements;
	}

	public SRPGEvent getEvent() {
		return srpgEvent;
	}

	public SRPGField getField() {
		return srpgField;
	}

	public SRPGFieldMove getFieldMove() {
		return srpgMove;
	}

	public SRPGPosition getPosition() {
		return srpgPosition;
	}

	public SRPGTeams getTeams() {
		return srpgTeams;
	}

	public LFont getChoiceFont() {
		return choiceFont;
	}

	public void setChoiceFont(LFont choiceFont) {
		this.choiceFont = choiceFont;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getMaxExp() {
		return maxExp;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public Thread getSrpgThread() {
		return srpgThread;
	}

	public void close() {

	}

	/**
	 * 注销SRPGScreen
	 */
	public void dispose() {
		this.isEventLoop = false;
		try {
			if (srpgThread != null) {
				srpgThread.interrupt();
				srpgThread = null;
			}
		} catch (Exception e) {
		} finally {
			try {
				srpgPosition = null;
				srpgEffect = null;
				srpgChoiceField = null;
				srpgChoiceView = null;
				srpgDrawView = null;
				srpgEvent = null;
				srpgTeams = null;
				if (srpgHelper != null) {
					srpgHelper.dispose();
					srpgHelper = null;
				}
				if (srpgActors != null) {
					srpgActors.dispose();
					srpgActors = null;
				}
				if (srpgField != null) {
					srpgField.dispose();
					srpgField = null;
				}
				if (srpgElements != null) {
					srpgElements.dispose();
					srpgElements = null;
				}
				if (srpgAvgView != null) {
					srpgAvgView.dispose();
					srpgAvgView = null;
				}
			} catch (Exception e) {
			}
		}
		close();
		LGradation.close();
		LSystem.gc();
	}

}
