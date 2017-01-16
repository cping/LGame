package org.test;

import loon.LTexture;
import loon.action.avg.AVGDialog;
import loon.action.sprite.AnimationHelper;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.srpg.SRPGScreen;
import loon.srpg.ability.SRPGDamageData;
import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActorFactory;
import loon.srpg.actor.SRPGActorStatus;
import loon.srpg.actor.SRPGActors;
import loon.srpg.actor.SRPGStatus;
import loon.srpg.field.SRPGField;
import loon.srpg.field.SRPGFieldElement;
import loon.srpg.field.SRPGFieldElements;
import loon.srpg.field.SRPGTeams;
import loon.utils.ArrayMap;
import loon.utils.MathUtils;
import loon.utils.timer.LTimerContext;

/**
 * 此示例暂时不能运行在HTML5之上(因为GWT不支持多线程，目前此示例有待改进，一是重写srpg模块，二是上teavm)
 */
public class SRPGTest extends SRPGScreen {

	public SRPGTest() {
		//
		super("assets/srpg/map.txt", AVGDialog.getRMXPDialog("assets/srpg/w11.png", 460,
				150), 48, 48);

		// 不使用回合交替特效进行提示
		setPhase(false);
	}

	/**
	 * 设置当前地图中索引与具体地图瓦片的对应关系
	 */
	protected void initFieldElementConfig(SRPGFieldElements elements) {
		// LGame默认提供了22种基础地形,不同的地形对应了不同的移动限制效果.
		// 当然,用户可以在需要时自行扩展.
		elements.putBattleElement(0, ELEMENT_PALACE, "皇宫");
		elements.putBattleElement(1, ELEMENT_WALL, "雕塑");
	}

	protected void initMapConfig(SRPGField field) {
		field.setBigImageMap("assets/srpg/map.png");
	}

	protected void winnerCheck() {

	}

	protected boolean startSrpgProcess() {

		return false;
	}

	protected boolean endSrpgProcess() {
		return false;
	}

	private SRPGActor p, hero, hero1;

	protected void initActorConfig(SRPGActors actors) {

		// 载入默认的角色及技能设置
		actors.makeDefActors();

		// 自定义角色状态
		SRPGActorStatus status = new SRPGActorStatus();
		status.status_jobname = "人形自走炮";
		status.status_max_hp = new float[] { 180F, 9F };
		status.status_max_mp = new float[] { 100F, 1.0F };
		status.status_power = new float[] { 140F, 8.8F };
		status.status_vitality = new float[] { 145F, 6.5F };
		status.status_agility = new float[] { 140F, 4F };
		status.status_magic = new float[] { 10F, 0F };
		status.status_resume = new float[] { 0F, 0F };
		status.status_mind = new float[] { 125F, 3F };
		status.status_sp = new float[] { 95F, 5.5F };
		status.status_dexterity = new float[] { 95F, 6.5F };
		status.status_regeneration = new float[] { 71F, 0.9F };
		status.status_guardelement = new int[] { 105, 95, 105, 105, 105, 95,
				105, 105 };
		status.status_move = new float[] { 3F, 0F };
		status.ability = new int[] { 0, 1, 2, 10, 6 };
		status.status_movetype = 0;

		SRPGActorStatus status1 = new SRPGActorStatus();
		status1.status_jobname = "火星来的灌水女王";
		status1.status_max_hp = new float[] { 180F, 9F };
		status1.status_max_mp = new float[] { 300F, 2.0F };
		status1.status_power = new float[] { 140F, 8.8F };
		status1.status_vitality = new float[] { 145F, 6.5F };
		status1.status_agility = new float[] { 140F, 4F };
		status1.status_magic = new float[] { 10F, 0F };
		status1.status_resume = new float[] { 0F, 0F };
		status1.status_mind = new float[] { 125F, 3F };
		status1.status_sp = new float[] { 95F, 5.5F };
		status1.status_dexterity = new float[] { 95F, 6.5F };
		status1.status_regeneration = new float[] { 71F, 0.9F };
		status1.status_guardelement = new int[] { 105, 95, 105, 105, 105, 95,
				105, 105 };
		status1.status_move = new float[] { 3F, 0F };
		status1.ability = new int[] { 0, 14, 16, 20, 6 };
		status1.status_movetype = 0;

		// 保存到静态引用的SRPGActorFactory类当中(所以自定义的SRPGActorStatus加载一次即可，
		// 不用每个Scrren都设置一次)
		SRPGActorFactory.putActorStatus(status);
		SRPGActorFactory.putActorStatus(status1);

		// 在索引0位置注入角色，角色动画采集方式为E社标准，职业索引为15(从SRPGActorFactory中取)，等级为1，分组为1，坐标为7,12
		p = actors.putActor(0, "鹏凌三千", AnimationHelper
				.makeEObject("assets/srpg/cp.png"), 15, 1, 1, 7, 12);
		// 方向向下
		p.setDirection(MOVE_UP);
		// 设定脸谱(没有也无所谓)
		p.getActorStatus().face =  LTexture.createTexture("assets/srpg/cpf.png");
		// 身份为敌方主角
		p.getActorStatus().leader = LEADER_MAIN;
		// AI模式为顽抗模式
		p.getActorStatus().computer = STUBBORNLYRESIST;

		hero = actors.putActor(1, "诚哥", AnimationHelper
				.makeRMVXObject("assets/srpg/rz.png"), 16, 90, 0, 8, 3);
		hero.setDirection(MOVE_LEFT);
		hero.getActorStatus().face = LTexture.createTexture("assets/srpg/rzf.png");

		hero1 = actors.putActor(2, "鱿鱼娘", "assets/srpg/wz.png", 17, 95, 0, 7, 3);
		hero1.setDirection(MOVE_RIGHT);
		hero1.getActorStatus().face = LTexture.createTexture("assets/srpg/wzf.png");
		hero1.getActorStatus().leader = LEADER_MAIN;
	}

