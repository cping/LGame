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
package loon.component;

import loon.LSystem;
import loon.canvas.LColor;
import loon.component.layout.HorizontalAlign;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 工具用组件类,用于把一组注入此类的组件按照卡片方式展示并进行集合操作
 */
public class LCardGroup extends LContainer {

	private LComponent _lastClickedCard;
	private HorizontalAlign _alignment;

	private int _defaultSortOrder;

	private float _cardRotation;
	private float _heightOffset;
	private float _verticalOffsetY;

	private boolean _clickCardToMoveUp;
	private boolean _forceFitContainer;
	private boolean _updateCards;
	private boolean _middleProtrusion;

	private LColor _selectedColor;

	public LCardGroup() {
		this(-25f);
	}

	public LCardGroup(float rotation) {
		this(rotation, 25f);
	}

	public LCardGroup(float rotation, float heightOffset) {
		this(rotation, heightOffset, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight() / 2);
	}

	public LCardGroup(float rotation, float heightOffset, int x, int y, int w, int h) {
		super(x, y, w, h);
		setCardRotation(rotation);
		setHeightOffset(heightOffset);
		setSelectedColor(LColor.red);
		setAlignment(HorizontalAlign.CENTER);
		setClickCardToMoveUp(true);
		setForceFitContainer(true);
		setMiddleProtrusionCard(true);
		setElastic(false);
		setLocked(false);
	}

	public boolean isMiddleProtrusionCard() {
		return this._middleProtrusion;
	}

	public LCardGroup setMiddleProtrusionCard(boolean m) {
		this._middleProtrusion = m;
		return this;
	}

	public LCardGroup addCard(LComponent... cs) {
		add(cs);
		setCardUpdate(false);
		return this;
	}

	public int removeCurrentCard() {
		return removeCurrentClickedChild();
	}

	public LCardGroup removeCard(LComponent c) {
		remove(c);
		setCardUpdate(false);
		return this;
	}

	public LCardGroup removeCardName(String name) {
		removeAllName(name);
		setCardUpdate(false);
		return this;
	}

	public LCardGroup removeCardFlag(int flag) {
		removeAllFlag(flag);
		setCardUpdate(false);
		return this;
	}

	public LCardGroup removeCardTag(Object o) {
		removeAllTag(o);
		setCardUpdate(false);
		return this;
	}

	public LCardGroup removeCardFlagAndTag(int flag, Object o) {
		removeAllFlagAndTag(flag, o);
		setCardUpdate(false);
		return this;
	}

	public boolean isClickCardToMoveUp() {
		return this._clickCardToMoveUp;
	}

	public LCardGroup setClickCardToMoveUp(boolean b) {
		this._clickCardToMoveUp = b;
		return this;
	}

	public LColor getSelectedColor() {
		return this._selectedColor;
	}

	public LCardGroup setSelectedColor(LColor c) {
		this._selectedColor = c;
		return this;
	}

	public LCardGroup setHeightOffset(float o) {
		this._heightOffset = o;
		return this;
	}

	public float getHeightOffset() {
		return this._heightOffset;
	}

	public LCardGroup setCardRotation(float r) {
		this._cardRotation = r;
		return this;
	}

	public float getCardRotation() {
		return this._cardRotation;
	}

	@Override
	protected LContainer validateResize() {
		super.validateResize();
		this._updateCards = false;
		return this;
	}

	public void endClickedCard() {
		if (_lastClickedCard != null && _lastClickedCard.isClickUp()) {
			_lastClickedCard.setColor(LColor.white);
			if (_clickCardToMoveUp) {
				_lastClickedCard.setY(_lastClickedCard.getY() + _heightOffset);
			} else {
				_lastClickedCard.setY(_lastClickedCard.getY() - _heightOffset);
			}
		}
	}

	public void startClickCard() {
		final LComponent curClicked = getClickedChild();
		if (curClicked != null && curClicked.isClickUp()) {
			curClicked.setColor(_selectedColor);
			if (_clickCardToMoveUp) {
				curClicked.setY(curClicked.getY() - _heightOffset);
			} else {
				curClicked.setY(curClicked.getY() + _heightOffset);
			}
			_lastClickedCard = curClicked;
		} else {
			_lastClickedCard = null;
		}
	}

	public LComponent getClickedCard() {
		return this._lastClickedCard;
	}

