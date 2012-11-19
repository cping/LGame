package loon.srpg;

import loon.core.graphics.LFont;

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
 * @version 0.1
 */
public interface SRPGType {

	final static String[][] BACK_MENU_1 = { { "回合结束", "交战双方", "设定", "重新开始" },
			{ "回合結束", "交戰雙方", "設定", "重新開始" },
			{ "ターンの终わり", "紛争当事者", "設定", "ゲームを再起动します" },
			{ "Turn the End", "Warring parties", "Set", "Restart" } };

	final static String[][] BACK_MENU_2 = {
			{ "地图棋盘", "敌我标识", "行动完毕标识", "敌方行动显示" },
			{ "地圖棋盤", "敵我標識", "行動完畢標識", "敵方行動顯示" },
			{ "地図グリッド", "識別敵か味方", "表示の終了のアクション", "表示の敵のアクション" },
			{ "Map Grid", "Difference", "Action completed", "Enemy action" } };

	final static String[][] BATTLE = {
			{ "战斗方式", "道具", "转换方向", "属性", "行动结束" },
			{ "戰鬥方式", "道具", "轉換方向", "屬性", "行動結束" },
			{ "戦闘の方法", "どうぐ", "方向転換", "ステータスウィンドウ", "ターンの终わり" },
			{ "Skills", "Item", "Change direction", "Status", "Action the End" } };

	final static String[][] STRING_CARRY = {
			{ "目标内魔法对象不存在", "指定移动对象", "请指定传送目标", "确定传送", "取消传送" },
			{ "目標內魔法對象不存在", "指定移動對象", "請指定傳送目標", "確定傳送", "取消傳送" },
			{ "この魔法の対象となるキャラクターが存在しません", "キャラクターの移動先を指定してください",
					"転送オブジェクトを指定してください", "指定位置に移動する", "移動しない" },
			{ "Target object does not exist", "Moving objects specified",
					"Move the character selection", "Agreed to move",
					"Do not agree to move" } };

	final static String[][] WINNER_LOSER = { { "你的战斗胜利了 !", "你的战斗失败了 !" },
			{ "你的戰鬥勝利了 !", "你的戰鬥失敗了 !" }, { "敵を殲滅しました !", "敵に倒されてしまいました !" },
			{ "You're a Winner !", "You're a Loser !" } };

	final static String[][] YES_NO = { { "确定选择", "放弃选择" }, { "確定選擇", "放棄選擇" },
			{ "選択を確認するには", "選択した項目をあきらめる" }, { "I Agree", "Does not Agree" } };

	final static String[][] DISPLAY = { { "显示", "不显示" }, { "顯示", "不顯示" },
			{ "設定する", "設定しない" }, { "Display", "Not on Display" } };

	final static String[][] NO_SUPPORT = { { "当前选项无法使用" }, { "當前選項無法使用" },
			{ "現在のオプションは使用できません" }, { "The current option is not available" } };

	final static String[][] TOUCH_NO_SUPPORT = { { "您选择的目标不能操作" },
			{ "您選擇的目標不能操作" }, { "このアビリティの範囲内にターゲットが存在しません" },
			{ "The target of your choice can not be operated" } };

	final static String[][] STOP = { { "是否中止回合?", "是否重新开始?" },
			{ "是否中止回合?", "是否重新開始?" }, { "本当にフェイズを終了しますか?", "再起動するかどうか?" },
			{ "End of turn ?", "Restart ?" } };

	// 默认文字
	final static public LFont DEFAULT_FONT = LFont.getFont("Monospaced", 0, 20);

	final static public LFont DEFAULT_BIG_FONT = LFont.getFont("Monospaced", 1, 35);

	// ---- 默认的地形 ----//
	// 平地
	public final static int ELEMENT_FLAT = 0;

	// 山丘
	public final static int ELEMENT_HILL = 1;

	// 耕地
	public final static int ELEMENT_FARMLAND = 2;

	// 荒野
	public final static int ELEMENT_WILDERNESS = 3;

	// 沼泽
	public final static int ELEMENT_SWAMP = 4;

	// 河水
	public final static int ELEMENT_RIVER = 5;

	// 海
	public final static int ELEMENT_SEA = 6;

	// 雪
	public final static int ELEMENT_SNOW = 7;

	// 冰山
	public final static int ELEMENT_ICEBERGW = 8;

	// 沙地
	public final static int ELEMENT_SANDY = 9;

	// 沙丘
	public final static int ELEMENT_DUNE = 10;

	// 墙
	public final static int ELEMENT_WALL = 11;

	// 岩石
	public final static int ELEMENT_ROCK = 12;

	// 宫殿
	public final static int ELEMENT_PALACE = 13;

	// 城墙
	public final static int ELEMENT_CITYWALL = 14;

	// 泥潭
	public final static int ELEMENT_MIRE = 15;

	// 室内
	public final static int ELEMENT_INDOOR = 16;

	// 堤坝
	public final static int ELEMENT_DAM = 17;

	// 道路
	public final static int ELEMENT_ROAD = 18;

