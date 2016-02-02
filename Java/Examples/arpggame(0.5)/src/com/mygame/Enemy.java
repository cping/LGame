package com.mygame;

import java.util.Vector;

import loon.geom.RectBox;
import loon.utils.MathUtils;

public class Enemy {

	public int MAX_FRAMES[];
	public int MAX_FRAME_DELAY[];
	public static int STATE_NORMAL = 0;
	public static int STATE_DYING = 4;
	public static int STATE_DEATH = 5;
	public static int START_OF_ODD_STATES = 4;
	public static int VISION_HEIGHT = 4;
	public static int VISION_WIDTH = 3;
	boolean walking;
	boolean flicker;
	int frameDelay;
	int flickerTime;
	int height;
	int width;
	float x;
	float y;
	float destX;
	float destY;
	int state;
	int frame;
	int hp;
	float speed;
	int timeToAct;
	int choice;
	boolean makeChoice;
	boolean droppedItem;
	boolean droppedRare;
	boolean aggro;
	int stopDelay;
	boolean actionDelaySet;
	int actionDelay;
	int visionInfo[][];
	int shotDelay;
	Item rare;

	public Enemy() {
		walking = false;
		flicker = false;
		frameDelay = 0;
		flickerTime = 0;
		height = 26;
		width = 16;
		x = 400 - width / 2;
		y = 300 - height / 2;
		destX = 0f;
		destY = 0f;
		state = 0;
		frame = 0;
		hp = 5;
		speed = 0.5f;
		timeToAct = 0;
		choice = 0;
		makeChoice = true;
		droppedItem = false;
		droppedRare = false;
		aggro = false;
		stopDelay = 0;
		actionDelaySet = false;
		actionDelay = 0;
		shotDelay = 0;
	}

	public void updateStateAndFrame() {
		if (frameDelay <= 0) {
			frame++;
			frameDelay = MAX_FRAME_DELAY[state];
		}
		if (frame >= MAX_FRAMES[state]) {
			if (state == STATE_DYING)
				state = STATE_DEATH;
			frame = 0;
		}
	}

	public void update(Map map, Player player1, Vector<?> vector,
			Bullet[] abullet, int i, ChargeShot[] achargeshot, int j,
			Vector<?> vector1) {
	}

