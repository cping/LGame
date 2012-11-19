package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.graphics.LFont;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;

public class RankingScreenA extends MenuScreen {

	private int level;
	public String name = "Name";
	private boolean onlineFailure = false;
	public static java.util.ArrayList<String> rankingName = new java.util.ArrayList<String>();
	public static java.util.ArrayList<Integer> rankingScore = new java.util.ArrayList<Integer>();

	public RankingScreenA(LFont font, int level) {
		this.level = level;
	}

	public final void ChangeNameSelected() {

	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		this.DrawListBackground(gameTime, batch);
		if (!((rankingName.size() != 0) || this.onlineFailure)) {
			
		}
	}

	public void DrawListBackground(GameTime gameTime, SpriteBatch batch) {
	}

	protected final void GetAllList(boolean me) {
		rankingName.clear();
		rankingScore.clear();
		
	}

	protected final void GetLevelList(int level, boolean me) {
		rankingName.clear();
		rankingScore.clear();
		
	}

	public final void GetMyAllListEntrySelected() {
		this.GetAllList(true);
	}

	public final void GetMyLevelListEntrySelected() {
		this.GetLevelList(this.level, true);
	}

	public final void GetProfil() {
		
	}

	public final void GetTopAllListEntrySelected() {
		this.GetAllList(false);
	}

	public final void GetTopLevelListEntrySelected() {
		this.GetLevelList(this.level, false);
	}

	@Override
	public void loadContent() {
		this.GetProfil();
	}


	@Override
	public void unloadContent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameTime elapsedTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressed(LTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void released(LTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(LTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressed(LKey e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void released(LKey e) {
		// TODO Auto-generated method stub
		
	}
}