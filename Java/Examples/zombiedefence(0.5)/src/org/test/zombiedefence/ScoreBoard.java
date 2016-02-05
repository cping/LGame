package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.Vector2f;

public class ScoreBoard extends DrawableObject {

	private int day;
	private LFont myFont;
	private int numHeadShot;
	private int numKill;

	public ScoreBoard(LTexture t2DScoreBoard, Vector2f position, LFont myFont,
			int numKill, int numHeadShot, float accuracy, int day) {
		super(t2DScoreBoard, position);
		this.numKill = numKill;
		this.numHeadShot = numHeadShot;
		this.myFont = myFont;
		this.day = day;
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		batch.drawString(this.myFont, (new Integer(this.day)).toString(), 265f,
				140f, LColor.white);
		batch.drawString(this.myFont, (new Integer(this.numKill)).toString(),
				430f, 260f, LColor.wheat);
		batch.drawString(this.myFont,
				(new Integer(this.numHeadShot)).toString(), 430f, 310f,
				LColor.wheat);
		batch.drawString(this.myFont, "" + (this.numKill * Help.scorePerKill),
				530f, 260f, LColor.wheat);
		batch.drawString(this.myFont, ""
				+ (this.numHeadShot * Help.scorePerHeadShot), 530f, 310f,
				LColor.wheat);
		batch.drawString(
				this.myFont,
				""
						+ ((this.numHeadShot * Help.scorePerHeadShot) + (this.numKill * Help.scorePerKill)),
				530f, 350f, LColor.wheat);
		batch.drawString(this.myFont, (new Integer(Help.money)).toString(),
				250f, 420f, LColor.white);
	}

	@Override
	public void Update() {
		super.Update();
	}
}