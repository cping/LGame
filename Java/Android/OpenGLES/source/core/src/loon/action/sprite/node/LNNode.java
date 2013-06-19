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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchScreen;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.input.LInput;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LInputFactory.Touch;
import loon.core.input.LTouch;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

public class LNNode implements LRelease {
	
	public Object Tag;

	public void down(LTouch e) {

	}

	public void up(LTouch e) {

	}

	public void drag(LTouch e) {

	}

	private static final Comparator<LNNode> DEFAULT_COMPARATOR = new Comparator<LNNode>() {

		private int match(int x, int y) {
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}

		@Override
		public int compare(LNNode p1, LNNode p2) {
			if (p1 == null || p2 == null) {
				if (p1 != null) {
					return p1._zOrder;
				}
				if (p2 != null) {
					return p2._zOrder;
				}
				return 0;
			}
			if (Touch.isDrag()) {
				return p2._zOrder - p1._zOrder;
			}
			return match(p2._zOrder, p1._zOrder);
		}
	};
	private Comparator<LNNode> comparator = LNNode.DEFAULT_COMPARATOR;

	public static interface CallListener {

		public void act(float dt);

	}

	public LNClickListener Click;

	public void SetClick(LNClickListener c) {
		Click = c;
	}

	public LNClickListener GetClick() {
		return Click;
	}

	public CallListener Call;

	public void SetCall(CallListener u) {
		Call = u;
	}

	public CallListener GetCall() {
		return Call;
	}

	protected boolean _locked;

	public LNNode[] childs = new LNNode[0];

	protected int _childCount = 0;

	private LNNode latestInserted = null;

	protected ArrayList<LNAction> _actionList;

	protected Vector2f _anchor = new Vector2f();

	protected LColor _color;

	protected int _size_width, _size_height;

	protected int _orig_width, _orig_height;

	protected void setWidth(int w) {
		this._size_width = w;
		if (_orig_width == 0) {
			this._orig_width = w;
		}
	}

	protected void setHieght(int h) {
		this._size_height = h;
		if (_orig_height == 0) {
			this._orig_height = h;
		}
	}

	void setNodeSize(int w, int h) {
		setWidth(w);
		setHieght(h);
	}

	protected int _top;

	protected int _left;

	protected float _alpha;

	protected LNNode _parent = null;

	protected final Vector2f _position = new Vector2f();

	protected float _rotation;

	protected float _rotationAlongX;

	protected float _rotationAlongY;

	protected final Vector2f _scale = new Vector2f(1f, 1f);

	protected boolean _visible = true;

	protected int _zOrder = 0;

	protected boolean _autoDestroy;

	protected boolean _isClose;

	private int cam_x, cam_y;

	protected int _screenX, _screenY;

	protected boolean _enabled = true;

	protected boolean _focusable = true;

	protected boolean _selected = false;

	protected boolean _limitMove;

	protected SpriteBatchScreen _screen;

	protected RectBox _screenRect;

	protected LInput _input;

	protected final Vector2f _offset = new Vector2f();

	LNNode() {
		this(LSystem.screenRect);
	}

	public LNNode(int x, int y, int width, int height) {
		this(null, x, y, width, height);
	}

	public LNNode(RectBox rect) {
		this(null, rect.x(), rect.y(), rect.width, rect.height);
	}

	public LNNode(SpriteBatchScreen screen, RectBox rect) {
		this(screen, rect.x(), rect.y(), rect.width, rect.height);
	}

	public LNNode(SpriteBatchScreen screen, int x, int y, int width, int height) {
		this.setLocation(x, y);
		this._rotation = 0f;
		this.scale[0] = 1f;
		this.scale[1] = 1f;
		this._scale.x = 1f;
		this._scale.y = 1f;
		this._alpha = 1f;
		this._left = 0;
		this._top = 0;
		this._screen = screen;
		this._color = new LColor(0xff, 0xff, 0xff, 0xff);
		this._actionList = new ArrayList<LNAction>();
		this._limitMove = true;
		this._locked = true;
		this._size_width = width;
		this._size_height = height;
		this._screenRect = LSystem.screenRect;
		if (this._size_width == 0) {
			this._size_width = 10;
		}
		if (this._size_height == 0) {
			this._size_height = 10;
		}
	}

