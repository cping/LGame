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
package application;

import loon.Counter;
import loon.LTexture;
import loon.Stage;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.MoveTo;
import loon.action.map.AStarFinder;
import loon.action.map.Field2D;
import loon.action.map.MoveDraw;
import loon.action.map.TileAllocation;
import loon.action.map.TileMap;
import loon.action.map.battle.BattleProcess;
import loon.action.map.items.Role;
import loon.action.map.items.Team;
import loon.action.map.items.Teams;
import loon.action.sprite.AnimatedEntity;
import loon.action.sprite.Draw;
import loon.action.sprite.PixelShadow;
import loon.action.sprite.StatusBar;
import loon.action.sprite.effect.PixelChopEffect;
import loon.action.sprite.effect.StringEffect;
import loon.action.sprite.effect.PixelChopEffect.ChopDirection;
import loon.canvas.LColor;
import loon.component.LMenuSelect;
import loon.component.LRadar;
import loon.component.LRadar.Mode;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.events.ActionUpdate;
import loon.events.Touched;
import loon.geom.BooleanValue;
import loon.geom.IntValue;
import loon.geom.PointI;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;
import loon.utils.TArray;
import loon.utils.reply.Nullable;
import loon.utils.Array;

/**
 * 说一下为什么分了SLG和SRPG示例,因为SRPG那个示例是多线程的,这个是单线程的,
 * 这个逻辑能用在任何环境,SRPG那个不能跑GWT（SRPG示例需要以后走teavm运行）
 * 
 * 这个重构的SLG基础例子还是比较麻烦,有时间需要简化
 */
public class SLGTest extends Stage {

	/**
	 * 游戏菜单封装
	 *
	 */
	public class Menu {

		private LMenuSelect select;

		public Menu(String labels, float x, float y) {
			this.select = new LMenuSelect(labels, x, y);
			this.select.setBackground(getGameWinFrame(select.width(), select.height()));
		}

		public void load() {
			add(get());
			// 限制屏幕点击位置(也就是这个组件的点击事件不会传导到屏幕touch,
			// 虽然可以写成默认的,但是有些操作需要截取时又无法截取点击,所以还是用设置模式)
			addTouchLimit(get());
		}

		public void show() {
			this.select.setVisible(true);
		}

		public void hide() {
			this.select.setVisible(false);
		}

		public void update(String labels, float x, float y) {
			this.select.setLocation(x, y);
			this.select.setLabels(labels);
			this.select.setBackground(getGameWinFrame(select.width(), select.height()));
		}

		public void update(float x, float y) {
			float posX = x + (select.getWidth() / 2);
			float posY = y - (select.getHeight() / 2);
			float offset = 25;
			if (posX <= offset) {
				posX = offset;
			}
			if (posX >= getScreenWidth() - select.getWidth() - offset) {
				posX = getScreenWidth() - select.getWidth() - offset;
			}
			if (posY <= offset) {
				posY = offset;
			}
			if (posY >= getScreenHeight() - select.getHeight() - offset) {
				posY = getScreenHeight() - select.getHeight() - offset;
			}
			this.select.setLocation(posX, posY);
		}

		public void setListener(LMenuSelect.ClickEvent m) {
			this.select.setMenuListener(m);
		}

		public LMenuSelect get() {
			return this.select;
		}

	}

	/**
	 * 移动路径渲染用精灵
	 */
	public class Move extends MoveDraw {

		public Move(TileMap gameMap, int tileSize) {
			super(gameMap, tileSize);
		}

		protected void moveDraw(GLEx g, float offsetX, float offsetY) {
			for (int j = 0; j <= maxY - 1; j++) {
				for (int i = 0; i <= maxX - 1; i++) {
					if (moveList[i][j] > -1) {
						g.draw(iconImages[2], i * tileSize + offsetX, j * tileSize + offsetY);
					} else if (attackList[i][j] > 0) {
						g.draw(iconImages[3], i * tileSize + offsetX, j * tileSize + offsetY);
					}
				}
			}

			int count = 0;
			for (int j = 0; j <= maxY - 1; j++) {
				for (int i = 0; i <= maxX - 1; i++) {
					if (movingList[i][j] == -1) {
						continue;
					}
					count = 0;
					if ((movingList[i][j] == 0) || (movingList[i][j] == moveCount)) {
						if ((i > 0) && (movingList[i - 1][j] > -1)
								&& ((movingList[i - 1][j] - getMapCost(i - 1, j) == movingList[i][j])
										|| (movingList[i][j] - getMapCost(i, j) == movingList[i - 1][j]))) {
							count = 1;
						}
						if ((j > 0) && (movingList[i][j - 1] > -1)
								&& ((movingList[i][j - 1] - getMapCost(i, j - 1) == movingList[i][j])
										|| (movingList[i][j] - getMapCost(i, j) == movingList[i][j - 1]))) {
							count = 2;
						}
						if ((i < maxX - 1) && (movingList[i + 1][j] > -1)
								&& ((movingList[i + 1][j] - getMapCost(i + 1, j) == movingList[i][j])
										|| (movingList[i][j] - getMapCost(i, j) == movingList[i + 1][j]))) {
							count = 3;
						}
						if ((j < maxY - 1) && (movingList[i][j + 1] > -1)
								&& ((movingList[i][j + 1] - getMapCost(i, j + 1) == movingList[i][j])
										|| (movingList[i][j] - getMapCost(i, j) == movingList[i][j + 1]))) {
							count = 4;
						}
						if (movingList[i][j] != 0) {
							count = count + 4;
						}
					} else {
						count = 6;
						if ((i > 0) && (movingList[i - 1][j] > -1)
								&& ((movingList[i - 1][j] - getMapCost(i - 1, j) == movingList[i][j])
										|| (movingList[i][j] - getMapCost(i, j) == movingList[i - 1][j]))) {
							count = count + 1;
						}
						if ((j > 0) && (movingList[i][j - 1] > -1)
								&& ((movingList[i][j - 1] - getMapCost(i, j - 1) == movingList[i][j])
										|| (movingList[i][j] - getMapCost(i, j) == movingList[i][j - 1]))) {
							count = count + 2;
						}
						if ((i < maxX - 1) && (movingList[i + 1][j] > -1)
								&& ((movingList[i + 1][j] - getMapCost(i + 1, j) == movingList[i][j])
										|| (movingList[i][j] - getMapCost(i, j) == movingList[i + 1][j]))) {
							count = count + 3;
						}
						if ((j < maxY - 1) && (movingList[i][j + 1] > -1)
								&& ((movingList[i][j + 1] - getMapCost(i, j + 1) == movingList[i][j])
										|| (movingList[i][j] - getMapCost(i, j) == movingList[i][j + 1]))) {
							count = count + 5;
						}
					}

					if (count > 0) {
						g.draw(iconImages[count + 4], i * tileSize + offsetX, j * tileSize + offsetY);
					}

				}
			}

		}

