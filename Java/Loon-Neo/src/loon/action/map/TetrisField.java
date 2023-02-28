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
package loon.action.map;

import loon.LTexture;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 俄罗斯方块专用游戏区域生成类
 */
public class TetrisField {

	public interface TetrisListener {

		public void draw(GLEx g, float x, float y, LTexture[] stones);

	}

	final static class TetrisBlock {

		private int blockID;
		private int[][] startPosition;

		private int rotatePosition = 0;
		private TetrisField field2d;

		public TetrisBlock(TetrisField f, int blockID) {
			this.field2d = f;
			this.startPosition = new int[field2d.maxBlockSize][field2d.maxBlockLen];
			this.setBlockID(blockID);
			createStone();
		}

		private void createStone() {
			switch (this.blockID) {
			case 1:
				this.startPosition[0][0] = 4;
				this.startPosition[0][1] = 2;
				this.startPosition[1][0] = 5;
				this.startPosition[1][1] = 2;
				this.startPosition[2][0] = 4;
				this.startPosition[2][1] = 3;
				this.startPosition[3][0] = 5;
				this.startPosition[3][1] = 3;
				break;
			case 2:
				this.startPosition[0][0] = 5;
				this.startPosition[0][1] = 0;
				this.startPosition[1][0] = 5;
				this.startPosition[1][1] = 1;
				this.startPosition[2][0] = 5;
				this.startPosition[2][1] = 2;
				this.startPosition[3][0] = 5;
				this.startPosition[3][1] = 3;
				break;
			case 3:
				this.startPosition[0][0] = 5;
				this.startPosition[0][1] = 2;
				this.startPosition[1][0] = 4;
				this.startPosition[1][1] = 3;
				this.startPosition[2][0] = 5;
				this.startPosition[2][1] = 3;
				this.startPosition[3][0] = 6;
				this.startPosition[3][1] = 3;
				break;
			case 4:
				this.startPosition[0][0] = 4;
				this.startPosition[0][1] = 1;
				this.startPosition[1][0] = 4;
				this.startPosition[1][1] = 2;
				this.startPosition[2][0] = 4;
				this.startPosition[2][1] = 3;
				this.startPosition[3][0] = 5;
				this.startPosition[3][1] = 3;
				break;
			case 5:
				this.startPosition[0][0] = 5;
				this.startPosition[0][1] = 1;
				this.startPosition[1][0] = 5;
				this.startPosition[1][1] = 2;
				this.startPosition[2][0] = 5;
				this.startPosition[2][1] = 3;
				this.startPosition[3][0] = 4;
				this.startPosition[3][1] = 3;
				break;
			case 6:
				this.startPosition[0][0] = 5;
				this.startPosition[0][1] = 1;
				this.startPosition[1][0] = 4;
				this.startPosition[1][1] = 2;
				this.startPosition[2][0] = 5;
				this.startPosition[2][1] = 2;
				this.startPosition[3][0] = 4;
				this.startPosition[3][1] = 3;
				break;
			case 7:
				this.startPosition[0][0] = 4;
				this.startPosition[0][1] = 1;
				this.startPosition[1][0] = 4;
				this.startPosition[1][1] = 2;
				this.startPosition[2][0] = 5;
				this.startPosition[2][1] = 2;
				this.startPosition[3][0] = 5;
				this.startPosition[3][1] = 3;
			}
		}

