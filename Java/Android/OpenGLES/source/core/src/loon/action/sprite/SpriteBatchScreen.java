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
package loon.action.sprite;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import loon.action.map.Config;
import loon.action.map.TileMap;
import loon.action.sprite.node.DefinitionReader;
import loon.action.sprite.node.LNAction;
import loon.action.sprite.node.LNNode;
import loon.core.LObject;
import loon.core.LSystem;
import loon.core.event.ActionKey;
import loon.core.geom.AABB;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;
import loon.core.input.LInput;
import loon.core.input.LKey;
import loon.core.input.LInputFactory.Touch;
import loon.core.timer.LTimerContext;
import loon.physics.PBody;
import loon.physics.PPhysManager;
import loon.physics.PShape;
import loon.physics.PWorldBox;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.collection.ArrayMap;

/**
 * 该类为0.3.3版最新增加的Screen类，图形渲染使用单一的SpriteBatch，相较于使用GLEx，更适合多纹理渲染。
 * 
 */
public abstract class SpriteBatchScreen extends Screen implements Config {

	private int keySize = 0;

	private float objX = 0, objY = 0;

	private ArrayMap keyActions = new ArrayMap(CollectionUtils.INITIAL_CAPACITY);

	private SpriteBatch batch;

	private ArrayList<SpriteBatchObject> objects;

	private ArrayList<SpriteBatchObject> pendingAdd;

	private ArrayList<SpriteBatchObject> pendingRemove;

	private ArrayList<TileMap> tiles = new ArrayList<TileMap>(10);

	private Vector2f offset = new Vector2f();

	private LObject follow;

	private TileMap indexTile;

	private LNNode content;

	private LNNode modal;

	private LNNode hoverNode;

	private LNNode selectedNode;

	private LNNode[] clickNode = new LNNode[1];

	private boolean isClicked;

	protected UpdateListener updateListener;

	private boolean usePhysics = false;

	private PPhysManager _manager;

	private PWorldBox _box;

	private boolean _fixed = false;

	private float _dt = 1F / 60F;

	private void limitWorld(boolean _fixed) {
		if (_fixed) {
			if (this._box == null) {
				this._box = new PWorldBox(_manager, 0f, 0f, getWidth(),
						getHeight());
			}
			if (_physicsRect != null) {
				this._box.set(_physicsRect.x, _physicsRect.y,
						_physicsRect.width, _physicsRect.height);
			}
			this._box.build();
		} else {
			if (_box != null) {
				this._box.removeWorld();
			}
		}
	}

	public PPhysManager getPhysicsManager() {
		if (!usePhysics) {
			throw new RuntimeException("You do not set the physics engine !");
		}
		return _manager;
	}

	public boolean isPhysics() {
		return usePhysics;
	}

	private RectBox _physicsRect;

	public void setPhysicsRect(float x, float y, float w, float h) {
		if (this._physicsRect == null) {
			this._physicsRect = new RectBox(x, y, w, h);
		} else {
			this._physicsRect.setBounds(x, y, w, h);
		}
	}

	public void setPhysics(boolean fix, PPhysManager man) {
		this._manager = man;
		this._fixed = fix;
		this.limitWorld(_fixed);
		this.usePhysics = true;
	}

	public void setPhysics(boolean fix, float scale, float gx, float gy) {
		if (_manager == null) {
			this._manager = new PPhysManager(scale, gx, gy);
		} else {
			this._manager.scale = scale;
			this._manager.gravity.set(gx, gy);
		}
		this._manager.setEnableGravity(true);
		this._manager.setStart(true);
		this._fixed = fix;
		this.limitWorld(_fixed);
		this.usePhysics = true;
	}

	public void setPhysics(boolean fix) {
		setPhysics(fix, 10F);
	}

	public void setPhysics(boolean fix, float scale) {
		if (_manager == null) {
			this._manager = new PPhysManager(scale);
		} else {
			this._manager.scale = scale;
		}
		this._manager.setEnableGravity(true);
		this._manager.setStart(true);
		this._fixed = fix;
		this.limitWorld(_fixed);
		this.usePhysics = true;
	}

	public float getTimeStep() {
		return this._dt;
	}

	public void setTimeStep(float dt) {
		this._dt = dt;
	}

