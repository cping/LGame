package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class CLevelChooserScreen implements CScreen {
	private boolean autodrag;
	private RectBox[] chooserFrameRects;
	private LTexture chooserFrameTexuture;
	private boolean firstRelease = true;
	private String highscore = " ";
	private Vector2f highscorePos;
	private Vector2f leftButtonPos;
	private String level = " ";
	private LTexture levelLockedTexture;
	private RectBox[] levelLockRects;
	private Vector2f levelPos;
	private Vector2f levelsMargin;
	private Vector2f levelsOffset;
	private RectBox[] levelsRects;
	private MainGame mainGame;
	private int maxXPos;
	private Vector2f middleButtonPos;
	private RectBox middlePoint;
	private CMenu resumeMenu;
	private boolean selectResume;
	private Vector2f ThumbSize;
	private Vector2f wavePos;
	private String waves = " ";
	private float xpos;
	private float xvelocity;

	public CLevelChooserScreen(MainGame mGame) {
		this.mainGame = mGame;
		this.levelsRects = new RectBox[this.mainGame.levels];
		this.chooserFrameRects = new RectBox[this.mainGame.levels];
		this.levelLockRects = new RectBox[this.mainGame.levels];
		this.levelsOffset = new Vector2f(249f * this.mainGame.scalePos.y,
				130f * this.mainGame.scalePos.y);
		this.levelsMargin = new Vector2f(30f * this.mainGame.scalePos.y, 0f);
		this.highscorePos = new Vector2f(300f * this.mainGame.scalePos.y,
				55f * this.mainGame.scalePos.y);
		this.wavePos = new Vector2f(300f * this.mainGame.scalePos.y,
				25f * this.mainGame.scalePos.y);
		this.levelPos = new Vector2f(300f * this.mainGame.scalePos.y,
				85f * this.mainGame.scalePos.y);
		int y = (int) this.levelsOffset.y;
		this.ThumbSize = new Vector2f(355f, 213f);
		if (this.mainGame.scalePos.y > 1.5) {
			this.ThumbSize.x = 800f;
			this.ThumbSize.y = 480f;
		} else if (this.mainGame.scalePos.y > 1f) {
			this.ThumbSize.x = 533f;
			this.ThumbSize.y = 320f;
		}
		for (int i = 0; i < this.mainGame.levels; i++) {
			int x = ((int) this.levelsOffset.x)
					+ ((((int) this.levelsMargin.x) + ((int) this.ThumbSize.x)) * i);
			this.levelsRects[i] = new RectBox(x, y, (int) this.ThumbSize.x,
					(int) this.ThumbSize.y);
			this.levelLockRects[i] = new RectBox(x, y, (int) this.ThumbSize.x,
					(int) this.ThumbSize.y);
			if (this.mainGame.scalePos.y > 1.5f) {
				this.levelLockRects[i].width = 0x400;
				this.levelLockRects[i].height = 0x200;
			} else if (this.mainGame.scalePos.y > 1f) {
				this.levelLockRects[i].width = 0x400;
				this.levelLockRects[i].height = 0x200;
			}
			if (this.mainGame.scalePos.y > 1.5f) {
				this.chooserFrameRects[i] = new RectBox(x
						- ((int) (16f * this.mainGame.scalePos.y)), y
						- ((int) (16f * this.mainGame.scalePos.y)), 0x400,
						0x400);
			} else if (this.mainGame.scalePos.y > 1f) {
				this.chooserFrameRects[i] = new RectBox(x
						- ((int) (16f * this.mainGame.scalePos.y)), y
						- ((int) (16f * this.mainGame.scalePos.y)), 0x400,
						0x200);
			} else {
				this.chooserFrameRects[i] = new RectBox(x
						- ((int) (16f * this.mainGame.scalePos.y)), y
						- ((int) (16f * this.mainGame.scalePos.y)), 0x200,
						0x100);
			}
		}
		this.xvelocity = 0f;
		this.autodrag = false;
		this.middlePoint = new RectBox((400f * this.mainGame.scalePos.y),
				(240f * this.mainGame.scalePos.y), 1, 1);
		if (this.mainGame.scalePos.y > 1.5) {
			this.middlePoint.x = 960;
			this.middlePoint.y = 540;
		} else if (this.mainGame.scalePos.y > 1f) {
			this.middlePoint.x = 0x200;
			this.middlePoint.y = 360;
		}
		this.resumeMenu = new CMenu(this.mainGame, 2);
		this.maxXPos = (this.levelsRects[0].width + ((int) this.levelsMargin.x))
				* (this.mainGame.levels - 1);
		this.maxXPos *= -1;
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneColor) {
		batch.draw(this.mainGame.standardBackTexture,
				this.mainGame.fullScreenRect, defaultSceneColor);
		batch.drawString(this.mainGame.standartFont, "Waves: " + this.waves,
				this.wavePos, defaultSceneColor);
		batch.drawString(this.mainGame.standartFont, "HighScore: "
				+ this.highscore, this.highscorePos, defaultSceneColor);
		batch.drawString(this.mainGame.standartFont, "Level: " + this.level,
				this.levelPos, defaultSceneColor);
		for (int i = 0; i < this.mainGame.levels; i++) {
			if (LSystem.screenRect.contains(this.levelsRects[i])) {
				batch.draw(LTextures
						.loadTexture(this.mainGame.level[i].backGround),
						this.levelsRects[i], defaultSceneColor);
				if (this.mainGame.level[i].locked) {
					batch.draw(this.levelLockedTexture, this.levelLockRects[i],
							defaultSceneColor);
				}
				batch.draw(this.chooserFrameTexuture,
						this.chooserFrameRects[i], defaultSceneColor);
			}
		}
		if (this.selectResume) {
			this.resumeMenu.draw(batch, defaultSceneColor);
		}
	}

	public final float getNearestLevelChooserVelocity() {
		int index = -1;
		for (int i = 0; i < this.mainGame.levels; i++) {
			if (this.levelsRects[i].intersects(this.middlePoint)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			float num3 = ((int) this.levelsOffset.x)
					- this.levelsRects[index].x;
			this.updateLevelStats(index);
			this.mainGame.currentLevel = index;
			if (!this.mainGame.level[index].locked) {
				this.selectResume = true;
			}
			return num3;
		}
		this.selectResume = false;
		return -99f;
	}

	public final void init() {
		this.xpos = -((this.levelsRects[0].width + ((int) this.levelsMargin.x)) * this.mainGame.currentLevel);
		this.resumeMenu.reset();
		this.updateLevelStats(this.mainGame.currentLevel);
		if (!this.mainGame.level[this.mainGame.currentLevel].locked) {
			this.selectResume = true;
		}
	}

	public final void LoadContent() {
		this.chooserFrameTexuture = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "/chooserFrame.png");
		this.levelLockedTexture = LTextures.loadTexture(this.mainGame.gfxRoot
				+ "/levelLock.png");
		this.leftButtonPos = new Vector2f(200f, 350f);
		this.middleButtonPos = new Vector2f(300f, 350f);
		this.resumeMenu.setMenuItem(0, this.leftButtonPos,
				LTextures.loadTexture("assets/menu/buttons/buttonNewGame.png"),
				1.7f);
		this.resumeMenu.setMenuItem(1, new Vector2f(400f, 350f),
				LTextures.loadTexture("assets/menu/buttons/buttonResume.png"),
				1.7f);
	}

	public final void update(float time) {
		if (this.selectResume) {
			this.resumeMenu.update(time);
			if (this.resumeMenu.selectedItem != -1) {
				if (this.resumeMenu.ready) {
					switch (this.resumeMenu.selectedItem) {
					case 0:
						this.mainGame.gameLoopScreen.reset();
						this.mainGame
								.switchGameMode(MainGame.EGMODE.GMODE_GAME);
						this.resumeMenu.reset();
						this.selectResume = false;
						return;

					case 1:
						this.mainGame.gameLoopScreen.reset();
						this.mainGame.gameLoopScreen
								.loadGameWave(this.mainGame.level[this.mainGame.currentLevel].filename);
						this.mainGame
								.switchGameMode(MainGame.EGMODE.GMODE_GAME);
						this.resumeMenu.reset();
						this.selectResume = false;
						return;

					default:
						return;
					}
				}
				return;
			}
		}
		if (this.mainGame.isPressedBack()) {
			this.mainGame.switchGameMode(MainGame.EGMODE.GMODE_MENU);
		}
		if (this.mainGame.currentToucheState.AnyTouch()
				&& this.mainGame.previouseToucheState.AnyTouch()) {
			Vector2f vector = this.mainGame.getDragDelta();
			if (vector.x != 0f) {
				this.firstRelease = true;
				this.selectResume = false;
			}
			if ((vector.x == 0f) && this.firstRelease) {
				this.firstRelease = false;
			} else {
				this.xvelocity = vector.x * 3f;
			}
			this.autodrag = false;
		} else if (this.xvelocity < 0f) {
			if (this.xvelocity > -20f) {
				this.autodrag = true;
			}
			if (!this.autodrag) {
				this.xvelocity += time * 100f;
				if (this.xvelocity > 0f) {
					this.xvelocity = 0f;
				}
			} else {
				float num = this.getNearestLevelChooserVelocity();
				if (num != -99f) {
					this.xvelocity = num * 0.2f;
				} else {
					this.xvelocity = -15f;
				}
			}
		} else if (this.xvelocity > 0f) {
			if (this.xvelocity < 20f) {
				this.autodrag = true;
			}
			if (!this.autodrag) {
				this.xvelocity -= time * 100f;
				if (this.xvelocity < 0f) {
					this.xvelocity = 0f;
				}
			} else {
				float num2 = this.getNearestLevelChooserVelocity();
				if (num2 != -99f) {
					this.xvelocity = num2 * 0.2f;
				} else {
					this.xvelocity = 15f;
				}
			}
		} else if (!this.autodrag) {
			this.xvelocity = -15f;
		}
		this.xpos += this.xvelocity;
		if (this.xpos > 0f) {
			this.xpos = 0f;
			this.updateLevelStats(0);
			if (!this.mainGame.level[0].locked) {
				this.selectResume = true;
			}
			this.mainGame.currentLevel = 0;
		} else if (this.xpos < this.maxXPos) {
			this.xpos = this.maxXPos;
			this.updateLevelStats(this.mainGame.levels - 1);
			if (!this.mainGame.level[this.mainGame.levels - 1].locked) {
				this.selectResume = true;
			}
			this.mainGame.currentLevel = this.mainGame.levels - 1;
		}
		for (int i = 0; i < this.mainGame.levels; i++) {
			this.levelsRects[i].x = ((int) this.levelsOffset.x)
					+ ((((int) this.levelsMargin.x) + ((int) this.ThumbSize.x)) * i);
			this.levelsRects[i].x += (int) this.xpos;
			this.levelLockRects[i].x = this.levelsRects[i].x;
			this.chooserFrameRects[i].x = (((int) this.levelsOffset.x) + ((((int) this.levelsMargin.x) + ((int) this.ThumbSize.x)) * i))
					- ((int) (16f * this.mainGame.scalePos.y));
			this.chooserFrameRects[i].x += (int) this.xpos;
		}
	}

	public final void updateLevelStats(int index) {
		this.highscore = "" + this.mainGame.level[index].highscore;
		if (this.mainGame.level[index].maxWaves >= this.mainGame.level[index].maxWave) {
			this.waves = this.mainGame.level[index].maxWave + " / "
					+ this.mainGame.level[index].maxWaves;
		} else {
			this.waves = "" + this.mainGame.level[index].maxWave;
		}
		this.level = (new Integer(index + 1)).toString();
		if (!this.selectResume) {
			this.resumeMenu.reset();
		}
		if (!this.mainGame.level[index].saved) {
			this.resumeMenu.menuItem[0].pos.x = (int) this.middleButtonPos.x;
			this.resumeMenu.menuItem[1].skipItem = true;
		} else {
			this.resumeMenu.menuItem[0].pos.x = (int) this.leftButtonPos.x;
			this.resumeMenu.menuItem[1].skipItem = false;
		}
	}
}