		@Override
		public void draw(GLEx g, float offsetX, float offsetY) {
			PointI pos = null;
			if (action == 0) {
				moveDraw(g, offsetX, offsetY);
			} else if (action == 1 && actionIdx != -1) {
				GameRole role = getRoleIdxObject(actionIdx);
				pos = gameMap.pixelsToTileMap(role.getX(), role.getY());
				for (int j = 0; j <= maxY - 1; j++) {
					for (int i = 0; i <= maxX - 1; i++) {
						if (pos.x == i && pos.y == j) {
							g.draw(iconImages[3], i * tileSize + offsetX, (j + 1) * tileSize + offsetY);
							g.draw(iconImages[3], (i + 1) * tileSize + offsetX, j * tileSize + offsetY);
							g.draw(iconImages[2], i * tileSize + offsetX, j * tileSize + offsetY);
							g.draw(iconImages[0], i * tileSize + offsetX, j * tileSize + offsetY);
							g.draw(iconImages[3], (i - 1) * tileSize + offsetX, j * tileSize + offsetY);
							g.draw(iconImages[3], i * tileSize + offsetX, (j - 1) * tileSize + offsetY);
						}
					}
				}
			}
			pos = gameMap.pixelsToScrollTileMap(getTouchX(), getTouchY());
			GameRole role = getRole(pos.x, pos.y);
			if (action == 1 && role != null && role.getTeam() == Team.Enemy) {
				g.draw(iconImages[4], pos.x * tileSize + offsetX, pos.y * tileSize + offsetY);
			} else {
				g.draw(iconImages[0], pos.x * tileSize + offsetX, pos.y * tileSize + offsetY);
			}
			for (int j = 0; j <= maxY - 1; j++) {
				for (int i = 0; i <= maxX - 1; i++) {
					for (Team team : teams.list()) {
						for (Role ch : team.list()) {
							GameRole r = (GameRole) ch.getRoleObject();
							if (r.isStop()) { // 待机
								pos = gameMap.pixelsToTileMap(r.getX(), r.getY());
								if (pos.x == i && pos.y == j) {
									g.draw(unitImages[3], i * tileSize + offsetX, j * tileSize + offsetY);
								}
							}
						}
					}
				}
			}
		}

		@Override
		public void updateState(int idx) {
			getRoleIdxObject(idx).stop();
		}

		@Override
		public boolean allowSetMove(int x, int y) {
			GameRole role = getRoleIdxObject(actionIdx);
			// 当为我军时
			if (role.getTeam() == Team.Player) {
				if (getRoleIdx(Team.Enemy, x, y) > -1) {
					return false;
				}
			} else {
				if (getRoleIdx(Team.Player, x, y) > -1) {
					return false;
				}
			}
			return true;
		}

		@Override
		public int getMapCost(int x, int y) {
			return SLGTest.this.getMapCost(x, y);
		}

		@Override
		public int getMove(int idx) {
			return getRoleIdxObject(idx).getMove();
		}

		@Override
		public Vector2f getRolePos(int idx) {
			return getRoleIdxObject(idx).getPosition();
		}

	}

	/**
	 * 用户状态信息
	 */
	public class State extends Draw {

		private int select = -1;

		private int idx = -1;

		private int tileSize = 32;

		public State(int tileSize) {
			this.tileSize = tileSize;
			this.setZ(5000);
			this.setOffset(gameMap.getOffset());
			this.setRepaintAutoOffset(true);
		}

		public void select(int sel, int idx) {
			this.setSelect(sel);
			this.setIndex(idx);
		}

		public void setSelect(int sel) {
			this.select = sel;
		}

		public int getSelect() {
			return this.select;
		}

		public void setIndex(int idx) {
			this.idx = idx;
		}

		public int getIndeX() {
			return this.idx;
		}

		public float getLeft(GameRole role, float v) {
			float x = (role.getX() - role.getWidth() / 2) + v - role.getWidth();
			return x;
		}

		public float getTop(GameRole role, float v) {
			float y = (role.getY() - role.getHeight() / 2) + v - role.getHeight() * 3;
			return y;
		}

		public void resetSelect() {
			this.select(-1, -1);
		}

