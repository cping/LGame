package com.mygame;

import java.util.Vector;

import loon.core.geom.RectBox;

public class Player {

	public static int MAX_FRAMES[] = { 3, 3, 3, 3, 6, 6, 6, 6 };
	public static int MAX_FRAME_DELAY[] = { 10, 10, 10, 10, 5, 5, 5, 5 };
	public static int MAX_WEAPON_SWITCH_DELAY = 30;
	public static int MAX_SWORD_DELAY = 15;
	public static int MAX_SWORD_PAUSE = 15;
	public static int STATE_BIRTH = 97;
	public static int STATE_NORMAL = 0;
	public static int STATE_DAMAGE = 98;
	public static int STATE_DEATH = 99;
	public static int START_OF_ODD_STATES = 96;
	public static int SWORD_HEIGHT = 4;
	public static int SWORD_WIDTH = 3;
	int height;
	int width;
	int flickerTime;
	int rollDelay;
	float x;
	float y;
	int state;
	int frame;
	int frameDelay;
	int swordPause;
	int swordDelay;
	int menuDelay;
	int shotDelay;
	int dashDelay;
	int grabDelay;
	float jumpDest;
	boolean walking;
	boolean attacking;
	boolean flicker;
	boolean dashing;
	boolean jumping;
	boolean rolling;
	boolean draggingUp;
	boolean draggingDown;
	boolean draggingRight;
	boolean draggingLeft;
	int bullets;
	int grabbedThing;
	boolean canGrab;
	String zMessage;
	float tempWorldX;
	float tempWorldY;
	int hp;
	int lives;
	int weapon;
	int weaponSwitchDelay;
	float speed;
	int swordLevel;
	float speedLevel;
	float dashLevel;
	int healthLevel;
	int gems;
	int keys;
	int swdInfo[][];
	boolean boss1killed;
	boolean hasSword;
	boolean hasHex1;
	boolean pickedUpSword;

	public Player() {
		height = 48;
		width = 48;
		flickerTime = 0;
		rollDelay = 0;
		x = 400 - width / 2;
		y = 300 - height / 2;
		state = 0;
		frame = 0;
		frameDelay = 0;
		swordPause = 0;
		swordDelay = 30;
		menuDelay = 30;
		shotDelay = 30;
		dashDelay = 0;
		grabDelay = 0;
		jumpDest = 0.0F;
		walking = false;
		attacking = false;
		flicker = false;
		dashing = false;
		jumping = false;
		rolling = false;
		draggingUp = false;
		draggingDown = false;
		draggingRight = false;
		draggingLeft = false;
		bullets = 30;
		grabbedThing = -1;
		canGrab = false;
		zMessage = "";
		tempWorldX = 0.0F;
		tempWorldY = 0.0F;
		hp = 3;
		lives = 3;
		weapon = 0;
		weaponSwitchDelay = 0;
		speed = 1.0F;
		swordLevel = 2;
		speedLevel = 2.0F;
		dashLevel = 8F;
		healthLevel = 3;
		gems = 0;
		keys = 0;
		boss1killed = false;
		hasSword = false;
		hasHex1 = false;
		pickedUpSword = false;
		swdInfo = new int[4][5];
		swdInfo[0][0] = 24;
		swdInfo[0][1] = -30;
		swdInfo[0][2] = 0;
		swdInfo[0][SWORD_WIDTH] = 18;
		swdInfo[0][SWORD_HEIGHT] = 36;
		swdInfo[2][0] = 24;
		swdInfo[2][1] = height - 6;
		swdInfo[2][2] = 180;
		swdInfo[2][SWORD_WIDTH] = 18;
		swdInfo[2][SWORD_HEIGHT] = 36;
		swdInfo[3][0] = -20;
		swdInfo[3][1] = 10;
		swdInfo[3][2] = 270;
		swdInfo[3][SWORD_WIDTH] = 36;
		swdInfo[3][SWORD_HEIGHT] = 18;
		swdInfo[1][0] = width + 10;
		swdInfo[1][1] = 10;
		swdInfo[1][2] = 90;
		swdInfo[1][SWORD_WIDTH] = 36;
		swdInfo[1][SWORD_HEIGHT] = 18;
	}

