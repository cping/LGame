/**
 * Copyright 2008 - 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use plane file except in compliance with the License. You may obtain a copy
 * of the License at
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
package loon.stg;

import loon.canvas.LColor;
import loon.events.SysInput;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.stg.effect.Picture;
import loon.utils.timer.LTimer;

public abstract class STGObject {

	protected Object Tag;
	
	protected int countUpdate;

	protected int count = 0;

	protected STGScreen stg;

	protected SysInput input;

	protected STGObjects objects;

	protected int plnNo;

	protected int attribute;

	protected int hitPoint = 1;

	protected int scorePoint = 0;

	protected boolean hitFlag = false;

	protected int targetPlnNo;

	protected int speed;

	protected int hitX;

	protected int hitY;

	private int hitW;

	private int hitH;

	protected int scroll;

	public float offsetX, offsetY;

	public STGObject(STGScreen stg, int no, float x, float y, int tpno) {
		this.plnNo = no;
		this.stg = stg;
		this.input = stg;
		this.objects = stg.stgObjects;
		this.targetPlnNo = tpno;
		this.scroll = 3;
		STGPlane plane = stg.planes.get(no);
		if (plane != null && plane.rect != null) {
			this.hitW = (int) (plane.rect.width * plane.scaleX);
			this.hitH = (int) (plane.rect.height * plane.scaleY);
		}
	}

	public abstract void update();

	public SysInput screenInput() {
		return input;
	}

	public float getX(int id) {
		return stg.getPlanePosX(id);
	}

	public float getY(int id) {
		return stg.getPlanePosY(id);
	}

	public void setX(int id, float x) {
		stg.setPosX(id, x);
	}

	public void setY(int id, float y) {
		stg.setPosY(id, y);
	}

	public void setScale(int id, float scale) {
		stg.setPlaneScale(id, scale);
	}

	public void setScale(int id, float sx, float sy) {
		stg.setPlaneScale(id, sx, sy);
	}

	public int getScreenWidth() {
		return stg.getWidth();
	}

	public int getScreenHeight() {
		return stg.getHeight();
	}

	public void setScale(float scale) {
		stg.setPlaneScale(this.plnNo, scale);
	}

	public void setScale(float sx, float sy) {
		stg.setPlaneScale(this.plnNo, sx, sy);
	}

	public void setPlaneGraphics(int imgId, float x, float y) {
		setPlane(imgId, x, y, true);
	}

	public void setPlaneSize(int index, int w, int h) {
		stg.setPlaneSize(index, w, h);
	}

	public void setPlaneSize(int w, int h) {
		stg.setPlaneSize(this.plnNo, w, h);
	}

	public void setPlane(int imgId, float x, float y, boolean v) {
		stg.setPlaneBitmap(this.plnNo, 0, imgId);
		stg.setPlaneView(this.plnNo, v);
		stg.setPlanePos(this.plnNo, x, y);
	}

	boolean setPlaneScale(float sx, float sy) {
		return stg.setPlaneScale(this.plnNo, sx, sy);
	}

	public boolean setPlaneBitmap(int animeNo, int imgId) {
		return stg.setPlaneBitmap(this.plnNo, animeNo, imgId);
	}

	public boolean setPlaneAnimeDelay(long delay) {
		return stg.setPlaneAnimeDelay(this.plnNo, delay);
	}

	public boolean setPlaneAnime(boolean anime) {
		return stg.setPlaneAnime(this.plnNo, anime);
	}

	public boolean setPlaneString(String mes) {
		return stg.setPlaneString(this.plnNo, mes);
	}

	public boolean setPlaneCenterString(String mes) {
		return stg.setPlaneCenterString(this.plnNo, mes);
	}

	public boolean setPlaneFont(String font, int style, int size) {
		return stg.setPlaneFont(this.plnNo, font, style, size);
	}

	public boolean setPlaneColor(int r, int g, int b) {
		return stg.setPlaneColor(this.plnNo, r, g, b);
	}

	public boolean setPlaneView(boolean v) {
		return stg.setPlaneView(this.plnNo, v);
	}

	public boolean setPlaneDraw(Picture draw) {
		return stg.setPlaneDraw(this.plnNo, draw);
	}

	public boolean setPlaneAngle(float rotation) {
		return stg.setPlaneAngle(this.plnNo, rotation);
	}

	public boolean setPlaneBitmapColor(LColor c) {
		return stg.setPlaneBitmapColor(this.plnNo, c);
	}

	public void addClass(String className, float x, float y, int tpno) {
		stg.addClass(stg.getScreenPackName() + "." + className, x, y, tpno);
	}

	public STGObject newPlane(String className, float x, float y, int tpno) {
		return stg.newPlane(stg.getScreenPackName() + "." + className, x, y,
				tpno);
	}

	public void addPlane(STGObject o) {
		stg.addPlane(o);
	}

	public void addBombHero(String className) {
		stg.addBombHero(stg.getScreenPackName() + "." + className);
	}

	public void addBombHero(String className, int x, int y) {
		stg.addBombHero(stg.getScreenPackName() + "." + className, x, y);
	}

	public void setLocation(float x, float y) {
		this.setX(x);
		this.setY(y);
	}

	public boolean deletePlane() {
		return stg.deletePlane(this.plnNo);
	}

	public synchronized void delete() {
		if (objects != null) {
			objects.delObj(this.plnNo);
		}
	}

	public void scrollMove() {
		move(0, scroll);
	}

	public void move(float x, float y) {
		stg.setPlaneMov(this.plnNo, x, y);
	}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}

	public boolean isHitFlag() {
		return hitFlag;
	}

	public void setHitFlag(boolean hitFlag) {
		this.hitFlag = hitFlag;
	}

	public int getHitPoint() {
		return hitPoint;
	}

	public void setHitPoint(int hitPoint) {
		this.hitPoint = hitPoint;
	}

	public void setHitX(int hitX) {
		this.hitX = hitX;
	}

	public void setHitY(int hitY) {
		this.hitY = hitY;
	}

	public void setHitH(int hitH) {
		this.hitH = hitH;
	}

	public void setHitW(int hitW) {
		this.hitW = hitW;
	}

	public int getHitH() {
		if (hitH != 0) {
			return hitH;
		}
		RectBox rect = rect();
		if (rect == null) {
			return hitH;
		} else {
			return rect.height;
		}
	}

	public int getHitW() {
		if (hitW != 0) {
			return hitW;
		}
		RectBox rect = rect();
		if (rect == null) {
			return hitW;
		} else {
			return rect.width;
		}
	}

	public int getHitX() {
		return hitX;
	}

	public int getHitY() {
		return hitY;
	}

	public int getPlnNo() {
		return plnNo;
	}

	public void setPlnNo(int plnNo) {
		this.plnNo = plnNo;
	}

	public int getScorePoint() {
		return scorePoint;
	}

	public void setScorePoint(int scorePoint) {
		this.scorePoint = scorePoint;
	}

	public int getTargetPlnNo() {
		return targetPlnNo;
	}

	public void setTargetPlnNo(int targetPlnNo) {
		this.targetPlnNo = targetPlnNo;
	}

	public boolean isAnimation() {
		return stg.planes.get(plnNo).animation;
	}

	public void setAnimation(boolean animation) {
		stg.planes.get(plnNo).animation = animation;
	}

	public void setScaleX(float sx) {
		stg.planes.get(plnNo).scaleX = sx;
	}

	public void setScaleY(float sy) {
		stg.planes.get(plnNo).scaleY = sy;
	}

	public float getScaleX() {
		return stg.planes.get(plnNo).scaleX;
	}

	public float getScaleY() {
		return stg.planes.get(plnNo).scaleY;
	}

	public int getAnimeNo() {
		return stg.planes.get(plnNo).animeNo;
	}

	public void setAnimeNo(int animeNo) {
		stg.planes.get(plnNo).animeNo = animeNo;
	}

	public LColor getColor() {
		return stg.planes.get(plnNo).color;
	}

	public void setColor(LColor color) {
		stg.planes.get(plnNo).color = color;
	}

	public LTimer getDelay() {
		return stg.planes.get(plnNo).delay;
	}

	public Picture getDraw() {
		return stg.planes.get(plnNo).draw;
	}

	public LFont getFont() {
		return stg.planes.get(plnNo).font;
	}

	public int getPlaneMode() {
		return stg.planes.get(plnNo).planeMode;
	}

	public void setX(float x) {
		stg.planes.get(plnNo).posX = x;
	}

	public void setY(float y) {
		stg.planes.get(plnNo).posY = y;
	}

	public float getX() {
		return stg.planes.get(plnNo).posX;
	}

	public float getY() {
		return stg.planes.get(plnNo).posY;
	}

	public int getWidth() {
		return stg.planes.get(plnNo).rect.width;
	}

	public boolean contains(float x, float y) {
		return contains(x, y, 0, 0);
	}

	public boolean contains(STGObject o) {
		if (o == null) {
			return false;
		}
		return contains(o.getX() + hitX, o.getY() + hitY, o.getHitW(),
				o.getHitH());
	}

	public boolean contains(float x, float y, float width, float height) {
		return (x >= this.getX() && y >= this.getY()
				&& ((x + width) <= (this.getX() + this.getHitW())) && ((y + height) <= (this
				.getY() + this.getHitH())));
	}

	private RectBox rect() {
		STGPlane plane = stg.planes.get(plnNo);
		if (plane == null) {
			return null;
		}
		RectBox rect = plane.rect;
		if (rect == null && hitW != 0 && hitH != 0) {
			plane.rect = new RectBox(0, 0, hitW, hitH);
		} else if (rect == null) {
			return null;
		}
		rect.setX(plane.posX);
		rect.setY(plane.posY);
		if (hitW == 0) {
			hitW = (int) (rect.width * plane.scaleX);
		} else {
			rect.width = hitW;
		}
		if (hitH == 0) {
			hitH = (int) (rect.height * plane.scaleY);
		} else {
			rect.height = hitH;
		}
		return rect;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getHeight() {
		return stg.planes.get(plnNo).rect.height;
	}

	public boolean isView() {
		return stg.planes.get(plnNo).view;
	}

	public void setView(boolean view) {
		stg.planes.get(plnNo).view = view;
	}

	public int getDieSleep() {
		return countUpdate;
	}

	public void setDieSleep(int d) {
		this.countUpdate = d;
	}

	public int getScroll() {
		return scroll;
	}

	public void setScroll(int scroll) {
		this.scroll = scroll;
	}

}