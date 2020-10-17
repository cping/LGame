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
package loon.component;

import loon.LTexture;
import loon.canvas.LColor;
import loon.events.SysTouch;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 生成一组CheckBox进行分组操作,一组中能被选中的只有一个
 */
public class LCheckGroup extends LComponent {

	private LCheckBox selectedBtn;

	private final TArray<LCheckBox> checks;

	private float minX = -1, minY = -1, maxX = -1, maxY = -1;

	public LCheckGroup() {
		super(0, 0, 1, 1);
		this.customRendering = false;
		this.checks = new TArray<LCheckBox>(10);
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		for (LCheckBox check : checks) {
			check.createUI(g);
		}
	}

	@Override
	public void update(long elapsedTime) {
		for (LCheckBox check : checks) {
			check.update(elapsedTime);
		}
	}

	public LCheckGroup add(LCheckBox check) {
		if (minX == -1) {
			minX = check.getX();
		}
		if (minY == -1) {
			minY = check.getY();
		}
		minX = MathUtils.min(minX, check.getX());
		minY = MathUtils.min(minY, check.getY());
		maxX += MathUtils.max(maxY, check.getWidth());
		maxY += MathUtils.max(maxY, check.getHeight());
		setLocation(minX, minY);
		setSize(maxX, maxY);
		checks.add(check);
		return this;
	}

	@Override
	public void setColor(LColor c) {
		super.setColor(c);
		for (LCheckBox check : checks) {
			check.setColor(c);
		}
	}

	public TArray<LCheckBox> getCheckBoxs() {
		return checks;
	}

	@Override
	protected void processTouchDragged() {
		super.processTouchDragged();
		for (LCheckBox check : checks) {
			check.processTouchDragged();
		}
		super.processTouchDragged();
	}

	@Override
	protected void processTouchEntered() {
		super.processTouchEntered();
		for (LCheckBox check : checks) {
			check.processTouchEntered();
		}
	}

	@Override
	protected void processTouchExited() {
		super.processTouchExited();
		for (LCheckBox check : checks) {
			check.processTouchExited();
		}
	}

	@Override
	protected void processKeyPressed() {
		super.processKeyPressed();
		for (LCheckBox check : checks) {
			check.processKeyPressed();
		}
	}

	@Override
	protected void processKeyReleased() {
		super.processKeyReleased();
		for (LCheckBox check : checks) {
			check.processKeyReleased();
		}
	}

	@Override
	protected void processTouchClicked() {
		super.processTouchClicked();
		for (LCheckBox check : checks) {
			check.processTouchClicked();
		}
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		for (LCheckBox check : checks) {
			if (check.contains(SysTouch.getX(), SysTouch.getY())) {
				check.processTouchPressed();
				selectedBtn = check;
			}
		}
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (selectedBtn != null) {
			if (selectedBtn.contains(SysTouch.getX(), SysTouch.getY())) {
				selectedBtn.processTouchReleased();
			}
			for (LCheckBox check : checks) {
				if (selectedBtn != check) {
					check.setTicked(false);
				}
			}
		}
	}

	@Override
	public String getUIName() {
		return "CheckGroup";
	}

}
