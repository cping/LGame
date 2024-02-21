package com.mygame;

import loon.utils.reply.ObjRef;

public class Train extends Entity {
	public int angle;
	public static int CARRIAGE_GAP = 0x44c;
	private int crashAngle;
	public boolean crashed;
	private int crashTargetAngle;
	public boolean crashToLava;
	private int crashx;
	private int crashy;
	private EDirections enterDir;
	public boolean enteredWorld;
	private EDirections exitDir;
	public boolean finished;
	public int levelset;
	public static int MIN_TRAIN_SPEED = 40;

	public int pos;
	public boolean shouldStop;
	public static int SOFT_CRASH_TICKS = 30;
	public int softcrashticks;
	public int speed;
	private boolean stopped;
	public Tile tile;
	public static int TILE_CORNER_LENGTH = 0x514;
	public static int TILE_LENGTH = 0x5dc;
	private int tilelength;
	private Train trail;
	public static int TRAIN_DESCEND_CHANGE_TIME = 4;
	public static int TRAIN_INSIDE_TILE = 0x270f;
	private int yoffs;

	public Train(int aLevelset) {
		this.levelset = aLevelset;
		this.crashed = false;
		this.pos = 0;
		this.tile = null;
		this.speed = MIN_TRAIN_SPEED;
		this.trail = null;
		this.softcrashticks = -1;
		this.enteredWorld = false;
		this.finished = false;
		this.stopped = false;
		this.shouldStop = false;
		this.yoffs = 0;
	}

	public EEntityClass getEntityType() {
		return EEntityClass.EEntityTrain;
	}

	public static void getPos(ObjRef<Integer> x,
		ObjRef<Integer> y, ObjRef<Integer> angle,
			int tilex, int tiley, ETileTypes tiletype, int drawpos, int tilew,
			int tileh, int tilelength, EDirections enterDir) {
		x.set( tilex);
		y.set( tiley);
		if (tiletype == ETileTypes.ETileCross) {
			if ((enterDir == EDirections.EDirTop)
					|| (enterDir == EDirections.EDirDown)) {
				tiletype = ETileTypes.ETileTopDown;
			} else {
				tiletype = ETileTypes.ETileLeftRight;
			}
		}
		switch (tiletype) {
		case ETileLeftRight:
		case ETileBridgeHorizontal:
		case ECaveLeft:
		case ECaveRight:

			y.add( tileh / 2);
			if (enterDir == EDirections.EDirLeft) {
				drawpos = tilelength - drawpos;
				angle.set( 180);
			} else {
				angle.set( 0);
			}
			x.add( (tilew * drawpos) / tilelength);

			return;

		case ETileTopDown:
		case ETileBridgeVertical:
		case ECaveBottom:
		case ECaveTop:
		
			x.add( tilew / 2);
			if (enterDir == EDirections.EDirTop) {
				drawpos = tilelength - drawpos;
				angle.set( 90);
			} else {
				angle.set( 270);
			}
			y.add( (tileh * drawpos) / tilelength);
			return;

		case ETileTopRight:

			if (enterDir != EDirections.EDirLeft) {
				angle.set( 270 + ((90 * drawpos) / tilelength));
				break;
			}
			angle.set( 180 - ((90 * drawpos) / tilelength));
			drawpos = tilelength - drawpos;
			x.add( tilew);
			y.add( (GameUtils.sin((drawpos * 90) / tilelength) * tileh) >> 14);
			x.sub( (GameUtils.cos((drawpos * 90) / tilelength) * tilew) >> 14);
	
			return;

		case ETileRightDown:
			
			if (enterDir != EDirections.EDirLeft) {
				angle.set( 90 - ((90 * drawpos) / tilelength));
			} else {
				angle.set( 180 + ((90 * drawpos) / tilelength));
				drawpos = tilelength - drawpos;
			}
			x.add( tilew);
			y.add( tileh);
			y.sub( (GameUtils.sin((drawpos * 90) / tilelength) * tileh) >> 14);
			x.sub( (GameUtils.cos((drawpos * 90) / tilelength) * tilew) >> 14);
			return;

		case ETileDownLeft:
		
			if (enterDir != EDirections.EDirRight) {
				angle.set( 90 + ((90 * drawpos) / tilelength));
			} else {
				angle.set( 360 - ((90 * drawpos) / tilelength));
				drawpos = tilelength - drawpos;
			}
			y.add( tileh);
			y.sub( (GameUtils.sin((drawpos * 90) / tilelength) * tileh) >> 14);
			x.add( (GameUtils.cos((drawpos * 90) / tilelength) * tilew) >> 14);
			return;

		case ETileLeftTop:
		
			if (enterDir != EDirections.EDirRight) {
				angle.set( 270 - ((90 * drawpos) / tilelength));
			} else {
				angle.set( (90 * drawpos) / tilelength);
				drawpos = tilelength - drawpos;
			}
			y.add( (GameUtils.sin((drawpos * 90) / tilelength) * tileh) >> 14);
			x.add( (GameUtils.cos((drawpos * 90) / tilelength) * tilew) >> 14);
			return;

		default:

			x.add( tilew / 2);
			y.add( tileh / 2);
			return;
		}
		x.add( tilew);
		y.add( (GameUtils.sin((drawpos * 90) / tilelength) * tileh) >> 14);
		x.sub( (GameUtils.cos((drawpos * 90) / tilelength) * tilew) >> 14);
	}