	public boolean isLocked() {
		return _locked;
	}

	public void setLocked(boolean locked) {
		this._locked = locked;
	}

	public void addNode(LNNode node) {
		this.addNode(node, 0);
	}

	public synchronized void addNode(LNNode node, int z) {
		if (this.contains(node)) {
			return;
		}
		if (node.getContainer() != null) {
			node.setContainer(null);
		}
		node.setContainer(this);
		int index = 0;
		boolean flag = false;
		for (int i = 0; i < this._childCount; i++) {
			LNNode node2 = this.childs[i];
			int zd = 0;
			if (node2 != null) {
				zd = node2.getZOrder();
			}
			if (zd > z) {
				flag = true;
				this.childs = (LNNode[]) CollectionUtils.expand(this.childs, 1,
						false);
				childs[index] = node;
				_childCount++;
				node.setScreen(_screen);
				this.latestInserted = node;
				break;
			}
			index++;
		}
		if (!flag) {
			this.childs = (LNNode[]) CollectionUtils.expand(this.childs, 1,
					false);
			this.childs[0] = node;
			this._childCount++;
			node.setScreen(_screen);
			this.latestInserted = node;
		}
		node.setZOrder(z);
		node.setParent(this);
		Arrays.sort(childs, comparator);
	}

	public synchronized void add(LNNode node, int index) {
		if (node.getContainer() != null) {
			throw new IllegalStateException(node
					+ " already reside in another node!!!");
		}
		node.setContainer(this);
		LNNode[] newChilds = new LNNode[this.childs.length + 1];
		this._childCount++;
		int ctr = 0;
		for (int i = 0; i < this._childCount; i++) {
			if (i != index) {
				newChilds[i] = this.childs[ctr];
				ctr++;
			}
		}
		this.childs = newChilds;
		this.childs[index] = node;
		node.setScreen(_screen);
		this.sortComponents();
		this.latestInserted = node;
	}

	public synchronized boolean contains(LNNode node) {
		if (node == null) {
			return false;
		}
		if (childs == null) {
			return false;
		}
		for (int i = 0; i < this._childCount; i++) {
			if (childs[i] != null && node.equals(childs[i])) {
				return true;
			}
		}
		return false;
	}

	public synchronized int removeNode(LNNode node) {
		for (int i = 0; i < this._childCount; i++) {
			if (this.childs[i] == node) {
				this.removeNode(i);
				return i;
			}
		}
		return -1;
	}

	public synchronized int removeNode(Class<? extends LNNode> clazz) {
		if (clazz == null) {
			return -1;
		}
		int count = 0;
		for (int i = _childCount; i > 0; i--) {
			int index = i - 1;
			LNNode node = this.childs[index];
			Class<? extends LNNode> cls = node.getClass();
			if (clazz == null || clazz == cls || clazz.isInstance(node)
					|| clazz.equals(cls)) {
				this.removeNode(index);
				count++;
			}
		}
		return count;
	}

	public synchronized LNNode removeNode(int index) {
		LNNode node = this.childs[index];
		this._screen.setNodeStat(node, false);
		node.setContainer(null);
		this.childs = (LNNode[]) CollectionUtils.cut(this.childs, index);
		this._childCount--;
		return node;
	}

	public void clear() {
		this._screen.clearNodesStat(this.childs);
		for (int i = 0; i < this._childCount; i++) {
			this.childs[i].setContainer(null);
		}
		this.childs = new LNNode[0];
		this._childCount = 0;
	}

	public synchronized void replace(LNNode oldComp, LNNode newComp) {
		int index = this.removeNode(oldComp);
		this.add(newComp, index);
	}

	public void close() {
		if (_screen != null) {
			if (_screen.removeNode(this) != -1) {
				this.dispose();
			}
		}
	}

	public void update(float dt) {

	}

