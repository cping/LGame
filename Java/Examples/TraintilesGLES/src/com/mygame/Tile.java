/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
package com.mygame;

public class Tile {
	private static int bridgeHeight;
	private static int bridgeOffs;
	private static int caveBottomYOffset;
	private static Sprite caves = null;
	private static int caveYOffset;

	private int changeTicks = -1;
	public int col;
	private static int currentTileset;

	private boolean dropReverse;
	public int dropTick = -1;

	public int lavaborders = 0;
	private static Sprite lavabridge;
	private static Sprite lavabridge2;
	private static Sprite lavabridgeshadow;
	private static Sprite lavacorner;
	private static Sprite lavacorner2;
	private static Sprite lavatiles;
	private static Sprite lavatileshoriz;

	private int lockAmount = 0;
	private boolean locked = false;

	private static int pressLowerY;
	private ETileTypes previoustype;
	public int row;
	public int subtype;
	private static int tileHReal;
	private static Sprite tiles = null;
	private static int tileWReal;
	public ETileTypes type;
	public int x;
	public int y;

	public final boolean bridgeDropped() {
		return (this.dropTick == -1);
	}

	private static void clearTiles() {
		if (tiles != null) {
			tiles = null;
		}
	}

	public static int getTileH() {
		return tiles.getHeight();
	}

	public static int getTileHReal() {
		return tileHReal;
	}

	public static int getTileW() {
		return tiles.getWidth();
	}

	public static int getTileWReal() {
		return tileWReal;
	}

	public static void initTiles(int tileset) {
		if (caves == null) {
			caves = new Sprite("caves", 8, 1, 9, true);
			lavabridge = new Sprite("lavabridge", 1, 1, 9, true);
			lavabridge2 = new Sprite("lavabridge2", 1, 1, 9, true);
			lavabridgeshadow = new Sprite("lavabridgeshadow", 1, 1, 9, true);
			lavatiles = new Sprite("lavatiles", 2, 1, 9, true);
			lavatileshoriz = new Sprite("lavatileshoriz", 1, 2, 9, true);
			lavacorner = new Sprite("lavacorner", 1, 1, 9, true);
			lavacorner2 = new Sprite("lavacorner2", 1, 1, 0x21, true);
		}
		if ((tiles == null) || (tileset != currentTileset)) {
			currentTileset = tileset;
			clearTiles();
			bridgeOffs = 0;
			if (currentTileset == 0) {
				tiles = new Sprite("tiles", 11, 1, 0x12, false);
			} else if (currentTileset == 1) {
				tiles = new Sprite("tiles2", 11, 1, 0x12, false);
			} else {
				tiles = new Sprite("tiles3", 11, 1, 0x12, false);
				bridgeOffs = (tiles.getWidth() - (lavabridge.getWidth() * 3)) / 6;
				bridgeHeight = lavabridge2.getHeight() - 2;
			}
			pressLowerY = tiles.getHeight() / 8;
			int num = (currentTileset == 2) ? ((caves.getHeight() * 8) / 10)
					: (caves.getHeight() + 8);
			caveYOffset = num - tiles.getHeight();
			caveBottomYOffset = (num * 0x53) / 260;
			tileWReal = (tiles.getWidth() * 260) / 0x11c;
			tileHReal = (tiles.getHeight() * 0xd5) / 0xf8;
		}
	}

	public final void InitWithString(String value) {
		String[] strArray = value.split("[,]", -1);
		this.col = Integer.parseInt(strArray[0]);
		this.row = Integer.parseInt(strArray[1]);
		this.x = Integer.parseInt(strArray[2]);
		this.y = Integer.parseInt(strArray[3]);
		this.type = ETileTypes.forValue(Integer.parseInt(strArray[4]));
		this.subtype = Integer.parseInt(strArray[5]);
	}

	public final boolean IsCave() {
		return ((this.type.getValue() >= ETileTypes.ECaveBottom.getValue()) && (this.type
				.getValue() <= ETileTypes.ECaveTop.getValue()));
	}

