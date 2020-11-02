package loon.srpg.field;

import loon.utils.CollectionUtils;

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
public class SRPGFieldMove {

	public int[][] moves;

	private int[][] needs;

	private static SRPGFieldMove instance;

	public static SRPGFieldMove getInstance(int[][] res) {
		if (instance == null) {
			instance = new SRPGFieldMove(res);
		} else {
			instance.set(res);
		}
		return instance;
	}

	public SRPGFieldMove(int[][] res) {
		this.set(res);
	}

	public void set(int[][] res) {
		this.moves = CollectionUtils.copyOf(res);
	}

	public int[][] getFieldCopy() {
		return CollectionUtils.copyOf(moves);
	}

	public int[][] moveArea(int x, int y, int size) {
		return moveArea(x, y, -1, -1, size);
	}

	public int[][] moveArea(int x, int y, int w, int h) {
		if (!movePossible(x, y, w, h)) {
			return null;
		} else {
			return moveArea(x, y, w, h, -1);
		}
	}

	public int[][] moveArea(int cx, int cy, int cw, int ch, int size) {
		int x = moves[0].length;
		int y = moves.length;
		this.needs = new int[y][x];
		boolean[][] res = new boolean[y][x];
		int[][] res1 = new int[y][x];
		for (int j = 0; j < y; j++) {
			for (int i = 0; i < x; i++) {
				needs[j][i] = -1;
				res[j][i] = false;
				res1[j][i] = moves[j][i];
			}

		}
		this.needs[cy][cx] = 0;
		res1[cy][cx] = 0;
		int index = 1;
		for (int i = 1; size == -1 || i <= size; i++) {
			boolean flag = false;
			int x1 = cx - index - 1;
			int y1 = cy - index - 1;
			int x2 = cx + index + 1;
			int y2 = cy + index + 1;
			if (x1 < 0) {
				x1 = 0;
			}
			if (y1 < 0) {
				y1 = 0;
			}
			if (x2 > x) {
				x2 = x;
			}
			if (y2 > y) {
				y2 = y;
			}
			for (int c1 = y1; c1 < y2; c1++) {
				for (int c2 = x1; c2 < x2; c2++) {
					if (res1[c1][c2] != 0) {
						continue;
					}
					if (c2 - 1 > -1) {
						res[c1][c2 - 1] = true;
					}
					if (c1 - 1 > -1) {
						res[c1 - 1][c2] = true;
					}
					if (c2 + 1 < x) {
						res[c1][c2 + 1] = true;
					}
					if (c1 + 1 < y) {
						res[c1 + 1][c2] = true;
					}
				}

			}

			for (int c1 = y1; c1 < y2; c1++) {
				for (int c2 = x1; c2 < x2; c2++) {
					if (!res[c1][c2] || res1[c1][c2] <= 0) {
						continue;
					}
					res1[c1][c2]--;
					if (res1[c1][c2] == 0 && needs[c1][c2] == -1) {
						needs[c1][c2] = i;
						flag = true;
					}
				}

			}

			if (cw != -1 && ch != -1 && needs[ch][cw] != -1) {
				break;
			}
			if (flag) {
				index++;
			}
		}

		return res1;
	}

	public int[][] moveRoute(int x1, int y1, int x2, int y2, int size) {
		int moveArea[][] = moveArea(x1, y1, size);
		int width = moveArea[0].length;
		int height = moveArea.length;
		if (x2 < 0 || y2 < 0 || x2 >= width || y2 >= height) {
			return null;
		}
		if (moveArea[y2][x2] != 0 || x1 == x2 && y1 == y2) {
			return null;
		}
		int[][] pos = new int[size][2];
		pos[0][0] = x2;
		pos[0][1] = y2;
		for (int i = 1; i < size; i++) {
			pos[i][0] = -1;
			pos[i][1] = -1;
		}

		int moveX = x2;
		int moveY = y2;
		int index = size;
		int count = 1;
		do {
			if (count >= size) {
				break;
			}
			int csize = size + 1;
			int mx = moveX;
			int my = moveY;
			for (int j = 0; j < 4; j++) {
				moveX = (((j - 2) + 1) % 2) * -1 + mx;
				moveY = ((j - 2) % 2) * -1 + my;
				if (moveX < width && moveY < height && moveX > -1 && moveY > -1
						&& csize > needs[moveY][moveX]
						&& needs[moveY][moveX] != -1) {
					csize = needs[moveY][moveX];
					pos[count][0] = moveX;
					pos[count][1] = moveY;
				}
			}

			moveX = pos[count][0];
			moveY = pos[count][1];
			if (moveX == x1 && moveY == y1) {
				index = count;
				break;
			}
			count++;
		} while (true);
		int[][] result = new int[index][2];
		for (int j = 0; j < index; j++) {
			for (int i = 0; i < 2; i++) {
				result[index - j - 1][i] = pos[j][i];
			}
		}
		return result;
	}

	public int[][] movePower(int x, int y, int size) {
		moveArea(x, y, size);
		return needs;
	}

	public int[][] movePower(int x1, int y1, int x2, int y2) {
		moveArea(x1, y1, x2, y2);
		return needs;
	}

	public boolean[][] movePossible(int x, int y) {
		int mx = moves[0].length;
		int my = moves.length;
		boolean[][] res1 = new boolean[my][mx];
		boolean[][] res2 = new boolean[my][mx];
		int[][] movePossible = new int[my][mx];
		for (int j = 0; j < my; j++) {
			for (int i = 0; i < mx; i++) {
				res1[j][i] = false;
				res2[j][i] = false;
				movePossible[j][i] = moves[j][i];
			}
		}

		res1[y][x] = true;
		res2[y][x] = true;
		boolean flag = true;
		for (int count = 0; flag; count++) {
			flag = false;
			int nx1 = x - count - 1;
			int ny1 = y - count - 1;
			int nx2 = x + count + 2;
			int ny2 = y + count + 2;
			if (nx1 < 0) {
				nx1 = 0;
			}
			if (ny1 < 0) {
				ny1 = 0;
			}
			if (nx2 > mx) {
				nx2 = mx;
			}
			if (ny2 > my) {
				ny2 = my;
			}
			for (int j = ny1; j < ny2; j++) {
				for (int i = nx1; i < nx2; i++) {
					if (!res2[j][i]) {
						continue;
					}
					if (i - 1 > -1 && movePossible[j][i - 1] != -1) {
						res1[j][i - 1] = true;
					}
					if (j - 1 > -1 && movePossible[j - 1][i] != -1) {
						res1[j - 1][i] = true;
					}
					if (i + 1 < mx && movePossible[j][i + 1] != -1) {
						res1[j][i + 1] = true;
					}
					if (j + 1 < my && movePossible[j + 1][i] != -1) {
						res1[j + 1][i] = true;
					}
				}

			}

			for (int j = ny1; j < ny2; j++) {
				for (int i = nx1; i < nx2; i++)
					if (res1[j][i] && !res2[j][i]) {
						res2[j][i] = true;
						flag = true;
					}

			}

		}

		return res2;
	}

	public boolean movePossible(int x1, int y1, int x2, int y2) {
		return movePossible(x1, y1)[y2][x2];
	}

}