	public Tile GetTile() {
		return this.tile;
	}

	public boolean isFinished() {
		return this.finished;
	}

	public void setCrashed(boolean soft) {
		this.crashed = true;
		if (soft) {
			this.softcrashticks = 0;
			this.pos = 0;
			this.crashx = super.x;
			this.crashy = super.y;
			this.crashAngle = this.angle;
			GameUtils
					.initRandom((int) ((0xe25beaceL) ^ (super.x + (super.y * 0xe246a))));
			this.crashTargetAngle = ((GameUtils.getRandom() >> 3) % 70) - 0x23;
			if (this.trail != null) {
				if (this.trail.stopped) {
					this.trail.start();
					this.trail.updatePos();
				}
				this.trail.setCrashed(true);
			}
		} else {
			this.stop(true);
		}
	}

	public void setFinished() {
		this.finished = true;
	}

	public int SetTile(Tile aTile) {
		if ((aTile.whereGoesFromDir(this.exitDir) == EDirections.TRAIN_CRASH)
				|| ((super.type == 0) && aTile.isLocked())) {
			if ((aTile.isLocked() && (aTile.type != ETileTypes.ETileBridgeHorizontal))
					&& (aTile.type != ETileTypes.ETileBridgeVertical)) {
				this.setCrashed(false);
				return -1;
			}
			this.enterDir = this.exitDir;
			if (this.tile.bridgeDropped()) {
				this.crashToLava = true;
			} else {
				this.crashToLava = false;
			}
			this.setCrashed(true);
			return -2;
		}
		if (((this.trail == null) && (this.tile != null))
				&& !this.tile.IsCave()) {
			this.tile.setLocked(false);
		}
		if (this.pos > 0) {
			this.pos -= this.tilelength;
		}
		this.tile = aTile;
		if (!this.tile.IsCave()) {
			this.tile.setLocked(true);
			this.enteredWorld = true;
		}
		if (this.tile.IsCave() && !this.enteredWorld) {
			switch (tile.type) {
			case ECaveBottom:
				this.enterDir = EDirections.EDirTop;
				this.exitDir = this.enterDir;
				switch (this.tile.type) {
				case ETileLeftTop:
				case ETileTopRight:
				case ETileRightDown:
				case ETileDownLeft:
					this.tilelength = TILE_CORNER_LENGTH;
					break;

				default:
					this.tilelength = TILE_LENGTH;
					break;
				}
				this.updatePos();
				break;

			case ECaveLeft:
				this.enterDir = EDirections.EDirRight;
				this.exitDir = this.enterDir;
				switch (this.tile.type) {
				case ETileLeftTop:
				case ETileTopRight:
				case ETileRightDown:
				case ETileDownLeft:
					this.tilelength = TILE_CORNER_LENGTH;
					break;

				default:
					this.tilelength = TILE_LENGTH;
					break;
				}
				this.updatePos();
				break;

			case ECaveRight:
				this.enterDir = EDirections.EDirLeft;
				this.exitDir = this.enterDir;
				switch (this.tile.type) {
				case ETileLeftTop:
				case ETileTopRight:
				case ETileRightDown:
				case ETileDownLeft:
					this.tilelength = TILE_CORNER_LENGTH;
					break;

				default:
					this.tilelength = TILE_LENGTH;
					break;
				}
				this.updatePos();
				break;

			case ECaveTop:
				this.enterDir = EDirections.EDirDown;
				this.exitDir = this.enterDir;
				switch (this.tile.type) {
				case ETileLeftTop:
				case ETileTopRight:
				case ETileRightDown:
				case ETileDownLeft:
					this.tilelength = TILE_CORNER_LENGTH;
					break;

				default:
					this.tilelength = TILE_LENGTH;
					break;
				}
				this.updatePos();
				break;
			default:
				break;
			}
		} else {
			this.enterDir = this.exitDir;
			this.exitDir = this.tile.whereGoesFromDir(this.enterDir);
		}
		return 0;

	}

