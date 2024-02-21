package com.mygame;

import java.util.Collections;

import loon.utils.reply.ObjRef;

public class StateGame extends GameState {

	private int ambience;
	private float ambienceVolume;
	private float ambienceVolumeTarget;
	private Sprite animal;

	private Sprite bignumbers;
	private Button btnexit;
	private Sprite btnforward;
	private Button btnplay;
	private Button btnrestart;
	private Sprite btnshadow;

	private Sprite bush;

	private Sprite carriage;
	private Sprite carriageBlue;
	private Sprite carriageYellow;
	
	private java.util.ArrayList<Integer> caveActive = new java.util.ArrayList<Integer>();
	private Sprite cavebar;
	private Sprite cavebarround;
	private Sprite cavebartop;
	private int[] caveRoundOffsets = new int[] { 10, -13, 4, -7, 0x11, -12, 6,
			-3, 7, 8, 2, 15 };
	private java.util.ArrayList<Tile> caves = new java.util.ArrayList<Tile>();
	private Sprite coal;
	private Sprite dragon;
	private boolean editMode;
	private EntitySort esort = new EntitySort();
	private Sprite explosion;
	
	private Sprite explosiondebris;
	private boolean fastForward;
	private Sprite gamelabels;
	private int gametick;
	private Sprite getready;
	private boolean graphicsLoaded;
	private boolean ibackButtonPressed;
	private Sprite indicators;
	
	private Sprite lavabubble;
	private Sprite lavaflow;
	private Sprite lavasplash;
	private int leftCaves;
	private Sprite level;
	
	private int LEVEL_TILES_COLS;
	private int LEVEL_TILES_ROWS;
	private int levelEndAnimTicks;
	private boolean levelEnded;
	private int levelh;
	private int levelNum;
	private int levelSet;
	private int levelStartTicks;
	private int levelToEndTicks;
	private int levelw;
	private int levelx;
	private int levely;
	private Sprite mountainside;
	private Sprite mountaintop;

	private int nextCave;
	private int nextCaveChangeTick;
	private Sprite num;
	private Sprite numslash;
	
	private int[] particleTick = new int[100];
	private int[] particleType = new int[100];
	private int[] particleX = new int[100];
	private int[] particleY = new int[100];
	private Button pause;
	private boolean paused;
	private int rightCaves;
	private java.util.ArrayList<ScheduleItem> schedule = new java.util.ArrayList<ScheduleItem>();

	private int shouldDoLevelAnim;
	private boolean shouldDoPauseAfterLevelStart;
	private Sprite signs;
	private boolean skipLevel;
	private Sprite smoke;

	private boolean success;
	private int tileh;
	private java.util.ArrayList<Tile> tiles = new java.util.ArrayList<Tile>();
	private int tilew;
	private Sprite train;

	private Sprite trainBlue;
	private static int trainCarOffsetY;
	private static int trainCoalOffsetY;
	private static int trainOffsetY;
	private int[] trainPassed = new int[3];
	private int[] trainPassedChangeTick = new int[3];
	private java.util.ArrayList<Entity> trains = new java.util.ArrayList<Entity>();
	private int trainspeed;
	private int[] trainTargets = new int[3];
	private float trainVolume;
	private float trainVolumeTarget;
	private Sprite trainYellow;
	private Sprite trees;
	private int tutorialChangeTick;
	private Sprite tutorialcursor;
	private Sprite tutorialcursorshadow;
	private int tutorialId;
	private int tutorialPhase;
	private java.util.ArrayList<Tile> tutorialTiles = new java.util.ArrayList<Tile>();


	private void paintTutorial(Painter painter) {
		this.gametick++;
		int num = (this.tutorialId == 2) ? 280 : 200;
		int num2 = (this.tutorialId == 2) ? 140 : 60;
		int num3 = (this.tutorialId == 2) ? 0xed : 0x9d;
		int num4 = (this.tutorialId == 2) ? 0xcd : 0x7d;
		int num5 = this.gametick % num;
		int w = super.game.getW();
		int h = super.game.getH();
		int num8 = (this.tutorialId == 2) ? ((h / 2) - (this.tileh / 4))
				: (h / 2);
		int num9 = this.tilew / 10;

		for (int i = 0; i < this.tutorialTiles.size(); i++) {
			this.tutorialTiles.get(i).Paint(painter, 0);
		}
		if (this.tutorialId == 2) {
			this.paintCaveSign(painter, this.tutorialTiles.get(9), 2);
		} else {
			if (num5 == 10) {
				this.caveActive.set(0, this.gametick);
			}
			this.paintCaveSign(painter, this.tutorialTiles.get(6), 1);
		}
		boolean flag = this.tutorialId < 3;
		int tileh = this.tileh;
		int num12 = w / 2;
		int num13 = (w / 2) + ((this.tilew * 5) / 2);
		int num14 = (this.tilew * 3) / 8;
		int num15 = ((num5 - num2) - 5) - 0x16;
		int num16 = ((num4 - num2) - 5) - 0x16;
		if (num15 >= num16) {
			num12 = num13;
		} else if ((num15 > 0) && (num15 < num16)) {
			int num17 = num13 - num12;
			num12 += ((GameUtils.sin(((num15 * 180) / num16) - 90) + 0x2000) * num17) >> 14;
		}
		int num18 = num8 + (this.tileh / 2);
		int num19 = (this.tutorialId == 2) ? 180 : 100;
		int frame = (num5 < num19) ? 0 : 1;
		int num21 = (GameUtils.sin((num5 * 0x438) / 200) * this.tileh) >> 0x10;
		if ((num5 > (num2 - 5)) && (num5 < (num2 + 5))) {
			tileh -= (GameUtils.sin((((num5 - num2) + 5) * 180) / 10) * tileh) >> 13;
		}
		if (flag) {
			this.tutorialcursorshadow.Paint(painter,
					(float) (((num12 + tileh) + num21) - num14),
					(float) ((num18 - (tileh / 2)) + (this.tileh / 8)), frame);
		}
		if (this.tutorialId == 3) {
			int num22 = (w / 2) + ((this.tilew * 7) / 2);
			int num23 = (h / 2) - this.tileh;
			int num24 = this.tileh / 2;
			if (num5 > 0x5f) {
				if (num5 < 100) {
					num24 -= (((num5 - 100) + 5) * num24) / 5;
				} else {
					num24 = 0;
				}
			}
			this.btnforward.Paint(painter, (float) num22, (float) num23,
					(num24 == 0) ? 1 : 0);
			this.tutorialcursor.Paint(painter, (float) (num22 - num14),
					(float) (num23 - num24), 0);
		}
		int num25 = this.tilew * 10;
		int x = 0;
		if (this.tutorialId == 3) {
			if (num5 < 100) {
				x = ((w / 2) - (num25 / 2))
						+ ((num5 * ((num25 * 3) / 8)) / 100);
			} else {
				x = ((w / 2) - (num25 / 8))
						+ ((((num25 * 5) / 8) * (num5 - 100)) / 50);
			}
		} else {
			x = ((w / 2) - (num25 / 2)) + ((num5 * num25) / 200);
		}
		painter.setClip(((w / 2) - ((this.tilew * 5) / 2)) - (this.tilew / 3),
				(num8 - (this.tileh * 2)) - (this.tileh / 2), (this.tilew * 5)
						+ ((this.tilew * 2) / 3), (this.tileh * 5)
						+ (this.tileh / 2));
		this.paintTrain(painter, x, num8 + (this.tileh / 2), 0, 0, 0, false);
		if ((this.tutorialId == 2) && (num5 > 0x73)) {
			int num27 = this.tileh * 8;
			int num28 = (num8 - (this.tileh * 3))
					+ ((num27 * (num5 - 0x73)) / 150);
			this.paintTrain(painter, (w / 2) + 2, num28, 270, 0, 1, false);
		}
		painter.removeClip();
		for (int j = 0; j < this.tutorialTiles.size(); j++) {
			if (this.tutorialTiles.get(j).IsCave()) {
				this.tutorialTiles.get(j).Paint(painter, 1);
			}
		}
		if (this.tutorialId == 2) {
			switch (num5) {
			case 10:
				this.caveActive.set(1, this.gametick);
				break;

			case 50:
				this.caveActive.set(0, this.gametick);
				break;
			}
			this.paintCaveSign(painter, this.tutorialTiles.get(0), 0);
			this.paintCaveSign(painter, this.tutorialTiles.get(3), 1);
		} else {
			this.paintCaveSign(painter, this.tutorialTiles.get(0), 0);
		}
		if (this.tutorialId == 2) {
			this.paintCaveSign(painter, this.tutorialTiles.get(12), 3);
		}
		if (flag) {
			this.tutorialcursor.Paint(painter,
					(float) ((num12 + (tileh / 4)) - num14),
					(float) ((num18 - tileh) - num21), frame);
		}
		int num30 = 3;
		int num31 = (w - (this.btnplay.getW() / 2)) - num9;
		int y = (h - (this.btnplay.getH() / 2)) - num9;
		this.btnshadow.Paint(painter, (float) (num31 + num30),
				(float) (y + num30), 0);
		boolean flag2 = this.btnplay.paint(painter, super.game, num31, y);
		if ((this.tutorialId < 3) && ((num5 == num2) || (num5 == num3))) {
			this.tutorialTiles.get((this.tutorialId == 2) ? 6 : 3).rotate();
		}
		if (flag2) {
			this.tutorialId = -1;
			this.clearTutorial();
			super.game.clearMouseStatus();
			this.gametick = 0;
			this.caveActive.set(0, -1);
			this.tutorialPhase = 0;
			this.loadLevel(true);
			// super.game.getAudioEngine().setVolume(1f);
			super.game.doButtonPressSound();
		}
	}

