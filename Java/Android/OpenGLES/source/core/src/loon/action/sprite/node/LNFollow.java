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

import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.utils.MathUtils;

public class LNFollow extends LNAction {
	
	LNFollow(){
		
	}

	protected LNNode _followedNode;

	protected boolean _boundarySet;

	protected boolean _boundaryFullyCovered;

	RectBox winRect;

	Vector2f halfScreenSize;

	Vector2f fullScreenSize;

	float leftBoundary;

	float rightBoundary;

	float topBoundary;

	float bottomBoundary;

	public void setBoundarySet(boolean flag) {
		_boundarySet = flag;
	}

	public boolean getBoundarySet() {
		return _boundarySet;
	}

	public static LNFollow Action(LNNode followedNode) {
		LNFollow follow = new LNFollow();
		follow._followedNode = followedNode;
		follow._boundarySet = false;
		follow._boundaryFullyCovered = false;

		follow.winRect = LSystem.screenRect;

		follow.fullScreenSize = new Vector2f(follow.winRect.width,
				follow.winRect.height);
		follow.halfScreenSize = Vector2f.mul(follow.fullScreenSize, 0.5f);
		return follow;
	}

	public static LNFollow Action(LNNode followedNode, RectBox rect) {
		LNFollow follow = new LNFollow();
		follow._followedNode = followedNode;
		follow._boundarySet = true;
		follow._boundaryFullyCovered = false;

		follow.winRect = LSystem.screenRect;
		follow.fullScreenSize = new Vector2f(follow.winRect.width,
				follow.winRect.height);
		follow.halfScreenSize = follow.fullScreenSize.mul(0.5f);

		follow.leftBoundary = -((rect.x + rect.width) - follow.fullScreenSize.x);
		follow.rightBoundary = -rect.x;
		follow.topBoundary = -rect.y;
		follow.bottomBoundary = -((rect.y + rect.height) - follow.fullScreenSize.y);

		if (follow.rightBoundary < follow.leftBoundary) {
			follow.rightBoundary = follow.leftBoundary = (follow.leftBoundary + follow.rightBoundary) / 2;
		}

		if (follow.topBoundary < follow.bottomBoundary) {
			follow.topBoundary = follow.bottomBoundary = (follow.topBoundary + follow.bottomBoundary) / 2;
		}
		if ((follow.topBoundary == follow.bottomBoundary)
				&& (follow.leftBoundary == follow.rightBoundary)) {
			follow._boundaryFullyCovered = true;
		}
		return follow;
	}

	@Override
	public void step(float dt) {
		if (_boundarySet) {
			if (_boundaryFullyCovered) {
				return;
			}
			Vector2f pos = halfScreenSize.sub(_followedNode.getPosition());
			super._target.setPosition(
					MathUtils.clamp(pos.x, leftBoundary, rightBoundary),
					MathUtils.clamp(pos.y, bottomBoundary, topBoundary));
		} else {
			super._target.setPosition(halfScreenSize.sub(_followedNode
					.getPosition()));
		}
	}

	@Override
	public void update(float time) {
		if (_followedNode._isClose) {
			super._isEnd = true;
		}
	}

	@Override
	public LNAction copy() {
		return Action(_followedNode, winRect);
	}

}