	@Override
	public void onResume() {
		if (usePhysics) {
			_manager.setStart(true);
			_manager.setEnableGravity(true);
		}
	}

	@Override
	public void onPause() {
		if (usePhysics) {
			_manager.setStart(false);
			_manager.setEnableGravity(false);
		}
	}

	public boolean isFixed() {
		return _fixed;
	}

	public interface UpdateListener {

		public void act(SpriteBatchObject obj, long elapsedTime);

	}

	public synchronized void loadNodeDef(String resName) {
		DefinitionReader.get().load(resName);
	}

	public synchronized void loadNodeDef(InputStream res) {
		DefinitionReader.get().load(res);
	}

	public SpriteBatchScreen() {
		super();
		this.objects = new ArrayList<SpriteBatchObject>(10);
		this.pendingAdd = new ArrayList<SpriteBatchObject>(10);
		this.pendingRemove = new ArrayList<SpriteBatchObject>(10);
		this.init();
	}

	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	private void init() {
		setNode(new LNNode(this, LSystem.screenRect));
	}

	public void setNode(LNNode node) {
		if (content == node) {
			return;
		}
		this.content = node;
	}

	public LNNode node() {
		return content;
	}

	public int size() {
		return content == null ? 0 : content.getNodeCount();
	}

	public void runAction(LNAction action) {
		if (content != null) {
			content.runAction(action);
		}
	}

	public void addNode(LNNode node) {
		addNode(node, 0);
	}

	public void add(LNNode node) {
		addNode(node, 0);
	}

	public void addNode(LNNode node, int z) {
		if (node == null) {
			return;
		}
		this.content.addNode(node, z);
		this.processTouchMotionEvent();
	}