	public final void updateNode(float dt) {
		if (_isClose) {
			return;
		}
		synchronized (childs) {
			if (_isClose) {
				return;
			}
			if (_parent != null) {
				validatePosition();
			}
			if (Call != null) {
				Call.act(dt);
			}
			for (int i = 0; i < _actionList.size(); i++) {
				if (this._actionList.get(i).isEnd()) {
					this._actionList.remove(this._actionList.get(i));
					i--;
				} else {
					this._actionList.get(i).step(dt);
					if (this._actionList.isEmpty()) {
						break;
					}
					if (this._actionList.get(i).isEnd()) {
						this._actionList.remove(this._actionList.get(i));
						i--;
					}
				}
			}
			LNNode component;
			for (int i = 0; i < this._childCount; i++) {
				component = childs[i];
				if (component != null) {
					component.updateNode(dt);
				}
			}
		}
		update(dt);
	}

	protected void validateSize() {
		for (int i = 0; i < this._childCount; i++) {
			if (this.childs[i] != null) {
				this.childs[i].validateSize();
			}
		}
	}

	public void sendToFront(LNNode node) {
		if (this.childs == null) {
			return;
		}
		if (this._childCount <= 1 || this.childs[0] == node) {
			return;
		}
		if (childs[0] == node) {
			return;
		}
		for (int i = 0; i < this._childCount; i++) {
			if (this.childs[i] == node) {
				this.childs = (LNNode[]) CollectionUtils.cut(this.childs, i);
				this.childs = (LNNode[]) CollectionUtils.expand(this.childs, 1,
						false);
				this.childs[0] = node;
				this.sortComponents();
				break;
			}
		}
	}

	public void sendToBack(LNNode node) {
		if (this._childCount <= 1 || this.childs[this._childCount - 1] == node) {
			return;
		}
		if (childs[this._childCount - 1] == node) {
			return;
		}
		for (int i = 0; i < this._childCount; i++) {
			if (this.childs[i] == node) {
				this.childs = (LNNode[]) CollectionUtils.cut(this.childs, i);
				this.childs = (LNNode[]) CollectionUtils.expand(this.childs, 1,
						true);
				this.childs[this._childCount - 1] = node;
				this.sortComponents();
				break;
			}
		}
	}

	public void sortComponents() {
		Arrays.sort(this.childs, this.comparator);
	}

	protected void transferFocus(LNNode component) {
		for (int i = 0; i < this._childCount; i++) {
			if (component == this.childs[i]) {
				int j = i;
				do {
					if (--i < 0) {
						i = this._childCount - 1;
					}
					if (i == j) {
						return;
					}
				} while (!this.childs[i].requestFocus());

				break;
			}
		}
	}

	protected void transferFocusBackward(LNNode component) {
		for (int i = 0; i < this._childCount; i++) {
			if (component == this.childs[i]) {
				int j = i;
				do {
					if (++i >= this._childCount) {
						i = 0;
					}
					if (i == j) {
						return;
					}
				} while (!this.childs[i].requestFocus());

				break;
			}
		}
	}

	public Comparator<LNNode> getComparator() {
		return this.comparator;
	}

	public void setComparator(Comparator<LNNode> c) {
		if (c == null) {
			throw new NullPointerException("Comparator can not null !");
		}

		this.comparator = c;
		this.sortComponents();
	}

	public LNNode findNode(int x1, int y1) {
		if (!this.intersects(x1, y1)) {
			return null;
		}
		for (int i = 0; i < this._childCount; i++) {
			if (childs[i] != null) {
				if (this.childs[i].intersects(x1, y1)) {
					LNNode node = isContainer() ? this.childs[i]
							: (this.childs[i]).findNode(x1, y1);
					return node;
				}
			}
		}
		return this;
	}

	public int getNodeCount() {
		return this._childCount;
	}

	public LNNode[] getNodes() {
		return this.childs;
	}

	public LNNode get() {
		return this.latestInserted;
	}

	public void draw(SpriteBatch batch) {

	}

