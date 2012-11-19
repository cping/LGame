package org.test;

import org.test.MenuEntry.SelectEvent;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class RankingScreen extends RankingScreenA
{
	private MenuEntry backMenuEntry;
	private MenuEntry myListMenuEntry;
	private MenuEntry nameChangeMenuEntry;
	private MenuEntry top15MenuEntry;

	public RankingScreen()
	{
		super(LFont.getFont(20), 0);
		super.GetAllList(true);
	}

	@Override
	public void draw(SpriteBatch batch,GameTime gameTime)
	{
		super.draw(batch,gameTime);
	}

	@Override
	public void loadContent()
	{
		super.loadContent();
		this.backMenuEntry = new MenuEntry(new RectBox(180, 0, 80, 80));
		this.backMenuEntry.setBasePosition(new Vector2f(60f, 650f));
		this.backMenuEntry.Selected =new SelectEvent() {
			
			@Override
			public void invoke(MenuEntry entry) {
				onCancel();
			}
		};
		this.backMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom| MenuEntryEffects.ComeFromBottom);
		super.getMenuEntries().add(this.backMenuEntry);
		this.nameChangeMenuEntry = new MenuEntry(new RectBox(0, 360, 0x14f, 90));
		this.nameChangeMenuEntry.setBasePosition(new Vector2f(312.5f, 650f));
		this.nameChangeMenuEntry.Selected =new SelectEvent() {
			
			@Override
			public void invoke(MenuEntry entry) {
				ChangeNameSelected();
				
			}
		};
		this.nameChangeMenuEntry.setEntryAnimation(MenuEntryEffects.GoToRight | MenuEntryEffects.ComeFromRight);
		super.getMenuEntries().add(this.nameChangeMenuEntry);
		this.top15MenuEntry = new MenuEntry(new RectBox(620, 0x166, 0x5b, 0x5b));
		this.top15MenuEntry.setBasePosition(new Vector2f(300f, 100f));

		this.top15MenuEntry.Selected =new SelectEvent() {
			
			@Override
			public void invoke(MenuEntry entry) {
				GetTopAllListEntrySelected();
				
			}
		};
		this.top15MenuEntry.setEntryAnimation(MenuEntryEffects.GoToTop | MenuEntryEffects.ComeFromTop);
		super.getMenuEntries().add(this.top15MenuEntry);
		this.myListMenuEntry = new MenuEntry(new RectBox(0x2c7, 0x166, 0x5b, 0x5b));
		this.myListMenuEntry.setBasePosition(400f, 100f);
		this.myListMenuEntry.Selected = new SelectEvent() {
			
			@Override
			public void invoke(MenuEntry entry) {
				GetMyAllListEntrySelected();
				
			}
		};
		this.myListMenuEntry.setEntryAnimation(MenuEntryEffects.GoToTop | MenuEntryEffects.ComeFromTop);
		super.getMenuEntries().add(this.myListMenuEntry);
	}
}