		public int[][] rotateStone(int[][] currentPosition) {
			int[][] newPosition = new int[field2d.maxBlockSize][field2d.maxBlockLen];

			switch (this.blockID) {
			case 2:
				if (this.rotatePosition == 0) {
					int x = currentPosition[3][0];
					int y = currentPosition[3][1];
					newPosition[0][0] = (x - 3);
					newPosition[0][1] = y;
					newPosition[1][0] = (x - 2);
					newPosition[1][1] = y;
					newPosition[2][0] = (x - 1);
					newPosition[2][1] = y;
					newPosition[3][0] = x;
					newPosition[3][1] = y;
					this.rotatePosition = 1;
				} else {
					int x = currentPosition[3][0];
					int y = currentPosition[3][1];
					newPosition[0][0] = x;
					newPosition[0][1] = (y - 3);
					newPosition[1][0] = x;
					newPosition[1][1] = (y - 2);
					newPosition[2][0] = x;
					newPosition[2][1] = (y - 1);
					newPosition[3][0] = x;
					newPosition[3][1] = y;
					this.rotatePosition = 0;
				}
				return newPosition;
			case 3:
				if (this.rotatePosition == 0) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];
					newPosition[0][0] = x;
					newPosition[0][1] = (y - 1);
					newPosition[1][0] = x;
					newPosition[1][1] = (y + 1);
					newPosition[2][0] = x;
					newPosition[2][1] = y;
					newPosition[3][0] = (x + 1);
					newPosition[3][1] = y;
					this.rotatePosition = 1;
				} else if (this.rotatePosition == 1) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];
					newPosition[0][0] = x;
					newPosition[0][1] = (y + 1);
					newPosition[1][0] = (x - 1);
					newPosition[1][1] = y;
					newPosition[2][0] = x;
					newPosition[2][1] = y;
					newPosition[3][0] = (x + 1);
					newPosition[3][1] = y;
					this.rotatePosition = 2;
				} else if (this.rotatePosition == 2) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];
					newPosition[0][0] = x;
					newPosition[0][1] = (y - 1);
					newPosition[1][0] = (x - 1);
					newPosition[1][1] = y;
					newPosition[2][0] = x;
					newPosition[2][1] = y;
					newPosition[3][0] = x;
					newPosition[3][1] = (y + 1);
					this.rotatePosition = 3;
				} else if (this.rotatePosition == 3) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];
					newPosition[0][0] = (x - 1);
					newPosition[0][1] = y;
					newPosition[1][0] = (x + 1);
					newPosition[1][1] = y;
					newPosition[2][0] = x;
					newPosition[2][1] = y;
					newPosition[3][0] = x;
					newPosition[3][1] = (y - 1);
					this.rotatePosition = 0;
				}
				return newPosition;
			case 4:
				if (this.rotatePosition == 0) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];
					newPosition[0][0] = x;
					newPosition[0][1] = y;
					newPosition[1][0] = (x + 1);
					newPosition[1][1] = y;
					newPosition[2][0] = (x + 2);
					newPosition[2][1] = y;
					newPosition[3][0] = (x + 2);
					newPosition[3][1] = (y - 1);
					this.rotatePosition = 1;
				} else if (this.rotatePosition == 1) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];
					newPosition[0][0] = (x - 1);
					newPosition[0][1] = (y - 2);
					newPosition[1][0] = x;
					newPosition[1][1] = (y - 2);
					newPosition[2][0] = x;
					newPosition[2][1] = (y - 1);
					newPosition[3][0] = x;
					newPosition[3][1] = y;
					this.rotatePosition = 2;
				} else if (this.rotatePosition == 2) {
					int x = currentPosition[1][0];
					int y = currentPosition[1][1];
					newPosition[0][0] = (x - 2);
					newPosition[0][1] = (y + 1);
					newPosition[1][0] = (x - 2);
					newPosition[1][1] = y;
					newPosition[2][0] = (x - 1);
					newPosition[2][1] = y;
					newPosition[3][0] = x;
					newPosition[3][1] = y;
					this.rotatePosition = 3;
				} else if (this.rotatePosition == 3) {
					int x = currentPosition[1][0];
					int y = currentPosition[1][1];
					newPosition[0][0] = x;
					newPosition[0][1] = y;
					newPosition[1][0] = x;
					newPosition[1][1] = (y + 1);
					newPosition[2][0] = x;
					newPosition[2][1] = (y + 2);
					newPosition[3][0] = (x + 1);
					newPosition[3][1] = (y + 2);
					this.rotatePosition = 0;
				}
				return newPosition;
			case 5:
				if (this.rotatePosition == 0) {
					int x = currentPosition[1][0];
					int y = currentPosition[1][1];
					newPosition[0][0] = (x - 2);
					newPosition[0][1] = (y - 1);
					newPosition[1][0] = (x - 1);
					newPosition[1][1] = (y - 1);
					newPosition[2][0] = x;
					newPosition[2][1] = (y - 1);
					newPosition[3][0] = x;
					newPosition[3][1] = y;
					this.rotatePosition = 1;
				} else if (this.rotatePosition == 1) {
					int x = currentPosition[0][0];
					int y = currentPosition[0][1];
					newPosition[0][0] = x;
					newPosition[0][1] = y;
					newPosition[1][0] = (x + 1);
					newPosition[1][1] = y;
					newPosition[2][0] = x;
					newPosition[2][1] = (y + 1);
					newPosition[3][0] = x;
					newPosition[3][1] = (y + 2);
					this.rotatePosition = 2;
				} else if (this.rotatePosition == 2) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];
					newPosition[0][0] = x;
					newPosition[0][1] = y;
					newPosition[1][0] = x;
					newPosition[1][1] = (y + 1);
					newPosition[2][0] = (x + 1);
					newPosition[2][1] = (y + 1);
					newPosition[3][0] = (x + 2);
					newPosition[3][1] = (y + 1);
					this.rotatePosition = 3;
				} else if (this.rotatePosition == 3) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];
					newPosition[0][0] = x;
					newPosition[0][1] = y;
					newPosition[1][0] = (x + 1);
					newPosition[1][1] = y;
					newPosition[2][0] = (x + 1);
					newPosition[2][1] = (y - 1);
					newPosition[3][0] = (x + 1);
					newPosition[3][1] = (y - 2);
					this.rotatePosition = 0;
				}
				return newPosition;
			case 6:
				if (this.rotatePosition == 0) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];
					newPosition[0][0] = (x - 1);
					newPosition[0][1] = y;
					newPosition[1][0] = x;
					newPosition[1][1] = (y + 1);
					newPosition[2][0] = x;
					newPosition[2][1] = y;
					newPosition[3][0] = (x + 1);
					newPosition[3][1] = (y + 1);
					this.rotatePosition = 1;
				} else {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];

					newPosition[0][0] = x;
					newPosition[0][1] = (y - 1);
					newPosition[1][0] = (x - 1);
					newPosition[1][1] = y;
					newPosition[2][0] = x;
					newPosition[2][1] = y;
					newPosition[3][0] = (x - 1);
					newPosition[3][1] = (y + 1);
					this.rotatePosition = 0;
				}
				return newPosition;
			case 7:
				if (this.rotatePosition == 0) {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];

					newPosition[0][0] = (x - 1);
					newPosition[0][1] = (y - 1);
					newPosition[1][0] = (x - 1);
					newPosition[1][1] = y;
					newPosition[2][0] = x;
					newPosition[2][1] = y;
					newPosition[3][0] = x;
					newPosition[3][1] = (y + 1);
					this.rotatePosition = 1;
				} else {
					int x = currentPosition[2][0];
					int y = currentPosition[2][1];

					newPosition[0][0] = (x + 1);
					newPosition[0][1] = y;
					newPosition[1][0] = (x - 1);
					newPosition[1][1] = (y + 1);
					newPosition[2][0] = x;
					newPosition[2][1] = y;
					newPosition[3][0] = x;
					newPosition[3][1] = (y + 1);
					this.rotatePosition = 0;
				}
				return newPosition;
			}
			return currentPosition;
		}

		public void noRoate() {
			this.rotatePosition -= 1;
			if (this.rotatePosition == -1) {
				this.rotatePosition = (field2d.maxBlockSize - 1);
			}
		}

		public int[][] getStartPosition() {
			return this.startPosition;
		}

		public void setBlockID(int blockID) {
			this.blockID = blockID;
		}

		public int getBlockID() {
			return this.blockID;
		}
	}

	private TetrisBlock stoneCurrent;
	private TetrisBlock stoneNext;

	private TetrisListener listener;

	private final int maxBlockSize;
	private final int maxBlockLen;

	private int[][] stonePosition;
	private int[][] gameFieldStones;
	private int startStoneValue = 0;
	private int endStoneValue = 1;
	private int curLines = 0;
	private int curLevel = 1;
	private int maxLevel = 20;
	private int curPoints = 0;
	private int rows;
	private int cols;

	private boolean gameStart = false;
	private boolean gameOver = false;

	public TetrisField(int row, int col) {
		this(row, col, 0);
	}

	public TetrisField(int row, int col, int v) {
		this.rows = row;
		this.cols = col;
		this.maxBlockSize = 4;
		this.maxBlockLen = 2;
		this.stonePosition = new int[maxBlockSize][maxBlockLen];
		this.gameFieldStones = new int[rows][cols];
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				this.gameFieldStones[x][y] = v;
			}
		}
		this.gameStart = false;
		this.gameOver = false;
	}

	public int getCols() {
		return this.cols;
	}

	public int getRows() {
		return this.rows;
	}

	public void createCurrentStone() {
		createCurrentStone(startStoneValue, endStoneValue);
	}

	public void createCurrentStone(int minStone, int maxStone) {
		createCurrentStone(MathUtils.random(minStone, maxStone));
	}

	public void createCurrentStone(int nextStoneID) {
		this.stoneCurrent = this.stoneNext;
		createNextStone(nextStoneID);
		this.stonePosition = this.stoneCurrent.getStartPosition();
		if (this.gameFieldStones[5][4] != 0) {
			this.gameOver = true;
		}
		setCurrentStonePosition(this.stonePosition);
	}

	public void createRandomStone() {
		createRandomStone(startStoneValue, endStoneValue);
	}

	public void createRandomStone(int minStone, int maxStone) {
		createNextStone(MathUtils.random(minStone, maxStone));
		createCurrentStone(MathUtils.random(minStone, maxStone));
	}

	public void createNextStone(int stoneID) {
		this.stoneNext = new TetrisBlock(this, stoneID);
	}

	public void setStoneValue(int minStone, int maxStone) {
		setStartStoneValue(minStone);
		setEndStoneValue(maxStone);
	}

	public void setCurrentStonePosition(int[][] stoneNewPosition) {
		for (int i = 0; i < maxBlockSize; i++) {
			this.gameFieldStones[stoneNewPosition[i][0]][stoneNewPosition[i][1]] = this.stoneCurrent.getBlockID();
		}
	}

	public void setCurrentStonePosition(int[][] stoneNewPosition, int[][] StoneOldPosition) {
		for (int i = 0; i < maxBlockSize; i++) {
			this.gameFieldStones[StoneOldPosition[i][0]][StoneOldPosition[i][1]] = 0;
		}
		for (int i = 0; i < maxBlockSize; i++) {
			this.gameFieldStones[stoneNewPosition[i][0]][stoneNewPosition[i][1]] = this.stoneCurrent.getBlockID();
		}
		this.stonePosition = stoneNewPosition;
	}

	public boolean incrementPositionY(boolean isThread) {
		int[][] stoneNewPosition = new int[maxBlockSize][maxBlockLen];
		int canStart = 0;

		for (int i = 0; i < maxBlockSize; i++) {
			if (this.stonePosition[i][1] > (maxBlockSize - 1)) {
				canStart = 1;
			}
		}
		if ((canStart == 1) || (isThread)) {
			for (int i = 0; i < maxBlockSize; i++) {
				stoneNewPosition[i][1] = (this.stonePosition[i][1] + 1);
				stoneNewPosition[i][0] = this.stonePosition[i][0];
			}

			if (!hasCollision(stoneNewPosition)) {
				setCurrentStonePosition(stoneNewPosition, this.stonePosition);
				return true;
			}
			setCurrentStonePosition(this.stonePosition);
			return false;
		}

		return false;
	}

	public void rightPositionX() {
		int[][] stoneNewPosition = new int[maxBlockSize][maxBlockLen];
		int canStart = 0;
		for (int i = 0; i < maxBlockSize; i++) {
			if (this.stonePosition[i][1] > (maxBlockSize - 1)) {
				canStart = 1;
			}
		}
		if (canStart == 1) {
			for (int i = 0; i < maxBlockSize; i++) {
				stoneNewPosition[i][1] = this.stonePosition[i][1];
				stoneNewPosition[i][0] = (this.stonePosition[i][0] + 1);
			}
			if (!hasCollision(stoneNewPosition)) {
				setCurrentStonePosition(stoneNewPosition, this.stonePosition);
			} else {
				setCurrentStonePosition(this.stonePosition);
			}
		}
	}

	public void leftPositionX() {
		int[][] stoneNewPosition = new int[maxBlockSize][maxBlockLen];
		int canStart = 0;

		for (int i = 0; i < maxBlockSize; i++) {
			if (this.stonePosition[i][1] > (maxBlockSize - 1)) {
				canStart = 1;
			}
		}

		if (canStart == 1) {
			for (int i = 0; i < maxBlockSize; i++) {
				stoneNewPosition[i][1] = this.stonePosition[i][1];
				stoneNewPosition[i][0] = (this.stonePosition[i][0] - 1);
			}

			if (!hasCollision(stoneNewPosition)) {
				setCurrentStonePosition(stoneNewPosition, this.stonePosition);
			} else {
				setCurrentStonePosition(this.stonePosition);
			}
		}
	}

	public void downPositionX() {
		incrementPositionY(true);
	}

	public void rotateStone() {
		int[][] stoneNewPosition = new int[maxBlockSize][maxBlockLen];

		stoneNewPosition = this.stoneCurrent.rotateStone(this.stonePosition);

		if (!hasCollision(stoneNewPosition)) {
			setCurrentStonePosition(stoneNewPosition, this.stonePosition);
		} else {
			this.stoneCurrent.noRoate();
			setCurrentStonePosition(this.stonePosition);
		}
	}

	public boolean hasCollision(int[][] stoneNewPosition) {
		for (int i = 0; i < maxBlockSize; i++) {
			this.gameFieldStones[this.stonePosition[i][0]][this.stonePosition[i][1]] = 0;
		}
		for (int i = 0; i < maxBlockSize; i++) {
			if (stoneNewPosition[i][0] < 0) {
				return true;
			}
			if (stoneNewPosition[i][0] == this.rows) {
				return true;
			}
			if (stoneNewPosition[i][1] == this.cols) {
				return true;
			}
			if (this.gameFieldStones[stoneNewPosition[i][0]][stoneNewPosition[i][1]] != 0) {
				return true;
			}
		}
		return false;
	}

	public boolean hasLines() {
		int i = 0;
		int j = 0;
		int[] lines = new int[maxBlockSize];
		int Quantity = 0;
		boolean isLine = false;

		for (i = (maxBlockSize - 1); i < this.cols; i++) {
			for (j = 0; j < this.rows; j++) {
				isLine = true;
				if (this.gameFieldStones[j][i] == 0) {
					isLine = false;
					break;
				}
			}

			if (isLine) {
				lines[Quantity] = i;
				Quantity++;
			}
		}

		if (Quantity > 0) {
			int[][] TempGameField = new int[this.rows][this.cols];
			int sum = 0;
			this.curLines += Quantity;
			this.curLevel = (MathUtils.round(this.curLines / this.rows) + 1);

			if (this.curLevel > this.maxLevel) {
				this.curLevel = this.maxLevel;
			}

			this.curPoints = ((int) (this.curPoints + MathUtils.pow(this.rows * this.curLevel, Quantity)));

			for (i = this.cols - 1; i > (maxBlockSize - 1); i--) {
				for (j = 0; j < this.rows; j++) {
					isLine = true;
					if (this.gameFieldStones[j][i] == 0) {
						isLine = false;
						break;
					}
				}
				if (!isLine) {
					for (j = 0; j < this.rows; j++) {
						TempGameField[j][(i + sum)] = this.gameFieldStones[j][i];
					}
				} else {
					sum++;
				}
			}

			for (i = 0; i < maxBlockSize; i++) {
				for (j = 0; j < this.rows; j++) {
					TempGameField[j][i] = this.gameFieldStones[j][i];
				}
			}

			this.gameFieldStones = TempGameField;

			return true;
		}
		return false;
	}

	public int[][] getStonePosition() {
		return this.gameFieldStones;
	}

	public void draw(GLEx g, LTexture[] stones) {
		draw(g, 0f, 0f, stones);
	}

	public void draw(GLEx g, float x, float y, LTexture[] stones) {
		if (listener != null) {
			listener.draw(g, x, y, stones);
		}
	}

	public int getNextStone() {
		if (this.stoneNext == null) {
			this.stoneNext = new TetrisBlock(this, MathUtils.random(startStoneValue, endStoneValue));
		}
		return this.stoneNext.getBlockID();
	}

	public int getLines() {
		return this.curLines;
	}

	public int getLevel() {
		return this.curLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getPoints() {
		return this.curPoints;
	}

	public TetrisListener getListener() {
		return listener;
	}

	public void setListener(TetrisListener listener) {
		this.listener = listener;
	}

	public int getStartStoneValue() {
		return startStoneValue;
	}

	public void setStartStoneValue(int startStoneValue) {
		this.startStoneValue = startStoneValue;
		if (this.startStoneValue <= 0) {
			this.startStoneValue = 1;
		}
	}

	public int getEndStoneValue() {
		return endStoneValue;
	}

	public void setEndStoneValue(int endStoneValue) {
		this.endStoneValue = endStoneValue;
	}

	public boolean isGameOver() {
		return this.gameOver;
	}

	public boolean isGameStart() {
		return gameStart;
	}

	public void setGameStart(boolean gameStart) {
		this.gameStart = gameStart;
	}

	public boolean isGameRunning() {
		return isGameStart() && !isGameOver();
	}

	public void start() {
		this.gameStart = true;
		this.gameOver = false;
		this.createRandomStone();
	}

}
