package com.mygame;

import java.io.*;
import java.util.Vector;

import loon.BaseIO;
import loon.EmulatorListener;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.canvas.LColor;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.SysKey;
import loon.geom.RectBox;
import loon.utils.ArrayByte;
import loon.utils.timer.GameTime;


public class Game1 extends DrawableScreen implements EmulatorListener{

	public final int MAX_BULLETS = 20;
	public final int MAX_CHARGESHOTS = 5;
	int maxEnemies;
	int maxLevels;
	int maxThings;
	int maxMaps;
	int level;
	int curBGM;
	int bgmInfo[];
	float worldX;
	float worldY;
	float fogX;
	float fogSpeed;
	boolean fogGoingRight;
	boolean titleScreen;
	boolean win;
	boolean levelClear;
	boolean menuDelayPolled;
	Player player;
	Vector<Item> items;
	Bullet bullets[];
	ChargeShot shots[];
	Bullet eBullets[][];
	ChargeShot eShots[][];
	LTexture tileMap;
	LTexture playerImage;
	LTexture deadPlayerImage;
	LTexture rollPlayerImage;
	LTexture swordImage;
	LTexture swordImage2;
	LTexture enemy1Image;
	LTexture enemy2Image;
	LTexture enemy3Image;
	LTexture enemy4Image;
	LTexture boss1Image;
	LTexture health;
	LTexture eHealth;
	LTexture gameoverImage;
	LTexture titleImage;
	LTexture winImage;
	LTexture levelClearImage;
	LTexture greenGemImage;
	LTexture blueGemImage;
	LTexture hud;
	LTexture hudGem;
	LTexture hudHealth;
	LTexture hudBullet;
	LTexture neblockImage;
	LTexture bossblock1Image;
	LTexture swordBlockImage;
	LTexture blockImage;
	LTexture moveableBlockImage;
	LTexture lockedBlockImage;
	LTexture bulletImage;
	LTexture chargeShotImage;
	LTexture eBulletImage;
	LTexture eChargeShotImage;
	LTexture ammo15Image;
	LTexture mapSwordImage;
	LTexture hexContainerImage;
	LTexture playerGet;
	LTexture swordGet;
	LTexture zButtonImage;
	LTexture keyImage;
	LTexture stepSwitchImage;

	Vector<Level> levels;

	public Game1() {
		LSystem.EMULATOR_BUTTIN_SCALE = 1.5f;
		maxEnemies = 0;
		maxLevels = 4;
		maxThings = 0;
		maxMaps = 3;
		level = 0;
		curBGM = 0;
		worldX = 0.0F;
		worldY = 0.0F;
		fogX = 0.0F;
		fogSpeed = 0.5F;
		fogGoingRight = false;
		titleScreen = true;
		win = false;
		levelClear = false;
		menuDelayPolled = false;
	}

	public int enemiesLeft() {
		return ((Level) levels.get(level)).enemies.size();
	}

	public void reset() {
		level = 0;
		player.hp = player.healthLevel;
		player.x = 400F;
		player.y = 300F;
		player.dashing = false;
		player.rolling = false;
		player.state = 0;
		player.frame = 0;
		player.attacking = false;
		worldX = 0.0F;
		worldY = 0.0F;
		levelClear = false;
		menuDelayPolled = false;
		player.swordDelay = 30;
	}

	public void updateBGM() {

	}
	
	

