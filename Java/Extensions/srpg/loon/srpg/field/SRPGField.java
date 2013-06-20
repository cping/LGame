package loon.srpg.field;

import loon.action.map.Field2D;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;
import loon.srpg.SRPGType;
import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActors;


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
public class SRPGField implements LRelease {

	// 无限制(不受限)
	public static final int FIELD_NORMAL = 0x0;

	// 水中(非特殊状态无法穿越)
	public static final int FIELD_WATER = 0x1;

	// 泥潭(减速)
	public static final int FIELD_MIRE = 0x2;

	// 墙壁(非特殊状态无法穿越)
	public static final int FIELD_WALL = 0x80000000;

	// 死地(入者死)
	public static final int FIELD_KILL = 0x4;

	// 补充(加血加魔)
	public static final int FIELD_PLUS = 0x5;

	private int lazyHashCode = 1;

	private int maxX = 0, maxY = 0;

	private Field2D field2d;

	private int[][] chips;

	private int limitWidth, limitHeight;

	private int x1, y1, x2, y2, posX, posY;

	private int width, height, tileWidth, tileHeight, drawWidth, drawHeight;

	private SRPGFieldElements battleList;

	private int fieldMode;

	private LTexture bigImageMap;

	private boolean isVisible;

	public SRPGField(String fileName, int tileWidth, int tileHeight,
			SRPGFieldElements list) {
		this.isVisible = true;
		this.set(new Field2D(fileName, tileWidth, tileHeight), list);
	}

	public SRPGField(Field2D field, SRPGFieldElements list) {
		this.isVisible = true;
		this.set(field, list);
	}

	public void set(Field2D field, SRPGFieldElements list) {
		this.field2d = field;
		this.chips = field2d.getMap();
		this.width = field.getWidth();
		this.height = field.getHeight();
		this.tileWidth = field.getTileWidth();
		this.tileHeight = field.getTileHeight();
		this.drawWidth = width * tileWidth;
		this.drawHeight = height * tileHeight;
		this.limitWidth = tileWidth - 1;
		this.limitHeight = tileHeight - 1;
		this.battleList = list;
		if (battleList == null) {
			this.battleList = new SRPGFieldElements();
		}
	}

	public SRPGFieldElements getBattleList() {
		return battleList;
	}

	public void setBattleList(SRPGFieldElements battleList) {
		this.battleList = battleList;
	}

	public int[][] getChips() {
		return chips;
	}

	public int getPosChips(int x, int y) {
		if (x < 0 || y < 0 || posX >= width || posY >= height) {
			if (x < 0) {
				x = 0;
			} else if (x >= width) {
				x = width - 1;
			}
			if (y < 0) {
				y = 0;
			} else if (y >= height) {
				y = height - 1;
			}
			return chips[y][x];
		}
		return chips[y][x];
	}

	public SRPGFieldElement getPosMapElement(int x, int y) {
		return battleList.getBattleElement(getPosChips(x, y));
	}

	public void setPosChips(int index, int x, int y) {
		chips[y][x] = index;
	}

	public LTexture getPosImage(int x, int y) {
		if (x < 0 || y < 0 || posX >= width || posY >= height) {
			return null;
		}
		switch (fieldMode) {
		case SRPGType.FIELD_NORMAL:
			return battleList.getBattleElementImage(getPosChips(x, y))
					.getTexture();
		case SRPGType.FIELD_BIGMAP:
			if (bigImageMap == null) {
				return null;
			} else {
				return bigImageMap.getSubTexture(x * tileWidth, y * tileHeight,
						tileWidth, tileHeight);
			}
		case SRPGType.FIELD_BLEND:
			if (bigImageMap == null) {
				return battleList.getBattleElementImage(getPosChips(x, y))
						.getTexture();
			} else {
				return bigImageMap.getSubTexture(x * tileWidth, y * tileHeight,
						tileWidth, tileHeight);
			}

		}
		return battleList.getBattleElementImage(getPosChips(x, y)).getTexture();
	}

	public boolean checkArea(int x, int y) {
		return x >= 0 && y >= 0 && x < width && y < height;
	}

