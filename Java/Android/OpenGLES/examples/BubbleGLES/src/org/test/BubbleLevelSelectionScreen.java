package org.test;

import java.util.ArrayList;

import org.test.MenuEntry.SelectEvent;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class BubbleLevelSelectionScreen extends LevelSelectionScreen {
	private MenuEntry backMenuEntry;
	private LFont menuFont;
	private LTexture menuTexture;
	private static ArrayList<Vector2f> list;
	static {
		list = new ArrayList<Vector2f>();
		list.add(new Vector2f(160f, 170f));
		list.add(new Vector2f(320f, 170f));
		list.add(new Vector2f(100f, 300f));
		list.add(new Vector2f(240f, 300f));
		list.add(new Vector2f(380f, 300f));
		list.add(new Vector2f(100f, 430f));
		list.add(new Vector2f(240f, 430f));
		list.add(new Vector2f(380f, 430f));
		list.add(new Vector2f(160f, 560f));
		list.add(new Vector2f(320f, 560f));
	}

	public BubbleLevelSelectionScreen() {
		super(BubbleDataManager.levels, list);

	}

	@Override
	public void DrawFirstPage(GameTime gameTime, Vector2f pageCenter,
			SpriteBatch batch) {
		super.DrawFirstPage(gameTime, pageCenter, batch);
		batch.draw(this.menuTexture, new Vector2f(pageCenter.x,
				55f + pageCenter.y), new RectBox(620, 0, 300, 0x166),
				LColor.white, 0f, new Vector2f(150f, 254f), (float) 1f,
				SpriteEffects.None);
	}

	@Override
	public void DrawLastPage(GameTime gameTime, Vector2f pageCenter,
			SpriteBatch batch) {
		super.DrawLastPage(gameTime, pageCenter, batch);
		batch.draw(this.menuTexture, new Vector2f(pageCenter.x,
				pageCenter.y - 75f), new RectBox(0x150, 0x167, 0x11c, 0xc2),
				LColor.white, 0f, new Vector2f(142.5f, 70.5f), (float) 1f,
				SpriteEffects.None);
		batch.draw(this.menuTexture, new Vector2f(pageCenter.x,
				55f + pageCenter.y), new RectBox(620, 0xfe, 300, 0x68),
				LColor.white, 0f, new Vector2f(150f, 0f), (float) 1f,
				SpriteEffects.None);
		batch.draw(this.menuTexture, new Vector2f(pageCenter.x,
				165f + pageCenter.y), new RectBox(0, 450, 300, 0x68),
				LColor.white, 0f, new Vector2f(150f, 0f), (float) 1f,
				SpriteEffects.None);
	}

	@Override
	public void DrawLevelEntry(GameTime gameTime, int level,
			Vector2f entryCenter, SpriteBatch batch) {
		super.DrawLevelEntry(gameTime, level, entryCenter, batch);
		if (BubbleDataManager.LevelUnocked(level)) {
			batch.draw(this.menuTexture, entryCenter, new RectBox(470, 0xe1,
					120, 120), LColor.white, 0f, new Vector2f(50f), (float) 1f,
					SpriteEffects.None);
			int num = level + 1;
			batch.setFont(menuFont);
			batch.drawString("" + num, entryCenter.x, entryCenter.y, LColor.red);
			String lev = "Lv:" + BubbleDataManager.LevelScore(level);
			batch.drawString(lev, entryCenter.x
					- (menuFont.stringWidth(lev) / 2) + 10, entryCenter.y
					+ menuFont.getHeight() + 10, LColor.white);
			if (BubbleDataManager.LevelScore(level) > 0) {
				batch.draw(this.menuTexture,
						entryCenter.add(new Vector2f(37f)), new RectBox(530,
								0x69, 0x24, 0x24), LColor.white, 0f,
						new Vector2f(), (float) 1f, SpriteEffects.None);
			}
		} else {
			batch.draw(this.menuTexture, entryCenter, new RectBox(410, 0x69,
					120, 120), LColor.white, 0, new Vector2f(50f),
					new Vector2f(1f), SpriteEffects.None);
		}
	}

	public void OnCancel() {

	}

	@Override
	public void loadContent() {
		super.loadContent();

		this.backMenuEntry = new MenuEntry(new RectBox(180, 0, 80, 80));
		this.backMenuEntry.Selected = new SelectEvent() {

			public void invoke(MenuEntry entry) {
				OnCancel();
			}
		};
		this.backMenuEntry.setBasePosition(new Vector2f(60f, 650f));
		this.backMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom
				| MenuEntryEffects.ComeFromBottom);
		super.getMenuEntries().add(this.backMenuEntry);
		this.menuTexture = LTextures.loadTexture("assets/MenuEntries.png");
		this.menuFont = LFont.getFont(30);
	}

	@Override
	public void StartLevel(int level) {
		super.StartLevel(level);
		if (BubbleDataManager.LevelUnocked(level)) {
			drawableScreen.addDrawable(new GameplayScreen(level));
		}
	}
}