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

		void draw(GLEx g, float x, float y, LTexture[] stones);

	}

	final static class TetrisBlock {

		private int blockID;
		private int[][] startPosition;

		private int rotatePosition = 0;
		private TetrisField field2d;

		public TetrisBlock(TetrisField f, int blockID) {
			this.field2d = f;
			this.startPosition = new int[field2d._maxBlockSize][field2d._maxBlockLen];
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
			int[][] newPosition = new int[field2d._maxBlockSize][field2d._maxBlockLen];

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
				this.rotatePosition = (field2d._maxBlockSize - 1);
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

	private TetrisBlock _stoneCurrent;
	private TetrisBlock _stoneNext;

	private TetrisListener _listener;

	private final int _maxBlockSize;
	private final int _maxBlockLen;

	private int[][] _stonePosition;
	private int[][] _gameFieldStones;
	private int _startStoneValue = 0;
	private int _endStoneValue = 1;
	private int _curLines = 0;
	private int _curLevel = 1;
	private int _maxLevel = 20;
	private int _curPoints = 0;
	private int _rows;
	private int _cols;

	private boolean _gameStart = false;
	private boolean _gameOver = false;

	public TetrisField(int row, int col) {
		this(row, col, 0);
	}

	public TetrisField(int row, int col, int v) {
		this._rows = row;
		this._cols = col;
		this._maxBlockSize = 4;
		this._maxBlockLen = 2;
		this._stonePosition = new int[_maxBlockSize][_maxBlockLen];
		this._gameFieldStones = new int[_rows][_cols];
		for (int x = 0; x < _rows; x++) {
			for (int y = 0; y < _cols; y++) {
				this._gameFieldStones[x][y] = v;
			}
		}
		this._gameStart = false;
		this._gameOver = false;
	}

	public int getCols() {
		return this._cols;
	}

	public int getRows() {
		return this._rows;
	}

	public void createCurrentStone() {
		createCurrentStone(_startStoneValue, _endStoneValue);
	}

	public void createCurrentStone(int minStone, int maxStone) {
		createCurrentStone(MathUtils.random(minStone, maxStone));
	}

	public void createCurrentStone(int nextStoneID) {
		this._stoneCurrent = this._stoneNext;
		createNextStone(nextStoneID);
		this._stonePosition = this._stoneCurrent.getStartPosition();
		if (this._gameFieldStones[5][4] != 0) {
			this._gameOver = true;
		}
		setCurrentStonePosition(this._stonePosition);
	}

	public void createRandomStone() {
		createRandomStone(_startStoneValue, _endStoneValue);
	}

	public void createRandomStone(int minStone, int maxStone) {
		createNextStone(MathUtils.random(minStone, maxStone));
		createCurrentStone(MathUtils.random(minStone, maxStone));
	}

	public void createNextStone(int stoneID) {
		this._stoneNext = new TetrisBlock(this, stoneID);
	}

	public void setStoneValue(int minStone, int maxStone) {
		setStartStoneValue(minStone);
		setEndStoneValue(maxStone);
	}

	public void setCurrentStonePosition(int[][] stoneNewPosition) {
		for (int i = 0; i < _maxBlockSize; i++) {
			this._gameFieldStones[stoneNewPosition[i][0]][stoneNewPosition[i][1]] = this._stoneCurrent.getBlockID();
		}
	}

	public void setCurrentStonePosition(int[][] stoneNewPosition, int[][] stoneOldPosition) {
		for (int i = 0; i < _maxBlockSize; i++) {
			this._gameFieldStones[stoneOldPosition[i][0]][stoneOldPosition[i][1]] = 0;
		}
		for (int i = 0; i < _maxBlockSize; i++) {
			this._gameFieldStones[stoneNewPosition[i][0]][stoneNewPosition[i][1]] = this._stoneCurrent.getBlockID();
		}
		this._stonePosition = stoneNewPosition;
	}

	public boolean incrementPositionY(boolean isThread) {
		int[][] stoneNewPosition = new int[_maxBlockSize][_maxBlockLen];
		int canStart = 0;
		for (int i = 0; i < _maxBlockSize; i++) {
			if (this._stonePosition[i][1] > (_maxBlockSize - 1)) {
				canStart = 1;
			}
		}
		if ((canStart == 1) || (isThread)) {
			for (int i = 0; i < _maxBlockSize; i++) {
				stoneNewPosition[i][1] = (this._stonePosition[i][1] + 1);
				stoneNewPosition[i][0] = this._stonePosition[i][0];
			}

			if (!hasCollision(stoneNewPosition)) {
				setCurrentStonePosition(stoneNewPosition, this._stonePosition);
				return true;
			}
			setCurrentStonePosition(this._stonePosition);
			return false;
		}

		return false;
	}

	public void rightPositionX() {
		int[][] stoneNewPosition = new int[_maxBlockSize][_maxBlockLen];
		int canStart = 0;
		for (int i = 0; i < _maxBlockSize; i++) {
			if (this._stonePosition[i][1] > (_maxBlockSize - 1)) {
				canStart = 1;
			}
		}
		if (canStart == 1) {
			for (int i = 0; i < _maxBlockSize; i++) {
				stoneNewPosition[i][1] = this._stonePosition[i][1];
				stoneNewPosition[i][0] = (this._stonePosition[i][0] + 1);
			}
			if (!hasCollision(stoneNewPosition)) {
				setCurrentStonePosition(stoneNewPosition, this._stonePosition);
			} else {
				setCurrentStonePosition(this._stonePosition);
			}
		}
	}

	public void leftPositionX() {
		int[][] stoneNewPosition = new int[_maxBlockSize][_maxBlockLen];
		int canStart = 0;

		for (int i = 0; i < _maxBlockSize; i++) {
			if (this._stonePosition[i][1] > (_maxBlockSize - 1)) {
				canStart = 1;
			}
		}

		if (canStart == 1) {
			for (int i = 0; i < _maxBlockSize; i++) {
				stoneNewPosition[i][1] = this._stonePosition[i][1];
				stoneNewPosition[i][0] = (this._stonePosition[i][0] - 1);
			}

			if (!hasCollision(stoneNewPosition)) {
				setCurrentStonePosition(stoneNewPosition, this._stonePosition);
			} else {
				setCurrentStonePosition(this._stonePosition);
			}
		}
	}

	public void downPositionX() {
		incrementPositionY(true);
	}

	public void rotateStone() {
		int[][] stoneNewPosition = new int[_maxBlockSize][_maxBlockLen];

		stoneNewPosition = this._stoneCurrent.rotateStone(this._stonePosition);

		if (!hasCollision(stoneNewPosition)) {
			setCurrentStonePosition(stoneNewPosition, this._stonePosition);
		} else {
			this._stoneCurrent.noRoate();
			setCurrentStonePosition(this._stonePosition);
		}
	}

	public boolean hasCollision(int[][] stoneNewPosition) {
		for (int i = 0; i < _maxBlockSize; i++) {
			this._gameFieldStones[this._stonePosition[i][0]][this._stonePosition[i][1]] = 0;
		}
		for (int i = 0; i < _maxBlockSize; i++) {
			if (stoneNewPosition[i][0] < 0) {
				return true;
			}
			if (stoneNewPosition[i][0] == this._rows) {
				return true;
			}
			if (stoneNewPosition[i][1] == this._cols) {
				return true;
			}
			if (this._gameFieldStones[stoneNewPosition[i][0]][stoneNewPosition[i][1]] != 0) {
				return true;
			}
		}
		return false;
	}

	public boolean hasLines() {
		int i = 0;
		int j = 0;
		int[] lines = new int[_maxBlockSize];
		int quantity = 0;
		boolean isLine = false;

		for (i = (_maxBlockSize - 1); i < this._cols; i++) {
			for (j = 0; j < this._rows; j++) {
				isLine = true;
				if (this._gameFieldStones[j][i] == 0) {
					isLine = false;
					break;
				}
			}

			if (isLine) {
				lines[quantity] = i;
				quantity++;
			}
		}

		if (quantity > 0) {
			int[][] tempGameField = new int[this._rows][this._cols];
			int sum = 0;
			this._curLines += quantity;
			this._curLevel = (MathUtils.round(this._curLines / this._rows) + 1);

			if (this._curLevel > this._maxLevel) {
				this._curLevel = this._maxLevel;
			}

			this._curPoints = ((int) (this._curPoints + MathUtils.pow(this._rows * this._curLevel, quantity)));

			for (i = this._cols - 1; i > (_maxBlockSize - 1); i--) {
				for (j = 0; j < this._rows; j++) {
					isLine = true;
					if (this._gameFieldStones[j][i] == 0) {
						isLine = false;
						break;
					}
				}
				if (!isLine) {
					for (j = 0; j < this._rows; j++) {
						tempGameField[j][(i + sum)] = this._gameFieldStones[j][i];
					}
				} else {
					sum++;
				}
			}

			for (i = 0; i < _maxBlockSize; i++) {
				for (j = 0; j < this._rows; j++) {
					tempGameField[j][i] = this._gameFieldStones[j][i];
				}
			}

			this._gameFieldStones = tempGameField;

			return true;
		}
		return false;
	}

	public int[][] getStonePosition() {
		return this._gameFieldStones;
	}

	public void draw(GLEx g, LTexture[] stones) {
		draw(g, 0f, 0f, stones);
	}

	public void draw(GLEx g, float x, float y, LTexture[] stones) {
		if (_listener != null) {
			_listener.draw(g, x, y, stones);
		}
	}

	public int getNextStone() {
		if (this._stoneNext == null) {
			this._stoneNext = new TetrisBlock(this, MathUtils.random(_startStoneValue, _endStoneValue));
		}
		return this._stoneNext.getBlockID();
	}

	public int getLines() {
		return this._curLines;
	}

	public int getLevel() {
		return this._curLevel;
	}

	public int getMaxLevel() {
		return _maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this._maxLevel = maxLevel;
	}

	public int getPoints() {
		return this._curPoints;
	}

	public TetrisListener getListener() {
		return _listener;
	}

	public void setListener(TetrisListener listener) {
		this._listener = listener;
	}

	public int getStartStoneValue() {
		return _startStoneValue;
	}

	public void setStartStoneValue(int startStoneValue) {
		this._startStoneValue = startStoneValue;
		if (this._startStoneValue <= 0) {
			this._startStoneValue = 1;
		}
	}

	public int getEndStoneValue() {
		return _endStoneValue;
	}

	public void setEndStoneValue(int endStoneValue) {
		this._endStoneValue = endStoneValue;
	}

	public boolean isGameOver() {
		return this._gameOver;
	}

	public boolean isGameStart() {
		return _gameStart;
	}

	public void setGameStart(boolean gameStart) {
		this._gameStart = gameStart;
	}

	public boolean isGameRunning() {
		return isGameStart() && !isGameOver();
	}

	public void start() {
		this._gameStart = true;
		this._gameOver = false;
		this.createRandomStone();
	}

}
