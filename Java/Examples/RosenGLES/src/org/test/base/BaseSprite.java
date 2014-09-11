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
package org.test.base;

import java.util.Arrays;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class BaseSprite {
	public SpriteEffects effects = SpriteEffects.None;
	private AnimationIndex _anIndex;
	public int _Frame;
	protected int _framecount;
	public int _height;
	protected LTexture _myTexture;
	protected int _NumOfColumes;
	protected boolean _Paused;
	protected float _TimePerFrame;
	protected float _TotalElapsed;
	public int _width;
	public RectBox bounds;
	public LColor color;
	public float Depth;

	public float moveRate;
	public Vector2f Origin;
	public Vector2f Pos;
	public float Rotation;
	public Vector2f Scale;
	public boolean visible;

	public BaseSprite() {
		this.Pos = new Vector2f();
		this.color = LColor.white;
		this.moveRate = 1f;
		this.Origin = new Vector2f();
		this.Rotation = 0f;
		this.Scale = new Vector2f(1f, 1f);
		this.Depth = 0.5f;
		this.visible = true;
		int[] index = new int[1];
		this._anIndex = new AnimationIndex(index);
	}

	public BaseSprite(Vector2f origin, float rotation, Vector2f scale,
			float depth) {
		this.Pos = new Vector2f();
		this.color = LColor.white;
		this.moveRate = 1f;
		this.Origin = origin;
		this.Rotation = rotation;
		this.Scale = scale;
		this.Depth = depth;
		this.visible = true;
		int[] index = new int[1];
		this._anIndex = new AnimationIndex(index);
	}

	public void DrawFrame(SpriteBatch batch) {
		this.DrawFrame(batch, this._Frame);
	}

	public void DrawFrame(SpriteBatch batch, int frame) {
		if ((((this.visible && (((this.Pos.x - this.Origin.x) + this.getWidth()) >= 0f)) && ((this.Pos.x - this.Origin.x) <= LSystem.screenRect.width)) && ((this.Pos.y - this.Origin.y) <= LSystem.screenRect.height))
				&& (((this.Pos.y - this.Origin.y) + this.getHeight()) >= 0f)) {
			int num = frame % this._NumOfColumes;
			int num2 = frame / this._NumOfColumes;
			batch.draw(this._myTexture, this.Pos, num * this._width, num2
					* this._height, this._width, this._height, this.color,
					this.Rotation, this.Origin, this.Scale, effects);
		}
	}

	public int Frame() {
		return this._Frame;
	}

	public int[] getAnimationIndex() {
		return this._anIndex.getIndex();
	}

	public RectBox getGlobalBounds() {
		RectBox bounds = this.bounds;
		bounds.setBounds(Pos.x, Pos.y, getWidth(), getHeight());
		return bounds;
	}

	public float getHeight() {
		return (this._height * this.Scale.y);
	}

	public boolean isHittedBy(BaseSprite sp) {
		RectBox bounds = this.bounds;
		RectBox rectangle2 = sp.bounds;
		bounds.setBounds(Pos.x, Pos.y, getWidth(), getHeight());
		rectangle2.setBounds(sp.Pos.x, sp.Pos.y, sp.getWidth(), sp.getHeight());
		return bounds.intersects(rectangle2);
	}

	public void Load(String asset, int frameCount, float frame_max,
			boolean paused) {
		this.Load(asset, frameCount, 1, frame_max, paused);
	}

	public void Load(String asset, int frameCount, int numOfColumes,
			float frame_max, boolean paused) {
		this._NumOfColumes = numOfColumes;
		this._framecount = frameCount;
		this._myTexture = LTextures.loadTexture(asset + ".png");
		this._TimePerFrame = 0.01666667f * frame_max;
		this._Frame = 0;
		this._TotalElapsed = 0f;
		this._Paused = paused;
		this._height = this._myTexture.getHeight()
				/ (this._framecount / numOfColumes);
		this._width = this._myTexture.getWidth() / numOfColumes;
		this.bounds = new RectBox(0, 0, this._width, this._height);
	}

	public void nextFrame() {
		this._Frame = this._anIndex.next();
	}

	public void Pause() {
		this._Paused = true;
	}

	public void Play() {
		this._Paused = false;
	}

	public void Reset() {
		this._Frame = 0;
		this._TotalElapsed = 0f;
	}

	public void setAnimation(int[] anindex) {
		if (!Arrays.equals(this._anIndex.getIndex(), anindex)) {
			this._anIndex = new AnimationIndex(anindex);
			if (anindex.length == 1) {
				this._Paused = true;
			} else {
				this._Paused = false;
			}
		}
	}

	protected void specificUpdate(GameTime gameTime) {
	}

	public void Stop() {
		this.Pause();
		this.Reset();
	}

	public void UpdateFrame(GameTime gameTime) {
		this.specificUpdate(gameTime);
		if (this._Paused) {
			this._Frame = this._anIndex.first();
		} else {
			this._TotalElapsed += gameTime.getElapsedGameTime();
			if (this._TotalElapsed > this._TimePerFrame) {
				this.nextFrame();
				this._TotalElapsed -= this._TimePerFrame;
			}
		}
	}

	public float getWidth() {
		return (this._width * this.Scale.x);
	}

	public boolean IsPaused() {

		return this._Paused;

	}
}