	public final void drawNode(SpriteBatch batch) {
		if (_isClose) {
			return;
		}
		if (!this._visible) {
			return;
		}
		for (int i = this._childCount - 1; i >= 0; i--) {
			if (childs[i] != null && childs[i].getZOrder() < 0) {
				childs[i].drawNode(batch);
			}
		}
		this.draw(batch);
		int zOrder = 0;
		for (int i = this._childCount - 1; i >= 0; i--) {
			LNNode o = this.childs[i];
			if (o != null) {
				if (o.getZOrder() >= 0) {
					if (zOrder == 0) {
						zOrder = o.getZOrder();
					} else {
						zOrder = o.getZOrder();
					}

					o.drawNode(batch);
				}
			}
		}
	}

	public void setOffset(float x, float y) {
		this._offset.set(x, y);
	}

	public void setOffset(Vector2f v) {
		this._offset.set(v);
	}

	public Vector2f getOffset() {
		return this._offset;
	}

	private float[] pos = new float[2];

	public float[] convertToWorldPos() {
		pos[0] = _offset.x + _position.x;
		pos[1] = _offset.y + _position.y;
		if (this._parent != null) {
			float[] result = this._parent.convertToWorldPos();
			pos[0] += result[0];
			pos[1] += result[1];
		}
		return pos;
	}

	private float[] scale = new float[2];

	public float[] convertToWorldScale() {
		scale[0] = _scale.x;
		scale[1] = _scale.y;
		if (this._parent != null) {
			float[] result = this._parent.convertToWorldScale();
			scale[0] *= result[0];
			scale[1] *= result[1];
		}
		return scale;
	}

	public float convertToWorldRot() {
		float num = 0f;
		if (this._parent != null) {
			num += this._parent.convertToWorldRot();
		}
		return (num + this._rotation);
	}

	public void onSceneActive() {
	}

	public final void pauseAllAction() {
		for (LNAction action : this._actionList) {
			action.pause();
		}
	}

	public final void reorderNode(LNNode node, int NewOrder) {
		this.removeNode(node);
		this.addNode(node, NewOrder);
	}

	public final void resumeAllAction() {
		for (LNAction action : this._actionList) {
			action.resume();
		}
	}

	public final void removeAction(LNAction action) {
		_actionList.remove(action);
		action._target = null;
	}

	public final void stopAllAction() {
		for (LNAction action : _actionList) {
			action._isEnd = true;
		}
		this._actionList.clear();
	}
	
	public final void runAction(LNAction action) {
		this._actionList.add(action);
		action.setTarget(this);
	}

	public void setColor(LColor c) {
		this._color.setColor(c);
	}

	public void setColor(int r, int g, int b) {
		this._color.setColor(r, g, b);
	}

	public void setColor(int r, int g, int b, int a) {
		this._color.setColor(r, g, b, a);
	}

	public void setColor(float r, float g, float b) {
		this._color.setColor(r, g, b);
	}

	public void setColor(float r, float g, float b, float a) {
		this._color.setColor(r, g, b, a);
	}

	public void setLimitMove(boolean v) {
		this._limitMove = v;
	}

	public boolean isLimitMove() {
		return _limitMove;
	}

	public void setPosition(float x, float y) {
		this._position.set(x, y);
	}

	public void setPositionOrig(Vector2f v) {
		setPositionOrig(v.x, v.y);
	}

	public void setPositionOrig(float x, float y) {
		this._position.set((x + this._anchor.x) - (_screenRect.width / 2),
				(_screenRect.height / 2) - (y + this._anchor.y));
	}

	public void setPosition(Vector2f newPosition) {
		if (!newPosition.equals(this._position)) {
			this.position(newPosition);
		}
	}

	public void setPositionBL(float x, float y) {
		this.setPosition(
				(x + this._anchor.x) - (_screenRect.width / 2),
				((_screenRect.height / 2) - (((_screenRect.height - y) - this._size_height) + this._anchor.y)));
	}

	public void setPositionBR(float x, float y) {
		this.setPosition(
				(((_screenRect.width - x) - this._size_width) + this._anchor.x)
						- (_screenRect.width / 2),
				(_screenRect.height / 2)
						- (((_screenRect.height - y) - this._size_height) + this._anchor.y));
	}

