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

public class ControlSkin extends SkinAbstract<ControlSkin> {

	public static ControlSkin def() {
		return new ControlSkin();
	}

	private LTexture controlBaseTexture;

	private LTexture controlDotTexture;

	public ControlSkin() {
		this(LSystem.loadTexture(LSystem.getSystemImagePath() + "control_base.png"),
				LSystem.loadTexture(LSystem.getSystemImagePath() + "control_dot.png"));
	}

	public ControlSkin(LTexture basetex, LTexture dottex) {
		super();
		this.controlBaseTexture = basetex;
		this.controlDotTexture = dottex;
	}

	public LTexture getControlBaseTexture() {
		return controlBaseTexture;
	}

	public ControlSkin setControlBaseTexture(LTexture controlBaseTexture) {
		this.controlBaseTexture = controlBaseTexture;
		return this;
	}

	public LTexture getControlDotTexture() {
		return controlDotTexture;
	}

	public ControlSkin setControlDotTexture(LTexture controlDotTexture) {
		this.controlDotTexture = controlDotTexture;
		return this;
	}

	@Override
	public String getSkinName() {
		return "control";
	}

}