		/**
		 * 显示角色状态
		 * 
		 * @param g
		 * @param idx
		 */
		public void showRoleState(GLEx g, int idx, float x, float y) {
			if (idx > -1) {
				GameRole role = (GameRole) teams.getRole(idx).getRoleObject();
				float alpha = g.alpha();
				g.setAlpha(0.75f);
				float leftA = tileSize;
				float leftB = 1 * tileSize;
				float leftC = 2 * tileSize;
				float leftD = 3 * tileSize;
				float topA = tileSize;
				float topB = 2 * tileSize;
				float topC = 3 * tileSize;
				g.draw(listImages[0], x + getLeft(role, leftA), getTop(role, y));
				g.draw(listImages[1], x + (getLeft(role, leftB)), getTop(role, y));
				g.draw(listImages[1], x + (getLeft(role, leftC)), getTop(role, y));
				g.draw(listImages[2], x + (getLeft(role, leftD)), getTop(role, y));

				g.draw(listImages[3], x + (getLeft(role, leftA)), getTop(role, topA + y));
				g.draw(listImages[4], x + (getLeft(role, leftB)), getTop(role, topA + y));
				g.draw(listImages[4], x + (getLeft(role, leftC)), getTop(role, topA + y));
				g.draw(listImages[5], x + (getLeft(role, leftD)), getTop(role, topA + y));

				g.draw(listImages[3], x + getLeft(role, leftA), getTop(role, topB + y));
				g.draw(listImages[4], x + (getLeft(role, leftB)), getTop(role, topB + y));
				g.draw(listImages[4], x + (getLeft(role, leftC)), getTop(role, topB + y));
				g.draw(listImages[5], x + (getLeft(role, leftD)), getTop(role, topB + y));

				g.draw(listImages[6], x + (getLeft(role, leftA)), getTop(role, topC + y));
				g.draw(listImages[7], x + (getLeft(role, leftB)), getTop(role, topC + y));
				g.draw(listImages[7], x + (getLeft(role, leftC)), getTop(role, topC + y));
				g.draw(listImages[8], x + (getLeft(role, leftD)), getTop(role, topC + y));
				g.setAlpha(1.0f);
				// 显示角色数据
				g.draw(role.getBitmap(), x + (getLeft(role, leftB + tileSize / 2)), getTop(role, tileSize + y));
				g.setColor(LColor.white);
				g.drawText("HP:" + role.getHp(), x + (getLeft(role, leftB + 12)), getTop(role, topC + y - 21));
				g.drawText("MV:" + role.getMove(), x + (getLeft(role, leftB + 12)), getTop(role, topC + y - 1));
				g.setAlpha(alpha);
			}
		}

