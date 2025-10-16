/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public final class IndexArray extends BaseBufferSupport implements IndexData {

	private ShortBuffer shortBuffer;
	private ByteBuffer byteBuffer;

	private final boolean empty;

	public IndexArray(int maxIndices) {
		empty = maxIndices == 0;
		if (empty) {
			maxIndices = 1;
		}
		byteBuffer = getSupport().newUnsafeByteBuffer(maxIndices * 2);
		shortBuffer = byteBuffer.asShortBuffer();
		((Buffer) shortBuffer).flip();
		((Buffer) byteBuffer).flip();
	}

	@Override
	public int getNumIndices() {
		return empty ? 0 : shortBuffer.limit();
	}

	@Override
	public int getNumMaxIndices() {
		return empty ? 0 : shortBuffer.capacity();
	}

	@Override
	public void setIndices(final short[] indices, int offset, int count) {
		shortBuffer.clear();
		shortBuffer.put(indices, offset, count);
		shortBuffer.flip();
		byteBuffer.position(0);
		byteBuffer.limit(count << 1);
	}

	@Override
	public void setIndices(final ShortBuffer indices) {
		final int pos = indices.position();
		((Buffer) shortBuffer).clear();
		((Buffer) shortBuffer).limit(indices.remaining());
		shortBuffer.put(indices);
		((Buffer) shortBuffer).flip();
		((Buffer) indices).position(pos);
		((Buffer) byteBuffer).position(0);
		((Buffer) byteBuffer).limit(shortBuffer.limit() << 1);
	}

	@Override
	public void updateIndices(int targetOffset, final short[] indices, int offset, int count) {
		final int pos = byteBuffer.position();
		((Buffer) byteBuffer).position(targetOffset * 2);
		getSupport().copy(indices, offset, byteBuffer, count);
		((Buffer) byteBuffer).position(pos);
	}

	@Override
	public ShortBuffer getBuffer(boolean dirty) {
		return shortBuffer;
	}

	@Override
	public void bind() {
	}

	@Override
	public void unbind() {
	}

	@Override
	public void invalidate() {
	}

	@Override
	public void close() {
		getSupport().disposeUnsafeByteBuffer(byteBuffer);
		shortBuffer = null;
		byteBuffer = null;
	}
}