	public Level readLevel(int level) {
		try {
			Level l = new Level();
			System.out.println((new StringBuilder("map: ")).append(level)
					.toString());
			ObjectInputStream y = new ObjectInputStream(
					Game1.class.getClassLoader().getResourceAsStream(
							new StringBuilder("assets/level/map")
									.append(level).append(".lv").toString()));
			l.map.width = y.readInt();
			l.map.height = y.readInt();
			l.map.resize();
			if (worldX < 0.0F)
				worldX = 0.0F;
			else if (worldX > (float) (l.map.width * l.map.tileWidth - 800))
				worldX = l.map.width * l.map.tileWidth - 800;
			else if (worldY < 0.0F)
				worldY = 0.0F;
			else if (worldY > (float) (l.map.height * l.map.tileHeight - 600))
				worldY = l.map.height * l.map.tileHeight - 600;
			for (int j = 0; j < l.map.height; j++) {
				for (int i = 0; i < l.map.width; i++) {
					l.map.tiles[i][j] = y.readInt();
					System.out.print((new StringBuilder(" "))
							.append(l.map.tiles[i][j]).append(",").toString());
				}

			}

			l.map.setCol();
			maxEnemies = y.readInt();
			for (int i = 0; i < maxEnemies; i++) {
				float tempX = y.readInt();
				float tempY = y.readInt();
				float tempType = y.readInt();
				if (tempType == 0.0F)
					l.enemies.add(new Enemy1(tempX * (float) l.map.tileWidth,
							tempY * (float) l.map.tileHeight));
				if (tempType == 1.0F)
					l.enemies.add(new Enemy2(tempX * (float) l.map.tileWidth,
							tempY * (float) l.map.tileHeight));
				if (tempType == 2.0F && !player.boss1killed)
					l.enemies.add(new Boss1(tempX * (float) l.map.tileWidth,
							tempY * (float) l.map.tileHeight));
				if (tempType == 3F)
					l.enemies.add(new Enemy3(tempX * (float) l.map.tileWidth,
							tempY * (float) l.map.tileHeight));
			}

			maxThings = y.readInt();
			for (int i = 0; i < maxThings; i++) {
				float tempX = y.readInt();
				float tempY = y.readInt();
				float tempType = y.readInt();
				if (tempType == 0.0F)
					l.things.add(new NEBlock(tempX * (float) l.map.tileWidth,
							tempY * (float) l.map.tileHeight));
				if (tempType == 1.0F && !player.boss1killed)
					l.things.add(new BossBlock1(
							tempX * (float) l.map.tileWidth, tempY
									* (float) l.map.tileHeight));
				if (tempType == 2.0F && !player.hasSword)
					l.things.add(new Sword1(tempX * (float) l.map.tileWidth,
							tempY * (float) l.map.tileHeight));
				if (tempType == 3F && !player.hasSword)
					l.things.add(new SwordBlock(
							tempX * (float) l.map.tileWidth, tempY
									* (float) l.map.tileHeight));
				if (tempType == 4F && !player.hasHex1)
					l.things.add(new HexContainer(tempX
							* (float) l.map.tileWidth, tempY
							* (float) l.map.tileHeight));
				if (tempType == 5F)
					l.things.add(new GrabBlock1(
							tempX * (float) l.map.tileWidth, tempY
									* (float) l.map.tileHeight));
				if (tempType == 6F)
					l.things.add(new NormalBlock1(tempX
							* (float) l.map.tileWidth, tempY
							* (float) l.map.tileHeight));
				if (tempType == 7F)
					l.things.add(new KeyPlayer(tempX * (float) l.map.tileWidth,
							tempY * (float) l.map.tileHeight));
				if (tempType == 8F)
					l.things.add(new LockedBlock(tempX
							* (float) l.map.tileWidth, tempY
							* (float) l.map.tileHeight));
			}

			int maxSwitches = y.readInt();
			for (int i = 0; i < maxSwitches; i++) {
				float tempX = y.readInt();
				float tempY = y.readInt();
				float tempType = y.readInt();
				float blockNum = y.readInt();
				Vector<Thing> tempBlocks = new Vector<Thing>();
				for (int j = 0; j < blockNum; j++) {
					float blockX = y.readInt();
					float blockY = y.readInt();
					int blockType = y.readInt();
					if (blockType == 6)
						tempBlocks.add(new NormalBlock1(blockX
								* (float) l.map.tileWidth, blockY
								* (float) l.map.tileHeight));
				}

				if (tempType == 0.0F)
					l.switches.add(new StepSwitch(
							(int) (tempX * (float) l.map.tileWidth),
							(int) (tempY * (float) l.map.tileHeight),
							tempBlocks));
			}

			System.out.print("\n");
			l.map.upExit = y.readInt();
			l.map.downExit = y.readInt();
			l.map.leftExit = y.readInt();
			l.map.rightExit = y.readInt();
			y.close();
			return l;
		}  catch (Exception e) {
			System.out.println("Exception");
			e.getCause();
			e.printStackTrace();
			return null;
		}
	}

	private float lastWorldX, lastWorldY;

	private int levelNo;

	private boolean ditry = true;

