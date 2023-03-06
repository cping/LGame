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
package loon.se;

import loon.Accelerometer;
import loon.Assets;
import loon.Asyn;
import loon.Clipboard;
import loon.Graphics;
import loon.LGame;
import loon.LSetting;
import loon.Log;
import loon.Platform;
import loon.Save;
import loon.canvas.Canvas;
import loon.events.InputMake;
import loon.opengl.Mesh;

public class JavaSEGame extends LGame{

	public JavaSEGame(LSetting config, Platform plat) {
		super(config, plat);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Mesh makeMesh(Canvas canvas) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Environment env() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double time() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int tick() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JavaSEAssets assets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Asyn asyn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graphics graphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputMake input() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clipboard clipboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Log log() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Save save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Accelerometer accel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sys getPlatform() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMobile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDesktop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBrowser() {
		// TODO Auto-generated method stub
		return false;
	}

}