	@Override
	public void process(long elapsedTime) {
		if (!_component_visible) {
			return;
		}
		if (_destroyed) {
			return;
		}
		if (_childs == null) {
			return;
		}
		if (!_updateCards) {
			updateCards();
		}
		if (isPointInUI() && isClickUp()) {
			setCardClick();
		}
	}

	public void setCardClick() {
		endClickedCard();
		startClickCard();
	}

	public boolean isForceFitContainer() {
		return this._forceFitContainer;
	}

	public LCardGroup setForceFitContainer(boolean f) {
		this._forceFitContainer = f;
		return this;
	}

	public LCardGroup setAlignment(HorizontalAlign h) {
		this._alignment = h;
		return this;
	}

	public HorizontalAlign getAlignment() {
		return this._alignment;
	}

	public LCardGroup updateCards() {
		if (isEmpty()) {
			return this;
		}
		setCardClick();
		setCardsPosition();
		setCardsRotation();
		setChildZOrders(_defaultSortOrder);
		_updateCards = true;
		return this;
	}

	public boolean isCardUpdated() {
		return this._updateCards;
	}

	public LCardGroup setCardUpdate(boolean u) {
		this._updateCards = u;
		return this;
	}

	private void setCardsRotation() {
		if (_childs == null) {
			return;
		}
		final int size = _childs.length;
		for (int i = 0; i < size; i++) {
			final LComponent comp = _childs[i];
			if (comp != null) {
				final int idx = i;
				final float angle = getCardRotation(idx);
				comp.setRotation(angle);
				comp.setLocation(comp.getX(), comp.getY() + getCardVerticalOffset(idx));
			}
		}
	}

	private void setCardsPosition() {
		final float cardsTotalWidth = getChildTotalWidth();
		final float containerWidth = getWidth();
		if (_forceFitContainer && cardsTotalWidth >= containerWidth) {
			matchChildrenToFitContainer(cardsTotalWidth);
		} else {
			matchChildrenWithoutOverlap(cardsTotalWidth);
		}
	}

	private float getAnchorPositionByAlignment(float childrenWidth) {
		if (_alignment == null) {
			return 0f;
		}
		float widthSpace = getWidth();
		switch (_alignment) {
		case LEFT:
			return getCenterX() - widthSpace / 2f;
		case CENTER:
			return getCenterX() - (childrenWidth / 2f);
		case RIGHT:
			return getCenterX() + widthSpace / 2f - childrenWidth;
		default:
			return 0f;
		}
	}

	private float getCardRotation(int index) {
		final int count = getChildCount();
		if (count < 3) {
			return 0;
		}
		return -(_cardRotation * (index - (count - 1f) / 2f) / ((count - 1f) / 2f));
	}

	private float getCardVerticalOffset(int index) {
		if (!_updateCards || _verticalOffsetY == -1) {
			final int count = getChildCount();
			if (count < 3) {
				return 0;
			}
			final float result = MathUtils.abs(_heightOffset
					* (1f - MathUtils.pow(index - (count - 1f) / 2f, 2f) / MathUtils.pow((count - 1f) / 2f, 2f)));
			float off = 0f;
			if (_childs != null && _childs.length > 0) {
				off = getChildTotalHeight() / count / 3f;
			}
			_verticalOffsetY = (_middleProtrusion ? -result : result) + off;
		}
		return _verticalOffsetY;
	}

	private void matchChildrenToFitContainer(float childrenTotalWidth) {
		if (_childs == null) {
			return;
		}
		final float width = getWidth();
		final float distanceBetweenChildren = (width - childrenTotalWidth) / (getChildCount() - 1);
		float currentX = getX();
		int size = _childs.length;
		for (int i = 0; i < size; i++) {
			LComponent comp = _childs[i];
			if (comp != null) {
				float adjustedChildWidth = comp.getWidth();
				comp.setLocation(currentX, getY());
				currentX += adjustedChildWidth + distanceBetweenChildren;
			}
		}

	}

	private void matchChildrenWithoutOverlap(float childrenTotalWidth) {
		if (_childs == null) {
			return;
		}
		float currentPosition = getAnchorPositionByAlignment(childrenTotalWidth);
		int size = _childs.length;
		for (int i = 0; i < size; i++) {
			LComponent comp = _childs[i];
			if (comp != null) {
				float adjustedChildWidth = comp.getWidth();
				float newX = currentPosition;
				comp.setLocation(newX, getY());
				currentPosition += adjustedChildWidth;
			}

		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	public String getUIName() {
		return "CardGroup";
	}

	@Override
	public void destory() {
	}

}