	public final boolean isLavaEmptyTile() {
		if ((this.type.getValue() != ETileTypes.ETileCustom3.getValue())
				&& (this.type.getValue() != ETileTypes.ETileBridgeHorizontal
						.getValue())) {
			return (this.type.getValue() == ETileTypes.ETileBridgeVertical
					.getValue());
		}
		return true;
	}

	public final boolean isLocked() {
		return this.locked;
	}

	public final void Paint(Painter painter, int layer) {
		this.Paint(painter, layer, 0, false, false);
	}

	public final void Paint(Painter painter, int layer, int tick,
			boolean paused, boolean lavalevel) {
		int y = this.y;
		int type = this.type.getValue();
		if (!paused) {
			if ((this.isLocked() && (this.type != ETileTypes.ETileBridgeHorizontal))
					&& (this.type != ETileTypes.ETileBridgeVertical)) {
				this.lockAmount++;
				if (this.lockAmount > 4) {
					this.lockAmount = 4;
				}
			} else if (this.lockAmount > 0) {
				this.lockAmount--;
			}
		}
		y += (tileHReal * this.lockAmount) / 40;
		if (this.lavaborders != 0) {
			if ((this.lavaborders & 1) > 0) {
				lavatileshoriz.Paint(painter,
						(float) (this.x - ((tileWReal * 5) / 100)),
						(float) (this.y - ((tileWReal * 2) / 100)), 0);
			}
			if ((this.lavaborders & 8) > 0) {
				lavatiles.Paint(painter, (float) (this.x - 2), (float) this.y,
						0);
				if ((this.lavaborders & 1) > 0) {
					lavacorner.Paint(painter, (float) this.x,
							(float) (this.y + ((tileHReal * 2) / 100)), 0);
				}
			}
			if ((this.lavaborders & 2) > 0) {
				lavatiles
						.Paint(painter,
								(float) ((this.x + ((tileWReal * 100) / 100)) - lavatiles
										.getWidth()), (float) this.y, 1);
				if ((this.lavaborders & 1) > 0) {
					lavacorner.PaintScaled(painter,
							(float) (((this.x + tileWReal) - lavacorner
									.getWidth()) - 4),
							(float) (this.y + ((tileHReal * 4) / 100)), 0, 1f,
							1f, true);
				}
			}
			if ((this.lavaborders & 4) > 0) {
				lavatileshoriz
						.Paint(painter,
								(float) this.x,
								(float) ((this.y + ((tileHReal * 0x6c) / 100)) - ((lavatileshoriz
										.getHeight() * 80) / 100)), 1);
				if ((this.lavaborders & 8) > 0) {
					lavacorner2.Paint(painter,
							(float) (this.x + ((tileWReal * 5) / 100)),
							(float) (this.y + ((tileHReal * 0x6c) / 100)), 0);
				}
				if ((this.lavaborders & 2) > 0) {
					lavacorner2.PaintScaled(painter,
							(float) (((this.x + tileWReal) - lavacorner2
									.getWidth()) - 4),
							(float) (this.y + ((tileHReal * 0x6c) / 100)), 0,
							1f, 1f, true);
				}
			}
		}
		if (this.changeTicks != -1) {
			if (!paused) {
				this.changeTicks++;
			}
			if (this.changeTicks < 4) {
				float angle = (this.changeTicks * 90) / 4;
				if (angle < 45f) {
					type = this.previoustype.getValue();
				} else {
					angle -= 90f;
				}
				y += pressLowerY
						- ((pressLowerY * GameUtils
								.sin((this.changeTicks * 90) / 4)) >> 13);
				tiles.Paint(painter, (float) (this.x + (tileWReal / 2)),
						(float) (y + (tileHReal / 2)), type, angle);
				return;
			}
			this.changeTicks = -1;
		}
		if (this.IsCave()) {
			type -= 0x18;
			int num4 = this.y;
			if (this.type == ETileTypes.ECaveBottom) {
				num4 -= caveBottomYOffset;
			}
			if (currentTileset == 2) {
				if (layer == 0) {
					caves.PaintScaled(painter, (float) this.x,
							(float) (num4 - caveYOffset), type, 0.8f, 0.8f);
				} else {
					caves.PaintScaled(painter, (float) this.x,
							(float) (num4 - caveYOffset), type + 4, 0.8f, 0.8f);
				}
			} else {
				int x = this.x;
				if (this.type == ETileTypes.ECaveTop) {
					x -= 3;
				}
				if (layer == 0) {
					caves.Paint(painter, (float) x,
							(float) (num4 - caveYOffset), type);
				} else {
					caves.Paint(painter, (float) x,
							(float) (num4 - caveYOffset), type + 4);
				}
			}
		} else {
			if (currentTileset == 2) {
				if (type == 11) {
					type = -1;
				} else if ((type >= 12) && (type <= 13)) {
					type = 0;
				} else if ((type >= 14) && (type <= 0x15)) {
					type = 0;
				}
				if (!this.isLavaEmptyTile() && (this.dropTick != -1)) {
					if ((tick - this.dropTick) > 15) {
						this.dropTick = -1;
					} else {
						GameUtils
								.initRandom(((tick / 3) * 0xabe80)
										^ ((this.x * 0x46cc) + (this.dropTick * 0x346)));
						y += ((GameUtils.getRandom() >> 3) & 3) - 1;
					}
				}
			} else if ((type >= 11) && (type <= 0x15)) {
				type = 0;
			}
			switch (type) {
			case 0x16: {
				type = -1;
				boolean shake = this.isLocked() || (this.dropTick != -1);
				GameUtils.initRandom(((tick / 2) * 0xabe63)
						^ ((this.x * 0x46cc) + (this.y * 0x346)));
				int droppos = (this.dropTick != -1) ? (tick - this.dropTick)
						: 0;
				if (this.dropReverse) {
					droppos -= 20;
				}
				int num7 = this.x + bridgeOffs;
				paintBridgepart(painter, num7, y, droppos, false, shake);
				droppos -= this.dropReverse ? -10 : 10;
				num7 += lavabridge.getWidth();
				paintBridgepart(painter, num7, y, droppos, false, shake);
				droppos -= this.dropReverse ? -10 : 10;
				num7 += lavabridge.getWidth();
				paintBridgepart(painter, num7, y, droppos, false, shake);
				break;
			}
			case 0x17: {
				type = -1;
				boolean flag2 = this.isLocked() || (this.dropTick != -1);
				GameUtils.initRandom(((tick / 2) * 0xabe63)
						^ ((this.x * 0x46cc) + (this.y * 0x346)));
				int num8 = (this.dropTick != -1) ? (tick - this.dropTick) : 0;
				if (this.dropReverse) {
					num8 -= 20;
				}
				y -= 4;
				int num9 = this.x + 5;
				paintBridgepart(painter, num9, y, num8, true, flag2);
				y += bridgeHeight;
				num8 -= this.dropReverse ? -10 : 10;
				paintBridgepart(painter, num9, y, num8, true, flag2);
				y += bridgeHeight;
				num8 -= this.dropReverse ? -10 : 10;
				paintBridgepart(painter, num9, y, num8, true, flag2);
				break;
			}
			}
			if (type != -1) {
				tiles.Paint(painter, (float) (this.x + (tileWReal / 2)),
						(float) (y + (tileHReal / 2)), type);
			}
		}
	}