	public void setTrail(Train aTrail) {
		this.trail = aTrail;
	}

	public void start() {
		this.stopped = false;
	}

	public void stop() {
		this.stop(false);
	}

	public void stop(boolean recursive) {
		this.stopped = true;
		if (recursive && (this.trail != null)) {
			this.trail.stop(true);
		}
	}

	public int Tick() {
		if ((this.tile != null) && !this.stopped) {
			if (this.crashed) {
				this.updateCrashPos();
			} else {
				if (this.shouldStop) {
					this.speed -= 4;
					if (this.speed < 0) {
						this.speed = 0;
					}
				}
				this.pos += this.speed;
				if ((this.tile.IsCave() && (this.trail != null))
						&& ((this.pos > CARRIAGE_GAP) && this.trail.stopped)) {
					this.trail.start();
				}
				this.updatePos();
				if (this.pos >= this.tilelength) {
					return this.exitDir.getValue();
				}
			}
		}
		return TRAIN_INSIDE_TILE;
	}

	private void updateCrashPos() {
		this.softcrashticks++;
		if (this.softcrashticks < SOFT_CRASH_TICKS) {
			int num = (this.speed * (SOFT_CRASH_TICKS - this.softcrashticks))
					/ SOFT_CRASH_TICKS;
			this.pos += num;
			int num2 = (((GameUtils.cos(this.crashAngle) * this.pos) * Tile
					.getTileWReal()) >> 13) / TILE_LENGTH;
			int num3 = -(((GameUtils.sin(this.crashAngle) * this.pos) * Tile
					.getTileHReal()) >> 13) / TILE_LENGTH;
			super.x = this.crashx + num2;
			super.y = this.crashy + num3;
			if (this.crashToLava && (super.type == 0)) {
				super.y += (this.softcrashticks * Tile.getTileHReal())
						/ (SOFT_CRASH_TICKS * 6);
			} else if (this.softcrashticks < (SOFT_CRASH_TICKS / 2)) {
				this.angle = this.crashAngle
						+ (((this.crashTargetAngle * this.softcrashticks) * 2) / SOFT_CRASH_TICKS);
			} else {
				this.angle = this.crashAngle + this.crashTargetAngle;
			}
		}
	}

	private void updatePos() {
		if (this.tile != null) {
			int tilew = Tile.getTileWReal();
			int tileh = Tile.getTileHReal();
			ObjRef<Integer> tempRef_x = new ObjRef<Integer>(
					this.x);
			ObjRef<Integer> tempRef_y = new ObjRef<Integer>(
					this.y);
			ObjRef<Integer> tempRef_angle = new ObjRef<Integer>(
					this.angle);

			getPos(tempRef_x, tempRef_y, tempRef_angle, this.tile.x,
					this.tile.y, this.tile.type, this.pos, tilew, tileh,
					this.tilelength, this.enterDir);

			super.x = tempRef_x.get();
			super.y = tempRef_y.get();
			this.angle = tempRef_angle.get();
			if (((this.tile.type != ETileTypes.ECaveBottom) && (this.tile.type != ETileTypes.ECaveTop))
					&& ((this.tile.type != ETileTypes.ECaveLeft) && (this.tile.type != ETileTypes.ECaveRight))) {
				this.yoffs++;
				if (this.yoffs > TRAIN_DESCEND_CHANGE_TIME) {
					this.yoffs = TRAIN_DESCEND_CHANGE_TIME;
				}
			} else if (this.yoffs > 0) {
				this.yoffs--;
			}
			super.y += (tileh * this.yoffs) / (TRAIN_DESCEND_CHANGE_TIME * 15);
			super.x++;
		}
	}
}