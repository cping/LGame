package org.test;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.StringUtils;

public class CStatistics {
	private int failedEnemys;
	private Vector2f failedEnemysPos;
	private Vector2f failedEnemysPos2;
	private String filename;
	private double gameTime;
	private Vector2f gameTimePos;
	private Vector2f gameTimePos2;
	private int highScore;
	private Vector2f highScorePos;
	private Vector2f highScorePos2;
	private int killedEnemys;
	private Vector2f killedEnemysPos;
	private Vector2f killedEnemysPos2;
	private int levelLost;
	private Vector2f levelLostPos;
	private Vector2f levelLostPos2;
	private int levelWin;
	private Vector2f levelWinPos;
	private Vector2f levelWinPos2;
	private MainGame mainGame;
	private int spentMoney;
	private Vector2f spentMoneyPos;
	private Vector2f spentMoneyPos2;
	private Vector2f StatisticsPos;
	private int wavesCompleted;
	private Vector2f wavesCompletedPos;
	private Vector2f wavesCompletedPos2;

	public CStatistics(String filename, MainGame mGame) {
		this.filename = filename;
		this.mainGame = mGame;
		this.failedEnemysPos = new Vector2f(100f * this.mainGame.scalePos.y,
				50f * this.mainGame.scalePos.y);
		this.killedEnemysPos = new Vector2f(100f * this.mainGame.scalePos.y,
				100f * this.mainGame.scalePos.y);
		this.wavesCompletedPos = new Vector2f(100f * this.mainGame.scalePos.y,
				150f * this.mainGame.scalePos.y);
		this.gameTimePos = new Vector2f(100f * this.mainGame.scalePos.y,
				200f * this.mainGame.scalePos.y);
		this.levelLostPos = new Vector2f(100f * this.mainGame.scalePos.y,
				250f * this.mainGame.scalePos.y);
		this.levelWinPos = new Vector2f(100f * this.mainGame.scalePos.y,
				300f * this.mainGame.scalePos.y);
		this.spentMoneyPos = new Vector2f(100f * this.mainGame.scalePos.y,
				350f * this.mainGame.scalePos.y);
		this.highScorePos = new Vector2f(100f * this.mainGame.scalePos.y,
				400f * this.mainGame.scalePos.y);
		this.failedEnemysPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				50f * this.mainGame.scalePos.y);
		this.killedEnemysPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				100f * this.mainGame.scalePos.y);
		this.wavesCompletedPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				150f * this.mainGame.scalePos.y);
		this.gameTimePos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				200f * this.mainGame.scalePos.y);
		this.levelLostPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				250f * this.mainGame.scalePos.y);
		this.levelWinPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				300f * this.mainGame.scalePos.y);
		this.spentMoneyPos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				350f * this.mainGame.scalePos.y);
		this.highScorePos2 = new Vector2f(330f * this.mainGame.scalePos.y,
				400f * this.mainGame.scalePos.y);
		this.StatisticsPos = new Vector2f(496f * this.mainGame.scalePos.y,
				40f * this.mainGame.scalePos.y);
	}

	public final void addFailedEnemys() {
		this.failedEnemys++;
	}

	public final void addgameTime(float time) {
		this.gameTime += time;
	}

	public final void addKilledEnemys() {
		this.killedEnemys++;
	}

	public final void addLevelLosed() {
		this.levelLost++;
	}

	public final void addLevelWin() {
		this.levelWin++;
	}

	public final void addSpentMoney(int money) {
		this.spentMoney += (int) money;
	}

	public final void addWavesCompleted() {
		this.wavesCompleted++;
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneColor) {
		batch.draw(this.mainGame.standardBackTexture,
				this.mainGame.fullScreenRect, defaultSceneColor);
		batch.draw(this.mainGame.buttonStatistics, this.StatisticsPos,
				defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, "Missed Enemies:",
				this.failedEnemysPos, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont,
				(new Integer(this.failedEnemys)).toString(),
				this.failedEnemysPos2, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, "Killed Enemies:",
				this.killedEnemysPos, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont,
				(new Integer(this.killedEnemys)).toString(),
				this.killedEnemysPos2, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, "Waves Completed:",
				this.wavesCompletedPos, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, (new Integer(
				this.wavesCompleted)).toString(), this.wavesCompletedPos2,
				defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, "Played Time:",
				this.gameTimePos, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, this.getLevelTimeString(),
				this.gameTimePos2, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, "Level Lost:",
				this.levelLostPos, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont,
				(new Integer(this.levelLost)).toString(), this.levelLostPos2,
				defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, "Level Win:",
				this.levelWinPos, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont,
				(new Integer(this.levelWin)).toString(), this.levelWinPos2,
				defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, "Spent Money:",
				this.spentMoneyPos, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont,
				(new Integer(this.spentMoney)).toString(), this.spentMoneyPos2,
				defaultSceneColor);
		batch.drawString(this.mainGame.smalFont, "Highscore:",
				this.highScorePos, defaultSceneColor);
		batch.drawString(this.mainGame.smalFont,
				(new Integer(this.highScore)).toString(), this.highScorePos2,
				defaultSceneColor);
	}

	public final String getLevelTimeString() {
		int gameTime = (int) this.gameTime;
		int num2 = gameTime / 0x15180;
		gameTime -= num2 * 0x15180;
		int num3 = gameTime / 0xe10;
		gameTime -= num3 * 0xe10;
		int num4 = gameTime / 60;
		gameTime -= num4 * 60;
		return StringUtils.concat(new Integer(num2), " days ", (new Integer(
				num3)).toString(), " h ", (new Integer(num4)), " min ",
				gameTime, " sec ");
	}

	public final void loadStatistics() {

	}

	public final void reset() {
		this.wavesCompleted = 0;
		this.failedEnemys = 0;
		this.killedEnemys = 0;
		this.gameTime = 0.0;
		this.levelLost = 0;
		this.levelWin = 0;
		this.spentMoney = 0;
	}

	public final void saveStatistics() {

	}

	public final void update(float time) {
		if (this.mainGame.isPressedBack()) {
			this.mainGame.switchGameMode(MainGame.EGMODE.GMODE_MENU);
		}
		if (!this.mainGame.currentToucheState.AnyTouch()
				&& this.mainGame.previouseToucheState.AnyTouch()) {
			this.mainGame.switchGameMode(MainGame.EGMODE.GMODE_MENU);
		}
		this.highScore = 0;
		for (int i = 0; i < this.mainGame.levels; i++) {
			this.highScore += this.mainGame.level[i].highscore;
		}
	}

	public String getFilename() {
		return filename;
	}

}