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
package loon.opengl;

import loon.LRelease;
import loon.LTexture;
import loon.utils.IArray;

public class LTextureFree implements IArray, LRelease {

	private boolean onImageFile, closed;

	private LTexture[] textures;

	private int count = 0;

	public LTextureFree() {
		this(4);
	}

	public LTextureFree(int maxSize) {
		this(maxSize, false);
	}

	public LTextureFree(int maxSize, boolean oif) {
		this.textures = new LTexture[maxSize];
		onImageFile = oif;
	}

	private void expandCapacity(int capacity) {
		if (textures.length < capacity) {
			LTexture[] bagArray = new LTexture[capacity];
			System.arraycopy(textures, 0, bagArray, 0, count);
			textures = bagArray;
		}
	}

	private void compressCapacity(int capacity) {
		if (capacity + this.count < textures.length) {
			LTexture[] newArray = new LTexture[this.count + 2];
			System.arraycopy(textures, 0, newArray, 0, this.count);
			textures = newArray;
		}
	}

	public boolean contains(LTexture tex) {
		if (tex == null) {
			return false;
		}
		for (int i = 0; i < count; i++) {
			LTexture curtex = textures[i];
			boolean exist = (curtex != null);
			if (exist && (curtex == tex || curtex.equals(tex))) {
				return true;
			}
		}
		return false;
	}

	public boolean add(LTexture tex, boolean isCan) {
		if (isCan && contains(tex)) {
			return false;
		}
		if (this.count >= this.textures.length) {
			expandCapacity((count + 1) * 2);
		}
		return (textures[count++] = tex) != null;
	}

	public boolean add(LTexture tex) {
		return add(tex, false);
	}

	public LTextureFree add(LTexture... list) {
		if (closed || (list == null)) {
			return this;
		}
		for (LTexture element : list) {
			if (element != null) {
				add(element, false);
			}
		}
		return this;
	}

	public LTexture remove(int index) {
		LTexture removed = this.textures[index];
		int size = this.count - index - 1;
		if (size > 0) {
			System.arraycopy(this.textures, index + 1, this.textures, index, size);
		}
		this.textures[--this.count] = null;
		if (size == 0) {
			this.textures = new LTexture[0];
		}
		return removed;
	}

	public boolean remove(LTexture tex) {
		if ((textures == null) || (tex == null)) {
			return false;
		}
		boolean removed = false;
		for (int i = count; i > 0; i--) {
			LTexture curtex = textures[i - 1];
			boolean exist = (curtex != null);
			if (exist && (tex == curtex) || (tex.equals(curtex))) {
				removed = true;
				count--;
				textures[i - 1] = textures[count];
				textures[count] = null;
				if (count == 0) {
					textures = new LTexture[0];
				} else {
					compressCapacity(2);
				}
				return removed;
			}
		}
		return removed;
	}

	public boolean isOnImageFile() {
		return onImageFile;
	}

	@Override
	public int size() {
		return count;
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public boolean isEmpty() {
		return count == 0 || textures == null;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@Override
	public void close() {
		this.free();
	}

	@Override
	public void clear() {
		this.textures = new LTexture[4];
		this.count = 0;
	}

	public LTextureFree free() {
		if (closed || (count == 0)) {
			return this;
		}
		for (int i = 0; i < count; i++) {
			LTexture tex = textures[i];
			if (tex == null) {
				continue;
			}
			if (!onImageFile) {
				tex.close();
			} else if (onImageFile && !tex.isDrawCanvas()) {
				tex.close();
			}
			tex = null;
		}
		textures = null;
		closed = true;
		return this;
	}

}
