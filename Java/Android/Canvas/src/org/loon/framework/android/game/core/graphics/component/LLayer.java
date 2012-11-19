package org.loon.framework.android.game.core.graphics.component;

import java.util.HashMap;
import java.util.Iterator;

import org.loon.framework.android.game.action.map.Field2D;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LComponent;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.graphics.filter.ImageFilterFactory;
import org.loon.framework.android.game.core.graphics.filter.ImageFilterType;
import org.loon.framework.android.game.core.timer.LTimer;

import android.graphics.Bitmap;
import android.graphics.Matrix;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class LLayer extends ActorLayer {

	private static final Matrix matrix = new Matrix();

	protected boolean visible, actorDrag, pressed;

	private Actor dragActor;

	private LTimer timer = new LTimer(0);

	private boolean isTouchClick;

	private ImageFilterFactory factory;

	public LLayer(int w, int h) {
		this(0, 0, w, h);
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
		this.factory = ImageFilterFactory.getInstance();
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(100);
	}

	public void downClick(int x, int y) {
	}

	public void upClick(int x, int y) {
	}

	public void downKey() {
	}

	public void upKey() {
	}

	public void drag(int x, int y) {
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
	public void action(long elapsedTime) {

	}

	public void update(long elapsedTime) {
		if (visible) {
			super.update(elapsedTime);
			if (timer.action(this.elapsedTime = elapsedTime)) {
				action(elapsedTime);
			}
		}
	}

	public void createCustomUI(LGraphics g, int x, int y, int w, int h) {
		if (!visible) {
			return;
		}
		paintObjects(g, x, y, x + w, y + h);
		if (x == 0 && y == 0) {
			paint(g);
		} else {
			g.translate(x, y);
			paint(g);
			g.translate(-x, -y);
		}
	}

	public void paint(LGraphics g) {

	}

	public void paintObjects(LGraphics g, int minX, int minY, int maxX, int maxY) {
		if (objects == null) {
			return;
		}
		synchronized (objects) {
			boolean isListener = false;
			int paintSeq = 0;
			Iterator<?> iter = objects.iterator();
			Actor thing = null;
			while (iter.hasNext()) {
				thing = (Actor) iter.next();
				if (!thing.isVisible()) {
					continue;
				}
				isListener = (thing.actorListener != null);
				if (isListener) {
					thing.actorListener.update(elapsedTime);
				}
				thing.update(elapsedTime);
				RectBox rect = thing.getRectBox();
				int actorX = minX + thing.x;
				int actorY = minY + thing.y;
				int actorWidth = rect.getWidth();
				int actorHeight = rect.getHeight();
				if (actorX + actorWidth < minX || actorX > maxX
						|| actorY + actorHeight < minY || actorY > maxY) {
					continue;
				}
				LImage actorImage = thing.getImage();
				if (actorImage != null) {
					boolean isBitmapFilter = (ImageFilterType.NoneFilter != thing.filterType);
					thing.setLastPaintSeqNum(paintSeq++);
					Bitmap bitmap = actorImage.getBitmap();
					if (isBitmapFilter) {
						bitmap = factory.doFilter(bitmap, thing.filterType);
					}
					int rotation = thing.getRotation();
					if (thing.alpha < 1.0) {
						g.setAlpha(thing.alpha);
					}
					if (rotation != 0) {
						if (thing.supportRotateSheet && thing.sheet != null
								&& thing.scaleX == 1 && thing.scaleY == 1) {
							thing.sheet.draw2(g, actorX, actorY, rotation);
						} else {
							float halfWidth = actorImage.getWidth() / 2;
							float halfHeight = actorImage.getHeight() / 2;
							float newWidth = actorX + halfWidth;
							float newHeight = actorY + halfHeight;
							matrix.reset();
							matrix.preTranslate(newWidth, newHeight);
							matrix.preRotate(rotation);
							matrix.preTranslate(-newWidth, -newHeight);
							matrix.preTranslate(actorX, actorY);
							matrix.preScale(thing.scaleX, thing.scaleY);
							g.drawBitmap(bitmap, matrix, false);
						}
					} else {
						int width = (int) (actorImage.getWidth() * thing.scaleX);
						int height = (int) (actorImage.getHeight() * thing.scaleY);
						g.drawBitmap(bitmap, actorX, actorY, width, height);
					}
					if (thing.alpha < 1.0) {
						g.setAlpha(1.0F);
					}
					if (isBitmapFilter) {
						bitmap.recycle();
						bitmap = null;
					}
				}
				if (actorX == 0 && actorY == 0) {
					thing.draw(g);
					if (isListener) {
						thing.actorListener.draw(g);
					}
				} else {
					g.translate(actorX, actorY);
					thing.draw(g);
					if (isListener) {
						thing.actorListener.draw(g);
					}
					g.translate(-actorX, -actorY);
				}
			}
		}
	}

	public void moveCamera(Actor actor) {
		moveCamera(actor.getX(), actor.getY());
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

	public void setField2DBackground(Field2D field, HashMap<?, ?> pathMap) {
		setField2DBackground(field, pathMap, null);
	}

	public void setField2DBackground(Field2D field, HashMap<?, ?> pathMap,
			String fileName) {
		setField2D(field);
		LImage background = null;
		if (fileName != null) {
			setTileBackground(fileName);
			background = getBackground();
		} else {
			background = LImage.createImage(getWidth(), getHeight(), false);
		}
		LGraphics g = background.getLGraphics();
		for (int i = 0; i < field.getWidth(); i++) {
			for (int j = 0; j < field.getHeight(); j++) {
				int index = field.getType(j, i);
				Object o = pathMap.get(index);
				if (o != null) {
					if (o instanceof LImage) {
						g.drawImage(((LImage) o), field.tilesToWidthPixels(i),
								field.tilesToHeightPixels(j));
					} else if (o instanceof Actor) {
						addObject(((Actor) o), field.tilesToWidthPixels(i),
								field.tilesToHeightPixels(j));
					}
				}
			}
		}
		g.dispose();
		setBackground(background);
	}

	public void setTileBackground(String fileName) {
		setTileBackground(LImage.createImage(fileName));
	}

	public void setTileBackground(LImage image) {
		if (image == null) {
			return;
		}
		int layerWidth = getWidth();
		int layerHeight = getHeight();
		int tileWidth = image.getWidth();
		int tileHeight = image.getHeight();

		LImage background = LImage.createImage(layerWidth, layerHeight, false);
		LGraphics g = background.getLGraphics();
		for (int x = 0; x < layerWidth; x += tileWidth) {
			for (int y = 0; y < layerHeight; y += tileHeight) {
				g.drawImage(image, x, y);
			}
		}
		g.dispose();

		setBackground(background);
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
		int increment = Math.abs((int) Math.IEEEremainder(scrollPos, cellSize));
		if (increment == 0) {
			increment = cellSize;
		}
		return increment;
	}

	public Actor getClickActor() {
		return dragActor;
	}

	protected void processTouchPressed() {
		if (!isTouchClick) {
			return;
		}
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

	protected void processTouchReleased() {
		if (!isTouchClick) {
			return;
		}
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

	protected void processTouchEntered() {
		this.pressed = true;
	}

	protected void processTouchExited() {
		this.pressed = false;
	}

	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.downKey();
		}
	}

	protected void processKeyReleased() {
		if (this.isSelected()) {
			this.upKey();
		}
	}

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

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
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

	protected void validateSize() {
		super.validateSize();
	}

	public String getUIName() {
		return "Layer";
	}

	public void createUI(LGraphics g, int x, int y, LComponent component,
			LImage[] buttonImage) {

	}

}
