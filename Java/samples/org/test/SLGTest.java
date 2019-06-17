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

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import loon.LSysException;
import loon.LTexture;
import loon.Stage;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.MoveTo;
import loon.action.map.TileMap;
import loon.action.sprite.Sprite;
import loon.canvas.LColor;
import loon.event.GameKey;
import loon.event.SysKey;
import loon.event.SysTouch;
import loon.event.Touched;
import loon.opengl.GLEx;
import loon.opengl.LTexturePackClip;
import loon.opengl.TextureUtils;
import loon.utils.TArray;
import loon.utils.reply.Port;
import loon.utils.timer.LTimerContext;

public class SLGTest extends Stage {

	// 菜单
	private Menu menu = null;

	private String state;

	private int lastTileX;

	private int lastTileY;

	private int curTileX;

	private int curTileY;

	private int turn = 1;

	private int actionUnit = -1;

	private int moveCount = 0;

	private int[][] moveList;

	private int[][] movingList;

	private int[][] attackList;

	private int maxX;

	private int maxY;

	private TArray<Role> unitList = new TArray<Role>(10);

	// 战斗个体图
	private LTexture[] unitImages = TextureUtils
			.getSplitTextures(TextureUtils.filterColor("assets/unit.png", new LColor(255, 0, 255)), tile, tile);

	private LTexture[] iconImages = TextureUtils
			.getSplitTextures(TextureUtils.filterColor("assets/icon.png", new LColor(255, 0, 255)), tile, tile);

	private LTexture[] listImages = TextureUtils
			.getSplitTextures(TextureUtils.filterColor("assets/list.png", new LColor(255, 0, 255)), tile, tile);

	final static int tile = 32;

	public class Menu {
		// 是否可见
		boolean visible;

		// 菜单纵幅
		int height;

		// 菜单横幅
		int width;

		// 光标位置
		int cur;

		// 菜单类型
		int menuType;

		// 菜单选项
		TArray<String> menuItem;

		int size = 0;

		public Menu(int size) {
			this.menuItem = new TArray<String>(10);
			this.setVisible(false);
			this.setMenuType(0);
			this.setCur(0);
			this.size = size;
		}

		public void free() {
			this.menuItem.clear();
		}

		public int getCur() {
			return cur;
		}

		/**
		 * 设定光标
		 * 
		 * @param cur
		 */
		public void setCur(int cur) {
			if (cur < 0) {
				cur = this.height - 1;
			}
			if (cur > this.height - 1) {
				cur = 0;
			}
			this.cur = cur;
		}

		/**
		 * 获得指定索引位置菜单
		 * 
		 * @param index
		 * @return
		 */
		public String getMenuItem(int index) {
			return (String) menuItem.get(index);

		}

		/**
		 * 获得在地图上相对位置
		 * 
		 * @param size
		 * @param x
		 * @return
		 */
		public int getLeft(int x) {
			return x;
		}

		/**
		 * 设定菜单项
		 *
		 */
		public void setMenuItem() {
			switch (this.menuType) {
			case 0:
				this.width = 3;
				this.height = 1;
				menuItem.add("结束");
				break;
			case 1:
				this.width = 3;
				this.height = 1;
				menuItem.add("待机");
				break;
			case 2:
				this.width = 3;
				this.height = 2;
				menuItem.add("攻击");
				menuItem.add("待机");
				break;
			}
		}

		public int getMenuType() {
			return menuType;
		}

		public void setMenuType(int menuType) {
			this.free();
			this.menuType = menuType;
			this.setMenuItem();
		}

		public boolean isVisible() {
			return visible;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

	}

	public class Role {

		// 名称
		String name;
		// 分队（0:我军 1:敌军）
		int team;
		// hp
		int hp;
		// 角色精灵
		Sprite sprite;
		// 移动力
		int move;
		// 行动状态(0:未行动 1:已行动)
		int action;

		// 是否已进行攻击
		boolean isAttack = false;

		int x = 0;

		int y = 0;

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
		public Role(String name, int team, LTexture image, int move, int x, int y) {
			this.name = name;
			this.team = team;
			this.hp = 10;
			this.sprite = new Sprite(image);
			this.move = move;
			this.x = x;
			this.y = y;
		}

		public int getAction() {
			return action;
		}

		public void setAction(int action) {
			this.action = action;
		}

		public int getHp() {
			return hp;
		}

		public void setHp(int hp) {
			this.hp = hp;
		}

		public LTexture getImage() {
			return sprite.getBitmap();
		}

		public int getMove() {
			return move;
		}