	private void paintTutorialFingerLevel1(Painter painter) {
		int num = 0;
		int num2 = 0;
		int frame = 0;
		int num4 = (this.tilew * 3) / 8;
		int num5 = (this.levelx + (this.tilew * 3)) + (this.tilew / 2);
		int num6 = (this.levely + (this.tileh * 3)) + (this.tileh / 4);
		int num7 = (this.levelx + (this.tilew * 5)) + (this.tilew / 2);
		int num8 = (this.levely + this.tileh) + (this.tileh / 4);
		int num9 = (this.levelx + (this.tilew * 7)) + (this.tilew / 2);
		int num10 = this.levely + (this.tileh * 4);
		if (this.tutorialPhase == 0) {
			num = num5;
			num2 = num6;
		} else if (this.tutorialPhase == 1) {
			int num11 = (this.gametick - this.tutorialChangeTick) - 10;
			if (num11 < 15) {
				int num12 = num7 - num5;
				int num13 = num8 - num6;
				if (num11 > 0) {
					int num14 = GameUtils.sin(((num11 * 180) / 15) - 90) + 0x2000;
					num = num5 + ((num14 * num12) >> 14);
					num2 = num6 + ((num14 * num13) >> 14);
				} else {
					num = num5;
					num2 = num6;
				}
				frame = 1;
			} else {
				num = num7;
				num2 = num8;
			}
		} else if (this.tutorialPhase == 2) {
			frame = 1;
			num = num7;
			int num15 = this.gametick - this.tutorialChangeTick;
			if (num15 < 15) {
				int num16 = num9 - num7;
				int num17 = num10 - num8;
				int num18 = GameUtils.sin(((num15 * 180) / 15) - 90) + 0x2000;
				num = num7 + ((num18 * num16) >> 14);
				num2 = num8 + ((num18 * num17) >> 14);
			} else {
				num = num9;
				num2 = num10;
			}
		}
		int num19 = (GameUtils.sin(this.gametick * 4) * this.tileh) >> 0x10;
		this.tutorialcursor.Paint(painter, (float) (num - num4),
				(float) (num2 - num19), frame);
	}

	private void pauseGame() {
		this.pauseGame(false);
	}

	private void pauseGame(boolean instant) {
		this.paused = true;
		super.game.clearMouseStatus();
		// this.audio.setVolume(0f, instant);
	}

	private void randomizeLevel() {
		this.clearTiledata();
		this.resetLevel();
		int num = super.game.getTick() & 0x1ff;
		GameUtils.initRandom(0xfabe67c + (num * 0xabe72));
		int levely = this.levely;
		int levelx = this.levelx;
		for (int i = 0; i < this.LEVEL_TILES_ROWS; i++) {
			for (int j = 0; j < this.LEVEL_TILES_COLS; j++) {
				Tile item = new Tile();
				item.x = levelx;
				item.y = levely;
				item.col = j;
				item.row = i;
				item.type = ETileTypes
						.forValue((GameUtils.getRandom() >> 4) % 0x12);
				item.subtype = 0;
				this.tiles.add(item);
				levelx += this.tilew;
			}
			levelx = this.levelx;
			levely += this.tileh;
		}
	}

	private void resetLevel() {
		this.ibackButtonPressed = false;
		if (this.trains.size() > 0) {
			this.trains.clear();
		}
		this.gametick = 0;
		if (this.caves.size() > 0) {
			for (int k = 0; k < this.caves.size(); k++) {
				this.caveActive.set(k, -1);
			}
		}
		for (int i = 0; i < 3; i++) {
			this.trainPassed[i] = 0;
			this.trainPassedChangeTick[i] = -1;
		}
		for (Tile tile : this.tiles) {
			tile.setLocked(false);
		}
		this.levelEnded = false;
		this.levelToEndTicks = -1;
		this.updateDoodads();
		for (int j = 0; j < 100; j++) {
			this.particleX[j] = -1;
		}
		this.tutorialPhase = 0;
	}

	private void startAmbience(boolean instant) {
		this.ambienceVolumeTarget = (this.levelSet == 2) ? 0.8f : 0.4f;

		this.ambienceVolume = 0f;

	}

	private void stopAmbience(boolean instant) {
		this.ambienceVolumeTarget = 0f;
		if (instant) {
			this.ambienceVolume = this.ambienceVolumeTarget;

		}
	}

	@Override
	public void tick() {
		if ((((this.levelNum >= 4) && !this.paused) && ((this.levelStartTicks == -1) && !this.levelEnded))
				&& (this.levelToEndTicks == -1)) {
			int num = super.game.getMouseX();
			int num2 = super.game.getMouseY();
			int num3 = super.game.getW();
			int num4 = this.tilew / 10;
			int num5 = (num3 - (this.pause.getW() / 2)) - num4;
			boolean flag2 = GameUtils.isInside(num, num2, num5
					- (this.btnforward.getWidth() / 2),
					this.btnforward.getHeight() / 4,
					this.btnforward.getWidth(), this.btnforward.getHeight())
					&& super.game.isMouseDown();
			this.doFastForward(flag2);
		}
		this.doTick();
		if (this.fastForward) {
			this.doTick();
		}
	}

	private void tickAmbience() {
		if (this.ambienceVolume != this.ambienceVolumeTarget) {
			float num = 0.005f;
			if (this.ambienceVolume < this.ambienceVolumeTarget) {
				this.ambienceVolume += num;
				if (this.ambienceVolume > this.ambienceVolumeTarget) {
					this.ambienceVolume = this.ambienceVolumeTarget;
				}
			} else if (this.ambienceVolume > this.ambienceVolumeTarget) {
				this.ambienceVolume -= num;
				if (this.ambienceVolume < this.ambienceVolumeTarget) {
					this.ambienceVolume = this.ambienceVolumeTarget;
				}
			}

		}
	}


	private void trainCrashed(Train train, boolean softcrash) {
		if (this.levelToEndTicks == -1) {
			this.doFastForward(false);
			this.trainVolume = 0f;
			this.trainVolumeTarget = 0f;

			if (softcrash) {

			} else {

				this.addParticle(train.x, train.y, 1);
			}
			this.levelToEndTicks = 50;
			this.success = false;
		}
	}

	private void trainFinished(int color) {
		boolean flag = false;
		if (color != -1) {
			this.trainPassed[color]++;
			this.trainPassedChangeTick[color] = super.game.getTick();
			for (int i = 0; i < 3; i++) {
				if (this.trainPassed[i] < this.trainTargets[i]) {
					flag = true;
				}
			}
		}
		if (this.skipLevel) {
			flag = false;
			this.skipLevel = false;
		}
		if (!flag) {
			this.doFastForward(false);

			this.levelToEndTicks = 30;
			this.success = true;
			int num2 = super.game.getValue(EValues.EValueSelectedLevel);
			if ((super.game.getSettings().m_levels.get(num2 - 1) < 1)
					&& (((num2 == 5) || (num2 == 15)) || (((num2 == 20) || (num2 == 30)) || (num2 == 0x2d)))) {
				this.shouldDoLevelAnim = num2 - 1;
				super.game.setValue(EValues.EValueDoLevelSelectAnimation,
						this.shouldDoLevelAnim);
			} else if (((num2 == 15) || (num2 == 30)) || (num2 == 0x2d)) {
				this.shouldDoLevelAnim = 0x270f;
			} else {
				this.shouldDoLevelAnim = -1;
			}
			super.game.getSettings().m_levels.set(num2 - 1, 1);
			super.game.getSettings().Save();
			if (((super.game.getSettings().m_levels.get(14) > 0) && (super.game
					.getSettings().m_levels.get(0x1d) > 0))
					&& (super.game.getSettings().m_levels.get(0x2c) > 0)) {
				super.game.setValue(EValues.EValueDoGameEndAnimation, 1);
			}
		}
		// this.audio.playSfx(this.sfxcash, 1f, 0.5f, 0f, 1);
	}

	private void unpauseGame() {
		// this.audio.setVolume(1f);
		this.paused = false;
		super.game.clearMouseStatus();
		this.updateTrainVolume();
		this.startAmbience(false);
	}

	private void updateCaveData() {
		if (this.caves.size() != 0) {
			this.leftCaves = 0;
			this.rightCaves = 0;
			for (int i = 0; i < this.caves.size(); i++) {
				if (this.caves.get(i).col == -1) {
					this.leftCaves |= ((int) 1) << this.caves.get(i).row;
				} else if (this.caves.get(i).col == this.LEVEL_TILES_COLS) {
					this.rightCaves |= ((int) 1) << this.caves.get(i).row;
				}
			}
		}
	}

	private void updateDoodads() {
		if (this.trains.size() > 0) {
			for (int i = 0; i < this.trains.size(); i++) {
				if (this.trains.get(i).getEntityType() == EEntityClass.EEntityDoodad) {
					this.trains.remove(i);
					i--;
				}
			}
		}
		if (this.tiles.size() > 0) {
			for (int j = 0; j < this.tiles.size(); j++) {
				Tile tile = this.tiles.get(j);
				if ((tile.type.getValue() >= ETileTypes.ETileCustom3.getValue())
						&& (tile.type.getValue() <= ETileTypes.ETileCustom12
								.getValue())) {
					int num3 = (int) (((int) tile.type.getValue() - 11) % (int) ETileTypes.ETileCustom1
							.getValue());
					if ((this.levelSet != 2) || (num3 != 0)) {
						Entity item = new Entity();
						item.x = tile.x + (this.tilew / 2);
						item.y = tile.y + (this.tileh / 2);
						item.type = num3;
						this.trains.add(item);
					}
				}
			}
			int num4 = 0;
			for (int k = 0; k < this.LEVEL_TILES_ROWS; k++) {
				for (int m = 0; m < this.LEVEL_TILES_COLS; m++) {
					int num7 = 0;
					if ((this.levelSet == 2)
							&& this.tiles.get(num4).isLavaEmptyTile()) {
						if ((k > 0)
								&& !this.getTile(m, k - 1).isLavaEmptyTile()) {
							num7 |= 1;
						}
						if ((m > 0)
								&& !this.getTile(m - 1, k).isLavaEmptyTile()) {
							num7 |= 8;
						}
						if ((m < (this.LEVEL_TILES_COLS - 1))
								&& !this.getTile(m + 1, k).isLavaEmptyTile()) {
							num7 |= 2;
						}
						if (((k < (this.LEVEL_TILES_ROWS - 1)) && !this
								.getTile(m, k + 1).isLavaEmptyTile())
								&& (this.tiles.get(num4).type != ETileTypes.ETileBridgeVertical)) {
							num7 |= 4;
						}
					}
					this.tiles.get(num4).lavaborders = num7;
					num4++;
				}
			}
		}
	}

	private void updateNextCave() {
		int ticks = -1;
		this.nextCave = 0;
		if ((this.schedule.size() > 0) && (this.levelToEndTicks == -1)) {
			for (ScheduleItem item : this.schedule) {
				if (((ticks != -1) || (item.ticks <= (this.gametick + 60)))
						&& (ticks != item.ticks)) {
					continue;
				}
				if ((this.nextCave & (((int) 1) << item.caveid)) == 0) {
					this.nextCave |= ((int) 1) << item.caveid;
					this.nextCaveChangeTick = this.gametick;
					ticks = item.ticks;
				}
			}
		}
	}