	public void setPositionTL(float x, float y) {
		this.setPosition(
				(x + this._anchor.x) - (_screenRect.width / 2),
				((_screenRect.height / 2) - (_screenRect.height - (y + this._anchor.y))));
	}

	public void setPositionTR(float x, float y) {
		this.setPosition(
				((((_screenRect.width - x) - this._size_width) + this._anchor.x) - (_screenRect.width / 2)) + 240,
				((_screenRect.height / 2) - (y + this._anchor.y)) + 160);

	}

	public Vector2f getAnchor() {
		return this._anchor;
	}

	public void setAnchor(Vector2f v) {
		this._anchor = v;
	}

	public LColor getColor() {
		return _color;
	}

	public float getAlpha() {
		return _alpha;
	}

	public float getOpacity() {
		return _alpha * 255;
	}

	public void setOpacity(float o) {
		this._alpha = o / 255f;
	}

	public void setAlpha(float v) {
		this._alpha = v;
		this._color.a = this._alpha;
	}

	public LNNode getParent() {
		return this._parent;
	}

	public void setParent(LNNode v) {
		this._parent = v;
	}

	public Vector2f getPosition() {
		return this._position;
	}

	public void position(Vector2f v) {
		this._position.set(v);
	}

	public float getRotation() {
		return MathUtils.toDegrees(this._rotation);
	}

	public final void setRotation(float v) {
		this._rotation = MathUtils.toRadians(v);
	}

	public final Vector2f getScale() {
		return this._scale;
	}

	public final void setScale(Vector2f value) {
		this._scale.set(value);
	}

	public final void setScale(float x, float y) {
		this._scale.set(x, y);
	}

	public final float getScaleX() {
		return this._scale.x;
	}

	public final void setScaleX(float value) {
		this._scale.x = value;
	}

	public final float getScaleY() {
		return this._scale.y;
	}

	public final void setScaleY(float value) {
		this._scale.y = value;
	}

	public final int getZOrder() {
		return this._zOrder;
	}

	public final void setZOrder(int value) {
		this._zOrder = value;
	}

	public int getScreenWidth() {
		return _screenRect.width;
	}

	public int getScreenHeight() {
		return _screenRect.height;
	}

	public int getWidth() {
		return (int) (_size_width * scale[0]);
	}

	public int getHeight() {
		return (int) (_size_height * scale[1]);
	}

	public void moveCamera(int x, int y) {
		if (!this._limitMove) {
			setLocation(x, y);
			return;
		}
		int tempX = x;
		int tempY = y;
		int tempWidth = (getWidth() - _screenRect.width);
		int tempHeight = (getHeight() - _screenRect.height);

		int limitX = tempX + tempWidth;
		int limitY = tempY + tempHeight;

		if (_size_width >= _screenRect.width) {
			if (limitX > tempWidth) {
				tempX = (_screenRect.width - _size_width);
			} else if (limitX < 1) {
				tempX = _position.x();
			}
		} else {
			return;
		}
		if (_size_height >= _screenRect.height) {
			if (limitY > tempHeight) {
				tempY = (_screenRect.height - _size_height);
			} else if (limitY < 1) {
				tempY = _position.y();
			}
		} else {
			return;
		}
		this.cam_x = tempX;
		this.cam_y = tempY;
		this.setLocation(cam_x, cam_y);
	}

	protected boolean isNotMoveInScreen(int x, int y) {
		if (!this._limitMove) {
			return false;
		}
		int width = (getWidth() - _screenRect.width);
		int height = (getHeight() - _screenRect.height);
		int limitX = x + width;
		int limitY = y + height;
		if (getWidth() >= _screenRect.width) {
			if (limitX >= width - 1) {
				return true;
			} else if (limitX <= 1) {
				return true;
			}
		} else {
			if (!_screenRect.contains(x, y, getWidth(), getHeight())) {
				return true;
			}
		}
		if (getHeight() >= _screenRect.height) {
			if (limitY >= height - 1) {
				return true;
			} else if (limitY <= 1) {
				return true;
			}
		} else {
			if (!_screenRect.contains(x, y, getWidth(), getHeight())) {
				return true;
			}
		}
		return false;
	}