	protected void initTeamConfig(SRPGTeams team) {
		// 设定分组名称(顺序配置分组ID，也可详细设置)
		team.setTeams(new String[] { "鱿鱼娘一方", "鹏凌三千一方" });
	}

	public void onClickActor(SRPGActor actor, int x, int y) {

	}

	public void onClickField(SRPGFieldElement element, int x, int y) {

	}

	protected void processAttackAfter(int index, SRPGActor actor) {

	}

	protected void processAttackBefore(int index, SRPGActor actor) {

	}

	protected void processChangePhaseAfter() {

		// 禁止滚屏执行
		setTouchMoveLock(true);
		// 让事件仅在主角方阶段发生
		if (getTeams().getPhase() == 0) {
			switch (getTeams().getTurn()) {
			
			// 当回合1时
			case 1:
			
				// 显示提示信息，自动播放
				setHelper(new String[] { "太平洋深处的水产世界", "坐井喷天火星城" }, true);
				
				// 设定一个移动状态标记，以调控剧情中的移动节奏
				boolean moveFlag = true;
				for (int j = 0; j < 4; j++) {
					for (int i = 0; i < (moveFlag ? 1 : 2); i++) {
						hero1.moveActorShow(MOVE_LEFT, j < 2 ? 90 : 30);
						hero.moveActorShow(MOVE_LEFT, j < 2 ? 90 : 30);
						hero.setDirection(MOVE_LEFT);
						hero1.setDirection(MOVE_RIGHT);
						hero.waitMove(this);
						hero1.waitMove(this);
					}
					moveFlag = !moveFlag;
					for (int i = 0; i < (moveFlag ? 1 : 2); i++) {
						hero.moveActorShow(MOVE_RIGHT, j < 2 ? 90 : 30);
						hero1.moveActorShow(MOVE_RIGHT, j < 2 ? 90 : 30);
						hero.setDirection(MOVE_LEFT);
						hero1.setDirection(MOVE_RIGHT);
						hero.waitMove(this);
						hero1.waitMove(this);
					}
					hero.waitMove(this);
					hero1.waitMove(this);

					switch (j) {
					case 0:

						// 调用AVG脚本中的分支判定
						callAvgScript("assets/srpg/script.txt", "index", "0");
						break;
					case 1:
						callAvgScript("assets/srpg/script.txt", "index", "1");
						break;
					default:
						break;
					}
				}
				callAvgScript("assets/srpg/script.txt", "index", "2");
				break;
			case 2:
				callAvgScript("assets/srpg/script.txt", "index", "3");
				break;
			default:
				break;
			}
		}
		// 恢复滚屏执行
		setTouchMoveLock(false);
	}

	protected void processChangePhaseBefore() {

	}

	/**
	 * 当角色已经被确定死亡时
	 */
	protected boolean processDeadActor(int index) {

		if (index != 0) {
			setHelper("鹏凌三千回家吃饭去了 !");
			return true;
		} else {
			SRPGActor actor = findActor(index);
			SRPGStatus oldStatus = actor.getActorStatus();
			if (oldStatus.level < 99) {
				ArrayMap vars = new ArrayMap(10);
				vars.put("index", "4");
				switch (MathUtils.random.nextInt(3)) {
				case 0:
					vars
							.put(
									"cpmes",
									"鹏凌三千:鹏翼一展凌九天，三千世界等云烟。长啸能将星斗碎，高翮会令银河干。凤凰于我看家狗，真龙是吾盘中餐。须弥倾覆身未老，昆仑山崩体犹闲。可笑蝼蚁啖金石，金石未损只自残！");
					break;
				case 1:
					vars.put("cpmes",
							"鹏凌三千:哈哈哈哈哈哈，信春哥不过是原地满血复活，让你见识一下本少爷原地满血升级复活的神技～");
					break;
				case 2:
					vars.put("cpmes", "鹏凌三千:道法无形，无器可容，君子不器，始臻全功，生死随心，神魔震惊！");
					break;
				}
				// 让角色消失
				actor.setVisible(false);
				actor.setExist(false);

				LTexture old = actor.getActorStatus().face;
				// 重新设定状态
				actor.setActorStatus(SRPGActorFactory.makeActorStatus("鹏凌三千",
						oldStatus.number, oldStatus.level + 1, oldStatus.team,
						oldStatus.group));
				actor.getActorStatus().face = old;
				// 执行脚本 
				callAvgScript("assets/srpg/script.txt", vars);
			}
			return false;
		}

	}

	/**
	 * 角色伤害数据输入前
	 */
	protected void processDamageInputBefore(SRPGDamageData damagedata, int atk,
			int def) {

	}

	/**
	 * 角色伤害数据输入后
	 */
	protected void processDamageInputAfter(SRPGDamageData damagedata, int atk,
			int def) {

	}

	/**
	 * 角色死亡后
	 */
	protected void processDeadActorAfter(int index, SRPGActor actor) {

	}

	/**
	 * 角色死亡后
	 */
	protected void processDeadActorBefore(int index, SRPGActor actor) {

	}

	public void onLoading() {
		makeEmulatorButton();
		
		add(MultiScreenTest.getBackButton(this,1,getWidth() - 100,
				 25));
	}

	public void processLevelUpAfter(int index, SRPGActor actor) {

	}

	public void processLevelUpBefore(int index, SRPGActor actor) {

	}

	public void onDown(GameTouch e) {

	}

	public void onMove(GameTouch e) {

	}

	public void onUp(GameTouch e) {

	}

	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}