	public void moveUp(Map m, Vector<?> things) {
		y -= speed;
		RectBox col[] = getColTop(m);
		RectBox thingCol[] = getColThings(things);
		RectBox rect = new RectBox();
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i]))
				y += (float) (col[i].y + col[i].height) - y;
		}
		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(thingCol[i]) && ((Thing) things.get(i)).solid
					&& ((Thing) things.get(i)).state != Thing.STATE_DEATH)
				y += (float) (thingCol[i].y + thingCol[i].height) - y;
		}

	}

	public void moveDown(Map m, Vector<?> things) {
		y += speed;
		RectBox col[] = getColBottom(m);
		RectBox thingCol[] = getColThings(things);
		RectBox rect = new RectBox();
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				y -= (y + (float) height) - (float) col[i].y;
			}
		}
		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(thingCol[i]) && ((Thing) things.get(i)).solid
					&& ((Thing) things.get(i)).state != Thing.STATE_DEATH)
				y -= (y + (float) height) - (float) thingCol[i].y;
		}

	}

	public void moveLeft(Map m, Vector<?> things) {
		x -= speed;
		RectBox col[] = getColLeft(m);
		RectBox thingCol[] = getColThings(things);
		RectBox rect = new RectBox();
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i]))
				x += (float) (col[i].x + col[i].width) - x;
		}

		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(thingCol[i]) && ((Thing) things.get(i)).solid
					&& ((Thing) things.get(i)).state != Thing.STATE_DEATH)
				x += (float) (thingCol[i].x + thingCol[i].width) - x;
		}

	}

	public void moveRight(Map m, Vector<?> things) {
		x += speed;
		RectBox col[] = getColRight(m);
		RectBox thingCol[] = getColThings(things);
		RectBox rect = new RectBox();
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i]))
				x -= (x + (float) width) - (float) col[i].x;
		}

		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(thingCol[i]) && ((Thing) things.get(i)).solid
					&& ((Thing) things.get(i)).state != Thing.STATE_DEATH)
				x -= (x + (float) width) - (float) thingCol[i].x;
		}

	}

	public RectBox[] getColThings(Vector<?> things) {
		RectBox col[] = new RectBox[things.size()];
		for (int i = 0; i < things.size(); i++)
			col[i] = new RectBox((int) ((Thing) things.get(i)).x,
					(int) ((Thing) things.get(i)).y,
					((Thing) things.get(i)).width,
					((Thing) things.get(i)).height);

		return col;
	}

	public RectBox[] getColTop(Map m) {
		RectBox col[] = new RectBox[3];
		int startX = (int) (x / (float) m.tileWidth);
		int startY = (int) (y / (float) m.tileWidth);
		for (int i = -1; i <= 1; i++)
			try {
				if (m.tileInfo[m.tiles[startX + i][startY]] == 1)
					col[i + 1] = m.tileCol[startX + i][startY];
				else
					col[i + 1] = new RectBox(0, 0, 0, 0);
			} catch (Exception e) {
				col[i + 1] = new RectBox(0, 0, 0, 0);
			}

		return col;
	}

	public RectBox[] getColBottom(Map m) {
		RectBox col[] = new RectBox[3];
		int startX = (int) (x / (float) m.tileWidth);
		int startY = (int) (y / (float) m.tileWidth);
		for (int i = -1; i <= 1; i++)
			try {
				if (m.tileInfo[m.tiles[startX + i][startY + 1]] == 1)
					col[i + 1] = m.tileCol[startX + i][startY + 1];
				else
					col[i + 1] = new RectBox(0, 0, 0, 0);
			} catch (Exception e) {
				col[i + 1] = new RectBox(0, 0, 0, 0);
			}

		return col;
	}

	public RectBox[] getColLeft(Map m) {
		RectBox col[] = new RectBox[3];
		int startX = (int) (x / (float) m.tileWidth);
		int startY = (int) (y / (float) m.tileWidth);
		for (int i = -1; i <= 1; i++)
			try {
				if (m.tileInfo[m.tiles[startX][startY + i]] == 1)
					col[i + 1] = m.tileCol[startX][startY + i];
				else
					col[i + 1] = new RectBox(0, 0, 0, 0);
			} catch (Exception e) {
				col[i + 1] = new RectBox(0, 0, 0, 0);
			}

		return col;
	}

	public RectBox[] getColRight(Map m) {
		RectBox col[] = new RectBox[3];
		int startX = (int) (x / (float) m.tileWidth);
		int startY = (int) (y / (float) m.tileWidth);
		for (int i = -1; i <= 1; i++)
			try {
				if (m.tileInfo[m.tiles[startX + 1][startY + i]] == 1)
					col[i + 1] = m.tileCol[startX + 1][startY + i];
				else
					col[i + 1] = new RectBox(0, 0, 0, 0);
			} catch (Exception e) {
				col[i + 1] = new RectBox(0, 0, 0, 0);
			}

		return col;
	}

	public void deathCheck() {
		if (hp <= 0 && state != STATE_DYING && state != STATE_DEATH) {
			frame = 0;
			state = STATE_DYING;
			flicker = false;
		}
	}

	public void knockback(int state) {
		if (state == 0)
			y -= 10D;
		else if (state == 1)
			x += 10D;
		else if (state == 2)
			y += 10D;
		else if (state == 3)
			x -= 10D;
	}

	public boolean isSeeing(Player player) {
		return state != STATE_DYING
				&& state != STATE_DEATH
				&& flickerTime <= 0
				&& x + (float) visionInfo[state][0]
						+ (float) visionInfo[state][VISION_WIDTH] > (float) player.x
				&& x + (float) visionInfo[state][0] < (float) (player.x + (float) player.width)
				&& y + (float) visionInfo[state][1]
						+ (float) visionInfo[state][VISION_HEIGHT] > (float) player.y
				&& y + (float) visionInfo[state][1] < (float) (player.y + (float) player.height);
	}

	public void drop(Vector<Item> items) {
		int num = (int) (Math.random() * 4f);
		if (num == 3) {
			items.add(rare);
			droppedRare = true;
		}
		if (!droppedRare) {
			num = (int) (MathUtils.random() * 2f);
			if (num == 1) {
				num = (int) (Math.random() * 2f);
				if (num == 0)
					items.add(new Life((float) x, (float) y));
				else if (num == 1)
					items.add(new GreenGem((float) x, (float) y));
			}
		}
	}

	public void collision(Player player) {
		if (state != STATE_DYING && state != STATE_DEATH && flickerTime <= 0
				&& player.flickerTime <= 0 && player.hp > 0
				&& x + (float) width > (float) player.x
				&& x < (float) (player.x + (float) player.width)
				&& y + (float) height > (float) player.y
				&& y < (float) (player.y + (float) player.height)) {
			player.hp--;
			player.flickerTime = 50;
			player.knockback();
			if (player.hp > 0) {

			} else {

				player.menuDelay = 30;
			}
		}
	}

}
