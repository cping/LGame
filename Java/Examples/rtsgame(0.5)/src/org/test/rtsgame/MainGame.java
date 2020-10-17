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
package org.test.rtsgame;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.SysTouch;
import loon.utils.timer.GameTime;

//主运行类
public class MainGame extends DrawableScreen {

	public static ScoreData ScoreData = new ScoreData();

	private EntityManager manager;

	public static Settings Settings = new Settings();

	@Override
	public void draw(SpriteBatch batch) {

	}

	@Override
	public void loadContent() {
		SysTouch.startTouchCollection();

		Settings.Load();
		ScoreData.Load();

		this.manager = new EntityManager(this);
		Components().add(manager);

		LoadingEntity.Load(this.manager, true, new GameEntity[] {
				new BackgroundEntity(), new MainMenuEntity() });
	}

	@Override
	public void unloadContent() {

	}

	@Override
	public void pressed(GameTouch e) {

	}

	@Override
	public void released(GameTouch e) {

	}

	@Override
	public void move(GameTouch e) {

	}

	@Override
	public void drag(GameTouch e) {

	}

	@Override
	public void pressed(GameKey e) {

	}

	@Override
	public void released(GameKey e) {

	}

	@Override
	public void update(GameTime gameTime) {

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}