	private void updateTrainVolume() {
		int num = 0;
		for (int i = 0; i < this.trains.size(); i++) {
			if (this.trains.get(i).getEntityType() == EEntityClass.EEntityTrain) {
				Train train = (Train) this.trains.get(i);
				if (!train.isFinished() && (train.type == 0)) {
					num++;
				}
			}
		}
		switch (num) {
		case 0:
			this.trainVolumeTarget = 0f;
			break;

		case 1:
			this.trainVolumeTarget = 0.3f;
			break;

		case 2:
			this.trainVolumeTarget = 0.5f;
			break;
		}
		if (num >= 3) {
			this.trainVolumeTarget = 0.6f;
		}
	}

	public StateGame(GameCore parent) {
		super.initState(parent);

		this.ambience = -1;
		this.initState(parent);
		this.levelSet = -1;
		this.trees = null;
		this.bush = null;
		this.animal = null;
		this.graphicsLoaded = false;
		this.lavabubble = new Sprite("lavabubble", 1, 1, 0x12, true);
		this.lavaflow = new Sprite("lavaflow", 1, 1, 9, true);
		this.lavasplash = new Sprite("lavasplash", 2, 1, 0x12, true);
		this.train = new Sprite("trainred", 0x13, 2, 0x12, true);
		this.trainBlue = new Sprite("trainblue", 0x13, 2, 0x12, true);
		this.trainYellow = new Sprite("trainyellow", 0x13, 2, 0x12, true);
		this.coal = new Sprite("coal", 0x13, 1, 0x12, true);
		this.carriage = new Sprite("carred", 0x13, 1, 0x12, true);
		this.carriageBlue = new Sprite("carblue", 0x13, 1, 0x12, true);
		this.carriageYellow = new Sprite("caryellow", 0x13, 1, 0x12, true);
		this.dragon = null;

	}

	@Override
	public void activateState() {
		this.shouldDoPauseAfterLevelStart = false;

		this.doFastForward(false);
		this.editMode = false;
		this.tutorialId = -1;
		this.paused = false;
		this.ibackButtonPressed = false;
		if (super.game.getValue(EValues.EValueSelectedMainLevel) != this.levelSet) {
			this.levelSet = super.game
					.getValue(EValues.EValueSelectedMainLevel);
			if (this.trees != null) {
				this.trees = null;
			}
			if (this.bush != null) {
				this.bush = null;
			}
			if (this.animal != null) {
				this.animal = null;
			}
			if (this.ambience != -1) {

				this.ambience = -1;
			}
			if (this.levelSet == 0) {
				this.trees = new Sprite("trees", 3, 1, 0x22, false);
				this.bush = new Sprite("bush", 3, 1, 0x22, false);
				this.animal = new Sprite("animal", 3, 1, 0x12, false);
				if (this.dragon != null) {
					this.dragon = null;
				}

			} else if (this.levelSet == 1) {
				this.trees = new Sprite("trees2", 3, 1, 0x22, false);
				this.bush = new Sprite("bush2", 3, 1, 0x22, false);
				this.animal = new Sprite("animal2", 3, 1, 0x12, false);
				if (this.dragon != null) {
					this.dragon = null;
				}

			} else {
				this.trees = new Sprite("trees3", 2, 1, 0x22, false);
				this.bush = new Sprite("bush3", 3, 1, 0x22, false);
				this.dragon = new Sprite("dragon", 1, 1, 0x12, false);

			}
			if (this.levelSet == 2) {
				this.LEVEL_TILES_COLS = 10;
				this.LEVEL_TILES_ROWS = 8;
			} else {
				this.LEVEL_TILES_COLS = 8;
				this.LEVEL_TILES_ROWS = 6;
			}
			Tile.initTiles(this.levelSet);
		}
		if (!this.graphicsLoaded) {
			this.bignumbers = new Sprite("bignumbers2", 11, 1, 9, true);
			this.smoke = new Sprite("smoke", 1, 1);
			this.mountaintop = new Sprite("mountaintop", 1, 1, 0x12, true);
			this.mountainside = new Sprite("mountainside", 1, 1, 0x12, true);
			this.signs = new Sprite("signs", 6, 1, 0x22, true);
			this.cavebar = new Sprite("cavebar", 1, 1, 9, true);
			this.cavebartop = new Sprite("cavebartop", 1, 1, 9, true);
			this.cavebarround = new Sprite("cavebarround", 1, 1, 10, true);
			this.indicators = new Sprite("indicators", 3, 1, 0x12, true);
			this.num = new Sprite("num", 10, 1, 9, true);
			this.numslash = new Sprite("numslash", 1, 1, 10, true);
			this.gamelabels = new Sprite("gamelabels", 1, 2, 0x12, true);
			this.pause = new Button(EButtonTypes.ENormal, "pause");
			this.btnrestart = new Button(EButtonTypes.ENormal, "btnrestart");
			this.btnplay = new Button(EButtonTypes.ENormal, "btnplay");
			this.btnexit = new Button(EButtonTypes.ENormal, "btnexit");
			this.btnforward = new Sprite("btnforward", 2, 1);
			this.btnshadow = new Sprite("btnshadow", 1, 1);
			this.explosion = new Sprite("explosion3", 3, 1, 0x12, true);
			this.explosiondebris = new Sprite("explosiondebris", 3, 1, 0x12,
					true);
			this.tutorialcursor = new Sprite("tutorialcursor", 2, 1, 0x21);
			this.tutorialcursorshadow = new Sprite("tutorialcursorshadow", 2,
					1, 0x21);
			this.getready = new Sprite("getready", 1, 1, 9);
			this.level = new Sprite("level", 1, 1, 9);
			this.graphicsLoaded = true;
		}
		this.ambienceVolumeTarget = 0f;
		this.ambienceVolume = 0f;
		trainOffsetY = this.train.getHeight() / 7;
		trainCarOffsetY = this.train.getHeight() / 7;
		trainCoalOffsetY = 0x10;
		if (this.levelSet == 2) {
			trainOffsetY = (trainOffsetY * 8) / 10;
			trainCarOffsetY = (trainCarOffsetY * 8) / 10;
			trainCoalOffsetY = (trainCoalOffsetY * 8) / 10;
		}
		for (int i = 0; i < 3; i++) {
			this.trainTargets[i] = 0;
			this.trainPassed[i] = 0;
		}
		this.tileh = Tile.getTileHReal();
		this.tilew = Tile.getTileWReal();
		int num2 = this.tileh * this.LEVEL_TILES_ROWS;
		int num3 = GameUtils.getScreenH();
		this.levelx = this.tilew / 2;
		this.levely = num3 - num2;
		this.levelw = this.LEVEL_TILES_COLS * this.tilew;
		this.levelh = this.LEVEL_TILES_ROWS * this.tileh;
		this.levelNum = super.game.getValue(EValues.EValueSelectedLevel);
		this.trainspeed = Train.MIN_TRAIN_SPEED;
		this.loadLevel(((this.levelNum != 1) && (this.levelNum != 3))
				&& (this.levelNum != 4));

		this.trainVolume = 0f;
		this.trainVolumeTarget = 0f;
		if (this.levelNum == 1) {
			this.tutorialId = 1;
			this.initTutorial();
		} else if (this.levelNum == 3) {
			this.tutorialId = 2;
			this.initTutorial();
		} else if (this.levelNum == 4) {
			this.tutorialId = 3;
			this.initTutorial();
		} else {

		}
	}


	private void addParticle(int x, int y, int type) {
		for (int i = 0; i < 100; i++) {
			if (this.particleX[i] == -1) {
				this.particleX[i] = x;
				this.particleY[i] = y;
				this.particleTick[i] = this.gametick;
				this.particleType[i] = type;
				return;
			}
		}
	}

	@Override
	public void backButtonPressed() {
		if (((!this.paused && !this.levelEnded) && ((this.levelToEndTicks == -1) && (this.levelStartTicks == -1)))
				&& (this.tutorialId == -1)) {
			this.pauseGame();
			this.ibackButtonPressed = false;
		} else if (this.levelStartTicks != -1) {
			this.shouldDoPauseAfterLevelStart = true;
		} else if (this.paused) {
			this.unpauseGame();
			this.ibackButtonPressed = false;
		} else if (this.tutorialId != -1) {
			this.tutorialId = -1;
			this.clearTutorial();
			super.game.clearMouseStatus();
			this.gametick = 0;
			super.game.changeState(EStates.EGameStateLevelSelect);
		} else if (this.levelEnded) {
			this.ibackButtonPressed = true;
		}
	}

	private void clearTiledata() {
		this.tiles.clear();
		this.caves.clear();
		this.caveActive.clear();
		this.leftCaves = 0;
		this.rightCaves = 0;
		this.schedule.clear();
	}

	private void clearTutorial() {
		if (this.tutorialTiles.size() > 0) {
			this.tutorialTiles.clear();
		}
	}

	private void createEmptyLevel() {
		int levely = this.levely;
		int levelx = this.levelx;
		for (int i = 0; i < this.LEVEL_TILES_ROWS; i++) {
			for (int j = 0; j < this.LEVEL_TILES_COLS; j++) {
				Tile item = new Tile();
				item.x = levelx;
				item.y = levely;
				item.col = j;
				item.row = i;
				item.type = ETileTypes.ETileEmpty;
				item.subtype = 0;
				this.tiles.add(item);
				levelx += this.tilew;
			}
			levelx = this.levelx;
			levely += this.tileh;
		}
		this.updateDoodads();
	}

	private void createTrain(Tile tile) {
		int subtype = tile.subtype;
		Train item = new Train(this.levelSet);
		item.type = 0;
		item.subtype = subtype;
		item.speed = this.trainspeed;
		item.SetTile(tile);
		this.trains.add(item);
		Train train2 = new Train(this.levelSet);
		train2.type = 1;
		train2.subtype = subtype;
		train2.speed = this.trainspeed;
		train2.SetTile(tile);
		this.trains.add(train2);
		train2.stop();
		Train train3 = new Train(this.levelSet);
		train3.type = 2;
		train3.subtype = subtype;
		train3.speed = this.trainspeed;
		train3.SetTile(tile);
		this.trains.add(train3);
		train3.stop();
		item.setTrail(train2);
		train2.setTrail(train3);
		this.updateTrainVolume();
	}

	@Override
	public void deactivateState() {
		this.resetLevel();
		this.clearTiledata();

		this.stopAmbience(true);
	}

	private void doFastForward(boolean value) {
		if (this.fastForward != value) {
			this.fastForward = value;

		}
	}

