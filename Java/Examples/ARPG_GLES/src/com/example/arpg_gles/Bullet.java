package com.example.arpg_gles;

import java.util.Vector;

import loon.core.geom.RectBox;

public class Bullet {

	float x;
	float y;
	float angle;
	float speed;
	float height;
	float width;
	float decay;
	float time;
	int state;
	boolean dead;

	public Bullet() {
	}

	public Bullet(float x, float y) {
		time = 10F;
		height = 5F;
		width = 5F;
		speed = 15F;
		decay = 0.1F;
		this.x = x;
		this.y = y;
		dead = false;
	}

	public Bullet(float x, float y, boolean dead) {
		time = 1.0F;
		height = 5F;
		width = 5F;
		speed = 15F;
		decay = 0.03F;
		this.x = x;
		this.y = y;
		this.dead = dead;
	}

	public void resetBullet(float x, float y, int state) {
		time = 1.0F;
		this.x = x;
		this.y = y;
		this.state = state;
		dead = false;
		angle = state * 90;
	}

	public void update(Map m, Vector<?> enemies) {
		if (!dead) {
			if (state == 0)
				moveUp(m);
			else if (state == 1)
				moveRight(m);
			else if (state == 2)
				moveDown(m);
			else if (state == 3)
				moveLeft(m);
			collision(enemies);
		}
	}

	public void update(Map m, Player player) {
		if (!dead) {
			if (state == 0)
				moveUp(m);
			else if (state == 1)
				moveRight(m);
			else if (state == 2)
				moveDown(m);
			else if (state == 3)
				moveLeft(m);
			collision(player);
		}
	}

	public void collision(Vector<?> enemies) {
		for (int i = 0; i < enemies.size(); i++)
			if (((Enemy) enemies.get(i)).state != Enemy.STATE_DYING
					&& ((Enemy) enemies.get(i)).state != Enemy.STATE_DEATH
					&& ((Enemy) enemies.get(i)).flickerTime <= 0
					&& ((Enemy) enemies.get(i)).hp > 0
					&& (double) (x + width) > ((Enemy) enemies.get(i)).x
					&& (double) x < ((Enemy) enemies.get(i)).x
							+ (double) ((Enemy) enemies.get(i)).width
					&& (double) (y + height) > ((Enemy) enemies.get(i)).y
					&& (double) y < ((Enemy) enemies.get(i)).y
							+ (double) ((Enemy) enemies.get(i)).height) {
				((Enemy) enemies.get(i)).hp -= 2;
				((Enemy) enemies.get(i)).aggro = true;
				((Enemy) enemies.get(i)).flickerTime = 50;
				if (state == 0)
					((Enemy) enemies.get(i)).state = 2;
				else if (state == 1)
					((Enemy) enemies.get(i)).state = 3;
				else if (state == 2)
					((Enemy) enemies.get(i)).state = 0;
				else if (state == 3)
					((Enemy) enemies.get(i)).state = 1;
				dead = true;
				((Enemy) enemies.get(i)).knockback(state);

			}

	}

	public void collision(Player player) {
		if (player.state != Enemy.STATE_DYING
				&& player.state != Enemy.STATE_DEATH && player.flickerTime <= 0
				&& player.hp > 0 && x + width > player.x
				&& x < player.x + (float) player.width && y + height > player.y
				&& y < player.y + (float) player.height) {
			player.hp--;
			player.flickerTime = 50;
			dead = true;

		}
	}

	public void moveUp(Map m) {
		y -= speed;
		RectBox col[] = getColTop(m);
		for (int i = 0; i < 3; i++)
			if ((new RectBox((int) x, (int) y, (int) width, (int) height))
					.intersects(col[i]))
				dead = true;

	}

	private RectBox rect = new RectBox();

	public void moveDown(Map m) {
		y += speed;
		RectBox col[] = getColBottom(m);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				dead = true;
			}
		}

	}

	public void moveLeft(Map m) {
		x -= speed;
		RectBox col[] = getColLeft(m);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				dead = true;
			}
		}

	}

	public void moveRight(Map m) {
		x += speed;
		RectBox col[] = getColRight(m);
		for (int i = 0; i < 3; i++) {
			rect.setBounds(x, y, width, height);
			if (rect.intersects(col[i])) {
				dead = true;
			}
		}

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

}
