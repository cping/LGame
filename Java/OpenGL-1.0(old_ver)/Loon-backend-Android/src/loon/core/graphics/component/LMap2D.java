/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.core.graphics.component;

import java.io.IOException;

import loon.action.map.Field2D;
import loon.action.map.TileMapConfig;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchSheet;
import loon.core.graphics.LComponent;
import loon.core.graphics.LContainer;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.TextureUtils;

/**
 * 该类用以显示简单的二维数组地图。
 * 
 * Example1(其中参数分别为地图信息，地图原始图片，显示的X与Y坐标，以及每块瓦片的截取大小.建议配合Loon默认提供的LevelEditor.
 * jar使用):
 * 
 * LMap2D map2d = new LMap2D("assets/map.txt", "assets/defaultmap.png",
 * 0,0,8,8);
 * 
 **/
public class LMap2D extends LContainer {

	private boolean grid = false;

	private int mainX, mainY;

	private SpriteBatch batch;

	private SpriteBatchSheet texture;

	private int blockSize;

	private Field2D fied2d;

	private static LTexture background;

	private int mapId;
	
	public LMap2D(String datafile, String mapFile, int x, int y,
			int rowTileWidth, int colTileHeight) throws IOException {
		this(datafile, mapFile, x, y, 0, 0, rowTileWidth, colTileHeight, 32);
	}

	/**
	 * 如果需要变更显示时的瓦片大小，可调整tileSize参数来实现
	 * 
	 * @param datafile
	 * @param mapFile
	 * @param x
	 * @param y
	 * @param rowTileWidth
	 * @param colTileHeight
	 * @param tileSize
	 * @throws IOException
	 */
	public LMap2D(String datafile, String mapFile, int x, int y,
			int rowTileWidth, int colTileHeight, int tileSize)
			throws IOException {
		this(datafile, mapFile, x, y, 0, 0, rowTileWidth, colTileHeight,
				tileSize);
	}

	public LMap2D(String datafile, String mapFile, int x, int y, int width,
			int height, int rowTileWidth, int colTileHeight, int tileSize)
			throws IOException {
		super(x, y, width, height);
		int[][] maps = TileMapConfig.loadJustArray(datafile);
		this.blockSize = tileSize;
		if (background == null) {
			LMap2D.background = TextureUtils.createTexture(1, 1, LColor.black);
		}
		this.fied2d = new Field2D(maps);
		this.texture = new SpriteBatchSheet(mapFile, rowTileWidth,
				colTileHeight, 0);
		this.batch = new SpriteBatch();
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(100);
		if (width == 0 || height == 0) {
			setWidth(fied2d.getHeight() * blockSize);
			setHeight(fied2d.getWidth() * blockSize);
		}
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void draw(SpriteBatch batch, int x, int y) {
		int windowX = x + getWidth();
		int windowY = y + getHeight();
		int tempX;
		int tempY;

		int minCol = -(x / blockSize);
		int maxCol = (minCol + ((windowX + (blockSize * 2)) / blockSize));
		if (minCol < 0) {
			minCol = 0;
		}
		if (maxCol < 0) {
			maxCol = 0;
		}
		if (maxCol > fied2d.getHeight()) {
			maxCol = fied2d.getHeight();
		}
		if (minCol > maxCol) {
			minCol = maxCol;
		}

		int minRow = -(y / blockSize);
		int maxRow = (minRow + ((windowY + (blockSize * 2)) / blockSize));
		if (minRow < 0) {
			minRow = 0;
		}
		if (maxRow < 0) {
			maxRow = 0;
		}
		if (maxRow > fied2d.getWidth()) {
			maxRow = fied2d.getWidth();
		}
		if (minRow > maxRow) {
			minRow = maxRow;
		}
		batch.draw(background, x, y, getWidth(), getHeight());
		for (int row = minRow; row < maxRow; row++) {
			for (int col = minCol; col < maxCol; col++) {
				tempX = (x + (col * blockSize));
				tempY = (y + (row * blockSize));
				int id = fied2d.getType(col, row) - 1;
				if (id != -1) {
					texture.animate(id);
					texture.update(tempX, tempY, blockSize, blockSize);
					texture.draw(batch, windowX, windowY);
				}
				if (grid) {
					batch.drawRect(tempX, tempY, blockSize, blockSize);
				}

			}
		}
	}

	protected void processTouchClicked() {
		if (!input.isMoving()) {
			super.processTouchClicked();
		}
	}

	protected void processTouchDragged() {
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
			super.processTouchDragged();
		}
	}

	public int getTileID() {
		return mapId;
	}

	protected void processTouchPressed() {
		if (!input.isMoving()) {
			int posx = 0;
			int posy = 0;
			if (getContainer() == null) {
				posx = (int) ((getX() + input.getTouchX()) / blockSize);
				posy = (int) ((getY() + input.getTouchY()) / blockSize);
			} else {
				posx = (int) ((getContainer().getX() + getX() + input
						.getTouchX()) / blockSize);
				posy = (int) ((getContainer().getY() + getY() + input
						.getTouchY()) / blockSize);
			}
			mapId = fied2d.getType(posx, posy);
			super.processTouchPressed();
		}
	}

	public int getMapPixelWidth() {
		return (int) blockSize * fied2d.getHeight();
	}

	public int getMapPixelHeight() {
		return (int) blockSize * fied2d.getWidth();
	}

	protected void processTouchReleased() {
		if (!input.isMoving()) {
			super.processTouchReleased();
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		LFont font = g.getFont();
		int color = g.getColorARGB();
		if (batch != null) {
			batch.begin();
			draw(batch, mainX + x, mainY + y);
			batch.end();
		}
		g.setColor(color);
		g.setFont(font);

	}

	public int getBlockSize() {
		return blockSize;
	}

	public Field2D getFied2d() {
		return fied2d;
	}

	public boolean isGrid() {
		return grid;
	}

	public void setGrid(boolean grid) {
		this.grid = grid;
	}

	public int getMainX() {
		return mainX;
	}

	public void setMainX(int mainX) {
		this.mainX = mainX;
	}

	public int getMainY() {
		return mainY;
	}

	public void setMainY(int mainY) {
		this.mainY = mainY;
	}

	@Override
	public String getUIName() {
		return "Map2D";
	}

	public void dispose() {
		super.dispose();
		if (texture != null) {
			texture.dispose();
		}
		if (batch != null) {
			batch.dispose();
		}
	}
}
