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
package loon.action.sprite.bone;

import loon.LSystem;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.FloatArray;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class Bone implements Comparable<Bone> {

	private TArray<BoneSheet> _boneSheet;

	private RectBox _size;

	private String _name;

	private float _posX;
	private float _posY;
	private float _scaleX;
	private float _scaleY;
	private float _originX;
	private float _originY;
	private float _angle;

	private float _clipX;
	private float _clipY;
	private float _layer;

	private int _clipWidth;
	private int _clipHeight;
	private int _clipIndex;
	private int _flipFlag;

	public Bone(String name, int posX, int posY, int scaleX, int scaleY, int originX, int originY, int angle,
			int flipFlag, int clipX, int clipY, int clipWidth, int clipHeight, int layer, TArray<BoneSheet> ssl) {
		load(name, posX, posY, scaleX, scaleY, originX, originY, angle, flipFlag, clipX, clipY, clipWidth, clipHeight,
				layer, ssl);
	}

	public Bone(String name, FloatArray v, TArray<BoneSheet> bs) {
		load(name, v, bs);
	}

	public Bone load(String name, int posX, int posY, int scaleX, int scaleY, int originX, int originY, int angle,
			int flipFlag, int clipX, int clipY, int clipWidth, int clipHeight, int layer, TArray<BoneSheet> bs) {
		this._name = name;
		this._posX = posX;
		this._posY = posY;
		this._scaleX = scaleX;
		this._scaleY = scaleY;
		this._originX = originX;
		this._originY = originY;
		this._angle = angle;
		this._flipFlag = flipFlag;
		this._clipX = clipX;
		this._clipY = clipY;
		this._clipWidth = clipWidth;
		this._clipHeight = clipHeight;
		this._layer = layer;
		this._clipIndex = 0;
		this._boneSheet = bs;
		return this;
	}

	public Bone load(String name, FloatArray valueList, TArray<BoneSheet> bs) {
		this._name = name;
		this._posX = valueList.get(BoneFlags.POS_X);
		this._posY = valueList.get(BoneFlags.POS_Y);
		this._scaleX = valueList.get(BoneFlags.SCALE_X);
		this._scaleY = valueList.get(BoneFlags.SCALE_Y);
		this._originX = valueList.get(BoneFlags.ORIGIN_X);
		this._originY = valueList.get(BoneFlags.ORIGIN_Y);
		this._angle = valueList.get(BoneFlags.ANGLE);
		this._flipFlag = (int) valueList.get(BoneFlags.FLIP);
		this._clipX = valueList.get(BoneFlags.CLIP_X);
		this._clipY = valueList.get(BoneFlags.CLIP_Y);
		this._clipWidth = (int) valueList.get(BoneFlags.CLIP_WIDTH);
		this._clipHeight = (int) valueList.get(BoneFlags.CLIP_HEIGHT);
		this._layer = valueList.get(BoneFlags.LAYER);
		this._clipIndex = (int) valueList.get(BoneFlags.CLIP_INDEX);
		this._boneSheet = bs;
		return this;
	}

	public RectBox getRectBox() {
		if (_size == null) {
			_size = new RectBox(_posX, _posY, _clipWidth, _clipHeight);
		} else {
			_size.set(_posX, _posY, _clipWidth, _clipHeight);
		}
		return _size;
	}

	public float getLayer() {
		return _layer;
	}

	public FloatArray getBoneValues() {
		FloatArray valueList = new FloatArray();
		valueList.add(_posX);
		valueList.add(_posY);
		valueList.add(_scaleX);
		valueList.add(_scaleY);
		valueList.add(_originX);
		valueList.add(_originY);
		valueList.add(_angle);
		valueList.add(_flipFlag);
		valueList.add(_clipX);
		valueList.add(_clipY);
		valueList.add(_clipWidth);
		valueList.add(_clipHeight);
		valueList.add(_layer);
		valueList.add(_clipIndex);
		return valueList;
	}

	public float getAngle() {
		return _angle;
	}

	public Bone setAngle(float angle) {
		this._angle = angle;
		return this;
	}

	public TArray<BoneSheet> getSheetList() {
		return _boneSheet;
	}

	public void draw(GLEx g, float x, float y) {
		_boneSheet.get(_clipIndex).draw(g, x + _posX, y + _posY, _scaleX, _scaleY, _clipX, _clipY, _clipWidth,
				_clipHeight, _originX, _originY, _angle, _flipFlag);
	}

	@Override
	public int compareTo(Bone b) {
		if (this.getLayer() > b.getLayer()) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public String toString() {
		return StringUtils.join(LSystem.COMMA, _name, _posX, _posY, _scaleX, _scaleY, _originX, _originY, _angle,
				_flipFlag, _clipX, _clipY, _clipWidth, _clipHeight, _layer, _boneSheet);
	}

	public TArray<BoneSheet> getBoneSheet() {
		return _boneSheet;
	}

	public String getName() {
		return _name;
	}

	public float getPosX() {
		return _posX;
	}

	public float getPosY() {
		return _posY;
	}

	public float getScaleX() {
		return _scaleX;
	}

	public float getScaleY() {
		return _scaleY;
	}

	public float getOriginX() {
		return _originX;
	}

	public float getOriginY() {
		return _originY;
	}

	public float getClipX() {
		return _clipX;
	}

	public float getClipY() {
		return _clipY;
	}

	public int getClipWidth() {
		return _clipWidth;
	}

	public int getClipHeight() {
		return _clipHeight;
	}

	public int getClipIndex() {
		return _clipIndex;
	}

	public int getFlipFlag() {
		return _flipFlag;
	}

}
