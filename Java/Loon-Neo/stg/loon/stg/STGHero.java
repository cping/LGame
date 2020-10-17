package loon.stg;

import loon.LRelease;
import loon.action.map.AStarFindHeuristic;
import loon.action.map.AStarFinder;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.events.GameTouch;
import loon.events.SysKey;
import loon.geom.Vector2f;
import loon.utils.TArray;
import loon.utils.timer.LTimer;


public abstract class STGHero extends STGObject {

	private static boolean moveStop;

	private boolean shoting;

	protected int maxHp = 10;

	protected int maxMp = 10;

	protected int explosion = -1;

	protected int lastHp = maxHp;

	protected int lastMp = maxMp;

	protected int mpCount = 0;

	protected int shotOB = 0;

	protected int shotOBLimit = 20;

	protected int limit = 8;

	private String heroShot, damagedEffect;

	public static class HeroTouch implements LRelease {

		private AStarFindHeuristic heuristic = null;

		private Field2D field2D;

		private boolean allDirection;

		private TArray<Vector2f> findPath = new TArray<Vector2f>();

		private int startX, startY, endX, endY, moveX, moveY;

		private int direction, speed, touchX, touchY;

		private int width, height, tileWidth, tileHeight;

		private int readerWidth, readerHeight;

		protected final static int BLOCK_SIZE = 32;

		private boolean isComplete, isVisible;

		private LTimer timer;

		private STGHero heroObject;

		public HeroTouch(STGHero heroObject, int maxWidth, int maxHeight,
				boolean all) {
			this.direction = Config.EMPTY;
			this.heroObject = heroObject;
			this.readerWidth = maxWidth;
			this.readerHeight = maxHeight;
			this.timer = new LTimer(0);
			this.isVisible = true;
			this.isComplete = false;
			this.allDirection = all;
			if (heroObject != null) {
				this.width = heroObject.getHitW() < BLOCK_SIZE ? BLOCK_SIZE
						: heroObject.getHitW();
				this.height = heroObject.getHitH() < BLOCK_SIZE ? BLOCK_SIZE
						: heroObject.getHitH();
			} else {
				this.width = BLOCK_SIZE;
				this.height = BLOCK_SIZE;
			}

			this.tileWidth = width;
			this.tileHeight = height;
			int w = maxWidth / this.tileWidth;
			int h = maxHeight / this.tileHeight;
			this.field2D = new Field2D(new int[h][w], this.tileWidth,
					this.tileHeight);
			this.speed = 4;
		}

		public AStarFindHeuristic getHeuristic() {
			return heuristic;
		}

		public void setHeuristic(AStarFindHeuristic heuristic) {
			this.heuristic = heuristic;
		}

		public void updateMove() {
		
			if (!heroObject.contains(touchX, touchY)) {
				if (findPath != null) {
					findPath.clear();
				}
				
				findPath = AStarFinder.find(heuristic, field2D,
						field2D.pixelsToTilesWidth((int)heroObject.getX()),
						field2D.pixelsToTilesHeight((int)heroObject.getY()),
						field2D.pixelsToTilesWidth(touchX),
						field2D.pixelsToTilesHeight(touchY), allDirection);

			} else if (findPath != null) {
				findPath.clear();
			}
		}

		public void onTouch(GameTouch e) {
			if (!moveStop) {
				this.touchX = e.x();
				this.touchY = e.y();
				this.updateMove();
			}
		}

		public void update(long elapsedTime) {
			if (field2D == null || findPath == null) {
				return;
			}
			if (isComplete()) {
				return;
			}
			if (timer.action(elapsedTime)) {
				if (endX == startX && endY == startY) {
					if (findPath != null) {
						if (findPath.size > 1) {
							Vector2f moveStart = findPath.get(0);
							Vector2f moveEnd = findPath.get(1);
							startX = field2D.tilesToWidthPixels(moveStart.x());
							startY = field2D.tilesToHeightPixels(moveStart.y());
							endX = moveEnd.x() * field2D.getTileWidth();
							endY = moveEnd.y() * field2D.getTileHeight();
							moveX = moveEnd.x() - moveStart.x();
							moveY = moveEnd.y() - moveStart.y();
							direction = Field2D.getDirection(moveX, moveY);
							findPath.removeIndex(0);
						} else {
							findPath.clear();
						}
					}
				}
				switch (direction) {
				case Config.TUP:
					startY -= speed;
					if (startY < endY) {
						startY = endY;
					}
					break;
				case Config.TDOWN:
					startY += speed;
					if (startY > endY) {
						startY = endY;
					}
					break;
				case Config.TLEFT:
					startX -= speed;
					if (startX < endX) {
						startX = endX;
					}
					break;
				case Config.TRIGHT:
					startX += speed;
					if (startX > endX) {
						startX = endX;
					}
					break;
				case Config.UP:
					startX += speed;
					startY -= speed;
					if (startX > endX) {
						startX = endX;
					}
					if (startY < endY) {
						startY = endY;
					}
					break;
				case Config.DOWN:
					startX -= speed;
					startY += speed;
					if (startX < endX) {
						startX = endX;
					}
					if (startY > endY) {
						startY = endY;
					}
					break;
				case Config.LEFT:
					startX -= speed;
					startY -= speed;
					if (startX < endX) {
						startX = endX;
					}
					if (startY < endY) {
						startY = endY;
					}
					break;
				case Config.RIGHT:
					startX += speed;
					startY += speed;
					if (startX > endX) {
						startX = endX;
					}
					if (startY > endY) {
						startY = endY;
					}
					break;
				}
				heroObject.setLocation(startX, startY);
			}
		}

