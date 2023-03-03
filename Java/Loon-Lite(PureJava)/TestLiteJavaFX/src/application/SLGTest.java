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
import loon.LSystem;
import loon.LTexture;
import loon.Stage;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.MoveTo;
import loon.action.map.AStarFinder;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.action.sprite.AnimatedEntity;
import loon.action.sprite.Draw;
import loon.action.sprite.StatusBar;
import loon.action.sprite.effect.PixelChopEffect;
import loon.action.sprite.effect.StringEffect;
import loon.action.sprite.effect.PixelChopEffect.ChopDirection;
import loon.canvas.LColor;
import loon.component.LMenuSelect;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.events.ActionUpdate;
import loon.events.Touched;
import loon.events.Updateable;
import loon.geom.BooleanValue;
import loon.geom.IntValue;
import loon.geom.PointI;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LTexturePackClip;
import loon.opengl.TextureUtils;
import loon.utils.Array;
import loon.utils.TArray;

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
	public class Move extends Draw {

		private int tileSize;

		private int actionIdx;

		private int[][] moveList;

		private int[][] movingList;

		private int[][] attackList;

		private int moveCount = 0;

		private int maxX;

		private int maxY;

		private int action = 0;

		private PointI moveCourse = pointi();

		private PointI attackPos = pointi();

		private boolean moving = false;

		private boolean selecting = false;

		public Move(TileMap gameMap, int tileSize) {
			// 让角色坐标随地图偏移
			this.setOffset(gameMap.getOffset());
			this.setRepaintAutoOffset(true);
			this.setZ(10000);
			this.maxX = gameMap.getTileWidth();
			this.maxY = gameMap.getTileHeight();
			this.tileSize = tileSize;
			this.moveList = new int[maxX][maxY];
			this.movingList = new int[maxX][maxY];
			this.attackList = new int[maxX][maxY];
			this.clear();
		}

		public Move setFinalState(int idx) {
			this.setActionIndex(idx);
			this.setAction(2);
			getRoleIdxObject(idx).stop();
			return this;
		}

		public void setAttackState(int idx) {
			this.setActionIndex(idx);
			this.setAction(1);
		}

		public void setActionIndex(int idx) {
			this.actionIdx = idx;
		}

		public int getActionIndex() {
			return this.actionIdx;
		}

		public Move setAction(int idx) {
			this.action = idx;
			return this;
		}

		public int getAction() {
			return this.action;
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

		public void setMoveCount(int x, int y) {
			movingList[x][y] = moveCount;
		}

		public void setMoveCount(int x, int y, int count) {
			Role role = getRole(actionIdx);
			// 当为我军时
			if (role.team == PLAYER_TEAM) {
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

		public boolean isSelecting() {
			return this.selecting;
		}

		@Override
		public void clear() {
			super.clear();
			for (int y = 0; y <= maxY - 1; y++) {
				for (int x = 0; x <= maxX - 1; x++) {
					moveCount = 0;
					moveList[x][y] = -1;
					movingList[x][y] = -1;
					attackList[x][y] = 0;
				}
			}
			this.actionIdx = -1;
			this.action = 0;
			this.moveCourse.empty();
			this.attackPos.empty();
			this.moving = false;
			this.selecting = false;
		}

		public int getMoveCount(int x, int y) {
			if ((x < 0) || (x > maxX - 1) || (y < 0) || (y > maxY - 1)) {
				// 无法移动返回-1
				return -1;
			}
			return moveList[x][y];
		}

		public boolean isMoving() {
			return this.moving;
		}

		public void setMoveRange() {

			Role role = getRole(actionIdx);
			int x = gameMap.pixelsToTilesWidth(role.getX());
			int y = gameMap.pixelsToTilesHeight(role.getY());
			int area = role.move; // 有效范围

			moveList[x][y] = 0; // 设定现在为移动0步

			for (int count = 0; count <= area - 1; count++) {
				for (int j = redressY(y - area); j < redressY(y + area); j++) {
					for (int i = redressX(x - (area - Math.abs(y - j))); i <= redressX(
							x + (area - Math.abs(y - j))); i++) {
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
			moving = true;
		}

		public void setAttackPos(int x, int y) {
			this.attackPos.set(x, y);
		}

		public float getAttackX() {
			return this.attackPos.getX();
		}

		public float getAttackY() {
			return this.attackPos.getY();
		}

		/**
		 * 设定移动路线
		 * 
		 */
		public void setMoveCourse(int moveX, int moveY) {
			if (moveList[moveX][moveY] == -1) {
				return;
			}
			if (movingList[moveX][moveY] == moveCount) {
				return;
			}
			moveCourse.set(moveX, moveY);
			// 选择可行的最短路径
			if ((movingList[redressX(moveX - 1)][moveY] != moveCount)
					&& (movingList[moveX][redressY(moveY - 1)] != moveCount)
					&& (movingList[redressX(moveX + 1)][moveY] != moveCount)
					&& (movingList[moveX][redressY(moveY + 1)] != moveCount)
					|| (moveCount + getMapCost(moveX, moveY) > getRole(actionIdx).move)) {

				for (int j = 0; j <= maxY - 1; j++) {
					for (int i = 0; i <= maxX - 1; i++) {
						movingList[i][j] = -1;
					}
				}
				int x = moveX;
				int y = moveY;
				moveCount = moveList[x][y];
				movingList[x][y] = moveCount;
				// 获得移动路径
				for (int i = moveCount; i > 0; i--) {
					switch (setMovePath(x, y)) {
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
				moveCount = moveList[moveX][moveY];
				movingList[x][y] = 0;
				return;
			}
			// 获得矫正的移动步数
			moveCount = moveCount + getMapCost(moveX, moveY);

			if (movingList[moveX][moveY] > -1) {
				moveCount = movingList[moveX][moveY];
				for (int j = 0; j <= maxY - 1; j++) {
					for (int i = 0; i <= maxX - 1; i++) {
						if (movingList[i][j] > movingList[moveX][moveY]) {
							movingList[i][j] = -1;
						}
					}
				}
			}
			movingList[moveX][moveY] = moveCount;
			this.selecting = true;
		}

		/**
		 * 获得已经设置的移动路径
		 * 
		 * @return
		 */
		public PointI getMoveCourse() {
			return moveCourse.cpy();
		}

		public float getMoveCourseX() {
			return moveCourse.getX();
		}

		public float getMoveCourseY() {
			return moveCourse.getY();
		}

		/**
		 * 设定最短移动路径
		 * 
		 * @param x
		 * @param y
		 * @return
		 */
		protected int setMovePath(int x, int y) {
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

		public boolean isAttacking() {
			return action == 1 && actionIdx != -1;
		}

		@Override
		public void draw(GLEx g, float offsetX, float offsetY) {
			PointI pos = null;
			if (action == 0) {
				moveDraw(g, offsetX, offsetY);
			} else if (action == 1 && actionIdx != -1) {
				Role role = getRoleIdxObject(actionIdx);
				pos = pixelsToTileMap(role.getX(), role.getY());
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
			pos = pixelsToScrollTileMap(getTouchX(), getTouchY());
			Role role = getRole(pos.x, pos.y);
			if (action == 1 && role != null && role.team == ENEMY_TEAM) {
				g.draw(iconImages[4], pos.x * tileSize + offsetX, pos.y * tileSize + offsetY);
			} else {
				g.draw(iconImages[0], pos.x * tileSize + offsetX, pos.y * tileSize + offsetY);
			}
			for (int j = 0; j <= maxY - 1; j++) {
				for (int i = 0; i <= maxX - 1; i++) {
					for (Role r : unitList) {
						if (r.action == 2) { // 待机
							pos = pixelsToTileMap(r.getX(), r.getY());
							if (pos.x == i && pos.y == j) {
								g.draw(unitImages[3], i * tileSize + offsetX, j * tileSize + offsetY);
							}
						}
					}
				}
			}
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

		public float getLeft(Role role, float v) {
			float x = (role.getX() - role.getWidth() / 2) + v - role.getWidth();
			return x;
		}

		public float getTop(Role role, float v) {
			float y = (role.getY() - role.getHeight() / 2) + v - role.getHeight() * 3;
			return y;
		}

		public void resetSelect() {
			this.select(-1, -1);
		}

		public void show() {
			this.setVisible(true);
		}

		public void hide() {
			this.setVisible(false);
		}

		/**
		 * 显示角色状态
		 * 
		 * @param g
		 * @param idx
		 */
		public void showRoleState(GLEx g, int idx, float x, float y) {
			if (idx > -1) {
				Role role = (Role) unitList.get(idx);
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
	public class Role extends AnimatedEntity {

		// 分队（0:我军 1:敌军）
		int team;
		// hp
		int hp;

		int move;
		// 行动状态(0:未行动 1:已行动 2:战斗完毕)
		int action;

		StatusBar hpstatus;

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
		public Role(String name, int team, String path, int move, int tileX, int tileY) {
			super(path, 32, 32, gameMap.tilesToPixelsX(tileX), gameMap.tilesToPixelsY(tileY), gameTile, gameTile);
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
			this.team = team;
			this.action = 2;
			this.hp = 10;
			this.move = move;
			// 血条
			this.hpstatus = new StatusBar(width() - 4, height() / 6);
			hpstatus.set(hp);
			hpstatus.setOffsetX(2);
			// 不跟随父精灵旋转
			hpstatus.setFollowRotation(false);
			if (team == PLAYER_TEAM) {
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
			return this.action == 2;
		}

		public void attackStop() {
			this.action = 1;
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

		public int getMove() {
			return move;
		}

		public void setMove(int move) {
			this.move = move;
		}

		public int getTeam() {
			return team;
		}

		public void setTeam(int team) {
			this.team = team;
		}

		public void start() {
			action = 0;
			setColor(LColor.white);
		}

		public void stop() {
			action = 2;
			setColor(LColor.gray);
		}

	}

	/**
	 * 创建角色
	 * 
	 * @param name
	 * @param team
	 * @param imageIndex
	 * @param move
	 * @param x
	 * @param y
	 */
	private Role createRole(String name, int team, String path, int imageIndex, int move, int x, int y) {
		if (team == PLAYER_TEAM) {
			playerCount++;
		} else if (team == ENEMY_TEAM) {
			enemyCount++;
		}
		Role role = new Role(name, team, path, move, x, y);
		unitList.add(role);
		add(role);
		return role;
	}

	public Role getRoleIdxObject(int idx) {
		if (idx > -1 && idx < unitList.size) {
			return unitList.get(idx);
		}
		return null;
	}

	public Role getRole(final int index) {
		if (unitList != null && index > -1) {
			return (Role) unitList.get(index);
		}
		return null;
	}

	public Role getRole(final int tileX, final int tileY) {
		for (Role role : unitList) {
			PointI pos = pixelsToTileMap(role.getX(), role.getY());
			if (tileX == pos.x && tileY == pos.y) {
				return role;
			}
		}
		return null;
	}

	public int getRoleIdx(final int x, final int y) {
		return getRoleIdx(-1, x, y);
	}

	public int getRoleIdx(final int team, final int x, final int y) {
		int index = 0;
		for (Role role : unitList) {
			PointI pos = pixelsToTileMap(role.getX(), role.getY());
			if (team == -1) {
				if (x == pos.x && y == pos.y) {
					return index;
				}
			} else {
				if (x == pos.x && y == pos.y && team == role.team) {
					return index;
				}
			}
			index++;
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
		return type;
	}

	public void removeRole(final BooleanValue gameRunning, final int team, final Role e) {
		// 从单元集合中删除角色
		unitList.remove(e);
		// 放倒角色淡出并删除敌人
		get(e).rotateTo(90).fadeOut(6f).start().setActionListener(new ActionListener() {

			@Override
			public void stop(ActionBind o) {
				remove(e);
				if (team == PLAYER_TEAM) {
					playerCount--;
				} else {
					enemyCount--;
				}
				gameRunning.set(false);
			}

			@Override
			public void start(ActionBind o) {
			}

			@Override
			public void process(ActionBind o) {
			}
		});
	}

	public void attackEnemy(final BooleanValue gameRunning, int team, final Move moveState, final Role attacker,
			final Role enemy) {
		// 如果目标是敌人
		if (enemy.team != attacker.team) {

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
						removeRole(gameRunning, 1, enemy);
					}
					attacker.stop();
					gameRunning.set(false);
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
			gameRunning.set(false);
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
	public boolean checkAttackPlayer(Role attacker, int tileX, int tileY) {
		return checkAttack(attacker, tileX, tileY, PLAYER_TEAM);
	}

	public boolean checkAttackEnemy(Role attacker, int tileX, int tileY) {
		return checkAttack(attacker, tileX, tileY, ENEMY_TEAM);
	}

	public boolean checkAttack(Role attacker, int tileX, int tileY, int team) {
		if (attacker == null) {
			return false;
		}
		PointI attackPos = pixelsToTileMap(attacker.getX(), attacker.getY());
		for (Role r : unitList) {
			if (r.team == team) {
				PointI pos = pixelsToTileMap(r.getX(), r.getY());
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
	public Role checkRoleExist(int tileX, int tileY, int length, int team) {
		for (Role r : unitList) {
			if (r.team != team) {
				continue;
			}
			PointI pos = pixelsToTileMap(r.getX(), r.getY());
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
					return r;
				}
			}
		}
		return null;
	}

	public boolean checkMoveArea(Field2D field, Role role) {
		PointI pos = pixelsToTileMap(role.getX(), role.getY());
		return checkMoveArea(field, pos.x, pos.y);
	}

	public boolean checkMoveArea(Field2D field, int tileX, int tileY) {
		for (Role r : unitList) {
			PointI pos = pixelsToTileMap(r.getX(), r.getY());
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
		for (Role e : unitList) {
			if (e.team == 1) {
				PointI pos = pixelsToTileMap(e.getX(), e.getY());
				field.setTileType(pos.x, pos.y, 'E');
			}
		}
	}

	public void clearEnemyPos(Field2D field) {
		field.replaceType('E', 0);
	}

	public void updatePlayerPos(Field2D field) {
		for (Role e : unitList) {
			if (e.team == 0) {
				PointI pos = pixelsToTileMap(e.getX(), e.getY());
				field.setTileType(pos.x, pos.y, 'P');
			}
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
	protected PointI randMove(Field2D map, Move move, Role role, Counter count) {

		int x = random(-role.move, role.move);
		int y = random(-role.move, role.move);

		PointI pos = pixelsToTileMap(role.getX(), role.getY());

		int newX = move.redressX(pos.x + x);
		int newY = move.redressY(pos.y + y);

		if (checkMoveArea(map, newX, newY)) {
			// 获得实际寻径结果以避免障碍物误判移动范围
			TArray<Vector2f> path = AStarFinder.find(map, pos.x, pos.y, newX, newY, false);
			if (path.size - 1 < role.move) {
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
	protected PointI randMove(Field2D map, Move move, Role role) {
		return randMove(map, move, role, newCounter());
	}

	public PointI tilePixels(float x, float y) {
		int newX = gameMap.getPixelX(x);
		int newY = gameMap.getPixelY(y);
		return pointi(newX, newY);
	}

	/**
	 * 转化地图到屏幕像素(考虑地图滚动)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public PointI tileMapToScrollTilePixels(float x, float y) {
		int newX = gameMap.toTileScrollPixelX(x);
		int newY = gameMap.toTileScrollPixelX(y);
		return pointi(newX, newY);
	}

	/**
	 * 转化屏幕像素到地图(考虑地图滚动)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public PointI pixelsToScrollTileMap(float x, float y) {
		int tileX = gameMap.toPixelScrollTileX(x);
		int tileY = gameMap.toPixelScrollTileY(y);
		return pointi(tileX, tileY);
	}

	/**
	 * 转化屏幕像素到地图(不考虑地图滚动)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public PointI pixelsToTileMap(float x, float y) {
		int tileX = gameMap.pixelsToTilesWidth(x);
		int tileY = gameMap.pixelsToTilesHeight(y);
		return pointi(tileX, tileY);
	}

	/**
	 * 转化地图到屏幕像素(不考虑地图滚动)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public PointI tileMapToPixels(float x, float y) {
		int tileX = gameMap.tilesToPixelsX(x);
		int tileY = gameMap.tilesToPixelsY(y);
		return pointi(tileX, tileY);
	}

	// 我方team标识
	public final static int PLAYER_TEAM = 0;

	// 敌方team标识
	public final static int ENEMY_TEAM = 1;

	private TArray<PointI> lockedLocation = new TArray<PointI>();

	private BooleanValue locked = new BooleanValue();

	private int lastTileX;

	private int lastTileY;

	private TArray<Role> unitList = new TArray<Role>(10);

	final static int gameTile = 32;
	// 战斗个体图
	private LTexture[] unitImages = TextureUtils.getSplitTextures(
			TextureUtils.filterColor("assets/slg/unit.png", new LColor(255, 0, 255), 0, 0.15f, true), gameTile,
			gameTile);

	private LTexture[] iconImages = TextureUtils.getSplitTextures(
			TextureUtils.filterColor("assets/slg/icon.png", new LColor(255, 0, 255), 0, 0.15f, true), gameTile,
			gameTile);

	private LTexture[] listImages = TextureUtils.getSplitTextures(
			TextureUtils.filterColor("assets/slg/list.png", new LColor(255, 0, 255), 0, 0.15f, true), gameTile,
			gameTile);

	private TileMap gameMap = null;

	private int playerCount = 0;

	private int enemyCount = 0;

	@Override
	public void create() {

		// 清空初始数据(重载Screen时有用)
		this.lastTileX = lastTileY = 0;
		this.playerCount = enemyCount = 0;
		this.unitList.clear();
		this.locked.set(false);
		this.lockedLocation.clear();

		// 构建一个2D的二维数组游戏地图
		this.gameMap = new TileMap("assets/slg/map2.txt", 32, 32);
		// 设置切图方式
		TArray<LTexturePackClip> clips = new TArray<LTexturePackClip>(10);
		// 索引,名称,开始切图的x,y位置,以及切下来多少
		clips.add(new LTexturePackClip(0, "1", 0, 0, 32, 32));
		clips.add(new LTexturePackClip(1, "2", 0, 32, 32, 32));
		clips.add(new LTexturePackClip(2, "3", 32, 0, 32, 32));
		clips.add(new LTexturePackClip(3, "4", 32, 32, 32, 32));

		// 注入切图用地图，以及切图方式(也可以直接注入xml配置文件)
		gameMap.setImagePack("assets/slg/map.png", clips);
		// 限制进入区域
		gameMap.setLimit(new int[] { 1, 2, 3, 'P', 'E' });
		// 允许进入区域
		gameMap.setAllowMove(new int[] { 0 });
		// 执行地图与图片绑定
		gameMap.pack();

		// 注入地图到游戏窗体
		add(gameMap);

		// 创建角色:name=空罐少女,team=0(我军),imageindex=3,x=7,y=1,以下雷同
		createRole("空罐少女", 0, "assets/slg/player.png", 1, 5, 12, 20);
		createRole("猫猫1", 0, "assets/slg/player2.png", 0, 4, 11, 20);
		createRole("猫猫2", 0, "assets/slg/player2.png", 0, 4, 13, 20);
		// 创建角色:name=躲猫兵团1,team=1(敌军),imageindex=6,x=4,y=5,以下雷同
		createRole("躲猫兵团1", 1, "assets/slg/enemy1.png", 2, 3, 11, 4);
		createRole("躲猫兵团2", 1, "assets/slg/enemy1.png", 2, 2, 11, 5);
		createRole("躲猫兵团3", 1, "assets/slg/enemy1.png", 2, 3, 11, 6);
		createRole("躲猫兵团5", 1, "assets/slg/enemy1.png", 2, 2, 13, 4);
		createRole("躲猫兵团5", 1, "assets/slg/enemy1.png", 2, 2, 13, 5);
		createRole("躲猫兵团6", 1, "assets/slg/enemy1.png", 2, 3, 13, 6);

		createRole("躲猫兵团8", 1, "assets/slg/enemy1.png", 2, 2, 11, 18);
		createRole("躲猫兵团9", 1, "assets/slg/enemy1.png", 2, 3, 12, 18);
		createRole("躲猫兵团7", 1, "assets/slg/enemy1.png", 2, 2, 13, 18);

		// 当前操作的角色索引
		final IntValue roleIndex = refInt(-1);

		// 显示网格
		// add(new GridEntity());

		// 点击数计数器
		final Counter clickCount = new Counter();

		final BooleanValue gameRunning = refBool();

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
				int idx = roleIndex.result();
				if (idx != -1 && getRoleIdxObject(idx).team != ENEMY_TEAM) {
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
		final Array<Role> playerStack = new Array<Role>();
		final Array<Role> enemyStack = new Array<Role>();

		// 玩家回合与敌方回合标识(事实上就是两个互斥锁,loon默认是单线程的,有且只有一个mainloop(为了兼容gwt),
		// 所以异步处理上比较麻烦(当然写成多线程也可以,参考SRPG那个例子,不过那样就无法跑网页了(以后换成teavm后台应该可以)))
		final BooleanValue playerRound = refBool();
		final BooleanValue enemyRound = refBool();

		// 回合数
		final Counter roundCount = new Counter();

		// 显示敌人坐标到雷达中
		Field2D tmp = this.gameMap.getField2D().cpy();
		updateEnemyPos(tmp);

		// 触屏up事件处理(我方角色操作)
		up(new Touched() {

			@Override
			public void on(float x, float y) {
				playerTouch(enemyRound, gameRunning, moveState, menuState, menu, roleIndex, clickCount, x, y);
			}
		});

		// 监听拖拽事件滚动地图
		drag(new Touched() {

			@Override
			public void on(float x, float y) {
				gameMap.scroll(x, y);
			}
		});

		// 构建一个Screen内循环事件,1秒循环一次
		setTimeout(new Updateable() {

			@Override
			public void action(Object a) {
				// 判断回合变更
				updateRound(roleIndex, playerRound, enemyRound, playerStack, enemyStack, moveState, menuState, menu,
						roundCount);
				// 判断敌方回合
				enemyRound(roleIndex, enemyStack, moveState, gameRunning, roundCount);

			}
		}, LSystem.SECOND);

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
	public void playerTouch(final BooleanValue enemyRound, final BooleanValue gameRunning, final Move moveState,
			final State menuState, final Menu menu, final IntValue roleIndex, final Counter count, final float x,
			final float y) {
		// 敌方回合不响应触屏事件
		if (enemyRound.result()) {
			return;
		}
		// 如果角色在移动则不能触发事件
		if (gameRunning.result()) {
			return;
		}

		// 地图不跟随对象滚动
		gameMap.followDonot();

		PointI tilePos = pixelsToScrollTileMap(x, y);

		int curTileX = tilePos.x;
		int curTileY = tilePos.y;
		int index = getRoleIdx(curTileX, curTileY);
		if (index != -1) {
			count.clear();
			Role role = getRole(index);
			switch (role.team) {
			case PLAYER_TEAM:
				moveState.clear();
				roleIndex.set(index);
				if (lastTileX == curTileX && lastTileY == curTileY) {
					updateMenu(moveState, menuState, menu, curTileX, curTileY);
					moveState.clear();
				} else {
					selectMove(moveState, menuState, menu, index, curTileX, curTileY);
				}
				break;
			case ENEMY_TEAM:
				if (moveState.isAttacking()) {
					Role attacker = getRoleIdxObject(roleIndex.result());
					// 检查攻击范围
					if (checkAttackEnemy(attacker, curTileX, curTileY)) {
						// 以索引ID获得敌人并执行攻击
						attackEnemy(gameRunning, ENEMY_TEAM, moveState, attacker, role);
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
		} else if (roleIndex.result() != -1) {
			final Role runRole = getRole(roleIndex.result());
			if (runRole.team == PLAYER_TEAM && !runRole.isStop()) {
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
					final PointI startPos = tilePixels(runRole.x(), runRole.y());
					final PointI newPos = tileMapToPixels(moveState.getMoveCourseX(), moveState.getMoveCourseY());

					Field2D field = toCurrentMap(true);

					// 移动角色到指定地图位置
					final MoveTo move = new MoveTo(field, startPos.x, startPos.y, newPos.x, newPos.y, false, 8);
					move.setActionListener(new ActionListener() {

						@Override
						public void stop(ActionBind o) {
							gameRunning.set(false);
							moveState.clear();
							menu.update(o.getX() + gameMap.getOffsetX(), o.getY() + gameMap.getOffsetY());
							menu.show();
							// 当移动停止时，理论上应该判断下上下左右的障碍物（敌人）关系，然后决定面朝方向，
							// 或者直接交给用户决定，这仅仅是个示例，所以直接面部向下了
							runRole.animate("down");
						}

						@Override
						public void start(ActionBind o) {
							gameRunning.set(true);
						}

						@Override
						public void process(ActionBind o) {
							gameRunning.set(true);
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
		PointI pos = tileMapToPixels(curTileX, curTileY);
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

	/**
	 * 回合变更
	 * 
	 * @param playerRound
	 * @param enemyRound
	 * @param playerStack
	 * @param enemyStack
	 * @param moveState
	 * @param roundCount
	 */
	public void updateRound(final IntValue roleIndex, BooleanValue playerRound, BooleanValue enemyRound,
			final Array<Role> playerStack, final Array<Role> enemyStack, final Move moveState, final State menuState,
			final Menu menu, final Counter roundCount) {

		// 累计停止动作的角色数量
		int playAmount = 0;
		int enemyAmount = 0;
		for (Role r : unitList) {
			if (r.team == PLAYER_TEAM && r.isStop()) {
				playAmount++;
			} else if (r.team == ENEMY_TEAM && r.isStop()) {
				enemyAmount++;
			}
		}

		// 改变对应回合标识
		if (enemyAmount >= this.enemyCount) {
			playerRound.set(true);
			enemyRound.set(false);
			roundCount.increment();
		} else if (playAmount >= this.playerCount) {
			playerRound.set(false);
			enemyRound.set(true);
		}

		// 开始对象回合
		if (playerRound.result() || enemyRound.result()) {
			locked.set(true);
			lockedLocation.clear();
			roleIndex.set(-1);
			moveState.clear();
			menuState.hide();
			menu.hide();
		}
		if (playerRound.result()) {
			for (Role r : unitList) {
				if (r.team == PLAYER_TEAM) {
					gameMap.followAction(r);
					break;
				}
			}
			add(LToast.makeText("我方第" + roundCount.getValue() + "回合", Style.ERROR));
			for (Role r : unitList) {
				r.start();
				if (r.team == PLAYER_TEAM) {
					if (!playerStack.contains(r)) {
						playerStack.add(r);
					}
				}
			}
			playerRound.set(false);
		} else if (enemyRound.result()) {
			final LToast toast = LToast.makeText("敌方第" + roundCount.getValue() + "回合", Style.ERROR);
			add(toast);
			locked.set(true);
			addProcess(new ActionUpdate() {

				@Override
				public void action(Object a) {
					if (enemyStack.size() >= enemyCount) {
						locked.set(false);
					}
					if (locked.result() && toast.isStop()) {
						for (Role r : unitList) {
							r.start();
							if (r.team == ENEMY_TEAM) {
								if (!enemyStack.contains(r)) {
									enemyStack.add(r);
								}
							}
						}
					}
				}

				@Override
				public boolean completed() {
					return !locked.result();
				}
			});
			enemyRound.set(false);
		}
	}

	/**
	 * 敌方回合
	 * 
	 * @param enemyStack
	 * @param moveState
	 * @param roleMoving
	 * @param enemyRound
	 * @param roundCount
	 */
	public void enemyRound(final IntValue roleIndex, final Array<Role> enemyStack, final Move moveState,
			final BooleanValue gameRunning, final Counter roundCount) {
		if (locked.result()) {
			return;
		}
		if (!gameRunning.result()) {
			if (enemyStack.size() > 0) {
				final Role runRole = enemyStack.pop();
				if (runRole == null) {
					gameRunning.set(false);
					return;
				}
				gameRunning.set(true);

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
					final PointI startPos = tilePixels(runRole.x(), runRole.y());
					final PointI endPos = tileMapToPixels(movePos.getX(), movePos.getY());

					// 四方向移动，移动角色到指定地图位置(延迟5帧触发stop事件)
					final MoveTo move = new MoveTo(map, startPos.x, startPos.y, endPos.x, endPos.y, false, 8, 5);
					move.setActionListener(new ActionListener() {

						@Override
						public void stop(ActionBind o) {
							// 清空跟随对象
							gameMap.followDonot();
							final PointI endPos = pixelsToTileMap(o.getX(), o.getY());
							final Role enemy = checkRoleExist(endPos.x, endPos.y, 1, PLAYER_TEAM);
							if (enemy != null) {
								gameRunning.set(true);
								attackEnemy(gameRunning, 0, moveState, runRole, enemy);
							} else {
								runRole.stop();
								gameRunning.set(false);
							}
							// 当移动停止时，理论上应该判断下上下左右的障碍物（敌人）关系，然后决定面朝方向，
							// 或者直接交给用户决定，这仅仅是个示例，所以直接面部向下了
							runRole.animate("down");
						}

						@Override
						public void start(ActionBind o) {
							gameRunning.set(true);
						}

						@Override
						public void process(ActionBind o) {
							gameRunning.set(true);
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
					PointI point = pixelsToTileMap(runRole.getX(), runRole.getY());
					final Role enemy = checkRoleExist(point.x, point.y, 1, PLAYER_TEAM);
					if (enemy != null && runRole.action < 1) {
						moveState.clear();
						gameRunning.set(true);
						attackEnemy(gameRunning, 0, moveState, runRole, enemy);
					} else {
						runRole.stop();
						gameRunning.set(false);
					}
				}
			} else {
				gameRunning.set(false);
			}
		}
	}

}