		public void setMove(int move) {
			this.move = move;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getTeam() {
			return team;
		}

		public void setTeam(int team) {
			this.team = team;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public boolean isAttack() {
			return isAttack;
		}

		public void setAttack(boolean isAttack) {
			this.isAttack = isAttack;
		}
	}

	TileMap map = null;

	private boolean isRoleMoveing = false;

	@Override
	public void create() {
		actionUnit = -1;
		state = "战斗开始";
		turn = 1;
		try {
			map = new TileMap("srpg/map1.txt", 32, 32);
		} catch (LSysException e) {
			e.printStackTrace();
		}
		// 设置切图方式
		TArray<LTexturePackClip> clips = new TArray<LTexturePackClip>(10);
		// 索引,名称,开始切图的x,y位置,以及切下来多少
		clips.add(new LTexturePackClip(0, "1", 0, 0, 32, 32));
		clips.add(new LTexturePackClip(1, "2", 0, 32, 32, 32));
		clips.add(new LTexturePackClip(2, "3", 32, 0, 32, 32));
		clips.add(new LTexturePackClip(3, "4", 32, 32, 32, 32));
		// 注入切图用地图，以及切图方式(也可以直接注入xml配置文件)
		map.setImagePack("assets/map.png", clips);
		map.setLimit(new int[] { 1, 2, 3 });
		// 执行地图与图片绑定
		map.pack();
		add(map);
		this.maxX = map.getTileWidth();
		this.maxY = map.getTileHeight();
		this.moveList = new int[maxX][maxY];
		this.movingList = new int[maxX][maxY];
		this.attackList = new int[maxX][maxY];

		// 菜单
		this.menu = new Menu(maxX - 1);
		// 创建角色:name=空罐少女,team=0(我军),imageindex=3,x=7,y=1,以下雷同
		createRole("空罐少女", 0, 0, 3, 7, 1);
		createRole("猫猫1", 0, 1, 6, 1, 2);
		createRole("猫猫2", 0, 0, 3, 2, 6);
		createRole("猫猫3", 0, 0, 3, 12, 6);
		// 创建角色:name=躲猫兵团1,team=1(敌军),imageindex=6,x=4,y=5,以下雷同
		createRole("躲猫兵团1", 1, 2, 4, 4, 5);
		createRole("躲猫兵团2", 1, 2, 4, 8, 5);
		createRole("躲猫兵团3", 1, 2, 4, 5, 7);
		createRole("躲猫兵团4", 1, 2, 4, 7, 2);
		createRole("躲猫兵团5", 1, 2, 4, 7, 7);
		up(new Touched() {

			@Override
			public void on(float x, float y) {
				enter(SysKey.ENTER);
			}
		});

		for (int y = 0; y <= maxY - 1; y++) {
			for (int x = 0; x <= maxX - 1; x++) {
				moveCount = 0;
				moveList[x][y] = -1;
				movingList[x][y] = -1;
				attackList[x][y] = 0;
			}
		}

		add(new Port<LTimerContext>() {

			@Override
			public void onEmit(LTimerContext event) {
				if (!isRoleMoveing) {
					// 角色绘制
					for (int index = 0; index < unitList.size(); index++) {
						Role role = (Role) unitList.get(index);

						role.sprite.setX(role.getX() * tile);
						role.sprite.setY(role.getY() * tile);

					}
				}
				if ((SysTouch.isDrag() || SysTouch.isDown())) {
					curTileX = map.pixelsToTilesWidth(getTouchX());
					curTileY = map.pixelsToTilesWidth(getTouchY());
				}
			}

		});

		add(MultiScreenTest.getBackButton(this, 1));
	}

	public void initRange() {
		for (int y = 0; y <= maxY - 1; y++) {
			for (int x = 0; x <= maxX - 1; x++) {
				moveCount = 0;
				moveList[x][y] = -1;
				movingList[x][y] = -1;
				attackList[x][y] = 0;
			}
		}
	}

	public void closeMenu() {
		menu.visible = false;
	}

	/**
	 * 设定菜单
	 * 
	 * @param menuType
	 */
	public void openMenu(int menuType) {
		menu.visible = true;
		menu.setMenuType(menuType);
		menu.cur = 0;
	}

	/**
	 * 获得移动到指定地点所需步数
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getMoveCount(int x, int y) {
		if ((x < 0) || (x > maxX - 1) || (y < 0) || (y > maxY - 1)) {
			// 无法移动返回-1
			return -1;
		}
		return moveList[x][y];
	}

	/**
	 * 设定移动步数
	 * 
	 * @param x
	 * @param y
	 * @param count
	 */
	public void setMoveCount(int x, int y, int count) {
		Role role = getRole(actionUnit);
		// 当为我军时
		if (role.team == 0) {
			if (getRoleIdx(1, x, y) > -1) {
				return;
			}
		} else {
			if (getRoleIdx(0, x, y) > -1) {
				return;
			}
		}
		int cost = getMapCost(x, y);
		// 指定位置无法进入
		if (cost < 0) {
			return;
		}
		count = count + cost;
		// 移动步数超过移动能力
		if (count > role.move) {
			return;
		}
		// 获得移动所需步数
		if ((moveList[x][y] == -1) || (count < moveList[x][y])) {
			moveList[x][y] = count;
		}
	}

	/**
	 * 设定攻击范围
	 * 
	 * @param isAttack
	 */
	public void setAttackRange(final boolean isAttack) {
		try {
			int x, y, point;
			if (isAttack == true) {
				point = 2;
			} else {
				point = 1;
			}
			Role role = getRole(actionUnit);
			x = role.getX();
			y = role.getY();
			// 判定攻击点
			if (x > 0) {
				attackList[x - 1][y] = point;
			}
			if (y > 0) {
				attackList[x][y - 1] = point;
			}
			if (x < maxX - 1) {
				attackList[x + 1][y] = point;
			}
			if (y < maxY - 1) {
				attackList[x][y + 1] = point;
			}
		} catch (Exception e) {
		}
	}

	public boolean isAttackCheck() {
		for (int i = 0; i < unitList.size(); i++) {
			Role role = getRole(i);
			if (role.team != 1) {
				continue;
			}
			if (attackList[role.getX()][role.getY()] == 2) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设定所有角色参与行动
	 * 
	 */
	public void setBeforeAction() {
		for (Iterator<Role> it = unitList.iterator(); it.hasNext();) {
			Role role = (Role) it.next();
			role.setAction(0);
		}
	}

	/**
	 * 返回指定索引下角色
	 * 
	 * @param index
	 * @return
	 */
	public Role getRole(final int index) {
		if (unitList != null && index > -1) {
			return (Role) unitList.get(index);
		}
		return null;
	}

	/**
	 * 矫正x坐标
	 * 
	 * @param x
	 * @return
	 */
	public int redressX(int x) {
		if (x < 0)
			x = 0;
		if (x > maxX - 1)
			x = maxX - 1;
		return x;
	}

	/**
	 * 矫正y坐标
	 * 
	 * @param y
	 * @return
	 */
	public int redressY(int y) {
		if (y < 0)
			y = 0;
		if (y > maxY - 1)
			y = maxY - 1;
		return y;
	}

	/**
	 * 敌军行动
	 * 
	 */
	public void enemyAction() {
		for (int index = 0; index < unitList.size(); index++) {
			Role role = (Role) unitList.get(index);
			if (role.team != 1) {
				continue;
			}
			if (role.action == 1) {
				continue;
			}
			actionUnit = index;
			setMoveRange();
			// 随机选择敌方移动地点
			int x = role.move - new Random().nextInt(role.move + 1);
			int y = (role.move - Math.abs(x)) - new Random().nextInt((role.move - Math.abs(x)) + 1);
			x = redressX(role.getX() + x);
			y = redressY(role.getY() + y);
			if ((moveList[x][y] > 0) && (getRoleIdx(0, x, y) == -1) && (getRoleIdx(1, x, y) == -1)) {
				// 记录角色最后的移动位置
				lastTileX = role.getX();
				lastTileY = role.getY();
				curTileX = x;
				curTileY = y;
				moveCount = moveList[x][y];
				movingList[x][y] = moveCount;
				moveCount = moveList[curTileX][curTileY];
				movingList[x][y] = 0;
				moveRole();
			}
			state = "敌方行动";
			curTileX = 0;
			curTileY = 0;
			role.setAction(1);
			role.setAttack(false);
			actionUnit = -1;
			initRange();
		}
	}

	/**
	 * 设定移动路线
	 * 
	 */
	public void setMoveCourse() {
		if (moveList[curTileX][curTileY] == -1) {
			return;
		}
		if (movingList[curTileX][curTileY] == moveCount) {
			return;
		}

		// 选择可行的最短路径
		if ((movingList[redressX(curTileX - 1)][curTileY] != moveCount)
				&& (movingList[curTileX][redressY(curTileY - 1)] != moveCount)
				&& (movingList[redressX(curTileX + 1)][curTileY] != moveCount)
				&& (movingList[curTileX][redressY(curTileY + 1)] != moveCount)
				|| (moveCount + getMapCost(curTileX, curTileY) > getRole(actionUnit).move)) {

			for (int j = 0; j <= maxY - 1; j++) {
				for (int i = 0; i <= maxX - 1; i++) {
					movingList[i][j] = -1;
				}
			}
			int x = curTileX;
			int y = curTileY;
			moveCount = moveList[curTileX][curTileY];
			movingList[x][y] = moveCount;
			// 获得移动路径
			for (int i = moveCount; i > 0; i--) {
				switch (setMoveCouse(x, y)) {
				case 0:
					x = x - 1;
					break;
				case 1:
					y = y - 1;
					break;
				case 2:
					x = x + 1;
					break;
				case 3:
					y = y + 1;
					break;
				case 4:
					break;
				}

			}
			moveCount = moveList[curTileX][curTileY];
			movingList[x][y] = 0;
			return;
		}
		// 获得矫正的移动步数
		moveCount = moveCount + getMapCost(curTileX, curTileY);

		if (movingList[curTileX][curTileY] > -1) {
			moveCount = movingList[curTileX][curTileY];
			for (int j = 0; j <= maxY - 1; j++) {
				for (int i = 0; i <= maxX - 1; i++) {
					if (movingList[i][j] > movingList[curTileX][curTileY]) {
						movingList[i][j] = -1;
					}
				}
			}
		}
		movingList[curTileX][curTileY] = moveCount;
	}

	/**
	 * 设定最短移动路径
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int setMoveCouse(int x, int y) {
		// 判定左方最短路径
		if ((x > 0) && (moveList[x - 1][y] > -1) && (moveList[x - 1][y] < moveList[x][y])
				&& (moveList[x - 1][y] == moveCount - getMapCost(x, y))) {

			moveCount = moveCount - getMapCost(x, y);
			movingList[x - 1][y] = moveCount;
			return 0;
		}
		// 判定上方最短路径
		if ((y > 0) && (moveList[x][y - 1] > -1) && (moveList[x][y - 1] < moveList[x][y])
				&& (moveList[x][y - 1] == moveCount - getMapCost(x, y))) {
			moveCount = moveCount - getMapCost(x, y);
			movingList[x][y - 1] = moveCount;
			return 1;
		}

		// 判定右方最短路径
		if ((x < maxX - 1) && (moveList[x + 1][y] > -1) && (moveList[x + 1][y] < moveList[x][y])
				&& (moveList[x + 1][y] == moveCount - getMapCost(x, y))) {
			moveCount = moveCount - getMapCost(x, y);
			movingList[x + 1][y] = moveCount;
			return 2;

		}

		// 判定下方最短路径
		if ((y < maxY - 1) && (moveList[x][y + 1] > -1) && (moveList[x][y + 1] < moveList[x][y])
				&& (moveList[x][y + 1] == moveCount - getMapCost(x, y))) {

			moveCount = moveCount - getMapCost(x, y);
			movingList[x][y + 1] = moveCount;
			return 3;
		}
		return 4;
	}

	public int getMapCost(int x, int y) {
		int type = map.getField().getTileType(x, y);

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
		return type;
	}

	/**
	 * 移动角色
	 * 
	 */
	public void moveRole() {

		final Role role = getRole(actionUnit);
		final MoveTo move = new MoveTo(map.getField(), role.getX() * tile, role.getY() * tile, curTileX * tile,
				curTileY * tile, false, 8);
		// 监听MoveTo
		move.setActionListener(new ActionListener() {

			@Override
			public void stop(ActionBind o) {
				isRoleMoveing = false;
				role.setX(map.pixelsToTilesWidth(o.getX()));
				role.setY(map.pixelsToTilesHeight(o.getY()));
			}

			@Override
			public void start(ActionBind o) {
				isRoleMoveing = true;
				role.setX(map.pixelsToTilesWidth(o.getX()));
				role.setY(map.pixelsToTilesHeight(o.getY()));
			}

			@Override
			public void process(ActionBind o) {
				role.setX(map.pixelsToTilesWidth(o.getX()));
				role.setY(map.pixelsToTilesHeight(o.getY()));
			}
		});
		// 开始缓动动画
		role.sprite.selfAction().event(move).start();
	}

	public void drawText(GLEx g, String text, float x, float y) {
		g.drawText(text, x, y);
	}

	/**
	 * 获得指定索引及分组下角色
	 * 
	 * @param team
	 * @param x
	 * @param y
	 * @return
	 */
	public int getRoleIdx(final int team, final int x, final int y) {
		int index = 0;
		for (Iterator it = unitList.iterator(); it.hasNext();) {
			Role role = (Role) it.next();
			if (x == role.getX() && y == role.getY() && team == role.team) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * 设定移动范围
	 */
	public void setMoveRange() {
		Role role = getRole(actionUnit);
		int x = role.getX();
		int y = role.getY();
		int area = role.move; // 有效范围

		moveList[x][y] = 0; // 设定现在为移动0步

		for (int count = 0; count <= area - 1; count++) {
			for (int j = redressY(y - area); j < redressY(y + area); j++) {
				for (int i = redressX(x - (area - Math.abs(y - j))); i <= redressX(x + (area - Math.abs(y - j))); i++) {
					// 如果能够移动指定步数
					if ((getMoveCount(i - 1, j) == count) || (getMoveCount(i, j - 1) == count)
							|| (getMoveCount(i + 1, j) == count) || (getMoveCount(i, j + 1) == count)) {
						setMoveCount(i, j, count);
					}
				}
			}
		}

		area = area + 1; // 射程
		for (int j = redressY(y - area); j <= redressY(y + area); j++) {
			for (int i = redressX(x - (area - Math.abs(y - j))); i <= redressX(x + (area - Math.abs(y - j))); i++) {
				// 远程攻击
				if ((getMoveCount(i - 1, j) > -1) || (getMoveCount(i, j - 1) > -1) || (getMoveCount(i + 1, j) > -1)
						|| (getMoveCount(i, j + 1) > -1)) {
					attackList[i][j] = 1;
				}
			}
		}
	}

	private void createRole(String name, int team, int imageIndex, int move, int x, int y) {
		Role role = new Role(name, team, unitImages[imageIndex], move, x, y);
		unitList.add(role);
		add(role.sprite);
	}

	@Override
	public void draw(GLEx g) {

		int count = 0;
		// 移动范围绘制
		if ((state.equalsIgnoreCase("角色移动")) || (state.equalsIgnoreCase("移动范围"))) {
			for (int j = 0; j <= maxY - 1; j++) {
				for (int i = 0; i <= maxX - 1; i++) {
					if (moveList[i][j] > -1) {
						g.draw(iconImages[2], i * tile, j * tile);
					} else if (attackList[i][j] > 0) {
						g.draw(iconImages[3], i * tile, j * tile);
					}
				}
			}
		}
		// 角色绘制
		for (int index = 0; index < unitList.size(); index++) {
			Role role = (Role) unitList.get(index);
			/*
			 * if (index == actionUnit) { // 当前控制角色处理（此示例未加入特殊处理）
			 * g.draw(role.getImage(), role.getX() * tile, role.getY() * tile);
			 * } else { g.draw(role.getImage(), role.getX() * tile, role.getY()
			 * * tile); }
			 */
			// 已行动完毕
			if (role.action == 1) {
				g.draw(unitImages[3], role.getX() * tile, role.getY() * tile);
			}
		}
		// 攻击范围绘制
		if (state.equalsIgnoreCase("进行攻击")) {
			for (int j = 0; j <= maxY - 1; j++) {
				for (int i = 0; i <= maxX - 1; i++) {
					int result = attackList[i][j];
					if (result == 2) {
						g.draw(iconImages[3], i * tile, j * tile);
					}
					// 标注选中的攻击对象
					if (result == 2 && getRoleIdx(1, i, j) > -1 && curTileX == i && curTileY == j) {
						g.draw(iconImages[4], i * tile, j * tile);
					}
				}
			}
		}
		// 绘制移动路线
		if (state.equalsIgnoreCase("角色移动")) {
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
						g.draw(iconImages[count + 4], i * tile, j * tile);
					}
				}
			}
		}
		// 菜单
		if (menu.visible) {
			g.setAlpha(0.50f);

			g.draw(listImages[0], menu.getLeft(curTileX) * tile, 0);
			for (int i = 1; i <= menu.width; i++) {
				g.draw(listImages[1], (menu.getLeft(curTileX) + i) * tile, 0);
			}
			g.draw(listImages[2], (menu.getLeft(curTileX) + menu.width + 1) * tile, 0);
			for (int j = 1; j <= menu.height; j++) {
				g.draw(listImages[3], menu.getLeft(curTileX) * tile, j * tile);
				for (int i = 1; i <= menu.width; i++) {
					g.draw(listImages[4], (menu.getLeft(curTileX) + i) * tile, j * tile);
				}
				g.draw(listImages[5], (menu.getLeft(curTileX) + menu.width + 1) * tile, j * tile);
			}
			g.draw(listImages[6], menu.getLeft(curTileX) * tile, (menu.height + 1) * tile);
			for (int i = 1; i <= menu.width; i++) {
				g.draw(listImages[7], (menu.getLeft(curTileX) + i) * tile, (menu.height + 1) * tile);
			}
			g.draw(listImages[8], (menu.getLeft(curTileX) + menu.width + 1) * tile, (menu.height + 1) * tile);
			g.setAlpha(1.0f);

			g.draw(iconImages[1], (menu.getLeft(curTileX) + 1) * tile, (menu.cur + 1) * tile);
			// 写入文字
			for (int j = 1; j <= menu.height; j++) {
				g.setColor(LColor.white);
				drawText(g, menu.getMenuItem(j - 1), (menu.getLeft(curTileX) + 2) * tile, ((j * tile)) + 24);
			}

		}
		// 显示状态
		if (state.equalsIgnoreCase("状态显示")) {
			int i = getRoleIdx(0, curTileX, curTileY);
			if (i == -1) {
				i = getRoleIdx(1, curTileX, curTileY);
			}
			if (i > -1) {
				Role role = (Role) unitList.get(i);
				g.setAlpha(0.75f);
				g.draw(listImages[0], menu.getLeft(curTileX) * tile, 0);
				g.draw(listImages[1], (menu.getLeft(curTileX) + 1) * tile, 0);
				g.draw(listImages[1], (menu.getLeft(curTileX) + 2) * tile, 0);
				g.draw(listImages[2], (menu.getLeft(curTileX) + 3) * tile, 0);

				g.draw(listImages[3], (menu.getLeft(curTileX)) * tile, tile);
				g.draw(listImages[4], (menu.getLeft(curTileX) + 1) * tile, tile);
				g.draw(listImages[4], (menu.getLeft(curTileX) + 2) * tile, tile);
				g.draw(listImages[5], (menu.getLeft(curTileX) + 3) * tile, tile);

				g.draw(listImages[3], menu.getLeft(curTileX) * tile, 64);
				g.draw(listImages[4], (menu.getLeft(curTileX) + 1) * tile, 64);
				g.draw(listImages[4], (menu.getLeft(curTileX) + 2) * tile, 64);
				g.draw(listImages[5], (menu.getLeft(curTileX) + 3) * tile, 64);

				g.draw(listImages[6], (menu.getLeft(curTileX)) * tile, 96);
				g.draw(listImages[7], (menu.getLeft(curTileX) + 1) * tile, 96);
				g.draw(listImages[7], (menu.getLeft(curTileX) + 2) * tile, 96);
				g.draw(listImages[8], (menu.getLeft(curTileX) + 3) * tile, 96);
				g.setAlpha(1.0f);
				// 显示角色数据
				g.draw(role.getImage(), (menu.getLeft(curTileX) + 1) * tile + 16, tile);
				g.setColor(LColor.white);
				drawText(g, "HP:" + role.getHp(), (menu.getLeft(curTileX) + 1) * tile + 12, 78);
				drawText(g, "MV:" + role.getMove(), (menu.getLeft(curTileX) + 1) * tile + 12, 95);
			}
		}
		// 战斗回合
		if (state.equalsIgnoreCase("战斗开始") || state.equalsIgnoreCase("战斗结束")) {
			g.setAlpha(0.5f);
			g.setColor(LColor.black);
			g.fillRect(0, 90, 480, 140);
			g.setColor(LColor.white);
			g.setAlpha(1.0f);
			drawText(g, "第" + turn + "回合", 220, 160);
		}
		// 我方移动
		else if (state.equalsIgnoreCase("开始移动")) {
			// 未添加处理
		} else if (state.equalsIgnoreCase("敌方行动")) {
			for (int i = unitList.size() - 1; i > -1; i--) {
				Role role = (Role) unitList.get(i);
				// 敌军，且无法再次移动和攻击
				if (role.team == 1 && role.action == 1) {
					int x = role.getX();
					int y = role.getY();
					int index = 0;
					// 当敌军移动地点附近才能在我方人物时, 直接删除List中我方角色(实际开发中应加入相应判定)
					if ((index = getRoleIdx(0, x, y + 1)) > -1 && !role.isAttack()) {
						remove(unitList.removeIndex(index));
					} else if ((index = getRoleIdx(0, x, y - 1)) > -1 && !role.isAttack()) {
						remove(unitList.removeIndex(index));
					} else if ((index = getRoleIdx(0, x + 1, y)) > -1 && !role.isAttack()) {
						remove(unitList.removeIndex(index));
					} else if ((index = getRoleIdx(0, x - 1, y)) > -1 && !role.isAttack()) {
						remove(unitList.removeIndex(index));
					}
					role.setAttack(true);
				}
			}

		} else {
			// 绘制光标
			g.draw(iconImages[0], curTileX * tile, curTileY * tile);

		}

	}

	@Override
	public void onKeyDown(GameKey e) {

	}

	public void onKeyUp(GameKey e) {
		enter(e.getKeyCode());
	}

	private void enter(int eventCode) {
		switch (eventCode) {
		// 按下Enter，开始触发游戏事件
		case SysKey.ENTER:
			int index = 0;
			// 当游戏状态为[状态显示]下
			if (state.equalsIgnoreCase("状态显示")) {
				// 光标指向我方未行动角色
				index = getRoleIdx(0, curTileX, curTileY);
				if ((index > -1) && (getRole(index).action == 0)) {
					state = "角色移动";
					actionUnit = getRoleIdx(0, curTileX, curTileY);
					// 绘制移动范围
					setMoveRange();
					movingList[curTileX][curTileY] = moveCount;

					// 光标指向敌方未行动角色
				} else if (getRoleIdx(1, curTileX, curTileY) > -1) {
					state = "移动范围";
					actionUnit = getRoleIdx(1, curTileX, curTileY);
					setMoveRange();

					// 查看角色情报
				} else {
					state = "情报查看";
					openMenu(0);

				}
			}
			// 选择移动
			else if (state.equalsIgnoreCase("角色移动")) {
				// 无法移动的区域
				if (moveList[curTileX][curTileY] < 0) {
					return;
				}
				// 监测移动地点
				if ((getRoleIdx(0, curTileX, curTileY) == -1) || (moveList[curTileX][curTileY] == 0)) {
					lastTileX = getRole(actionUnit).getX();
					lastTileY = getRole(actionUnit).getY();
					moveRole();
					state = "行动菜单";
					// 绘制攻击范围
					setAttackRange(true);
					// 判定菜单项
					if (isAttackCheck()) {
						openMenu(2);
					} else {
						openMenu(1);
					}

				}
			}
			// 当角色移动后
			else if (state.equalsIgnoreCase("行动菜单")) {
				if (menu.getMenuItem(menu.cur).equalsIgnoreCase("攻击")) {
					state = "进行攻击";
					closeMenu();

				} else if (menu.getMenuItem(menu.cur).equalsIgnoreCase("待机")) {
					state = "状态显示";
					closeMenu();
					getRole(actionUnit).action = 1;
					actionUnit = -1;
					initRange();

				}
			}
			// 攻击时
			else if (state.equalsIgnoreCase("进行攻击")) {
				// 无法攻击
				if (attackList[curTileX][curTileY] < 2) {
					return;
				}
				// 当指定地点敌方存在时
				if ((index = getRoleIdx(1, curTileX, curTileY)) > -1) {
					// 删除List中敌方角色（此处可设定减血规范）
					unitList.removeIndex(index);
					state = "状态显示";
					// 改变行动状态
					getRole(actionUnit).action = 1;
					actionUnit = -1;
					initRange();

				}
			}
			// 查看角色移动范围
			else if (state.equalsIgnoreCase("移动范围")) {
				state = "状态显示";
				Role role = getRole(actionUnit);
				curTileX = role.getX();
				curTileY = role.getY();
				actionUnit = -1;
				initRange();

			}
			// 查看角色情报
			else if (state.equalsIgnoreCase("情报查看")) {
				// 本回合战斗结束
				if (menu.getMenuItem(menu.cur).equalsIgnoreCase("结束")) {
					closeMenu();
					curTileX = 0;
					curTileY = 0;
					setBeforeAction();
					state = "战斗结束";

				}
			}
			// 我军开始行动
			else if (state.equalsIgnoreCase("战斗开始")) {
				state = "状态显示";

			}
			// 敌军开始行动
			else if (state.equalsIgnoreCase("战斗结束")) {
				state = "敌方行动";
				enemyAction();
				setBeforeAction();
				turn = turn + 1;
				state = "战斗开始";

			}
			break;
		// 按下ESC，取消已做选择
		case SysKey.ESCAPE:
			if (state.equalsIgnoreCase("角色移动")) // 移动
			{
				state = "状态显示";
				Role role = (Role) unitList.get(actionUnit);
				curTileX = role.getX();
				curTileY = role.getY();
				actionUnit = -1;
				initRange();

			} else if (state.equalsIgnoreCase("行动菜单")) // 移动后
			{
				state = "角色移动";
				closeMenu();
				setAttackRange(false); // 不显示攻击范围
				Role role = (Role) unitList.get(actionUnit);
				role.setX(lastTileX);
				role.setY(lastTileY);

			} else if (state.equalsIgnoreCase("进行攻击")) // 攻击状态
			{
				state = "行动菜单";
				Role role = (Role) unitList.get(actionUnit);
				curTileX = role.getX();
				curTileY = role.getY();
				openMenu(menu.menuType);

			} else if (state.equalsIgnoreCase("移动范围")) { // 移动范围

				state = "状态显示";
				Role role = (Role) unitList.get(actionUnit);
				curTileX = role.getX();
				curTileY = role.getY();
				actionUnit = -1;
				initRange();

			}

			else if (state.equalsIgnoreCase("情报查看")) // 角色情报
			{
				state = "状态显示";
				closeMenu();

			}

			else if (state.equalsIgnoreCase("战斗开始")) // 我军行动
			{
				state = "状态显示";

			}

			else if (state.equalsIgnoreCase("战斗结束")) // 敌军行动
			{
				state = "敌方行动";
				enemyAction();
				setBeforeAction();
				turn = turn + 1;
				state = "战斗开始";

			}
			break;
		}
		if (eventCode > -1) {
			eventCode = -1;
		}
		if (state.equalsIgnoreCase("战斗开始"))
			return;
		if (state.equalsIgnoreCase("战斗结束"))
			return;
		if (state.equalsIgnoreCase("敌方行动"))
			return;
		// 菜单可见
		if (menu.visible) {
			switch (SysKey.getKeyCode()) {
			case SysKey.UP:
				if (menu.cur > 0) {
					menu.cur = menu.cur - 1;
				}
				break;
			case SysKey.DOWN:
				if (menu.cur < menu.height - 1) {
					menu.cur = menu.cur + 1;
				}
				break;
			}

		}
		// 菜单不可见
		else {
			switch (SysKey.getKeyCode()) {
			case SysKey.LEFT:
				curTileX = redressX(curTileX - 1);
				break;
			case SysKey.UP:
				curTileY = redressY(curTileY - 1);
				break;
			case SysKey.RIGHT:
				curTileX = redressX(curTileX + 1);
				break;
			case SysKey.DOWN:
				curTileY = redressY(curTileY + 1);
				break;
			}
		}
		if (state.equalsIgnoreCase("角色移动")) {
			setMoveCourse();
		}

	}

	public void onCancelClick() {
		if (state.equalsIgnoreCase("角色移动")) // 移动
		{
			state = "状态显示";
			Role role = (Role) unitList.get(actionUnit);
			curTileX = role.getX();
			curTileY = role.getY();
			actionUnit = -1;
			initRange();

		} else if (state.equalsIgnoreCase("行动菜单")) // 移动后
		{
			state = "角色移动";
			closeMenu();
			setAttackRange(false); // 不显示攻击范围
			Role role = (Role) unitList.get(actionUnit);
			role.setX(lastTileX);
			role.setY(lastTileY);

		} else if (state.equalsIgnoreCase("进行攻击")) // 攻击状态
		{
			state = "行动菜单";
			Role role = (Role) unitList.get(actionUnit);
			curTileX = role.getX();
			curTileY = role.getY();
			openMenu(menu.menuType);

		} else if (state.equalsIgnoreCase("移动范围")) { // 移动范围

			state = "状态显示";
			Role role = (Role) unitList.get(actionUnit);
			curTileX = role.getX();
			curTileY = role.getY();
			actionUnit = -1;
			initRange();

		}

		else if (state.equalsIgnoreCase("情报查看")) // 角色情报
		{
			state = "状态显示";
			closeMenu();
		}

		else if (state.equalsIgnoreCase("战斗开始")) // 我军行动
		{
			state = "状态显示";
		} else if (state.equalsIgnoreCase("战斗结束")) // 敌军行动
		{
			state = "敌方行动";
			enemyAction();
			setBeforeAction();
			turn = turn + 1;
			state = "战斗开始";

		}

	}

	public void onCircleClick() {
		int index = 0;
		// 当游戏状态为[状态显示]下
		if (state.equalsIgnoreCase("状态显示")) {
			// 光标指向我方未行动角色
			index = getRoleIdx(0, curTileX, curTileY);
			if ((index > -1) && (getRole(index).action == 0)) {
				state = "角色移动";
				actionUnit = getRoleIdx(0, curTileX, curTileY);
				// 绘制移动范围
				setMoveRange();
				movingList[curTileX][curTileY] = moveCount;

				// 光标指向敌方未行动角色
			} else if (getRoleIdx(1, curTileX, curTileY) > -1) {
				state = "移动范围";
				actionUnit = getRoleIdx(1, curTileX, curTileY);
				setMoveRange();

				// 查看角色情报
			} else {
				state = "情报查看";
				openMenu(0);

			}
		}
		// 选择移动
		else if (state.equalsIgnoreCase("角色移动")) {
			// 无法移动的区域
			if (moveList[curTileX][curTileY] < 0) {
				return;
			}
			// 监测移动地点
			if ((getRoleIdx(0, curTileX, curTileY) == -1) || (moveList[curTileX][curTileY] == 0)) {
				lastTileX = getRole(actionUnit).getX();
				lastTileY = getRole(actionUnit).getY();
				moveRole();
				state = "行动菜单";
				// 绘制攻击范围
				setAttackRange(true);
				// 判定菜单项
				if (isAttackCheck()) {
					openMenu(2);
				} else {
					openMenu(1);
				}

			}
		}
		// 当角色移动后
		else if (state.equalsIgnoreCase("行动菜单")) {
			if (menu.getMenuItem(menu.cur).equalsIgnoreCase("攻击")) {
				state = "进行攻击";
				closeMenu();

			} else if (menu.getMenuItem(menu.cur).equalsIgnoreCase("待机")) {
				state = "状态显示";
				closeMenu();
				getRole(actionUnit).action = 1;
				actionUnit = -1;
				initRange();

			}
		}
		// 攻击时
		else if (state.equalsIgnoreCase("进行攻击")) {
			// 无法攻击
			if (attackList[curTileX][curTileY] < 2) {
				return;
			}
			// 当指定地点敌方存在时
			if ((index = getRoleIdx(1, curTileX, curTileY)) > -1) {
				// 删除List中敌方角色（此处可设定减血规范）
				unitList.removeIndex(index);
				state = "状态显示";
				// 改变行动状态
				getRole(actionUnit).action = 1;
				actionUnit = -1;
				initRange();

			}
		}
		// 查看角色移动范围
		else if (state.equalsIgnoreCase("移动范围")) {
			state = "状态显示";
			Role role = getRole(actionUnit);
			curTileX = role.getX();
			curTileY = role.getY();
			actionUnit = -1;
			initRange();

		}
		// 查看角色情报
		else if (state.equalsIgnoreCase("情报查看")) {
			// 本回合战斗结束
			if (menu.getMenuItem(menu.cur).equalsIgnoreCase("结束")) {
				closeMenu();
				curTileX = 0;
				curTileY = 0;
				setBeforeAction();
				state = "战斗结束";

			}
		}
		// 我军开始行动
		else if (state.equalsIgnoreCase("战斗开始")) {
			state = "状态显示";

		}
		// 敌军开始行动
		else if (state.equalsIgnoreCase("战斗结束")) {
			state = "敌方行动";
			enemyAction();
			setBeforeAction();
			turn = turn + 1;
			state = "战斗开始";

		}
	}

}