	private void doTick() {
		int num = super.game.getMouseX();
		int num2 = super.game.getMouseY();
		this.tickAmbience();
		if (this.levelStartTicks != -1) {
			if ((super.game.isMouseDown() || this.shouldDoPauseAfterLevelStart)
					&& (this.levelStartTicks < 70)) {
				this.levelStartTicks = 70;
				super.game.clearMouseStatus();
			}
			if (this.levelStartTicks == 70) {
				this.startAmbience(false);
			}
			this.levelStartTicks++;
			if (this.levelStartTicks < 70) {
				return;
			}
			if (this.levelStartTicks >= 0x55) {
				if (this.shouldDoPauseAfterLevelStart) {
					this.shouldDoPauseAfterLevelStart = false;
					this.pauseGame();
				}
				this.levelStartTicks = -1;
			}
		}
		if (this.levelEndAnimTicks != -1) {
			this.levelEndAnimTicks++;
			if ((this.success && (this.levelEndAnimTicks > 40))
					|| (!this.success && (this.levelEndAnimTicks > 20))) {
				this.levelEndAnimTicks = -1;
			}
		}
		if (((super.game.getTick() % 3) == 0)
				&& (this.trainVolume != this.trainVolumeTarget)) {
			float num3 = this.paused ? 0.05f : 0.01f;
			if (this.trainVolume < this.trainVolumeTarget) {
				this.trainVolume += num3;
				if (this.trainVolume > this.trainVolumeTarget) {
					this.trainVolume = this.trainVolumeTarget;
				}
			} else if (this.trainVolume > this.trainVolumeTarget) {
				this.trainVolume -= num3;
				if (this.trainVolume < this.trainVolumeTarget) {
					this.trainVolume = this.trainVolumeTarget;
				}
			}

		}
		if (this.tutorialId == -1) {
			if (this.levelToEndTicks != -1) {
				this.levelToEndTicks--;
				if (this.levelToEndTicks < 0) {
					this.levelToEndTicks = -1;
					this.levelEnded = true;
					this.levelEndAnimTicks = 0;
					this.stopAmbience(false);
				}
				if (super.game.isMouseDown()) {
					super.game.clearMouseStatus();
				}
			}
			if (!this.paused && !this.levelEnded) {
				this.gametick++;
				if ((this.levelSet == 2) && ((this.gametick % 30) == 0)) {
					GameUtils
							.initRandom((int) ((0xe25beaceL) ^ ((((this.gametick + 0x114) / 30) + super.game
									.getValue(EValues.EValueSelectedLevel)) * 0xab23e)));
					int num4 = 0;
					boolean flag = false;
					while (!flag && (num4 < 30)) {
						int num5 = (GameUtils.getRandom() >> 3)
								% this.tiles.size();
						if ((this.tiles.get(num5).type.getValue() < ETileTypes.ETileLake
								.getValue())
								&& !this.tiles.get(num5).isLocked()) {
							this.tiles.get(num5).dropTick = this.gametick;
							flag = true;
						}
						num4++;
					}
				}
				int num6 = super.game.getValue(EValues.EValueSelectedLevel);
				if ((super.game.isMouseDown() && (super.game.getMouseDownTick() == super.game
						.getTick())) && (this.levelToEndTicks == -1)) {
					boolean flag2 = false;
					int num7 = 0;
					for (int i = 0; i < this.LEVEL_TILES_ROWS; i++) {
						for (int j = 0; (j < this.LEVEL_TILES_COLS) && !flag2; j++) {
							Tile tile = this.tiles.get(num7);
							if (((num > tile.x) && (num < (tile.x + this.tilew)))
									&& ((num2 > tile.y) && (num2 < (tile.y + this.tileh)))) {
								flag2 = true;
								if (num6 == 1) {
									if (((this.tutorialPhase == 0) && (j == 3))
											&& (i == 3)) {
										tile.rotate();
										this.tutorialPhase = 1;
										this.tutorialChangeTick = this.gametick;
										// this.audio.playSfxOneShot(this.sfxnaks1,
										// 1f);
									} else if (((this.tutorialPhase == 1) && (j == 5))
											&& (i == 1)) {
										this.tutorialPhase = 2;
										this.tutorialChangeTick = this.gametick;
										tile.rotate();
										// this.audio.playSfxOneShot(this.sfxnaks1,
										// 1f);
									}
								} else {
									tile.rotate();

								}
								super.game.clearMouseStatus();
							}
							num7++;
						}
					}
				}
				if ((this.schedule.size() > 0) && (this.levelToEndTicks == -1)) {
					for (ScheduleItem item : this.schedule) {
						if (item.ticks == this.gametick) {
							
							this.createTrain(this.caves.get(item.caveid));
						} else if (item.ticks == (this.gametick + 60)) {
							this.caveActive.set(item.caveid, this.gametick);
							
							this.updateNextCave();
						}
					}
				}
				if (this.trains.size() > 0) {
					for (int k = 0; k < this.trains.size(); k++) {
						if (this.trains.get(k).getEntityType() != EEntityClass.EEntityTrain) {
							continue;
						}
						Train train = (Train) this.trains.get(k);
						if (!train.isFinished()) {
							int num15 = train.Tick();
							if ((((!this.paused && !this.levelEnded) && (!this.editMode && (train.type == 0))) && ((this.levelToEndTicks == -1) && (!train.tile
									.IsCave() || (!train.enteredWorld && (train.pos > ((Train.TILE_LENGTH * 2) / 3))))))
									&& ((this.gametick % 6) == 0)) {
								int num16 = this.tilew / 4;
								int x = train.x
										+ ((GameUtils.cos(train.angle) * num16) >> 13);
								int y = ((train.y - trainOffsetY) - (this.trainBlue
										.getHeight() / 4))
										- ((GameUtils.sin(train.angle) * num16) >> 13);
								this.addParticle(x, y, 0);
							}
							if (num15 != Train.TRAIN_INSIDE_TILE) {
								Tile tile2 = train.GetTile();
								if (tile2.IsCave() && train.enteredWorld) {
									if (train.type == 2) {
										this.trainFinished(train.subtype);
									}
									train.setFinished();
									this.updateTrainVolume();
									continue;
								}
								int num19 = tile2.col;
								int row = tile2.row;
								switch (EDirections.forValue(num15)) {
								case EDirTop:
									row--;
									break;

								case EDirRight:
									num19++;
									break;

								case EDirDown:
									row++;
									break;

								case EDirLeft:
									num19--;
									break;
								default:
									break;
								}
								if (((num19 < 0) || (num19 == this.LEVEL_TILES_COLS))
										|| ((row < 0) || (row == this.LEVEL_TILES_ROWS))) {
									boolean flag3 = false;
									for (int m = 0; m < this.caves.size(); m++) {
										if (((this.caves.get(m).row == row) && (this.caves
												.get(m).col == num19))
												&& ((this.caves.get(m).subtype - 3) == train.subtype)) {
											if (train.type == 2) {
												train.GetTile()
														.trainLeftTile(
																EDirections
																		.forValue(num15),
																this.gametick);
											}
											train.SetTile(this.caves.get(m));
											flag3 = true;
											if (train.type == 0) {
												
												
											
											}
										}
									}
									if (!flag3) {
										train.setCrashed(false);
										this.trainCrashed(train, false);
									}
								} else {
									Tile aTile = this.getTile(num19, row);
									if (train.type == 2) {
										train.GetTile().trainLeftTile(
												EDirections.forValue(num15),
												this.gametick);
									}
									switch (train.SetTile(aTile)) {
									case -1: {
										this.trainCrashed(train, false);
										continue;
									}
									case -2: {
										this.trainCrashed(train, true);
										if ((this.levelSet == 2)
												&& (aTile.type == ETileTypes.ETileCustom3)) {
											train.crashToLava = true;
										}
										continue;
									}
									}
									if ((aTile.type == ETileTypes.ETileBridgeHorizontal)
											|| (aTile.type == ETileTypes.ETileBridgeVertical)) {
									
									}
								}
							}
						}
					}
					if ((this.levelToEndTicks != -1) && !this.success) {
						for (int n = 0; n < this.trains.size(); n++) {
							if (this.trains.get(n).getEntityType() == EEntityClass.EEntityTrain) {
								Train train2 = (Train) this.trains.get(n);
								if (!train2.isFinished() && !train2.crashed) {
									train2.shouldStop = true;
								}
							}
						}
					}
					Collections.sort(trains, esort);
				}
			}
		}
	}


