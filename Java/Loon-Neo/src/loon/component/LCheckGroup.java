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

import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 生成一组CheckBox进行分组操作,一组中能被选中的只有一个
 */
public class LCheckGroup extends LComponent {

	private LCheckBox _selectedBtn;

	private final TArray<LCheckBox> _checks;

	private float _minX = -1, _minY = -1, _maxX = -1, _maxY = -1;

	public LCheckGroup() {
		super(0, 0, 1, 1);
		this.customRendering = false;
		this._checks = new TArray<LCheckBox>(10);
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		for (LCheckBox check : _checks) {
			check.createUI(g);
		}
	}

	@Override
	public void update(long elapsedTime) {
		for (LCheckBox check : _checks) {
			check.update(elapsedTime);
		}
	}

	public LCheckGroup add(LCheckBox check) {
		if (_minX == -1) {
			_minX = check.getX();
		}
		if (_minY == -1) {
			_minY = check.getY();
		}
		_minX = MathUtils.min(_minX, check.getX());
		_minY = MathUtils.min(_minY, check.getY());
		_maxX += MathUtils.max(_maxY, check.getWidth());
		_maxY += MathUtils.max(_maxY, check.getHeight());
		setLocation(_minX, _minY);
		setSize(_maxX, _maxY);
		_checks.add(check);
		return this;
	}

	@Override
	public void setColor(LColor c) {
		super.setColor(c);
		for (LCheckBox check : _checks) {
			check.setColor(c);
		}
	}

	public TArray<LCheckBox> getCheckBoxs() {
		return _checks;
	}

	@Override
	protected void processTouchDragged() {
		super.processTouchDragged();
		for (LCheckBox check : _checks) {
			check.processTouchDragged();
		}
		super.processTouchDragged();
	}

	@Override
	protected void processTouchEntered() {
		super.processTouchEntered();
		for (LCheckBox check : _checks) {
			check.processTouchEntered();
		}
	}

	@Override
	protected void processTouchExited() {
		super.processTouchExited();
		for (LCheckBox check : _checks) {
			check.processTouchExited();
		}
	}

	@Override
	protected void processKeyPressed() {
		super.processKeyPressed();
		for (LCheckBox check : _checks) {
			check.processKeyPressed();
		}
	}

	@Override
	protected void processKeyReleased() {
		super.processKeyReleased();
		for (LCheckBox check : _checks) {
			check.processKeyReleased();
		}
	}

	@Override
	protected void processTouchClicked() {
		super.processTouchClicked();
		for (LCheckBox check : _checks) {
			check.processTouchClicked();
		}
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		for (LCheckBox check : _checks) {
			if (check.contains(getTouchX(), getTouchY())) {
				check.processTouchPressed();
				_selectedBtn = check;
			}
		}
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (_selectedBtn != null) {
			if (_selectedBtn.contains(getTouchX(), getTouchY())) {
				_selectedBtn.processTouchReleased();
			}
			for (LCheckBox check : _checks) {
				if (_selectedBtn != check) {
					check.setTicked(false);
				}
			}
		}
	}

	@Override
	public String getUIName() {
		return "CheckGroup";
	}

	@Override
	public void destory() {

	}

}