	private static void paintBridgepart(Painter painter, int x, int y,
			int droppos, boolean vertical, boolean shake) {
		if (droppos < 0) {
			droppos = 0;
		}
		if (droppos < 20) {
			int num = 0;
			int num2 = tileHReal / 2;
			if ((droppos > 0) && (droppos < 10)) {
				num = (num2 * droppos) / 10;
			} else if (droppos >= 10) {
				num = num2;
				painter.setOpacity((float) (1f - (((float) (droppos - 10)) / 10f)));
			}
			float angle = 0f;
			if (droppos > 0) {
				int num4 = ((((x * 0x25abe) ^ (y * 0x32bee)) >> 5) % 40) - 20;
				angle += (droppos * num4) / 20;
			}
			num += shake ? (((GameUtils.getRandom() >> 3) & 3) - 1) : 0;
			if (!vertical && (droppos < 10)) {
				lavabridgeshadow.Paint(painter, (float) x, (float) (y + num2),
						0, angle);
			}
			if (vertical) {
				lavabridge2.Paint(painter, (float) x, (float) (y + num), 0,
						angle);
			} else {
				lavabridge.Paint(painter, (float) x, (float) (y + num), 0,
						angle);
			}
			painter.setOpacity(1f);
		}
	}

	public final int rotate() {
		if (!this.locked) {
			ETileTypes type = this.type;
			switch (this.type) {
			case ETileLeftRight:
				type = ETileTypes.ETileTopDown;
				break;

			case ETileTopDown:
				type = ETileTypes.ETileLeftRight;
				break;

			case ETileTopRight:
				type = ETileTypes.ETileRightDown;
				break;

			case ETileRightDown:
				type = ETileTypes.ETileDownLeft;
				break;

			case ETileDownLeft:
				type = ETileTypes.ETileLeftTop;
				break;

			case ETileLeftTop:
				type = ETileTypes.ETileTopRight;
				break;
			default:
				break;
			}
			if (type != this.type) {
				this.previoustype = this.type;
				this.type = type;
				this.changeTicks = 0;
			}
		}
		return 0;
	}