	private int findCave(int col, int row) {
		if (this.caves.size() > 0) {
			for (int i = 0; i < this.caves.size(); i++) {
				if ((this.caves.get(i).row == row)
						&& (this.caves.get(i).col == col)) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public void gameHidden() {
		this.pauseGame(true);
	}

	private Tile getTile(int col, int row) {
		return this.tiles.get(col + (row * this.LEVEL_TILES_COLS));
	}

	private void initTutorial() {
		int num3;
		int num4;
		int num = super.game.getW();
		int num2 = super.game.getH();
		int num5 = (this.tutorialId == 2) ? ((num2 / 2) - (this.tileh / 4))
				: (num2 / 2);
		if (this.tutorialId == 2) {
			num4 = (num / 2) - (this.tilew / 2);
			num3 = num5 - (this.tileh * 3);
			for (int j = 0; j < 3; j++) {
				Tile item = new Tile();
				item.x = num4;
				item.y = num3;
				item.col = 3;
				item.row = j - 1;
				if (j == 0) {
					item.type = ETileTypes.ECaveTop;
					item.subtype = 1;
				} else {
					item.type = ETileTypes.ETileTopDown;
				}
				this.tutorialTiles.add(item);
				num3 += this.tileh;
			}
		}
		num3 = num5;
		num4 = ((num / 2) - (this.tilew / 2)) - (this.tilew * 3);
		for (int i = 0; i < 7; i++) {
			Tile tile2 = new Tile();
			tile2.x = num4;
			tile2.y = num3;
			tile2.col = i - 1;
			tile2.row = 3;
			switch (i) {
			case 0:
				tile2.type = ETileTypes.ECaveLeft;
				tile2.subtype = 0;
				break;

			case 1:
			case 2:
			case 4:
			case 5:
				tile2.type = ETileTypes.ETileLeftRight;
				break;

			case 3:
				tile2.type = (this.tutorialId == 1) ? ETileTypes.ETileTopDown
						: ETileTypes.ETileLeftRight;
				break;

			case 6:
				tile2.type = ETileTypes.ECaveRight;
				tile2.subtype = 3;
				break;
			}
			this.tutorialTiles.add(tile2);
			num4 += this.tilew;
		}
		if (this.tutorialId == 2) {
			num4 = (num / 2) - (this.tilew / 2);
			num3 = num5 + this.tileh;
			for (int k = 0; k < 3; k++) {
				Tile tile3 = new Tile();
				tile3.x = num4;
				tile3.y = num3;
				tile3.col = 3;
				tile3.row = 4;
				if (k == 2) {
					tile3.type = ETileTypes.ECaveBottom;
					tile3.subtype = 4;
				} else {
					tile3.type = ETileTypes.ETileTopDown;
				}
				this.tutorialTiles.add(tile3);
				num3 += this.tileh;
			}
		}
	}

	private void loadLevel(boolean showIntro) {
		this.resetLevel();
		this.clearTiledata();
		this.levelEndAnimTicks = -1;
		if (showIntro) {
			this.levelStartTicks = 0;
		}
		ObjRef<Integer> tempRef_trainspeed = new ObjRef<Integer>(
				this.trainspeed);
		ObjRef<java.util.ArrayList<Tile>> tempRef_tiles = new ObjRef<java.util.ArrayList<Tile>>(
				this.tiles);
		ObjRef<java.util.ArrayList<Tile>> tempRef_caves = new ObjRef<java.util.ArrayList<Tile>>(
				this.caves);
		ObjRef<java.util.ArrayList<ScheduleItem>> tempRef_schedule = new ObjRef<java.util.ArrayList<ScheduleItem>>(
				this.schedule);
		boolean flag = super.game.LoadLevel(
				super.game.getValue(EValues.EValueSelectedLevel),
				tempRef_trainspeed, tempRef_tiles, tempRef_caves,
				tempRef_schedule);
		this.trainspeed = tempRef_trainspeed.get();
		this.tiles = tempRef_tiles.get();
		this.caves = tempRef_caves.get();
		this.schedule = tempRef_schedule.get();
		this.graphicsLoaded = false;
		if (flag) {
			this.skipLevel = false;
			for (int i = 0; i < this.caves.size(); i++) {
				this.caveActive.add(-1);
				this.caves.get(i).x = this.levelx
						+ (this.caves.get(i).col * this.tilew);
				this.caves.get(i).y = this.levely
						+ (this.caves.get(i).row * this.tileh);
			}
			for (int j = 0; j < this.tiles.size(); j++) {
				this.tiles.get(j).x = this.levelx
						+ (this.tiles.get(j).col * this.tilew);
				this.tiles.get(j).y = this.levely
						+ (this.tiles.get(j).row * this.tileh);
			}
			this.updateCaveData();
			for (int k = 0; k < 3; k++) {
				this.trainTargets[k] = 0;
			}
			if (this.schedule.size() > 0) {
				for (ScheduleItem item : this.schedule) {
					if (item.caveid <= this.caves.size()) {
						int index = (this.caves.size() == 0) ? 60 : this.caves
								.get(item.caveid).subtype;
						if (index < 3) {
							this.trainTargets[index]++;
						}
					}
				}
				this.nextCave = -1;
				this.updateNextCave();
			} else {
				this.nextCave = -1;
			}
			this.updateDoodads();
		} else {
			this.createEmptyLevel();
			this.randomizeLevel();
			this.updateDoodads();
		}
	}

	@Override
	public void paint(Painter painter) {
		if (this.tutorialId != -1) {
			this.paintTutorial(painter);
		} else {
			int w = super.game.getW();
			int h = super.game.getH();
			int levely = this.levely;
			
		
			if (this.levelSet == 2) {
				painter.save();
				for (int num10 = (levely - this.lavaflow.getHeight())
						+ (this.gametick % this.lavaflow.getHeight()); num10 < h; num10 += this.lavaflow
						.getHeight()) {
					for (int num11 = (this.levelx - ((GameUtils
							.sin(this.gametick * 2) * this.tileh) >> 13))
							- this.tileh; num11 < ((this.levelw + this.levelx) + 0x20); num11 += this.lavaflow
							.getWidth()) {
						this.lavaflow.Paint(painter, (float) num11,
								(float) num10, 0);
					}
				}
				int num12 = 30;
				int levelx = this.levelx;
				int num14 = this.levely;
				GameUtils.initRandom(0x25beace);
				for (int num15 = 0; num15 < num12; num15++) {
					int num16 = this.gametick
							+ ((GameUtils.getRandom() >> 3) & 0x1ff);
					int num17 = 60;
					int num18 = num16 % num17;
					int startvalue = (((num16 / num17) + ((GameUtils
							.getRandom() >> 3) & 0xff)) * 0xab42)
							^ (GameUtils.getRandom() >> 3);
					int num20 = GameUtils.getRandomSeed();
					GameUtils.initRandom(startvalue);
					int num21 = levelx
							+ ((GameUtils.getRandom() >> 3) % this.levelw);
					int num22 = num14
							+ ((GameUtils.getRandom() >> 3) % this.levelh);
					int num23 = 30 + ((GameUtils.getRandom() >> 3) % 30);
					if (num18 < num23) {
						float num24 = GameUtils
								.sin(((num18 * 360) / num23) - 90) / 0x2000;
						num24 = (num24 + 1f) / 2f;
						float scalex = 0.5f + ((num18 * (((GameUtils
								.getRandom() >> 3) % 100) / 100)) / num23);

						this.lavabubble.PaintScaled(painter, (float) num21,
								(float) (num22 + num18), 0, scalex, scalex);

					}
					GameUtils.initRandom(num20);
				}
				painter.restore();
			} else {

			}

			int num26 = this.levelx + (this.tilew / 2);
			int num27 = this.levely - (this.tileh / 4);
			int num28 = this.tileh / 10;
			for (int i = 0; i < this.LEVEL_TILES_COLS; i++) {
				int num30 = (num27 + ((GameUtils.sin((this.gametick * 10)
						- (i * 60)) * num28) >> 14))
						+ (num28 / 2);
				if (this.levelSet == 2) {
					this.mountaintop.PaintScaled(painter, (float) num26,
							(float) num30, 0, 0.7f, 0.7f);
				} else {
					this.mountaintop.Paint(painter, (float) num26,
							(float) num30, 0);
				}
				num26 += this.tilew;
			}
		
			int num31 = this.levelx - ((this.tilew * 5) / 0x16);
			int num32 = (this.levelx + this.levelw) + ((this.tilew * 3) / 10);
			int num33 = this.levely + (this.tileh / 2);
			for (int j = 0; j < (this.LEVEL_TILES_ROWS + 1); j++) {
				int num35 = num33;
				boolean flag = false;
				if ((this.leftCaves & (((int) 1) << j)) != 0) {
					flag = true;
				}
				if ((this.rightCaves & (((int) 1) << j)) != 0) {
					flag = true;
				}
				if (this.levelSet == 2) {
					this.mountainside.PaintScaled(painter, (float) num31,
							(float) num35, 0, 0.8f, 0.8f);
					this.mountainside.PaintScaled(painter, (float) num32,
							(float) num35, 0, 0.8f, 0.8f);
				} else {
					this.mountainside.Paint(painter, (float) num31,
							(float) num35, 0);
					this.mountainside.Paint(painter, (float) num32,
							(float) num35, 0);
				}
				if (flag) {
					for (int num36 = 0; num36 < this.caves.size(); num36++) {
						if (this.caves.get(num36).row == j) {
							this.caves.get(num36).Paint(painter, 0);
						}
					}
				}
				num33 += this.tileh;
			}
			for (int k = 0; k < this.caves.size(); k++) {
				if (this.caves.get(k).type == ETileTypes.ECaveTop) {
					this.caves.get(k).Paint(painter, 0);
					this.paintCaveSign(painter, this.caves.get(k), k);
				}
			}
			int num38 = 0;
			for (int m = 0; m < this.LEVEL_TILES_ROWS; m++) {
				for (int num40 = 0; num40 < this.LEVEL_TILES_COLS; num40++) {
					this.tiles.get(num38).Paint(painter, 0, this.gametick,
							this.paused, this.levelSet == 2);
					num38++;
				}
			}
			int num41 = (this.levelx + this.levelw) + ((this.tilew * 4) / 10);
			this.cavebartop.Paint(painter, (float) num41, (float) this.levely,
					0);
			for (int n = this.levely + this.cavebartop.getHeight(); n < h; n += this.cavebar
					.getHeight()) {
				this.cavebar.Paint(painter, (float) num41, (float) n, 0);
			}
			int num43 = this.caves.size();
			int index = 0;
			int num45 = 0;
			if (this.trains.size() > 0) {
				for (int num46 = 0; num46 < this.trains.size(); num46++) {
					int y = this.trains.get(num46).y;
					if (index < num43) {
						boolean flag2 = true;
						while (flag2) {
							if (this.caves.get(index).y < y) {
								if (this.caves.get(index).col == this.LEVEL_TILES_COLS) {
									if (num45 < index) {
										while (num45 < index) {
											if (this.caves.get(num45).col == this.LEVEL_TILES_COLS) {
												this.caves.get(num45).Paint(
														painter, 1);
											}
											num45++;
											if (num45 == num43) {
												flag2 = false;
											}
										}
									}
									this.paintCaveSign(painter,
											this.caves.get(index), index);
								}
								index++;
								if (index == num43) {
									flag2 = false;
								}
							} else {
								flag2 = false;
							}
						}
					}
					if (this.trains.get(num46).getEntityType() == EEntityClass.EEntityTrain) {
						Train train = (Train) this.trains.get(num46);
						if (!train.finished) {
							painter.setClip(this.levelx
									- ((this.tilew * 2) / 5), 0, this.levelw
									+ ((this.tilew * 4) / 5), h);
							boolean shake = (train.crashed && (train.softcrashticks != -1))
									&& (train.softcrashticks < (Train.SOFT_CRASH_TICKS / 2));
							this.paintTrain(painter, train.x, train.y,
									train.angle, train.type, train.subtype,
									shake);
							painter.removeClip();
						}
					} else {
						int type = this.trains.get(num46).type;
						int x = this.trains.get(num46).x;
						int num50 = this.trains.get(num46).y;
						if ((type >= 0) && (type <= 2)) {
							num50 += this.tileh / 2;
							float angle = 0f;
							if ((this.levelSet == 0) || (this.levelSet == 2)) {
								angle = ((float) (GameUtils
										.sin(((this.gametick * 4) + (x * 10))
												+ (num50 * 5)) * 4)) / 8092f;
							} else {
								angle = ((float) (GameUtils.sin((5 + (x * 10))
										+ (num50 * 5)) * 2)) / 8092f;
							}
							if (this.levelSet == 2) {
								type--;
							}
							this.trees.Paint(painter, (float) x, (float) num50,
									type, angle);
						} else if ((type >= 3) && (type <= 5)) {
							num50 += this.bush.getHeight() / 2;
							if ((this.levelSet == 0)
									|| ((this.levelSet == 2) && (type == 5))) {
								if ((this.levelSet == 2) && (type == 5)) {
									num50 -= this.bush.getHeight() / 4;
								}
								float num52 = (GameUtils
										.sin(((this.gametick * 6) + (x * 4))
												+ (num50 * 7)) * 3.5f) / 8092f;
								this.bush.Paint(painter, (float) x,
										(float) num50, type - 3, num52);
							} else {
								this.bush.Paint(painter, (float) x,
										(float) num50, type - 3);
							}
						} else if ((type >= 6) && (type <= 8)) {
							if (this.levelSet == 2) {
								this.dragon.Paint(painter, (float) x,
										(float) num50, 0);
							} else {
								this.animal.Paint(painter, (float) x,
										(float) num50, type - 6);
							}
						}
						if ((this.levelSet == 2)
								&& ((type == 3) || (type == 4))) {
							this.paintLavasplash(painter, x,
									num50 - this.tileh, 4, 8, 10);
						}
					}
					if (num45 < num43) {
						boolean flag4 = true;
						while (flag4) {
							if ((this.caves.get(num45).y + this.tileh) < y) {
								if (this.caves.get(num45).col == this.LEVEL_TILES_COLS) {
									this.caves.get(num45).Paint(painter, 1);
								}
								num45++;
								if (num45 == num43) {
									flag4 = false;
								}
							} else {
								flag4 = false;
							}
						}
					}
				}
			}
			for (int num53 = 0; num53 < num43; num53++) {
				Tile cave = this.caves.get(num53);
				if ((cave.col != this.LEVEL_TILES_COLS) || (num53 >= num45)) {
					if ((cave.col == this.LEVEL_TILES_COLS) && (num53 >= index)) {
						this.paintCaveSign(painter, cave, num53);
					}
					cave.Paint(painter, 1);
					if ((cave.row != -1) && (cave.col != this.LEVEL_TILES_COLS)) {
						this.paintCaveSign(painter, cave, num53);
					}
				}
			}
			int num54 = (this.levelx + this.levelw)
					+ ((((w - this.levelx) - this.levelw) * 7) / 10);
			int num55 = num54;
			int num56 = 0;
			int num57 = this.num.getWidth() / 8;
			int num58 = -1;
			for (int num59 = this.levely; num59 < h; num59 += (this.cavebarround
					.getHeight() * 9) / 10) {
				int num60 = num55
						+ ((this.caveRoundOffsets[num56] * this.cavebarround
								.getWidth()) / 100);
				int num61 = num59;
				this.cavebarround.Paint(painter, (float) num60, (float) num61,
						0);
				switch (num56) {
				case 1:
				case 2:
				case 3:
					for (int num62 = 0; num62 < 3; num62++) {
						if ((this.trainTargets[num62] > 0) && (num62 > num58)) {
							num58 = num62;
							int num63 = this.trainPassedChangeTick[num62];
							int num64 = -1;
							if (num63 > -1) {
								num64 = ((super.game.getTick() - num63) * 180) / 14;
								if (num64 > 180) {
									num64 = -1;
									this.trainPassedChangeTick[num62] = -1;
								}
							}
							num61 += (this.cavebarround.getHeight() / 20)
									+ ((this.indicators.getHeight() * 9) / 10);
							this.num.Paint(
									painter,
									(float) ((num60 - this.num.getWidth()) - num57),
									(float) num61, this.trainPassed[num62]);
							this.numslash.Paint(painter, (float) num60,
									(float) num61, 0);
							this.num.Paint(painter, (float) (num60 + num57),
									(float) num61, this.trainTargets[num62]);
							num61 -= this.indicators.getHeight() / 2;
							if (num64 > -1) {
								float num65 = (((float) GameUtils.sin(num64)) / 8092f) + 1f;
								this.indicators.PaintScaled(painter,
										(float) num60, (float) num61, num62,
										num65, num65);
							} else {
								this.indicators.Paint(painter, (float) num60,
										(float) num61, num62);
							}
							// C# TO JAVA CONVERTER TODO TASK: There is no
							// 'goto' in Java:
							num56++;
						}
					}
					break;
				}

			}
			if (this.levelSet == 2) {
				for (int num66 = 0; num66 < this.tiles.size(); num66++) {
					if (this.tiles.get(num66).type == ETileTypes.ETileCustom3) {
						this.paintLavasplash(painter, this.tiles.get(num66).x
								+ (this.tilew / 2), this.tiles.get(num66).y
								+ (this.tileh / 2), 1, 3, 60);
					}
				}
			}
			for (int num69 = 0; num69 < 100; num69++) {
				if (this.particleX[num69] != -1) {
					if (this.particleType[num69] == 0) {
						int num70 = ((this.gametick - this.particleTick[num69]) << 10) / 0x2d;
						if (num70 > 0x400) {
							this.particleX[num69] = -1;
						} else {
							int num71 = this.particleX[num69];
							int num72 = this.particleY[num69];
							GameUtils
									.initRandom(((this.particleTick[num69] * 0xace6) + 0xbaab)
											^ (num71 + (num72 * 0xe246a)));
							int num73 = this.tilew / 2;
							int num74 = ((GameUtils.getRandom() >> 3) % num73)
									- (num73 / 2);
							int num75 = ((GameUtils.getRandom() >> 3) % num73)
									- (num73 / 2);
							num71 += (num74 * num70) >> 10;
							num72 += (num75 * num70) >> 10;
							num72 -= (this.tileh * num70) >> 11;
							float num67 = ((float) (0x400 - num70))
									/ ((float) (0x400 + ((GameUtils.getRandom() >> 3) & 0x1ff)));
							painter.setOpacity((float) num67);
							float num68 = 0.8f + ((0.8f * num70) / ((float) (0x400 + ((GameUtils
									.getRandom() >> 3) & 0xff))));
							if (this.levelSet == 2) {
								num68 *= 0.8f;
							}
							this.smoke.PaintScaled(painter, (float) num71,
									(float) num72, 0, num68, num68);
							painter.setOpacity(1.0f);
						}
					} else if (this.particleType[num69] == 1) {
						int num76 = ((this.gametick - this.particleTick[num69]) << 10) / 40;
						if (num76 > 0x400) {
							num76 = 0x400;
						}
						int num77 = this.particleX[num69];
						int num78 = this.particleY[num69];
						int num79 = ((((((this.particleTick[num69] + num77) + num78) % 0x200) * 0xace68) + 0xba785b) ^ 0x2bea86a) & 0xfffffff;
						GameUtils.initRandom(num79);
						float num80 = (num76 < 0x100) ? (((float) (num76 + 0x100)) / 512f)
								: 1f;
						if (num76 < 0x200) {
							painter.setOpacity(((float) num76) / 512f);
						}
						for (int num81 = 0; num81 < 5; num81++) {
							int num82 = ((GameUtils.getRandom() >> 3) % this.tilew)
									- (this.tileh / 2);
							int num83 = ((GameUtils.getRandom() >> 3) % this.tileh)
									- (this.tileh / 2);
							this.btnshadow.Paint(painter,
									(float) (num77 + num82),
									(float) (num78 + num83), 0);
						}
						num79 = GameUtils.getRandomSeed();
						if (num76 > 0x200) {
							painter.setOpacity((float) ((0x400 - num76) / 0x200));
						} else {
							painter.setOpacity(1f);
						}
						GameUtils.initRandom(num79);
						for (int num84 = 0; num84 < 7; num84++) {
							int frame = ((GameUtils.getRandom() + (num76 / 20)) >> 3) % 3;
							int num86 = ((GameUtils.getRandom() >> 3) % (this.tilew * 2))
									- this.tilew;
							int num87 = ((GameUtils.getRandom() >> 3) % ((this.tileh * 3) / 2))
									- ((this.tileh * 3) / 4);
							num86 = (num86 * (0x400 + num76)) >> 11;
							num87 = (num87 * (0x400 + num76)) >> 11;
							this.explosion.PaintScaled(painter,
									(float) (num77 + num86),
									(float) (num78 + num87), frame, num80,
									num80);
						}
						painter.setOpacity(1f);
						for (int num88 = 0; num88 < 7; num88++) {
							int num89 = num76
									- ((GameUtils.getRandom() >> 3) & 0xff);
							if (num89 > 0) {
								num89 = (num89 * 0x400) / 0x300;
								int num90 = (GameUtils.getRandom() >> 3) % 3;
								int num91 = ((GameUtils.getRandom() >> 3) % (this.tilew * 4))
										- (this.tilew * 2);
								int num92 = ((GameUtils.getRandom() >> 3) % (this.tileh * 3))
										- ((this.tileh * 3) / 2);
								num91 = (num91 * num89) >> 10;
								num92 = (num92 * num89) >> 10;
								if (num89 < 0x300) {
									num92 -= (GameUtils
											.sin((num89 * 180) / 0x300) * this.tileh) >> 13;
								} else {
									num92 -= (GameUtils
											.sin(((num89 - 0x300) * 180) / 0x100) * this.tileh) >> 15;
								}
								float num93 = ((GameUtils.getRandom() >> 3) + ((num89 < 0x300) ? num89
										: 0x300)) / 5;
								if (num91 < 0) {
									num93 = -num93;
								}
								this.explosiondebris.Paint(painter,
										(float) (num77 + num91),
										(float) (num78 + num92), num90, num93);
							} else {
								GameUtils.getRandom();
								GameUtils.getRandom();
								GameUtils.getRandom();
								GameUtils.getRandom();
							}
						}
					}
				}
			}
			if (super.game.getValue(EValues.EValueSelectedLevel) == 1) {
				this.paintTutorialFingerLevel1(painter);
			}
			if (this.levelSet == 1) {
				GameUtils.initRandom((int) ((0xe25beaceL) ^ (super.game
						.getValue(EValues.EValueSelectedLevel) * 0xab23e)));
				int num94 = (w + (this.animal.getWidth() * 2)) / 3;
				int num95 = (num94 + 60) + ((GameUtils.getRandom() >> 3) % 160);
				int num96 = (GameUtils.getRandom() >> 3) % 100;
				if (this.gametick > num96) {
					int num97 = (this.gametick - num96) % num95;
					int num98 = ((((this.gametick / num95) + ((GameUtils
							.getRandom() >> 3) & 0x3ff)) * (0xab23e + ((GameUtils
							.getRandom() >> 3) & 0x3ff))) ^ 0x25beace) & 0xfffffff;
					GameUtils.initRandom(num98);
					int num99 = num97 * 3;
					float num100 = 1f;
					boolean flipped = false;
					if (((GameUtils.getRandom() >> 3) % 0x3e8) < 500) {
						num99 = (w + this.animal.getWidth()) - num99;
					} else {
						flipped = true;
						num99 -= this.animal.getWidth();
					}
					int num101 = (GameUtils.sin(num97 << 2) * this.tileh) >> 15;
					int num102 = this.tileh * 2;
					int num103 = ((GameUtils.getRandom() >> 3) % (this.levelh - this.tileh))
							+ this.levely;
					int num104 = ((GameUtils.getRandom() >> 3) % (this.levelh - this.tileh))
							+ this.levely;
					int num105 = (((num104 - num103) * num97) / num94) + num103;
					if (num99 > (this.levelx + this.levelw)) {
						int num106 = (((num99 - this.levelx) - this.levelw) * 500)
								/ this.tilew;
						int num107 = this.tileh / 2;
						if (num106 >= 180) {
							num102 -= num107;
						} else {
							num102 -= ((GameUtils.sin(num106 - 90) * num107) / 0x4000)
									+ (num107 / 2);
						}
					} else if (num99 < this.levelx) {
						int num108 = ((this.levelx - num99) * 500) / this.tilew;
						int num109 = this.tileh / 2;
						if (num108 >= 180) {
							num102 -= num109;
						} else {
							num102 -= ((GameUtils.sin(num108 - 90) * num109) / 0x4000)
									+ (num109 / 2);
						}
					}
					this.animal.PaintScaled(painter, (float) num99,
							(float) ((num105 + num102) + (num101 / 4)), 0,
							num100, 1f, flipped);
					this.animal.PaintScaled(painter, (float) num99,
							(float) (num105 + num101), 1, num100, 1f, flipped);
				}
			}
			int num110 = this.tilew / 10;
			if ((!this.paused && (this.levelStartTicks == -1))
					&& (!this.levelEnded && (this.levelEndAnimTicks == -1))) {
				int num111 = (w - (this.pause.getW() / 2)) - num110;
				if ((this.pause.paint(painter, super.game, num111,
						(h - (this.pause.getH() / 2)) - num110) && !this.levelEnded)
						&& ((this.levelToEndTicks == -1) && (this.levelStartTicks == -1))) {
					this.pauseGame();
					super.game.doButtonPressSound();
				}
				if (this.levelNum >= 4) {
					this.btnforward.Paint(painter, (float) num111,
							(float) ((this.btnforward.getHeight() * 3) / 4),
							this.fastForward ? 1 : 0);
				}
			}
			int num112 = 3;
			if (this.paused) {
				int num113 = (this.btnplay.getW() + num110) + num110;
				int num114 = w - num113;

				num114 += num113 / 2;
				int num115 = (this.btnexit.getH() * 7) / 5;
				this.btnshadow.Paint(painter, (float) (num114 + num112),
						(float) (num115 + num112), 0);
				if (this.btnexit.paint(painter, super.game, num114, num115)) {
					super.game.doButtonPressSound();

					super.game.changeState(EStates.EGameStateLevelSelect);
					super.game.clearMouseStatus();
					this.stopAmbience(true);
				}
				num115 += this.btnrestart.getH() * 2;
				this.btnshadow.Paint(painter, (float) (num114 + num112),
						(float) (num115 + num112), 0);
				if (this.btnrestart.paint(painter, super.game, num114, num115)) {
					super.game.doButtonPressSound();
					this.trainVolume = 0f;
					this.trainVolumeTarget = 0f;

					this.loadLevel(false);
					this.paused = false;
					super.game.clearMouseStatus();
					this.startAmbience(false);
				}
				num115 = (h - (this.btnplay.getH() / 2)) - num110;
				this.btnshadow.Paint(painter, (float) (num114 + num112),
						(float) (num115 + num112), 0);
				if (this.btnplay.paint(painter, super.game, num114, num115)) {
					this.unpauseGame();
					super.game.doButtonPressSound();
				}
			}
			if (this.levelEnded) {
				int num116 = (this.levelEndAnimTicks == -1) ? 0x400
						: (this.success ? ((this.levelEndAnimTicks << 10) / 40)
								: ((this.levelEndAnimTicks << 10) / 20));
				this.btnexit.disable(num116 < 0x400);
				this.btnrestart.disable(num116 < 0x400);
				this.btnplay.disable(num116 < 0x400);
				int num117 = (h / 2) - (this.gamelabels.getHeight() / 2);

				int num118 = num117 + this.gamelabels.getHeight();
				int num119 = num117;
				if (num116 < 0x400) {
					num119 += -num118
							+ ((GameUtils.sin((num116 * 90) >> 10) * num118) >> 13);
				}
				this.gamelabels.Paint(painter, (float) (w / 2), (float) num119,
						this.success ? 0 : 1);
				num117 += this.gamelabels.getHeight() + this.tileh;
				int num120 = this.btnrestart.getW();
				int num121 = w / 2;
				int num122 = (num120 * 3) / 2;
				if (this.levelEnded) {
					if (!this.success || (this.shouldDoLevelAnim == -1)) {
						num121 -= num122 / 2;
					}
				} else {
					num121 -= num122;
				}
				if (num116 < 0x400) {
					painter.setOpacity((num116 < 0x200) ? (0)
							: ((float) ((num116 - 0x200) / 0x200)));
				}
				if (!this.success || (this.shouldDoLevelAnim == -1)) {
					this.btnshadow.Paint(painter, (float) (num121 + num112),
							(float) (num117 + num112), 0);
					if (this.btnexit.paint(painter, super.game, num121, num117)
							|| this.ibackButtonPressed) {
						this.ibackButtonPressed = false;
						super.game.changeState(EStates.EGameStateLevelSelect);
						super.game.clearMouseStatus();
						super.game.doButtonPressSound();
					}
					num121 += num122;
				}
				if (!this.success) {
					this.btnshadow.Paint(painter, (float) (num121 + num112),
							(float) (num117 + num112), 0);
					if (this.btnrestart.paint(painter, super.game, num121,
							num117)) {
						this.trainVolume = 0f;
						this.trainVolumeTarget = 0f;

						this.loadLevel(true);
						this.paused = false;
						super.game.clearMouseStatus();
						super.game.doButtonPressSound();
					}
					num121 += num122;
				}
				if (this.success) {
					this.btnshadow.Paint(painter, (float) (num121 + num112),
							(float) (num117 + num112), 0);
					if (this.btnplay.paint(painter, super.game, num121, num117)
							|| ((this.shouldDoLevelAnim != -1) && this.ibackButtonPressed)) {
						this.ibackButtonPressed = false;
						super.game.doButtonPressSound();
						if (this.shouldDoLevelAnim != -1) {
							if (this.shouldDoLevelAnim == 0x270f) {
								super.game
										.changeState(EStates.EGameStateLevelSelect);
							} else {
								super.game
										.changeState(EStates.EGameStateMainLevelSelect);
							}
							super.game.clearMouseStatus();
						} else {
							int num123 = super.game
									.getValue(EValues.EValueSelectedLevel);
							if (super.game.isTrial() && (num123 == 5)) {
								super.game.changeState(EStates.EGameStateTrial);
							} else {
								super.game
										.setValue(EValues.EValueSelectedLevel,
												num123 + 1);
								this.levelNum = super.game
										.getValue(EValues.EValueSelectedLevel);
								if (this.levelNum == 3) {
									this.tutorialId = 2;
									this.initTutorial();
								}
								if (this.levelNum == 4) {
									this.tutorialId = 3;
									this.initTutorial();
								}
								this.levelEnded = false;
								this.loadLevel(this.tutorialId == -1);
							}
							super.game.clearMouseStatus();
						}
					}
				}
				painter.setOpacity(1.0f);
			}
			if (this.levelStartTicks != -1) {

				painter.save();
				painter.translate(-40f, 0f);
				int num124 = this.indicators.getHeight() / 5;
				int num125 = (this.indicators.getHeight() + num124) + num124;
				int num126 = (this.levelStartTicks << 10) / 20;
				if (num126 > 0x400) {
					num126 = 0x400;
				}
				int aAngle = (num126 < 0x100) ? (-12 - (((0x100 - num126) * 30) / 0x100))
						: ((this.levelStartTicks < 70) ? -12
								: (-12 + (((this.levelStartTicks - 70) * 100) / 15)));
				painter.rotate(aAngle);
				int num128 = this.getready.getHeight();
				int num129 = this.getready.getWidth() / 10;
				if (num126 < 0x100) {
					painter.setOpacity((float) (num126 / 0x100));
				}
				this.getready.Paint(painter, (float) num129, (float) num128, 0);
				painter.setOpacity(1.0f);
				num128 += this.getready.getHeight();
				int num130 = (num126 < 0x300) ? (((num126 - 0x100) * num125) >> 9)
						: num125;
				if (num130 > 0) {
					int num131 = num128 + (num125 / 2);

					int num132 = num130 - num124;
					if (num132 > 0) {
						painter.setClip(0, num131 - (num132 / 2), w * 2, num132);
						num128 += num124;
						int num133 = super.game
								.getValue(EValues.EValueSelectedLevel);
						int num134 = num133 / 10;
						int num135 = num133 % 10;
						int num136 = (num129 * 3) / 2;
						this.level.Paint(painter, (float) num136,
								(float) (num128 - 4), 0);
						num136 += this.level.getWidth()
								+ ((this.bignumbers.getWidth() * 2) / 3);
						if (num134 > 0) {
							this.bignumbers.Paint(painter, (float) num136,
									(float) num128, num134);
							num136 += this.bignumbers.getWidth();
						}
						this.bignumbers.Paint(painter, (float) num136,
								(float) num128, num135);
						num130 = (num126 < 0x400) ? (((num126 - 0x200) * num125) >> 9)
								: num125;
						if (num130 > 0) {
							num128 += (num125 * 3) / 2;
							num136 = ((num129 * 3) / 2)
									+ (this.indicators.getWidth() / 2);
							num131 = num128 + (num125 / 2);

							int num137 = num130 - num124;
							if (num137 > 0) {
								painter.setClip(0, num131 - (num137 / 2),
										w * 2, num137);
								num128 += num124
										+ (this.indicators.getHeight() / 2);
								if (this.schedule.size() > 0) {
									int num138 = this.schedule.size();
									int num139 = 0x200 / num138;
									int num140 = num126 - 0x200;
									for (ScheduleItem item : this.schedule) {
										if (item.caveid > this.caves.size()) {
											continue;
										}
										if (num140 > 0) {
											int num141 = (this.caves.size() == 0) ? 60
													: this.caves
															.get(item.caveid).subtype;
											if ((num141 >= 0) && (num141 < 3)) {
												float num142 = ((float) num140)
														/ ((float) num139);
												if (num142 > 1f) {
													num142 = 1f;
												}
												painter.setOpacity((float) num142);
												this.indicators.Paint(painter,
														(float) num136,
														(float) num128, num141);
												num136 += this.indicators
														.getWidth();
												painter.setOpacity(1.0f);
											}
										}
										num140 -= num139;
									}
								}
							}
						}
					}
					painter.removeClip();
				}
				painter.restore();
			}
		}
	}

	private void paintCaveSign(Painter painter, Tile cave, int index) {
		float num = (this.levelSet == 2) ? 0.7f : 1f;
		float angle = 15f;
		boolean flag = false;
		boolean flag2 = false;
		boolean flipped = false;
		int x = cave.x;
		int y = cave.y;
		switch (cave.type) {
		case ECaveBottom:
			flag2 = true;
			angle = -15f;
			y += this.tileh / 4;
			break;

		case ECaveLeft:
			x += (this.tilew * 2) / 3;
			if (cave.row == 0) {
				y += this.tileh / 6;
			}
			if (((cave.subtype == 3) || (cave.subtype == 5))
					|| (cave.subtype == 4)) {
				flipped = true;
			}
			break;

		case ECaveRight: {
			angle = -5f;
			if (cave.row <= 0) {
				x -= this.tilew / 3;
				y += this.tileh / 4;
				break;
			}
			Tile tile = this.getTile(cave.col - 1, cave.row - 1);
			if (tile.isLocked()) {
				angle = 12f;
				flag = true;
			}
			if (this.findCave(this.LEVEL_TILES_COLS, cave.row - 1) > -1) {
				flag2 = true;
			}
			if ((tile.type.getValue() == ETileTypes.ETileEmpty.getValue())
					|| (tile.type.getValue() >= ETileTypes.ETileCustom1
							.getValue())) {
				x -= this.tilew / 6;
			} else {
				x -= this.tilew / 0x10;
			}
			y += this.tileh / 8;
			break;
		}
		case ECaveTop:
			flag2 = true;
			y += (this.tileh * 0x6a) / 100;
			if (this.levelSet == 2) {
				y += this.tileh / 0x10;
			}
			angle = -15f;
			if (this.findCave(cave.col - 1, -1) > -1) {
				flipped = true;
				x += this.tilew + (this.tilew / 4);
			} else {
				x -= this.tilew / 5;
			}
			break;
		default:
			break;
		}
		if ((this.nextCave & (((int) 1) << index)) > 0) {
			int num5 = this.gametick - this.nextCaveChangeTick;
			float num6 = ((float) (GameUtils.sin(this.gametick << 4) * 3)) / 8092f;
			if (num5 < 10) {
				num6 = (num6 * num5) / 10f;
			}
			angle += num6;
		}
		if (caveActive.size()>0&&this.caveActive.get(index) != -1) {
			int num7 = 60 + ((Train.TILE_LENGTH * 2) / this.trainspeed);
			int num8 = ((this.gametick - this.caveActive.get(index)) << 10)
					/ num7;
			if (num8 >= 0x400) {
				this.caveActive.set(index, -1);
			} else {
				float num9 = ((float) (GameUtils.sin(this.gametick << 4) * 10)) / 8092f;
				if (num8 < 50) {
					num9 = (((num9 * 0.7f) * num8) / 50f) + (num9 * 0.3f);
				}
				if (num8 > 0x300) {
					num9 = ((0x400 - num8) * num9) / 256f;
				}
				angle += num9;
			}
		}
		float scalex = flag ? (num * 0.7f) : num;
		float scaley = flag2 ? (num * 0.8f) : num;
		this.signs.PaintScaledRotated(painter, (float) x, (float) y,
				cave.subtype, scalex, scaley, angle, flipped);
	}

	private void paintLavasplash(Painter painter, int x, int y, int mincount,
			int maxcount, int interval) {
		int startvalue = (((((x + y) % 0x200) * 0xace68) + 0xba785b) ^ 0x2bea86a) & 0xfffffff;
		GameUtils.initRandom(startvalue);
		int num2 = mincount
				+ ((GameUtils.getRandom() >> 3) % (maxcount - mincount));
		for (int i = 0; i < num2; i++) {
			int num4 = 10 + ((GameUtils.getRandom() >> 3) % 20);
			int num5 = (30 + num4) + interval;
			int num6 = this.gametick + ((GameUtils.getRandom() >> 3) % 200);
			int num7 = num6 % num5;
			int num8 = (((num6 / num5) + ((GameUtils.getRandom() >> 3) & 0xff)) * 0xab42)
					^ (GameUtils.getRandom() >> 3);
			int num9 = GameUtils.getRandomSeed();
			GameUtils.initRandom(num8);
			int num10 = ((num7 - ((GameUtils.getRandom() >> 3) % num4)) * 0x400) / 30;
			if ((num10 >= 0) && (num10 < 0x400)) {
				int frame = (GameUtils.getRandom() >> 3) % 2;
				int num12 = ((GameUtils.getRandom() >> 3) % (this.tilew * 2))
						- this.tilew;
				int num13 = ((GameUtils.getRandom() >> 3) % (this.tileh * 3))
						- this.tileh;
				num12 = (num10 > 0x300) ? num12 : ((num12 * num10) / 0x300);
				num13 = (num10 > 0x300) ? num13 : ((num13 * num10) / 0x300);
				if (num10 < 0x300) {
					num13 -= (GameUtils.sin((num10 * 180) / 0x300) * this.tileh) >> 13;
				}
				float angle = ((GameUtils.getRandom() >> 3) + ((num10 < 0x300) ? num10
						: 0x300)) / 5;
				if (num12 < 0) {
					angle = -angle;
				}
				if (num10 > 0x300) {
					painter.setOpacity(1f - ((num10 - 0x300) / 0x100));
				}
				this.lavasplash.Paint(painter, (float) (x + num12),
						(float) (y + num13), frame, angle);
				painter.setOpacity(1f);
			}
			GameUtils.initRandom(num9);
		}
	}

	public final void paintTrain(Painter painter, int x, int y, int angle,
			int type, int color, boolean shake) {
		this.paintTrain(painter, x, y, angle, type, color, shake, -1f, -1,
				false);
	}

	public final void paintTrain(Painter painter, int x, int y, int angle,
			int type, int color, boolean shake, float scale, int offset,
			boolean divide) {
		float scalex = (this.levelSet == 2) ? 0.8f : ((scale == -1f) ? 1f
				: scale);
		float scaley = scalex;
		boolean flipped = false;
		int frame = (((angle + 360) * 0x48) / 360) % 0x48;
		if (shake) {
			GameUtils.initRandom(((this.gametick / 2) * 0xabe63)
					^ ((x * 0x46cc) + (y * 0x346)));
			y += ((GameUtils.getRandom() >> 3) & 3) - 1;
		}
		if (type == 0) {
			//车头
			if (offset == -1) {
				offset = trainOffsetY;
			}
			if ((frame > 0x12) && (frame < 0x37)) {
				flipped = true;
				if (frame <= 0x24) {
					frame = 0x24 - frame;
				} else {
					frame = (0x36 - frame) + 0x13;
				}
			} else if (frame >= 0x37) {
				frame = (frame - 0x37) + 20;
			}
			if (divide) {
				float num4 = x / 10f;
				float num5 = y / 10f;
				if (color == 0) {
					this.train.PaintScaled(painter, num4, num5 - offset, frame,
							scalex, scaley, flipped);
				} else if (color == 1) {
					this.trainYellow.PaintScaled(painter, num4, num5 - offset,
							frame, scalex, scaley, flipped);
				} else {
					this.trainBlue.PaintScaled(painter, num4, num5 - offset,
							frame, scalex, scaley, flipped);
				}
			} else if (color == 0) {
				this.train.PaintScaled(painter, x, (y - offset), frame, scalex,
						scaley, flipped);
			} else if (color == 1) {
				this.trainYellow.PaintScaled(painter, x, (y - offset), frame,
						scalex, scaley, flipped);
			} else {
				this.trainBlue.PaintScaled(painter, x, (y - offset), frame,
						scalex, scaley, flipped);
			}
		} else {
			if ((frame > 0x12) && (frame < 0x25)) {
				flipped = true;
				frame = 0x24 - frame;
			} else if ((frame >= 0x25) && (frame < 0x37)) {
				frame -= 0x24;
			} else if (frame >= 0x37) {
				flipped = true;
				frame = 0x48 - frame;
			}
			if (type == 1) {
				// �?
				if (offset == -1) {
					offset = trainCoalOffsetY;
				}
				if (divide) {
					float num6 = ((float) x) / 10f;
					float num7 = ((float) y) / 10f;
					this.coal.PaintScaled(painter, num6, num7 - offset, frame,
							scalex, scaley, flipped);
				}
				this.coal.PaintScaled(painter, x, (y - offset), frame, scalex,
						scaley, flipped);
			} else {
				//后坐
				if (offset == -1) {
					offset = trainCarOffsetY;
				}
				if (divide) {
					float num8 = ((float) x) / 10f;
					float num9 = ((float) y) / 10f;
					if (color == 0) {
						this.carriage.PaintScaled(painter, num8, num9 - offset,
								frame, scalex, scaley, flipped);
					} else if (color == 1) {
						this.carriageYellow.PaintScaled(painter, num8, num9
								- offset, frame, scalex, scaley, flipped);
					} else {
						this.carriageBlue.PaintScaled(painter, num8, num9
								- offset, frame, scalex, scaley, flipped);
					}
				} else if (color == 0) {
					this.carriage.PaintScaled(painter, (float) x,
							(float) (y - offset), frame, scalex, scaley,
							flipped);
				} else if (color == 1) {
					this.carriageYellow.PaintScaled(painter, (float) x,
							(float) (y - offset), frame, scalex, scaley,
							flipped);
				} else {
					this.carriageBlue.PaintScaled(painter, (float) x,
							(float) (y - offset), frame, scalex, scaley,
							flipped);
				}
			}
		}
	}

}