	public void updateStateAndFrame() {
		if (walking && !attacking || dashing || rolling) {
			if (frameDelay <= 0) {
				frame++;
				if (!dashing)
					frameDelay = MAX_FRAME_DELAY[state];
				else
					frameDelay = MAX_FRAME_DELAY[state] / 2;
			}
			if (frame >= MAX_FRAMES[state]) {
				if (rolling) {
					rolling = false;
					state -= 4;
					rollDelay = 30;
				}
				frame = 0;
			}
		} else if (attacking && !dashing)
			frame = 3;
		else
			frame = 0;
	}

	private RectBox rect = new RectBox();

	public void update(Map m, float worldX, float worldY, Vector<?> things) {
		tempWorldX = worldX;
		tempWorldY = worldY;
		if (frameDelay > 0)
			frameDelay--;
		if (swordDelay > 0)
			swordDelay--;
		if (rollDelay > 0)
			rollDelay--;
		if (dashDelay > 0)
			dashDelay--;
		if (shotDelay > 0)
			shotDelay--;
		if (swordPause > 0)
			swordPause--;
		if (grabDelay > 0)
			grabDelay--;
		if (swordPause <= 0 && attacking) {
			attacking = false;
			swordDelay = MAX_SWORD_DELAY;
		}
		if (weaponSwitchDelay > 0)
			weaponSwitchDelay--;
		if (flickerTime > 0) {
			flickerTime--;
			if (!flicker)
				flicker = true;
			else if (flicker)
				flicker = false;
		}
		boolean b = false;
		if (grabbedThing < 0) {
			for (int i = 0; i < things.size(); i++)
				if (((Thing) things.get(i)).moveable)
					if (state == 2) {
						rect.setBounds(x, y, width, height + 1);
						if (!b
								&& (rect.intersects(
										(int) ((Thing) things.get(i)).x,
										(int) ((Thing) things.get(i)).y,
										((Thing) things.get(i)).width,
										((Thing) things.get(i)).height))) {
							canGrab = true;
							zMessage = "Grab";
							b = true;
						}
					} else if (state == 1) {
						rect.setBounds(x, y, width + 1, height);
						if (!b
								&& (rect.intersects(((Thing) things.get(i)).x,
										(int) ((Thing) things.get(i)).y,
										((Thing) things.get(i)).width,
										((Thing) things.get(i)).height))) {
							canGrab = true;
							zMessage = "Grab";
							b = true;
						}
					} else if (state == 3) {
						rect.setBounds(x - 1, y, width, height);
						if (!b
								&& (rect.intersects(
										(int) ((Thing) things.get(i)).x,
										(int) ((Thing) things.get(i)).y,
										((Thing) things.get(i)).width,
										((Thing) things.get(i)).height))) {
							canGrab = true;
							zMessage = "Grab";
							b = true;
						}
					} else {
						rect.setBounds(x, y - 1, width, height);
						if (state == 0
								&& !b
								&& rect.intersects(
										(int) ((Thing) things.get(i)).x,
										(int) ((Thing) things.get(i)).y,
										((Thing) things.get(i)).width,
										((Thing) things.get(i)).height)) {
							canGrab = true;
							zMessage = "Grab";
							b = true;
						}
					}

		}
		if (!b) {
			canGrab = false;
			if (grabbedThing > 0)
				zMessage = "Release";
			else if (hasSword)
				zMessage = "Sword";
			else
				zMessage = "";
		}
		if (dashing) {
			speed = dashLevel;
			if (hp > 0)
				if (state == 0) {
					if (y < (m.height * m.tileHeight - 300)
							&& tempWorldY > 0.0F)
						tempWorldY = moveUp(m, tempWorldY, things);
					else
						moveUp(m, tempWorldY, things);
				} else if (state == 2) {
					if (y > 300F
							&& tempWorldY < (m.height * m.tileHeight - 600))
						tempWorldY = moveDown(m, tempWorldY, things);
					else
						moveDown(m, tempWorldY, things);
				} else if (state == 1) {
					if (x > 400F && tempWorldX < (m.width * m.tileWidth - 800))
						tempWorldX = moveRight(m, tempWorldX, things);
					else
						moveRight(m, tempWorldX, things);
				} else if (state == 3)
					if (x < (m.width * m.tileWidth - 400) && tempWorldX > 0.0F)
						tempWorldX = moveLeft(m, tempWorldX, things);
					else
						moveLeft(m, tempWorldX, things);
		} else if (jumping) {
			tempWorldY = jumpDown(m, tempWorldY);
			if (y >= jumpDest)
				jumping = false;
		} else if (rolling) {
			speed = dashLevel;
			if (state < 4)
				state += 4;
			if (hp > 0)
				if (state == 4) {
					if (y < (m.height * m.tileHeight - 300)
							&& tempWorldY > 0.0F)
						tempWorldY = rollUp(m, tempWorldY, things);
					else
						rollUp(m, tempWorldY, things);
				} else if (state == 6) {
					if (y > 300F
							&& tempWorldY < (m.height * m.tileHeight - 600))
						tempWorldY = rollDown(m, tempWorldY, things);
					else
						rollDown(m, tempWorldY, things);
				} else if (state == 5) {
					if (x > 400F && tempWorldX < (m.width * m.tileWidth - 800))
						tempWorldX = rollRight(m, tempWorldX, things);
					else
						rollRight(m, tempWorldX, things);
				} else if (state == 7)
					if (x < (m.width * m.tileWidth - 400) && tempWorldX > 0.0F)
						tempWorldX = rollLeft(m, tempWorldX, things);
					else
						rollLeft(m, tempWorldX, things);
		} else {
			speed = speedLevel;
		}
		updateStateAndFrame();
	}

