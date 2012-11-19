package org.loon.framework.javase.game.action.sprite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import org.loon.framework.javase.game.utils.GraphicsUtils;

/**
 * Copyright 2008 - 2009
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
 * @email��ceponline@yahoo.com.cn
 * @version 0.1
 */
public class SpriteMake {

	final static public int DOWN = 0;

	final static public int LEFT = 1;

	final static public int RIGHT = 2;

	final static public int UP = 3;

	final static public int LOWER_LEFT = 4;

	final static public int LOWER_RIGHT = 5;

	final static public int UPPER_LEFT = 6;

	final static public int UPPER_RIGHT = 7;

	private Image shadowImage;

	private Image[][] images;

	private int imageWidth;

	private int imageHeight;

	public SpriteMake(String fileName, int row, int col) {
		this.imageWidth = row;
		this.imageHeight = col;
		this.images = GraphicsUtils.getSplit2Images(fileName, row, col, false);
		this.images = GraphicsUtils.getFlipHorizintalImage2D(images);
	}

	public Image makeShadowImage() {
		if (shadowImage == null) {
			shadowImage = GraphicsUtils.createImage(
					imageWidth - imageWidth / 3, imageWidth / 2, true);
			Graphics g = shadowImage.getGraphics();
			GraphicsUtils.setAlpha(g, 0.5d);
			GraphicsUtils.rectOval(g, 0, 0, imageWidth - imageWidth / 3,
					imageWidth / 3, Color.black);
			g.dispose();
		}
		return shadowImage;
	}


	public Image[][] getImages() {
		return images;
	}

	public Sprite getMoveSprite(int index, long timer) {
		return new Sprite(images[index], timer);
	}

	public Image[] getMove(int index) {
		return images[index];
	}

	public Image getOnlyMove(int index) {
		return images[index][0];
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

}