	public boolean isContainer() {
		return true;
	}

	public boolean contains(int x, int y) {
		return contains(x, y, 0, 0);
	}

	public boolean contains(int x, int y, int width, int height) {
		return (this._visible)
				&& (x >= pos[0] && y >= pos[1]
						&& ((x + width) <= (pos[0] + getWidth())) && ((y + height) <= (pos[1] + getHeight())));
	}

	public boolean intersects(int x1, int y1) {
		return (this._visible)
				&& (x1 >= pos[0] && x1 <= pos[0] + getWidth() && y1 >= pos[1] && y1 <= pos[1]
						+ getHeight());
	}

	public boolean intersects(LNNode node) {
		float[] nodePos = node.convertToWorldPos();
		return (this._visible)
				&& (node._visible)
				&& (pos[0] + getWidth() >= nodePos[0]
						&& pos[0] <= nodePos[0] + node.getWidth()
						&& pos[1] + getWidth() >= nodePos[1] && pos[1] <= nodePos[1]
						+ node.getHeight());
	}

	public boolean isVisible() {
		return this._visible;
	}

	public void setVisible(boolean visible) {
		if (this._visible == visible) {
			return;
		}
		this._visible = visible;
		if (_screen != null) {
			this._screen.setNodeStat(this, this._visible);
		}
	}

	public boolean isEnabled() {
		return (this._parent == null) ? this._enabled
				: (this._enabled && this._parent.isEnabled());
	}

	public void setEnabled(boolean b) {
		if (this._enabled == b) {
			return;
		}
		this._enabled = b;
		this._screen.setNodeStat(this, this._enabled);
	}

	public boolean isSelected() {
		if (!_selected) {
			for (int i = 0; i < this._childCount; i++) {
				if (this.childs[i].isSelected()) {
					return true;
				}
			}
			return false;

		} else {
			return true;
		}
	}

	public final void setSelected(boolean b) {
		this._selected = b;
	}

	public boolean requestFocus() {
		return this._screen.selectNode(this);
	}

	public void transferFocus() {
		if (this.isSelected() && this._parent != null) {
			this._parent.transferFocus(this);
		}
	}

	public void transferFocusBackward() {
		if (this.isSelected() && this._parent != null) {
			this._parent.transferFocusBackward(this);
		}
	}

	public boolean isFocusable() {
		return this._focusable;
	}

	public void setFocusable(boolean b) {
		this._focusable = b;
	}

	public LNNode getContainer() {
		return this._parent;
	}

	final void setContainer(LNNode node) {
		this._parent = node;
		this.validatePosition();
	}

	public final void setScreen(SpriteBatchScreen s) {
		if (s == _screen) {
			return;
		}
		this._screen = s;
		this._input = s.getInput();
	}

	public void setBounds(float dx, float dy, int width, int height) {
		setLocation(dx, dy);
		if (this._size_width != width || this._size_height != height) {
			this._size_width = width;
			this._size_height = height;
			if (width == 0) {
				width = 1;
			}
			if (height == 0) {
				height = 1;
			}
			this.validateSize();
		}
	}

	public int getX() {
		return _position.x();
	}

	public int getY() {
		return _position.y();
	}

	public void setX(Integer x) {
		if (this._position.x != x || x == 0) {
			this._position.x = x;
			this.validatePosition();
		}
	}

	public void setX(float x) {
		if (this._position.x != x || x == 0) {
			this._position.x = x;
			this.validatePosition();
		}
	}

	public void setY(Integer y) {
		if (this._position.y != y || y == 0) {
			this._position.y = y;
			this.validatePosition();
		}
	}

	public void setY(float y) {
		if (this._position.y != y || y == 0) {
			this._position.y = y;
			this.validatePosition();
		}
	}

	public void setLocation(Vector2f location) {
		setLocation(location.x, location.y);
	}

	public void setLocation(float dx, float dy) {
		if (this._position.x != dx || this._position.y != dy || dx == 0
				|| dy == 0) {
			this._position.set(dx, dy);
			this.validatePosition();
		}
	}