	public float moveUp(Map m, float worldY, Vector<?> things) {
		y -= speed;
		worldY -= speed;
		RectBox col[];
		RectBox col2[];
		if (grabbedThing >= 0) {
			((Thing) things.get(grabbedThing)).y -= speed;
			col = getColY(m, -1);
			col2 = getColY(m, 0);
		} else {
			col = col2 = getColY(m, 0);
		}
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++)
			if (grabbedThing >= 0) {
				rect.setBounds((int) ((Thing) things.get(grabbedThing)).x,
						(int) ((Thing) things.get(grabbedThing)).y,
						((Thing) things.get(grabbedThing)).width,
						((Thing) things.get(grabbedThing)).height);
				if (rect.intersects(col[i])) {
					float dist = (col[i].y + col[i].height)
							- ((Thing) things.get(grabbedThing)).y;
					y += dist;
					worldY += speed;
					((Thing) things.get(grabbedThing)).y += dist;
				}
				rect.setBounds(x, y, width, height);
				if (rect.intersects(col2[i])) {
					float dist = (col2[i].y + col2[i].height) - y;
					y += dist;
					worldY += speed;
					((Thing) things.get(grabbedThing)).y += dist;
				}
			} else {
				rect.setBounds(x, y, width, height);
				if (rect.intersects(col[i])) {
					y += (col[i].y + col[i].height) - y;
					worldY += speed;
					dashing = false;
					dashDelay = 30;
				}
			}

		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (grabbedThing >= 0) {
				rect.setBounds((int) ((Thing) things.get(grabbedThing)).x,
						(int) ((Thing) things.get(grabbedThing)).y,
						((Thing) things.get(grabbedThing)).width,
						((Thing) things.get(grabbedThing)).height);
				if (i != grabbedThing && rect.intersects(thingCol[i])
						&& ((Thing) things.get(i)).solid) {
					float dist = (thingCol[i].y + thingCol[i].height)
							- ((Thing) things.get(grabbedThing)).y;
					y += dist;
					worldY += speed;
					((Thing) things.get(grabbedThing)).y += dist;
				}
				rect.setBounds(x, y, width, height);
				if (i != grabbedThing && rect.intersects(thingCol[i])
						&& ((Thing) things.get(i)).solid) {
					float dist = (thingCol[i].y + thingCol[i].height) - y;
					y += dist;
					worldY += speed;
					((Thing) things.get(grabbedThing)).y += dist;
				}
			} else if (i != grabbedThing && rect.intersects(thingCol[i]))
				if (((Thing) things.get(i)).solid) {
					if (((Thing) things.get(i)).getClass().toString()
							.contains("LockedBlock")
							&& keys > 0
							&& ((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						((Thing) things.get(i)).state = Thing.STATE_DEATH;
						keys--;
					} else if (((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						y += (thingCol[i].y + thingCol[i].height) - y;
						worldY += speed;
						dashing = false;
						dashDelay = 30;
					}
				} else {
					((Thing) things.get(i)).collision(this);
				}
		}

		if (grabbedThing < 0)
			state = 0;
		walking = true;
		return worldY;
	}

	public float moveDown(Map m, float worldY, Vector<?> things) {
		y += speed;
		worldY += speed;
		RectBox col[];
		RectBox col2[];
		if (grabbedThing >= 0) {
			((Thing) things.get(grabbedThing)).y += speed;
			col = getColY(m, 2);
			col2 = getColY(m, 1);
		} else {
			col = col2 = getColY(m, 1);
		}
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (grabbedThing >= 0) {
				rect.setBounds((int) ((Thing) things.get(grabbedThing)).x,
						(int) ((Thing) things.get(grabbedThing)).y,
						((Thing) things.get(grabbedThing)).width,
						((Thing) things.get(grabbedThing)).height);
				if (rect.intersects(col[i])) {
					float dist = (((Thing) things.get(grabbedThing)).y + ((Thing) things
							.get(grabbedThing)).height) - col[i].y;
					y -= dist;
					worldY -= speed;
					((Thing) things.get(grabbedThing)).y -= dist;
				}
				rect.setBounds(x, y, width, height);
				if (rect.intersects(col2[i])) {
					float dist = (y + height) - col2[i].y;
					y -= dist;
					worldY -= speed;
					((Thing) things.get(grabbedThing)).y -= dist;
				}
			} else if (rect.intersects(col[i])) {
				y -= (y + height) - col[i].y;
				worldY -= speed;
				dashing = false;
				dashDelay = 30;
			}
		}

		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (grabbedThing >= 0) {
				rect.setBounds((int) ((Thing) things.get(grabbedThing)).x,
						(int) ((Thing) things.get(grabbedThing)).y,
						((Thing) things.get(grabbedThing)).width,
						((Thing) things.get(grabbedThing)).height);
				if (i != grabbedThing && rect.intersects(thingCol[i])
						&& ((Thing) things.get(i)).solid) {
					float dist = (((Thing) things.get(grabbedThing)).y + ((Thing) things
							.get(grabbedThing)).height) - thingCol[i].y;
					y -= dist;
					worldY -= speed;
					((Thing) things.get(grabbedThing)).y -= dist;
				}
				if (i != grabbedThing
						&& (new RectBox((int) x, (int) y, width, height))
								.intersects(thingCol[i])
						&& ((Thing) things.get(i)).solid) {
					float dist = (y + height) - thingCol[i].y;
					y -= dist;
					worldY -= speed;
					((Thing) things.get(grabbedThing)).y -= dist;
				}
			} else if (i != grabbedThing && rect.intersects(thingCol[i]))
				if (((Thing) things.get(i)).solid) {
					if (((Thing) things.get(i)).getClass().toString()
							.contains("LockedBlock")
							&& keys > 0
							&& ((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						((Thing) things.get(i)).state = Thing.STATE_DEATH;
						keys--;
					} else if (((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						y -= (y + height) - thingCol[i].y;
						worldY -= speed;
						dashing = false;
						dashDelay = 30;
					}
				} else {
					((Thing) things.get(i)).collision(this);
				}
		}
		if (grabbedThing < 0)
			state = 2;
		walking = true;
		return worldY;
	}

	public float moveLeft(Map m, float worldX, Vector<?> things) {
		x -= speed;
		worldX -= speed;
		RectBox col[];
		RectBox col2[];
		if (grabbedThing >= 0) {
			((Thing) things.get(grabbedThing)).x -= speed;
			col = getColX(m, -1);
			col2 = getColX(m, 0);
		} else {
			col = col2 = getColX(m, 0);
		}
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (grabbedThing >= 0) {
				rect.setBounds((int) ((Thing) things.get(grabbedThing)).x,
						(int) ((Thing) things.get(grabbedThing)).y,
						((Thing) things.get(grabbedThing)).width,
						((Thing) things.get(grabbedThing)).height);
				if (rect.intersects(col[i])) {
					float dist = (col[i].x + col[i].width)
							- ((Thing) things.get(grabbedThing)).x;
					x += dist;
					worldX += speed;
					((Thing) things.get(grabbedThing)).x += dist;
				}
				if (rect.intersects(col2[i])) {
					float dist = (col2[i].x + col2[i].width) - x;
					x += dist;
					worldX += speed;
					((Thing) things.get(grabbedThing)).x += dist;
				}
			} else if (rect.intersects(col[i])) {
				x += (col[i].x + col[i].width) - x;
				worldX += speed;
				dashing = false;
				dashDelay = 30;
			}
		}
		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (grabbedThing >= 0) {
				rect.setBounds((int) ((Thing) things.get(grabbedThing)).x,
						(int) ((Thing) things.get(grabbedThing)).y,
						((Thing) things.get(grabbedThing)).width,
						((Thing) things.get(grabbedThing)).height);
				if (i != grabbedThing && rect.intersects(thingCol[i])
						&& ((Thing) things.get(i)).solid) {
					float dist = (thingCol[i].x + col[i].width)
							- ((Thing) things.get(grabbedThing)).x;
					x += dist;
					worldX += speed;
					((Thing) things.get(grabbedThing)).x += dist;
				}
				if (i != grabbedThing && rect.intersects(thingCol[i])
						&& ((Thing) things.get(i)).solid) {
					float dist = (thingCol[i].x + thingCol[i].width) - x;
					x += dist;
					worldX += speed;
					((Thing) things.get(grabbedThing)).x += dist;
				}
			} else if (i != grabbedThing && rect.intersects(thingCol[i]))
				if (((Thing) things.get(i)).solid) {
					if (((Thing) things.get(i)).getClass().toString()
							.contains("LockedBlock")
							&& keys > 0
							&& ((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						((Thing) things.get(i)).state = Thing.STATE_DEATH;
						keys--;
					} else if (((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						x += (thingCol[i].x + thingCol[i].width) - x;
						worldX += speed;
						dashing = false;
						dashDelay = 30;
					}
				} else {
					((Thing) things.get(i)).collision(this);
				}
		}
		if (grabbedThing < 0)
			state = 3;
		walking = true;
		return worldX;
	}

	public float moveRight(Map m, float worldX, Vector<?> things) {
		x += speed;
		worldX += speed;
		RectBox col[];
		RectBox col2[];
		if (grabbedThing >= 0) {
			((Thing) things.get(grabbedThing)).x += speed;
			col = getColX(m, 2);
			col2 = getColX(m, 1);
		} else {
			col = col2 = getColX(m, 1);
		}
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (grabbedThing >= 0) {
				rect.setBounds((int) ((Thing) things.get(grabbedThing)).x,
						(int) ((Thing) things.get(grabbedThing)).y,
						((Thing) things.get(grabbedThing)).width,
						((Thing) things.get(grabbedThing)).height);
				if (rect.intersects(col[i])) {
					float dist = (((Thing) things.get(grabbedThing)).x + ((Thing) things
							.get(grabbedThing)).width) - col[i].x;
					x -= dist;
					worldX -= speed;
					((Thing) things.get(grabbedThing)).x -= dist;
				}
				if (rect.intersects(col2[i])) {
					float dist = (x + width) - col2[i].x;
					x -= dist;
					worldX -= speed;
					((Thing) things.get(grabbedThing)).x -= dist;
				}
			} else if (rect.intersects(col[i])) {
				x -= (x + width) - col[i].x;
				worldX -= speed;
				dashing = false;
				dashDelay = 30;
			}
		}

		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (grabbedThing >= 0) {
				rect.setBounds((int) ((Thing) things.get(grabbedThing)).x,
						(int) ((Thing) things.get(grabbedThing)).y,
						((Thing) things.get(grabbedThing)).width,
						((Thing) things.get(grabbedThing)).height);
				if (i != grabbedThing && rect.intersects(thingCol[i])
						&& ((Thing) things.get(i)).solid) {
					float dist = (((Thing) things.get(grabbedThing)).x + ((Thing) things
							.get(grabbedThing)).width) - thingCol[i].x;
					x -= dist;
					worldX -= speed;
					((Thing) things.get(grabbedThing)).x -= dist;
				}
				if (i != grabbedThing && rect.intersects(thingCol[i])
						&& ((Thing) things.get(i)).solid) {
					float dist = (x + width) - thingCol[i].x;
					x -= dist;
					worldX -= speed;
					((Thing) things.get(grabbedThing)).x -= dist;
				}
			} else if (i != grabbedThing && rect.intersects(thingCol[i]))
				if (((Thing) things.get(i)).solid) {
					if (((Thing) things.get(i)).getClass().toString()
							.contains("LockedBlock")
							&& keys > 0
							&& ((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						((Thing) things.get(i)).state = Thing.STATE_DEATH;
						keys--;
					} else if (((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						x -= (x + width) - thingCol[i].x;
						worldX -= speed;
						dashing = false;
						dashDelay = 30;
					}
				} else {
					((Thing) things.get(i)).collision(this);
				}
		}

		if (grabbedThing < 0)
			state = 1;
		walking = true;
		return worldX;
	}

	public float rollUp(Map m, float worldY, Vector<?> things) {
		y -= speed;
		worldY -= speed;
		RectBox col[] = getColY(m, 0);
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				y += (col[i].y + col[i].height) - y;
				worldY += speed;
				rolling = false;
				state -= 4;
				rollDelay = 30;
			}
		}

		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(thingCol[i]))
				if (((Thing) things.get(i)).solid) {
					if (((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						y += (thingCol[i].y + thingCol[i].height) - y;
						worldY += speed;
						rolling = false;
						state -= 4;
						rollDelay = 30;
					}
				} else {
					((Thing) things.get(i)).collision(this);
				}
		}
		walking = true;
		return worldY;
	}

	public float rollDown(Map m, float worldY, Vector<?> things) {
		y += speed;
		worldY += speed;
		RectBox col[] = getColY(m, 1);
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				y -= (y + height) - col[i].y;
				worldY -= speed;
				rolling = false;
				state -= 4;
				rollDelay = 30;
			}
		}
		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(thingCol[i]))
				if (((Thing) things.get(i)).solid) {
					if (((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						y -= (y + height) - thingCol[i].y;
						worldY -= speed;
						rolling = false;
						state -= 4;
						rollDelay = 30;
					}
				} else {
					((Thing) things.get(i)).collision(this);
				}
		}

		walking = true;
		return worldY;
	}

	public float rollLeft(Map m, float worldX, Vector<?> things) {
		x -= speed;
		worldX -= speed;
		RectBox col[] = getColX(m, 0);
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				x += (col[i].x + col[i].width) - x;
				worldX += speed;
				rolling = false;
				state -= 4;
				rollDelay = 30;
			}
		}

		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(thingCol[i]))
				if (((Thing) things.get(i)).solid) {
					if (((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						x += (thingCol[i].x + thingCol[i].width) - x;
						worldX += speed;
						rolling = false;
						state -= 4;
						rollDelay = 30;
					}
				} else {
					((Thing) things.get(i)).collision(this);
				}
		}

		walking = true;
		return worldX;
	}

	public float rollRight(Map m, float worldX, Vector<?> things) {
		x += speed;
		worldX += speed;
		RectBox col[] = getColX(m, 1);
		RectBox thingCol[] = getColThings(things);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				x -= (x + width) - col[i].x;
				worldX -= speed;
				rolling = false;
				state -= 4;
				rollDelay = 30;
			}
		}

		for (int i = 0; i < thingCol.length; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(thingCol[i]))
				if (((Thing) things.get(i)).solid) {
					if (((Thing) things.get(i)).state != Thing.STATE_DEATH) {
						x -= (x + width) - thingCol[i].x;
						worldX -= speed;
						rolling = false;
						state -= 4;
						rollDelay = 30;
					}
				} else {
					((Thing) things.get(i)).collision(this);
				}
		}
		walking = true;
		return worldX;
	}

	public float jumpDown(Map m, float worldY) {
		y += 8F;
		worldY += 8F;
		dashing = false;
		state = 2;
		walking = true;
		return worldY;
	}

	public void knockback() {
		if (state == 0)
			y += 10F;
		else if (state == 1)
			x -= 10F;
		else if (state == 2)
			y -= 10F;
		else if (state == 3)
			x += 10F;
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

	public RectBox[] getColY(Map m, int yAdd) {
		RectBox col[] = new RectBox[3];
		int startX = (int) (x / m.tileWidth);
		int startY = (int) (y / m.tileWidth);
		for (int i = -1; i <= 1; i++)
			try {
				if (m.tileInfo[m.tiles[startX + i][startY + yAdd]] == 1)
					col[i + 1] = m.tileCol[startX + i][startY + yAdd];
				else
					col[i + 1] = new RectBox(0, 0, 0, 0);
			} catch (Exception e) {
				col[i + 1] = new RectBox(0, 0, 0, 0);
			}

		return col;
	}

	public RectBox[] getColX(Map m, int xAdd) {
		RectBox col[] = new RectBox[3];
		int startX = (int) (x / m.tileWidth);
		int startY = (int) (y / m.tileWidth);
		for (int i = -1; i <= 1; i++)
			try {
				if (m.tileInfo[m.tiles[startX + xAdd][startY + i]] == 1)
					col[i + 1] = m.tileCol[startX + xAdd][startY + i];
				else
					col[i + 1] = new RectBox(0, 0, 0, 0);
			} catch (Exception e) {
				col[i + 1] = new RectBox(0, 0, 0, 0);
			}

		return col;
	}

	public void swordDash(Vector<?> e, Vector<?> items) {
		for (int i = 0; i < e.size(); i++)
			if (((Enemy) e.get(i)).state != Enemy.STATE_DYING
					&& ((Enemy) e.get(i)).state != Enemy.STATE_DEATH
					&& ((Enemy) e.get(i)).flickerTime <= 0
					&& (x + swdInfo[state][0] + swdInfo[state][SWORD_WIDTH]) > ((Enemy) e
							.get(i)).x
					&& (x + swdInfo[state][0]) < ((Enemy) e.get(i)).x
							+ ((Enemy) e.get(i)).width
					&& (y + swdInfo[state][1] + swdInfo[state][SWORD_HEIGHT]) > ((Enemy) e
							.get(i)).y
					&& (y + swdInfo[state][1]) < ((Enemy) e.get(i)).y
							+ ((Enemy) e.get(i)).height) {
				((Enemy) e.get(i)).hp -= swordLevel * 2;
				((Enemy) e.get(i)).aggro = true;
				((Enemy) e.get(i)).flickerTime = 100;
				((Enemy) e.get(i)).knockback(state);
				if (((Enemy) e.get(i)).hp > 0) {

				} else {
					if (((Enemy) e.get(i)).getClass().toString()
							.contains("Boss1"))
						boss1killed = true;

				}
			}

	}

	public void swordSwing(Vector<?> e, Vector<?> items) {
		if (attacking) {
			for (int i = 0; i < e.size(); i++)
				if (((Enemy) e.get(i)).state != Enemy.STATE_DYING
						&& ((Enemy) e.get(i)).state != Enemy.STATE_DEATH
						&& ((Enemy) e.get(i)).flickerTime <= 0
						&& (x + swdInfo[state][0] + swdInfo[state][SWORD_WIDTH]) > ((Enemy) e
								.get(i)).x
						&& (x + swdInfo[state][0]) < ((Enemy) e.get(i)).x
								+ ((Enemy) e.get(i)).width
						&& (y + swdInfo[state][1] + swdInfo[state][SWORD_HEIGHT]) > ((Enemy) e
								.get(i)).y
						&& (y + swdInfo[state][1]) < ((Enemy) e.get(i)).y
								+ ((Enemy) e.get(i)).height
						&& !((Enemy) e.get(i)).getClass().toString()
								.contains("Enemy3")) {
					((Enemy) e.get(i)).hp -= swordLevel;
					((Enemy) e.get(i)).aggro = true;
					if (state == 0)
						((Enemy) e.get(i)).state = 2;
					else if (state == 1)
						((Enemy) e.get(i)).state = 3;
					else if (state == 2)
						((Enemy) e.get(i)).state = 0;
					else if (state == 3)
						((Enemy) e.get(i)).state = 1;
					((Enemy) e.get(i)).flickerTime = 50;
					((Enemy) e.get(i)).knockback(state);
					if (((Enemy) e.get(i)).hp > 0) {

					} else {
						if (((Enemy) e.get(i)).getClass().toString()
								.contains("Boss1"))
							boss1killed = true;

					}
				}

			swordDelay = 30;
		}
	}

}
