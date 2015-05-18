package com.mygame;

import java.util.Vector;

import loon.core.geom.RectBox;

public class Enemy1 extends Enemy {

	Enemy1(float x, float y) {
		MAX_FRAMES = (new int[] { 2, 2, 2, 2, 2, 2 });
		MAX_FRAME_DELAY = (new int[] { 20, 20, 20, 20, 10, 10 });
		for (int i = 0; i < 3; i++)
			if ((int) (Math.random() * 2D) > 0)
				choice++;

		this.x = x;
		this.y = y;
		hp = 4;
		timeToAct = 0;
		height = 52;
		width = 32;
		speed = 1f;
		rare = new BlueGem(x, y);
		visionInfo = new int[5][5];
		visionInfo[0][0] = 0;
		visionInfo[0][1] = -100;
		visionInfo[0][2] = 0;
		visionInfo[0][Enemy.VISION_WIDTH] = 32;
		visionInfo[0][Enemy.VISION_HEIGHT] = 100;
		visionInfo[2][0] = 0;
		visionInfo[2][1] = height;
		visionInfo[2][2] = 180;
		visionInfo[2][Enemy.VISION_WIDTH] = 32;
		visionInfo[2][Enemy.VISION_HEIGHT] = 100;
		visionInfo[3][0] = -100;
		visionInfo[3][1] = 0;
		visionInfo[3][2] = 270;
		visionInfo[3][Enemy.VISION_WIDTH] = 100;
		visionInfo[3][Enemy.VISION_HEIGHT] = 52;
		visionInfo[1][0] = width;
		visionInfo[1][1] = 0;
		visionInfo[1][2] = 90;
		visionInfo[1][Enemy.VISION_WIDTH] = 100;
		visionInfo[1][Enemy.VISION_HEIGHT] = 52;
		visionInfo[4][0] = 0;
		visionInfo[4][1] = 0;
		visionInfo[4][2] = 0;
		visionInfo[4][Enemy.VISION_WIDTH] = 0;
		visionInfo[4][Enemy.VISION_HEIGHT] = 0;
	}

	public void update(Map m, Player player, Vector<?> items, Bullet eBullets[],
			int MAX_BULLETS, ChargeShot eShot[], int MAX_CHARGESHOTS,
			Vector<?> things) {
		if (state != STATE_DEATH) {
			if (state != STATE_DYING) {
				if (timeToAct <= 0 && makeChoice) {
					choice = (int) (Math.random() * 4D);
					if (choice == 0)
						destY = y - height;
					else if (choice == 1)
						destX = x + width;
					else if (choice == 2)
						destY = y + height;
					else if (choice == 3)
						destX = x - width;
					timeToAct = 200;
					makeChoice = false;
				}
				if (choice == 0) {
					if (y > destY) {
						moveUp(m, things);
					} else {
						makeChoice = true;
						choice = 5;
					}
				} else if (choice == 1) {
					if (x < destX) {
						moveRight(m, things);
					} else {
						makeChoice = true;
						choice = 5;
					}
				} else if (choice == 2) {
					if (y < destY) {
						moveDown(m, things);
					} else {
						makeChoice = true;
						choice = 5;
					}
				} else if (choice == 3)
					if (x > destX) {
						moveLeft(m, things);
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

	public void moveUp(Map m, Vector<?> things) {
		y -= speed;
		boolean b = false;
		RectBox col[] = getColTop(m);
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				y += (col[i].y + col[i].height) - y;
				b = true;
			}
		}
		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(thingCol[i]) && ((Thing) things.get(i)).solid
					&& ((Thing) things.get(i)).state != Thing.STATE_DEATH) {
				y +=  (thingCol[i].y + thingCol[i].height) - y;
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

	public void moveDown(Map m, Vector<?> things) {
		y += speed;
		boolean b = false;
		RectBox col[] = getColBottom(m);
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++){
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				y -= (y + height) -  col[i].y;
				b = true;
			}
		}

		for (int i = 0; i < thingCol.length; i++){
			rect.setBounds(x, y, width, height);
			if (rect
					.intersects(thingCol[i])
					&& ((Thing) things.get(i)).solid
					&& ((Thing) things.get(i)).state != Thing.STATE_DEATH) {
				y -= (y + height) - thingCol[i].y;
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

	public void moveLeft(Map m, Vector<?> things) {
		x -= speed;
		boolean b = false;
		RectBox col[] = getColLeft(m);
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++){
			rect.setBounds(x, y, width, height);
			if (rect
					.intersects(col[i])) {
				x += (col[i].x + col[i].width) - x;
				b = true;
			}
		}

		for (int i = 0; i < thingCol.length; i++){
			rect.setBounds(x, y, width, height);
			if (rect
					.intersects(thingCol[i])
					&& ((Thing) things.get(i)).solid
					&& ((Thing) things.get(i)).state != Thing.STATE_DEATH) {
				x += (thingCol[i].x + thingCol[i].width) - x;
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

	public void moveRight(Map m, Vector<?> things) {
		x += speed;
		boolean b = false;
		RectBox col[] = getColRight(m);
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++){
			rect.setBounds(x, y, width, height);
			if (rect
					.intersects(col[i])) {
				x -= (x + width) - (double) col[i].x;
				b = true;
			}
		}

		for (int i = 0; i < thingCol.length; i++){
			rect.setBounds(x, y, width, height);
			if (rect
					.intersects(thingCol[i])
					&& ((Thing) things.get(i)).solid
					&& ((Thing) things.get(i)).state != Thing.STATE_DEATH) {
				x -= (x +  width) - thingCol[i].x;
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
}
