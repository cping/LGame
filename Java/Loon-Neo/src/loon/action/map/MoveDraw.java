/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
package loon.action.map;

import loon.action.sprite.Draw;
import loon.geom.PointI;
import loon.geom.Vector2f;

public abstract class MoveDraw extends Draw {

	protected int tileSize;

	protected int actionIdx;

	protected int[][] moveList;

	protected int[][] movingList;

	protected int[][] attackList;

	protected int moveCount = 0;

	protected int maxX;

	protected int maxY;

	protected int action = 0;

	protected PointI moveCourse = new PointI();

	protected PointI attackPos = new PointI();

	protected boolean moving = false;

	protected boolean selecting = false;
	
	protected TileMap gameMap;

	public MoveDraw(TileMap gameMap, int tileSize) {
		// 让角色坐标随地图偏移
		this.setOffset(gameMap.getOffset());
		this.setRepaintAutoOffset(true);
		this.setZ(10000);
		this.gameMap = gameMap;
		this.maxX = gameMap.getTileWidth();
		this.maxY = gameMap.getTileHeight();
		this.tileSize = tileSize;
		this.moveList = new int[maxX][maxY];
		this.movingList = new int[maxX][maxY];
		this.attackList = new int[maxX][maxY];
		this.clear();
	}
	
	public abstract void updateState(int idx);

	public MoveDraw setFinalState(int idx) {
		this.setActionIndex(idx);
		this.setAction(2);
		updateState(idx);
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

	public MoveDraw setAction(int idx) {
		this.action = idx;
		return this;
	}

	public int getAction() {
		return this.action;
	}

	public int fixX(int x) {
		if (x < 0) {
			x = 0;
		}
		if (x > maxX - 1) {
			x = maxX - 1;
		}
		return x;
	}

	public int fixY(int y) {
		if (y < 0) {
			y = 0;
		}
		if (y > maxY - 1) {
			y = maxY - 1;
		}
		return y;
	}

	public void setMoveCount(int x, int y) {
		movingList[x][y] = moveCount;
	}
	
	public abstract boolean allowSetMove(int x, int y);
	
	public abstract int getMapCost(int x,int y);
	
	public abstract int getMove(int idx);

	public void setMoveCount(int x, int y, int count) {
		if(!allowSetMove(x, y)) {
			return;
		}
		int cost = getMapCost(x, y);
		// 指定位置无法进入
		if (cost < 0) {
			return;
		}
		count = count + cost;
		// 移动步数超过移动能力
		if (count > getMove(actionIdx)) {
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
	
	public abstract Vector2f getRolePos(int idx);

	public void setMoveRange() {
		Vector2f role =  getRolePos(actionIdx);
		int x = gameMap.pixelsToTilesWidth(role.getX());
		int y = gameMap.pixelsToTilesHeight(role.getY());
		int area = getMove(actionIdx); // 有效范围

		moveList[x][y] = 0; // 设定现在为移动0步

		for (int count = 0; count <= area - 1; count++) {
			for (int j = fixY(y - area); j < fixY(y + area); j++) {
				for (int i = fixX(x - (area - Math.abs(y - j))); i <= fixX(
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
		for (int j = fixY(y - area); j <= fixY(y + area); j++) {
			for (int i = fixX(x - (area - Math.abs(y - j))); i <= fixX(x + (area - Math.abs(y - j))); i++) {
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
		if ((movingList[fixX(moveX - 1)][moveY] != moveCount)
				&& (movingList[moveX][fixY(moveY - 1)] != moveCount)
				&& (movingList[fixX(moveX + 1)][moveY] != moveCount)
				&& (movingList[moveX][fixY(moveY + 1)] != moveCount)
				|| (moveCount + getMapCost(moveX, moveY) > getMove(actionIdx))) {

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


	public boolean isAttacking() {
		return action == 1 && actionIdx != -1;
	}

}