		public long getDelay() {
			return timer.getDelay();
		}

		public void setDelay(long d) {
			timer.setDelay(d);
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getDirection() {
			return direction;
		}

		public int getSpeed() {
			return speed;
		}

		public void setSpeed(int speed) {
			this.speed = speed;
		}

		public boolean isComplete() {
			return findPath == null || findPath.size == 0 || isComplete;
		}

		public void setComplete(boolean c) {
			this.isComplete = true;
		}

		public boolean isVisible() {
			return isVisible;
		}

		public void setVisible(boolean v) {
			this.isVisible = v;
		}

		@Override
		public void close() {
			if (findPath != null) {
				findPath.clear();
				findPath = null;
			}
		}

		public int getReaderHeight() {
			return readerHeight;
		}

		public int getReaderWidth() {
			return readerWidth;
		}

	}

	public STGHero(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		super.attribute = STGScreen.NO_HIT;
		super.speed = 8;
	}

	public void stop() {
		moveStop = true;
	}

	public void reset() {
		moveStop = false;
	}

	public int getHP() {
		return this.lastHp;
	}

	public int getMP() {
		return this.lastMp;
	}

	public void upLastHp(int hp) {
		this.lastHp += hp;
		if (this.lastHp > maxHp) {
			this.lastHp = maxHp;
		}

	}

	public void upLastMp(int mp) {
		this.lastMp += mp;
		if (this.lastMp > maxMp) {
			this.lastMp = maxMp;
		}

	}

	public abstract void onMove();

	@Override
	public void update() {
		onMove();
		if (super.attribute == 0) {
			++this.mpCount;
			if (this.mpCount % 500 == 0 && this.getMP() < 10) {
				++this.lastMp;
			}
			this.moveInputs();
			STGHero.moveStop = false;
		} else {
			if (this.explosion == -1) {
				this.moveInputs();
				STGHero.moveStop = false;
				move(0, -speed);
				if (getY() < getScreenHeight() - 100) {
					super.attribute = STGScreen.HERO;
					this.explosion = 0;
					setPlaneView(true);
					this.count = 0;
					return;
				}
			} else {
				STGHero.moveStop = true;
				this.scrollMove();
				if (this.count == 0) {
					if (damagedEffect != null) {
						addBombHero(damagedEffect);
					} else {
						onDamaged();
					}
				} else if (this.count > 3 || getY() > getScreenHeight()) {
					this.explosion = -1;
					--this.lastHp;
					if (this.lastHp == 0) {
						delete();
					}
					return;
				}
			}
			++this.count;
			if (this.count % 2 == 0) {
				setPlaneView(false);
			} else {
				setPlaneView(true);
			}
		}
	}

	protected void moveInputs() {
		switch (SysKey.getKeyCode()) {
		case SysKey.LEFT:
			if (getX() > limit) {
				move(-speed, 0);
			}
			break;
		case SysKey.RIGHT:
			if (getX() < getScreenWidth() - getHitW() - limit) {
				move(speed, 0);
			}
			break;
		case SysKey.DOWN:
			if (getY() < getScreenHeight() - getHitH() - limit) {
				move(0, speed);
			}
			break;
		case SysKey.UP:
			if (getY() > limit) {
				move(0, -speed);
			}
			break;
		case SysKey.ENTER:
			if (this.lastMp > 0) {
				if (!shoting) {
					if (heroShot == null) {
						onShot();
					} else {
						addClass(heroShot, getX(), getY() - 16, 0);
					}
					this.shoting = true;
				}
				++this.shotOB;
				if (this.shotOB % shotOBLimit == 0) {
					--this.lastMp;
				}
			}
			break;
		}
		if (SysKey.getKeyCode() == SysKey.ENTER) {
			this.shoting = false;
		}
	}

	public abstract void onShot();

	public abstract void onDamaged();

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getMaxMp() {
		return maxMp;
	}

	public void setMaxMp(int maxMp) {
		this.maxMp = maxMp;
	}

	public void setHP(int hp) {
		this.maxHp = hp;
		this.lastHp = hp;
	}

	public void setMP(int mp) {
		this.maxMp = mp;
		this.lastMp = mp;
	}

	public String getHeroShot() {
		return heroShot;
	}

	public void setHeroShot(String heroShot) {
		this.heroShot = heroShot;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getDamagedEffect() {
		return damagedEffect;
	}

	public void setDamagedEffect(String damagedEffect) {
		this.damagedEffect = damagedEffect;
	}

}
