/**
 * Copyright 2008 - 2011
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
package loon.srpg.view;

import loon.LSystem;
import loon.canvas.LColor;
import loon.canvas.LGradation;
import loon.font.IFont;
import loon.font.LFont;
import loon.font.Font.Style;
import loon.opengl.GLEx;
import loon.srpg.ability.SRPGAbilityFactory;
import loon.srpg.actor.SRPGStatus;


public class SRPGAbilityNameView extends SRPGDrawView {

	private final static LFont defFont = LFont.getFont("Dialog", Style.PLAIN, 12);

	private SRPGAbilityFactory ab;

	private SRPGStatus status;

	private int namelen;

	public SRPGAbilityNameView(SRPGAbilityFactory ability, SRPGStatus status) {
		this.setExist(true);
		this.setLock(false);
		super.width = LSystem.viewSize.getWidth();
		super.height = defFont.getHeight();
		this.ab = ability;
		this.status = status;
		this.namelen = defFont.stringWidth(status.name);
	}

	@Override
	public void draw(GLEx g) {
		IFont old = g.getFont();
		g.setColor(LColor.white);
		g.setFont(defFont);
		LColor color = LColor.blue;
		if (status.team != 0) {
			color = LColor.red;
		}
		LColor color1 = LColor.black;
		LGradation.create(color, color1, super.width, super.height)
				.drawWidth(g, super.left, super.top);
		g.setColor(LColor.white);
		g.drawString(ab.getAbilityName(), 5 + super.left, 12 + super.top);
		g.drawString(status.name, (super.width - namelen - 5) + super.left,
				12 + super.top);
		g.setFont(old);
		g.resetColor();
	}

}
