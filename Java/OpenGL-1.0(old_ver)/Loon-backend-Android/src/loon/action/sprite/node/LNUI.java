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
package loon.action.sprite.node;

import loon.core.geom.Vector2f;

public class LNUI extends LNNode {
	
	public float _bold;

	protected int _touchID;

	public LNUI() {
		super();
		this.init();
	}

	private void init() {
		this._touchID = -1;
		this._enabled = true;
		this._bold = 0f;
		super.setNodeSize(0, 0);
	}

	public boolean isInside(Vector2f point) {
		float[] pos = super.convertToWorldPos();
		return (((point.x >= (pos[0] - this._bold)) && (point.x < ((pos[0] + super
				.getWidth()) + this._bold))) && ((point.y >= (pos[1] - this._bold)) && (point.y < ((pos[1] + super
				.getHeight()) + this._bold))));
	}

	public void touchesCancel() {
		this._touchID = -1;
	}

	public void setBold(float b) {
		this._bold = b;
	}

	public float getBold() {
		return this._bold;
	}
}