	public int[][] getMoveSpace(int i) {
		int[][] res = new int[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				res[y][x] = getMoveCost(i, 0, x, y);
			}
		}
		return res;
	}

	public int getMoveCost(int index, int x, int y) {
		return getMoveCost(index, 0, x, y);
	}

	public int getMoveCost(int index, int type, int x, int y) {
		SRPGFieldElement element = getPosMapElement(x, y);
		if (element == null) {
			return -1;
		}
		int mv = element.mv;
		int state = element.state;
		if ((mv == -1) && ((index & FIELD_WATER) != FIELD_NORMAL)) {
			mv = 1;
		}
		if (((state & FIELD_WALL) != FIELD_NORMAL)
				&& ((index & FIELD_KILL) == FIELD_NORMAL)) {
			mv = -1;
		}
		if (((state & FIELD_WATER) != FIELD_NORMAL)
				&& ((index & FIELD_WATER) == FIELD_NORMAL)) {
			mv = -1;
		}
		if (((state & FIELD_MIRE) != FIELD_NORMAL)
				&& ((index & FIELD_WATER) == 1)) {
			mv = 1;
		}
		if ((mv != -1) && ((index & FIELD_MIRE) != FIELD_NORMAL)) {
			mv = 1;
		}
		if ((mv != -1) && ((index & 0x20) != FIELD_NORMAL)) {
			mv = mv * 2 - 1;
		}
		if (((state & FIELD_KILL) != FIELD_NORMAL)
				&& ((index & 0x20) == FIELD_NORMAL)
				&& ((index & 0x10) == FIELD_NORMAL)) {
			mv = -1;
		} else {
			switch (type) {
			case 0x10:
				mv = -1;
				break;
			case 0x20:
				mv = 1;
				break;
			}
		}
		return mv;
	}

	public int[][] getMoveSpaceActor(int[][] res, SRPGActors actors, int atk,
			int def) {
		if ((def & 8) == 0) {
			for (int i = 0; i < actors.size(); i++) {
				SRPGActor actor = actors.find(i);
				if (actor.isVisible()
						&& actor.getActorStatus().group != actors.find(atk)
								.getActorStatus().group && actor.isVisible()
						&& actor.getActorStatus().hp > 0) {
					res[actor.getPosY()][actor.getPosX()] = -1;
				}
			}

		}
		return res;
	}

	public int[][] getMoveSpaceAll(SRPGActors actors, int i) {
		int move = actors.find(i).getActorStatus().movetype;
		int[][] res = new int[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				res[y][x] = getMoveCost(i, move, x, y);
			}
		}
		return getMoveSpaceActor(res, actors, i, move);
	}

	public boolean checkWall(int x, int y) {
		if (y >= 0 && y < getHeight() && x >= 0 && x < getWidth()) {
			return getPosMapElement(x, y).state == 0;
		} else {
			return false;
		}
	}

	/**
	 * 以战场元素绘制战场地图
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void draw(GLEx g, int x, int y, int w, int h) {
		if (!isVisible) {
			return;
		}

		int lazy = 1;
		lazy = LSystem.unite(lazy, x);
		lazy = LSystem.unite(lazy, y);
		lazy = LSystem.unite(lazy, w);
		lazy = LSystem.unite(lazy, h);

		int old = g.getBlendMode();

		g.setBlendMode(GL.MODE_SPEED);

		switch (fieldMode) {
		case SRPGType.FIELD_NORMAL:
			if (lazy != lazyHashCode) {
				x1 = x / tileWidth;
				y1 = y / tileHeight;
				x2 = x % tileWidth;
				y2 = y % tileHeight;
				maxY = h / tileHeight + (h % tileHeight + limitHeight)
						/ tileHeight + (y % tileHeight + limitHeight)
						/ tileHeight;
				maxX = w / tileWidth + (w % tileWidth + limitWidth) / tileWidth
						+ (x % tileWidth + limitWidth) / tileWidth;
				battleList.begin();
				for (int nx = 0; nx < maxX; nx++) {
					for (int ny = 0; ny < maxY; ny++) {
						posX = x1 + nx;
						posY = y1 + ny;
						if (posX < 0 || posY < 0 || posX >= width
								|| posY >= height) {
							continue;
						}
						int index = chips[posY][posX];
						battleList.draw(index, nx * tileWidth - x2, ny
								* tileHeight - y2, tileWidth, tileHeight);
					}
				}
				battleList.end();
				lazyHashCode = lazy;
			} else {
				battleList.glCache();
			}
			break;
		case SRPGType.FIELD_BIGMAP:
			g.drawTexture(bigImageMap, -x, -y);
			break;
		case SRPGType.FIELD_BLEND:
			g.drawTexture(bigImageMap, -x, -y);
			if (lazy != lazyHashCode) {
				x1 = x / tileWidth;
				y1 = y / tileHeight;
				x2 = x % tileWidth;
				y2 = y % tileHeight;
				maxY = h / tileHeight + (h % tileHeight + limitHeight)
						/ tileHeight + (y % tileHeight + limitHeight)
						/ tileHeight;
				maxX = w / tileWidth + (w % tileWidth + limitWidth) / tileWidth
						+ (x % tileWidth + limitWidth) / tileWidth;
				battleList.begin();
				for (int nx = 0; nx < maxX; nx++) {
					for (int ny = 0; ny < maxY; ny++) {
						posX = x1 + nx;
						posY = y1 + ny;
						if (posX < 0 || posY < 0 || posX >= width
								|| posY >= height) {
							continue;
						}
						int index = chips[posY][posX];
						battleList.draw(index, nx * tileWidth - x2, ny
								* tileHeight - y2, tileWidth, tileHeight);
					}
				}
				battleList.end();
				lazyHashCode = lazy;
			} else {
				battleList.glCache();
			}
			break;
		}
		g.setBlendMode(old);
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public LTexture getBigImageMap() {
		return bigImageMap;
	}

	public synchronized void setBigImageMap(String fileName) {
		setBigImageMap(new LTexture(fileName, Format.STATIC));
	}

	public synchronized void setBigImageMap(LTexture img) {
		if (img != null) {
			if (this.bigImageMap != null) {
				this.bigImageMap.destroy();
				this.bigImageMap = null;
			}
			if (img.getWidth() == drawWidth && img.getHeight() == drawHeight) {
				this.bigImageMap = img;
			} else {
				this.bigImageMap = img.getSubTexture(0, 0, drawWidth,
						drawHeight);
			}
			this.fieldMode = SRPGType.FIELD_BIGMAP;
		} else {
			this.fieldMode = SRPGType.FIELD_NORMAL;
		}
		LSystem.gc();
	}

	public int getDrawWidth() {
		return drawWidth;
	}

	public int getDrawHeight() {
		return drawHeight;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getFieldMode() {
		return fieldMode;
	}

	public void setFieldMode(int fieldMode) {
		this.fieldMode = fieldMode;
	}

	@Override
	public void dispose() {
		if (bigImageMap != null) {
			bigImageMap.destroy();
			bigImageMap = null;
		}
		if (chips != null) {
			chips = null;
		}
		if (field2d != null) {
			field2d = null;
		}
	}
	
}
