/**
 * Copyright 2008 - 2010
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
 * @version 0.1
 */
package loon.component;

import loon.LTexture;
import loon.action.map.Field2D;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.ArrayMap;
import loon.utils.LIterator;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

public class LLayer extends ActorLayer {

	private float width;

	private float height;

	private float colorAlpha;

	private float actorX;

	private float actorY;

	private float actorWidth;

	private float actorHeight;

	protected boolean actorDrag, pressed;

	private Actor dragActor;

	private LTimer timer = new LTimer(0);

	private boolean isTouchClick;

	private Actor thing = null;

	private boolean isListener = false;

	private boolean isVSync;

	private int paintSeq = 0;

	public LLayer(int w, int h) {
		this(0, 0, w, h);
	}

	public LLayer(int w, int h, int size) {
		this(0, 0, w, h, true, size);
	}

	public LLayer(int w, int h, boolean bounded) {
		this(0, 0, w, h, bounded);
	}

	public LLayer(int x, int y, int w, int h) {
		this(x, y, w, h, true);
	}

	public LLayer(int x, int y, int w, int h, boolean bounded) {
		this(x, y, w, h, bounded, 1);
	}

	public LLayer(int x, int y, int w, int h, boolean bounded, int size) {
		super(x, y, w, h, size, bounded);
		this.setLocation(x, y);
		this.actorDrag = true;
		this.visible = true;
		this.customRendering = true;
		this.isTouchClick = true;
		this.isLimitMove = true;
		this.isVSync = true;
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(100);
	}

	public void setVSync(boolean vsync) {
		this.isVSync = vsync;
	}

	public boolean isVSync() {
		return isVSync;
	}

	public void downClick(int x, int y) {
		if (Click != null) {
			Click.DownClick(this, x, y);
		}
	}

	public void upClick(int x, int y) {
		if (Click != null) {
			Click.UpClick(this, x, y);
		}
	}

	public void drag(int x, int y) {
		if (Click != null) {
			Click.DragClick(this, x, y);
		}
	}

	public void downKey() {
	}

	public void upKey() {
	}

	/**
	 * 设定动作触发延迟时间
	 * 
	 * @param delay
	 */
	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	/**
	 * 返回动作触发延迟时间
	 * 
	 * @return
	 */
	public long getDelay() {
		return timer.getDelay();
	}

	/**
	 * 动作处理
	 * 
	 * @param elapsedTime
	 */
	@Override
	public void action(long elapsedTime) {

	}

	@Override
	public void update(long elapsedTime) {
		if (visible) {
			super.update(elapsedTime);
			if (timer.action(this.elapsedTime = elapsedTime)) {
				action(elapsedTime);
				if (!isVSync) {
					LIterator<Actor> it = objects.iterator();
					for (; it.hasNext();) {
						thing = (Actor) it.next();
						if (!thing.visible) {
							continue;
						}
						thing.update(elapsedTime);
					}
				}
			}
		}
	}