	// 湿地
	public final static int ELEMENT_WETLAND = 19;

	// 烈火
	public final static int ELEMENT_AGNI = 20;

	// 核污染
	public final static int ELEMENT_NUCLEAR = 21;

	// ---- 地形结束 ----//

	// ---- 默认的AI ----//
	// 普通的AI运算
	public static final int TYPE_NORMAL = 0;

	// 什么也不做，等待下一个处理
	public static final int TYPE_WAIT = 1;

	// 牧师类职业AI运算
	public static final int TYPE_PRIEST = 2;

	// 法师类职业AI运算
	public static final int TYPE_WIZARD = 3;

	// 不进行移动
	public static final int TYPE_NOMOVE = 4;

	// 采取逃避策略
	public static final int TYPE_ESCAPE = 5;

	// 牧师类职业AI运算(选择较低生命值的对象进行技能释放)
	public static final int TYPE_PRIEST_LOWER = 6;

	// 通常
	public static final int NORMAL[] = { TYPE_NORMAL };

	// 待机
	public static final int WAIT[] = { TYPE_WAIT };

	// 牧师模式
	public static final int PRIEST[] = { TYPE_PRIEST };

	// 巫师模式
	public static final int WIZARD[] = { TYPE_WIZARD };

	// 不动明王模式(原地不动,但依旧有攻击等行为)
	public static final int NOMOVE[] = { TYPE_NOMOVE };

	// 逃往模式(回避战斗,疯狂的往边缘移动)
	public static final int ESCAPE[] = { TYPE_ESCAPE };

	// 牧师模式(以救死为要务)
	public static final int PRIESTLOWER[] = { TYPE_PRIEST_LOWER };

	// 魔战士模式(一回合魔法为主,一回合攻击为主)
	public static final int WIZARD_NORMAL[] = { TYPE_WIZARD, TYPE_NORMAL };

	// 魔法专精模式(一回合治疗魔法,一回合攻击魔法)
	public static final int PRIEST_WIZARD[] = { TYPE_PRIEST_LOWER, TYPE_WIZARD };

	// 僧侣战士模式(一回合攻击,一回合治疗)
	public static final int NORMAL_PRIEST[] = { TYPE_PRIEST, TYPE_NORMAL };

	// 混合模式(一回合攻击,一回合治疗,一回合魔法)
	public static final int NORMAL_PRIEST_WIZARD[] = { TYPE_NORMAL,
			TYPE_WIZARD, TYPE_PRIEST };

	// 顽抗模式(不动,物理攻击,魔法攻击,补血,逃跑……)
	public static final int STUBBORNLYRESIST[] = { TYPE_NOMOVE, TYPE_NORMAL,
			TYPE_WIZARD, TYPE_PRIEST, TYPE_ESCAPE };

	// 女祭司模式(传统SRPG游戏中很常见的,一边补血一边逃跑那种……)
	public static final int[] PRIESTESS = { TYPE_PRIEST, TYPE_ESCAPE };

	// ---- AI结束 ----//

	// ---- 默认的地图显示方式 ----//
	public static final int FIELD_NORMAL = 0;

	public static final int FIELD_BIGMAP = 1;

	public static final int FIELD_BLEND = 2;

	// ---- 地图结束 ----//

	// ---- 角色重要度 ----//

	public static final int LEADER_NO = 0;

	public static final int LEADER_NORMAL = 1;

	public static final int LEADER_MAIN = 2;

	// ---- 移动方向 ----//

	public static final int MOVE_DOWN = 0;

	public static final int MOVE_LEFT = 1;

	public static final int MOVE_RIGHT = 2;

	public static final int MOVE_UP = 3;

	// ---- 战斗进程处理 ----//

	public static final int PROC_ENEMY = -1;

	public static final int PROC_NORMAL = 0;

	public static final int PROC_MOVEVIEW = 1;

	public static final int PROC_MOVING = 2;

	public static final int PROC_COMMAND = 3;

	public static final int PROC_CHANGEVECTOR = 4;

	public static final int PROC_ABILITYSELECT = 5;

	public static final int PROC_ABILITYTARGET = 6;

	public static final int PROC_TARGETSURE = 7;

	public static final int PROC_ATTACK = 8;

	public static final int PROC_COUNTER = 9;

	// ---- 允许攻击的目标类型 ----//

	public static final int TARGET_ENEMY = 0;

	public static final int TARGET_FRIEND = 1;

	public static final int TARGET_ALL = 2;

	// ---- 技能类型 ----//

	public static final int GENRE_ATTACK = 0;

	public static final int GENRE_RECOVERY = 1;

	public static final int GENRE_HELPER = 2;

	public static final int GENRE_CURE = 3;

	public static final int GENRE_MPDAMAGE = 4;

	public static final int GENRE_MPRECOVERY = 5;

	public static final int GENRE_ALLDAMAGE = 6;

	public static final int GENRE_ALLRECOVERY = 7;

	// ---- 是否允许反击 ----//

	public static final int SELECTNEED_YES = 0;

	public static final int SELECTNEED_NO = 1;

}
