package org.loon.framework.javase.game.core.graphics.component.awt;

import java.awt.Frame;
import java.awt.Window;

import org.loon.framework.javase.game.GameModel;


/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class AWTScene extends Frame implements IScene {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AWTScene(String title) {
		super(title);
	}

	public Window getWindow() {
		return (Window) this;
	}

	public int getCodeType() {
		return GameModel.Awt;
	}

}
