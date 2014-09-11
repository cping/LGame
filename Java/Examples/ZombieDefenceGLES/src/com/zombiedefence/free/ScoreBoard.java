package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;

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