	@Override
	public void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (!visible) {
			return;
		}
		paintObjects(g, x, y, x + w, y + h);
		if (x == 0 && y == 0) {
			paint(g);
		} else {
			try {
				g.translate(x, y);
				paint(g);
			} finally {
				g.translate(-x, -y);
			}
		}
	}

	public void paint(GLEx g) {

	}

	public void paintObjects(GLEx g, int minX, int minY, int maxX, int maxY) {
		synchronized (objects) {
			LIterator<Actor> it = objects.iterator();
			for (; it.hasNext();) {
				thing = it.next();
				if (!thing.visible) {
					continue;
				}
				isListener = (thing.actorListener != null);

				if (isVSync) {
					if (isListener) {
						thing.actorListener.update(elapsedTime);
					}
					thing.update(elapsedTime);
				}

				RectBox rect = thing.getRectBox();
				actorX = minX + thing.getX();
				actorY = minY + thing.getY();
				actorWidth = rect.width;
				actorHeight = rect.height;
				if (actorX + actorWidth < minX || actorX > maxX
						|| actorY + actorHeight < minY || actorY > maxY) {
					continue;
				}
				LTexture actorImage = thing.getImage();
				if (actorImage != null) {
					width = actorImage.getWidth();
					height = actorImage.getHeight();
					thing.setLastPaintSeqNum(paintSeq++);
					colorAlpha = thing.getAlpha();
					if (colorAlpha != 1f) {
						g.setAlpha(colorAlpha);
					}
					g.draw(actorImage,
							actorX,
							actorY,
							width,
							height,
							baseColor == null ? thing.filterColor : baseColor
									.mul(thing.filterColor), thing
									.getRotation(), thing.scaleX, thing.scaleY,
							thing.flipX, thing.flipY);
					if (colorAlpha != 1f) {
						g.setAlpha(1f);
					}
				}
				if (thing.isConsumerDrawing) {
					if (actorX == 0 && actorY == 0) {
						thing.draw(g);
						if (isListener) {
							thing.actorListener.draw(g);
						}
					} else {
						try {
							g.saveTx();
							g.translate(actorX, actorY);
							thing.draw(g);
							if (isListener) {
								thing.actorListener.draw(g);
							}
						} finally {
							g.translate(-actorX, -actorY);
							g.restoreTx();
						}
					}
				}
			}

		}
	}

	public void moveCamera(Actor actor) {
		moveCamera(actor.x(), actor.y());
	}

	public void centerOn(final Actor object) {
		object.setLocation(getWidth() / 2 - object.getWidth() / 2, getHeight()
				/ 2 - object.getHeight() / 2);
	}

	public void topOn(final Actor object) {
		object.setLocation(getWidth() / 2 - object.getWidth() / 2, 0);
	}

	public void leftOn(final Actor object) {
		object.setLocation(0, getHeight() / 2 - object.getHeight() / 2);
	}

	public void rightOn(final Actor object) {
		object.setLocation(getWidth() - object.getWidth(), getHeight() / 2
				- object.getHeight() / 2);
	}

	public void bottomOn(final Actor object) {
		object.setLocation(getWidth() / 2 - object.getWidth() / 2, getHeight()
				- object.getHeight());
	}

	public void setField2DBackground(Field2D field, ArrayMap pathMap) {
		setField2DBackground(field, pathMap, null);
	}

	public void setField2DBackground(Field2D field, ArrayMap pathMap,
			String fileName) {
		setField2D(field);
		Image background = null;
		if (fileName != null) {
			Image tmp = Image.createImage(fileName);
			background = setTileBackground(tmp, true);
			if (tmp != null) {
				tmp.close();
				tmp = null;
			}
		} else {
			background = Image.createImage((int) getWidth(), (int) getHeight());
		}
		Canvas g = background.getCanvas();
		for (int i = 0; i < field.getWidth(); i++) {
			for (int j = 0; j < field.getHeight(); j++) {
				int index = field.getType(j, i);
				Object o = pathMap.get(index);
				if (o != null) {
					if (o instanceof Image) {
						g.draw(((Image) o), field.tilesToWidthPixels(i),
								field.tilesToHeightPixels(j));
					} else if (o instanceof Actor) {
						addObject(((Actor) o), field.tilesToWidthPixels(i),
								field.tilesToHeightPixels(j));
					}
				}
			}
		}
		g.close();
		setBackground(background.texture());
		if (background != null) {
			background.close();
			background = null;
		}
	}

	public void setTileBackground(String fileName) {
		setTileBackground(Image.createImage(fileName));
	}

	public void setTileBackground(Image image) {
		setTileBackground(image, false);
	}

	public Image setTileBackground(Image image, boolean isReturn) {
		if (image == null) {
			return null;
		}
		int layerWidth = (int) getWidth();
		int layerHeight = (int) getHeight();
		int tileWidth = image.getWidth();
		int tileHeight = image.getHeight();

		Image tempImage = Image.createImage(layerWidth, layerHeight);
		Canvas g = tempImage.getCanvas();
		for (int x = 0; x < layerWidth; x += tileWidth) {
			for (int y = 0; y < layerHeight; y += tileHeight) {
				g.draw(image, x, y);
			}
		}
		g.close();
		if (isReturn) {
			return tempImage;
		}
		setBackground(tempImage.texture());
		if (tempImage != null) {
			tempImage.close();
			tempImage = null;
		}
		return null;
	}

	public int getScroll(RectBox visibleRect, int orientation, int direction) {
		int cellSize = this.getCellSize();
		double scrollPos = 0.0D;
		if (orientation == 0) {
			if (direction < 0) {
				scrollPos = visibleRect.getMinX();
			} else if (direction > 0) {
				scrollPos = visibleRect.getMaxX();
			}
		} else if (direction < 0) {
			scrollPos = visibleRect.getMinY();
		} else if (direction > 0) {
			scrollPos = visibleRect.getMaxY();
		}
		int increment = MathUtils.abs((int) MathUtils.IEEEremainder(scrollPos,
				cellSize));
		if (increment == 0) {
			increment = cellSize;
		}
		return increment;
	}

	public Actor getClickActor() {
		return dragActor;
	}

	@Override
	protected void processTouchEntered() {
		this.pressed = true;
	}

	@Override
	protected void processTouchExited() {
		this.pressed = false;
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.downKey();
		}
	}

	@Override
	protected void processKeyReleased() {
		if (this.isSelected()) {
			this.upKey();
		}
	}

	@Override
	protected void processTouchPressed() {
		if (!isTouchClick) {
			return;
		}
		super.processTouchPressed();
		if (!input.isMoving()) {
			int dx = this.input.getTouchX() - this.getScreenX();
			int dy = this.input.getTouchY() - this.getScreenY();
			dragActor = getSynchronizedObject(dx, dy);
			if (dragActor != null) {
				if (dragActor.isClick()) {
					dragActor.downClick(dx, dy);
					if (dragActor.actorListener != null) {
						dragActor.actorListener.downClick(dx, dy);
					}
				}
			}
			this.downClick(dx, dy);
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!isTouchClick) {
			return;
		}
		super.processTouchReleased();
		if (!input.isMoving()) {
			int dx = this.input.getTouchX() - this.getScreenX();
			int dy = this.input.getTouchY() - this.getScreenY();
			dragActor = getSynchronizedObject(dx, dy);
			if (dragActor != null) {
				if (dragActor.isClick()) {
					dragActor.upClick(dx, dy);
					if (dragActor.actorListener != null) {
						dragActor.actorListener.upClick(dx, dy);
					}
				}
			}
			this.upClick(dx, dy);
			this.dragActor = null;
		}
	}

	@Override
	protected void processTouchDragged() {
		int dropX = 0;
		int dropY = 0;
		if (!locked) {
			boolean moveActor = false;
			if (actorDrag) {
				synchronized (objects) {
					dropX = this.input.getTouchX() - this.getScreenX();
					dropY = this.input.getTouchY() - this.getScreenY();
					if (dragActor == null) {
						dragActor = getSynchronizedObject(dropX, dropY);
					}
					if (dragActor != null && dragActor.isDrag()) {
						synchronized (dragActor) {
							objects.sendToFront(dragActor);
							RectBox rect = dragActor.getBoundingRect();
							int dx = dropX - (rect.width / 2);
							int dy = dropY - (rect.height / 2);
							if (dragActor.getLLayer() != null) {
								dragActor.setLocation(dx, dy);
								dragActor.drag(dropX, dropY);
								if (dragActor.actorListener != null) {
									dragActor.actorListener.drag(dropX, dropY);
								}
							}
							moveActor = true;
						}
					}
				}
			}
			if (!moveActor) {
				synchronized (input) {
					dropX = this.input.getTouchDX();
					dropY = this.input.getTouchDY();
					if (isNotMoveInScreen(dropX + this.x(), dropY + this.y())) {
						return;
					}
					if (getContainer() != null) {
						getContainer().sendToFront(this);
					}
					this.move(dropX, dropY);
					this.drag(dropX, dropY);
				}
			}
		} else {
			if (!actorDrag) {
				return;
			}
			synchronized (objects) {
				dropX = this.input.getTouchX() - this.getScreenX();
				dropY = this.input.getTouchY() - this.getScreenY();
				if (dragActor == null) {
					dragActor = getSynchronizedObject(dropX, dropY);
				}
				if (dragActor != null && dragActor.isDrag()) {
					synchronized (dragActor) {
						objects.sendToFront(dragActor);
						RectBox rect = dragActor.getBoundingRect();
						int dx = dropX - (rect.width / 2);
						int dy = dropY - (rect.height / 2);
						if (dragActor.getLLayer() != null) {
							dragActor.setLocation(dx, dy);
							dragActor.drag(dropX, dropY);
							if (dragActor.actorListener != null) {
								dragActor.actorListener.drag(dropX, dropY);
							}
						}
					}
				}
			}
		}
		super.dragClick();
	}

	public boolean isTouchPressed() {
		return this.pressed;
	}

	public boolean isActorDrag() {
		return actorDrag;
	}

	public void setActorDrag(boolean actorDrag) {
		this.actorDrag = actorDrag;
	}

	public boolean isLimitMove() {
		return isLimitMove;
	}

	public void setLimitMove(boolean isLimitMove) {
		this.isLimitMove = isLimitMove;
	}

	public boolean isTouchClick() {
		return isTouchClick;
	}

	public void setTouchClick(boolean isTouchClick) {
		this.isTouchClick = isTouchClick;
	}

	public int getLayerTouchX() {
		return this.input.getTouchX() - this.getScreenX();
	}

	public int getLayerTouchY() {
		return this.input.getTouchY() - this.getScreenY();
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	@Override
	public void close() {
		super.close();
		if (collisionChecker != null) {
			collisionChecker.dispose();
			collisionChecker = null;
		}
		if (objects != null) {
			Object[] o = objects.toActors();
			for (int i = 0; i < o.length; i++) {
				Actor actor = (Actor) o[i];
				if (actor != null) {
					actor.close();
					actor = null;
				}
			}
		}
	}

	@Override
	public String getUIName() {
		return "Layer";
	}
}
