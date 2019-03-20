/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.component.skin;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;

public class ControlSkin {

	private LTexture controlBaseTexture;

	private LTexture controlDotTexture;
	
	public static ControlSkin def(){
		return new ControlSkin();
	}

	public ControlSkin() {
		this(LTextures.loadTexture(LSystem.FRAMEWORK_IMG_NAME
				+ "control_base.png"), LTextures
				.loadTexture(LSystem.FRAMEWORK_IMG_NAME + "control_dot.png"));
	}

	public ControlSkin(LTexture basetex, LTexture dottex) {
		this.controlBaseTexture = basetex;
		this.controlDotTexture = dottex;
	}

	public LTexture getControlBaseTexture() {
		return controlBaseTexture;
	}

	public void setControlBaseTexture(LTexture controlBaseTexture) {
		this.controlBaseTexture = controlBaseTexture;
	}

	public LTexture getControlDotTexture() {
		return controlDotTexture;
	}

	public void setControlDotTexture(LTexture controlDotTexture) {
		this.controlDotTexture = controlDotTexture;
	}

}