	public int removeNode(LNNode node) {
		int removed = this.removeNode(this.content, node);
		if (removed != -1) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	public int removeNode(Class<? extends LNNode> clazz) {
		int removed = this.removeNode(this.content, clazz);
		if (removed != -1) {
			this.processTouchMotionEvent();
		}
		return removed;
	}

	private int removeNode(LNNode container, LNNode node) {
		int removed = container.removeNode(node);
		LNNode[] nodes = container.childs;
		int i = 0;
		while (removed == -1 && i < nodes.length - 1) {
			if (nodes[i].isContainer()) {
				removed = this.removeNode(nodes[i], node);
			}
			i++;
		}

		return removed;
	}

	private int removeNode(LNNode container, Class<? extends LNNode> clazz) {
		int removed = container.removeNode(clazz);
		LNNode[] nodes = container.childs;
		int i = 0;
		while (removed == -1 && i < nodes.length - 1) {
			if (nodes[i].isContainer()) {
				removed = this.removeNode(nodes[i], clazz);
			}
			i++;
		}
		return removed;
	}

	private void processEvents() {
		this.processTouchMotionEvent();
		if (this.hoverNode != null && this.hoverNode.isEnabled()) {
			this.processTouchEvent();
		}
		if (this.selectedNode != null && this.selectedNode.isEnabled()) {
			this.processKeyEvent();
		}
	}

	private void processTouchMotionEvent() {
		if (this.hoverNode != null && this.hoverNode.isEnabled()
				&& Touch.isDrag()) {
			if (getTouchDY() != 0 || getTouchDY() != 0) {
				this.hoverNode.processTouchDragged();
			}
		} else {
			if (Touch.isDrag() || Touch.isMove() || Touch.isDown()) {
				LNNode node = this.findNode(getTouchX(), getTouchY());
				if (node != null) {
					this.hoverNode = node;
				}
			}
		}
	}

	private void processTouchEvent() {
		int pressed = getTouchPressed(), released = getTouchReleased();
		if (pressed > LInput.NO_BUTTON) {
			if (!isClicked) {
				this.hoverNode.processTouchPressed();
			}
			this.clickNode[0] = this.hoverNode;
			if (this.hoverNode.isFocusable()) {
				if ((pressed == Touch.TOUCH_DOWN || pressed == Touch.TOUCH_UP)
						&& this.hoverNode != this.selectedNode) {
					this.selectNode(this.hoverNode);
				}
			}
		}
		if (released > LInput.NO_BUTTON) {
			if (!isClicked) {
				this.hoverNode.processTouchReleased();
			}
		}
		this.isClicked = false;
	}

	private void processKeyEvent() {
		if (getKeyPressed() != LInput.NO_KEY) {
			this.selectedNode.keyPressed();
		}
		if (getKeyReleased() != LInput.NO_KEY && this.selectedNode != null) {
			this.selectedNode.processKeyReleased();
		}
	}

	public LNNode findNode(int x, int y) {
		if (content == null) {
			return null;
		}
		if (this.modal != null && !this.modal.isContainer()) {
			return content.findNode(x, y);
		}
		LNNode panel = (this.modal == null) ? this.content : (this.modal);
		LNNode node = panel.findNode(x, y);
		return node;
	}

	public void clearFocus() {
		this.deselectNode();
	}

	void deselectNode() {
		if (this.selectedNode == null) {
			return;
		}
		this.selectedNode.setSelected(false);
		this.selectedNode = null;
	}

	public boolean selectNode(LNNode node) {
		if (!node.isVisible() || !node.isEnabled() || !node.isFocusable()) {
			return false;
		}
		this.deselectNode();
		node.setSelected(true);
		this.selectedNode = node;
		return true;
	}

	public void setNodeStat(LNNode node, boolean active) {
		if (!active) {
			if (this.hoverNode == node) {
				this.processTouchMotionEvent();
			}
			if (this.selectedNode == node) {
				this.deselectNode();
			}
			this.clickNode[0] = null;
			if (this.modal == node) {
				this.modal = null;
			}
		} else {
			this.processTouchMotionEvent();
		}
		if (node == null) {
			return;
		}
		if (node.isContainer()) {
			LNNode[] nodes = (node).childs;
			int size = (node).getNodeCount();
			for (int i = 0; i < size; i++) {
				this.setNodeStat(nodes[i], active);
			}
		}
	}

	public void clearNodesStat(LNNode[] node) {
		boolean checkTouchMotion = false;
		for (int i = 0; i < node.length; i++) {
			if (this.hoverNode == node[i]) {
				checkTouchMotion = true;
			}

			if (this.selectedNode == node[i]) {
				this.deselectNode();
			}

			this.clickNode[0] = null;

		}

		if (checkTouchMotion) {
			this.processTouchMotionEvent();
		}
	}

	final void validateContainer(LNNode container) {
		if (content == null) {
			return;
		}
		LNNode[] nodes = container.childs;
		int size = container.getNodeCount();
		for (int i = 0; i < size; i++) {
			if (nodes[i].isContainer()) {
				this.validateContainer(nodes[i]);
			}
		}
	}

	public ArrayList<LNNode> getNodes(Class<? extends LNNode> clazz) {
		if (content == null) {
			return null;
		}
		if (clazz == null) {
			return null;
		}
		LNNode[] nodes = content.childs;
		int size = nodes.length;
		ArrayList<LNNode> l = new ArrayList<LNNode>(size);
		for (int i = size; i > 0; i--) {
			LNNode node = nodes[i - 1];
			Class<? extends LNNode> cls = node.getClass();
			if (clazz == null || clazz == cls || clazz.isInstance(node)
					|| clazz.equals(cls)) {
				l.add(node);
			}
		}
		return l;
	}

	public LNNode getTopNode() {
		if (content == null) {
			return null;
		}
		LNNode[] nodes = content.childs;
		int size = nodes.length;
		if (size > 1) {
			return nodes[1];
		}
		return null;
	}

	public LNNode getBottomNode() {
		if (content == null) {
			return null;
		}
		LNNode[] nodes = content.childs;
		int size = nodes.length;
		if (size > 0) {
			return nodes[size - 1];
		}
		return null;
	}

	public void setSize(int w, int h) {
		if (content != null) {
			this.content.setSize(w, h);
		}
	}

	public LNNode getHoverNode() {
		return this.hoverNode;
	}

	public LNNode getSelectedNode() {
		return this.selectedNode;
	}

	public LNNode getModal() {
		return this.modal;
	}

	public void setModal(LNNode node) {
		if (node != null && !node.isVisible()) {
			throw new RuntimeException(
					"Can't set invisible node as modal node!");
		}
		this.modal = node;
	}

	public LNNode get() {
		if (content != null) {
			return content.get();
		}
		return null;
	}

	public void commits() {
		if (isClose()) {
			return;
		}
		final int additionCount = pendingAdd.size();
		if (additionCount > 0) {
			for (int i = 0; i < additionCount; i++) {
				SpriteBatchObject object = pendingAdd.get(i);
				objects.add(object);
			}
			pendingAdd.clear();
		}
		final int removalCount = pendingRemove.size();
		if (removalCount > 0) {
			for (int i = 0; i < removalCount; i++) {
				SpriteBatchObject object = pendingRemove.get(i);
				objects.remove(object);
			}
			pendingRemove.clear();
		}
	}

	public SpriteBatchObject add(SpriteBatchObject object) {
		pendingAdd.add(object);
		return object;
	}

	public SpriteBatchObject remove(SpriteBatchObject object) {
		pendingRemove.add(object);
		if (usePhysics) {
			unbindPhysics(object);
		}
		return object;
	}

	public void removeTileObjects() {
		final int count = objects.size();
		final Object[] objectArray = objects.toArray();
		for (int i = 0; i < count; i++) {
			SpriteBatchObject o = (SpriteBatchObject) objectArray[i];
			pendingRemove.add(o);
			if (usePhysics) {
				unbindPhysics(o);
			}
		}
		pendingAdd.clear();
	}

	public SpriteBatchObject findObject(float x, float y) {
		for (SpriteBatchObject o : objects) {
			if ((o.getX() == x && o.getY() == y)
					|| o.getRectBox().contains(x, y)) {
				return o;
			}
		}
		return null;
	}

	public TileMap getIndexTile() {
		return indexTile;
	}

	public void setIndexTile(TileMap indexTile) {
		this.indexTile = indexTile;
	}

	public void follow(LObject o) {
		this.follow = o;
	}

	@Override
	public final void onLoad() {
		if (batch == null) {
			batch = new SpriteBatch(3000);
		}
		content.setScreen(this);
		for (LNNode node : content.childs) {
			if (node != null) {
				node.onSceneActive();
			}
		}
	}

	@Override
	public final void onLoaded() {
		create();
	}

	public abstract void create();

	public void addActionKey(Integer keyCode, ActionKey e) {
		keyActions.put(keyCode, e);
		keySize = keyActions.size();
	}

	public void removeActionKey(Integer keyCode) {
		keyActions.remove(keyCode);
		keySize = keyActions.size();
	}

	public void pressActionKey(Integer keyCode) {
		ActionKey key = (ActionKey) keyActions.getValue(keyCode);
		if (key != null) {
			key.press();
		}
	}

	public void releaseActionKey(Integer keyCode) {
		ActionKey key = (ActionKey) keyActions.getValue(keyCode);
		if (key != null) {
			key.release();
		}
	}

	public void clearActionKey() {
		keyActions.clear();
		keySize = 0;
	}

	public void releaseActionKeys() {
		keySize = keyActions.size();
		if (keySize > 0) {
			for (int i = 0; i < keySize; i++) {
				ActionKey act = (ActionKey) keyActions.get(i);
				act.release();
			}
		}
	}

	public void setOffset(TileMap tile, float sx, float sy) {
		offset.set(sx, sy);
		tile.setOffset(offset);
	}

	public final Vector2f getOffset() {
		return offset;
	}

	public void putTileMap(TileMap t) {
		tiles.add(t);
	}

	public void removeTileMap(TileMap t) {
		tiles.remove(t);
	}

	public void addTileObject(SpriteBatchObject o) {
		add(o);
	}

	public JumpObject addJumpObject(float x, float y, float w, float h,
			Animation a) {
		JumpObject o = null;
		if (indexTile != null) {
			o = new JumpObject(x, y, w, h, a, indexTile);
		} else if (tiles.size() > 0) {
			o = new JumpObject(x, y, w, h, a, tiles.get(0));
		} else {
			return null;
		}
		add(o);
		return o;
	}

	public MoveObject addMoveObject(float x, float y, float w, float h,
			Animation a) {
		MoveObject o = null;
		if (indexTile != null) {
			o = new MoveObject(x, y, w, h, a, indexTile);
		} else if (tiles.size() > 0) {
			o = new MoveObject(x, y, w, h, a, tiles.get(0));
		} else {
			return null;
		}
		add(o);
		return o;
	}

	public void removeTileObject(SpriteBatchObject o) {
		remove(o);
	}

	private HashMap<SpriteBatchObject, PBody> _Bodys = new HashMap<SpriteBatchObject, PBody>(
			CollectionUtils.INITIAL_CAPACITY);

	public PBody findPhysics(SpriteBatchObject o) {
		if (usePhysics) {
			PBody body = _Bodys.get(o);
			return body;
		} else {
			throw new RuntimeException("You do not set the physics engine !");
		}
	}

	public void unbindPhysics(SpriteBatchObject o) {
		if (usePhysics) {
			PBody body = _Bodys.remove(o);
			if (body != null) {
				body.setTag(null);
				_manager.world.removeBody(body);
			}
		}
	}

	public PBody addPhysics(boolean fix, SpriteBatchObject o, float density) {
		return bindPhysics(fix, add(o), density);
	}

	public PBody addPhysics(boolean fix, SpriteBatchObject o) {
		return bindPhysics(fix, add(o), 1F);
	}

	public PBody addTexturePhysics(boolean fix, SpriteBatchObject o,
			float density) {
		return bindTexturePhysics(fix, add(o), density);
	}

	public PBody addTexturePhysics(boolean fix, SpriteBatchObject o) {
		return bindTexturePhysics(fix, add(o), 1F);
	}

	public PBody bindPhysics(boolean fix, SpriteBatchObject o, float density) {
		if (usePhysics) {
			PBody body = _manager.addBox(fix, o.getRectBox(),
					MathUtils.toRadians(o.getRotation()), density);
			body.setTag(o);
			_Bodys.put(o, body);
			return body;
		} else {
			throw new RuntimeException("You do not set the physics engine !");
		}
	}

	public PBody addCirclePhysics(boolean fix, SpriteBatchObject o,
			float density) {
		return bindCirclePhysics(fix, add(o), density);
	}

	public PBody addCirclePhysics(boolean fix, SpriteBatchObject o) {
		return bindCirclePhysics(fix, add(o), 1F);
	}

	public PBody bindCirclePhysics(boolean fix, SpriteBatchObject o) {
		return bindCirclePhysics(fix, add(o), 1F);
	}

	public PBody bindCirclePhysics(boolean fix, SpriteBatchObject o,
			float density) {
		if (usePhysics) {
			RectBox rect = o.getRectBox();
			float r = (rect.width + rect.height) / 4;
			PBody body = _manager.addCircle(fix, o.x(), o.y(), r,
					MathUtils.toRadians(o.getRotation()), density);
			body.setTag(o);
			_Bodys.put(o, body);
			return body;
		} else {
			throw new RuntimeException("You do not set the physics engine !");
		}
	}

	public PBody bindTexturePhysics(boolean fix, SpriteBatchObject o,
			float density) {
		if (usePhysics) {
			PBody body = _manager.addShape(fix, o.getAnimation()
					.getSpriteImage(), MathUtils.toRadians(o.getRotation()),
					density);
			if (body.size() > 0) {
				body.inner_shapes()[0].setPosition(o.x() / _manager.scale,
						o.y() / _manager.scale);
			}
			body.setTag(o);
			_Bodys.put(o, body);
			return body;
		} else {
			throw new RuntimeException("You do not set the physics engine !");
		}
	}

	public PBody bindTexturePhysics(boolean fix, SpriteBatchObject o) {
		return bindTexturePhysics(fix, o, 1F);
	}

	public PBody bindPhysics(boolean fix, SpriteBatchObject o) {
		return bindPhysics(fix, o, 1F);
	}

	public PBody bindPhysics(PBody body, SpriteBatchObject o) {
		if (usePhysics) {
			body.setTag(o);
			_manager.addBody(body);
			_Bodys.put(o, body);
			return body;
		} else {
			throw new RuntimeException("You do not set the physics engine !");
		}
	}

	@Override
	public final void alter(LTimerContext timer) {
		for (int i = 0; i < keySize; i++) {
			ActionKey act = (ActionKey) keyActions.get(i);
			if (act.isPressed()) {
				act.act(elapsedTime);
				if (act.isReturn) {
					return;
				}
			}
		}
		if (content.isVisible()) {
			processEvents();
			content.updateNode(timer.getMilliseconds());
		}
		if (usePhysics) {
			if (_dt < 0) {
				_manager.step(timer.getMilliseconds());
			} else {
				_manager.step(_dt);
			}
		}
		if (follow != null) {
			if (usePhysics) {
				_manager.offset(follow.getX(), follow.getY());
			}
			for (TileMap tile : tiles) {
				float offsetX = getHalfWidth() - follow.getX();
				offsetX = MathUtils.min(offsetX, 0);
				offsetX = MathUtils.max(offsetX, getWidth() - tile.getWidth());

				float offsetY = getHalfHeight() - follow.getY();
				offsetY = MathUtils.min(offsetY, 0);
				offsetY = MathUtils
						.max(offsetY, getHeight() - tile.getHeight());

				setOffset(tile, offsetX, offsetY);
				tile.update(elapsedTime);
			}
		}
		for (SpriteBatchObject o : objects) {
			if (usePhysics) {
				PBody body = _Bodys.get(o);
				if (body != null) {
					PShape shape = body.inner_shapes()[0];
					final float rotation = (shape.getAngle() * MathUtils.RAD_TO_DEG) % 360;
					AABB aabb = shape.getAABB();

					o.setLocation(_manager.getScreenX(aabb.minX),
							_manager.getScreenY(aabb.minY));
					o.setRotation(rotation);
				}
			}
			o.update(elapsedTime);
			if (updateListener != null) {
				updateListener.act(o, elapsedTime);
			}
		}
		update(elapsedTime);
		commits();
	}

	@Override
	public final void draw(GLEx g) {
		if (isOnLoadComplete()) {
			batch.begin();
			before(batch);
			for (TileMap tile : tiles) {
				tile.draw(g, batch, offset.x(), offset.y());
			}
			for (SpriteBatchObject o : objects) {
				objX = o.getX() + offset.x;
				objY = o.getY() + offset.y;
				if (contains(objX, objY)) {
					o.draw(batch, offset.x, offset.y);
				}
			}
			if (content.isVisible()) {
				content.drawNode(batch);
			}
			after(batch);
			batch.end();
		}
	}

	public abstract void after(SpriteBatch batch);

	public abstract void before(SpriteBatch batch);

	@Override
	public final void onKeyDown(LKey e) {
		keySize = keyActions.size();
		if (keySize > 0) {
			int keyCode = e.getKeyCode();
			for (int i = 0; i < keySize; i++) {
				Integer code = (Integer) keyActions.getKey(i);
				if (code == keyCode) {
					ActionKey act = (ActionKey) keyActions.getValue(code);
					act.press();
				}
			}
		}
		press(e);
	}

	public abstract void press(LKey e);

	@Override
	public final void onKeyUp(LKey e) {
		keySize = keyActions.size();
		if (keySize > 0) {
			int keyCode = e.getKeyCode();
			for (int i = 0; i < keySize; i++) {
				Integer code = (Integer) keyActions.getKey(i);
				if (code == keyCode) {
					ActionKey act = (ActionKey) keyActions.getValue(code);
					act.release();
				}
			}
		}
		release(e);
	}

	public abstract void release(LKey e);

	public abstract void update(long elapsedTime);

	public abstract void close();

	@Override
	public void setAutoDestory(final boolean a) {
		super.setAutoDestory(a);
		if (content != null) {
			content.setAutoDestroy(a);
		}
	}

	@Override
	public boolean isAutoDestory() {
		if (content != null) {
			return content.isAutoDestory();
		}
		return super.isAutoDestory();
	}

	@Override
	public final void dispose() {
		if (usePhysics) {
			_manager.setStart(false);
			_manager.setEnableGravity(false);
			_Bodys.clear();
		}
		this.keySize = 0;
		if (batch != null) {
			batch.dispose();
			batch = null;
		}
		if (content != null) {
			content.dispose();
			content = null;
		}
		if (indexTile != null) {
			indexTile.dispose();
			indexTile = null;
		}
		if (objects != null) {
			objects.clear();
			objects = null;
		}
		if (pendingAdd != null) {
			pendingAdd.clear();
			pendingAdd = null;
		}
		if (pendingRemove != null) {
			pendingRemove.clear();
			pendingRemove = null;
		}
		tiles.clear();
		close();
	}

}