	public void move(float dx, float dy) {
		if (dx != 0 || dy != 0) {
			if (dx > -100 && dx < 100 && dy > -100 && dy < 100) {
				if (_parent != null && _limitMove) {
					if (_parent.contains((int) (pos[0] + dx),
							(int) (pos[1] + dy), _size_width, _size_height)) {
						this._position.move(dx, dy);
						this.validatePosition();
					}
				} else {
					this._position.move(dx, dy);
					this.validatePosition();
				}
			}
		}
	}

	public void setSize(int w, int h) {
		if (this._size_width != w || this._size_height != h) {
			this._size_width = w;
			this._size_height = h;
			if (this._size_width == 0) {
				this._size_width = 1;
			}
			if (this._size_height == 0) {
				this._size_height = 1;
			}
			this.validateSize();
		}
	}

	public void validatePosition() {
		if (_isClose) {
			return;
		}
		if (_parent != null) {
			this._screenX = (int) pos[0];
			this._screenY = (int) pos[1];
		} else {
			this._screenX = _position.x();
			this._screenY = _position.y();
		}
		for (int i = 0; i < this._childCount; i++) {
			if (this.childs[i] != null) {
				this.childs[i].validatePosition();
			}
		}
	}

	private RectBox temp_rect;

	public RectBox getRectBox() {
		if (_rotation != 0) {
			int[] result = MathUtils.getLimit(_position.getX(),
					_position.getY(), getWidth(), getHeight(),
					MathUtils.toDegrees(_rotation));
			if (temp_rect == null) {
				temp_rect = new RectBox(result[0], result[1], result[2],
						result[3]);
			} else {
				temp_rect.setBounds(result[0], result[1], result[2], result[3]);
			}
		} else {
			if (temp_rect == null) {
				temp_rect = new RectBox(_position.getX(), _position.getY(),
						getWidth(), getHeight());
			} else {
				temp_rect.setBounds(_position.getX(), _position.getY(),
						getWidth(), getHeight());
			}
		}
		return temp_rect;
	}

	public int getScreenX() {
		return this._screenX;
	}

	public int getScreenY() {
		return this._screenY;
	}

	public void processTouchPressed() {
		if (!_visible || !_enabled) {
			return;
		}
		if (Click != null) {
			Click.DownClick(this, Touch.getX(), Touch.getY());
		}
	}

	public void processTouchReleased() {
		if (!_visible || !_enabled) {
			return;
		}
		if (Click != null) {
			Click.UpClick(this, Touch.getX(), Touch.getY());
		}
	}

	public void processTouchDragged() {
		if (!_visible || !_enabled) {
			return;
		}
		if (!_locked && _input != null) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this._input.getTouchDX(), this._input.getTouchDY());
		}
		if (Click != null) {
			Click.DragClick(this, Touch.getX(), Touch.getY());
		}
	}

	public void processKeyPressed() {

	}

	public void processKeyReleased() {
	}

	public final void keyPressed() {
		this.checkFocusKey();
		this.processKeyPressed();
	}

	void checkFocusKey() {
		if (_input != null && this._input.getKeyPressed() == Key.ENTER) {
			this.transferFocus();
		} else {
			this.transferFocusBackward();
		}
	}

	public int getCamX() {
		return cam_x == 0 ? _position.x() : cam_x;
	}

	public int getCamY() {
		return cam_y == 0 ? _position.y() : cam_y;
	}

	public boolean isClose() {
		return _isClose;
	}

	public void setAutoDestroy(boolean flag) {
		this._autoDestroy = flag;
	}

	public boolean isAutoDestory() {
		return _autoDestroy;
	}

	@Override
	public void dispose() {
		this._isClose = true;
		if (this._parent != null) {
			this._parent.removeNode(this);
		}
		this._selected = false;
		this._visible = false;
		if (_screen != null) {
			this._screen.setNodeStat(this, false);
		}
		if (_autoDestroy) {
			if (childs != null) {
				for (LNNode c : childs) {
					if (c != null) {
						c.dispose();
					}
				}
			}
		}
	}

}