	public final void setLocked(boolean aLocked) {
		if (this.dropTick == -1) {
			this.locked = aLocked;
		}
	}

	public final void trainLeftTile(EDirections dir, int tick) {
		if (tick == -1) {
			this.dropTick = -1;
			this.locked = false;
		} else if ((this.type == ETileTypes.ETileBridgeHorizontal)
				|| (this.type == ETileTypes.ETileBridgeVertical)) {
			this.dropTick = tick;
			if ((dir == EDirections.EDirLeft) || (dir == EDirections.EDirTop)) {
				this.dropReverse = true;
			} else {
				this.dropReverse = false;
			}
			this.locked = true;
		}
	}

	public final EDirections whereGoesFromDir(EDirections dir) {
		switch (this.type) {
		case ETileLeftRight:
		case ETileBridgeHorizontal:
			if (dir == EDirections.EDirRight) {
				return EDirections.EDirRight;
			}
			if (dir != EDirections.EDirLeft) {
				break;
			}
			return EDirections.EDirLeft;

		case ETileTopDown:
		case ETileBridgeVertical:
			if (dir == EDirections.EDirTop) {
				return EDirections.EDirTop;
			}
			if (dir != EDirections.EDirDown) {
				break;
			}
			return EDirections.EDirDown;

		case ETileTopRight:
			if (dir != EDirections.EDirDown) {
				if (dir != EDirections.EDirLeft) {
					break;
				}
				return EDirections.EDirTop;
			}
			return EDirections.EDirRight;

		case ETileRightDown:
			if (dir != EDirections.EDirTop) {
				if (dir != EDirections.EDirLeft) {
					break;
				}
				return EDirections.EDirDown;
			}
			return EDirections.EDirRight;

		case ETileDownLeft:
			if (dir != EDirections.EDirTop) {
				if (dir != EDirections.EDirRight) {
					break;
				}
				return EDirections.EDirDown;
			}
			return EDirections.EDirLeft;

		case ETileLeftTop:
			if (dir != EDirections.EDirDown) {
				if (dir != EDirections.EDirRight) {
					break;
				}
				return EDirections.EDirTop;
			}
			return EDirections.EDirLeft;

		case ETileCross:
			if (dir != EDirections.EDirTop) {
				if (dir == EDirections.EDirDown) {
					return EDirections.EDirDown;
				}
				if (dir == EDirections.EDirLeft) {
					return EDirections.EDirLeft;
				}
				if (dir == EDirections.EDirRight) {
					return EDirections.EDirRight;
				}
				break;
			}
			return EDirections.EDirTop;

		case ECaveBottom:
			return EDirections.EDirDown;

		case ECaveLeft:
			return EDirections.EDirLeft;

		case ECaveRight:
			return EDirections.EDirRight;

		case ECaveTop:
			return EDirections.EDirTop;

		default:
			return EDirections.TRAIN_CRASH;
		}
		return EDirections.TRAIN_CRASH;
	}
}
