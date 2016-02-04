package org.test.rtsgame;

import loon.LSystem;
import loon.action.sprite.painting.DrawableEvent;
import loon.geom.Vector2f;

//主菜单用类
public class MainMenuEntity extends MenuEntity
{

	private void ContGameMenuEntrySelected()
	{
		LoadingEntity.Load(super.getScreenManager(), false, new GameEntity[] {new GameplayEntity(), new PauseMenuEntity()});
	}

	@Override
	public void LoadContent()
	{
		super.titleTexture = super.getScreenManager().getGameContent().mainMenu;
		MenuEntry item = new MenuEntry(this, "Continue", new Vector2f(306f, 192f));
		MenuEntry entry2 = new MenuEntry(this, "New Game", new Vector2f(282f, 234f));
		MenuEntry entry3 = new MenuEntry(this, "Options", new Vector2f(312f, 276f));
		MenuEntry entry4 = new MenuEntry(this, "Exit", new Vector2f(342f, 318f));

		item.Selected = new DrawableEvent() {
			
			@Override
			public void invoke() {
		
					ContGameMenuEntrySelected();
			
			}
		};

		entry2.Selected =new DrawableEvent() {
			
			@Override
			public void invoke() {
			
				PlayGameMenuEntrySelected();
				
			}
		};

		entry3.Selected =new DrawableEvent() {
			
			@Override
			public void invoke() {
				
				OptionsMenuEntrySelected();
				
			}
		};

		entry4.Selected =new DrawableEvent() {
			
			@Override
			public void invoke() {
				QuitGameMenuEntrySelected();
				
			}
		};
		if (MainGame.ScoreData.Level != 0)
		{
			super.getMenuEntries().add(item);
		}
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(entry3);
		super.getMenuEntries().add(entry4);
	}

	@Override
	protected void OnCancel()
	{
		LSystem.exit();
	}

	private void OptionsMenuEntrySelected()
	{
		super.getScreenManager().AddScreen(new OptionsMenuEntity());
	}

	private void PlayGameMenuEntrySelected()
	{
		MainGame.ScoreData.Level = 0;
		MainGame.ScoreData.Score = 0;
		LoadingEntity.Load(super.getScreenManager(), false,  new GameEntity[] {new GameplayEntity(), new PauseMenuEntity()});
	}

	private void QuitGameMenuEntrySelected()
	{
		LSystem.exit();
	}
}