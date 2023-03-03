/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package application;

import loon.LTexture;
import loon.LTransition;
import loon.Screen;
import loon.canvas.LColor;
import loon.events.GameTouch;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;
import loon.utils.MathUtils;
import loon.utils.timer.LTimerContext;

public class Test extends Screen{
	private LTexture[] unitImages = TextureUtils
			.getSplitTextures(TextureUtils.filterColor("assets/slg/unit.png", new LColor(255, 0, 255),0,0.15f,true), 32, 32);
	@Override
	public void draw(GLEx g) {
	g.draw(unitImages[0], 120, 120);
		
	}
	
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void onLoad() {
		System.out.println(MathUtils.max(1f, 0.85f));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alter(LTimerContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touchDown(GameTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touchUp(GameTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touchMove(GameTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touchDrag(GameTouch e) {
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

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
