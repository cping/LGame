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
package loon.geom;

import loon.LSysException;
import loon.utils.reply.TChange;

public class ObservableXYZ<T> implements XYZ, SetXYZ {

	public final static <T> ObservableXYZ<T> at(TChange<T> change, XYZ pos, T obj) {
		return new ObservableXYZ<T>(change, pos, obj);
	}
	
	private TChange<T> _change;

	private XYZ _pos;

	private SetXYZ _tempPos;

	private T _obj;

	public ObservableXYZ(TChange<T> v, XYZ pos) {
		this(v, pos, null);
	}

	public ObservableXYZ(TChange<T> v, XYZ pos, T obj) {
		if (v == null) {
			throw new LSysException("The XYZ Object cannot be null !");
		}
		this._change = v;
		this._pos = pos;
		this._obj = obj;
	}

	private boolean checkUpdate() {
		return _pos != null;
	}

	private SetXYZ location() {
		if (_pos instanceof SetXYZ) {
			return ((SetXYZ) _pos);
		}
		if (_tempPos == null) {
			_tempPos = new Vector3f();
		}
		return _tempPos;
	}

	@Override
	public void setX(float x) {
		if (checkUpdate() && _pos.getX() != x) {
			location().setX(x);
			if (_change != null) {
				_change.onUpdate(_obj);
			}
		}
	}

	@Override
	public void setY(float y) {
		if (checkUpdate() && _pos.getY() != y) {
			location().setY(y);
			if (_change != null) {
				_change.onUpdate(_obj);
			}
		}
	}

	@Override
	public void setZ(float z) {
		if (checkUpdate() && _pos.getZ() != z) {
			location().setZ(z);
			if (_change != null) {
				_change.onUpdate(_obj);
			}
		}
	}

	public int x() {
		return (int) _pos.getX();
	}

	public int y() {
		return (int) _pos.getY();
	}

	public int z() {
		return (int) _pos.getZ();
	}

	@Override
	public float getX() {
		return _pos.getX();
	}

	@Override
	public float getY() {
		return _pos.getY();
	}

	@Override
	public float getZ() {
		return _pos.getZ();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		XYZ xy = (XYZ) o;
		return (_pos.getX() == xy.getX()) && (_pos.getY() == xy.getY() && (_pos.getZ() == xy.getZ()));
	}

	@Override
	public final String toString() {
		return "(" + _pos.getX() + "," + _pos.getY() + "," + _pos.getZ() + ")";
	}
}