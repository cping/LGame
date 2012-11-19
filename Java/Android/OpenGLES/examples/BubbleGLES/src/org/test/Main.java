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
package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.input.LTransition;
import loon.core.timer.GameTime;

public class Main extends DrawableScreen {

	@Override
	public void draw(SpriteBatch batch) {

	}

	@Override
	public void loadContent() {

		ScreenData.SetScreenResolution(480, 800);
		BubbleDataManager.Initialize();
		MenuEntry.setBasicTexture(LTextures
				.loadTexture("assets/MenuEntries.png"));
		addDrawable(new PublisherScreen(new ProducerScreen(
				new MenuBackgroundScreen(), new MainMenuScreen())), 0);
	}

	@Override
	public void unloadContent() {
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
	public void drag(LTouch e) {
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

	@Override
	public void update(GameTime gameTime) {
		// TODO Auto-generated method stub

	}
	
	public LTransition onTransition(){
		return LTransition.newEmpty();
	}

}
