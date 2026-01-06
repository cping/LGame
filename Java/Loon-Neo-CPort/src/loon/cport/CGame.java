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
package loon.cport;

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
import loon.Support;
import loon.cport.bridge.SDLCall;
import loon.events.InputMake;

public class CGame extends LGame {

	public static enum GameSysPlatform {
		NONE, WIN, MAC, LINUX, XBOX, SWITCH, STREAM, PS
	}

	public static class CSetting extends LSetting {

		public boolean resizable = false;

		public boolean maximized = false;

		public boolean minimized = false;

		public boolean fullscreen = false;

		public boolean autoIconify = true;

		public String iconPath = null;

		public GameSysPlatform gamePlatform = GameSysPlatform.NONE;

	}

	private int _startTime;

	private CSetting _csetting;

	public CGame(LSetting config, Platform plat) {
		super(config, plat);
		_startTime = SDLCall.getTicks();
	}

	@Override
	public boolean isCPort() {
		return true;
	}

	@Override
	public Type type() {
		if (_csetting != null) {
			switch (_csetting.gamePlatform) {
			case NONE:
			case WIN:
			case MAC:
			case LINUX:
			default:
				return Type.NativePC;
			case XBOX:
				return Type.NativeXBOX;
			case SWITCH:
				return Type.NativeSWITCH;
			case STREAM:
				return Type.NativeSTREAM;
			case PS:
				return Type.NativePS;
			}
		}
		return Type.STUB;
	}

	@Override
	public double time() {
		return SDLCall.getTicks();
	}

	@Override
	public int tick() {
		return (SDLCall.getTicks() - _startTime);
	}

	@Override
	public void openURL(String url) {

	}

	@Override
	public Assets assets() {
		return null;
	}

	@Override
	public Asyn asyn() {
		return null;
	}

	@Override
	public Graphics graphics() {
		return null;
	}

	@Override
	public InputMake input() {
		return null;
	}

	@Override
	public Clipboard clipboard() {
		return null;
	}

	@Override
	public Log log() {
		return null;
	}

	@Override
	public Save save() {
		return null;
	}

	@Override
	public Accelerometer accel() {
		return null;
	}

	@Override
	public Support support() {
		return null;
	}

}
