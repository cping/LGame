package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;

public class ScrollablePane {
	private RectBox buttonNext;
	private RectBox buttonPrev;
	private Vector2f firstItemPosition;
	private Vector2f interItemDistance;
	public int iSelectedItem;
	private boolean isNextPressed;
	private boolean isPrevPressed;
	private boolean isScrolling;
	public java.util.ArrayList<DrawableObject> itemList;
	public int iUpdate;
	private Vector2f panePosition;
	private LTexture paneTexture;
	public int transLength;

	public ScrollablePane(LTexture paneTexture, Vector2f position) {
		this.paneTexture = paneTexture;
		this.panePosition = position.cpy();
		this.interItemDistance = new Vector2f(0f, 60f);
		this.itemList = new java.util.ArrayList<DrawableObject>();
		this.buttonPrev = new RectBox((int) position.x, (int) position.y,
				paneTexture.getWidth(), 100);
		this.buttonNext = new RectBox((int) position.x,
				(((int) position.y) + paneTexture.getHeight()) - 100,
				paneTexture.getWidth(), 100);
		this.isScrolling = false;
		this.isPrevPressed = false;
		this.isNextPressed = false;
		this.iSelectedItem = 0;
		this.iUpdate = 0;
		this.transLength = 10;
	}

	public final void AddItem(DrawableObject item) {
		this.itemList.add(item);
		if (this.itemList.size() == 1) {
			this.firstItemPosition = new Vector2f(
					(float) (this.paneTexture.getWidth() / 2),
					(float) (this.paneTexture.getHeight() / 2))
					.add(this.panePosition);
		}
		item.position = this.firstItemPosition.add(this.interItemDistance
				.mul(this.itemList.size() - 1));
	}

	public final void Draw(SpriteBatch batch) {
		int num = 0;
		float num2 = 0.4f;
		for (DrawableObject obj2 : this.itemList) {
			if (num == this.iSelectedItem) {
				num2 = 1f;
			} else {
				num2 = 0.4f;
			}
			num++;
			if ((obj2.position.y > (this.panePosition.y + 20f))
					&& (obj2.position.y < ((this.panePosition.y + this.paneTexture
							.getHeight()) - 20f))) {
				Vector2f position = obj2.position.sub(obj2.origin
						.mul(obj2.scale.x));
				position.x = this.panePosition.x + 10f;
				RectBox sourceRectangle = null;
				if (ScreenLevelup.t2DPlateTexture != null) {
					batch.draw(ScreenLevelup.t2DPlateTexture, position,
							sourceRectangle,
							Global.Pool.getColor(1f, 1f, 1f, num2));
				}
				obj2.alpha = num2;
				obj2.Update();
				obj2.Draw(batch);
			}
		}
		batch.draw(this.paneTexture, this.panePosition, null, new LColor(1f,
				1f, 1f, 1f), 0f, 0f, 0f, 1f, SpriteEffects.None);
	}

	public final void Scroll() {
		this.iUpdate++;
		if (this.isNextPressed) {
			this.itemList.get(0).position = this.firstItemPosition
					.add(((this.interItemDistance.div(2f)).mul((float) (1.0 + Math
							.cos((((float) this.iUpdate) / ((float) this.transLength)) * 3.1415926535897931)))));
		} else if (this.isPrevPressed) {
			this.itemList.get(0).position = this.firstItemPosition
					.sub(((this.interItemDistance.div(2f)).mul((float) (1.0 + Math
							.cos((((float) this.iUpdate) / ((float) this.transLength)) * 3.1415926535897931)))));
		}
		for (int i = 1; i < this.itemList.size(); i++) {
			this.itemList.get(i).position = this.itemList.get(0).position
					.add(this.interItemDistance.mul(i));
		}
		if (this.iUpdate >= this.transLength) {
			this.isScrolling = false;
			this.iUpdate = 0;
			this.isNextPressed = false;
			this.isPrevPressed = false;
		}
	}

	public final void TouchInputDetection(
			java.util.ArrayList<Vector2f> mousePositionList) {
		if (Screen.isTouchInputValid) {
			for (Vector2f vector : mousePositionList) {
				if (this.buttonNext.contains(vector.x, vector.y)) {
					if (this.iSelectedItem < (this.itemList.size() - 1)) {
						this.isNextPressed = true;
						this.iSelectedItem++;
						this.isScrolling = true;
						this.firstItemPosition.subLocal(this.interItemDistance);
					}
				} else if (this.buttonPrev.contains(vector.x, vector.y)
						&& (this.iSelectedItem > 0)) {
					this.isPrevPressed = true;
					this.iSelectedItem--;
					this.isScrolling = true;
					this.firstItemPosition.addSelf(this.interItemDistance);
				}
			}
		}
	}

	public final void Update(java.util.ArrayList<Vector2f> mousePositionList) {
		if (!this.isScrolling) {
			this.TouchInputDetection(mousePositionList);
		} else {
			this.Scroll();
		}
	}
}