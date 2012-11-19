package org.test;

import java.io.DataInputStream;

import loon.core.resource.Resources;

public class Map {

	public byte[][] background;

	public byte[][] bomb;

	public byte[][] enemy;

	public int height;

	public byte[][] player;

	public byte[][] stone;

	public byte[][] treasure;

	public int width;

	public Map(int width, int height) {
		if ((width <= 0) || (height <= 0)) {
			throw new IllegalArgumentException();
		}
		this.background = new byte[width][height];
		this.stone = new byte[width][height];
		this.treasure = new byte[width][height];
		this.enemy = new byte[width][height];
		this.player = new byte[width][height];
		this.bomb = new byte[width][height];
		this.width = width;
		this.height = height;
	}

	public final java.util.ArrayList<Enemy> GetEnemys() {
		java.util.ArrayList<Enemy> list = new java.util.ArrayList<Enemy>();
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				if (this.enemy[i][j] > 0) {
					Enemy item = new Enemy();
					item.i = this.enemy[i][j];
					item.x = i * 0x20;
					item.y = j * 0x20;
					list.add(item);
				}
			}
		}
		return list;
	}

	public final Player GetPlayer() {
		Player player = new Player();
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				if (this.player[i][j] == 1) {
					player.x = i * 0x20;
					player.y = j * 0x20;
				}
			}
		}
		return player;
	}

	public final boolean Load(String filename) {
		try {
			DataInputStream reader = new DataInputStream(
					Resources
							.openResource("assets/Levels/" + filename + ".bin"));

			for (int i = 0; i < this.width; i++) {
				for (int num2 = 0; num2 < this.height; num2++) {
					this.background[i][num2] = reader.readByte();
				}
			}
			for (int j = 0; j < this.width; j++) {
				for (int num4 = 0; num4 < this.height; num4++) {
					this.stone[j][num4] = reader.readByte();
				}
			}
			for (int k = 0; k < this.width; k++) {
				for (int num6 = 0; num6 < this.height; num6++) {
					this.treasure[k][num6] = reader.readByte();
				}
			}
			for (int m = 0; m < this.width; m++) {
				for (int num8 = 0; num8 < this.height; num8++) {
					this.enemy[m][num8] = reader.readByte();
				}
			}
			for (int n = 0; n < this.width; n++) {
				for (int num10 = 0; num10 < this.height; num10++) {
					this.player[n][num10] = reader.readByte();
				}
			}
			for (int num11 = 0; num11 < this.width; num11++) {
				for (int num12 = 0; num12 < this.height; num12++) {
					this.bomb[num11][num12] = 0;
				}
			}
			if (reader != null) {
				reader.close();
				reader = null;
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}