	@Override
	public void draw(SpriteBatch batch) {
		if(!isOnLoadComplete()){
			return;
		}
		int tempTile = 0;
		int tempHeight = 0;
		Level lev = (Level) levels.get(level);
		if (levelNo != level || lastWorldX != worldX || lastWorldY != worldY
				|| ditry) {
			tileMap.glBegin();
			for (int i = 0; i < lev.map.width; i++) {
				for (int j = 0; j < lev.map.height; j++) {
					tempTile = lev.map.tiles[i][j];
					tempHeight = 0;
					if (tempTile >= tileMap.getWidth() / lev.map.tileWidth) {
						for (; tempTile >= tileMap.getWidth()
								/ lev.map.tileWidth;) {
							tempTile -= tileMap.getWidth() / lev.map.tileWidth;
							tempHeight++;
						}
					}
					float x = (i * (lev).map.tileWidth) - worldX;
					float y = (j * (lev).map.tileHeight) - worldY;
					tileMap.drawEmbedded(x, y,
							(i * (lev).map.tileWidth + lev.map.tileWidth)
									- worldX,
							(j * (lev).map.tileHeight + lev.map.tileHeight)
									- worldY, tempTile * (lev).map.tileWidth,
							tempHeight * (lev).map.tileHeight,
							tempTile * (lev).map.tileWidth
									+ (lev).map.tileWidth, tempHeight
									* (lev).map.tileHeight
									+ (lev).map.tileHeight);
				}
			}
			tileMap.glEnd();
			tileMap.newBatchCache();
			ditry = false;
		} else {
			tileMap.glCacheCommit();
		}

		levelNo = level;
		lastWorldX = worldX;
		lastWorldY = worldY;

		stepSwitchImage.glBegin();
		for (int i = 0; i < (lev).switches.size(); i++) {
			if (((Switch) (lev).switches.get(i)).getClass().getSimpleName()
					.equalsIgnoreCase("StepSwitch")) {
				stepSwitchImage
						.drawEmbedded(
								((Switch) (lev).switches.get(i)).x - worldX,
								((Switch) (lev).switches.get(i)).y - worldY,
								(((Switch) (lev).switches.get(i)).x + ((Switch) ((Level) levels
										.get(level)).switches.get(i)).width)
										- worldX,
								(((Switch) (lev).switches.get(i)).y + ((Switch) ((Level) levels
										.get(level)).switches.get(i)).height)
										- worldY,
								(((Switch) (lev).switches.get(i)).active ? 1
										: 0)
										* ((Switch) ((Level) levels.get(level)).switches
												.get(i)).width,
								0.0F,
								(((Switch) (lev).switches.get(i)).active ? 1
										: 0)
										* ((Switch) ((Level) levels.get(level)).switches
												.get(i)).width
										+ ((Switch) ((Level) levels.get(level)).switches
												.get(i)).width, 50F);
			}
			blockImage.glBegin();
			for (int j = 0; j < ((Switch) ((Level) levels.get(level)).switches
					.get(i)).blocks.size(); j++) {
				if (((Switch) ((Level) levels.get(level)).switches.get(i)).blocks
						.get(j) != null
						&& ((Thing) ((Switch) ((Level) levels.get(level)).switches
								.get(i)).blocks.get(j)).state != Thing.STATE_DEATH
						&& ((Thing) ((Switch) ((Level) levels.get(level)).switches
								.get(i)).blocks.get(i)).getClass().toString()
								.contains("NormalBlock1"))
					blockImage
							.draw(((Thing) ((Switch) ((Level) levels.get(level)).switches
									.get(i)).blocks.get(j)).x - worldX,
									((Thing) ((Switch) ((Level) levels
											.get(level)).switches.get(i)).blocks
											.get(j)).y
											- worldY);
			}
			blockImage.glEnd();
		}
		stepSwitchImage.glEnd();

		for (int i = 0; i < ((Level) levels.get(level)).things.size(); i++)
			if (((Level) levels.get(level)).things.get(i) != null
					&& ((Thing) ((Level) levels.get(level)).things.get(i)).state != Thing.STATE_DEATH) {
				if (((Thing) ((Level) levels.get(level)).things.get(i))
						.getClass().toString().contains("NEBlock"))
					batch.draw(
							neblockImage,
							((Thing) ((Level) levels.get(level)).things.get(i)).x
									- worldX,
							((Thing) ((Level) levels.get(level)).things.get(i)).y
									- worldY);
				if (((Thing) ((Level) levels.get(level)).things.get(i))
						.getClass().toString().contains("BossBlock1"))
					batch.draw(
							bossblock1Image,
							((Thing) ((Level) levels.get(level)).things.get(i)).x
									- worldX,
							((Thing) ((Level) levels.get(level)).things.get(i)).y
									- worldY);
				if (((Thing) ((Level) levels.get(level)).things.get(i))
						.getClass().toString().contains("Sword1"))
					batch.draw(
							mapSwordImage,
							((Thing) ((Level) levels.get(level)).things.get(i)).x
									- worldX,
							((Thing) ((Level) levels.get(level)).things.get(i)).y
									- worldY);
				if (((Thing) ((Level) levels.get(level)).things.get(i))
						.getClass().toString().contains("SwordBlock"))
					batch.draw(
							swordBlockImage,
							((Thing) ((Level) levels.get(level)).things.get(i)).x
									- worldX,
							((Thing) ((Level) levels.get(level)).things.get(i)).y
									- worldY);
				if (((Thing) ((Level) levels.get(level)).things.get(i))
						.getClass().toString().contains("HexContainer"))
					batch.draw(
							hexContainerImage,
							((Thing) ((Level) levels.get(level)).things.get(i)).x
									- worldX,
							((Thing) ((Level) levels.get(level)).things.get(i)).y
									- worldY);
				if (((Thing) ((Level) levels.get(level)).things.get(i))
						.getClass().toString().contains("GrabBlock1"))
					batch.draw(
							moveableBlockImage,
							((Thing) ((Level) levels.get(level)).things.get(i)).x
									- worldX,
							((Thing) ((Level) levels.get(level)).things.get(i)).y
									- worldY);
				if (((Thing) ((Level) levels.get(level)).things.get(i))
						.getClass().toString().contains("SysKey"))
					batch.draw(
							keyImage,
							((Thing) ((Level) levels.get(level)).things.get(i)).x
									- worldX,
							((Thing) ((Level) levels.get(level)).things.get(i)).y
									- worldY);
				if (((Thing) ((Level) levels.get(level)).things.get(i))
						.getClass().toString().contains("LockedBlock"))
					batch.draw(
							lockedBlockImage,
							((Thing) ((Level) levels.get(level)).things.get(i)).x
									- worldX,
							((Thing) ((Level) levels.get(level)).things.get(i)).y
									- worldY);
			}

		if (!titleScreen && !win && !levelClear) {
			if (!player.pickedUpSword && !player.rolling && !player.flicker
					&& player.hp > 0) {

				batch.drawEmbedded(playerImage, player.x - worldX, player.y
						- worldY, (player.x + (float) player.width) - worldX,
						(player.y + (float) player.height) - worldY,
						player.frame * player.width, player.state
								* player.height, player.frame * player.width
								+ player.width, player.state * player.height
								+ player.height, LColor.white);
			} else if (player.pickedUpSword) {
				batch.draw(playerGet, player.x - worldX, player.y - worldY);
				batch.draw(mapSwordImage, player.x - worldX, player.y - worldY
						- (float) mapSwordImage.getHeight());
			} else if (player.rolling)
				batch.drawEmbedded(rollPlayerImage, player.x - worldX, player.y
						- worldY, (player.x + (float) player.width) - worldX,
						(player.y + (float) player.height) - worldY,
						player.frame * player.width, (player.state - 4)
								* player.height, player.frame * player.width
								+ player.width, (player.state - 4)
								* player.height + player.height, LColor.white);
			else if (player.hp <= 0)
				batch.draw(deadPlayerImage, player.x - worldX, player.y
						- worldY);
			for (int i = 0; i < ((Level) levels.get(level)).enemies.size(); i++)
				if (((Level) levels.get(level)).enemies.get(i) != null
						&& ((Enemy) ((Level) levels.get(level)).enemies.get(i)).state != Enemy.STATE_DEATH
						&& !((Enemy) ((Level) levels.get(level)).enemies.get(i)).flicker)
					if (((Enemy) ((Level) levels.get(level)).enemies.get(i))
							.getClass().toString().contains("Enemy1"))
						batch.drawEmbedded(
								enemy1Image,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x - worldX,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y - worldY,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).width)
										- worldX,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).height)
										- worldY,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height, LColor.white);
					else if (((Enemy) ((Level) levels.get(level)).enemies
							.get(i)).getClass().toString().contains("Enemy2"))
						batch.drawEmbedded(
								enemy2Image,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x - worldX,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y - worldY,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).width)
										- worldX,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).height)
										- worldY,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height, LColor.white);
					else if (((Enemy) ((Level) levels.get(level)).enemies
							.get(i)).getClass().toString().contains("Enemy3"))
						batch.drawEmbedded(
								enemy3Image,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x - worldX,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y - worldY,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).width)
										- worldX,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).height)
										- worldY,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height, LColor.white);
					else if (((Enemy) ((Level) levels.get(level)).enemies
							.get(i)).getClass().toString().contains("Enemy4"))
						batch.drawEmbedded(
								enemy4Image,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x - worldX,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y - worldY,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).width)
										- worldX,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).height)
										- worldY,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height, LColor.white);
					else if (((Enemy) ((Level) levels.get(level)).enemies
							.get(i)).getClass().toString().contains("Boss1"))
						batch.drawEmbedded(
								boss1Image,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x - worldX,
								(float) ((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y - worldY,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).x + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).width)
										- worldX,
								(float) (((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).y + (double) ((Enemy) ((Level) levels
										.get(level)).enemies.get(i)).height)
										- worldY,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).frame
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).width,
								((Enemy) ((Level) levels.get(level)).enemies
										.get(i)).state
										* ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height
										+ ((Enemy) ((Level) levels.get(level)).enemies
												.get(i)).height, LColor.white);

			for (int i = 0; i < 20; i++)
				if (bullets[i] != null && !bullets[i].dead) {
					batch.draw(bulletImage, bullets[i].x - worldX, bullets[i].y
							- worldY, bullets[i].angle);
				}

			for (int i = 0; i < 5; i++)
				if (shots[i] != null && !shots[i].dead) {
					batch.draw(chargeShotImage, shots[i].x - worldX, shots[i].y
							- worldY, shots[i].angle);
				}

			for (int i = 0; i < items.size(); i++)
				if (items.get(i) != null
						&& ((Item) items.get(i)).state != Item.STATE_DEATH
						&& !((Item) items.get(i)).flicker)
					if (((Item) items.get(i)).getClass().toString()
							.contains("Life"))
						batch.draw(health, (float) ((Item) items.get(i)).x
								- worldX, (float) ((Item) items.get(i)).y
								- worldY);
					else if (((Item) items.get(i)).getClass().toString()
							.contains("GreenGem"))
						batch.draw(greenGemImage,
								(float) ((Item) items.get(i)).x - worldX,
								(float) ((Item) items.get(i)).y - worldY);
					else if (((Item) items.get(i)).getClass().toString()
							.contains("BlueGem"))
						batch.draw(blueGemImage,
								(float) ((Item) items.get(i)).x - worldX,
								(float) ((Item) items.get(i)).y - worldY);
					else if (((Item) items.get(i)).getClass().toString()
							.contains("Ammo15"))
						batch.draw(ammo15Image, (float) ((Item) items.get(i)).x
								- worldX, (float) ((Item) items.get(i)).y
								- worldY);

			if (player.attacking || player.dashing)
				if (player.swordLevel == 1) {
					batch.draw(swordImage, (player.x - worldX)
							+ (float) player.swdInfo[player.state][0],
							(player.y - worldY)
									+ (float) player.swdInfo[player.state][1],
							player.swdInfo[player.state][2]);
				} else if (player.swordLevel == 2) {
					batch.draw(swordImage2, (player.x - worldX)
							+ (float) player.swdInfo[player.state][0],
							(player.y - worldY)
									+ (float) player.swdInfo[player.state][1],
							player.swdInfo[player.state][2]);
				}
			if (player.pickedUpSword)
				batch.draw(swordGet, 200F, 450F);
		}
		if (!titleScreen && !win && !levelClear) {
			batch.draw(hud, 0.0F, 0.0F);
			for (int i = 0; i < player.healthLevel; i++)
				batch.draw(eHealth, 15 + 30 * i, 20F);

			for (int i = 0; i < player.hp; i++)
				batch.draw(hudHealth, 15 + 30 * i, 20F);

			batch.draw(zButtonImage, 225F, 5F);

			if (player.hp <= 0)
				batch.draw(gameoverImage, 200F, 266F);
		} else if (titleScreen)
			batch.draw(titleImage, 136F, 206F);
		else if (levelClear)
			batch.draw(levelClearImage, 0.0F, 271F);
		else if (win)
			batch.draw(winImage, 263F, 271F);

	}

	@Override
	public void loadContent() {

		player = new Player();
		levels = new Vector<Level>();
		for (int i = 1; i <= maxMaps; i++)
			levels.add(readLevel(i));

		tileMap = LTextures.loadTexture("assets/graphics/tilemap.png");
		playerImage = LTextures.loadTexture("assets/graphics/player.png");
		deadPlayerImage = LTextures.loadTexture("assets/graphics/playerdead.png");
		playerGet = LTextures.loadTexture("assets/graphics/playerget.png");
		rollPlayerImage = LTextures.loadTexture("assets/graphics/playerroll.png");
		swordImage = LTextures.loadTexture("assets/graphics/sword.png");
		swordImage2 = LTextures.loadTexture("assets/graphics/sword2.png");
		enemy1Image = LTextures.loadTexture("assets/graphics/neoscout1.png");
		enemy2Image = LTextures.loadTexture("assets/graphics/alienzaku1.png");
		enemy3Image = LTextures.loadTexture("assets/graphics/hunter.png");
		enemy4Image = LTextures.loadTexture("assets/graphics/neoscout1.png");
		boss1Image = LTextures.loadTexture("assets/graphics/boss1.png");
		health = LTextures.loadTexture("assets/graphics/health.png");
		eHealth = LTextures.loadTexture("assets/graphics/ehealth.png");
		gameoverImage = LTextures.loadTexture("assets/graphics/gameover.png");
		titleImage = LTextures.loadTexture("assets/graphics/title.png");
		winImage = LTextures.loadTexture("assets/graphics/win.png");
		levelClearImage = LTextures.loadTexture("assets/graphics/levelclear.png");
		greenGemImage = LTextures.loadTexture("assets/graphics/greengem.png");
		blueGemImage = LTextures.loadTexture("assets/graphics/bluegem.png");
		hud = LTextures.loadTexture("assets/graphics/hud.png");
		hudGem = LTextures.loadTexture("assets/graphics/hudgem.png");
		hudHealth = LTextures.loadTexture("assets/graphics/hudhealth.png");
		hudBullet = LTextures.loadTexture("assets/graphics/hudbullet.png");
		neblockImage = LTextures.loadTexture("assets/graphics/neblock.png");
		bossblock1Image = LTextures.loadTexture("assets/graphics/bossblock1.png");
		blockImage = LTextures.loadTexture("assets/graphics/block.png");
		moveableBlockImage = LTextures.loadTexture("assets/graphics/moveblock.png");
		lockedBlockImage = LTextures.loadTexture("assets/graphics/lockedblock.png");
		bulletImage = LTextures.loadTexture("assets/graphics/bullet.png");
		chargeShotImage = LTextures.loadTexture("assets/graphics/chargeshot.png");
		eBulletImage = LTextures.loadTexture("assets/graphics/ebullet.png");
		eChargeShotImage = LTextures.loadTexture("assets/graphics/echargeshot.png");
		ammo15Image = LTextures.loadTexture("assets/graphics/ammo15.png");
		mapSwordImage = LTextures.loadTexture("assets/graphics/mapsword.png");
		swordBlockImage = LTextures.loadTexture("assets/graphics/swordblock.png");
		hexContainerImage = LTextures.loadTexture("assets/graphics/hexcontainer.png");
		swordGet = LTextures.loadTexture("assets/graphics/swordget.png");
		stepSwitchImage = LTextures.loadTexture("assets/graphics/stepswitch.png");
		zButtonImage = LTextures.loadTexture("assets/graphics/zbutton.png");
		keyImage = LTextures.loadTexture("assets/graphics/key.png");
		items = new Vector<Item>();
		bullets = new Bullet[20];
		for (int i = 0; i < 20; i++)
			bullets[i] = new Bullet(-1F, -1F, true);

		shots = new ChargeShot[5];
		for (int i = 0; i < 5; i++)
			shots[i] = new ChargeShot(-1F, -1F, true);

		bgmInfo = (new int[] { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 });

	}

	@Override
	public void unloadContent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drag(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(GameKey e) {
	}

	@Override
	public void released(GameKey e) {
		// TODO Auto-generated method stub

	}

	private RectBox rect = new RectBox();
	@Override
	public void update(GameTime gameTime) {
		if(!isOnLoadComplete()){
			return;
		}
		if (titleScreen)
			curBGM = 0;
		else if (win)
			curBGM = 0;
		else if (levelClear)
			curBGM = 0;
		else
			curBGM = bgmInfo[level];
		if ((levelClear || win) && !menuDelayPolled) {
			player.menuDelay = 30;
			menuDelayPolled = true;
		}
		if (player.menuDelay >= 0)
			player.menuDelay--;
		updateBGM();
		if (isKeyPressed(SysKey.Z)) {
			if (!titleScreen && !win && !levelClear) {
				boolean b = false;
				if (player.canGrab && player.grabbedThing < 0) {
					for (int i = 0; i < ((Level) levels.get(level)).things
							.size(); i++)
						if (player.state == 2 && player.grabDelay <= 0) {
							rect.setBounds((int) player.x,
									(int) player.y, player.width,
									player.height + 1);
							if (!b
									&& rect
											.intersects(
													(int) ((Thing) ((Level) levels
															.get(level)).things
															.get(i)).x,
													(int) ((Thing) ((Level) levels
															.get(level)).things
															.get(i)).y,
													((Thing) ((Level) levels
															.get(level)).things
															.get(i)).width,
													((Thing) ((Level) levels
															.get(level)).things
															.get(i)).height)) {
								player.grabbedThing = i;
								player.draggingUp = true;
								player.grabDelay = 30;
								b = true;
							}
						} else if (player.state == 0 && player.grabDelay <= 0) {
							rect.setBounds((int) player.x,
									(int) player.y - 1, player.width,
									player.height);
							if (!b
									&& rect
											.intersects(
													(int) ((Thing) ((Level) levels
															.get(level)).things
															.get(i)).x,
													(int) ((Thing) ((Level) levels
															.get(level)).things
															.get(i)).y,
													((Thing) ((Level) levels
															.get(level)).things
															.get(i)).width,
													((Thing) ((Level) levels
															.get(level)).things
															.get(i)).height)) {
								player.grabbedThing = i;
								player.draggingDown = true;
								player.grabDelay = 30;
								b = true;
							}
						} else if (player.state == 1 && player.grabDelay <= 0) {
							rect.setBounds((int) player.x,
									(int) player.y, player.width + 1,
									player.height);
							if (!b
									&& (rect
											.intersects(
													(int) ((Thing) ((Level) levels
															.get(level)).things
															.get(i)).x,
													(int) ((Thing) ((Level) levels
															.get(level)).things
															.get(i)).y,
													((Thing) ((Level) levels
															.get(level)).things
															.get(i)).width,
													((Thing) ((Level) levels
															.get(level)).things
															.get(i)).height))) {
								player.grabbedThing = i;
								player.draggingRight = true;
								player.grabDelay = 30;
								b = true;
							}
						} else if (player.state == 3
								&& player.grabDelay <= 0
								&& !b
								&& (new RectBox((int) player.x - 1,
										(int) player.y, player.width,
										player.height))
										.intersects(
												(int) ((Thing) ((Level) levels
														.get(level)).things
														.get(i)).x,
												(int) ((Thing) ((Level) levels
														.get(level)).things
														.get(i)).y,
												((Thing) ((Level) levels
														.get(level)).things
														.get(i)).width,
												((Thing) ((Level) levels
														.get(level)).things
														.get(i)).height)) {
							player.grabbedThing = i;
							player.draggingRight = true;
							player.grabDelay = 30;
							b = true;
						}

				}
				if (player.draggingUp || player.draggingDown
						|| player.draggingRight || player.draggingLeft)
					b = true;
				if (player.hp <= 0 && player.menuDelay <= 0)
					reset();
				else if (player.draggingUp && player.grabDelay <= 0) {
					player.draggingUp = false;
					player.grabbedThing = -1;
					player.grabDelay = 30;
					player.swordDelay = Player.MAX_SWORD_DELAY;
					b = true;
				} else if (player.draggingDown && player.grabDelay <= 0) {
					player.draggingDown = false;
					player.grabbedThing = -1;
					player.grabDelay = 30;
					player.swordDelay = Player.MAX_SWORD_DELAY;
					b = true;
				} else if (player.draggingRight && player.grabDelay <= 0) {
					player.draggingRight = false;
					player.grabbedThing = -1;
					player.grabDelay = 30;
					player.swordDelay = Player.MAX_SWORD_DELAY;
					b = true;
				} else if (player.draggingLeft && player.grabDelay <= 0) {
					player.draggingLeft = false;
					player.grabbedThing = -1;
					player.grabDelay = 30;
					player.swordDelay = Player.MAX_SWORD_DELAY;
					b = true;
				} else if (!b)
					if (player.pickedUpSword) {
						player.pickedUpSword = false;
						player.swordDelay = Player.MAX_SWORD_DELAY;
					} else if (player.hasSword && !player.rolling
							&& !player.jumping && !player.dashing && !b
							&& player.swordDelay <= 0 && player.hp > 0
							&& !player.attacking && player.swordDelay <= 0) {
						player.attacking = true;

						player.swordPause = Player.MAX_SWORD_PAUSE;
						player.swordSwing(((Level) levels.get(level)).enemies,
								items);
					}
			}
		} else if (titleScreen && player.menuDelay <= 0)
			titleScreen = false;
		else if (levelClear && player.menuDelay <= 0) {
			level++;
			reset();
		} else if (win && player.menuDelay <= 0) {
			win = false;
			level = 1;
			reset();
		}
		if (!player.pickedUpSword && !player.rolling && !player.jumping
				&& !player.dashing && !player.attacking && player.hp > 0
				&& !titleScreen && !win && !levelClear) {
			if (isKeyPressed(SysKey.X) && player.hasSword && player.dashDelay <= 0) {
				player.dashing = true;

			}
			if (isKeyPressed(SysKey.C) && player.rollDelay <= 0)
				player.rolling = true;
			if (isKeyPressed(SysKey.A) && player.bullets > 0 && !player.rolling
					&& player.shotDelay <= 0) {
				boolean b = false;
				for (int i = 0; i < 20; i++)
					if (!b) {
						bullets[i]
								.resetBullet(
										player.x
												+ ((float) (player.width / 2) - bullets[i].width / 2.0F),
										player.y
												+ ((float) (player.height / 2) - bullets[i].height / 2.0F),
										player.state);
						player.shotDelay = 30;
						player.bullets--;
						b = true;
					}

			}

			if (isKeyPressed(SysKey.UP) && !player.draggingRight
					&& !player.draggingLeft) {
				Vector<Thing> temp = new Vector<Thing>();
				for (int i = 0; i < ((Level) levels.get(level)).things.size(); i++)
					temp.add((Thing) ((Level) levels.get(level)).things.get(i));

				for (int i = 0; i < ((Level) levels.get(level)).switches.size(); i++) {
					for (int j = 0; j < ((Switch) ((Level) levels.get(level)).switches
							.get(i)).blocks.size(); j++)
						temp.add((Thing) ((Switch) ((Level) levels.get(level)).switches
								.get(i)).blocks.get(j));

				}

				if (player.y < (float) (((Level) levels.get(level)).map.height
						* ((Level) levels.get(level)).map.tileHeight - 300)
						&& worldY > 0.0F)
					worldY = player.moveUp(((Level) levels.get(level)).map,
							worldY, temp);
				else
					player.moveUp(((Level) levels.get(level)).map, worldY, temp);
			}
			if (isKeyPressed(SysKey.DOWN) && !player.draggingRight
					&& !player.draggingLeft) {
				Vector<Thing> temp = new Vector<Thing>();
				for (int i = 0; i < ((Level) levels.get(level)).things.size(); i++)
					temp.add((Thing) ((Level) levels.get(level)).things.get(i));

				for (int i = 0; i < ((Level) levels.get(level)).switches.size(); i++) {
					for (int j = 0; j < ((Switch) ((Level) levels.get(level)).switches
							.get(i)).blocks.size(); j++)
						temp.add((Thing) ((Switch) ((Level) levels.get(level)).switches
								.get(i)).blocks.get(j));

				}

				if (player.y > 300F
						&& worldY < (float) (((Level) levels.get(level)).map.height
								* ((Level) levels.get(level)).map.tileHeight - 600))
					worldY = player.moveDown(((Level) levels.get(level)).map,
							worldY, temp);
				else
					player.moveDown(((Level) levels.get(level)).map, worldY,
							temp);
			}
			if (isKeyPressed(SysKey.LEFT) && !player.draggingUp
					&& !player.draggingDown) {
				Vector<Thing> temp = new Vector<Thing>();
				for (int i = 0; i < ((Level) levels.get(level)).things.size(); i++)
					temp.add((Thing) ((Level) levels.get(level)).things.get(i));

				for (int i = 0; i < ((Level) levels.get(level)).switches.size(); i++) {
					for (int j = 0; j < ((Switch) ((Level) levels.get(level)).switches
							.get(i)).blocks.size(); j++)
						temp.add((Thing) ((Switch) ((Level) levels.get(level)).switches
								.get(i)).blocks.get(j));

				}

				if (player.x < (float) (((Level) levels.get(level)).map.width
						* ((Level) levels.get(level)).map.tileWidth - 400)
						&& worldX > 0.0F)
					worldX = player.moveLeft(((Level) levels.get(level)).map,
							worldX, temp);
				else
					player.moveLeft(((Level) levels.get(level)).map, worldX,
							temp);
			}
			if (isKeyPressed(SysKey.RIGHT) && !player.draggingUp
					&& !player.draggingDown) {
				Vector<Thing> temp = new Vector<Thing>();
				for (int i = 0; i < ((Level) levels.get(level)).things.size(); i++)
					temp.add((Thing) ((Level) levels.get(level)).things.get(i));

				for (int i = 0; i < ((Level) levels.get(level)).switches.size(); i++) {
					for (int j = 0; j < ((Switch) ((Level) levels.get(level)).switches
							.get(i)).blocks.size(); j++)
						temp.add((Thing) ((Switch) ((Level) levels.get(level)).switches
								.get(i)).blocks.get(j));

				}
				if (player.x > 400F
						&& worldX < (float) (((Level) levels.get(level)).map.width
								* ((Level) levels.get(level)).map.tileWidth - 800))
					worldX = player.moveRight(((Level) levels.get(level)).map,
							worldX, temp);
				else
					player.moveRight(((Level) levels.get(level)).map, worldX,
							temp);
			} else if (!isKeyPressed(SysKey.UP) && !isKeyPressed(SysKey.DOWN)
					&& !isKeyPressed(SysKey.LEFT) && !isKeyPressed(SysKey.RIGHT)) {
				player.walking = false;
				if (player.state == Player.STATE_NORMAL)
					player.frame = 0;
			}
		}

		if (!titleScreen && !win) {
			Vector<Thing> temp = new Vector<Thing>();
			for (int i = 0; i < ((Level) levels.get(level)).things.size(); i++)
				temp.add((Thing) ((Level) levels.get(level)).things.get(i));

			for (int i = 0; i < ((Level) levels.get(level)).switches.size(); i++) {
				for (int j = 0; j < ((Switch) ((Level) levels.get(level)).switches
						.get(i)).blocks.size(); j++)
					temp.add((Thing) ((Switch) ((Level) levels.get(level)).switches
							.get(i)).blocks.get(j));

			}

			player.update(((Level) levels.get(level)).map, worldX, worldY, temp);
			worldX = player.tempWorldX;
			worldY = player.tempWorldY;
			if (!player.pickedUpSword) {
				if (player.dashing)
					player.swordDash(((Level) levels.get(level)).enemies, items);
				for (int i = 0; i < ((Level) levels.get(level)).enemies.size(); i++)
					if (((Level) levels.get(level)).enemies.get(i) != null
							&& ((Enemy) ((Level) levels.get(level)).enemies
									.get(i)).state != Enemy.STATE_DEATH) {
						((Enemy) ((Level) levels.get(level)).enemies.get(i))
								.update(((Level) levels.get(level)).map,
										player, items, null, 0, null, 0, temp);
					} else {
						((Enemy) ((Level) levels.get(level)).enemies.get(i)).rare.x = ((Enemy) ((Level) levels
								.get(level)).enemies.get(i)).x;
						((Enemy) ((Level) levels.get(level)).enemies.get(i)).rare.y = ((Enemy) ((Level) levels
								.get(level)).enemies.get(i)).y;
						((Enemy) ((Level) levels.get(level)).enemies.get(i))
								.drop(items);
						((Level) levels.get(level)).enemies.remove(i);
					}

				for (int i = 0; i < items.size(); i++)
					if (items.get(i) != null
							&& ((Item) items.get(i)).state != Item.STATE_DEATH)
						((Item) items.get(i)).update(player);

				for (int i = 0; i < 20; i++)
					if (bullets[i] != null && !bullets[i].dead)
						bullets[i].update(((Level) levels.get(level)).map,
								((Level) levels.get(level)).enemies);

				for (int i = 0; i < 5; i++)
					if (shots[i] != null && !shots[i].dead)
						shots[i].update(((Level) levels.get(level)).map,
								((Level) levels.get(level)).enemies, bullets,
								20);

			}
			for (int i = 0; i < ((Level) levels.get(level)).things.size(); i++)
				if (((Level) levels.get(level)).things.get(i) != null
						&& ((Thing) ((Level) levels.get(level)).things.get(i)).state != Thing.STATE_DEATH)
					((Thing) ((Level) levels.get(level)).things.get(i)).update(
							player, enemiesLeft());
				else if (((Thing) ((Level) levels.get(level)).things.get(i)).state == Thing.STATE_DEATH)
					((Level) levels.get(level)).things.remove(i);

			for (int i = 0; i < ((Level) levels.get(level)).switches.size(); i++) {
				((Switch) ((Level) levels.get(level)).switches.get(i)).update(
						player, ((Level) levels.get(level)).things);
				for (int j = 0; j < ((Switch) ((Level) levels.get(level)).switches
						.get(i)).blocks.size(); j++)
					if (((Switch) ((Level) levels.get(level)).switches.get(i)).blocks
							.get(j) != null
							&& ((Thing) ((Switch) ((Level) levels.get(level)).switches
									.get(i)).blocks.get(j)).state != Item.STATE_DEATH)
						((Thing) ((Switch) ((Level) levels.get(level)).switches
								.get(i)).blocks.get(j)).update(player,
								enemiesLeft());

			}

			if (player.y < 0.0F) {
				level = ((Level) levels.get(level)).map.upExit;
				worldY = ((Level) levels.get(level)).map.height
						* ((Level) levels.get(level)).map.tileHeight - 600;
				player.y = ((float) ((Level) levels.get(level)).map.height - 1.5F)
						* (float) ((Level) levels.get(level)).map.tileHeight;
			} else if (player.y > (float) (((Level) levels.get(level)).map.height * ((Level) levels
					.get(level)).map.tileHeight)) {
				level = ((Level) levels.get(level)).map.downExit;
				worldY = 0.0F;
				player.y = 1.5F * (float) ((Level) levels.get(level)).map.tileHeight;
			} else if (player.x < 0.0F) {
				level = ((Level) levels.get(level)).map.leftExit;
				worldX = ((Level) levels.get(level)).map.width
						* ((Level) levels.get(level)).map.tileWidth - 800;
				player.x = ((float) ((Level) levels.get(level)).map.width - 1.5F)
						* (float) ((Level) levels.get(level)).map.tileWidth;
			} else if (player.x > (float) (((Level) levels.get(level)).map.width * ((Level) levels
					.get(level)).map.tileWidth)) {
				level = ((Level) levels.get(level)).map.rightExit;
				worldX = 0.0F;
				player.x = 1.5F * (float) ((Level) levels.get(level)).map.tileWidth;
			}
		}

	}

	@Override
	public void onUpClick() {
		setKeyDown(SysKey.UP);
	}

	@Override
	public void onLeftClick() {
		setKeyDown(SysKey.LEFT);
	}

	@Override
	public void onRightClick() {
		setKeyDown(SysKey.RIGHT);
		
	}

	@Override
	public void onDownClick() {
		setKeyDown(SysKey.DOWN);
	}

	@Override
	public void onTriangleClick() {
		System.out.println("onTriangleClick");
		setKeyDown(SysKey.Z);
	}

	@Override
	public void onSquareClick() {
		System.out.println("onSquareClick");
		setKeyDown(SysKey.C);
	}

	@Override
	public void onCircleClick() {
		System.out.println("onCircleClick");
		setKeyDown(SysKey.A);
	}

	@Override
	public void onCancelClick() {
		System.out.println("onCancelClick");
		setKeyDown(SysKey.X);
	}

	@Override
	public void unUpClick() {
		setKeyUp(SysKey.UP);
	}

	@Override
	public void unLeftClick() {
		setKeyUp(SysKey.LEFT);
	}

	@Override
	public void unRightClick() {
		setKeyUp(SysKey.RIGHT);
	}

	@Override
	public void unDownClick() {
		setKeyUp(SysKey.DOWN);
	}

	@Override
	public void unTriangleClick() {
		setKeyUp(SysKey.Z);
	}

	@Override
	public void unSquareClick() {
		setKeyUp(SysKey.C);
		
	}

	@Override
	public void unCircleClick() {
		setKeyUp(SysKey.A);
		
	}

	@Override
	public void unCancelClick() {
		setKeyUp(SysKey.X);
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}