		@Override
		public void draw(GLEx g, float offsetX, float offsetY) {
			if (select != -1) {
				switch (select) {
				case 0:
					showRoleState(g, idx, offsetX, offsetY);
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * 角色封装
	 *
	 */
	public class GameRole extends AnimatedEntity {

		StatusBar hpstatus;

		Role character;

		/**
		 * 设定角色参数
		 * 
		 * @param name
		 * @param team
		 * @param image
		 * @param move
		 * @param x
		 * @param y
		 */
		public GameRole(String name, int id, int team, String path, int move, int tileX, int tileY) {
			super(path, 32, 32, gameMap.tilesToPixelsX(tileX), gameMap.tilesToPixelsY(tileY), gameTile, gameTile);
			// 构建一个Role信息载体(只有系统Role类可以和系统Teams配套使用)
			character = new Role(id, name);
			character.setTeam(team);
			character.setHealth(10);
			if (team == Team.Enemy) {
				character.setMoved(true);
			}
			character.setMovePoints(move);
			character.setRoleObject(this);

			this.setName(name);
			// 绑定动画索引和图像纹理索引
			int delay = 260;
			this.setPlayIndex("down", PlayIndex.at(delay, 0, 2));
			this.setPlayIndex("left", PlayIndex.at(delay, 3, 5));
			this.setPlayIndex("right", PlayIndex.at(delay, 6, 8));
			this.setPlayIndex("up", PlayIndex.at(delay, 9, 11));
			this.animate("down");
			// 让角色坐标随地图偏移
			this.setOffset(gameMap.getOffset());
			this.setZ(10);
			// 血条
			this.hpstatus = new StatusBar(width() - 4, height() / 6);
			hpstatus.set(character.getHealth());
			hpstatus.setOffsetX(2);
			// 不跟随父精灵旋转
			hpstatus.setFollowRotation(false);
			if (team == Team.Player) {
				hpstatus.setColorbefore(LColor.blue);
			} else {
				hpstatus.setColorbefore(LColor.red);
			}
			this.addChild(hpstatus);
		}

		public StatusBar getHPStatus() {
			return hpstatus;
		}

		public boolean isStop() {
			return character.isMoved();
		}

		public boolean isAllUnDoneAction() {
			return character.isAllUnDoneAction();
		}

		public void attackStop() {
			character.setAttack(true);
		}

		public int getHp() {
			return character.getHealth();
		}

		public void setHp(int hp) {
			character.setHealth(hp);
		}

		public int getMove() {
			return character.getMovePoints();
		}

		public void setMove(int move) {
			this.character.setMovePoints(move);
		}

		public int getTeam() {
			return character.getTeam();
		}

		public void start() {
			character.undoneAction();
			setColor(LColor.white);
		}

		public void stop() {
			character.doneAction();
			setColor(LColor.gray);
		}

	}

	/**
	 * 创建角色并注入Team
	 * 
	 * @param name
	 * @param team
	 * @param imageIndex
	 * @param move
	 * @param x
	 * @param y
	 */
	private GameRole createTeamRole(String name, Counter counter, int team, String path, int imageIndex, int move,
			int x, int y) {
		GameRole role = new GameRole(name, counter.incId(), team, path, move, x, y);
		// 为此精灵创建阴影
		role.createShadow(true);
		// 让角色按照XY位置排序,以修正遮挡关系(如果不设为true,则不参与xy排序)
		role.setAutoXYSort(true);
		// 添加游戏角色到分组中
		teams.add(team, role.character);
		add(role);
		return role;
	}

	public GameRole getRoleIdxObject(int idx) {
		return (GameRole) teams.getRole(idx).getRoleObject();
	}

	public GameRole getRole(final int tileX, final int tileY) {
		for (Role role : teams.all()) {
			PointI pos = gameMap.pixelsToTileMap(role.getX(), role.getY());
			if (tileX == pos.x && tileY == pos.y) {
				return (GameRole) role.getRoleObject();
			}
		}
		return null;
	}

	public int getRoleIdx(final int x, final int y) {
		return getRoleIdx(-1, x, y);
	}

	public int getRoleIdx(final int team, final int x, final int y) {
		TArray<Role> list = (team == -1 ? teams.all() : teams.get(team).list());
		for (Role role : list) {
			PointI pos = gameMap.pixelsToTileMap(role.getX(), role.getY());
			if (x == pos.x && y == pos.y) {
				return role.getID();
			}
		}
		return -1;
	}

	public int getMapCost(int x, int y) {
		int type = gameMap.getField2D().getTileType(x, y);
		switch (type) {
		case 0:
			type = 1; // 草
			break;
		case 1:
			type = 2; // 树
			break;
		case 2:
			type = 3; // 山地
			break;
		case 3:
			type = -1; // 湖泽(不能进入)
			break;
		}
		if (type != -1) { // 有人
			if (getRole(x, y) != null) {
				return -1;
			}
		}
		return type;
	}

	public void removeRole(final int team, final GameRole e) {
		// 从单元集合中删除角色
		teams.remove(e.character);
		// 设定死亡
		e.character.setDead(true);
		// 放倒角色淡出并删除敌人
		get(e).rotateTo(90).fadeOut(6f).start().setActionListener(new ActionListener() {

			@Override
			public void stop(ActionBind o) {
				remove(e);

				getBattle().set(false);
			}

			@Override
			public void start(ActionBind o) {
			}

			@Override
			public void process(ActionBind o) {
			}
		});
	}

	public void attackEnemy(int team, final Move moveState, final GameRole attacker, final GameRole enemy) {
		// 如果目标是敌人
		if (enemy.getTeam() != attacker.getTeam()) {

			int centerSize = (gameTile) / 2;

			// 获得攻击位置中值(加入地图偏移量)
			float attackX = enemy.x() + gameMap.getOffsetX() + centerSize;
			float attackY = enemy.y() + gameMap.getOffsetY() + centerSize;
			final int khp = random(1, 3);

			PixelChopEffect chop = PixelChopEffect.get(ChopDirection.WNTES, LColor.red, attackX, attackY, 2,
					centerSize);
			chop.setAutoRemoved(true);
			chop.setZ(1500);
			add(chop);

			// 文字上浮
			StringEffect str = StringEffect.up(String.valueOf(khp), Vector2f.at(attackX, attackY), LColor.red);
			str.setOffsetX(-str.getFontWidth() / 2);
			str.setAutoRemoved(true);
			str.setZ(1500);

			add(str);

			// 敌人颤抖
			get(enemy).shakeTo(0.5f).start().setActionListener(new ActionListener() {

				@Override
				public void stop(ActionBind o) {
					// 改变hp
					int curhp = enemy.getHp();
					int newhp = curhp - khp;
					enemy.setHp(newhp);
					enemy.getHPStatus().setUpdate(newhp);
					if (enemy.getHp() <= 0) {
						// 死亡则删除角色
						removeRole(1, enemy);
					}
					attacker.stop();
					getBattle().set(false);
				}

				@Override
				public void start(ActionBind o) {

				}

				@Override
				public void process(ActionBind o) {

				}
			});

		} else {
			attacker.stop();
			getBattle().set(false);
		}
	}

	/**
	 * 变更当前地图角色位置当二维数组中(实际开发中应该使用多个Field2D做分层,示例只为省事……)
	 * 
	 * @return
	 */
	public Field2D toCurrentMap(boolean player) {
		// 复制二维数组地图
		Field2D field = gameMap.getField2D().cpy();
		if (player) {
			// 添加敌人数据到地图
			updateEnemyPos(field);
		} else {
			// 添加玩家数据到地图
			updatePlayerPos(field);
		}
		return field;
	}

	/**
	 * 检查是否可攻击的玩家
	 * 
	 * @param tileX
	 * @param tileY
	 * @return
	 */
	public boolean checkAttackPlayer(GameRole attacker, int tileX, int tileY) {
		return checkAttack(attacker, tileX, tileY, Team.Player);
	}

	public boolean checkAttackEnemy(GameRole attacker, int tileX, int tileY) {
		return checkAttack(attacker, tileX, tileY, Team.Enemy);
	}

	public boolean checkAttack(GameRole attacker, int tileX, int tileY, int team) {
		if (attacker == null) {
			return false;
		}
		PointI attackPos = gameMap.pixelsToTileMap(attacker.getX(), attacker.getY());
		for (Role r : teams.get(team).list()) {
			PointI pos = gameMap.pixelsToTileMap(r.getX(), r.getY());
			boolean checkAttacker = false;
			if (attackPos.x - 1 == tileX && attackPos.y == tileY) {
				checkAttacker = true;
			}
			if (attackPos.x + 1 == tileX && attackPos.y == tileY) {
				checkAttacker = true;
			}
			if (attackPos.x == tileX && attackPos.y - 1 == tileY) {
				checkAttacker = true;
			}
			if (attackPos.x == tileX && attackPos.y + 1 == tileY) {
				checkAttacker = true;
			}
			if (checkAttacker && tileX == pos.x && tileY == pos.y) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查指定坐标旁是否有角色存在
	 * 
	 * @param tileX
	 * @param tileY
	 * @param length
	 * @return
	 */
	public GameRole checkRoleExist(int tileX, int tileY, int length, int team) {
		for (Role r : teams.get(team).list()) {
			PointI pos = gameMap.pixelsToTileMap(r.getX(), r.getY());
			for (int i = 1; i <= length; i++) {
				boolean exist = false;
				if (tileX == pos.x + i && tileY == pos.y) {
					exist = true;
				}
				if (tileX == pos.x - i && tileY == pos.y) {
					exist = true;
				}
				if (tileX == pos.x && tileY == pos.y + i) {
					exist = true;
				}
				if (tileX == pos.x && tileY == pos.y - i) {
					exist = true;
				}
				if (exist) {
					return (GameRole) r.getRoleObject();
				}
			}
		}
		return null;
	}

	public boolean checkMoveArea(Field2D field, GameRole role) {
		PointI pos = gameMap.pixelsToTileMap(role.getX(), role.getY());
		return checkMoveArea(field, pos.x, pos.y);
	}

	public boolean checkMoveArea(Field2D field, int tileX, int tileY) {
		for (Role r : teams.all()) {
			PointI pos = gameMap.pixelsToTileMap(r.getX(), r.getY());
			if (tileX == pos.x && tileY == pos.y) {
				return false;
			}
		}
		// 动态位置占用检查
		if (lockedLocation.contains(pointi(tileX, tileY))) {
			return false;
		}

		return field.isHit(tileX, tileY);
	}

	/**
	 * 变更敌人所在位置的地图上标识
	 */
	public void updateEnemyPos(Field2D field) {
		for (Role e : teams.getEnemy().list()) {
			PointI pos = gameMap.pixelsToTileMap(e.getX(), e.getY());
			field.setTileType(pos.x, pos.y, 'E');
		}
	}

	public void clearEnemyPos(Field2D field) {
		field.replaceType('E', 0);
	}

	public void updatePlayerPos(Field2D field) {
		for (Role e : teams.getPlayer().list()) {
			PointI pos = gameMap.pixelsToTileMap(e.getX(), e.getY());
			field.setTileType(pos.x, pos.y, 'P');
		}
	}

	public void clearPlayerPos(Field2D field) {
		field.replaceType('P', 0);
	}

	public void updatePos(Field2D field) {
		this.updateEnemyPos(field);
		this.updatePlayerPos(field);
	}

	public void clearPos(Field2D field) {
		clearEnemyPos(field);
		clearPlayerPos(field);
	}

	/**
	 * 返回一个随机移动位置
	 * 
	 * @param team
	 * @param move
	 * @param role
	 * @return
	 */
	protected PointI randMove(Field2D map, Move move, GameRole role, Counter count) {

		int x = random(-role.getMove(), role.getMove());
		int y = random(-role.getMove(), role.getMove());

		PointI pos = gameMap.pixelsToTileMap(role.getX(), role.getY());

		int newX = move.fixX(pos.x + x);
		int newY = move.fixY(pos.y + y);

		if (checkMoveArea(map, newX, newY)) {
			// 获得实际寻径结果以避免障碍物误判移动范围
			TArray<Vector2f> path = AStarFinder.find(map, pos.x, pos.y, newX, newY, false);
			if (path.size - 1 < role.getMove()) {
				return pointi(newX, newY);
			}
		}
		if (count.getValue() > 30) {
			return pointi(pos.x, pos.y);
		}
		count.increment();
		return randMove(map, move, role, count);
	}

	/**
	 * 返回一个随机移动位置
	 * 
	 * @param move
	 * @param role
	 * @return
	 */
	protected PointI randMove(Field2D map, Move move, GameRole role) {
		return randMove(map, move, role, newCounter());
	}


	private TArray<PointI> lockedLocation = new TArray<PointI>();

	private int lastTileX, lastTileY;

	// 角色分组
	private Teams teams = new Teams();

	// 默认瓦片大小
	final static int gameTile = 32;
	// 战斗个体图
	private LTexture[] unitImages = TextureUtils
			.getSplitTextures(TextureUtils.filterColor("assets/slg/unit.png", new LColor(255, 0, 255)), gameTile, gameTile);

	private LTexture[] iconImages = TextureUtils
			.getSplitTextures(TextureUtils.filterColor("assets/slg/icon.png", new LColor(255, 0, 255)), gameTile, gameTile);

	private LTexture[] listImages = TextureUtils
			.getSplitTextures(TextureUtils.filterColor("assets/slg/list.png", new LColor(255, 0, 255)), gameTile, gameTile);

	private TileMap gameMap = null;

	@Override
	public void create() {

		// 清空初始数据(重载Screen时有用)
		this.lastTileX = lastTileY = 0;

		// 为分组创建两个子分组,己方与敌人
		this.teams.createPE();
		this.lockedLocation.clear();

		// 构建一个2D的二维数组游戏地图
		this.gameMap = new TileMap("assets/slg/map2.txt", 32, 32);
		// 设置切图方式
		/*TArray<LTexturePackClip> clips = new TArray<LTexturePackClip>(10);
		// 索引,名称,开始切图的x,y位置,以及切下来多少
		clips.add(new LTexturePackClip(0, "1", 0, 0, 32, 32));
		clips.add(new LTexturePackClip(1, "2", 0, 32, 32, 32));
		clips.add(new LTexturePackClip(2, "3", 32, 0, 32, 32));
		clips.add(new LTexturePackClip(3, "4", 32, 32, 32, 32));

		// 注入切图用地图，以及切图方式(也可以直接注入xml配置文件)
		gameMap.setImagePack("assets/slg/map.png", clips);*/
		
		TileAllocation<Nullable<String>> v =TileAllocation.none("assets/slg/map.png");
				 v.clip("1", 0, 0, 32, 32)
			     .clip("2", 0, 32, 32, 32)
			     .clip("3", 32, 0, 32, 32)
			     .clip("4", 32, 32, 32, 32);
		
		gameMap.setImagePack(TileAllocation.none("assets/slg/map.png")
		 .clip("1", 0, 0, 32, 32)
	     .clip("2", 0, 32, 32, 32)
	     .clip("3", 32, 0, 32, 32)
	     .clip("4", 32, 32, 32, 32));
		// 限制进入区域
		gameMap.setLimit(new int[] { 1, 2, 3, 'P', 'E' });
		// 允许进入区域
		gameMap.setAllowMove(new int[] { 0 });
		// 执行地图与图片绑定
		gameMap.pack();

		// 注入地图到游戏窗体
		add(gameMap);

		// id标识生成用
		final Counter idCounter = new Counter();
		// 创建角色:name=空罐少女,id(累加),team=0(我军),imageindex=1,移动能力5格,x=12,y=20,以下雷同
		createTeamRole("空罐少女", idCounter, Team.Player, "assets/slg/player.png", 1, 5, 12, 20);
		createTeamRole("猫猫1", idCounter, Team.Player, "assets/slg/player2.png", 0, 4, 11, 20);
		createTeamRole("猫猫2", idCounter, Team.Player, "assets/slg/player2.png", 0, 4, 13, 20);
		// 创建角色:name=躲猫兵团1,id(累加),team=1(敌军),imageindex=2,移动能力3格,x=11,y=4,以下雷同
		final String path = "assets/slg/enemy1.png";
		createTeamRole("躲猫兵团1", idCounter, Team.Enemy, path, 2, 3, 11, 4);
		createTeamRole("躲猫兵团2", idCounter, Team.Enemy, path, 2, 2, 11, 5);
		createTeamRole("躲猫兵团3", idCounter, Team.Enemy, path, 2, 3, 11, 6);
		createTeamRole("躲猫兵团5", idCounter, Team.Enemy, path, 2, 2, 13, 4);
		createTeamRole("躲猫兵团5", idCounter, Team.Enemy, path, 2, 2, 13, 5);
		createTeamRole("躲猫兵团6", idCounter, Team.Enemy, path, 2, 3, 13, 6);
		createTeamRole("躲猫兵团8", idCounter, Team.Enemy, path, 2, 2, 11, 18);
		createTeamRole("躲猫兵团9", idCounter, Team.Enemy, path, 2, 3, 12, 18);
		createTeamRole("躲猫兵团7", idCounter, Team.Enemy, path, 2, 2, 13, 18);

		// 为精灵集合注入像素风阴影构建器
		getSprites().setSpritesShadow(new PixelShadow(32, 32, LColor.black));
		// 为精灵集合开启xy位置层级修正,按照xy位置变化渲染顺序(默认不开启,自动运算会耗费资源)
		getSprites().setAutoSortXYLayer(true);
		// 当前操作的角色索引
		final IntValue roleIndex = refInt(-1);

		// 显示网格
		// add(new GridEntity());

		// 点击数计数器
		final Counter clickCount = new Counter();

		final Move moveState = new Move(gameMap, gameTile);
		add(moveState);

		final State menuState = new State(gameTile);
		add(menuState);

		final Menu menu = new Menu("攻击,待机", 0, 0);
		menu.hide();
		menu.load();

		// 监听菜单事件
		menu.setListener(new LMenuSelect.ClickEvent() {

			@Override
			public void onSelected(int index, String context) {
				int idx = roleIndex.get();
				if (idx != -1 && getRoleIdxObject(idx).getTeam() != Team.Enemy) {
					if (index == 0) {
						moveState.setAttackState(idx);
					} else if (index == 1) {
						moveState.setFinalState(idx);
					}
					menu.hide();
					menuState.hide();
				}
			}
		});

		// 玩家与敌方本回合操作对象临时存储用堆栈
		final Array<GameRole> playerStack = new Array<GameRole>();
		final Array<GameRole> enemyStack = new Array<GameRole>();

		// 显示敌人坐标到雷达中
		Field2D tmp = this.gameMap.getField2D().cpy();
		updateEnemyPos(tmp);
		LRadar radar = new LRadar(Mode.Octagon, 0, 0);
		// 清空雷达中水滴样点
		// radar.clearDrop();
		radar.addField2DToDrop(tmp, LColor.blue, 'E');
		topRightOn(radar);
		add(radar);

		// 触屏up事件处理(我方角色操作)
		up(new Touched() {

			@Override
			public void on(float x, float y) {
				playerTouch(moveState, menuState, menu, roleIndex, clickCount, x, y);
			}
		});

		// 监听拖拽事件滚动地图
		drag(new Touched() {

			@Override
			public void on(float x, float y) {
				gameMap.scroll(x, y);
			}
		});
		
		final BattleProcess battleProcess = getBattle();

		// 添加我方回合事件
		battleProcess.addEvent(new BattleProcess.TurnPlayerEvent(false) {

			// 每次我方回合开始
			@Override
			public void onStart(long elapsedTime, BooleanValue start) {
				for (Role r : teams.getPlayer().list()) {
					gameMap.followAction(r);
					break;
				}

				add(LToast.makeText("我方第" + battleProcess.getTurnCount() + "回合", Style.ERROR));
				for (Role re : teams.all()) {
					GameRole r = (GameRole) re.getRoleObject();
					r.start();
					if (r.getTeam() == Team.Player) {
						if (!playerStack.contains(r)) {
							playerStack.add(r);
						}
					}
				}
				// 完成开始事件
				start.set(true);
			}

			// 每次我方回合正式循环
			@Override
			public void onProcess(long elapsedTime, BooleanValue process) {
				// 我方全部角色移动结束
				if (teams.getPlayer().isMoved()) {
					// 完成我方回合事件
					done();
				}
			}

			// 刷新资源
			@Override
			public void onReset() {
				lockedLocation.clear();
				roleIndex.set(-1);
				moveState.clear();
				menuState.hide();
				menu.hide();
			}
		});

		// 添加敌方回合事件
		battleProcess.addEvent(new BattleProcess.TurnEnemyEvent(false) {

			// 每次敌方回合开始
			@Override
			public void onStart(long elapsedTime, BooleanValue start) {
				final LToast toast = LToast.makeText("敌方第" + battleProcess.getTurnCount() + "回合", Style.ERROR);
				add(toast);
				// 战斗进程等待回合显示事件结束
				wait(new ActionUpdate() {
					@Override
					public void action(Object a) {
						if (toast.isStop()) {
							for (Role re : teams.all()) {
								GameRole r = (GameRole) re.getRoleObject();
								r.start();
								if (r.getTeam() == Team.Enemy) {
									if (!enemyStack.contains(r)) {
										enemyStack.add(r);
									}
								}
							}
						}

					}

					@Override
					public boolean completed() {
						return toast.isStop();
					}
				});
				// 完成开始标记
				start.set(true);
			}

			// 每次敌方回合正式循环
			@Override
			public void onProcess(long elapsedTime, BooleanValue process) {
				// 敌方全部角色移动结束
				if (teams.getEnemy().isMoved()) {
					// 完成敌方回合事件
					done();
				}
				if (!battleProcess.get()) {
					if (enemyStack.size() > 0) {
						final GameRole runRole = enemyStack.pop();
						if (runRole == null) {
							battleProcess.set(false);
							return;
						}
						battleProcess.set(true);

						Field2D map = toCurrentMap(false);
						// 随机选择敌方移动地点,复杂的寻径需要AI支持,等我后面完善battle库部分替换(那部分事实上就是loon的AI实现,但是这库不可能完成度那么高不敢那么叫-_-)
						final PointI movePos = randMove(map, moveState, runRole);

						// 检查是否允许移动到此范围
						if (checkMoveArea(map, movePos.x, movePos.y) && !runRole.isStop()) {
							// 地图切换跟随对象
							gameMap.followAction(runRole);
							// 把随机位置注入位置锁(因为后面是异步执行的Action移动,不占用的话其它角色寻径检查位置时可能会出错……)
							lockedLocation.add(pointi(movePos.x, movePos.y));
							// 转换像素坐标为地图实际坐标
							final PointI startPos = gameMap.tilePixels(runRole.x(), runRole.y());
							final PointI endPos = gameMap.tileMapToPixels(movePos.getX(), movePos.getY());

							// 四方向移动，移动角色到指定地图位置(延迟5帧触发stop事件)
							final MoveTo move = new MoveTo(map, startPos.x, startPos.y, endPos.x, endPos.y, false, 8,
									5);
							move.setActionListener(new ActionListener() {

								@Override
								public void stop(ActionBind o) {
									// 清空跟随对象
									gameMap.followDonot();
									final PointI endPos = gameMap.pixelsToTileMap(o.getX(), o.getY());
									final GameRole enemy = checkRoleExist(endPos.x, endPos.y, 1, Team.Player);
									if (enemy != null) {
										battleProcess.set(true);
										attackEnemy(0, moveState, runRole, enemy);
									} else {
										runRole.stop();
										battleProcess.set(false);
									}
									// 当移动停止时，理论上应该判断下上下左右的障碍物（敌人）关系，然后决定面朝方向，
									// 或者直接交给用户决定，这仅仅是个示例，所以直接面部向下了
									runRole.animate("down");
								}

								@Override
								public void start(ActionBind o) {
									battleProcess.set(true);
								}

								@Override
								public void process(ActionBind o) {
									battleProcess.set(true);
									// 判定移动方向是否变更，避免反复刷新动画事件
									if (move.isDirectionUpdate()) {
										switch (move.getDirection()) {
										case Field2D.TUP:
											runRole.animate("up");
											break;
										default:
										case Field2D.TDOWN:
											runRole.animate("down");
											break;
										case Field2D.TLEFT:
											runRole.animate("left");
											break;
										case Field2D.TRIGHT:
											runRole.animate("right");
											break;
										}
									}
								}
							});
							// 开始缓动动画
							get(runRole).event(move).start();
						} else { // 如果随机生成的移动位置不可用
							PointI point = gameMap.pixelsToTileMap(runRole.getX(), runRole.getY());
							final GameRole enemy = checkRoleExist(point.x, point.y, 1, Team.Player);
							if (enemy != null && runRole.isAllUnDoneAction()) {
								moveState.clear();
								battleProcess.set(true);
								attackEnemy(0, moveState, runRole, enemy);
							} else {
								runRole.stop();
								battleProcess.set(false);
							}
						}
					} else {
						battleProcess.set(false);
					}
				}

			}
		});

	}

	/**
	 * 我方角色触屏操作
	 * 
	 * @param enemyRound
	 * @param roleMoving
	 * @param moveState
	 * @param menuState
	 * @param menu
	 * @param roleIndex
	 * @param count
	 * @param x
	 * @param y
	 */
	public void playerTouch(final Move moveState, final State menuState, final Menu menu, final IntValue roleIndex,
			final Counter count, final float x, final float y) {

		// 敌方回合不响应触屏事件
		if (getBattle().isCurrentEnemy()) {
			return;
		}
		// 如果角色在移动则不能触发事件
		if (getBattle().get()) {
			return;
		}

		// 地图不跟随对象滚动
		gameMap.followDonot();

		PointI tilePos = gameMap.pixelsToScrollTileMap(x, y);

		int curTileX = tilePos.x;
		int curTileY = tilePos.y;
		int index = getRoleIdx(curTileX, curTileY);
		if (index != -1) {
			count.clear();
			GameRole role = getRoleIdxObject(index);
			switch (role.getTeam()) {
			case Team.Player:
				moveState.clear();
				roleIndex.set(index);
				if (lastTileX == curTileX && lastTileY == curTileY) {
					updateMenu(moveState, menuState, menu, curTileX, curTileY);
					moveState.clear();
				} else {
					selectMove(moveState, menuState, menu, index, curTileX, curTileY);
				}
				break;
			case Team.Enemy:
				if (moveState.isAttacking()) {
					GameRole attacker = getRoleIdxObject(roleIndex.get());
					// 检查攻击范围
					if (checkAttackEnemy(attacker, curTileX, curTileY)) {
						// 以索引ID获得敌人并执行攻击
						attackEnemy(Team.Enemy, moveState, attacker, role);
						menuState.hide();
						menu.hide();
						moveState.clear();
						roleIndex.set(-1);
					} else {
						notAttack(moveState, menuState, menu, index, curTileX, curTileY);
						roleIndex.set(-1);
					}
				} else {
					roleIndex.set(index);
					selectMove(moveState, menuState, menu, index, curTileX, curTileY);
				}
				break;
			}
		} else if (roleIndex.get() != -1) {
			final GameRole runRole = getRoleIdxObject(roleIndex.get());
			if (runRole.getTeam() == Team.Player && !runRole.isStop()) {
				if (count.getValue() == 0) {
					moveState.setMoveCourse(curTileX, curTileY);
					menuState.resetSelect();
					menuState.setVisible(false);
					// 累加计数器
					count.increment();
				} else {
					if (moveState.getMoveCourseX() == 0 && moveState.getMoveCourseY() == 0) {
						return;
					}
					// 转换像素坐标为地图实际坐标
					final PointI startPos = gameMap.tilePixels(runRole.x(), runRole.y());
					final PointI newPos = gameMap.tileMapToPixels(moveState.getMoveCourseX(),
							moveState.getMoveCourseY());

					Field2D field = toCurrentMap(true);

					// 移动角色到指定地图位置
					final MoveTo move = new MoveTo(field, startPos.x, startPos.y, newPos.x, newPos.y, false, 8);
					move.setActionListener(new ActionListener() {

						@Override
						public void stop(ActionBind o) {
							getBattle().set(false);
							moveState.clear();
							menu.update(o.getX() + gameMap.getOffsetX(), o.getY() + gameMap.getOffsetY());
							menu.show();
							// 当移动停止时，理论上应该判断下上下左右的障碍物（敌人）关系，然后决定面朝方向，
							// 或者直接交给用户决定，这仅仅是个示例，所以直接面部向下了
							runRole.animate("down");
						}

						@Override
						public void start(ActionBind o) {
							getBattle().set(true);
						}

						@Override
						public void process(ActionBind o) {
							getBattle().set(true);
							// 存储上一个移动方向，避免反复刷新动画事件
							if (move.isDirectionUpdate()) {
								switch (move.getDirection()) {
								case Field2D.TUP:
									runRole.animate("up");
									break;
								default:
								case Field2D.TDOWN:
									runRole.animate("down");
									break;
								case Field2D.TLEFT:
									runRole.animate("left");
									break;
								case Field2D.TRIGHT:
									runRole.animate("right");
									break;
								}
							}

						}
					});
					// 开始缓动动画
					get(runRole).event(move).start();
					moveState.clear();
					count.clear();
				}
			} else {
				moveState.clear();
			}
		}
		lastTileX = curTileX;
		lastTileY = curTileY;
	}

	public void updateMenu(Move moveState, State menuState, Menu menu, int curTileX, int curTileY) {
		menuState.hide();
		PointI pos = gameMap.tileMapToPixels(curTileX, curTileY);
		menu.update(pos.x + gameMap.getOffsetX(), pos.y + gameMap.getOffsetY());
		menu.show();
	}

	public void selectMove(Move moveState, State menuState, Menu menu, int index, int curTileX, int curTileY) {
		moveState.setActionIndex(index);
		moveState.setMoveRange();
		menuState.select(0, index);
		menuState.show();
		menu.hide();
	}

	public void notAttack(Move moveState, State menuState, Menu menu, int index, int curTileX, int curTileY) {
		add(LToast.makeText("不在攻击范围中", Style.ERROR));
		moveState.setActionIndex(-1);
		menuState.hide();
		menu.hide();
		moveState.clear();
	}

}
