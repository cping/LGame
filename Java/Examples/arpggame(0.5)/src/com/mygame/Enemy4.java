package com.mygame;

import java.util.Vector;

import loon.geom.RectBox;

public class Enemy4 extends Enemy {

	Enemy4(float x, float y) {
		MAX_FRAMES = (new int[] { 2, 2, 2, 2, 2, 2 });
		MAX_FRAME_DELAY = (new int[] { 20, 20, 20, 20, 10, 10 });
		for (int i = 0; i < 3; i++)
			if ((int) (Math.random() * 2D) > 0)
				choice++;

		this.x = x;
		this.y = y;
		hp = 8;
		timeToAct = 0;
		height = 52;
		width = 32;
		speed = 0.5f;
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

	public void update(Map m, Player player, Vector<?> items,
			Bullet eBullets[], int MAX_BULLETS, ChargeShot eShot[],
			int MAX_CHARGESHOTS, Vector<?> things) {
		if (state != STATE_DEATH) {
			if (state != STATE_DYING) {
				if (shotDelay >= 0)
					shotDelay--;
				if (stopDelay >= 0)
					stopDelay--;
				if (actionDelay >= 0)
					actionDelay--;
				if (isSeeing(player))
					aggro = true;
				if (!aggro) {
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
				} else {
					speed = 3f;
					if (stopDelay <= 0) {
						if (player.x > x)
							state = 1;
						else
							state = 3;
						if (player.y > y)
							state = 2;
						else if (player.y < y)
							state = 0;
						if (state == 0) {
							int dist = (int) (y - player.y);
							if (dist < 150) {
								if (player.x < x)
									moveLeft2(m);
								if (player.x > x)
									moveRight2(m);
								moveDown2(m);
							} else {
								if (actionDelay <= 0) {
									if (player.x < x)
										moveLeft2(m);
									if (player.x > x)
										moveRight2(m);
								}
								if (player.x > x - 10D && player.x < x + 10D
										&& !actionDelaySet) {
									actionDelay = 20;
									actionDelaySet = true;
								}
								if (shotDelay <= 0 && actionDelay <= 0
										&& actionDelaySet) {

									for (int i = 0; i < MAX_BULLETS; i++)
										if (eBullets[i].dead) {
											eBullets[i].resetBullet(
													(float) (x + (width / 2)),
													(float) (y + (height / 2)),
													0);
											shotDelay = 100;
											stopDelay = 100;
											actionDelaySet = false;

										}

								}
							}
						} else if (state == 1) {
							int dist = (int) (player.x - x);
							if (dist < 150) {
								if (player.y < y)
									moveUp2(m);
								if (player.y > y)
									moveDown2(m);
								moveLeft2(m);
							} else {
								if (actionDelay <= 0) {
									if (player.y < y)
										moveUp2(m);
									if (player.y > y)
										moveDown2(m);
								}
								if (player.y > y - 10D && player.y < y + 10D
										&& !actionDelaySet) {
									actionDelay = 20;
									actionDelaySet = true;
								}
								if (shotDelay <= 0 && actionDelay <= 0
										&& actionDelaySet) {

									for (int i = 0; i < MAX_BULLETS; i++)
										if (eBullets[i].dead) {
											eBullets[i].resetBullet(
													(float) (x + (width / 2)),
													(float) (y + (height / 2)),
													1);
											shotDelay = 100;
											stopDelay = 100;
											actionDelaySet = false;

										}

								}
							}
						} else if (state == 2) {
							int dist = (int) (player.y - y);
							if (dist < 150) {
								if (player.x < x)
									moveLeft2(m);
								if (player.x > x)
									moveRight2(m);
								moveUp2(m);
							} else {
								if (actionDelay <= 0) {
									if (player.x < x)
										moveLeft2(m);
									if (player.x > x)
										moveRight2(m);
								}
								if (player.x > x - 10D && player.x < x + 10D
										&& !actionDelaySet) {
									actionDelay = 20;
									actionDelaySet = true;
								}
								if (shotDelay <= 0 && actionDelay <= 0
										&& actionDelaySet) {

									for (int i = 0; i < MAX_BULLETS; i++)
										if (eBullets[i].dead) {
											eBullets[i].resetBullet(
													(float) (x + (width / 2)),
													(float) (y + (height / 2)),
													2);
											shotDelay = 100;
											stopDelay = 100;
											actionDelaySet = false;

										}

								}
							}
						} else if (state == 3) {
							int dist = (int) (x - player.x);
							if (dist < 150) {
								if (player.y < y)
									moveUp2(m);
								if (player.y > y)
									moveDown2(m);
								moveRight2(m);
							} else {
								if (actionDelay <= 0) {
									if (player.y < y)
										moveUp2(m);
									if (player.y > y)
										moveDown2(m);
								}
								if (player.y > y - 10D && player.y < y + 10D
										&& !actionDelaySet) {
									actionDelay = 20;
									actionDelaySet = true;
								}
								if (shotDelay <= 0 && actionDelay <= 0
										&& actionDelaySet) {

									for (int i = 0; i < MAX_BULLETS; i++)
										if (eBullets[i].dead) {
											eBullets[i].resetBullet(
													(float) (x + (width / 2)),
													(float) (y + (height / 2)),
													1);
											shotDelay = 100;
											stopDelay = 100;
											actionDelaySet = false;

										}

								}
							}
						}
					}
				}
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
			rect.setBounds(x, y, width, height);
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
			rect.setBounds(x, y, width, height);
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
			rect.setBounds(x, y, width, height);
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

	public void moveUp2(Map m) {
		y -= speed;
		RectBox col[] = getColTop(m);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i]))
				y += (col[i].y + col[i].height) - y;
		}
		walking = true;
	}

	public void moveDown2(Map m) {
		y += speed;
		RectBox col[] = getColBottom(m);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i]))
				y -= (y + height) - col[i].y;
		}

		walking = true;
	}

	public void moveLeft2(Map m) {
		x -= speed;
		RectBox col[] = getColLeft(m);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i]))
				x += (col[i].x + col[i].width) - x;
		}

		walking = true;
	}

	public void moveRight2(Map m) {
		x += speed;
		RectBox col[] = getColRight(m);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i]))
				x -= (x + width) - col[i].x;
		}

		walking = true;
	}
}
