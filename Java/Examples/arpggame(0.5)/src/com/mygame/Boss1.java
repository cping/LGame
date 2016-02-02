package com.mygame;

import java.util.Vector;

import loon.geom.RectBox;
import loon.utils.MathUtils;

public class Boss1 extends Enemy {

	Boss1(float x, float y) {
		MAX_FRAMES = (new int[] { 2, 2, 2, 2, 2, 2 });
		MAX_FRAME_DELAY = (new int[] { 5, 5, 5, 5, 10, 10 });
		for (int i = 0; i < 3; i++)
			if ((int) (Math.random() * 2D) > 0)
				choice++;

		this.x = x;
		this.y = y;
		hp = 10;
		speed = 1.5f;
		height = 104;
		width = 64;
		timeToAct = 0;
		rare = new BlueGem(x, y);
		visionInfo = new int[5][5];
		visionInfo[0][0] = 0;
		visionInfo[0][1] = -100;
		visionInfo[0][2] = 0;
		visionInfo[0][Enemy.VISION_WIDTH] = 64;
		visionInfo[0][Enemy.VISION_HEIGHT] = 100;
		visionInfo[2][0] = 0;
		visionInfo[2][1] = height;
		visionInfo[2][2] = 180;
		visionInfo[2][Enemy.VISION_WIDTH] = 64;
		visionInfo[2][Enemy.VISION_HEIGHT] = 100;
		visionInfo[3][0] = -100;
		visionInfo[3][1] = 0;
		visionInfo[3][2] = 270;
		visionInfo[3][Enemy.VISION_WIDTH] = 100;
		visionInfo[3][Enemy.VISION_HEIGHT] = 104;
		visionInfo[1][0] = width;
		visionInfo[1][1] = 0;
		visionInfo[1][2] = 90;
		visionInfo[1][Enemy.VISION_WIDTH] = 100;
		visionInfo[1][Enemy.VISION_HEIGHT] = 104;
		visionInfo[4][0] = 0;
		visionInfo[4][1] = 0;
		visionInfo[4][2] = 0;
		visionInfo[4][Enemy.VISION_WIDTH] = 0;
		visionInfo[4][Enemy.VISION_HEIGHT] = 0;
	}

	public void update(Map m, Player player, Vector<?> items, Bullet eBullets[],
			int MAX_BULLETS, ChargeShot eShots[], int MAX_CHARGESHOT) {
		if (state != STATE_DEATH) {
			if (state != STATE_DYING) {
				if (timeToAct <= 0 && makeChoice) {
					choice = (int) (MathUtils.random() * 4f);
					if (choice == 0)
						destY = y - height;
					else if (choice == 1)
						destX = x + width;
					else if (choice == 2)
						destY = y + height;
					else if (choice == 3)
						destX = x - width;
					timeToAct = 0;
					makeChoice = false;
				}
				if (choice == 0) {
					if (y > destY) {
						moveUp(m);
					} else {
						makeChoice = true;
						choice = 5;
					}
				} else if (choice == 1) {
					if (x < destX) {
						moveRight(m);
					} else {
						makeChoice = true;
						choice = 5;
					}
				} else if (choice == 2) {
					if (y < destY) {
						moveDown(m);
					} else {
						makeChoice = true;
						choice = 5;
					}
				} else if (choice == 3)
					if (x > destX) {
						moveLeft(m);
					} else {
						makeChoice = true;
						choice = 5;
					}
				if (timeToAct > 0)
					timeToAct--;
				if (flickerTime > 0) {
					flickerTime--;
					if (!flicker)
						flicker = true;
					else if (flicker)
						flicker = false;
				}
				deathCheck();
				collision(player);
			}
			if (frameDelay > 0)
				frameDelay--;
			updateStateAndFrame();
		}
	}

	private RectBox rect = new RectBox();

	public void moveUp(Map m) {
		y -= speed;
		boolean b = false;
		RectBox col[] = getColTop(m);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				y += (col[i].y + col[i].height) - y;
				b = true;
			}
		}
		if (b) {
			makeChoice = true;
			choice = 5;
		}
		state = 0;
		walking = true;
	}

	public void moveDown(Map m) {
		y += speed;
		boolean b = false;
		RectBox col[] = getColBottom(m);
		for (int i = 0; i < 3; i++) {
			if (rect.intersects(col[i])) {
				y -= (y + height) - col[i].y;
				b = true;
			}
		}
		if (b) {
			makeChoice = true;
			choice = 5;
		}
		state = 2;
		walking = true;
	}

	public void moveLeft(Map m) {
		x -= speed;
		boolean b = false;
		RectBox col[] = getColLeft(m);
		for (int i = 0; i < 3; i++) {
			if (rect.intersects(col[i])) {
				x += (col[i].x + col[i].width) - x;
				b = true;
			}
		}
		if (b) {
			makeChoice = true;
			choice = 5;
		}
		state = 3;
		walking = true;
	}

	public void moveRight(Map m) {
		x += speed;
		boolean b = false;
		RectBox col[] = getColRight(m);
		for (int i = 0; i < 3; i++) {
			if (rect.intersects(col[i])) {
				x -= (x + width) - col[i].x;
				b = true;
			}
		}
		if (b) {
			makeChoice = true;
			choice = 5;
		}
		state = 1;
		walking = true;
	}

	public void drop(Vector<Item> items) {
		items.add(new Life(x, y));
	}
}
