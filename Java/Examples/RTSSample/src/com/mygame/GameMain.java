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
package com.mygame;

import loon.LKey;
import loon.LTouch;
import loon.Touch;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.core.timer.GameTime;

//主运行类
public class GameMain extends DrawableScreen {

	public static ScoreData ScoreData = new ScoreData();

	private EntityManager manager;

	public static Settings Settings = new Settings();

	@Override
	public void draw(SpriteBatch batch) {

	}

	@Override
	public void loadContent() {
		Touch.startTouchCollection();

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
	public void pressed(LTouch e) {

	}

	@Override
	public void released(LTouch e) {

	}

	@Override
	public void move(LTouch e) {

	}

	@Override
	public void drag(LTouch e) {

	}

	@Override
	public void pressed(LKey e) {

	}

	@Override
	public void released(LKey e) {

	}

	@Override
	public void update(GameTime gameTime) {

	}

}
